package com.esp.library.exceedersesp.controllers.applications;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.PopupMenu;
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
import com.esp.library.utilities.common.ESP_LIB_Constants;
import com.esp.library.utilities.common.ESP_LIB_CustomLogs;
import com.esp.library.utilities.common.ESP_LIB_EndlessRecyclerViewScrollListener;
import com.esp.library.utilities.common.ESP_LIB_KeyboardUtils;
import com.esp.library.utilities.common.ESP_LIB_ProgressBarAnimation;
import com.esp.library.utilities.common.ESP_LIB_Shared;
import com.esp.library.utilities.common.ESP_LIB_SharedPreference;
import com.esp.library.utilities.setup.applications.ESP_LIB_ApplicationFieldsRecyclerAdapter;
import com.esp.library.utilities.setup.applications.ESP_LIB_ApplicationItemsAdapter;
import com.facebook.shimmer.ShimmerFrameLayout;

import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import utilities.adapters.setup.applications.ESP_LIB_ListAddApplicationSectionsAdapter;
import utilities.adapters.setup.applications.ESP_LIB_ListSubmissionApplicationsAdapter;
import utilities.data.apis.ESP_LIB_APIs;
import utilities.data.applicants.ESP_LIB_ApplicationsDAO;
import utilities.data.applicants.ESP_LIB_SubmittalApplicationsDAO;
import utilities.data.applicants.addapplication.ESP_LIB_CategoryAndDefinationsDAO;
import utilities.data.applicants.addapplication.ESP_LIB_CurrencyDAO;
import utilities.data.applicants.dynamics.ESP_LIB_DyanmicFormSectionFieldDetailsDAO;
import utilities.data.applicants.dynamics.ESP_LIB_DynamicFormDAO;
import utilities.data.applicants.dynamics.ESP_LIB_DynamicFormSectionDAO;
import utilities.data.applicants.dynamics.ESP_LIB_DynamicFormSectionFieldDAO;
import utilities.data.applicants.dynamics.ESP_LIB_DynamicFormSectionFieldsCardsDAO;
import utilities.data.applicants.dynamics.ESP_LIB_DynamicSectionValuesDAO;
import utilities.data.applicants.dynamics.ESP_LIB_DynamicStagesCriteriaListDAO;
import utilities.data.applicants.dynamics.ESP_LIB_DynamicStagesDAO;
import utilities.interfaces.ESP_LIB_CriteriaFieldsListener;
import utilities.interfaces.ESP_LIB_FeedbackSubmissionClick;


public class ESP_LIB_ApplicationFeedDetailScreenActivity extends ESP_LIB_BaseActivity implements ESP_LIB_ApplicationFieldsRecyclerAdapter.ApplicationDetailFieldsAdapterListener,
        ESP_LIB_FeedbackSubmissionClick, ESP_LIB_CriteriaFieldsListener {

    public static String ACTIVITY_NAME = "controllers.applications.ApplicationDetailScreenActivity";
    ESP_LIB_BaseActivity bContext;

    ESP_LIB_ProgressBarAnimation anim = null;
    ESP_LIB_ApplicationsDAO mApplication;
    private ESP_LIB_ListAddApplicationSectionsAdapter mApplicationSectionsAdapter;
    ESP_LIB_DynamicFormSectionFieldDAO fieldToBeUpdated = null;
    private static final int REQUEST_CHOOSER = 12345;
    private static final int REQUEST_LOOKUP = 2;
    String actualResponseJson;
    ESP_LIB_SharedPreference pref;
    boolean isComingFromService, isClosingDatePassed;


    Toolbar toolbar;
    View viewline, viewlinecurve;
    TextView txtfeedminelabel, txtfeedminevalue;
    RecyclerView items_list;
    TextView toolbarheading;
    ImageButton ibToolbarBack;
    ImageView ivrighticon;
    TextView definitionName;
    LinearLayout rejected_status;
    Button addsub_btn;
    RecyclerView app_list;
    RecyclerView submission_app_list;
    LinearLayout llsubmission_app_list;
    View pendinglineview;
    TextView txtrequestdetail;
    LinearLayout lldetail;
    ImageView ivnorecord;
    TextView txtfeedbacknorecord;
    LinearLayout llrows;
    RelativeLayout rldetailrow;
    RelativeLayout rlotherexperts;
    RelativeLayout rlexpertvalues;
    RelativeLayout rlnosubmission;
    RelativeLayout rlsubmissionrow;
    TextView txtsubmissions;
    LinearLayout llmaindetail;
    View topcardview;
    RelativeLayout main_content, rlfeedminerow;
    NestedScrollView nestedscrollview;
    ShimmerFrameLayout shimmer_view_container;
    ImageView ivdetailarrow, ivsubmissionrowarrow;
    SwipeRefreshLayout swipeRefreshLayout;
    TextView txtdetailrowtext;
    TextView submissionallowedtext;
    TextView txtexperts;
    View dividersubmission;


    ArrayList<ESP_LIB_ApplicationsDAO> app_actual_list = new ArrayList<>();
    public boolean isCalculatedField = false;
    public boolean isKeyboardVisible = false;
    AlertDialog pDialog;
    boolean isNotified;
    ESP_LIB_SubmittalApplicationsDAO actual_response = null;
    Call<List<ESP_LIB_CurrencyDAO>> loadStages_call = null;
    Call<ESP_LIB_SubmittalApplicationsDAO> detail_call = null;
    RecyclerView.LayoutManager mApplicationSubmissionLayoutManager;
    int PAGE_NO = 1;
    int PER_PAGE_RECORDS = 12;
    int IN_LIST_RECORDS = 0;
    int SCROLL_TO = 0;
    int TOTAL_RECORDS_AVAILABLE = 0;


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

                /*if (IN_LIST_RECORDS < TOTAL_RECORDS_AVAILABLE) {
                    getSubmissions(true);
                }*/
            }
        });


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        changeStatusBarColor(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.esp_lib_activity_feed_application_detail);
        initailizeIds();
        initailize();
        setGravity();
        loadData();


        txtdetailrowtext.setText(pref.getlabels().getApplication() + " " + getString(R.string.esp_lib_text_details));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
            }
        });


        UpdateTopView();


        try {
            ESP_LIB_Shared.getInstance().createFolder(ESP_LIB_Constants.FOLDER_PATH, ESP_LIB_Constants.FOLDER_NAME, bContext);
        } catch (Exception e) {
            e.printStackTrace();
        }

        addsub_btn.setOnClickListener(v -> {
            SubmitRequest();
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

            showSubmissionList();


        });
        rlsubmissionrow.setEnabled(false);


        ESP_LIB_KeyboardUtils.addKeyboardToggleListener(this, new ESP_LIB_KeyboardUtils.SoftKeyboardToggleListener() {
            @Override
            public void onToggleSoftKeyboard(boolean isVisible) {
                isNotified = isVisible;
                isKeyboardVisible = isVisible;
            }
        });

        ivrighticon.setOnClickListener(view -> {
            showMenu(view, bContext);
        });


    }

    private void showSubmissionList() {
        if (llsubmission_app_list.getVisibility() == View.VISIBLE) {
            /*llsubmission_app_list.setVisibility(View.GONE);
            rlotherexperts.setVisibility(View.GONE);
            dividersubmission.setVisibility(View.GONE);
            ivsubmissionrowarrow.setImageResource(R.drawable.ic_arrow_down);*/
        } else {
            ivsubmissionrowarrow.setImageResource(R.drawable.ic_arrow_up);
            llsubmission_app_list.setVisibility(View.VISIBLE);
            dividersubmission.setVisibility(View.VISIBLE);
            rlotherexperts.setVisibility(View.VISIBLE);
            int mySubmissions = mApplication.getNumberOfSubmissions() - app_actual_list.size();
            if (mySubmissions > 0) {
                rlexpertvalues.setVisibility(View.VISIBLE);
                txtexperts.setText(mySubmissions + " " + getString(R.string.esp_lib_text_other_experts));
            }

        }
        if (app_actual_list.size() == 0)
            rlnosubmission.setVisibility(View.VISIBLE);
        else
            rlnosubmission.setVisibility(View.GONE);
    }

    private void refreshData() {
        PAGE_NO = 1;
        PER_PAGE_RECORDS = 12;
        IN_LIST_RECORDS = 0;
        TOTAL_RECORDS_AVAILABLE = 0;
        app_actual_list.clear();
        loadData();
    }

    private void SubmitRequest() {
        ESP_LIB_CategoryAndDefinationsDAO categoryAndDefinationsDAO = new ESP_LIB_CategoryAndDefinationsDAO();
        if (categoryAndDefinationsDAO.getParentApplicationInfo() != null) {
            categoryAndDefinationsDAO.getParentApplicationInfo().setApplicationId(mApplication.getParentApplicationId());
            categoryAndDefinationsDAO.getParentApplicationInfo().setTitleFieldValue(mApplication.getDefinitionName());
        }
        categoryAndDefinationsDAO.setParentApplicationId(mApplication.getParentApplicationId());
        categoryAndDefinationsDAO.setCategory(mApplication.getCategory());
        categoryAndDefinationsDAO.setId(mApplication.getId());
        categoryAndDefinationsDAO.setName(mApplication.getDefinitionName());
        Bundle bundle = new Bundle();
        bundle.putSerializable(ESP_LIB_CategoryAndDefinationsDAO.Companion.getBUNDLE_KEY(), categoryAndDefinationsDAO);
        ESP_LIB_Shared.getInstance().callIntentWithResult(ESP_LIB_AddApplicationsFromScreenActivity.class, this, bundle, 2);
    }

    private void initailizeIds() {

        toolbar = findViewById(R.id.gradienttoolbar);
        rlfeedminerow = findViewById(R.id.rlfeedminerow);
        rlfeedminerow.setPadding(17, 30, 0, 0);
        viewline = findViewById(R.id.viewline);
        viewlinecurve = findViewById(R.id.viewlinecurve);
        txtfeedminelabel = findViewById(R.id.txtfeedminelabel);
        txtfeedminevalue = findViewById(R.id.txtfeedminevalue);
        txtexperts = findViewById(R.id.txtexperts);
        toolbarheading = findViewById(R.id.toolbarheading);
        ibToolbarBack = findViewById(R.id.ibToolbarBack);
        ivrighticon = findViewById(R.id.ivrighticon);
        definitionName = findViewById(R.id.definitionName);
        definitionName.setPadding(17, 20, 0, 0);
        rejected_status = findViewById(R.id.rejected_status);
        addsub_btn = findViewById(R.id.addsub_btn);
        app_list = findViewById(R.id.app_list);
        submission_app_list = findViewById(R.id.submission_app_list);
        llsubmission_app_list = findViewById(R.id.llsubmission_app_list);
        pendinglineview = findViewById(R.id.pendinglineview);
        txtrequestdetail = findViewById(R.id.txtrequestdetail);
        lldetail = findViewById(R.id.lldetail);
        ivnorecord = findViewById(R.id.ivnorecord);
        txtfeedbacknorecord = findViewById(R.id.txtfeedbacknorecord);
        llrows = findViewById(R.id.llrows);
        rldetailrow = findViewById(R.id.rldetailrow);
        rlotherexperts = findViewById(R.id.rlotherexperts);
        rlexpertvalues = findViewById(R.id.rlexpertvalues);
        rlnosubmission = findViewById(R.id.rlnosubmission);
        rlsubmissionrow = findViewById(R.id.rlsubmissionrow);
        txtsubmissions = findViewById(R.id.txtsubmissions);
        llmaindetail = findViewById(R.id.llmaindetail);
        topcardview = findViewById(R.id.topcardview);
        main_content = findViewById(R.id.main_content);
        nestedscrollview = findViewById(R.id.nestedscrollview);
        shimmer_view_container = findViewById(R.id.shimmer_view_container);
        ivdetailarrow = findViewById(R.id.ivdetailarrow);
        ivsubmissionrowarrow = findViewById(R.id.ivsubmissionrowarrow);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        txtdetailrowtext = findViewById(R.id.txtdetailrowtext);
        submissionallowedtext = findViewById(R.id.submissionallowedtext);
        dividersubmission = findViewById(R.id.dividersubmission);
        items_list = findViewById(R.id.items_list);
        items_list.setPadding(17, 0, 0, 0);

        viewline.setVisibility(View.VISIBLE);
        viewlinecurve.setVisibility(View.VISIBLE);
        rlfeedminerow.setVisibility(View.VISIBLE);
        txtfeedminelabel.setText(getString(R.string.esp_lib_text_feed));
        ivrighticon.setImageResource(R.drawable.esp_lib_drawable_ic_vertical_dots);
    }

    private void initailize() {
        bContext = ESP_LIB_ApplicationFeedDetailScreenActivity.this;
        pref = new ESP_LIB_SharedPreference(bContext);
        pDialog = ESP_LIB_Shared.getInstance().setProgressDialog(bContext);

        RecyclerView.LayoutManager mApplicationLayoutManager = new LinearLayoutManager(bContext);
        app_list.setHasFixedSize(true);
        app_list.setLayoutManager(mApplicationLayoutManager);
        app_list.setItemAnimator(new DefaultItemAnimator());

        items_list.setHasFixedSize(true);
        items_list.setLayoutManager(new LinearLayoutManager(bContext, LinearLayoutManager.VERTICAL, false));
        items_list.setItemAnimator(new DefaultItemAnimator());

        mApplicationSubmissionLayoutManager = new LinearLayoutManager(bContext);
        submission_app_list.setHasFixedSize(true);
        submission_app_list.setLayoutManager(mApplicationSubmissionLayoutManager);
        submission_app_list.setItemAnimator(new DefaultItemAnimator());


        setupToolbar(toolbar);

        int themeColor = ContextCompat.getColor(bContext, R.color.colorPrimaryDark);
        swipeRefreshLayout.setColorSchemeColors(themeColor, themeColor, themeColor);

    }

    private void showMenu(View v, Context mContext) {


        PopupMenu popup = new PopupMenu(mContext, v);
        popup.inflate(R.menu.menu_list_item_add_application_fields);
        popup.setGravity(Gravity.CENTER);
        Menu menuOpts = popup.getMenu();
        MenuItem remove_action = menuOpts.findItem(R.id.action_remove);
        remove_action.setTitle(getString(R.string.esp_lib_text_dismiss));
        popup.setOnMenuItemClickListener(menuItem -> {

            if (menuItem.getItemId() == R.id.action_remove) {
                dismissFeedStages();
            }
            return false;
        });
        popup.show();

    }


    public void dismissFeedStages() {
        start_loading_animation(false);
        try {
            ESP_LIB_APIs apis = ESP_LIB_Shared.getInstance().retroFitObject(bContext);
            Call<Object> objectCall = apis.getdismissApplication(mApplication.getParentApplicationId(), getString(R.string.esp_lib_text_feed));
            objectCall.enqueue(new Callback<Object>() {
                @Override
                public void onResponse(Call<Object> call, Response<Object> response) {

                    stop_loading_animation();
                    ivrighticon.setVisibility(View.GONE);
                }

                @Override
                public void onFailure(Call<Object> call, Throwable t) {
                    stop_loading_animation();
                }
            });

        } catch (Exception ex) {
            stop_loading_animation();
        }
    }//LoggedInUser end

    private void detailClick() {
        if (llmaindetail.getVisibility() == View.VISIBLE) {
            llrows.setVisibility(View.VISIBLE);
            llmaindetail.setVisibility(View.GONE);
            if (actual_response != null) {
                rlsubmissionrow.setVisibility(View.VISIBLE);
            }
            ivdetailarrow.setImageResource(R.drawable.ic_arrow_down);
            rldetailrow.setBackgroundResource(R.drawable.esp_lib_drawable_shadow);
        } else {
            rldetailrow.setBackgroundResource(R.drawable.esp_lib_drawable_draw_bg_white);
            ivdetailarrow.setImageResource(R.drawable.ic_arrow_up);

            llmaindetail.setVisibility(View.VISIBLE);

            txtrequestdetail.setVisibility(View.GONE);
            lldetail.setVisibility(View.GONE);

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

        toolbarheading.setText(pref.getlabels().getApplication() + " " + getString(R.string.esp_lib_text_details));
        Bundle bnd = getIntent().getExtras();
        if (bnd != null) {
            mApplication = (ESP_LIB_ApplicationsDAO) bnd.getSerializable(ESP_LIB_ApplicationsDAO.Companion.getBUNDLE_KEY());

        }
    }


    private void GetApplicationFrom(ESP_LIB_ApplicationsDAO mApplication) {


        try {

            final ESP_LIB_APIs apis = ESP_LIB_Shared.getInstance().retroFitObject(bContext);
            detail_call = apis.getSubmittalForm(mApplication.getId(), mApplication.getParentApplicationId());
            detail_call.enqueue(new Callback<ESP_LIB_SubmittalApplicationsDAO>() {
                @Override
                public void onResponse(Call<ESP_LIB_SubmittalApplicationsDAO> call, Response<ESP_LIB_SubmittalApplicationsDAO> response) {
                    stop_loading_animation();


                    if (response != null && response.body() != null) {

                        if (!response.body().isDismissed())
                            ivrighticon.setVisibility(View.VISIBLE);

                        actual_response = response.body();
                        actualResponseJson = actual_response.toJson();
                        rlsubmissionrow.setVisibility(View.VISIBLE);
                        //  if (ESP_LIB_Shared.getInstance().hasLinkDefinitionId(response.body())) {
                        if (response.body().getMySubmissions() != null) {
                            // rlsubmissionrow.setVisibility(View.VISIBLE);
                            app_actual_list.clear();
                            app_actual_list.addAll(response.body().getMySubmissions());
                            ESP_LIB_ListSubmissionApplicationsAdapter adapter = new ESP_LIB_ListSubmissionApplicationsAdapter(app_actual_list, bContext, "", false);
                            adapter.isClickable(false);
                            submission_app_list.setAdapter(adapter);
                            txtsubmissions.setText(getString(R.string.esp_lib_text_submissions) + " (" + app_actual_list.size() + "/" + response.body().getApplicationCard().getNumberOfSubmissions() + ")");


                            //getSubmissions(false);
                        }/* else {
                            rlsubmissionrow.setVisibility(View.GONE);
                        }*/

                        showTopCardData(response);


                        if ((!response.body().isMultipleSubmissionsAllowed() && response.body().getMySubmissions().size() == 0) ||
                                response.body().isMultipleSubmissionsAllowed())
                            addsub_btn.setVisibility(View.VISIBLE);
                        else if (!response.body().isMultipleSubmissionsAllowed() && response.body().getMySubmissions().size() > 0)
                            addsub_btn.setVisibility(View.GONE);

                        ArrayList<ESP_LIB_DynamicFormSectionDAO> sections;
                        List<ESP_LIB_DynamicSectionValuesDAO> sectionsValues = actual_response.getSectionValues();

                        if (sectionsValues != null)
                            sections = GetFieldsCards(response.body().getForm(), sectionsValues);
                        else
                            sections = GetOnlyFieldsCards(response.body().getForm(), null);

                        if (sections != null && sections.size() > 0) {
                            mApplicationSectionsAdapter = new ESP_LIB_ListAddApplicationSectionsAdapter(sections, bContext, "", true, false);
                            mApplicationSectionsAdapter.setActualResponseJson(actualResponseJson);
                            app_list.setAdapter(mApplicationSectionsAdapter);
                            mApplicationSectionsAdapter.notifyDataSetChanged();
                            mApplicationSectionsAdapter.setmApplicationFieldsAdapterListenerFeed(ESP_LIB_ApplicationFeedDetailScreenActivity.this);
                        } else {

                            //  SubmitRequest();

                            //   UnSuccessResponse();
                        }


                    }
                    // rlsubmissionrow.performClick();
                    showSubmissionList();
                    ivsubmissionrowarrow.setVisibility(View.GONE);

                }

                @Override
                public void onFailure(Call<ESP_LIB_SubmittalApplicationsDAO> call, Throwable t) {
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


    private void showTopCardData(Response<ESP_LIB_SubmittalApplicationsDAO> response) {
        ESP_LIB_ApplicationsDAO applicationCard = response.body().getApplicationCard();
        if (applicationCard != null && applicationCard.getSummary() != null) {

            ESP_LIB_ApplicationItemsAdapter itemsAdapter = new ESP_LIB_ApplicationItemsAdapter(applicationCard.getSummary().getCardValues(), bContext);
            items_list.setAdapter(itemsAdapter);

            if (applicationCard.getSummary().getName() != null)
                definitionName.setText(applicationCard.getSummary().getName().trim());

            txtfeedminevalue.setText(applicationCard.getSummary().getTitle());
            viewline.setVisibility(View.VISIBLE);
            viewlinecurve.setVisibility(View.VISIBLE);
            rlfeedminerow.setVisibility(View.VISIBLE);
            if (applicationCard.getSummary().isFeed()) {
                txtfeedminelabel.setText(getString(R.string.esp_lib_text_feed));
                txtfeedminelabel.setTextColor(ContextCompat.getColor(bContext, R.color.esp_lib_color_yellowishOrange));
                viewline.setBackgroundColor(ContextCompat.getColor(bContext, R.color.esp_lib_color_yellowishOrange));
                viewlinecurve.setBackgroundColor(ContextCompat.getColor(bContext, R.color.esp_lib_color_yellowishOrange));
            } else if (applicationCard.getSummary().isMine()) {
                txtfeedminelabel.setText(getString(R.string.esp_lib_text_mine));
                txtfeedminelabel.setTextColor(ContextCompat.getColor(bContext, R.color.esp_lib_color_blue));
                viewline.setBackgroundColor(ContextCompat.getColor(bContext, R.color.esp_lib_color_blue));
                viewlinecurve.setBackgroundColor(ContextCompat.getColor(bContext, R.color.esp_lib_color_blue));
            }/*else if (!applicationCard.getSummary().isMine() && (applicationCard.getSummary().getTitle() != null &&
                    !applicationCard.getSummary().getTitle().isEmpty())) {
                rlfeedminerow.setVisibility(View.VISIBLE);
                txtfeedminelabel.setVisibility(View.GONE);
                // ivcircledot.setVisibility(View.GONE);
                txtfeedminevalue.setText(applicationCard.getSummary().getTitle());
            }*/ else
                definitionName.setPadding(17, 30, 0, 15);
        }
    }

   /* private void getSubmissions(Boolean isLoadMore) {

        start_loading_animation(true);

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

            final ESP_LIB_APIs apis = ESP_LIB_Shared.getInstance().retroFitObject(bContext);

            Call<ESP_LIB_ResponseApplicationsDAO> call = apis.getUserApplicationsV3(daoESPLIB);
            call.enqueue(new Callback<ESP_LIB_ResponseApplicationsDAO>() {
                @Override
                public void onResponse(Call<ESP_LIB_ResponseApplicationsDAO> call, Response<ESP_LIB_ResponseApplicationsDAO> response) {

                    stop_loading_animation();

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
                                    adapter.isClickable(false);
                                    submission_app_list.setAdapter(adapter);
                                    submission_app_list.scrollToPosition(SCROLL_TO - 5);
                                } catch (Exception e) {
                                }

                            } else {
                                try {
                                    app_actual_list.addAll(response.body().getApplications());
                                    ESP_LIB_ListSubmissionApplicationsAdapter adapter = new ESP_LIB_ListSubmissionApplicationsAdapter(app_actual_list, bContext, "");
                                    adapter.isClickable(false);
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
                    } else
                        ESP_LIB_Shared.getInstance().messageBox(getString(R.string.esp_lib_text_some_thing_went_wrong), bContext);

                    if (app_actual_list.size() == 0)
                        rlnosubmission.setVisibility(View.VISIBLE);
                    else
                        rlnosubmission.setVisibility(View.GONE);
                }

                @Override
                public void onFailure(Call<ESP_LIB_ResponseApplicationsDAO> call, Throwable t) {
                    stop_loading_animation();
                    ESP_LIB_Shared.getInstance().messageBox(getString(R.string.esp_lib_text_some_thing_went_wrong), bContext);
                }
            });

        } catch (Exception ex) {
            ex.printStackTrace();
            stop_loading_animation();
            ESP_LIB_Shared.getInstance().messageBox(getString(R.string.esp_lib_text_some_thing_went_wrong), bContext);


        }
    }*/


    public void LoadStages() {
        start_loading_animation(false);
        try {
            loadStages_call = ESP_LIB_Shared.getInstance().retroFitObject(bContext).getCurrency();
            loadStages_call.enqueue(new Callback<List<ESP_LIB_CurrencyDAO>>() {
                @Override
                public void onResponse(Call<List<ESP_LIB_CurrencyDAO>> call, Response<List<ESP_LIB_CurrencyDAO>> response) {

                    if (response.body() != null && response.body().size() > 0) {
                        ESP_LIB_ESPApplication.getInstance().setCurrencies(response.body());

                        if (mApplication != null) {

                            if (ESP_LIB_Shared.getInstance().isWifiConnected(bContext)) {
                                GetApplicationFrom(mApplication);
                            } else {
                                ESP_LIB_Shared.getInstance().showAlertMessage(getString(R.string.esp_lib_text_internet_error_heading), getString(R.string.esp_lib_text_internet_connection_error), bContext);
                            }

                        }

                    } else
                        stop_loading_animation();
                }

                @Override
                public void onFailure(Call<List<ESP_LIB_CurrencyDAO>> call, Throwable t) {
                    stop_loading_animation();
                }
            });

        } catch (Exception ex) {
            stop_loading_animation();
        }
    }//LoggedInUser end


    private ArrayList<ESP_LIB_DynamicFormSectionDAO> GetFieldsCards(ESP_LIB_DynamicFormDAO response, List<ESP_LIB_DynamicSectionValuesDAO> sectionsValues) {
        ArrayList<ESP_LIB_DynamicFormSectionDAO> sections = new ArrayList<>();

        if (response.getSections() != null) {
            for (int i = 0; i < response.getSections().size(); i++) {
                ESP_LIB_DynamicFormSectionDAO sectionDAO = response.getSections().get(i);
                if (sectionDAO.getType() == 2) {
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

                                                    if (tempField.getType() == 11 && tempField.getValue() != null && !tempField.getValue().contains(":"))
                                                        tempField.setValue("");

                                                    if (tempField.getSectionCustomFieldId() == instanceValue.getSectionCustomFieldId()) {
                                                        String value = instanceValue.getValue();
                                                        if (value != null && !value.isEmpty()) {
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
        }

        ArrayList<ESP_LIB_DynamicFormSectionDAO> ESPLIBDynamicFormSectionDAOS = GetOnlyFieldsCards(response, sectionsValues);
        sections.clear();
        sections.addAll(ESPLIBDynamicFormSectionDAOS);
        return sections;
    }

    private ArrayList<ESP_LIB_DynamicFormSectionDAO> GetOnlyFieldsCards(ESP_LIB_DynamicFormDAO response, List<ESP_LIB_DynamicSectionValuesDAO> sectionsValues) {

        ArrayList<ESP_LIB_DynamicFormSectionDAO> sections = new ArrayList<>();

        if (response.getSections() != null) {
            //Setting Sections With FieldsCards.
            for (ESP_LIB_DynamicFormSectionDAO sectionDAO : response.getSections()) {

                if (sectionDAO.getType() == 2) {
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

                if (fields.get(h).getType() == 11 && fields.get(h).getValue() != null && !fields.get(h).getValue().contains(":"))
                    fields.get(h).setValue("");

                tempFields.add(fields.get(h));
            }
        }
        return tempFields;
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


    @Override
    public void onFieldValuesChanged() {

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


    private void setGravity() {
        if (pref.getLanguage().equalsIgnoreCase("ar")) {
            definitionName.setGravity(Gravity.RIGHT);

        } else {
            definitionName.setGravity(Gravity.LEFT);
        }
    }

    @Override
    public void feedbackClick(boolean isAccepted, @Nullable ESP_LIB_DynamicStagesCriteriaListDAO criteriaListDAO, @Nullable ESP_LIB_DynamicStagesDAO ESPLIBDynamicStagesDAO, int position) {


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

    private void stop_loading_animation() {
        swipeRefreshLayout.setRefreshing(false);
        try {
            if (pDialog.isShowing())
                pDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }

        shimmer_view_container.setVisibility(View.GONE);


    }

    @Override
    public void onBackPressed() {

        if (shimmer_view_container.getVisibility() == View.VISIBLE) {
            if (detail_call != null)
                detail_call.cancel();
            if (loadStages_call != null)
                loadStages_call.cancel();
        }
        super.onBackPressed();

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
    protected void onResume() {
        super.onResume();
        if (ESP_LIB_ESPApplication.getInstance().isGoToMainScreen())
            refreshData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ESP_LIB_ApplicationFieldsRecyclerAdapter.isCalculatedMappedField = false;
    }
}
