package com.unnamed.b.atv.sample.fileTreeView;

import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.sample.R;
import com.unnamed.b.atv.sample.holder.FileNodeViewHolder;

import java.io.File;

/**
 * Created by Ye He on 11/02/2016.
 */
public class FileTreeFactory {
    public static void setUpNode(TreeNode root, File rootFile) {
        if (rootFile.exists()) {
            TreeNode rootDirNode;
            if (rootFile.isDirectory()) {
                rootDirNode = new TreeNode(new FileNodeViewHolder.IconTreeItem(R.drawable.folder_48, rootFile));
            } else {
                rootDirNode = new TreeNode(new FileNodeViewHolder.IconTreeItem(R.drawable.file_48, rootFile));
            }

            root.addChild(rootDirNode);
        }
    }

    public static void setUpNodes(TreeNode root, File rootFile) {
        if (rootFile.exists() && rootFile.isDirectory()) {
            File[] files = rootFile.listFiles();
            if (files != null) {
                for (File file : files) {
                    setUpNode(root, file);
                }
            }
        }
    }
}
