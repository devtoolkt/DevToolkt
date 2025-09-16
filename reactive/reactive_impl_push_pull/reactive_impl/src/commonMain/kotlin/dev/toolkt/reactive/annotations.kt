package dev.toolkt.reactive

/**
 * Annotation to indicate that a property is a part of a volatile processing state that should be cleared before
 * the transaction ends.
 */
@Target(AnnotationTarget.PROPERTY)
annotation class VolatileProcessingState
