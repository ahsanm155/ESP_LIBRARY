package utilities.adapters.setup.applications

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import com.esp.library.R
import com.esp.library.utilities.common.ESP_LIB_Shared
import com.esp.library.utilities.setup.applications.ESP_LIB_ApplicationFieldsRecyclerAdapter
import com.esp.library.exceedersesp.ESP_LIB_BaseActivity
import utilities.data.applicants.dynamics.ESP_LIB_DynamicFormSectionDAO
import utilities.data.applicants.dynamics.ESP_LIB_DynamicFormSectionFieldDAO
import utilities.data.applicants.dynamics.ESP_LIB_DynamicFormSectionFieldsCardsDAO
import utilities.data.applicants.dynamics.ESP_LIB_DynamicStagesCriteriaListDAO


class ESP_LIB_ListAddApplicationSectionFieldsCardsAdapter(private val mApplicationSectionFieldsCardESPLIBS: MutableList<ESP_LIB_DynamicFormSectionFieldsCardsDAO>, con: ESP_LIB_BaseActivity,
                                                          internal var searched_text: String, internal var actualResponseJson: String, isViewOnly: Boolean, ESPLIBDynamicStagesCriteriaListDAO: ESP_LIB_DynamicStagesCriteriaListDAO?, sectionType: Int) : androidx.recyclerview.widget.RecyclerView.Adapter<ESP_LIB_ListAddApplicationSectionFieldsCardsAdapter.ParentViewHolder>() {

    private val TAG = javaClass.simpleName
    internal var isViewOnly: Boolean = false
    internal var ESPLIBDynamicStagesCriteriaListDAO: ESP_LIB_DynamicStagesCriteriaListDAO? = null
    private var context: ESP_LIB_BaseActivity
    var sectionType: Int? = null
    var textSectionHeader: String? = null
    var isMultipleSection: Boolean=false
    var criteriaListDAOESPLIB: ESP_LIB_DynamicStagesCriteriaListDAO? = null
    var mApplications: List<ESP_LIB_DynamicFormSectionDAO>? = null
    var mESPLIBApplicationFieldsRecyclerAdapter: ESP_LIB_ApplicationFieldsRecyclerAdapter? = null


    private var mESPLIBApplicationFieldsAdapterListener: ESP_LIB_ApplicationFieldsRecyclerAdapter.ApplicationFieldsAdapterListener? = null

    fun setmApplicationFieldsAdapterListener(mESPLIBApplicationFieldsAdapterListener: ESP_LIB_ApplicationFieldsRecyclerAdapter.ApplicationFieldsAdapterListener) {
        this.mESPLIBApplicationFieldsAdapterListener = mESPLIBApplicationFieldsAdapterListener
    }

    fun getCriteriaObject(criterialistDAOESPLIB: ESP_LIB_DynamicStagesCriteriaListDAO?) {
        criteriaListDAOESPLIB = criterialistDAOESPLIB;
    }

    fun setmApplications(applications: List<ESP_LIB_DynamicFormSectionDAO>) {
        mApplications = applications
    }

    fun getFieldsAdapterReference(): ESP_LIB_ApplicationFieldsRecyclerAdapter? {
        return mESPLIBApplicationFieldsRecyclerAdapter
    }

    fun setSectionHeader(textSectionHeader: String) {
        this.textSectionHeader = textSectionHeader;
    }
    fun isMultipleSection(isMultipleSection: Boolean) {
        this.isMultipleSection = isMultipleSection;
    }

    open class ParentViewHolder(v: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(v)

    inner class ActivitiesList(v: View) : ParentViewHolder(v) {

        private val POPUP_CONSTANT = "mPopup"
        private val POPUP_FORCE_SHOW_ICON = "setForceShowIcon"
        internal var ibRemoveCard: ImageButton
        internal var tvSectionSubHeader: TextView
        internal var rvFields: androidx.recyclerview.widget.RecyclerView
        internal var llparentlayout: LinearLayout? = null

        init {
            rvFields = itemView.findViewById(R.id.rvFields)
            ibRemoveCard = itemView.findViewById(R.id.ibRemoveCard)
            tvSectionSubHeader = itemView.findViewById(R.id.tvSectionSubHeader)
            try {
                llparentlayout = itemView.findViewById(R.id.llparentlayout)
            } catch (e: java.lang.Exception) {

            }

            ibRemoveCard.setOnClickListener { view ->
                //creating a popup menu
                val popup = PopupMenu(context, view)

                //inflating menu from xml resource
                popup.inflate(R.menu.menu_list_item_add_application_fields)
                popup.gravity = Gravity.CENTER
                //val menuOpts = popup.menu


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

                //adding click listener
                popup.setOnMenuItemClickListener { menuItem ->
                    val id = menuItem.itemId

                    if (id == R.id.action_remove) {
                        //handle menu1 click
                        mApplicationSectionFieldsCardESPLIBS.removeAt(adapterPosition)
                        notifyDataSetChanged()
                    }
                    false
                }
                //displaying the popup
                popup.show()
            }
        }


    }

    init {
        var getAssessmentStatus = ESPLIBDynamicStagesCriteriaListDAO?.assessmentStatus
        context = con
        this.isViewOnly = isViewOnly
        this.sectionType = sectionType
        this.ESPLIBDynamicStagesCriteriaListDAO = ESPLIBDynamicStagesCriteriaListDAO
        if (getAssessmentStatus == null)
            getAssessmentStatus = ""
        if (this.isViewOnly && getAssessmentStatus.equals(context.getString(R.string.esp_lib_text_active), ignoreCase = true))
            this.isViewOnly = false

    }

    private fun setPadding(holder: ActivitiesList) {
        val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        )
        params.setMargins(40,0,40,0)
        holder.rvFields.setLayoutParams(params)
        holder.rvFields.setPadding(50, 30, 50, 0)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParentViewHolder {
        val v: View

        if (isViewOnly)
            v = LayoutInflater.from(parent.context).inflate(R.layout.esp_lib_repeater_add_application_section_fields_view, parent, false)
        else
            v = LayoutInflater.from(parent.context).inflate(R.layout.esp_lib_repeater_add_application_section_fields_card, parent, false)
        return ActivitiesList(v)
    }


    override fun onBindViewHolder(holder_parent: ParentViewHolder, position: Int) {

        val holder = holder_parent as ActivitiesList


        /*if (ListUsersApplicationsAdapter.isSubApplications)
            holder.llparentlayout?.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
        else
            holder.llparentlayout?.setBackgroundResource(R.drawable.draw_bg_pale_grey);*/


     //   if (mApplicationSectionFieldsCards.size > 1) {
            if (!isViewOnly && isMultipleSection) {
                holder.tvSectionSubHeader.visibility = View.VISIBLE
                holder.tvSectionSubHeader.text = textSectionHeader + " # " + (position + 1)
            }
            else
                holder.tvSectionSubHeader.visibility = View.GONE
            if (isViewOnly && isMultipleSection) {
                holder.rvFields.setBackgroundResource(R.drawable.esp_lib_drawable_draw_bg_pale_grey)
                setPadding(holder)
            }
        /*} else {
            holder.tvSectionSubHeader.visibility = View.GONE
        }*/


        holder.ibRemoveCard.visibility = View.GONE
        if (itemCount > 1) {
            holder.ibRemoveCard.visibility = View.VISIBLE
        }

        if (isViewOnly)
            holder.ibRemoveCard.visibility = View.GONE

        //recycler view for fields
        holder.rvFields.setHasFixedSize(true)
        holder.rvFields.isNestedScrollingEnabled = false

        val linearLayoutManager = androidx.recyclerview.widget.LinearLayoutManager(context, androidx.recyclerview.widget.LinearLayoutManager.VERTICAL, false)
        holder.rvFields.layoutManager = linearLayoutManager


        mESPLIBApplicationFieldsRecyclerAdapter = sectionType?.let {
            ESP_LIB_ApplicationFieldsRecyclerAdapter(actualResponseJson, isViewOnly,
                    context, ESPLIBDynamicStagesCriteriaListDAO, it)
        }



        mESPLIBApplicationFieldsRecyclerAdapter?.getCriteriaObject(criteriaListDAOESPLIB)
        mESPLIBApplicationFieldsRecyclerAdapter?.setRefreshList(mApplicationSectionFieldsCardESPLIBS[position].fields)
        mESPLIBApplicationFieldsRecyclerAdapter?.setmApplications(mApplications)
        holder.rvFields.adapter = mESPLIBApplicationFieldsRecyclerAdapter
        mESPLIBApplicationFieldsRecyclerAdapter?.setmApplicationFieldsAdapterListener(object : ESP_LIB_ApplicationFieldsRecyclerAdapter.ApplicationFieldsAdapterListener {

            override fun onFieldValuesChanged() {

                if (mESPLIBApplicationFieldsAdapterListener != null) {
                    mESPLIBApplicationFieldsAdapterListener!!.onFieldValuesChanged()
                }
            }

            override fun onAttachmentFieldClicked(fieldDAOESPLIB: ESP_LIB_DynamicFormSectionFieldDAO, position: Int) {

                if (mESPLIBApplicationFieldsAdapterListener != null) {
                    mESPLIBApplicationFieldsAdapterListener!!.onAttachmentFieldClicked(fieldDAOESPLIB, position)
                }

            }

            override fun onLookupFieldClicked(fieldDAOESPLIB: ESP_LIB_DynamicFormSectionFieldDAO, position: Int, isCalculatedMappedField: Boolean) {

                if (mESPLIBApplicationFieldsAdapterListener != null) {
                    mESPLIBApplicationFieldsAdapterListener!!.onLookupFieldClicked(fieldDAOESPLIB, position, isCalculatedMappedField)
                }
            }
        })


    }//End Holder Class


    override fun getItemCount(): Int {
        return mApplicationSectionFieldsCardESPLIBS.size

    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }


    fun addCard(cardCopy: ESP_LIB_DynamicFormSectionFieldsCardsDAO, position: Int) {
        val fieldCardList = ESP_LIB_Shared.getInstance().addData(mApplications?.get(position)?.getFieldsCardsList(), cardCopy)
        //   mApplications?.get(position)?.setFieldsCardsList(fieldCardList)
        notifyDataSetChanged()
    }


}
