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
package top.sephy.infra.mybatis.type;

import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

public abstract class AbstractComaStringCollectionHandler<E extends Serializable, T extends Collection<E>>
    extends BaseTypeHandler<T> {

    static String DELIMITER = ",";

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException {
        String val = null;
        if (parameter.isEmpty()) {
            val = "";
        } else {
            val = parameter.stream().filter(Objects::nonNull).map(String::valueOf).filter(StringUtils::isNoneBlank)
                .collect(Collectors.joining(DELIMITER));
        }
        ps.setString(i, val);
    }

    @Override
    public T getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return toCollection(rs.getString(columnName));
    }

    @Override
    public T getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return toCollection(rs.getString(columnIndex));
    }

    @Override
    public T getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return toCollection(cs.getString(columnIndex));
    }

    abstract T empty();

    abstract T newCollection();

    abstract E stringToElement(String str);

    public T toCollection(String columnValue) {
        if (StringUtils.isBlank(columnValue)) {
            return empty();
        }
        String[] values = columnValue.split(DELIMITER);
        T collection = newCollection();
        for (String value : values) {
            collection.add(stringToElement(value));
        }
        return collection;
    }
}
