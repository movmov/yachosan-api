package yachosan;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import net.sf.log4jdbc.Log4jdbcProxyDataSource;
import org.dozer.spring.DozerBeanMapperFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.web.HttpMapperProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourceArrayPropertyEditor;
import yachosan.domain.model.ProposedDate;
import yachosan.domain.model.ScheduleId;
import yachosan.infra.proposeddate.ProposedDateDeserializer;
import yachosan.infra.proposeddate.ProposedDateKeyDeserializer;
import yachosan.infra.proposeddate.ProposedDateSerializer;
import yachosan.infra.scheduleid.ScheduleIdConverter;
import yachosan.infra.scheduleid.ScheduleIdDeserializer;
import yachosan.infra.scheduleid.ScheduleIdKeyDeserializer;
import yachosan.infra.scheduleid.ScheduleIdSerializer;

import javax.sql.DataSource;

@Configuration
public class AppConfig {
    @Autowired
    DataSourceProperties dataSourceProperties;
    @Autowired
    HttpMapperProperties httpMapperProperties;
    DataSource dataSource;

    @ConfigurationProperties(prefix = DataSourceAutoConfiguration.CONFIGURATION_PREFIX)
    @Bean
    DataSource realDataSource() {
        DataSourceBuilder factory = DataSourceBuilder
                .create(this.dataSourceProperties.getClassLoader())
                .url(this.dataSourceProperties.getUrl())
                .username(this.dataSourceProperties.getUsername())
                .password(this.dataSourceProperties.getPassword());
        this.dataSource = factory.build();
        return this.dataSource;
    }

    @Bean
    DataSource dataSource() {
        return new Log4jdbcProxyDataSource(this.dataSource);
    }

    @Bean
    JSR310Module jsr310Module() {
        return new JSR310Module();
    }

    @Bean
    Module yachosanModule() {
        SimpleModule module = new SimpleModule();
        module.addSerializer(ScheduleId.class, new ScheduleIdSerializer());
        module.addDeserializer(ScheduleId.class, new ScheduleIdDeserializer());
        module.addKeyDeserializer(ScheduleId.class, new ScheduleIdKeyDeserializer());

        module.addSerializer(ProposedDate.class, new ProposedDateSerializer());
        module.addDeserializer(ProposedDate.class, new ProposedDateDeserializer());
        module.addKeyDeserializer(ProposedDate.class, new ProposedDateKeyDeserializer());
        return module;
    }

    @Bean
    ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        if (this.httpMapperProperties.isJsonSortKeys()) {
            objectMapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS,
                    true);
        }
        // customize
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        return objectMapper;
    }

    @Bean
    DozerBeanMapperFactoryBean dozerMapper() throws Exception {
        DozerBeanMapperFactoryBean factoryBean = new DozerBeanMapperFactoryBean();
        ResourceArrayPropertyEditor editor = new ResourceArrayPropertyEditor();
        editor.setAsText("classpath*:/dozer/**/*.xml");
        factoryBean.setMappingFiles((Resource[]) editor.getValue());
        return factoryBean;
    }

    @Bean
    ScheduleIdConverter scheduleIdConverter() {
        return new ScheduleIdConverter();
    }
}
