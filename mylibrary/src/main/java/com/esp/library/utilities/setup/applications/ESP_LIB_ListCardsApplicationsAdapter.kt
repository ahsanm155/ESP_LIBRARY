package utilities.adapters.setup.applications

import android.app.Activity
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.esp.library.R
import com.esp.library.exceedersesp.ESP_LIB_ESPApplication
import com.esp.library.exceedersesp.controllers.applications.ESP_LIB_ApplicationDetailScreenActivity
import com.esp.library.exceedersesp.controllers.applications.ESP_LIB_ApplicationFeedDetailScreenActivity
import com.esp.library.utilities.common.ESP_LIB_CustomLogs
import com.esp.library.utilities.common.ESP_LIB_Enums
import com.esp.library.utilities.common.ESP_LIB_Shared
import kotlinx.android.synthetic.main.esp_lib_custom_alert_view.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import utilities.adapters.setup.applications.ESP_LIB_ListUsersApplicationsAdapter.Companion.isSubApplications
import utilities.data.applicants.ESP_LIB_ApplicationSingleton
import utilities.data.applicants.ESP_LIB_ApplicationsDAO
import utilities.interfaces.ESP_LIB_AnyClick
import java.util.*

class ESP_LIB_ListCardsApplicationsAdapter(private var mApplications: ArrayList<ESP_LIB_ApplicationsDAO>?, con: Context,
                                           internal var searched_text: String?, subApplications: Boolean) :
        androidx.recyclerview.widget.RecyclerView.Adapter<ESP_LIB_ListCardsApplicationsAdapter.ParentViewHolder>(), Filterable {

    val TAG = javaClass.simpleName

    private var context: Context? = null
    private var isComingFromSeeAll: Boolean = false

    var mApplicationsFiltered: ArrayList<ESP_LIB_ApplicationsDAO>? = null
    open class ParentViewHolder(v: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(v)

    inner class ActivitiesList(v: View) : ParentViewHolder(v) {

        internal var cards: RelativeLayout
        internal var requestnumvalue: TextView
        internal var definitionName: TextView
        internal var rlpendingfor: RelativeLayout? = null
        internal var startedOn: TextView
        internal var startedOntext: TextView
        internal var txtstatus: TextView
        internal var txtrequestedbyvalue: TextView
        internal var txtsubmissionvalue: TextView
        internal var txtfeedvalue: TextView
        internal var ivsign: ImageView
        internal var ivcircledot: ImageView
        internal var pendingfor: TextView? = null
        internal var txtola: TextView
        internal var txtolavalue: TextView
        internal var llOLA: RelativeLayout
        internal var rlstatus: RelativeLayout
        internal var btviewdetails: Button
        internal var btreject: Button
        internal var llcardButtons: LinearLayout
        internal var status_list: androidx.recyclerview.widget.RecyclerView
        internal var voverduedot: View
        internal var viewfeed: View


        init {

            viewfeed = itemView.findViewById(R.id.viewfeed)
            cards = itemView.findViewById(R.id.cards)
            btviewdetails = itemView.findViewById(R.id.btviewdetails)
            btreject = itemView.findViewById(R.id.btreject)
            txtola = itemView.findViewById(R.id.txtola)
            txtolavalue = itemView.findViewById(R.id.txtolavalue)
            txtfeedvalue = itemView.findViewById(R.id.txtfeedvalue)
            requestnumvalue = itemView.findViewById(R.id.requestnumvalue)
            llcardButtons = itemView.findViewById(R.id.llcardButtons)
            definitionName = itemView.findViewById(R.id.definitionName)
            startedOn = itemView.findViewById(R.id.startedOn)
            txtrequestedbyvalue = itemView.findViewById(R.id.txtrequestedbyvalue)
            ivsign = itemView.findViewById(R.id.ivsign)
            ivcircledot = itemView.findViewById(R.id.ivcircledot)
            startedOntext = itemView.findViewById(R.id.startedOntext)
            txtstatus = itemView.findViewById(R.id.txtstatus)
            rlstatus = itemView.findViewById(R.id.rlstatus)
            txtsubmissionvalue = itemView.findViewById(R.id.txtsubmissionvalue)
            llOLA = itemView.findViewById(R.id.llOLA)
            status_list = itemView.findViewById(R.id.status_list)
            voverduedot = itemView.findViewById(R.id.voverduedot)
            status_list.setHasFixedSize(true)
            status_list.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
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
        mApplicationsFiltered = mApplications
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParentViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.esp_lib_list_card_applications_row, parent, false)
        return ActivitiesList(v)
    }


    fun setFeedButtonsVisibility(isComingFromSeesll: Boolean) {
        isComingFromSeeAll = isComingFromSeesll
    }

    fun setInterfaceClickListener(anyClick: ESP_LIB_AnyClick?) {
        clickListener=anyClick
        ESP_LIB_CustomLogs.displayLogs(TAG+" clickListener: "+clickListener)
    }


    fun setSpots(mApplications: ArrayList<ESP_LIB_ApplicationsDAO>) {
        this.mApplicationsFiltered = mApplications
    }

    fun getSpots(): ArrayList<ESP_LIB_ApplicationsDAO> {
        return this.mApplicationsFiltered!!
    }

    override fun onBindViewHolder(holder_parent: ParentViewHolder, position: Int) {


        val holder = holder_parent as ActivitiesList
        val applicationsDAO = mApplicationsFiltered?.get(position)



        if (isComingFromSeeAll)
        {
            holder.llcardButtons.visibility = View.GONE
            holder.viewfeed.visibility = View.GONE
            holder.txtfeedvalue.visibility = View.GONE
            holder.ivcircledot.visibility = View.GONE
        }

        if (applicationsDAO?.type.equals(context?.getString(R.string.esp_lib_text_application), ignoreCase = true)) {
            val category = applicationsDAO?.category
            holder.viewfeed.setBackgroundColor(ContextCompat.getColor(context!!, R.color.esp_lib_color_blue))
            holder.txtfeedvalue.setTextColor(ContextCompat.getColor(context!!, R.color.esp_lib_color_blue))
            holder.txtfeedvalue.text = context?.getString(R.string.esp_lib_text_mine)
            holder.txtsubmissionvalue.text = category
        } else {
            holder.txtola.text=context?.getString(R.string.esp_lib_text_submissionscolon)
            holder.txtolavalue.text= applicationsDAO?.numberOfSubmissions.toString()
            holder.viewfeed.setBackgroundColor(ContextCompat.getColor(context!!, R.color.esp_lib_color_yellowishOrange))
            holder.txtfeedvalue.setTextColor(ContextCompat.getColor(context!!, R.color.esp_lib_color_yellowishOrange))
            holder.txtsubmissionvalue.text = applicationsDAO?.parentDefinitionName
        }


        val definitionName = applicationsDAO?.definitionName
        val applicationNumber = applicationsDAO?.applicationNumber
        var applicationName: String? = ""
        applicationName = when (ESP_LIB_ESPApplication.getInstance()?.user?.loginResponse?.role?.toLowerCase(Locale.getDefault()).equals(ESP_LIB_Enums.applicant.toString(), ignoreCase = true)) {
            true -> ESP_LIB_ESPApplication.getInstance()?.user?.loginResponse?.name
            false -> applicationsDAO?.applicantName
        }

        holder.txtrequestedbyvalue.text = applicationsDAO?.applicantName
        holder.requestnumvalue.text = applicationNumber

        if (applicationsDAO!!.isSigned)
            holder.ivsign.visibility = View.VISIBLE

        /*       if (ESPApplication.getInstance()?.user?.loginResponse?.role?.toLowerCase() != context?.getString(R.string.applicantsmall)
                       && applicationsDAO?.isOverDue!!)
                   holder.voverduedot.visibility = View.VISIBLE*/

        if (searched_text != null && searched_text!!.length > 0) {

            //  holder.category.text = ESP_LIB_Shared.getInstance().getSearchedTextHighlight(searched_text, category, context)
            holder.definitionName.text = ESP_LIB_Shared.getInstance().getSearchedTextHighlight(searched_text, definitionName, context)
            // holder.applicationNumber.text = ESP_LIB_Shared.getInstance().getSearchedTextHighlight(searched_text, applicationNumber, context)

        } else {

            if (isSubApplications) {
                //    holder.categorytext.text = context?.getString(R.string.esp_lib_text_applicantcolon)
                // holder.category.text = applicationName
                holder.definitionName.text = definitionName
                holder.rlpendingfor?.visibility = View.VISIBLE
            } else {

                // holder.category.text = category
                holder.definitionName.text = definitionName
                holder.rlpendingfor?.visibility = View.GONE
            }
            //holder.applicationNumber.text = applicationNumber
        }


        if (applicationsDAO.statusId == 1) // if draft application hide submitted on
        {
            holder.startedOn.visibility = View.GONE
            holder.startedOntext.visibility = View.GONE
        }

        var displayDate = "";

        if (applicationsDAO.startedOn != null && applicationsDAO.startedOn!!.length > 0) {
            displayDate = ESP_LIB_Shared.getInstance().getDisplayDate(context, applicationsDAO.startedOn, true)
            holder.startedOn.text = displayDate
        } /*else {
            displayDate = ESP_LIB_Shared.getInstance().getDisplayDate(context, applicationsDAO.createdOn, true)
            holder.startedOn.text = displayDate
        }*/


        val days = ESP_LIB_Shared.getInstance().fromStringToDate(context, displayDate)


        var daysVal = context?.getString(R.string.esp_lib_text_day)
        if (days > 1)
            daysVal = context?.getString(R.string.esp_lib_text_days)

        holder.pendingfor?.setText(days.toString() + " " + daysVal)



        if (applicationsDAO != null) {
            setStatusColor(holder, applicationsDAO)
        }



        holder.cards.setOnClickListener {
            onCardAction(applicationsDAO)
        }

        holder.btviewdetails.setOnClickListener {
            onCardAction(applicationsDAO)
        }

        holder.btreject.setOnClickListener {
            ESP_LIB_CustomLogs.displayLogs(TAG+" clickListener btreject: "+clickListener)
            clickListener?.onActionPerformed(applicationsDAO,mApplicationsFiltered!!)

        }

        val statusAdapter = ESP_LIB_ApplicationStatusAdapter(applicationsDAO.stageStatuses, context!!);
        holder.status_list.adapter = statusAdapter


    }//End Holder Class


    private fun onCardAction(applicationsDAO: ESP_LIB_ApplicationsDAO) {
        if (ESP_LIB_ESPApplication.getInstance()?.user?.profileStatus == null || ESP_LIB_ESPApplication.getInstance()?.user?.profileStatus == context?.getString(R.string.esp_lib_text_profile_complete)) {
            appDetail(applicationsDAO, false)
        } else if (ESP_LIB_ESPApplication.getInstance()?.user?.profileStatus == context?.getString(R.string.esp_lib_text_profile_incomplete)) {
            ESP_LIB_Shared.getInstance().showAlertProfileMessage(context?.getString(R.string.esp_lib_text_profile_error_heading), context?.getString(R.string.esp_lib_text_profile_error_desc), context)

        } else if (ESP_LIB_ESPApplication.getInstance()?.user?.profileStatus == context?.getString(R.string.esp_lib_text_profile_incomplete_admin)) {
            ESP_LIB_Shared.getInstance().showAlertProfileMessage(context?.getString(R.string.esp_lib_text_profile_error_heading), context?.getString(R.string.esp_lib_text_profile_error_desc_admin), context)
        }
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

            }
            1 // New as draft
            -> {

                holder.txtstatus.setText(R.string.esp_lib_text_draftcaps)
                holder.txtstatus.setTextColor(ContextCompat.getColor(context!!, R.color.esp_lib_color_status_draft))
                drawable.setColor(ContextCompat.getColor(context!!, R.color.esp_lib_color_status_draft_background))

            }
            2 // Pending
            -> {
                holder.txtstatus.setText(R.string.esp_lib_text_pending)
                holder.txtstatus.setTextColor(ContextCompat.getColor(context!!, R.color.esp_lib_color_status_pending))
                drawable.setColor(ContextCompat.getColor(context!!, R.color.esp_lib_color_status_pending_background))

            }
            3 // Accepted
            -> {

                holder.txtstatus.setText(R.string.esp_lib_text_accepted)
                holder.txtstatus.setTextColor(ContextCompat.getColor(context!!, R.color.esp_lib_color_status_accepted))
                holder.ivsign.setImageResource(R.drawable.esp_lib_drawable_ic_icon_green_signed)
                drawable.setColor(ContextCompat.getColor(context!!, R.color.esp_lib_color_status_accepted_background))

            }
            4  // Rejected
            -> {
                holder.txtstatus.setText(R.string.esp_lib_text_rejected)
                holder.ivsign.setImageResource(R.drawable.esp_lib_drawable_ic_icon_red_signed)
                holder.txtstatus.setTextColor(ContextCompat.getColor(context!!, R.color.esp_lib_color_status_rejected))
                drawable.setColor(ContextCompat.getColor(context!!, R.color.esp_lib_color_status_rejected_background))

            }
            5  // Cancelled
            -> {
                holder.txtstatus.setText(R.string.esp_lib_text_cancelled)
                holder.txtstatus.setTextColor(ContextCompat.getColor(context!!, R.color.esp_lib_color_status_draft))
                drawable.setColor(ContextCompat.getColor(context!!, R.color.esp_lib_color_status_draft_background))

            }
            else -> {
                holder.txtstatus.setText(R.string.esp_lib_text_pending)
                holder.txtstatus.setTextColor(ContextCompat.getColor(context!!, R.color.esp_lib_color_status_pending))
                drawable.setColor(ContextCompat.getColor(context!!, R.color.esp_lib_color_status_pending_background))

            }
        }
    }

    private fun appDetail(ESPLIBApplicationsDAO: ESP_LIB_ApplicationsDAO, isResubmit: Boolean) {
        //      ESP_LIB_CustomLogs.displayLogs(LOG_TAG + " mApplications.getStatus(): " + ESPLIBApplicationsDAO.status!!.toLowerCase())

        //    val status = ESPLIBApplicationsDAO.status!!.toLowerCase(Locale.getDefault())
        val statusId = ESPLIBApplicationsDAO.statusId


        if (ESP_LIB_ESPApplication.getInstance()?.user?.loginResponse?.role?.toLowerCase(Locale.getDefault()).equals(ESP_LIB_Enums.applicant.toString(), ignoreCase = true)
                || isSubApplications) {
            val bundle = Bundle()
            bundle.putSerializable(ESP_LIB_ApplicationsDAO.BUNDLE_KEY, ESPLIBApplicationsDAO)
            //  bundle.putString("appStatus", status)
            bundle.putInt("statusId", statusId)
            bundle.putBoolean("isResubmit", isResubmit)
            bundle.putBoolean("isSubApplications", isSubApplications)
            if (ESPLIBApplicationsDAO.type.equals(context?.getString(R.string.esp_lib_text_application), ignoreCase = true))
                ESP_LIB_Shared.getInstance().callIntentWithResult(ESP_LIB_ApplicationDetailScreenActivity::class.java, context as Activity?, bundle, 2)
            else
                ESP_LIB_Shared.getInstance().callIntentWithResult(ESP_LIB_ApplicationFeedDetailScreenActivity::class.java, context as Activity?, bundle, 2)
        } else {

            if (ESP_LIB_ApplicationSingleton.instace.application != null) {
                ESP_LIB_ApplicationSingleton.instace.application = null
            }
            val bundle = Bundle()
            bundle.putSerializable(ESP_LIB_ApplicationsDAO.BUNDLE_KEY, ESPLIBApplicationsDAO)
            //  bundle.putString("appStatus", status)
            bundle.putInt("statusId", statusId)
            bundle.putBoolean("isComingfromAssessor", true)
            if (ESPLIBApplicationsDAO.type.equals(context?.getString(R.string.esp_lib_text_application), ignoreCase = true))
                ESP_LIB_Shared.getInstance().callIntentWithResult(ESP_LIB_ApplicationDetailScreenActivity::class.java, context as Activity?, bundle, 2)
            else
                ESP_LIB_Shared.getInstance().callIntentWithResult(ESP_LIB_ApplicationFeedDetailScreenActivity::class.java, context as Activity?, bundle, 2)
        }
    }

    override fun getItemCount(): Int {
        return mApplicationsFiltered?.size ?: 0
    }


    override fun getItemViewType(position: Int): Int {
        return position
    }


    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                if (charString.isEmpty()) {
                    mApplicationsFiltered = mApplications
                } else {
                    val filteredList = ArrayList<ESP_LIB_ApplicationsDAO>()
                    for (row in mApplications!!) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.definitionName?.toLowerCase()?.contains(charString.toLowerCase())!!) {
                            filteredList.add(row)
                        }
                    }

                    mApplicationsFiltered = filteredList
                }

                val filterResults = FilterResults()
                filterResults.values = mApplicationsFiltered
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                mApplicationsFiltered = filterResults.values as ArrayList<ESP_LIB_ApplicationsDAO>
                notifyDataSetChanged()
            }
        }
    }



    companion object {
        var clickListener: ESP_LIB_AnyClick?=null

    }
}
