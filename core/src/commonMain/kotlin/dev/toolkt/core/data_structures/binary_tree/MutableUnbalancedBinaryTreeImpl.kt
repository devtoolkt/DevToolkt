package dev.toolkt.core.data_structures.binary_tree

import dev.toolkt.core.data_structures.binary_tree.BinaryTree.Side
import dev.toolkt.core.data_structures.binary_tree.MutableUnbalancedBinaryTree.SwapResult
import dev.toolkt.core.data_structures.binary_tree.MutableUnbalancedBinaryTreeImpl.NodeHandleImpl
import dev.toolkt.core.data_structures.binary_tree.MutableUnbalancedBinaryTreeImpl.ProperNode
import dev.toolkt.core.data_structures.binary_tree.MutableUnbalancedBinaryTreeImpl.ProperNode.InOrderNeighbourRelation
import kotlin.jvm.JvmInline

class MutableUnbalancedBinaryTreeImpl<PayloadT, ColorT> internal constructor(
    internal val origin: OriginNode<PayloadT, ColorT> = OriginNode(),
) : MutableUnbalancedBinaryTree<PayloadT, ColorT> {
    internal sealed interface ParentNode<PayloadT, ColorT> {
        fun buildUpLink(
            child: ProperNode<PayloadT, ColorT>,
        ): UpLink<PayloadT, ColorT>
    }

    internal class OriginNode<PayloadT, ColorT>(
        private var mutableRoot: ProperNode<PayloadT, ColorT>? = null,
    ) : ParentNode<PayloadT, ColorT> {
        val root: ProperNode<PayloadT, ColorT>?
            get() = mutableRoot

        fun setRoot(
            newRoot: ProperNode<PayloadT, ColorT>?,
        ) {
            mutableRoot = newRoot
        }

        override fun buildUpLink(
            child: ProperNode<PayloadT, ColorT>,
        ): UpLink<PayloadT, ColorT> {
            if (child != root) {
                throw IllegalArgumentException("Child node must be the root of the tree")
            }

            return OriginLink(
                origin = this,
            )
        }

    }

    internal class ProperNode<PayloadT, ColorT>(
        private var mutableParent: ParentNode<PayloadT, ColorT>,
        private var mutableLeftDownLink: DownLink<PayloadT, ColorT>? = null,
        private var mutableRightDownLink: DownLink<PayloadT, ColorT>? = null,
        private var mutableSubtreeSize: Int = 1,
        private var mutableColor: ColorT,
        private var mutablePayload: PayloadT,
    ) : ParentNode<PayloadT, ColorT> {
        enum class Validity {
            Valid, Invalid,
        }

        data class IntegrityVerificationResult(
            val computedSubtreeSize: Int,
        )

        /**
         * An in-order predecessor / successor relation
         */
        sealed class InOrderNeighbourRelation<PayloadT, ColorT> {
            /**
             * The in-order neighbour is the node's ascendant
             */
            data class Ascendant<PayloadT, ColorT>(
                val ascendantNeighbourLink: NeighbourLink<PayloadT, ColorT>,
            ) : InOrderNeighbourRelation<PayloadT, ColorT>() {
                override val directDownLink: DownLink<PayloadT, ColorT>
                    get() = ascendantNeighbourLink

                override val neighbour: ProperNode<PayloadT, ColorT>
                    get() = ascendantNeighbourLink.neighbour
            }

            /**
             * The in-order neighbour is the node's descendant
             */
            sealed class Descendant<PayloadT, ColorT> : InOrderNeighbourRelation<PayloadT, ColorT>() {
                final override val neighbour: ProperNode<PayloadT, ColorT>
                    get() = descendantNeighbour

                /**
                 * The number of tree levels that separate the node from its in-order
                 * neighbour
                 */
                abstract val depth: Int

                /**
                 * Node's direct child on the respective side (left in the case of
                 * the predecessor relation, right in the case of the successor relation).
                 *
                 * Might be the in-order neighbour itself
                 */
                internal abstract val directChild: ProperNode<PayloadT, ColorT>

                /**
                 * The in-order neighbour
                 */
                internal abstract val descendantNeighbour: ProperNode<PayloadT, ColorT>
            }

            /**
             * A relation in which the child itself is the in-order neighbour
             */
            data class Close<PayloadT, ColorT>(
                /**
                 * Node's child, the in-order neighbour
                 */
                val childLink: ChildLink<PayloadT, ColorT>,
            ) : Descendant<PayloadT, ColorT>() {
                override val directDownLink: DownLink<PayloadT, ColorT>
                    get() = childLink

                override val depth: Int = 1

                override val directChild: ProperNode<PayloadT, ColorT>
                    get() = childLink.child

                override val descendantNeighbour: ProperNode<PayloadT, ColorT>
                    get() = directChild
            }

            /**
             * A relation in which the in-order neighbour is a descendant of the
             * node's child on the opposite side
             */
            data class Distant<PayloadT, ColorT>(
                /**
                 * Node's child on the path between itself and its neighbour
                 */
                val intermediateChildLink: ChildLink<PayloadT, ColorT>,
                /**
                 * The node's neighbour, separated by at least one node
                 */
                val distantNeighbour: ProperNode<PayloadT, ColorT>,
                override val depth: Int,
            ) : Descendant<PayloadT, ColorT>() {
                val intermediateChild: ProperNode<PayloadT, ColorT>
                    get() = intermediateChildLink.child

                override val directDownLink: DownLink<PayloadT, ColorT>
                    get() = intermediateChildLink

                override val directChild: ProperNode<PayloadT, ColorT>
                    get() = intermediateChildLink.child

                override val descendantNeighbour: ProperNode<PayloadT, ColorT>
                    get() = distantNeighbour
            }

            val asDescendant: Descendant<PayloadT, ColorT>?
                get() = this as? Descendant<PayloadT, ColorT>

            internal abstract val directDownLink: DownLink<PayloadT, ColorT>

            internal abstract val neighbour: ProperNode<PayloadT, ColorT>
        }

        private var validity = Validity.Valid

        val isValid: Boolean
            get() = validity == Validity.Valid

        companion object {
            fun <PayloadT, ColorT> linkUp(
                descendant: ProperNode<PayloadT, ColorT>,
                side: BinaryTree.Side,
                ascendant: ProperNode<PayloadT, ColorT>?,
            ) {
                descendant.setDownLink(
                    downLink = ascendant?.let {
                        NeighbourLink(neighbour = it)
                    },
                    side = side,
                )
            }

            fun <PayloadT, ColorT> linkChild(
                parent: ProperNode<PayloadT, ColorT>,
                side: Side,
                child: ProperNode<PayloadT, ColorT>,
            ) {
                parent.setDownLink(
                    downLink = ChildLink(child = child),
                    side = side,
                )

                child.setParent(
                    parent = parent,
                )
            }

            fun <PayloadT, ColorT> linkDown(
                parent: ProperNode<PayloadT, ColorT>,
                side: Side,
                downLink: DownLink<PayloadT, ColorT>?,
            ) {
                parent.setDownLink(
                    downLink = downLink,
                    side = side,
                )

                if (downLink is ChildLink<PayloadT, ColorT>) {
                    downLink.child.setParent(
                        parent = parent,
                    )
                }
            }
        }

        override fun buildUpLink(
            child: ProperNode<PayloadT, ColorT>,
        ): UpLink<PayloadT, ColorT> = ParentLink(
            parent = this,
            childSide = getChildSide(child = child),
        )

        val parent: ParentNode<PayloadT, ColorT>
            get() = mutableParent

        val upLink: UpLink<PayloadT, ColorT>
            get() = parent.buildUpLink(
                child = this,
            )

        val parentLink: ParentLink<PayloadT, ColorT>?
            get() = upLink as? ParentLink<PayloadT, ColorT>

        val properParent: ProperNode<PayloadT, ColorT>?
            get() = parentLink?.parent

        val leftChild: ProperNode<PayloadT, ColorT>?
            get() = mutableLeftDownLink?.child

        val rightChild: ProperNode<PayloadT, ColorT>?
            get() = mutableRightDownLink?.child

        val subtreeSize: Int
            get() = mutableSubtreeSize

        val payload: PayloadT
            get() = mutablePayload

        val color: ColorT
            get() = mutableColor

        fun isLeaf(): Boolean = leftChild == null && rightChild == null

        fun getDownLink(
            side: Side,
        ): DownLink<PayloadT, ColorT>? = when (side) {
            Side.Left -> mutableLeftDownLink
            Side.Right -> mutableRightDownLink
        }

        fun getChild(
            side: Side,
        ): ProperNode<PayloadT, ColorT>? = when (side) {
            Side.Left -> leftChild
            Side.Right -> rightChild
        }

        fun getSideMostDescendant(
            side: Side,
        ): Pair<ProperNode<PayloadT, ColorT>, Int>? {
            val sideChild = getChild(
                side = side,
            ) ?: return null

            val (descendant, depth) = sideChild.getSideMostQuasiDescendant(
                side = side,
            )

            return Pair(descendant, depth + 1)
        }

        fun getSideMostQuasiDescendant(
            side: Side,
        ): Pair<ProperNode<PayloadT, ColorT>, Int> {
            val sideChild = getChild(
                side = side,
            ) ?: return Pair(this, 0)

            val (quasiDescendant, depth) = sideChild.getSideMostQuasiDescendant(
                side = side,
            )

            return Pair(quasiDescendant, depth + 1)
        }

        fun getInOrderNeighbour(
            side: Side,
        ): ProperNode<PayloadT, ColorT>? = getInOrderNeighbourRelation(
            side = side,
        )?.neighbour

        fun getInOrderNeighbourRelation(
            side: Side,
        ): InOrderNeighbourRelation<PayloadT, ColorT>? {
            val sideDownLink = getDownLink(
                side = side,
            ) ?: return null

            when (sideDownLink) {
                is ChildLink<PayloadT, ColorT> -> {
                    val (distantNeighbour, depth) = sideDownLink.child.getSideMostDescendant(
                        side = side.opposite,
                    ) ?: return InOrderNeighbourRelation.Close(
                        childLink = sideDownLink,
                    )

                    return InOrderNeighbourRelation.Distant(
                        intermediateChildLink = sideDownLink,
                        distantNeighbour = distantNeighbour,
                        depth = depth,
                    )
                }

                is NeighbourLink<PayloadT, ColorT> -> {
                    return InOrderNeighbourRelation.Ascendant(
                        ascendantNeighbourLink = sideDownLink,
                    )
                }
            }
        }

        fun getChildSide(
            child: ProperNode<PayloadT, ColorT>,
        ): Side = when {
            child === leftChild -> Side.Left
            child === rightChild -> Side.Right
            else -> throw IllegalArgumentException("The given node is not a child of this node")
        }

        fun setDownLink(
            downLink: DownLink<PayloadT, ColorT>?,
            side: Side,
        ) {
            requireValid()

            when (side) {
                Side.Left -> mutableLeftDownLink = downLink
                Side.Right -> mutableRightDownLink = downLink
            }
        }

        fun setParent(
            parent: ParentNode<PayloadT, ColorT>,
        ) {
            requireValid()

            if (parent == this) {
                throw IllegalArgumentException("Cannot set a node as its own parent")
            }

            mutableParent = parent
        }

        fun setSubtreeSize(
            size: Int,
        ) {
            requireValid()

            require(size >= 0)

            mutableSubtreeSize = size
        }

        fun setPayload(
            payload: PayloadT,
        ) {
            requireValid()

            mutablePayload = payload
        }

        fun setColor(
            color: ColorT,
        ) {
            requireValid()

            mutableColor = color
        }

        fun verifyIntegrity(
            expectedParent: ParentNode<PayloadT, ColorT>,
        ): IntegrityVerificationResult {
            requireValid()

            if (parent != expectedParent) {
                throw AssertionError("Inconsistent parent, expected: $expectedParent, actual: ${this.parent}")
            }

            val computedLeftSubtreeSize = leftChild?.verifyIntegrity(
                expectedParent = this,
            )?.computedSubtreeSize ?: 0

            val computedRightSubtreeSize = rightChild?.verifyIntegrity(
                expectedParent = this,
            )?.computedSubtreeSize ?: 0

            val computedTotalSubtreeSize = computedLeftSubtreeSize + 1 + computedRightSubtreeSize

            if (mutableSubtreeSize != computedTotalSubtreeSize) {
                throw AssertionError("Inconsistent cached subtree size, true: $computedTotalSubtreeSize, cached: $mutableSubtreeSize")
            }

            return IntegrityVerificationResult(
                computedSubtreeSize = computedTotalSubtreeSize,
            )
        }

        fun updateSubtreeSizeRecursively(
            /**
             * The number of gained descendants. If negative, it means the node
             * lost descendants.
             */
            delta: Int,
        ) {
            requireValid()

            setSubtreeSize(subtreeSize + delta)

            properParent?.updateSubtreeSizeRecursively(
                delta = delta,
            )
        }

        private fun requireValid() {
            if (validity == Validity.Invalid) {
                throw IllegalStateException("The node is already invalidated")
            }
        }

        fun invalidate() {
            if (validity == Validity.Invalid) {
                throw IllegalStateException("The node is already invalidated")
            }

            validity = Validity.Invalid
        }
    }

    internal sealed class UpLink<PayloadT, ColorT> {
        abstract val parent: ParentNode<PayloadT, ColorT>

        abstract val childLocation: BinaryTree.Location<PayloadT, ColorT>

        abstract fun unlink()

        abstract fun relink(
            newChild: ProperNode<PayloadT, ColorT>,
        )
    }

    internal class OriginLink<PayloadT, ColorT>(
        private val origin: OriginNode<PayloadT, ColorT>,
    ) : UpLink<PayloadT, ColorT>() {
        override val parent: ParentNode<PayloadT, ColorT>
            get() = origin

        override val childLocation: BinaryTree.Location<PayloadT, ColorT>
            get() = BinaryTree.RootLocation

        override fun unlink() {
            origin.setRoot(
                newRoot = null,
            )
        }

        override fun relink(
            newChild: ProperNode<PayloadT, ColorT>,
        ) {
            origin.setRoot(
                newRoot = newChild
            )

            newChild.setParent(
                parent = origin,
            )
        }
    }

    internal class ParentLink<PayloadT, ColorT>(
        override val parent: ProperNode<PayloadT, ColorT>,
        val childSide: Side,
    ) : UpLink<PayloadT, ColorT>() {
        val siblingSide: Side
            get() = childSide.opposite

        val child: ProperNode<PayloadT, ColorT>?
            get() = parent.getChild(side = childSide)

        val sibling: ProperNode<PayloadT, ColorT>?
            get() = parent.getChild(side = siblingSide)

        override val childLocation: BinaryTree.Location<PayloadT, ColorT>
            get() = BinaryTree.RelativeLocation(
                parentHandle = parent.pack(),
                side = childSide,
            )

        override fun unlink() {
            val child = this.child ?: throw IllegalStateException("The child location is non-occupied")

            val oppositeChildDownLink = child.getDownLink(
                side = childSide.opposite,
            )

            if (oppositeChildDownLink !is NeighbourLink<PayloadT, ColorT> || oppositeChildDownLink.neighbour != parent) {
                throw IllegalStateException("Inconsistent child's opposite down-link")
            }

            val childDownLink = child.getDownLink(
                side = childSide,
            )

            parent.setDownLink(
                downLink = childDownLink,
                side = childSide,
            )
        }

        override fun relink(
            newChild: ProperNode<PayloadT, ColorT>,
        ) {
            parent.setDownLink(
                downLink = ChildLink(
                    child = newChild,
                ),
                side = childSide,
            )

            newChild.setParent(
                parent = parent,
            )
        }
    }

    internal sealed interface DownLink<PayloadT, ColorT> {
        val child: ProperNode<PayloadT, ColorT>?

        val neighbour: ProperNode<PayloadT, ColorT>?
    }

    /**
     * A link to the child
     */
    @JvmInline
    internal value class ChildLink<PayloadT, ColorT>(
        override val child: ProperNode<PayloadT, ColorT>,
    ) : DownLink<PayloadT, ColorT> {
        override val neighbour: Nothing?
            get() = null
    }

    /**
     * A link to the in-order neighbour
     */
    @JvmInline
    internal value class NeighbourLink<PayloadT, ColorT>(
        override val neighbour: ProperNode<PayloadT, ColorT>,
    ) : DownLink<PayloadT, ColorT> {
        override val child: Nothing?
            get() = null
    }

    @JvmInline
    internal value class NodeHandleImpl<PayloadT, ColorT>(
        private val properNode: ProperNode<PayloadT, ColorT>,
    ) : BinaryTree.NodeHandle<PayloadT, ColorT> {
        override val isValid: Boolean
            get() = properNode.isValid

        fun resolve(): ProperNode<PayloadT, ColorT> {
            if (!properNode.isValid) {
                throw IllegalStateException("The node has been invalidated")
            }

            return properNode
        }
    }

    override fun attach(
        location: BinaryTree.Location<PayloadT, ColorT>,
        payload: PayloadT,
        color: ColorT,
    ): BinaryTree.NodeHandle<PayloadT, ColorT> {
        val attachedNode = when (location) {
            BinaryTree.RootLocation -> {
                if (origin.root != null) {
                    throw IllegalStateException("The tree already has a root")
                }

                val rootNode = ProperNode(
                    mutableParent = origin,
                    mutableColor = color,
                    mutablePayload = payload,
                )

                origin.setRoot(
                    newRoot = rootNode,
                )

                rootNode
            }

            is BinaryTree.RelativeLocation<PayloadT, ColorT> -> {
                val parent = location.parentHandle.unpack()
                val side = location.side

                val existingDownLink = parent.getDownLink(side = side)

                if (existingDownLink is ChildLink<PayloadT, ColorT>) {
                    throw IllegalStateException("Cannot insert leaf to a non-empty location")
                }

                val newNode = ProperNode(
                    mutableParent = parent,
                    mutableColor = color,
                    mutablePayload = payload,
                )

                ProperNode.linkChild(
                    parent = parent,
                    side = side,
                    child = newNode,
                )

                ProperNode.linkUp(
                    descendant = newNode,
                    side = side.opposite,
                    ascendant = parent,
                )

                ProperNode.linkDown(
                    parent = newNode,
                    side = side,
                    downLink = existingDownLink,
                )

                parent.updateSubtreeSizeRecursively(
                    delta = +1,
                )

                newNode
            }
        }

        return attachedNode.pack()
    }

    override fun cutOff(
        leafHandle: BinaryTree.NodeHandle<PayloadT, ColorT>,
    ): BinaryTree.Location<PayloadT, ColorT> {
        val node = leafHandle.unpack()

        if (!node.isLeaf()) {
            throw IllegalArgumentException("The given node is not a leaf")
        }

        val upLink = node.upLink

        val properParent = upLink.parent.asProper

        upLink.unlink()

        properParent?.updateSubtreeSizeRecursively(
            delta = -1,
        )

        node.invalidate()

        return upLink.childLocation
    }

    override fun collapse(
        nodeHandle: BinaryTree.NodeHandle<PayloadT, ColorT>,
    ): BinaryTree.NodeHandle<PayloadT, ColorT> {
        val node = nodeHandle.unpack()

        val elevatedNode = collapse(
            node = node,
            side = Side.Left,
        ) ?: collapse(
            node = node,
            side = Side.Right,
        ) ?: throw IllegalArgumentException("Cannot collapse a node with two children")

        return elevatedNode.pack()
    }

    private fun collapse(
        node: MutableUnbalancedBinaryTreeImpl.ProperNode<PayloadT, ColorT>,
        side: Side,
    ): MutableUnbalancedBinaryTreeImpl.ProperNode<PayloadT, ColorT>? {
        val upLink = node.upLink

        val oppositeDownLink = node.getDownLink(
            side = side.opposite,
        )

        if (oppositeDownLink is ChildLink<PayloadT, ColorT>) {
            return null
        }

        val neighbourRelation = node.getInOrderNeighbourRelation(
            side = side,
        )?.asDescendant ?: return null

        val singleChild = neighbourRelation.directChild
        val descendantNeighbour = neighbourRelation.descendantNeighbour

        run {
            val loopLink = descendantNeighbour.getDownLink(side = side.opposite)

            if (loopLink?.neighbour != node) {
                throw AssertionError("Inconsistent loop link")
            }
        }

        upLink.relink(singleChild)

        ProperNode.linkUp(
            descendant = descendantNeighbour,
            side = side.opposite,
            ascendant = oppositeDownLink?.neighbour,
        )

        upLink.parent.asProper?.updateSubtreeSizeRecursively(
            delta = -1,
        )

        node.invalidate()

        return singleChild
    }

    override fun swap(
        nodeHandle: BinaryTree.NodeHandle<PayloadT, ColorT>,
        side: Side,
    ): SwapResult<PayloadT, ColorT> {
        val topNode = nodeHandle.unpack()
        val topUpLink = topNode.upLink

        val neighbourRelation = topNode.getInOrderNeighbourRelation(
            side = side,
        )?.asDescendant
            ?: throw AssertionError("The node doesn't have an in-order descendant neighbour on the $side side")

        val neighbour = neighbourRelation.neighbour

        // Prepare the relinking transactionally

        val relinkNeighbour = prepareSwapNodeRelink(
            sourceNode = neighbour,
            targetNode = topNode,
            side = side.opposite,
        )

        val relinkInterconnection = prepareSwapInterconnectionRelink(
            topNode = topNode,
            neighbourRelation = neighbourRelation,
            side = side,
        )

        val relinkTopNode = prepareSwapNodeRelink(
            sourceNode = topNode,
            targetNode = neighbour,
            side = side,
        )

        // Start the actual relinking

        // Relink the top up-link
        topUpLink.relink(
            newChild = neighbour,
        )

        // Relink the top node's subtree from the opposite side
        relinkNeighbour()

        // Relink the connection on the path between the top node and the neighbour
        relinkInterconnection()

        // Invert the neighbour down-link between the top node and the neighbour
        ProperNode.linkUp(
            descendant = topNode,
            side = side.opposite,
            ascendant = neighbour,
        )

        // Relink the neighbour's subtree from the primary side
        relinkTopNode()

        return SwapResult(
            neighbourHandle = neighbourRelation.neighbour.pack(),
            neighbourDepth = neighbourRelation.depth,
        )
    }

    /**
     * Prepare the symmetric part of the swap operation
     *
     * @return a function that performs the relinking
     */
    private fun prepareSwapNodeRelink(
        sourceNode: ProperNode<PayloadT, ColorT>,
        targetNode: ProperNode<PayloadT, ColorT>,
        side: Side,
    ): () -> Unit {
        val targetNeighbourRelation = targetNode.getInOrderNeighbourRelation(
            side = side,
        )

        val sourceNodeColor = targetNode.color
        val sourceNodeSubtreeSize = targetNode.subtreeSize

        return {
            ProperNode.linkDown(
                parent = sourceNode,
                side = side,
                downLink = targetNeighbourRelation?.directDownLink,
            )

            targetNeighbourRelation?.asDescendant?.descendantNeighbour?.let { oppositeDescendantNeighbour ->
                ProperNode.linkUp(
                    descendant = oppositeDescendantNeighbour,
                    side = side.opposite,
                    ascendant = sourceNode,
                )
            }

            sourceNode.setColor(sourceNodeColor)
            sourceNode.setSubtreeSize(sourceNodeSubtreeSize)
        }
    }

    private fun prepareSwapInterconnectionRelink(
        topNode: ProperNode<PayloadT, ColorT>,
        neighbourRelation: InOrderNeighbourRelation.Descendant<PayloadT, ColorT>,
        side: Side,
    ): () -> Unit {
        val neighbour = neighbourRelation.neighbour

        return when (neighbourRelation) {
            is InOrderNeighbourRelation.Close<PayloadT, ColorT> -> ({
                ProperNode.linkChild(
                    parent = neighbour,
                    side = side,
                    child = topNode,
                )
            })

            is InOrderNeighbourRelation.Distant<PayloadT, ColorT> -> {
                val intermediateChild = neighbourRelation.intermediateChild
                val distantNeighbourParent = neighbourRelation.distantNeighbour.properParent
                    ?: throw AssertionError("The in-order neighbour should have a proper parent")

                ({
                    ProperNode.linkChild(
                        parent = neighbour,
                        side = side,
                        child = intermediateChild,
                    )

                    ProperNode.linkChild(
                        parent = distantNeighbourParent,
                        side = side.opposite,
                        child = topNode,
                    )
                })
            }
        }
    }

    override fun rotate(
        pivotNodeHandle: BinaryTree.NodeHandle<PayloadT, ColorT>,
        direction: BinaryTree.RotationDirection,
    ): BinaryTree.NodeHandle<PayloadT, ColorT> {
        val pivotNode = pivotNodeHandle.unpack()

        val parentLink = pivotNode.upLink

        val ascendingChild = pivotNode.getChild(side = direction.startSide)
            ?: throw IllegalStateException("The pivot node has no child on the ${direction.startSide} side")

        // If the downlink in the close grandchild's place was a neighbour link, it must link to the pivot node.
        // This link should be discarded.
        val closeGrandchild = ascendingChild.getChild(side = direction.endSide)

        val distantGrandchild = ascendingChild.getChild(side = direction.startSide)

        when (closeGrandchild) {
            null -> {
                pivotNode.setDownLink(
                    downLink = NeighbourLink(
                        neighbour = ascendingChild,
                    ),
                    side = direction.startSide,
                )
            }

            else -> {
                ProperNode.linkChild(
                    parent = pivotNode,
                    child = closeGrandchild,
                    side = direction.startSide,
                )
            }
        }

        ProperNode.linkChild(
            parent = ascendingChild,
            child = pivotNode,
            side = direction.endSide,
        )

        parentLink.relink(
            newChild = ascendingChild,
        )

        val originalPivotNodeSubtreeSize = pivotNode.subtreeSize
        val originalDistantGrandchildSize = distantGrandchild.subtreeSizeOrZero

        // The ascending node has exactly the same set of descendants as the pivot
        // node had before (with the exception that the parent-child relation
        // inverted, but that doesn't affect the subtree size)
        ascendingChild.setSubtreeSize(originalPivotNodeSubtreeSize)

        // The pivot node lost descendants in the subtree of its original
        // distant grandchild. It also lost the ascending child.
        pivotNode.setSubtreeSize(originalPivotNodeSubtreeSize - originalDistantGrandchildSize - 1)

        return ascendingChild.pack()
    }

    override val currentRootHandle: BinaryTree.NodeHandle<PayloadT, ColorT>?
        get() = origin.root?.pack()

    override val size: Int
        get() = origin.root?.subtreeSize ?: 0

    private fun resolveImpl(
        location: BinaryTree.Location<PayloadT, ColorT>,
    ): ProperNode<PayloadT, ColorT>? = when (location) {
        BinaryTree.RootLocation -> origin.root

        is BinaryTree.RelativeLocation<PayloadT, ColorT> -> {
            val parent = location.parentHandle.unpack()
            val side = location.side

            parent.getChild(
                side = side,
            )
        }
    }

    override fun resolve(
        location: BinaryTree.Location<PayloadT, ColorT>,
    ): BinaryTree.NodeHandle<PayloadT, ColorT>? = resolveImpl(
        location = location,
    )?.pack()

    /**
     * Get the payload of the node associated with the given [nodeHandle].
     */
    override fun getPayload(
        nodeHandle: BinaryTree.NodeHandle<PayloadT, ColorT>,
    ): PayloadT = nodeHandle.unpack().payload

    override fun getSubtreeSize(
        subtreeRootHandle: BinaryTree.NodeHandle<PayloadT, ColorT>,
    ): Int = subtreeRootHandle.unpack().subtreeSizeOrZero

    override fun getColor(
        nodeHandle: BinaryTree.NodeHandle<PayloadT, ColorT>,
    ): ColorT = nodeHandle.unpack().color

    override fun getInOrderNeighbour(
        nodeHandle: BinaryTree.NodeHandle<PayloadT, ColorT>,
        side: Side,
    ): BinaryTree.NodeHandle<PayloadT, ColorT>? {
        val node = nodeHandle.unpack()

        val neighbour = node.getInOrderNeighbour(side = side) ?: return null

        return neighbour.pack()
    }

    /**
     * Get the handle to the parent of the node associated with the given [nodeHandle].
     */
    override fun getParent(
        nodeHandle: BinaryTree.NodeHandle<PayloadT, ColorT>,
    ): BinaryTree.NodeHandle<PayloadT, ColorT>? {
        val node = nodeHandle.unpack()
        return node.properParent?.pack()
    }

    override fun setPayload(
        nodeHandle: BinaryTree.NodeHandle<PayloadT, ColorT>,
        payload: PayloadT,
    ) {
        val node = nodeHandle.unpack()

        node.setPayload(
            payload = payload,
        )
    }

    override fun setColor(
        nodeHandle: BinaryTree.NodeHandle<PayloadT, ColorT>,
        newColor: ColorT,
    ) {
        val node = nodeHandle.unpack()

        node.setColor(
            color = newColor,
        )
    }
}

private val <PayloadT, ColorT> ProperNode<PayloadT, ColorT>?.subtreeSizeOrZero: Int
    get() = this?.subtreeSize ?: 0

private val <PayloadT, ColorT> MutableUnbalancedBinaryTreeImpl.ParentNode<PayloadT, ColorT>.asProper: ProperNode<PayloadT, ColorT>?
    get() = this as? ProperNode<PayloadT, ColorT>

private fun <PayloadT, ColorT> BinaryTree.NodeHandle<PayloadT, ColorT>.unpack(): ProperNode<PayloadT, ColorT> {
    @Suppress("UNCHECKED_CAST") val nodeHandleImpl =
        this as? NodeHandleImpl<PayloadT, ColorT> ?: throw IllegalArgumentException("Unrelated handle type")

    return nodeHandleImpl.resolve()
}

private fun <PayloadT, ColorT> ProperNode<PayloadT, ColorT>.pack(): BinaryTree.NodeHandle<PayloadT, ColorT> =
    NodeHandleImpl(
        properNode = this,
    )
