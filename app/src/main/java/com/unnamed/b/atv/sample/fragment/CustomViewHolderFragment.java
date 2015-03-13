package com.unnamed.b.atv.sample.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.sample.R;
import com.unnamed.b.atv.sample.holder.HeaderHolder;
import com.unnamed.b.atv.sample.holder.IconTreeItemHolder;
import com.unnamed.b.atv.sample.holder.PlaceHolderHolder;
import com.unnamed.b.atv.sample.holder.ProfileHolder;
import com.unnamed.b.atv.sample.holder.SocialViewHolder;
import com.unnamed.b.atv.view.AndroidTreeView;

/**
 * Created by Bogdan Melnychuk on 2/12/15.
 */
public class CustomViewHolderFragment extends Fragment {
    private AndroidTreeView tView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_default, null, false);
        final ViewGroup containerView = (ViewGroup) rootView.findViewById(R.id.container);
        rootView.findViewById(R.id.status_bar).setVisibility(View.GONE);

        final TreeNode root = TreeNode.root();

        TreeNode myProfile = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_person, "My Profile")).setViewHolder(new ProfileHolder(getActivity()));
        TreeNode bruce = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_person, "Bruce Wayne")).setViewHolder(new ProfileHolder(getActivity()));
        TreeNode clark = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_person, "Clark Kent")).setViewHolder(new ProfileHolder(getActivity()));
        TreeNode barry = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_person, "Barry Allen")).setViewHolder(new ProfileHolder(getActivity()));
        addProfileData(myProfile);
        addProfileData(clark);
        addProfileData(bruce);
        addProfileData(barry);
        root.addChildren(myProfile, bruce, barry, clark);

        tView = new AndroidTreeView(getActivity(), root);
        tView.setDefaultAnimation(true);
        tView.setDefaultContainerStyle(R.style.TreeNodeStyleDivided, true);
        containerView.addView(tView.getView());

        if (savedInstanceState != null) {
            String state = savedInstanceState.getString("tState");
            if (!TextUtils.isEmpty(state)) {
                tView.restoreState(state);
            }
        }

        return rootView;
    }

    private void addProfileData(TreeNode profile) {
        TreeNode socialNetworks = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_people, "Social")).setViewHolder(new HeaderHolder(getActivity()));
        TreeNode places = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_place, "Places")).setViewHolder(new HeaderHolder(getActivity()));

        TreeNode facebook = new TreeNode(new SocialViewHolder.SocialItem(R.string.ic_post_facebook)).setViewHolder(new SocialViewHolder(getActivity()));
        TreeNode linkedin = new TreeNode(new SocialViewHolder.SocialItem(R.string.ic_post_linkedin)).setViewHolder(new SocialViewHolder(getActivity()));
        TreeNode google = new TreeNode(new SocialViewHolder.SocialItem(R.string.ic_post_gplus)).setViewHolder(new SocialViewHolder(getActivity()));
        TreeNode twitter = new TreeNode(new SocialViewHolder.SocialItem(R.string.ic_post_twitter)).setViewHolder(new SocialViewHolder(getActivity()));

        TreeNode lake = new TreeNode(new PlaceHolderHolder.PlaceItem("A rose garden")).setViewHolder(new PlaceHolderHolder(getActivity()));
        TreeNode mountains = new TreeNode(new PlaceHolderHolder.PlaceItem("The white house")).setViewHolder(new PlaceHolderHolder(getActivity()));

        places.addChildren(lake, mountains);
        socialNetworks.addChildren(facebook, google, twitter, linkedin);
        profile.addChildren(socialNetworks, places);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("tState", tView.getSaveState());
    }
}
