package com.esp.library.exceedersesp.controllers.fieldstype.classes;

import android.content.Context;
import android.os.Handler;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.esp.library.R;
import com.esp.library.exceedersesp.ESP_LIB_ESPApplication;
import com.esp.library.exceedersesp.controllers.Profile.ESP_LIB_EditSectionDetails;
import com.esp.library.exceedersesp.controllers.fieldstype.other.ESP_LIB_CalculatedMappedRequestTrigger;
import com.esp.library.exceedersesp.controllers.fieldstype.other.ESP_LIB_Validation;
import com.esp.library.exceedersesp.controllers.fieldstype.viewholders.ESP_LIB_CurrencyEditTextTypeViewHolder;
import com.esp.library.utilities.common.ESP_LIB_CustomLogs;
import com.esp.library.utilities.common.ESP_LIB_GetValues;
import com.esp.library.utilities.common.ESP_LIB_Shared;
import com.esp.library.utilities.common.ESP_LIB_SharedPreference;
import com.esp.library.utilities.setup.ESP_LIB_CustomSpinnerAdapter;
import com.esp.library.utilities.setup.applications.ESP_LIB_ApplicationFieldsRecyclerAdapter;
import com.esp.library.utilities.setup.applications.ESP_LIB_ListUsersApplicationsAdapterV2;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import utilities.common.ESP_LIB_MaterialSpinner;
import utilities.data.applicants.ESP_LIB_ApplicationDetailFieldsDAO;
import utilities.data.applicants.addapplication.ESP_LIB_CurrencyDAO;
import utilities.data.applicants.dynamics.ESP_LIB_DynamicFormSectionFieldDAO;
import utilities.data.applicants.dynamics.ESP_LIB_DynamicResponseDAO;
import utilities.data.applicants.dynamics.ESP_LIB_DynamicStagesCriteriaListDAO;
import utilities.interfaces.ESP_LIB_CriteriaFieldsListener;

public class ESP_LIB_CurrencyItem {

    private String TAG = getClass().getSimpleName();
    private static ESP_LIB_CurrencyItem currencyItem = null;
    private boolean isViewOnly;
    private ESP_LIB_ApplicationFieldsRecyclerAdapter.ApplicationFieldsAdapterListener mApplicationFieldsAdapterListener;
    private ESP_LIB_CriteriaFieldsListener ESPLIBCriteriaFieldsListener;
    private ESP_LIB_DynamicStagesCriteriaListDAO criteriaListDAO;
    private String actualResponseJson;
    private ESP_LIB_EditSectionDetails edisectionDetailslistener;
    AlertDialog materialAlertDialogBuilder = null;
    int mSelectedIndex = 0;


    public static ESP_LIB_CurrencyItem getInstance() {
        if (currencyItem == null)
            return currencyItem = new ESP_LIB_CurrencyItem();
        else
            return currencyItem;
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

    public void showCurrencyEditTextItemView(final ESP_LIB_CurrencyEditTextTypeViewHolder holder, final int position,
                                             ESP_LIB_DynamicFormSectionFieldDAO fieldDAO, boolean isviewOnly,
                                             Context mContext, ESP_LIB_DynamicStagesCriteriaListDAO ESPLIBDynamicStagesCriteriaListDAO,
                                             boolean isCalculatedMappedField) {
        isViewOnly = isviewOnly;
        ESP_LIB_SharedPreference pref = new ESP_LIB_SharedPreference(mContext);
        try {
            if (pref.getLanguage().equalsIgnoreCase("ar")) {
                holder.tilFieldLabel.setGravity(Gravity.RIGHT);
                holder.etValue.setGravity(Gravity.RIGHT);

            } else {

                holder.tilFieldLabel.setGravity(Gravity.LEFT);
                holder.etValue.setGravity(Gravity.LEFT);

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

        if (!isViewOnly) {

            ESP_LIB_DynamicFormSectionFieldDAO finalFieldDAO2 = fieldDAO;
            holder.etValue.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean isfocusable) {


                    if (!isfocusable) {
                        //  if (isCalculatedMappedField)
                        if (finalFieldDAO2.isTigger())
                            ESP_LIB_CalculatedMappedRequestTrigger.submitCalculatedMappedRequest(mContext, isViewOnly, finalFieldDAO2);

                    }

                }
            });

            holder.etValue.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        //Clear focus here from edittext
                        holder.etValue.clearFocus();
                    }
                    return false;
                }
            });

            ESP_LIB_DynamicFormSectionFieldDAO finalFieldDAO = fieldDAO;
            holder.etValue.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {

                    String outputedText = editable.toString();
                    String error = "";
                    finalFieldDAO.setValidate(false);
                    holder.tilFieldLabel.setError(null);
                    holder.tilFieldLabel.setErrorEnabled(false);
                    if (!finalFieldDAO.isReadOnly()) {
                        error = ESP_LIB_Shared.getInstance().edittextErrorChecks(mContext, outputedText, error, finalFieldDAO);
                    }

                    if (error.length() > 0) {
                        holder.tilFieldLabel.setErrorEnabled(true);
                        holder.tilFieldLabel.setError(error);
                        finalFieldDAO.setValue("");
                    } else {

                        finalFieldDAO.setValue(outputedText);
                        finalFieldDAO.setValidate(true);

                        if (finalFieldDAO.isRequired() && outputedText.isEmpty())
                            finalFieldDAO.setValidate(false);


                    }
                    validateForm(finalFieldDAO);
                }
            });


        }

        //Setting Label
        String label = fieldDAO.getLabel();
        if (isViewOnly) {
            if (ESP_LIB_ListUsersApplicationsAdapterV2.Companion.isSubApplications()) {
                label = fieldDAO.getLabel() + ":";
                holder.tValueLabel.setText(label);
            } else
                holder.tValueLabel.setText(mContext.getString(R.string.esp_lib_text_value));
        } else {
            if (fieldDAO.isRequired()) {
                label += " *";
            }
            holder.tCurrencyLabel.setVisibility(View.VISIBLE);
            holder.tCurrencyLabel.setText(label);
        }
        //


        //  holder.etValue.setText(getValue);
        try {
            fieldDAO = ESP_LIB_Shared.getInstance().populateCurrencyByObject(fieldDAO);
        } catch (Exception e) {
            if (actualResponseJson != null) {
                ESP_LIB_DynamicResponseDAO actualResponse = new Gson().fromJson(actualResponseJson, ESP_LIB_DynamicResponseDAO.class);
                int getSectionCustomFieldId = fieldDAO.getSectionCustomFieldId();
                if (actualResponse.getApplicationStatus() != null) {
                    if (actualResponse.getApplicationStatus().toLowerCase().equalsIgnoreCase(mContext.getString(R.string.esp_lib_text_neww))) {
                        ESP_LIB_GetValues gV = new ESP_LIB_GetValues();
                        List<ESP_LIB_ApplicationDetailFieldsDAO> apd = gV.getFormValues(actualResponse, 11);

                        for (int i = 0; i < apd.size(); i++) {
                            if (apd.get(i).getType() == 11) {
                                if (getSectionCustomFieldId == apd.get(i).getSectionId()) {

                                    try {
                                        String value = "";
                                        fieldDAO = apd.get(i).getFieldsDAO();
                                        if (apd.get(i).getFieldsDAO().getValue() != null)
                                            value = apd.get(i).getFieldsDAO().getValue();
                                        ESP_LIB_CustomLogs.displayLogs(TAG + " showCurrencyEditTextItemView val: " + apd.get(i).getFieldsDAO() + " value: " + value);
                                        break;
                                    } catch (Exception ee) {

                                    }
                                }
                            }
                        }

                    }
                }
            }
        }

        //Setting Currency
        ESP_LIB_CurrencyDAO selectedCurrency = null;
        if (fieldDAO != null) {
            if (fieldDAO.getValue() != null && !TextUtils.isEmpty(fieldDAO.getValue())
                    && (fieldDAO.getSelectedCurrencySymbol() == null || TextUtils.isEmpty(fieldDAO.getSelectedCurrencySymbol()))) {

                try {
                    selectedCurrency = ESP_LIB_Shared.getInstance().getCurrencyById(Integer.valueOf(fieldDAO.getValue()));
                    fieldDAO.setSelectedCurrencyId(selectedCurrency.getId());
                    fieldDAO.setSelectedCurrencySymbol(selectedCurrency.getSymobl());
                    //Setting empty value for the required Validation.
                    if (isViewOnly) {
                        if (ESP_LIB_ListUsersApplicationsAdapterV2.Companion.isSubApplications())
                            holder.tValue.setText("");
                        else {
                            holder.tValue.setText("");
                            holder.tCurrencyLabel.setText("");
                            holder.tCurrencyLabel.setVisibility(View.GONE);
                        }
                    } else
                        holder.etValue.setText("");
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            } else {
                if (isViewOnly) {
                    if (ESP_LIB_ListUsersApplicationsAdapterV2.Companion.isSubApplications())
                        holder.tValue.setText(fieldDAO.getValue());
                    else {
                        holder.tValue.setText(fieldDAO.getSelectedCurrencyId() + " (" + fieldDAO.getSelectedCurrencySymbol() + ") " + fieldDAO.getValue());
                        holder.tCurrencyLabel.setText(label);
                        holder.tCurrencyLabel.setVisibility(View.VISIBLE);
                    }
                } else
                    holder.etValue.setText(fieldDAO.getValue());

            }

            try {
                //Try to get the currency from SelectedCurrencyId.
                if (selectedCurrency == null && fieldDAO.getSelectedCurrencyId() > 0) {
                    selectedCurrency = ESP_LIB_Shared.getInstance().getCurrencyById(fieldDAO.getSelectedCurrencyId());
                    fieldDAO.setSelectedCurrencyId(selectedCurrency.getId());
                    fieldDAO.setSelectedCurrencySymbol(selectedCurrency.getSymobl());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (selectedCurrency != null) {
                if (isViewOnly) {
                    if (ESP_LIB_ListUsersApplicationsAdapterV2.Companion.isSubApplications())
                        holder.tValue.setText(selectedCurrency.getCode() + " (" + selectedCurrency.getSymobl() + ") " + holder.tValue.getText());
                    else {
                        holder.tValue.setText(selectedCurrency.getCode() + " (" + selectedCurrency.getSymobl() + ") " + fieldDAO.getValue());
                        holder.tCurrencyLabel.setText(label);
                        holder.tCurrencyLabel.setVisibility(View.VISIBLE);
                    }
                } else
                    holder.etCurrency.setText(selectedCurrency.getCode() + " (" + selectedCurrency.getSymobl() + ")");
            } else {
                if (isViewOnly) {
                    holder.tValue.setText("");
                    holder.tCurrencyLabel.setText("");
                    holder.tCurrencyLabel.setVisibility(View.GONE);
                } else
                    holder.etCurrency.setText("");
            }
            //

            int maxLength = 1000;

            if (fieldDAO.getMaxVal() > 0) {
                maxLength = fieldDAO.getMaxVal();
            }


            InputFilter[] FilterArray = new InputFilter[1];
            FilterArray[0] = new InputFilter.LengthFilter(maxLength);
            if (isViewOnly)
                holder.tValue.setFilters(FilterArray);
            else
                holder.etValue.setFilters(FilterArray);



           /* if (!isViewOnly) {
                ESP_LIB_DynamicFormSectionFieldDAO finalFieldDAO1 = fieldDAO;
                holder.btnClickArea.setOnClickListener(view -> {

                    if (materialAlertDialogBuilder == null) {
                        List<String> currencyCodesList = ESP_LIB_Shared.getInstance().getCurrencyCodesList(finalFieldDAO1);
                        String[] singleChoiceArr = currencyCodesList.toArray(new String[currencyCodesList.size()]);
                        mSelectedIndex = currencyCodesList.indexOf(holder.etCurrency.getText().toString());
                        if (currencyCodesList.size() <= 1)
                            return;
                        materialAlertDialogBuilder = new MaterialAlertDialogBuilder(mContext, R.style.Esp_Lib_Style_AlertDialogTheme)
                                .setTitle(mContext.getString(R.string.esp_lib_text_currency))
                                .setCancelable(false)
                                .setSingleChoiceItems(singleChoiceArr, mSelectedIndex, (dialogInterface, currencyPosition) -> {

                                    mSelectedIndex = materialAlertDialogBuilder.getListView().getCheckedItemPosition();
                                    ESP_LIB_CurrencyDAO ESPLIBCurrencyDAO = ESP_LIB_ESPApplication.getInstance().getCurrencies().get(currencyPosition);
                                    ESP_LIB_CurrencyDAO selectedCurrency1 = ESP_LIB_Shared.getInstance().getCurrencyByCode(ESPLIBCurrencyDAO.getCode());

                                    holder.etCurrency.setText(selectedCurrency1.getCode() + " (" + selectedCurrency1.getSymobl() + ")");

                                    finalFieldDAO1.setSelectedCurrencyId(selectedCurrency1.getId());
                                    finalFieldDAO1.setSelectedCurrencySymbol(selectedCurrency1.getSymobl());

                                    // if (isCalculatedMappedField)
                                    if (finalFieldDAO1.isTigger())
                                        ESP_LIB_CalculatedMappedRequestTrigger.submitCalculatedMappedRequest(mContext, isViewOnly, finalFieldDAO1);
                                    materialAlertDialogBuilder = null;
                                    dialogInterface.cancel();
                                })
                                .setNegativeButton(mContext.getString(R.string.esp_lib_text_cancel), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        materialAlertDialogBuilder = null;
                                        dialogInterface.cancel();
                                    }
                                })
                                .create();
                        materialAlertDialogBuilder.show();

                    }


                });
            }*/

            if (!isViewOnly) {
                if (!fieldDAO.isReadOnly()) {
                    List<String> currency = ESP_LIB_Shared.getInstance().getCurrencyCodesList(fieldDAO);
                    ESP_LIB_CustomSpinnerAdapter adapter = new ESP_LIB_CustomSpinnerAdapter(mContext, R.layout.esp_lib_row_custom_spinner, currency);
                    holder.msCurrency.setAdapter(adapter);
                    if (selectedCurrency != null) {
                        int preSelectedIndex = -1;
                        for (int i = 0; i < currency.size(); i++) {
                            String currencyCode = currency.get(i);
                            if (currencyCode.equals(selectedCurrency.getCode() + " (" + selectedCurrency.getSymobl() + ")")) {
                                preSelectedIndex = i;
                                break;
                            }
                        }
                        if (preSelectedIndex != -1) {
                            holder.msCurrency.setSelection(preSelectedIndex);
                            adapter.setSelectedIndex(preSelectedIndex);
                        }
                    }
                    ESP_LIB_DynamicFormSectionFieldDAO finalFieldDAO1 = fieldDAO;
                    holder.msCurrency.setOnItemSelectedListener(new ESP_LIB_MaterialSpinner.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(@NotNull ESP_LIB_MaterialSpinner parent, @org.jetbrains.annotations.Nullable View view, int spinnerPos, long id) {

                            ESP_LIB_CurrencyDAO currencyDAO = ESP_LIB_ESPApplication.getInstance().getCurrencies().get(spinnerPos);
                            ESP_LIB_CurrencyDAO selectedCurrency = ESP_LIB_Shared.getInstance().getCurrencyByCode(currencyDAO.getCode());

                            holder.etCurrency.setText(selectedCurrency.getCode() + " (" + selectedCurrency.getSymobl() + ")");

                            finalFieldDAO1.setSelectedCurrencyId(selectedCurrency.getId());
                            finalFieldDAO1.setSelectedCurrencySymbol(selectedCurrency.getSymobl());

                            adapter.setSelectedIndex(spinnerPos);

                            if (finalFieldDAO1.isTigger())
                                ESP_LIB_CalculatedMappedRequestTrigger.submitCalculatedMappedRequest(mContext, isViewOnly, finalFieldDAO1);
                        }

                        @Override
                        public void onNothingSelected(@NotNull ESP_LIB_MaterialSpinner parent) {

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

            if (isViewOnly) {
                if (fieldDAO.getValue() == null || fieldDAO.getValue().isEmpty())
                    holder.tValue.setText("-");

                holder.tCurrencyLabel.setText(label);
                holder.tCurrencyLabel.setVisibility(View.VISIBLE);
            }


        }

        ESP_LIB_DynamicFormSectionFieldDAO finalFieldDAO3 = fieldDAO;
        new Handler().postDelayed(() -> {
            if (criteriaListDAO != null && !criteriaListDAO.isValidate() && criteriaListDAO.form.getSections() != null && criteriaListDAO.form.getSections().size() == 1) {
                try {
                    holder.etValue.setText("");
                    validateForm(finalFieldDAO3);
                } catch (Exception e) {
                }
            }


        }, 2000);

    }

    private void validateForm(ESP_LIB_DynamicFormSectionFieldDAO fieldDAO) {
        ESP_LIB_Validation validation = new ESP_LIB_Validation(mApplicationFieldsAdapterListener, ESPLIBCriteriaFieldsListener,
                criteriaListDAO, fieldDAO);
        validation.setSectionListener(edisectionDetailslistener);
        validation.validateForm();
    }


}
