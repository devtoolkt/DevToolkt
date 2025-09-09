package dev.toolkt.reactive.test_utils

class TransformationVerifier2<SourceT1, SourceT2, TransformedT>(
    private val transformDirectly: (SourceT1, SourceT2) -> TransformedT,
) {
    private var transformationCount = 0

    fun transform(
        source1: SourceT1,
        source2: SourceT2,
    ): TransformedT {
        ++transformationCount

        return transformDirectly(source1, source2)
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
