package top.sephy.infra.mybatis.plus.injector;

import java.io.Serial;

import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.executor.keygen.NoKeyGenerator;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import top.sephy.infra.entity.DeleteLog;


public class InsertDeleteLogById extends AbstractMethod {

    @Serial
    private static final long serialVersionUID = -6584990316370685448L;
    public static String METHOD = "insertDeleteLog";
    public static String DESC = "插入一条删除记录";

    public static String SCRIPT = """
        <script>
            INSERT INTO delete_log
            <trim prefix="(" suffix=")" suffixOverrides=",">
                <if test="id != null">
                    id,
                </if>
                table_name,
                <if test="dataId != null">data_id,</if>
                <if test="deleteContent != null">delete_content,</if>
                <if test="createdBy != null">created_by,</if>
                <if test="createdTime != null">created_time,</if>
                <if test="updatedBy != null">updated_by,</if>
                <if test="updatedTime != null">updated_time,</if>
            </trim>
            VALUES (
            <trim suffixOverrides=",">
                <if test="id != null"> #{id},</if>
                '%s',
                <if test="dataId != null">#{dataId},</if>
                <if test="deleteContent != null">#{deleteContent},</if>
                <if test="createdBy != null">#{createdBy},</if>
                <if test="createdTime != null">#{createdTime},</if>
                <if test="updatedBy != null">#{updatedBy},</if>
                <if test="updatedTime != null">#{updatedTime},</if>
            </trim>
            )
        </script>
        """;

    public InsertDeleteLogById() {
        super(METHOD);
    }

    @Override
    public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {

        KeyGenerator keyGenerator = NoKeyGenerator.INSTANCE;
        String keyProperty = null;
        String keyColumn = null;
        Class<DeleteLog> deleteLogClass = DeleteLog.class;
        String sql = String.format(SCRIPT, tableInfo.getTableName());
        SqlSource sqlSource = super.createSqlSource(configuration, sql, deleteLogClass);
        return this.addInsertMappedStatement(mapperClass, deleteLogClass, methodName, sqlSource, keyGenerator,
            keyProperty, keyColumn);
    }
}
