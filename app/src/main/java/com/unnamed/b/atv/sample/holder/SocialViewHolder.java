package com.unnamed.b.atv.sample.holder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.github.johnkil.print.PrintView;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.sample.R;

import java.util.Random;

/**
 * Created by Bogdan Melnychuk on 2/13/15.
 */
public class SocialViewHolder extends TreeNode.BaseNodeViewHolder<SocialViewHolder.SocialItem> {

    private static final String[] NAMES = new String[]{"Bruce Wayne", "Clark Kent", "Barry Allen", "Hal Jordan"};

    public SocialViewHolder(Context context) {
        super(context);
    }

    @Override
    public View createNodeView(TreeNode node, SocialItem value) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.layout_social_node, null, false);

        final PrintView iconView = (PrintView) view.findViewById(R.id.icon);
        iconView.setIconText(context.getResources().getString(value.icon));

        TextView connectionsLabel = (TextView) view.findViewById(R.id.connections);
        Random r = new Random();
        connectionsLabel.setText(r.nextInt(150) + " connections");

        TextView userNameLabel = (TextView) view.findViewById(R.id.username);
        userNameLabel.setText(NAMES[r.nextInt(4)]);

        TextView sizeText = (TextView) view.findViewById(R.id.size);
        sizeText.setText(r.nextInt(10) + " items");

        return view;
    }

    @Override
    public void toggle(boolean active) {
    }


    public static class SocialItem {
        public int icon;

        public SocialItem(int icon) {
            this.icon = icon;
        }
        // rest will be hardcoded
    }

}
