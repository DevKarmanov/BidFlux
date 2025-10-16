package van.karm.complaint.infrastructure.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import van.karm.complaint.application.service.query.EntityQueryService;
import van.karm.complaint.domain.model.archive.ComplaintArchive;
import van.karm.complaint.domain.model.complaint.Complaint;
import van.karm.complaint.infrastructure.service.query.GenericEntityQueryService;
import van.karm.shared.application.provider.AllowedFieldsProvider;
import van.karm.shared.application.rule.FieldRule;
import van.karm.shared.infrastructure.query.QueryExecutor;

@Configuration
public class QueryConfig {

    @Bean
    public EntityQueryService<Complaint> complaintQueryService(
            QueryExecutor executor,
            @Qualifier("complaint-field-provider") AllowedFieldsProvider fieldsProvider,
            @Qualifier("complaint-page-field-provider") AllowedFieldsProvider pageFieldsProvider,
            @Qualifier("complaint-rule") FieldRule rule) {
        return new GenericEntityQueryService<>(executor, fieldsProvider, pageFieldsProvider, rule, Complaint.class);
    }

    @Bean
    public EntityQueryService<ComplaintArchive> complaintArchiveQueryService(
            QueryExecutor executor,
            @Qualifier("complaint-archive-field-provider") AllowedFieldsProvider fieldsProvider,
            @Qualifier("complaint-archive-page-field-provider") AllowedFieldsProvider pageFieldsProvider,
            @Qualifier("complaint-rule") FieldRule rule) {
        return new GenericEntityQueryService<>(executor, fieldsProvider, pageFieldsProvider, rule, ComplaintArchive.class);
    }
}