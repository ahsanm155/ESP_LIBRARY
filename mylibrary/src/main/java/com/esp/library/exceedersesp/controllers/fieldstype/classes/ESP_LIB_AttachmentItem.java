package com.esp.library.exceedersesp.controllers.fieldstype.classes;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;

import androidx.appcompat.widget.PopupMenu;


import com.esp.library.R;
import com.esp.library.exceedersesp.controllers.Profile.ESP_LIB_EditSectionDetails;
import com.esp.library.exceedersesp.controllers.Profile.adapters.ESP_LIB_ListofSectionsFieldsAdapter;
import com.esp.library.exceedersesp.controllers.fieldstype.other.ESP_LIB_AttachmentImageDownload;
import com.esp.library.exceedersesp.controllers.fieldstype.other.ESP_LIB_Validation;
import com.esp.library.exceedersesp.controllers.fieldstype.viewholders.ESP_LIB_AttachmentTypeViewHolder;
import com.esp.library.utilities.common.ESP_LIB_SharedPreference;
import com.esp.library.utilities.setup.applications.ESP_LIB_ApplicationFieldsRecyclerAdapter;

import utilities.adapters.setup.applications.ESP_LIB_ListUsersApplicationsAdapter;
import utilities.common.ESP_LIB_CommonMethodsKotlin;
import utilities.data.applicants.dynamics.ESP_LIB_DyanmicFormSectionFieldDetailsDAO;
import utilities.data.applicants.dynamics.ESP_LIB_DynamicFormSectionFieldDAO;
import utilities.data.applicants.dynamics.ESP_LIB_DynamicStagesCriteriaListDAO;
import utilities.interfaces.ESP_LIB_CriteriaFieldsListener;

public class ESP_LIB_AttachmentItem {


    private String TAG = getClass().getSimpleName();
    private boolean isViewOnly;
    private static ESP_LIB_AttachmentItem attachmentItem = null;
    private ESP_LIB_ApplicationFieldsRecyclerAdapter.ApplicationFieldsAdapterListener mApplicationFieldsAdapterListener;
    private ESP_LIB_CriteriaFieldsListener ESPLIBCriteriaFieldsListener;
    private ESP_LIB_DynamicStagesCriteriaListDAO criteriaListDAO;
    private ESP_LIB_ApplicationFieldsRecyclerAdapter applicationFieldsRecyclerAdapter;
    private ESP_LIB_ListofSectionsFieldsAdapter listofSectionsFieldsAdapter;
    private ESP_LIB_EditSectionDetails edisectionDetailslistener;

    public static ESP_LIB_AttachmentItem getInstance() {
        if (attachmentItem == null)
            return attachmentItem = new ESP_LIB_AttachmentItem();
        else
            return attachmentItem;
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

    public void getAdapter(ESP_LIB_ListofSectionsFieldsAdapter listofsectionsFieldsAdapter) {
        listofSectionsFieldsAdapter = listofsectionsFieldsAdapter;
    }


    public void getAdapter(ESP_LIB_EditSectionDetails edisectionDetails) {
        edisectionDetailslistener = edisectionDetails;
    }


    public void showAttachmentTypeItemView(final ESP_LIB_AttachmentTypeViewHolder holder, final int position,
                                           ESP_LIB_DynamicFormSectionFieldDAO fieldDAO, boolean isviewOnly,
                                           Context mContext, ESP_LIB_DynamicStagesCriteriaListDAO ESPLIBDynamicStagesCriteriaListDAO,
                                           boolean iscalculatedMappedField) {
        isViewOnly = isviewOnly;

        if (fieldDAO.isReadOnly()) {
            isViewOnly = true;
          //  holder.rlattachmentdetails.setBackgroundResource(R.drawable.esp_lib_drawable_draw_bg_disable_fields_with_stroke);
        }

        ESP_LIB_SharedPreference pref = new ESP_LIB_SharedPreference(mContext);
        try {


            if (pref.getLanguage().equalsIgnoreCase("ar")) {
                if (isViewOnly)
                    holder.tValueLabel.setGravity(Gravity.RIGHT);

            } else {

                if (isViewOnly)
                    holder.tValueLabel.setGravity(Gravity.LEFT);

            }

            holder.progressbar.setVisibility(View.GONE);


            //Setting Label
            String label = fieldDAO.getLabel();

            if (isViewOnly || fieldDAO.isMappedCalculatedField()) {
                if (ESP_LIB_ListUsersApplicationsAdapter.Companion.isSubApplications())
                    label = fieldDAO.getLabel() + ":";
                holder.tValueLabel.setText(label);
                holder.ivdots.setVisibility(View.INVISIBLE);
                holder.llattachment.setVisibility(View.GONE);
            } else {
                holder.ivdots.setVisibility(View.VISIBLE);
                holder.llattachment.setVisibility(View.VISIBLE);
                if (fieldDAO.isRequired()) {
                    label += " *";
                }

                holder.tValueLabel.setText(label);
            }


            //Setting pre-filled Value. If Have
            String uploadedFileName = "";
            ESP_LIB_DyanmicFormSectionFieldDetailsDAO getdetails = fieldDAO.getDetails();
            fieldDAO.setDetails(getdetails);

            if (fieldDAO.getDetails() != null && fieldDAO.getDetails().getName() != null &&
                    !TextUtils.isEmpty(fieldDAO.getDetails().getName())) {
                uploadedFileName = fieldDAO.getDetails().getName();
            }

            if (!TextUtils.isEmpty(uploadedFileName)) {
                holder.rlattachmentdetails.setVisibility(View.VISIBLE);
                holder.llattachment.setVisibility(View.GONE);

                String extension = uploadedFileName.substring(uploadedFileName.lastIndexOf("."));

                String fileSize = extension.replaceFirst(".", "");
                if (fieldDAO.getDetails().getFileSize() != null)
                    fileSize = extension.replaceFirst(".", "").toUpperCase() + ", " + fieldDAO.getDetails().getFileSize();
                holder.txtextensionsize.setText(fileSize);


                setIconBasedOnMimeType(extension, holder.attachtypeicon);


                if (isViewOnly || fieldDAO.isMappedCalculatedField())
                    holder.txtacctehmentname.setText(uploadedFileName);
                else {
                    holder.txtacctehmentname.setText(uploadedFileName);
                    fieldDAO.setValidate(true);
                    validateForm(fieldDAO);
                }
            } else {
                holder.rlattachmentdetails.setVisibility(View.GONE);
                if (!isViewOnly || !fieldDAO.isMappedCalculatedField()) {
                    //Handling Required Condition
                    holder.llattachment.setVisibility(View.VISIBLE);

                    if (fieldDAO.isRequired())
                        fieldDAO.setValidate(false);
                    else
                        fieldDAO.setValidate(true);

                    validateForm(fieldDAO);
                }
            }
            //

            if (isViewOnly || fieldDAO.isMappedCalculatedField()) {
                holder.llattachment.setVisibility(View.GONE);
                if (getdetails != null)
                    ESP_LIB_AttachmentImageDownload.getInstance().downloadImage(holder, getdetails, uploadedFileName, mContext,
                            applicationFieldsRecyclerAdapter, listofSectionsFieldsAdapter, position);

            }

            if (!isViewOnly) {
                if (!fieldDAO.isReadOnly()) {


                    // String finalUploadedFileName1 = uploadedFileName;
                    holder.llattachment.setOnClickListener(v -> {
                        if (!ESP_LIB_CommonMethodsKotlin.Companion.checkPermission(mContext)) {
                            ESP_LIB_CommonMethodsKotlin.Companion.requestPermission(mContext);
                        } else {
                            if (mApplicationFieldsAdapterListener != null) {
                                mApplicationFieldsAdapterListener.onAttachmentFieldClicked(fieldDAO, position);
                            } else if (edisectionDetailslistener != null) {
                                edisectionDetailslistener.onAttachmentFieldClicked(fieldDAO, position);
                            }
                        }


                    });

                    String finalUploadedFileName = uploadedFileName;
                    holder.rlattachmentdetails.setOnClickListener(v -> {
                        if (!ESP_LIB_CommonMethodsKotlin.Companion.checkPermission(mContext)) {
                            ESP_LIB_CommonMethodsKotlin.Companion.requestPermission(mContext);
                        } else {

                            if (getdetails != null)
                                ESP_LIB_AttachmentImageDownload.getInstance().downloadImage(holder, getdetails, finalUploadedFileName,
                                        mContext, applicationFieldsRecyclerAdapter, listofSectionsFieldsAdapter, position);
                        }
                    });


                    holder.ivdots.setOnClickListener(v -> {
                        showMenu(v, position, mContext, fieldDAO);
                    });


                }
            }
            if (ESPLIBDynamicStagesCriteriaListDAO != null &&
                    (!ESPLIBDynamicStagesCriteriaListDAO.isOwner() && ESPLIBDynamicStagesCriteriaListDAO.getAssessmentStatus().equalsIgnoreCase(mContext.getString(R.string.esp_lib_text_active)))) {
                holder.llattachment.setEnabled(false);
                holder.rlattachmentdetails.setEnabled(false);

            }

        } catch (Exception e) {
            //  e.printStackTrace();

        }

    }

    public void setIconBasedOnMimeType(String extension, ImageView imageView) {

        if (extension.equalsIgnoreCase(".docx") || extension.equalsIgnoreCase(".doc"))
            imageView.setImageResource(R.drawable.esp_lib_drawable_ic_icons_documents_docx_green);

        else if (extension.equalsIgnoreCase(".pptx") || extension.equalsIgnoreCase(".ppt"))
            imageView.setImageResource(R.drawable.esp_lib_drawable_ic_icons_documents_pptx_green);

        else if (extension.equalsIgnoreCase(".xls") || extension.equalsIgnoreCase(".xlsx"))
            imageView.setImageResource(R.drawable.esp_lib_drawable_ic_icons_documents_xls_green);

        else if (extension.equalsIgnoreCase(".pdf"))
            imageView.setImageResource(R.drawable.esp_lib_drawable_ic_icons_documents_pdf_green);

        else {
            String mimeTypeFromExtension = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.replaceFirst(".", ""));
            if (mimeTypeFromExtension != null ? mimeTypeFromExtension.contains("image") : false)
                imageView.setImageResource(R.drawable.esp_lib_drawable_ic_icons_documents_img_green);
            else
                imageView.setImageResource(R.drawable.esp_lib_drawable_ic_icons_documents_unknown_green);
        }

    }

    private void showMenu(View v, int position, Context mContext, ESP_LIB_DynamicFormSectionFieldDAO fieldDAO) {


        PopupMenu popup = new PopupMenu(mContext, v);
        popup.inflate(R.menu.menu_list_item_add_application_fields);
        popup.setGravity(Gravity.CENTER);
       // Menu menuOpts = popup.getMenu();

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                if (menuItem.getItemId() == R.id.action_remove) {
                    fieldDAO.setDetails(null);
                    if (applicationFieldsRecyclerAdapter != null)
                        applicationFieldsRecyclerAdapter.notifyItemChanged(position);
                    else if (listofSectionsFieldsAdapter != null)
                        listofSectionsFieldsAdapter.notifyItemChanged(position);

                }
                return false;
            }
        });


        popup.show();

    }

    private void validateForm(ESP_LIB_DynamicFormSectionFieldDAO fieldDAO) {
        ESP_LIB_Validation validation = new ESP_LIB_Validation(mApplicationFieldsAdapterListener, ESPLIBCriteriaFieldsListener,
                criteriaListDAO, fieldDAO);
        validation.setSectionListener(edisectionDetailslistener);
        validation.validateForm();
    }


}
