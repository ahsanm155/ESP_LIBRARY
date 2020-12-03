package com.esp.library.exceedersesp.controllers.submissions


import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import com.esp.library.R
import com.esp.library.exceedersesp.fragments.applications.ESP_LIB_UsersApplicationsFragment
import com.esp.library.utilities.common.ESP_LIB_SharedPreference
import com.esp.library.utilities.customcontrols.ESP_LIB_CustomViewPager
import com.esp.library.utilities.customcontrols.ESP_LIB_myBadgeView
import com.esp.library.utilities.setup.ViewPagerAdapter
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.esp_lib_custom_tab_count.*


class ESP_LIB_SubmissionActivityTabs : androidx.fragment.app.Fragment() {

    internal var TAG = javaClass.simpleName


    private var viewPagerESPLIB: ESP_LIB_CustomViewPager? = null
    internal var submit_request: ESP_LIB_UsersApplicationsFragment? = null
    internal var fm: androidx.fragment.app.FragmentManager? = null
    internal var pref: ESP_LIB_SharedPreference? = null



    @SuppressLint("RestrictedApi")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.esp_lib_activity_submission_card_tabs, container, false)

        pref = ESP_LIB_SharedPreference(context)
        viewPagerESPLIB = v.findViewById(R.id.viewpager)
        setupViewPager(viewPagerESPLIB!!)
        tabLayout = v.findViewById(R.id.tabs)
        tabLayout?.setupWithViewPager(viewPagerESPLIB)
        viewPagerESPLIB?.setPagingEnabled(false);


        intializeTabs()

        return v
    }




    private fun intializeTabs()
    {
        val tab_all = LayoutInflater.from(context).inflate(R.layout.esp_lib_submission_custom_tab, null) as RelativeLayout
        val tab_text_all = tab_all.findViewById<View>(R.id.tab_text) as TextView
        tab_text_all.text = getString(R.string.esp_lib_text_all)
        tabLayout?.getTabAt(0)?.customView = tab_all
        val badge_all = activity?.let { ESP_LIB_myBadgeView(it, tab_all.findViewById(R.id.tab_badge)) }
        badge_all?.updateTabBadge(0)

        val tab_open = LayoutInflater.from(context).inflate(R.layout.esp_lib_submission_custom_tab, null) as RelativeLayout
        val tab_text_open = tab_open.findViewById<View>(R.id.tab_text) as TextView
        tab_text_open.text = getString(R.string.esp_lib_text_opencaps)
        tabLayout?.getTabAt(1)?.customView = tab_text_open
        val badge_open = activity?.let { ESP_LIB_myBadgeView(it, tab_open.findViewById(R.id.tab_badge)) }
        badge_open?.updateTabBadge(0)

        val tab_accepted = LayoutInflater.from(context).inflate(R.layout.esp_lib_submission_custom_tab, null) as RelativeLayout
        val tab_text_accepted = tab_accepted.findViewById<View>(R.id.tab_text) as TextView
        tab_text_accepted.text = getString(R.string.esp_lib_text_accepted)
        tabLayout?.getTabAt(2)?.customView = tab_accepted
        val badge_accepted = activity?.let { ESP_LIB_myBadgeView(it, tab_accepted.findViewById(R.id.tab_badge)) }
        badge_accepted?.updateTabBadge(0)

        val tab_rejected = LayoutInflater.from(context).inflate(R.layout.esp_lib_submission_custom_tab, null) as RelativeLayout
        val tab_text_rejected = tab_rejected.findViewById<View>(R.id.tab_text) as TextView
        tab_text_rejected.text = getString(R.string.esp_lib_text_rejected)
        tabLayout?.getTabAt(3)?.customView = tab_rejected
        val badge_rejected = activity?.let { ESP_LIB_myBadgeView(it, tab_rejected.findViewById(R.id.tab_badge)) }
        badge_rejected?.updateTabBadge(0)


    }


    private fun setupViewPager(viewPager: androidx.viewpager.widget.ViewPager) {
        val actualResponseJson=arguments?.getString("actualResponseJson")
        val adapter = requireActivity().supportFragmentManager.let { ViewPagerAdapter(it) }
        adapter.addFragment(ESP_LIB_SubmissionCardFragment.newInstance(getString(R.string.esp_lib_text_all),actualResponseJson!!), getString(R.string.esp_lib_text_all))
        adapter.addFragment(ESP_LIB_SubmissionCardFragment.newInstance(getString(R.string.esp_lib_text_opencaps),actualResponseJson), getString(R.string.esp_lib_text_opencaps))
        adapter.addFragment(ESP_LIB_SubmissionCardFragment.newInstance(getString(R.string.esp_lib_text_accepted),actualResponseJson), getString(R.string.esp_lib_text_accepted))
        adapter.addFragment(ESP_LIB_SubmissionCardFragment.newInstance(getString(R.string.esp_lib_text_rejected),actualResponseJson), getString(R.string.esp_lib_text_rejected))
        viewPager.adapter = adapter
    }

    companion object {

        var tabLayout: TabLayout? = null
        fun newInstance(): ESP_LIB_SubmissionActivityTabs {

            return ESP_LIB_SubmissionActivityTabs()
        }
    }



}

