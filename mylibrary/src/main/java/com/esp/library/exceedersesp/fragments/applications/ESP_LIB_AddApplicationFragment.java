package com.esp.library.exceedersesp.fragments.applications;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.esp.library.R;
import com.esp.library.exceedersesp.ESP_LIB_BaseActivity;
import com.esp.library.exceedersesp.ESP_LIB_ESPApplication;
import com.esp.library.exceedersesp.controllers.applications.ESP_LIB_ChooseLookUpOption;
import com.esp.library.ipaulpro.afilechooser.utils.FileUtils;
import com.esp.library.utilities.common.ESP_LIB_CustomLogs;
import com.esp.library.utilities.common.ESP_LIB_KeyboardUtils;
import com.esp.library.utilities.common.ESP_LIB_ProgressBarAnimation;
import com.esp.library.utilities.common.ESP_LIB_RealPathUtil;
import com.esp.library.utilities.common.ESP_LIB_Shared;
import com.esp.library.utilities.common.ESP_LIB_SharedPreference;
import com.esp.library.utilities.customevents.EventOptions;
import com.esp.library.utilities.setup.applications.ESP_LIB_ApplicationFieldsRecyclerAdapter;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import utilities.adapters.setup.applications.ESP_LIB_ListAddApplicationSectionsAdapter;
import utilities.data.apis.ESP_LIB_APIs;
import utilities.data.applicants.ESP_LIB_CalculatedMappedFieldsDAO;
import utilities.data.applicants.addapplication.ESP_LIB_DefinationsDAO;
import utilities.data.applicants.addapplication.ESP_LIB_CurrencyDAO;
import utilities.data.applicants.addapplication.ESP_LIB_LookUpDAO;
import utilities.data.applicants.addapplication.ESP_LIB_ResponseFileUploadDAO;
import utilities.data.applicants.dynamics.ESP_LIB_DyanmicFormSectionFieldDetailsDAO;
import utilities.data.applicants.dynamics.ESP_LIB_DynamicFormDAO;
import utilities.data.applicants.dynamics.ESP_LIB_DynamicFormSectionDAO;
import utilities.data.applicants.dynamics.ESP_LIB_DynamicFormSectionFieldDAO;
import utilities.data.applicants.dynamics.ESP_LIB_DynamicFormSectionFieldLookupValuesDAO;
import utilities.data.applicants.dynamics.ESP_LIB_DynamicFormSectionFieldsCardsDAO;
import utilities.data.applicants.dynamics.ESP_LIB_DynamicFormValuesDAO;
import utilities.data.applicants.dynamics.ESP_LIB_DynamicResponseDAO;
import utilities.data.applicants.dynamics.ESP_LIB_DynamicSectionValuesDAO;

import static com.esp.library.utilities.setup.applications.ESP_LIB_ApplicationFieldsRecyclerAdapter.SECTIONCONSTANT;


public class ESP_LIB_AddApplicationFragment extends Fragment implements
        ESP_LIB_ApplicationFieldsRecyclerAdapter.ApplicationFieldsAdapterListener {

    String TAG = getClass().getSimpleName();

    ESP_LIB_BaseActivity bContext;

    boolean isServiceRunning;
    public ESP_LIB_ListAddApplicationSectionsAdapter mApplicationSectionsAdapter;

    Retrofit retrofit = null;
    Call<ESP_LIB_DynamicResponseDAO> detail_call = null;
    Call<ESP_LIB_ResponseFileUploadDAO> call_upload = null;
    Call<Integer> submit_call = null;
    ESP_LIB_DynamicResponseDAO actual_response = null;
    String actualResponseJson = null;
    InputMethodManager imm = null;
    ESP_LIB_ProgressBarAnimation anim = null;
    ESP_LIB_DefinationsDAO definationsDAO;
    AlertDialog dialog = null;

    private static Button btnSubmit;
    private static final int REQUEST_CHOOSER = 12345;
    private static final int REQUEST_LOOKUP = 2;
    ESP_LIB_DynamicFormSectionFieldDAO fieldToBeUpdated = null;
    ESP_LIB_SharedPreference pref;
    boolean isNotified;
    public static boolean isCalculatedField = false;
    private boolean isKeyboardVisible = false;
    private static TextView definitionDescription;
    private static TextView definitionNameTitle;

    LinearLayout no_application_available_div;
    RecyclerView app_list;
    TextView category_name;
    TextView txtcategory;

    ESP_LIB_DynamicFormSectionFieldDAO ESPLIBDynamicFormSectionFieldDAOCalculatedMapped = null;
    android.app.AlertDialog pDialog;
    RecyclerView.LayoutManager mApplicationLayoutManager;

    public ESP_LIB_AddApplicationFragment() {
        // Required empty public constructor
    }

    public static ESP_LIB_AddApplicationFragment newInstance(ESP_LIB_DefinationsDAO cat, Button btn_Submit,
                                                             TextView definitiondescription, TextView definitionnameTitle) {
        ESP_LIB_AddApplicationFragment fragment = new ESP_LIB_AddApplicationFragment();
        Bundle args = new Bundle();
        args.putSerializable(ESP_LIB_DefinationsDAO.Companion.getBUNDLE_KEY(), cat);
        fragment.setArguments(args);

        btnSubmit = btn_Submit;
        definitionDescription = definitiondescription;
        definitionNameTitle = definitionnameTitle;

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (getArguments() != null) {
            definationsDAO = (ESP_LIB_DefinationsDAO) getArguments().getSerializable(ESP_LIB_DefinationsDAO.Companion.getBUNDLE_KEY());

        }


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.esp_lib_fragment_add_application, container, false);
        initailize(v);
        setGravity();


        if (definationsDAO != null) {
            category_name.setText(definationsDAO.getCategory());
        }
        if (ESP_LIB_Shared.getInstance().isWifiConnected(bContext)) {
            LoadStages();
        } else {
            ESP_LIB_Shared.getInstance().showAlertMessage(bContext.getString(R.string.esp_lib_text_internet_error_heading), bContext.getString(R.string.esp_lib_text_internet_connection_error), bContext);
        }

        ESP_LIB_KeyboardUtils.addKeyboardToggleListener(bContext, isVisible -> isKeyboardVisible = isVisible);

     /*   KeyboardUtils.addKeyboardToggleListener(this,
                object : KeyboardUtils.SoftKeyboardToggleListener {
            override fun onToggleSoftKeyboard(isVisible: Boolean) {
                submit_request?.refreshAdapter(isVisible)
            }
        })*/

        return v;
    }

    private void initailize(View v) {
        bContext = (ESP_LIB_BaseActivity) getActivity();
        pDialog = ESP_LIB_Shared.getInstance().setProgressDialog(bContext);
        pref = new ESP_LIB_SharedPreference(bContext);

        no_application_available_div = v.findViewById(R.id.no_application_available_div);
        app_list = v.findViewById(R.id.app_list);
        category_name = v.findViewById(R.id.category_name);
        txtcategory = v.findViewById(R.id.txtcategory);

        mApplicationLayoutManager = new LinearLayoutManager(getActivity());
        app_list.setHasFixedSize(true);
        app_list.setLayoutManager(mApplicationLayoutManager);
        app_list.setItemAnimator(new DefaultItemAnimator());

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }


    private void LoadStages() {
        start_loading_animation();

        try {

            ESP_LIB_APIs apis = ESP_LIB_Shared.getInstance().retroFitObject(bContext);

            Call<List<ESP_LIB_CurrencyDAO>> call = apis.getCurrency();

            call.enqueue(new Callback<List<ESP_LIB_CurrencyDAO>>() {
                @Override
                public void onResponse(Call<List<ESP_LIB_CurrencyDAO>> call, Response<List<ESP_LIB_CurrencyDAO>> response) {

                    if (response.body() != null && response.body().size() > 0) {
                        ESP_LIB_ESPApplication.getInstance().setCurrencies(response.body());
                    }
                    if (ESP_LIB_Shared.getInstance().isWifiConnected(bContext)) {
                        GetApplicationFrom(definationsDAO);
                    } else {
                        ESP_LIB_Shared.getInstance().showAlertMessage(bContext.getString(R.string.esp_lib_text_internet_error_heading), bContext.getString(R.string.esp_lib_text_internet_connection_error), bContext);
                    }
                }

                @Override
                public void onFailure(Call<List<ESP_LIB_CurrencyDAO>> call, Throwable t) {
                    stop_loading_animation();
                }
            });

        } catch (Exception ex) {
            ex.printStackTrace();
            stop_loading_animation();
        }
    }//LoggedInUser end

    private void GetApplicationFrom(ESP_LIB_DefinationsDAO ESPLIBCategoryAndDefinationsDAO) {


        try {


            final ESP_LIB_APIs apis = ESP_LIB_Shared.getInstance().retroFitObject(bContext);
            int parentAppId = 0;
            if (ESPLIBCategoryAndDefinationsDAO.getParentApplicationInfo() != null)
                parentAppId = ESPLIBCategoryAndDefinationsDAO.getParentApplicationInfo().getApplicationId();
            else
                parentAppId = ESPLIBCategoryAndDefinationsDAO.getParentApplicationId();

            if (parentAppId > 0)
                detail_call = apis.getSubDefincationForm(ESPLIBCategoryAndDefinationsDAO.getId(), parentAppId);
            else
                detail_call = apis.AllDefincationForm(ESPLIBCategoryAndDefinationsDAO.getId());
            detail_call.enqueue(new Callback<ESP_LIB_DynamicResponseDAO>() {
                @Override
                public void onResponse(Call<ESP_LIB_DynamicResponseDAO> call, Response<ESP_LIB_DynamicResponseDAO> response) {

                    stop_loading_animation();

                    if (response != null && response.body() != null) {

                        actual_response = response.body();
                        actualResponseJson = actual_response.toJson();
                        ArrayList<ESP_LIB_DynamicFormSectionDAO> sections;
                        List<ESP_LIB_DynamicSectionValuesDAO> sectionsValues = actual_response.getSectionValues();

                        if (definitionNameTitle.getText().toString().isEmpty()) {
                            definitionNameTitle.setText(actual_response.getName());
                            definitionDescription.setText(actual_response.getDescription());
                        }
                        if (sectionsValues != null)
                            sections = GetFieldsCards(actual_response.getForm(), sectionsValues);
                        else
                            sections = GetOnlyFieldsCards(response.body().getForm(), null);

                        if (sections != null && sections.size() > 0) {
                            mApplicationSectionsAdapter = new ESP_LIB_ListAddApplicationSectionsAdapter(sections, bContext, "", false, false);
                            mApplicationSectionsAdapter.setActualResponseJson(actualResponseJson);
                            app_list.setAdapter(mApplicationSectionsAdapter);
                            mApplicationSectionsAdapter.notifyDataSetChanged();
                            mApplicationSectionsAdapter.setmApplicationFieldsAdapterListener(ESP_LIB_AddApplicationFragment.this);
                            SuccessResponse();
                        } else {

                            SubmitRequest(getString(R.string.esp_lib_text_submit));

                            //   UnSuccessResponse();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ESP_LIB_DynamicResponseDAO> call, Throwable t) {
                    stop_loading_animation();
                    ESP_LIB_Shared.getInstance().showAlertMessage(pref.getlabels().getApplication(), getString(R.string.esp_lib_text_some_thing_went_wrong), bContext);
                    return;
                }
            });

        } catch (Exception ex) {
            ex.printStackTrace();
            stop_loading_animation();
            ESP_LIB_Shared.getInstance().showAlertMessage(pref.getlabels().getApplication(), getString(R.string.esp_lib_text_some_thing_went_wrong), bContext);


        }
    }


    @Override
    public void onDestroyView() {
        if (detail_call != null) {
            detail_call.cancel();
        }

        super.onDestroyView();
    }


    private void start_loading_animation() {
        try {
            if (pDialog != null && !pDialog.isShowing())
                pDialog.show();
        } catch (Exception e) {

        }
    }

    private void stop_loading_animation() {

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    if (pDialog.isShowing())
                        pDialog.dismiss();
                } catch (Exception e) {

                }
            }
        }, 1000);


    }

    private void SuccessResponse() {
        app_list.setVisibility(View.VISIBLE);
        no_application_available_div.setVisibility(View.GONE);

    }

    private void UnSuccessResponse() {
        app_list.setVisibility(View.GONE);
        no_application_available_div.setVisibility(View.GONE);
    }


    private ArrayList<ESP_LIB_DynamicFormSectionDAO> GetFieldsCards(ESP_LIB_DynamicFormDAO response, List<ESP_LIB_DynamicSectionValuesDAO> sectionsValues) {
        ArrayList<ESP_LIB_DynamicFormSectionDAO> sections = new ArrayList<>();

        if (response.getSections() != null) {
            for (int i = 0; i < response.getSections().size(); i++) {
                ESP_LIB_DynamicFormSectionDAO sectionDAO = response.getSections().get(i);
                int sectionId = sectionDAO.getId();


                for (int j = 0; j < sectionsValues.size(); j++) {

                    int sectionValuesId = sectionsValues.get(j).getId();

                    if (sectionId == sectionValuesId) {

                        List<ESP_LIB_DynamicSectionValuesDAO.Instance> instances = sectionsValues.get(j).getInstances();

                        List<ESP_LIB_DynamicFormSectionFieldsCardsDAO> cardsList = new ArrayList<>();

                        for (int k = 0; k < (instances != null ? instances.size() : 0); k++) {

                            List<ESP_LIB_DynamicSectionValuesDAO.Instance.Value> sectionValuesAsFields = instances.get(k).getValues();
                            List<ESP_LIB_DynamicFormSectionFieldDAO> finalFields = new ArrayList<>();


                            for (int l = 0; l < (sectionValuesAsFields != null ? sectionValuesAsFields.size() : 0); l++) {

                                ESP_LIB_DynamicSectionValuesDAO.Instance.Value instanceValue = sectionValuesAsFields.get(l);

                                List<ESP_LIB_DynamicFormSectionFieldDAO> fields = sectionDAO.getFields();
                                if (sectionDAO.getFields() != null) {
                                    for (int m = 0; m < sectionDAO.getFields().size(); m++) {

                                        if (fields != null) {
                                            ESP_LIB_DynamicFormSectionFieldDAO parentSectionField = fields.get(m);

                                            if (parentSectionField.isVisible()) {
                                                ESP_LIB_DynamicFormSectionFieldDAO tempField = ESP_LIB_Shared.getInstance().setObjectValues(parentSectionField);

                                                if (tempField.getSectionCustomFieldId() == instanceValue.getSectionCustomFieldId()) {
                                                    String value = instanceValue.getValue();
                                                    if (instanceValue.getType() == 13) // lookupvalue
                                                    {
                                                        value = instanceValue.getSelectedLookupText();
                                                        if (value == null)
                                                            value = instanceValue.getValue();
                                                    }
                                                    if (instanceValue.getType() == 11 && value != null && value.isEmpty())
                                                        value = tempField.getValue();

                                                    tempField.setValue(value);
                                                    if (tempField.getType() == 7) { // for attachments only
                                                        try {
                                                            getAttachmentsDetail(tempField, instanceValue);
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }
                                                    }

                                                    if (tempField.getType() == 18 || tempField.getType() == 19) // calculated and mapped
                                                    {
                                                        if (instanceValue.getType() == 7) {
                                                            tempField.setType(instanceValue.getType());
                                                            getAttachmentsDetail(tempField, instanceValue);
                                                        } else if (instanceValue.getType() == 11)
                                                            tempField.setType(instanceValue.getType());
                                                        else if (instanceValue.getType() == 4)
                                                            tempField.setType(instanceValue.getType());
                                                    }

                                                    finalFields.add(tempField);
                                                    // Latest Field will be add here
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            cardsList.add(new ESP_LIB_DynamicFormSectionFieldsCardsDAO(finalFields));
                        }

                        sectionDAO.setRefreshFieldsCardsList(cardsList);
                        sections.add(sectionDAO);

                    }

                }
                // sections = Shared.getInstance().removeInvisbleFields(sectionDAO, sections);
            }
        }

        ArrayList<ESP_LIB_DynamicFormSectionDAO> ESPLIBDynamicFormSectionDAOS = GetOnlyFieldsCards(response, sectionsValues);
        sections.clear();
        sections.addAll(ESPLIBDynamicFormSectionDAOS);
        return sections;
    }

    private void getAttachmentsDetail(ESP_LIB_DynamicFormSectionFieldDAO tempField, ESP_LIB_DynamicSectionValuesDAO.Instance.Value instanceValue) {
        try {
            String attachmentFileSize = "";
            String getOutputMediaFile = ESP_LIB_Shared.getInstance().getOutputMediaFile(Objects.requireNonNull(instanceValue.getDetails()).getName()).getPath();
            boolean isFileExist = ESP_LIB_Shared.getInstance().isFileExist(getOutputMediaFile, bContext);
            if (isFileExist) {
                File file = null;
                // String path = RealPathUtil.getPath(bContext, Uri.parse(getOutputMediaFile));
                file = new File(getOutputMediaFile);
                attachmentFileSize = ESP_LIB_Shared.getInstance().getAttachmentFileSize(file);
            }


            ESP_LIB_DyanmicFormSectionFieldDetailsDAO details = new ESP_LIB_DyanmicFormSectionFieldDetailsDAO();
            details.setDownloadUrl(Objects.requireNonNull(instanceValue.getDetails()).getDownloadUrl());
            details.setMimeType(instanceValue.getDetails().getMimeType());
            details.setCreatedOn(instanceValue.getDetails().getCreatedOn());
            details.setName(instanceValue.getDetails().getName());
            details.setFileSize(attachmentFileSize);
            tempField.setDetails(details);
        } catch (Exception e) {
            //  e.printStackTrace();
        }
    }

    private ArrayList<ESP_LIB_DynamicFormSectionDAO> GetOnlyFieldsCards(ESP_LIB_DynamicFormDAO response, List<ESP_LIB_DynamicSectionValuesDAO> sectionsValues) {

        ArrayList<ESP_LIB_DynamicFormSectionDAO> sections = new ArrayList<>();

        if (response.getSections() != null) {
            //Setting Sections With FieldsCards.
            for (ESP_LIB_DynamicFormSectionDAO sectionDAO : response.getSections()) {

                if (sectionDAO.getFields() != null && sectionDAO.getFields().size() > 0) {
                    List<ESP_LIB_DynamicFormSectionFieldDAO> fields;
                    if (sectionsValues != null)
                        fields = parentFieldList(sectionDAO, true);
                    else
                        fields = ESP_LIB_Shared.getInstance().invisibleList(sectionDAO, true);
                    ESP_LIB_DynamicFormSectionFieldsCardsDAO fieldsCard = new ESP_LIB_DynamicFormSectionFieldsCardsDAO(fields);
                    sectionDAO.getFieldsCardsList().add(fieldsCard);
                    sections.add(sectionDAO);
                }

            }
        }
        return sections;
    }

    private List<ESP_LIB_DynamicFormSectionFieldDAO> parentFieldList(ESP_LIB_DynamicFormSectionDAO sectionDAO, boolean isClearMappedCalculatedFields) {
        List<ESP_LIB_DynamicFormSectionFieldDAO> fields = sectionDAO.getFields();
        List<ESP_LIB_DynamicFormSectionFieldDAO> tempFields = new ArrayList<>();
        for (int h = 0; h < (fields != null ? fields.size() : 0); h++) {
            if (fields.get(h).isVisible() && !fields.get(h).isReadOnly()) {
                if (isClearMappedCalculatedFields && (fields.get(h).getType() == 18 || fields.get(h).getType() == 19))
                    fields.get(h).setValue("");

                tempFields.add(fields.get(h));
            }
        }
        return tempFields;
    }

    public void SingleSelection(final ESP_LIB_DynamicFormSectionFieldDAO single) {


        if (single != null) {

            String[] values = null;
            if (single.getLookupValues() != null && single.getLookupValues().size() > 0) {
                values = new String[single.getLookupValues().size()];
                int i = 0;
                for (ESP_LIB_DynamicFormSectionFieldLookupValuesDAO lookup : single.getLookupValues()) {

                    /*if (lookup.isSelected()) {
                    }*/
                    values[i] = lookup.getLabel();

                    i++;
                }
            }


            if (values != null && values.length > 0) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(bContext);
                builder.setTitle(single.getLabel());

                final String[] finalValues = values;

                builder.setItems(finalValues, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int i) {


                        if (single.getLookupValues() != null && single.getLookupValues().size() > 0) {

                            for (ESP_LIB_DynamicFormSectionFieldLookupValuesDAO lookup : single.getLookupValues()) {

                                if (lookup.getLabel().toLowerCase().equals(finalValues[i].toLowerCase())) {
                                    lookup.setSelected(true);
                                    ESP_LIB_DynamicFormValuesDAO post = new ESP_LIB_DynamicFormValuesDAO();
                                    post.setSectionCustomFieldId(single.getSectionCustomFieldId());
                                    post.setSectionId(single.getObjectId());
                                    post.setValue(lookup.getId() + "");

                                    if (single.getPost() != null) {
                                        single.setPost(null);
                                    }
                                    single.setPost(post);

                                    single.setError_field(null);


                                } else {
                                    lookup.setSelected(false);
                                }
                            }
                        }

                        single.setViewGenerated(false);

                        if (mApplicationSectionsAdapter != null) {
                            mApplicationSectionsAdapter.notifyDataSetChanged();
                        }

                        if (dialog != null) {
                            dialog.dismiss();
                        }


                    }
                });
                // AlertDialog alert = builder.create();


                dialog = builder.create();

                dialog.show();
            }


        }

        /**/
    }


    @Override
    public void onFieldValuesChanged() {

        //Check the formValidation

        List<ESP_LIB_DynamicFormSectionFieldDAO> adapter_list = null;

        if (mApplicationSectionsAdapter != null) {
            adapter_list = mApplicationSectionsAdapter.GetAllFields();
        }


        if (adapter_list != null && adapter_list.size() > 0) {

            boolean isAllFieldsValidateTrue = true;

            for (ESP_LIB_DynamicFormSectionFieldDAO ESPLIBDynamicFormSectionFieldDAO : adapter_list) {
                if (ESPLIBDynamicFormSectionFieldDAO.getSectionType() != SECTIONCONSTANT) {


                    String error = "";
                    error = ESP_LIB_Shared.getInstance().edittextErrorChecks(bContext, ESPLIBDynamicFormSectionFieldDAO.getValue(), error, ESPLIBDynamicFormSectionFieldDAO);
                    if (error.length() > 0 && (ESPLIBDynamicFormSectionFieldDAO.getType() != 18 && ESPLIBDynamicFormSectionFieldDAO.getType() != 19)) {
                        isAllFieldsValidateTrue = false;
                        break;
                    }


                    if (ESPLIBDynamicFormSectionFieldDAO.isRequired()) {
                        if (!ESPLIBDynamicFormSectionFieldDAO.isValidate()) {
                            isAllFieldsValidateTrue = false;
                            break;
                        }
                    }
                }
            }

            if (isAllFieldsValidateTrue) {
                btnSubmit.setEnabled(true);
                btnSubmit.setAlpha(1);
                btnSubmit.setBackgroundResource(R.drawable.esp_lib_drawable_draw_bg_green);
            } else {
                btnSubmit.setEnabled(false);
                btnSubmit.setAlpha(0.5f);
                btnSubmit.setBackgroundResource(R.drawable.esp_lib_drawable_draw_bg_grey_disable_button);
            }
        }


    }

    @Override
    public void onAttachmentFieldClicked(ESP_LIB_DynamicFormSectionFieldDAO fieldDAO, int position) {

        fieldToBeUpdated = fieldDAO;
        fieldToBeUpdated.setUpdatePositionAttachment(position);


        String getAllowedValuesCriteria = fieldToBeUpdated.getAllowedValuesCriteria();


        getAllowedValuesCriteria = getAllowedValuesCriteria.replaceAll("\\.", "");

        // getAllowedValuesCriteria = "image/" + getAllowedValuesCriteria;

        String[] values = getAllowedValuesCriteria.split(",");

        List<String> valuesList = new ArrayList<String>(Arrays.asList(values));
        ArrayList<String> refineValuesList = new ArrayList<>();
        for (int j = 0; j < valuesList.size(); j++) {
            if (!valuesList.get(j).equals("-2"))
                refineValuesList.add(valuesList.get(j));
        }

        String[] mimeTypes = new String[refineValuesList.size()];
        for (int i = 0; i < refineValuesList.size(); i++) {
            String type = refineValuesList.get(i);
            String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(type.toLowerCase());
            if (mimeType != null)
                mimeTypes[i] = mimeType;
            else
                mimeTypes[i] = type;
        }

        ESP_LIB_CustomLogs.displayLogs(TAG + " getAllowedValuesCriteria: " + getAllowedValuesCriteria + " mimeTypes: " + Arrays.toString(mimeTypes));

        // Intent getContentIntent = FileUtils.createGetContentIntent();

        final Intent getContentIntent = new Intent(Intent.ACTION_GET_CONTENT);
        // The MIME data type filter

        getContentIntent.setType("*/*");

        if (getAllowedValuesCriteria.length() > 0)
            getContentIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        // Only return URIs that can be opened with ContentResolver
        getContentIntent.addCategory(Intent.CATEGORY_OPENABLE);

        Intent intent = Intent.createChooser(getContentIntent, bContext.getString(R.string.esp_lib_text_selectafile));
        startActivityForResult(intent, REQUEST_CHOOSER);

    }

    @Override
    public void onLookupFieldClicked(ESP_LIB_DynamicFormSectionFieldDAO fieldDAO, int position, boolean isCalculatedMappedField) {

        try {

            if (!pDialog.isShowing()) {
                fieldToBeUpdated = fieldDAO;
                fieldToBeUpdated.setUpdatePositionAttachment(position);

                Bundle bundle = new Bundle();
                bundle.putSerializable(ESP_LIB_DynamicFormSectionFieldDAO.Companion.getBUNDLE_KEY(), fieldToBeUpdated);
                bundle.putBoolean("isCalculatedMappedField", isCalculatedMappedField);

                Intent chooseLookupOption = new Intent(bContext, ESP_LIB_ChooseLookUpOption.class);
                chooseLookupOption.putExtras(bundle);
                startActivityForResult(chooseLookupOption, REQUEST_LOOKUP);

            }
        } catch (Exception e) {
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == REQUEST_CHOOSER && data != null) {

                final Uri uri = data.getData();

                if (uri != null) {
                    int getMaxVal = fieldToBeUpdated.getMaxVal();
                    boolean isFileSizeValid = true;
                    if (getMaxVal > 0)
                        isFileSizeValid = getFileSize(ESP_LIB_RealPathUtil.getPath(bContext, uri), getMaxVal);

                    ESP_LIB_CustomLogs.displayLogs(TAG + " getMaxVal: " + getMaxVal + " isFileSizeValid: " +
                            isFileSizeValid + " getRealPathFromURI: " + ESP_LIB_RealPathUtil.getPath(bContext, uri));

                    if (isFileSizeValid) {

                        try {
                            UpdateLoadImageForField(fieldToBeUpdated, uri);

                        } catch (Exception e) {
                            ESP_LIB_Shared.getInstance().messageBox(getString(R.string.esp_lib_text_pleasetryagain), bContext);
                        }
                    } else {

                        ESP_LIB_Shared.getInstance().showAlertMessage("", getString(R.string.esp_lib_text_sizeshouldbelessthen) + " " + getMaxVal + " " + getString(R.string.esp_lib_text_mb), bContext);
                    }
                }

            } else if (requestCode == REQUEST_LOOKUP && data != null) {

                ESP_LIB_DynamicFormSectionFieldDAO dfs = (ESP_LIB_DynamicFormSectionFieldDAO) data.getExtras().getSerializable(ESP_LIB_DynamicFormSectionFieldDAO.Companion.getBUNDLE_KEY());
                ESP_LIB_LookUpDAO lookup = (ESP_LIB_LookUpDAO) data.getExtras().getSerializable(ESP_LIB_LookUpDAO.Companion.getBUNDLE_KEY());
                boolean isCalculatedMappedField = data.getExtras().getBoolean("isCalculatedMappedField");
                if (fieldToBeUpdated != null && lookup != null) {
                    SetUpLookUpValues(fieldToBeUpdated, lookup, isCalculatedMappedField);
                }

            }

        }

    }


    private boolean getFileSize(String path, int getMaxVal) {
        File file = new File(path);
        double bytes = file.length();
        double kilobytes = (bytes / 1024);
        double megabytes = (kilobytes / 1024);


        return getMaxVal >= megabytes;

    }

    public void SubmitRequest(String whatodo) {


        //For new API version /submitv2
        ESP_LIB_DynamicResponseDAO submit_jsonNew = new Gson().fromJson(actualResponseJson, ESP_LIB_DynamicResponseDAO.class);//Shared.getInstance().CloneAddFormWithForm(actual_response);

        if (submit_jsonNew != null) {

            List<ESP_LIB_DynamicSectionValuesDAO> sectionValuesListToPost = new ArrayList<>();
           /* if (mApplicationSectionsAdapter == null) {
                Shared.getInstance().showAlertMessage(getString(R.string.error), getString(R.string.formisempty), bContext);
                return;
            }*/

            if (mApplicationSectionsAdapter != null && mApplicationSectionsAdapter.getmApplications() != null) {
                for (int s = 0; s < mApplicationSectionsAdapter.getmApplications().size(); s++) {
                    ESP_LIB_DynamicFormSectionDAO updatedSection = mApplicationSectionsAdapter.getmApplications().get(s);

                    //for (DynamicFormSectionDAO updatedSection : mApplicationSectionsAdapter.getmApplications()) {

                    if (updatedSection != null && updatedSection.getFieldsCardsList().size() > 0) {

                        ESP_LIB_DynamicSectionValuesDAO sectionValuesDAO = new ESP_LIB_DynamicSectionValuesDAO();

                        sectionValuesDAO.setId(updatedSection.getId());

                        List<ESP_LIB_DynamicSectionValuesDAO.Instance> instancesList = new ArrayList<>();

                        //For Setting InstancesList
                        for (ESP_LIB_DynamicFormSectionFieldsCardsDAO ESPLIBDynamicFormSectionFieldsCardsDAO : updatedSection.getFieldsCardsList()) {

                            ESP_LIB_DynamicSectionValuesDAO.Instance instance = new ESP_LIB_DynamicSectionValuesDAO.Instance();

                            List<ESP_LIB_DynamicSectionValuesDAO.Instance.Value> valuesList = new ArrayList<>();

                            if (ESPLIBDynamicFormSectionFieldsCardsDAO.getFields() != null) {
                                //For Setting Intance->Values
                                for (ESP_LIB_DynamicFormSectionFieldDAO ESPLIBDynamicFormSectionFieldDAO : ESPLIBDynamicFormSectionFieldsCardsDAO.getFields()) {

                                    if (actual_response.getSectionValues() != null && ESPLIBDynamicFormSectionFieldDAO.isReadOnly())
                                        continue;

                                    ESP_LIB_DynamicSectionValuesDAO.Instance.Value value = new ESP_LIB_DynamicSectionValuesDAO.Instance.Value();
                                    ESP_LIB_DynamicFormSectionFieldDAO ESPLIBDynamicFormSectionFieldDAOTemp = ESP_LIB_Shared.getInstance().setObjectValues(ESPLIBDynamicFormSectionFieldDAO);
                                    ESPLIBDynamicFormSectionFieldDAOTemp.setValue(ESPLIBDynamicFormSectionFieldDAO.getValue());
                                    value.setSectionCustomFieldId(ESPLIBDynamicFormSectionFieldDAOTemp.getSectionCustomFieldId());
                                    value.setType(ESPLIBDynamicFormSectionFieldDAOTemp.getType());
                                    value.setSectionId(ESPLIBDynamicFormSectionFieldDAOTemp.getObjectId());
                                    value.setValue(ESPLIBDynamicFormSectionFieldDAOTemp.getValue());

                                    if (ESPLIBDynamicFormSectionFieldDAOTemp.getType() == 11) {
                                        String finalValue = value.getValue();
                                        if (finalValue != null && !finalValue.isEmpty())
                                            finalValue += ":" + ESPLIBDynamicFormSectionFieldDAOTemp.getSelectedCurrencyId() + ":" + ESPLIBDynamicFormSectionFieldDAOTemp.getSelectedCurrencySymbol();

                                        value.setValue(finalValue);
                                    } else if (ESPLIBDynamicFormSectionFieldDAOTemp.getType() == 13) {
                                        if (ESPLIBDynamicFormSectionFieldDAOTemp.getLookupValue() != null || !TextUtils.isEmpty(ESPLIBDynamicFormSectionFieldDAOTemp.getLookupValue()))
                                            value.setValue(String.valueOf(ESPLIBDynamicFormSectionFieldDAOTemp.getId()));
                                    }

                                    ESP_LIB_CustomLogs.displayLogs(TAG + " value.getValue(): " + value.getValue() + " getType: " + ESPLIBDynamicFormSectionFieldDAOTemp.getType());
                                    valuesList.add(value);

                                }
                            }
                            // Adding Instances
                            instance.setValues(valuesList);
                            instancesList.add(instance);
                        }

                        //Adding Instances To SectionValue
                        sectionValuesDAO.setInstances(instancesList);
                            sectionValuesListToPost.add(sectionValuesDAO);
                    }


                }
            }
            if (sectionValuesListToPost.size() > 0)
                submit_jsonNew.setSectionValues(sectionValuesListToPost);

            if (whatodo.equalsIgnoreCase("calculatedValues"))
                getCalculatedValues(submit_jsonNew);
            else
                SubmitForm(submit_jsonNew, whatodo);
        }

    }//END SubmitRequest

    private void UpdateLoadImageForField(ESP_LIB_DynamicFormSectionFieldDAO field, Uri uri) {
        if (field != null) {

            MultipartBody.Part body = null;

            try {

                body = ESP_LIB_Shared.getInstance().prepareFilePart(uri, bContext);
                UpLoadFile(field, body, uri);

            } catch (Exception e) {
                ESP_LIB_Shared.getInstance().errorLogWrite("FILE", e.getMessage());
            }

        }
    }

    private void SetUpLookUpValues(ESP_LIB_DynamicFormSectionFieldDAO field, ESP_LIB_LookUpDAO lookup, boolean isCalculatedMappedField) {

        field.setValue(String.valueOf(lookup.getId()));
        field.setLookupValue(lookup.getName());
        field.setId(lookup.getId());
        if (mApplicationSectionsAdapter != null) {
            mApplicationSectionsAdapter.notifyDataSetChanged();
        }
        // if (isCalculatedMappedField)
        if (field.isTigger())
            SubmitRequest("calculatedValues");

    }

    private void UpLoadFile(final ESP_LIB_DynamicFormSectionFieldDAO field, final MultipartBody.Part body, final Uri uri) {

        start_loading_animation();
        try {

            ESP_LIB_APIs apis = ESP_LIB_Shared.getInstance().retroFitObject(getContext());

            call_upload = apis.upload(body);
            call_upload.enqueue(new Callback<ESP_LIB_ResponseFileUploadDAO>() {
                @Override
                public void onResponse(Call<ESP_LIB_ResponseFileUploadDAO> call, Response<ESP_LIB_ResponseFileUploadDAO> response) {
                    stop_loading_animation();
                    if (response != null && response.body() != null) {

                        if (field != null) {

                            try {

                                //File file = FileUtils.getFile(bContext, uri);

                                File file = null;
                                String path_arslan = ESP_LIB_RealPathUtil.getPath(bContext, uri);
                                file = new File(path_arslan);

                                String attachmentFileSize = ESP_LIB_Shared.getInstance().getAttachmentFileSize(file);
                                ESP_LIB_DyanmicFormSectionFieldDetailsDAO detail = new ESP_LIB_DyanmicFormSectionFieldDetailsDAO();
                                detail.setName(file.getName());
                                detail.setDownloadUrl(response.body().getDownloadUrl());
                                detail.setMimeType(FileUtils.getMimeType(file));
                                detail.setCreatedOn(ESP_LIB_Shared.getInstance().GetCurrentDateTime());
                                detail.setFileSize(attachmentFileSize);
                                field.setDetails(detail);

                                field.setValue(response.body().getFileId());


                                if (mApplicationSectionsAdapter != null) {
                                    mApplicationSectionsAdapter.notifyDataSetChanged();
                                }
                                if (field.isTigger())
                                    SubmitRequest("calculatedValues");

                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                        }

                    } else {

                        ESP_LIB_Shared.getInstance().messageBox(getString(R.string.esp_lib_text_some_thing_went_wrong), bContext);
                    }

                }

                @Override
                public void onFailure(Call<ESP_LIB_ResponseFileUploadDAO> call, Throwable t) {
                    stop_loading_animation();
                    ESP_LIB_Shared.getInstance().showAlertMessage(getString(R.string.esp_lib_text_error), getString(R.string.esp_lib_text_some_thing_went_wrong), bContext);
                    // UploadFileInformation(fileDAO);
                }
            });

        } catch (Exception ex) {
            stop_loading_animation();
            if (ex != null) {
                ESP_LIB_Shared.getInstance().showAlertMessage(getString(R.string.esp_lib_text_error), getString(R.string.esp_lib_text_some_thing_went_wrong), bContext);
                //UploadFileInformation(fileDAO);

            }
        }
    }//LoggedInUser end

    private void SubmitForm(ESP_LIB_DynamicResponseDAO post, String whatodo) {

        start_loading_animation();
        try {

            ESP_LIB_APIs apis = ESP_LIB_Shared.getInstance().retroFitObject(bContext);

            if (whatodo.equals(getString(R.string.esp_lib_text_draft))) {
                submit_call = apis.DraftApplication(post);
            } else {
                submit_call = apis.SubmitApplication(post);
            }

            submit_call.enqueue(new Callback<Integer>() {
                @Override
                public void onResponse(Call<Integer> call, Response<Integer> response) {
                    stop_loading_animation();
                    //  if (response != null && response.body() != null) {
                    if (response.code() == 409)
                        ESP_LIB_Shared.getInstance().showAlertMessage("", getString(R.string.esp_lib_text_closingdatepassed), bContext);
                    else {
                        if (response.isSuccessful()) {
                           /* Bundle bnd = new Bundle();
                            bnd.putBoolean("whatodo", true);
                            Intent intent = new Intent();
                            intent.putExtras(bnd);
                            bContext.setResult(2, intent);
                            bContext.finish();*/
                            EventBus.getDefault().post(new EventOptions.EventRefreshData());
                            // if (ESP_LIB_ESPApplication.getInstance().isComponent()) {
                            if (ESP_LIB_ESPApplication.getInstance().isSpecificApplication())
                                bContext.finish();
                            else {
                                ESP_LIB_ESPApplication.getInstance().setOnCLosedTab(true);
                                ESP_LIB_ESPApplication.getInstance().setGoToMainScreen(true);
                                bContext.finish();
                            }
                            /*} else {
                                Intent intent = new Intent(bContext, ESP_LIB_ApplicationsActivityDrawer.class);
                                ComponentName cn = intent.getComponent();
                                Intent mainIntent = Intent.makeRestartActivityTask(cn);
                                startActivity(mainIntent);
                            }*/

                        } else
                            ESP_LIB_Shared.getInstance().messageBox(getString(R.string.esp_lib_text_some_thing_went_wrong), bContext);
                    }
                    /*} else {

                        Shared.getInstance().messageBox(getString(R.string.some_thing_went_wrong), bContext);
                    }
*/
                }

                @Override
                public void onFailure(Call<Integer> call, Throwable t) {
                    stop_loading_animation();
                    ESP_LIB_Shared.getInstance().showAlertMessage(getString(R.string.esp_lib_text_error), getString(R.string.esp_lib_text_some_thing_went_wrong), bContext);
                    // UploadFileInformation(fileDAO);
                }
            });

        } catch (Exception ex) {
            stop_loading_animation();
            if (ex != null) {
                ESP_LIB_Shared.getInstance().showAlertMessage(getString(R.string.esp_lib_text_error), getString(R.string.esp_lib_text_some_thing_went_wrong), bContext);
                //UploadFileInformation(fileDAO);

            }
        }
    }//LoggedInUser end

    private void setGravity() {
        if (pref.getLanguage().equalsIgnoreCase("ar")) {
            category_name.setGravity(Gravity.RIGHT);
            txtcategory.setGravity(Gravity.RIGHT);

        } else {
            category_name.setGravity(Gravity.LEFT);
            txtcategory.setGravity(Gravity.LEFT);
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        registerReciever();
    }


    @Override
    public void onDetach() {
        super.onDetach();
        unRegisterReciever();

    }

    private void registerReciever() {

        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);

    }

    private void unRegisterReciever() {
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void dataRefreshEvent(EventOptions.EventTriggerController eventTriggerController) {
        unRegisterReciever();
        Handler handler = new Handler();
        handler.postDelayed(() -> SubmitRequest("calculatedValues"), 1000);

    }

    private void getCalculatedValues(ESP_LIB_DynamicResponseDAO post) {
        try {
            if (isKeyboardVisible)
                isCalculatedField = true;
            //  start_loading_animation();
            Call<List<ESP_LIB_CalculatedMappedFieldsDAO>> calculatedMapped_call = ESP_LIB_Shared.getInstance().retroFitObject(getActivity()).getCalculatedValues(post);

            calculatedMapped_call.enqueue(new Callback<List<ESP_LIB_CalculatedMappedFieldsDAO>>() {
                @Override
                public void onResponse(Call<List<ESP_LIB_CalculatedMappedFieldsDAO>> call, Response<List<ESP_LIB_CalculatedMappedFieldsDAO>> response) {

                    if (response != null && response.body() != null) {

                        List<ESP_LIB_CalculatedMappedFieldsDAO> calculatedMappedFieldsList = response.body();

                        for (int i = 0; i < calculatedMappedFieldsList.size(); i++) {
                            ESP_LIB_CalculatedMappedFieldsDAO ESPLIBCalculatedMappedFieldsDAO = calculatedMappedFieldsList.get(i);

                            if (mApplicationSectionsAdapter.getmApplications() != null) {
                                List<ESP_LIB_DynamicFormSectionDAO> ESPLIBDynamicFormSectionDAOS = mApplicationSectionsAdapter.getmApplications();
                                for (int u = 0; u < ESPLIBDynamicFormSectionDAOS.size(); u++) {
                                    ESP_LIB_DynamicFormSectionDAO updatedSection = ESPLIBDynamicFormSectionDAOS.get(u);
                                    if (updatedSection.getFieldsCardsList().size() > 0) {
                                        //For Setting InstancesList
                                        //for (DynamicFormSectionFieldsCardsDAO dynamicFormSectionFieldsCardsDAO : updatedSection.getFieldsCardsList()) {

                                        for (int g = 0; g < updatedSection.getFieldsCardsList().size(); g++) {
                                            if (ESPLIBCalculatedMappedFieldsDAO.getSectionIndex() == g) {
                                                ESP_LIB_DynamicFormSectionFieldsCardsDAO ESPLIBDynamicFormSectionFieldsCardsDAO = updatedSection.getFieldsCardsList().get(g);

                                                if (ESPLIBDynamicFormSectionFieldsCardsDAO.getFields() != null) {

                                                    for (int p = 0; p < ESPLIBDynamicFormSectionFieldsCardsDAO.getFields().size(); p++) {
                                                        ESP_LIB_DynamicFormSectionFieldDAO ESPLIBDynamicFormSectionFieldDAO = ESPLIBDynamicFormSectionFieldsCardsDAO.getFields().get(p);

                                                        if (ESPLIBDynamicFormSectionFieldDAO.getSectionCustomFieldId() == ESPLIBCalculatedMappedFieldsDAO.getSectionCustomFieldId()) {
                                                            int targetFieldType = ESPLIBCalculatedMappedFieldsDAO.getTargetFieldType();

                                                            if (targetFieldType == 13) {

                                                                if (!ESPLIBCalculatedMappedFieldsDAO.getValue().isEmpty()) {
                                                                    String[] split = ESPLIBCalculatedMappedFieldsDAO.getValue().split(":");
                                                                    String lookupId = split[0];
                                                                    if (!lookupId.isEmpty() && ESP_LIB_Shared.getInstance().isNumeric(lookupId))
                                                                        ESPLIBDynamicFormSectionFieldDAO.setId(Integer.parseInt(lookupId));

                                                                    if (split.length >= 1) {
                                                                        try {
                                                                            String lookupText = split[1];
                                                                            ESPLIBDynamicFormSectionFieldDAO.setLookupValue(lookupText);
                                                                        }catch (Exception e){e.printStackTrace();}
                                                                    }
                                                                }


                                                                List<ESP_LIB_LookUpDAO> servicelookupItems = ESPLIBCalculatedMappedFieldsDAO.getLookupItems();
                                                                if (ESPLIBDynamicFormSectionFieldDAO.getLookupValue() != null && !ESPLIBDynamicFormSectionFieldDAO.getLookupValue().isEmpty()) {

                                                                    List<String> servicelookupItemsTemp = new ArrayList<>();
                                                                    if (servicelookupItems != null) {
                                                                        for (int s = 0; s < servicelookupItems.size(); s++) {
                                                                            servicelookupItemsTemp.add(servicelookupItems.get(s).getName());
                                                                        }
                                                                        if (!servicelookupItemsTemp.contains(ESPLIBDynamicFormSectionFieldDAO.getLookupValue()))
                                                                            ESPLIBDynamicFormSectionFieldDAO.setLookupValue("");
                                                                    }

                                                                }


                                                                ESPLIBCalculatedMappedFieldsDAO.setLookupItems(servicelookupItems);
                                                                ESP_LIB_Shared.getInstance().saveLookUpItems(ESPLIBCalculatedMappedFieldsDAO.getSectionCustomFieldId(), servicelookupItems);

                                                            }

                                                            if (targetFieldType == 7) {

                                                                ESPLIBDynamicFormSectionFieldDAO.setMappedCalculatedField(true);
                                                                ESPLIBDynamicFormSectionFieldDAO.setType(ESPLIBCalculatedMappedFieldsDAO.getTargetFieldType());
                                                                ESPLIBDynamicFormSectionFieldDAO.setValue(ESPLIBCalculatedMappedFieldsDAO.getValue());
                                                                String attachmentFileSize = "";
                                                                if (ESPLIBCalculatedMappedFieldsDAO.getDetails() != null) {
                                                                    String getOutputMediaFile = ESP_LIB_Shared.getInstance().getOutputMediaFile(ESPLIBCalculatedMappedFieldsDAO.getDetails().getName()).getPath();
                                                                    boolean isFileExist = ESP_LIB_Shared.getInstance().isFileExist(getOutputMediaFile, bContext);
                                                                    if (isFileExist) {
                                                                        File file = null;
                                                                        file = new File(getOutputMediaFile);
                                                                        attachmentFileSize = ESP_LIB_Shared.getInstance().getAttachmentFileSize(file);
                                                                    }

                                                                    ESPLIBCalculatedMappedFieldsDAO.getDetails().setFileSize(attachmentFileSize);
                                                                }
                                                                ESPLIBDynamicFormSectionFieldDAO.setDetails(ESPLIBCalculatedMappedFieldsDAO.getDetails());
                                                            } else if (targetFieldType == 4) {
                                                                String calculatedDisplayDate = ESP_LIB_Shared.getInstance().getDisplayDate(bContext, ESPLIBCalculatedMappedFieldsDAO.getValue(), true);
                                                                ESPLIBDynamicFormSectionFieldDAO.setValue(calculatedDisplayDate);


                                                            } else if (targetFieldType == 11) {
                                                                ESP_LIB_DynamicFormSectionFieldDAO fieldDAO = ESP_LIB_Shared.getInstance().populateCurrency(ESPLIBCalculatedMappedFieldsDAO.getValue());
                                                                String concateValue = fieldDAO.getValue() + " " + fieldDAO.getSelectedCurrencySymbol();
                                                                ESPLIBDynamicFormSectionFieldDAO.setValue(concateValue);


                                                            } else
                                                                ESPLIBDynamicFormSectionFieldDAO.setValue(ESPLIBCalculatedMappedFieldsDAO.getValue());


                                                        }

                                                    }

                                                }
                                            }
                                        }
                                    }


                                }
                            }

                        }
                        if (mApplicationSectionsAdapter != null && !isNotified)
                            mApplicationSectionsAdapter.notifyDataSetChanged();


                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                registerReciever();
                            }
                        }, 1000);

                        if (ESP_LIB_ChooseLookUpOption.Companion.isOpen())
                            EventBus.getDefault().post(new EventOptions.EventTriggerController());

                    } else {
                        ESP_LIB_CustomLogs.displayLogs(TAG + " null response");
                        registerReciever();
                        // stop_loading_animation();
                        ESP_LIB_Shared.getInstance().messageBox(getString(R.string.esp_lib_text_some_thing_went_wrong), bContext);
                    }


                }

                @Override
                public void onFailure(Call<List<ESP_LIB_CalculatedMappedFieldsDAO>> call, Throwable t) {
                    ESP_LIB_CustomLogs.displayLogs(TAG + " failure response");
                    registerReciever();
                    //  stop_loading_animation();
                    ESP_LIB_Shared.getInstance().messageBox(getString(R.string.esp_lib_text_some_thing_went_wrong), bContext);
                }
            });

        } catch (Exception ex) {
            ex.printStackTrace();
            // stop_loading_animation();
            ESP_LIB_Shared.getInstance().messageBox(getString(R.string.esp_lib_text_some_thing_went_wrong), bContext);

        }
    }//LoggedInUser end


    public void refreshAdapter(boolean visible) {
        isNotified = visible;
    }


}

