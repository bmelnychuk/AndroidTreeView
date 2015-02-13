package com.unnamed.b.atv.sample.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.sample.R;
import com.unnamed.b.atv.sample.holder.IconTreeItemHolder;
import com.unnamed.b.atv.view.AndroidTreeView;

/**
 * Created by Bogdan Melnychuk on 2/12/15.
 */
public class FolderStructureFragment extends Fragment {
    private TextView statusBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_default, null, false);
        ViewGroup containerView = (ViewGroup) rootView.findViewById(R.id.container);

        statusBar = (TextView) rootView.findViewById(R.id.status_bar);

        TreeNode root = TreeNode.root();
        TreeNode computerRoot = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_laptop, "My Computer"));

        TreeNode myDocuments = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_folder, "My Documents"));
        TreeNode downloads = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_folder, "Downloads"));
        TreeNode file1 = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "File 1"));
        TreeNode file2 = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "File 2"));
        TreeNode file3 = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "File 3"));
        TreeNode file4 = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "File 4"));
        fillDownloadsFolder(downloads);
        downloads.addChildren(file1, file2, file3, file4);

        TreeNode myMedia = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_photo_library, "Photos"));
        TreeNode photo1 = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_photo, "Photo 1"));
        TreeNode photo2 = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_photo, "Photo 2"));
        TreeNode photo3 = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_photo, "Photo 3"));
        myMedia.addChildren(photo1, photo2, photo3);

        myDocuments.addChild(downloads);
        computerRoot.addChildren(myDocuments, myMedia);

        root.addChildren(computerRoot);

        AndroidTreeView tView = new AndroidTreeView(getActivity(), root);
        tView.setDefaultContainerStyle(R.style.TreeNodeStyleCustom);
        tView.setDefaultViewHolder(IconTreeItemHolder.class);
        tView.setDefaultNodeClickListener(nodeClickListener);

        containerView.addView(tView.getView());
        tView.expandNode(computerRoot);

        return rootView;
    }

    private static int counter = 0;

    private void fillDownloadsFolder(TreeNode node) {
        TreeNode downloads = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_folder, "Downloads" + (counter++)));
        node.addChild(downloads);
        if (counter < 5) {
            fillDownloadsFolder(downloads);
        }
    }

    private TreeNode.TreeNodeClickListener nodeClickListener = new TreeNode.TreeNodeClickListener() {
        @Override
        public void onClick(TreeNode node, Object value) {
            IconTreeItemHolder.IconTreeItem item = (IconTreeItemHolder.IconTreeItem) value;
            statusBar.setText("Last clicked: " + item.text);
            if (node.isLeaf()) {
                Toast.makeText(getActivity(), item.text, Toast.LENGTH_SHORT).show();
            }
        }
    };
}
