package ru.klimachkov.hw.security.acl;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.expression.EvaluationContext;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;

import java.util.function.Supplier;

public class AclMethodSecurityExpressionHandler extends DefaultMethodSecurityExpressionHandler {

    @Override
    protected MethodSecurityExpressionOperations createSecurityExpressionRoot(
            Authentication authentication,
            MethodInvocation invocation
    ) {
        AclMethodSecurityExpressionRoot root = new AclMethodSecurityExpressionRoot(authentication);
        root.setThis(invocation.getThis());
        root.setPermissionEvaluator(getPermissionEvaluator());
        root.setTrustResolver(getTrustResolver());
        root.setRoleHierarchy(getRoleHierarchy());
        root.setDefaultRolePrefix(getDefaultRolePrefix());

        return root;
    }

    @Override
    public EvaluationContext createEvaluationContext(
            Supplier<Authentication> authentication,
            MethodInvocation invocation
    ) {
        return createEvaluationContext(authentication.get(), invocation);
    }
}
