package com.expediagroup.graphql.client

/**
 * Annotation to automatically exclude auto-generated client code from JaCoCo reports.
 *
 * Starting with JaCoCo 8.3, classes and methods annotated with `@Generated` annotation that has RUNTIME retention will be excluded from
 * code coverage report. We are using custom annotation instead of `javax.annotation.Generated` or `javax.annotation.processing.Generated`
 * as their retention policy is just SOURCE.
 */
@Target(allowedTargets = [AnnotationTarget.CLASS])
@Retention(value = AnnotationRetention.RUNTIME)
annotation class Generated
