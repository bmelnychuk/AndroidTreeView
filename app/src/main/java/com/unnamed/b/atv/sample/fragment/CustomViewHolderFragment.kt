package com.unnamed.b.atv.sample.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.unnamed.b.atv.model.TreeNode
import com.unnamed.b.atv.sample.R
import com.unnamed.b.atv.sample.holder.*
import com.unnamed.b.atv.view.AndroidTreeView


/**
 * Created by Bogdan Melnychuk on 2/12/15.
 * Converted to Kolin by Kumar Shivang on 16/07/17
 */
class CustomViewHolderFragment : Fragment() {
    private var tView: AndroidTreeView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_default, container, false)
        val containerView = rootView.findViewById(R.id.container) as ViewGroup
        rootView.findViewById(R.id.status_bar).visibility = View.GONE

        val root = TreeNode.root()

        val myProfile = TreeNode(IconTreeItemHolder.IconTreeItem(R.string.ic_person, "My Profile")).setViewHolder(ProfileHolder(activity))
        val bruce = TreeNode(IconTreeItemHolder.IconTreeItem(R.string.ic_person, "Bruce Wayne")).setViewHolder(ProfileHolder(activity))
        val clark = TreeNode(IconTreeItemHolder.IconTreeItem(R.string.ic_person, "Clark Kent")).setViewHolder(ProfileHolder(activity))
        val barry = TreeNode(IconTreeItemHolder.IconTreeItem(R.string.ic_person, "Barry Allen")).setViewHolder(ProfileHolder(activity))
        addProfileData(myProfile)
        addProfileData(clark)
        addProfileData(bruce)
        addProfileData(barry)
        root.addChildren(myProfile, bruce, barry, clark)

        tView = AndroidTreeView(activity, root)
        tView!!.setDefaultAnimation(true)
        tView!!.setDefaultContainerStyle(R.style.TreeNodeStyleDivided, true)
        containerView.addView(tView!!.view)

        if (savedInstanceState != null) {
            val state = savedInstanceState.getString("tState")
            if (!TextUtils.isEmpty(state)) {
                tView!!.restoreState(state!!)
            }
        }

        return rootView
    }

    private fun addProfileData(profile: TreeNode) {
        val socialNetworks = TreeNode(IconTreeItemHolder.IconTreeItem(R.string.ic_people, "Social")).setViewHolder(HeaderHolder(activity))
        val places = TreeNode(IconTreeItemHolder.IconTreeItem(R.string.ic_place, "Places")).setViewHolder(HeaderHolder(activity))

        val facebook = TreeNode(SocialViewHolder.SocialItem(R.string.ic_post_facebook)).setViewHolder(SocialViewHolder(activity))
        val linkedin = TreeNode(SocialViewHolder.SocialItem(R.string.ic_post_linkedin)).setViewHolder(SocialViewHolder(activity))
        val google = TreeNode(SocialViewHolder.SocialItem(R.string.ic_post_gplus)).setViewHolder(SocialViewHolder(activity))
        val twitter = TreeNode(SocialViewHolder.SocialItem(R.string.ic_post_twitter)).setViewHolder(SocialViewHolder(activity))

        val lake = TreeNode(PlaceHolderHolder.PlaceItem("A rose garden")).setViewHolder(PlaceHolderHolder(activity))
        val mountains = TreeNode(PlaceHolderHolder.PlaceItem("The white house")).setViewHolder(PlaceHolderHolder(activity))

        places.addChildren(lake, mountains)
        socialNetworks.addChildren(facebook, google, twitter, linkedin)
        profile.addChildren(socialNetworks, places)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("tState", tView!!.saveState)
    }
}
