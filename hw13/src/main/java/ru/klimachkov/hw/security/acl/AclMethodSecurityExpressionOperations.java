package ru.klimachkov.hw.security.acl;

import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;

public interface AclMethodSecurityExpressionOperations extends MethodSecurityExpressionOperations {

    boolean isAdmin();

    boolean canWrite(Object targetId, String targetType);

    boolean canDelete(Object targetId, String targetType);
}
