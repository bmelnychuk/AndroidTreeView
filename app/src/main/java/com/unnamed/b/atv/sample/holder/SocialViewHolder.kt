package com.unnamed.b.atv.sample.holder

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.github.johnkil.print.PrintView
import com.unnamed.b.atv.model.TreeNode
import com.unnamed.b.atv.sample.R
import java.util.*

/**
 * Created by Bogdan Melnychuk on 2/13/15.
 */
class SocialViewHolder(context: Context) : TreeNode.BaseNodeViewHolder<SocialViewHolder.SocialItem>(context) {

    override fun createNodeView(node: TreeNode, value: SocialItem): View? {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.layout_social_node, null, false)

        val iconView = view.findViewById(R.id.icon) as PrintView
        iconView.iconText = context.resources.getString(value.icon)

        val connectionsLabel = view.findViewById(R.id.connections) as TextView
        val r = Random()
        connectionsLabel.text = r.nextInt(150).toString() + " connections"

        val userNameLabel = view.findViewById(R.id.username) as TextView
        userNameLabel.text = NAMES[r.nextInt(4)]

        val sizeText = view.findViewById(R.id.size) as TextView
        sizeText.text = r.nextInt(10).toString() + " items"

        return view
    }

    override fun toggle(active: Boolean) {}


    class SocialItem(var icon: Int)// rest will be hardcoded

    companion object {

        private val NAMES = arrayOf("Bruce Wayne", "Clark Kent", "Barry Allen", "Hal Jordan")
    }

}
