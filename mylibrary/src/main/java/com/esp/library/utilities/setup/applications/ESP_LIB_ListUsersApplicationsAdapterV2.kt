package com.esp.library.utilities.setup.applications

import android.app.Activity
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.PopupMenu
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.forEachIndexed
import com.esp.library.R
import com.esp.library.exceedersesp.ESP_LIB_ESPApplication
import com.esp.library.exceedersesp.controllers.applications.ESP_LIB_ApplicationDetailScreenActivity
import com.esp.library.exceedersesp.fragments.applications.ESP_LIB_UsersApplicationsFragment
import com.esp.library.utilities.common.ESP_LIB_CustomLogs
import com.esp.library.utilities.common.ESP_LIB_Enums
import com.esp.library.utilities.common.ESP_LIB_Shared
import utilities.data.applicants.ESP_LIB_ApplicationSingleton
import utilities.data.applicants.ESP_LIB_ApplicationsDAO
import utilities.interfaces.ESP_LIB_DeleteDraftListener
import java.util.*

class ESP_LIB_ListUsersApplicationsAdapterV2(private var mApplications: List<ESP_LIB_ApplicationsDAO>?, con: Context,
                                             internal var searched_text: String?, subApplications: Boolean) :
        androidx.recyclerview.widget.RecyclerView.Adapter<ESP_LIB_ListUsersApplicationsAdapterV2.ParentViewHolder>() {

    private var context: Context? = null
    var popup: PopupMenu? = null
    var ESPLIBDeleteDraftListener: ESP_LIB_DeleteDraftListener? = null


    open class ParentViewHolder(v: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(v)

    inner class ActivitiesList(v: View) : ParentViewHolder(v) {

        internal var status_list: androidx.recyclerview.widget.RecyclerView
        internal var items_list: androidx.recyclerview.widget.RecyclerView
        internal var rlstatus: RelativeLayout
        internal var rlfeedminerow: RelativeLayout
        internal var cards: CardView
        internal var ibRemoveCard: ImageButton
        internal var txtstatus: TextView
        internal var txtfeedminelabel: TextView
        internal var ivcircledot: ImageView
        internal var definitionName: TextView
        internal var txtfeedminevalue: TextView
        internal var ivsign: ImageView

        init {

            ivsign = itemView.findViewById(R.id.ivsign)
            cards = itemView.findViewById(R.id.cards)
            txtstatus = itemView.findViewById(R.id.txtstatus)
            rlstatus = itemView.findViewById(R.id.rlstatus)
            txtfeedminelabel = itemView.findViewById(R.id.txtfeedminelabel)
            ivcircledot = itemView.findViewById(R.id.ivcircledot)
            rlfeedminerow = itemView.findViewById(R.id.rlfeedminerow)
            ibRemoveCard = itemView.findViewById(R.id.ibRemoveCard)
            definitionName = itemView.findViewById(R.id.definitionName)
            txtfeedminevalue = itemView.findViewById(R.id.txtfeedminevalue)

            status_list = itemView.findViewById(R.id.status_list)
            status_list.setHasFixedSize(true)
            status_list.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context, androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL, false)
            status_list.itemAnimator = androidx.recyclerview.widget.DefaultItemAnimator()

            items_list = itemView.findViewById(R.id.items_list)
            items_list.setHasFixedSize(true)
            items_list.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context, androidx.recyclerview.widget.LinearLayoutManager.VERTICAL, false)
            items_list.itemAnimator = androidx.recyclerview.widget.DefaultItemAnimator()

        }
    }

    init {
        context = con
        isSubApplications = subApplications
    }

    fun getPopUpMenu(): PopupMenu? {
        return popup
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParentViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.esp_lib_activity_dynamic_list_applications_row, parent, false)
        return ActivitiesList(v)
    }


    fun getFragmentContext(activity: ESP_LIB_UsersApplicationsFragment) {
        ESPLIBDeleteDraftListener = activity
    }

    override fun onBindViewHolder(holder_parent: ParentViewHolder, position: Int) {


        val holder = holder_parent as ActivitiesList
        val applicationsDAO = mApplications?.get(position)

        val definitionName = applicationsDAO?.definitionName
        holder.definitionName.setPadding(0, 30, 0, 0)
        holder.definitionName.text = definitionName

        if (applicationsDAO!!.isSigned)
            holder.ivsign.visibility = View.VISIBLE

        val statusAdapter = ESP_LIB_ApplicationStatusAdapter(applicationsDAO.stageStatuses, context!!)
        holder.status_list.adapter = statusAdapter

        val itemsAdapter = ESP_LIB_ApplicationItemsAdapter(applicationsDAO.summary?.cardValues, context!!)
        holder.items_list.adapter = itemsAdapter
        holder.items_list.suppressLayout(true) // disbale recycler item click
        holder.ibRemoveCard.setOnClickListener { v -> ShowMenu(v, applicationsDAO) }



        setStatusColor(holder, applicationsDAO)



        holder.cards.setOnClickListener {
            //holder.cards.isEnabled = false

            if (ESP_LIB_ESPApplication.getInstance()?.user?.profileStatus == null || ESP_LIB_ESPApplication.getInstance()?.user?.profileStatus == context?.getString(R.string.esp_lib_text_profile_complete)) {
                appDetail(applicationsDAO, false)
            } else if (ESP_LIB_ESPApplication.getInstance()?.user?.profileStatus == context?.getString(R.string.esp_lib_text_profile_incomplete)) {
                ESP_LIB_Shared.getInstance().showAlertProfileMessage(context?.getString(R.string.esp_lib_text_profile_error_heading), context?.getString(R.string.esp_lib_text_profile_error_desc), context)
            } else if (ESP_LIB_ESPApplication.getInstance()?.user?.profileStatus == context?.getString(R.string.esp_lib_text_profile_incomplete_admin)) {
                ESP_LIB_Shared.getInstance().showAlertProfileMessage(context?.getString(R.string.esp_lib_text_profile_error_heading), context?.getString(R.string.esp_lib_text_profile_error_desc_admin), context)
            }

            /* val handler = Handler()
             handler.postDelayed({ holder.cards.isEnabled = true }, 2000)*/

        }

        if (applicationsDAO.summary?.isFeed!!) {
            holder.rlfeedminerow.visibility = View.VISIBLE
            holder.txtfeedminevalue.setText(applicationsDAO.summary?.title)
            holder.rlfeedminerow.setPadding(0, 30, 0, 0)
        }
        else if(!applicationsDAO.summary?.isFeed!!)
        {
            if(!applicationsDAO.summary?.title.isNullOrEmpty())
            {
                holder.txtfeedminelabel.visibility=View.GONE
                holder.ivcircledot.visibility=View.GONE
                holder.rlfeedminerow.visibility = View.VISIBLE
                holder.txtfeedminevalue.setText(applicationsDAO.summary?.title)
                holder.rlfeedminerow.setPadding(0, 30, 0, 0)
            }

        }


    }//End Holder Class

    private fun ShowMenu(v: View, ESPLIBApplicationsDAO: ESP_LIB_ApplicationsDAO) {

        val POPUP_CONSTANT = "mPopup"
        val POPUP_FORCE_SHOW_ICON = "setForceShowIcon"

        popup = PopupMenu(context!!, v)
        popup?.inflate(R.menu.menu_user_list_actions)
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
        val drawable = holder.rlstatus.background as GradientDrawable
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

    companion object {
        var isSubApplications: Boolean = false
        private val LOG_TAG = "ListUsersApplicationsAdapterV2"

    }
}
