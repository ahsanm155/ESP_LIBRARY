package utilities.adapters.setup.applications

import android.os.Bundle
import androidx.core.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.esp.library.R
import com.esp.library.utilities.common.ESP_LIB_Shared

import com.esp.library.exceedersesp.ESP_LIB_BaseActivity
import com.esp.library.exceedersesp.controllers.applications.ESP_LIB_AssessorApplicationStagesCeriteriaDetailScreenActivity
import utilities.data.applicants.dynamics.ESP_LIB_DynamicStagesDAO


class ESP_LIB_ListStagesDetailAdapter(private val mStages: List<ESP_LIB_DynamicStagesDAO>?, con: ESP_LIB_BaseActivity, internal var searched_text: String) : androidx.recyclerview.widget.RecyclerView.Adapter<ESP_LIB_ListStagesDetailAdapter.ParentViewHolder>() {


    private var context: ESP_LIB_BaseActivity
    open class ParentViewHolder(v: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(v)

    inner class ActivitiesList(v: View) : ParentViewHolder(v) {

        internal var cardView: androidx.cardview.widget.CardView
        internal var stage_name: TextView
        internal var status: TextView
        internal var criteria_count: TextView
        internal var condition: TextView


        init {

            cardView = itemView.findViewById(R.id.cardview)
            stage_name = itemView.findViewById(R.id.stage_name)
            status = itemView.findViewById(R.id.status)
            criteria_count = itemView.findViewById(R.id.criteria_count)
            condition = itemView.findViewById(R.id.condition)
        }

    }


    init {
        context = con
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParentViewHolder {
        val v: View
        v = LayoutInflater.from(parent.context).inflate(R.layout.esp_lib_repeater_application_stages, parent, false)
        return ActivitiesList(v)
    }


    override fun onBindViewHolder(holder_parent: ParentViewHolder, position: Int) {

        val holder = holder_parent as ActivitiesList

        holder.stage_name.text = mStages!![position].name
        holder.status.text = mStages[position].status

        if (mStages[position].status?.toLowerCase().equals(context.getString(R.string.esp_lib_text_completed), ignoreCase = true)) {
            holder.status.setTextColor(ContextCompat.getColor(context,R.color.esp_lib_color_status_accepted))
        } else if (mStages[position].status?.toLowerCase().equals(context.getString(R.string.esp_lib_text_locked), ignoreCase = true)) {
            holder.status.setTextColor(ContextCompat.getColor(context,R.color.esp_lib_color_light_grey))
        } else if (mStages[position].status?.toLowerCase().equals(context.getString(R.string.esp_lib_text_open), ignoreCase = true)) {
            holder.status.setTextColor(ContextCompat.getColor(context,R.color.esp_lib_color_status_pending))
        } else {
            holder.status.setTextColor(ContextCompat.getColor(context,R.color.esp_lib_color_light_grey))
        }


        if (mStages[position].criteriaList != null && mStages[position].criteriaList!!.size > 0) {
            holder.criteria_count.text = mStages[position].criteriaList?.size.toString() + ""
        } else {
            holder.criteria_count.text = "0"
        }


        if (mStages[position].isAll) {
            holder.condition.text = context.getString(R.string.esp_lib_text_all)
        } else {
            holder.condition.text = context.getString(R.string.esp_lib_text_any)
        }

        holder.cardView.setOnClickListener {
            val bundle = Bundle()
            bundle.putSerializable(ESP_LIB_DynamicStagesDAO.BUNDLE_KEY, mStages[position])
            ESP_LIB_Shared.getInstance().callIntentWithResult(ESP_LIB_AssessorApplicationStagesCeriteriaDetailScreenActivity::class.java, context, bundle, 2)
        }


    }//End Holder Class


    override fun getItemCount(): Int {
        return mStages?.size ?: 0

    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }


    companion object {

        private val LOG_TAG = "ListStagesDetailAdapter"


    }
}
