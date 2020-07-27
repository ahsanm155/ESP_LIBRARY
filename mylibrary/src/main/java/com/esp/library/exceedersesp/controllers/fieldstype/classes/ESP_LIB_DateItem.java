package com.esp.library.exceedersesp.controllers.fieldstype.classes;

import android.app.DatePickerDialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.DatePicker;

import androidx.core.content.ContextCompat;

import com.esp.library.R;
import com.esp.library.exceedersesp.controllers.Profile.ESP_LIB_EditSectionDetails;
import com.esp.library.exceedersesp.controllers.fieldstype.other.ESP_LIB_CalculatedMappedRequestTrigger;
import com.esp.library.exceedersesp.controllers.fieldstype.other.ESP_LIB_Validation;
import com.esp.library.exceedersesp.controllers.fieldstype.viewholders.ESP_LIB_PickerTypeViewHolder;
import com.esp.library.utilities.common.ESP_LIB_CustomLogs;
import com.esp.library.utilities.common.ESP_LIB_DateTimeUtils;
import com.esp.library.utilities.common.ESP_LIB_GetValues;
import com.esp.library.utilities.common.ESP_LIB_Shared;
import com.esp.library.utilities.common.ESP_LIB_SharedPreference;
import com.esp.library.utilities.setup.applications.ESP_LIB_ApplicationFieldsRecyclerAdapter;
import com.google.gson.Gson;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import utilities.adapters.setup.applications.ESP_LIB_ListUsersApplicationsAdapter;
import utilities.data.applicants.ESP_LIB_ApplicationDetailFieldsDAO;
import utilities.data.applicants.dynamics.ESP_LIB_DynamicFormSectionFieldDAO;
import utilities.data.applicants.dynamics.ESP_LIB_DynamicResponseDAO;
import utilities.data.applicants.dynamics.ESP_LIB_DynamicStagesCriteriaListDAO;
import utilities.interfaces.ESP_LIB_CriteriaFieldsListener;

public class ESP_LIB_DateItem {

    private String TAG = getClass().getSimpleName();
    private static ESP_LIB_DateItem dateItem = null;
    private ESP_LIB_ApplicationFieldsRecyclerAdapter.ApplicationFieldsAdapterListener mApplicationFieldsAdapterListener;
    private ESP_LIB_CriteriaFieldsListener ESPLIBCriteriaFieldsListener;
    private ESP_LIB_DynamicStagesCriteriaListDAO criteriaListDAO;
    private boolean isViewOnly;
    private String actualResponseJson;
    private ESP_LIB_EditSectionDetails edisectionDetailslistener;


    public static ESP_LIB_DateItem getInstance() {
        if (dateItem == null)
            return dateItem = new ESP_LIB_DateItem();
        else
            return dateItem;
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

    public void showDateTypeItemView(final ESP_LIB_PickerTypeViewHolder holder, final int position,
                                     ESP_LIB_DynamicFormSectionFieldDAO fieldDAO, boolean isviewOnly,
                                     Context mContext, ESP_LIB_DynamicStagesCriteriaListDAO ESPLIBDynamicStagesCriteriaListDAO,
                                     boolean isCalculatedMappedField) {
        isViewOnly = isviewOnly;
        ESP_LIB_SharedPreference pref = new ESP_LIB_SharedPreference(mContext);
        String getValue = fieldDAO.getValue();
        try {
            if (pref.getLanguage().equalsIgnoreCase("ar")) {
                holder.tilFieldLabel.setGravity(Gravity.RIGHT);
                holder.etValue.setGravity(Gravity.RIGHT);
                if (isViewOnly)
                    holder.tValueLabel.setGravity(Gravity.RIGHT);

            } else {

                holder.tilFieldLabel.setGravity(Gravity.LEFT);
                holder.etValue.setGravity(Gravity.LEFT);
                if (isViewOnly)
                    holder.tValueLabel.setGravity(Gravity.LEFT);

            }
        } catch (Exception e) {
            //  e.printStackTrace();
        }

        if (ESPLIBDynamicStagesCriteriaListDAO != null &&
                (!ESPLIBDynamicStagesCriteriaListDAO.isOwner() && ESPLIBDynamicStagesCriteriaListDAO.getAssessmentStatus().equalsIgnoreCase(mContext.getString(R.string.esp_lib_text_active)))) {
            holder.etValue.setText(fieldDAO.getLabel());
            holder.etValue.setEnabled(false);
            return;
        }

        //Setting Label
        String label = fieldDAO.getLabel();
        if (isViewOnly) {
            if (ESP_LIB_ListUsersApplicationsAdapter.Companion.isSubApplications())
                label = fieldDAO.getLabel() + ":";
            holder.tValueLabel.setText(label);
        } else {
            if (fieldDAO.isRequired()) {
                label += " *";
            }
            /*if (fieldDAO.isReadOnly()) {
                holder.tilFieldDisableLabel.setVisibility(View.VISIBLE);
                holder.llmain.setVisibility(View.GONE);
                holder.etvalueDisable.setText(getValue);
                holder.tilFieldDisableLabel.setHint(label);
            } else {
                holder.tilFieldDisableLabel.setVisibility(View.GONE);
                holder.tilFieldLabel.setVisibility(View.VISIBLE);*/
            holder.tilFieldLabel.setHint(label);
            // }
        }
        //

        if (actualResponseJson != null) {
            ESP_LIB_DynamicResponseDAO actualResponse = new Gson().fromJson(actualResponseJson, ESP_LIB_DynamicResponseDAO.class);
            int getSectionCustomFieldId = fieldDAO.getSectionCustomFieldId();
            if (actualResponse != null && actualResponse.getApplicationStatus() != null) {
                if (actualResponse.getApplicationStatus().toLowerCase().equalsIgnoreCase("new")) {
                    ESP_LIB_GetValues gV = new ESP_LIB_GetValues();
                    List<ESP_LIB_ApplicationDetailFieldsDAO> apd = gV.getFormValues(actualResponse, 4);

                    for (int i = 0; i < apd.size(); i++) {
                        if (apd.get(i).getType() == 4) {
                            if (getSectionCustomFieldId == apd.get(i).getSectionId()) {
                                // fieldDAO.setValue(apd.get(i).getFieldvalue());
                                ESP_LIB_CustomLogs.displayLogs(TAG + " showDateTypeItemView val: " + apd.get(i).getFieldvalue());
                                break;
                            }
                        }
                    }

                }

            }
        }


        fieldDAO.setValue(getValue);
        //Setting Value

        if (isViewOnly) {
            if (fieldDAO.getValue() == null || TextUtils.isEmpty(fieldDAO.getValue()))
                holder.tValue.setText("-");
        }
        if (fieldDAO.getValue() != null && !TextUtils.isEmpty(fieldDAO.getValue())) {
            String displayDate = ESP_LIB_Shared.getInstance().getDisplayDate(mContext, fieldDAO.getValue(), false);

            if (isViewOnly) {
                if (displayDate.isEmpty())
                    displayDate = "-";

                holder.tValue.setText(displayDate);
            } else {
                holder.etValue.setText(displayDate);
                fieldDAO.setValidate(true);
                holder.ivclear.setVisibility(View.VISIBLE);
                validateForm(fieldDAO);
            }

            /*if (fieldDAO.isReadOnly()) {
                try {
                    holder.tilFieldDisableLabel.setVisibility(View.VISIBLE);
                    holder.llmain.setVisibility(View.GONE);
                    holder.etvalueDisable.setText(displayDate);
                } catch (Exception e) {
                }
            }*/

        } else {
            if (!isViewOnly) {
                //Handling Required Condition
                if (fieldDAO.isRequired())
                    fieldDAO.setValidate(false);
                else
                    fieldDAO.setValidate(true);

                validateForm(fieldDAO);
            }
        }

        if (!isViewOnly) {
            if (pref.getLanguage().equalsIgnoreCase("en"))
                holder.etValue.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.esp_lib_drawable_ic_icons_inputs_calendar_black, 0);
            else
                holder.etValue.setCompoundDrawablesWithIntrinsicBounds(R.drawable.esp_lib_drawable_ic_icons_inputs_calendar_black, 0, 0, 0);
        }

        /*if (fieldDAO.isReadOnly()) {
            try {
                if (pref.getLanguage().equalsIgnoreCase("en"))
                    holder.etvalueDisable.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.esp_lib_drawable_ic_icons_inputs_calendar_grey, 0);
                else
                    holder.etvalueDisable.setCompoundDrawablesWithIntrinsicBounds(R.drawable.esp_lib_drawable_ic_icons_inputs_calendar_grey, 0, 0, 0);
            } catch (Exception e) {
            }
        }*/


        if (!isViewOnly) {
            if (!fieldDAO.isReadOnly()) {


                holder.ivclear.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.etValue.setText("");
                        fieldDAO.setValue("");
                        fieldDAO.setValidate(false);
                        holder.ivclear.setVisibility(View.GONE);
                        validateForm(fieldDAO);
                    }
                });

                holder.etValue.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        final Calendar calendar = ESP_LIB_Shared.getInstance().getTodayCalendar();
                        final int year = calendar.get(Calendar.YEAR);
                        final int month = calendar.get(Calendar.MONTH);
                        final int day = calendar.get(Calendar.DAY_OF_MONTH);


                        DatePickerDialog datePickerDialog = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year_p, int month_p, int day_p) {

                               /* String selectedDate = year_p + "-" + Shared.getInstance().AddZero((month_p + 1)) + "-" + Shared.getInstance().AddZero(day_p);
                                //Appending Time As Zero, Just for formatting.
                                selectedDate += "T" + Shared.getInstance().AddZero(0) + ":" + Shared.getInstance().AddZero(0) + ":00Z";
*/

                                String formatedDate = ESP_LIB_Shared.getInstance().getDatePickerGMTDate(datePicker);
                                holder.etValue.setText(ESP_LIB_Shared.getInstance().getDisplayDate(mContext, formatedDate, false));
                                fieldDAO.setValue(formatedDate);
                                fieldDAO.setValidate(true);

                                holder.ivclear.setVisibility(View.VISIBLE);
                                // if (isCalculatedMappedField)
                                if (fieldDAO.isTigger())
                                    ESP_LIB_CalculatedMappedRequestTrigger.submitCalculatedMappedRequest(mContext, isViewOnly, fieldDAO);

                                validateForm(fieldDAO);

                            }
                        }, year, month, day);


                        if (fieldDAO.getMinDate() != null) {
                            Date minDate = ESP_LIB_DateTimeUtils.iso8601ToJavaDate(fieldDAO.getMinDate());
                            if (minDate == null)
                                minDate = Calendar.getInstance().getTime();

                            datePickerDialog.getDatePicker().setMinDate(minDate.getTime());
                        }

                        if (fieldDAO.getMaxDate() != null) {
                            Date maxDate = ESP_LIB_DateTimeUtils.iso8601ToJavaDate(fieldDAO.getMaxDate());
                            if (maxDate == null)
                                maxDate = Calendar.getInstance().getTime();

                            datePickerDialog.getDatePicker().setMaxDate(maxDate.getTime());
                        }

                        datePickerDialog.show();

                    }
                });
            }
        }
        if (holder.etValue != null) {
            if (fieldDAO.isReadOnly()) {
                // holder.tilFieldLabel.setBackgroundResource(R.drawable.esp_lib_drawable_draw_bg_disable_fields);

                holder.etValue.setEnabled(false);
                holder.ivclear.setVisibility(View.GONE);
                fieldDAO.setValidate(true);
                validateForm(fieldDAO);

                holder.etValue.setTextColor(ContextCompat.getColor(mContext, R.color.esp_lib_color_coolgrey));
                if (pref.getLanguage().equalsIgnoreCase("en"))
                    holder.etValue.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.esp_lib_drawable_ic_lock_grey, 0);
                else
                    holder.etValue.setCompoundDrawablesWithIntrinsicBounds(R.drawable.esp_lib_drawable_ic_lock_grey, 0, 0, 0);

            } else
                holder.etValue.setEnabled(true);
        }
    }

    private void validateForm(ESP_LIB_DynamicFormSectionFieldDAO fieldDAO) {
        ESP_LIB_Validation validation = new ESP_LIB_Validation(mApplicationFieldsAdapterListener, ESPLIBCriteriaFieldsListener,
                criteriaListDAO, fieldDAO);
        validation.setSectionListener(edisectionDetailslistener);
        validation.validateForm();
    }

}
