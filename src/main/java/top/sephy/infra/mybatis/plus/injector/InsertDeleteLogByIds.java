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

import com.baomidou.mybatisplus.core.metadata.TableInfo;

public class InsertDeleteLogByIds extends AbstractInsertDeleteLog {

    @Serial
    private static final long serialVersionUID = -6584990316370685448L;
    public static String METHOD = "insertDeleteLogByIds";

    public static String WHERE_EXPRESSION = """
        WHERE %s in
        <foreach collection="deleteIds" item="_deleteId" open="(" separator="," close=")">
            #{_deleteId}
        </foreach>
        """;

    public InsertDeleteLogByIds() {
        super(METHOD);
    }

    @Override
    String getWhereExpression(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
        return String.format(WHERE_EXPRESSION, tableInfo.getKeyProperty());
    }
}
