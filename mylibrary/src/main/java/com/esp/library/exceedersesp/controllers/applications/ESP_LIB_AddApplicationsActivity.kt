package com.esp.library.exceedersesp.controllers.applications

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.Gravity
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.esp.library.R
import com.esp.library.exceedersesp.fragments.applications.ESP_LIB_AddApplicationCategoryAndDefinationsFragment
import com.esp.library.utilities.common.ESP_LIB_SharedPreference
import com.esp.library.exceedersesp.ESP_LIB_BaseActivity
import kotlinx.android.synthetic.main.esp_lib_gradienttoolbar.*
import utilities.adapters.setup.applications.ESP_LIB_ListApplicationCategoryAndDefinationAdapter
import utilities.data.applicants.addapplication.ESP_LIB_CategoryAndDefinationsDAO


class ESP_LIB_AddApplicationsActivity : ESP_LIB_BaseActivity(), ESP_LIB_ListApplicationCategoryAndDefinationAdapter.CategorySelection {


    internal var context: ESP_LIB_BaseActivity? = null
    internal var fm: androidx.fragment.app.FragmentManager? = null
    internal var submit_request: ESP_LIB_AddApplicationCategoryAndDefinationsFragment? = null
    internal var imm: InputMethodManager? = null
    internal var pref: ESP_LIB_SharedPreference? = null


    override fun StatusChange(update: ESP_LIB_CategoryAndDefinationsDAO) {
        if (submit_request != null) {
            submit_request!!.UpdateDefincation(update)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        changeStatusBarColor(true)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.esp_lib_activity_add_applications)
        initailize()
        //  setGravity()

        val ft = fm!!.beginTransaction()
        ft.add(R.id.request_fragment, submit_request!!)
        ft.commit()


    }

    private fun initailize() {
        context = this@ESP_LIB_AddApplicationsActivity
        pref = ESP_LIB_SharedPreference(context)
        imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        setSupportActionBar(gradienttoolbar)
        supportActionBar?.setTitle("")
        toolbarheading.text = getString(R.string.esp_lib_text_select) + " " + pref?.getlabels()?.application
        ibToolbarBack.setOnClickListener { onBackPressed() }
        fm = getSupportFragmentManager()
        submit_request = ESP_LIB_AddApplicationCategoryAndDefinationsFragment.newInstance()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == 2) {

            if (data != null) {
                val bnd = data.extras

                if (bnd != null) {
                    if (bnd.getBoolean("whatodo")) {
                        val bnd_ = Bundle()
                        bnd_.putBoolean("whatodo", true)
                        val intent = Intent()
                        intent.putExtras(bnd_)
                        setResult(2, intent)
                        finish()

                    }
                }
            }
        }


        super.onActivityResult(requestCode, resultCode, data)

    }

    private fun setGravity() {
        val pref = ESP_LIB_SharedPreference(context)
        if (pref.language.equals("ar", ignoreCase = true)) {
            toolbarheading.gravity = Gravity.RIGHT

        } else {
            toolbarheading.gravity = Gravity.LEFT
        }
    }

    companion object {
        var ACTIVITY_NAME = "controllers.applications.AddApplicationsActivity"
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (currentFocus != null)
            imm?.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val v = getCurrentFocus()
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.windowToken, 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }
}
