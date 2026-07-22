package ru.klimachkov.hw.security.acl;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.acls.AclPermissionCacheOptimizer;
import org.springframework.security.acls.AclPermissionEvaluator;
import org.springframework.security.acls.domain.AclAuthorizationStrategy;
import org.springframework.security.acls.domain.AclAuthorizationStrategyImpl;
import org.springframework.security.acls.domain.ConsoleAuditLogger;
import org.springframework.security.acls.domain.DefaultPermissionGrantingStrategy;
import org.springframework.security.acls.domain.SpringCacheBasedAclCache;
import org.springframework.security.acls.jdbc.BasicLookupStrategy;
import org.springframework.security.acls.jdbc.JdbcMutableAclService;
import org.springframework.security.acls.jdbc.LookupStrategy;
import org.springframework.security.acls.model.AclCache;
import org.springframework.security.acls.model.AclService;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.acls.model.PermissionGrantingStrategy;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.sql.DataSource;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class AclConfig {

    private final DataSource dataSource;

    @Bean
    public AclAuthorizationStrategy aclAuthorizationStrategy() {
        return new AclAuthorizationStrategyImpl(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }

    @Bean
    public PermissionGrantingStrategy permissionGrantingStrategy() {
        return new DefaultPermissionGrantingStrategy(new ConsoleAuditLogger());
    }

    @Bean
    public AclCache aclCache() {
        return new SpringCacheBasedAclCache(
                new ConcurrentMapCache("acl_cache"),
                permissionGrantingStrategy(),
                aclAuthorizationStrategy()
        );
    }

    @Bean
    public LookupStrategy lookupStrategy() {
        return new BasicLookupStrategy(
                dataSource,
                aclCache(),
                aclAuthorizationStrategy(),
                permissionGrantingStrategy()
        );
    }

    @Bean
    public MutableAclService aclService() {
        JdbcMutableAclService service = new JdbcMutableAclService(dataSource, lookupStrategy(), aclCache());

        // identity() и scope_identity() не работают в используемой версии H2
        service.setSidIdentityQuery("SELECT CURRVAL('acl_sid_seq')");
        service.setClassIdentityQuery("SELECT CURRVAL('acl_class_seq')");

        return service;
    }

    @Bean
    public MethodSecurityExpressionHandler methodSecurityExpressionHandler() {
        AclMethodSecurityExpressionHandler expressionHandler = new AclMethodSecurityExpressionHandler();
        AclService aclService = aclService();

        expressionHandler.setPermissionEvaluator(new AclPermissionEvaluator(aclService));
        expressionHandler.setPermissionCacheOptimizer(new AclPermissionCacheOptimizer(aclService));

        return expressionHandler;
    }
}
