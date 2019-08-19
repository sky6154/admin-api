package blog.develobeer.adminApi.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Configuration
public class QuerydslConfig {
    @PersistenceContext(unitName = "blog")
    private EntityManager blogEntityManager;

    @PersistenceContext(unitName = "admin")
    private EntityManager adminEntityManager;

    @Bean(name="blogQueryFactory")
    public JPAQueryFactory blogQueryFactory(){
        return new JPAQueryFactory(blogEntityManager);
    }

    @Bean(name="adminQueryFactory")
    public JPAQueryFactory adminQueryFactory(){
        return new JPAQueryFactory(adminEntityManager);
    }
}
