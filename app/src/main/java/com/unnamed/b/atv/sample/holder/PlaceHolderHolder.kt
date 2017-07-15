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
class PlaceHolderHolder(context: Context) : TreeNode.BaseNodeViewHolder<PlaceHolderHolder.PlaceItem>(context) {

    override fun createNodeView(node: TreeNode, value: PlaceItem): View? {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.layout_place_node, null, false)


        val placeName = view.findViewById(R.id.place_name) as TextView
        placeName.text = value.name

        val r = Random()
        val like = r.nextBoolean()

        val likeView = view.findViewById(R.id.like) as PrintView
        likeView.iconText = context.getString(if (like) R.string.ic_thumbs_up else R.string.ic_thumbs_down)
        return view
    }

    override fun toggle(active: Boolean) {}


    class PlaceItem(var name: String)// rest will be hardcoded

}
