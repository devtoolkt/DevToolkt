package dev.toolkt.core.collections

import dev.toolkt.core.collections.StableCollection.Handle
import dev.toolkt.core.data_structures.binary_tree.BinaryTree
import dev.toolkt.core.data_structures.binary_tree.RedBlackColor
import dev.toolkt.core.data_structures.binary_tree.MutableBalancedBinaryTree
import dev.toolkt.core.data_structures.binary_tree.lookup.find
import dev.toolkt.core.data_structures.binary_tree.traverse
import kotlin.jvm.JvmInline

class MutableTreeSet<E : Comparable<E>> internal constructor() : AbstractMutableSet<E>(), MutableStableSet<E> {
    @JvmInline
    internal value class TreeSetHandle<E> internal constructor(
        internal val nodeHandle: BinaryTree.NodeHandle<E, RedBlackColor>,
    ) : Handle<E>

    private val elementTree = MutableBalancedBinaryTree.redBlack<E>()

    override val size: Int
        get() = elementTree.size

    override fun iterator(): MutableIterator<E> = MutableBalancedBinaryTreeIterator(
        tree = elementTree,
    )

    override val handles: Sequence<Handle<E>>
        get() = elementTree.traverse().map { it.pack() }

    override fun lookup(element: E): Handle<E>? {
        val location = elementTree.find(payload = element)
        val nodeHandle = elementTree.resolve(location = location) ?: return null
        return nodeHandle.pack()
    }

    override fun getVia(handle: Handle<E>): E? {
        val nodeHandle = handle.unpack() ?: return null
        return elementTree.getPayload(nodeHandle = nodeHandle)
    }

    override fun stableIterator(): StableIterator<E>? = mutableStableIterator()

    override fun add(
        element: E,
    ): Boolean = addEx(element) != null

    override fun addEx(element: E): Handle<E>? {
        val location = elementTree.find(element)

        val existingNodeHandle = elementTree.resolve(location = location)

        if (existingNodeHandle != null) {
            return null
        }

        val insertedNodeHandle = elementTree.insert(
            location = location,
            payload = element,
        )

        return insertedNodeHandle.pack()
    }

    override fun remove(
        element: E,
    ): Boolean {
        val handle = lookup(element = element) ?: return false

        removeVia(handle = handle)

        return true
    }

    override fun removeVia(handle: Handle<E>): E? {
        val nodeHandle = handle.unpack() ?: return null

        val removedElement = elementTree.getPayload(nodeHandle = nodeHandle)

        elementTree.remove(nodeHandle = nodeHandle)

        return removedElement
    }

    override fun mutableStableIterator(): MutableStableIterator<E>? = MutableBalancedBinaryTreeStableIterator.iterate(
        mutableTree = elementTree,
    )

    override fun contains(element: E): Boolean {
        val location = elementTree.find(element)
        return elementTree.resolve(location = location) != null
    }

}

fun <E : Comparable<E>> mutableTreeSetOf(
    vararg elements: E,
): MutableTreeSet<E> {
    val set = MutableTreeSet<E>()

    for (element in elements) {
        set.add(element)
    }

    return set
}

private fun <E> Handle<E>.unpack(): BinaryTree.NodeHandle<E, RedBlackColor>? {
    this as? MutableTreeSet.TreeSetHandle<E> ?: throw IllegalArgumentException(
        "Handle is not a TreeSetHandle: $this"
    )

    return when {
        nodeHandle.isValid -> nodeHandle
        else -> null
    }
}

private fun <E> BinaryTree.NodeHandle<E, RedBlackColor>.pack(): Handle<E> = MutableTreeSet.TreeSetHandle(
    nodeHandle = this,
)
