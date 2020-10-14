package utilities.adapters.setup

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.esp.library.R
import com.esp.library.exceedersesp.ESP_LIB_BaseActivity
import com.esp.library.exceedersesp.SingleController.CompRoot
import com.esp.library.utilities.common.*
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import utilities.data.apis.ESP_LIB_APIs
import utilities.data.applicants.ESP_LIB_FirebaseTokenDAO
import utilities.data.setup.ESP_LIB_OrganizationPersonaDao
import utilities.data.setup.ESP_LIB_PersonaDAO
import utilities.data.setup.ESP_LIB_TokenDAO
import java.util.*
import java.util.concurrent.TimeUnit

class ESP_LIB_ListPersonaDAOAdapter(persoans: List<ESP_LIB_OrganizationPersonaDao.Personas>, internal var section: ESP_LIB_OrganizationPersonaDao,
                                    context: ESP_LIB_BaseActivity, private val personas: ESP_LIB_TokenDAO) : androidx.recyclerview.widget.RecyclerView.Adapter<ESP_LIB_ListPersonaDAOAdapter.ViewHolder>() {

    private val TAG = "ListPersonaDAOAdapter"
    internal var mUser: RefreshToken
    internal var pref: ESP_LIB_SharedPreference
    private val context: ESP_LIB_BaseActivity
    internal var pDialog: AlertDialog? = null
    internal var persoans: List<ESP_LIB_OrganizationPersonaDao.Personas> = ArrayList()

    init {
        this.context = context
        this.persoans = persoans
        try {
            mUser = context as RefreshToken
        } catch (e: ClassCastException) {
            throw ClassCastException("lisnter" + " must implement on Activity")
        }
        pref = ESP_LIB_SharedPreference(context)
        pDialog = ESP_LIB_Shared.getInstance().setProgressDialog(context)
    }

    interface RefreshToken {
        fun StatusChange(update: ESP_LIB_PersonaDAO)
    }

    inner class ViewHolder(v: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(v) {

        internal var cardView: LinearLayout
        internal var organization: TextView
        internal var organization_role: TextView
        internal var user_type: ImageView


        init {
            cardView = itemView.findViewById(R.id.cards)
            organization = itemView.findViewById(R.id.organization)
            organization_role = itemView.findViewById(R.id.organization_role)
            user_type = itemView.findViewById(R.id.user_type)
        }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v: View
        v = LayoutInflater.from(parent.context).inflate(R.layout.esp_lib_repeater_org_list, parent, false)
        return ViewHolder(v)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val personaDAO = persoans[position]


        if (personaDAO.type.toLowerCase().equals("app", ignoreCase = true)) {
            holder.organization_role.text = context.getString(R.string.esp_lib_text_applicant) + " - " + section.name
        } else {
            holder.organization_role.text = context.getString(R.string.esp_lib_text_assessor) + " - " + section.name
        }
        Glide.with(context).load(personaDAO.imagerUrl).placeholder(R.drawable.esp_lib_drawable_default_profile_picture)
                .error(R.drawable.esp_lib_drawable_default_profile_picture).into(holder.user_type)
        holder.organization.text = personaDAO.name


        holder.cardView.setOnClickListener {
            val personaDAO1 = ESP_LIB_PersonaDAO()
            personaDAO1.refresh_token = personas.refresh_token
            personaDAO1.id = personaDAO.id.toString()

            //mUser.StatusChange(personaDAO1)
            ESP_LIB_CustomLogs.displayLogs(TAG + " personaDAO.id: " + personaDAO1.id + " personaDAO.getOrgId(): " + section.id)
            pref.saveLocales(section.supportedLocales)

            pref.savePersonaId(personaDAO1.id!!.toInt())
            pref.saveOrganizationId(section.id)

            if (personaDAO.type?.toLowerCase().equals("app", ignoreCase = true)) {
                pref.saveSelectedUserRole(ESP_LIB_Enums.applicant.toString())
            } else {
                pref.saveSelectedUserRole(ESP_LIB_Enums.assessor.toString())
            }

            postFirebaseToken(personaDAO1.id)

        }


    }//End Holder Class

    private fun postFirebaseToken(personaId: String?) {

        start_loading_animation()


        val firebaseTokenDAO = ESP_LIB_FirebaseTokenDAO()
        firebaseTokenDAO.fbTokenId = pref.firebaseId
        firebaseTokenDAO.personaId = pref.personaId
        firebaseTokenDAO.token = pref.firebaseToken
        firebaseTokenDAO.organizationId = pref.organizationId
        firebaseTokenDAO.deviceId = ESP_LIB_Shared.getInstance().getDeviceId(context)

        try {

            val apis = CompRoot()?.getService(context)
            var firebase_call = apis?.postFirebaseToken(firebaseTokenDAO)

            firebase_call?.enqueue(object : Callback<ESP_LIB_FirebaseTokenDAO> {
                override fun onResponse(call: Call<ESP_LIB_FirebaseTokenDAO>, response: Response<ESP_LIB_FirebaseTokenDAO>?) {
                    stop_loading_animation()
                    if (response?.body() != null) {

                        if (response.body().status) {
                            val fbid = response.body().data as Double
                            pref.saveFirebaseId(fbid.toInt())
                            val personaDAO1 = ESP_LIB_PersonaDAO()
                            personaDAO1.refresh_token = personas.refresh_token
                            personaDAO1.id = personaId.toString()
                            mUser.StatusChange(personaDAO1)
                            ESP_LIB_CustomLogs.displayLogs(TAG + " personaDAO.id: " + "$personaId" + " personaDAO.getOrgId(): " + section.id)
                            pref.saveLocales(section.supportedLocales)
                        } else
                            ESP_LIB_Shared.getInstance().showAlertMessage(context.getString(R.string.esp_lib_text_error), response.body().errorMessage, context)

                    } else
                        ESP_LIB_Shared.getInstance().showAlertMessage(context.getString(R.string.esp_lib_text_error), context.getString(R.string.esp_lib_text_some_thing_went_wrong), context)

                }

                override fun onFailure(call: Call<ESP_LIB_FirebaseTokenDAO>, t: Throwable) {
                    ESP_LIB_CustomLogs.displayLogs("$TAG ${t.printStackTrace()}")
                    stop_loading_animation()
                    ESP_LIB_Shared.getInstance().showAlertMessage(context.getString(R.string.esp_lib_text_error), context.getString(R.string.esp_lib_text_some_thing_went_wrong), context)

                }
            })

        } catch (ex: Exception) {

            ex.printStackTrace()
            stop_loading_animation()
            ESP_LIB_Shared.getInstance().showAlertMessage(context.getString(R.string.esp_lib_text_error), context.getString(R.string.esp_lib_text_some_thing_went_wrong), context)

        }

    }

    private fun start_loading_animation() {

        try {
            if (!pDialog!!.isShowing)
                pDialog!!.show()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun stop_loading_animation() {
        try {
            if (pDialog!!.isShowing)
                pDialog!!.dismiss()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    override fun getItemCount(): Int {
        return persoans.size

    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    private fun RefreshList() {
        notifyDataSetChanged()
    }


}
