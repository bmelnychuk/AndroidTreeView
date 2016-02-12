package com.unnamed.b.atv.sample.fileTreeView;

import android.content.Context;
import android.graphics.Color;

import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.sample.holder.FileNodeViewHolder;
import com.unnamed.b.atv.view.AndroidTreeView;
import com.unnamed.b.atv.view.TreeNodeWrapperView;

import java.io.File;

/**
 * Created by Ye He on 20/01/16.
 */
public class FileTreeView extends AndroidTreeView {
    private TreeNode highlightedNode;
    private String selectedFilePath;
    private File rootFile;

    public FileTreeView(Context context, File file) {
        super(context);
        TreeNode root = TreeNode.root();
        FileTreeFactory.setUpNode(root, file);
        rootFile = file;
        setRoot(root);
        setDefaultViewHolder(FileNodeViewHolder.class);
        setUse2dScroll(true);
        setFullWidth(true);
    }


    public void highlight(String path) {
        TreeNode treeNode = expandTo2(new File(path), mRoot);
        if (treeNode != null) {
            setHighlightedNode(treeNode);
            selectedFilePath = path;
        }
    }

    private TreeNode expandTo2(File file, TreeNode root) {
        if (file == null) {
            return null;
        }
        File parentFile = file.getParentFile();

        if (isRootFile(file)) {
            for (TreeNode childNode : root.getChildren()) {
                FileNodeViewHolder.IconTreeItem item = (FileNodeViewHolder.IconTreeItem) childNode.getValue();
                if (item.file.getName().equals(file.getName())) {
                    return childNode;
                }
            }

            return null;
        }

        TreeNode treeNode = expandTo2(parentFile, root);
        if (treeNode == null) {
            return null;
        } else {
            treeNode.deleteChildren();
            FileTreeFactory.setUpNodes(treeNode, parentFile);
            for (TreeNode childNode : treeNode.getChildren()) {
                FileNodeViewHolder.IconTreeItem item = (FileNodeViewHolder.IconTreeItem) childNode.getValue();
                if (item.file.getName().equals(file.getName())) {
                    expandNode(treeNode);
                    if (childNode.isLeaf()) {
                        expandNode(childNode);
                    }
                    return childNode;
                }

            }
            return null;
        }
    }

    private boolean isRootFile(File file) {
        return rootFile.getAbsolutePath().equals(file.getAbsolutePath());
    }

    public void setHighlightedNode(TreeNode highlightedNode) {
        TreeNodeWrapperView view;
        if (this.highlightedNode != null) {
            view = (TreeNodeWrapperView) this.highlightedNode.getViewHolder().getView();
            view.getNodeContainer().setBackgroundColor(Color.TRANSPARENT);
        }
        this.highlightedNode = highlightedNode;
        view = (TreeNodeWrapperView) this.highlightedNode.getViewHolder().getView();
        view.getNodeContainer().setBackgroundColor(Color.GREEN);
    }

    public TreeNode getHighlightedNode() {
        return highlightedNode;
    }

    public String getSelectedFilePath() {
        return selectedFilePath;
    }

    public void setSelectedFilePath(String selectedFilePath) {
        this.selectedFilePath = selectedFilePath;
    }

    public TreeNode getRoot() {
        return mRoot;
    }
}
