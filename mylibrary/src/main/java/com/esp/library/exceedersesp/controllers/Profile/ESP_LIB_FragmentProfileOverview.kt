package com.esp.library.exceedersesp.controllers.Profile


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.esp.library.R

import kotlinx.android.synthetic.main.esp_lib_fragment_profile_overview.view.*
import utilities.data.applicants.profile.ESP_LIB_ApplicationProfileDAO


class ESP_LIB_FragmentProfileOverview : androidx.fragment.app.Fragment() {

    internal var bundle: Bundle? = null
    internal var isprofile: Boolean = false


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.esp_lib_fragment_profile_overview, container, false)
        bundle = arguments
        val dataapplicant = bundle!!.getSerializable("dataapplicant") as ESP_LIB_ApplicationProfileDAO
        dataapplicant.applicant

        isprofile = bundle!!.getBoolean("isprofile", false)
        if (isprofile)
            moveToNext()

        view.btcontinuee.setOnClickListener{
            moveToNext()
        }

        return view

    }

    private fun moveToNext() {
        val navHostFragment = ESP_LIB_FragmentProfileImage()
        navHostFragment.arguments = bundle
        val fm = requireActivity().supportFragmentManager
        val ft = fm.beginTransaction()
        ft.setCustomAnimations(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left, R.anim.anim_slide_in_right, R.anim.anim_slide_out_right)
        ft.replace(R.id.request_fragment_org, navHostFragment)
        if (!isprofile)
            ft.addToBackStack("FragmentProfileOverview")
        ft.commit()
    }

}
