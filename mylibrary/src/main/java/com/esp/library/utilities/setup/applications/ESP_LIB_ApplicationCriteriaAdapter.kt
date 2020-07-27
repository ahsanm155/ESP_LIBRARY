package com.esp.library.utilities.setup.applications

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Handler
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.esp.library.R
import com.esp.library.exceedersesp.ESP_LIB_BaseActivity
import com.esp.library.exceedersesp.ESP_LIB_ESPApplication
import com.esp.library.exceedersesp.controllers.applications.ESP_LIB_ActivityStageDetails
import com.esp.library.exceedersesp.controllers.applications.ESP_LIB_ApplicationDetailScreenActivity
import com.esp.library.exceedersesp.controllers.applications.ESP_LIB_UsersList
import com.esp.library.exceedersesp.controllers.feedback.ESP_LIB_FeedbackForm
import com.esp.library.utilities.common.ESP_LIB_Shared
import com.esp.library.utilities.common.ESP_LIB_SharedPreference
import com.esp.library.utilities.common.ESP_LIB_ViewAnimationUtils
import com.esp.library.utilities.common.GoogleFontsLibrary
import com.esp.library.utilities.data.applicants.signature.ESP_LIB_SignatureDAO
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import utilities.adapters.setup.applications.ESP_LIB_ListAddApplicationSectionsAdapter
import utilities.data.applicants.dynamics.*
import utilities.interfaces.ESP_LIB_FeedbackSubmissionClick


class ESP_LIB_ApplicationCriteriaAdapter(val criterialist: List<ESP_LIB_DynamicStagesCriteriaListDAO?>, con: Context, rvCrietria: androidx.recyclerview.widget.RecyclerView)
    : androidx.recyclerview.widget.RecyclerView.Adapter<ESP_LIB_ApplicationCriteriaAdapter.ParentViewHolder>() {


    var TAG = "ApplicationCriteriaAdapter"
    private var context: Context? = null
    internal var imm: InputMethodManager? = null

    //  var criteriaList: List<DynamicStagesCriteriaListDAO>?
    var actualResponseJson: String? = null
    var isViewOnly: Boolean = false
    var listener: ESP_LIB_FeedbackSubmissionClick? = null
    var rvCrietrias: androidx.recyclerview.widget.RecyclerView? = null
    var isNotifyOnly: Boolean = false
    var isComingfromAssessor: Boolean = false
    var mApplicationSectionsAdapterESPLIB: ESP_LIB_ListAddApplicationSectionsAdapter? = null
    val POPUP_CONSTANT = "mPopup"
    val POPUP_FORCE_SHOW_ICON = "setForceShowIcon"
    var ESPLIBDynamicStagesDAO: ESP_LIB_DynamicStagesDAO? = null
    var isNotifyOnlyPosition: Int = 0
    var criteriaListESPLIB: List<ESP_LIB_DynamicStagesCriteriaListDAO?>? = null
    var pref: ESP_LIB_SharedPreference? = null

    var rawStyles = intArrayOf(R.raw.caveat_regular, R.raw.dancingscript, R.raw.gloriagallelujah_regular, R.raw.greatvibes_regular, R.raw.indieflower_regular,
            R.raw.kaushanscript_regular, R.raw.pacifico_regular, R.raw.pangolin_regular, R.raw.parisienne_regular, R.raw.rocksalt_regular,
            R.raw.sacramento_regular)

    var rawStylesNames = arrayOf("caveat_regular", "dancingscript", "gloriagallelujah_regular", "greatvibes_regular", "indieflower_regular",
            "kaushanscript_regular", "pacifico_regular", "pangolin_regular", "parisienne_regular", "rocksalt_regular",
            "sacramento_regular")


    init {
        context = con
        pref = ESP_LIB_SharedPreference(context)
        criteriaListESPLIB = criterialist
        rvCrietrias = rvCrietria
        try {
            listener = context as ESP_LIB_FeedbackSubmissionClick
        } catch (e: java.lang.Exception) {

        }
        if (ESP_LIB_ESPApplication.getInstance()?.user?.loginResponse?.role?.toLowerCase() != context!!.getString(R.string.esp_lib_text_applicantsmall))
            isComingfromAssessor = true
    }


    fun getStage(dynamicstagesDAOESPLIB: ESP_LIB_DynamicStagesDAO) {
        ESPLIBDynamicStagesDAO = dynamicstagesDAOESPLIB
    }

    fun setCriterias(criterialist: List<ESP_LIB_DynamicStagesCriteriaListDAO?>) {
        criteriaListESPLIB = criterialist

        for (i in criteriaListESPLIB!!.indices) {
            val getForm = criterialist.get(i)?.form
            val getFormvalues = criterialist.get(i)?.formValues
            //Setting Sections With FieldsCards.
            var sectionDAO = ESP_LIB_DynamicFormSectionDAO()
            if (getForm?.sections != null && getForm.sections!!.size > 0) {
                for (st in 0 until getForm.sections!!.size) {
                    sectionDAO = getForm.sections!![st]
                    if (sectionDAO.fields != null && sectionDAO.fields!!.size > 0) {
                        for (s in sectionDAO.fields!!.indices) {
                            for (j in getFormvalues!!.indices) {
                                val dynamicFormValuesDAO = getFormvalues.get(j)
                                if (dynamicFormValuesDAO.sectionCustomFieldId == sectionDAO.fields?.get(s)?.sectionCustomFieldId) {
                                    dynamicFormValuesDAO.value = sectionDAO.fields?.get(s)?.value
                                }
                            }
                        }

                    }

                }
            }
        }

    }

    open class ParentViewHolder(v: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(v)

    inner class ActivitiesList(v: View) : ParentViewHolder(v) {

        internal var rvFields: androidx.recyclerview.widget.RecyclerView
        internal var rvCriteriaComments: androidx.recyclerview.widget.RecyclerView
        internal var txtcrierianame: TextView
        internal var ivainforrow: ImageView
        internal var ivexpandcollapserrow: ImageView
        internal var viewdivider: View
        internal var fieldsdivider: View
        internal var ivsignature: ImageView
        internal var durationtextvalue: TextView
        internal var ownertextvalue: TextView
        internal var txtstatus: TextView
        internal var txtmoreinfo: TextView
        internal var txtline: TextView
        internal var etxtsign: AppCompatEditText
        internal var rlincludesignatue: View
        internal var rlacceptapprove: LinearLayout
        internal var llcriterialayout: LinearLayout
        internal var llsignature: LinearLayout
        internal var etxtsignLabel: TextInputLayout
        internal var lldetail: RelativeLayout
        internal var rlmoreinfo: RelativeLayout
        internal var btapprove: Button
        internal var btreject: Button
        internal var add_criteria_comments: ImageButton
        internal var ibReassignCard: ImageButton
        internal var pendinglineview: View

        init {
            pendinglineview = itemView.findViewById(R.id.pendinglineview)
            txtcrierianame = itemView.findViewById(R.id.txtcrierianame)
            fieldsdivider = itemView.findViewById(R.id.fieldsdivider)
            viewdivider = itemView.findViewById(R.id.viewdivider)
            ivainforrow = itemView.findViewById(R.id.ivainforrow)
            llsignature = itemView.findViewById(R.id.llsignature)
            ivsignature = itemView.findViewById(R.id.ivsignature)
            ivexpandcollapserrow = itemView.findViewById(R.id.ivexpandcollapserrow)
            rvFields = itemView.findViewById(R.id.rvFields)
            rvCriteriaComments = itemView.findViewById(R.id.rvCriteriaComments)
            llcriterialayout = itemView.findViewById(R.id.llcriterialayout)
            durationtextvalue = itemView.findViewById(R.id.durationtextvalue)
            ownertextvalue = itemView.findViewById(R.id.ownertextvalue)
            txtstatus = itemView.findViewById(R.id.txtstatus)
            rlincludesignatue = itemView.findViewById(R.id.rlincludesignatue)
            etxtsign = itemView.findViewById(R.id.etxtsign)
            etxtsignLabel = itemView.findViewById(R.id.etxtsignLabel)
            txtmoreinfo = itemView.findViewById(R.id.txtmoreinfo)
            txtline = itemView.findViewById(R.id.txtline)
            rlacceptapprove = itemView.findViewById(R.id.rlacceptapprove)
            btapprove = itemView.findViewById(R.id.btapprove)
            btreject = itemView.findViewById(R.id.btreject)
            rlmoreinfo = itemView.findViewById(R.id.rlmoreinfo)
            add_criteria_comments = itemView.findViewById(R.id.add_criteria_comments)
            ibReassignCard = itemView.findViewById(R.id.ibReassignCard)
            lldetail = itemView.findViewById(R.id.lldetail)
        }
    }


    fun getActualResponse(actualresponseJson: String) {
        actualResponseJson = actualresponseJson
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParentViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.esp_lib_list_criteria_row, parent, false)
        return ActivitiesList(view)
    }

    fun notifyOnly(position: Int) {
        isNotifyOnly = true;
        isNotifyOnlyPosition = position
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder_parent: ParentViewHolder, position: Int) {
        val holder = holder_parent as ActivitiesList

        val criteriaListDAO = criteriaListESPLIB?.get(position)


        val tf = Typeface.createFromAsset(context!!.assets, "font/greatvibes_regular.otf")
        holder.etxtsign.typeface = tf
        holder.etxtsign.setText(criteriaListDAO?.ownerName)

        holder.rvFields.setHasFixedSize(true)
        holder.rvFields.isNestedScrollingEnabled = false
        val linearLayoutManagerCrteria = androidx.recyclerview.widget.LinearLayoutManager(context, androidx.recyclerview.widget.LinearLayoutManager.VERTICAL, false)
        holder.rvFields.layoutManager = linearLayoutManagerCrteria


        holder.rvCriteriaComments.setHasFixedSize(true)
        holder.rvCriteriaComments.isNestedScrollingEnabled = false
        val linearLayoutManagerCrteriaComments = androidx.recyclerview.widget.LinearLayoutManager(context, androidx.recyclerview.widget.LinearLayoutManager.VERTICAL, false)
        holder.rvCriteriaComments.layoutManager = linearLayoutManagerCrteriaComments



        holder.txtcrierianame.text = criteriaListDAO?.name
        var daysVal = context?.getString(R.string.esp_lib_text_day)
        if (criteriaListDAO?.daysToComplete != null && criteriaListDAO.daysToComplete > 1)
            daysVal = context?.getString(R.string.esp_lib_text_days)

        holder.durationtextvalue.text = criteriaListDAO?.daysToComplete.toString() + " " + daysVal

        var criteriaOwner = criteriaListDAO?.ownerName;

        if (criteriaListDAO?.ownerName.isNullOrEmpty())
            criteriaOwner = context?.getString(R.string.esp_lib_text_applicant)

        holder.ownertextvalue.text = criteriaOwner
        //val assessmentStatus = criteriaListDAO?.assessmentStatus
        holder.txtstatus.text = criteriaListDAO?.assessmentStatus



        if (isNotifyOnly) {
            mApplicationSectionsAdapterESPLIB?.notifyItemChanged(isNotifyOnlyPosition)
        }
        val sectionsStages = GetStagesFieldsCards(criteriaListDAO)
        val sections = ArrayList<ESP_LIB_DynamicFormSectionDAO>()
        for (j in 0 until sectionsStages.size) {
            if (sectionsStages.get(j).fields == null) {
                holder.rvFields.visibility = View.INVISIBLE
                break
            }
            sections.add(sectionsStages.get(j))
        }
        setStatusColor(holder, criteriaListDAO, sections, position)
        isViewOnly = !criteriaListDAO?.assessmentStatus.equals(context?.getString(R.string.esp_lib_text_activecaps))

        if (criteriaListDAO?.type.equals(context?.getString(R.string.esp_lib_text_feedback), ignoreCase = true))
            holder.btreject.visibility = View.GONE
        else
            holder.btreject.visibility = View.VISIBLE

        if (!criteriaListDAO?.approveText.isNullOrEmpty())
            holder.btapprove.text = criteriaListDAO?.approveText

        if (!criteriaListDAO?.rejectText.isNullOrEmpty())
            holder.btreject.text = criteriaListDAO?.rejectText


        if (criteriaListDAO?.comments != null && criteriaListDAO.comments!!.size > 0) {
            holder.add_criteria_comments.setImageDrawable(ContextCompat.getDrawable(context!!, R.drawable.ic_card_commented))
        } else {
            holder.add_criteria_comments.setImageDrawable(ContextCompat.getDrawable(context!!, R.drawable.ic_card_not_commented))
        }



        mApplicationSectionsAdapterESPLIB = ESP_LIB_ListAddApplicationSectionsAdapter(sections, (context as ESP_LIB_BaseActivity?)!!, "", isViewOnly)
        actualResponseJson?.let { mApplicationSectionsAdapterESPLIB?.setActualResponseJson(it) }
        holder.rvFields.adapter = mApplicationSectionsAdapterESPLIB
        mApplicationSectionsAdapterESPLIB?.getCriteriaObject(criteriaListDAO)
        try {
            mApplicationSectionsAdapterESPLIB?.setmApplicationFieldsAdapterListener2(context as ESP_LIB_ApplicationDetailScreenActivity)
        } catch (e: java.lang.Exception) {
            mApplicationSectionsAdapterESPLIB?.setmApplicationFieldsAdapterListener2(context as ESP_LIB_ActivityStageDetails)
        }


        holder.rvFields.visibility = View.GONE
        holder.rlacceptapprove.visibility = View.GONE
        holder.pendinglineview.visibility = View.GONE

        if (!ESP_LIB_ApplicationDetailScreenActivity.criteriaWasLoaded)
            iterateOnSection(sections, holder, criteriaListDAO)

        holder.ivexpandcollapserrow.setOnClickListener {
            if (!criteriaListDAO?.assessmentStatus.equals(context?.getString(R.string.esp_lib_text_reassigned), ignoreCase = true)) {

                if (holder.txtmoreinfo.text.equals(context?.getString(R.string.esp_lib_text_moreinformation))) {
                    moreInfo(holder, criteriaListDAO, position, sections)
                    holder.fieldsdivider.visibility = View.VISIBLE
                } else {
                    ESP_LIB_ViewAnimationUtils.collapse(holder.rvFields, null)
                    holder.rlacceptapprove.visibility = View.GONE
                    holder.fieldsdivider.visibility = View.GONE
                    val handler = Handler()
                    handler.postDelayed({
                        holder.txtmoreinfo.text = context?.getString(R.string.esp_lib_text_moreinformation)
                        holder.ivexpandcollapserrow.setImageResource(R.drawable.ic_more_info_down)
                        holder.rvFields.visibility = View.GONE
                    }, 300)


                }
            }
        }


        holder.btapprove.setOnClickListener {
            listener?.feedbackClick(true, criteriaListDAO, ESPLIBDynamicStagesDAO, position)
        }
        holder.btreject.setOnClickListener {
            listener?.feedbackClick(false, criteriaListDAO, ESPLIBDynamicStagesDAO, position)
        }

        if (holder.txtmoreinfo.text.equals(context?.getString(R.string.esp_lib_text_lessinformation))) {
            moreInfo(holder, criteriaListDAO, position, sections)
        }

        if (pref?.selectedUserRole.equals(context?.getString(R.string.esp_lib_text_assessor), ignoreCase = true))
            isComingfromAssessor = true

        if (!isComingfromAssessor)
            holder.add_criteria_comments.visibility = View.GONE



        holder.add_criteria_comments.setOnClickListener {
            if (!criteriaListDAO?.assessmentStatus.equals(context?.getString(R.string.esp_lib_text_reassigned), ignoreCase = true)) {
                /*val bundle = Bundle()
                bundle.putSerializable(ESP_LIB_DynamicStagesCriteriaListDAO.BUNDLE_KEY, criteriaListDAO)
                ESP_LIB_Shared.getInstance().callIntentWithResult(ESP_LIB_AssessorApplicationStagesCeriteriaCommentsScreenActivity::class.java, (context as Activity?), bundle, 2)*/


                val intent = Intent(context, ESP_LIB_FeedbackForm::class.java)
                intent.putExtra("actualResponseJson", actualResponseJson)
                intent.putExtra("criteriaListDAO", criteriaListDAO)
                intent.putExtra("isAccepted", true)
                intent.putExtra("isAddCriteria", true)
                context?.startActivity(intent)

            }
        }






        if ((ESP_LIB_ESPApplication.getInstance()?.user?.role.equals(context?.getString(R.string.esp_lib_text_admin), ignoreCase = true)) ||
                ((isComingfromAssessor && criteriaListDAO!!.isOwner) && (ESPLIBDynamicStagesDAO?.status.equals(context?.getString(R.string.esp_lib_text_open), ignoreCase = true)
                        || ESPLIBDynamicStagesDAO?.status.equals(context?.getString(R.string.esp_lib_text_locked), ignoreCase = true)) &&
                        !criteriaListDAO.assessmentStatus.equals(context?.getString(R.string.esp_lib_text_accepted), ignoreCase = true) &&
                        !criteriaListDAO.assessmentStatus.equals(context?.getString(R.string.esp_lib_text_rejected), ignoreCase = true)))
            holder.ibReassignCard.visibility = View.VISIBLE
        else
            holder.ibReassignCard.visibility = View.GONE

        holder.ibReassignCard.setOnClickListener { v ->
            if (!criteriaListDAO?.assessmentStatus.equals(context?.getString(R.string.esp_lib_text_reassigned), ignoreCase = true))
                popUpMenu(v, criteriaListDAO)
        }

        if (criteriaListDAO?.comments != null)
            populateComments(criteriaListDAO, holder)


        if (mApplicationSectionsAdapterESPLIB?.itemCount == 0 && holder.rlacceptapprove.visibility == View.GONE)
            holder.ivexpandcollapserrow.visibility = View.GONE


    }//End Holder Class

    private fun populateComments(criteriaListDAO: ESP_LIB_DynamicStagesCriteriaListDAO, holder: ActivitiesList) {
        if (criteriaListDAO.comments!!.isNotEmpty()) {
            holder.llcriterialayout.visibility = View.VISIBLE
            val commentsList = ArrayList<ESP_LIB_DynamicStagesCriteriaCommentsListDAO>()
            for (i in criteriaListDAO.comments!!.indices) {
                val getComment = criteriaListDAO.comments?.get(i)
                commentsList.add(getComment!!)
            }
            val criteriaCommentAdapter = ESP_LIB_CriteriaCommentAdapter(context!!, commentsList)
            holder.rvCriteriaComments.adapter = criteriaCommentAdapter
        }
    }

    private fun moreInfo(holder: ActivitiesList, criteriaListDAOESPLIB: ESP_LIB_DynamicStagesCriteriaListDAO?, position: Int,
                         section: ArrayList<ESP_LIB_DynamicFormSectionDAO>) {
        holder.txtmoreinfo.text = context?.getString(R.string.esp_lib_text_lessinformation)
        holder.ivexpandcollapserrow.setImageResource(R.drawable.ic_more_info_up)
        holder.rvFields.visibility = View.VISIBLE

        if (section != null && section.size > 0) {
            for (sectionFields in section) {
                if (sectionFields.fieldsCardsList != null || sectionFields.fieldsCardsList!!.isNotEmpty()) {
                    for (i in 0 until sectionFields.fieldsCardsList!!.size) {
                        if (sectionFields.fieldsCardsList?.get(i)?.fields?.size!! > 0) {
                            if (criteriaListDAOESPLIB?.assessmentStatus.equals(context?.getString(R.string.esp_lib_text_activecaps), ignoreCase = true)) {
                                /*  holder.txtmoreinfo.visibility = View.VISIBLE
                                  holder.ivainforrow.visibility = View.VISIBLE*/

                                holder.txtmoreinfo.visibility = View.GONE
                                holder.ivainforrow.visibility = View.GONE
                            }
                        }

                    }
                }
            }
        } else {
            if (!isNotifyOnly) {
                holder.txtmoreinfo.visibility = View.GONE
                holder.ivainforrow.visibility = View.GONE
            }
        }

        ESP_LIB_ViewAnimationUtils.expand(holder.rvFields)

        actionButtons(criteriaListDAOESPLIB, holder, position)
    }

    private fun actionButtons(criteriaListDAOESPLIB: ESP_LIB_DynamicStagesCriteriaListDAO?, holder: ActivitiesList, position: Int) {
        val actualResponseJson = Gson().fromJson(actualResponseJson, ESP_LIB_DynamicResponseDAO::class.java)
        if (criteriaListDAOESPLIB?.assessmentStatus.equals(context?.getString(R.string.esp_lib_text_activecaps), ignoreCase = true)
                && criteriaListDAOESPLIB!!.isOwner && !actualResponseJson.applicationStatus.equals(context?.getString(R.string.esp_lib_text_rejected), ignoreCase = true)) {
            holder.rlacceptapprove.visibility = View.VISIBLE
            holder.pendinglineview.visibility = View.VISIBLE
        } else {
            holder.rlacceptapprove.visibility = View.GONE
        }
    }

    private fun iterateOnSection(section: ArrayList<ESP_LIB_DynamicFormSectionDAO>, holder: ActivitiesList, criteriaListDAOESPLIB: ESP_LIB_DynamicStagesCriteriaListDAO?) {

        if (criteriaListDAOESPLIB?.comments != null && criteriaListDAOESPLIB.comments!!.size > 0)
            holder.viewdivider.visibility = View.VISIBLE

        if (section != null && section.size > 0) {
            for (sectionFields in section) {
                for (i in 0 until sectionFields.fieldsCardsList!!.size) {
                    if (sectionFields.fieldsCardsList?.get(i)?.fields!!.size > 0) {
                        if (criteriaListDAOESPLIB?.assessmentStatus.equals(context?.getString(R.string.esp_lib_text_activecaps), ignoreCase = true)) {
                            holder.rvFields.visibility = View.GONE
                            holder.txtmoreinfo.visibility = View.GONE
                            holder.ivainforrow.visibility = View.GONE
                        } /*else {
                    holder.ivexpandcollapserrow.visibility = View.GONE
                }*/

                    } else
                        holder.ivexpandcollapserrow.visibility = View.GONE
                }
            }
        } else {
            if (!criteriaListDAOESPLIB!!.isOwner)
                holder.ivexpandcollapserrow.visibility = View.GONE
            else {
                holder.txtmoreinfo.visibility = View.GONE
                holder.ivainforrow.visibility = View.GONE
            }
        }
    }

    private fun GetStagesFieldsCards(stagesESPLIB: ESP_LIB_DynamicStagesCriteriaListDAO?): List<ESP_LIB_DynamicFormSectionDAO> {

        val sections = ArrayList<ESP_LIB_DynamicFormSectionDAO>()

        val getForm = stagesESPLIB?.form
        //Setting Sections With FieldsCards.
        var sectionDAO = ESP_LIB_DynamicFormSectionDAO()
        if (getForm?.sections != null && getForm.sections!!.size > 0) {
            for (st in 0 until getForm.sections!!.size) {
                sectionDAO = getForm.sections!![st]
                if (sectionDAO.fields != null && sectionDAO.fields!!.size > 0) {
                    sectionDAO.defaultName = stagesESPLIB.name
                    if (stagesESPLIB.formValues.size > 0) {
                        for (j in 0 until stagesESPLIB.formValues.size) {
                            val getSectionCustomFieldId = stagesESPLIB.formValues[j].sectionCustomFieldId
                            for (i in 0 until sectionDAO.fields!!.size) {


                                val sectionCustomFieldId = sectionDAO.fields!![i].sectionCustomFieldId
                                val getType = sectionDAO.fields!![i].type
                                if (sectionCustomFieldId == getSectionCustomFieldId) {
                                    var value = stagesESPLIB.formValues[j].value

                                    if (getType == 13) // lookupvalue
                                    {
                                        val fieldsCardsList = stagesESPLIB.form.sections?.get(0)?.fields?.get(j)
                                        //stages.formValues.get(j)=fieldsCardsList

                                        // value = stages.formValues[j].selectedLookupText

                                        /*  value = stages.formValues[j].selectedLookupText
                                          if (value == null)
                                              value = stages.formValues[j].value

                                          sectionDAO.fields!![i].lookupValue = value
                                          if (stages.formValues[j].value != null && Shared.getInstance().isNumeric(stages.formValues[j].value))
                                              sectionDAO.fields!![i].id = Integer.parseInt(stages.formValues[j].value!!)*/


                                        value = fieldsCardsList?.lookupValue
                                        if (value == null)
                                            value = stagesESPLIB.formValues[j].selectedLookupText

                                        sectionDAO.fields!![i].lookupValue = value
                                        if (stagesESPLIB.formValues[j].value != null && ESP_LIB_Shared.getInstance().isNumeric(stagesESPLIB.formValues[j].value))
                                            sectionDAO.fields!![i].id = Integer.parseInt(stagesESPLIB.formValues[j].value!!)

                                    }
                                    //  sectionDAO.fields!![i].value = value
                                    if (getType == 7) { // for attachments only
                                        try {
                                            val details = ESP_LIB_DyanmicFormSectionFieldDetailsDAO()
                                            details.downloadUrl = stagesESPLIB.formValues[j].details!!.downloadUrl
                                            details.mimeType = stagesESPLIB.formValues[j].details!!.mimeType
                                            details.createdOn = stagesESPLIB.formValues[j].details!!.createdOn
                                            details.name = stagesESPLIB.formValues[j].details!!.name
                                            sectionDAO.fields!![i].details = details
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }

                                    }

                                    /*if (value.isNullOrEmpty())
                                        value = "-"*/
                                    sectionDAO.fields!![i].value = value
                                    sectionDAO.fields!![i].sectionId = sectionDAO.id

                                }
                            }

                        }
                        ESP_LIB_Shared.getInstance().loadFeedback(sectionDAO, sections, stagesESPLIB)
                    } else {
                        ESP_LIB_Shared.getInstance().loadFeedback(sectionDAO, sections, stagesESPLIB)
                    }
                }

            }
        } else {
            ESP_LIB_Shared.getInstance().loadFeedback(sectionDAO, sections, stagesESPLIB)
        }


        return sections
    }

    private fun setStatusColor(holder: ActivitiesList, criteriaListDAOESPLIB: ESP_LIB_DynamicStagesCriteriaListDAO?,
                               section: ArrayList<ESP_LIB_DynamicFormSectionDAO>,
                               position: Int) {
        // holder.rlincludesignatue.visibility = View.GONE
        holder.txtstatus.visibility = View.VISIBLE
        val assessmentStatus = criteriaListDAOESPLIB?.assessmentStatus
        val actualResponseJson = Gson().fromJson(actualResponseJson, ESP_LIB_DynamicResponseDAO::class.java)
        holder.lldetail.setBackgroundResource(R.drawable.esp_lib_drawable_draw_bg_white)
        /*if (ESPApplication.getInstance().isComponent || ESPApplication.getInstance()?.user?.loginResponse?.role?.toLowerCase(Locale.getDefault()) == context?.getString(R.string.assessor))
            holder.lldetail.setBackgroundResource(R.drawable.draw_bg_white)
        else
            holder.lldetail.setBackgroundResource(R.drawable.draw_bg_white_grey_stroke)
*/
        if (assessmentStatus == null) {
            holder.txtline.setBackgroundColor(ContextCompat.getColor(context!!, R.color.esp_lib_color_status_new))
            return
        };

        if (actualResponseJson.applicationStatusId == 5) // cancalled
        {
            val status = context?.getString(R.string.esp_lib_text_cancelled)
            holder.txtstatus.text = status
            holder.ivainforrow.visibility = View.GONE
            holder.rlacceptapprove.visibility = View.GONE
            holder.ivexpandcollapserrow.visibility = View.GONE
            holder.txtstatus.setTextColor(ContextCompat.getColor(context!!, R.color.esp_lib_color_status_draft))
            holder.txtline.setBackgroundColor(ContextCompat.getColor(context!!, R.color.esp_lib_color_status_draft_background))
            return
        }

        // holder.txtstatus.setBackgroundResource(R.drawable.status_background)
        // val drawable = holder.txtstatus.getBackground() as GradientDrawable

        when (assessmentStatus) {
            context?.getString(R.string.esp_lib_text_invited) // Invited
            -> {
                holder.txtstatus.setTextColor(ContextCompat.getColor(context!!, R.color.esp_lib_color_status_invited))
                holder.txtline.setBackgroundColor(ContextCompat.getColor(context!!, R.color.esp_lib_color_status_invited))
                //   drawable.setColor(ContextCompat.getColor(context!!, R.color.status_invited_background))
            }
            context?.getString(R.string.esp_lib_text_neww) // New
            -> {
                holder.txtstatus.setTextColor(ContextCompat.getColor(context!!, R.color.esp_lib_color_status_new))
                holder.txtline.setBackgroundColor(ContextCompat.getColor(context!!, R.color.esp_lib_color_status_new))
                //   drawable.setColor(ContextCompat.getColor(context!!, R.color.status_new_background))
            }
            context?.getString(R.string.esp_lib_text_pending) // Pending
            -> {
                holder.txtstatus.text = context!!.getString(R.string.esp_lib_text_pending)
                holder.txtstatus.setTextColor(ContextCompat.getColor(context!!, R.color.esp_lib_color_status_pending))
                holder.txtline.setBackgroundColor(ContextCompat.getColor(context!!, R.color.esp_lib_color_status_pending))
                //   drawable.setColor(ContextCompat.getColor(context!!, R.color.status_pending_background))
            }
            context?.getString(R.string.esp_lib_text_inprogress) // inprogress
            -> {
                holder.txtstatus.text = context!!.getString(R.string.esp_lib_text_pending)
                holder.txtstatus.setTextColor(ContextCompat.getColor(context!!, R.color.esp_lib_color_status_pending))
                holder.txtline.setBackgroundColor(ContextCompat.getColor(context!!, R.color.esp_lib_color_status_pending))
                //  drawable.setColor(ContextCompat.getColor(context!!, R.color.status_pending_background))
            }
            context?.getString(R.string.esp_lib_text_accepted) // Accepted
            -> {

                holder.etxtsignLabel.hint = context?.getString(R.string.esp_lib_text_accepted)
                if (criteriaListDAOESPLIB.isSigned) {
                    populateData(criteriaListDAOESPLIB.signature, holder)
                    //  holder.rlincludesignatue.visibility = View.VISIBLE
                    holder.etxtsign.setBackgroundResource(R.drawable.esp_lib_drawable_ic_icon_signature_base)
                    hideAcceptRejectStatus(holder)
                }

                //  DrawableCompat.setTint(holder.etxtsign.background, ContextCompat.getColor(context!!, R.color.colorPrimaryDark));

                holder.txtstatus.setTextColor(ContextCompat.getColor(context!!, R.color.esp_lib_color_status_draft))
                holder.txtline.setBackgroundColor(ContextCompat.getColor(context!!, R.color.esp_lib_color_status_accepted))
                //  drawable.setColor(ContextCompat.getColor(context!!, R.color.status_accepted_background))
            }
            context?.getString(R.string.esp_lib_text_rejected)  // Rejected
            -> {
                rejectedCurve(holder, criteriaListDAOESPLIB)
                holder.txtstatus.setTextColor(ContextCompat.getColor(context!!, R.color.esp_lib_color_status_rejected))
                holder.txtline.setBackgroundColor(ContextCompat.getColor(context!!, R.color.esp_lib_color_status_rejected))
                //  drawable.setColor(ContextCompat.getColor(context!!, R.color.status_rejected_background))
            }

            context?.getString(R.string.esp_lib_text_cancelled)   // Cancelled
            -> {
                holder.txtstatus.setTextColor(ContextCompat.getColor(context!!, R.color.esp_lib_color_status_draft))
                holder.txtline.setBackgroundColor(ContextCompat.getColor(context!!, R.color.esp_lib_color_status_draft_background))
            }
            context?.getString(R.string.esp_lib_text_reassigned)  // reassign
            -> {

                holder.lldetail.setBackgroundResource(R.drawable.esp_lib_drawable_draw_bg_grey_stroke)
                holder.txtstatus.setTextColor(ContextCompat.getColor(context!!, R.color.esp_lib_color_coolgrey))
                holder.txtline.setBackgroundColor(ContextCompat.getColor(context!!, R.color.esp_lib_color_coolgrey))
                //   drawable.setColor(ContextCompat.getColor(context!!, R.color.transparent_color))
            }

            context?.getString(R.string.esp_lib_text_activecaps)  // Active
            -> {


                if (actualResponseJson.applicationStatus.equals(context?.getString(R.string.esp_lib_text_rejected), ignoreCase = true)) {
                    holder.txtstatus.setTextColor(ContextCompat.getColor(context!!, R.color.esp_lib_color_status_rejected))
                    holder.txtstatus.setText(context?.getString(R.string.esp_lib_text_rejected))
                    holder.txtline.setBackgroundColor(ContextCompat.getColor(context!!, R.color.esp_lib_color_status_rejected))
                    rejectedCurve(holder, criteriaListDAOESPLIB)
                    criteriaListDAOESPLIB.assessmentStatus = context?.getString(R.string.esp_lib_text_rejected)
                } else if (!criteriaListDAOESPLIB.isOwner) {
                    holder.txtstatus.setTextColor(ContextCompat.getColor(context!!, R.color.esp_lib_color_status_pending))
                    holder.txtstatus.text = context?.getString(R.string.esp_lib_text_pending)
                    holder.txtline.setBackgroundColor(ContextCompat.getColor(context!!, R.color.esp_lib_color_status_pending))
                    //   drawable.setColor(ContextCompat.getColor(context!!, R.color.status_pending_background))
                } else {

                    if (isNotifyOnly) {
                        if (holder.txtmoreinfo.text.equals(context?.getString(R.string.esp_lib_text_lessinformation))) {
                            moreInfo(holder, criteriaListDAOESPLIB, isNotifyOnlyPosition, section)
                        }
                    } else {
                        holder.ivexpandcollapserrow.post {

                            if (holder.txtmoreinfo.text.equals(context?.getString(R.string.esp_lib_text_moreinformation)) &&
                                    actualResponseJson.stageVisibilityApplicant.equals(context?.getString(R.string.esp_lib_text_no), ignoreCase = true)) {
                                holder.ivexpandcollapserrow.performClick()
                            } else if (holder.txtmoreinfo.text.equals(context?.getString(R.string.esp_lib_text_moreinformation)) && !ESP_LIB_ApplicationDetailScreenActivity.criteriaWasLoaded) {
                                if ((criteriaListESPLIB!!.size - 1) == position)
                                    ESP_LIB_ApplicationDetailScreenActivity.criteriaWasLoaded = true
                                holder.ivexpandcollapserrow.performClick()
                            }
                        }
                    }



                    holder.txtstatus.setTextColor(ContextCompat.getColor(context!!, R.color.colorPrimaryDark))
                    holder.txtstatus.text = context?.getString(R.string.esp_lib_text_opencaps)
                    holder.txtline.setBackgroundColor(ContextCompat.getColor(context!!, R.color.esp_lib_color_status_new))
                    //   drawable.setColor(ContextCompat.getColor(context!!, R.color.status_new_background))
                }
            }
            else -> {
                holder.txtstatus.text = context!!.getString(R.string.esp_lib_text_pending)
                holder.txtstatus.setTextColor(ContextCompat.getColor(context!!, R.color.esp_lib_color_status_pending))
                holder.txtline.setBackgroundColor(ContextCompat.getColor(context!!, R.color.esp_lib_color_status_pending))
            }
        }
    }

    private fun populateData(response: ESP_LIB_SignatureDAO?, holder: ActivitiesList) {
        if (response == null)
            return

        if (response.type.equals(context?.getString(R.string.esp_lib_text_font), ignoreCase = true)) {
            holder.rlincludesignatue.visibility = View.VISIBLE
            holder.llsignature.visibility = View.GONE

            holder.etxtsign.setText(response.signatoryName)


            val indexOf = rawStylesNames.indexOf(response.fontFamily)
            if (indexOf == -1) {
                val typeface = Typeface.createFromAsset(context?.assets, "font/greatvibes_regular.otf")
                holder.etxtsign.setTypeface(typeface)
            } else {
                holder.etxtsign.setTypeface(GoogleFontsLibrary.setGoogleFont(context, rawStyles[indexOf]))
            }

        } else {
            holder.rlincludesignatue.visibility = View.GONE
            holder.llsignature.visibility = View.VISIBLE

            Glide.with(context!!).load(response.file?.downloadUrl)
                    .error(R.drawable.esp_lib_drawable_default_profile_picture)
                    .into(holder.ivsignature)
        }
    }

    private fun hideAcceptRejectStatus(holder: ActivitiesList) {
        holder.txtstatus.visibility = View.GONE
        val params = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
        params.addRule(RelativeLayout.ALIGN_PARENT_END)
        holder.add_criteria_comments.layoutParams = params
    }

    private fun rejectedCurve(holder: ActivitiesList, criteriaListDAOESPLIB: ESP_LIB_DynamicStagesCriteriaListDAO) {
        holder.etxtsignLabel.hint = context?.getString(R.string.esp_lib_text_rejected)
        holder.etxtsignLabel.error = " "
        holder.etxtsignLabel.isErrorEnabled = true
        holder.etxtsignLabel.errorIconDrawable = null
        holder.etxtsignLabel.setErrorTextAppearance(R.style.Esp_Lib_Style_error_appearance)


        if (criteriaListDAOESPLIB.isSigned) {
            populateData(criteriaListDAOESPLIB.signature, holder)
            //holder.rlincludesignatue.visibility = View.VISIBLE
            holder.etxtsign.setBackgroundResource(R.drawable.esp_lib_drawable_ic_icon_signature_base_rejected)
            hideAcceptRejectStatus(holder)
        }

    }

    override fun getItemCount(): Int {
        return criteriaListESPLIB!!.size
    }

    fun notifyChangeIfAny(criteriaId: Int) {
        for (i in criteriaListESPLIB!!.indices) {
            if (criteriaId == criteriaListESPLIB?.get(i)?.id) {
                val isValidate = criteriaListESPLIB?.get(i)?.isValidate
                val tempBtApprove = rvCrietrias?.layoutManager?.findViewByPosition(i)?.findViewById<Button>(R.id.btapprove)
                val tempBtReject = rvCrietrias?.layoutManager?.findViewByPosition(i)?.findViewById<Button>(R.id.btreject)
                checkButtonStatus(tempBtApprove, tempBtReject, isValidate)

            }
        }


    }

    private fun checkButtonStatus(tempBtApprove: Button?, tempBtReject: Button?, isEnable: Boolean?) {
        when (isEnable) {
            true -> {
                tempBtApprove?.isEnabled = true
                tempBtApprove?.alpha = 1f
            }
            false -> {
                tempBtApprove?.isEnabled = false
                tempBtApprove?.alpha = 0.5f

            }

        }
        tempBtReject?.isEnabled = true
        tempBtReject?.alpha = 1f

    }

    private fun popUpMenu(view: View, criteriaListDAOESPLIB: ESP_LIB_DynamicStagesCriteriaListDAO?) {
        //creating a popup menu
        val popup = PopupMenu(context!!, view)
        popup.gravity = Gravity.CENTER

        try {
            // Reflection apis to enforce show icon
            val fields = popup.javaClass.declaredFields
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

        //inflating menu from xml resource
        popup.inflate(R.menu.menu_reassign)
        //adding click listener
        popup.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.action_reassign) {

                var intent = Intent(context, ESP_LIB_UsersList::class.java)
                intent.putExtra("criteriaListDAO", criteriaListDAOESPLIB)
                context?.startActivity(intent)


            }
            false
        }
        //displaying the popup
        popup.show()
    }

    fun getAllCriteriaFields(): List<ESP_LIB_DynamicFormSectionFieldDAO>? {
        val fields = java.util.ArrayList<ESP_LIB_DynamicFormSectionFieldDAO>()
        for (criterialist in criteriaListESPLIB!!) {
            for (sectionDAO in criterialist?.form?.sections!!) {
                if (sectionDAO.fieldsCardsList != null) {
                    for (sectionFieldsCardsDAO in sectionDAO.fieldsCardsList!!) {
                        fields.addAll(sectionFieldsCardsDAO.fields!!)
                    }
                }
            }

        }

        return fields
    }


}
