package com.esp.library.exceedersesp.fragments.setup


import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import com.esp.library.R
import com.esp.library.exceedersesp.ESP_LIB_BaseActivity
import com.esp.library.exceedersesp.ESP_LIB_ESPApplication
import com.esp.library.exceedersesp.SingleController.CompRoot
import com.esp.library.exceedersesp.controllers.ESP_LIB_SplashScreenActivity
import com.esp.library.exceedersesp.controllers.ESP_LIB_WebViewScreenActivity
import com.esp.library.exceedersesp.controllers.Profile.ESP_LIB_ProfileMainActivity
import com.esp.library.exceedersesp.controllers.applications.ESP_LIB_ApplicationsActivityDrawer
import com.esp.library.utilities.common.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.esp_lib_fragment_login_screen.view.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import utilities.data.apis.ESP_LIB_APIs
import utilities.data.applicants.profile.ESP_LIB_ApplicationProfileDAO
import utilities.data.setup.*
import utilities.model.ESP_LIB_Labels
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit


class ESP_LIB_LoginScreenFragment : androidx.fragment.app.Fragment() {

    internal var TAG = "LoginScreenFragment"

    internal lateinit var context: ESP_LIB_BaseActivity

    internal var email: String? = null
    internal var password: String? = null
    internal var imm: InputMethodManager? = null
    internal var ESPLIBToken: ESP_LIB_TokenDAO? = null

    internal var login_call: Call<ESP_LIB_TokenDAO>? = null

    internal var anim: ESP_LIB_ProgressBarAnimation? = null

    internal var alertOrgWindowESPLIB: ESP_LIB_SelectOrganizationWindow? = null
    internal var pref: ESP_LIB_SharedPreference? = null
    internal var ClickCount = 0

    internal var pDialog: AlertDialog? = null
    internal var txtenteremailpass: TextView? = null
    var ESPLIBApplicationProfilebody: ESP_LIB_ApplicationProfileDAO? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val v = inflater.inflate(R.layout.esp_lib_fragment_login_screen, container, false)
        initailize(v)
        setGravity(v)


        v.forgot_password.setOnClickListener { view ->
            val bn = Bundle()
            bn.putString("heading", getString(R.string.esp_lib_text_forgot_password))
            //   bn.putString("url", "https://qaesp.azurewebsites.net/forgotpassword")
            bn.putString("url", ESP_LIB_Constants.base_url.replace("webapi/", "") + "forgotpassword")
            ESP_LIB_Shared.getInstance().callIntent(ESP_LIB_WebViewScreenActivity::class.java, context, bn)
        }

        ESP_LIB_CustomLogs.displayLogs("$TAG getDeviceId: ${ESP_LIB_Shared.getInstance().getDeviceId(context)}")


        if (ESP_LIB_Shared.getInstance().ReadPref("url", "base_url", context) != null) {
            val url_ = ESP_LIB_Shared.getInstance().ReadPref("url", "base_url", context)
            v.url_text.setText(url_)
        } else {
            v.url_view.visibility = View.VISIBLE
            v.login_view.visibility = View.GONE
        }

        v.url_ok.setOnClickListener { view ->
            val new_base_url = v.url_text.text.toString()
            if (new_base_url.length == 0) {
                ESP_LIB_Shared.getInstance().messageBox(getString(R.string.esp_lib_text_enterbaseurl), context)
            } else if (!ESP_LIB_Shared.getInstance().checkURL(new_base_url)) {
                ESP_LIB_Shared.getInstance().messageBox(getString(R.string.esp_lib_text_entervalidurl), context)
            } else {
                if (ESP_LIB_Shared.getInstance().ReadPref("url", "base_url", context) != null) {
                    ESP_LIB_Shared.getInstance().WritePref("url", null, "base_url", context)
                }
                ESP_LIB_Shared.getInstance().WritePref("url", new_base_url, "base_url", context)
                ESP_LIB_Constants.base_url = ESP_LIB_Shared.getInstance().ReadPref("url", "base_url", context) + ESP_LIB_Constants.base_url_api
                v.url_view.visibility = View.GONE
                v.login_view.visibility = View.VISIBLE
            }
        }

        v.logo.setOnClickListener { view ->
            ClickCount++

            if (ClickCount > 5) {
                ESP_LIB_Shared.getInstance().messageBox(getString(R.string.esp_lib_text_youaredevelopernow), context)
                v.url_view.visibility = View.VISIBLE
                v.login_view.visibility = View.GONE
                ClickCount = 0
            }
        }


        if (pref?.getidenediClientId().isNullOrEmpty()) {
            v.idenedi_login_btn.visibility = View.GONE
            v.txtor.visibility = View.GONE
        }

        v.idenedi_login_btn.setOnClickListener {
            val bn = Bundle()
            bn.putString("heading", getString(R.string.esp_lib_text_logineithidenedi))

            //   bn.putString("url", "https://app.idenedi.com/app_permission/?response_type=code&client_id=" + pref?.getidenediClientId() + "&redirect_uri=https://isp.exceedgulf.com/login")
              //   bn.putString("url", "https://app.idenedi.com/app_permission/?response_type=code&client_id=" + pref?.getidenediClientId() + "&redirect_uri="+ESP_LIB_Constants.base_url.replace("webapi/", "")+"login")
           // bn.putString("url", "https://idenedi-prod-stag.azurewebsites.net/app_permission/?response_type=code&client_id=" + pref?.getidenediClientId() + "&redirect_uri=https://qaesp.azurewebsites.net/login")
            bn.putString("url", pref?.getidenediLoginUri()+"/authorization/?response_type=code&client_id=" + pref?.getidenediClientId() + "&redirect_uri=https://qaesp.azurewebsites.net/login")
            bn.putBoolean("isIdenedi", true)
            ESP_LIB_Shared.getInstance().callIntent(ESP_LIB_WebViewScreenActivity::class.java, context, bn)
        }

        v.email_input.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun afterTextChanged(editable: Editable) {


                val outputedText = editable.toString()

                if (!ESP_LIB_Shared.getInstance().isValidEmailAddress(outputedText)) {
                    v.tilFieldLabel.isErrorEnabled = true
                    v.tilFieldLabel.error = context.getString(R.string.esp_lib_text_invalidemail)
                } else {
                    v.tilFieldLabel.isErrorEnabled = false
                    v.tilFieldLabel.error = null
                }


            }
        })


        val username_pre = ESP_LIB_Shared.getInstance().ReadPref("Uname", "login_info", context)
        var password_pre = ESP_LIB_Shared.getInstance().ReadPref("Pass", "login_info", context)


        if (username_pre != null && (password_pre != null && !password_pre.equals(""))) {

            email = username_pre
            password = password_pre


            v.email_input.setText(username_pre)
            v.password_input.setText(password_pre)

            if (ESP_LIB_Shared.getInstance().isWifiConnected(context)) {
                val pt = ESP_LIB_PostTokenDAO()
                pt.client_id = "ESPMobile"
                pt.grant_type = "password"
                pt.password = password_pre
                pt.username = username_pre

                GetToken(pt)


            } else {
                ESP_LIB_Shared.getInstance().showAlertMessage(getString(R.string.esp_lib_text_internet_error_heading), getString(R.string.esp_lib_text_internet_connection_error), context)
            }


        } else {
            getIdenediUser()
        }

        pref?.savelanguage("en")
        //  populateSpinner(v)

        FirebaseInstanceId.getInstance().instanceId
                .addOnCompleteListener(OnCompleteListener { task ->
                    if (!task.isSuccessful) {

                        ESP_LIB_CustomLogs.displayLogs("$TAG getFirebaseInstanceId failed $task.exception")
                        return@OnCompleteListener
                    }
                    // Get new Instance ID token
                    val firebaseToken = task.result?.token
                    val firebasetokenid = task.result?.id

                    pref?.saveFirebaseToken(firebaseToken)
                    // Log and toast
                    ESP_LIB_CustomLogs.displayLogs("$TAG getFirebaseInstanceId token $firebaseToken \ntokenid: $firebasetokenid")
                })



        return v
    }

    private fun initailize(v: View) {
        context = activity as ESP_LIB_BaseActivity
        pref = ESP_LIB_SharedPreference(context)
        pDialog = ESP_LIB_Shared.getInstance().setProgressDialog(context)
        txtenteremailpass = v.findViewById(R.id.txtenteremailpass)
        imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        v.url_view.visibility = View.GONE
        v.login_btn.setOnClickListener { view -> SignInUser(v) }


    }

    private fun getIdenediUser() {
        if (pref?.refreshToken != null) {
            val pt = ESP_LIB_PostTokenDAO()
            pt.client_id = "ESPMobile"
            pt.grant_type = "refresh_token"
            pt.password = ""
            pt.username = ""
            pt.scope = ESP_LIB_Shared.getInstance().ReadPref("scropId", "login_info", context)
            pt.refresh_token = pref?.refreshToken
            GetRefreshToken(pt)
        }
    }

    private fun populateSpinner(v: View) {

        if (pref?.language.equals("", ignoreCase = true)) {
            pref?.savelanguage(Locale.getDefault().language)
            setApplicationlanguage(v)
        } else {

            setApplicationlanguage(v)
        }

        if (pref?.language.equals(Locale.getDefault().getLanguage(), ignoreCase = true)) {
            pref?.savelanguage(Locale.getDefault().getLanguage());
            setApplicationlanguage(v);
        }


        val arrayLanguages = ArrayList<String>()
        arrayLanguages.add(getString(R.string.esp_lib_text_english))
        arrayLanguages.add(getString(R.string.esp_lib_text_arabic))

        val adapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, arrayLanguages)
        v.splanguage.adapter = adapter

        if (pref?.language.equals("en", ignoreCase = true))
            v.splanguage.setSelection(0)
        else if (pref?.language.equals("ar", ignoreCase = true))
            v.splanguage.setSelection(1)
        /*else if (pref.getLanguage().equalsIgnoreCase("fr"))
            splanguage.setSelection(2);*/

        v.splanguage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
                //changeLanguage(arrayLanguages[i], v)
            }

            override fun onNothingSelected(adapterView: AdapterView<*>) {

            }
        }

    }

    private fun changeLanguage(languageType: String, v: View) {
        val language: String

        if (languageType.equals(getString(R.string.esp_lib_text_arabic), ignoreCase = true))
            language = "ar"
        else
            language = "en"

        pref?.savelanguage(language)
        setApplicationlanguage(v)


    }

    fun setApplicationlanguage(v: View) {
        val res = resources
        val dm = res.displayMetrics
        val conf = res.configuration
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            conf.setLocale(Locale(pref?.language)) // API 17+ only.
        } else {
            conf.locale = Locale(pref?.language)
        }
        res.updateConfiguration(conf, dm)

        updateFields(v)
    }

    private fun applyLanguage() {
        val res = resources
        val dm = res.displayMetrics
        val conf = res.configuration
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            conf.setLocale(Locale(pref?.language)) // API 17+ only.
        } else {
            conf.locale = Locale(pref?.language)
        }
        res.updateConfiguration(conf, dm)


    }


    private fun updateFields(v: View) {
        v.txtwelcome.text = getString(R.string.esp_lib_text_welcometoesp)
        txtenteremailpass?.text = getString(R.string.esp_lib_text_enteremailandpassword)
        v.tilFieldLabel.hint = getString(R.string.esp_lib_text_email)
        v.tilFieldLabelPassword.hint = getString(R.string.esp_lib_text_password)
        v.login_btn.text = getString(R.string.esp_lib_text_sign_in)
        v.forgot_password.text = getString(R.string.esp_lib_text_forgotpassword)
        v.txtlanguage.text = getString(R.string.esp_lib_text_language)
        setGravity(v)
    }

    private fun setGravity(v: View) {
        if (pref?.language.equals("ar", ignoreCase = true)) {
            v.email_input.gravity = Gravity.END
            v.password_input.gravity = Gravity.END

        } else {
            v.email_input.gravity = Gravity.START
            v.password_input.gravity = Gravity.START
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    private fun SignInUser(v: View) {

        imm!!.hideSoftInputFromWindow(requireView().windowToken, 0)

        email = null
        password = null

        email = v.email_input.text.toString().trim { it <= ' ' }
        password = v.password_input.text.toString().trim { it <= ' ' }


        if (email!!.length == 0) {

            ESP_LIB_Shared.getInstance().showAlertMessage(context.getString(R.string.esp_lib_text_login_email_label), context.getString(R.string.esp_lib_text_login_email_error), context)
            return
        }

        if (!ESP_LIB_Shared.getInstance().isValidEmailAddress(email)) {

            ESP_LIB_Shared.getInstance().showAlertMessage(context.getString(R.string.esp_lib_text_login_email_label), context.getString(R.string.esp_lib_text_login_email_error), context)
            return
        }

        if (password!!.length == 0) {

            ESP_LIB_Shared.getInstance().showAlertMessage(context.getString(R.string.esp_lib_text_login_password_label), context.getString(R.string.esp_lib_text_login_valid_password_error), context)
            return
        }

        if (password!!.length < 6) {
            ESP_LIB_Shared.getInstance().showAlertMessage(context.getString(R.string.esp_lib_text_login_password_label), context.getString(R.string.esp_lib_text_length_password_error), context)
            return
        }

        if (ESP_LIB_Shared.getInstance().isWifiConnected(context)) {
            val pt = ESP_LIB_PostTokenDAO()
            pt.client_id = "ESPMobile"
            pt.grant_type = "password"
            pt.password = password
            pt.username = email



            GetToken(pt)

        } else {
            ESP_LIB_Shared.getInstance().showAlertMessage(getString(R.string.esp_lib_text_internet_error_heading), getString(R.string.esp_lib_text_internet_connection_error), context)
        }

    }

    fun RefreshToken(ESPLIBPersonaDAO: ESP_LIB_PersonaDAO) {

        if (alertOrgWindowESPLIB != null) {
            alertOrgWindowESPLIB!!.dismiss()
        }

        if (ESP_LIB_Shared.getInstance().isWifiConnected(context)) {
            val pt = ESP_LIB_PostTokenDAO()
            pt.client_id = "ESPMobile"
            pt.grant_type = "refresh_token"
            pt.password = password
            pt.username = email
            pt.scope = ESPLIBPersonaDAO.id
            pt.refresh_token = ESPLIBPersonaDAO.refresh_token
            ESP_LIB_Shared.getInstance().WritePref("scropId", ESPLIBPersonaDAO.id, "login_info", context)
            GetRefreshToken(pt)

        } else {
            ESP_LIB_Shared.getInstance().showAlertMessage(getString(R.string.esp_lib_text_internet_error_heading), getString(R.string.esp_lib_text_internet_connection_error), context)
        }
    }

    private fun linkIdendiUser(isSentToProfile: Boolean) {
        start_loading_animation()
        try {

            val apis = CompRoot().getService(context);
            val call_idenediToken = apis?.linkIdenediUser(pref?.getidenediAuthDAO())

            call_idenediToken?.enqueue(object : Callback<ESP_LIB_IdenediAuthDAO> {
                override fun onResponse(call: Call<ESP_LIB_IdenediAuthDAO>?, response: Response<ESP_LIB_IdenediAuthDAO>?) {

                    stop_loading_animation()

                    if (response?.isSuccessful!!) {
                        pref?.saveidenediAuthDAO(null)
                        pref?.saveRefreshToken(null)

                        if (isSentToProfile) {
                            val intentToBeNewRoot = Intent(context, ESP_LIB_ProfileMainActivity::class.java)
                            val cn = intentToBeNewRoot.component
                            val mainIntent = Intent.makeRestartActivityTask(cn)
                            mainIntent.putExtra("dataapplicant", ESPLIBApplicationProfilebody)
                            startActivity(mainIntent)
                        } else
                            ESP_LIB_Shared.getInstance().callIntentClearAllActivities(ESP_LIB_ApplicationsActivityDrawer::class.java, context, null)
                    } else {

                        ESP_LIB_Shared.getInstance().showAlertMessage(getString(R.string.esp_lib_text_login_label), getString(R.string.esp_lib_text_some_thing_went_wrong), context)
                    }
                }

                override fun onFailure(call: Call<ESP_LIB_IdenediAuthDAO>, t: Throwable) {
                    ESP_LIB_CustomLogs.displayLogs("$TAG there")
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

    private fun GetToken(ESPLIBPostTokenDAO: ESP_LIB_PostTokenDAO) {

        start_loading_animation()

        try {

            val apis = CompRoot().getService(context);
            login_call = apis?.getToken(ESPLIBPostTokenDAO.grant_type, ESPLIBPostTokenDAO.username, ESPLIBPostTokenDAO.password, ESPLIBPostTokenDAO.client_id)

            login_call!!.enqueue(object : Callback<ESP_LIB_TokenDAO> {
                override fun onResponse(call: Call<ESP_LIB_TokenDAO>, response: Response<ESP_LIB_TokenDAO>?) {

                    if (response != null && response.body() != null) {

                        // Shared.getInstance().showAlertMessage("login",response.body().getEmail(),context);
                        getTokenData(response)


                    } else {

                        try {
                            val errorMsg = invalidAttempts(response!!.errorBody().string())
                            ESP_LIB_Shared.getInstance().showAlertMessage(getString(R.string.esp_lib_text_login_label), errorMsg, context)
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }


                    }
                }

                override fun onFailure(call: Call<ESP_LIB_TokenDAO>, t: Throwable) {
                    t.printStackTrace()
                    ESP_LIB_CustomLogs.displayLogs("$TAG theree")
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

    private fun getTokenData(response: Response<ESP_LIB_TokenDAO>) {

        if (response.body().personas != null && response.body().personas.length > 0) {

            ESP_LIB_ESPApplication.getInstance().tokenPersonas = response.body()

            val applicantPersonaId = response.body().applicantPersonaId?.toIntOrNull() ?: 0
            if (applicantPersonaId > 0 && (response.body().role.equals(ESP_LIB_Enums.admin.toString(), ignoreCase = true)
                            || response.body().role.equals(ESP_LIB_Enums.user.toString(), ignoreCase = true))) {
                response.body().role = ESP_LIB_Enums.applicant.toString()
            } else if (applicantPersonaId == 0 && (response.body().role.equals(ESP_LIB_Enums.admin.toString(), ignoreCase = true)
                            || response.body().role.equals(ESP_LIB_Enums.user.toString(), ignoreCase = true))) {
                response.body().role = ESP_LIB_Enums.assessor.toString()
            } else if (applicantPersonaId == 0 && (response.body().role.equals(ESP_LIB_Enums.applicant.toString(), ignoreCase = true))) {
                response.body().role = ESP_LIB_Enums.applicant.toString()
            }


            var list: List<ESP_LIB_PersonaDAO>? = null
            val personas = response.body().personas
            val gson = Gson()
            list = gson.fromJson<List<ESP_LIB_PersonaDAO>>(personas, object : TypeToken<List<ESP_LIB_PersonaDAO>>() {}.type)

            if (list != null && list.size > 0) {
                val app_list = ESP_LIB_Shared.getInstance().GetApplicantOrganization(list, context)
                if (app_list == null || app_list.size == 0) {
                    stop_loading_animation()
                    ESP_LIB_Shared.getInstance().showAlertMessage(getString(R.string.esp_lib_text_login_label), getString(R.string.esp_lib_text_login_error), context)
                    return
                }
                response.body().password = password

                if (app_list.size == 1) {
                    //User is Applicant and logged in
                    val userDAO = ESP_LIB_UserDAO()
                    userDAO.loginResponse = response.body()
                    ESP_LIB_ESPApplication.getInstance().access_token=userDAO.loginResponse?.access_token

                    ESP_LIB_ESPApplication.getInstance().user = userDAO
                    ESP_LIB_ESPApplication.getInstance().filter = ESP_LIB_Shared.getInstance().ResetApplicationFilter(context)

                    ESP_LIB_Shared.getInstance().WritePref("Uname", response.body().email, "login_info", context)
                    ESP_LIB_Shared.getInstance().WritePref("Pass", response.body().password, "login_info", context)

                    try {
                        if (ESP_LIB_Shared.getInstance().ReadPref("scropId", "login_info", context) != null) {
                            ESP_LIB_Shared.getInstance().WritePref("scropId", null, "login_info", context)
                        }

                        ESP_LIB_Shared.getInstance().WritePref("scropId", app_list[0].id, "login_info", context)

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    var role = response.body().role
                    if (role.equals(ESP_LIB_Enums.admin.toString(), ignoreCase = true)
                            || role.equals(ESP_LIB_Enums.user.toString(), ignoreCase = true) ||
                            role.equals(ESP_LIB_Enums.assessor.toString(), ignoreCase = true)) {
                        role = ESP_LIB_Enums.assessor.toString()
                    }
                    pref?.saveSelectedUserRole(role)

                    getlabels(response)


                } else if (app_list.size > 1) {

                    var ScropId: String? = null

                    try {
                        if (ESP_LIB_Shared.getInstance().ReadPref("scropId", "login_info", context) != null) {
                            ScropId = ESP_LIB_Shared.getInstance().ReadPref("scropId", "login_info", context)
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }


                    if (ScropId != null && ScropId.length > 0) {

                        val pt = ESP_LIB_PostTokenDAO()
                        pt.client_id = "ESPMobile"
                        pt.grant_type = "refresh_token"
                        pt.password = response.body().password
                        pt.username = response.body().email
                        pt.scope = ScropId
                        pt.refresh_token = response.body().refresh_token
                        GetRefreshToken(pt)

                    } else {

                        stop_loading_animation()
                        alertOrgWindowESPLIB = ESP_LIB_SelectOrganizationWindow.newInstance(response.body())
                        alertOrgWindowESPLIB!!.show(requireActivity().supportFragmentManager, getString(R.string.esp_lib_text_alert))
                        alertOrgWindowESPLIB!!.isCancelable = false


                        //getFragmentManager
                    }
                }

            }
        } else {
            stop_loading_animation()
            ESP_LIB_Shared.getInstance().showAlertMessage(getString(R.string.esp_lib_text_login_label), getString(R.string.esp_lib_text_login_error), context)

        }

    }

    private fun GetRefreshToken(ESPLIBPostTokenDAO: ESP_LIB_PostTokenDAO) {

        start_loading_animation()

        try {

            val apis = CompRoot()?.getService(context);
            login_call = apis?.getRefreshToken(ESPLIBPostTokenDAO.scope, ESPLIBPostTokenDAO.grant_type, ESPLIBPostTokenDAO.username, ESPLIBPostTokenDAO.password, ESPLIBPostTokenDAO.client_id, ESPLIBPostTokenDAO.scope, ESPLIBPostTokenDAO.refresh_token)


            login_call!!.enqueue(object : Callback<ESP_LIB_TokenDAO> {
                override fun onResponse(call: Call<ESP_LIB_TokenDAO>, response: Response<ESP_LIB_TokenDAO>?) {


                    if (response != null && response.body() != null) {

                        if (response.body().personas != null && response.body().personas.length > 0) {


                            if (ESP_LIB_ESPApplication.getInstance().user != null) {
                                ESP_LIB_ESPApplication.getInstance().user = null
                            }


                            val applicantPersonaId = response.body().applicantPersonaId?.toIntOrNull()
                                    ?: 0
                            if (applicantPersonaId > 0 && (response.body().role.equals(ESP_LIB_Enums.admin.toString(), ignoreCase = true)
                                            || response.body().role.equals(ESP_LIB_Enums.user.toString(), ignoreCase = true))) {
                                response.body().role = ESP_LIB_Enums.applicant.toString()
                            } else if (applicantPersonaId == 0 && (response.body().role.equals(ESP_LIB_Enums.admin.toString(), ignoreCase = true)
                                            || response.body().role.equals(ESP_LIB_Enums.user.toString(), ignoreCase = true))) {
                                response.body().role = ESP_LIB_Enums.assessor.toString()
                            } else if (applicantPersonaId == 0 && (response.body().role.equals(ESP_LIB_Enums.applicant.toString(), ignoreCase = true))) {
                                response.body().role = ESP_LIB_Enums.applicant.toString()
                            }

                            val userDAO = ESP_LIB_UserDAO()
                            response.body().password = ESPLIBPostTokenDAO.password
                            userDAO.loginResponse = response.body()
                            ESP_LIB_ESPApplication.getInstance().access_token=userDAO.loginResponse?.access_token

                            ESP_LIB_ESPApplication.getInstance().user = userDAO
                            ESP_LIB_ESPApplication.getInstance().filter = ESP_LIB_Shared.getInstance().ResetApplicationFilter(context)

                            ESP_LIB_Shared.getInstance().WritePref("Uname", ESPLIBPostTokenDAO.username, "login_info", context)
                            ESP_LIB_Shared.getInstance().WritePref("Pass", ESPLIBPostTokenDAO.password, "login_info", context)


                            getlabels(response)


                        } else {
                            stop_loading_animation()
                            ESP_LIB_Shared.getInstance().showAlertMessage(getString(R.string.esp_lib_text_login_label), getString(R.string.esp_lib_text_applicant_error), context)
                        }


                    } else {
                        stop_loading_animation()
                        ESP_LIB_Shared.getInstance().showAlertMessage(getString(R.string.esp_lib_text_login_label), getString(R.string.esp_lib_text_some_thing_went_wrong), context)

                    }
                }

                override fun onFailure(call: Call<ESP_LIB_TokenDAO>, t: Throwable?) {

                    stop_loading_animation()

                    if (t != null && isAdded) {
                        ESP_LIB_Shared.getInstance().showAlertMessage(getString(R.string.esp_lib_text_login_label), getString(R.string.esp_lib_text_some_thing_went_wrong), context)
                    }
                    return

                }
            })

        } catch (ex: Exception) {
            stop_loading_animation()
            if (isAdded) {
                ESP_LIB_Shared.getInstance().showAlertMessage(getString(R.string.esp_lib_text_login_label), getString(R.string.esp_lib_text_some_thing_went_wrong), context)
            }
        }

    }

    private fun sendIdendiCode() {
        start_loading_animation()
        var getError: String = ""
        try {
            //  val logging = HttpLoggingInterceptor()
            val logging = HttpLoggingInterceptor(HttpLoggingInterceptor.Logger { message ->

                if (message.contains("grant_type"))
                    getError = message
                /* when (ESP_LIB_Shared.getInstance().isJSONValid(message)) {
                    true -> {
                        if (message.contains("grant_type"))
                            getError = message
                    };
                }*/
            })
            logging.level = HttpLoggingInterceptor.Level.BODY
            val httpClient = OkHttpClient.Builder()
            httpClient.addInterceptor(logging)
            httpClient.addInterceptor { chain ->
                val original = chain.request()
                val requestBuilder = original.newBuilder()
                        .header("locale", ESP_LIB_Shared.getInstance().getLanguageSimpleContext(context));
                //   .header("Content-Type ", "application/x-www-form-urlencoded")
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
            // var call_idenediToken = apis.getIdenediToken()

            var call_idenediToken = apis.getIdenedirefreshToken("password",
                    "", "", "ESPMobile", pref?.idenediCode!!)


            call_idenediToken.enqueue(object : Callback<ESP_LIB_TokenDAO> {
                override fun onResponse(call: Call<ESP_LIB_TokenDAO>?, response: Response<ESP_LIB_TokenDAO>?) {

                    stop_loading_animation()

                    pref?.saveIdenediCode(null)
                    if (response != null && response.body() != null) {
                        val refreshToken = response.body()?.refresh_token
                        pref?.saveRefreshToken(refreshToken)
                        getTokenData(response)

                    } else {

                        if (response?.code() == 400) {
                            val idenediAuthDAO = Gson().fromJson(getError, ESP_LIB_IdenediAuthDAO::class.java)
                            val error_description = idenediAuthDAO?.error_description
                            val errorDescriptionObj = Gson().fromJson(error_description, ESP_LIB_IdenediAuthDAO::class.java)
                            val refreshToken = errorDescriptionObj.RefreshToken
                            pref?.saveRefreshToken(refreshToken)

                            val idenediDAO = ESP_LIB_IdenediAuthDAO()
                            idenediDAO.RefreshToken = refreshToken
                            idenediDAO.AccessToken = errorDescriptionObj.AccessToken
                            idenediDAO.EmailAddress = errorDescriptionObj.EmailAddress
                            idenediDAO.IdenediId = errorDescriptionObj.IdenediId
                            pref?.saveidenediAuthDAO(idenediDAO)

                            txtenteremailpass?.text = getString(R.string.esp_lib_text_idenedinotlinkedtext)
                            view?.login_btn?.text = getString(R.string.esp_lib_text_logintolink)
                            view?.idenedi_login_btn?.visibility = View.GONE
                            view?.txtor?.visibility = View.GONE


                        } else
                        {
                            ESP_LIB_Shared.getInstance().messageBox(getString(R.string.esp_lib_text_some_thing_went_wrong), context as Activity?)
                            pref?.saveIdenediCode(null)
                        }
                    }
                }


                override fun onFailure(call: Call<ESP_LIB_TokenDAO>, t: Throwable) {
                    ESP_LIB_CustomLogs.displayLogs("$TAG thereee")
                    pref?.saveIdenediCode(null)
                    stop_loading_animation()
                    ESP_LIB_Shared.getInstance().messageBox(getString(R.string.esp_lib_text_some_thing_went_wrong), context as Activity?)

                }
            })

        } catch (ex: Exception) {
            pref?.saveIdenediCode(null)
            ex.printStackTrace()
            stop_loading_animation()
            ESP_LIB_Shared.getInstance().messageBox(getString(R.string.esp_lib_text_some_thing_went_wrong), context as Activity?)

        }

    }

    private fun invalidAttempts(errorBody: String): String {
        var remainingVal = 0
        var errorMsg = getString(R.string.esp_lib_text_login_error)
        try {
            val jsonObject = JSONObject(errorBody)
            val error = jsonObject.getString("error")
            val error_description = jsonObject.getString("error_description")
            if (error.equals(getString(R.string.esp_lib_text_locked), ignoreCase = true)) {
                errorMsg = getString(R.string.esp_lib_text_login_error_account_locked)
                remainingVal = -1
            } else {
                val remainingSplit = error.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                if (remainingSplit[1] != null)
                    remainingVal = Integer.parseInt(remainingSplit[1])
            }
            ESP_LIB_CustomLogs.displayLogs("$TAG error: $error error_description: $error_description remainingAttempts: $remainingVal")
        } catch (e: Exception) {
            e.printStackTrace()
        }

        stop_loading_animation()
        if (remainingVal == 3)
            errorMsg = getString(R.string.esp_lib_text_login_error_remaining3)
        else if (remainingVal == 2)
            errorMsg = getString(R.string.esp_lib_text_login_error_remaining2)
        else if (remainingVal == 1)
            errorMsg = getString(R.string.esp_lib_text_login_error_remaining1)
        else if (remainingVal > 5)
            errorMsg = getString(R.string.esp_lib_text_login_error_locked)

        return errorMsg
    }

    private fun start_loading_animation() {
        try {
            if (!pDialog!!.isShowing)
                pDialog!!.show()
        } catch (e: java.lang.Exception) {
        }
    }

    private fun stop_loading_animation() {
        try {
            if (pDialog!!.isShowing)
                pDialog!!.dismiss()
        } catch (e: java.lang.Exception) {
        }
    }

    private fun getlabels(serviceResponse: Response<ESP_LIB_TokenDAO>?) {
        start_loading_animation()
        try {

            val apis = CompRoot()?.getService(context);
            val labels_call = apis?.getLabels()
            labels_call?.enqueue(object : Callback<ESP_LIB_Labels> {
                override fun onResponse(call: Call<ESP_LIB_Labels>, response: Response<ESP_LIB_Labels>) {

                    val pref = ESP_LIB_SharedPreference(context)
                    pref.savelabels(response.body())


                    val personas = ESP_LIB_ESPApplication.getInstance()?.user?.loginResponse?.personas
                    val organizationId = ESP_LIB_ESPApplication.getInstance()?.user?.loginResponse?.organizationId

                    try {
                        val jsonArray = JSONArray(personas)
                        for (i in 0 until jsonArray.length()) {
                            val jobj = jsonArray.getJSONObject(i)

                            val orgId = jobj.getString("orgId")


                            if (organizationId.equals(orgId, ignoreCase = true)) {
                                val locales = jobj.getString("locales")
                                val myList = ArrayList(Arrays.asList(*locales.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()))
                                val role = serviceResponse?.body()?.role
                                ESP_LIB_ESPApplication.getInstance().user.role = role

                                if (role.equals(ESP_LIB_Enums.admin.toString(), ignoreCase = true)
                                        || role.equals(ESP_LIB_Enums.user.toString(), ignoreCase = true) ||
                                        role.equals(ESP_LIB_Enums.assessor.toString(), ignoreCase = true)) {
                                    stop_loading_animation()
                                    if (myList.contains(pref.language)) {
                                        if (txtenteremailpass?.text.toString() == getString(R.string.esp_lib_text_idenedinotlinkedtext))
                                            linkIdendiUser(false)
                                        else
                                            ESP_LIB_Shared.getInstance().callIntentClearAllActivities(ESP_LIB_ApplicationsActivityDrawer::class.java, context, null)
                                    } else {
                                        pref.savelanguage(myList[0])
                                        applyLanguage()
                                        val intentToBeNewRoot = Intent(context, ESP_LIB_SplashScreenActivity::class.java)
                                        val cn = intentToBeNewRoot.component
                                        val mainIntent = Intent.makeRestartActivityTask(cn)
                                        startActivity(mainIntent)
                                    }
                                } else
                                    getApplicant(myList)


                            }

                        }
                    } catch (e: JSONException) {
                        stop_loading_animation()
                        e.printStackTrace()
                    }


                }

                override fun onFailure(call: Call<ESP_LIB_Labels>, t: Throwable) {
                    stop_loading_animation()
                    ESP_LIB_Shared.getInstance().messageBox(getString(R.string.esp_lib_text_some_thing_went_wrong), context)
                }
            })


        } catch (ex: Exception) {
            ex.printStackTrace()
            stop_loading_animation()
            ESP_LIB_Shared.getInstance().messageBox(getString(R.string.esp_lib_text_some_thing_went_wrong), context)
        }

    }

    private fun getApplicant(myList: List<String>) {
        start_loading_animation()
        try {

            val apis = CompRoot()?.getService(context);
            val labels_call = apis?.Getapplicant()

            labels_call?.enqueue(object : Callback<ESP_LIB_ApplicationProfileDAO> {
                override fun onResponse(call: Call<ESP_LIB_ApplicationProfileDAO>, response: Response<ESP_LIB_ApplicationProfileDAO>) {
                    stop_loading_animation()
                    ESPLIBApplicationProfilebody = response.body()

                    if (ESPLIBApplicationProfilebody != null) {
                        val profileSubmitted = response.body().applicant.isProfileSubmitted

                        if (profileSubmitted) {
                            if (myList.contains(pref?.language)) {
                                if (txtenteremailpass?.text?.toString() == context.getString(R.string.esp_lib_text_idenedinotlinkedtext))
                                    linkIdendiUser(false)
                                else
                                    ESP_LIB_Shared.getInstance().callIntentClearAllActivities(ESP_LIB_ApplicationsActivityDrawer::class.java, context, null)
                            } else {
                                pref?.savelanguage(myList[0])
                                applyLanguage()
                                val intentToBeNewRoot = Intent(context, ESP_LIB_SplashScreenActivity::class.java)
                                val cn = intentToBeNewRoot.component
                                val mainIntent = Intent.makeRestartActivityTask(cn)
                                startActivity(mainIntent)
                            }
                        } else {
                            if (txtenteremailpass?.text.toString() == getString(R.string.esp_lib_text_idenedinotlinkedtext))
                                linkIdendiUser(true)
                            else {
                                val intentToBeNewRoot = Intent(context, ESP_LIB_ProfileMainActivity::class.java)
                                val cn = intentToBeNewRoot.component
                                val mainIntent = Intent.makeRestartActivityTask(cn)
                                mainIntent.putExtra("dataapplicant", ESPLIBApplicationProfilebody)
                                startActivity(mainIntent)
                            }
                        }
                    } else
                        ESP_LIB_Shared.getInstance().messageBox(getString(R.string.esp_lib_text_some_thing_went_wrong), context)
                }

                override fun onFailure(call: Call<ESP_LIB_ApplicationProfileDAO>, t: Throwable) {
                    stop_loading_animation()
                    t.printStackTrace()
                    try {
                        ESP_LIB_Shared.getInstance().messageBox(getString(R.string.esp_lib_text_some_thing_went_wrong), context)
                    } catch (e: java.lang.Exception) {
                    }
                }
            })


        } catch (ex: Exception) {
            ex.printStackTrace()
            stop_loading_animation()
            try {
                ESP_LIB_Shared.getInstance().messageBox(getString(R.string.esp_lib_text_some_thing_went_wrong), context)
            } catch (e: java.lang.Exception) {
            }
        }

    }


    companion object {

        fun newInstance(param1: String, param2: String): ESP_LIB_LoginScreenFragment {
            return ESP_LIB_LoginScreenFragment()
        }
    }

    override fun onResume() {
        super.onResume()
        if (!pref?.idenediCode.isNullOrEmpty())
            sendIdendiCode()
    }


}
