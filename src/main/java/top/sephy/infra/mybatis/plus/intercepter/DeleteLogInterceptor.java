package top.sephy.infra.mybatis.plus.intercepter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;

import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import com.baomidou.mybatisplus.extension.parser.JsqlParserGlobal;
import com.baomidou.mybatisplus.extension.plugins.inner.DataChangeRecorderInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.JsonAggregateOnNullType;
import net.sf.jsqlparser.expression.JsonFunction;
import net.sf.jsqlparser.expression.JsonFunctionType;
import net.sf.jsqlparser.expression.JsonKeyValuePair;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectItem;
import top.sephy.infra.exception.SystemException;
import top.sephy.infra.mybatis.interceptor.CurrentUserExtractor;

/**
 * save delete content before real delete operation, user can customize table name, column name
 * 
 */
@Slf4j
public class DeleteLogInterceptor implements InnerInterceptor {

    private int defaultBatchUpdateLimit = 1000;
    private boolean batchUpdateLimitationOpened = false;
    private final Map<String, Integer> batchUpdateLimitMap = new ConcurrentHashMap<>();// 表名->批量更新上限

    private String tableName;

    private String colNameTable;

    private String colNameDataId;

    private String colNameContent;

    private String colNameOperator;

    private boolean saveOperator = false;

    private CurrentUserExtractor<?> currentUserExtractor;

    private Map<String, Integer> ignoreTables = new ConcurrentHashMap<>();

    private Map<String, PlainSelect> selectCache = new ConcurrentHashMap<>();

    private Map<String, Insert> insertCache = new ConcurrentHashMap<>();

    public DeleteLogInterceptor() {
        this("delete_log", "table_name", "data_id", "content");
    }

    public DeleteLogInterceptor(String tableName, String colNameTable, String colNameDataId, String colNameContent) {
        this.tableName = tableName;
        this.colNameTable = colNameTable;
        this.colNameDataId = colNameDataId;
        this.colNameContent = colNameContent;
        addIgnoreTable(tableName.toLowerCase());
    }

    public void setOperator(@NonNull String colNameOperator, @NonNull CurrentUserExtractor<?> currentUserExtractor) {
        this.colNameOperator = colNameOperator;
        this.currentUserExtractor = currentUserExtractor;
        this.saveOperator = true;
    }

    public void addIgnoreTable(String tableName) {
        ignoreTables.put(tableName.toLowerCase(), 1);
    }

    @Override
    public void beforePrepare(StatementHandler sh, Connection connection, Integer transactionTimeout) {
        PluginUtils.MPStatementHandler mpSh = PluginUtils.mpStatementHandler(sh);
        MappedStatement ms = mpSh.mappedStatement();
        final BoundSql boundSql = mpSh.boundSql();
        SqlCommandType sct = ms.getSqlCommandType();
        if (sct == SqlCommandType.DELETE) {
            PluginUtils.MPBoundSql mpBs = mpSh.mPBoundSql();
            long startTs = System.currentTimeMillis();
            try {
                Statement statement = JsqlParserGlobal.parse(mpBs.sql());
                if (statement instanceof Delete delete) {
                    String sqlTableName = StringUtils.replace(delete.getTable().getName(), "`", "");
                    if (ignoreTables.containsKey(sqlTableName.toLowerCase())) {
                        return;
                    }
                    processDelete((Delete)statement, ms, boundSql, connection);
                }
            } catch (Exception e) {
                if (e instanceof DataChangeRecorderInnerInterceptor.DataUpdateLimitationException) {
                    throw (DataChangeRecorderInnerInterceptor.DataUpdateLimitationException)e;
                }
                log.error("Unexpected error for mappedStatement={}, sql={}", ms.getId(), mpBs.sql(), e);
                return;
            }
            long costThis = System.currentTimeMillis() - startTs;
        }
    }

    public void processDelete(Delete deleteStmt, MappedStatement mappedStatement, BoundSql boundSql,
        Connection connection) {
        Table table = deleteStmt.getTable();
        TableInfo tableInfo = TableInfoHelper.getTableInfo(table.getName());
        if (tableInfo != null) {
            PlainSelect select = getPlainSelect(deleteStmt, tableInfo);
            copyOriginalData(select, mappedStatement, boundSql, connection);
        }
    }

    private Insert generateInsertSelectSql(Select select) {
        Insert statement = insertCache.computeIfAbsent(select.toString(), s -> {
            Insert insert = new Insert();
            List<Column> insertCols = new ArrayList<>();
            insertCols.add(new Column(colNameTable));
            insertCols.add(new Column(colNameDataId));
            insertCols.add(new Column(colNameContent));
            if (saveOperator) {
                insertCols.add(new Column(colNameOperator));
            }
            insert.setColumns(new ExpressionList<>(insertCols));
            insert.setTable(new Table(tableName));
            insert.setSelect(select);
            return insert;
        });
        log.debug("insert sql: {}", statement);
        return statement;
    }

    static String surroundedBySingleQuotes(String str) {
        return "'" + str + "'";
    }

    private PlainSelect getPlainSelect(Delete deleteStmt, TableInfo tableInfo) {

        PlainSelect select = selectCache.computeIfAbsent(deleteStmt.toString(), s -> {
            Table deleteTable = deleteStmt.getTable();
            PlainSelect plainSelect = new PlainSelect();
            plainSelect.setFromItem(deleteTable);

            List<SelectItem<?>> selectItems = new ArrayList<>();
            // `table_name`
            selectItems.add(new SelectItem<>(new StringValue(deleteTable.getName())));

            Table selectItemTable = null;
            if (deleteTable.getAlias() != null) {
                selectItemTable = new Table(deleteTable.getAlias().getName());
            }

            // `data_id`
            selectItems.add(new SelectItem<>(new Column(selectItemTable, tableInfo.getKeyColumn())));

            JsonFunction jsonFunction = new JsonFunction();
            jsonFunction.withType(JsonFunctionType.MYSQL_OBJECT);
            jsonFunction.withOnNullType(JsonAggregateOnNullType.NULL);
            jsonFunction.add(new JsonKeyValuePair(surroundedBySingleQuotes(tableInfo.getKeyColumn()),
                new Column(selectItemTable, tableInfo.getKeyColumn()).toString(), false, false));
            List<TableFieldInfo> fieldList = tableInfo.getFieldList();
            for (TableFieldInfo tableFieldInfo : fieldList) {
                jsonFunction.add(new JsonKeyValuePair(surroundedBySingleQuotes(tableFieldInfo.getColumn()),
                    new Column(selectItemTable, tableFieldInfo.getColumn()).toString(), false, false));
            }

            // JSON_OBJECT
            selectItems.add(new SelectItem<>(jsonFunction));

            if (this.colNameOperator != null && this.currentUserExtractor != null) {
                Object currentUserId = this.currentUserExtractor.getCurrentUserId();
                selectItems.add(new SelectItem<>(currentUserId instanceof String
                    ? new StringValue(currentUserId.toString()) : new LongValue(currentUserId.toString())));
            }

            plainSelect.setSelectItems(selectItems);
            plainSelect.setWhere(deleteStmt.getWhere());
            plainSelect.setJoins(deleteStmt.getJoins());

            return plainSelect;
        });

        log.debug("select sql: {}", select);

        return select;
    }

    private int copyOriginalData(PlainSelect selectStmt, MappedStatement mappedStatement, BoundSql boundSql,
        Connection connection) {

        String insertSelectSql = generateInsertSelectSql(selectStmt).toString();
        try (PreparedStatement statement = connection.prepareStatement(insertSelectSql)) {
            DefaultParameterHandler parameterHandler =
                new DefaultParameterHandler(mappedStatement, boundSql.getParameterObject(), boundSql);
            parameterHandler.setParameters(statement);
            int i = statement.executeUpdate();
            checkTableBatchLimitExceeded(selectStmt, i);
            return i;
        } catch (Exception e) {
            if (e instanceof DataChangeRecorderInnerInterceptor.DataUpdateLimitationException) {
                throw (DataChangeRecorderInnerInterceptor.DataUpdateLimitationException)e;
            }
            log.error("try to get record tobe deleted for selectStmt={}", selectStmt, e);
            throw new SystemException(e);
        }
    }

    private void checkTableBatchLimitExceeded(PlainSelect plainSelect, int count) {

        if (!batchUpdateLimitationOpened) {
            return;
        }

        final FromItem fromItem = plainSelect.getFromItem();
        if (fromItem instanceof Table) {
            Table fromTable = (Table)fromItem;
            final String tableName = fromTable.getName().toLowerCase();
            Integer limit = batchUpdateLimitMap.getOrDefault(tableName, defaultBatchUpdateLimit);
            if (count > limit) {
                String msg =
                    String.format("batch update limit exceed for configured tableName=%s, limit=%d, " + "count=%d",
                        tableName, limit, count);
                throw new DataChangeRecorderInnerInterceptor.DataUpdateLimitationException(msg);
            }
        }
        if (count > defaultBatchUpdateLimit) {
            String msg = String.format("batch update limit exceed for configured tableName=%s, limit=%d, " + "count=%d",
                tableName, defaultBatchUpdateLimit, count);
            throw new DataChangeRecorderInnerInterceptor.DataUpdateLimitationException(msg);
        }
    }
}
