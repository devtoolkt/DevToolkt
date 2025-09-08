package dev.toolkt.core.data_structures.binary_tree.lookup

sealed class BinaryTreeLookupCommand {
    data object Remove : BinaryTreeLookupCommand()
}
