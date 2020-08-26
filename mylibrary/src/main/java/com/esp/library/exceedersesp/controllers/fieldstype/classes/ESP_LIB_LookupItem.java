package com.esp.library.exceedersesp.controllers.fieldstype.classes;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;


import com.esp.library.R;
import com.esp.library.exceedersesp.controllers.Profile.ESP_LIB_EditSectionDetails;
import com.esp.library.exceedersesp.controllers.fieldstype.other.ESP_LIB_Validation;
import com.esp.library.exceedersesp.controllers.fieldstype.viewholders.ESP_LIB_PickerTypeViewHolder;
import com.esp.library.utilities.common.ESP_LIB_CustomLogs;
import com.esp.library.utilities.common.ESP_LIB_SharedPreference;
import com.esp.library.utilities.setup.applications.ESP_LIB_ApplicationFieldsRecyclerAdapter;
import com.esp.library.utilities.setup.applications.ESP_LIB_ListUsersApplicationsAdapterV2;

import utilities.data.applicants.dynamics.ESP_LIB_DynamicFormSectionFieldDAO;
import utilities.data.applicants.dynamics.ESP_LIB_DynamicStagesCriteriaListDAO;
import utilities.interfaces.ESP_LIB_CriteriaFieldsListener;

public class ESP_LIB_LookupItem {

    private String TAG = getClass().getSimpleName();
    private static ESP_LIB_LookupItem lookupItem = null;
    private ESP_LIB_ApplicationFieldsRecyclerAdapter.ApplicationFieldsAdapterListener mApplicationFieldsAdapterListener;
    private ESP_LIB_CriteriaFieldsListener ESPLIBCriteriaFieldsListener;
    private ESP_LIB_DynamicStagesCriteriaListDAO criteriaListDAO;
    private boolean isViewOnly;
    private String actualResponseJson;
    ESP_LIB_EditSectionDetails edisectionDetailslistener;


    public static ESP_LIB_LookupItem getInstance() {
        if (lookupItem == null)
            return lookupItem = new ESP_LIB_LookupItem();
        else
            return lookupItem;
    }


    public void mApplicationFieldsAdapterListener(ESP_LIB_ApplicationFieldsRecyclerAdapter.ApplicationFieldsAdapterListener applicationFieldsAdapterListener) {
        mApplicationFieldsAdapterListener = applicationFieldsAdapterListener;
    }

    public void criteriaFieldsListener(ESP_LIB_CriteriaFieldsListener criteriafieldsListenerESPLIB) {
        ESPLIBCriteriaFieldsListener = criteriafieldsListenerESPLIB;
    }

    public void criteriaListDAO(ESP_LIB_DynamicStagesCriteriaListDAO criterialistDAO) {
        criteriaListDAO = criterialistDAO;
    }

    public void getactualResponseJson(String actualresponseJson) {
        actualResponseJson = actualresponseJson;
    }

    public void getAdapter(ESP_LIB_EditSectionDetails edisectionDetails) {
        edisectionDetailslistener = edisectionDetails;
    }


    public void showLookUpTypeItemView(final ESP_LIB_PickerTypeViewHolder holder, final int position,
                                       ESP_LIB_DynamicFormSectionFieldDAO fieldDAO, boolean isviewOnly,
                                       Context mContext, ESP_LIB_DynamicStagesCriteriaListDAO ESPLIBDynamicStagesCriteriaListDAO,
                                       boolean isCalculatedMappedField) {

        isViewOnly = isviewOnly;
        ESP_LIB_SharedPreference pref = new ESP_LIB_SharedPreference(mContext);
        String getValue = fieldDAO.getValue();
        ESP_LIB_CustomLogs.displayLogs(TAG + " showLookUpTypeItemView getValue: " + getValue);
        try {
            if (pref.getLanguage().equalsIgnoreCase("ar")) {
                holder.tilFieldLabel.setGravity(Gravity.RIGHT);
                holder.etValue.setGravity(Gravity.RIGHT);
                if (isViewOnly) {
                    holder.tValue.setGravity(Gravity.RIGHT);
                    holder.tValueLabel.setGravity(Gravity.RIGHT);
                }

            } else {

                holder.tilFieldLabel.setGravity(Gravity.LEFT);
                holder.etValue.setGravity(Gravity.LEFT);
                if (isViewOnly) {
                    holder.tValue.setGravity(Gravity.LEFT);
                    holder.tValueLabel.setGravity(Gravity.LEFT);
                }

            }
        } catch (Exception e) {
            // e.printStackTrace();
        }

        if (ESPLIBDynamicStagesCriteriaListDAO != null &&
                (!ESPLIBDynamicStagesCriteriaListDAO.isOwner() && ESPLIBDynamicStagesCriteriaListDAO.getAssessmentStatus().equalsIgnoreCase(mContext.getString(R.string.esp_lib_text_active)))) {
            holder.etValue.setText(fieldDAO.getLabel());
            holder.etValue.setEnabled(false);
            return;
        }


        if (isViewOnly) {

            String label = fieldDAO.getLabel();
            if (ESP_LIB_ListUsersApplicationsAdapterV2.Companion.isSubApplications())
                label = fieldDAO.getLabel() + ":";

            holder.tValueLabel.setText(label);
            if (fieldDAO.getValue() == null || TextUtils.isEmpty(fieldDAO.getValue()))
                getValue="-";
            holder.tValue.setText(getValue);
            fieldDAO.setValidate(true);
            return;
        }


        //Setting Label
        String label = fieldDAO.getLabel();

        if (fieldDAO.isRequired() && !isViewOnly) {
            label += " *";
        }

        holder.tilFieldLabel.setHint(label);

        //

        //Setting pre-filled Value. If Have
        String lookupValue = "";
        int lookupId = 0;

        if (fieldDAO.getLookupValue() != null && !TextUtils.isEmpty(fieldDAO.getLookupValue())) {
            lookupValue = fieldDAO.getLookupValue();
            lookupId = fieldDAO.getId();
        } else {

           /* if(actualResponseJson!=null) {
                DynamicResponseDAO actualResponse = new Gson().fromJson(actualResponseJson, DynamicResponseDAO.class);
                int getSectionCustomFieldId = fieldDAO.getSectionCustomFieldId();
                if (actualResponse.getApplicationStatus() != null) {
                    if (actualResponse.getApplicationStatus().toLowerCase().equalsIgnoreCase(mContext.getString(R.string.neww))
                            || actualResponse.getApplicationStatus().toLowerCase().equalsIgnoreCase(mContext.getString(R.string.rejectedsmall))) {
                        GetValues gV = new GetValues();
                        List<ApplicationDetailFieldsDAO> apd = gV.getFormValues(actualResponse, 13);

                        for (int i = 0; i < apd.size(); i++) {
                            if (apd.get(i).getType() == 13) {
                                if (getSectionCustomFieldId == apd.get(i).getSectionId()) {
                                    lookupValue = apd.get(i).getFieldvalue();
                                    lookupId = apd.get(i).getLookupId();
                                    CustomLogs.displayLogs(TAG + " showLookUpTypeItemView val: " + apd.get(i).getFieldsDAO());
                                    break;
                                }
                            }
                        }

                    }
                }
            }*/
        }
        ESP_LIB_CustomLogs.displayLogs(TAG + " showLookUpTypeItemView lookupValue: " + lookupValue);
        if (lookupValue == null || lookupValue.replaceAll("\\s", "").length() == 0)
            lookupValue = getValue;

        if (!TextUtils.isEmpty(lookupValue)) {

            holder.etValue.setText(lookupValue);
            fieldDAO.setValue(String.valueOf(lookupId));
            fieldDAO.setValidate(true);
            holder.ivclear.setVisibility(View.VISIBLE);

            validateForm(fieldDAO);

        } else {

            //Handling Required Condition
            if (fieldDAO.isRequired())
                fieldDAO.setValidate(false);
            else
                fieldDAO.setValidate(true);

            validateForm(fieldDAO);

        }
        //


        if (!isViewOnly) {
            if (!fieldDAO.isReadOnly()) {

                holder.ivclear.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.etValue.setText("");
                        fieldDAO.setValue("");
                        fieldDAO.setLookupValue("");
                        fieldDAO.setValidate(false);
                        holder.ivclear.setVisibility(View.GONE);
                        validateForm(fieldDAO);
                      //  setDrawable(holder, pref);
                    }
                });

                holder.etValue.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mApplicationFieldsAdapterListener != null)
                            mApplicationFieldsAdapterListener.onLookupFieldClicked(fieldDAO, position, isCalculatedMappedField);
                        else if (edisectionDetailslistener != null)
                            edisectionDetailslistener.onLookupFieldClicked(fieldDAO, position, isCalculatedMappedField);


                    }
                });
            }
        }

        if (holder.etValue != null) {
            if (fieldDAO.isReadOnly())
                holder.etValue.setEnabled(false);
            else
                holder.etValue.setEnabled(true);
        }

        setDrawable(holder,pref);


    }

    private void setDrawable(ESP_LIB_PickerTypeViewHolder holder, ESP_LIB_SharedPreference pref)
    {
        //if (holder.ivclear.getVisibility() == View.GONE) {
            if (pref.getLanguage().equalsIgnoreCase("en"))
                holder.etValue.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.esp_lib_drawable_ic_arrow_down_black, 0);
            else
                holder.etValue.setCompoundDrawablesWithIntrinsicBounds(R.drawable.esp_lib_drawable_ic_arrow_down_black, 0, 0, 0);
      //  }
    }

    private void validateForm(ESP_LIB_DynamicFormSectionFieldDAO fieldDAO) {
        ESP_LIB_Validation validation = new ESP_LIB_Validation(mApplicationFieldsAdapterListener, ESPLIBCriteriaFieldsListener,
                criteriaListDAO, fieldDAO);
        validation.setSectionListener(edisectionDetailslistener);
        validation.validateForm();
    }

}
