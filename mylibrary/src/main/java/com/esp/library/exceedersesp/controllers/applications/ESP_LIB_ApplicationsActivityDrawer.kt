package com.esp.library.exceedersesp.controllers.applications

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.SearchAutoComplete
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import com.esp.library.R
import com.esp.library.exceedersesp.ESP_LIB_BaseActivity
import com.esp.library.exceedersesp.ESP_LIB_ESPApplication
import com.esp.library.exceedersesp.controllers.ESP_LIB_WebViewScreenActivity
import com.esp.library.exceedersesp.controllers.applications.filters.ESP_LIB_FilterScreenActivity
import com.esp.library.exceedersesp.fragments.ESP_LIB_NavigationDrawerFragment
import com.esp.library.exceedersesp.fragments.applications.ESP_LIB_UsersApplicationsFragment
import com.esp.library.exceedersesp.fragments.applications.ESP_LIB_UsersSearchApplicationsFragment
import com.esp.library.utilities.common.*
import com.esp.library.utilities.customevents.EventOptions
import kotlinx.android.synthetic.main.esp_lib_activity_applications_drawer.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import utilities.adapters.setup.ESP_LIB_ListPersonaDAOAdapter
import utilities.data.setup.ESP_LIB_PersonaDAO
import java.util.*


class ESP_LIB_ApplicationsActivityDrawer : ESP_LIB_BaseActivity(), ESP_LIB_UsersApplicationsFragment.HideShowPlus, ESP_LIB_AlertActionWindow.ActionInterface,
        ESP_LIB_ListPersonaDAOAdapter.RefreshToken {


    internal var context: ESP_LIB_BaseActivity? = null

    // internal var wasSheetShowing: Boolean = false
    internal var fm: androidx.fragment.app.FragmentManager? = null
    internal var submit_request: ESP_LIB_UsersApplicationsFragment? = null
    internal var submit_request_searchESPLIB: ESP_LIB_UsersSearchApplicationsFragment? = null

    internal var pref: ESP_LIB_SharedPreference? = null
    internal var pDialog: AlertDialog? = null
    private var mESPLIBNavigationDrawerFragment: ESP_LIB_NavigationDrawerFragment? = null
    /* private var mDefAdapter: ListApplicationSubDefinationAdapter? = null*/
    /*internal var definition_list: ArrayList<CategoryAndDefinationsDAO> = ArrayList()
    internal var filter_definition_list: ArrayList<CategoryAndDefinationsDAO> = ArrayList()*/
    // var sheetBehavior: BottomSheetBehavior<*>? = null
    /*internal var filter_adapter: FilterItemsAdapter? = null*/

    var TAG: String = "ApplicationsActivityDrawer"

    @SuppressLint("RestrictedApi")
    override fun mAction(IsShown: Boolean) {
        if (IsShown) {
            add_account.visibility = View.GONE
        } else {
            // addAccount.setVisibility(View.VISIBLE);
            if (ESP_LIB_ESPApplication.getInstance()?.user?.loginResponse?.role?.toLowerCase(Locale.getDefault()) == ESP_LIB_Enums.applicant.toString()) {
                //if (sheetBehavior?.state != BottomSheetBehavior.STATE_EXPANDED && request_fragment_search.visibility == View.GONE) {
                add_account.visibility = View.VISIBLE
                //    }

            } else {
                add_account.visibility = View.GONE
            }

        }
    }

    override fun mActionTo(whattodo: String) {
        if (whattodo == "profile") {
            val bn = Bundle()
            bn.putString("heading", getString(R.string.esp_lib_text_profile))
            bn.putString("url", ESP_LIB_Constants.base_url.replace("webapi/", "") + "profile")
            ESP_LIB_Shared.getInstance().callIntentWithResult(ESP_LIB_WebViewScreenActivity::class.java, context, bn, 5)

        }
    }

    override fun StatusChange(update: ESP_LIB_PersonaDAO) {
        if (mESPLIBNavigationDrawerFragment != null) {
            mESPLIBNavigationDrawerFragment?.RefreshToken(update)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        changeStatusBarColor(true)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.esp_lib_activity_applications_drawer)

        initailize()
        setGravity()

        add_account.setOnClickListener { v ->
            val manager = supportFragmentManager
            if (ESP_LIB_ESPApplication.getInstance()?.user?.profileStatus == null || ESP_LIB_ESPApplication.getInstance()?.user?.profileStatus == getString(R.string.esp_lib_text_profile_complete)) {
                ESP_LIB_Shared.getInstance().callIntentWithResult(ESP_LIB_AddApplicationsActivity::class.java, context, null, 2)
            } else if (ESP_LIB_ESPApplication.getInstance().user.profileStatus == getString(R.string.esp_lib_text_profile_incomplete)) {
                ESP_LIB_Shared.getInstance().showAlertProfileMessage(getString(R.string.esp_lib_text_profile_error_heading), getString(R.string.esp_lib_text_profile_error_desc), context)
            } else if (ESP_LIB_ESPApplication.getInstance().user.profileStatus == getString(R.string.esp_lib_text_profile_incomplete_admin)) {
                ESP_LIB_Shared.getInstance().showAlertProfileMessage(getString(R.string.esp_lib_text_profile_error_heading), getString(R.string.esp_lib_text_profile_error_desc_admin), context)
            }
        }

        toolbar_heading.setText(pref?.getlabels()?.applications)


        fm = this.context?.getSupportFragmentManager()

        request_fragment_search.visibility = View.GONE


        val submit_request_tabs = ESP_LIB_ApplicationActivityTabs.newInstance()
        val ft = fm!!.beginTransaction()
        ft.add(R.id.request_fragment, submit_request_tabs)
        ft.commit()

        submit_request_searchESPLIB = ESP_LIB_UsersSearchApplicationsFragment.newInstance()
        val ft_search = fm!!.beginTransaction()
        ft_search.add(R.id.request_fragment_search, submit_request_searchESPLIB!!)
        ft_search.commit()

        mESPLIBNavigationDrawerFragment = supportFragmentManager.findFragmentById(R.id.navigation_drawer) as ESP_LIB_NavigationDrawerFragment?
        mESPLIBNavigationDrawerFragment?.setUp(R.id.navigation_drawer, findViewById<View>(R.id.drawer_layout) as androidx.drawerlayout.widget.DrawerLayout)


    }

    private fun initailize() {
        context = this@ESP_LIB_ApplicationsActivityDrawer
        pref = ESP_LIB_SharedPreference(context)
        pDialog = ESP_LIB_Shared.getInstance().setProgressDialog(bContext)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowHomeEnabled(false)
        supportActionBar!!.title = ""
        toolbar.navigationIcon = ContextCompat.getDrawable(this, R.drawable.ic_nav_menu)
        toolbar.navigationIcon?.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP)
        toolbar.setNavigationOnClickListener { mESPLIBNavigationDrawerFragment?.DrawerOpen() }


    }


    private fun setLayoutMargin(floatingButtonMargin: Int) {
        val lp = add_account.layoutParams as CoordinatorLayout.LayoutParams
        lp.setMargins(0, 0, 20, floatingButtonMargin)
        add_account.layoutParams = lp
    }


    override fun onResume() {
        super.onResume()
        registerEventBus()

        if (ESP_LIB_Constants.isApplicationChagned) {
            if (submit_request != null) {
                submit_request?.reLoadApplications()
                ESP_LIB_Constants.isApplicationChagned = false
            }
        }



    }


    override fun onPause() {
        super.onPause()
        unRegisterEventBus()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun bottomSheetShowingEvent(eventBottomSheetShowing: EventOptions.EventBottomSheetShowing) {
        if (ESP_LIB_ESPApplication.getInstance().isComponent || ESP_LIB_ESPApplication.getInstance()?.user?.loginResponse?.role?.toLowerCase(Locale.getDefault()) == ESP_LIB_Enums.applicant.toString()) {
              setLayoutMargin(resources.getDimensionPixelSize(R.dimen._55sdp))
          } /*else {
              setLayoutMargin(resources.getDimensionPixelSize(R.dimen._15sdp))
          }*/

    }

    private fun registerEventBus() {
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this)
    }

    private fun unRegisterEventBus() {
        EventBus.getDefault().unregister(this)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        //Filter applied
        if (requestCode == 2) {

            if (data != null) {
                val bnd = data.extras

                if (bnd != null) {
                    if (bnd.getBoolean("whatodo")) {

                        if (submit_request != null) {
                            submit_request?.reLoadApplications()
                        }

                    }
                }
            }
        } else if (requestCode == 5) {

            if (submit_request != null) {
                submit_request?.GetProfileStatus()
            }
        }


        super.onActivityResult(requestCode, resultCode, data)

    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_my_applications, menu)
        val myActionMenuItem = menu.findItem(R.id.action_search)
        myActionMenuItem?.isVisible = false


        val searchView = myActionMenuItem?.actionView as SearchView
        val editText = searchView.findViewById<View>(androidx.appcompat.R.id.search_src_text) as SearchAutoComplete
        editText.hint = resources.getString(R.string.esp_lib_text_search_) + " " + pref?.getlabels()?.applications
        editText.setHintTextColor(ContextCompat.getColor(context!!, R.color.esp_lib_color_white))
        editText.setTextColor(ContextCompat.getColor(context!!, R.color.esp_lib_color_white))
        val searchClose = searchView.findViewById<ImageView>(androidx.appcompat.R.id.search_close_btn)
        searchClose?.setImageResource(R.drawable.esp_lib_drawable_ic_icons_close_white)
        searchClose?.setOnClickListener {
            myActionMenuItem.collapseActionView()
            // submit_request.ResetLoadApplications();
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String): Boolean {
                if (!searchView.isIconified) {
                    searchView.isIconified = false
                }

                /* if (myActionMenuItem != null) {
                     myActionMenuItem!!.collapseActionView()
                 }*/
                return false
            }

            override fun onQueryTextChange(query: String?): Boolean {
                if (query != null) {

                    try {

                        if (submit_request_searchESPLIB != null) {
                            submit_request_searchESPLIB?.UpdateViewAutoSearched(query)
                        }

                    } catch (e: Exception) {
                    }
                }
                return false
            }
        })

        myActionMenuItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {

            @SuppressLint("RestrictedApi")
            override fun onMenuItemActionExpand(item: MenuItem): Boolean {

                submit_request_searchESPLIB?.app_search_list?.stopScroll()

                Handler().postDelayed({
                    submit_request_searchESPLIB?.app_actual_list?.clear()
                    submit_request_searchESPLIB?.search_result?.text = ""
                    submit_request_searchESPLIB?.txtrequestcount?.text = ""
                    submit_request_searchESPLIB?.mApplicationAdapter = null
                    request_fragment_search.visibility = View.VISIBLE
                }, 300)


                return true
            }

            @SuppressLint("RestrictedApi")
            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                request_fragment_search.visibility = View.GONE
                if (ESP_LIB_ESPApplication.getInstance()?.user?.loginResponse?.role?.toLowerCase(Locale.getDefault()) == ESP_LIB_Enums.applicant.toString()) {
                    add_account.visibility = View.VISIBLE
                }
                return true
            }
        })

        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_filter -> {
                ESP_LIB_Shared.getInstance().callIntentWithResult(ESP_LIB_FilterScreenActivity::class.java, context, null, 2)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setGravity() {
        if (pref?.language.equals("ar", ignoreCase = true)) {
            toolbar_heading.gravity = Gravity.RIGHT

        } else {
            toolbar_heading.gravity = Gravity.LEFT
        }
    }

    companion object {

        var ACTIVITY_NAME = "controllers.applications.ApplicationsActivityDrawer"
        // var subDefinationsDAOFilteredList: ArrayList<CategoryAndDefinationsDAO> = ArrayList()
    }

    /* override fun deleteFilters(filtersList: CategoryAndDefinationsDAO) {
         if (Shared.getInstance().isWifiConnected(bContext)) {
             if (filter_adapter != null) {
                 subDefinationsDAOFilteredList.remove(filtersList)
                 filter_adapter?.notifyDataSetChanged()
                 populateFilters()
             }
         } else {
             Shared.getInstance().showAlertMessage(getString(R.string.internet_error_heading), getString(R.string.internet_connection_error), bContext)
         }
     }*/

    /*  override fun refreshSubDefinition() {

          if (filter_horizontal_list.visibility == View.GONE && request_fragment_search.visibility == View.GONE) {
              loadDefinations()
          }



      }*/

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
