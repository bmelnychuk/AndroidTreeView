AndroidTreeView
====================

### Recent changes

This repo has Diverged from the original one. Haven't setup maven dependency yet. Anyone who is interested can help.

### Description

Tree view implementation for android

[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-AndroidTreeView-brightgreen.svg?style=flat)](https://android-arsenal.com/details/1/1534)

### Demo

[![AndroidTreeView Demo on Google Play Store](http://developer.android.com/images/brand/en_generic_rgb_wo_60.png)](https://play.google.com/store/apps/details?id=com.unnamed.b.atv.demo)


### Features
+ 1. N - level expandable/collapsable tree
+ 2. Custom values, views, styles for nodes
+ 3. Save state after rotation
+ 4. Selection mode for nodes
+ 5. Dynamic add/remove node

### Known Limitations
+ For Android 4.0 (+/- nearest version) if you have too deep view hierarchy and with tree its easily possible, your app may crash

<br>
<br>

<img width='300' hspace='20' align='left' src='https://lh4.ggpht.com/xzkb3N58LH2Tsb_gGs0u3_x81VOLwlhcp-f4pz_sR_iR3vAKXfJoAcwBjN74LvzpVLE=h900-rw' />

<img width='300' hspace='20' src='https://lh5.ggpht.com/Ut6By_iUnkNfzIbaPBsc8hBeQeFj_2UXJh_1tfwDdlTAqGkhiR72A_AwQ0L0GH3OFag=h900-rw' />

<img width='300' hspace='20' src='https://www.dropbox.com/s/nc6q4jubaau0x5m/Screenshot_2015-02-15-23-16-56.png?dl=1' />
<img width='300' hspace='20' src='https://drive.google.com/uc?id=0B3hs6EXn55WUNzJmelk3cmRzcEE' />


### Integration

**1)** Add library as a dependency to your project 

```compile 'com.github.bmelnychuk:atv:1.2.+'```

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

### Projects using this library

[Blue Dot : World Chat](https://play.google.com/store/apps/details?id=com.commandapps.bluedot)
