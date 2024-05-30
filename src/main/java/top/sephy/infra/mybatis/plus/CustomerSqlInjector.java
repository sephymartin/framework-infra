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
