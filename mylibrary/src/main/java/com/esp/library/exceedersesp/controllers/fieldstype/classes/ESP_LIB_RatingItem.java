package com.esp.library.exceedersesp.controllers.fieldstype.classes;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.RatingBar;

import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.core.graphics.drawable.DrawableCompat;

import com.esp.library.exceedersesp.controllers.Profile.ESP_LIB_EditSectionDetails;
import com.esp.library.exceedersesp.controllers.fieldstype.other.ESP_LIB_CalculatedMappedRequestTrigger;
import com.esp.library.exceedersesp.controllers.fieldstype.other.ESP_LIB_Validation;
import com.esp.library.exceedersesp.controllers.fieldstype.viewholders.ESP_LIB_RatingTypeViewHolder;
import com.esp.library.utilities.common.ESP_LIB_CustomLogs;
import com.esp.library.utilities.setup.applications.ESP_LIB_ApplicationFieldsRecyclerAdapter;

import utilities.data.applicants.dynamics.ESP_LIB_DynamicFormSectionFieldDAO;
import utilities.data.applicants.dynamics.ESP_LIB_DynamicStagesCriteriaListDAO;
import utilities.interfaces.ESP_LIB_CriteriaFieldsListener;

public class ESP_LIB_RatingItem {

    private String TAG = getClass().getSimpleName();
    private static ESP_LIB_RatingItem ratingItem = null;
    private ESP_LIB_ApplicationFieldsRecyclerAdapter.ApplicationFieldsAdapterListener mApplicationFieldsAdapterListener;
    private ESP_LIB_CriteriaFieldsListener ESPLIBCriteriaFieldsListener;
    private ESP_LIB_DynamicStagesCriteriaListDAO criteriaListDAO;
    private boolean isViewOnly;
    private ESP_LIB_EditSectionDetails edisectionDetailslistener;

    public static ESP_LIB_RatingItem getInstance() {
        if (ratingItem == null)
            return ratingItem = new ESP_LIB_RatingItem();
        else
            return ratingItem;
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

    public void getAdapter(ESP_LIB_EditSectionDetails edisectionDetails) {
        edisectionDetailslistener = edisectionDetails;
    }

    public void showRatingBarItemView(final ESP_LIB_RatingTypeViewHolder holder, final int position,
                                      ESP_LIB_DynamicFormSectionFieldDAO fieldDAO, boolean isviewOnly,
                                      Context mContext, ESP_LIB_DynamicStagesCriteriaListDAO ESPLIBDynamicStagesCriteriaListDAO,
                                      boolean isCalculatedMappedField) {
        isViewOnly = isviewOnly;
        // SharedPreference pref = new SharedPreference(mContext);

        String label = fieldDAO.getLabel();
        AppCompatRatingBar ratingBar = holder.ratingBar;

        if (fieldDAO.getMaxVal() > 5) {
            ratingBar = new AppCompatRatingBar(mContext, null, android.R.attr.ratingBarStyleSmall);
            ratingBar.setIsIndicator(false);
            ratingBar.setStepSize(1);
            holder.parentlayout.addView(ratingBar);
            holder.ratingBar.setVisibility(View.GONE);
            holder.parentlayout.setVisibility(View.VISIBLE);
        }

        if (fieldDAO.isRequired() && !isViewOnly) {
            label += " *";
        }
        holder.tValueLabel.setText(label);
        ratingBar.setNumStars(fieldDAO.getMaxVal());
        ratingBar.setMax(fieldDAO.getMaxVal());


        if (fieldDAO.getValue().equalsIgnoreCase("-"))
            fieldDAO.setValue("");

        if (fieldDAO.getValue() != null && !TextUtils.isEmpty(fieldDAO.getValue())) {
            ratingBar.setRating(Integer.parseInt(fieldDAO.getValue()));
            fieldDAO.setValidate(true);
        }

        if (fieldDAO.getAllowedValuesCriteria() != null) {
            String ratingColor = fieldDAO.getAllowedValuesCriteria();
            Drawable drawable = ratingBar.getProgressDrawable();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                DrawableCompat.setTint(drawable, Color.parseColor(ratingColor));
            } else {
                drawable.setColorFilter(Color.parseColor(ratingColor), PorterDuff.Mode.SRC_IN);
            }
            //drawable.setColorFilter(Color.parseColor(ratingColor), PorterDuff.Mode.SRC_ATOP);
        }

        if (isViewOnly)
            ratingBar.setEnabled(false);


        if (!isViewOnly) {
            if (!fieldDAO.isReadOnly()) {
                ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                    public void onRatingChanged(RatingBar ratingBar, float rating,
                                                boolean fromUser) {
                        int ratingValue = Math.round(rating);
                        ESP_LIB_CustomLogs.displayLogs(TAG + " rating_value: " + ratingValue);
                        fieldDAO.setValue(String.valueOf(ratingValue));

                        if (rating > 0)
                            fieldDAO.setValidate(true);
                        else
                            fieldDAO.setValidate(false);

                        // if (isCalculatedMappedField)
                        if (fieldDAO.isTigger())
                            ESP_LIB_CalculatedMappedRequestTrigger.submitCalculatedMappedRequest(mContext, isViewOnly, fieldDAO);

                        validateForm(fieldDAO);

                    }
                });
            }
        }

        AppCompatRatingBar finalRatingBar = ratingBar;
        new Handler().postDelayed(() -> {
            if (criteriaListDAO != null && !criteriaListDAO.isValidate() && criteriaListDAO.form.getSections() != null && criteriaListDAO.form.getSections().size() == 1) {
                try {
                    finalRatingBar.setRating(0);
                    validateForm(fieldDAO);
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
