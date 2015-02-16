package com.unnamed.b.atv.sample.holder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.sample.R;

/**
 * Created by Bogdan Melnychuk on 2/15/15.
 */
public class SelectableItemHolder extends TreeNode.BaseNodeViewHolder<String> {
    private TextView tvValue;
    private CheckBox nodeSelector;

    public SelectableItemHolder(Context context) {
        super(context);
    }

    @Override
    public View createNodeView(final TreeNode node, String value) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.layout_selectable_item, null, false);

        tvValue = (TextView) view.findViewById(R.id.node_value);
        tvValue.setText(value);


        nodeSelector = (CheckBox) view.findViewById(R.id.node_selector);
        nodeSelector.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                node.setSelected(isChecked);
            }
        });
        nodeSelector.setChecked(node.isSelected());

        if (node.isLastChild()) {
            view.findViewById(R.id.bot_line).setVisibility(View.INVISIBLE);
        }

        return view;
    }


    @Override
    public void toggleSelectionMode(boolean editModeEnabled) {
        nodeSelector.setVisibility(editModeEnabled ? View.VISIBLE : View.GONE);
        nodeSelector.setChecked(mNode.isSelected());
    }
}
