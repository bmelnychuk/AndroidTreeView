package com.unnamed.b.atv.view

import android.content.Context
import android.text.TextUtils
import android.view.ContextThemeWrapper
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.LinearLayout
import android.widget.ScrollView
import com.unnamed.b.atv.R
import com.unnamed.b.atv.holder.SimpleViewHolder
import com.unnamed.b.atv.model.TreeNode
import java.util.*

/**
 * Created by Bogdan Melnychuk on 2/10/15.
 * Converted to Kolin by Kumar Shivang on 16/07/17
 */
class AndroidTreeView {

    protected var mRoot: TreeNode? = null
    private var mContext: Context? = null
    private var applyForRoot: Boolean = false
    private var containerStyle = 0
    private var defaultViewHolderClass: Class<out TreeNode.BaseNodeViewHolder<*>> = SimpleViewHolder::class.java
    private var nodeClickListener: TreeNode.TreeNodeClickListener? = null
    private var nodeLongClickListener: TreeNode.TreeNodeLongClickListener? = null
    //------------------------------------------------------------
    //  Selection methods

    // TODO fix double iteration over tree
    var isSelectionModeEnabled: Boolean = false
        set(selectionModeEnabled) {
            if (!selectionModeEnabled) {
                deselectAll()
            }
            field = selectionModeEnabled

            if (mRoot != null) {
                for (node in mRoot!!.getChildren()) {
                    toggleSelectionMode(node, selectionModeEnabled)
                }
            }

        }
    private var mUseDefaultAnimation = false
    var is2dScrollEnabled = false
        private set
    var isAutoToggleEnabled = true
        private set

    constructor(context: Context) {
        mContext = context
    }

    fun setRoot(mRoot: TreeNode) {
        this.mRoot = mRoot
    }

    constructor(context: Context, root: TreeNode) {
        mRoot = root
        mContext = context
    }

    fun setDefaultAnimation(defaultAnimation: Boolean) {
        this.mUseDefaultAnimation = defaultAnimation
    }

    @JvmOverloads fun setDefaultContainerStyle(style: Int, applyForRoot: Boolean = false) {
        containerStyle = style
        this.applyForRoot = applyForRoot
    }

    fun setUse2dScroll(use2dScroll: Boolean) {
        this.is2dScrollEnabled = use2dScroll
    }

    fun setUseAutoToggle(enableAutoToggle: Boolean) {
        this.isAutoToggleEnabled = enableAutoToggle
    }

    fun setDefaultViewHolder(viewHolder: Class<out TreeNode.BaseNodeViewHolder<*>>) {
        defaultViewHolderClass = viewHolder
    }

    fun setDefaultNodeClickListener(listener: TreeNode.TreeNodeClickListener) {
        nodeClickListener = listener
    }

    fun setDefaultNodeLongClickListener(listener: TreeNode.TreeNodeLongClickListener) {
        nodeLongClickListener = listener
    }

    fun expandAll() {
        if (mRoot != null) {
            expandNode(mRoot!!, true)
        }
    }

    fun collapseAll() {
        if (mRoot != null) {
            for (n in mRoot!!.getChildren()) {
                collapseNode(n, true)
            }
        }
    }


    fun getView(style: Int): View {
        val view: ViewGroup
        if (style > 0) {
            val newContext = ContextThemeWrapper(mContext, style)
            view = if (is2dScrollEnabled) TwoDScrollView(newContext) else ScrollView(newContext)
        } else {
            view = if (is2dScrollEnabled) TwoDScrollView(mContext!!) else ScrollView(mContext)
        }

        var containerContext: Context? = mContext
        if (containerStyle != 0 && applyForRoot) {
            containerContext = ContextThemeWrapper(mContext, containerStyle)
        }
        val viewTreeItems = LinearLayout(containerContext, null, containerStyle)

        viewTreeItems.id = R.id.tree_items
        viewTreeItems.orientation = LinearLayout.VERTICAL
        view.addView(viewTreeItems)

        if (mRoot != null) {
            mRoot!!.setViewHolder(object : TreeNode.BaseNodeViewHolder<Any>(mContext!!) {
                override fun createNodeView(node: TreeNode, value: Any): View? {
                    return null
                }

                override val nodeItemsView: ViewGroup
                    get() = viewTreeItems
            })
            expandNode(mRoot!!, false)
        }
        return view
    }

    val view: View
        get() = getView(-1)


    fun expandLevel(level: Int) {
        if (mRoot != null) {
            for (n in mRoot!!.getChildren()) {
                expandLevel(n, level)
            }
        }
    }

    private fun expandLevel(node: TreeNode, level: Int) {
        if (node.level <= level) {
            expandNode(node, false)
        }
        for (n in node.getChildren()) {
            expandLevel(n, level)
        }
    }

    fun expandNode(node: TreeNode) {
        expandNode(node, false)
    }

    fun collapseNode(node: TreeNode) {
        collapseNode(node, false)
    }

    val saveState: String
        get() {
            val builder = StringBuilder()
            if (mRoot != null) {
                getSaveState(mRoot!!, builder)
            }
            if (builder.isNotEmpty()) {
                builder.setLength(builder.length - 1)
            }
            return builder.toString()
        }

    fun restoreState(saveState: String) {
        if (!TextUtils.isEmpty(saveState)) {
            collapseAll()
            val openNodesArray = saveState.split(NODES_PATH_SEPARATOR.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val openNodes = HashSet(Arrays.asList(*openNodesArray))
            if (mRoot != null) {
                restoreNodeState(mRoot!!, openNodes)
            }
        }
    }

    private fun restoreNodeState(node: TreeNode, openNodes: Set<String>) {
        for (n in node.getChildren()) {
            if (openNodes.contains(n.path)) {
                expandNode(n)
                restoreNodeState(n, openNodes)
            }
        }
    }

    private fun getSaveState(root: TreeNode, sBuilder: StringBuilder) {
        for (node in root.getChildren()) {
            if (node.isExpanded()) {
                sBuilder.append(node.path)
                sBuilder.append(NODES_PATH_SEPARATOR)
                getSaveState(node, sBuilder)
            }
        }
    }

    fun toggleNode(node: TreeNode) {
        if (node.isExpanded()) {
            collapseNode(node, false)
        } else {
            expandNode(node, false)
        }

    }

    private fun collapseNode(node: TreeNode, includeSubnodes: Boolean) {
        node.setExpanded(false)
        val nodeViewHolder = getViewHolderForNode(node)

        if (mUseDefaultAnimation) {
            val view = nodeViewHolder?.nodeItemsView
            if (view != null) {
                collapse(view)
            }
        } else {
            nodeViewHolder?.nodeItemsView?.visibility = View.GONE
        }
        nodeViewHolder?.toggle(false)
        if (includeSubnodes) {
            for (n in node.getChildren()) {
                collapseNode(n, includeSubnodes)
            }
        }
    }

    private fun expandNode(node: TreeNode, includeSubnodes: Boolean) {
        node.setExpanded(true)
        val parentViewHolder = getViewHolderForNode(node)
        parentViewHolder?.nodeItemsView?.removeAllViews()


        parentViewHolder?.toggle(true)

        for (n in node.getChildren()) {
            addNode(parentViewHolder?.nodeItemsView, n)

            if (n.isExpanded() || includeSubnodes) {
                expandNode(n, includeSubnodes)
            }

        }
        if (mUseDefaultAnimation) {
            val parentView = parentViewHolder?.nodeItemsView
            if (parentView != null) {
                expand(parentView)
            }
        } else {
            parentViewHolder?.nodeItemsView?.visibility = View.VISIBLE
        }

    }

    private fun addNode(container: ViewGroup?, n: TreeNode) {
        val viewHolder = getViewHolderForNode(n)
        val nodeView = viewHolder?.view
        container?.addView(nodeView)
        if (isSelectionModeEnabled) {
            viewHolder?.toggleSelectionMode(isSelectionModeEnabled)
        }

        nodeView?.setOnClickListener {
            if (n.getClickListener() != null) {
                n.getClickListener()!!.onClick(n, n.value!!)
            } else if (nodeClickListener != null) {
                nodeClickListener!!.onClick(n, n.value!!)
            }
            if (isAutoToggleEnabled) {
                toggleNode(n)
            }
        }

        nodeView?.setOnLongClickListener(View.OnLongClickListener {
            if (n.getLongClickListener() != null) {
                return@OnLongClickListener n.getLongClickListener()!!.onLongClick(n, n.value!!)
            } else if (nodeLongClickListener != null) {
                return@OnLongClickListener nodeLongClickListener!!.onLongClick(n, n.value!!)
            }
            if (isAutoToggleEnabled) {
                toggleNode(n)
            }
            false
        })
    }

    fun <E> getSelectedValues(clazz: Class<E>): List<E> {
        val selected = selected
        val result = selected
                .map { it.value }
                .filter { it != null && it.javaClass == clazz && it as? E != null }
                .map { it as E }
        return result
    }

    private fun toggleSelectionMode(parent: TreeNode, mSelectionModeEnabled: Boolean) {
        toogleSelectionForNode(parent, mSelectionModeEnabled)
        if (parent.isExpanded()) {
            for (node in parent.getChildren()) {
                toggleSelectionMode(node, mSelectionModeEnabled)
            }
        }
    }

    val selected: List<TreeNode>
        get() {
            if (isSelectionModeEnabled) {
                return getSelected(mRoot!!)
            } else {
                return ArrayList()
            }
        }

    // TODO Do we need to go through whole tree? Save references or consider collapsed nodes as not selected
    private fun getSelected(parent: TreeNode): List<TreeNode> {
        val result = ArrayList<TreeNode>()
        for (n in parent.getChildren()) {
            if (n.isSelected) {
                result.add(n)
            }
            result.addAll(getSelected(n))
        }
        return result
    }

    fun selectAll(skipCollapsed: Boolean) {
        makeAllSelection(true, skipCollapsed)
    }

    fun deselectAll() {
        makeAllSelection(false, false)
    }

    private fun makeAllSelection(selected: Boolean, skipCollapsed: Boolean) {
        if (isSelectionModeEnabled && mRoot != null) {
            for (node in mRoot!!.getChildren()) {
                selectNode(node, selected, skipCollapsed)
            }
        }
    }

    fun selectNode(node: TreeNode, selected: Boolean) {
        if (isSelectionModeEnabled) {
            node.isSelected = selected
            toogleSelectionForNode(node, true)
        }
    }

    private fun selectNode(parent: TreeNode, selected: Boolean, skipCollapsed: Boolean) {
        parent.isSelected = selected
        toogleSelectionForNode(parent, true)
        val toContinue = if (skipCollapsed) parent.isExpanded() else true
        if (toContinue) {
            for (node in parent.getChildren()) {
                selectNode(node, selected, skipCollapsed)
            }
        }
    }

    private fun toogleSelectionForNode(node: TreeNode, makeSelectable: Boolean) {
        val holder = getViewHolderForNode(node)
        if (holder?.isInitialized == true) {
            getViewHolderForNode(node)?.toggleSelectionMode(makeSelectable)
        }
    }

    private fun getViewHolderForNode(node: TreeNode): TreeNode.BaseNodeViewHolder<*>? {
        var viewHolder: TreeNode.BaseNodeViewHolder<*>? = node.getViewHolder()
        if (viewHolder == null) {
            try {
                val `object` = defaultViewHolderClass.getConstructor(Context::class.java).newInstance(mContext)
                viewHolder = `object`
                node.setViewHolder(viewHolder)
            } catch (e: Exception) {
                throw RuntimeException("Could not instantiate class " + defaultViewHolderClass)
            }

        }
        if (viewHolder?.treeView == null) {
            viewHolder?.setTreeViev(this)
        }
        if (viewHolder != null && viewHolder.containerStyle <= 0) {
            viewHolder.containerStyle = containerStyle
        }
        return viewHolder
    }

    //-----------------------------------------------------------------
    //Add / Remove

    fun addNode(parent: TreeNode, nodeToAdd: TreeNode) {
        parent.addChild(nodeToAdd)
        if (parent.isExpanded()) {
            val parentViewHolder = getViewHolderForNode(parent)
            addNode(parentViewHolder?.nodeItemsView, nodeToAdd)
        }
    }

    fun removeNode(node: TreeNode) {
        if (node.parent != null) {
            val parent = node.parent
            val index = parent!!.deleteChild(node)
            if (parent.isExpanded() && index >= 0) {
                val parentViewHolder = getViewHolderForNode(parent)
                parentViewHolder?.nodeItemsView?.removeViewAt(index)
            }
        }
    }

    companion object {
        private val NODES_PATH_SEPARATOR = ";"

        private fun expand(v: View) {
            v.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            val targetHeight = v.measuredHeight

            v.layoutParams.height = 0
            v.visibility = View.VISIBLE
            val a = object : Animation() {
                override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                    v.layoutParams.height = if (interpolatedTime == 1f)
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    else
                        (targetHeight * interpolatedTime).toInt()
                    v.requestLayout()
                }

                override fun willChangeBounds(): Boolean {
                    return true
                }
            }

            // 1dp/ms
            a.duration = (targetHeight / v.context.resources.displayMetrics.density).toInt().toLong()
            v.startAnimation(a)
        }

        private fun collapse(v: View) {
            val initialHeight = v.measuredHeight

            val a = object : Animation() {
                override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                    if (interpolatedTime == 1f) {
                        v.visibility = View.GONE
                    } else {

                        v.layoutParams?.height = initialHeight - (initialHeight * interpolatedTime).toInt()
                        v.requestLayout()
                    }
                }

                override fun willChangeBounds(): Boolean {
                    return true
                }
            }

            // 1dp/ms
            val density = v.context?.resources?.displayMetrics?.density
            if (density != null)
                a.duration = (initialHeight / density).toInt().toLong()
            v.startAnimation(a)
        }
    }
}
