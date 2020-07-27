package utilities.adapters.setup.applications

import android.content.Context
import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import com.esp.library.R
import com.esp.library.utilities.common.ESP_LIB_Shared
import com.esp.library.utilities.common.ESP_LIB_SharedPreference
import com.esp.library.exceedersesp.controllers.applications.ESP_LIB_ActivityStageDetails
import com.esp.library.utilities.common.ESP_LIB_Enums
import com.esp.library.utilities.setup.applications.ESP_LIB_ApplicationCriteriaAdapter
import com.google.gson.Gson
import utilities.data.applicants.dynamics.ESP_LIB_DynamicFormSectionFieldDAO
import utilities.data.applicants.dynamics.ESP_LIB_DynamicResponseDAO
import utilities.data.applicants.dynamics.ESP_LIB_DynamicStagesCriteriaListDAO
import utilities.data.applicants.dynamics.ESP_LIB_DynamicStagesDAO
import java.util.*


class ESP_LIB_ApplicationStagesAdapter(iscomingfromAssessor: Boolean, val stagesListESPLIB: MutableList<ESP_LIB_DynamicStagesDAO>,
                                       actualresponseJson: String, con: Context, var nestedscrollview: NestedScrollView)
    : androidx.recyclerview.widget.RecyclerView.Adapter<ESP_LIB_ApplicationStagesAdapter.ParentViewHolder>() {


    var TAG = "ApplicationCriteriaAdapter"
    private var context: Context

    // var stagesList: List<DynamicStagesDAO>
    var actualResponseJson: String
    var isComingfromAssessor: Boolean = false
    public var criteriaAdapterESPLIB: ESP_LIB_ApplicationCriteriaAdapter? = null;
    var criteriaListCollections = ArrayList<ESP_LIB_DynamicStagesCriteriaListDAO?>()
    var pref: ESP_LIB_SharedPreference? = null
    var drawable: GradientDrawable? = null

    open class ParentViewHolder(v: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(v)

    inner class ActivitiesList(v: View) : ParentViewHolder(v) {

        internal var rvCrietrias: androidx.recyclerview.widget.RecyclerView
        internal var rvExpandCrietrias: androidx.recyclerview.widget.RecyclerView
        internal var llstagesrow: LinearLayout
        internal var lldetail: LinearLayout
        internal var rlstatus: RelativeLayout
        internal var txtStagename: TextView
        internal var txtstatus: TextView
        internal var ivsign: ImageView
        internal var acceptedontext: TextView
        internal var acceptedonvalue: TextView
        internal var sequencetextvalue: TextView
        internal var acceptencetextvalue: TextView
        internal var acceptencetext: TextView
        internal var txtline: TextView
        internal var ivarrow: ImageView
        internal var rlaccepreject: RelativeLayout

        init {
            rvCrietrias = itemView.findViewById(R.id.rvCrietrias)
            rvExpandCrietrias = itemView.findViewById(R.id.rvExpandCrietrias)
            llstagesrow = itemView.findViewById(R.id.llstagesrow)
            lldetail = itemView.findViewById(R.id.lldetail)
            txtStagename = itemView.findViewById(R.id.txtStagename)
            txtstatus = itemView.findViewById(R.id.txtstatus)
            ivsign = itemView.findViewById(R.id.ivsign)
            rlstatus = itemView.findViewById(R.id.rlstatus)
            acceptedonvalue = itemView.findViewById(R.id.acceptedonvalue)
            acceptedontext = itemView.findViewById(R.id.acceptedontext)
            sequencetextvalue = itemView.findViewById(R.id.sequencetextvalue)
            acceptencetextvalue = itemView.findViewById(R.id.acceptencetextvalue)
            acceptencetext = itemView.findViewById(R.id.acceptencetext)
            txtline = itemView.findViewById(R.id.txtline)
            ivarrow = itemView.findViewById(R.id.ivarrow)
            rlaccepreject = itemView.findViewById(R.id.rlaccepreject)
        }
    }

    init {
        context = con
        isComingfromAssessor = iscomingfromAssessor
        actualResponseJson = actualresponseJson
        pref = ESP_LIB_SharedPreference(context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParentViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.esp_lib_list_stage_row, parent, false)
        return ActivitiesList(v)

    }


    override fun onBindViewHolder(holder_parent: ParentViewHolder, position: Int) {
        val holder = holder_parent as ActivitiesList
        val dynamicStagesDAO = stagesListESPLIB.get(position)
        if (isComingfromAssessor) {


            holder.llstagesrow.visibility = View.VISIBLE
            holder.txtStagename.text = dynamicStagesDAO.name
            holder.sequencetextvalue.text = dynamicStagesDAO.order.toString()
            holder.txtstatus.text = dynamicStagesDAO.status


            /*  if (dynamicStagesDAO.isAll) {
                  holder.conditiontextvalue.text = context.getString(R.string.all)
              } else {
                  holder.conditiontextvalue.text = context.getString(R.string.any)
              }*/

            val displayDate = ESP_LIB_Shared.getInstance().getStageDisplayDate(context, dynamicStagesDAO.completedOn)
            if (displayDate.isNullOrEmpty())
                holder.rlaccepreject.visibility = View.GONE

            val actualResponse = Gson().fromJson(actualResponseJson, ESP_LIB_DynamicResponseDAO::class.java)
            if (actualResponse.applicationStatus.equals(ESP_LIB_Enums.rejected.toString(), ignoreCase = true)) // rejected
                holder.acceptedontext.text = context.getString(R.string.esp_lib_text_rejectedon)


            holder.acceptedonvalue.text = displayDate


            if (dynamicStagesDAO.linkDefinitionId > 0) {
                holder.acceptencetext.text = context.getString(R.string.esp_lib_text_subdefinition)
                holder.acceptencetextvalue.text = dynamicStagesDAO.linkDefinitionName
            } else {
                holder.acceptencetext.text = context.getString(R.string.esp_lib_text_acceptancecriteria)
                if (dynamicStagesDAO.criteriaList != null && dynamicStagesDAO.criteriaList!!.isNotEmpty()) {
                    holder.acceptencetextvalue.text = dynamicStagesDAO.criteriaList!!.size.toString()
                } else {
                    holder.acceptencetextvalue.text = "0"
                }
            }

            setStatusColor(holder, dynamicStagesDAO, position)


            /*if (dynamicStagesDAO.status.equals(context.getString(R.string.open), ignoreCase = true))
                holder.ivarrow.post { holder.ivarrow.performClick() }*/


        } else {
            holder.llstagesrow.visibility = View.GONE

            for (i in 0 until dynamicStagesDAO.criteriaList!!.size) {
                val getList = dynamicStagesDAO.criteriaList?.get(i);
                val isArrayHasValue = criteriaListCollections.any { x -> x?.assessmentId == getList?.assessmentId }
                if (!isArrayHasValue) {
                    if (getList?.isEnabled!!)
                        criteriaListCollections.add(getList)
                }
            }

            if (position == (stagesListESPLIB.size - 1)) {
                holder.rvCrietrias.setHasFixedSize(true)
                holder.rvCrietrias.isNestedScrollingEnabled = false
                holder.rvCrietrias.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
                criteriaAdapterESPLIB = ESP_LIB_ApplicationCriteriaAdapter(criteriaListCollections, context, holder.rvCrietrias)
                criteriaAdapterESPLIB?.getActualResponse(actualResponseJson)
                holder.rvCrietrias.adapter = criteriaAdapterESPLIB
            }
        }


        if (dynamicStagesDAO.criteriaList?.size == 0) {
            holder.ivarrow.visibility = View.GONE
            holder.rvExpandCrietrias.visibility = View.GONE
        }

        holder.llstagesrow.setOnClickListener {


            /* if (holder.rvExpandCrietrias.visibility == View.GONE) {
                 holder.ivarrow.setImageResource(R.drawable.ic_arrow_up)
                 expandableCriterias(dynamicStagesDAO, holder)
                 holder.rvExpandCrietrias.visibility = View.VISIBLE
             } else
             {
                 holder.ivarrow.setImageResource(R.drawable.ic_arrow_down)
                 holder.rvExpandCrietrias.visibility = View.GONE
             }*/

            // if (!dynamicStagesDAO.status.equals(context.getString(R.string.locked), ignoreCase = true)) {
            //  if (dynamicStagesDAO.criteriaList!!.size > 0) {

            if ((!dynamicStagesDAO.status.equals(ESP_LIB_Enums.locked.toString(), ignoreCase = true))) {
                if (!dynamicStagesDAO.type.equals(ESP_LIB_Enums.link.toString(), ignoreCase = true)) {
                    if (holder.txtstatus.text != ESP_LIB_Enums.locked.toString()) {
                        val intent = Intent(context, ESP_LIB_ActivityStageDetails::class.java)
                        intent.putExtra("dynamicStagesDAO", dynamicStagesDAO)
                        intent.putExtra("actualResponseJson", actualResponseJson)
                        intent.putExtra("status", holder.txtstatus.text)
                        context.startActivity(intent)
                    }
                }
            }
        }


    }


    fun expandableCriterias(ESPLIBDynamicStagesDAO: ESP_LIB_DynamicStagesDAO, holder: ActivitiesList) {
        criteriaListCollections.clear()
        for (i in ESPLIBDynamicStagesDAO.criteriaList!!.indices) {
            val getList = ESPLIBDynamicStagesDAO.criteriaList?.get(i);
            val isArrayHasValue = criteriaListCollections.any { x -> x?.assessmentId == getList?.assessmentId }
            if (!isArrayHasValue) {
                if (getList?.isEnabled!!)
                    criteriaListCollections.add(getList)
            }
        }


        holder.rvExpandCrietrias.setHasFixedSize(true)
        holder.rvExpandCrietrias.isNestedScrollingEnabled = false
        holder.rvExpandCrietrias.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
        criteriaAdapterESPLIB = ESP_LIB_ApplicationCriteriaAdapter(criteriaListCollections, context, holder.rvExpandCrietrias)
        criteriaAdapterESPLIB?.getStage(ESPLIBDynamicStagesDAO)
        criteriaAdapterESPLIB?.getActualResponse(actualResponseJson)
        holder.rvExpandCrietrias.adapter = criteriaAdapterESPLIB
    }

    override fun getItemCount(): Int {
        return stagesListESPLIB.size
    }

    fun notifyChangeIfAny(criteriaId: Int) {
        criteriaAdapterESPLIB?.notifyChangeIfAny(criteriaId)
    }

    private fun setStatusColor(holder: ActivitiesList, ESPLIBDynamicStagesDAO: ESP_LIB_DynamicStagesDAO, position: Int) {
        var status = ESPLIBDynamicStagesDAO.status?.toLowerCase(Locale.getDefault())
        val actualResponseJson = Gson().fromJson(actualResponseJson, ESP_LIB_DynamicResponseDAO::class.java)
        //  holder.lldetail.setBackgroundResource(R.drawable.draw_bg_white)

        var isSigned: Boolean = false
        for (i in 0 until ESPLIBDynamicStagesDAO.criteriaList!!.size) {
            isSigned = ESPLIBDynamicStagesDAO.criteriaList?.get(i)?.isSigned!!
            if (isSigned) {
                holder.ivsign.visibility = View.VISIBLE
                break
            }
        }


        if (actualResponseJson.applicationStatusId == 5) // cancalled
        {
            status = context.getString(R.string.esp_lib_text_locked)
            holder.txtstatus.setText(status)
        }
        if (status == null) {
            holder.txtline.setBackgroundColor(ContextCompat.getColor(context, R.color.esp_lib_color_status_new))
            return
        }
        holder.rlstatus.setBackgroundResource(R.drawable.esp_lib_drawable_status_background)
        drawable = holder.rlstatus.background as GradientDrawable


        when (status.toLowerCase(Locale.getDefault())) {
            ESP_LIB_Enums.invited.toString() // Invited
            -> {
                holder.txtstatus.setTextColor(ContextCompat.getColor(context, R.color.esp_lib_color_status_invited))
                holder.txtline.setBackgroundColor(ContextCompat.getColor(context, R.color.esp_lib_color_status_invited))
                drawable?.setColor(ContextCompat.getColor(context, R.color.esp_lib_color_status_invited_background))
            }
            ESP_LIB_Enums.newstatus.toString() // New
            -> {
                holder.txtstatus.setTextColor(ContextCompat.getColor(context, R.color.esp_lib_color_status_new))
                holder.txtline.setBackgroundColor(ContextCompat.getColor(context, R.color.esp_lib_color_status_new))
                drawable?.setColor(ContextCompat.getColor(context, R.color.esp_lib_color_status_new_background))
            }
            ESP_LIB_Enums.pending.toString() // Pending
            -> {
                holder.txtstatus.setText(context.getString(R.string.esp_lib_text_pending))
                holder.txtstatus.setTextColor(ContextCompat.getColor(context, R.color.esp_lib_color_status_pending))
                holder.txtline.setBackgroundColor(ContextCompat.getColor(context, R.color.esp_lib_color_status_pending))
                drawable?.setColor(ContextCompat.getColor(context, R.color.esp_lib_color_status_pending_background))
            }
            ESP_LIB_Enums.locked.toString() // locked
            -> {
                lockedCase(holder, ESPLIBDynamicStagesDAO, position, actualResponseJson, drawable!!)
            }

            ESP_LIB_Enums.completed.toString() // Completed
            -> {
                completeStage(ESPLIBDynamicStagesDAO, holder, drawable!!, actualResponseJson)
            }

            ESP_LIB_Enums.complete.toString() // Complete
            -> {
                completeStage(ESPLIBDynamicStagesDAO, holder, drawable!!, actualResponseJson)
            }

            ESP_LIB_Enums.rejected.toString()  // Rejected
            -> {

                holder.txtstatus.setTextColor(ContextCompat.getColor(context, R.color.esp_lib_color_status_rejected))
                holder.ivsign.setImageResource(R.drawable.esp_lib_drawable_ic_icon_red_signed)
                holder.txtline.setBackgroundColor(ContextCompat.getColor(context, R.color.esp_lib_color_status_rejected))
                drawable?.setColor(ContextCompat.getColor(context, R.color.esp_lib_color_status_rejected_background))
            }

            else -> {
                holder.txtstatus.setTextColor(ContextCompat.getColor(context, R.color.esp_lib_color_status_new))
                holder.txtline.setBackgroundColor(ContextCompat.getColor(context, R.color.esp_lib_color_status_new))
                drawable?.setColor(ContextCompat.getColor(context, R.color.esp_lib_color_status_new_background))
            }
        }
    }

    private fun lockedCase(holder: ActivitiesList, ESPLIBDynamicStagesDAO: ESP_LIB_DynamicStagesDAO, position: Int, actualResponseJsonESPLIB: ESP_LIB_DynamicResponseDAO, drawable: GradientDrawable) {
        val linkText = ESP_LIB_Enums.link.toString()
        if (((position == 0 && ESPLIBDynamicStagesDAO.type.equals(linkText, ignoreCase = true))
                        || (actualResponseJsonESPLIB.applicationStatus.equals(ESP_LIB_Enums.rejected.toString(), ignoreCase = true) ||
                        actualResponseJsonESPLIB.applicationStatus.equals(ESP_LIB_Enums.accepted.toString(), ignoreCase = true)))
                || position > 0 && stagesListESPLIB.get(position - 1).type.equals(ESP_LIB_Enums.completed.toString(), ignoreCase = true)
                && ESPLIBDynamicStagesDAO.type.equals(linkText, ignoreCase = true)) {
            holder.txtstatus.text = context.getString(R.string.esp_lib_text_completedcaps)

            if (actualResponseJsonESPLIB.applicationStatus.equals(ESP_LIB_Enums.rejected.toString(), ignoreCase = true)) {
                holder.ivsign.setImageResource(R.drawable.esp_lib_drawable_ic_icon_red_signed)
                holder.txtstatus.setTextColor(ContextCompat.getColor(context, R.color.esp_lib_color_status_rejected))
                drawable.setColor(ContextCompat.getColor(context, R.color.esp_lib_color_status_rejected_background))
            } else if (actualResponseJsonESPLIB.applicationStatus.equals(ESP_LIB_Enums.accepted.toString(), ignoreCase = true)) {
                holder.ivsign.setImageResource(R.drawable.esp_lib_drawable_ic_icon_green_signed)
                holder.txtstatus.setTextColor(ContextCompat.getColor(context, R.color.esp_lib_color_status_accepted))
                drawable.setColor(ContextCompat.getColor(context, R.color.esp_lib_color_status_accepted_background))
            } else {
                holder.txtstatus.setTextColor(ContextCompat.getColor(context, R.color.esp_lib_color_status_locked))
                drawable.setColor(ContextCompat.getColor(context, R.color.esp_lib_color_transparent_color))
            }

        }/* else if (!stagesList.get(position - 1).type.equals(context.getString(R.string.completed), ignoreCase = true)
                        && dynamicStagesDAO.type.equals(linkText, ignoreCase = true)) {

                }*/ else {
            holder.lldetail.setBackgroundResource(R.drawable.esp_lib_drawable_draw_bg_grey_stroke)
            holder.txtstatus.setTextColor(ContextCompat.getColor(context, R.color.esp_lib_color_status_locked))
            drawable.setColor(ContextCompat.getColor(context, R.color.esp_lib_color_status_locked_background))

        }
        holder.txtline.setBackgroundColor(ContextCompat.getColor(context, R.color.esp_lib_color_status_locked))

    }

    private fun completeStage(ESPLIBDynamicStagesDAO: ESP_LIB_DynamicStagesDAO, holder: ActivitiesList, drawable: GradientDrawable,
                              actualResponseJsonESPLIB: ESP_LIB_DynamicResponseDAO) {
        holder.txtstatus.setText(context.getString(R.string.esp_lib_text_completedcaps))

        var getAssessmentStatus = "";
        for (i in 0 until ESPLIBDynamicStagesDAO.criteriaList!!.size) {
            getAssessmentStatus = ESPLIBDynamicStagesDAO.criteriaList?.get(i)?.assessmentStatus.toString()
        }

        if (getAssessmentStatus.equals(ESP_LIB_Enums.rejected.toString(), ignoreCase = true) || actualResponseJsonESPLIB.applicationStatus.equals(ESP_LIB_Enums.rejected.toString(), ignoreCase = true)) {
            holder.ivsign.setImageResource(R.drawable.esp_lib_drawable_ic_icon_red_signed)
            holder.txtstatus.setTextColor(ContextCompat.getColor(context, R.color.esp_lib_color_status_rejected))
            drawable.setColor(ContextCompat.getColor(context, R.color.esp_lib_color_status_rejected_background))
        } else if (getAssessmentStatus.equals(ESP_LIB_Enums.accepted.toString(), ignoreCase = true) || actualResponseJsonESPLIB.applicationStatus.equals(ESP_LIB_Enums.accepted.toString(), ignoreCase = true)) {
            holder.ivsign.setImageResource(R.drawable.esp_lib_drawable_ic_icon_green_signed)
            holder.txtstatus.setTextColor(ContextCompat.getColor(context, R.color.esp_lib_color_status_accepted))
            drawable.setColor(ContextCompat.getColor(context, R.color.esp_lib_color_status_accepted_background))
        } else {
            holder.ivsign.setImageResource(R.drawable.esp_lib_drawable_ic_icon_green_signed)
            holder.txtstatus.setTextColor(ContextCompat.getColor(context, R.color.esp_lib_color_status_accepted))
            holder.txtline.setBackgroundColor(ContextCompat.getColor(context, R.color.esp_lib_color_status_accepted))
            drawable.setColor(ContextCompat.getColor(context, R.color.esp_lib_color_status_accepted_background))
        }
    }


    fun getAllCriteriaFields(): List<ESP_LIB_DynamicFormSectionFieldDAO>? {
        if (stagesListESPLIB != null && stagesListESPLIB.size > 0) {
            val fields = ArrayList<ESP_LIB_DynamicFormSectionFieldDAO>()
            for (stageslist in stagesListESPLIB) {
                for (criterialist in stageslist.criteriaList!!) {
                    for (sectionDAO in criterialist.form.sections!!) {
                        if (sectionDAO.fieldsCardsList != null) {
                            for (sectionFieldsCardsDAO in sectionDAO.fieldsCardsList!!) {
                                fields.addAll(sectionFieldsCardsDAO.fields!!)
                            }
                        }
                    }

                }

            }

            return fields

        }

        return null
    }
}
