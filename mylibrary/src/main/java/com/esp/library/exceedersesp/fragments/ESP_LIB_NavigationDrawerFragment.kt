package com.esp.library.exceedersesp.fragments

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.esp.library.R
import com.esp.library.exceedersesp.ESP_LIB_BaseActivity
import com.esp.library.exceedersesp.ESP_LIB_ESPApplication
import com.esp.library.exceedersesp.ESP_LIB_Settings
import com.esp.library.exceedersesp.controllers.ESP_LIB_SplashScreenActivity
import com.esp.library.exceedersesp.controllers.Profile.ESP_LIB_ProfileMainActivity
import com.esp.library.exceedersesp.controllers.lookupinfo.adapter.ESP_LIB_ListLookupInfoDAOAdapter
import com.esp.library.exceedersesp.controllers.setup.ESP_LIB_LoginScreenActivity
import com.esp.library.exceedersesp.fragments.setup.ESP_LIB_SelectOrganizationFragment
import com.esp.library.utilities.common.*
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import me.leolin.shortcutbadger.ShortcutBadger
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import utilities.adapters.setup.ESP_LIB_ListofOrganizationSectionsAdapter
import utilities.data.apis.ESP_LIB_APIs
import utilities.data.applicants.ESP_LIB_FirebaseTokenDAO
import utilities.data.applicants.profile.ESP_LIB_ApplicationProfileDAO
import utilities.data.lookup.ESP_LIB_LookupInfoListDAO
import utilities.data.setup.*
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * A simple [Fragment] subclass.
 */
class ESP_LIB_NavigationDrawerFragment : androidx.fragment.app.Fragment() {

    internal var TAG = javaClass.simpleName
    private var mDrawerToggle: ActionBarDrawerToggle? = null
    private var mDrawerLayout: androidx.drawerlayout.widget.DrawerLayout? = null
    private var mFragmentContainerView: View? = null
    private var mCurrentSelectedPosition = 0
    internal var fm: androidx.fragment.app.FragmentManager? = null
    internal var context: ESP_LIB_BaseActivity? = null
    internal var login_call: Call<ESP_LIB_TokenDAO>? = null
    internal var bManager: androidx.localbroadcastmanager.content.LocalBroadcastManager? = null
    internal var submit_request: ESP_LIB_SelectOrganizationFragment? = null
    internal var pref: ESP_LIB_SharedPreference? = null
    internal var arrayLanguages = ArrayList<String>()
    internal var isClick = false


    internal var profile_photo: ImageView? = null
    internal var name: TextView? = null
    internal var txtVersionName: TextView? = null
    internal var email: TextView? = null
    internal var org_name: TextView? = null
    internal var backBtn: ImageView? = null
    internal var app_nav_div: LinearLayout? = null
    internal var applications: TextView? = null
    internal var profile_nav_div: LinearLayout? = null
    internal var setting_nav_div: LinearLayout? = null
    internal var subbmisiion_nav_div: LinearLayout? = null
    internal var switchUser: LinearLayout? = null
    internal var role: TextView? = null
    internal var switchUserImg: ImageView? = null
    internal var splanguage: Spinner? = null
    internal var progressbar: ProgressBar? = null
    internal var menu_items: LinearLayout? = null
    internal var mToolbar: Toolbar? = null
    internal var org_list: androidx.recyclerview.widget.RecyclerView? = null
    internal var lookupList: androidx.recyclerview.widget.RecyclerView? = null
    internal var btsignout: Button? = null
    private var mOrganizeAdapter: androidx.recyclerview.widget.RecyclerView.Adapter<*>? = null
    private var orgLayoutManager: androidx.recyclerview.widget.RecyclerView.LayoutManager? = null
    private var loohupLayoutManager: androidx.recyclerview.widget.RecyclerView.LayoutManager? = null
    internal var personaDAOListESPLIB: MutableList<ESP_LIB_OrganizationPersonaDao.Personas> = ArrayList()
    internal var ESPLIBLookupInfoListDAOArrayList: MutableList<ESP_LIB_LookupInfoListDAO> = ArrayList()

    private val bReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {}
    }


    val isDrawerOpen: Boolean
        get() = mDrawerLayout != null && mDrawerLayout!!.isDrawerOpen(mFragmentContainerView!!)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        bManager?.unregisterReceiver(bReceiver)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // the fragment has menu items to contribute
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.esp_lib_fragment_navigation_drawer, container, false)
        //   holder = new ViewHolder(v);
        initailize(v)

        UpdateView()
        //  populateSpinner();
        fm = context?.supportFragmentManager
        /* submit_request = ESPApplication.getInstance()?.user?.loginResponse?.let { SelectOrganizationFragment.newInstance(it) }
         val ft = fm?.beginTransaction()
         ft?.add(R.id.request_fragment_org, submit_request!!)
         ft?.commit()*/


        bManager = androidx.localbroadcastmanager.content.LocalBroadcastManager.getInstance(requireContext())
        val intentFilter = IntentFilter()
        // intentFilter.addAction(Constants.RECEIVE_MENU_BADGES);
        bManager?.registerReceiver(bReceiver, intentFilter)

        if (ESP_LIB_ESPApplication.getInstance().user != null) {
            name?.text = ESP_LIB_ESPApplication.getInstance()?.user?.loginResponse?.name
            email?.text = ESP_LIB_ESPApplication.getInstance()?.user?.loginResponse?.email
            email?.visibility = View.GONE
        }

        if (ESP_LIB_ESPApplication.getInstance().user != null && ESP_LIB_ESPApplication.getInstance()?.user?.loginResponse?.imageUrl != null && ESP_LIB_ESPApplication.getInstance()?.user?.loginResponse?.imageUrl!!.length > 0) {
            Picasso.with(context)
                    .load(ESP_LIB_ESPApplication.getInstance()?.user?.loginResponse?.imageUrl)
                    .placeholder(R.drawable.esp_lib_drawable_ic_contact_default)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .transform(ESP_LIB_RoundedPicasso())
                    .resize(ESP_LIB_Constants.PROFILE_PHOTO_SIZE, ESP_LIB_Constants.PROFILE_PHOTO_SIZE)
                    .into(profile_photo)


        }

        getLookupInfoList()


        backBtn?.setOnClickListener { view ->
            DrawerClose()
        }
        app_nav_div?.setOnClickListener { view -> ESP_LIB_Shared.getInstance().DrawermenuAction(0, "", context) }

        btsignout?.setOnClickListener {

            if (ESP_LIB_Shared.getInstance().isWifiConnected(context)) {
                deleteFirebaseToken()
            } else {
                ESP_LIB_Shared.getInstance().showAlertMessage(getString(R.string.esp_lib_text_internet_error_heading), getString(R.string.esp_lib_text_internet_connection_error), context)

            }


        }

        profile_nav_div?.setOnClickListener { view -> getApplicant() }


        setting_nav_div?.setOnClickListener { startActivity(Intent(context, ESP_LIB_Settings::class.java)) }

        /*subbmisiion_nav_div?.setOnClickListener { view -> }*/

        splanguage?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
                //changeLanguage(arrayLanguages.get(i));

            }

            override fun onNothingSelected(adapterView: AdapterView<*>) {

            }
        }


        return v
        //selectItem(position);

    }

    private fun initailize(v: View) {
        context = activity as ESP_LIB_BaseActivity?
        pref = ESP_LIB_SharedPreference(context)

        mToolbar = v.findViewById(R.id.toolbar)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mToolbar?.navigationIcon?.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP)
        }


        txtVersionName = v.findViewById(R.id.txtVersionName)
        btsignout = v.findViewById(R.id.btsignout)
        lookupList = v.findViewById(R.id.rclist)
        org_list = v.findViewById(R.id.org_list)
        menu_items = v.findViewById(R.id.menu_items)
        progressbar = v.findViewById(R.id.progressbar)
        switchUserImg = v.findViewById(R.id.switchUserImg)
        splanguage = v.findViewById(R.id.splanguage)
        switchUser = v.findViewById(R.id.switchUser)
        role = v.findViewById(R.id.role)
        profile_nav_div = v.findViewById(R.id.profile_nav_div)
        setting_nav_div = v.findViewById(R.id.setting_nav_div)
        subbmisiion_nav_div = v.findViewById(R.id.subbmisiion_nav_div)
        applications = v.findViewById(R.id.applications)
        app_nav_div = v.findViewById(R.id.app_nav_div)
        org_name = v.findViewById(R.id.org_name)
        backBtn = v.findViewById(R.id.back_btn)
        name = v.findViewById(R.id.user_name)
        email = v.findViewById(R.id.user_email)
        profile_photo = v.findViewById(R.id.profile_photo)


        mToolbar?.visibility = View.GONE
        switchUser?.tag = getString(R.string.esp_lib_text_hidden)

        orgLayoutManager = androidx.recyclerview.widget.LinearLayoutManager(activity)
        loohupLayoutManager = androidx.recyclerview.widget.LinearLayoutManager(activity)
        org_list?.setHasFixedSize(true)
        org_list?.layoutManager = orgLayoutManager
        org_list?.itemAnimator = androidx.recyclerview.widget.DefaultItemAnimator()

        lookupList?.setHasFixedSize(true)
        lookupList?.layoutManager = loohupLayoutManager
        lookupList?.itemAnimator = androidx.recyclerview.widget.DefaultItemAnimator()

        if (pref?.getlabels() != null)
            applications?.text = pref?.getlabels()?.applications

        if (!ESP_LIB_Shared.getInstance().getVersionName(context).toString().isNullOrEmpty())
            txtVersionName?.text = getString(R.string.esp_lib_text_version) + " " + ESP_LIB_Shared.getInstance().getVersionName(context)
    }

    fun getLookupInfoList() {

        try {

            start_loading_animation()
            val apis = ESP_LIB_Shared.getInstance().retroFitObject(context)
            val labels_call = apis.ESPLIBLookupInfoList

            labels_call.enqueue(object : Callback<List<ESP_LIB_LookupInfoListDAO>> {
                override fun onResponse(call: Call<List<ESP_LIB_LookupInfoListDAO>>, response: Response<List<ESP_LIB_LookupInfoListDAO>>) {
                    stop_loading_animation()
                    if (response.body() != null) {
                        val body = response.body()
                        ESPLIBLookupInfoListDAOArrayList.clear()
                        for (i in body.indices) {
                            val lookupInfoListDAO = body[i]
                            if (lookupInfoListDAO.isShowToApplicant && lookupInfoListDAO.isVariable &&
                                    lookupInfoListDAO.isVisible) {
                                ESPLIBLookupInfoListDAOArrayList.add(lookupInfoListDAO)
                            }
                        }
                        val adapter = ESP_LIB_ListLookupInfoDAOAdapter(ESPLIBLookupInfoListDAOArrayList, context!!)
                        lookupList?.adapter = adapter
                    }
                }

                override fun onFailure(call: Call<List<ESP_LIB_LookupInfoListDAO>>, t: Throwable) {
                    stop_loading_animation()
                    t.printStackTrace()
                    //  Shared.getInstance().messageBox(getString(R.string.some_thing_went_wrong), context)
                }
            })


        } catch (ex: Exception) {
            ex.printStackTrace()
            stop_loading_animation()
            //  Shared.getInstance().messageBox(getString(R.string.some_thing_went_wrong), context)
        }

    }


    fun getApplicant() {

        try {

            start_loading_animation()
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY
            val httpClient = OkHttpClient.Builder()
            httpClient.addInterceptor(logging)

            httpClient.addInterceptor { chain ->
                val original = chain.request()
                val requestBuilder = original.newBuilder()
                        .header("Authorization", "bearer " + ESP_LIB_ESPApplication.getInstance()?.user?.loginResponse?.access_token)
                        .header("locale", ESP_LIB_Shared.getInstance().getLanguage(context))

                val request = requestBuilder.build()
                chain.proceed(request)
            }

            httpClient.connectTimeout(2, TimeUnit.MINUTES)
            httpClient.readTimeout(2, TimeUnit.MINUTES)
            httpClient.writeTimeout(2, TimeUnit.MINUTES)

            val gson = GsonBuilder()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                    .create()

            val retrofit = Retrofit.Builder()
                    .baseUrl(ESP_LIB_Constants.base_url)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(httpClient.build())
                    .build()


            val apis = retrofit.create(ESP_LIB_APIs::class.java)


            val labels_call = apis.Getapplicant()

            labels_call.enqueue(object : Callback<ESP_LIB_ApplicationProfileDAO> {
                override fun onResponse(call: Call<ESP_LIB_ApplicationProfileDAO>, response: Response<ESP_LIB_ApplicationProfileDAO>) {
                    stop_loading_animation()
                    val body = response.body()
                    ESPLIBApplicationDAO = response.body()

                    val mainIntent = Intent(context, ESP_LIB_ProfileMainActivity::class.java)
                    mainIntent.putExtra("dataapplicant", body)
                    mainIntent.putExtra("ischeckerror", true)
                    mainIntent.putExtra("isprofile", true)
                    startActivity(mainIntent)

                }

                override fun onFailure(call: Call<ESP_LIB_ApplicationProfileDAO>, t: Throwable) {
                    stop_loading_animation()
                    t.printStackTrace()
                    ESP_LIB_Shared.getInstance().messageBox(getString(R.string.esp_lib_text_some_thing_went_wrong), context)
                }
            })


        } catch (ex: Exception) {
            ex.printStackTrace()
            stop_loading_animation()
            ESP_LIB_Shared.getInstance().messageBox(getString(R.string.esp_lib_text_some_thing_went_wrong), context)
        }

    }

    private fun UpdateView() {

        if (ESP_LIB_ESPApplication.getInstance()?.user == null) {
            val intentToBeNewRoot = Intent(context, ESP_LIB_LoginScreenActivity::class.java)
            val cn = intentToBeNewRoot.component
            val mainIntent = Intent.makeRestartActivityTask(cn)
            startActivity(mainIntent)
        } else {
            if (ESP_LIB_ESPApplication.getInstance()?.user?.loginResponse?.role?.toLowerCase(Locale.getDefault()).equals(ESP_LIB_Enums.applicant.toString(), ignoreCase = true)) {

                //   role?.text = ESPApplication.getInstance()?.user?.loginResponse?.role
            } else {
                // role?.text = context?.resources?.getString(R.string.assessor)
                profile_nav_div?.visibility = View.GONE

            }

            role?.text = pref?.selectedUserRole

            if (pref?.selectedUserRole.equals(context?.resources?.getString(R.string.esp_lib_text_assessor),ignoreCase = true)
                    || pref?.selectedUserRole.equals(context?.resources?.getString(R.string.esp_lib_text_admin),ignoreCase = true)) {
                setting_nav_div?.visibility = View.VISIBLE
            } else
                setting_nav_div?.visibility = View.GONE


            org_name?.text = ESP_LIB_Shared.getInstance()?.toSubStr(ESP_LIB_Shared.getInstance()?.GetOrgName(), 20)


            val list: List<ESP_LIB_PersonaDAO>?
            val personas = ESP_LIB_ESPApplication.getInstance()?.user?.loginResponse?.personas
            val gson = Gson()
            list = gson.fromJson<List<ESP_LIB_PersonaDAO>>(personas, object : TypeToken<List<ESP_LIB_PersonaDAO>>() {

            }.type)
            var app_list: List<ESP_LIB_PersonaDAO>? = null
            if (list != null && list.size > 0) {
                app_list = ESP_LIB_Shared.getInstance()?.GetApplicantOrganization(list, context)
                val organizationId = ESP_LIB_ESPApplication.getInstance()?.user?.loginResponse?.organizationId
                for (i in app_list!!.indices) {
                    val orgId = app_list[i].orgId
                    if (organizationId!!.equals(orgId, ignoreCase = true)) {
                        pref?.saveLocales(app_list[i].locales)
                    }
                }

            }


            if (app_list != null && app_list.size > 1) {
                switchUser?.isEnabled = true
                switchUserImg?.visibility = View.VISIBLE

                switchUser?.setOnClickListener { v -> showHideOrganiztions() }
                switchUserImg?.setOnClickListener { v -> showHideOrganiztions() }


            } else {
                switchUser?.isEnabled = false
                switchUserImg?.visibility = View.GONE
            }
        }
    }

    fun DrawerOpen() {

        mDrawerLayout?.openDrawer(GravityCompat.START)

    }

    private fun populateSpinner() {
        val localesArray = ArrayList(Arrays.asList(*pref!!.locales.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()))
        arrayLanguages.clear()
        if (localesArray.size > 1) {
            splanguage?.visibility = View.VISIBLE

            for (i in localesArray.indices) {
                if (localesArray[i].equals("en", ignoreCase = true))
                    arrayLanguages.add(getString(R.string.esp_lib_text_english))
                else
                    arrayLanguages.add(getString(R.string.esp_lib_text_arabic))
            }

            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, arrayLanguages)
            splanguage?.adapter = adapter

        } else {
            splanguage?.visibility = View.GONE
        }

        if (pref?.language.equals("en", ignoreCase = true))
            splanguage?.setSelection(0)
        else if (pref?.language.equals("ar", ignoreCase = true))
            splanguage?.setSelection(1)


    }

    private fun changeLanguage(languageType: String) {
        val language: String

        if (languageType.equals(getString(R.string.esp_lib_text_arabic), ignoreCase = true))
            language = "ar"
        else
            language = "en"


        pref?.savelanguage(language)
        ESP_LIB_CustomLogs.displayLogs(TAG + " language: " + language + " getLanguage: " + pref?.language)
        if (isDrawerOpen)
            isClick = true
        DrawerClose()

    }

    fun setApplicationlanguage() {
        val res = resources
        val dm = res.displayMetrics
        val conf = res.configuration
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            conf.setLocale(Locale(pref?.language)) // API 17+ only.
        } else {
            conf.locale = Locale(pref?.language)
        }
        res.updateConfiguration(conf, dm)
        isClick = false

        val intentToBeNewRoot = Intent(context, ESP_LIB_SplashScreenActivity::class.java)
        val cn = intentToBeNewRoot.component
        val mainIntent = Intent.makeRestartActivityTask(cn)
        startActivity(mainIntent)

    }

    fun DrawerClose() {
        if (mDrawerLayout?.isDrawerOpen(GravityCompat.START)!!) {
            mDrawerLayout?.closeDrawer(GravityCompat.START)
        }
    }

    fun setUp(fragmentId: Int, drawerLayout: androidx.drawerlayout.widget.DrawerLayout) {
        mFragmentContainerView = activity?.findViewById(fragmentId)
        mDrawerLayout = drawerLayout

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout?.setDrawerShadow(R.drawable.esp_lib_drawable_draw_shadow, GravityCompat.START)

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the navigation drawer and the action bar app icon.
        mDrawerToggle = object : ActionBarDrawerToggle(activity, mDrawerLayout, mToolbar, R.string.esp_lib_text_navigation_drawer_open,
                R.string.esp_lib_text_navigation_drawer_close) {
            override fun onDrawerOpened(drawerView: View) {
                if (drawerView != null) {
                    super.onDrawerOpened(drawerView)
                }

            }

            override fun onDrawerClosed(drawerView: View) {
                if (drawerView != null) {
                    super.onDrawerClosed(drawerView)
                }
                if (isClick)
                    setApplicationlanguage()
            }
        }

        mDrawerLayout?.post { mDrawerToggle?.syncState() }
        mDrawerLayout?.addDrawerListener(mDrawerToggle!!)
        mDrawerToggle?.isDrawerIndicatorEnabled = true
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        mDrawerToggle?.onConfigurationChanged(newConfig)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (mDrawerToggle!!.onOptionsItemSelected(item)) {
            true
        } else super.onOptionsItemSelected(item)
    }


    private fun getOrganizations() {

        start_loading_animation()

        try {
            val apis = ESP_LIB_Shared.getInstance().retroFitObject(context)
            val organization_call = apis.organizations

            organization_call.enqueue(object : Callback<List<ESP_LIB_OrganizationPersonaDao>> {
                override fun onResponse(call: Call<List<ESP_LIB_OrganizationPersonaDao>>, response: Response<List<ESP_LIB_OrganizationPersonaDao>>) {
                    val body = response.body()

                    if (body != null) {


                        for (i in body.indices) {
                            val organizationPersonaDao = body[i]
                            val persoans = organizationPersonaDao.persoans
                            for (j in persoans.indices) {
                                val personas = persoans[j]
                                personaDAOListESPLIB.add(personas)
                            }
                        }

                        var tokenDAO = ESP_LIB_TokenDAO()
                        tokenDAO.access_token = ESP_LIB_ESPApplication.getInstance()?.user?.loginResponse?.access_token
                        tokenDAO.refresh_token = ESP_LIB_ESPApplication.getInstance()?.user?.loginResponse?.refresh_token

                        mOrganizeAdapter = ESP_LIB_ListofOrganizationSectionsAdapter(body, context!!, tokenDAO)
                        org_list?.adapter = mOrganizeAdapter
                        mOrganizeAdapter?.notifyDataSetChanged()

                        org_list?.visibility = View.VISIBLE
                        menu_items?.visibility = View.GONE
                        lookupList?.visibility = View.GONE

                    }
                    stop_loading_animation()
                }

                override fun onFailure(call: Call<List<ESP_LIB_OrganizationPersonaDao>>, t: Throwable) {
                    stop_loading_animation()
                    ESP_LIB_Shared.getInstance().showAlertMessage(getString(R.string.esp_lib_text_login_label), getString(R.string.esp_lib_text_some_thing_went_wrong), context)
                }
            })


        } catch (ex: Exception) {

            ex.printStackTrace()
            stop_loading_animation()
            ESP_LIB_Shared.getInstance().showAlertMessage(getString(R.string.esp_lib_text_login_label), getString(R.string.esp_lib_text_some_thing_went_wrong), context)


        }

    }

    private fun showHideOrganiztions() {

        val tag = switchUser?.tag as String

        if (tag != null) {

            if (tag.equals(getString(R.string.esp_lib_text_hidden), ignoreCase = true)) {

                switchUser?.tag = getString(R.string.esp_lib_text_shown)
                switchUserImg?.setImageDrawable(context?.resources?.getDrawable(R.drawable.ic_arrow_up))

                //  request_fragment_org.setVisibility(View.VISIBLE);


                val scopeId = ESP_LIB_Shared.getInstance().ReadPref("scropId", "login_info", context)
                val pt = ESP_LIB_PostTokenDAO()
                pt.client_id = "ESPMobile"
                pt.grant_type = "refresh_token"
                pt.password = ESP_LIB_ESPApplication.getInstance()?.user?.loginResponse?.password
                pt.username = ESP_LIB_ESPApplication.getInstance()?.user?.loginResponse?.email
                pt.scope = scopeId
                pt.refresh_token = ESP_LIB_ESPApplication.getInstance()?.user?.loginResponse?.refresh_token
                GetRefreshToken(pt, true)


            } else {

                switchUser?.tag = getString(R.string.esp_lib_text_hidden)
                switchUserImg?.setImageDrawable(context?.resources?.getDrawable(R.drawable.ic_arrow_down))

                // request_fragment_org.setVisibility(View.GONE);
                org_list?.visibility = View.GONE
                menu_items?.visibility = View.VISIBLE
                lookupList?.visibility = View.VISIBLE
            }
        }

    }

    //If multiple Organation for Applicant
    fun RefreshToken(ESPLIBPersonaDAO: ESP_LIB_PersonaDAO) {


        if (ESP_LIB_Shared.getInstance().isWifiConnected(context)) {

            val pt = ESP_LIB_PostTokenDAO()
            pt.client_id = "ESPMobile"
            pt.grant_type = "refresh_token"
            pt.password = ESP_LIB_ESPApplication.getInstance()?.user?.loginResponse?.password
            pt.username = ESP_LIB_ESPApplication.getInstance()?.user?.loginResponse?.email
            pt.scope = ESPLIBPersonaDAO.id
            pt.refresh_token = ESPLIBPersonaDAO.refresh_token

            ESP_LIB_Shared.getInstance().WritePref("scropId", ESPLIBPersonaDAO.id, "login_info", context)


            GetRefreshToken(pt, false)

        } else {
            ESP_LIB_Shared.getInstance().showAlertMessage(getString(R.string.esp_lib_text_internet_error_heading), getString(R.string.esp_lib_text_internet_connection_error), context)

        }
    }

    private fun GetRefreshToken(ESPLIBPostTokenDAO: ESP_LIB_PostTokenDAO, isOrganizationCall: Boolean) {

        start_loading_animation()

        try {
            val apis = ESP_LIB_Shared.getInstance().retroFitObject(context)

            login_call = apis.getRefreshToken(ESPLIBPostTokenDAO.scope, ESPLIBPostTokenDAO.grant_type, ESPLIBPostTokenDAO.username, ESPLIBPostTokenDAO.password, ESPLIBPostTokenDAO.client_id, ESPLIBPostTokenDAO.scope, ESPLIBPostTokenDAO.refresh_token)


            login_call?.enqueue(object : Callback<ESP_LIB_TokenDAO> {
                override fun onResponse(call: Call<ESP_LIB_TokenDAO>, response: Response<ESP_LIB_TokenDAO>?) {

                    stop_loading_animation()

                    if (response != null && response.body() != null) {

                        if (response.body().personas != null && response.body().personas.length > 0) {

                            if (ESP_LIB_ESPApplication.getInstance().user != null) {
                                ESP_LIB_ESPApplication.getInstance().user = null
                            }

                            val userDAO = ESP_LIB_UserDAO()
                            response.body().password = ESPLIBPostTokenDAO.password
                            userDAO.loginResponse = response.body()
                            ESP_LIB_ESPApplication.getInstance().user = userDAO
                            ESP_LIB_ESPApplication.getInstance().filter = ESP_LIB_Shared.getInstance().ResetApplicationFilter(context)


                            ESP_LIB_ESPApplication.getInstance().filter.isMySpace = !response.body().role?.toLowerCase(Locale.getDefault()).equals(ESP_LIB_Enums.applicant.toString(), ignoreCase = true)


                            ESP_LIB_Shared.getInstance().WritePref("Uname", ESPLIBPostTokenDAO.username, "login_info", context)
                            ESP_LIB_Shared.getInstance().WritePref("Pass", ESPLIBPostTokenDAO.password, "login_info", context)

                            if (isOrganizationCall)
                                getOrganizations()
                            else {
                                val intentToBeNewRoot = Intent(context, ESP_LIB_LoginScreenActivity::class.java)
                                val cn = intentToBeNewRoot.component
                                val mainIntent = Intent.makeRestartActivityTask(cn)
                                startActivity(mainIntent)
                            }


                        } else {

                            ESP_LIB_Shared.getInstance().messageBox(getString(R.string.esp_lib_text_expired), context)
                            ESP_LIB_Shared.getInstance().SignOutNotClear(context, true)
                        }


                    } else {

                        ESP_LIB_Shared.getInstance().messageBox(getString(R.string.esp_lib_text_expired), context)
                        ESP_LIB_Shared.getInstance().SignOutNotClear(context, true)

                    }
                }

                override fun onFailure(call: Call<ESP_LIB_TokenDAO>, t: Throwable) {

                    stop_loading_animation()
                    ESP_LIB_Shared.getInstance().messageBox(getString(R.string.esp_lib_text_expired), context)
                    ESP_LIB_Shared.getInstance().SignOutNotClear(context, true)

                }
            })

        } catch (ex: Exception) {
            stop_loading_animation()
            ESP_LIB_Shared.getInstance().messageBox(getString(R.string.esp_lib_text_expired), context)
            ESP_LIB_Shared.getInstance().SignOutNotClear(context, true)

        }

    }

    private fun deleteFirebaseToken() {

        start_loading_animation()


        try {

            val apis = ESP_LIB_Shared.getInstance().retroFitObject(context)
            val firebase_call = apis.deleteFirebaseToken(ESP_LIB_Shared.getInstance().getDeviceId(context))

            firebase_call.enqueue(object : Callback<ESP_LIB_FirebaseTokenDAO> {
                override fun onResponse(call: Call<ESP_LIB_FirebaseTokenDAO>, response: Response<ESP_LIB_FirebaseTokenDAO>?) {
                    stop_loading_animation()
                    val notificationManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    notificationManager.cancel(1000);
                    ShortcutBadger.applyCount(context, 0); //for 1.1.4+
                    ESP_LIB_Shared.getInstance().setBadge(context, 0)
                    ESP_LIB_Shared.getInstance().DrawermenuAction(2, "", context)
                    if (response?.body() != null) {
                        /*if (response.body().status) {
                            Shared.getInstance().DrawermenuAction(2, "", context)
                        } else {
                            Shared.getInstance().showAlertMessage(context?.getString(R.string.error), response.body().errorMessage, context)
                        }*/

                    } else
                        ESP_LIB_Shared.getInstance().showAlertMessage(context?.getString(R.string.esp_lib_text_error), context?.getString(R.string.esp_lib_text_some_thing_went_wrong), context)

                }

                override fun onFailure(call: Call<ESP_LIB_FirebaseTokenDAO>, t: Throwable) {
                    ESP_LIB_CustomLogs.displayLogs("$TAG ${t.printStackTrace()}")
                    stop_loading_animation()
                    ESP_LIB_Shared.getInstance().showAlertMessage(context?.getString(R.string.esp_lib_text_error), context?.getString(R.string.esp_lib_text_some_thing_went_wrong), context)

                }
            })

        } catch (ex: Exception) {

            ex.printStackTrace()
            stop_loading_animation()
            ESP_LIB_Shared.getInstance().showAlertMessage(context?.getString(R.string.esp_lib_text_error), context?.getString(R.string.esp_lib_text_some_thing_went_wrong), context)

        }

    }

    private fun start_loading_animation() {
        progressbar?.visibility = View.VISIBLE
    }

    private fun stop_loading_animation() {
        progressbar?.visibility = View.GONE
    }

    companion object {

        private val STATE_SELECTED_POSITION = "selected_navigation_drawer_position"

        var ESPLIBApplicationDAO: ESP_LIB_ApplicationProfileDAO? = null
    }

    override fun onResume() {
        super.onResume()
        DrawerClose()
    }

}
