package com.esp.library.exceedersesp.controllers.Profile.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.widget.PopupMenu
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import com.esp.library.R
import com.esp.library.exceedersesp.controllers.Profile.ESP_LIB_EditSectionDetails
import com.esp.library.utilities.common.ESP_LIB_Shared
import com.esp.library.utilities.common.ESP_LIB_SharedPreference
import utilities.data.applicants.dynamics.ESP_LIB_DynamicFormSectionDAO
import utilities.data.applicants.dynamics.ESP_LIB_DynamicFormSectionFieldDAO
import utilities.data.applicants.dynamics.ESP_LIB_DynamicFormSectionFieldsCardsDAO
import utilities.data.applicants.profile.ESP_LIB_ApplicationProfileDAO


class ESP_LIB_ListofSectionsAdapter(internal var ESPLIBDynamicFormSectionDAO: ESP_LIB_DynamicFormSectionDAO,
                                    internal var dataapplicant: ESP_LIB_ApplicationProfileDAO, internal var sectionsFieldESPLIBS: List<ESP_LIB_DynamicFormSectionFieldDAO>,
                                    private val context: Context, internal var ischeckerror: Boolean) : androidx.recyclerview.widget.RecyclerView.Adapter<ESP_LIB_ListofSectionsAdapter.ParentViewHolder>() {

    private val TAG = javaClass.simpleName
    internal var isViewOnly = false
    internal var pref: ESP_LIB_SharedPreference
    internal var fieldsCardsListESPLIB: List<ESP_LIB_DynamicFormSectionFieldsCardsDAO>


    init {
        fieldsCardsListESPLIB = ESPLIBDynamicFormSectionDAO.fieldsCardsList!!
        pref = ESP_LIB_SharedPreference(context)
    }


    open class ParentViewHolder(v: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(v)

    inner class ActivitiesList @SuppressLint("RestrictedApi")
    constructor(v: View) : ParentViewHolder(v) {

        internal var ibEditCard: ImageButton
        internal var rvFields: androidx.recyclerview.widget.RecyclerView
        internal var dividerview: View
        internal var llbasicinfo: LinearLayout
        internal var tNameLabel: TextView
        internal var tNameValue: TextView
        internal var tEmailLabel: TextView
        internal var tEmailValue: TextView
        internal var tProfileTypeLabel: TextView
        internal var tProfileTypeValue: TextView

        init {
            rvFields = itemView.findViewById(R.id.rvFields)
            ibEditCard = itemView.findViewById(R.id.ibRemoveCard)
            dividerview = itemView.findViewById(R.id.dividerview)
            llbasicinfo = itemView.findViewById(R.id.llbasicinfo)
            tNameLabel = itemView.findViewById(R.id.tNameLabel)
            tNameValue = itemView.findViewById(R.id.tNameValue)
            tEmailLabel = itemView.findViewById(R.id.tEmailLabel)
            tEmailValue = itemView.findViewById(R.id.tEmailValue)
            tProfileTypeLabel = itemView.findViewById(R.id.tProfileTypeLabel)
            tProfileTypeValue = itemView.findViewById(R.id.tProfileTypeValue)

            if (ESPLIBDynamicFormSectionDAO.isDefault) {
                llbasicinfo.visibility = View.VISIBLE
                val name = dataapplicant.applicant.name
                val emailAddress = dataapplicant.applicant.emailAddress
                var profileTemplateString = dataapplicant.applicant.profileTemplateString

                if (TextUtils.isEmpty(profileTemplateString))
                {
                    profileTemplateString = context.getString(R.string.esp_lib_text_base)
                    dataapplicant.applicant.profileTemplateString=profileTemplateString
                }

                tNameLabel.text = context.getString(R.string.esp_lib_text_name)
                tNameValue.text = name
                tEmailLabel.text = context.getString(R.string.esp_lib_text_email)
                tEmailValue.text = emailAddress
                tProfileTypeLabel.text = context.getString(R.string.esp_lib_text_profiletype)
                tProfileTypeValue.text = profileTemplateString
            } else
                llbasicinfo.visibility = View.GONE


        }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParentViewHolder {
        val v: View
        if (isViewOnly)
            v = LayoutInflater.from(parent.context).inflate(R.layout.esp_lib_repeater_add_application_section_fields_view, parent, false)
        else
            v = LayoutInflater.from(parent.context).inflate(R.layout.esp_lib_list_of_sections_row, parent, false)
        return ActivitiesList(v)
    }

    override fun onBindViewHolder(holder_parent: ParentViewHolder, position: Int) {

        val holder = holder_parent as ActivitiesList

        if (fieldsCardsListESPLIB.size > 1)
            holder.dividerview.visibility = View.VISIBLE

        //recycler view for fields
        holder.rvFields.setHasFixedSize(true)
        holder.rvFields.isNestedScrollingEnabled = false

        val linearLayoutManager = androidx.recyclerview.widget.LinearLayoutManager(context, androidx.recyclerview.widget.LinearLayoutManager.VERTICAL, false)
        holder.rvFields.layoutManager = linearLayoutManager

        // val adapter = ListofSectionsFieldsAdapter(fieldsCardsList[position].fields, context, ischeckerror)
        val adapter = ESP_LIB_ListofSectionsFieldsAdapter(fieldsCardsListESPLIB[position].fields, context, ischeckerror, true)
        holder.rvFields.adapter = adapter

        if (ESPLIBDynamicFormSectionDAO.type == 1)  // 1 = editable
            holder.ibEditCard.visibility = View.VISIBLE
        else if (ESPLIBDynamicFormSectionDAO.type == 2) // 2 = viewonly
            holder.ibEditCard.visibility = View.GONE


        holder.ibEditCard.setOnClickListener { v ->
            val dynamicFormSectionFieldsCardsDAO = fieldsCardsListESPLIB[position].fields
            ESPLIBDynamicFormSectionDAO.fields = dynamicFormSectionFieldsCardsDAO
            removeCard(v, position)
        }


    }//End Holder Class


    override fun getItemCount(): Int {
        return fieldsCardsListESPLIB.size

    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }


    private fun removeCard(v: View, position: Int) {
        //creating a popup menu
        val popup = PopupMenu(context, v)
        popup.gravity = Gravity.CENTER

        try {
            // Reflection apis to enforce show icon
            val fields = popup.javaClass.declaredFields
            for (field in fields) {
                if (field.name == "mPopup") {
                    field.isAccessible = true
                    val menuPopupHelper = field.get(popup)
                    val classPopupHelper = Class.forName(menuPopupHelper.javaClass.name)
                    val setForceIcons = classPopupHelper.getMethod("setForceShowIcon", Boolean::class.javaPrimitiveType!!)
                    setForceIcons.invoke(menuPopupHelper, true)
                    break
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        //inflating menu from xml resource
        popup.inflate(R.menu.menu_edit)

        //  val item_edit = popup.menu.findItem(R.id.action_edit)
        val item_delete = popup.menu.findItem(R.id.action_delete)
        item_delete.isVisible = false
        /*if (!dynamicFormSectionDAO.isMultipule)
            item_delete.isVisible = false*/

        //adding click listener
        popup.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.action_edit) {

                val i = Intent(context, ESP_LIB_EditSectionDetails::class.java)
                i.putExtra("data", ESPLIBDynamicFormSectionDAO)
                i.putExtra("dataapplicant", dataapplicant)
                i.putExtra("ischeckerror", ischeckerror)
                i.putExtra("position", position)
                context.startActivity(i)
            } else if (item.itemId == R.id.action_delete) {
                ESP_LIB_Shared.getInstance().messageBox("Delete", context as Activity)
            }
            false
        }
        //displaying the popup
        popup.show()
    }

}