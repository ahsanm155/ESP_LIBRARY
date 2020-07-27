package com.esp.library.exceedersesp.controllers.applications


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.esp.library.R
import com.esp.library.exceedersesp.ESP_LIB_ESPApplication
import com.esp.library.exceedersesp.fragments.applications.ESP_LIB_UsersApplicationsFragment
import com.esp.library.utilities.common.ESP_LIB_Enums
import com.esp.library.utilities.common.ESP_LIB_Shared
import com.esp.library.utilities.common.ESP_LIB_SharedPreference
import com.esp.library.utilities.customcontrols.ESP_LIB_CustomViewPager
import com.esp.library.utilities.customcontrols.ESP_LIB_DisplayUtils
import com.esp.library.utilities.customcontrols.ESP_LIB_myBadgeView
import com.esp.library.utilities.customevents.EventOptions
import com.esp.library.utilities.customevents.EventOptions.EventRefreshData
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.esp_lib_activity_main_applications_tabs.*
import kotlinx.android.synthetic.main.esp_lib_activity_main_applications_tabs.view.*
import org.greenrobot.eventbus.EventBus
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import utilities.data.applicants.addapplication.ESP_LIB_CategoryAndDefinationsDAO
import java.util.*


class ESP_LIB_ApplicationActivityTabs : androidx.fragment.app.Fragment() {

    internal var TAG = javaClass.simpleName


    private var viewPagerESPLIB: ESP_LIB_CustomViewPager? = null
    internal var submit_request: ESP_LIB_UsersApplicationsFragment? = null
    internal var fm: androidx.fragment.app.FragmentManager? = null
    internal var pref: ESP_LIB_SharedPreference? = null
    internal var rlbottomSheetHeader: RelativeLayout? = null
    internal var txtsubmissionrequest: TextView? = null
    internal var subDefinitionBody: ArrayList<ESP_LIB_CategoryAndDefinationsDAO>? = null


    var open: String = ""
    var closed: String = ""

    // internal var add_account: FloatingActionButton? = null
    var count: Int = 0


    @SuppressLint("RestrictedApi")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.esp_lib_activity_main_applications_tabs, container, false)

        pref = ESP_LIB_SharedPreference(context)
        viewPagerESPLIB = v.findViewById(R.id.viewpager)
        rlbottomSheetHeader = v.findViewById(R.id.rlbottomSheetHeader)
        txtsubmissionrequest = v.findViewById(R.id.txtsubmissionrequest)

        //    add_account = v.findViewById(R.id.addaccount)
        setupViewPager(viewPagerESPLIB!!)
        tabLayout = v.findViewById(R.id.tabs)
        tabLayout?.setupWithViewPager(viewPagerESPLIB)
        viewPagerESPLIB?.setPagingEnabled(false);
        //if (ESPApplication.getInstance()?.user?.loginResponse?.role?.toLowerCase() == getString(R.string.applicantsmall)) {
        open = getString(R.string.esp_lib_text_opencaps)
        closed = getString(R.string.esp_lib_text_closed)
        /*} else {
            open = getString(R.string.pending)
            closed = getString(R.string.all)
        }*/

        val tab1 = LayoutInflater.from(context).inflate(R.layout.esp_lib_custom_tab_count, null) as RelativeLayout
        val tab_text_1 = tab1.findViewById<View>(R.id.tab_text) as TextView
        tab_text_1.text = open
        tabLayout?.getTabAt(0)?.setCustomView(tab1)
        val badge1 = activity?.let { ESP_LIB_myBadgeView(it, tab1.findViewById(R.id.tab_badge)) }
        badge1?.updateTabBadge(0)

        val tab2 = LayoutInflater.from(context).inflate(R.layout.esp_lib_custom_tab_count, null) as RelativeLayout
        val tab_text_2 = tab2.findViewById<View>(R.id.tab_text) as TextView
        tab_text_2.text = closed
        tabLayout?.getTabAt(1)?.setCustomView(tab2)
        val badge2 = activity?.let { ESP_LIB_myBadgeView(it, tab2.findViewById(R.id.tab_badge)) }
        //set the badge for the tab
        badge2?.updateTabBadge(count)


        var tabMargin = ESP_LIB_DisplayUtils.dpToPx(activity, 50)

        if (count > 0)
            tabMargin = ESP_LIB_DisplayUtils.dpToPx(activity, 40)


        reduceMarginsInTabs(tabLayout!!, tabMargin)


        addaccount?.setOnClickListener { view ->
            val manager = fragmentManager
            if (ESP_LIB_ESPApplication.getInstance().user.profileStatus == null || ESP_LIB_ESPApplication.getInstance().user.profileStatus!!.equals(getString(R.string.esp_lib_text_profile_complete), ignoreCase = true)) {
                ESP_LIB_Shared.getInstance().callIntentWithResult(ESP_LIB_AddApplicationsActivity::class.java, activity, null, 2)
            } else if (ESP_LIB_ESPApplication.getInstance().user.profileStatus!!.equals(getString(R.string.esp_lib_text_profile_incomplete), ignoreCase = true)) {
                ESP_LIB_Shared.getInstance().showAlertProfileMessage(getString(R.string.esp_lib_text_profile_error_heading), getString(R.string.esp_lib_text_profile_error_desc), activity)
            } else if (ESP_LIB_ESPApplication.getInstance().user.profileStatus!!.equals(getString(R.string.esp_lib_text_profile_incomplete_admin), ignoreCase = true)) {
                ESP_LIB_Shared.getInstance().showAlertProfileMessage(getString(R.string.esp_lib_text_profile_error_heading), getString(R.string.esp_lib_text_profile_error_desc_admin), activity)
            }
        }


        v.rlbottomSheetHeader.setOnClickListener {

            val intent = Intent(context, ESP_LIB_ActivitySubmissionRequests::class.java)
            intent.putExtra("subDefinitionBody", subDefinitionBody)
            startActivity(intent)
        }





        return v
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

    fun loadDefinations() {

        //start_loading_animation()

        val apis = ESP_LIB_Shared.getInstance().retroFitObject(context)
        val def_call = apis.getSubDefinitionList()
        def_call.enqueue(object : Callback<List<ESP_LIB_CategoryAndDefinationsDAO>> {
            @SuppressLint("RestrictedApi")
            override fun onResponse(call: Call<List<ESP_LIB_CategoryAndDefinationsDAO>>, response: Response<List<ESP_LIB_CategoryAndDefinationsDAO>>) {
                //stop_loading_animation()
                if (response.body() != null && response.body().size > 0) {
                    val body = response.body()
                    subDefinitionBody = body as ArrayList<ESP_LIB_CategoryAndDefinationsDAO>?
                    txtsubmissionrequest?.text = pref?.getlabels()?.submissionRequests + " (" + body?.size + ")"


                    if (ESP_LIB_ESPApplication.getInstance().isComponent || ESP_LIB_ESPApplication.getInstance()?.user?.loginResponse?.role?.toLowerCase(Locale.getDefault()) == ESP_LIB_Enums.applicant.toString()) {
                        try {
                            setLayoutMargin(resources.getDimensionPixelSize(R.dimen._50sdp))

                            /* Bundle bnd = new Bundle();
                            bnd.putBoolean("whatodo", true);
                            Intent intent = new Intent();
                            intent.putExtras(bnd);
                            bContext.setResult(2, intent);
                            bContext.finish();*/
                            EventBus.getDefault().post(EventOptions.EventBottomSheetShowing())
                            rlbottomSheetHeader?.visibility = View.VISIBLE
                        } catch (e: Exception) {
                        }
                    }


                } else {
                    rlbottomSheetHeader?.visibility = View.GONE
                    setLayoutMargin(0)
                }
            }

            override fun onFailure(call: Call<List<ESP_LIB_CategoryAndDefinationsDAO>>, t: Throwable) {
                //   Shared.getInstance().messageBox(getString(R.string.some_thing_went_wrong), context as Activity?)
                // stop_loading_animation()
                // UnSuccessResponse()
            }
        })

    }

    private fun setLayoutMargin(LayoutMargin: Int) {
        try {
            val lp = viewPagerESPLIB?.layoutParams as CoordinatorLayout.LayoutParams
            lp.setMargins(0, 0, 0, LayoutMargin)
            viewPagerESPLIB?.layoutParams = lp
        } catch (e: Exception) {
        }
    }

    private fun setupViewPager(viewPager: androidx.viewpager.widget.ViewPager) {
        val adapter = fragmentManager?.let { ViewPagerAdapter(it) }
        // if (ESPApplication.getInstance()?.user?.loginResponse?.role?.toLowerCase() == getString(R.string.applicantsmall)) {
        adapter?.addFragment(ESP_LIB_UsersApplicationsFragment.newInstance(getString(R.string.esp_lib_text_open)), getString(R.string.esp_lib_text_opencaps))
        adapter?.addFragment(ESP_LIB_UsersApplicationsFragment.newInstance(getString(R.string.esp_lib_text_closedsmall)), getString(R.string.esp_lib_text_closed))
        /*} else {
            adapter?.addFragment(UsersApplicationsFragment.newInstance("pending"), getString(R.string.pending))
            adapter?.addFragment(UsersApplicationsFragment.newInstance("all"), getString(R.string.all))
        }*/
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
        fun newInstance(): ESP_LIB_ApplicationActivityTabs {

            return ESP_LIB_ApplicationActivityTabs()
        }
    }

    override fun onResume() {
        super.onResume()

        if (ESP_LIB_ESPApplication.getInstance().isOnCLosedTab) {
            viewPagerESPLIB?.currentItem = 0
            ESP_LIB_ESPApplication.getInstance().isOnCLosedTab = false
        }

        /*if (ESP_LIB_ESPApplication.getInstance().isComponent || ESP_LIB_ESPApplication.getInstance()?.user?.loginResponse?.role?.toLowerCase(Locale.getDefault()) == ESP_LIB_Enums.applicant.toString()) {
            if (ESP_LIB_Shared.getInstance().isWifiConnected(context)) {
                loadDefinations()
            } else {
                ESP_LIB_Shared.getInstance().showAlertMessage(getString(R.string.esp_lib_text_internet_error_heading), getString(R.string.esp_lib_text_internet_connection_error), context)
            }
        } else
            rlbottomSheetHeader?.visibility = View.GONE*/

    }


}

