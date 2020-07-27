package com.esp.library.exceedersesp.controllers.Profile.adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;


import com.esp.library.R;
import com.esp.library.exceedersesp.controllers.Profile.ESP_LIB_EditSectionDetails;
import com.esp.library.exceedersesp.controllers.fieldstype.classes.ESP_LIB_AttachmentItem;
import com.esp.library.exceedersesp.controllers.fieldstype.classes.ESP_LIB_CurrencyItem;
import com.esp.library.exceedersesp.controllers.fieldstype.classes.ESP_LIB_DateItem;
import com.esp.library.exceedersesp.controllers.fieldstype.classes.ESP_LIB_EdittextItem;
import com.esp.library.exceedersesp.controllers.fieldstype.classes.ESP_LIB_LookupItem;
import com.esp.library.exceedersesp.controllers.fieldstype.classes.ESP_LIB_MultiSelectionItem;
import com.esp.library.exceedersesp.controllers.fieldstype.classes.ESP_LIB_RatingItem;
import com.esp.library.exceedersesp.controllers.fieldstype.classes.ESP_LIB_SingleSelectionItem;
import com.esp.library.exceedersesp.controllers.fieldstype.viewholders.ESP_LIB_AttachmentTypeViewHolder;
import com.esp.library.exceedersesp.controllers.fieldstype.viewholders.ESP_LIB_CurrencyEditTextTypeViewHolder;
import com.esp.library.exceedersesp.controllers.fieldstype.viewholders.ESP_LIB_EditTextTypeViewHolder;
import com.esp.library.exceedersesp.controllers.fieldstype.viewholders.ESP_LIB_MultipleSelectionTypeViewHolder;
import com.esp.library.exceedersesp.controllers.fieldstype.viewholders.ESP_LIB_PickerTypeViewHolder;
import com.esp.library.exceedersesp.controllers.fieldstype.viewholders.ESP_LIB_RatingTypeViewHolder;
import com.esp.library.exceedersesp.controllers.fieldstype.viewholders.ESP_LIB_SingleSelectionTypeViewHolder;
import com.esp.library.utilities.common.ESP_LIB_SharedPreference;

import java.util.List;

import utilities.data.applicants.dynamics.ESP_LIB_DynamicFormSectionFieldDAO;


public class ESP_LIB_ListofSectionsFieldsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    String TAG = getClass().getSimpleName();

    List<ESP_LIB_DynamicFormSectionFieldDAO> sectionsFields;
    Context mContext;
    boolean isViewOnly;
    boolean ischeckerror;
    ESP_LIB_SharedPreference pref;
    ESP_LIB_EditSectionDetails edisectionDetails;


    public ESP_LIB_ListofSectionsFieldsAdapter(List<ESP_LIB_DynamicFormSectionFieldDAO> sectionsFields, Context context,
                                               boolean ischeckerror, boolean isviewOnly) {
        this.sectionsFields = sectionsFields;
        this.ischeckerror = ischeckerror;
        this.isViewOnly = isviewOnly;
        pref = new ESP_LIB_SharedPreference(context);
    }

    public void getListenerContext(ESP_LIB_EditSectionDetails editSectionDetails) {
        edisectionDetails = editSectionDetails;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.mContext = parent.getContext();

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.esp_lib_item_add_application_field_type_separator, parent, false);

        RecyclerView.ViewHolder itemView = new SeparatorTypeViewHolder(v);

        switch (viewType) {
            case 1://Short EditText
            case 2://Multi EditText
            case 3://Numbers EditText
            case 10://Email EditText
            case 15://HyperLink EditText
            case 16://PhoneNumber EditText
            case 17://Rollup EditText
            case 18://Calculated EditText
            case 19://Map field

                if (isViewOnly) {
                    v = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.esp_lib_item_add_application_field_type_text_view, parent, false);

                } else {
                    v = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.esp_lib_item_add_application_field_type_edit_text, parent, false);

                }
                itemView = new ESP_LIB_EditTextTypeViewHolder(v);
                break;

            case 11://Currency Type
                if (isViewOnly) {
                    v = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.esp_lib_item_add_application_field_type_text_view, parent, false);

                } else {
                    v = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.esp_lib_item_add_application_field_type_currency, parent, false);
                }
                itemView = new ESP_LIB_CurrencyEditTextTypeViewHolder(v);

                break;
            case 9://ratingbar Type

                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.esp_lib_item_add_application_field_type_rating, parent, false);
                itemView = new ESP_LIB_RatingTypeViewHolder(v);

                break;

            case 5: //SingleSelection Type
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.esp_lib_item_add_application_field_type_single_selection, parent, false);
                itemView = new ESP_LIB_SingleSelectionTypeViewHolder(v);
                break;
            case 6: //MultiSelection Type
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.esp_lib_item_add_application_field_type_multi_selection, parent, false);
                itemView = new ESP_LIB_MultipleSelectionTypeViewHolder(v);
                break;
            case 7: //Attachment Type

                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.esp_lib_item_add_application_field_type_attachment, parent, false);

                itemView = new ESP_LIB_AttachmentTypeViewHolder(v);

                break;
            case 4: //DateType
            case 13: //Lookup Type
                if (isViewOnly) {
                    v = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.esp_lib_item_add_application_field_type_text_view, parent, false);

                } else {
                    v = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.esp_lib_item_add_application_field_type_picker, parent, false);
                }
                itemView = new ESP_LIB_PickerTypeViewHolder(v);

                break;

        }


        return itemView;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ESP_LIB_DynamicFormSectionFieldDAO fieldDAO = sectionsFields.get(position);
        if (isViewOnly)
            fieldDAO.setShowToUserOnly(true);


        switch (fieldDAO.getType()) {
            case 1://Short EditText
            case 2://Multi EditText
            case 3://Numbers EditText
            case 10://Email EditText
            case 15://HyperLink EditText
            case 16://PhoneNumber EditText
            case 17://Rollup EditText
            case 18://Calculated EditText
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

    private void populateEditTextItem(int position, RecyclerView.ViewHolder holder) {
        ESP_LIB_EdittextItem.getInstance().showEditTextItemView((ESP_LIB_EditTextTypeViewHolder) holder, position,
                sectionsFields.get(position), isViewOnly, mContext, null,
                false);
        ESP_LIB_EdittextItem.getInstance().getAdapter(edisectionDetails);
        ESP_LIB_EdittextItem.getInstance().getProfileAdapter(this);
    }

    private void populateCurrencyItem(int position, RecyclerView.ViewHolder holder) {
        ESP_LIB_CurrencyItem.getInstance().showCurrencyEditTextItemView((ESP_LIB_CurrencyEditTextTypeViewHolder) holder, position,
                sectionsFields.get(position), isViewOnly, mContext, null,
                false);
        ESP_LIB_CurrencyItem.getInstance().getAdapter(edisectionDetails);
    }

    private void populateDateItem(int position, RecyclerView.ViewHolder holder) {
        ESP_LIB_DateItem.getInstance().showDateTypeItemView((ESP_LIB_PickerTypeViewHolder) holder, position,
                sectionsFields.get(position), isViewOnly, mContext, null,
                false);
        ESP_LIB_DateItem.getInstance().getAdapter(edisectionDetails);
    }


    private void populateSingleItem(int position, RecyclerView.ViewHolder holder) {
        ESP_LIB_SingleSelectionItem.getInstance().showSingleSelectionTypeItemView((ESP_LIB_SingleSelectionTypeViewHolder) holder, position,
                sectionsFields.get(position), isViewOnly, mContext, null,
                false);
        ESP_LIB_SingleSelectionItem.getInstance().getAdapter(edisectionDetails);
    }

    private void populateMultiItem(int position, RecyclerView.ViewHolder holder) {
        ESP_LIB_MultiSelectionItem.getInstance().showMultiSelectionTypeItemView((ESP_LIB_MultipleSelectionTypeViewHolder) holder, position,
                sectionsFields.get(position), isViewOnly, mContext, null,
                false);
        ESP_LIB_MultiSelectionItem.getInstance().getAdapter(edisectionDetails);
    }


    private void populateRatingItem(int position, RecyclerView.ViewHolder holder) {
        ESP_LIB_RatingItem.getInstance().showRatingBarItemView((ESP_LIB_RatingTypeViewHolder) holder, position,
                sectionsFields.get(position), isViewOnly, mContext, null,
                false);
        ESP_LIB_RatingItem.getInstance().getAdapter(edisectionDetails);
    }


    private void populateLookupItem(int position, RecyclerView.ViewHolder holder) {
        ESP_LIB_LookupItem.getInstance().showLookUpTypeItemView((ESP_LIB_PickerTypeViewHolder) holder, position,
                sectionsFields.get(position), isViewOnly, mContext, null,
                false);
        ESP_LIB_LookupItem.getInstance().getAdapter(edisectionDetails);
    }


    private void populateAttachmentItem(int position, RecyclerView.ViewHolder holder) {
        ESP_LIB_AttachmentItem.getInstance().showAttachmentTypeItemView((ESP_LIB_AttachmentTypeViewHolder) holder, position,
                sectionsFields.get(position), isViewOnly, mContext, null,
                false);
        ESP_LIB_AttachmentItem.getInstance().getAdapter(edisectionDetails);
        ESP_LIB_AttachmentItem.getInstance().getAdapter(this);
    }


    @Override
    public int getItemViewType(int position) {
        return sectionsFields.get(position).getType();
    }

    @Override
    public int getItemCount() {
        return sectionsFields == null ? 0 : sectionsFields.size();
    }

    protected class SeparatorTypeViewHolder extends RecyclerView.ViewHolder {

        public SeparatorTypeViewHolder(View itemView) {
            super(itemView);


        }

    }

    public static class ParentViewHolder extends RecyclerView.ViewHolder {
        public ParentViewHolder(View v) {
            super(v);
        }
    }

    public class ActivitiesList extends ParentViewHolder {

        ImageButton ibRemoveCard;
        RecyclerView rvFields;

        public ActivitiesList(View v) {

            super(v);
            rvFields = itemView.findViewById(R.id.rvFields);
            ibRemoveCard = itemView.findViewById(R.id.ibRemoveCard);

        }

    }


}
