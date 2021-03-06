package blog.develobeer.adminApi.config.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "adminEntityManagerFactory",
        transactionManagerRef = "adminTransactionManager",
        basePackages = {"blog.develobeer.adminApi.repo.admin"})
public class AdminDataSourceConfig implements Serializable {
    private static final long serialVersionUID = -2308261361241623735L;

    private Map<String, Object> jpaProperties() {
        Map<String, Object> props = new HashMap<>();
        props.put("hibernate.show_sql", true);
        props.put("hibernate.database_platform", "org.hibernate.dialect.MySQL5InnoDBDialect");
        props.put("hibernate.hbm2ddl.auto", "none");
        props.put("hibernate.format_sql", "true");

        return props;
    }

    @Bean(name = "adminHikariConfig")
    @ConfigurationProperties(prefix = "spring.datasource.admin")
    public HikariConfig adminHikariConfig() {
        return new HikariConfig();
    }

    @Bean(name = "adminDataSource")
    public DataSource adminDataSource(@Qualifier("adminHikariConfig") HikariConfig adminHikariConfig) {
        return new HikariDataSource(adminHikariConfig);
    }

    @Bean(name = "adminEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean adminEntityManagerFactory(
            EntityManagerFactoryBuilder builder, @Qualifier("adminDataSource") DataSource adminDataSource) {
        return builder
                .dataSource(adminDataSource)
                .packages("blog.develobeer.adminApi.domain.admin")
                .persistenceUnit("admin")
                .properties(this.jpaProperties())
                .build();
    }

    @Bean(name = "adminTransactionManager")
    public PlatformTransactionManager adminTransactionManager(
            @Qualifier("adminEntityManagerFactory") EntityManagerFactory adminEntityManagerFactory) {
        return new JpaTransactionManager(adminEntityManagerFactory);
    }
}