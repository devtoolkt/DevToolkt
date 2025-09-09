package dev.toolkt.reactive

class MomentContext internal constructor() : PureContext() {
    companion object {
        fun <ResultT> executeExternally(
            block: context(MomentContext) () -> ResultT,
        ): ResultT {
            TODO()
        }
    }
}
