package van.karm.shared.infrastructure.config;

import jakarta.persistence.EntityManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import van.karm.shared.infrastructure.query.QueryExecutor;
import van.karm.shared.infrastructure.query.QueryExecutorImpl;
import van.karm.shared.infrastructure.query.builder.JPQLQueryBuilder;
import van.karm.shared.infrastructure.query.builder.QueryBuilder;
import van.karm.shared.infrastructure.query.runner.JpaQueryRunner;
import van.karm.shared.infrastructure.query.runner.QueryRunner;
import van.karm.shared.infrastructure.query.runner.page.JpaPagedQueryRunner;
import van.karm.shared.infrastructure.query.runner.page.PagedQueryRunner;
import van.karm.shared.infrastructure.query.selector.DefaultFieldSelector;
import van.karm.shared.infrastructure.query.selector.FieldSelector;

@Configuration
public class SharedQueryAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(FieldSelector.class)
    public FieldSelector fieldSelector() {
        return new DefaultFieldSelector();
    }

    @Bean
    @ConditionalOnMissingBean(QueryBuilder.class)
    public QueryBuilder queryBuilder() {
        return new JPQLQueryBuilder();
    }

    @Bean
    @ConditionalOnMissingBean(QueryRunner.class)
    public QueryRunner queryRunner(EntityManager em) {
        return new JpaQueryRunner(em);
    }

    @Bean
    @ConditionalOnMissingBean(PagedQueryRunner.class)
    public PagedQueryRunner pagedQueryRunner(EntityManager em) {return new JpaPagedQueryRunner(em);}


    @Bean
    @ConditionalOnMissingBean(QueryExecutor.class)
    public QueryExecutor queryExecutor(FieldSelector fieldSelector,
                                       QueryBuilder queryBuilder,
                                       QueryRunner queryRunner,
                                       PagedQueryRunner pagedQueryRunner) {
        return new QueryExecutorImpl(fieldSelector, queryBuilder, queryRunner,pagedQueryRunner);
    }
}
