package com.unnamed.b.atv.sample.activity

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import com.unnamed.b.atv.sample.R

/**
 * Created by Bogdan Melnychuk on 2/12/15.
 * Converted to Kolin by Kumar Shivang on 16/07/17
 */
class SingleFragmentActivity : AppCompatActivity() {

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.activity_single_fragment)

        val b = intent.extras
        val fragmentClass = b.get(FRAGMENT_PARAM) as Class<*>
        if (bundle == null) {
            val f = Fragment.instantiate(this, fragmentClass.name)
            f.arguments = b
            supportFragmentManager.beginTransaction().replace(R.id.fragment, f, fragmentClass.name).commit()
        }
    }

    companion object {
        val FRAGMENT_PARAM = "fragment"
    }
}
