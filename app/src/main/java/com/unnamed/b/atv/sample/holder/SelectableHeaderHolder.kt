package com.unnamed.b.atv.sample.holder

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import com.github.johnkil.print.PrintView
import com.unnamed.b.atv.model.TreeNode
import com.unnamed.b.atv.sample.R

/**
 * Created by Bogdan Melnychuk on 2/15/15.
 */
class SelectableHeaderHolder(context: Context) : TreeNode.BaseNodeViewHolder<IconTreeItemHolder.IconTreeItem>(context) {
    private var tvValue: TextView? = null
    private var arrowView: PrintView? = null
    private var nodeSelector: CheckBox? = null

    override fun createNodeView(node: TreeNode, value: IconTreeItemHolder.IconTreeItem): View? {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.layout_selectable_header, null, false)

        tvValue = view.findViewById(R.id.node_value) as TextView
        tvValue!!.text = value.text

        val iconView = view.findViewById(R.id.icon) as PrintView
        iconView.iconText = context.resources.getString(value.icon)

        arrowView = view.findViewById(R.id.arrow_icon) as PrintView
        if (node.isLeaf) {
            arrowView!!.visibility = View.GONE
        }

        nodeSelector = view.findViewById(R.id.node_selector) as CheckBox
        nodeSelector!!.setOnCheckedChangeListener { buttonView, isChecked ->
            node.isSelected = isChecked
            for (n in node.getChildren()) {
                treeView!!.selectNode(n, isChecked)
            }
        }
        nodeSelector!!.isChecked = node.isSelected

        return view
    }

    override fun toggle(active: Boolean) {
        arrowView!!.iconText = context.resources.getString(if (active) R.string.ic_keyboard_arrow_down else R.string.ic_keyboard_arrow_right)
    }

    override fun toggleSelectionMode(editModeEnabled: Boolean) {
        nodeSelector!!.visibility = if (editModeEnabled) View.VISIBLE else View.GONE
        nodeSelector!!.isChecked = mNode!!.isSelected
    }
}
