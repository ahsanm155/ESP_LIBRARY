/*
package utilities.adapters.setup.applications

import android.app.Activity
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.Handler
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import com.esp.library.R
import com.esp.library.exceedersesp.ESP_LIB_ESPApplication
import com.esp.library.exceedersesp.SingleController.CompRoot
import com.esp.library.exceedersesp.controllers.applications.ESP_LIB_ApplicationDetailScreenActivity
import com.esp.library.exceedersesp.fragments.applications.ESP_LIB_UsersApplicationsFragment
import com.esp.library.utilities.common.ESP_LIB_CustomLogs
import com.esp.library.utilities.common.ESP_LIB_Enums
import com.esp.library.utilities.common.ESP_LIB_Shared
import com.esp.library.utilities.setup.applications.ESP_LIB_ApplicationStatusAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import utilities.data.applicants.ESP_LIB_ApplicationSingleton
import utilities.data.applicants.ESP_LIB_ApplicationsDAO
import utilities.data.applicants.feedback.ESP_LIB_ApplicationsFeedbackDAO
import utilities.interfaces.ESP_LIB_DeleteDraftListener
import java.util.*

class ESP_LIB_ListUsersApplicationsAdapter(private var mApplications: List<ESP_LIB_ApplicationsDAO>?, con: Context,
                                           internal var searched_text: String?, subApplications: Boolean) :
        androidx.recyclerview.widget.RecyclerView.Adapter<ESP_LIB_ListUsersApplicationsAdapter.ParentViewHolder>() {

    private var context: Context? = null
    var popup: PopupMenu? = null
    var ESPLIBDeleteDraftListener: ESP_LIB_DeleteDraftListener? = null


    open class ParentViewHolder(v: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(v)

    inner class ActivitiesList(v: View) : ParentViewHolder(v) {

        internal var cards: RelativeLayout
        internal var rlola: RelativeLayout
        internal var rlcategory: RelativeLayout
        internal var category: TextView
        internal var definitionName: TextView
        internal var txtolavalue: TextView
        internal var rlpendingfor: RelativeLayout? = null
        internal var startedOn: TextView
        internal var startedOntext: TextView
        internal var applicationNumber: TextView
        internal var rlrequestNum: RelativeLayout
        internal var txtstatus: TextView
        internal var reasontext: TextView
        internal var rlreason: RelativeLayout
        internal var rlstatus: RelativeLayout
        internal var ibRemoveCard: ImageButton
        internal var categorytext: TextView
        internal var ivsign: ImageView
        internal var pendingfor: TextView? = null
        internal var status_list: androidx.recyclerview.widget.RecyclerView
        internal var voverduedot: View


        init {

            cards = itemView.findViewById(R.id.cards)
            rlcategory = itemView.findViewById(R.id.rlcategory)
            rlola = itemView.findViewById(R.id.rlola)
            category = itemView.findViewById(R.id.category)
            definitionName = itemView.findViewById(R.id.definitionName)
            startedOn = itemView.findViewById(R.id.startedOn)
            startedOntext = itemView.findViewById(R.id.startedOntext)
            ivsign = itemView.findViewById(R.id.ivsign)
            applicationNumber = itemView.findViewById(R.id.applicationNumber)
            rlrequestNum = itemView.findViewById(R.id.rlrequestNum)
            txtolavalue = itemView.findViewById(R.id.txtolavalue)
            rlstatus = itemView.findViewById(R.id.rlstatus)
            txtstatus = itemView.findViewById(R.id.txtstatus)
            rlreason = itemView.findViewById(R.id.rlreason)
            ibRemoveCard = itemView.findViewById(R.id.ibRemoveCard)
            categorytext = itemView.findViewById(R.id.categorytext)
            reasontext = itemView.findViewById(R.id.reasontext)
            status_list = itemView.findViewById(R.id.status_list)
            voverduedot = itemView.findViewById(R.id.voverduedot)
            status_list.setHasFixedSize(true)
            status_list.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context, androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL, false)
            status_list.setItemAnimator(androidx.recyclerview.widget.DefaultItemAnimator())

            try {
                rlpendingfor = itemView.findViewById(R.id.rlpendingfor)
                pendingfor = itemView.findViewById(R.id.pendingfor)
            } catch (e: java.lang.Exception) {

            }


        }
    }

    init {
        context = con
        isSubApplications = subApplications

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParentViewHolder {
        val v: View
        if (isSubApplications)
            v = LayoutInflater.from(parent.context).inflate(R.layout.esp_lib_list_subapplications_row, parent, false)
        else
            v = LayoutInflater.from(parent.context).inflate(R.layout.esp_lib_list_applications_row, parent, false)
        return ActivitiesList(v)
    }

    fun getPopUpMenu(): PopupMenu? {
        return popup
    }


    fun setSpots(mApplications: List<ESP_LIB_ApplicationsDAO>) {
        this.mApplications = mApplications
    }

    fun getSpots(): List<ESP_LIB_ApplicationsDAO> {
        return this.mApplications!!
    }

    fun getFragmentContext(activity: ESP_LIB_UsersApplicationsFragment) {
        ESPLIBDeleteDraftListener = activity
    }

    override fun onBindViewHolder(holder_parent: ParentViewHolder, position: Int) {


        val holder = holder_parent as ActivitiesList
        val applicationsDAO = mApplications?.get(position)


        applicationsDAO?.mainCardValues?.forEach { mainCard ->
                if (mainCard.label.equals(context?.getString(R.string.esp_lib_text_ola)))
                    applicationsDAO.ola = mainCard.value

            }


        if (!applicationsDAO?.ola.isNullOrEmpty()) {
            holder.rlola.visibility = View.VISIBLE
            holder.txtolavalue.text = applicationsDAO?.ola
        }

        val category = applicationsDAO?.category
        val definitionName = applicationsDAO?.definitionName
        val applicationNumber = applicationsDAO?.applicationNumber
        var applicationName: String? = ""
        applicationName = when (ESP_LIB_ESPApplication.getInstance()?.user?.loginResponse?.role?.toLowerCase(Locale.getDefault()).equals(ESP_LIB_Enums.applicant.toString(), ignoreCase = true)) {
            true -> ESP_LIB_ESPApplication.getInstance()?.user?.loginResponse?.name
            false -> applicationsDAO?.applicantName
        }

        if (applicationsDAO!!.isSigned)
            holder.ivsign.visibility = View.VISIBLE

        */
/*       if (ESPApplication.getInstance()?.user?.loginResponse?.role?.toLowerCase() != context?.getString(R.string.applicantsmall)
                       && applicationsDAO?.isOverDue!!)
                   holder.voverduedot.visibility = View.VISIBLE*//*


        if (searched_text != null && searched_text!!.length > 0) {

            holder.category.text = ESP_LIB_Shared.getInstance().getSearchedTextHighlight(searched_text, category, context)
            holder.definitionName.text = ESP_LIB_Shared.getInstance().getSearchedTextHighlight(searched_text, definitionName, context)
            holder.applicationNumber.text = ESP_LIB_Shared.getInstance().getSearchedTextHighlight(searched_text, applicationNumber, context)

        } else {

            if (isSubApplications) {
                holder.categorytext.text = context?.getString(R.string.esp_lib_text_applicantcolon)
                holder.category.text = applicationName
                holder.definitionName.text = definitionName
                holder.rlpendingfor?.visibility = View.VISIBLE
            } else {
                if (category.isNullOrEmpty())
                    holder.rlcategory.visibility = View.GONE

                holder.category.text = category
                holder.definitionName.text = definitionName
                holder.rlpendingfor?.visibility = View.GONE
            }
            holder.applicationNumber.text = applicationNumber
        }


        var displayDate = "";

        if (applicationsDAO?.submittedOn != null && applicationsDAO.submittedOn!!.length > 0) {
            displayDate = ESP_LIB_Shared.getInstance().getDisplayDate(context, applicationsDAO.submittedOn, true)
            holder.startedOn.text = displayDate
        } else {
            displayDate = ESP_LIB_Shared.getInstance().getDisplayDate(context, applicationsDAO?.createdOn, true)
            holder.startedOn.text = displayDate
        }


        val days = ESP_LIB_Shared.getInstance().fromStringToDate(context, displayDate)


        var daysVal = context?.getString(R.string.esp_lib_text_day)
        if (days > 1)
            daysVal = context?.getString(R.string.esp_lib_text_days)

        holder.pendingfor?.setText(days.toString() + " " + daysVal)

        holder.ibRemoveCard.setOnClickListener { v ->
            if (applicationsDAO != null) {
                ShowMenu(v, applicationsDAO)
            }
        }


        if (applicationsDAO != null) {
            setStatusColor(holder, applicationsDAO)
        }



        holder.cards.setOnClickListener {
            holder.cards.isEnabled = false

            if (ESP_LIB_ESPApplication.getInstance()?.user?.profileStatus == null || ESP_LIB_ESPApplication.getInstance()?.user?.profileStatus == context?.getString(R.string.esp_lib_text_profile_complete)) {
                if (applicationsDAO != null) {
                    appDetail(applicationsDAO, false)
                }
            } else if (ESP_LIB_ESPApplication.getInstance()?.user?.profileStatus == context?.getString(R.string.esp_lib_text_profile_incomplete)) {
                ESP_LIB_Shared.getInstance().showAlertProfileMessage(context?.getString(R.string.esp_lib_text_profile_error_heading), context?.getString(R.string.esp_lib_text_profile_error_desc), context)

            } else if (ESP_LIB_ESPApplication.getInstance()?.user?.profileStatus == context?.getString(R.string.esp_lib_text_profile_incomplete_admin)) {
                ESP_LIB_Shared.getInstance().showAlertProfileMessage(context?.getString(R.string.esp_lib_text_profile_error_heading), context?.getString(R.string.esp_lib_text_profile_error_desc_admin), context)
            }

            val handler = Handler()
            handler.postDelayed({ holder.cards.isEnabled = true }, 2000)

        }

        val statusAdapter = ESP_LIB_ApplicationStatusAdapter(applicationsDAO?.stageStatuses, context!!);
        holder.status_list.adapter = statusAdapter

        if (applicationsDAO?.statusId == 4) {
            holder.rlreason.visibility = View.VISIBLE
            */
/*val lp = holder.txtstatus.getLayoutParams() as RelativeLayout.LayoutParams
            lp.addRule(RelativeLayout.BELOW, holder.reasontext.getId());
            holder.txtstatus.setLayoutParams(lp);*//*

            GetApplicationFeedBack(applicationsDAO.id.toString(), holder)
        } else
            holder.rlreason.visibility = View.GONE

        if (applicationsDAO?.statusId == 1) // if draft application hide submitted on and align status for this purpose swap submitted on with request # and hide request #
        {
            holder.startedOntext.text = context?.getString(R.string.esp_lib_text_requestnumber)
            holder.startedOn.text = holder.applicationNumber.text
            holder.rlrequestNum.visibility = View.GONE
        }


    }//End Holder Class

    private fun ShowMenu(v: View, ESPLIBApplicationsDAO: ESP_LIB_ApplicationsDAO) {

        val POPUP_CONSTANT = "mPopup"
        val POPUP_FORCE_SHOW_ICON = "setForceShowIcon"

        popup = PopupMenu(context!!, v)
        popup?.inflate(R.menu.menu_user_list_actions);
        val menuOpts = popup?.menu

        popup?.gravity = Gravity.CENTER

        try {
            // Reflection apis to enforce show icon
            val fields = popup!!.javaClass.declaredFields
            for (field in fields) {
                if (field.name == POPUP_CONSTANT) {
                    field.isAccessible = true
                    val menuPopupHelper = field.get(popup)
                    val classPopupHelper = Class.forName(menuPopupHelper.javaClass.name)
                    val setForceIcons = classPopupHelper.getMethod(POPUP_FORCE_SHOW_ICON, Boolean::class.javaPrimitiveType!!)
                    setForceIcons.invoke(menuPopupHelper, true)
                    break
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }


        val action_resubmit = menuOpts?.findItem(R.id.action_resubmit)
        val action_delete_request = menuOpts?.findItem(R.id.action_delete_request)

        if (ESPLIBApplicationsDAO.statusId == 1) {
            action_delete_request?.isEnabled = true
            action_resubmit?.isEnabled = false
        } else {
            action_delete_request?.isEnabled = false
            action_resubmit?.isEnabled = true
        }


        popup?.setOnMenuItemClickListener { item ->
            val id = item.itemId
            if (id == R.id.action_resubmit) {
                appDetail(ESPLIBApplicationsDAO, true)
            } else if (id == R.id.action_delete_request) {
                ESPLIBDeleteDraftListener?.deletedraftApplication(ESPLIBApplicationsDAO)
            }
            false
        }
        popup?.show()


    }


    private fun setStatusColor(holder: ActivitiesList, ESPLIBApplicationsDAO: ESP_LIB_ApplicationsDAO) {
        val getStatusId = ESPLIBApplicationsDAO.statusId
        holder.rlstatus.setBackgroundResource(R.drawable.esp_lib_drawable_status_background)
        val drawable = holder.rlstatus.getBackground() as GradientDrawable
        when (getStatusId) {
            0 // Invited
            -> {

                holder.txtstatus.setText(R.string.esp_lib_text_invited)
                holder.txtstatus.setTextColor(ContextCompat.getColor(context!!, R.color.esp_lib_color_status_invited))
                drawable.setColor(ContextCompat.getColor(context!!, R.color.esp_lib_color_status_invited_background))
                holder.ibRemoveCard.visibility = View.GONE
            }
            1 // New as draft
            -> {
                holder.txtstatus.setText(R.string.esp_lib_text_draftcaps)
                holder.txtstatus.setTextColor(ContextCompat.getColor(context!!, R.color.esp_lib_color_status_draft))
                drawable.setColor(ContextCompat.getColor(context!!, R.color.esp_lib_color_status_draft_background))
                if (ESP_LIB_ESPApplication.getInstance().isComponent || ESP_LIB_ESPApplication.getInstance()?.user?.loginResponse?.role?.toLowerCase(Locale.getDefault()).equals(ESP_LIB_Enums.applicant.toString(), ignoreCase = true))
                    holder.ibRemoveCard.visibility = View.VISIBLE
                else
                    holder.ibRemoveCard.visibility = View.GONE
            }
            2 // Pending
            -> {
                holder.txtstatus.setText(R.string.esp_lib_text_pending)
                holder.txtstatus.setTextColor(ContextCompat.getColor(context!!, R.color.esp_lib_color_status_pending))
                drawable.setColor(ContextCompat.getColor(context!!, R.color.esp_lib_color_status_pending_background))
                holder.ibRemoveCard.visibility = View.GONE
            }
            3 // Accepted
            -> {

                holder.txtstatus.setText(R.string.esp_lib_text_accepted)
                holder.ivsign.setImageResource(R.drawable.esp_lib_drawable_ic_icon_green_signed)
                holder.txtstatus.setTextColor(ContextCompat.getColor(context!!, R.color.esp_lib_color_status_accepted))
                drawable.setColor(ContextCompat.getColor(context!!, R.color.esp_lib_color_status_accepted_background))
                holder.ibRemoveCard.visibility = View.GONE
            }
            4  // Rejected
            -> {
                holder.txtstatus.setText(R.string.esp_lib_text_rejected)
                holder.ivsign.setImageResource(R.drawable.esp_lib_drawable_ic_icon_red_signed)
                holder.txtstatus.setTextColor(ContextCompat.getColor(context!!, R.color.esp_lib_color_status_rejected))
                drawable.setColor(ContextCompat.getColor(context!!, R.color.esp_lib_color_status_rejected_background))
                if (ESP_LIB_ESPApplication.getInstance().isComponent || ESP_LIB_ESPApplication.getInstance()?.user?.loginResponse?.role?.toLowerCase(Locale.getDefault()).equals(ESP_LIB_Enums.applicant.toString(), ignoreCase = true))
                    holder.ibRemoveCard.visibility = View.VISIBLE
                else
                    holder.ibRemoveCard.visibility = View.GONE
            }
            5  // Cancelled
            -> {
                holder.txtstatus.setText(R.string.esp_lib_text_cancelled)
                holder.txtstatus.setTextColor(ContextCompat.getColor(context!!, R.color.esp_lib_color_status_draft))
                drawable.setColor(ContextCompat.getColor(context!!, R.color.esp_lib_color_status_draft_background))
                holder.ibRemoveCard.visibility = View.GONE
            }
            else -> {
                holder.txtstatus.setText(R.string.esp_lib_text_pending)
                holder.txtstatus.setTextColor(ContextCompat.getColor(context!!, R.color.esp_lib_color_status_pending))
                drawable.setColor(ContextCompat.getColor(context!!, R.color.esp_lib_color_status_pending_background))
                holder.ibRemoveCard.visibility = View.GONE
            }
        }
    }

    private fun appDetail(ESPLIBApplicationsDAO: ESP_LIB_ApplicationsDAO, isResubmit: Boolean) {
        ESP_LIB_CustomLogs.displayLogs(LOG_TAG + " mApplications.getStatus(): " + ESPLIBApplicationsDAO.status!!.toLowerCase(Locale.getDefault()))

        val status = ESPLIBApplicationsDAO.status!!.toLowerCase(Locale.getDefault())
        val statusId = ESPLIBApplicationsDAO.statusId


        if (ESP_LIB_ESPApplication.getInstance().isComponent || ESP_LIB_ESPApplication.getInstance()?.user?.loginResponse?.role?.toLowerCase(Locale.getDefault()).equals(ESP_LIB_Enums.applicant.toString(), ignoreCase = true)
                || isSubApplications) {
            val bundle = Bundle()
            bundle.putSerializable(ESP_LIB_ApplicationsDAO.BUNDLE_KEY, ESPLIBApplicationsDAO)
            bundle.putString("appStatus", status)
            bundle.putInt("statusId", statusId)
            bundle.putBoolean("isResubmit", isResubmit)
            bundle.putBoolean("isSubApplications", isSubApplications)
            ESP_LIB_Shared.getInstance().callIntentWithResult(ESP_LIB_ApplicationDetailScreenActivity::class.java, context as Activity?, bundle, 2)
        } else {

            if (ESP_LIB_ApplicationSingleton.instace.application != null) {
                ESP_LIB_ApplicationSingleton.instace.application = null
            }
            val bundle = Bundle()
            bundle.putSerializable(ESP_LIB_ApplicationsDAO.BUNDLE_KEY, ESPLIBApplicationsDAO)
            bundle.putString("appStatus", status)
            bundle.putInt("statusId", statusId)
            bundle.putBoolean("isComingfromAssessor", true)
            ESP_LIB_Shared.getInstance().callIntentWithResult(ESP_LIB_ApplicationDetailScreenActivity::class.java, context as Activity?, bundle, 2)
        }
    }

    override fun getItemCount(): Int {
        return mApplications?.size ?: 0
    }


    override fun getItemViewType(position: Int): Int {
        return position
    }

    fun GetApplicationFeedBack(id: String, holder: ActivitiesList) {

        val apis = CompRoot().getService(context);

        val detail_call = apis?.GetApplicationFeedBack(id)
        detail_call?.enqueue(object : Callback<List<ESP_LIB_ApplicationsFeedbackDAO>> {
            override fun onResponse(call: Call<List<ESP_LIB_ApplicationsFeedbackDAO>>, response: Response<List<ESP_LIB_ApplicationsFeedbackDAO>>?) {

                if (response?.body() != null && response.body().size > 0) {
                    for (i in 0 until response.body().size) {
                        val comment = response.body()[i].comment
                        val reasonTextConcate = context?.getString(R.string.esp_lib_text_reasonfordecline) + " " + comment
                        val wordtoSpan: Spannable = SpannableString(reasonTextConcate)
                        wordtoSpan.setSpan(ForegroundColorSpan(ContextCompat.getColor(context!!, R.color.esp_lib_color_coolgrey)), 0, 22, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        wordtoSpan.setSpan(ForegroundColorSpan(ContextCompat.getColor(context!!, R.color.esp_lib_color_black)), 23, reasonTextConcate.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                        if (comment.isNullOrEmpty())
                            holder.rlreason.visibility = View.GONE
                        else
                            holder.reasontext.text = wordtoSpan;


                    }


                }
            }

            override fun onFailure(call: Call<List<ESP_LIB_ApplicationsFeedbackDAO>>, t: Throwable) {


                // Shared.getInstance().showAlertMessage(pref.getlabels().getApplication(), getString(R.string.some_thing_went_wrong), bContext);

            }
        })


    }


    companion object {

        private val LOG_TAG = "ListUsersApplicationsAdapter"
        var isSubApplications: Boolean = false;

    }
}
*/
