package com.unnamed.b.atv.model

import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.unnamed.b.atv.R
import com.unnamed.b.atv.view.AndroidTreeView
import com.unnamed.b.atv.view.TreeNodeWrapperView
import java.util.*

/**
 * Created by Bogdan Melnychuk on 2/10/15.
 * Converted to Kolin by Kumar Shivang on 16/07/17
 */
class TreeNode(val value: Any?) {

    var id: Int = 0
        private set
    private var mLastId: Int = 0
    var parent: TreeNode? = null
        private set
    var isSelected: Boolean = false
        get() = isSelectable && field
    var isSelectable = true
    private val children: MutableList<TreeNode>
    private var mViewHolder: BaseNodeViewHolder<*>? = null
    private var mClickListener: TreeNodeClickListener? = null
    private var mLongClickListener: TreeNodeLongClickListener? = null
    private var mExpanded: Boolean = false

    private fun generateId(): Int {
        return ++mLastId
    }

    init {
        children = ArrayList<TreeNode>()
    }

    fun addChild(childNode: TreeNode): TreeNode {
        childNode.parent = this
        childNode.id = generateId()
        children.add(childNode)
        return this
    }

    fun addChildren(vararg nodes: TreeNode): TreeNode {
        for (n in nodes) {
            addChild(n)
        }
        return this
    }

    fun addChildren(nodes: Collection<TreeNode>): TreeNode {
        for (n in nodes) {
            addChild(n)
        }
        return this
    }

    fun deleteChild(child: TreeNode): Int {
        for (i in children.indices) {
            if (child.id == children[i].id) {
                children.removeAt(i)
                return i
            }
        }
        return -1
    }

    fun getChildren(): List<TreeNode> {
        return Collections.unmodifiableList(children)
    }

    fun size(): Int {
        return children.size
    }

    val isLeaf: Boolean
        get() = size() == 0

    fun isExpanded(): Boolean {
        return mExpanded
    }

    fun setExpanded(expanded: Boolean): TreeNode {
        mExpanded = expanded
        return this
    }

    val path: String
        get() {
            val path = StringBuilder()
            var node = this
            while (node.parent != null) {
                path.append(node.id)
                node = node.parent!!
                if (node.parent != null) {
                    path.append(NODES_ID_SEPARATOR)
                }
            }
            return path.toString()
        }


    val level: Int
        get() {
            var level = 0
            var root = this
            while (root.parent != null) {
                root = root.parent!!
                level++
            }
            return level
        }

    val isLastChild: Boolean
        get() {
            if (!isRoot) {
                val parentSize = parent!!.children.size
                if (parentSize > 0) {
                    val parentChildren = parent!!.children
                    return parentChildren[parentSize - 1].id == id
                }
            }
            return false
        }

    fun setViewHolder(viewHolder: BaseNodeViewHolder<*>?): TreeNode {
        mViewHolder = viewHolder
        if (viewHolder != null) {
            viewHolder.mNode = this
        }
        return this
    }

    fun setClickListener(listener: TreeNodeClickListener): TreeNode {
        mClickListener = listener
        return this
    }

    fun getClickListener(): TreeNodeClickListener? {
        return this.mClickListener
    }

    fun setLongClickListener(listener: TreeNodeLongClickListener): TreeNode {
        mLongClickListener = listener
        return this
    }

    fun getLongClickListener(): TreeNodeLongClickListener? {
        return mLongClickListener
    }

    fun getViewHolder(): BaseNodeViewHolder<*>? {
        return mViewHolder
    }

    val isFirstChild: Boolean
        get() {
            if (!isRoot) {
                val parentChildren = parent!!.children
                return parentChildren[0].id == id
            }
            return false
        }

    val isRoot: Boolean
        get() = parent == null

    val root: TreeNode
        get() {
            var root = this
            while (root.parent != null) {
                root = root.parent!!
            }
            return root
        }

    interface TreeNodeClickListener {
        fun onClick(node: TreeNode, value: Any)
    }

    interface TreeNodeLongClickListener {
        fun onLongClick(node: TreeNode, value: Any): Boolean
    }

    abstract class BaseNodeViewHolder<in E>(protected var context: Context) {
        var treeView: AndroidTreeView? = null
            protected set
        var mNode: TreeNode? = null
        private var mView: View? = null
        open var containerStyle: Int = 0

        val view: View
            get() {
                if (mView != null) {
                    return mView!!
                }
                val nodeView = nodeView
                val nodeWrapperView = TreeNodeWrapperView(nodeView!!.context, containerStyle)
                nodeWrapperView.insertNodeView(nodeView)
                mView = nodeWrapperView

                return mView!!
            }

        fun setTreeViev(treeViev: AndroidTreeView) {
            this.treeView = treeViev
        }

        val nodeView: View?
            get() = createNodeView(mNode!!, mNode!!.value as E)

        open val nodeItemsView: ViewGroup
            get() = view.findViewById(R.id.node_items) as ViewGroup

        val isInitialized: Boolean
            get() = mView != null


        abstract fun createNodeView(node: TreeNode, value: E): View?

        open fun toggle(active: Boolean) {
            // empty
        }

        open fun toggleSelectionMode(editModeEnabled: Boolean) {
            // empty
        }
    }

    companion object {
        val NODES_ID_SEPARATOR = ":"

        fun root(): TreeNode {
            val root = TreeNode(null)
            root.isSelectable = false
            return root
        }
    }
}
