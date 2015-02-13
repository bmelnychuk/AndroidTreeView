AndroidTreeView
====================

### Description

Tree view implementation for android

### Featrues
+ 1. N - level expandable/collapsable tree
+ 2. Custom values, views, styles for nodes
+ 3. Save state after ration

<img width='300' hspace='20' align='left' src='https://lh5.googleusercontent.com/ULU7wQysM8ZBSdKnqTT7UyzLDiFYQStW0vp-YTP64yZlLR5SVT4jP38bcxN02AXzV0QA=w2506-h1116' />

<img width='300' hspace='20' src='https://lh4.googleusercontent.com/4gaWF0mkyCGCkwZIedX-UETFOBNcP1I78YWtZnMPt-S5qePXpdP47QmK8Sp5JR4FCSbu=w2506-h1116' />

### Integration

**1)** Add library as a dependency to your project

**2)** Create your tree starting from root element. ```TreeNode.root()``` element will not be displayed so it doesn't require anything to be set.
```java
TreeNode root = TreeNode.root();
```

Create and add your nodes (use your custom object as constructor param)
```java
 TreeNode parent = new TreeNode("MyParentNode");
 TreeNode child0 = new TreeNode("ChildNode0");
 TreeNode child1 = new TreeNode("ChildNode1");
 parent.addChildren(child0, child1);
 root.addChild(parent);
```

**3)** Add tree view to layout
```java 
 AndroidTreeView tView = new AndroidTreeView(getActivity(), root);
 containerView.addView(tView.getView());
``` 
The simplest but not styled tree is ready. Now you can see ```parent``` node as root of your tree

**4)** Custom view for nodes

Extend ```TreeNode.BaseNodeViewHolder``` and overwrite ```createNodeView``` method to prepare custom view for node:
```java
public class MyHolder extends TreeNode.BaseNodeViewHolder<IconTreeItem> {
    ...
    @Override
    public View createNodeView(TreeNode node, IconTreeItem value) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.layout_profile_node, null, false);
        TextView tvValue = (TextView) view.findViewById(R.id.node_value);
        tvValue.setText(value.text);
        
        return view;
    }
    ...
    public static class IconTreeItem {
        public int icon;
        public String text;
    }
}
```

**5)** Connect view holder with node 
```java 
  IconTreeItem nodeItem = new IconTreeItem();
  TreeNode child1 = new TreeNode(nodeItem).setViewHolder(new MyHolder(mContext));
```

**6)** Consider using 
```java 
TreeNode.setClickListener(TreeNodeClickListener listener);
AndroidTreeView.setDefaultViewHolder
AndroidTreeView.setDefaultNodeClickListener
...
```

For more details use sample application as example
