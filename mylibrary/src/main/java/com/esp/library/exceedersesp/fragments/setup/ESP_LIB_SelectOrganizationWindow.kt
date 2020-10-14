package com.esp.library.exceedersesp.fragments.setup

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import com.esp.library.R
import com.esp.library.utilities.common.ESP_LIB_Constants
import com.esp.library.utilities.common.ESP_LIB_Shared
import com.esp.library.utilities.common.ESP_LIB_SharedPreference
import com.esp.library.exceedersesp.ESP_LIB_BaseActivity
import com.esp.library.exceedersesp.ESP_LIB_ESPApplication
import com.esp.library.exceedersesp.SingleController.CompRoot
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.esp_lib_alert_select_organization_window.view.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import utilities.adapters.setup.ESP_LIB_ListofOrganizationSectionsAdapter
import utilities.data.apis.ESP_LIB_APIs
import utilities.data.setup.ESP_LIB_OrganizationPersonaDao
import utilities.data.setup.ESP_LIB_TokenDAO
import java.util.*

//
class ESP_LIB_SelectOrganizationWindow : androidx.fragment.app.DialogFragment() {

    internal var TAG = javaClass.simpleName
    internal var personas: ESP_LIB_TokenDAO? = null
    private var mOrganizeAdapter: androidx.recyclerview.widget.RecyclerView.Adapter<*>? = null
    private var mOrgLayoutManager: androidx.recyclerview.widget.RecyclerView.LayoutManager? = null
    internal var context: ESP_LIB_BaseActivity? = null
    internal var personaDAOListESPLIB: MutableList<ESP_LIB_OrganizationPersonaDao.Personas> = ArrayList()
    internal var pDialog: AlertDialog? = null
    internal var pref: ESP_LIB_SharedPreference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = activity as ESP_LIB_BaseActivity?
        if (arguments != null) {
            personas = requireArguments().getSerializable(ESP_LIB_TokenDAO.BUNDLE_KEY) as ESP_LIB_TokenDAO
        }


    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.esp_lib_alert_select_organization_window, container, false)
        initailize(v)
        if (personas != null)
            v.txtemail.text = personas!!.email
        ESP_LIB_ESPApplication.getInstance().access_token=personas?.access_token
        if (ESP_LIB_Shared.getInstance().isWifiConnected(context)) {
            getOrganizations(v)
        } else {
            ESP_LIB_Shared.getInstance().showAlertMessage(context?.getString(R.string.esp_lib_text_internet_error_heading), context?.getString(R.string.esp_lib_text_internet_connection_error), context)
        }
        return v
    }

    private fun initailize(v: View) {
        mOrgLayoutManager = androidx.recyclerview.widget.LinearLayoutManager(activity)
        v.org_list.setHasFixedSize(true)
        v.org_list.layoutManager = mOrgLayoutManager
        v.org_list.itemAnimator = androidx.recyclerview.widget.DefaultItemAnimator()
        dialog?.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        pref = ESP_LIB_SharedPreference(context)
        pDialog = ESP_LIB_Shared.getInstance().setProgressDialog(context)
        v.close.setOnClickListener { view -> dismiss() }
    }


    private fun getOrganizations(v: View) {
        start_loading_animation(v)

        try {

            val apis = CompRoot().getService(context)
            val organization_call = apis?.organizations

            organization_call?.enqueue(object : Callback<List<ESP_LIB_OrganizationPersonaDao>> {
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

                        mOrganizeAdapter = context?.let { personas?.let { it1 -> ESP_LIB_ListofOrganizationSectionsAdapter(body, it, it1) } }
                        v.org_list.adapter = mOrganizeAdapter
                        mOrganizeAdapter!!.notifyDataSetChanged()
                    }
                    stop_loading_animation(v)
                }

                override fun onFailure(call: Call<List<ESP_LIB_OrganizationPersonaDao>>, t: Throwable) {
                    stop_loading_animation(v)
                    ESP_LIB_Shared.getInstance().showAlertMessage(getString(R.string.esp_lib_text_error), getString(R.string.esp_lib_text_some_thing_went_wrong), context)
                }
            })


        } catch (ex: Exception) {

            ex.printStackTrace()
            stop_loading_animation(v)
            ESP_LIB_Shared.getInstance().showAlertMessage(getString(R.string.esp_lib_text_error), getString(R.string.esp_lib_text_some_thing_went_wrong), context)


        }

    }

    private fun start_loading_animation(v: View) {
        v.rlmainlayout.visibility = View.GONE
        if (!pDialog!!.isShowing)
            pDialog!!.show()
    }

    private fun stop_loading_animation(v: View) {
        v.rlmainlayout.visibility = View.VISIBLE
        if (pDialog!!.isShowing)
            pDialog!!.dismiss()
    }

    companion object {

        fun newInstance(persona: ESP_LIB_TokenDAO): ESP_LIB_SelectOrganizationWindow {
            val fragment = ESP_LIB_SelectOrganizationWindow()
            val args = Bundle()
            args.putSerializable(ESP_LIB_TokenDAO.BUNDLE_KEY, persona)
            fragment.arguments = args
            return fragment
        }
    }

}
