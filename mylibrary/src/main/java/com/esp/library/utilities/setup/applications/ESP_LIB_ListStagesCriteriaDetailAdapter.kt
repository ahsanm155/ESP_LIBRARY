package utilities.adapters.setup.applications

import android.os.Bundle
import androidx.appcompat.widget.PopupMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.esp.library.R
import com.esp.library.utilities.common.ESP_LIB_CustomLogs
import com.esp.library.utilities.common.ESP_LIB_Shared
import com.esp.library.exceedersesp.ESP_LIB_BaseActivity
import com.esp.library.exceedersesp.controllers.applications.ESP_LIB_AssessorApplicationStagesCeriteriaCommentsScreenActivity
import utilities.data.applicants.dynamics.ESP_LIB_DynamicStagesCriteriaListDAO
import utilities.data.applicants.dynamics.ESP_LIB_DynamicStagesDAO


class ESP_LIB_ListStagesCriteriaDetailAdapter(private val mCriteriaESPLIBS: List<ESP_LIB_DynamicStagesCriteriaListDAO>?, mstage: ESP_LIB_DynamicStagesDAO?, con: ESP_LIB_BaseActivity, internal var searched_text: String) : androidx.recyclerview.widget.RecyclerView.Adapter<ESP_LIB_ListStagesCriteriaDetailAdapter.ParentViewHolder>() {

    internal var mstatus: CriteriaStatusChange? = null
    private var context: ESP_LIB_BaseActivity
    var mStage: ESP_LIB_DynamicStagesDAO? = null;

    interface CriteriaStatusChange {
        fun StatusChange(update: ESP_LIB_DynamicStagesCriteriaListDAO, isAccepted: Boolean)
    }

    open class ParentViewHolder(v: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(v)

    inner class ActivitiesList(v: View) : ParentViewHolder(v) {

        internal var cards: LinearLayout
        internal var status_dot: RelativeLayout
        internal var criteria_name: TextView
        internal var days: TextView
        internal var criteria_owner: TextView
        internal var status: TextView
        internal var add_criteria_comments: ImageButton
        internal var add_criteria_action: ImageButton


        init {

            cards = itemView.findViewById(R.id.cards)
            status_dot = itemView.findViewById(R.id.status_dot)
            criteria_name = itemView.findViewById(R.id.criteria_name)
            status = itemView.findViewById(R.id.status)
            days = itemView.findViewById(R.id.days)
            criteria_owner = itemView.findViewById(R.id.criteria_owner)
            add_criteria_comments = itemView.findViewById(R.id.add_criteria_comments)
            add_criteria_action = itemView.findViewById(R.id.add_criteria_action)
        }

    }


    init {
        context = con
        mStage = mstage

        try {
            mstatus = context as CriteriaStatusChange
        } catch (e: ClassCastException) {
            throw ClassCastException("lisnter" + " must implement on Activity")
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParentViewHolder {
        val v: View
        v = LayoutInflater.from(parent.context).inflate(R.layout.esp_lib_repeater_application_stages_criteria, parent, false)
        return ActivitiesList(v)
    }


    override fun onBindViewHolder(holder_parent: ParentViewHolder, position: Int) {

        val holder = holder_parent as ActivitiesList

        holder.criteria_name.text = mCriteriaESPLIBS!![position].name

        var noDays = " " + context.getString(R.string.esp_lib_text_day)
        if (mCriteriaESPLIBS[position].daysToComplete <= 1) {
            noDays = " " + context.getString(R.string.esp_lib_text_day)
        } else if (mCriteriaESPLIBS[position].daysToComplete > 1) {
            noDays = " " + context.getString(R.string.esp_lib_text_day)
        }
        holder.days.text = mCriteriaESPLIBS[position].daysToComplete.toString() + noDays
        try {
            val manager_name = ESP_LIB_Shared.getInstance().toSubStr(mCriteriaESPLIBS[position].ownerName, 20)
            holder.criteria_owner.text = manager_name

        } catch (e: Exception) {

        }
        ESP_LIB_CustomLogs.displayLogs("mStage?.status: " + mStage?.status)


        if (mCriteriaESPLIBS[position].assessmentStatus != null && mCriteriaESPLIBS[position].assessmentStatus!!.length > 0) {
            holder.status.visibility = View.VISIBLE
            if (mCriteriaESPLIBS[position].assessmentStatus!!.toLowerCase().equals(context.getString(R.string.esp_lib_text_active), ignoreCase = true) || mCriteriaESPLIBS[position].assessmentStatus!!.toLowerCase().equals(context.getString(R.string.esp_lib_text_neww), ignoreCase = true)) {
                holder.status.setText(R.string.esp_lib_text_pending)

                holder.status_dot.setBackgroundDrawable(context.resources.getDrawable(R.drawable.esp_lib_drawable_draw_bg_pending))

                if (mCriteriaESPLIBS[position].isOwner) {
                    holder.status.visibility = View.GONE
                    holder.add_criteria_action.visibility = View.VISIBLE
                    holder.add_criteria_action.setOnClickListener { view -> ShowMenu(view, mCriteriaESPLIBS[position]) }
                }


            } else if (mCriteriaESPLIBS[position].assessmentStatus!!.toLowerCase().equals(context.getString(R.string.esp_lib_text_rejectedsmall), ignoreCase = true)) {
                holder.status.setText(R.string.esp_lib_text_rejected)
                holder.status_dot.background = context.resources.getDrawable(R.drawable.esp_lib_drawable_draw_bg_rejected)
            } else if (mCriteriaESPLIBS[position].assessmentStatus!!.toLowerCase().equals(context.getString(R.string.esp_lib_text_acceptedsmall), ignoreCase = true)) {
                holder.status.setText(R.string.esp_lib_text_accepted)
                holder.status_dot.background = context.resources.getDrawable(R.drawable.esp_lib_drawable_draw_bg_accepted)
            } else {
                holder.status_dot.background = context.resources.getDrawable(R.drawable.esp_lib_drawable_draw_bg_pending)
                holder.status.visibility = View.GONE
            }

        } else {
            holder.status.visibility = View.GONE
        }




        if (mCriteriaESPLIBS[position].comments != null && mCriteriaESPLIBS[position].comments!!.size > 0) {
            holder.add_criteria_comments.setImageDrawable(context.resources.getDrawable(R.drawable.ic_card_commented))
        } else {
            holder.add_criteria_comments.setImageDrawable(context.resources.getDrawable(R.drawable.ic_card_not_commented))
        }

        holder.add_criteria_comments.setOnClickListener {
            val bundle = Bundle()
            bundle.putSerializable(ESP_LIB_DynamicStagesCriteriaListDAO.BUNDLE_KEY, mCriteriaESPLIBS[position])
            ESP_LIB_Shared.getInstance().callIntentWithResult(ESP_LIB_AssessorApplicationStagesCeriteriaCommentsScreenActivity::class.java, context, bundle, 2)
        }

        if (mStage?.status.equals(context.getString(R.string.esp_lib_text_locked), ignoreCase = true)) {
            holder.add_criteria_action.visibility = View.INVISIBLE
            holder.add_criteria_comments.visibility = View.INVISIBLE
        }

    }//End Holder Class


    override fun getItemCount(): Int {
        return mCriteriaESPLIBS?.size ?: 0

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

    private fun ShowMenu(v: View, criteriaESPLIB: ESP_LIB_DynamicStagesCriteriaListDAO) {
        val popup = PopupMenu(context, v)
        popup.inflate(R.menu.menu_status);
        val menuOpts = popup.menu
        val findItem_accept = menuOpts.findItem(R.id.action_accept)
        val findItem_reject = menuOpts.findItem(R.id.action_reject)

        if (!criteriaESPLIB.approveText.isNullOrEmpty())
            findItem_accept.title = criteriaESPLIB.approveText

        if (!criteriaESPLIB.rejectText.isNullOrEmpty())
            findItem_reject.title = criteriaESPLIB.rejectText


        popup.setOnMenuItemClickListener { item ->
            val id = item.itemId
            if (id == R.id.action_accept) {
                if (mstatus != null) {
                    mstatus!!.StatusChange(criteriaESPLIB, true)
                }
            } else if (id == R.id.action_reject) {
                mstatus!!.StatusChange(criteriaESPLIB, false)
            }
            false
        }
        // popup.inflate(R.menu.menu_status)
        popup.show()

    }

    companion object {

        private val LOG_TAG = "ListStagesCriteriaDetailAdapter"


    }
}
