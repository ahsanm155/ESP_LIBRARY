package com.esp.library.exceedersesp.controllers.applications;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.esp.library.R;
import com.esp.library.exceedersesp.ESP_LIB_BaseActivity;
import com.esp.library.exceedersesp.ESP_LIB_ESPApplication;
import com.esp.library.exceedersesp.SingleController.CompRoot;
import com.esp.library.exceedersesp.controllers.feedback.ESP_LIB_ApplicationFeedbackAdapter;
import com.esp.library.exceedersesp.controllers.feedback.ESP_LIB_FeedbackForm;
import com.esp.library.ipaulpro.afilechooser.utils.FileUtils;
import com.esp.library.utilities.common.ESP_LIB_AlertActionWindow;
import com.esp.library.utilities.common.ESP_LIB_Constants;
import com.esp.library.utilities.common.ESP_LIB_CustomLogs;
import com.esp.library.utilities.common.ESP_LIB_EndlessRecyclerViewScrollListener;
import com.esp.library.utilities.common.ESP_LIB_Enums;
import com.esp.library.utilities.common.ESP_LIB_KeyboardUtils;
import com.esp.library.utilities.common.ESP_LIB_ProgressBarAnimation;
import com.esp.library.utilities.common.ESP_LIB_RealPathUtil;
import com.esp.library.utilities.common.ESP_LIB_Shared;
import com.esp.library.utilities.common.ESP_LIB_SharedPreference;
import com.esp.library.utilities.customevents.EventOptions;
import com.esp.library.utilities.data.applicants.ESP_LIB_CancelApplicationDAO;
import com.esp.library.utilities.data.applicants.signature.ESP_LIB_SignatureDAO;
import com.esp.library.utilities.setup.applications.ESP_LIB_ApplicationFieldsRecyclerAdapter;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import utilities.adapters.setup.applications.ESP_LIB_ApplicationStagesAdapter;
import utilities.adapters.setup.applications.ESP_LIB_ListAddApplicationSectionsAdapter;
import utilities.adapters.setup.applications.ESP_LIB_ListSubmissionApplicationsAdapter;
import utilities.adapters.setup.applications.ESP_LIB_ListUsersApplicationsAdapter;
import utilities.data.CriteriaRejectionfeedback.ESP_LIB_FeedbackDAO;
import utilities.data.apis.ESP_LIB_APIs;
import utilities.data.applicants.ESP_LIB_ApplicationSingleton;
import utilities.data.applicants.ESP_LIB_ApplicationsDAO;
import utilities.data.applicants.ESP_LIB_CalculatedMappedFieldsDAO;
import utilities.data.applicants.ESP_LIB_LinkApplicationsDAO;
import utilities.data.applicants.ESP_LIB_ResponseApplicationsDAO;
import utilities.data.applicants.addapplication.ESP_LIB_CurrencyDAO;
import utilities.data.applicants.addapplication.ESP_LIB_LookUpDAO;
import utilities.data.applicants.addapplication.ESP_LIB_PostApplicationsStatusDAO;
import utilities.data.applicants.addapplication.ESP_LIB_ResponseFileUploadDAO;
import utilities.data.applicants.dynamics.ESP_LIB_DyanmicFormSectionFieldDetailsDAO;
import utilities.data.applicants.dynamics.ESP_LIB_DynamicFormDAO;
import utilities.data.applicants.dynamics.ESP_LIB_DynamicFormSectionDAO;
import utilities.data.applicants.dynamics.ESP_LIB_DynamicFormSectionFieldDAO;
import utilities.data.applicants.dynamics.ESP_LIB_DynamicFormSectionFieldsCardsDAO;
import utilities.data.applicants.dynamics.ESP_LIB_DynamicFormValuesDAO;
import utilities.data.applicants.dynamics.ESP_LIB_DynamicResponseDAO;
import utilities.data.applicants.dynamics.ESP_LIB_DynamicSectionValuesDAO;
import utilities.data.applicants.dynamics.ESP_LIB_DynamicStagesCriteriaListDAO;
import utilities.data.applicants.dynamics.ESP_LIB_DynamicStagesDAO;
import utilities.data.applicants.feedback.ESP_LIB_ApplicationsFeedbackAttachmentsDAO;
import utilities.data.applicants.feedback.ESP_LIB_ApplicationsFeedbackDAO;
import utilities.data.filters.ESP_LIB_FilterDAO;
import utilities.interfaces.ESP_LIB_CriteriaFieldsListener;
import utilities.interfaces.ESP_LIB_FeedbackSubmissionClick;


public class ESP_LIB_ApplicationDetailScreenActivity extends ESP_LIB_BaseActivity implements ESP_LIB_ApplicationFieldsRecyclerAdapter.ApplicationDetailFieldsAdapterListener,
        ESP_LIB_AlertActionWindow.ActionInterface, ESP_LIB_FeedbackSubmissionClick, ESP_LIB_CriteriaFieldsListener {

    public static String ACTIVITY_NAME = "controllers.applications.ApplicationDetailScreenActivity";
    ESP_LIB_BaseActivity bContext;

    ESP_LIB_ProgressBarAnimation anim = null;
    ESP_LIB_ApplicationsDAO mApplication;
    private ESP_LIB_ListAddApplicationSectionsAdapter mApplicationSectionsAdapter;
    int statusId;
    ESP_LIB_DynamicFormSectionFieldDAO fieldToBeUpdated = null;
    private static final int REQUEST_CHOOSER = 12345;
    private static final int REQUEST_LOOKUP = 2;
    String actualResponseJson;
    ESP_LIB_SharedPreference pref;
    boolean isComingFromService, isClosingDatePassed;


    Toolbar toolbar;
    TextView categoryy;
    TextView toolbarheading;
    ImageButton ibToolbarBack;
    TextView definitionName;
    TextView txtrequestedbyval;
    TextView txtrequestedonval;
    TextView applicationNumber;
    LinearLayout rejected_status;
    Button submit_btn;
    RecyclerView app_list;
    RecyclerView status_list;
    TextView txtrequest;
    TextView txtrequestedby;
    TextView acceptedOn;
    TextView acceptedontext;
    RelativeLayout rlaccepreject;
    TextView categorytext;
    TextView heading;
    View linkdefinitioncard;
    SwitchCompat switchsubmissionallow;
    TextView txtmoreinfo;
    ImageView ivainforrow;
    RelativeLayout rldescription;
    TextView txtinfodescription;
    View pendinglineview;
    TextView txtrequestdetail;
    LinearLayout lldetail;
    TextView txtfeedback;
    ImageView ivnorecord;
    TextView txtfeedbacknorecord;
    LinearLayout llrows;
    RelativeLayout rldetailrow;
    RelativeLayout rlsubmissionrow;
    TextView txtsubmissions;
    LinearLayout llmaindetail;
    View topcardview;
    RelativeLayout main_content;
    RelativeLayout rlpendingfor;
    TextView pendingfor;
    Spinner spcloserequest;
    RecyclerView rvStagesFieldsCards;
    LinearLayout llstages;
    TextView txtapprovalStages;
    TextView status;
    View llstatuslayout;
    ImageView ivsign;
    RelativeLayout rlstatus;
    NestedScrollView nestedscrollview;
    ShimmerFrameLayout shimmer_view_container;
    ImageView ivdetailarrow, ivsubmissionrowarrow;
    SwipeRefreshLayout swipeRefreshLayout;
    // View vsperator;
    LinearLayout lldraftcard;
    TextView definitionNameTitle;
    TextView definitionDescription;
    TextView txtdetailrowtext;
    LinearLayout llnofeedbackrecord;
    RecyclerView rvFeedbackCommentsList;
    View llfeedback;
    TextView submissionallowedtext;
    RelativeLayout rlcategory;

    RecyclerView submission_app_list;
    LinearLayout llsubmission_app_list;
    public boolean isCalculatedField = false;
    public boolean isKeyboardVisible = false;
    public static boolean criteriaWasLoaded = false;
    AlertDialog pDialog;
    boolean isAddStages = false;
    boolean isResubmit, isSubApplications, isComingfromAssessor;
    ESP_LIB_ApplicationStagesAdapter ESPLIBApplicationStagesAdapter;
    boolean isServiceRunning, isNotified;

    ArrayList<ESP_LIB_ApplicationsDAO> app_actual_list = new ArrayList<>();
    ESP_LIB_DynamicResponseDAO actual_response = null;

    Call<List<ESP_LIB_CalculatedMappedFieldsDAO>> calculatedValues_call = null;
    Call<ESP_LIB_DynamicResponseDAO> applicationDetail_call = null;
    Call<ESP_LIB_LinkApplicationsDAO> linkApplication_call = null;
    Call<List<ESP_LIB_ApplicationsFeedbackDAO>> applicationFeedback_call = null;
    Call<ESP_LIB_LinkApplicationsDAO> postLinkDefinition = null;
    Call<List<ESP_LIB_CurrencyDAO>> loadStages_call = null;
    Call<ESP_LIB_ResponseFileUploadDAO> uploadFile_call = null;
    Call<Integer> submitForm_call = null;
    Call<Integer> StageFeedbackSubmit_call = null;


    RecyclerView.LayoutManager mApplicationSubmissionLayoutManager;
    int PAGE_NO = 1;
    int PER_PAGE_RECORDS = 12;
    int IN_LIST_RECORDS = 0;
    int SCROLL_TO = 0;
    int TOTAL_RECORDS_AVAILABLE = 0;

    @Override
    public void mActionTo(String whattodo) {
        if (whattodo.equals(getString(R.string.esp_lib_text_draft))) {
            SubmitRequest(getString(R.string.esp_lib_text_draft));
        } else {
            finish();
        }
    }

    private void AddScroller() {

        submission_app_list.addOnScrollListener(new ESP_LIB_EndlessRecyclerViewScrollListener((LinearLayoutManager) mApplicationSubmissionLayoutManager) {
            @Override
            public void onHide() {

            }

            @Override
            public void onShow() {

            }

            @Override
            public int getFooterViewType(int defaultNoFooterViewType) {
                return 0;
            }

            @Override
            public void onLoadMore(int page, int totalItemsCount) {

                if (IN_LIST_RECORDS < TOTAL_RECORDS_AVAILABLE) {
                    getSubmissions(true);
                }
            }
        });


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        changeStatusBarColor(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.esp_lib_activity_application_detail);
        initailizeIds();
        initailize();
        setGravity();
        loadData();


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                PAGE_NO = 1;
                PER_PAGE_RECORDS = 12;
                IN_LIST_RECORDS = 0;
                TOTAL_RECORDS_AVAILABLE = 0;
                app_actual_list.clear();
                loadData();
            }
        });


        txtfeedback.setText(pref.getlabels().getApplication() + " " + getString(R.string.esp_lib_text_feedback));
        Bundle bnd = getIntent().getExtras();
        if (bnd != null) {
            mApplication = (ESP_LIB_ApplicationsDAO) bnd.getSerializable(ESP_LIB_ApplicationsDAO.Companion.getBUNDLE_KEY());
            statusId = bnd.getInt("statusId");
            isResubmit = bnd.getBoolean("isResubmit");
            isSubApplications = bnd.getBoolean("isSubApplications");
            isComingfromAssessor = bnd.getBoolean("isComingfromAssessor");


            UpdateTopView();


        }

        // in case of draft we do below work

        if ((statusId == 1 || isResubmit) && !isComingfromAssessor) // 1= New or draft
        {
            if (mApplication != null) {
                definitionNameTitle.setText(mApplication.getDefinitionName());

            }

            rldetailrow.setVisibility(View.GONE);
            llfeedback.setVisibility(View.GONE);
            lldraftcard.setVisibility(View.VISIBLE);
            llmaindetail.setVisibility(View.VISIBLE);
            lldetail.setVisibility(View.GONE);
            topcardview.setVisibility(View.GONE);
            rlsubmissionrow.setVisibility(View.GONE);
            // vsperator.setVisibility(View.GONE);
            //  main_content.setBackgroundColor(ContextCompat.getColor(bContext, R.color.esp_lib_color_white));
        }

        definitionDescription.setOnClickListener(v -> {
            if (definitionDescription.getMaxLines() == 3)
                definitionDescription.setMaxLines(100);
            else
                definitionDescription.setMaxLines(3);
        });


        try {
            ESP_LIB_Shared.getInstance().createFolder(ESP_LIB_Constants.FOLDER_PATH, ESP_LIB_Constants.FOLDER_NAME, bContext);
        } catch (Exception e) {
            e.printStackTrace();
        }

        submit_btn.setOnClickListener(v -> {
            SubmitRequest(getString(R.string.esp_lib_text_submit));
        });

        rldetailrow.setOnClickListener(v -> {
            //  llrows.setVisibility(View.GONE);

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    detailClick();
                }
            }, 300);


        });
        rlsubmissionrow.setOnClickListener(v -> {
           /* ESP_LIB_DynamicResponseDAO ESPLIBDynamicResponseDAO = new Gson().fromJson(actualResponseJson, ESP_LIB_DynamicResponseDAO.class);
            Intent i = new Intent(bContext, ESP_LIB_UserSubApplicationsActivity.class);
            i.putExtra("dynamicResponseDAO", ESPLIBDynamicResponseDAO);
            startActivity(i);*/

            if (llsubmission_app_list.getVisibility() == View.VISIBLE) {
                llsubmission_app_list.setVisibility(View.GONE);
                ivsubmissionrowarrow.setImageResource(R.drawable.ic_arrow_down);
            } else {
                ivsubmissionrowarrow.setImageResource(R.drawable.ic_arrow_up);
                llsubmission_app_list.setVisibility(View.VISIBLE);
            }

        });
        txtmoreinfo.setOnClickListener(v ->
        {
            if (txtmoreinfo.getText().toString().equalsIgnoreCase(getString(R.string.esp_lib_text_moreinformation))) {
                rldescription.setVisibility(View.VISIBLE);
                pendinglineview.setVisibility(View.VISIBLE);
                txtmoreinfo.setText(getString(R.string.esp_lib_text_lessinformation));
                ivainforrow.setImageResource(R.drawable.esp_lib_drawable_icons_blue_arrow_up);

            } else {

                rldescription.setVisibility(View.GONE);
                pendinglineview.setVisibility(View.GONE);
                txtmoreinfo.setText(getString(R.string.esp_lib_text_moreinformation));
                ivainforrow.setImageResource(R.drawable.esp_lib_drawable_iconsarrowdownblue);

            }

        });


        ESP_LIB_KeyboardUtils.addKeyboardToggleListener(this, new ESP_LIB_KeyboardUtils.SoftKeyboardToggleListener() {
            @Override
            public void onToggleSoftKeyboard(boolean isVisible) {
                isNotified = isVisible;
                isKeyboardVisible = isVisible;
            }
        });


        switchsubmissionallow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isComingFromService) {
                    if (isChecked)
                        submissionDialog(getString(R.string.esp_lib_text_allowsubmissiontext), getString(R.string.esp_lib_text_allowsubmission), getString(R.string.esp_lib_text_allowsubmission));
                    else
                        submissionDialog(getString(R.string.esp_lib_text_stopsubmissiontext), getString(R.string.esp_lib_text_stopsubmission), getString(R.string.esp_lib_text_stopsubmission));
                } else
                    isComingFromService = false;
            }
        });


    }

    private void initailizeIds() {

        toolbar = findViewById(R.id.gradienttoolbar);
        categoryy = findViewById(R.id.category);
        toolbarheading = findViewById(R.id.toolbarheading);
        ibToolbarBack = findViewById(R.id.ibToolbarBack);
        definitionName = findViewById(R.id.definitionName);
        submission_app_list = findViewById(R.id.submission_app_list);
        llsubmission_app_list = findViewById(R.id.llsubmission_app_list);
        ivsign = findViewById(R.id.ivsign);
        txtrequestedbyval = findViewById(R.id.txtrequestedbyval);
        txtrequestedonval = findViewById(R.id.txtrequestedonval);
        acceptedOn = findViewById(R.id.acceptedOn);
        acceptedontext = findViewById(R.id.acceptedontext);
        rlaccepreject = findViewById(R.id.rlaccepreject);
        applicationNumber = findViewById(R.id.applicationNumber);
        rejected_status = findViewById(R.id.rejected_status);
        submit_btn = findViewById(R.id.submit_btn);
        status = findViewById(R.id.txtstatus);
        llstatuslayout = findViewById(R.id.llstatuslayout);
        rlstatus = findViewById(R.id.rlstatus);
        app_list = findViewById(R.id.app_list);
        status_list = findViewById(R.id.status_list);
        txtrequest = findViewById(R.id.applicationNumbertext);
        txtrequestedby = findViewById(R.id.txtrequestedby);
        categorytext = findViewById(R.id.categorytext);
        heading = findViewById(R.id.heading);
        linkdefinitioncard = findViewById(R.id.linkcard);
        switchsubmissionallow = findViewById(R.id.switchsubmissionallow);
        txtmoreinfo = findViewById(R.id.txtmoreinfo);
        ivainforrow = findViewById(R.id.ivainforrow);
        rldescription = findViewById(R.id.rldescription);
        txtinfodescription = findViewById(R.id.txtinfodescription);
        pendinglineview = findViewById(R.id.pendinglineview);
        txtrequestdetail = findViewById(R.id.txtrequestdetail);
        lldetail = findViewById(R.id.lldetail);
        txtfeedback = findViewById(R.id.txtfeedback);
        ivnorecord = findViewById(R.id.ivnorecord);
        txtfeedbacknorecord = findViewById(R.id.txtfeedbacknorecord);
        llrows = findViewById(R.id.llrows);
        rldetailrow = findViewById(R.id.rldetailrow);
        rlsubmissionrow = findViewById(R.id.rlsubmissionrow);
        txtsubmissions = findViewById(R.id.txtsubmissions);
        llmaindetail = findViewById(R.id.llmaindetail);
        topcardview = findViewById(R.id.topcardview);
        main_content = findViewById(R.id.main_content);
        rlpendingfor = findViewById(R.id.rlpendingfor);
        pendingfor = findViewById(R.id.pendingfor);
        spcloserequest = findViewById(R.id.spcloserequest);
        rvStagesFieldsCards = findViewById(R.id.rvStagesFieldsCards);
        llstages = findViewById(R.id.llstages);
        txtapprovalStages = findViewById(R.id.txtapprovalStages);
        nestedscrollview = findViewById(R.id.nestedscrollview);
        shimmer_view_container = findViewById(R.id.shimmer_view_container);
        ivdetailarrow = findViewById(R.id.ivdetailarrow);
        ivsubmissionrowarrow = findViewById(R.id.ivsubmissionrowarrow);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        // vsperator = findViewById(R.id.vsperator);
        lldraftcard = findViewById(R.id.lldraftcard);
        definitionNameTitle = findViewById(R.id.definitionNameTitle);
        definitionDescription = findViewById(R.id.definitionDescription);
        txtdetailrowtext = findViewById(R.id.txtdetailrowtext);
        llnofeedbackrecord = findViewById(R.id.llnofeedbackrecord);
        rvFeedbackCommentsList = findViewById(R.id.rvFeedbackCommentsList);
        llfeedback = findViewById(R.id.llfeedback);
        submissionallowedtext = findViewById(R.id.submissionallowedtext);
        rlcategory = findViewById(R.id.rlcategory);
    }

    private void initailize() {
        bContext = ESP_LIB_ApplicationDetailScreenActivity.this;
        pref = new ESP_LIB_SharedPreference(bContext);
        pDialog = ESP_LIB_Shared.getInstance().setProgressDialog(bContext);

        RecyclerView.LayoutManager mApplicationLayoutManager = new LinearLayoutManager(bContext);
        app_list.setHasFixedSize(true);
        app_list.setLayoutManager(mApplicationLayoutManager);
        app_list.setItemAnimator(new DefaultItemAnimator());

        //status_list = findViewById(R.id.status_list);
        status_list.setHasFixedSize(true);
        status_list.setLayoutManager(new LinearLayoutManager(bContext, LinearLayoutManager.HORIZONTAL, false));
        status_list.setItemAnimator(new DefaultItemAnimator());


        mApplicationSubmissionLayoutManager = new LinearLayoutManager(bContext);
        submission_app_list.setHasFixedSize(true);
        submission_app_list.setLayoutManager(mApplicationSubmissionLayoutManager);
        submission_app_list.setItemAnimator(new DefaultItemAnimator());


        RecyclerView.LayoutManager mApplicationLayoutManagerStages = new LinearLayoutManager(bContext);
        rvStagesFieldsCards.setHasFixedSize(true);
        rvStagesFieldsCards.setLayoutManager(mApplicationLayoutManagerStages);
        rvStagesFieldsCards.setItemAnimator(new DefaultItemAnimator());

        RecyclerView.LayoutManager mApplicationLayoutManagerComments = new LinearLayoutManager(bContext);
        rvFeedbackCommentsList.setHasFixedSize(true);
        rvFeedbackCommentsList.setLayoutManager(mApplicationLayoutManagerComments);
        rvFeedbackCommentsList.setItemAnimator(new DefaultItemAnimator());


        setupToolbar(toolbar);

        int themeColor = ContextCompat.getColor(bContext, R.color.colorPrimaryDark);
        swipeRefreshLayout.setColorSchemeColors(themeColor, themeColor, themeColor);


    }

    private void setCloseRequestSpinner(List<String> closeRequestList) {
        closeRequestList.add(0, getString(R.string.esp_lib_text_close_request_as));
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(bContext, R.layout.esp_lib_label_text, closeRequestList);
        spcloserequest.setSelection(0);
        spcloserequest.setAdapter(arrayAdapter);

        spcloserequest.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (closeRequestList.get(position).equalsIgnoreCase(getString(R.string.esp_lib_text_close))) {
                    submissionDialog(getString(R.string.esp_lib_text_completesubmissiontext), getString(R.string.esp_lib_text_yes), getString(R.string.esp_lib_text_closesubmission));
                } else if (closeRequestList.get(position).equalsIgnoreCase(getString(R.string.esp_lib_text_cancel))) {
                    Intent i = new Intent(bContext, ESP_LIB_CloseRequest.class);
                    i.putExtra("applicationId", mApplication.getId());
                    startActivity(i);
                }
                spcloserequest.setSelection(0);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


    }

    private void detailClick() {
        if (llmaindetail.getVisibility() == View.VISIBLE && !isSubApplications) {
            llrows.setVisibility(View.VISIBLE);
            submit_btn.setVisibility(View.GONE);


            llmaindetail.setVisibility(View.GONE);
            lldraftcard.setVisibility(View.GONE);
            if (actual_response != null && ESP_LIB_Shared.getInstance().hasLinkDefinitionId(actual_response))
                rlsubmissionrow.setVisibility(View.VISIBLE);
            ivdetailarrow.setImageResource(R.drawable.ic_arrow_down);
        } else {

            ivdetailarrow.setImageResource(R.drawable.ic_arrow_up);
            if ((statusId == 1 || isResubmit) && !isComingfromAssessor) {
                if (!isClosingDatePassed)
                    submit_btn.setVisibility(View.VISIBLE);

                lldraftcard.setVisibility(View.VISIBLE);
            }
            llmaindetail.setVisibility(View.VISIBLE);


            if (isSubApplications) {
                lldetail.setVisibility(View.VISIBLE);
            } else {
                txtrequestdetail.setVisibility(View.GONE);
                lldetail.setVisibility(View.GONE);
                ESP_LIB_ListUsersApplicationsAdapter.Companion.setSubApplications(false);
            }
        }

    }

    private void loadData() {
        if (ESP_LIB_Shared.getInstance().isWifiConnected(bContext)) {
            LoadStages();
        } else {
            ESP_LIB_Shared.getInstance().showAlertMessage(getString(R.string.esp_lib_text_internet_error_heading), getString(R.string.esp_lib_text_internet_connection_error), bContext);
        }
    }

    private void setupToolbar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        ibToolbarBack.setOnClickListener(v -> onBackPressed());
        UpdateTopView();
    }

    private void UpdateTopView() {
        if (mApplication != null) {
            String displayDate;
            if (mApplication.getSubmittedOn() != null && mApplication.getSubmittedOn().length() > 0) {
                displayDate = ESP_LIB_Shared.getInstance().getDisplayDate(bContext, mApplication.getSubmittedOn(), true);
            } else {
                displayDate = ESP_LIB_Shared.getInstance().getDisplayDate(bContext, mApplication.getCreatedOn(), true);
            }
            txtrequestedonval.setText(displayDate);
            txtrequestedbyval.setText(mApplication.getApplicantName());
            long days = ESP_LIB_Shared.getInstance().fromStringToDate(bContext, displayDate);
            String daysVal = getString(R.string.esp_lib_text_day);
            if (days > 1)
                daysVal = getString(R.string.esp_lib_text_days);


            pendingfor.setText(days + " " + daysVal);

            if (isSubApplications) {
                categoryy.setText(mApplication.getApplicantName());
                categorytext.setText(getString(R.string.esp_lib_text_applicantcolon));
                rlpendingfor.setVisibility(View.VISIBLE);
                llmaindetail.setVisibility(View.VISIBLE);
                rldetailrow.setVisibility(View.GONE);
                llstatuslayout.setVisibility(View.GONE);
                txtrequestdetail.setVisibility(View.VISIBLE);
                toolbarheading.setText(getString(R.string.esp_lib_text_submissiondetails));
            } else {

                if (mApplication.getCategory() == null || mApplication.getCategory().isEmpty())
                    rlcategory.setVisibility(View.GONE);

                if (mApplication.isSigned())
                    ivsign.setVisibility(View.VISIBLE);
                else
                    ivsign.setVisibility(View.GONE);

                categoryy.setText(mApplication.getCategory());
                rlpendingfor.setVisibility(View.GONE);
                // llstatuslayout.setVisibility(View.VISIBLE);
                llstatuslayout.setVisibility(View.GONE);
                status.setText(mApplication.getStatus());
              /*  ESP_LIB_ApplicationStatusAdapter statusAdapter = new ESP_LIB_ApplicationStatusAdapter(mApplication.getStageStatuses(), bContext);
                status_list.setAdapter(statusAdapter);*/
                setStatusColor(mApplication.getStatusId());
                toolbarheading.setText(pref.getlabels().getApplication() + " " + getString(R.string.esp_lib_text_details));
            }
            applicationNumber.setText(mApplication.getApplicationNumber());
            definitionName.setText(mApplication.getDefinitionName());


            /*categoryy.setText(mApplication.getCategory());*/


        }
    }

    public void GetApplicationDetail(String id) {

        start_loading_animation(false);
        ESP_LIB_APIs apis = new CompRoot().getService(bContext);
        applicationDetail_call = apis.GetApplicationDetailv2(id);
        applicationDetail_call.enqueue(new Callback<ESP_LIB_DynamicResponseDAO>() {
            @Override
            public void onResponse(Call<ESP_LIB_DynamicResponseDAO> call, Response<ESP_LIB_DynamicResponseDAO> response) {

                if (response != null && response.body() != null) {
                    isClosingDatePassed = false;
                    status.setText(response.body().getApplicationStatus());
                    setStatusColor(response.body().getApplicationStatusId());
                    ESP_LIB_ApplicationSingleton.Companion.getInstace().setApplication(response.body());
                    txtinfodescription.setText(response.body().getDescription());

                    if (response.body().getPermissions() != null && response.body().getPermissions().size() > 0) {
                        spcloserequest.setVisibility(View.VISIBLE);
                        setCloseRequestSpinner(response.body().getPermissions());
                    } else
                        spcloserequest.setVisibility(View.GONE);


                    if (mApplication == null) {
                        mApplication = new ESP_LIB_ApplicationsDAO();
                        mApplication.setId(response.body().getApplicationId());
                        mApplication.setApplicationNumber(response.body().getApplicationNumber());
                        mApplication.setApplicantName(response.body().getApplicantName());
                        mApplication.setCategory(response.body().getCategory());
                        mApplication.setCreatedOn(response.body().getCreatedOn());
                        mApplication.setStatus(response.body().getApplicationStatus());
                        mApplication.setStatusId(response.body().getApplicationStatusId());
                        UpdateTopView();
                    }


                    if ((statusId == 1 || isResubmit) && !isComingfromAssessor) // 1= New or draft
                    {

                        definitionDescription.setText(response.body().getDescription());

                        stop_loading_animation(false);
                        // if (llrows.getVisibility() == View.GONE)
                        submit_btn.setVisibility(View.VISIBLE);
                        actual_response = response.body();
                        actualResponseJson = response.body().toJson();


                        List<ESP_LIB_DynamicSectionValuesDAO> sectionsValues = actual_response.getSectionValues();
                        ArrayList<ESP_LIB_DynamicFormSectionDAO> sections = GetFieldsCards(actual_response.getForm(), sectionsValues,
                                actual_response.getStages(), false, false);


                        if (sections != null && sections.size() > 0) {

                            mApplicationSectionsAdapter = new ESP_LIB_ListAddApplicationSectionsAdapter(sections, bContext, "", false);
                            mApplicationSectionsAdapter.setActualResponseJson(actualResponseJson);
                            app_list.setAdapter(mApplicationSectionsAdapter);
                            // mApplicationSectionsAdapter.notifyDataSetChanged();
                            mApplicationSectionsAdapter.setmApplicationFieldsAdapterListener2(ESP_LIB_ApplicationDetailScreenActivity.this);

                        }
                        checkExpiry(response);
                    } else {
                        submit_btn.setVisibility(View.GONE);
                        actual_response = response.body();
                        actualResponseJson = response.body().toJson();
                        if (ESP_LIB_Shared.getInstance().hasLinkDefinitionId(response.body())) {
                            GetLinkApplicationInfo(mApplication.getId() + "", response.body());
                            if (rldetailrow.getVisibility() == View.VISIBLE) {
                                rlsubmissionrow.setVisibility(View.VISIBLE);
                                linkdefinitioncard.setVisibility(View.GONE);
                                //spcloserequest.setVisibility(View.VISIBLE);
                                getSubmissions(false);
                            }


                        } else {
                            rlsubmissionrow.setVisibility(View.GONE);
                            stop_loading_animation(false);
                        }


                        //lldetail.setVisibility(View.GONE);

                        List<ESP_LIB_DynamicSectionValuesDAO> sectionsValues = actual_response.getSectionValues();
                        ArrayList<ESP_LIB_DynamicFormSectionDAO> sections = GetFieldsCards(actual_response.getForm(),
                                sectionsValues, actual_response.getStages(), true, true);

                        if (sections.size() > 0) {
                            mApplicationSectionsAdapter = new ESP_LIB_ListAddApplicationSectionsAdapter(sections, bContext, "", true);
                            mApplicationSectionsAdapter.setActualResponseJson(actualResponseJson);
                            app_list.setAdapter(mApplicationSectionsAdapter);
                            //  mApplicationSectionsAdapter.notifyDataSetChanged();
                            mApplicationSectionsAdapter.setmApplicationFieldsAdapterListener2(ESP_LIB_ApplicationDetailScreenActivity.this);
                        }
                    }

                    //getApplicationFeedBack(id);

                } else {
                    stop_loading_animation(false);
                    ESP_LIB_Shared.getInstance().showAlertMessage(pref.getlabels().getApplication(), getString(R.string.esp_lib_text_some_thing_went_wrong), bContext);
                }
            }

            @Override
            public void onFailure(Call<ESP_LIB_DynamicResponseDAO> call, Throwable t) {
                t.printStackTrace();
                stop_loading_animation(false);
                ESP_LIB_Shared.getInstance().showAlertMessage(pref.getlabels().getApplication(), getString(R.string.esp_lib_text_some_thing_went_wrong), bContext);
            }
        });

    }

    private void getSubmissions(Boolean isLoadMore) {

        start_loading_animation(false);

        try {


            if (isLoadMore) {
                swipeRefreshLayout.setRefreshing(true);
            } else {
                PAGE_NO = 1;
                PER_PAGE_RECORDS = 12;
                IN_LIST_RECORDS = 0;
                TOTAL_RECORDS_AVAILABLE = 0;
            }


            ESP_LIB_ESPApplication.getInstance().getFilter().setPageNo(PAGE_NO);
            ESP_LIB_ESPApplication.getInstance().getFilter().setRecordPerPage(PER_PAGE_RECORDS);


            ESP_LIB_FilterDAO daoESPLIB = new ESP_LIB_FilterDAO();
            if (ESP_LIB_ESPApplication.getInstance().getFilter().getStatuses().size() == 4) {
                daoESPLIB = ESP_LIB_Shared.getInstance().CloneFilter(ESP_LIB_ESPApplication.getInstance().getFilter());
                daoESPLIB.setStatuses(null);
                ArrayList<String> empty_fitler = new ArrayList<String>();
                empty_fitler.add("0");

            } else {
                daoESPLIB = ESP_LIB_Shared.getInstance().CloneFilter(ESP_LIB_ESPApplication.getInstance().getFilter());
            }

            ESP_LIB_DynamicResponseDAO getDynamicStagesDAO = new Gson().fromJson(actualResponseJson, ESP_LIB_DynamicResponseDAO.class);
            daoESPLIB.setParentApplicationId(String.valueOf(getDynamicStagesDAO.getApplicationId()));
            daoESPLIB.setApplicantId("0");
            daoESPLIB.setDefinationId(null);
            daoESPLIB.setSearch("");

            ESP_LIB_APIs apis = new CompRoot().getService(bContext);

            Call<ESP_LIB_ResponseApplicationsDAO> call = apis.getUserApplicationsV3(daoESPLIB);
            call.enqueue(new Callback<ESP_LIB_ResponseApplicationsDAO>() {
                @Override
                public void onResponse(Call<ESP_LIB_ResponseApplicationsDAO> call, Response<ESP_LIB_ResponseApplicationsDAO> response) {

                    stop_loading_animation(false);

                    if (response != null && response.body() != null && response.body().getTotalRecords() > 0) {
                        if (response.body().getApplications() != null && response.body().getApplications().size() > 0) {
                            if (isLoadMore) {
                                if (app_actual_list == null) {
                                    app_actual_list.addAll(response.body().getApplications());
                                } else if (app_actual_list != null && app_actual_list.size() > 0) {
                                    app_actual_list.addAll(response.body().getApplications());
                                }

                                PAGE_NO++;
                                IN_LIST_RECORDS = app_actual_list.size();
                                TOTAL_RECORDS_AVAILABLE = response.body().getTotalRecords();
                                SCROLL_TO += PER_PAGE_RECORDS;

                                try {

                                    ESP_LIB_ListSubmissionApplicationsAdapter adapter = new ESP_LIB_ListSubmissionApplicationsAdapter(app_actual_list, bContext, "");
                                    submission_app_list.setAdapter(adapter);
                                    submission_app_list.scrollToPosition(SCROLL_TO - 5);
                                } catch (Exception e) {
                                }

                            } else {
                                try {
                                    app_actual_list.clear();
                                    app_actual_list.addAll(response.body().getApplications());
                                    ESP_LIB_ListSubmissionApplicationsAdapter adapter = new ESP_LIB_ListSubmissionApplicationsAdapter(app_actual_list, bContext, "");
                                    submission_app_list.setAdapter(adapter);
                                    PAGE_NO++;
                                    IN_LIST_RECORDS = app_actual_list.size();
                                    TOTAL_RECORDS_AVAILABLE = response.body().getTotalRecords();
                                    SCROLL_TO = 0;
                                    AddScroller();
                                } catch (Exception e) {
                                }
                            }
                        } else
                            ESP_LIB_Shared.getInstance().messageBox(getString(R.string.esp_lib_text_some_thing_went_wrong), bContext);
                    }
                }

                @Override
                public void onFailure(Call<ESP_LIB_ResponseApplicationsDAO> call, Throwable t) {
                    stop_loading_animation(false);
                    ESP_LIB_Shared.getInstance().messageBox(getString(R.string.esp_lib_text_some_thing_went_wrong), bContext);
                }
            });

        } catch (Exception ex) {
            ex.printStackTrace();
            stop_loading_animation(false);
            ESP_LIB_Shared.getInstance().messageBox(getString(R.string.esp_lib_text_some_thing_went_wrong), bContext);


        }
    }

    private void checkExpiry(Response<ESP_LIB_DynamicResponseDAO> response) {
        String endDateFormated = ESP_LIB_Shared.getInstance().getDisplayDate(bContext, response.body().getEndDate(), false);
        if (endDateFormated != null && !endDateFormated.isEmpty()) {
            String currentDateFormated = ESP_LIB_Shared.getInstance().GetCurrentDateTime();
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);
                SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH);
                Date endDate = sdf.parse(endDateFormated);
                String dateStr = sdf.format(sdf1.parse(currentDateFormated));
                Date currentDate = sdf.parse(dateStr);
                ESP_LIB_CustomLogs.displayLogs(ACTIVITY_NAME + " endDate: " + endDate + " currentDate: " + currentDate);

                if (currentDate.after(endDate)) {

                    stop_loading_animation(false);
                    String message = getString(R.string.esp_lib_text_notacceptrequest) + " " + mApplication.getDefinitionName() + " " +
                            getString(R.string.esp_lib_text_contactadministrator);
                    ESP_LIB_Shared.getInstance().showAlertMessage("", message, bContext);
                    submit_btn.setVisibility(View.GONE);
                    isClosingDatePassed = true;
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void GetLinkApplicationInfo(String id, ESP_LIB_DynamicResponseDAO responseBody) {

        start_loading_animation(false);

        ESP_LIB_APIs apis = new CompRoot().getService(bContext);
        linkApplication_call = apis.GetLinkApplicationInfo(id);
        linkApplication_call.enqueue(new Callback<ESP_LIB_LinkApplicationsDAO>() {
            @Override
            public void onResponse(Call<ESP_LIB_LinkApplicationsDAO> call, Response<ESP_LIB_LinkApplicationsDAO> response) {
                if (response != null && response.body() != null) {
                    ESP_LIB_LinkApplicationsDAO body = response.body();

                    int submissioncount = body.getPendingLinkApplications() + body.getAcceptedLinkApplications() + body.getRejectedLinkApplications();
                    // if (submissioncount > 0)
                    //    rlsubmissionrow.setVisibility(View.VISIBLE);

                    txtsubmissions.setText(getString(R.string.esp_lib_text_submissions) + " (" + submissioncount + ")");


                    if (statusId == 3 || statusId == 4) // 3 = accepted 4 = rejected
                    {

                        isComingFromService = true;
                        // switchsubmissionallow.setChecked(false);
                        submissionallowedtext.setText(getString(R.string.esp_lib_text_submissionsstopped));
                        switchsubmissionallow.setVisibility(View.INVISIBLE);
                    } else {
                        if (body.isSubmissionAllowed()) {

                            isComingFromService = true;
                            switchsubmissionallow.setChecked(true);
                        } else {

                            //  switchsubmissionallow.setChecked(false);
                            submissionallowedtext.setText(getString(R.string.esp_lib_text_submissionsstopped));
                            switchsubmissionallow.setVisibility(View.INVISIBLE);
                        }
                    }


                    stop_loading_animation(false);

                } else {
                    stop_loading_animation(false);
                    switchsubmissionallow.setEnabled(false);

                    //   Shared.getInstance().showAlertMessage(pref.getlabels().getApplication(), getString(R.string.some_thing_went_wrong), bContext);
                }
            }

            @Override
            public void onFailure(Call<ESP_LIB_LinkApplicationsDAO> call, Throwable t) {
                t.printStackTrace();
                stop_loading_animation(false);
                ESP_LIB_Shared.getInstance().showAlertMessage(pref.getlabels().getApplication(), getString(R.string.esp_lib_text_some_thing_went_wrong), bContext);
                return;
            }
        });
    }

    private void getApplicationFeedBack(String id) {

        start_loading_animation(false);

        ESP_LIB_APIs apis = new CompRoot().getService(bContext);

        applicationFeedback_call = apis.GetApplicationFeedBack(id);

        applicationFeedback_call.enqueue(new Callback<List<ESP_LIB_ApplicationsFeedbackDAO>>() {
            @Override
            public void onResponse(Call<List<ESP_LIB_ApplicationsFeedbackDAO>> call, Response<List<ESP_LIB_ApplicationsFeedbackDAO>> response) {
                stop_loading_animation(false);
                if (response.body() != null && response.body().size() > 0) {
                    rvFeedbackCommentsList.setVisibility(View.VISIBLE);
                    llnofeedbackrecord.setVisibility(View.GONE);
                    ArrayList<ESP_LIB_FeedbackDAO> feedbackList = new ArrayList<ESP_LIB_FeedbackDAO>();
                    List<ESP_LIB_ApplicationsFeedbackDAO> body = response.body();
                    for (int i = 0; i < body.size(); i++) {
                        ESP_LIB_ApplicationsFeedbackDAO ESPLIBApplicationsFeedbackDAO = body.get(i);
                        ESP_LIB_FeedbackDAO ESPLIBFeedbackDao = new ESP_LIB_FeedbackDAO();
                        ESPLIBFeedbackDao.setUserName(ESPLIBApplicationsFeedbackDAO.getFullName());
                        ESPLIBFeedbackDao.setComment(ESPLIBApplicationsFeedbackDAO.getComment());
                        ESPLIBFeedbackDao.setCheck(true);
                        ESPLIBFeedbackDao.setUserImage(ESPLIBApplicationsFeedbackDAO.getImageUrl());
                        String role = getString(R.string.esp_lib_text_member);
                        if (ESPLIBApplicationsFeedbackDAO.isAdmin())
                            role = ESP_LIB_Enums.assessor.toString();

                       /* if (role.equalsIgnoreCase(bContext.getString(R.string.applicant)))
                            role = getString(R.string.member);*/


                        ESPLIBFeedbackDao.setUserType(role);

                        for (int j = 0; j < ESPLIBApplicationsFeedbackDAO.getAttachments().size(); j++) {
                            ESP_LIB_ApplicationsFeedbackAttachmentsDAO ESPLIBApplicationsFeedbackAttachmentsDAO = ESPLIBApplicationsFeedbackDAO.getAttachments().get(j);
                            ESP_LIB_DyanmicFormSectionFieldDetailsDAO ESPLIBDyanmicFormSectionFieldDetailsDAO = new ESP_LIB_DyanmicFormSectionFieldDetailsDAO();
                            ESPLIBDyanmicFormSectionFieldDetailsDAO.setMimeType(ESPLIBApplicationsFeedbackAttachmentsDAO.getMimeType());
                            ESPLIBDyanmicFormSectionFieldDetailsDAO.setName(ESPLIBApplicationsFeedbackAttachmentsDAO.getName());
                            ESPLIBDyanmicFormSectionFieldDetailsDAO.setDownloadUrl(ESPLIBApplicationsFeedbackAttachmentsDAO.getDownloadUrl());
                            ESPLIBDyanmicFormSectionFieldDetailsDAO.setCreatedOn(ESPLIBApplicationsFeedbackAttachmentsDAO.getCreatedOn());
                            ESPLIBFeedbackDao.setAttachemntDetails(ESPLIBDyanmicFormSectionFieldDetailsDAO);
                        }
                        feedbackList.add(ESPLIBFeedbackDao);
                    }
                    ESP_LIB_ApplicationFeedbackAdapter feedbackAdapter = new ESP_LIB_ApplicationFeedbackAdapter(feedbackList, bContext, false);
                    rvFeedbackCommentsList.setAdapter(feedbackAdapter);
                } else {
                    llnofeedbackrecord.setVisibility(View.VISIBLE);
                    rvFeedbackCommentsList.setVisibility(View.GONE);
                }

                //   loadData();
            }

            @Override
            public void onFailure(Call<List<ESP_LIB_ApplicationsFeedbackDAO>> call, Throwable t) {
                stop_loading_animation(false);
                llnofeedbackrecord.setVisibility(View.VISIBLE);
                rvFeedbackCommentsList.setVisibility(View.GONE);
            }
        });


    }

    public void postLinkDefinitionData(int definitionId, boolean isallowsubmission) {


        start_loading_animation(true);
        try {
            ESP_LIB_APIs apis = new CompRoot().getService(bContext);
            postLinkDefinition = apis.saveLinkApplicationInfo(definitionId, isallowsubmission);


            postLinkDefinition.enqueue(new Callback<ESP_LIB_LinkApplicationsDAO>() {
                @Override
                public void onResponse(Call<ESP_LIB_LinkApplicationsDAO> call, Response<ESP_LIB_LinkApplicationsDAO> response) {
                    stop_loading_animation(true);
                    // if (!response.body().isSubmissionAllowed()) {
                    EventBus.getDefault().post(new EventOptions.EventRefreshData());
                    loadData();

                        /*submissionallowedtext.setText(getString(R.string.esp_lib_text_submissionsstopped));
                        switchsubmissionallow.setVisibility(View.INVISIBLE);
                        loadData();*/
                    //}
                }

                @Override
                public void onFailure(Call<ESP_LIB_LinkApplicationsDAO> call, Throwable t) {
                    t.printStackTrace();
                    stop_loading_animation(true);
                    if (getBContext() != null) {
                        ESP_LIB_Shared.getInstance().showAlertMessage(pref.getlabels().getApplication(), getString(R.string.esp_lib_text_some_thing_went_wrong), bContext);
                    }
                }
            });

        } catch (Exception ex) {

            stop_loading_animation(true);
            if (getBContext() != null) {
                ESP_LIB_Shared.getInstance().showAlertMessage(pref.getlabels().getApplication(), getString(R.string.esp_lib_text_some_thing_went_wrong), bContext);
            }
        }
    }

    public void LoadStages() {


        start_loading_animation(false);
        try {
            ESP_LIB_APIs apis = new CompRoot().getService(bContext);
            loadStages_call = apis.getCurrency();

            loadStages_call.enqueue(new Callback<List<ESP_LIB_CurrencyDAO>>() {
                @Override
                public void onResponse(Call<List<ESP_LIB_CurrencyDAO>> call, Response<List<ESP_LIB_CurrencyDAO>> response) {

                    if (response.body() != null && response.body().size() > 0) {
                        ESP_LIB_ESPApplication.getInstance().setCurrencies(response.body());

                        if (mApplication != null) {

                            if (ESP_LIB_Shared.getInstance().isWifiConnected(bContext)) {
                                if (mApplication == null)
                                    GetApplicationDetail(getIntent().getIntExtra("applicationId", 0) + "");
                                else
                                    GetApplicationDetail(mApplication.getId() + "");
                            } else {
                                ESP_LIB_Shared.getInstance().showAlertMessage(getString(R.string.esp_lib_text_internet_error_heading), getString(R.string.esp_lib_text_internet_connection_error), bContext);
                            }

                        }

                    } else
                        stop_loading_animation(false);
                }

                @Override
                public void onFailure(Call<List<ESP_LIB_CurrencyDAO>> call, Throwable t) {
                    stop_loading_animation(false);
                }
            });

        } catch (Exception ex) {
            stop_loading_animation(false);
        }
    }//LoggedInUser end

    private void setStatusColor(int statusId) {

        rejected_status.setVisibility(View.GONE);
        rlstatus.setBackgroundResource(R.drawable.esp_lib_drawable_status_background);
        GradientDrawable drawable = (GradientDrawable) rlstatus.getBackground();
        switch (statusId) {
            case 0: // Invited
                status.setText(getString(R.string.esp_lib_text_invited));
                status.setTextColor(getResources().getColor(R.color.esp_lib_color_status_invited));
                drawable.setColor(ContextCompat.getColor(bContext, R.color.esp_lib_color_status_invited_background));
                break;
            case 1: // New as Draft
                status.setText(getString(R.string.esp_lib_text_draftcaps));
                status.setTextColor(getResources().getColor(R.color.esp_lib_color_status_draft));
                drawable.setColor(ContextCompat.getColor(bContext, R.color.esp_lib_color_status_draft_background));
                break;
            case 2: // Pending
                status.setText(getString(R.string.esp_lib_text_pending));
                status.setTextColor(getResources().getColor(R.color.esp_lib_color_status_pending));
                drawable.setColor(ContextCompat.getColor(bContext, R.color.esp_lib_color_status_pending_background));
                break;
            case 3: // Accepted
                status.setText(getString(R.string.esp_lib_text_accepted));
                status.setTextColor(getResources().getColor(R.color.esp_lib_color_status_accepted));
                ivsign.setImageResource(R.drawable.esp_lib_drawable_ic_icon_green_signed);
                drawable.setColor(ContextCompat.getColor(bContext, R.color.esp_lib_color_status_accepted_background));
                break;
            case 4:  // Rejected
                status.setText(getString(R.string.esp_lib_text_rejected));
                status.setTextColor(getResources().getColor(R.color.esp_lib_color_status_rejected));
                ivsign.setImageResource(R.drawable.esp_lib_drawable_ic_icon_red_signed);
                drawable.setColor(ContextCompat.getColor(bContext, R.color.esp_lib_color_status_rejected_background));
                submit_btn.setText(getString(R.string.esp_lib_text_resubmit));
                break;

            case 5:  // Cancelled
                status.setText(getString(R.string.esp_lib_text_cancelled));
                status.setTextColor(getResources().getColor(R.color.esp_lib_color_status_draft));
                drawable.setColor(ContextCompat.getColor(bContext, R.color.esp_lib_color_status_draft_background));
                break;
        }
    }

    private ArrayList<ESP_LIB_DynamicFormSectionDAO> GetFieldsCards(ESP_LIB_DynamicFormDAO response, List<ESP_LIB_DynamicSectionValuesDAO> sectionsValues,
                                                                    List<ESP_LIB_DynamicStagesDAO> responseStages, boolean isAddStage, boolean isSubmit) {
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
                                                        tempField.setLookupValue(value);
                                                        if (instanceValue.getValue() != null && ESP_LIB_Shared.getInstance().isNumeric(instanceValue.getValue()))
                                                            tempField.setId(Integer.parseInt(instanceValue.getValue()));
                                                    }
                                                    if (instanceValue.getType() == 11 && value != null && value.isEmpty())
                                                        value = tempField.getValue();

                                                    /*if (value == null || value.isEmpty())
                                                        value = "-";*/

                                                    tempField.setValue(value);
                                                    if (tempField.getType() == 7) { // for attachments only
                                                        try {
                                                            getAttachmentsDetail(tempField, instanceValue);
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }
                                                    }

                                                    if (tempField.getType() == 19 || tempField.getType() == 18) // calculated and mapped
                                                    {
                                                        if (instanceValue.getType() == 7) {
                                                            tempField.setType(instanceValue.getType());
                                                            getAttachmentsDetail(tempField, instanceValue);
                                                        } else if (instanceValue.getType() == 11 && isAddStages) // here isAddStages bit is used for isviewonly
                                                            tempField.setType(instanceValue.getType());
                                                        else if (instanceValue.getType() == 4 && isAddStages)  // here isAddStages bit is used for isviewonly
                                                            tempField.setType(instanceValue.getType());
                                                    }

                                                    sectionDAO.getFields().set(m, tempField);
                                                    if (isSubmit) {
                                                        ESP_LIB_DynamicFormSectionFieldDAO fieldDAO = sectionDAO.getFields().get(m);
                                                        finalFields.add(fieldDAO);
                                                    }

                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            if (!isSubmit) // drafted case
                            {
                                if (sectionDAO.getFields() != null) {
                                    finalFields.clear();
                                    finalFields.addAll(sectionDAO.getFields());
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

        // both assessor and applicant are same then show stages

        if (isAddStage) {
            isAddStages = true;
            populateStagesData(responseStages);

        }

        return sections;
    }

    private void populateStagesData(List<ESP_LIB_DynamicStagesDAO> responseStages) {
        if (pref.getSelectedUserRole().equalsIgnoreCase(ESP_LIB_Enums.assessor.toString()))
            isComingfromAssessor = true;


        if (isComingfromAssessor || actual_response.getStageVisibilityApplicant().equalsIgnoreCase(ESP_LIB_Enums.all.toString())
                || actual_response.getStageVisibilityApplicant().equalsIgnoreCase(ESP_LIB_Enums.allwithfeedback.toString())
                || actual_response.getStageVisibilityApplicant().equalsIgnoreCase(ESP_LIB_Enums.current.toString())) {
            isComingfromAssessor = true; // if status = ALL or Current or All with feedback
            txtapprovalStages.setVisibility(View.VISIBLE);
        } else
            txtapprovalStages.setVisibility(View.GONE);


        List<ESP_LIB_DynamicStagesDAO> tempStages = new ArrayList<>();
        for (int i = 0; i < responseStages.size(); i++) {
            if (responseStages.get(i).isEnabled()) {
                tempStages.add(responseStages.get(i));
            }
        }

        if (tempStages.size() > 0) {

            ESPLIBApplicationStagesAdapter = new ESP_LIB_ApplicationStagesAdapter(isComingfromAssessor, tempStages,
                    actualResponseJson, bContext, nestedscrollview);
            rvStagesFieldsCards.setAdapter(ESPLIBApplicationStagesAdapter);
            ESPLIBApplicationStagesAdapter.notifyDataSetChanged();
            if (actual_response.getApplicationStatusId() == 4 || actual_response.getApplicationStatusId() == 3)
            // getApplicationStatusId = 4 for application rejected
            // getStatusId = 2 for stage rejected
            // getApplicationStatusId = 3 for application accepted
            // getStatusId = 1 for stage accepted
            {
                String getDate = "";
                String getDisplayString = "";
                for (int i = 0; i < tempStages.size(); i++) {
                    if ((tempStages.get(i).getStatusId() == 2 || tempStages.get(i).getStatusId() == 1) && (tempStages.get(i).getCompletedOn() != null || !tempStages.get(i).getCompletedOn().isEmpty())) {
                        getDate = tempStages.get(i).getCompletedOn();
                        if (tempStages.get(i).getStatusId() == 1)
                            getDisplayString = getString(R.string.esp_lib_text_acceptedon);
                        else
                            getDisplayString = getString(R.string.esp_lib_text_rejectedon);
                    }
                }

                if (getDate != null && !getDate.isEmpty()) {
                    String displayDate = ESP_LIB_Shared.getInstance().getDisplayDate(bContext, getDate, true);
                    rlaccepreject.setVisibility(View.VISIBLE);
                    acceptedontext.setText(getDisplayString);
                    acceptedOn.setText(displayDate);
                }
            }
            //  getSignature(tempStages);

        } else {
            txtapprovalStages.setVisibility(View.GONE);
            if (actual_response.getApplicationSubmittedDate() != null && actual_response.getApplicationSubmittedDate().length() > 0) {
                String displayDate = ESP_LIB_Shared.getInstance().getDisplayDate(bContext, actual_response.getApplicationSubmittedDate(), true);
                acceptedOn.setText(displayDate);
            }


        }
    }

    private void getSignature(List<ESP_LIB_DynamicStagesDAO> tempStages) {

        start_loading_animation(false);
        try {

            ESP_LIB_APIs apis = new CompRoot().getService(bContext);

            Call<ESP_LIB_SignatureDAO> uploadFile_call = apis.getSignature();
            uploadFile_call.enqueue(new Callback<ESP_LIB_SignatureDAO>() {
                @Override
                public void onResponse(Call<ESP_LIB_SignatureDAO> call, Response<ESP_LIB_SignatureDAO> response) {
                    stop_loading_animation(false);
                    if (response != null && response.body() != null) {
                        ESP_LIB_SignatureDAO signatureResponseBody = response.body();

                        for (int k = 0; k < tempStages.size(); k++) {
                            ESP_LIB_DynamicStagesDAO esp_lib_dynamicStagesDAO = tempStages.get(k);
                            if (esp_lib_dynamicStagesDAO.getCriteriaList() != null) {
                                for (int i = 0; i < esp_lib_dynamicStagesDAO.getCriteriaList().size(); i++) {
                                    ESP_LIB_DynamicStagesCriteriaListDAO esp_lib_dynamicStagesCriteriaListDAO = esp_lib_dynamicStagesDAO.getCriteriaList().get(i);
                                    esp_lib_dynamicStagesCriteriaListDAO.setSignature(signatureResponseBody);
                                }
                            }
                        }
                        ESPLIBApplicationStagesAdapter.notifyDataSetChanged();

                    }

                }

                @Override
                public void onFailure(Call<ESP_LIB_SignatureDAO> call, Throwable t) {
                    stop_loading_animation(false);
                    ESP_LIB_Shared.getInstance().showAlertMessage(getString(R.string.esp_lib_text_error), getString(R.string.esp_lib_text_some_thing_went_wrong), bContext);

                }
            });

        } catch (Exception ex) {
            stop_loading_animation(false);
            ESP_LIB_Shared.getInstance().showAlertMessage(getString(R.string.esp_lib_text_error), getString(R.string.esp_lib_text_some_thing_went_wrong), bContext);
        }
    }//LoggedInUser end

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

    public void SubmitRequest(String whatodo) {

        ESP_LIB_DynamicResponseDAO submit_jsonNew = new Gson().fromJson(actualResponseJson, ESP_LIB_DynamicResponseDAO.class);

        if (submit_jsonNew != null) {

            List<ESP_LIB_DynamicSectionValuesDAO> sectionValuesListToPost = new ArrayList<>();

            if (mApplicationSectionsAdapter == null) {
                ESP_LIB_Shared.getInstance().showAlertMessage(getString(R.string.esp_lib_text_error), getString(R.string.esp_lib_text_formisempty), bContext);
                return;
            }

            for (ESP_LIB_DynamicFormSectionDAO updatedSection : mApplicationSectionsAdapter.getmApplications()) {

                if (updatedSection.getFieldsCardsList() != null && updatedSection.getFieldsCardsList().size() > 0) {

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

                                ESP_LIB_DynamicSectionValuesDAO.Instance.Value value = new ESP_LIB_DynamicSectionValuesDAO.Instance.Value();

                                value.setSectionCustomFieldId(ESPLIBDynamicFormSectionFieldDAO.getSectionCustomFieldId());
                                value.setType(ESPLIBDynamicFormSectionFieldDAO.getType());
                                value.setValue(ESPLIBDynamicFormSectionFieldDAO.getValue());

                                if (ESPLIBDynamicFormSectionFieldDAO.getType() == 11) {
                                    String finalValue = value.getValue();
                                    if (finalValue != null && !finalValue.isEmpty())
                                        finalValue += ":" + ESPLIBDynamicFormSectionFieldDAO.getSelectedCurrencyId() + ":" + ESPLIBDynamicFormSectionFieldDAO.getSelectedCurrencySymbol();

                                    value.setValue(finalValue);
                                }
                                ESP_LIB_CustomLogs.displayLogs(ACTIVITY_NAME + " value.getValue(): " + value.getValue());

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

            if (sectionValuesListToPost.size() > 0)
                submit_jsonNew.setSectionValues(sectionValuesListToPost);


            ESP_LIB_CustomLogs.displayLogs(ACTIVITY_NAME + " post.getApplicationStatus(): " + submit_jsonNew.toJson());


            if (whatodo.equalsIgnoreCase("calculatedValues"))
                getCalculatedValues(submit_jsonNew);
            else
                SubmitForm(submit_jsonNew, whatodo);

        }

    }//END SubmitRequest

    @Override
    public void onFieldValuesChanged() {
        List<ESP_LIB_DynamicFormSectionFieldDAO> adapter_list = null;

        if (mApplicationSectionsAdapter != null) {
            adapter_list = mApplicationSectionsAdapter.GetAllFields();
        }
        if (adapter_list != null && adapter_list.size() > 0) {
            boolean isAllFieldsValidateTrue = true;
            for (ESP_LIB_DynamicFormSectionFieldDAO ESPLIBDynamicFormSectionFieldDAO : adapter_list) {

                if (ESPLIBDynamicFormSectionFieldDAO.getSectionType() != ESP_LIB_ApplicationFieldsRecyclerAdapter.SECTIONCONSTANT) {

                    String error = "";
                    error = ESP_LIB_Shared.getInstance().edittextErrorChecks(bContext, ESPLIBDynamicFormSectionFieldDAO.getValue(), error, ESPLIBDynamicFormSectionFieldDAO);
                    if (error.length() > 0 && (ESPLIBDynamicFormSectionFieldDAO.getType() != 18 && ESPLIBDynamicFormSectionFieldDAO.getType() != 19)) {
                        isAllFieldsValidateTrue = false;
                        break;
                    }

                    if (!ESPLIBDynamicFormSectionFieldDAO.isShowToUserOnly())  // if fields are not for displayed then validate
                    {
                        if (ESPLIBDynamicFormSectionFieldDAO.isRequired()) {
                            if (!ESPLIBDynamicFormSectionFieldDAO.isValidate()) {
                                isAllFieldsValidateTrue = false;
                                break;
                            }
                        }
                    }
                }
            }

            if (isAllFieldsValidateTrue) {
                submit_btn.setEnabled(true);
                submit_btn.setAlpha(1);
                submit_btn.setBackgroundResource(R.drawable.esp_lib_drawable_draw_bg_green);
            } else {
                submit_btn.setEnabled(false);
                submit_btn.setAlpha(0.5f);
                submit_btn.setBackgroundResource(R.drawable.esp_lib_drawable_draw_bg_grey_disable_button);
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

        ESP_LIB_CustomLogs.displayLogs(ACTIVITY_NAME + " getAllowedValuesCriteria: " + getAllowedValuesCriteria + " mimeTypes: " + Arrays.toString(mimeTypes));

        // Intent getContentIntent = FileUtils.createGetContentIntent();

        final Intent getContentIntent = new Intent(Intent.ACTION_GET_CONTENT);
        // The MIME data type filter

        getContentIntent.setType("*/*");
        if (getAllowedValuesCriteria.length() > 0)
            getContentIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        // Only return URIs that can be opened with ContentResolver
        getContentIntent.addCategory(Intent.CATEGORY_OPENABLE);

        Intent intent = Intent.createChooser(getContentIntent, getString(R.string.esp_lib_text_selectafile));
        startActivityForResult(intent, REQUEST_CHOOSER);
    }

    @Override
    public void onLookupFieldClicked(ESP_LIB_DynamicFormSectionFieldDAO fieldDAO, int position, boolean isCalculatedMappedField) {

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
                        isFileSizeValid = ESP_LIB_Shared.getInstance().getFileSize(ESP_LIB_RealPathUtil.getPath(bContext, uri), getMaxVal);

                    ESP_LIB_CustomLogs.displayLogs(ACTIVITY_NAME + " getMaxVal: " + getMaxVal + " isFileSizeValid: " +
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
                ESP_LIB_LookUpDAO lookup = (ESP_LIB_LookUpDAO) data.getExtras().getSerializable(ESP_LIB_LookUpDAO.Companion.getBUNDLE_KEY());
                boolean isCalculatedMappedField = data.getExtras().getBoolean("isCalculatedMappedField");
                if (fieldToBeUpdated != null && lookup != null) {
                    SetUpLookUpValues(fieldToBeUpdated, lookup, isCalculatedMappedField);
                }

            }

        }

    }

    public void UpdateLoadImageForField(ESP_LIB_DynamicFormSectionFieldDAO field, Uri uri) {
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

    public void SetUpLookUpValues(ESP_LIB_DynamicFormSectionFieldDAO field, ESP_LIB_LookUpDAO lookup, boolean isCalculatedMappedField) {

        field.setValue(String.valueOf(lookup.getId()));
        field.setLookupValue(lookup.getName());
        field.setId(lookup.getId());


        if (ESPLIBApplicationStagesAdapter != null)
            ESPLIBApplicationStagesAdapter.getCriteriaAdapterESPLIB().notifyOnly(fieldToBeUpdated.getUpdatePositionAttachment());
        else if (mApplicationSectionsAdapter != null) {
            mApplicationSectionsAdapter.notifyDataSetChanged();
            // if (isCalculatedMappedField)
            if (field.isTigger() && ESP_LIB_ApplicationFieldsRecyclerAdapter.isCalculatedMappedField)
                SubmitRequest("calculatedValues");

        }

    }

    private void UpLoadFile(final ESP_LIB_DynamicFormSectionFieldDAO field,
                            final MultipartBody.Part body, final Uri uri) {

        start_loading_animation(true);
        try {
            /* APIs Mapping respective Object*/
            ESP_LIB_APIs apis = new CompRoot().getService(bContext);

            uploadFile_call = apis.upload(body);
            uploadFile_call.enqueue(new Callback<ESP_LIB_ResponseFileUploadDAO>() {
                @Override
                public void onResponse(Call<ESP_LIB_ResponseFileUploadDAO> call, Response<ESP_LIB_ResponseFileUploadDAO> response) {
                    stop_loading_animation(true);
                    if (response != null && response.body() != null) {

                        if (field != null) {

                            try {

                                //File file = FileUtils.getFile(bContext, uri);

                                File file = null;
                                String path = ESP_LIB_RealPathUtil.getPath(bContext, uri);
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


                                if (ESPLIBApplicationStagesAdapter != null)
                                    ESPLIBApplicationStagesAdapter.getCriteriaAdapterESPLIB().notifyOnly(fieldToBeUpdated.getUpdatePositionAttachment());
                                else if (mApplicationSectionsAdapter != null) {
                                    mApplicationSectionsAdapter.notifyDataSetChanged();
                                    if (ESP_LIB_ApplicationFieldsRecyclerAdapter.isCalculatedMappedField)
                                        SubmitRequest("calculatedValues");
                                }


                            } catch (Exception e) {
                            }
                        }

                    } else {

                        ESP_LIB_Shared.getInstance().messageBox(getString(R.string.esp_lib_text_some_thing_went_wrong), bContext);
                    }

                }

                @Override
                public void onFailure(Call<ESP_LIB_ResponseFileUploadDAO> call, Throwable t) {
                    stop_loading_animation(true);
                    ESP_LIB_Shared.getInstance().showAlertMessage(getString(R.string.esp_lib_text_error), getString(R.string.esp_lib_text_some_thing_went_wrong), bContext);
                    // UploadFileInformation(fileDAO);
                }
            });

        } catch (Exception ex) {
            stop_loading_animation(true);
            if (ex != null) {
                ESP_LIB_Shared.getInstance().showAlertMessage(getString(R.string.esp_lib_text_error), getString(R.string.esp_lib_text_some_thing_went_wrong), bContext);
                //UploadFileInformation(fileDAO);

            }
        }
    }//LoggedInUser end

    private void SubmitForm(ESP_LIB_DynamicResponseDAO post, String whatodo) {

        if (post.getApplicationStatus().equalsIgnoreCase(ESP_LIB_Enums.rejected.toString()))
            post.setApplicationId(0);


        start_loading_animation(true);
        try {

            ESP_LIB_APIs apis = new CompRoot().getService(bContext);

            if (whatodo.equalsIgnoreCase(getString(R.string.esp_lib_text_draft))) {
                submitForm_call = apis.DraftApplication(post);
            } else {
                submitForm_call = apis.SubmitApplication(post);
            }

            submitForm_call.enqueue(new Callback<Integer>() {
                @Override
                public void onResponse(Call<Integer> call, Response<Integer> response) {
                    stop_loading_animation(true);

                    if (response.code() == 409)
                        ESP_LIB_Shared.getInstance().showAlertMessage("", getString(R.string.esp_lib_text_closingdatepassed), bContext);
                    else {
                        if (response != null && response.body() != null) {

                           /* Bundle bnd = new Bundle();
                            bnd.putBoolean("whatodo", true);
                            Intent intent = new Intent();
                            intent.putExtras(bnd);
                            setResult(2, intent);
                            finish();*/
                            EventBus.getDefault().post(new EventOptions.EventRefreshData());
                            // if (ESP_LIB_ESPApplication.getInstance().isComponent()) {
                            ESP_LIB_ESPApplication.getInstance().setOnCLosedTab(true);
                            finish();
                            /*} else {
                                Intent intent = new Intent(bContext, ESP_LIB_ApplicationsActivityDrawer.class);
                                ComponentName cn = intent.getComponent();
                                Intent mainIntent = Intent.makeRestartActivityTask(cn);
                                startActivity(mainIntent);
                            }*/
                        } else {

                            ESP_LIB_Shared.getInstance().messageBox(getString(R.string.esp_lib_text_some_thing_went_wrong), bContext);
                        }
                    }

                }

                @Override
                public void onFailure(Call<Integer> call, Throwable t) {
                    stop_loading_animation(true);
                    ESP_LIB_Shared.getInstance().showAlertMessage(getString(R.string.esp_lib_text_error), getString(R.string.esp_lib_text_some_thing_went_wrong), bContext);
                    // UploadFileInformation(fileDAO);
                }
            });

        } catch (Exception ex) {
            stop_loading_animation(true);
            if (ex != null) {
                ESP_LIB_Shared.getInstance().showAlertMessage(getString(R.string.esp_lib_text_error), getString(R.string.esp_lib_text_some_thing_went_wrong), bContext);
                //UploadFileInformation(fileDAO);

            }
        }
    }//LoggedInUser end

    public void stagefeedbackSubmitForm(ESP_LIB_PostApplicationsStatusDAO post) {


        start_loading_animation(true);
        try {

            ESP_LIB_APIs apis = new CompRoot().getService(bContext);
            StageFeedbackSubmit_call = apis.AcceptRejectApplication(post);


            StageFeedbackSubmit_call.enqueue(new Callback<Integer>() {
                @Override
                public void onResponse(Call<Integer> call, Response<Integer> response) {

                    stop_loading_animation(true);
                    ESP_LIB_CustomLogs.displayLogs(ACTIVITY_NAME + " stagefeedbackSubmitForm: " + response);
                    GetApplicationDetail(mApplication.getId() + "");
                }

                @Override
                public void onFailure(Call<Integer> call, Throwable t) {
                    stop_loading_animation(true);
                    if (t != null && bContext != null) {
                        ESP_LIB_Shared.getInstance().showAlertMessage(pref.getlabels().getApplication(), getString(R.string.esp_lib_text_some_thing_went_wrong), bContext);
                    }
                }
            });

        } catch (Exception ex) {
            if (ex != null) {
                stop_loading_animation(true);
                if (ex != null && bContext != null) {
                    ESP_LIB_Shared.getInstance().showAlertMessage(pref.getlabels().getApplication(), getString(R.string.esp_lib_text_some_thing_went_wrong), bContext);
                }
            }
        }
    }

    private void setGravity() {
        if (pref.getLanguage().equalsIgnoreCase("ar")) {
            definitionName.setGravity(Gravity.RIGHT);
            categoryy.setGravity(Gravity.RIGHT);
            applicationNumber.setGravity(Gravity.RIGHT);
            txtrequest.setGravity(Gravity.RIGHT);
            txtrequestedbyval.setGravity(Gravity.RIGHT);
            txtrequestedonval.setGravity(Gravity.RIGHT);
            txtrequestedby.setGravity(Gravity.RIGHT);
            status.setGravity(Gravity.RIGHT);
            heading.setGravity(Gravity.RIGHT);
            categorytext.setGravity(Gravity.RIGHT);

        } else {
            definitionName.setGravity(Gravity.LEFT);
            categoryy.setGravity(Gravity.LEFT);
            applicationNumber.setGravity(Gravity.LEFT);
            txtrequest.setGravity(Gravity.LEFT);
            txtrequestedbyval.setGravity(Gravity.LEFT);
            txtrequestedonval.setGravity(Gravity.LEFT);
            txtrequestedby.setGravity(Gravity.LEFT);
            status.setGravity(Gravity.LEFT);
            heading.setGravity(Gravity.LEFT);
            categorytext.setGravity(Gravity.LEFT);
        }
    }

    private void submissionDialog(String msg, String buttonText, String title) {

        new MaterialAlertDialogBuilder(this, R.style.Esp_Lib_Style_AlertDialogTheme)
                .setTitle(title)
                .setCancelable(false)
                .setMessage(msg)
                .setPositiveButton(buttonText, (dialogInterface, i) -> {
                    if (ESP_LIB_Shared.getInstance().isWifiConnected(bContext)) {
                        // if (title.equalsIgnoreCase(getString(R.string.esp_lib_text_allowsubmission)))
                        postLinkDefinitionData(mApplication.getId(), false);
                        /*else
                            postLinkDefinitionData(mApplication.getId(), false);*/
                    } else {
                        ESP_LIB_Shared.getInstance().showAlertMessage(getString(R.string.esp_lib_text_internet_error_heading), getString(R.string.esp_lib_text_internet_connection_error), bContext);
                    }
                    dialogInterface.dismiss();
                })
                .setNeutralButton(getString(R.string.esp_lib_text_cancel), (dialogInterface, i) -> {
                   /* isComingFromService = true;
                    if (title.equalsIgnoreCase(getString(R.string.esp_lib_text_allowsubmission))) {
                        switchsubmissionallow.setChecked(false);
                    } else {
                        switchsubmissionallow.setChecked(true);
                    }*/
                    dialogInterface.dismiss();
                })
                .show();


    }

    public void cancelDefinition(int definitionId) {

        ESP_LIB_CancelApplicationDAO esp_lib_cancelApplicationDAO = new ESP_LIB_CancelApplicationDAO();
        esp_lib_cancelApplicationDAO.setApplicationid(mApplication.getId());


        start_loading_animation(true);
        try {
            ESP_LIB_APIs apis = new CompRoot().getService(bContext);


            postLinkDefinition.enqueue(new Callback<ESP_LIB_LinkApplicationsDAO>() {
                @Override
                public void onResponse(Call<ESP_LIB_LinkApplicationsDAO> call, Response<ESP_LIB_LinkApplicationsDAO> response) {
                    stop_loading_animation(true);
                    if (!response.body().isSubmissionAllowed()) {

                        submissionallowedtext.setText(getString(R.string.esp_lib_text_submissionsstopped));
                        switchsubmissionallow.setVisibility(View.INVISIBLE);
                        loadData();
                    }
                }

                @Override
                public void onFailure(Call<ESP_LIB_LinkApplicationsDAO> call, Throwable t) {
                    t.printStackTrace();
                    stop_loading_animation(true);
                    if (getBContext() != null) {
                        ESP_LIB_Shared.getInstance().showAlertMessage(pref.getlabels().getApplication(), getString(R.string.esp_lib_text_some_thing_went_wrong), bContext);
                    }
                }
            });

        } catch (Exception ex) {

            stop_loading_animation(true);
            if (getBContext() != null) {
                ESP_LIB_Shared.getInstance().showAlertMessage(pref.getlabels().getApplication(), getString(R.string.esp_lib_text_some_thing_went_wrong), bContext);
            }
        }
    }


    public void SubmitStageRequest(boolean isAccepted, ESP_LIB_DynamicStagesCriteriaListDAO criteriaListDAO) {

        ESP_LIB_DynamicResponseDAO submit_jsonNew = new Gson().fromJson(actualResponseJson, ESP_LIB_DynamicResponseDAO.class);//Shared.getInstance().CloneAddFormWithForm(actual_response);
        List<ESP_LIB_DynamicFormValuesDAO> criteriaFormValues = getCriteriaFormValues(criteriaListDAO);

        // CustomLogs.displayLogs(ACTIVITY_NAME + " post.ApplicationSingleton(): " + ApplicationSingleton.getInstace().getApplication().getApplicationId());
        ESP_LIB_PostApplicationsStatusDAO post = new ESP_LIB_PostApplicationsStatusDAO();

        criteriaListDAO.setFormValues(criteriaFormValues);
        post.setAccepted(isAccepted);
        post.setApplicationId(submit_jsonNew.getApplicationId());
        post.setAssessmentId(criteriaListDAO.getAssessmentId());
        post.setComments("");
        post.setStageId(criteriaListDAO.getStageId());
        post.setValues(criteriaFormValues);


        ESP_LIB_CustomLogs.displayLogs(ACTIVITY_NAME + " post.getApplicationStatus(): " + post.toJson() + " toString: " + post.toString());
        stagefeedbackSubmitForm(post);


    }//END SubmitRequest

    private List<ESP_LIB_DynamicFormValuesDAO> getCriteriaFormValues(ESP_LIB_DynamicStagesCriteriaListDAO criteriaListDAO) {
        int sectionId = 0;
        List<ESP_LIB_DynamicFormValuesDAO> formValuesList = new ArrayList<>();
        ESP_LIB_DynamicFormDAO form = criteriaListDAO.getForm();
        if (form.getSections() != null) {
            for (ESP_LIB_DynamicFormSectionDAO sections : form.getSections()) {
                ESP_LIB_CustomLogs.displayLogs(ACTIVITY_NAME + " sections.getId(): " + sections.getId());
                sectionId = sections.getId();
                if (sections.getFields() != null) {
                    for (ESP_LIB_DynamicFormSectionFieldDAO ESPLIBDynamicFormSectionFieldDAO : sections.getFields()) {
                        ESP_LIB_DynamicFormValuesDAO value = new ESP_LIB_DynamicFormValuesDAO();
                        value.setSectionCustomFieldId(ESPLIBDynamicFormSectionFieldDAO.getSectionCustomFieldId());
                        value.setType(ESPLIBDynamicFormSectionFieldDAO.getType());
                        value.setValue(ESPLIBDynamicFormSectionFieldDAO.getValue());
                        value.setSectionId(sectionId);
                        value.setDetails(value.getDetails());
                        if (ESPLIBDynamicFormSectionFieldDAO.getType() == 11) {
                            String finalValue = value.getValue();
                            if (finalValue != null && !finalValue.isEmpty())
                                finalValue += ":" + ESPLIBDynamicFormSectionFieldDAO.getSelectedCurrencyId() + ":" + ESPLIBDynamicFormSectionFieldDAO.getSelectedCurrencySymbol();

                            value.setValue(finalValue);
                        }
                        ESP_LIB_CustomLogs.displayLogs(ACTIVITY_NAME + " value.getValue(): " + value.getValue());
                      /*  if (value.getValue() == null || (value.getValue().isEmpty()))
                            value.setValue("-");*/
                        formValuesList.add(value);
                    }
                }
            }
        }

        return formValuesList;
    }

    @Override
    public void feedbackClick(boolean isAccepted, @Nullable ESP_LIB_DynamicStagesCriteriaListDAO criteriaListDAO, @Nullable ESP_LIB_DynamicStagesDAO ESPLIBDynamicStagesDAO, int position) {

        boolean isApproved = isAccepted;

        if (ESPLIBDynamicStagesDAO != null) {
            int count = 0;
            ESP_LIB_DynamicResponseDAO actualResponseJsonsubmitJson = new Gson().fromJson(actualResponseJson, ESP_LIB_DynamicResponseDAO.class);
            ESP_LIB_DynamicStagesDAO ESPLIBDynamicStagesDAO1 = actualResponseJsonsubmitJson.getStages().get(actualResponseJsonsubmitJson.getStages().size() - 1);
            int size = ESPLIBDynamicStagesDAO1.getCriteriaList().size();
            if (ESPLIBDynamicStagesDAO.isAll()) {

                //if last stage and last criteria then open feedback on approve button
                //if last stage and any criteria then open feedback on reject button

                int getCount = criteriaCount(ESPLIBDynamicStagesDAO1, count, size);
                if (isApproved && getCount == size - 1)
                    isApproved = false;

            } else {

                //if last stage and last criteria then open feedback on reject button
                //if last stage and any criteria then open feedback on approve button

                if (!isApproved) {
                    int getCount = criteriaCount(ESPLIBDynamicStagesDAO1, count, size);
                    if (getCount != size - 1)
                        isApproved = true;
                } else if (ESPLIBDynamicStagesDAO.getId() == ESPLIBDynamicStagesDAO1.getId())
                    isApproved = false;


            }
        }

        isApproved = false;
        if (!isApproved) {
            Intent intent = new Intent(this, ESP_LIB_FeedbackForm.class);
            intent.putExtra("actualResponseJson", actualResponseJson);
            intent.putExtra("criteriaListDAO", criteriaListDAO);
            intent.putExtra("isAccepted", isAccepted);
            startActivity(intent);
        } else {
            SubmitStageRequest(isAccepted, criteriaListDAO);
        }


    }

    private int criteriaCount(ESP_LIB_DynamicStagesDAO ESPLIBDynamicStagesDAO1, int count, int size) {
        for (int i = 0; i < size; i++) {
            if (ESPLIBDynamicStagesDAO1.getCriteriaList() != null) {
                ESP_LIB_DynamicStagesCriteriaListDAO ESPLIBDynamicStagesCriteriaListDAO = ESPLIBDynamicStagesDAO1.getCriteriaList().get(i);

                if (ESPLIBDynamicStagesCriteriaListDAO.getAssessmentStatus() != null && (ESPLIBDynamicStagesCriteriaListDAO.getAssessmentStatus().equalsIgnoreCase(getString(R.string.esp_lib_text_accepted))
                        || ESPLIBDynamicStagesCriteriaListDAO.getAssessmentStatus().equalsIgnoreCase(getString(R.string.esp_lib_text_rejected)))) {
                    count++;
                }
            }
        }

        return count;
    }

    @Override
    public void validateCriteriaFields(ESP_LIB_DynamicStagesCriteriaListDAO dynamicStagesCriteriaList) {

        List<ESP_LIB_DynamicFormSectionFieldDAO> adapter_list = null;
        if (ESPLIBApplicationStagesAdapter != null) {
            adapter_list = ESPLIBApplicationStagesAdapter.getAllCriteriaFields();
        }
        boolean isAllFieldsValidateTrue = true;

        int criteriaId = dynamicStagesCriteriaList.getId();

        for (int i = 0; i < dynamicStagesCriteriaList.form.getSections().size(); i++) {
            ESP_LIB_DynamicFormSectionDAO ESPLIBDynamicFormSectionDAO = dynamicStagesCriteriaList.form.getSections().get(i);

            for (int k = 0; k < ESPLIBDynamicFormSectionDAO.getFields().size(); k++) {
                int id = ESPLIBDynamicFormSectionDAO.getFields().get(k).getId();

                if (adapter_list != null && adapter_list.size() > 0) {

                    for (ESP_LIB_DynamicFormSectionFieldDAO ESPLIBDynamicFormSectionFieldDAO : adapter_list) {

                        if (ESPLIBDynamicFormSectionFieldDAO.getId() == id) {

                            if (ESPLIBDynamicFormSectionFieldDAO.isRequired()) {
                                if (!ESPLIBDynamicFormSectionFieldDAO.isValidate()) {
                                    isAllFieldsValidateTrue = false;
                                    break;
                                }

                            }


                        }
                    }
                }
            }


        }

        for (int p = 0; p < ESPLIBApplicationStagesAdapter.getStagesListESPLIB().size(); p++) {
            if (ESPLIBApplicationStagesAdapter.getStagesListESPLIB().get(p).getCriteriaList() != null) {
                for (int q = 0; q < ESPLIBApplicationStagesAdapter.getStagesListESPLIB().get(p).getCriteriaList().size(); q++) {

                    int id = ESPLIBApplicationStagesAdapter.getStagesListESPLIB().get(p).getCriteriaList().get(q).getId();
                    if (criteriaId == id) {
                        ESPLIBApplicationStagesAdapter.getStagesListESPLIB().get(p).getCriteriaList().get(q).setValidate(isAllFieldsValidateTrue);
                        try {
                            ESPLIBApplicationStagesAdapter.notifyChangeIfAny(criteriaId);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

        }
    }

    private void start_loading_animation(boolean isShowDialog) {

        swipeRefreshLayout.setRefreshing(true);

        if (isShowDialog) {
            try {
                if (!pDialog.isShowing())
                    pDialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            shimmer_view_container.setVisibility(View.VISIBLE);
        }

    }

    private void stop_loading_animation(boolean isShowDialog) {
        swipeRefreshLayout.setRefreshing(false);
        if (isShowDialog) {
            try {
                if (pDialog.isShowing())
                    pDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            shimmer_view_container.setVisibility(View.GONE);
        }

    }

    @Override
    public void onBackPressed() {

        if (shimmer_view_container.getVisibility() == View.VISIBLE) {
            if (calculatedValues_call != null)
                calculatedValues_call.cancel();
            if (applicationDetail_call != null)
                applicationDetail_call.cancel();
            if (linkApplication_call != null)
                linkApplication_call.cancel();
            if (applicationFeedback_call != null)
                applicationFeedback_call.cancel();
            if (postLinkDefinition != null)
                postLinkDefinition.cancel();
            if (loadStages_call != null)
                loadStages_call.cancel();
            if (uploadFile_call != null)
                uploadFile_call.cancel();
            if (submitForm_call != null)
                submitForm_call.cancel();
            if (StageFeedbackSubmit_call != null)
                StageFeedbackSubmit_call.cancel();

        }

        if (submit_btn.getVisibility() == View.VISIBLE) {
            ESP_LIB_AlertActionWindow action_window = ESP_LIB_AlertActionWindow.newInstance(getString(R.string.esp_lib_text_save_draft), getString(R.string.esp_lib_text_your) + " " + pref.getlabels().getApplication() + " " + getString(R.string.esp_lib_text_wasnotsubmitted), getString(R.string.esp_lib_text_save_draft_ok), getString(R.string.esp_lib_text_discard) + " " + pref.getlabels().getApplication(), getString(R.string.esp_lib_text_draft));
            action_window.show(getSupportFragmentManager(), "");
            action_window.setCancelable(true);
        } else if (topcardview.getVisibility() == View.GONE) {
            detailClick();
        } else {
            super.onBackPressed();
            criteriaWasLoaded = false;
            if (!isSubApplications)
                ESP_LIB_ListUsersApplicationsAdapter.Companion.setSubApplications(false);
            /*Intent intent = new Intent(bContext, ApplicationsActivityDrawer.class);
            ComponentName cn = intent.getComponent();
            Intent mainIntent = Intent.makeRestartActivityTask(cn);
            startActivity(mainIntent);*/


        }
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
        if (((statusId != 1 || !isResubmit) && isComingfromAssessor) || ESP_LIB_FeedbackForm.Companion.isComingFromFeedbackFrom()) {
            loadData();
        }
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
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (ESP_LIB_ApplicationFieldsRecyclerAdapter.isCalculatedMappedField)
                    SubmitRequest("calculatedValues");
            }
        }, 1000);
    }

    public void getCalculatedValues(ESP_LIB_DynamicResponseDAO post) {

        if (isKeyboardVisible)
            isCalculatedField = true;

        try {
            if (isAddStages)
                post = getCriteriaFormPost();


            //  start_loading_animation(true);
            ESP_LIB_APIs apis = new CompRoot().getService(bContext);
            calculatedValues_call = apis.getCalculatedValues(post);

            calculatedValues_call.enqueue(new Callback<List<ESP_LIB_CalculatedMappedFieldsDAO>>() {
                @Override
                public void onResponse(Call<List<ESP_LIB_CalculatedMappedFieldsDAO>> call, Response<List<ESP_LIB_CalculatedMappedFieldsDAO>> response) {

                    if (response != null && response.body() != null) {
                        //  List<DynamicSectionValuesDAO> sectionValuesListToPost = new ArrayList<>();
                        List<ESP_LIB_CalculatedMappedFieldsDAO> calculatedMappedFieldsList = response.body();

                        for (int i = 0; i < calculatedMappedFieldsList.size(); i++) {
                            ESP_LIB_CalculatedMappedFieldsDAO ESPLIBCalculatedMappedFieldsDAO = calculatedMappedFieldsList.get(i);


                            if (isAddStages)
                                populateCriteriaCalculatedFields(ESPLIBCalculatedMappedFieldsDAO);
                            else {
                                if (mApplicationSectionsAdapter.getmApplications() != null) {
                                    List<ESP_LIB_DynamicFormSectionDAO> ESPLIBDynamicFormSectionDAOS = mApplicationSectionsAdapter.getmApplications();
                                    for (int u = 0; u < ESPLIBDynamicFormSectionDAOS.size(); u++) {
                                        ESP_LIB_DynamicFormSectionDAO updatedSection = ESPLIBDynamicFormSectionDAOS.get(u);
                                        if (updatedSection.getFieldsCardsList().size() > 0) {
                                            //For Setting InstancesList
                                            //   for (DynamicFormSectionFieldsCardsDAO dynamicFormSectionFieldsCardsDAO : updatedSection.getFieldsCardsList()) {
                                            for (int g = 0; g < updatedSection.getFieldsCardsList().size(); g++) {

                                                if (ESPLIBCalculatedMappedFieldsDAO.getSectionIndex() == g) {
                                                    ESP_LIB_DynamicFormSectionFieldsCardsDAO ESPLIBDynamicFormSectionFieldsCardsDAO = updatedSection.getFieldsCardsList().get(g);
                                                    if (ESPLIBDynamicFormSectionFieldsCardsDAO.getFields() != null) {
                                                        for (int p = 0; p < ESPLIBDynamicFormSectionFieldsCardsDAO.getFields().size(); p++) {
                                                            ESP_LIB_DynamicFormSectionFieldDAO ESPLIBDynamicFormSectionFieldDAO = ESPLIBDynamicFormSectionFieldsCardsDAO.getFields().get(p);

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
                                                                    String calculatedDisplayDate = ESP_LIB_Shared.getInstance().getDisplayDate(bContext, ESPLIBCalculatedMappedFieldsDAO.getValue(), false);
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
                        }


                        if (txtapprovalStages.getVisibility() == View.GONE && ESPLIBApplicationStagesAdapter != null) {
                            ESPLIBApplicationStagesAdapter.getCriteriaAdapterESPLIB().setCriterias(ESPLIBApplicationStagesAdapter.getCriteriaAdapterESPLIB().getCriteriaListESPLIB());
                            ESPLIBApplicationStagesAdapter.notifyDataSetChanged();
                            // populateStagesData(actual_response.getStages());
                        }

                        if (mApplicationSectionsAdapter != null && !isNotified)
                            mApplicationSectionsAdapter.notifyDataSetChanged();


                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                registerReciever();
                                // stop_loading_animation(true);
                            }
                        }, 1000);

                        if (ESP_LIB_ChooseLookUpOption.Companion.isOpen())
                            EventBus.getDefault().post(new EventOptions.EventTriggerController());

                    } else {
                        //stop_loading_animation(true);
                        ESP_LIB_CustomLogs.displayLogs(ACTIVITY_NAME + " null response");
                        registerReciever();
                        ESP_LIB_Shared.getInstance().messageBox(getString(R.string.esp_lib_text_some_thing_went_wrong), bContext);
                    }


                }

                @Override
                public void onFailure(Call<List<ESP_LIB_CalculatedMappedFieldsDAO>> call, Throwable t) {
                    // stop_loading_animation(true);
                    ESP_LIB_CustomLogs.displayLogs(ACTIVITY_NAME + " failure response");
                    registerReciever();
                    ESP_LIB_Shared.getInstance().messageBox(getString(R.string.esp_lib_text_some_thing_went_wrong), bContext);
                }
            });

        } catch (Exception ex) {
            ex.printStackTrace();
            //  stop_loading_animation(true);
            registerReciever();
            ESP_LIB_Shared.getInstance().messageBox(getString(R.string.esp_lib_text_some_thing_went_wrong), bContext);
        }
    }//LoggedInUser end


    private void populateCriteriaCalculatedFields(ESP_LIB_CalculatedMappedFieldsDAO ESPLIBCalculatedMappedFieldsDAO) {

        if (ESPLIBApplicationStagesAdapter.getCriteriaAdapterESPLIB() != null &&
                ESPLIBApplicationStagesAdapter.getCriteriaAdapterESPLIB().getCriteriaListESPLIB() != null) {
            for (int y = 0; y < ESPLIBApplicationStagesAdapter.getCriteriaAdapterESPLIB().getCriteriaListESPLIB().size(); y++) {
                ESP_LIB_DynamicStagesCriteriaListDAO ESPLIBDynamicStagesCriteriaListDAO = ESPLIBApplicationStagesAdapter.getCriteriaAdapterESPLIB().getCriteriaListESPLIB().get(y);
                ESP_LIB_DynamicFormDAO form = ESPLIBDynamicStagesCriteriaListDAO.getForm();
                if (form.getSections() != null) {
                    for (int r = 0; r < form.getSections().size(); r++) {
                        ESP_LIB_DynamicFormSectionDAO updatedSection = form.getSections().get(r);
                        if (updatedSection.getFieldsCardsList().size() > 0) {
                            //For Setting InstancesList
                            //   for (DynamicFormSectionFieldsCardsDAO dynamicFormSectionFieldsCardsDAO : updatedSection.getFieldsCardsList()) {
                            for (int g = 0; g < updatedSection.getFieldsCardsList().size(); g++) {

                                if (ESPLIBCalculatedMappedFieldsDAO.getSectionIndex() == g) {
                                    ESP_LIB_DynamicFormSectionFieldsCardsDAO ESPLIBDynamicFormSectionFieldsCardsDAO = updatedSection.getFieldsCardsList().get(g);
                                    if (ESPLIBDynamicFormSectionFieldsCardsDAO.getFields() != null) {
                                        for (int p = 0; p < ESPLIBDynamicFormSectionFieldsCardsDAO.getFields().size(); p++) {
                                            ESP_LIB_DynamicFormSectionFieldDAO ESPLIBDynamicFormSectionFieldDAO = ESPLIBDynamicFormSectionFieldsCardsDAO.getFields().get(p);

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
                                                    String calculatedDisplayDate = ESP_LIB_Shared.getInstance().getDisplayDate(bContext, ESPLIBCalculatedMappedFieldsDAO.getValue(), false);
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
        }
    }

    private ESP_LIB_DynamicResponseDAO getCriteriaFormPost() {
        ESP_LIB_DynamicResponseDAO actual_response_temp = null;
        if (ESPLIBApplicationStagesAdapter.getCriteriaAdapterESPLIB() != null) {

            List<ESP_LIB_DynamicStagesCriteriaListDAO> criteriaList = ESPLIBApplicationStagesAdapter.getCriteriaAdapterESPLIB().getCriteriaListESPLIB();
            actual_response_temp = actual_response;
            if (actual_response_temp.getStages() != null) {
                for (int i = 0; i < actual_response_temp.getStages().size(); i++) {
                    List<ESP_LIB_DynamicStagesCriteriaListDAO> criteriaListTemp = new ArrayList<>();
                    int stageId = actual_response_temp.getStages().get(i).getId();
                    for (int j = 0; j < criteriaList.size(); j++) {
                        ESP_LIB_DynamicStagesCriteriaListDAO ESPLIBDynamicStagesCriteriaListDAO = criteriaList.get(j);
                        List<ESP_LIB_DynamicFormValuesDAO> criteriaFormValues = getCriteriaFormValues(ESPLIBDynamicStagesCriteriaListDAO);
                        ESPLIBDynamicStagesCriteriaListDAO.setFormValues(criteriaFormValues);

                        List<ESP_LIB_DynamicFormSectionDAO> sections = ESPLIBDynamicStagesCriteriaListDAO.getForm().getSections();

                        if (sections != null) {
                            for (int w = 0; w < sections.size(); w++) {
                                sections.get(w).setDynamicStagesCriteriaListDAO(null);
                            }
                        }
                        if (stageId == ESPLIBDynamicStagesCriteriaListDAO.getStageId()) {
                            criteriaListTemp.add(ESPLIBDynamicStagesCriteriaListDAO);
                        }
                    }

                    actual_response_temp.getStages().get(i).setCriteriaList(criteriaListTemp);

                }

            }

        }
        return actual_response_temp;
    }

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
                                if (mApplicationSectionsAdapter != null && isCalculatedField)
                                    mApplicationSectionsAdapter.notifyDataSetChanged();
                            }
                        }, 500);
                    }

                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ESP_LIB_ApplicationFieldsRecyclerAdapter.isCalculatedMappedField = false;
    }
}
