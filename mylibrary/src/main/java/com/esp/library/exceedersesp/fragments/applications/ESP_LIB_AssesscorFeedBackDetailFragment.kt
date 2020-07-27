package com.esp.library.exceedersesp.fragments.applications

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.esp.library.R
import com.esp.library.exceedersesp.ESP_LIB_ESPApplication
import com.esp.library.utilities.common.*
import com.esp.library.exceedersesp.ESP_LIB_BaseActivity
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.esp_lib_fragment_assessor_feedback_detail.view.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import utilities.adapters.setup.applications.ESP_LIB_ListApplicationFeedBackAdapter
import utilities.data.apis.ESP_LIB_APIs
import utilities.data.applicants.ESP_LIB_ApplicationSingleton
import utilities.data.applicants.feedback.ESP_LIB_ApplicationsFeedbackDAO
import java.util.concurrent.TimeUnit

class ESP_LIB_AssesscorFeedBackDetailFragment : Fragment() {

    internal var context: ESP_LIB_BaseActivity? = null
    internal var detail_call: Call<List<ESP_LIB_ApplicationsFeedbackDAO>>? = null
    internal var anim: ESP_LIB_ProgressBarAnimation? = null
    private var mApplicationAdapter: RecyclerView.Adapter<*>? = null
    internal var pref: ESP_LIB_SharedPreference? = null
    internal var pDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = activity as ESP_LIB_BaseActivity?
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.esp_lib_fragment_assessor_feedback_detail, container, false)

        initailize(v)

        if (ESP_LIB_Shared.getInstance().isWifiConnected(context)) {
            if (ESP_LIB_ApplicationSingleton.instace.application != null) {
                GetApplicationFeedBack(ESP_LIB_ApplicationSingleton.instace.application!!.applicationId.toString() + "",v)
            }

        } else {
            val alertMesasgeWindow = ESP_LIB_AlertMesasgeWindow.newInstance(getString(R.string.esp_lib_text_internet_error_heading), getString(R.string.esp_lib_text_internet_connection_error), getString(R.string.esp_lib_text_alert), getString(R.string.esp_lib_text_ok))
            alertMesasgeWindow.show(requireActivity().supportFragmentManager, getString(R.string.esp_lib_text_alert))
            alertMesasgeWindow.isCancelable = false

        }


        return v
    }

    private fun initailize(v: View) {
        pDialog = ESP_LIB_Shared.getInstance().setProgressDialog(context)
        val mApplicationLayoutManager = LinearLayoutManager(context)
        v.app_list.setHasFixedSize(true)
        v.app_list.layoutManager = mApplicationLayoutManager
        v.app_list.itemAnimator = DefaultItemAnimator()
        pref = ESP_LIB_SharedPreference(context)
    }

    override fun onDestroyView() {

        if (detail_call != null) {
            detail_call!!.cancel()
        }
        super.onDestroyView()
    }

    fun GetApplicationFeedBack(id: String, v: View) {

        start_loading_animation(v)
        try {

            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY
            val httpClient = OkHttpClient.Builder()
            httpClient.addInterceptor(logging)

            httpClient.addInterceptor { chain ->
                val original = chain.request()
                val requestBuilder = original.newBuilder()
                        .header("locale", ESP_LIB_Shared.getInstance().getLanguage(context))
                        .header("Authorization", "bearer " + ESP_LIB_ESPApplication.getInstance().user.loginResponse?.access_token)
                val request = requestBuilder.build()
                chain.proceed(request)
            }

            httpClient.connectTimeout(10, TimeUnit.SECONDS)
            httpClient.readTimeout(10, TimeUnit.SECONDS)
            httpClient.writeTimeout(10, TimeUnit.SECONDS)

            val gson = GsonBuilder()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                    .create()

            val retrofit = Retrofit.Builder()
                    .baseUrl(ESP_LIB_Constants.base_url)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(httpClient.build())

                    .build()


            val apis = retrofit.create(ESP_LIB_APIs::class.java)


            detail_call = apis.GetApplicationFeedBack(id)
            detail_call!!.enqueue(object : Callback<List<ESP_LIB_ApplicationsFeedbackDAO>> {
                override fun onResponse(call: Call<List<ESP_LIB_ApplicationsFeedbackDAO>>, response: Response<List<ESP_LIB_ApplicationsFeedbackDAO>>?) {

                    stop_loading_animation(v)

                    if (response != null && response.body() != null && response.body().size > 0) {

                        mApplicationAdapter = context?.let { ESP_LIB_ListApplicationFeedBackAdapter(response.body(), it, "") }
                        v.app_list.adapter = mApplicationAdapter
                        mApplicationAdapter!!.notifyDataSetChanged()

                        v.no_record.visibility = View.GONE
                        v.detail_view.visibility = View.VISIBLE
                    } else {
                        v.no_record.visibility = View.VISIBLE
                        v.detail_view.visibility = View.GONE
                    }
                }

                override fun onFailure(call: Call<List<ESP_LIB_ApplicationsFeedbackDAO>>, t: Throwable) {
                    stop_loading_animation(v)


                    if (isAdded) {
                        val alertMesasgeWindow = ESP_LIB_AlertMesasgeWindow.newInstance(pref?.getlabels()?.application, t.message, getString(R.string.esp_lib_text_alert), getString(R.string.esp_lib_text_ok))
                        alertMesasgeWindow.show(fragmentManager!!, getString(R.string.esp_lib_text_alert))
                        alertMesasgeWindow.isCancelable = false
                    }

                }
            })

        } catch (ex: Exception) {

            stop_loading_animation(v)

            if (isAdded) {
                val alertMesasgeWindow = ESP_LIB_AlertMesasgeWindow.newInstance(pref?.getlabels()?.application, ex.message, getString(R.string.esp_lib_text_alert), getString(R.string.esp_lib_text_ok))
                alertMesasgeWindow.show(requireActivity().supportFragmentManager, getString(R.string.esp_lib_text_alert))
                alertMesasgeWindow.isCancelable = false
            }

        }

    }

    private fun start_loading_animation(v: View) {
        v.detail_view.visibility = View.GONE
        if (!pDialog!!.isShowing)
            pDialog!!.show()
    }

    private fun stop_loading_animation(v: View) {
        v.detail_view.visibility = View.VISIBLE
        if (pDialog!!.isShowing)
            pDialog!!.dismiss()
    }

    companion object {

        fun newInstance(): ESP_LIB_AssesscorFeedBackDetailFragment {
            val fragment = ESP_LIB_AssesscorFeedBackDetailFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

}// Required empty public constructor
