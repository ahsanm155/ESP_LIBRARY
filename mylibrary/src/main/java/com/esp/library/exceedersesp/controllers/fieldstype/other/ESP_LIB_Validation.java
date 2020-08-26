package com.esp.library.exceedersesp.controllers.fieldstype.other;

import android.os.Handler;

import com.esp.library.exceedersesp.controllers.Profile.ESP_LIB_EditSectionDetails;
import com.esp.library.utilities.setup.applications.ESP_LIB_ApplicationFieldsRecyclerAdapter;
import utilities.data.applicants.dynamics.ESP_LIB_DynamicFormSectionFieldDAO;
import utilities.data.applicants.dynamics.ESP_LIB_DynamicStagesCriteriaListDAO;
import utilities.interfaces.ESP_LIB_CriteriaFieldsListener;

public class ESP_LIB_Validation {

    private ESP_LIB_ApplicationFieldsRecyclerAdapter.ApplicationFieldsAdapterListener mApplicationFieldsAdapterListener;
    private ESP_LIB_CriteriaFieldsListener ESPLIBCriteriaFieldsListener;
    private ESP_LIB_DynamicStagesCriteriaListDAO criteriaListDAO;
    private ESP_LIB_DynamicFormSectionFieldDAO fieldDAO;
    ESP_LIB_EditSectionDetails editSectionDetailslistener;

    public ESP_LIB_Validation(ESP_LIB_ApplicationFieldsRecyclerAdapter.ApplicationFieldsAdapterListener applicationFieldsAdapterListener,
                              ESP_LIB_CriteriaFieldsListener criteriafieldsListenerESPLIB, ESP_LIB_DynamicStagesCriteriaListDAO criterialistDAO,
                              ESP_LIB_DynamicFormSectionFieldDAO fielddAO) {

        mApplicationFieldsAdapterListener = applicationFieldsAdapterListener;
        ESPLIBCriteriaFieldsListener = criteriafieldsListenerESPLIB;
        criteriaListDAO = criterialistDAO;
        fieldDAO = fielddAO;
    }

    public void setSectionListener(ESP_LIB_EditSectionDetails edisectionDetailslistener)
    {
        editSectionDetailslistener=edisectionDetailslistener;
    }

    public void validateForm() {

        if (criteriaListDAO != null && ESPLIBCriteriaFieldsListener != null)
            setCriteriaValidation();

        if (mApplicationFieldsAdapterListener != null)
            mApplicationFieldsAdapterListener.onFieldValuesChanged();

        if(editSectionDetailslistener!=null)
            editSectionDetailslistener.onFieldValuesChanged();
    }

    private void setCriteriaValidation() {
        try {
            if (ESPLIBCriteriaFieldsListener != null) {
                criteriaListDAO.setValidate(fieldDAO.isValidate());
                ESPLIBCriteriaFieldsListener.validateCriteriaFields(criteriaListDAO);

            }
        } catch (Exception e) {

        }
    }
}
