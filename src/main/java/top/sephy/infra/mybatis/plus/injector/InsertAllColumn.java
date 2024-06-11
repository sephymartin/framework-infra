/*
 * Copyright 2022-2024 sephy.top
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package top.sephy.infra.mybatis.plus.injector;

import static java.util.stream.Collectors.joining;

import java.io.Serial;
import java.util.List;
import java.util.Objects;

import org.apache.ibatis.executor.keygen.Jdbc3KeyGenerator;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.executor.keygen.NoKeyGenerator;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.sql.SqlInjectionUtils;
import com.baomidou.mybatisplus.core.toolkit.sql.SqlScriptUtils;

public class InsertAllColumn extends AbstractMethod {

    @Serial
    private static final long serialVersionUID = 8070883928677567740L;
    public static String METHOD = "insertAllColumn";
    public static String DESC = "插入一条数据（全部字段插入）";
    public static String SQL = "<script>\nINSERT INTO %s %s VALUES %s\n</script>";

    /**
     * 自增主键字段是否忽略
     *
     * @since 3.5.4
     */
    private boolean ignoreAutoIncrementColumn;

    public InsertAllColumn() {
        this(METHOD);
    }

    /**
     * @param ignoreAutoIncrementColumn 是否忽略自增长主键字段
     * @since 3.5.4
     */
    public InsertAllColumn(boolean ignoreAutoIncrementColumn) {
        this(METHOD);
        this.ignoreAutoIncrementColumn = ignoreAutoIncrementColumn;
    }

    /**
     * @param name 方法名
     * @since 3.5.0
     */
    public InsertAllColumn(String name) {
        super(name);
    }

    /**
     * @param name 方法名
     * @param ignoreAutoIncrementColumn 是否忽略自增长主键字段
     * @since 3.5.4
     */
    public InsertAllColumn(String name, boolean ignoreAutoIncrementColumn) {
        super(name);
        this.ignoreAutoIncrementColumn = ignoreAutoIncrementColumn;
    }

    @Override
    public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
        KeyGenerator keyGenerator = NoKeyGenerator.INSTANCE;

        String columnScript = SqlScriptUtils.convertTrim(getAllInsertSqlColumn(tableInfo, ignoreAutoIncrementColumn),
            LEFT_BRACKET, RIGHT_BRACKET, null, COMMA);
        String valuesScript = LEFT_BRACKET + NEWLINE + SqlScriptUtils
            .convertTrim(getAllInsertSqlProperty(tableInfo, null, ignoreAutoIncrementColumn), null, null, null, COMMA)
            + NEWLINE + RIGHT_BRACKET;
        String keyProperty = null;
        String keyColumn = null;
        // 表包含主键处理逻辑,如果不包含主键当普通字段处理
        if (StringUtils.isNotBlank(tableInfo.getKeyProperty())) {
            if (tableInfo.getIdType() == IdType.AUTO) {
                /* 自增主键 */
                keyGenerator = Jdbc3KeyGenerator.INSTANCE;
                keyProperty = tableInfo.getKeyProperty();
                // 去除转义符
                keyColumn = SqlInjectionUtils.removeEscapeCharacter(tableInfo.getKeyColumn());
            } else if (null != tableInfo.getKeySequence()) {
                keyGenerator = TableInfoHelper.genKeyGenerator(methodName, tableInfo, builderAssistant);
                keyProperty = tableInfo.getKeyProperty();
                keyColumn = tableInfo.getKeyColumn();
            }
        }
        String sql = String.format(SQL, tableInfo.getTableName(), columnScript, valuesScript);
        SqlSource sqlSource = super.createSqlSource(configuration, sql, modelClass);
        return this.addInsertMappedStatement(mapperClass, modelClass, methodName, sqlSource, keyGenerator, keyProperty,
            keyColumn);
    }

    String getAllInsertSqlColumn(TableInfo tableInfo, boolean ignoreAutoIncrementColumn) {
        List<TableFieldInfo> fieldList = tableInfo.getFieldList();
        if (ignoreAutoIncrementColumn && tableInfo.getIdType() == IdType.AUTO) {
            return fieldList.stream().map(TableFieldInfo::getInsertSqlColumn).filter(Objects::nonNull)
                .collect(joining(NEWLINE));
        }

        return tableInfo.getKeyInsertSqlColumn(false, EMPTY, true) + fieldList.stream()
            .map(TableFieldInfo::getInsertSqlColumn).filter(Objects::nonNull).collect(joining(NEWLINE));
    }

    String getAllInsertSqlProperty(TableInfo tableInfo, final String prefix, boolean ignoreAutoIncrementColumn) {
        List<TableFieldInfo> fieldList = tableInfo.getFieldList();
        final String newPrefix = prefix == null ? EMPTY : prefix;
        if (ignoreAutoIncrementColumn && tableInfo.getIdType() == IdType.AUTO) {
            return fieldList.stream().map(i -> getInsertSqlProperty(i, prefix)).filter(Objects::nonNull)
                .collect(joining(NEWLINE));
        }
        return getKeyInsertSqlProperty(tableInfo, false, newPrefix, true) + fieldList.stream()
            .map(i -> getInsertSqlProperty(i, newPrefix)).filter(Objects::nonNull).collect(joining(NEWLINE));
    }

    String getInsertSqlProperty(TableFieldInfo fieldInfo, final String prefix) {
        final String newPrefix = prefix == null ? EMPTY : prefix;
        return fieldInfo.getInsertSqlProperty(newPrefix);
    }

    public String getKeyInsertSqlProperty(TableInfo tableInfo, final boolean batch, final String prefix,
        final boolean newLine) {
        final String newPrefix = prefix == null ? EMPTY : prefix;
        if (tableInfo.havePK()) {
            final String prefixKeyProperty = newPrefix + tableInfo.getKeyProperty();
            String keyColumn = SqlScriptUtils.safeParam(prefixKeyProperty) + COMMA;
            if (tableInfo.getIdType() == IdType.AUTO) {
                if (batch) {
                    // 批量插入必须返回空自增情况下
                    return EMPTY;
                }
                return SqlScriptUtils.convertIf(keyColumn, String.format("%s != null", prefixKeyProperty), newLine);
            }
            return keyColumn + (newLine ? NEWLINE : EMPTY);
        }
        return EMPTY;
    }
}
