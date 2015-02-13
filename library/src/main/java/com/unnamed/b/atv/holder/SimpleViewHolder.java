package com.unnamed.b.atv.holder;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.unnamed.b.atv.model.TreeNode;

/**
 * Created by Bogdan Melnychuk on 2/11/15.
 */
public class SimpleViewHolder extends TreeNode.BaseNodeViewHolder {

    public SimpleViewHolder(Context context) {
        super(context);
    }

    @Override
    public View createNodeView(TreeNode node) {
        final TextView tv = new TextView(context);
        tv.setText(String.valueOf(node.getValue()));
        return tv;
    }

    @Override
    public void toggle(boolean active) {

    }
}
