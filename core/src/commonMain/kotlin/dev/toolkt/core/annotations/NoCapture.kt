package dev.toolkt.core.annotations

/**
 * Annotation to indicate that a local variable should not be captured by a lambda or a local function, most likely
 * because it would prevent its garbage collection.
 */
@Target(AnnotationTarget.LOCAL_VARIABLE)
annotation class NoCapture
