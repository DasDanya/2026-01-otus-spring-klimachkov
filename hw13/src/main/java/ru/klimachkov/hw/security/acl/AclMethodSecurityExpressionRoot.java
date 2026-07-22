package ru.klimachkov.hw.security.acl;

import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.core.Authentication;

public class AclMethodSecurityExpressionRoot
        extends SecurityExpressionRoot
        implements AclMethodSecurityExpressionOperations {

    private Object filterObject;

    private Object returnObject;

    private Object target;

    public AclMethodSecurityExpressionRoot(Authentication authentication) {
        super(authentication);
    }

    @Override
    public Object getFilterObject() {
        return filterObject;
    }

    @Override
    public void setFilterObject(Object filterObject) {
        this.filterObject = filterObject;
    }

    @Override
    public Object getReturnObject() {
        return returnObject;
    }

    @Override
    public void setReturnObject(Object returnObject) {
        this.returnObject = returnObject;
    }

    @Override
    public Object getThis() {
        return target;
    }

    public void setThis(Object target) {
        this.target = target;
    }

    @Override
    public boolean isAdmin() {
        return hasRole("ADMIN");
    }

    @Override
    public boolean canWrite(Object targetId, String targetType) {
        return isAdminOrHasPermission(targetId, targetType, write);
    }

    @Override
    public boolean canDelete(Object targetId, String targetType) {
        return isAdminOrHasPermission(targetId, targetType, delete);
    }

    private boolean isAdminOrHasPermission(Object targetId, String targetType, Object permission) {
        return isAdmin() || hasPermission(targetId, targetType, permission);
    }
}
