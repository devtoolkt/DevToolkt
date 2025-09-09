package dev.toolkt.reactive.test_utils

class TransformationVerifier<SourceT, TransformedT>(
    private val transformDirectly: (SourceT) -> TransformedT,
) {
    private var transformationCount = 0

    fun transform(
        source: SourceT,
    ): TransformedT {
        ++transformationCount

        return transformDirectly(source)
    }

    /**
     * Resets the transformation count to zero and returns the count before reset.
     */
    fun getAndResetTransformationCount(): Int {
        val count = transformationCount

        transformationCount = 0

        return count
    }
}
