package com.unnamed.b.atv.view;

import android.content.Context;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.unnamed.b.atv.R;
import com.unnamed.b.atv.holder.SimpleViewHolder;
import com.unnamed.b.atv.model.TreeNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Bogdan Melnychuk on 2/10/15.
 */
public class AndroidTreeView {
    private static final String NODES_PATH_SEPARATOR = ";";

    public static final int DEFAULT_CONTAINER_STYLE = R.style.TreeNodeStyle;

    private TreeNode mRoot;
    private Context mContext;
    private boolean applyForRoot;
    private int containerStyle = 0;
    private Class<? extends TreeNode.BaseNodeViewHolder> defaultViewHolderClass = SimpleViewHolder.class;
    private TreeNode.TreeNodeClickListener nodeClickListener;
    private boolean mSelectionModeEnabled;

    public AndroidTreeView(Context context, TreeNode root) {
        mRoot = root;
        mContext = context;
    }

    public void setDefaultContainerStyle(int style) {
        setDefaultContainerStyle(style, false);
    }

    public void setDefaultContainerStyle(int style, boolean applyForRoot) {
        containerStyle = style;
        this.applyForRoot = applyForRoot;
    }

    public void setDefaultViewHolder(Class<? extends TreeNode.BaseNodeViewHolder> viewHolder) {
        defaultViewHolderClass = viewHolder;
    }

    public void setDefaultNodeClickListener(TreeNode.TreeNodeClickListener listener) {
        nodeClickListener = listener;
    }

    public void expandLevel(int level) {
        for (TreeNode n : mRoot.getChildren()) {
            expandLevel(n, level);
        }
    }


    public void expandAll() {
        for (TreeNode n : mRoot.getChildren()) {
            expandAllChildren(n);
        }
    }

    public void collapseAll() {
        for (TreeNode n : mRoot.getChildren()) {
            collapseAllChildren(n);
        }
    }


    public View getView(int style) {
        final ScrollView view;
        if (style > 0) {
            ContextThemeWrapper newContext = new ContextThemeWrapper(mContext, style);
            view = new ScrollView(newContext);
        } else {
            view = new ScrollView(mContext);
        }

        Context containerContext = mContext;
        if (containerStyle != 0 && applyForRoot) {
            containerContext = new ContextThemeWrapper(mContext, containerStyle);
        }
        final LinearLayout viewTreeItems = new LinearLayout(containerContext);

        viewTreeItems.setId(R.id.tree_items);
        viewTreeItems.setOrientation(LinearLayout.VERTICAL);
        view.addView(viewTreeItems);

        populateTree(mRoot, viewTreeItems);
        return view;
    }

    public View getView() {
        return getView(-1);
    }

    public String getSaveState() {
        final StringBuilder builder = new StringBuilder();
        getSaveState(mRoot, builder);
        if (builder.length() > 0) {
            builder.setLength(builder.length() - 1);
        }
        return builder.toString();
    }

    public void expandNode(TreeNode node) {
        final String[] path = node.getPath().split(TreeNode.NODES_ID_SEPARATOR);
        expandNode(path, mRoot, 1);
    }

    public void collapseNode(TreeNode node) {
        if (node.isRoot()) {
            throw new RuntimeException("Root node can't be collapsed");
        }
        toggleNode(node, false);
    }

    public void restoreState(String saveState) {
        if (!TextUtils.isEmpty(saveState)) {
            collapseAll();
            final String[] openNodesArray = saveState.split(NODES_PATH_SEPARATOR);
            final Set<String> openNodes = new HashSet<>(Arrays.asList(openNodesArray));
            restoreNodeState(mRoot, openNodes);
        }
    }

    public void setSelectionModeEnabled(boolean selectionModeEnabled) {
        if (!selectionModeEnabled) {
            // TODO fix double iteration over tree
            deselectAll();
        }
        mSelectionModeEnabled = selectionModeEnabled;


        for (TreeNode node : mRoot.getChildren()) {
            toggleSelectionMode(node, selectionModeEnabled);
        }
    }

    public List<TreeNode> getSelected() {
        if (mSelectionModeEnabled) {
            return getSelected(mRoot);
        } else {
            return new ArrayList<>();
        }
    }

    public void selectNode(TreeNode node) {
        if (mSelectionModeEnabled) {
            node.setSelected(true);
            toogleSelectionForNode(node, true);
        }
    }

    public void selectAll(boolean skipCollapsed) {
        makeAllSelection(true, skipCollapsed);
    }

    public void deselectAll() {
        makeAllSelection(false, false);
    }

    public void makeAllSelection(boolean selected, boolean skipCollapsed) {
        if (mSelectionModeEnabled) {
            for (TreeNode node : mRoot.getChildren()) {
                selectNode(node, selected, skipCollapsed);
            }
        }
    }

    private void selectNode(TreeNode parent, boolean selected, boolean skipCollapsed) {
        parent.setSelected(selected);
        toogleSelectionForNode(parent, true);
        boolean toContinue = skipCollapsed ? parent.isExpanded() : true;
        if (toContinue) {
            for (TreeNode node : parent.getChildren()) {
                selectNode(node, selected, skipCollapsed);
            }
        }
    }

    // TODO Do we need to go through whole tree? Save references or consider collapsed nodes as not selected
    private List<TreeNode> getSelected(TreeNode parent) {
        List<TreeNode> result = new ArrayList<>();
        for (TreeNode n : parent.getChildren()) {
            if (n.isSelected()) {
                result.add(n);
            }
            result.addAll(getSelected(n));
        }
        return result;
    }

    private void toggleSelectionMode(TreeNode parent, boolean mSelectionModeEnabled) {
        toogleSelectionForNode(parent, mSelectionModeEnabled);
        for (TreeNode node : parent.getChildren()) {
            toggleSelectionMode(node, mSelectionModeEnabled);
        }
    }


    private void expandNode(String[] path, TreeNode parent, int offset) {
        if (path.length >= offset) {
            final Integer nodeId = Integer.parseInt(path[path.length - offset]);
            for (TreeNode n : parent.getChildren()) {
                if (n.getId() == nodeId) {
                    toggleNode(n, true);
                    expandNode(path, n, offset + 1);
                }
            }
        }
    }

    private void restoreNodeState(TreeNode node, Set<String> openNodes) {
        for (TreeNode n : node.getChildren()) {
            if (openNodes.contains(n.getPath())) {
                toggleNode(n, true);
                restoreNodeState(n, openNodes);
            }
        }
    }

    private void getSaveState(TreeNode root, StringBuilder sBuilder) {
        for (TreeNode node : root.getChildren()) {
            if (node.isExpanded()) {
                sBuilder.append(node.getPath());
                sBuilder.append(NODES_PATH_SEPARATOR);
                getSaveState(node, sBuilder);
            }
        }
    }

    private void populateTree(final TreeNode node, final ViewGroup nodeItemsContainer) {
        for (final TreeNode n : node.getChildren()) {
            final TreeNode.BaseNodeViewHolder viewHolder = getViewHolderForNode(n);

            final View nodeView = viewHolder.getView();
            nodeItemsContainer.addView(nodeView);
            toggleNode(n, n.isExpanded());
            if (mSelectionModeEnabled) {
                getViewHolderForNode(node).toggleSelectionMode(mSelectionModeEnabled);
            }

            nodeView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggleNode(n);
                    if (n.getClickListener() != null) {
                        n.getClickListener().onClick(n, n.getValue());
                    } else if (nodeClickListener != null) {
                        nodeClickListener.onClick(n, n.getValue());
                    }
                }
            });

            populateTree(n, viewHolder.getNodeItemsView());

        }
    }

    private void toggleNode(TreeNode node) {
        toggleNode(node, !node.isExpanded());
    }

    private void toggleNode(TreeNode node, boolean active) {
        getViewHolderForNode(node).getNodeItemsView().setVisibility(active ? View.VISIBLE : View.GONE);
        getViewHolderForNode(node).toggle(active);
        node.setExpanded(active);
    }

    private void toogleSelectionForNode(TreeNode node, boolean makeSelectable) {
        getViewHolderForNode(node).toggleSelectionMode(makeSelectable);
    }

    private TreeNode.BaseNodeViewHolder getViewHolderForNode(TreeNode node) {
        TreeNode.BaseNodeViewHolder viewHolder = node.getViewHolder();
        if (viewHolder == null) {
            try {
                final Object object = defaultViewHolderClass.getConstructor(Context.class).newInstance(new Object[]{mContext});
                viewHolder = (TreeNode.BaseNodeViewHolder) object;
                node.setViewHolder(viewHolder);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (viewHolder.getContainerStyle() <= 0) {
            viewHolder.setContainerStyle(containerStyle);
        }
        return viewHolder;
    }

    private void collapseAllChildren(TreeNode node) {
        if (node.isExpanded()) {
            toggleNode(node);
        }
        for (TreeNode n : node.getChildren()) {
            collapseAllChildren(n);
        }
    }

    private void expandLevel(TreeNode node, int level) {
        if (node.getLevel() <= level) {
            toggleNode(node, true);
        }
        for (TreeNode n : node.getChildren()) {
            expandLevel(n, level);
        }
    }

    private void expandAllChildren(TreeNode node) {
        if (!node.isExpanded()) {
            toggleNode(node);
        }
        for (TreeNode n : node.getChildren()) {
            expandAllChildren(n);
        }
    }

}
