package com.unnamed.b.atv.sample.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.view.*
import android.widget.TextView
import android.widget.Toast
import com.unnamed.b.atv.model.TreeNode
import com.unnamed.b.atv.sample.R
import com.unnamed.b.atv.sample.holder.IconTreeItemHolder
import com.unnamed.b.atv.view.AndroidTreeView


/**
 * Created by Bogdan Melnychuk on 2/12/15.
 * Converted to Kolin by Kumar Shivang on 16/07/17
 */
class FolderStructureFragment : Fragment() {
    private var statusBar: TextView? = null
    private var tView: AndroidTreeView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val rootView = inflater.inflate(R.layout.fragment_default, container, false)
        val containerView = rootView.findViewById(R.id.container) as ViewGroup

        statusBar = rootView.findViewById(R.id.status_bar) as TextView

        val root = TreeNode.root()
        val computerRoot = TreeNode(IconTreeItemHolder.IconTreeItem(R.string.ic_laptop, "My Computer"))

        val myDocuments = TreeNode(IconTreeItemHolder.IconTreeItem(R.string.ic_folder, "My Documents"))
        val downloads = TreeNode(IconTreeItemHolder.IconTreeItem(R.string.ic_folder, "Downloads"))
        val file1 = TreeNode(IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Folder 1"))
        val file2 = TreeNode(IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Folder 2"))
        val file3 = TreeNode(IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Folder 3"))
        val file4 = TreeNode(IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Folder 4"))
        fillDownloadsFolder(downloads)
        downloads.addChildren(file1, file2, file3, file4)

        val myMedia = TreeNode(IconTreeItemHolder.IconTreeItem(R.string.ic_photo_library, "Photos"))
        val photo1 = TreeNode(IconTreeItemHolder.IconTreeItem(R.string.ic_photo, "Folder 1"))
        val photo2 = TreeNode(IconTreeItemHolder.IconTreeItem(R.string.ic_photo, "Folder 2"))
        val photo3 = TreeNode(IconTreeItemHolder.IconTreeItem(R.string.ic_photo, "Folder 3"))
        myMedia.addChildren(photo1, photo2, photo3)

        myDocuments.addChild(downloads)
        computerRoot.addChildren(myDocuments, myMedia)

        root.addChildren(computerRoot)

        tView = AndroidTreeView(activity, root)
        tView!!.setDefaultAnimation(true)
        tView!!.setDefaultContainerStyle(R.style.TreeNodeStyleCustom)
        tView!!.setDefaultViewHolder(IconTreeItemHolder::class.java)
        tView!!.setDefaultNodeClickListener(nodeClickListener)
        tView!!.setDefaultNodeLongClickListener(nodeLongClickListener)

        containerView.addView(tView!!.view)

        if (savedInstanceState != null) {
            val state = savedInstanceState.getString("tState")
            if (!TextUtils.isEmpty(state)) {
                tView!!.restoreState(state!!)
            }
        }

        return rootView
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.expandAll -> tView!!.expandAll()

            R.id.collapseAll -> tView!!.collapseAll()
        }
        return true
    }

    private var counter = 0

    private fun fillDownloadsFolder(node: TreeNode) {
        val downloads = TreeNode(IconTreeItemHolder.IconTreeItem(R.string.ic_folder, "Downloads" + counter++))
        node.addChild(downloads)
        if (counter < 5) {
            fillDownloadsFolder(downloads)
        }
    }

    private val nodeClickListener = object : TreeNode.TreeNodeClickListener {
        override fun onClick(node: TreeNode, value: Any) {
            val item = value as IconTreeItemHolder.IconTreeItem
            statusBar!!.text = "Last clicked: " + item.text
        }
    }

    private val nodeLongClickListener = object : TreeNode.TreeNodeLongClickListener {
        override fun onLongClick(node: TreeNode, value: Any): Boolean {
            val item = value as IconTreeItemHolder.IconTreeItem
            Toast.makeText(activity, "Long click: " + item.text, Toast.LENGTH_SHORT).show()
            return true
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("tState", tView!!.saveState)
    }
}
