package com.unnamed.b.atv.holder

import android.content.Context
import android.view.View
import android.widget.TextView

import com.unnamed.b.atv.model.TreeNode

/**
 * Created by Bogdan Melnychuk on 2/11/15.
 * Converted to Kolin by Kumar Shivang on 16/07/17
 */
class SimpleViewHolder(context: Context) : TreeNode.BaseNodeViewHolder<Any>(context) {

    override fun createNodeView(node: TreeNode, value: Any): View {
        val tv = TextView(context)
        tv.text = value.toString()
        return tv
    }

    override fun toggle(active: Boolean) {

    }
}
