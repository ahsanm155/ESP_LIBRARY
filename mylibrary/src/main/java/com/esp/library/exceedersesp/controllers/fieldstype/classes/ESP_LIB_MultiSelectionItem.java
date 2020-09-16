package com.esp.library.exceedersesp.controllers.fieldstype.classes;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import androidx.core.content.ContextCompat;


import com.esp.library.R;
import com.esp.library.exceedersesp.controllers.Profile.ESP_LIB_EditSectionDetails;
import com.esp.library.exceedersesp.controllers.fieldstype.other.ESP_LIB_CalculatedMappedRequestTrigger;
import com.esp.library.exceedersesp.controllers.fieldstype.other.ESP_LIB_Validation;
import com.esp.library.exceedersesp.controllers.fieldstype.viewholders.ESP_LIB_MultipleSelectionTypeViewHolder;
import com.esp.library.utilities.common.ESP_LIB_Shared;
import com.esp.library.utilities.common.ESP_LIB_SharedPreference;
import com.esp.library.utilities.setup.applications.ESP_LIB_ApplicationFieldsRecyclerAdapter;
import com.esp.library.utilities.setup.applications.ESP_LIB_ListUsersApplicationsAdapterV2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import utilities.data.applicants.dynamics.ESP_LIB_DynamicFormSectionFieldDAO;
import utilities.data.applicants.dynamics.ESP_LIB_DynamicStagesCriteriaListDAO;
import utilities.interfaces.ESP_LIB_CriteriaFieldsListener;

public class ESP_LIB_MultiSelectionItem {

    private String TAG = getClass().getSimpleName();
    private static ESP_LIB_MultiSelectionItem multiSelectionItem = null;
    private ESP_LIB_ApplicationFieldsRecyclerAdapter.ApplicationFieldsAdapterListener mApplicationFieldsAdapterListener;
    private ESP_LIB_CriteriaFieldsListener ESPLIBCriteriaFieldsListener;
    private ESP_LIB_DynamicStagesCriteriaListDAO criteriaListDAO;
    private boolean isViewOnly;
    private String actualResponseJson;
    private ESP_LIB_EditSectionDetails edisectionDetailslistener;

    public static ESP_LIB_MultiSelectionItem getInstance() {
        if (multiSelectionItem == null)
            return multiSelectionItem = new ESP_LIB_MultiSelectionItem();
        else
            return multiSelectionItem;
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

    public void showMultiSelectionTypeItemView(final ESP_LIB_MultipleSelectionTypeViewHolder holder, final int position,
                                               ESP_LIB_DynamicFormSectionFieldDAO fieldDAO, boolean isviewOnly,
                                               Context mContext, ESP_LIB_DynamicStagesCriteriaListDAO ESPLIBDynamicStagesCriteriaListDAO,
                                               boolean isCalculatedMappedField) {

        isViewOnly = isviewOnly;
        ESP_LIB_SharedPreference pref = new ESP_LIB_SharedPreference(mContext);
        String getValue = fieldDAO.getValue();
        try {
            if (pref.getLanguage().equalsIgnoreCase("ar")) {
                if (isViewOnly) {
                    holder.tValueLabel.setGravity(Gravity.RIGHT);
                }

            } else {

                if (isViewOnly) {
                    holder.tValueLabel.setGravity(Gravity.LEFT);

                }

            }

            if (fieldDAO.isReadOnly())
                isViewOnly = true;

            //Setting Label
            String label = fieldDAO.getLabel();

            if (isViewOnly) {
                if (ESP_LIB_ListUsersApplicationsAdapterV2.Companion.isSubApplications())
                    label = fieldDAO.getLabel() + ":";
                holder.tValueLabel.setText(label);
            } else {
                if (fieldDAO.isRequired()) {
                    label += " *";
                }

                holder.tValueLabel.setText(label);
            }
            //

            //Setting pre-filled Value. If Have

            Typeface typeface = Typeface.createFromAsset(mContext.getAssets(), "font/lato/lato_bold.ttf");
            List<String> lookupLabelsList = ESP_LIB_Shared.getInstance().getLookupLabelsList(fieldDAO.getLookupValues());
            for (int i = 0; i < lookupLabelsList.size(); i++) {
                String getLabel = lookupLabelsList.get(i);
                CheckBox cb = new CheckBox(mContext);
                cb.setButtonDrawable(ContextCompat.getDrawable(mContext, R.drawable.esp_lib_drawable_checkbox_button_selector));
                cb.setTextSize(16);
                cb.setText(getLabel);
                cb.setId(i);
                cb.setTypeface(typeface);
                removeRippleEffectFromCheckBox(cb);
                cb.setPadding(30, 0, 30, 0);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 22, 0, 0);
                holder.llcheckbox.addView(cb, params);


                if(fieldDAO.isReadOnly())
                    cb.setTextColor(ContextCompat.getColor(mContext,R.color.esp_lib_color_grey));

                if (ESPLIBDynamicStagesCriteriaListDAO != null &&
                        (!ESPLIBDynamicStagesCriteriaListDAO.isOwner() && ESPLIBDynamicStagesCriteriaListDAO.getAssessmentStatus().equalsIgnoreCase(mContext.getString(R.string.esp_lib_text_active)))) {
                    cb.setClickable(false);
                }


                if (!isViewOnly) {
                    if (!fieldDAO.isReadOnly()) {
                        int finalI = i;
                        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                                if (fieldDAO.getLookupValues() != null)
                                    fieldDAO.getLookupValues().get(finalI).setSelected(isChecked);

                                String lookupValue = ESP_LIB_Shared.getInstance().getSelectedLookupValues(fieldDAO.getLookupValues(), false, false);
                                fieldDAO.setValue(lookupValue);

                                if (TextUtils.isEmpty(lookupValue))
                                    fieldDAO.setValidate(false);
                                else
                                    fieldDAO.setValidate(true);


                                    validateForm(fieldDAO);

                               // if (isCalculatedMappedField)
                                if (fieldDAO.isTigger())
                                    ESP_LIB_CalculatedMappedRequestTrigger.submitCalculatedMappedRequest(mContext, isViewOnly, fieldDAO);
                            }
                        });
                    }
                }

            }


            String lookupValue = "";


            if (getValue != null && getValue.length() > 0) {
                if (actualResponseJson != null)
                    lookupValue = ESP_LIB_Shared.getInstance().populateLookupValues(getValue, actualResponseJson);
                else
                    lookupValue = ESP_LIB_Shared.getInstance().populateLookupValuesForProfileSections(getValue, fieldDAO);
            } else if (fieldDAO.getLookupValues() != null && fieldDAO.getLookupValues().size() > 0) {
                lookupValue = ESP_LIB_Shared.getInstance().getSelectedLookupValues(fieldDAO.getLookupValues(), true, false);
            }

            if (actualResponseJson != null) {
                if (lookupValue == null || lookupValue.length() == 0 || lookupValue.equalsIgnoreCase("null"))
                    lookupValue = ESP_LIB_Shared.getInstance().populateStageLookupValues(getValue, actualResponseJson);
            }
            if (!TextUtils.isEmpty(lookupValue)) {
                if (isViewOnly) {
                    // holder.tValue.setText(lookupValue);
                    setCheckBoxValues(lookupLabelsList, lookupValue, holder, false, mContext);
                    fieldDAO.setValidate(true);
                } else {

                    setCheckBoxValues(lookupLabelsList, lookupValue, holder, true, mContext);

                    fieldDAO.setValidate(true);
                    validateForm(fieldDAO);
                }
            } else {
                if (!isViewOnly) {
                    //Handling Required Condition
                    if (fieldDAO.isRequired())
                        fieldDAO.setValidate(false);
                    else
                        fieldDAO.setValidate(true);

                    validateForm(fieldDAO);
                } else
                    setCheckBoxValues(lookupLabelsList, lookupValue, holder, false, mContext);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        validateForm(fieldDAO);
    }

    private void setCheckBoxValues(List<String> lookupLabelsList, String lookupValue,
                                   ESP_LIB_MultipleSelectionTypeViewHolder holder, boolean isEnable, Context mContext) {
        for (int i = 0; i < lookupLabelsList.size(); i++) {
            String getLable = lookupLabelsList.get(i);
            CheckBox cb = (CheckBox) holder.llcheckbox.getChildAt(i);
            cb.setClickable(isEnable);
            List<String> selectedValuesList = new ArrayList<String>(Arrays.asList(lookupValue.split(",")));
            for (int j = 0; j < selectedValuesList.size(); j++) {
                if (getLable.trim().equalsIgnoreCase(selectedValuesList.get(j).trim())) {
                    if (isViewOnly)
                        cb.setButtonDrawable(ContextCompat.getDrawable(mContext, R.drawable.esp_lib_drawable_ic_icons_checkbox_checked_disabled));
                    else
                        cb.setButtonDrawable(ContextCompat.getDrawable(mContext, R.drawable.esp_lib_drawable_checkbox_button_selector));
                    cb.setChecked(true);
                }
            }
        }
    }

    private void removeRippleEffectFromCheckBox(CheckBox checkBox) {
        Drawable drawable = checkBox.getBackground();
        if (drawable instanceof RippleDrawable) {
            drawable = ((RippleDrawable) drawable).findDrawableByLayerId(0);
            checkBox.setBackground(drawable);
        }
    }


    private void validateForm(ESP_LIB_DynamicFormSectionFieldDAO fieldDAO) {
        ESP_LIB_Validation validation = new ESP_LIB_Validation(mApplicationFieldsAdapterListener, ESPLIBCriteriaFieldsListener,
                criteriaListDAO, fieldDAO);
        validation.setSectionListener(edisectionDetailslistener);
        validation.validateForm();
    }

}
