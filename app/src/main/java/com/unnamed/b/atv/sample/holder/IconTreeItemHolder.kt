package com.unnamed.b.atv.sample.holder

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView

import com.github.johnkil.print.PrintView
import com.unnamed.b.atv.model.TreeNode
import com.unnamed.b.atv.sample.R

/**
 * Created by Bogdan Melnychuk on 2/12/15.
 */
class IconTreeItemHolder(context: Context) : TreeNode.BaseNodeViewHolder<IconTreeItemHolder.IconTreeItem>(context) {
    private var tvValue: TextView? = null
    private var arrowView: PrintView? = null

    override fun createNodeView(node: TreeNode, value: IconTreeItem): View? {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.layout_icon_node, null, false)
        tvValue = view.findViewById(R.id.node_value) as TextView
        tvValue!!.text = value.text

        val iconView = view.findViewById(R.id.icon) as PrintView
        iconView.iconText = context.resources.getString(value.icon)

        arrowView = view.findViewById(R.id.arrow_icon) as PrintView

        view.findViewById(R.id.btn_addFolder).setOnClickListener {
            val newFolder = TreeNode(IconTreeItemHolder.IconTreeItem(R.string.ic_folder, "New Folder"))
            treeView!!.addNode(node, newFolder)
        }

        view.findViewById(R.id.btn_delete).setOnClickListener { treeView!!.removeNode(node) }

        //if My computer
        if (node.level == 1) {
            view.findViewById(R.id.btn_delete).visibility = View.GONE
        }

        return view
    }

    override fun toggle(active: Boolean) {
        arrowView!!.iconText = context.resources.getString(if (active) R.string.ic_keyboard_arrow_down else R.string.ic_keyboard_arrow_right)
    }

    class IconTreeItem(var icon: Int, var text: String)
}
