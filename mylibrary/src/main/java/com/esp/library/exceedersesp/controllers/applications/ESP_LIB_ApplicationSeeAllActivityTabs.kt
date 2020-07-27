package com.esp.library.exceedersesp.controllers.applications


import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.viewpager.widget.ViewPager
import com.esp.library.R
import com.esp.library.exceedersesp.fragments.applications.ESP_LIB_SeeAllApplicationsFragment
import com.esp.library.exceedersesp.fragments.applications.ESP_LIB_UsersApplicationsFragment
import com.esp.library.utilities.common.ESP_LIB_Shared
import com.esp.library.utilities.common.ESP_LIB_SharedPreference
import com.esp.library.utilities.customcontrols.ESP_LIB_CustomViewPager
import com.esp.library.utilities.customcontrols.ESP_LIB_DisplayUtils
import com.esp.library.utilities.customcontrols.ESP_LIB_myBadgeView
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.esp_lib_activity_seeall_applications_tabs.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import utilities.data.applicants.ESP_LIB_ApplicationsDAO
import java.util.*


class ESP_LIB_ApplicationSeeAllActivityTabs : androidx.fragment.app.Fragment() {

    internal var TAG = javaClass.simpleName


    private var viewPagerESPLIB: ESP_LIB_CustomViewPager? = null
    internal var submit_request: ESP_LIB_UsersApplicationsFragment? = null
    internal var fm: androidx.fragment.app.FragmentManager? = null
    internal var pref: ESP_LIB_SharedPreference? = null


    var feed: String = ""
    var assigned: String = ""
    var count: Int = 0


    @SuppressLint("RestrictedApi")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.esp_lib_activity_seeall_applications_tabs, container, false)
        when (ESP_LIB_Shared.getInstance().isWifiConnected(requireContext())) {
            true -> getSubDefinations(v)
            false -> ESP_LIB_Shared.getInstance().showAlertMessage(getString(R.string.esp_lib_text_internet_error_heading), getString(R.string.esp_lib_text_internet_connection_error), requireActivity())
        }




        return v
    }

    private fun initailizeData(v: View, isFeed: Boolean)
    {
        initailize(v, isFeed)
        initalizeTabs(v, isFeed)
    }

    private fun initailize(v: View, isFeed: Boolean) {
        pref = ESP_LIB_SharedPreference(context)
        viewPagerESPLIB = v.findViewById(R.id.viewpager)
        setupViewPager(viewPagerESPLIB!!, isFeed)
        tabLayout = v.findViewById(R.id.tabs)
        tabLayout?.setupWithViewPager(viewPagerESPLIB)
        viewPagerESPLIB?.setPagingEnabled(false);
        feed = getString(R.string.esp_lib_text_feed)
        assigned = getString(R.string.esp_lib_text_assigned)
    }

    private fun initalizeTabs(v: View, isFeed: Boolean) {
        val tab1 = LayoutInflater.from(context).inflate(R.layout.esp_lib_custom_tab_count, null) as RelativeLayout
        val tab_text_1 = tab1.findViewById<View>(R.id.tab_text) as TextView
        tab_text_1.text = assigned
        tabLayout?.getTabAt(0)?.setCustomView(tab1)
        val badge1 = activity?.let { ESP_LIB_myBadgeView(it, tab1.findViewById(R.id.tab_badge)) }
        badge1?.updateTabBadge(0)
        tabLayout?.visibility = View.GONE
        if (isFeed) {
            val tab2 = LayoutInflater.from(context).inflate(R.layout.esp_lib_custom_tab_count, null) as RelativeLayout
            val tab_text_2 = tab2.findViewById<View>(R.id.tab_text) as TextView
            tab_text_2.text = feed
            tabLayout?.getTabAt(1)?.setCustomView(tab2)
            val badge2 = activity?.let { ESP_LIB_myBadgeView(it, tab2.findViewById(R.id.tab_badge)) }
            //set the badge for the tab
            badge2?.updateTabBadge(count)
            tabLayout?.visibility = View.VISIBLE
        }

        var tabMargin = ESP_LIB_DisplayUtils.dpToPx(activity, 40)

        if (count > 0)
            tabMargin = ESP_LIB_DisplayUtils.dpToPx(activity, 40)


        reduceMarginsInTabs(tabLayout!!, tabMargin)
    }

    private fun reduceMarginsInTabs(tabLayout: TabLayout, marginOffset: Int) {

        val tabStrip = tabLayout.getChildAt(0)
        if (tabStrip is ViewGroup) {
            for (i in 0 until tabStrip.childCount) {
                val tabView = tabStrip.getChildAt(i)
                if (tabView.layoutParams is ViewGroup.MarginLayoutParams) {
                    (tabView.layoutParams as ViewGroup.MarginLayoutParams).leftMargin = marginOffset
                    (tabView.layoutParams as ViewGroup.MarginLayoutParams).rightMargin = marginOffset
                }
            }

            tabLayout.requestLayout()
        }
    }


    private fun setLayoutMargin(LayoutMargin: Int) {
        try {
            val lp = viewPagerESPLIB?.layoutParams as CoordinatorLayout.LayoutParams
            lp.setMargins(0, 0, 0, LayoutMargin)
            viewPagerESPLIB?.layoutParams = lp
        } catch (e: Exception) {
        }
    }

    private fun setupViewPager(viewPager: ViewPager, isFeed: Boolean) {
        val adapter = fragmentManager?.let { ViewPagerAdapter(it) }
        adapter?.addFragment(ESP_LIB_SeeAllApplicationsFragment.newInstance(getString(R.string.esp_lib_text_assigned)), getString(R.string.esp_lib_text_assigned))
        if (isFeed)
            adapter?.addFragment(ESP_LIB_SeeAllApplicationsFragment.newInstance(getString(R.string.esp_lib_text_feed)), getString(R.string.esp_lib_text_feed))
        viewPager.adapter = adapter
    }

    internal inner class ViewPagerAdapter(manager: androidx.fragment.app.FragmentManager) : androidx.fragment.app.FragmentPagerAdapter(manager) {
        private val mFragmentList = ArrayList<androidx.fragment.app.Fragment>()
        private val mFragmentTitleList = ArrayList<String>()

        override fun getItem(position: Int): androidx.fragment.app.Fragment {
            return mFragmentList[position]
        }

        override fun getCount(): Int {
            return mFragmentList.size
        }

        fun addFragment(fragment: androidx.fragment.app.Fragment, title: String) {
            mFragmentList.add(fragment)
            mFragmentTitleList.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return mFragmentTitleList[position]
        }
    }

    companion object {

        var tabLayout: TabLayout? = null
        fun newInstance(): ESP_LIB_ApplicationSeeAllActivityTabs {

            return ESP_LIB_ApplicationSeeAllActivityTabs()
        }
    }

    override fun onResume() {
        super.onResume()

    }

    fun getSubDefinations(v: View) {
        v.shimmer_view_container.visibility=View.VISIBLE
        val apis = ESP_LIB_Shared.getInstance().retroFitObject(context)
        val def_call = apis.getfeedsList()
        def_call.enqueue(object : Callback<List<ESP_LIB_ApplicationsDAO>> {
            override fun onResponse(call: Call<List<ESP_LIB_ApplicationsDAO>>, response: Response<List<ESP_LIB_ApplicationsDAO>>) {
                if (response.body() != null && response.body().isNotEmpty()) {
                    initailizeData(v,true)
                } else {
                    initailizeData(v,false)
                }
                v.shimmer_view_container.visibility=View.GONE
            }

            override fun onFailure(call: Call<List<ESP_LIB_ApplicationsDAO>>, t: Throwable) {
                v.shimmer_view_container.visibility=View.GONE
                ESP_LIB_Shared.getInstance().messageBox(getString(R.string.esp_lib_text_some_thing_went_wrong), context as Activity?)

            }
        })

    }


}

