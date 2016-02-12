package com.unnamed.b.atv.sample.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.unnamed.b.atv.sample.R;
import com.unnamed.b.atv.sample.fragment.CustomViewHolderFragment;
import com.unnamed.b.atv.sample.fragment.FolderStructureFragment;
import com.unnamed.b.atv.sample.fragment.SelectableTreeFragment;
import com.unnamed.b.atv.sample.fragment.TwoDScrollingArrowExpandFragment;
import com.unnamed.b.atv.sample.fragment.TwoDScrollingFragment;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;


public class MainActivity extends ActionBarActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        final LinkedHashMap<String, Class<?>> listItems = new LinkedHashMap<>();
        listItems.put("Folder Structure Example", FolderStructureFragment.class);
        listItems.put("Custom Holder Example", CustomViewHolderFragment.class);
        listItems.put("Selectable Nodes", SelectableTreeFragment.class);
        listItems.put("2d scrolling", TwoDScrollingFragment.class);
        listItems.put("Expand with arrow only", TwoDScrollingArrowExpandFragment.class);


        final List<String> list = new ArrayList(listItems.keySet());
        final ListView listview = (ListView) findViewById(R.id.listview);
        final SimpleArrayAdapter adapter = new SimpleArrayAdapter(this, list);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Class<?> clazz = listItems.values().toArray(new Class<?>[]{})[position];
                Intent i = new Intent(MainActivity.this, SingleFragmentActivity.class);
                i.putExtra(SingleFragmentActivity.FRAGMENT_PARAM, clazz);
                MainActivity.this.startActivity(i);
            }
        });

    }

    private class SimpleArrayAdapter extends ArrayAdapter<String> {
        public SimpleArrayAdapter(Context context, List<String> objects) {
            super(context, android.R.layout.simple_list_item_1, objects);

        }

        @Override
        public long getItemId(int position) {
            return position;
        }
    }
}