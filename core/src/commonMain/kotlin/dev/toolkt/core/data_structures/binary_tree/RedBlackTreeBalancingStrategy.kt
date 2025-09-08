package dev.toolkt.core.data_structures.binary_tree

import dev.toolkt.core.errors.assert

class RedBlackTreeBalancingStrategy<PayloadT>() : BinaryTreeBalancingStrategy<PayloadT, RedBlackColor>() {
    override val defaultColor: RedBlackColor
        get() = RedBlackColor.Red

    companion object;

    override fun rebalanceAfterAttach(
        internalTree: MutableUnconstrainedBinaryTree<PayloadT, RedBlackColor>,
        attachedNodeHandle: BinaryTree.NodeHandle<PayloadT, RedBlackColor>,
    ): RebalanceResult<PayloadT, RedBlackColor> = fixPotentialRedViolationRecursively(
        internalTree = internalTree,
        nodeHandle = attachedNodeHandle,
    )

    /**
     * Fix a (potential) red violation in the subtree with the root corresponding
     * to the [nodeHandle].
     */
    private fun fixPotentialRedViolationRecursively(
        internalTree: MutableUnconstrainedBinaryTree<PayloadT, RedBlackColor>,
        nodeHandle: BinaryTree.NodeHandle<PayloadT, RedBlackColor>,
    ): RebalanceResult<PayloadT, RedBlackColor> {
        val color = internalTree.getColor(nodeHandle = nodeHandle)

        assert(color == RedBlackColor.Red) {
            throw AssertionError("Red violation fixup must start with a red node")
        }

        val relativeLocation = internalTree.locateRelatively(
            nodeHandle = nodeHandle,
        ) ?: run {
            // Case #3
            // If this is the root, it can't be in a red violation with its
            // parent, as it has no parent. We can fix the red violation by simply changing the root's color to black.

            internalTree.paint(
                nodeHandle = nodeHandle,
                newColor = RedBlackColor.Black,
            )

            return RebalanceResult(
                retractionHeight = 0,
                finalLocation = BinaryTree.RootLocation,
            )
        }

        val parentHandle = relativeLocation.parentHandle

        val side = relativeLocation.side

        if (internalTree.getColor(nodeHandle = parentHandle) == RedBlackColor.Black) {
            // Case #1
            // If the parent is black, there's no red violation between this
            // node and its parent
            return RebalanceResult(
                finalLocation = relativeLocation,
                retractionHeight = 0,
            )
        }

        // From now on, we know that the parent is red

        val parentRelativeLocation = internalTree.locateRelatively(
            nodeHandle = parentHandle,
        ) ?: run {
            // Case #4
            // The parent is the root, so it can't get into a red
            // violation with its parent (as it has no parent). We can fix the
            // red violation by simply changing the root's color to black.

            internalTree.paint(
                nodeHandle = parentHandle,
                newColor = RedBlackColor.Black,
            )

            // Although we touched the parent's color, we didn't really move it
            return RebalanceResult(
                finalLocation = relativeLocation,
                retractionHeight = 0,
            )
        }

        val grandparentHandle = parentRelativeLocation.parentHandle

        val grandparentLocation = internalTree.locate(nodeHandle = grandparentHandle)

        assert(internalTree.getColor(nodeHandle = grandparentHandle) == RedBlackColor.Black) {
            "The grandparent must be black, as the parent is red"
        }

        val uncleHandle = internalTree.getSibling(location = parentRelativeLocation)
        val uncleSide = parentRelativeLocation.siblingSide
        val uncleColor = uncleHandle?.let { internalTree.getColor(nodeHandle = it) }

        return when (uncleColor) {
            RedBlackColor.Red -> {
                // Case #2

                // As the uncle is also red (like this node and its parent),
                // we can swap the color of the grandparent (black) with the
                // color of its children (red). This fixed the red violation
                // between this node and its parent.

                internalTree.paint(
                    nodeHandle = parentHandle,
                    newColor = RedBlackColor.Black,
                )

                internalTree.paint(
                    nodeHandle = uncleHandle,
                    newColor = RedBlackColor.Black,
                )

                internalTree.paint(
                    nodeHandle = grandparentHandle,
                    newColor = RedBlackColor.Red,
                )

                // The subtree starting at the fixed node is now balanced

                // While we fixed one red violation, we might've introduced
                // another. Let's fix this recursively.
                val recursiveResult = fixPotentialRedViolationRecursively(
                    internalTree = internalTree,
                    nodeHandle = grandparentHandle,
                )

                recursiveResult.copy(
                    retractionHeight = recursiveResult.retractionHeight + 1,
                )
            }

            else -> {
                // N and P are red, he uncle is black

                if (side == uncleSide) {
                    // Case #5: N is the closer grandchild of G.
                    // We can reduce this to a fit for case #6 by a single rotation
                    internalTree.rotate(
                        pivotNodeHandle = parentHandle,
                        direction = uncleSide.directionFrom,
                    )

                    // This operation pushes the fixed node one level down, but
                    // this doesn't affect the remaining logic
                }

                // Case #6: N is the distant grandchild of G
                val newSubtreeRootHandle = internalTree.rotate(
                    pivotNodeHandle = grandparentHandle,
                    direction = uncleSide.directionTo,
                )

                assert(newSubtreeRootHandle == nodeHandle || newSubtreeRootHandle == parentHandle) {
                    "The new subtree root must be either this node or its parent"
                }

                internalTree.paint(
                    nodeHandle = newSubtreeRootHandle,
                    newColor = RedBlackColor.Black,
                )

                internalTree.paint(
                    nodeHandle = grandparentHandle,
                    newColor = RedBlackColor.Red,
                )

                // The violation is fixed!
                RebalanceResult(
                    retractionHeight = 2,
                    finalLocation = grandparentLocation,
                )
            }
        }
    }

    override fun rebalanceAfterCutOff(
        internalTree: MutableUnconstrainedBinaryTree<PayloadT, RedBlackColor>,
        cutOffLeafLocation: BinaryTree.RelativeLocation<PayloadT, RedBlackColor>,
        cutOffLeafColor: RedBlackColor,
    ): RebalanceResult<PayloadT, RedBlackColor> = when (cutOffLeafColor) {
        RedBlackColor.Black -> fixBlackViolationRecursively(
            internalTree = internalTree,
            nodeHandle = null,
            relativeLocation = cutOffLeafLocation,
        )

        else -> RebalanceResult(
            finalLocation = cutOffLeafLocation,
            retractionHeight = 0,
        )
    }

    private fun fixBlackViolationRecursively(
        internalTree: MutableUnconstrainedBinaryTree<PayloadT, RedBlackColor>,
        /**
         * A handle to the node to be fixed, null represents a null node
         */
        nodeHandle: BinaryTree.NodeHandle<PayloadT, RedBlackColor>?,
        /**
         * The relative location of the node to be fixed
         */
        relativeLocation: BinaryTree.RelativeLocation<PayloadT, RedBlackColor>,
    ): RebalanceResult<PayloadT, RedBlackColor> {
        val color = nodeHandle?.let {
            internalTree.getColor(nodeHandle = it)
        }

        assert(color != RedBlackColor.Red) {
            "Black violation fixup phase cannot start with a red node"
        }

        // The parent of the fixed node doesn't change during a single fixup phase.
        // Other parts of the close family (sibling, nephews) may change.
        val parentHandle = relativeLocation.parentHandle

        // The side of the fixed node in relation to its parent. It also doesn't
        // change during a single fixup phase.
        val side = relativeLocation.side

        // The primary cases considered below (#3 - #6) are quasi-final, i.e.
        // are either final or lead to a final case. All these cases require
        // that the fixed node has a proper sibling and leave the tree in the
        // state when it still has a proper sibling.

        // Case #3: The sibling S is red, so P and the nephews C and D have to be black
        val wasCase3Applied = run {
            // If the node is proper, it has a proper sibling from Conclusion 2.
            // If it's a null node (which is possible on the first recursion level),
            // its sibling also must be proper, as it must have a black height one,
            // which was the black height of the node we deleted.
            val siblingHandle = internalTree.getSibling(
                location = relativeLocation,
            ) ?: throw AssertionError("The node has no sibling")

            val siblingColor = internalTree.getColor(nodeHandle = siblingHandle)

            if (siblingColor != RedBlackColor.Red) return@run false

            internalTree.rotate(
                pivotNodeHandle = parentHandle,
                direction = side.directionTo,
            )

            internalTree.paint(
                nodeHandle = parentHandle,
                newColor = RedBlackColor.Red,
            )

            internalTree.paint(
                nodeHandle = siblingHandle,
                newColor = RedBlackColor.Black,
            )

            // Now the parent is red and the sibling (old close nephew) is black. Depending on the color of the nephews,
            // it's a match for cases #4, #5 or #6
            true
        }

        // Case #4: P is red (the sibling S is black) and S’s children are black
        run {
            val parentColor = internalTree.getColor(nodeHandle = parentHandle)
            if (parentColor != RedBlackColor.Red) return@run

            val siblingHandle = internalTree.getSibling(
                location = relativeLocation,
            ) ?: throw AssertionError("The node has no sibling")

            val siblingColor = internalTree.getColor(nodeHandle = siblingHandle)

            assert(siblingColor == RedBlackColor.Black) {
                "The sibling must be black, as the parent is red"
            }

            val (closeNephewHandle, distantNephewHandle) = internalTree.getChildren(
                nodeHandle = siblingHandle,
                side = side,
            )

            val closeNephewColor = closeNephewHandle?.let {
                internalTree.getColor(nodeHandle = it)
            }

            val distantNephewColor = distantNephewHandle?.let {
                internalTree.getColor(nodeHandle = it)
            }

            if (closeNephewColor == RedBlackColor.Red) return@run
            if (distantNephewColor == RedBlackColor.Red) return@run

            internalTree.paint(
                nodeHandle = parentHandle,
                newColor = RedBlackColor.Black,
            )

            internalTree.paint(
                nodeHandle = siblingHandle,
                newColor = RedBlackColor.Red,
            )

            // The violation was fixed!
            return RebalanceResult(
                finalLocation = relativeLocation,
                retractionHeight = 0,
            )
        }

        // Case #5 S’s close child C is red (the sibling S is black), and S’s distant child D is black
        val wasCase5Applied = run {
            val siblingHandle = internalTree.getSibling(
                location = relativeLocation,
            ) ?: throw AssertionError("The node has no sibling")

            val siblingColor = internalTree.getColor(nodeHandle = siblingHandle)

            val (closeNephewHandle, distantNephewHandle) = internalTree.getChildren(
                nodeHandle = siblingHandle,
                side = side,
            )

            val closeNephewColor = closeNephewHandle?.let {
                internalTree.getColor(nodeHandle = it)
            }

            val distantNephewColor = distantNephewHandle?.let {
                internalTree.getColor(nodeHandle = it)
            }

            if (closeNephewColor != RedBlackColor.Red) return@run false
            if (distantNephewColor == RedBlackColor.Red) return@run false

            // From now on, we know that the close nephew is red and the distant nephew is effectively black

            assert(siblingColor == RedBlackColor.Black) {
                "The sibling must be black, as the close nephew is red"
            }

            internalTree.rotate(
                pivotNodeHandle = siblingHandle,
                direction = side.directionFrom,
            )

            internalTree.paint(
                nodeHandle = closeNephewHandle,
                newColor = RedBlackColor.Black,
            )

            internalTree.paint(
                nodeHandle = siblingHandle,
                newColor = RedBlackColor.Red,
            )

            // Now the parent color is unchanged and the new sibling (old close nephew) is black. The distant nephew (old
            // sibling) is now red. This is a fit for case #6.
            true
        }

        // Case #6: S’s distant child D is red (the sibling S is black)
        run {
            val parentColor = internalTree.getColor(nodeHandle = parentHandle)

            val siblingHandle = internalTree.getSibling(
                location = relativeLocation,
            ) ?: throw AssertionError("The node has no sibling")

            val siblingColor = internalTree.getColor(nodeHandle = siblingHandle)

            val (_, distantNephewHandle) = internalTree.getChildren(
                nodeHandle = siblingHandle,
                side = side,
            )

            val distantNephewColor = distantNephewHandle?.let {
                internalTree.getColor(nodeHandle = it)
            }

            if (distantNephewColor != RedBlackColor.Red) return@run

            // From now on, we know that the distant nephew is red

            assert(siblingColor == RedBlackColor.Black) {
                "The sibling must be black, as the distant nephew is red"
            }

            internalTree.rotate(
                pivotNodeHandle = parentHandle,
                direction = side.directionTo,
            )

            internalTree.setColor(
                nodeHandle = parentHandle,
                newColor = RedBlackColor.Black,
            )

            internalTree.setColor(
                nodeHandle = siblingHandle,
                newColor = parentColor,
            )

            internalTree.paint(
                nodeHandle = distantNephewHandle,
                newColor = RedBlackColor.Black,
            )

            // The violation was fixed!
            return RebalanceResult(
                finalLocation = relativeLocation,
                retractionHeight = 0,
            )
        }

        if (wasCase3Applied) {
            throw AssertionError("Case #3 application should always lead to Case #4 or Case #6 application")
        }

        if (wasCase5Applied) {
            throw AssertionError("Case #5 application should always lead to Case #6 application")
        }

        // Now we know that none of the primary cases applied

        val parentColor = internalTree.getColor(nodeHandle = parentHandle)

        assert(parentColor == RedBlackColor.Black) {
            // If the parent was red, it should've triggered case #4 (if both nephews were black) or cases #5/#6 (otherwise)
            "The parent is not black, which is unexpected at this point"
        }

        val siblingHandle = internalTree.getSibling(
            location = relativeLocation,
        ) ?: throw AssertionError("The node has no sibling")

        val siblingColor = internalTree.getColor(nodeHandle = siblingHandle)

        assert(siblingColor == RedBlackColor.Black) {
            // If the sibling was red, it should've triggered case #3 (and later one of the final cases)
            "The sibling is not black, which is unexpected at this point"
        }

        val (closeNephewHandle, distantNephewHandle) = internalTree.getChildren(
            nodeHandle = siblingHandle,
            side = side,
        )

        val closeNephewColor = closeNephewHandle?.let {
            internalTree.getColor(nodeHandle = it)
        }

        val distantNephewColor = distantNephewHandle?.let {
            internalTree.getColor(nodeHandle = it)
        }

        assert(distantNephewColor != RedBlackColor.Red) {
            // If the distant nephew was red, it should've triggered case #6 (a final case)
            "The distant nephew is red, which is unexpected at this point"
        }

        assert(closeNephewColor != RedBlackColor.Red) {
            // We just checked that the distant nephew is black
            // If the close nephew was red, it should've triggered case #5 (and later case #6)
            "The close nephew is red, which is unexpected at this point"
        }

        // Case #2: P, S, and S’s children are black
        internalTree.paint(
            nodeHandle = siblingHandle,
            newColor = RedBlackColor.Red,
        )

        // After paining the sibling red, the subtree starting at this node is balanced

        when (val parentRelativeLocation = internalTree.locateRelatively(nodeHandle = parentHandle)) {
            null -> {
                // Case #1: The parent is root
                // The violation was fixed!
                return RebalanceResult(
                    finalLocation = relativeLocation,
                    retractionHeight = 0,
                )
            }

            else -> {
                // Although the subtree is balanced (has the same black height on each path), it's still one less than
                // all the other paths in the whole tree. We need to fix it recursively.
                val recursiveResult = fixBlackViolationRecursively(
                    internalTree = internalTree,
                    nodeHandle = parentHandle,
                    relativeLocation = parentRelativeLocation,
                )

                return recursiveResult.copy(
                    retractionHeight = recursiveResult.retractionHeight + 1,
                )
            }
        }
    }

    override fun rebalanceAfterCollapse(
        internalTree: MutableUnconstrainedBinaryTree<PayloadT, RedBlackColor>,
        elevatedNodeHandle: BinaryTree.NodeHandle<PayloadT, RedBlackColor>,
    ): RebalanceResult<PayloadT, RedBlackColor> {
        // As the elevated node was a single child of its parent, it must be
        // a red node
        internalTree.paint(
            nodeHandle = elevatedNodeHandle,
            newColor = RedBlackColor.Black,
        )

        val elevatedNodeLocation = internalTree.locate(nodeHandle = elevatedNodeHandle)

        return RebalanceResult(
            finalLocation = elevatedNodeLocation,
            retractionHeight = 0,
        )
    }
}
