package top.sephy.infra.mybatis.plus;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.data.convert.Jsr310Converters;
import org.springframework.data.util.NullableWrapperConverters;

import com.baomidou.mybatisplus.autoconfigure.ConfigurationCustomizer;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.github.pagehelper.PageInterceptor;
import com.github.pagehelper.autoconfigure.PageHelperStandardProperties;

import top.sephy.infra.mybatis.interceptor.AutoFillInterceptor;
import top.sephy.infra.mybatis.interceptor.CurrentUserExtractor;
import top.sephy.infra.mybatis.interceptor.QueryConditionExtractorInterceptor;
import top.sephy.infra.mybatis.query.DefaultQueryContextExtractor;
import top.sephy.infra.mybatis.query.QueryContextExtractor;
import top.sephy.infra.utils.ThreadContextUtils;

public abstract class AbstractBaseMybatisConfig {

    static DefaultConversionService CONVERSION_SERVICE =
        (DefaultConversionService)DefaultConversionService.getSharedInstance();

    static {
        // see
        Jsr310Converters.getConvertersToRegister().forEach(CONVERSION_SERVICE::addConverter);
        NullableWrapperConverters.registerConvertersIn(CONVERSION_SERVICE);
        CONVERSION_SERVICE.removeConvertible(Object.class, Object.class);
    }

    // @Bean
    public QueryContextExtractor queryCriteriaExtractor() {
        return new DefaultQueryContextExtractor(true);
    }

    // @Bean
    public AutoFillInterceptor auditingInterceptor() {

        CurrentUserExtractor<Object> userExtractor = new CurrentUserExtractor<>() {
            @Override
            public Object getCurrentUserId() {
                Long userId = ThreadContextUtils.getUserId();
                if (userId == null) {
                    userId = 0L;
                }
                return userId;
            }

            @Override
            public String getCurrentUserName() {
                String username = ThreadContextUtils.getUsername();
                if (username == null) {
                    username = "";
                }
                return username;
            }
        };

        return new AutoFillInterceptor(CONVERSION_SERVICE, userExtractor);
    }

    @Bean
    public CustomerSqlInjector customerSqlInjector() {
        return new CustomerSqlInjector();
    }

    // @Bean
    public QueryConditionExtractorInterceptor
        queryConditionExtractorInterceptor(QueryContextExtractor queryContextExtractor) {
        return new QueryConditionExtractorInterceptor(queryContextExtractor);
    }

    @ConditionalOnClass(PageHelperStandardProperties.class)
    @Bean
    public ConfigurationCustomizer mybatisPlusConfigurationCustomizer(PageHelperStandardProperties standardProperties) {

        return configuration -> {
            PageInterceptor pageInterceptor = new PageInterceptor();
            pageInterceptor.setProperties(standardProperties.getProperties());
            configuration.addInterceptor(pageInterceptor);
            configuration.addInterceptor(mybatisPlusInterceptor());
            configuration.addInterceptor(auditingInterceptor());
            configuration.addInterceptor(queryConditionExtractorInterceptor(queryCriteriaExtractor()));
        };
    }

    // @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 防止不带 where 条件全表更新/删除
        interceptor.addInnerInterceptor(new BlockAttackInnerInterceptor());
        /// interceptor.addInnerInterceptor(new InsertBatchSomeColumnInterceptor());
        return interceptor;
    }

}