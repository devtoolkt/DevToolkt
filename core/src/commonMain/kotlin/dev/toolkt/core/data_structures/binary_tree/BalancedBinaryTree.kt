package dev.toolkt.core.data_structures.binary_tree

/**
 * A generic binary tree, guaranteed to remain balanced. Methods in this interface support only read-only access to the
 * tree; read/write access is supported through the [MutableBalancedBinaryTree] interface. See [BinaryTree] for more details.
 */
interface BalancedBinaryTree<out PayloadT, out ColorT> : BinaryTree<PayloadT, ColorT>
