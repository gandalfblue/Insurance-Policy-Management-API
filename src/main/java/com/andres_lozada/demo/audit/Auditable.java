package com.andres_lozada.demo.audit;

import com.andres_lozada.demo.enums.AuditOperation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a service method as auditable.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Auditable {

    String entity();

    AuditOperation operation();
}
