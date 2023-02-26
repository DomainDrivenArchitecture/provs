package org.domaindrivenarchitecture.provs.test.tags

import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Tags
import org.junit.jupiter.api.Test

private const val CONTAINER_TEST = "containertest"
private const val EXTENSIVE_CONTAINER_TEST = "extensivecontainertest"
private const val NON_CI = "nonci"


@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@Retention
@Tag(CONTAINER_TEST)
@Test
annotation class ContainerTest


@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@Retention
@Tags(
    Tag(CONTAINER_TEST),
    Tag(EXTENSIVE_CONTAINER_TEST)
)
@Test
annotation class ExtensiveContainerTest


@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@Retention
@Tag(NON_CI)
@Test
// For test which do not run in ci pipeline
annotation class NonCi