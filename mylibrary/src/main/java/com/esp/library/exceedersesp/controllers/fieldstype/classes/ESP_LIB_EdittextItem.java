package com.esp.library.exceedersesp.controllers.fieldstype.classes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;

import androidx.core.content.ContextCompat;

import com.esp.library.R;
import com.esp.library.exceedersesp.controllers.Profile.ESP_LIB_EditSectionDetails;
import com.esp.library.exceedersesp.controllers.Profile.adapters.ESP_LIB_ListofSectionsFieldsAdapter;
import com.esp.library.exceedersesp.controllers.fieldstype.other.ESP_LIB_CalculatedMappedRequestTrigger;
import com.esp.library.exceedersesp.controllers.fieldstype.other.ESP_LIB_Validation;
import com.esp.library.exceedersesp.controllers.fieldstype.viewholders.ESP_LIB_EditTextTypeViewHolder;
import com.esp.library.utilities.common.ESP_LIB_CustomLogs;
import com.esp.library.utilities.common.ESP_LIB_Shared;
import com.esp.library.utilities.common.ESP_LIB_SharedPreference;
import com.esp.library.utilities.setup.applications.ESP_LIB_ApplicationFieldsRecyclerAdapter;
import com.esp.library.utilities.setup.applications.ESP_LIB_ListUsersApplicationsAdapterV2;

import utilities.data.applicants.dynamics.ESP_LIB_DynamicFormSectionFieldDAO;
import utilities.data.applicants.dynamics.ESP_LIB_DynamicStagesCriteriaListDAO;
import utilities.interfaces.ESP_LIB_CriteriaFieldsListener;

public class ESP_LIB_EdittextItem {

    private String TAG = getClass().getSimpleName();
    private static ESP_LIB_EdittextItem edittextItem = null;
    private ESP_LIB_ApplicationFieldsRecyclerAdapter.ApplicationFieldsAdapterListener mApplicationFieldsAdapterListener;
    private ESP_LIB_CriteriaFieldsListener ESPLIBCriteriaFieldsListener;
    private ESP_LIB_DynamicStagesCriteriaListDAO criteriaListDAO;
    private boolean isViewOnly, isCalculatedMappedField;
    private ESP_LIB_ApplicationFieldsRecyclerAdapter applicationFieldsRecyclerAdapter;
    ESP_LIB_EditSectionDetails edisectionDetailslistener;
    ESP_LIB_SharedPreference pref;

    ESP_LIB_ListofSectionsFieldsAdapter listofSectionsFieldsAdapter;

    public static ESP_LIB_EdittextItem getInstance() {
        if (edittextItem == null)
            return edittextItem = new ESP_LIB_EdittextItem();
        else
            return edittextItem;
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

    public void getAdapter(ESP_LIB_ApplicationFieldsRecyclerAdapter applicationfieldsRecyclerAdapter) {
        applicationFieldsRecyclerAdapter = applicationfieldsRecyclerAdapter;
    }

    public void getAdapter(ESP_LIB_EditSectionDetails edisectionDetails) {
        edisectionDetailslistener = edisectionDetails;
    }

    public void getProfileAdapter(ESP_LIB_ListofSectionsFieldsAdapter listofsectionsFieldsAdapter) {
        listofSectionsFieldsAdapter = listofsectionsFieldsAdapter;
    }


    @SuppressLint("ClickableViewAccessibility")
    public void showEditTextItemView(final ESP_LIB_EditTextTypeViewHolder holder, final int position,
                                     ESP_LIB_DynamicFormSectionFieldDAO fieldDAO, boolean isviewOnly,
                                     Context mContext, ESP_LIB_DynamicStagesCriteriaListDAO ESPLIBDynamicStagesCriteriaListDAO,
                                     boolean iscalculatedMappedField) {
        isViewOnly = isviewOnly;
        isCalculatedMappedField = iscalculatedMappedField;
        pref = new ESP_LIB_SharedPreference(mContext);
        // final DynamicFormSectionFieldDAO fieldDAO = fieldDAO;

        if (fieldDAO.getId() == 5366 || fieldDAO.getId() == 3614) // for stemex
        {
            fieldDAO.setReadOnly(true);
        }

        try {
            if (pref.getLanguage().equalsIgnoreCase("ar")) {
                holder.tilFieldLabel.setGravity(Gravity.RIGHT);
                holder.etValue.setGravity(Gravity.RIGHT);
                if (isViewOnly) {
                    holder.tValueLabel.setGravity(Gravity.RIGHT);
                    holder.tValue.setGravity(Gravity.RIGHT);
                }

            } else {

                holder.tilFieldLabel.setGravity(Gravity.LEFT);
                holder.etValue.setGravity(Gravity.LEFT);
                if (isViewOnly) {
                    holder.tValueLabel.setGravity(Gravity.LEFT);
                    holder.tValue.setGravity(Gravity.LEFT);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!isViewOnly) {
            if (fieldDAO.getType() != 10 && fieldDAO.getType() != 15) // 10 = Email 15 = hyperlink
                holder.etValue.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
            else
                holder.etValue.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        }

        String getValue = fieldDAO.getValue();

        String dateFormat = ESP_LIB_Shared.getInstance().getDisplayDate(mContext, getValue, true);

        if (dateFormat != null && !dateFormat.isEmpty())
            getValue = dateFormat;


        if (ESPLIBDynamicStagesCriteriaListDAO != null &&
                (!ESPLIBDynamicStagesCriteriaListDAO.isOwner() && ESPLIBDynamicStagesCriteriaListDAO.getAssessmentStatus().equalsIgnoreCase(mContext.getString(R.string.esp_lib_text_active)))) {
            holder.etValue.setText(fieldDAO.getLabel());
            holder.etValue.setEnabled(false);
            holder.etValue.setFocusable(false);

            return;
        }


        if (isViewOnly || fieldDAO.isMappedCalculatedField()) {

            if (fieldDAO.getType() == 15 && getValue != null && !getValue.isEmpty()) {
                holder.tValue.setSingleLine(true);
                holder.tValue.setEllipsize(TextUtils.TruncateAt.END);
                if (!getValue.startsWith("http"))
                    getValue = "http://" + getValue;
            }


            String label = fieldDAO.getLabel();
            if (ESP_LIB_ListUsersApplicationsAdapterV2.Companion.isSubApplications())
                label = fieldDAO.getLabel() + ":";
            holder.tValueLabel.setText(label);
            holder.tValue.setText(getValue);
            fieldDAO.setValidate(true);

            if (getValue == null || getValue.isEmpty() || getValue.equalsIgnoreCase("http://-"))
                holder.tValue.setText("-");

            try {
                if (fieldDAO.getType() == 17) {
                    if (getValue == null || getValue.replaceAll("\\s", "").length() == 0)
                        holder.tValue.setVisibility(View.GONE);
                    else
                        holder.tValue.setVisibility(View.VISIBLE);
                    holder.ivicon.setVisibility(View.GONE);
                    holder.ivicon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.esp_lib_drawable_ic_show_rollup));
                } else if (fieldDAO.getType() == 18) {
                    if (getValue == null || getValue.replaceAll("\\s", "").length() == 0)
                        holder.tValue.setVisibility(View.GONE);
                    else
                        holder.tValue.setVisibility(View.VISIBLE);
                    holder.ivicon.setVisibility(View.GONE);
                    holder.ivicon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.esp_lib_drawable_ic_show_calculated));
                } else if (fieldDAO.getType() == 19) {


                    if (getValue == null || getValue.replaceAll("\\s", "").length() == 0)
                        holder.tValue.setVisibility(View.GONE);
                    else
                        holder.tValue.setVisibility(View.VISIBLE);
                } else if (fieldDAO.getType() == 2) {

                    /*CommonMethodsKotlin.applyCustomEllipsizeSpanning(maxLines, holder.tValue, mContext);

                    holder.tValue.setOnClickListener(v -> {

                        if (holder.tValue.getText().toString().contains("[ ... ]")) {
                            maxLines = 1000;
                        } else {
                            maxLines = 5;
                        }
                       // CommonMethodsKotlin.applyCustomEllipsizeSpanning(maxLines, holder.tValue, mContext);
                        if (applicationFieldsRecyclerAdapter != null)
                            applicationFieldsRecyclerAdapter.notifyItemChanged(position);

                        if (listofSectionsFieldsAdapter != null)
                            listofSectionsFieldsAdapter.notifyItemChanged(position);


                    });*/

                }


            } catch (Exception e) {
                e.printStackTrace();
            }


            validateForm(fieldDAO, ESPLIBDynamicStagesCriteriaListDAO);

            return;
        }


        holder.etValue.setOnFocusChangeListener((view, isfocusable) -> {

            if (fieldDAO.getType() == 17 || fieldDAO.getType() == 18 || fieldDAO.getType() == 19) {

                fieldDAO.setValue("");
                fieldDAO.setValidate(true);
                return;
            }
            if (!isfocusable) {
                // if (isCalculatedMappedField )
                if (fieldDAO.isTigger())
                    ESP_LIB_CalculatedMappedRequestTrigger.submitCalculatedMappedRequest(mContext, isViewOnly, fieldDAO);
            } else {
                new Handler().postDelayed(() -> {
                    String text=holder.etValue.getText().toString();
                    holder.etValue.setText(text);
                    holder.etValue.setSelection(text.length());
                    }, 500);
            }
        });

        holder.etValue.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                //Clear focus here from edittext
                holder.etValue.clearFocus();
            }
            return false;
        });

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
                fieldDAO.setValidate(false);
                holder.tilFieldLabel.setError(null);
                holder.tilFieldLabel.setErrorEnabled(false);
                if (!fieldDAO.isReadOnly() && fieldDAO.getType() != 19) {
                    error = ESP_LIB_Shared.getInstance().edittextErrorChecks(mContext, outputedText, error, fieldDAO);
                }

                if (error.length() > 0) {
                    holder.tilFieldLabel.setErrorEnabled(true);
                    holder.tilFieldLabel.setError(error);
                    fieldDAO.setValue(outputedText);
                } else {

                    fieldDAO.setValue(outputedText);
                    fieldDAO.setValidate(true);

                    if (fieldDAO.isRequired() && outputedText.isEmpty())
                        fieldDAO.setValidate(false);


                }
                validateForm(fieldDAO, ESPLIBDynamicStagesCriteriaListDAO);
            }
        });

        String label = fieldDAO.getLabel();

        if (fieldDAO.isRequired() && !isViewOnly) {
            label += " *";
        }


        holder.tilFieldLabel.setHint(label);
        holder.etValue.setText(getValue);

        if (getValue == null || getValue.isEmpty())  // used for 1 case... disable criteria approve button if criteria has only one required field
        {

            final Handler handler = new Handler();
            String finalGetValue = getValue;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    holder.etValue.setText("a");
                    holder.etValue.setText(finalGetValue);
                }
            }, 500);


        }


        holder.etValue.setSingleLine(false);
        if (fieldDAO.isReadOnly() || fieldDAO.getType() == 17 || fieldDAO.getType() == 18
                || fieldDAO.getType() == 19) {
            ESP_LIB_CustomLogs.displayLogs(TAG + " getTargetFieldType: " + fieldDAO.getTargetFieldType());
            //setPropertiesBasedOnType(fieldDAO.getTargetFieldType(), holder, fieldDAO);
            holder.etValue.setEnabled(false);
          /*  if (fieldDAO.isReadOnly()) {
                holder.tilFieldDisableLabel.setVisibility(View.VISIBLE);
                holder.tilFieldLabel.setVisibility(View.GONE);
                holder.etvalueDisable.setText(getValue);
                holder.tilFieldDisableLabel.setHint(label);
            } else {
                holder.tilFieldDisableLabel.setVisibility(View.GONE);
                holder.tilFieldLabel.setVisibility(View.VISIBLE);
                holder.tilFieldLabel.setHint(label);
            }*/
        } else
            holder.etValue.setEnabled(true);


        int maxLength = 1000;
        switch (fieldDAO.getType()) {
            case 1:// Short EditText
                holder.etValue.setSingleLine(true);
                holder.etValue.setImeOptions(EditorInfo.IME_ACTION_DONE);
                break;
            case 2:// MultiEditText
                holder.etValue.setMinLines(3);
                holder.etValue.setMaxLines(5);

                holder.etValue.setOnTouchListener((v, event) -> {
                    if (holder.etValue.hasFocus()) {
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_SCROLL) {
                            v.getParent().requestDisallowInterceptTouchEvent(false);
                            return true;
                        }
                    }
                    return false;
                });

                break;
            case 3:// NumbersEditText
                //  holder.etValue.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);

                holder.etValue.setRawInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                holder.etValue.setKeyListener(DigitsKeyListener.getInstance("0123456789."));
                holder.etValue.setImeOptions(EditorInfo.IME_ACTION_DONE);
                break;
            case 10:// EmailEditText
                if (!isViewOnly) {
                    if (pref.getLanguage().equalsIgnoreCase("en")) {

                        holder.etValue.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.esp_lib_drawable_ic_icons_message_grey, 0);
                    } else {

                        holder.etValue.setCompoundDrawablesWithIntrinsicBounds(R.drawable.esp_lib_drawable_ic_icons_message_grey, 0, 0, 0);
                    }
                }
                holder.etValue.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                holder.etValue.setImeOptions(EditorInfo.IME_ACTION_DONE);
                break;
            case 15:// HyperLink EditText
                if (!isViewOnly) {
                    if (pref.getLanguage().equalsIgnoreCase("en")) {

                        holder.etValue.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.esp_lib_drawable_ic_icons_event_link_grey, 0);
                    } else {

                        holder.etValue.setCompoundDrawablesWithIntrinsicBounds(R.drawable.esp_lib_drawable_ic_icons_event_link_grey, 0, 0, 0);
                    }
                }
                holder.etValue.setSingleLine(true);
                holder.etValue.setImeOptions(EditorInfo.IME_ACTION_DONE);
                break;
            case 16:// PhoneNumber EditText
                if (!isViewOnly) {
                    if (pref.getLanguage().equalsIgnoreCase("en")) {
                        holder.etValue.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.esp_lib_drawable_ic_icons_phone_grey, 0);
                    } else {
                        holder.etValue.setCompoundDrawablesWithIntrinsicBounds(R.drawable.esp_lib_drawable_ic_icons_phone_grey, 0, 0, 0);
                    }
                }
                holder.etValue.setInputType(InputType.TYPE_CLASS_PHONE);
                holder.etValue.setImeOptions(EditorInfo.IME_ACTION_DONE);
                maxLength = 15;
                break;
        }

        //    setPropertiesBasedOnType(fieldDAO.getType(), holder, fieldDAO);

        if (fieldDAO.getMaxVal() > 0) {
            maxLength = fieldDAO.getMaxVal();
        }


        if (fieldDAO.getType() == 3)
            maxLength = 9;

        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(maxLength);
        holder.etValue.setFilters(FilterArray);


        if (fieldDAO.isReadOnly()) {

            if (fieldDAO.getType() == 15) {
                holder.etValue.setEnabled(true);
                holder.etValue.setFocusable(false);
            }
            fieldDAO.setValidate(true);
            holder.etValue.setTextColor(ContextCompat.getColor(mContext, R.color.esp_lib_color_coolgrey));
            if (pref.getLanguage().equalsIgnoreCase("en"))
                holder.etValue.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.esp_lib_drawable_ic_lock_grey, 0);
            else
                holder.etValue.setCompoundDrawablesWithIntrinsicBounds(R.drawable.esp_lib_drawable_ic_lock_grey, 0, 0, 0);

        }

        validateForm(fieldDAO, ESPLIBDynamicStagesCriteriaListDAO);


        new Handler().postDelayed(() -> {
            try {
                if (criteriaListDAO != null && !criteriaListDAO.isValidate() && criteriaListDAO.form.getSections() != null && criteriaListDAO.form.getSections().size() == 1) {
                    holder.etValue.setText("");
                    validateForm(fieldDAO, ESPLIBDynamicStagesCriteriaListDAO);
                }
            } catch (Exception e) {
            }
        }, 2000);

    }

    private void validateForm(ESP_LIB_DynamicFormSectionFieldDAO fieldDAO, ESP_LIB_DynamicStagesCriteriaListDAO dynamicStagesCriteriaListDAO) {
        ESP_LIB_Validation validation = new ESP_LIB_Validation(mApplicationFieldsAdapterListener, ESPLIBCriteriaFieldsListener,
                dynamicStagesCriteriaListDAO, fieldDAO);
        validation.setSectionListener(edisectionDetailslistener);
        validation.validateForm();
    }


}
