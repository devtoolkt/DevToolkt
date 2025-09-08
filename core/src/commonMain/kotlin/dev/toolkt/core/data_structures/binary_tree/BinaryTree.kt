package dev.toolkt.core.data_structures.binary_tree

/**
 * A generic binary tree. Methods in this interface support only read-only access to the tree; read/write access is
 * supported through the [MutableUnconstrainedBinaryTree] / [MutableBalancedBinaryTree] interfaces.
 *
 * This interface doesn't require the tree to be balanced, but practical implementations will typically be balanced to
 * ensure good performance of tree operations.
 *
 * @param PayloadT The type of the payload stored in the tree's nodes
 * @param ColorT The type of the color stored in the tree's nodes. The meaning of "color" is unspecified and is up to
 * the specific implementation of the tree balancing strategy.
 */
interface BinaryTree<out PayloadT, out ColorT> {
    /**
     * A side of the tree or the tree's node, either left or right.
     */
    sealed class Side {
        data object Left : Side() {
            override val opposite: Side = Right

            override val directionTo = RotationDirection.CounterClockwise
        }

        data object Right : Side() {
            override val opposite: Side = Left

            override val directionTo = RotationDirection.Clockwise
        }

        /**
         * The opposite side to this side.
         */
        internal abstract val opposite: Side

        /**
         * The rotation direction that makes the parent take the position of
         * the child on this side
         */
        internal abstract val directionTo: RotationDirection

        /**
         * The rotation direction that makes the child on this side take the
         * position of its parent
         */
        internal val directionFrom: RotationDirection
            get() = directionTo.opposite
    }

    /**
     * A direction of rotation of a subtree around a node.
     */
    sealed class RotationDirection {
        data object Clockwise : RotationDirection() {
            override val opposite = CounterClockwise

            override val startSide = Side.Left
        }

        data object CounterClockwise : RotationDirection() {
            override val opposite = Clockwise

            override val startSide = Side.Right
        }

        abstract val opposite: RotationDirection

        abstract val startSide: Side

        val endSide: Side
            get() = startSide.opposite
    }

    /**
     * A stable handle to the node inside the tree. Invalidates once the node is
     * removed through this handle. If two handles correspond to the same node,
     * they compare equal.
     */
    interface NodeHandle<out PayloadT, out ColorT> {
        /**
         * Check whether this handle is valid, i.e. the node it refers to
         * is still present in the tree. Invalid handles cannot be used,
         * even for read-only operations. All node handles returned by
         * [BinaryTree] operations are valid right after the operation
         * is completed.
         */
        val isValid: Boolean
    }

    sealed interface Location<out PayloadT, out ColorT> {
        val parentHandle: NodeHandle<PayloadT, ColorT>?
    }

    /**
     * Location of the root node
     */
    data object RootLocation : Location<Nothing, Nothing> {
        override val parentHandle: NodeHandle<Nothing, Nothing>? = null
    }

    /**
     * Location relative to the parent node
     */
    data class RelativeLocation<out PayloadT, out ColorT>(
        /**
         * The handle to the parent node
         */
        override val parentHandle: NodeHandle<PayloadT, ColorT>,
        /**
         * The side of the parent node where the child is located
         */
        val side: Side,
    ) : Location<PayloadT, ColorT> {
        val siblingSide: Side
            get() = side.opposite

    }

    val currentRootHandle: NodeHandle<PayloadT, ColorT>?

    val size: Int

    /**
     * Resolve the [location] to a stable handle to the node in the tree.
     */
    fun resolve(
        location: Location<@UnsafeVariance PayloadT, @UnsafeVariance ColorT>,
    ): NodeHandle<PayloadT, ColorT>?

    /**
     * Get the payload of the node associated with the given [nodeHandle].
     */
    fun getPayload(
        nodeHandle: NodeHandle<@UnsafeVariance PayloadT, @UnsafeVariance ColorT>,
    ): PayloadT

    fun getSubtreeSize(
        subtreeRootHandle: BinaryTree.NodeHandle<@UnsafeVariance PayloadT, @UnsafeVariance ColorT>,
    ): Int

    fun getColor(
        nodeHandle: NodeHandle<@UnsafeVariance PayloadT, @UnsafeVariance ColorT>,
    ): ColorT

    /**
     * Get the handle to the in-order neighbour from the given [side] of the node associated with the given [nodeHandle]
     */
    fun getInOrderNeighbour(
        nodeHandle: NodeHandle<@UnsafeVariance PayloadT, @UnsafeVariance ColorT>,
        side: Side,
    ): NodeHandle<PayloadT, ColorT>?

    /**
     * Get the handle to the parent of the node associated with the given [nodeHandle].
     */
    fun getParent(
        nodeHandle: NodeHandle<@UnsafeVariance PayloadT, @UnsafeVariance ColorT>,
    ): NodeHandle<PayloadT, ColorT>?
}

fun <PayloadT, ColorT> BinaryTree<PayloadT, ColorT>.isEmpty(): Boolean = size == 0

fun <PayloadT, ColorT> BinaryTree<PayloadT, ColorT>.traverse(): Sequence<BinaryTree.NodeHandle<PayloadT, ColorT>> {
    val minimalNodeHandle = getMinimalDescendant() ?: return emptySequence()

    return generateSequence(
        minimalNodeHandle,
    ) { nodeHandle ->
        getInOrderSuccessor(nodeHandle = nodeHandle)
    }
}

/**
 * Get the handle to the child on the given [side] of the node associated
 * with the given [nodeHandle].
 */
fun <PayloadT, ColorT> BinaryTree<PayloadT, ColorT>.getChild(
    nodeHandle: BinaryTree.NodeHandle<PayloadT, ColorT>,
    side: BinaryTree.Side,
): BinaryTree.NodeHandle<PayloadT, ColorT>? = resolve(
    location = nodeHandle.getChildLocation(side = side),
)

/**
 * Get children of this node, starting from the given [side]. The first
 * child will be the "closer" child, the second one will be the "distant"
 * child.
 */
fun <PayloadT, ColorT> BinaryTree<PayloadT, ColorT>.getChildren(
    nodeHandle: BinaryTree.NodeHandle<PayloadT, ColorT>,
    side: BinaryTree.Side,
): Pair<
        BinaryTree.NodeHandle<PayloadT, ColorT>?,
        BinaryTree.NodeHandle<PayloadT, ColorT>?,
        > {
    val closerChild = getChild(
        nodeHandle = nodeHandle,
        side = side,
    )

    val distantChild = getChild(
        nodeHandle = nodeHandle,
        side = side.opposite,
    )

    return Pair(closerChild, distantChild)
}

/**
 * Get a sibling of a node occupying the given [location] in the tree.
 */
fun <PayloadT, ColorT> BinaryTree<PayloadT, ColorT>.getSibling(
    location: BinaryTree.RelativeLocation<PayloadT, ColorT>,
): BinaryTree.NodeHandle<PayloadT, ColorT>? = getChild(
    nodeHandle = location.parentHandle,
    side = location.siblingSide,
)

fun <PayloadT, ColorT> BinaryTree<PayloadT, ColorT>.getLeftChild(
    nodeHandle: BinaryTree.NodeHandle<PayloadT, ColorT>,
): BinaryTree.NodeHandle<PayloadT, ColorT>? = getChild(
    nodeHandle = nodeHandle,
    side = BinaryTree.Side.Left,
)

fun <PayloadT, ColorT> BinaryTree<PayloadT, ColorT>.getRightChild(
    nodeHandle: BinaryTree.NodeHandle<PayloadT, ColorT>,
): BinaryTree.NodeHandle<PayloadT, ColorT>? = getChild(
    nodeHandle = nodeHandle,
    side = BinaryTree.Side.Right,
)

/**
 * Get a relative location of the child on the given [side] of the node
 */
fun <PayloadT, ColorT> BinaryTree.NodeHandle<PayloadT, ColorT>.getChildLocation(
    side: BinaryTree.Side,
): BinaryTree.RelativeLocation<PayloadT, ColorT> = BinaryTree.RelativeLocation(
    parentHandle = this,
    side = side,
)

fun <PayloadT, ColorT> BinaryTree.NodeHandle<PayloadT, ColorT>.getLeftChildLocation(): BinaryTree.RelativeLocation<PayloadT, ColorT> =
    getChildLocation(
        side = BinaryTree.Side.Left,
    )

fun <PayloadT, ColorT> BinaryTree.NodeHandle<PayloadT, ColorT>.getRightChildLocation(): BinaryTree.RelativeLocation<PayloadT, ColorT> =
    getChildLocation(
        side = BinaryTree.Side.Right,
    )

fun <PayloadT, ColorT> BinaryTree<PayloadT, ColorT>.getChildSide(
    parentHandle: BinaryTree.NodeHandle<PayloadT, ColorT>,
    childHandle: BinaryTree.NodeHandle<PayloadT, ColorT>,
): BinaryTree.Side {
    val leftChildHandle = getChild(
        nodeHandle = parentHandle,
        side = BinaryTree.Side.Left,
    )

    val rightChildHandle = getChild(
        nodeHandle = parentHandle,
        side = BinaryTree.Side.Right,
    )

    return when (childHandle) {
        leftChildHandle -> BinaryTree.Side.Left

        rightChildHandle -> BinaryTree.Side.Right

        else -> {
            throw IllegalArgumentException("The given node is not a child of the given parent")
        }
    }
}

fun <PayloadT, ColorT> BinaryTree<PayloadT, ColorT>.locate(
    nodeHandle: BinaryTree.NodeHandle<PayloadT, ColorT>,
): BinaryTree.Location<PayloadT, ColorT> = locateRelatively(
    nodeHandle = nodeHandle,
) ?: BinaryTree.RootLocation

/**
 * @return A relative location of the node associated with [nodeHandle] in the tree,
 * or null if the node is the root of the tree (i.e. has no parent).
 */
fun <PayloadT, ColorT> BinaryTree<PayloadT, ColorT>.locateRelatively(
    nodeHandle: BinaryTree.NodeHandle<PayloadT, ColorT>,
): BinaryTree.RelativeLocation<PayloadT, ColorT>? {
    val parentHandle = getParent(
        nodeHandle = nodeHandle,
    ) ?: return null

    val side = getChildSide(
        parentHandle = parentHandle,
        childHandle = nodeHandle,
    )

    return BinaryTree.RelativeLocation(
        parentHandle = parentHandle,
        side = side,
    )
}

fun <PayloadT, ColorT> BinaryTree<PayloadT, ColorT>.getMinimalDescendant(): BinaryTree.NodeHandle<PayloadT, ColorT>? =
    getSideMostDescendant(
        side = BinaryTree.Side.Left,
    )

fun <PayloadT, ColorT> BinaryTree<PayloadT, ColorT>.getMaximalDescendant(): BinaryTree.NodeHandle<PayloadT, ColorT>? =
    getSideMostDescendant(
        side = BinaryTree.Side.Right,
    )

fun <PayloadT, ColorT> BinaryTree<PayloadT, ColorT>.getSideMostDescendant(
    side: BinaryTree.Side,
): BinaryTree.NodeHandle<PayloadT, ColorT>? = getSideMostFreeLocation(
    side = side,
).parentHandle


fun <PayloadT, ColorT> BinaryTree<PayloadT, ColorT>.getSideMostFreeLocation(
    side: BinaryTree.Side,
): BinaryTree.Location<PayloadT, ColorT> {
    val root = this.currentRootHandle ?: return BinaryTree.RootLocation

    return getSideMostFreeLocation(
        nodeHandle = root,
        side = side,
    )
}

fun <PayloadT, ColorT> BinaryTree<PayloadT, ColorT>.getSideMostFreeLocation(
    nodeHandle: BinaryTree.NodeHandle<PayloadT, ColorT>,
    side: BinaryTree.Side,
): BinaryTree.RelativeLocation<PayloadT, ColorT> {
    val sideChildHandle = getChild(
        nodeHandle = nodeHandle,
        side = side,
    ) ?: return BinaryTree.RelativeLocation(
        parentHandle = nodeHandle,
        side = side,
    )

    return getSideMostFreeLocation(
        nodeHandle = sideChildHandle,
        side = side,
    )
}

fun <PayloadT, ColorT> BinaryTree<PayloadT, ColorT>.getInOrderPredecessor(
    nodeHandle: BinaryTree.NodeHandle<PayloadT, ColorT>,
): BinaryTree.NodeHandle<PayloadT, ColorT>? = getInOrderNeighbour(
    nodeHandle = nodeHandle,
    side = BinaryTree.Side.Left,
)

fun <PayloadT, ColorT> BinaryTree<PayloadT, ColorT>.getInOrderSuccessor(
    nodeHandle: BinaryTree.NodeHandle<PayloadT, ColorT>,
): BinaryTree.NodeHandle<PayloadT, ColorT>? = getInOrderNeighbour(
    nodeHandle = nodeHandle,
    side = BinaryTree.Side.Right,
)

fun <PayloadT, ColorT> BinaryTree<PayloadT, ColorT>.getNextInOrderFreeLocation(
    nodeHandle: BinaryTree.NodeHandle<PayloadT, ColorT>,
    side: BinaryTree.Side,
): BinaryTree.RelativeLocation<PayloadT, ColorT> {
    val sideChildLocation = nodeHandle.getChildLocation(side = side)

    val sideChildHandle = resolve(
        location = sideChildLocation,
    ) ?: return sideChildLocation

    return getSideMostFreeLocation(
        nodeHandle = sideChildHandle,
        side = side.opposite,
    )
}
