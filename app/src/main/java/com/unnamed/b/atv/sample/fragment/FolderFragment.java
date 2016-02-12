package com.unnamed.b.atv.sample.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.sample.R;
import com.unnamed.b.atv.sample.fileTreeView.FileTreeFactory;
import com.unnamed.b.atv.sample.fileTreeView.FileTreeView;
import com.unnamed.b.atv.sample.holder.FileNodeViewHolder;

import java.io.File;

/**
 * Created by Ye He on 12/02/2016.
 * Demonstration of tree view working on large file system.
 */
public class FolderFragment extends Fragment {
    private FileTreeView tView;
    private String tState;
    private String selectedFilePath;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_default, container, false);

        if (savedInstanceState != null) {
            tState = savedInstanceState.getString("tState");
            selectedFilePath = savedInstanceState.getString("selectedFilePath");
        }

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        ViewGroup containerView = (ViewGroup) getActivity().findViewById(R.id.container);
        addFileTreeView(containerView, "/");

        if (tState != null) {
            tView.restoreState(tState);
        }

        if (selectedFilePath != null) {
            tView.highlight(selectedFilePath);
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("tState", tView.getSaveState());
        outState.putString("selectedFilePath", tView.getSelectedFilePath());
    }

    /**
     * Expand folder view to the file following the path and highlight the node.
     *
     * @param path an absolute path giving the location of the file
     */
    public void highlightFile(String path) {
        tView.highlight(path);
    }

    private void addFileTreeView(ViewGroup containerView, String filePath) {
        File rootFile = new File(filePath);

        tView = new FileTreeView(this.getActivity(), rootFile);
        tView.setDefaultNodeClickListener(myNodeClickListener);

        containerView.addView(tView.getView());
    }

    private TreeNode.TreeNodeClickListener myNodeClickListener = new TreeNode.TreeNodeClickListener() {
        @Override
        public void onClick(TreeNode node, Object value) {
            FileNodeViewHolder.IconTreeItem item = (FileNodeViewHolder.IconTreeItem) value;
            String filePath = item.file.getAbsolutePath();
            tView.setHighlightedNode(node);
            tView.setSelectedFilePath(filePath);
            // build up children nodes
            File[] files = item.file.listFiles();
            node.deleteChildren();
            if (files != null) {
                for (File file : files) {
                    FileTreeFactory.setUpNode(node, file);
                }
            }
        }
    };
}

