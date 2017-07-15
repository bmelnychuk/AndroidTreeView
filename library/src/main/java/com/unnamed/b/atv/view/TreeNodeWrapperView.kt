package com.unnamed.b.atv.view

import android.content.Context
import android.view.ContextThemeWrapper
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout

import com.unnamed.b.atv.R

/**
 * Created by Bogdan Melnychuk on 2/10/15.
 * Converted to Kolin by Kumar Shivang on 16/07/17
 */
class TreeNodeWrapperView(context: Context, private val containerStyle: Int) : LinearLayout(context) {
    private var nodeItemsContainer: LinearLayout? = null
    var nodeContainer: ViewGroup? = null
        private set

    init {
        init()
    }

    private fun init() {
        orientation = LinearLayout.VERTICAL

        nodeContainer = RelativeLayout(context)
        nodeContainer!!.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        nodeContainer!!.id = R.id.node_header

        val newContext = ContextThemeWrapper(context, containerStyle)
        nodeItemsContainer = LinearLayout(newContext, null, containerStyle)
        nodeItemsContainer!!.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        nodeItemsContainer!!.id = R.id.node_items
        nodeItemsContainer!!.orientation = LinearLayout.VERTICAL
        nodeItemsContainer!!.visibility = View.GONE

        addView(nodeContainer)
        addView(nodeItemsContainer)
    }


    fun insertNodeView(nodeView: View) {
        nodeContainer!!.addView(nodeView)
    }
}
