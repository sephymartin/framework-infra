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
