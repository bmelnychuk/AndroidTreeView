package com.unnamed.b.atv.sample.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.unnamed.b.atv.model.TreeNode
import com.unnamed.b.atv.sample.R
import com.unnamed.b.atv.sample.holder.IconTreeItemHolder
import com.unnamed.b.atv.sample.holder.ProfileHolder
import com.unnamed.b.atv.sample.holder.SelectableHeaderHolder
import com.unnamed.b.atv.sample.holder.SelectableItemHolder
import com.unnamed.b.atv.view.AndroidTreeView


/**
 * Created by Bogdan Melnychuk on 2/12/15.
 * Converted to Kolin by Kumar Shivang on 16/07/17
 */
class SelectableTreeFragment : Fragment() {
    private var tView: AndroidTreeView? = null
    private var selectionModeEnabled = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val rootView = inflater.inflate(R.layout.fragment_selectable_nodes, null, false)
        val containerView = rootView.findViewById(R.id.container) as ViewGroup

        val selectionModeButton = rootView.findViewById(R.id.btn_toggleSelection)
        selectionModeButton.setOnClickListener {
            selectionModeEnabled = !selectionModeEnabled
            tView!!.isSelectionModeEnabled = selectionModeEnabled
        }

        val selectAllBtn = rootView.findViewById(R.id.btn_selectAll)
        selectAllBtn.setOnClickListener {
            if (!selectionModeEnabled) {
                Toast.makeText(activity, "Enable selection mode first", Toast.LENGTH_SHORT).show()
            }
            tView!!.selectAll(true)
        }

        val deselectAll = rootView.findViewById(R.id.btn_deselectAll)
        deselectAll.setOnClickListener {
            if (!selectionModeEnabled) {
                Toast.makeText(activity, "Enable selection mode first", Toast.LENGTH_SHORT).show()
            }
            tView!!.deselectAll()
        }

        val check = rootView.findViewById(R.id.btn_checkSelection)
        check.setOnClickListener {
            if (!selectionModeEnabled) {
                Toast.makeText(activity, "Enable selection mode first", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(activity, tView!!.selected.size.toString() + " selected", Toast.LENGTH_SHORT).show()
            }
        }

        val root = TreeNode.root()

        val s1 = TreeNode(IconTreeItemHolder.IconTreeItem(R.string.ic_sd_storage, "Storage1")).setViewHolder(ProfileHolder(activity))
        val s2 = TreeNode(IconTreeItemHolder.IconTreeItem(R.string.ic_sd_storage, "Storage2")).setViewHolder(ProfileHolder(activity))
        s1.isSelectable = false
        s2.isSelectable = false

        val folder1 = TreeNode(IconTreeItemHolder.IconTreeItem(R.string.ic_folder, "Folder 1")).setViewHolder(SelectableHeaderHolder(activity))
        val folder2 = TreeNode(IconTreeItemHolder.IconTreeItem(R.string.ic_folder, "Folder 2")).setViewHolder(SelectableHeaderHolder(activity))
        val folder3 = TreeNode(IconTreeItemHolder.IconTreeItem(R.string.ic_folder, "Folder 3")).setViewHolder(SelectableHeaderHolder(activity))

        fillFolder(folder1)
        fillFolder(folder2)
        fillFolder(folder3)

        s1.addChildren(folder1, folder2)
        s2.addChildren(folder3)

        root.addChildren(s1, s2)

        tView = AndroidTreeView(activity, root)
        tView!!.setDefaultAnimation(true)
        containerView.addView(tView!!.view)

        if (savedInstanceState != null) {
            val state = savedInstanceState.getString("tState")
            if (!TextUtils.isEmpty(state)) {
                tView!!.restoreState(state!!)
            }
        }
        return rootView
    }

    private fun fillFolder(folder: TreeNode) {
        val file1 = TreeNode("File1").setViewHolder(SelectableItemHolder(activity))
        val file2 = TreeNode("File2").setViewHolder(SelectableItemHolder(activity))
        val file3 = TreeNode("File3").setViewHolder(SelectableItemHolder(activity))
        folder.addChildren(file1, file2, file3)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("tState", tView!!.saveState)
    }
}
