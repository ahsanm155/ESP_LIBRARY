package com.esp.library.exceedersesp.controllers.Profile

import android.os.Bundle
import com.esp.library.R
import com.esp.library.exceedersesp.ESP_LIB_BaseActivity
import com.esp.library.utilities.common.ESP_LIB_Shared


import com.esp.library.exceedersesp.controllers.applications.ESP_LIB_ApplicationsActivityDrawer

import utilities.data.applicants.profile.ESP_LIB_ApplicationProfileDAO

class ESP_LIB_ProfileMainActivity : ESP_LIB_BaseActivity() {
    var navHostESPLIBFragment: ESP_LIB_FragmentProfileOverview? = null
    internal var ischeckerror: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        changeStatusBarColor(true)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.esp_lib_profile_main_activity)


        val dataapplicant = intent.getSerializableExtra("dataapplicant") as ESP_LIB_ApplicationProfileDAO
        dataapplicant.applicant.isProfileSubmitted

        ischeckerror = intent.getBooleanExtra("ischeckerror", false)
        val bundle = Bundle()
        bundle.putSerializable("dataapplicant", dataapplicant)
        bundle.putBoolean("ischeckerror", ischeckerror)
        bundle.putBoolean("isprofile", intent.getBooleanExtra("isprofile", false))
        navHostESPLIBFragment = ESP_LIB_FragmentProfileOverview()
        navHostESPLIBFragment?.arguments = bundle
        val fm = supportFragmentManager
        val ft = fm.beginTransaction()
        ft.setCustomAnimations(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left, R.anim.anim_slide_in_right, R.anim.anim_slide_out_right)
        ft.add(R.id.request_fragment_org, navHostESPLIBFragment!!)
        ft.commit()

    }

    override fun onBackPressed() {

        if (ischeckerror)
            ESP_LIB_Shared.getInstance().callIntentClearAllActivities(ESP_LIB_ApplicationsActivityDrawer::class.java, bContext, null)
        else
            super.onBackPressed()
    }
}
