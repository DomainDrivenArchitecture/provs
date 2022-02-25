package org.domaindrivenarchitecture.provs.test.tags

import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Tags
import org.junit.jupiter.api.Test

private const val CONTAINER_TEST = "containertest"
private const val EXTENSIVE_CONTAINER_TEST = "extensivecontainertest"
private const val CONTAINER_TEST_NON_CI = "containernonci"


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
@Tag(CONTAINER_TEST_NON_CI)
@Test
annotation class NonCi