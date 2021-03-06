package utilities.adapters.setup.applications

import android.app.Activity
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.Handler
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
import com.esp.library.exceedersesp.controllers.applications.ESP_LIB_ApplicationDetailScreenActivity
import com.esp.library.utilities.common.ESP_LIB_Enums
import com.esp.library.utilities.common.ESP_LIB_Shared
import com.esp.library.utilities.setup.applications.ESP_LIB_ApplicationStatusAdapter
import utilities.data.applicants.ESP_LIB_ApplicationSingleton
import utilities.data.applicants.ESP_LIB_ApplicationsDAO
import utilities.interfaces.ESP_LIB_DeleteDraftListener
import java.util.*

class ESP_LIB_ListSubmissionApplicationsAdapter(private var mApplications: MutableList<ESP_LIB_ApplicationsDAO>, con: Context,
                                                internal var searched_text: String?,
                                                val isSubApplications: Boolean) :
        androidx.recyclerview.widget.RecyclerView.Adapter<ESP_LIB_ListSubmissionApplicationsAdapter.ParentViewHolder>() {

    private var context: Context? = null
    private var isClickable: Boolean = true
    var popup: PopupMenu? = null
    var ESPLIBDeleteDraftListener: ESP_LIB_DeleteDraftListener? = null


    open class ParentViewHolder(v: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(v)

    inner class ActivitiesList(v: View) : ParentViewHolder(v) {

        internal var cards: RelativeLayout
        internal var definitionName: TextView
        internal var submittedOn: TextView
        internal var txtrequestnumber: TextView
        internal var txtstatus: TextView
        internal var rlstatus: RelativeLayout
        internal var ibRemoveCard: ImageButton
        internal var ivsign: ImageView
        internal var status_list: androidx.recyclerview.widget.RecyclerView


        init {

            cards = itemView.findViewById(R.id.cards)
            definitionName = itemView.findViewById(R.id.definitionName)
            submittedOn = itemView.findViewById(R.id.submittedOn)
            txtrequestnumber = itemView.findViewById(R.id.txtrequestnumber)
            ivsign = itemView.findViewById(R.id.ivsign)
            rlstatus = itemView.findViewById(R.id.rlstatus)
            txtstatus = itemView.findViewById(R.id.txtstatus)
            ibRemoveCard = itemView.findViewById(R.id.ibRemoveCard)
            status_list = itemView.findViewById(R.id.status_list)
            status_list.setHasFixedSize(true)
            status_list.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context, androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL, false)
            status_list.itemAnimator = androidx.recyclerview.widget.DefaultItemAnimator()


        }
    }

    init {
        context = con

    }

    fun isClickable(isclickable: Boolean) {
        isClickable = isclickable
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParentViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.esp_lib_list_submissions_applications_row, parent, false)
        return ActivitiesList(v)
    }


    override fun onBindViewHolder(holder_parent: ParentViewHolder, position: Int) {


        val holder = holder_parent as ActivitiesList
        val applicationsDAO = mApplications.get(position)



        val definitionName = applicationsDAO.definitionName
       /*
       val category = applicationsDAO?.category
       val applicationNumber = applicationsDAO?.applicationNumber
        var applicationName: String? = ""
        applicationName = when (ESP_LIB_ESPApplication.getInstance()?.user?.loginResponse?.role?.toLowerCase(Locale.getDefault()).equals(ESP_LIB_Enums.applicant.toString(), ignoreCase = true)) {
            true -> ESP_LIB_ESPApplication.getInstance()?.user?.loginResponse?.name
            false -> applicationsDAO?.applicantName
        }*/

        if (applicationsDAO.isSigned)
            holder.ivsign.visibility = View.VISIBLE

        /*       if (ESPApplication.getInstance()?.user?.loginResponse?.role?.toLowerCase() != context?.getString(R.string.applicantsmall)
                       && applicationsDAO?.isOverDue!!)
                   holder.voverduedot.visibility = View.VISIBLE*/

        if (searched_text != null && searched_text!!.length > 0) {
            holder.definitionName.text = ESP_LIB_Shared.getInstance().getSearchedTextHighlight(searched_text, definitionName, context)

        } else {
            holder.definitionName.text = definitionName
        }


        var displayDate = "";

        if (applicationsDAO.startedOn != null && applicationsDAO.startedOn!!.length > 0) {
            displayDate = ESP_LIB_Shared.getInstance().getDisplayDate(context, applicationsDAO.startedOn, true)
            holder.submittedOn.text = displayDate
        } else {
            displayDate = ESP_LIB_Shared.getInstance().getDisplayDate(context, applicationsDAO.submittedOn, true)
            holder.submittedOn.text = displayDate
        }

        holder.txtrequestnumber.text=applicationsDAO.applicationNumber



        holder.ibRemoveCard.setOnClickListener { v ->
            if (applicationsDAO != null) {
                ShowMenu(v, applicationsDAO)
            }
        }


        if (applicationsDAO != null) {
            setStatusColor(holder, applicationsDAO)
        }



        holder.cards.setOnClickListener {
          //  if (isClickable) {
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
           // }
        }

        val statusAdapter = ESP_LIB_ApplicationStatusAdapter(applicationsDAO.stageStatuses, context!!);
        holder.status_list.adapter = statusAdapter


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
                holder.txtstatus.setText(R.string.esp_lib_text_opencaps)
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
                holder.txtstatus.setText(R.string.esp_lib_text_opencaps)
                holder.txtstatus.setTextColor(ContextCompat.getColor(context!!, R.color.esp_lib_color_status_pending))
                drawable.setColor(ContextCompat.getColor(context!!, R.color.esp_lib_color_status_pending_background))
                holder.ibRemoveCard.visibility = View.GONE
            }
        }
    }

    private fun appDetail(ESPLIBApplicationsDAO: ESP_LIB_ApplicationsDAO, isResubmit: Boolean) {

        val statusId = ESPLIBApplicationsDAO.statusId


        if (ESP_LIB_ESPApplication.getInstance().isComponent || ESP_LIB_ESPApplication.getInstance()?.user?.loginResponse?.role?.toLowerCase(Locale.getDefault()).equals(ESP_LIB_Enums.applicant.toString(), ignoreCase = true)) {
            val bundle = Bundle()
            bundle.putSerializable(ESP_LIB_ApplicationsDAO.BUNDLE_KEY, ESPLIBApplicationsDAO)
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
            bundle.putInt("statusId", statusId)
            bundle.putBoolean("isComingfromAssessor", true)
            bundle.putBoolean("isResubmit", isResubmit)
            bundle.putBoolean("isSubApplications", isSubApplications)
            ESP_LIB_Shared.getInstance().callIntentWithResult(ESP_LIB_ApplicationDetailScreenActivity::class.java, context as Activity?, bundle, 2)
        }
    }

    override fun getItemCount(): Int {
        return mApplications?.size ?: 0
    }


    override fun getItemViewType(position: Int): Int {
        return position
    }

    companion object {

        private val LOG_TAG = "ListUsersApplicationsAdapter"

    }
}
