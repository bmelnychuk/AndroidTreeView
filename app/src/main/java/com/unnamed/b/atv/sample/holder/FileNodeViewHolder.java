package com.unnamed.b.atv.sample.holder;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.sample.R;

import java.io.File;

/**
 * Created by Ye He on 20/01/16.
 */
public class FileNodeViewHolder extends TreeNode.BaseNodeViewHolder<FileNodeViewHolder.IconTreeItem> {

    public static final int INDENTATION = 20;

    public FileNodeViewHolder(Context context) {
        super(context);
    }

    @Override
    public View createNodeView(TreeNode node, IconTreeItem value) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.layout_file_node, null, false);
        TextView tvValue = (TextView) view.findViewById(R.id.node_value);
        if (value.file.getName().isEmpty()) {
            tvValue.setText("/");
        } else {
            tvValue.setText(value.file.getName());
        }

        final ImageView iconView = (ImageView) view.findViewById(R.id.icon);

        int level = node.getLevel();
        if (level > 0) {
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) iconView.getLayoutParams();
            Resources r = context.getResources();
            int dp = (level - 1) * INDENTATION;

            layoutParams.leftMargin = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    dp,
                    r.getDisplayMetrics()
            );
            iconView.setImageResource(value.icon);
        }

        return view;
    }

    public static class IconTreeItem {
        public File file;
        public int icon;

        public IconTreeItem(int icon, File file) {
            this.icon = icon;
            this.file = file;
        }
    }
}
