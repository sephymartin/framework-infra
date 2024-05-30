package top.sephy.infra.mybatis.plus.injector;

import com.baomidou.mybatisplus.core.metadata.TableInfo;

public class InsertDeleteLogByQueryWrapper extends AbstractInsertDeleteLog {

    public InsertDeleteLogByQueryWrapper() {
        super("insertDeleteLogByQueryWrapper");
    }

    @Override
    String getWhereExpression(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
        return String.format("%s %s", sqlWhereEntityWrapper(true, tableInfo), sqlComment());
    }
}
