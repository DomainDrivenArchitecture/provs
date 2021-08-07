package org.domaindrivenarchitecture.provs.test.tags

import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test

private const val CONTAINER_TEST = "containertest"
private const val CONTAINER_TEST_NON_CI = "containernonci"


@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@kotlin.annotation.Retention
@Tag(CONTAINER_TEST)
@Test
annotation class ContainerTest


@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@kotlin.annotation.Retention
@Tag(CONTAINER_TEST_NON_CI)
@Test
annotation class NonCi