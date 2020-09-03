package com.esp.library.utilities.setup.applications;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.esp.library.R;
import com.esp.library.exceedersesp.ESP_LIB_BaseActivity;
import com.esp.library.exceedersesp.controllers.fieldstype.classes.ESP_LIB_AttachmentItem;
import com.esp.library.exceedersesp.controllers.fieldstype.classes.ESP_LIB_CurrencyItem;
import com.esp.library.exceedersesp.controllers.fieldstype.classes.ESP_LIB_DateItem;
import com.esp.library.exceedersesp.controllers.fieldstype.classes.ESP_LIB_EdittextItem;
import com.esp.library.exceedersesp.controllers.fieldstype.classes.ESP_LIB_LookupItem;
import com.esp.library.exceedersesp.controllers.fieldstype.classes.ESP_LIB_MultiSelectionItem;
import com.esp.library.exceedersesp.controllers.fieldstype.classes.ESP_LIB_RatingItem;
import com.esp.library.exceedersesp.controllers.fieldstype.classes.ESP_LIB_SingleSelectionItem;
import com.esp.library.exceedersesp.controllers.fieldstype.other.ESP_LIB_CalculatedMappedRequestTrigger;
import com.esp.library.exceedersesp.controllers.fieldstype.viewholders.ESP_LIB_AttachmentTypeViewHolder;
import com.esp.library.exceedersesp.controllers.fieldstype.viewholders.ESP_LIB_CurrencyEditTextTypeViewHolder;
import com.esp.library.exceedersesp.controllers.fieldstype.viewholders.ESP_LIB_EditTextTypeViewHolder;
import com.esp.library.exceedersesp.controllers.fieldstype.viewholders.ESP_LIB_MultipleSelectionTypeViewHolder;
import com.esp.library.exceedersesp.controllers.fieldstype.viewholders.ESP_LIB_PickerTypeViewHolder;
import com.esp.library.exceedersesp.controllers.fieldstype.viewholders.ESP_LIB_RatingTypeViewHolder;
import com.esp.library.exceedersesp.controllers.fieldstype.viewholders.ESP_LIB_SingleSelectionTypeViewHolder;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import utilities.data.applicants.dynamics.ESP_LIB_DynamicFormSectionDAO;
import utilities.data.applicants.dynamics.ESP_LIB_DynamicFormSectionFieldDAO;
import utilities.data.applicants.dynamics.ESP_LIB_DynamicStagesCriteriaListDAO;
import utilities.interfaces.ESP_LIB_CriteriaFieldsListener;

public class ESP_LIB_ApplicationFieldsRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ESP_LIB_DynamicFormSectionFieldDAO> mApplicationFields;
    private Context mContext;
    private ApplicationFieldsAdapterListener mApplicationFieldsAdapterListener;
    private String actualResponseJson;
    private boolean isViewOnly;
    private ESP_LIB_DynamicStagesCriteriaListDAO ESPLIBDynamicStagesCriteriaListDAO;
    private int sectionType;
    public static int SECTIONCONSTANT = 2;
    private ESP_LIB_DynamicStagesCriteriaListDAO criteriaListDAO;
    private ESP_LIB_CriteriaFieldsListener ESPLIBCriteriaFieldsListener;
    public static boolean isCalculatedMappedField = false;
    List<ESP_LIB_DynamicFormSectionDAO> mApplications;

    public ESP_LIB_ApplicationFieldsRecyclerAdapter(String actualResponseJson, boolean isViewOnly, ESP_LIB_BaseActivity context,
                                                    ESP_LIB_DynamicStagesCriteriaListDAO dynamicstagesCriteriaListDAOESPLIB, int sectionType) {
        this.actualResponseJson = actualResponseJson;
        this.isViewOnly = isViewOnly;
        this.ESPLIBDynamicStagesCriteriaListDAO = dynamicstagesCriteriaListDAOESPLIB;
        this.sectionType = sectionType;
        try {
            ESPLIBCriteriaFieldsListener = (ESP_LIB_CriteriaFieldsListener) context;
        } catch (Exception e) {

        }

        if (ESPLIBDynamicStagesCriteriaListDAO != null && ESPLIBDynamicStagesCriteriaListDAO.getAssessmentStatus() == null)
            ESPLIBDynamicStagesCriteriaListDAO.setAssessmentStatus("");
        if (this.isViewOnly && (ESPLIBDynamicStagesCriteriaListDAO != null && ESPLIBDynamicStagesCriteriaListDAO.getAssessmentStatus().equalsIgnoreCase(context.getString(R.string.esp_lib_text_active))))
            this.isViewOnly = false;
        else if (sectionType == SECTIONCONSTANT)
            this.isViewOnly = true;
    }

    public void getCriteriaObject(ESP_LIB_DynamicStagesCriteriaListDAO criterialistDAO) {
        criteriaListDAO = criterialistDAO;
    }

    public interface ApplicationFieldsAdapterListener {
        void onFieldValuesChanged();

        void onAttachmentFieldClicked(ESP_LIB_DynamicFormSectionFieldDAO fieldDAO, int postion);

        void onLookupFieldClicked(ESP_LIB_DynamicFormSectionFieldDAO fieldDAO, int position, boolean isCalculatedMappedField);

    }


    public interface ApplicationDetailFieldsAdapterListener {
        void onFieldValuesChanged();

        void onAttachmentFieldClicked(ESP_LIB_DynamicFormSectionFieldDAO fieldDAO, int position);

        void onLookupFieldClicked(ESP_LIB_DynamicFormSectionFieldDAO fieldDAO, int position, boolean isCalculatedMappedField);


    }


    public void setmApplicationFieldsAdapterListener(ApplicationFieldsAdapterListener mApplicationFieldsAdapterListener) {
        this.mApplicationFieldsAdapterListener = mApplicationFieldsAdapterListener;
    }

    public void setRefreshList(List<ESP_LIB_DynamicFormSectionFieldDAO> dynamicFormSectionFields) {
        if (dynamicFormSectionFields == null)
            dynamicFormSectionFields = new ArrayList<>();
        this.mApplicationFields = dynamicFormSectionFields;
        notifyDataSetChanged();
    }

    public void setmApplications(List<ESP_LIB_DynamicFormSectionDAO> applications) {
        mApplications = applications;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.mContext = parent.getContext();


        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.esp_lib_item_add_application_field_type_separator, parent, false);

        RecyclerView.ViewHolder itemView = new SeparatorTypeViewHolder(v);

        int item_layout = R.layout.esp_lib_item_add_application_field_type_text_view;
        try {
            if (ESP_LIB_ListUsersApplicationsAdapterV2.Companion.isSubApplications())
                item_layout = R.layout.esp_lib_item_sub_application_field_type_text_view;
        } catch (Exception e) {
        }

        switch (viewType) {

            case 1://Short EditText
            case 2://Multi EditText
            case 3://Numbers EditText
            case 10://Email EditText
            case 15://HyperLink EditText
            case 16://PhoneNumber EditText
            case 17://Rollup Text
            case 18://Calculated Text
            case 19://Map field
                v = populateViews(parent, v, item_layout, R.layout.esp_lib_item_add_application_field_type_edit_text);
                itemView = new ESP_LIB_EditTextTypeViewHolder(v);
                break;
            case 11://Currency Type
                v = populateViews(parent, v, item_layout, R.layout.esp_lib_item_add_application_field_type_currency);
                itemView = new ESP_LIB_CurrencyEditTextTypeViewHolder(v);
                break;
            case 9://ratingbar Type
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.esp_lib_item_add_application_field_type_rating, parent, false);
                itemView = new ESP_LIB_RatingTypeViewHolder(v);
                break;
            case 5://SingleSelection Type
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.esp_lib_item_add_application_field_type_single_selection, parent, false);
                itemView = new ESP_LIB_SingleSelectionTypeViewHolder(v);
                break;
            case 6://MultiSelection Type
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.esp_lib_item_add_application_field_type_multi_selection, parent, false);
                itemView = new ESP_LIB_MultipleSelectionTypeViewHolder(v);
                break;
            case 7://Attachment Type
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.esp_lib_item_add_application_field_type_attachment, parent, false);
                itemView = new ESP_LIB_AttachmentTypeViewHolder(v);
                break;
            case 4://DateType
            case 13://Lookup Type
                v = populateViews(parent, v, item_layout, R.layout.esp_lib_item_add_application_field_type_picker);
                itemView = new ESP_LIB_PickerTypeViewHolder(v);
                break;

        }


        return itemView;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        mApplicationFields.get(position).setSectionType(sectionType);

        ESP_LIB_DynamicFormSectionFieldDAO fieldDAO = mApplicationFields.get(position);
        if (isViewOnly)
            mApplicationFields.get(position).setShowToUserOnly(true);

        // if ((fieldDAO.getType() == 18 ) && !isCalculatedMappedField && !isViewOnly) {
        if (fieldDAO.isTigger() && !isCalculatedMappedField && !isViewOnly) {
            isCalculatedMappedField = true;
            ESP_LIB_CalculatedMappedRequestTrigger.submitCalculatedMappedRequest(mContext, isViewOnly, fieldDAO);
        } else if (fieldDAO.getType() == 13 && fieldDAO.getAllowedValuesCriteria() != null &&
                !fieldDAO.getAllowedValuesCriteria().isEmpty() && !isCalculatedMappedField) {

            JSONArray jsonArray = null;
            try {
                jsonArray = new JSONArray(fieldDAO.getAllowedValuesCriteria());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (jsonArray != null && jsonArray.length() > 0) {
                isCalculatedMappedField = true;
                ESP_LIB_CalculatedMappedRequestTrigger.submitCalculatedMappedRequest(mContext, isViewOnly, fieldDAO);
            }
        }

        switch (fieldDAO.getType()) {
            case 1://Short EditText
            case 2://Multi EditText
            case 3://Numbers EditText
            case 10://Email EditText
            case 15://HyperLink EditText
            case 16://PhoneNumber EditText
            case 17://Rollup Text
            case 18://Calculated Text
            case 19://Map Field
                populateEditTextItem(position, holder);
                break;
            case 11://Currency Type
                populateCurrencyItem(position, holder);
                break;
            case 4://DateType
                populateDateItem(position, holder);
                break;
            case 7://Attachment Type
                populateAttachmentItem(position, holder);
                break;
            case 13://Lookup Type
                populateLookupItem(position, holder);
                break;
            case 5://SingleSelection Type
                populateSingleItem(position, holder);
                break;
            case 6://MultiSelection Type
                populateMultiItem(position, holder);
                break;
            case 9://Ratingbar Type
                populateRatingItem(position, holder);
                break;
        }


    }

    @Override
    public int getItemViewType(int position) {
        return mApplicationFields.get(position).getType();
    }

    @Override
    public int getItemCount() {
        return mApplicationFields == null ? 0 : mApplicationFields.size();
    }


    private View populateViews(ViewGroup parent, View v, int item_layout, int viewLayout) {
        if (isViewOnly) {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(item_layout, parent, false);
        } else {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(viewLayout, parent, false);

        }
        return v;
    }

    private void populateEditTextItem(int position, RecyclerView.ViewHolder holder) {
        ESP_LIB_EdittextItem.getInstance().showEditTextItemView((ESP_LIB_EditTextTypeViewHolder) holder, position,
                mApplicationFields.get(position), isViewOnly, mContext, ESPLIBDynamicStagesCriteriaListDAO,
                isCalculatedMappedField);
        ESP_LIB_EdittextItem.getInstance().mApplicationFieldsAdapterListener(mApplicationFieldsAdapterListener);
        ESP_LIB_EdittextItem.getInstance().criteriaFieldsListener(ESPLIBCriteriaFieldsListener);
        ESP_LIB_EdittextItem.getInstance().criteriaListDAO(criteriaListDAO);
        ESP_LIB_EdittextItem.getInstance().getAdapter(this);
    }

    private void populateCurrencyItem(int position, RecyclerView.ViewHolder holder) {
        ESP_LIB_CurrencyItem.getInstance().showCurrencyEditTextItemView((ESP_LIB_CurrencyEditTextTypeViewHolder) holder, position,
                mApplicationFields.get(position), isViewOnly, mContext, ESPLIBDynamicStagesCriteriaListDAO,
                isCalculatedMappedField);
        ESP_LIB_CurrencyItem.getInstance().mApplicationFieldsAdapterListener(mApplicationFieldsAdapterListener);
        ESP_LIB_CurrencyItem.getInstance().criteriaFieldsListener(ESPLIBCriteriaFieldsListener);
        ESP_LIB_CurrencyItem.getInstance().criteriaListDAO(criteriaListDAO);
        ESP_LIB_CurrencyItem.getInstance().getactualResponseJson(actualResponseJson);
    }

    private void populateDateItem(int position, RecyclerView.ViewHolder holder) {
        ESP_LIB_DateItem.getInstance().showDateTypeItemView((ESP_LIB_PickerTypeViewHolder) holder, position,
                mApplicationFields.get(position), isViewOnly, mContext, ESPLIBDynamicStagesCriteriaListDAO,
                isCalculatedMappedField);
        ESP_LIB_DateItem.getInstance().mApplicationFieldsAdapterListener(mApplicationFieldsAdapterListener);
        ESP_LIB_DateItem.getInstance().criteriaFieldsListener(ESPLIBCriteriaFieldsListener);
        ESP_LIB_DateItem.getInstance().criteriaListDAO(criteriaListDAO);
        ESP_LIB_DateItem.getInstance().getactualResponseJson(actualResponseJson);

    }

    private void populateLookupItem(int position, RecyclerView.ViewHolder holder) {
        ESP_LIB_LookupItem.getInstance().mApplicationFieldsAdapterListener(mApplicationFieldsAdapterListener);
        ESP_LIB_LookupItem.getInstance().criteriaFieldsListener(ESPLIBCriteriaFieldsListener);
        ESP_LIB_LookupItem.getInstance().criteriaListDAO(criteriaListDAO);
        ESP_LIB_LookupItem.getInstance().getactualResponseJson(actualResponseJson);
        ESP_LIB_LookupItem.getInstance().showLookUpTypeItemView((ESP_LIB_PickerTypeViewHolder) holder, position,
                mApplicationFields.get(position), isViewOnly, mContext, ESPLIBDynamicStagesCriteriaListDAO,
                isCalculatedMappedField);
    }

    private void populateAttachmentItem(int position, RecyclerView.ViewHolder holder) {
        ESP_LIB_AttachmentItem.getInstance().showAttachmentTypeItemView((ESP_LIB_AttachmentTypeViewHolder) holder, position,
                mApplicationFields.get(position), isViewOnly, mContext, ESPLIBDynamicStagesCriteriaListDAO,
                isCalculatedMappedField);
        ESP_LIB_AttachmentItem.getInstance().mApplicationFieldsAdapterListener(mApplicationFieldsAdapterListener);
        ESP_LIB_AttachmentItem.getInstance().criteriaFieldsListener(ESPLIBCriteriaFieldsListener);
        ESP_LIB_AttachmentItem.getInstance().criteriaListDAO(criteriaListDAO);
        ESP_LIB_AttachmentItem.getInstance().getAdapter(this);
    }

    private void populateSingleItem(int position, RecyclerView.ViewHolder holder) {
        ESP_LIB_SingleSelectionItem.getInstance().mApplicationFieldsAdapterListener(mApplicationFieldsAdapterListener);
        ESP_LIB_SingleSelectionItem.getInstance().criteriaFieldsListener(ESPLIBCriteriaFieldsListener);
        ESP_LIB_SingleSelectionItem.getInstance().criteriaListDAO(criteriaListDAO);
        ESP_LIB_SingleSelectionItem.getInstance().getactualResponseJson(actualResponseJson);
        ESP_LIB_SingleSelectionItem.getInstance().showSingleSelectionTypeItemView((ESP_LIB_SingleSelectionTypeViewHolder) holder, position,
                mApplicationFields.get(position), isViewOnly, mContext, ESPLIBDynamicStagesCriteriaListDAO,
                isCalculatedMappedField);
    }

    private void populateMultiItem(int position, RecyclerView.ViewHolder holder) {
        ESP_LIB_MultiSelectionItem.getInstance().mApplicationFieldsAdapterListener(mApplicationFieldsAdapterListener);
        ESP_LIB_MultiSelectionItem.getInstance().criteriaFieldsListener(ESPLIBCriteriaFieldsListener);
        ESP_LIB_MultiSelectionItem.getInstance().criteriaListDAO(criteriaListDAO);
        ESP_LIB_MultiSelectionItem.getInstance().getactualResponseJson(actualResponseJson);
        ESP_LIB_MultiSelectionItem.getInstance().showMultiSelectionTypeItemView((ESP_LIB_MultipleSelectionTypeViewHolder) holder, position,
                mApplicationFields.get(position), isViewOnly, mContext, ESPLIBDynamicStagesCriteriaListDAO,
                isCalculatedMappedField);
    }

    private void populateRatingItem(int position, RecyclerView.ViewHolder holder) {
        ESP_LIB_RatingItem.getInstance().showRatingBarItemView((ESP_LIB_RatingTypeViewHolder) holder, position,
                mApplicationFields.get(position), isViewOnly, mContext, ESPLIBDynamicStagesCriteriaListDAO,
                isCalculatedMappedField);
        ESP_LIB_RatingItem.getInstance().mApplicationFieldsAdapterListener(mApplicationFieldsAdapterListener);
        ESP_LIB_RatingItem.getInstance().criteriaFieldsListener(ESPLIBCriteriaFieldsListener);
        ESP_LIB_RatingItem.getInstance().criteriaListDAO(criteriaListDAO);
    }

    protected class SeparatorTypeViewHolder extends RecyclerView.ViewHolder {

        LinearLayout llmain;

        public SeparatorTypeViewHolder(View itemView) {
            super(itemView);
            llmain = itemView.findViewById(R.id.llmain);
            llmain.setVisibility(View.GONE);
        }

    }


}
