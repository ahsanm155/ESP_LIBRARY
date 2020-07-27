package com.esp.library.exceedersesp.fragments.applications

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.esp.library.R
import com.esp.library.exceedersesp.ESP_LIB_BaseActivity
import com.esp.library.exceedersesp.controllers.applications.ESP_LIB_ApplicationSeeAllActivityTabs
import com.esp.library.utilities.common.ESP_LIB_Shared
import com.esp.library.utilities.common.ESP_LIB_SharedPreference
import kotlinx.android.synthetic.main.esp_lib_gradienttoolbar.*


class ESP_LIB_UsersCardApplications : AppCompatActivity() {


    internal var TAG = javaClass.simpleName
    var context: Context? = null
    var pref: ESP_LIB_SharedPreference? = null
    var pDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
       changeStatusBarColor(true)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.esp_lib_card_users_applications)
        initailize()
        val fm = getSupportFragmentManager()
        val submit_request_tabs = ESP_LIB_ApplicationSeeAllActivityTabs.newInstance()
        val ft = fm.beginTransaction()
        ft.add(R.id.request_fragment, submit_request_tabs)
        ft.commit()


    }

    private fun changeStatusBarColor(alsoFullScreen: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            if (alsoFullScreen) {
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            }
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.TRANSPARENT
        }
    }

    private fun initailize() {
        context = this
        pref = ESP_LIB_SharedPreference(context)
        pDialog = ESP_LIB_Shared.getInstance().setProgressDialog(context)
        setSupportActionBar(gradienttoolbar)
        ibToolbarBack.setOnClickListener { onBackPressed() }
        pDialog = ESP_LIB_Shared.getInstance().setProgressDialog(this)
        toolbarheading.text = getString(R.string.esp_lib_text_requesttoaction)


    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
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
