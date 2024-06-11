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

import org.apache.ibatis.executor.keygen.NoKeyGenerator;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.toolkit.sql.SqlScriptUtils;

public class InsertAllColumnList extends AbstractMethod {

    @Serial
    private static final long serialVersionUID = 8070883928677567740L;
    public static String METHOD = "insertAllColumnList";

    public static String SQL = "<script>\nINSERT INTO %s %s VALUES %s\n</script>";

    public InsertAllColumnList() {
        this(METHOD);
    }

    /**
     * @param name 方法名
     * @since 3.5.0
     */
    public InsertAllColumnList(String name) {
        super(name);
    }

    @Override
    public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {

        String columnScript =
            SqlScriptUtils.convertTrim(getAllInsertSqlColumn(tableInfo), LEFT_BRACKET, RIGHT_BRACKET, null, COMMA);

        String prefix = "_item";

        String singleValueScript = SqlScriptUtils.convertTrim(getAllInsertSqlProperty(tableInfo, prefix + DOT),
            LEFT_BRACKET, RIGHT_BRACKET, null, COMMA) + NEWLINE;

        String valuesScript = SqlScriptUtils.convertForeach(singleValueScript, "entityList", null, prefix, COMMA);

        String sql = String.format(SQL, tableInfo.getTableName(), columnScript, valuesScript);
        SqlSource sqlSource = super.createSqlSource(configuration, sql, modelClass);
        return this.addInsertMappedStatement(mapperClass, modelClass, methodName, sqlSource, NoKeyGenerator.INSTANCE,
            tableInfo.getKeyProperty(), tableInfo.getKeyColumn());
    }

    String getAllInsertSqlColumn(TableInfo tableInfo) {
        List<TableFieldInfo> fieldList = tableInfo.getFieldList();
        return tableInfo.getKeyColumn() + COMMA + NEWLINE + fieldList.stream().map(TableFieldInfo::getInsertSqlColumn)
            .filter(Objects::nonNull).collect(joining(NEWLINE));
    }

    String getAllInsertSqlProperty(TableInfo tableInfo, String prefix) {
        List<TableFieldInfo> fieldList = tableInfo.getFieldList();
        return SqlScriptUtils.safeParam(prefix + tableInfo.getKeyProperty()) + COMMA + NEWLINE + fieldList.stream()
            .map(i -> getInsertSqlProperty(i, prefix)).filter(Objects::nonNull).collect(joining(NEWLINE));
    }

    String getInsertSqlProperty(TableFieldInfo fieldInfo, final String prefix) {
        final String newPrefix = prefix == null ? EMPTY : prefix;
        return fieldInfo.getInsertSqlProperty(newPrefix);
    }
}
