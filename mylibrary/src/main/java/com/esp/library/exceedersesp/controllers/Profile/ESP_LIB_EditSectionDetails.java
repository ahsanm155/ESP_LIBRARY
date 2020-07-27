package com.esp.library.exceedersesp.controllers.Profile;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.esp.library.R;
import com.esp.library.exceedersesp.ESP_LIB_BaseActivity;
import com.esp.library.exceedersesp.ESP_LIB_ESPApplication;
import com.esp.library.exceedersesp.controllers.Profile.adapters.ESP_LIB_ListofSectionsFieldsAdapter;
import com.esp.library.exceedersesp.controllers.applications.ESP_LIB_ApplicationsActivityDrawer;
import com.esp.library.exceedersesp.controllers.applications.ESP_LIB_ChooseLookUpOption;
import com.esp.library.exceedersesp.controllers.fieldstype.viewholders.ESP_LIB_EditTextTypeViewHolder;
import com.esp.library.ipaulpro.afilechooser.utils.FileUtils;
import com.esp.library.utilities.common.ESP_LIB_CustomLogs;
import com.esp.library.utilities.common.ESP_LIB_KeyboardUtils;
import com.esp.library.utilities.common.ESP_LIB_RealPathUtil;
import com.esp.library.utilities.common.ESP_LIB_Shared;
import com.esp.library.utilities.common.ESP_LIB_SharedPreference;
import com.esp.library.utilities.customevents.EventOptions;
import com.esp.library.utilities.setup.applications.ESP_LIB_ApplicationFieldsRecyclerAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import utilities.data.apis.ESP_LIB_APIs;
import utilities.data.applicants.ESP_LIB_CalculatedMappedFieldsDAO;
import utilities.data.applicants.addapplication.ESP_LIB_CurrencyDAO;
import utilities.data.applicants.addapplication.ESP_LIB_LookUpDAO;
import utilities.data.applicants.addapplication.ESP_LIB_ResponseFileUploadDAO;
import utilities.data.applicants.dynamics.ESP_LIB_DyanmicFormSectionFieldDetailsDAO;
import utilities.data.applicants.dynamics.ESP_LIB_DynamicFormSectionDAO;
import utilities.data.applicants.dynamics.ESP_LIB_DynamicFormSectionFieldDAO;
import utilities.data.applicants.profile.ESP_LIB_ApplicationProfileDAO;
import utilities.data.applicants.profile.ESP_LIB_RealTimeValuesDAO;

public class ESP_LIB_EditSectionDetails extends ESP_LIB_BaseActivity implements ESP_LIB_ApplicationFieldsRecyclerAdapter.ApplicationFieldsAdapterListener {

    String TAG = getClass().getSimpleName();


    TextView txtheader;
    TextView txtsave;
    TextView txtcancel;
    LinearLayout layoutMain;
    RecyclerView rvFields;
    NestedScrollView scrollview;
    boolean isServiceRunning;
    LayoutInflater inflate;
    ESP_LIB_BaseActivity context;
    ESP_LIB_DynamicFormSectionDAO ESPLIBDynamicFormSectionDAO;
    ESP_LIB_SharedPreference pref;
    ESP_LIB_DynamicFormSectionFieldDAO fieldToBeUpdated;
    boolean isCalculatedField;
    boolean isKeyboardVisible;
    private static EditText val;
    List<ESP_LIB_DynamicFormSectionFieldDAO> fieldsList = new ArrayList<>();
    int count = 0;
    boolean ischeckerror;
    String basicName;
    ESP_LIB_ApplicationProfileDAO dataapplicant;
    com.esp.library.utilities.customcontrols.ESP_LIB_CustomButton btadd;
    AlertDialog pDialog;
    RelativeLayout rltoolbar;
    private static final int HIDE_THRESHOLD = 20;
    ESP_LIB_ListofSectionsFieldsAdapter listofSectionsFieldsAdapter;
    private static final int REQUEST_LOOKUP = 2;
    private static final int REQUEST_CHOOSER = 12345;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        changeStatusBarColor(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.esp_lib_sectiondetail);
        initailize();

        ischeckerror = getIntent().getBooleanExtra("ischeckerror", false);
        if (ESP_LIB_Shared.getInstance().isWifiConnected(getBContext())) {
            loadCurrencies();
        } else {
            ESP_LIB_Shared.getInstance().showAlertMessage(getBContext().getString(R.string.esp_lib_text_internet_error_heading), getBContext().getString(R.string.esp_lib_text_internet_connection_error), getBContext());
        }

        txtsave.setOnClickListener(v -> {

            if (txtsave.getText().equals(getString(R.string.esp_lib_text_add))) {
                ArrayList<ESP_LIB_ApplicationProfileDAO.Values> updateValues = getUpdateValues();
                postUpdatedData(updateValues, false, false);
            } else {
                if (ESPLIBDynamicFormSectionDAO.isDefault())
                    postBasicData();
                else
                    postSectionData();
            }

        });


        txtcancel.setOnClickListener(v -> {
            onBackPressed();
        });

        ESP_LIB_KeyboardUtils.addKeyboardToggleListener(context, new ESP_LIB_KeyboardUtils.SoftKeyboardToggleListener() {
            @Override
            public void onToggleSoftKeyboard(boolean isVisible) {

                if (isVisible) {
                    View v = getCurrentFocus();
                    if (v instanceof EditText) {
                        scrollview.smoothScrollTo(0, v.getBottom() + 10);
                    }
                }

                isKeyboardVisible = isVisible;
            }
        });

    }

    private void initailize() {
        context = ESP_LIB_EditSectionDetails.this;
        pDialog = ESP_LIB_Shared.getInstance().setProgressDialog(getBContext());

        txtheader = findViewById(R.id.txtheader);
        txtsave = findViewById(R.id.txtsave);
        txtcancel = findViewById(R.id.txtcancel);
        layoutMain = findViewById(R.id.lllayout);
        scrollview = findViewById(R.id.scrollview);
        rvFields = findViewById(R.id.rvFields);

        dataapplicant = (ESP_LIB_ApplicationProfileDAO) getIntent().getSerializableExtra("dataapplicant");
        ESPLIBDynamicFormSectionDAO = (ESP_LIB_DynamicFormSectionDAO) getIntent().getSerializableExtra("data");
        pref = new ESP_LIB_SharedPreference(context);
        inflate = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rltoolbar = findViewById(R.id.rltoolbar);
        rvFields.setHasFixedSize(true);
        rvFields.setLayoutManager(new LinearLayoutManager(context));
        rvFields.setItemAnimator(new DefaultItemAnimator());
    }


    private void postSectionData() {
        if (ischeckerror) {
            ArrayList<ESP_LIB_ApplicationProfileDAO.Values> updateValues = getUpdateValues();
            postUpdatedData(updateValues, true, false);
        } else {
            //   ApplicationProfileDAO.Applicant values = getValues();
            //CustomLogs.displayLogs(TAG + " joobjtosting: " + values.toJson());
            ArrayList<ESP_LIB_ApplicationProfileDAO.Values> updateValues = getUpdateValues();
            postUpdatedData(updateValues, true, true);

            //  postData(values);
        }
    }

    private ESP_LIB_ApplicationProfileDAO.Applicant getValues() {

        List<ESP_LIB_ApplicationProfileDAO.ApplicationSection> applicantSectionsList = dataapplicant.getApplicant().getApplicantSections();
        List<ESP_LIB_DynamicFormSectionFieldDAO> fields = ESPLIBDynamicFormSectionDAO.getFields();
        ESP_LIB_ApplicationProfileDAO.ApplicationSection applicationSection = null;
        ArrayList<ESP_LIB_ApplicationProfileDAO.Values> valueList = new ArrayList<>();

        if (fields != null) {
            for (int j = 0; j < fields.size(); j++) {
                ESP_LIB_DynamicFormSectionFieldDAO ESPLIBDynamicFormSectionFieldDAO = fields.get(j);

                int getSectionCustomFieldId = fields.get(j).getSectionCustomFieldId();
                if (fields.get(j).isVisible()) {
                    String value = fields.get(j).getValue();
                    int id = fields.get(j).getId();
                    int fieldsSectionId = ESPLIBDynamicFormSectionDAO.getId();

                    if (applicantSectionsList != null) {
                        for (int i = 0; i < applicantSectionsList.size(); i++) {
                            applicationSection = applicantSectionsList.get(i);
                            int sectionId = applicationSection.getSectionId();

                            if (fieldsSectionId == sectionId) {

                                if (ESPLIBDynamicFormSectionFieldDAO.getType() == 11) {
                                    value += ":" + ESPLIBDynamicFormSectionFieldDAO.getSelectedCurrencyId() + ":" + ESPLIBDynamicFormSectionFieldDAO.getSelectedCurrencySymbol();
                                    ESPLIBDynamicFormSectionFieldDAO.setValue(value);
                                }

                                if (ESPLIBDynamicFormSectionFieldDAO.getType() == 13) {

                                    if (ESPLIBDynamicFormSectionFieldDAO.getLookUpDAO() == null) {
                                        List<ESP_LIB_ApplicationProfileDAO.Values> values = applicationSection.getValues();
                                        for (int g = 0; g < values.size(); g++) {
                                            if (getSectionCustomFieldId == values.get(g).getSectionFieldId())
                                                value = values.get(g).getValue();
                                        }
                                    } else {
                                        value = String.valueOf(ESPLIBDynamicFormSectionFieldDAO.getLookUpDAO().getId());
                                    }
                                }


                                ESP_LIB_ApplicationProfileDAO.Values val = new ESP_LIB_ApplicationProfileDAO.Values();
                                val.setSectionFieldId(getSectionCustomFieldId);
                                val.setValue(value);
                                valueList.add(val);

                            }


                        }
                    }
                    applicationSection.setValues(valueList);
                    dataapplicant.getApplicant().setApplicantSections(applicantSectionsList);
                }
            }
        }

        return dataapplicant.getApplicant();
    }

    private ArrayList<ESP_LIB_ApplicationProfileDAO.Values> getUpdateValues() {
        //  ApplicationProfileDAO dataapplicant = (ApplicationProfileDAO) getIntent().getSerializableExtra("dataapplicant");
        List<ESP_LIB_ApplicationProfileDAO.ApplicationSection> applicantSectionsList = dataapplicant.getApplicant().getApplicantSections();
        List<ESP_LIB_DynamicFormSectionDAO> sections = dataapplicant.getSections();
        ArrayList<ESP_LIB_ApplicationProfileDAO.Values> valueList = new ArrayList<>();

        for (int jj = 0; jj < sections.size(); jj++) {
            int parentSectionId = sections.get(jj).getId();

            List<ESP_LIB_DynamicFormSectionFieldDAO> fields = ESPLIBDynamicFormSectionDAO.getFields();
            ESP_LIB_ApplicationProfileDAO.ApplicationSection applicationSection = null;


            for (int j = 0; j < fields.size(); j++) {
                ESP_LIB_DynamicFormSectionFieldDAO ESPLIBDynamicFormSectionFieldDAO = fields.get(j);
                int getSectionCustomFieldId = ESPLIBDynamicFormSectionFieldDAO.getSectionCustomFieldId();
                if (ESPLIBDynamicFormSectionFieldDAO.isVisible()) {

                    int id = ESPLIBDynamicFormSectionFieldDAO.getId();
                    int fieldsSectionId = ESPLIBDynamicFormSectionDAO.getId();


                    for (int i = 0; i < applicantSectionsList.size(); i++) {
                        applicationSection = applicantSectionsList.get(i);
                        int sectionId = applicationSection.getSectionId();

                        if (parentSectionId == sectionId) {


                            if (fieldsSectionId == sectionId) {

                       /* CustomLogs.displayLogs(TAG + " getSectionCustomFieldId: " + getSectionCustomFieldId +
                                " sectionId: " + sectionId + " id: " + id + " fieldsSectionId: " + fieldsSectionId
                                + " value: " + value);*/
                                String value = ESPLIBDynamicFormSectionFieldDAO.getValue();
                                if (ESPLIBDynamicFormSectionFieldDAO.getType() == 11) {

                                    value += ":" + ESPLIBDynamicFormSectionFieldDAO.getSelectedCurrencyId() + ":" + ESPLIBDynamicFormSectionFieldDAO.getSelectedCurrencySymbol();

                                    ESPLIBDynamicFormSectionFieldDAO.setValue(value);
                                }

                              /*  if (dynamicFormSectionFieldDAO.getType() == 13) {

                                    if (dynamicFormSectionFieldDAO.getLookUpDAO() == null) {
                                        List<ApplicationProfileDAO.Values> values = applicationSection.getValues();
                                        for (int g = 0; g < values.size(); g++) {
                                            if (getSectionCustomFieldId == values.get(g).getSectionFieldId())
                                                value = values.get(g).getValue();
                                        }
                                    } else {
                                        value = String.valueOf(dynamicFormSectionFieldDAO.getLookUpDAO().getId());
                                    }
                                }*/


                                ESP_LIB_ApplicationProfileDAO.Values val = new ESP_LIB_ApplicationProfileDAO.Values();
                                val.setSectionFieldId(getSectionCustomFieldId);
                                val.setValue(value);
                                valueList.add(val);
                            }
                            ESP_LIB_CustomLogs.displayLogs(TAG + " parentSectionId: " + parentSectionId);
                            break;
                        }

                    }
                    applicationSection.setValues(valueList);
                    dataapplicant.getApplicant().setApplicantSections(applicantSectionsList);
                }
            }


        }


        for (int j = 0; j < valueList.size(); j++) {
            ESP_LIB_CustomLogs.displayLogs(TAG + " valueList: " + valueList.get(j).toJson());
        }


        ESP_LIB_CustomLogs.displayLogs(TAG + " getApplicantSections: " + dataapplicant.getApplicant().getApplicantSections());

        /*for (Integer key : sectionsIndex.keySet()) {
            CustomLogs.displayLogs(TAG + " key: " + key);
        }*/

        return valueList;
    }


    private void populateData() {
        LinearLayout subLayout = subLayout();

        txtheader.setText(ESPLIBDynamicFormSectionDAO.getDefaultName());

        for (int j = 0; j < ESPLIBDynamicFormSectionDAO.getFields().size(); j++) {
            ESP_LIB_DynamicFormSectionFieldDAO ESPLIBDynamicFormSectionFieldDAO = ESPLIBDynamicFormSectionDAO.getFields().get(j);
            ESP_LIB_DynamicFormSectionFieldDAO ESPLIBDynamicFormSectionFieldDAO1 = ESP_LIB_Shared.getInstance().setObjectValues(ESPLIBDynamicFormSectionFieldDAO);

            fieldsList.add(ESPLIBDynamicFormSectionFieldDAO1);

        }
        ESPLIBDynamicFormSectionDAO.setFields(fieldsList);

        if (ESPLIBDynamicFormSectionDAO.isDefault()) {
            LinearLayout layoutName = (LinearLayout) inflate.inflate(R.layout.esp_lib_item_add_application_field_type_edit_text, null);
            LinearLayout.LayoutParams layoutParamsName = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            layoutParamsName.setMargins(0, 40, 0, 0);
            layoutName.setLayoutParams(layoutParamsName);
            RecyclerView.ViewHolder itemViewName = new SeparatorTypeViewHolder(layoutName);
            itemViewName = new ESP_LIB_EditTextTypeViewHolder(layoutName);
            // basicName = ESPApplication.getInstance().getUser().getLoginResponse().getName();
            basicName = dataapplicant.getApplicant().getName();
            ((ESP_LIB_EditTextTypeViewHolder) itemViewName).etValue.setText(basicName);
            ((ESP_LIB_EditTextTypeViewHolder) itemViewName).tilFieldLabel.setHint(getString(R.string.esp_lib_text_name) + " *");

            ((ESP_LIB_EditTextTypeViewHolder) itemViewName).etValue.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    basicName = String.valueOf(s);
                    onFieldValuesChanged();

                }
            });

            LinearLayout layout_email = (LinearLayout) inflate.inflate(R.layout.esp_lib_item_add_application_field_type_edit_text, null);
            LinearLayout.LayoutParams layoutParamsEmail = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            layoutParamsEmail.setMargins(0, 40, 0, 0);
            layout_email.setLayoutParams(layoutParamsEmail);
            RecyclerView.ViewHolder itemViewEmail = new SeparatorTypeViewHolder(layout_email);
            itemViewEmail = new ESP_LIB_EditTextTypeViewHolder(layout_email);
            ((ESP_LIB_EditTextTypeViewHolder) itemViewEmail).tilFieldLabel.setHint(getString(R.string.esp_lib_text_login_email_label));
            ((ESP_LIB_EditTextTypeViewHolder) itemViewEmail).etValue.setText(dataapplicant.getApplicant().getEmailAddress());
            ((ESP_LIB_EditTextTypeViewHolder) itemViewEmail).etValue.setEnabled(false);
            ((ESP_LIB_EditTextTypeViewHolder) itemViewEmail).etValue.setTextColor(ContextCompat.getColor(context, R.color.esp_lib_color_cooltwogrey));

            LinearLayout layout_profiletype = (LinearLayout) inflate.inflate(R.layout.esp_lib_item_add_application_field_type_edit_text, null);
            LinearLayout.LayoutParams layoutParamsProfileType = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            layoutParamsProfileType.setMargins(0, 40, 0, 0);
            layout_profiletype.setLayoutParams(layoutParamsProfileType);
            RecyclerView.ViewHolder itemViewProfileType = new SeparatorTypeViewHolder(layout_profiletype);
            itemViewProfileType = new ESP_LIB_EditTextTypeViewHolder(layout_profiletype);
            ((ESP_LIB_EditTextTypeViewHolder) itemViewProfileType).tilFieldLabel.setHint(getString(R.string.esp_lib_text_profiletype));
            ((ESP_LIB_EditTextTypeViewHolder) itemViewProfileType).etValue.setText(dataapplicant.getApplicant().getProfileTemplateString());
            ((ESP_LIB_EditTextTypeViewHolder) itemViewProfileType).etValue.setEnabled(false);
            ((ESP_LIB_EditTextTypeViewHolder) itemViewProfileType).etValue.setTextColor(ContextCompat.getColor(context, R.color.esp_lib_color_cooltwogrey));

            subLayout.addView(layoutName);
            subLayout.addView(layout_email);
            subLayout.addView(layout_profiletype);
        }

        List<ESP_LIB_DynamicFormSectionFieldDAO> fields = ESPLIBDynamicFormSectionDAO.getFields();

        listofSectionsFieldsAdapter = new ESP_LIB_ListofSectionsFieldsAdapter(fields, context, ischeckerror, false);
        listofSectionsFieldsAdapter.getListenerContext(this);
        rvFields.setAdapter(listofSectionsFieldsAdapter);

        /*for (int i = 0; i < fields.size(); i++) {
            DynamicFormSectionFieldDAO field = fields.get(i);
            int type = field.getType();
            count = i;
            if (field.isVisible()) {
                LinearLayout layout = populateFields(type, field, i);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                layoutParams.setMargins(0, 40, 0, 0);
                layout.setLayoutParams(layoutParams);
                subLayout.addView(layout);
            }
        }*/


        layoutMain.addView(subLayout);
        boolean isaddmore = getIntent().getBooleanExtra("isaddmore", false);
        if (isaddmore) // coming from SectionDetailScreen.class
            txtsave.setText(getString(R.string.esp_lib_text_add));
        else
            txtsave.setText(getString(R.string.esp_lib_text_save));


    }

    private LinearLayout subLayout() {
        LinearLayout ll = new LinearLayout(this);
        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        layoutParams1.setMargins(0, 0, 0, 0);
        ll.setLayoutParams(layoutParams1);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setBackgroundColor(Color.WHITE);
        return ll;
    }

    public void loadCurrencies() {
        start_loading_animation();

        try {
            /* APIs Mapping respective Object*/
            ESP_LIB_APIs apis = ESP_LIB_Shared.getInstance().retroFitObject(context);

            Call<List<ESP_LIB_CurrencyDAO>> call = apis.getCurrency();

            call.enqueue(new Callback<List<ESP_LIB_CurrencyDAO>>() {
                @Override
                public void onResponse(Call<List<ESP_LIB_CurrencyDAO>> call, Response<List<ESP_LIB_CurrencyDAO>> response) {

                    if (response.body() != null && response.body().size() > 0) {
                        ESP_LIB_ESPApplication.getInstance().setCurrencies(response.body());
                    }


                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            stop_loading_animation();
                        }
                    }, 2000);

                    populateData();
                }

                @Override
                public void onFailure(Call<List<ESP_LIB_CurrencyDAO>> call, Throwable t) {
                    stop_loading_animation();
                }
            });

        } catch (Exception ex) {
            stop_loading_animation();
        }
    }


    @Override
    public void onFieldValuesChanged() {

        List<ESP_LIB_DynamicFormSectionFieldDAO> fields = ESPLIBDynamicFormSectionDAO.getFields();
        boolean isAllFieldsValidateTrue = true;

        if (fields != null) {
            for (int i = 0; i < fields.size(); i++) {
                ESP_LIB_DynamicFormSectionFieldDAO ESPLIBDynamicFormSectionFieldDAO = fields.get(i);
                if (!ESPLIBDynamicFormSectionFieldDAO.isShowToUserOnly())  // if fields are not for displayed then validate
                {
                    String error = "";
                    error = ESP_LIB_Shared.getInstance().edittextErrorChecks(context, ESPLIBDynamicFormSectionFieldDAO.getValue(), error, ESPLIBDynamicFormSectionFieldDAO);
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
        }

        if (basicName != null) {
            validateFields(isAllFieldsValidateTrue, basicName);
        } else {
            if (isAllFieldsValidateTrue) {
                txtsave.setEnabled(true);
                txtsave.setAlpha(1);
                if (btadd != null) {
                    btadd.setEnabled(true);
                    btadd.setAlpha(1);
                }

            } else {
                txtsave.setEnabled(false);
                txtsave.setAlpha(0.5f);
                if (btadd != null) {
                    btadd.setEnabled(false);
                    btadd.setAlpha(0.5f);
                }
            }


        }
    }

    private void validateFields(boolean isAllFieldsValidateTrue, String basicName) {
        if (isAllFieldsValidateTrue && basicName.length() > 0) {
            txtsave.setEnabled(true);
            txtsave.setAlpha(1);
            if (btadd != null) {
                btadd.setEnabled(true);
                btadd.setAlpha(1);
            }

        } else {
            txtsave.setEnabled(false);
            txtsave.setAlpha(0.5f);
            if (btadd != null) {
                btadd.setEnabled(false);
                btadd.setAlpha(0.5f);
            }
        }
    }

    @Override
    public void onAttachmentFieldClicked(ESP_LIB_DynamicFormSectionFieldDAO fieldDAO, int position) {


        fieldToBeUpdated = fieldDAO;
        fieldToBeUpdated.setUpdatePositionAttachment(position);


        String getAllowedValuesCriteria = fieldToBeUpdated.getAllowedValuesCriteria();


        getAllowedValuesCriteria = getAllowedValuesCriteria.replaceAll("\\.", "");


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


        final Intent getContentIntent = new Intent(Intent.ACTION_GET_CONTENT);
        // The MIME data type filter

        getContentIntent.setType("*/*");
        if (getAllowedValuesCriteria.length() > 0)
            getContentIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        // Only return URIs that can be opened with ContentResolver
        getContentIntent.addCategory(Intent.CATEGORY_OPENABLE);

        Intent intent = Intent.createChooser(getContentIntent, getString(R.string.esp_lib_text_selectafile));
        intent.putExtra("position", position);
        startActivityForResult(intent, REQUEST_CHOOSER);
    }

    @Override
    public void onLookupFieldClicked(ESP_LIB_DynamicFormSectionFieldDAO fieldDAO, int position, boolean isCalculatedMappedField) {

        if (!pDialog.isShowing()) {
            fieldToBeUpdated = fieldDAO;
            fieldToBeUpdated.setUpdatePositionAttachment(position);
            Bundle bundle = new Bundle();
            bundle.putSerializable(ESP_LIB_DynamicFormSectionFieldDAO.Companion.getBUNDLE_KEY(), fieldToBeUpdated);

            Intent chooseLookupOption = new Intent(context, ESP_LIB_ChooseLookUpOption.class);
            chooseLookupOption.putExtras(bundle);
            startActivityForResult(chooseLookupOption, REQUEST_LOOKUP);
        }
    }


    protected class SeparatorTypeViewHolder extends RecyclerView.ViewHolder {

        public SeparatorTypeViewHolder(View itemView) {
            super(itemView);


        }

    }


    public void SetUpLookUpValues(ESP_LIB_DynamicFormSectionFieldDAO field, ESP_LIB_LookUpDAO lookup) {

        field.setValue(String.valueOf(lookup.getId()));
        field.setLookupValue(lookup.getName());
        field.setId(lookup.getId());

        if (listofSectionsFieldsAdapter != null)
            listofSectionsFieldsAdapter.notifyItemChanged(field.getUpdatePositionAttachment());

        if (field.isTigger())
            callService();


    }


    public void UpdateLoadImageForField(ESP_LIB_DynamicFormSectionFieldDAO field, Uri uri) {
        if (field != null) {

            MultipartBody.Part body = null;

            try {

                body = ESP_LIB_Shared.getInstance().prepareFilePart(uri, context);
                UpLoadFile(field, body, uri);

            } catch (Exception e) {
                ESP_LIB_Shared.getInstance().errorLogWrite("FILE", e.getMessage());
            }

        }
    }

    private void UpLoadFile(final ESP_LIB_DynamicFormSectionFieldDAO field, final MultipartBody.Part body, final Uri uri) {

        start_loading_animation();

        try {

            /* APIs Mapping respective Object*/
            ESP_LIB_APIs apis = ESP_LIB_Shared.getInstance().retroFitObject(context);

            Call<ESP_LIB_ResponseFileUploadDAO> call_upload = apis.upload(body);
            call_upload.enqueue(new Callback<ESP_LIB_ResponseFileUploadDAO>() {
                @Override
                public void onResponse(Call<ESP_LIB_ResponseFileUploadDAO> call, Response<ESP_LIB_ResponseFileUploadDAO> response) {
                    stop_loading_animation();
                    if (response != null && response.body() != null) {

                        if (field != null) {

                            try {

                                File file = null;
                                String path = ESP_LIB_RealPathUtil.getPath(context, uri);
                                file = new File(path);


                                String attachmentFileSize = ESP_LIB_Shared.getInstance().getAttachmentFileSize(file);
                                ESP_LIB_DyanmicFormSectionFieldDetailsDAO detail = new ESP_LIB_DyanmicFormSectionFieldDetailsDAO();
                                detail.setName(file.getName());
                                detail.setDownloadUrl(response.body().getDownloadUrl());
                                detail.setMimeType(FileUtils.getMimeType(file));
                                detail.setCreatedOn(ESP_LIB_Shared.getInstance().GetCurrentDateTime());
                                detail.setFileSize(attachmentFileSize);
                                field.setDetails(detail);

                                field.setValue(response.body().getFileId());


                                if (listofSectionsFieldsAdapter != null)
                                    listofSectionsFieldsAdapter.notifyItemChanged(fieldToBeUpdated.getUpdatePositionAttachment());

                                if (field.isTigger())
                                    callService();

                                ESP_LIB_CustomLogs.displayLogs(TAG + " attachment id: " + field.getId());
                            } catch (Exception e) {
                            }


                        }

                    } else {
                        ESP_LIB_Shared.getInstance().showAlertMessage(getString(R.string.esp_lib_text_error), getString(R.string.esp_lib_text_some_thing_went_wrong), context);
                    }

                }

                @Override
                public void onFailure(Call<ESP_LIB_ResponseFileUploadDAO> call, Throwable t) {
                    stop_loading_animation();
                    ESP_LIB_Shared.getInstance().showAlertMessage(getString(R.string.esp_lib_text_error), getString(R.string.esp_lib_text_some_thing_went_wrong), context);
                    // UploadFileInformation(fileDAO);
                }
            });

        } catch (Exception ex) {
            ex.printStackTrace();
            stop_loading_animation();
            ESP_LIB_Shared.getInstance().showAlertMessage(getString(R.string.esp_lib_text_error), getString(R.string.esp_lib_text_some_thing_went_wrong), context);
        }
    }//LoggedInUser end


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {

            if (REQUEST_CHOOSER == requestCode && data != null) {
                final Uri uri = data.getData();

                if (uri != null) {
                    int getMaxVal = fieldToBeUpdated.getMaxVal();
                    boolean isFileSizeValid = true;
                    if (getMaxVal > 0)
                        isFileSizeValid = ESP_LIB_Shared.getInstance().getFileSize(ESP_LIB_RealPathUtil.getPath(context, uri), getMaxVal);

                    if (isFileSizeValid) {

                        try {
                            UpdateLoadImageForField(fieldToBeUpdated, uri);

                        } catch (Exception e) {
                            ESP_LIB_Shared.getInstance().messageBox(getString(R.string.esp_lib_text_pleasetryagain), this);
                        }
                    } else {

                        ESP_LIB_Shared.getInstance().showAlertMessage("", getString(R.string.esp_lib_text_sizeshouldbelessthen) + " " + getMaxVal + " " + getString(R.string.esp_lib_text_mb), context);
                    }
                }

            } else if (REQUEST_LOOKUP == requestCode && data != null) {
                ESP_LIB_LookUpDAO lookup = (ESP_LIB_LookUpDAO) data.getExtras().getSerializable(ESP_LIB_LookUpDAO.Companion.getBUNDLE_KEY());
                if (fieldToBeUpdated != null && lookup != null) {
                    SetUpLookUpValues(fieldToBeUpdated, lookup);
                }

            }

        }

    }


    private void start_loading_animation() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void stop_loading_animation() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    public void postBasicData() {

        ESP_LIB_BasicDAO ESPLIBBasicDAO = new ESP_LIB_BasicDAO();
        ESPLIBBasicDAO.setName(basicName);

        start_loading_animation();
        try {
            final ESP_LIB_APIs apis = ESP_LIB_Shared.getInstance().retroFitObject(context);

            Call<ESP_LIB_BasicDAO> status_call;
            status_call = apis.saveBasicData(ESPLIBBasicDAO);


            status_call.enqueue(new Callback<ESP_LIB_BasicDAO>() {
                @Override
                public void onResponse(Call<ESP_LIB_BasicDAO> call, Response<ESP_LIB_BasicDAO> response) {
                    ESP_LIB_ESPApplication.getInstance().getUser().getLoginResponse().setName(basicName);
                    postSectionData();


                }

                @Override
                public void onFailure(Call<ESP_LIB_BasicDAO> call, Throwable t) {
                    stop_loading_animation();
                    if (t != null && getBContext() != null) {
                        ESP_LIB_Shared.getInstance().showAlertMessage(pref.getlabels().getApplication(), getString(R.string.esp_lib_text_some_thing_went_wrong), context);
                    }
                }
            });

        } catch (Exception ex) {
            if (ex != null) {
                stop_loading_animation();
                if (ex != null && getBContext() != null) {
                    ESP_LIB_Shared.getInstance().showAlertMessage(pref.getlabels().getApplication(), getString(R.string.esp_lib_text_some_thing_went_wrong), context);
                }
            }
        }
    }

   /* public void postData(ApplicationProfileDAO.Applicant post) {


        start_loading_animation();
        try {
            final APIs apis = Shared.getInstance().retroFitObject(context);

            Call<Integer> status_call;
            status_call = apis.saveApplicantData(post);


            status_call.enqueue(new Callback<Integer>() {
                @Override
                public void onResponse(Call<Integer> call, Response<Integer> response) {
                    stop_loading_animation();
                    CustomLogs.displayLogs(TAG + " postData success: " + response.body());
                    Shared.getInstance().callIntentClearAllActivities(ApplicationsActivityDrawer.class, getBContext(), null);

                }

                @Override
                public void onFailure(Call<Integer> call, Throwable t) {
                    t.printStackTrace();
                    stop_loading_animation();
                    if (t != null && getBContext() != null) {
                        Shared.getInstance().showAlertMessage(pref.getlabels().getApplication(), getString(R.string.some_thing_went_wrong), context);
                    }
                }
            });

        } catch (Exception ex) {
            if (ex != null) {
                stop_loading_animation();
                if (ex != null && getBContext() != null) {
                    Shared.getInstance().showAlertMessage(pref.getlabels().getApplication(), getString(R.string.some_thing_went_wrong), context);
                }
            }
        }
    }*/

    public void getApplicant() {

        try {

            start_loading_animation();

            final ESP_LIB_APIs apis = ESP_LIB_Shared.getInstance().retroFitObject(context);


            Call<ESP_LIB_ApplicationProfileDAO> labels_call = apis.Getapplicant();

            labels_call.enqueue(new Callback<ESP_LIB_ApplicationProfileDAO>() {
                @Override
                public void onResponse(Call<ESP_LIB_ApplicationProfileDAO> call, Response<ESP_LIB_ApplicationProfileDAO> response) {
                    stop_loading_animation();
                    ESP_LIB_ApplicationProfileDAO body = response.body();
                    Intent mainIntent = new Intent(getBContext(), ESP_LIB_ProfileMainActivity.class);
                    mainIntent.putExtra("dataapplicant", body);
                    mainIntent.putExtra("ischeckerror", true);
                    mainIntent.putExtra("isprofile", true);
                    startActivity(mainIntent);
                    finish();

                }

                @Override
                public void onFailure(Call<ESP_LIB_ApplicationProfileDAO> call, Throwable t) {
                    stop_loading_animation();
                    t.printStackTrace();
                    ESP_LIB_Shared.getInstance().messageBox(getString(R.string.esp_lib_text_some_thing_went_wrong), getBContext());
                }
            });


        } catch (Exception ex) {
            ex.printStackTrace();
            stop_loading_animation();
            ESP_LIB_Shared.getInstance().messageBox(getString(R.string.esp_lib_text_some_thing_went_wrong), getBContext());
        }
    }

    public void postUpdatedData(ArrayList<ESP_LIB_ApplicationProfileDAO.Values> post, boolean isUpdateSection,
                                boolean isMoveToMainScreen) {


        start_loading_animation();
        try {
            final ESP_LIB_APIs apis = ESP_LIB_Shared.getInstance().retroFitObject(context);

            if (isUpdateSection) {
                int index = getIntent().getIntExtra("position", -1); // coming from EditSectionDetail.class use for updating mulisection index
                if (index == -1)
                    index = 0;
                Call<ESP_LIB_ApplicationProfileDAO.Values> status_call_Update = apis.updateApplicantDataBySectionId(ESPLIBDynamicFormSectionDAO.getId(), index, post);

                status_call_Update.enqueue(new Callback<ESP_LIB_ApplicationProfileDAO.Values>() {
                    @Override
                    public void onResponse(Call<ESP_LIB_ApplicationProfileDAO.Values> call, Response<ESP_LIB_ApplicationProfileDAO.Values> response) {
                        // stop_loading_animation();
                        ESP_LIB_CustomLogs.displayLogs(TAG + " updateApplicantDataBySectionId success: " + response.body());
                        //Shared.getInstance().callIntentClearAllActivities(ApplicationsActivityDrawer.ACTIVITY_NAME, bContext, null);

                        if (isMoveToMainScreen) {
                            stop_loading_animation();
                            ESP_LIB_Shared.getInstance().callIntentClearAllActivities(ESP_LIB_ApplicationsActivityDrawer.class, getBContext(), null);
                        } else
                            getApplicant();
                    }

                    @Override
                    public void onFailure(Call<ESP_LIB_ApplicationProfileDAO.Values> call, Throwable t) {
                        stop_loading_animation();
                        t.printStackTrace();
                        if (t != null && getBContext() != null) {
                            ESP_LIB_Shared.getInstance().showAlertMessage(pref.getlabels().getApplication(), getString(R.string.esp_lib_text_some_thing_went_wrong), context);
                        }
                    }
                });

            } else {
                Call<Integer> status_call_post = apis.saveApplicantDataBySectionId(ESPLIBDynamicFormSectionDAO.getId(), post);

                status_call_post.enqueue(new Callback<Integer>() {
                    @Override
                    public void onResponse(Call<Integer> call, Response<Integer> response) {
                        //  stop_loading_animation();
                        // Shared.getInstance().callIntentClearAllActivities(ApplicationsActivityDrawer.ACTIVITY_NAME, bContext, null);

                        if (getIntent().getBooleanExtra("isprofile", false))
                            getApplicant();
                        else {
                            stop_loading_animation();
                            ESP_LIB_Shared.getInstance().callIntentClearAllActivities(ESP_LIB_ApplicationsActivityDrawer.class, getBContext(), null);
                        }

                        ESP_LIB_CustomLogs.displayLogs(TAG + " saveApplicantDataBySectionId success: " + response.body());
                    }

                    @Override
                    public void onFailure(Call<Integer> call, Throwable t) {
                        stop_loading_animation();
                        t.printStackTrace();
                        if (t != null && getBContext() != null) {
                            ESP_LIB_Shared.getInstance().showAlertMessage(pref.getlabels().getApplication(), getString(R.string.esp_lib_text_some_thing_went_wrong), context);
                        }
                    }
                });
            }


        } catch (Exception ex) {

            ex.printStackTrace();
            stop_loading_animation();
            if (getBContext() != null) {
                ESP_LIB_Shared.getInstance().showAlertMessage(pref.getlabels().getApplication(), getString(R.string.esp_lib_text_some_thing_went_wrong), context);
            }

        }
    }


    /*public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            *//*DynamicFormSectionFieldDAO dynamicFormSectionFieldDAO = (DynamicFormSectionFieldDAO) intent.getSerializableExtra("dynamicFormSectionFieldDAO");
            CustomLogs.displayLogs(TAG + " BroadcastReceiver sectionCustomFieldId: " + dynamicFormSectionFieldDAO.getSectionCustomFieldId() + " " +
                    dynamicFormSectionFieldDAO.getValue());*//*
            callService();
        }
    };

    private void callService() {

        if (!isServiceRunning) {
            unRegisterReciever();
            isServiceRunning = true;
            getRealTimeValues();
        }
    }
*/
    private void registerReciever() {
       /* isServiceRunning = false;
        LocalBroadcastManager.getInstance(context).registerReceiver(mMessageReceiver,
                new IntentFilter("getcalculatedvalues"));*/
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }

    private void unRegisterReciever() {
        //   LocalBroadcastManager.getInstance(context).unregisterReceiver(mMessageReceiver);
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unRegisterReciever();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReciever();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void dataRefreshEvent(EventOptions.EventTriggerController eventTriggerController) {
        callService();
    }

    private void callService() {
        unRegisterReciever();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getRealTimeValues();
            }
        }, 1000);
    }

    public void getRealTimeValues() {
        try {
            if (isKeyboardVisible)
                isCalculatedField = true;

            //   start_loading_animation();
            List<ESP_LIB_RealTimeValuesDAO> ESPLIBRealTimeValuesDAOList = new ArrayList<>();

            if (ESPLIBDynamicFormSectionDAO.getFields() != null) {
                for (int i = 0; i < ESPLIBDynamicFormSectionDAO.getFields().size(); i++) {
                    ESP_LIB_DynamicFormSectionFieldDAO fieldDAO = ESPLIBDynamicFormSectionDAO.getFields().get(i);

                    if (fieldDAO.getType() == 11) {
                        String finalValue = fieldDAO.getValue();
                        if (finalValue != null && !finalValue.isEmpty())
                            finalValue += ":" + fieldDAO.getSelectedCurrencyId() + ":" + fieldDAO.getSelectedCurrencySymbol();

                        fieldDAO.setValue(finalValue);
                    }

                    ESP_LIB_RealTimeValuesDAO ESPLIBRealTimeValuesDAO = new ESP_LIB_RealTimeValuesDAO();
                    ESPLIBRealTimeValuesDAO.setSectionFieldId(fieldDAO.getSectionCustomFieldId());
                    ESPLIBRealTimeValuesDAO.setValue(fieldDAO.getValue());
                    ESPLIBRealTimeValuesDAOList.add(ESPLIBRealTimeValuesDAO);
                }
            }


            Call<List<ESP_LIB_CalculatedMappedFieldsDAO>> submit_call = ESP_LIB_Shared.getInstance().retroFitObject(context).getRealTimeValues(ESPLIBDynamicFormSectionDAO.getId(), ESPLIBRealTimeValuesDAOList);

            submit_call.enqueue(new Callback<List<ESP_LIB_CalculatedMappedFieldsDAO>>() {
                @Override
                public void onResponse(Call<List<ESP_LIB_CalculatedMappedFieldsDAO>> call, Response<List<ESP_LIB_CalculatedMappedFieldsDAO>> response) {

                    if (response != null && response.body() != null) {
                        //  List<DynamicSectionValuesDAO> sectionValuesListToPost = new ArrayList<>();
                        List<ESP_LIB_CalculatedMappedFieldsDAO> calculatedMappedFieldsList = response.body();

                        for (int i = 0; i < calculatedMappedFieldsList.size(); i++) {
                            ESP_LIB_CalculatedMappedFieldsDAO ESPLIBCalculatedMappedFieldsDAO = calculatedMappedFieldsList.get(i);
                            List<ESP_LIB_ApplicationProfileDAO.ApplicationSection> applicantSectionsList = dataapplicant.getApplicant().getApplicantSections();

                            if (applicantSectionsList != null) {
                                for (int u = 0; u < applicantSectionsList.size(); u++) {
                                    //    if (dynamicFormSectionDAO.getFieldsCardsList().size() > 0) {
                                    //For Setting InstancesList
                                    //   for (DynamicFormSectionFieldsCardsDAO dynamicFormSectionFieldsCardsDAO : updatedSection.getFieldsCardsList()) {
                                    //  for (int g = 0; g < dynamicFormSectionDAO.getFieldsCardsList().size(); g++) {

                                    //   if (calculatedMappedFieldsDAO.getSectionIndex() == g) {
                                    //     DynamicFormSectionFieldsCardsDAO dynamicFormSectionFieldsCardsDAO = dynamicFormSectionDAO.getFieldsCardsList().get(g);
                                    if (ESPLIBDynamicFormSectionDAO.getFields() != null) {
                                        for (int p = 0; p < ESPLIBDynamicFormSectionDAO.getFields().size(); p++) {
                                            ESP_LIB_DynamicFormSectionFieldDAO ESPLIBDynamicFormSectionFieldDAO = ESPLIBDynamicFormSectionDAO.getFields().get(p);

                                            if (ESPLIBDynamicFormSectionFieldDAO.getSectionCustomFieldId() == ESPLIBCalculatedMappedFieldsDAO.getSectionCustomFieldId()) {
                                                int targetFieldType = ESPLIBCalculatedMappedFieldsDAO.getTargetFieldType();

                                                if (targetFieldType == 13) {
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

                                                if (targetFieldType == 7 || targetFieldType == 15) {

                                                    ESPLIBDynamicFormSectionFieldDAO.setMappedCalculatedField(true);
                                                    ESPLIBDynamicFormSectionFieldDAO.setType(ESPLIBCalculatedMappedFieldsDAO.getTargetFieldType());
                                                    ESPLIBDynamicFormSectionFieldDAO.setValue(ESPLIBCalculatedMappedFieldsDAO.getValue());
                                                    String attachmentFileSize = "";
                                                    if (ESPLIBCalculatedMappedFieldsDAO.getDetails() != null) {
                                                        String getOutputMediaFile = ESP_LIB_Shared.getInstance().getOutputMediaFile(ESPLIBCalculatedMappedFieldsDAO.getDetails().getName()).getPath();
                                                        boolean isFileExist = ESP_LIB_Shared.getInstance().isFileExist(getOutputMediaFile, context);
                                                        if (isFileExist) {
                                                            File file = null;
                                                            file = new File(getOutputMediaFile);
                                                            attachmentFileSize = ESP_LIB_Shared.getInstance().getAttachmentFileSize(file);
                                                        }

                                                        ESPLIBCalculatedMappedFieldsDAO.getDetails().setFileSize(attachmentFileSize);
                                                    }
                                                    ESPLIBDynamicFormSectionFieldDAO.setDetails(ESPLIBCalculatedMappedFieldsDAO.getDetails());
                                                } else if (targetFieldType == 4) {
                                                    String calculatedDisplayDate = ESP_LIB_Shared.getInstance().getDisplayDate(context, ESPLIBCalculatedMappedFieldsDAO.getValue(), false);
                                                    ESPLIBDynamicFormSectionFieldDAO.setValue(calculatedDisplayDate);


                                                } else if (targetFieldType == 11) {

                                                    ESP_LIB_DynamicFormSectionFieldDAO fieldDAO = ESP_LIB_Shared.getInstance().populateCurrency(ESPLIBCalculatedMappedFieldsDAO.getValue());
                                                    //  CurrencyDAO currencyById = Shared.getInstance().getCurrencyById(Integer.valueOf(calculatedMappedFieldsDAO.getValue()));
                                                    String concateValue = fieldDAO.getValue() + " " + fieldDAO.getSelectedCurrencySymbol();
                                                    // String concateValue = fieldDAO.getValue() + " " + currencyById.getSymobl();
                                                    ESPLIBDynamicFormSectionFieldDAO.setValue(concateValue);


                                                } else
                                                    ESPLIBDynamicFormSectionFieldDAO.setValue(ESPLIBCalculatedMappedFieldsDAO.getValue());


                                            }

                                        }
                                    }
                                    //    }


                                    //  }
                                    //   }


                                }
                            }

                        }
                       /* if (listofSectionsFieldsAdapter != null)
                            listofSectionsFieldsAdapter.notifyDataSetChanged();*/

                        List<ESP_LIB_DynamicFormSectionFieldDAO> fields = ESPLIBDynamicFormSectionDAO.getFields();
                        listofSectionsFieldsAdapter = new ESP_LIB_ListofSectionsFieldsAdapter(fields, context, ischeckerror, false);
                        listofSectionsFieldsAdapter.getListenerContext(ESP_LIB_EditSectionDetails.this);
                        rvFields.setAdapter(listofSectionsFieldsAdapter);

                        /*View v = getCurrentFocus();
                        if (v != null) {
                            v.clearFocus();
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        }*/

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
                        // stop_loading_animation();
                        ESP_LIB_CustomLogs.displayLogs(TAG + " null response");
                        registerReciever();
                        if (getBContext() != null)
                            ESP_LIB_Shared.getInstance().messageBox(getString(R.string.esp_lib_text_some_thing_went_wrong), getBContext());
                    }


                }

                @Override
                public void onFailure(Call<List<ESP_LIB_CalculatedMappedFieldsDAO>> call, Throwable t) {
                    //  stop_loading_animation();
                    ESP_LIB_CustomLogs.displayLogs(TAG + " failure response");
                    registerReciever();
                    if (getBContext() != null)
                        ESP_LIB_Shared.getInstance().messageBox(getString(R.string.esp_lib_text_some_thing_went_wrong), getBContext());
                }
            });

        } catch (Exception ex) {
            ex.printStackTrace();
            // stop_loading_animation();
            registerReciever();
            if (getBContext() != null)
                ESP_LIB_Shared.getInstance().messageBox(getString(R.string.esp_lib_text_some_thing_went_wrong), getBContext());
        }
    }//LoggedInUser end

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    if (isCalculatedField) {
                        isCalculatedField = false;
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (listofSectionsFieldsAdapter != null && isCalculatedField)
                                    listofSectionsFieldsAdapter.notifyDataSetChanged();
                            }
                        }, 500);
                    }
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }
}
