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
package top.sephy.infra.mybatis.plus;

import java.util.List;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.extension.injector.methods.AlwaysUpdateSomeColumnById;
import com.baomidou.mybatisplus.extension.injector.methods.InsertBatchSomeColumn;
import com.baomidou.mybatisplus.extension.injector.methods.Upsert;

import top.sephy.infra.mybatis.plus.injector.InsertAllColumn;
import top.sephy.infra.mybatis.plus.injector.InsertAllColumnList;
import top.sephy.infra.mybatis.plus.injector.InsertDeleteLogByIds;
import top.sephy.infra.mybatis.plus.injector.InsertDeleteLogByQueryWrapper;

/**
 * 添加Sql注入方法,支持批量插入
 *
 * @author sephy
 * @date 2020-03-01 14:51
 */
public class CustomerSqlInjector extends DefaultSqlInjector {

    @Override
    public List<AbstractMethod> getMethodList(Class<?> mapperClass, TableInfo tableInfo) {
        List<AbstractMethod> methodList = super.getMethodList(mapperClass, tableInfo);
        methodList.add(new Upsert());
        methodList.add(new InsertAllColumn());
        methodList.add(new InsertAllColumnList());
        methodList.add(new InsertBatchSomeColumn());
        methodList.add(new InsertDeleteLogByIds());
        methodList.add(new InsertDeleteLogByQueryWrapper());
        // methodList.add(new UpdateAllColumnById());
        methodList.add(new AlwaysUpdateSomeColumnById());
        return methodList;
    }
}
