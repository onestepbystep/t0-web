package cn.cgnb.conf;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * Created by ah on 2018/5/7.
 */
@Configuration
public class DataSourceConfig {


    @Bean(name="default")
    @ConfigurationProperties(prefix="spring.datasource")
    public DataSource getDataSource(){

        return DataSourceBuilder.create().build();
    }

    @Bean(name="defaultJdbc")
    public JdbcTemplate getDefaultTemplate(@Qualifier("default") DataSource dataSource) {

        return new JdbcTemplate(dataSource);
    }


    @Bean(name="ogg")
    @ConfigurationProperties(prefix = "ogg")
    public DataSource getDSDataSource(){
        
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "oggjdbc")
    public JdbcTemplate getMobileTemplate(@Qualifier("ogg") DataSource dataSource) {

        return new JdbcTemplate(dataSource);
    }


    @Bean(name="mobile")
    @ConfigurationProperties(prefix="mobile")
    public DataSource getOGGDataSource(){
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "mobilejdbc")
    public JdbcTemplate getOggTemplate(@Qualifier("mobile") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }






}
