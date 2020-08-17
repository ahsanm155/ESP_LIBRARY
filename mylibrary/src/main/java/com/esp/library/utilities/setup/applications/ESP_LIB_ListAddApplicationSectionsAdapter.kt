package utilities.adapters.setup.applications

import android.graphics.Color
import android.os.Handler
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import com.esp.library.R
import com.esp.library.exceedersesp.controllers.applications.ESP_LIB_ApplicationDetailScreenActivity
import com.esp.library.utilities.common.ESP_LIB_SharedPreference
import com.esp.library.utilities.setup.applications.ESP_LIB_ApplicationFieldsRecyclerAdapter
import com.esp.library.exceedersesp.ESP_LIB_BaseActivity
import com.esp.library.exceedersesp.controllers.applications.ESP_LIB_ActivityStageDetails
import com.esp.library.exceedersesp.controllers.applications.ESP_LIB_ApplicationFeedDetailScreenActivity
import com.esp.library.utilities.common.ESP_LIB_ViewAnimationUtils
import com.esp.library.utilities.setup.applications.ESP_LIB_ListUsersApplicationsAdapterV2
import com.google.gson.Gson
import utilities.data.applicants.dynamics.*
import utilities.interfaces.ESP_LIB_FeedbackSubmissionClick


class ESP_LIB_ListAddApplicationSectionsAdapter(mApplication: ArrayList<ESP_LIB_DynamicFormSectionDAO>?, con: ESP_LIB_BaseActivity, internal var searched_text: String,
                                                internal var isViewOnly: Boolean) : androidx.recyclerview.widget.RecyclerView.Adapter<ESP_LIB_ListAddApplicationSectionsAdapter.ParentViewHolder>() {
    private var actualResponseJson: String? = null
    internal var pref: ESP_LIB_SharedPreference
    private var context: ESP_LIB_BaseActivity
    var listener: ESP_LIB_FeedbackSubmissionClick? = null
    var isEnable: Boolean? = false
    var criteriaListDAOESPLIB: ESP_LIB_DynamicStagesCriteriaListDAO? = null
    var mApplicationFieldsCardsAdapterESPLIB: ESP_LIB_ListAddApplicationSectionFieldsCardsAdapter? = null;
    var ESPLIBDynamicStagesCriteriaListDAO: ESP_LIB_DynamicStagesCriteriaListDAO? = null
    private var mApplications: ArrayList<ESP_LIB_DynamicFormSectionDAO>? = null

    private var mESPLIBApplicationFieldsAdapterListener: ESP_LIB_ApplicationFieldsRecyclerAdapter.ApplicationFieldsAdapterListener? = null
    private var mESPLIBApplicationFieldsDetailAdapterListener: ESP_LIB_ApplicationFieldsRecyclerAdapter.ApplicationDetailFieldsAdapterListener? = null
    private var mESPLIBApplicationFeedFieldsDetailAdapterListener: ESP_LIB_ApplicationFieldsRecyclerAdapter.ApplicationDetailFieldsAdapterListener? = null

    fun setmApplicationFieldsAdapterListener(mESPLIBApplicationFieldsAdapterListener: ESP_LIB_ApplicationFieldsRecyclerAdapter.ApplicationFieldsAdapterListener) {
        this.mESPLIBApplicationFieldsAdapterListener = mESPLIBApplicationFieldsAdapterListener
    }

    fun setmApplicationFieldsAdapterListener2(mESPLIBApplicationFieldsDetailAdapterListener: ESP_LIB_ApplicationDetailScreenActivity) {
        this.mESPLIBApplicationFieldsDetailAdapterListener = mESPLIBApplicationFieldsDetailAdapterListener
    }

    fun setmApplicationFieldsAdapterListenerFeed(mESPLIBApplicationFeedFieldsDetailAdapterListener: ESP_LIB_ApplicationFeedDetailScreenActivity) {
        this.mESPLIBApplicationFeedFieldsDetailAdapterListener = mESPLIBApplicationFeedFieldsDetailAdapterListener
    }

    fun setmApplicationFieldsAdapterListener2(mApplicationFieldsDetailAdapterListener: ESP_LIB_ActivityStageDetails) {
        this.mESPLIBApplicationFieldsDetailAdapterListener = mApplicationFieldsDetailAdapterListener
    }


    fun getCriteriaObject(criterialistDAOESPLIB: ESP_LIB_DynamicStagesCriteriaListDAO?) {
        criteriaListDAOESPLIB = criterialistDAOESPLIB;
    }


    open class ParentViewHolder(v: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(v)

    inner class ActivitiesList(v: View) : ParentViewHolder(v) {

        internal var tvSectionHeader: TextView
        internal var txtaddsectionbutton: TextView
        internal var tvSectionHeaderCount: TextView
        internal var tvSectionLabelsName: TextView
        internal var vsperatorTop: View
        internal var vbottomSeperator: View
        internal var rlsection: RelativeLayout
        internal var ibshowoptions: ImageButton
        internal var ivarrow: ImageButton
        internal var rvFieldsCards: androidx.recyclerview.widget.RecyclerView
        internal var rladdnewsection: RelativeLayout
        internal var parentlayout: LinearLayout
        internal var viewsectionbottom: View

        init {
            ibshowoptions = itemView.findViewById(R.id.ibshowoptions)
            ivarrow = itemView.findViewById(R.id.ivarrow)
            viewsectionbottom = itemView.findViewById(R.id.viewsectionbottom)
            tvSectionHeaderCount = itemView.findViewById(R.id.tvSectionHeaderCount)
            tvSectionLabelsName = itemView.findViewById(R.id.tvSectionLabelsName)
            tvSectionHeader = itemView.findViewById(R.id.tvSectionHeader)
            rvFieldsCards = itemView.findViewById(R.id.rvFieldsCards)
            rladdnewsection = itemView.findViewById(R.id.rladdnewsection)
            txtaddsectionbutton = itemView.findViewById(R.id.txtaddsectionbutton)
            rlsection = itemView.findViewById(R.id.rlsection)
            parentlayout = itemView.findViewById(R.id.parentlayout)
            vsperatorTop = itemView.findViewById(R.id.vsperatorTop)
            vbottomSeperator = itemView.findViewById(R.id.vbottomSeperator)

        }

    }

    init {
        context = con
        mApplications = mApplication

        pref = ESP_LIB_SharedPreference(context)
        try {
            listener = context as ESP_LIB_FeedbackSubmissionClick
        } catch (e: Exception) {

        }
    }

    fun getmApplications(): List<ESP_LIB_DynamicFormSectionDAO>? {
        return mApplications
    }


    fun setActualResponseJson(actualResponseJson: String) {
        this.actualResponseJson = actualResponseJson
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParentViewHolder {
        val v: View = LayoutInflater.from(parent.context).inflate(R.layout.esp_lib_repeater_add_application_section, parent, false)
        return ActivitiesList(v)
    }
/*
    private fun setTextStyle(holder: ActivitiesList) {
        holder.rvFieldsCards.setPadding(0, 20, 0, 0)
        holder.tvSectionHeader.setPadding(40, 24, 0, 24)
        holder.tvSectionHeader.setTextColor(Color.BLACK)
        holder.tvSectionHeader.textSize = 18f
        val typeface = Typeface.createFromAsset(context.assets, "font/lato/lato_medium.ttf")
        holder.tvSectionHeader.setTypeface(typeface);
        holder.viewsectionbottom.visibility = View.VISIBLE

    }

    private fun setSubmissionTextStyle(holder: ActivitiesList) {
        val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(20, 0, 10, 10)
        holder.rvFieldsCards.setLayoutParams(params)
        // holder.rvFieldsCards.setPadding(0, 0, 0, 0)
        holder.tvSectionHeader.setPadding(50, 24, 0, 13)
        holder.tvSectionHeader.setTextColor(Color.BLACK)
        holder.tvSectionHeader.textSize = 15f
        val typeface = Typeface.createFromAsset(context.assets, "font/lato/lato_medium.ttf")
        holder.tvSectionHeader.setTypeface(typeface);

    }*/


    override fun onBindViewHolder(holder_parent: ParentViewHolder, position: Int) {

        val dynamicFormSectionDAO = mApplications?.get(position)

        val sectionType = dynamicFormSectionDAO?.type
        val holder = holder_parent as ActivitiesList

        if (position > 0)
            holder.vsperatorTop.visibility = View.GONE

        ESPLIBDynamicStagesCriteriaListDAO = dynamicFormSectionDAO?.dynamicStagesCriteriaListDAO

        if (ESPLIBDynamicStagesCriteriaListDAO?.assessmentStatus != null) {
            if (pref.language.equals("ar", ignoreCase = true))
                holder.tvSectionHeader.gravity = Gravity.END
            else
                holder.tvSectionHeader.gravity = Gravity.START

            isViewOnly = !ESPLIBDynamicStagesCriteriaListDAO?.assessmentStatus.equals(context.getString(R.string.esp_lib_text_active), ignoreCase = true)
            //holder.tvSectionHeader.text = dynamicFormSectionDAO.defaultName
            holder.tvSectionHeader.text = ESPLIBDynamicStagesCriteriaListDAO?.name
            holder.tvSectionHeaderCount.text = (position + 1).toString()

            holder.tvSectionHeader.visibility = View.GONE

        } else {
            holder.tvSectionHeader.visibility = View.GONE

            if (dynamicFormSectionDAO?.defaultName != null && !dynamicFormSectionDAO.defaultName!!.isEmpty()) {
                //   setSubmissionTextStyle(holder)
                holder.tvSectionHeader.visibility = View.VISIBLE
                if (isViewOnly) {
                    //  holder.tvSectionHeader.setPadding(36, 0, 0, 0)
                    if (ESP_LIB_ListUsersApplicationsAdapterV2.isSubApplications) {
                        //setSubmissionTextStyle(holder)
                        if (position > 0)
                            holder.viewsectionbottom.visibility = View.VISIBLE
                    } else {
                        holder.viewsectionbottom.visibility = View.GONE
                        //  holder.tvSectionHeader.setTextColor(ContextCompat.getColor(context, R.color.cooltwogrey))
                        // holder.tvSectionHeader.setPadding(40, 0, 0, 0)
                    }
                    holder.tvSectionHeader.text = dynamicFormSectionDAO.defaultName
                    holder.tvSectionHeaderCount.text = (position + 1).toString()

                } else {
                    holder.tvSectionHeader.text = dynamicFormSectionDAO.defaultName
                    holder.tvSectionHeaderCount.text = (position + 1).toString()
                }

            } else {
                if (position > 0)
                    holder.viewsectionbottom.visibility = View.VISIBLE
            }
        }
        holder.rladdnewsection.visibility = View.GONE

        if (criteriaListDAOESPLIB != null) {
            holder.tvSectionHeaderCount.visibility = View.GONE
            holder.vbottomSeperator.visibility = View.GONE
            holder.vsperatorTop.visibility = View.GONE
            holder.ivarrow.visibility = View.GONE
        }

        if (dynamicFormSectionDAO!!.isMultipule && holder.tvSectionLabelsName.visibility == View.GONE) {
            if (dynamicFormSectionDAO.fieldsCardsList!!.isNotEmpty() &&
                    dynamicFormSectionDAO.fieldsCardsList?.get(0)?.fields!!.isNotEmpty()) {
                holder.rladdnewsection.visibility = View.VISIBLE
            } else {
                holder.tvSectionHeader.visibility = View.GONE
            }
        }
        if (isViewOnly) {
            holder.rladdnewsection.visibility = View.GONE
            holder.parentlayout.setBackgroundColor(Color.WHITE)
        }

        holder.txtaddsectionbutton.text = context.getString(R.string.esp_lib_text_add) + " " + holder.tvSectionHeader.text


        //recycler view for items
        holder.rvFieldsCards.setHasFixedSize(true)
        holder.rvFieldsCards.isNestedScrollingEnabled = false
        val linearLayoutManager = androidx.recyclerview.widget.LinearLayoutManager(context, androidx.recyclerview.widget.LinearLayoutManager.VERTICAL, false)
        holder.rvFieldsCards.layoutManager = linearLayoutManager



        holder.ivarrow.setOnClickListener {
            if (holder.rvFieldsCards.visibility == View.VISIBLE) {


                ESP_LIB_ViewAnimationUtils.collapse(holder.rvFieldsCards,holder.tvSectionLabelsName)

                val handler = Handler()
                handler.postDelayed({
                    holder.rvFieldsCards.visibility = View.GONE

                    holder.tvSectionLabelsName.visibility = View.VISIBLE
                   // holder.rlsection.setBackgroundColor(ContextCompat.getColor(context, R.color.esp_lib_color_pale_grey))
                    //  holder.tvSectionHeaderCount.setTextColor(ContextCompat.getColor(context, R.color.grey))
                    holder.ivarrow.setImageResource(R.drawable.ic_arrow_down)
                    val concateLabel = StringBuilder()
                    for (i in dynamicFormSectionDAO.fieldsCardsList!!.indices) {

                        for (j in dynamicFormSectionDAO.fieldsCardsList!![i].fields!!.indices) {
                            val label = dynamicFormSectionDAO.fieldsCardsList!![i].fields?.get(j)?.label
                            concateLabel.append(label)
                            concateLabel.append(", ")
                        }
                    }

                    var fieldLabels: String
                    fieldLabels = concateLabel.toString().trim()
                    if (fieldLabels.endsWith(",")) {
                        fieldLabels = fieldLabels.substring(0, fieldLabels.length - 1);
                    }
                    holder.tvSectionLabelsName.text = fieldLabels

                    if (dynamicFormSectionDAO.isMultipule)
                        holder.rladdnewsection.visibility = View.GONE
                }, 300)


            } else {
                if (dynamicFormSectionDAO.isMultipule && !isViewOnly)
                    holder.rladdnewsection.visibility = View.VISIBLE
                holder.rvFieldsCards.visibility = View.VISIBLE
                holder.tvSectionLabelsName.visibility = View.GONE
              //  holder.rlsection.setBackgroundColor(ContextCompat.getColor(context, R.color.esp_lib_color_white))
                //   holder.tvSectionHeaderCount.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
                holder.ivarrow.setImageResource(R.drawable.ic_arrow_up)
                ESP_LIB_ViewAnimationUtils.expand(holder.rvFieldsCards)


            }
        }

        //    var getAssessmentStatus = dynamicStagesCriteriaListDAO?.assessmentStatus


        val refinementOfFieldCardList = refinementOfFieldCardList(dynamicFormSectionDAO.fieldsCardsList)
        dynamicFormSectionDAO.fieldsCardsList = refinementOfFieldCardList

        mApplicationFieldsCardsAdapterESPLIB = actualResponseJson?.let {
            ESP_LIB_ListAddApplicationSectionFieldsCardsAdapter(
                    dynamicFormSectionDAO.fieldsCardsList as MutableList<ESP_LIB_DynamicFormSectionFieldsCardsDAO>,
                    context, "", it, isViewOnly, ESPLIBDynamicStagesCriteriaListDAO, sectionType!!)
        }

        if (criteriaListDAOESPLIB != null)
            mApplicationFieldsCardsAdapterESPLIB?.getCriteriaObject(criteriaListDAOESPLIB)

        mApplicationFieldsCardsAdapterESPLIB?.setmApplications(mApplications!!)
        mApplicationFieldsCardsAdapterESPLIB?.setSectionHeader(holder.tvSectionHeader.text.toString())
        mApplicationFieldsCardsAdapterESPLIB?.isMultipleSection(dynamicFormSectionDAO.isMultipule)



        holder.rvFieldsCards.adapter = mApplicationFieldsCardsAdapterESPLIB


        // setAdapterData(position,dynamicStagesCriteriaListDAO,sectionType,holder)

        mApplicationFieldsCardsAdapterESPLIB?.setmApplicationFieldsAdapterListener(object : ESP_LIB_ApplicationFieldsRecyclerAdapter.ApplicationFieldsAdapterListener {

            override fun onFieldValuesChanged() {

                if (mESPLIBApplicationFieldsAdapterListener != null) {
                    mESPLIBApplicationFieldsAdapterListener!!.onFieldValuesChanged()
                } else if (mESPLIBApplicationFieldsDetailAdapterListener != null) {
                    mESPLIBApplicationFieldsDetailAdapterListener!!.onFieldValuesChanged()
                }
                else if (mESPLIBApplicationFeedFieldsDetailAdapterListener != null) {
                    mESPLIBApplicationFeedFieldsDetailAdapterListener!!.onFieldValuesChanged()
                }
            }

            override fun onAttachmentFieldClicked(fieldDAOESPLIB: ESP_LIB_DynamicFormSectionFieldDAO, position: Int) {

                if (mESPLIBApplicationFieldsAdapterListener != null) {
                    mESPLIBApplicationFieldsAdapterListener!!.onAttachmentFieldClicked(fieldDAOESPLIB, position)
                } else if (mESPLIBApplicationFieldsDetailAdapterListener != null) {
                    mESPLIBApplicationFieldsDetailAdapterListener!!.onAttachmentFieldClicked(fieldDAOESPLIB, position)
                }
                else if (mESPLIBApplicationFeedFieldsDetailAdapterListener != null) {
                    mESPLIBApplicationFeedFieldsDetailAdapterListener!!.onAttachmentFieldClicked(fieldDAOESPLIB, position)
                }
            }

            override fun onLookupFieldClicked(fieldDAOESPLIB: ESP_LIB_DynamicFormSectionFieldDAO, position: Int, isCalculatedMappedField: Boolean) {

                if (mESPLIBApplicationFieldsAdapterListener != null) {
                    mESPLIBApplicationFieldsAdapterListener!!.onLookupFieldClicked(fieldDAOESPLIB, position, isCalculatedMappedField)
                } else if (mESPLIBApplicationFieldsDetailAdapterListener != null) {
                    mESPLIBApplicationFieldsDetailAdapterListener!!.onLookupFieldClicked(fieldDAOESPLIB, position, isCalculatedMappedField)
                } else if (mESPLIBApplicationFeedFieldsDetailAdapterListener != null) {
                    mESPLIBApplicationFeedFieldsDetailAdapterListener!!.onLookupFieldClicked(fieldDAOESPLIB, position, isCalculatedMappedField)
                }
            }
        })


        holder.rladdnewsection.setOnClickListener {
            if (actualResponseJson != null && !actualResponseJson!!.isEmpty()) {
                val actualResponse = Gson().fromJson(actualResponseJson, ESP_LIB_DynamicResponseDAO::class.java)

                if (actualResponse != null) {
                    val sections = actualResponse.form?.sections
                    for (i in sections!!.indices) {
                        if (sections[i].id == dynamicFormSectionDAO.id) {
                            val fieldsList = sections[i].fields
                            val fieldsListTempArray = ArrayList<ESP_LIB_DynamicFormSectionFieldDAO>()
                            for (j in 0 until fieldsList!!.size) {
                                val fields = fieldsList.get(j)

                                if (fields.type == 18 || fields.type == 19)
                                    fields.value = ""

                                if (fields.isVisible) {
                                    fieldsListTempArray.add(fields)
                                }
                            }
                            val clonedFieldsCard = ESP_LIB_DynamicFormSectionFieldsCardsDAO(fieldsListTempArray)
                            mApplicationFieldsCardsAdapterESPLIB?.addCard(clonedFieldsCard, position)
                        }
                    }
                    // mApplicationFieldsCardsAdapter?.notifyDataSetChanged()
                    // setAdapterData(position,dynamicStagesCriteriaListDAO,sectionType,holder)
                    notifyItemChanged(position)
                }


            }
        }

        holder.ibshowoptions.setOnClickListener { view ->
            ShowMenu(view, ESPLIBDynamicStagesCriteriaListDAO)
        }
    }//End Holder Class



    private fun refinementOfFieldCardList(fieldsCardsListESPLIB: List<ESP_LIB_DynamicFormSectionFieldsCardsDAO>?): ArrayList<ESP_LIB_DynamicFormSectionFieldsCardsDAO> {
        val tempFieldsCardsList = ArrayList<ESP_LIB_DynamicFormSectionFieldsCardsDAO>()
        for (i in 0 until fieldsCardsListESPLIB!!.size) {
            val getFieldsCard = fieldsCardsListESPLIB.get(i)
            if (getFieldsCard.fields!!.size > 0) {
                tempFieldsCardsList.add(getFieldsCard)
            }
        }

        return tempFieldsCardsList;
    }

    private fun ShowMenu(v: View, criteriaESPLIB: ESP_LIB_DynamicStagesCriteriaListDAO?) {
        val popup = PopupMenu(context, v)
        popup.inflate(R.menu.menu_status);
        val menuOpts = popup.menu
        val findItem_accept = menuOpts.findItem(R.id.action_accept)
        val findItem_reject = menuOpts.findItem(R.id.action_reject)

        val POPUP_CONSTANT = "mPopup"
        val POPUP_FORCE_SHOW_ICON = "setForceShowIcon"

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


        findItem_reject.isVisible = !criteriaESPLIB?.type.equals(context.getString(R.string.esp_lib_text_feedback), ignoreCase = true)


        when (isEnable) {
            true -> findItem_accept?.isEnabled = true
            false -> findItem_accept?.isEnabled = false
        }
        if (!criteriaESPLIB?.approveText.isNullOrEmpty())
            findItem_accept.title = criteriaESPLIB?.approveText

        if (!criteriaESPLIB?.rejectText.isNullOrEmpty())
            findItem_reject.title = criteriaESPLIB?.rejectText


        popup.setOnMenuItemClickListener { item ->
            val id = item.itemId
            if (id == R.id.action_accept) {
                // listener?.feedbackClick(true, criteriaListDAO, dynamicStagesCriteriaListDAO)
            } else if (id == R.id.action_reject) {
                //  listener?.feedbackClick(false, criteriaListDAO, dynamicStagesCriteriaListDAO)
            }
            false
        }
        // popup.inflate(R.menu.menu_status)
        popup.show()

    }

    override fun getItemCount(): Int {
        return mApplications?.size ?: 0

    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    fun RefreshList() {
        notifyDataSetChanged()
    }

    fun GetAllFields(): List<ESP_LIB_DynamicFormSectionFieldDAO>? {
        if (mApplications != null && mApplications!!.size > 0) {

            val fields = ArrayList<ESP_LIB_DynamicFormSectionFieldDAO>()

            for (sectionDAO in mApplications!!) {

                if (sectionDAO.fieldsCardsList!!.size > 0) {

                    for (sectionFieldsCardsDAO in sectionDAO.fieldsCardsList!!) {
                        fields.addAll(sectionFieldsCardsDAO.fields!!)
                    }

                }

            }

            return fields

        }

        return null
    }

    companion object {

        private val LOG_TAG = "ListAddApplicationSectionsAdapter"


    }

}
