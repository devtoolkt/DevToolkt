package dev.toolkt.core.collections

abstract class HandleIterator<E, HandleT : Any>(
    initialAdvancement: Advancement.Ahead<E, HandleT>?,
) : MutableIterator<E> {
    /**
     * The iterator advancement, i.e. the relative difference between the internal
     * and the externally perceived state.
     */
    sealed interface Advancement<E, HandleT : Any> {
        /**
         * The iterator's state after a successful next() call. The iterator
         * is unconditionally ready for a remove() call. The iterator is ready
         * for a next() call, but it might throw if it turns out that there's
         * no next element in the iteration.
         */
        class Abreast<E, HandleT : Any>(
            val lastHandle: HandleT,
        ) : Advancement<E, HandleT> {
            /**
             * The cached ahead state
             */
            private var peekedAhead: Ahead<E, HandleT>? = null

            fun peekAhead(
                goAhead: (HandleT) -> Ahead<E, HandleT>?,
            ): Ahead<E, HandleT>? = when (val peekedAhead = this.peekedAhead) {
                null -> {
                    val freshPeekedAhead = goAhead(lastHandle)

                    this.peekedAhead = freshPeekedAhead

                    freshPeekedAhead
                }

                else -> peekedAhead
            }

            override fun getAhead(
                goAhead: (HandleT) -> Ahead<E, HandleT>?,
            ): Ahead<E, HandleT>? = peekAhead(goAhead = goAhead)
        }

        /**
         * The iterator's initial state or the state after a remove() call. The
         * iterator is _not_ ready for another remove() call. The iterator is ready
         * for a next() call, but it will throw if there's no next element in
         * the iteration (we already know if that's the case, as we're "ahead").
         */
        data class Ahead<E, HandleT : Any>(
            val nextHandle: HandleT,
            val nextElement: E,
        ) : Advancement<E, HandleT> {
            override fun getAhead(
                goAhead: (HandleT) -> Ahead<E, HandleT>?,
            ): Ahead<E, HandleT>? = this
        }

        /**
         * Get the ahead state of the iterator, without changing the internal state (other than for caching purposes)
         */
        fun getAhead(
            /**
             * The function that might be called to retrieve the handle's successor
             */
            goAhead: (HandleT) -> Ahead<E, HandleT>?,
        ): Ahead<E, HandleT>?
    }

    /**
     * The current advancement, or null if the iteration ended
     */
    private var advancement: Advancement<E, HandleT>? = initialAdvancement

    final override fun remove() {
        when (val currentAdvancement = this.advancement) {
            is Advancement.Abreast<E, HandleT> -> {
                val currentHandle = currentAdvancement.lastHandle
                val ahead = currentAdvancement.peekAhead(this::goAhead)

                remove(handle = currentHandle)

                advancement = ahead
            }

            else -> {
                // Possible cases:
                // - Initial iteration state (next was never called at all)
                // - Iteration ended (previous next() call returned the last element, hasNext() == false)
                // - The iteration is ongoing, but remove() was already called
                throw IllegalStateException("`next` has not been called yet, or the most recent `next` call has already been followed by a remove call.")
            }
        }
    }

    final override fun next(): E {
        val ahead =
            advancement?.getAhead(this::goAhead) ?: throw NoSuchElementException("The iteration has no next element.")

        advancement = Advancement.Abreast(
            lastHandle = ahead.nextHandle,
        )

        return ahead.nextElement
    }

    final override fun hasNext(): Boolean = advancement?.getAhead(this::goAhead) != null

    protected abstract fun goAhead(handle: HandleT): Advancement.Ahead<E, HandleT>?

    protected abstract fun remove(handle: HandleT)
}
