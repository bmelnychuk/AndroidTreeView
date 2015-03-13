package com.unnamed.b.atv.sample.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.sample.R;
import com.unnamed.b.atv.sample.holder.IconTreeItemHolder;
import com.unnamed.b.atv.sample.holder.ProfileHolder;
import com.unnamed.b.atv.sample.holder.SelectableHeaderHolder;
import com.unnamed.b.atv.sample.holder.SelectableItemHolder;
import com.unnamed.b.atv.view.AndroidTreeView;

/**
 * Created by Bogdan Melnychuk on 2/12/15.
 */
public class SelectableTreeFragment extends Fragment {
    private AndroidTreeView tView;
    private boolean selectionModeEnabled = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_selectable_nodes, null, false);
        ViewGroup containerView = (ViewGroup) rootView.findViewById(R.id.container);

        View selectionModeButton = rootView.findViewById(R.id.btn_toggleSelection);
        selectionModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectionModeEnabled = !selectionModeEnabled;
                tView.setSelectionModeEnabled(selectionModeEnabled);
            }
        });

        View selectAllBtn = rootView.findViewById(R.id.btn_selectAll);
        selectAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!selectionModeEnabled) {
                    Toast.makeText(getActivity(), "Enable selection mode first", Toast.LENGTH_SHORT).show();
                }
                tView.selectAll(true);
            }
        });

        View deselectAll = rootView.findViewById(R.id.btn_deselectAll);
        deselectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!selectionModeEnabled) {
                    Toast.makeText(getActivity(), "Enable selection mode first", Toast.LENGTH_SHORT).show();
                }
                tView.deselectAll();
            }
        });

        View check = rootView.findViewById(R.id.btn_checkSelection);
        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!selectionModeEnabled) {
                    Toast.makeText(getActivity(), "Enable selection mode first", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), tView.getSelected().size() + " selected", Toast.LENGTH_SHORT).show();
                }
            }
        });

        TreeNode root = TreeNode.root();

        TreeNode s1 = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_sd_storage, "Storage1")).setViewHolder(new ProfileHolder(getActivity()));
        TreeNode s2 = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_sd_storage, "Storage2")).setViewHolder(new ProfileHolder(getActivity()));
        s1.setSelectable(false);
        s2.setSelectable(false);

        TreeNode folder1 = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_folder, "Folder 1")).setViewHolder(new SelectableHeaderHolder(getActivity()));
        TreeNode folder2 = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_folder, "Folder 2")).setViewHolder(new SelectableHeaderHolder(getActivity()));
        TreeNode folder3 = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_folder, "Folder 3")).setViewHolder(new SelectableHeaderHolder(getActivity()));

        fillFolder(folder1);
        fillFolder(folder2);
        fillFolder(folder3);

        s1.addChildren(folder1, folder2);
        s2.addChildren(folder3);

        root.addChildren(s1, s2);

        tView = new AndroidTreeView(getActivity(), root);
        tView.setDefaultAnimation(true);
        containerView.addView(tView.getView());

        if (savedInstanceState != null) {
            String state = savedInstanceState.getString("tState");
            if (!TextUtils.isEmpty(state)) {
                tView.restoreState(state);
            }
        }
        return rootView;
    }

    private void fillFolder(TreeNode folder) {
        TreeNode file1 = new TreeNode("File1").setViewHolder(new SelectableItemHolder(getActivity()));
        TreeNode file2 = new TreeNode("File2").setViewHolder(new SelectableItemHolder(getActivity()));
        TreeNode file3 = new TreeNode("File3").setViewHolder(new SelectableItemHolder(getActivity()));
        folder.addChildren(file1, file2, file3);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("tState", tView.getSaveState());
    }
}
