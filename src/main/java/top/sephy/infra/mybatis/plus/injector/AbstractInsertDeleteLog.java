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

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.executor.keygen.NoKeyGenerator;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;

import top.sephy.infra.entity.DeleteLog;

public abstract class AbstractInsertDeleteLog extends AbstractMethod {

    @Serial
    private static final long serialVersionUID = -6584990316370685448L;
    public static String SCRIPT = """
        <script>
            INSERT INTO delete_log
            <trim prefix="(" suffix=")" suffixOverrides=",">
                `table_name`, `data_id`, `delete_content`,
                 <if test="createdBy != null">`created_by`,</if>
                 <if test="updatedBy != null">`updated_by`,</if>
             </trim>
            SELECT
            <trim prefix="" suffix="" suffixOverrides=",">
                %s as `table_name`, %s as id, %s as delete_content,
                <if test="createdBy != null">${createdBy} as `created_by`,</if>
                <if test="updatedBy != null">${updatedBy} as `updated_by`,</if>
            </trim>
            FROM %s %s
        </script>
        """;

    public AbstractInsertDeleteLog(String method) {
        super(method);
    }

    static String getJsonDeleteContentVal(TableInfo tableInfo) {
        List<TableFieldInfo> fieldList = tableInfo.getFieldList();
        List<String> columnNames = new ArrayList<>(fieldList.size() + 1);
        if (StringUtils.isNotBlank(tableInfo.getKeyColumn())) {
            columnNames.add(tableInfo.getKeyColumn());
        }
        for (TableFieldInfo tableFieldInfo : fieldList) {
            columnNames.add(tableFieldInfo.getColumn());
        }
        StringBuilder sb = new StringBuilder();
        sb.append("JSON_OBJECT(");
        for (String columnName : columnNames) {
            sb.append(surroundedBySingleQuotes(columnName)).append(", ").append(columnName).append(", ");
        }
        sb.deleteCharAt(sb.lastIndexOf(",")).append(")");
        return sb.toString();
    }

    @Override
    public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
        String deleteContentVal = getJsonDeleteContentVal(tableInfo);
        KeyGenerator keyGenerator = NoKeyGenerator.INSTANCE;
        Class<DeleteLog> deleteLogClass = DeleteLog.class;
        String idVal = tableInfo.havePK() ? tableInfo.getKeyColumn() : "null";
        String tableName = tableInfo.getTableName();
        String tableNameVal = surroundedBySingleQuotes(tableName);
        String whereExpression = getWhereExpression(mapperClass, modelClass, tableInfo);
        String sql = String.format(SCRIPT, tableNameVal, idVal, deleteContentVal, tableName, whereExpression);
        SqlSource sqlSource = super.createSqlSource(configuration, sql, deleteLogClass);
        return this.addInsertMappedStatement(mapperClass, deleteLogClass, methodName, sqlSource, keyGenerator,
            tableInfo.getKeyProperty(), tableInfo.getKeyColumn());
    }

    abstract String getWhereExpression(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo);

    static String surroundedBySingleQuotes(String str) {
        return "'" + str + "'";
    }

    static String surroundedByParamExpression(String str) {
        return "#{" + str + "}";
    }
}
