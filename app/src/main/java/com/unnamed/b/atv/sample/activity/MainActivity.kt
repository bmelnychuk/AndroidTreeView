package com.unnamed.b.atv.sample.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import com.unnamed.b.atv.sample.R
import com.unnamed.b.atv.sample.fragment.*

/**
* Converted to Kolin by Kumar Shivang on 16/07/17
*/

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val listItems = LinkedHashMap<String, Class<*>>()
        listItems.put("Folder Structure Example", FolderStructureFragment::class.java)
        listItems.put("Custom Holder Example", CustomViewHolderFragment::class.java)
        listItems.put("Selectable Nodes", SelectableTreeFragment::class.java)
        listItems.put("2d scrolling", TwoDScrollingFragment::class.java)
        listItems.put("Expand with arrow only", TwoDScrollingArrowExpandFragment::class.java)


        val list = ArrayList(listItems.keys)
        val listview = findViewById(R.id.listview) as ListView
        val adapter = SimpleArrayAdapter(this, list)
        listview.adapter = adapter
        listview.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val clazz = listItems.values.toTypedArray()[position]
            val i = Intent(this@MainActivity, SingleFragmentActivity::class.java)
            i.putExtra(SingleFragmentActivity.FRAGMENT_PARAM, clazz)
            this@MainActivity.startActivity(i)
        }

    }

    private inner class SimpleArrayAdapter(context: Context, objects: List<String>) : ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, objects) {

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }
    }
}