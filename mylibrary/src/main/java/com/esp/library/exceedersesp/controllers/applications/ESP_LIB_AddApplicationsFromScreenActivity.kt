package com.esp.library.exceedersesp.controllers.applications

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.view.Gravity
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.esp.library.R
import com.esp.library.exceedersesp.ESP_LIB_BaseActivity
import com.esp.library.exceedersesp.fragments.applications.ESP_LIB_AddApplicationFragment
import com.esp.library.ipaulpro.afilechooser.utils.FileUtils
import com.esp.library.utilities.common.*
import com.esp.library.utilities.setup.applications.ESP_LIB_ApplicationFieldsRecyclerAdapter
import com.esp.library.utilities.setup.applications.ESP_LIB_ListAddApplicationAdapter
import kotlinx.android.synthetic.main.esp_lib_activity_add_applications_form.*
import kotlinx.android.synthetic.main.esp_lib_gradienttoolbar.*
import utilities.data.applicants.addapplication.ESP_LIB_CategoryAndDefinationsDAO
import utilities.data.applicants.dynamics.ESP_LIB_DynamicFormSectionFieldDAO


class ESP_LIB_AddApplicationsFromScreenActivity : ESP_LIB_BaseActivity(), ESP_LIB_ListAddApplicationAdapter.CategorySelection,
        ESP_LIB_AlertActionWindow.ActionInterface {


    internal var TAG = javaClass.simpleName
    internal var context: ESP_LIB_BaseActivity? = null
    internal var fm: androidx.fragment.app.FragmentManager? = null
    internal var submit_request: ESP_LIB_AddApplicationFragment? = null
    internal var definationsDAOESPLIB: ESP_LIB_CategoryAndDefinationsDAO? = null
    internal var imm: InputMethodManager? = null
    internal var upload_file: ESP_LIB_DynamicFormSectionFieldDAO? = null
    internal var pref: ESP_LIB_SharedPreference? = null

    override fun StatusChange(update: ESP_LIB_DynamicFormSectionFieldDAO) {
        upload_file = update

        val getContentIntent = FileUtils.createGetContentIntent()
        val intent = Intent.createChooser(getContentIntent, getString(R.string.esp_lib_text_selectafile))
        startActivityForResult(intent, REQUEST_CHOOSER)
    }

    override fun SingleSelection(update: ESP_LIB_DynamicFormSectionFieldDAO) {

        if (submit_request != null) {
            submit_request!!.SingleSelection(update)
        }
    }

    override fun LookUp(update: ESP_LIB_DynamicFormSectionFieldDAO) {

        upload_file = update

        val bundle = Bundle()
        bundle.putSerializable(ESP_LIB_DynamicFormSectionFieldDAO.BUNDLE_KEY, update)
        ESP_LIB_Shared.getInstance().callIntentWithResult(ESP_LIB_ChooseLookUpOption::class.java, context, bundle, 2)

    }



    override fun mActionTo(whattodo: String) {
        if (whattodo == getString(R.string.esp_lib_text_draft)) {
            if (submit_request != null) {
                submit_request!!.SubmitRequest(getString(R.string.esp_lib_text_draft))
            }
        } else {
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        changeStatusBarColor(true)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.esp_lib_activity_add_applications_form)
        initialize()
        //  setGravity()
        val bnd = intent.extras
        //  var appStatus: String? = null
        if (bnd != null) {
            definationsDAOESPLIB = bnd.getSerializable(ESP_LIB_CategoryAndDefinationsDAO.BUNDLE_KEY) as ESP_LIB_CategoryAndDefinationsDAO
            /*if (definationsDAO != null)
                appStatus = bnd.getString("appStatus")*/
            if (definationsDAOESPLIB?.parentApplicationInfo != null) {
                definitionDescription.text = definationsDAOESPLIB?.parentApplicationInfo?.descriptionFieldValue
                definitionNameTitle.text = definationsDAOESPLIB?.parentApplicationInfo?.titleFieldValue
            } else if (definationsDAOESPLIB != null) {
                definitionDescription.text = definationsDAOESPLIB?.description
                definitionNameTitle.text = definationsDAOESPLIB?.name
            }
        }


        /*definitionDescription.setOnClickListener {

            if (definitionDescription.maxLines == 3)
                definitionDescription.maxLines = 50
            else
                definitionDescription.maxLines = 3

        }*/
        ESP_LIB_CustomLogs.displayLogs("$TAG appStatus ")


        fm = this.context?.supportFragmentManager
        if (definationsDAOESPLIB != null)
            submit_request = ESP_LIB_AddApplicationFragment.newInstance(definationsDAOESPLIB, submit_btn, definitionDescription, definitionNameTitle)

        val ft = fm!!.beginTransaction()
        ft.add(R.id.request_fragment, submit_request!!)
        ft.commit()

        submit_btn.setOnClickListener {
            if (submit_request != null) {
                val curr_view = currentFocus
                if (curr_view != null) {
                    imm!!.hideSoftInputFromWindow(curr_view.windowToken, 0)
                }

                submit_request!!.SubmitRequest(getString(R.string.esp_lib_text_submit))
            }
        }

        ESP_LIB_KeyboardUtils.addKeyboardToggleListener(this,
                object : ESP_LIB_KeyboardUtils.SoftKeyboardToggleListener {
                    override fun onToggleSoftKeyboard(isVisible: Boolean) {
                        submit_request?.refreshAdapter(isVisible)
                    }
                })

    }

    private fun initialize() {
        context = this@ESP_LIB_AddApplicationsFromScreenActivity
        pref = ESP_LIB_SharedPreference(context)
        submit_btn.isEnabled = false
        submit_btn.alpha = 0.5f
        submit_btn.setBackgroundResource(R.drawable.esp_lib_drawable_draw_bg_grey_disable_button)

        if (intent.getStringExtra("toolbarheading").isNullOrEmpty())
            toolbarheading.text = getString(R.string.esp_lib_text_add) + " " + pref?.getlabels()?.application
        else
            toolbarheading.text = intent.getStringExtra("toolbarheading")

        imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        setSupportActionBar(gradienttoolbar)
        supportActionBar?.title = ""
        //  ibToolbarBack.setImageResource(R.drawable.ic_nav_close)
        ibToolbarBack.setOnClickListener() {
            val view = currentFocus
            if (view != null) {
                imm!!.hideSoftInputFromWindow(view.windowToken, 0)
            }
            val action_window = ESP_LIB_AlertActionWindow.newInstance(getString(R.string.esp_lib_text_save_draft), getString(R.string.esp_lib_text_your) + " " + pref?.getlabels()?.application + " " + getString(R.string.esp_lib_text_wasnotsubmitted), getString(R.string.esp_lib_text_save_draft_ok), getString(R.string.esp_lib_text_discard) + " " + pref?.getlabels()?.application, getString(R.string.esp_lib_text_draft))
            action_window.show(supportFragmentManager, "")
            action_window.isCancelable = true
        }
    }


    override fun onBackPressed() {

        val action_window = ESP_LIB_AlertActionWindow.newInstance(getString(R.string.esp_lib_text_save_draft), getString(R.string.esp_lib_text_your) + " " + pref?.getlabels()?.application + " " + getString(R.string.esp_lib_text_wasnotsubmitted), getString(R.string.esp_lib_text_save_draft_ok), getString(R.string.esp_lib_text_discard) + " " + pref?.getlabels()?.application, getString(R.string.esp_lib_text_draft))
        action_window.show(supportFragmentManager, "")
        action_window.isCancelable = true
    }

    private fun setGravity() {
        if (pref?.language.equals("ar", ignoreCase = true)) {
            toolbarheading.gravity = Gravity.RIGHT

        } else {
            toolbarheading.gravity = Gravity.LEFT
        }
    }

    companion object {
        var ACTIVITY_NAME = "controllers.applications.AddApplicationsFromScreenActivity"
        private val REQUEST_CHOOSER = 1234
    }


    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0)

                    if (ESP_LIB_AddApplicationFragment.isCalculatedField) {
                        ESP_LIB_AddApplicationFragment.isCalculatedField = false
                        val handler = Handler()
                        handler.postDelayed({
                            submit_request?.mApplicationSectionsAdapter?.notifyDataSetChanged()
                        }, 500)
                    }


                }
            }
        }

        return super.dispatchTouchEvent(event)
    }


    override fun onDestroy() {
        super.onDestroy()
        ESP_LIB_ApplicationFieldsRecyclerAdapter.isCalculatedMappedField = false
        ESP_LIB_AddApplicationFragment.isCalculatedField = false
    }


}
