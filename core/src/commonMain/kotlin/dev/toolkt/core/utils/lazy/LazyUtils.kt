package dev.toolkt.core.utils.lazy

object LazyUtils {
    fun <ValueT : Any, ResultT> looped(
        block: (Lazy<ValueT>) -> Pair<ResultT, ValueT>,
    ): ResultT {
        val loopedLazy = LoopedLazy<ValueT>()

        val (result, value) = block(loopedLazy)

        loopedLazy.loop(value)

        return result
    }
}

fun <ValueT, TransformedValueT> Lazy<ValueT>.map(
    transform: (ValueT) -> TransformedValueT,
): Lazy<TransformedValueT> = object : Lazy<TransformedValueT> {
    override val value: TransformedValueT
        get() = transform(this@map.value)

    override fun isInitialized(): Boolean = this@map.isInitialized()
}
