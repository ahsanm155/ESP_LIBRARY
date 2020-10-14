package com.esp.library.exceedersesp.controllers.applications.filters;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.esp.library.R;
import com.esp.library.exceedersesp.ESP_LIB_BaseActivity;
import com.esp.library.exceedersesp.ESP_LIB_ESPApplication;
import com.esp.library.exceedersesp.SingleController.CompRoot;
import com.esp.library.exceedersesp.controllers.applications.ESP_LIB_ApplicationActivityTabs;
import com.esp.library.exceedersesp.fragments.applications.ESP_LIB_UsersApplicationsFragment;
import com.esp.library.utilities.common.ESP_LIB_Shared;
import com.esp.library.utilities.common.ESP_LIB_SharedPreference;
import com.esp.library.utilities.customevents.EventOptions;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.tabs.TabLayout;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import utilities.data.apis.ESP_LIB_APIs;
import utilities.data.filters.ESP_LIB_FilterDAO;
import com.esp.library.utilities.data.filters.ESP_LIB_FilterDefinitionSortDAO;
import com.esp.library.utilities.interfaces.ESP_LIB_FilterListener;

public class ESP_LIB_FilterScreenActivity extends ESP_LIB_BaseActivity implements ESP_LIB_FilterListener {

    public static String ACTIVITY_NAME = "controllers.applications.filters.FilterScreenActivity";
    ESP_LIB_BaseActivity bContext;
    List<AppCompatCheckBox> statuses_checkboxes;
    ESP_LIB_SharedPreference pref;

    Toolbar toolbar;
    ImageButton ibToolbarBack;
    TextView toolbarheading;
    LinearLayout status_layout;
    LinearLayout lldivider;
    LinearLayout status_row;
    LinearLayout llsort;
    NestedScrollView nestedscrollview;
    TextView statuses;
    ImageView status_btn;
    TextView txtbystatus;
    RecyclerView rvdefintionList;
    RecyclerView rvsortbyList;
    LinearLayout definition_row;
    LinearLayout sortby_row;
    ShimmerFrameLayout shimmer_view_container;
    ImageView definition_btn;
    ImageView sort_btn;
    TextView txtdefinitionstatuses;
    TextView txtsortbystatuses;
    List<String> statusesList = new ArrayList<>();
    public ESP_LIB_FilterDAO ESPLIBFilterDao = null;
    public static boolean isOpenFilterApplied = false;
    public static boolean isCloseFilterApplied = false;
    ESP_LIB_FilterDefinitionAdapter ESPLIBFilterDefinitionAdapter;
    ESP_LIB_FilterSortByAdapter ESPLIBFilterSortByAdapter;
    public static ESP_LIB_FilterScreenActivity filterScreenActivity = null;

    public static ESP_LIB_FilterScreenActivity getInstance() {
        if (filterScreenActivity == null) {
            return filterScreenActivity = new ESP_LIB_FilterScreenActivity();
        } else {

            return filterScreenActivity;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(ESP_LIB_ESPApplication.getInstance().getApplicationTheme());
        changeStatusBarColor(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.esp_lib_activity_application_filter);
        initialize();
        setGravity();


        boolean activityShowing = ESP_LIB_UsersApplicationsFragment.Companion.isShowingActivity();
        if (!activityShowing) {
            TabLayout tabLayout = ESP_LIB_ApplicationActivityTabs.Companion.getTabLayout();
            if (tabLayout != null) {
                int tab_position = tabLayout.getSelectedTabPosition();
                if (tab_position == 0) {
                    status_row.setVisibility(View.GONE);
                    lldivider.setVisibility(View.GONE);
                    llsort.setVisibility(View.VISIBLE);
                } else {
                    status_row.setVisibility(View.VISIBLE);
                    lldivider.setVisibility(View.VISIBLE);
                    llsort.setVisibility(View.GONE);
                }

            }
        }


        status_row.setOnClickListener(v -> {

            String row_status = (String) status_layout.getTag();

            if (row_status.equals(getString(R.string.esp_lib_text_hidden))) {
                status_layout.setTag(getString(R.string.esp_lib_text_shown));
                status_layout.setVisibility(View.VISIBLE);
                status_btn.setBackground(bContext.getResources().getDrawable(R.drawable.ic_arrow_up));
            } else if (row_status.equals(getString(R.string.esp_lib_text_shown))) {
                status_layout.setTag(getString(R.string.esp_lib_text_hidden));
                status_layout.setVisibility(View.GONE);
                status_btn.setBackground(bContext.getResources().getDrawable(R.drawable.ic_arrow_down));
            }

        });


        sortby_row.setOnClickListener(v -> {

            if (rvsortbyList.getVisibility() == View.GONE) {
                rvsortbyList.setVisibility(View.VISIBLE);
                sort_btn.setBackground(bContext.getResources().getDrawable(R.drawable.ic_arrow_up));
            } else {
                rvsortbyList.setVisibility(View.GONE);
                sort_btn.setBackground(bContext.getResources().getDrawable(R.drawable.ic_arrow_down));
            }

        });

        definition_row.setOnClickListener(v -> {

            if (rvdefintionList.getVisibility() == View.GONE) {
                rvdefintionList.setVisibility(View.VISIBLE);
                definition_btn.setBackground(bContext.getResources().getDrawable(R.drawable.ic_arrow_up));
            } else {
                rvdefintionList.setVisibility(View.GONE);
                definition_btn.setBackground(bContext.getResources().getDrawable(R.drawable.ic_arrow_down));
            }

        });
        initiateStatus();
        getDefinitionList();
        sorByList();

    }

    private void initialize() {
        bContext = ESP_LIB_FilterScreenActivity.this;
        pref = new ESP_LIB_SharedPreference(bContext);
        toolbar = findViewById(R.id.gradienttoolbar);
        ibToolbarBack = findViewById(R.id.ibToolbarBack);
        toolbarheading = findViewById(R.id.toolbarheading);
        toolbarheading.setText(getString(R.string.esp_lib_text_filters));
        status_layout = findViewById(R.id.status_layout);
        nestedscrollview = findViewById(R.id.nestedscrollview);
        lldivider = findViewById(R.id.lldivider);
        status_row = findViewById(R.id.status_row);
        llsort = findViewById(R.id.llsort);
        statuses = findViewById(R.id.statuses);
        status_btn = findViewById(R.id.status_btn);
        txtbystatus = findViewById(R.id.txtbystatus);
        rvdefintionList = findViewById(R.id.rvdefintionList);
        rvsortbyList = findViewById(R.id.rvsortbyList);
        definition_row = findViewById(R.id.definition_row);
        sortby_row = findViewById(R.id.sort_row);
        shimmer_view_container = findViewById(R.id.shimmer_view_container);
        definition_btn = findViewById(R.id.definition_btn);
        sort_btn = findViewById(R.id.sort_btn);
        txtdefinitionstatuses = findViewById(R.id.txtdefinitionstatuses);
        txtsortbystatuses = findViewById(R.id.txtsortbystatuses);

        status_layout.setTag(getString(R.string.esp_lib_text_hidden));
        setSupportActionBar(toolbar);
        ibToolbarBack.setOnClickListener(v -> onBackPressed());

        RecyclerView.LayoutManager mApplicationLayoutManager = new LinearLayoutManager(bContext);
        rvdefintionList.setHasFixedSize(true);
        rvdefintionList.setLayoutManager(mApplicationLayoutManager);
        rvdefintionList.setItemAnimator(new DefaultItemAnimator());


        RecyclerView.LayoutManager mApplicationLayoutManagerSortby = new LinearLayoutManager(bContext);
        rvsortbyList.setHasFixedSize(true);
        rvsortbyList.setLayoutManager(mApplicationLayoutManagerSortby);
        rvsortbyList.setItemAnimator(new DefaultItemAnimator());

        ESPLIBFilterDao = ESP_LIB_ESPApplication.getInstance().getFilter();

        if (ESPLIBFilterDao != null) {
            if (ESPLIBFilterDao.getStatuses() == null) {
                ArrayList list = new ArrayList<String>();
                List<String> statuses = ESPLIBFilterDao.getStatuses();
                list.add("1");
                list.add("2");
                list.add("3");
                list.add("4");
                list.add("5");
                statusesList.addAll(list);
                ESPLIBFilterDao.setStatuses(statusesList);
            } else
                statusesList = ESPLIBFilterDao.getStatuses();
        }


    }

    private void sorByList() {
        List<ESP_LIB_FilterDefinitionSortDAO> ESPLIBFilterDefinitionSortDAOSList = new ArrayList<>();
        ESPLIBFilterDefinitionSortDAOSList.add(new ESP_LIB_FilterDefinitionSortDAO(getString(R.string.esp_lib_text_suboldestfirst), false, 1));
        ESPLIBFilterDefinitionSortDAOSList.add(new ESP_LIB_FilterDefinitionSortDAO(getString(R.string.esp_lib_text_subnewestfirst), false, 4));
        ESPLIBFilterDefinitionSortDAOSList.add(new ESP_LIB_FilterDefinitionSortDAO(getString(R.string.esp_lib_text_assignoldestfirst), false, 2));
        ESPLIBFilterDefinitionSortDAOSList.add(new ESP_LIB_FilterDefinitionSortDAO(getString(R.string.esp_lib_text_assignnewestfirst), false, 5));
        ESPLIBFilterDefinitionSortDAOSList.add(new ESP_LIB_FilterDefinitionSortDAO(getString(R.string.esp_lib_text_duefirst), false, 6));
        ESPLIBFilterDefinitionSortDAOSList.add(new ESP_LIB_FilterDefinitionSortDAO(getString(R.string.esp_lib_text_duelast), false, 7));


        preSelectedSortBy(ESPLIBFilterDefinitionSortDAOSList);

    }

    private void preSelectedSortBy(List<ESP_LIB_FilterDefinitionSortDAO> filterDefinitionSortDAOSList) {
        for (int i = 0; i < filterDefinitionSortDAOSList.size(); i++) {
            if (ESPLIBFilterDao != null && ESPLIBFilterDao.getSortBy() == filterDefinitionSortDAOSList.get(i).getId()) {
                filterDefinitionSortDAOSList.get(i).setCheck(true);
                txtsortbystatuses.setText(filterDefinitionSortDAOSList.get(i).getName());
            } else
                filterDefinitionSortDAOSList.get(i).setCheck(false);
        }
        ESPLIBFilterSortByAdapter = new ESP_LIB_FilterSortByAdapter(filterDefinitionSortDAOSList, bContext);
        rvsortbyList.setAdapter(ESPLIBFilterSortByAdapter);

    }

    public void getDefinitionList() {
        ArrayList<String> list = new ArrayList<>();
        list.add("0");

        ESP_LIB_FilterDAO cloneFilter = new ESP_LIB_FilterDAO();
        ESP_LIB_FilterDAO filter = ESPLIBFilterDao;
        cloneFilter.setSearch("");
        cloneFilter.setStatuses(list);
        cloneFilter.setMySpace(true);
        cloneFilter.setApplicantId(filter.getApplicantId());


        start_loading_animation();
        ESP_LIB_FilterDAO ESPLIBFilterDAO = ESP_LIB_Shared.getInstance().CloneFilter(cloneFilter);
        ESPLIBFilterDAO.setMySpace(true);
        ESP_LIB_APIs apis = new CompRoot().getService(bContext);
        Call<List<ESP_LIB_FilterDefinitionSortDAO>> definition_call = apis.getDefinitioList(ESPLIBFilterDAO);
        definition_call.enqueue(new Callback<List<ESP_LIB_FilterDefinitionSortDAO>>() {
            @Override
            public void onResponse(Call<List<ESP_LIB_FilterDefinitionSortDAO>> call, Response<List<ESP_LIB_FilterDefinitionSortDAO>> response) {

                if (response != null && response.body() != null) {
                    List<Integer> applcaition_checkboxes = new ArrayList<>();
                    List<ESP_LIB_FilterDefinitionSortDAO> ESPLIBFilterDefinitionSortDAOSList = new ArrayList<>();
                    for (int i = 0; i < response.body().size(); i++) {
                        ESP_LIB_FilterDefinitionSortDAO ESPLIBFilterDefinitionSortDAO = response.body().get(i);
                        ESPLIBFilterDefinitionSortDAO.setId(ESPLIBFilterDefinitionSortDAO.getId());
                        ESPLIBFilterDefinitionSortDAO.setName(ESPLIBFilterDefinitionSortDAO.getName());
                        if (ESPLIBFilterDao != null && (ESPLIBFilterDao.getDefinitionIds() == null ||
                                ESPLIBFilterDao.getDefinitionIds().size() == 0)) {
                            ESPLIBFilterDefinitionSortDAO.setCheck(true);
                        }

                        applcaition_checkboxes.add(ESPLIBFilterDefinitionSortDAO.getId());
                        ESPLIBFilterDefinitionSortDAOSList.add(ESPLIBFilterDefinitionSortDAO);
                    }

                    if (ESPLIBFilterDefinitionSortDAOSList.size() > 0) {
                        ESP_LIB_FilterDefinitionSortDAO ESPLIBFilterDefinitionSortDAO = new ESP_LIB_FilterDefinitionSortDAO();
                        ESPLIBFilterDefinitionSortDAO.setId(1122); // 1122 = id of All
                        ESPLIBFilterDefinitionSortDAO.setName(getString(R.string.esp_lib_text_all));
                        if (ESPLIBFilterDao != null && (ESPLIBFilterDao.getDefinitionIds() == null ||
                                ESPLIBFilterDao.getDefinitionIds().size() == 0)) {
                            ESPLIBFilterDefinitionSortDAO.setCheck(true);
                        }
                        ESPLIBFilterDefinitionSortDAOSList.add(0, ESPLIBFilterDefinitionSortDAO);
                        ESPLIBFilterDefinitionAdapter = new ESP_LIB_FilterDefinitionAdapter(ESPLIBFilterDefinitionSortDAOSList, bContext);
                        rvdefintionList.setAdapter(ESPLIBFilterDefinitionAdapter);


                        if (ESPLIBFilterDefinitionSortDAOSList.size() > 0 && ESPLIBFilterDefinitionSortDAOSList.get(0).isCheck()) {
                            txtdefinitionstatuses.setText(getString(R.string.esp_lib_text_all));
                            /*ArrayList<Integer> tempList = new ArrayList<>();
                            tempList.add(0);
                            filterDao.setDefinitionIds(tempList);*/
                        }

                        preSelectedValues(ESPLIBFilterDefinitionSortDAOSList, applcaition_checkboxes);


                    }

                    stop_loading_animation();
                } else {
                    stop_loading_animation();
                    ESP_LIB_Shared.getInstance().showAlertMessage(pref.getlabels().getApplication(), getString(R.string.esp_lib_text_some_thing_went_wrong), bContext);
                }
            }

            @Override
            public void onFailure(Call<List<ESP_LIB_FilterDefinitionSortDAO>> call, Throwable t) {
                stop_loading_animation();
                ESP_LIB_Shared.getInstance().showAlertMessage(pref.getlabels().getApplication(), getString(R.string.esp_lib_text_some_thing_went_wrong), bContext);
            }
        });

    }


    private void preSelectedValues(List<ESP_LIB_FilterDefinitionSortDAO> ESPLIBFilterDefinitionSortDAOSList, List<Integer> applcaition_checkboxes) {
        if (ESPLIBFilterDao != null && (ESPLIBFilterDao.getDefinitionIds() != null &&
                ESPLIBFilterDao.getDefinitionIds().get(0) != 0)) {
            StringBuilder stringBuilder = new StringBuilder();
            for (int k = 0; k < ESPLIBFilterDao.getDefinitionIds().size(); k++) {
                int definitionId = ESPLIBFilterDao.getDefinitionIds().get(k);
                for (int j = 0; j < ESPLIBFilterDefinitionSortDAOSList.size(); j++) {
                    ESP_LIB_FilterDefinitionSortDAO ESPLIBFilterDefinitionSortDAO1 = ESPLIBFilterDefinitionSortDAOSList.get(j);
                    if (definitionId == ESPLIBFilterDefinitionSortDAO1.getId()) {
                        ESPLIBFilterDefinitionSortDAO1.setCheck(true);
                        stringBuilder.append(ESPLIBFilterDefinitionSortDAO1.getName());
                        stringBuilder.append(", ");

                    }
                }
            }
            checkAllText(ESPLIBFilterDefinitionSortDAOSList);
            setAllStatus(ESPLIBFilterDefinitionSortDAOSList, stringBuilder.toString());

            if (ESPLIBFilterDefinitionAdapter != null)
                ESPLIBFilterDefinitionAdapter.notifyDataSetChanged();
        } else {
            if (ESPLIBFilterDao != null)
                ESPLIBFilterDao.setDefinitionIds(applcaition_checkboxes);

            for (int j = 0; j < ESPLIBFilterDefinitionSortDAOSList.size(); j++) {
                ESPLIBFilterDefinitionSortDAOSList.get(j).setCheck(true);
            }
            setAllStatus(ESPLIBFilterDefinitionSortDAOSList, getString(R.string.esp_lib_text_all));
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initiateStatus() {
        String[] status = bContext.getResources().getStringArray(R.array.status);
     //   String[] status_enum = bContext.getResources().getStringArray(R.array.status_enum);

        if (status != null && status.length > 0) {
            statuses_checkboxes = new ArrayList<>();

            Typeface typeface = Typeface.createFromAsset(getAssets(), "font/lato/lato_regular.ttf");
            for (int i = 0; i < status.length; i++) {
                final AppCompatCheckBox checkBox = new AppCompatCheckBox(bContext);
                checkBox.setText(status[i]);
                checkBox.setTextSize(15);
                checkBox.setButtonDrawable( ContextCompat.getDrawable(bContext,R.drawable.esp_lib_drawable_checkbox_button_selector));
                checkBox.setTag(i);
                checkBox.setTypeface(typeface);
                checkBox.setPadding(50, 0, 30, 0);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 10, 0, 0);
                checkBox.setLayoutParams(params);



                if (i == 1 || i == 2) // 1 = new and 2 = pending
                {
                    checkBox.setVisibility(View.GONE);
                }
                if (statusesList.contains(i + "")) {
                    checkBox.setChecked(true);
                }

                checkBox.setOnClickListener(v -> {
                    int tage = (int) v.getTag();

                    if (statuses_checkboxes.get(tage).isChecked()) {

                        if (tage == 0) {
                            statusesList.clear();
                            CheckAllStatuses(true);
                            statuses.setText(getString(R.string.esp_lib_text_all));

                        } else {

                            if (!statusesList.contains(tage + "")) {
                                statusesList.add(tage + "");
                            }

                        }

                        //  checkBox.setTextColor(bContext.getResources().getColor(R.color.green));

                    } else {
                        //  checkBox.setTextColor(bContext.getResources().getColor(R.color.dark_grey));
                        //UN CHECKED
                        if (tage == 0) {
                            statusesList.clear();
                            CheckAllStatuses(false);
                            statuses.setText("");
                        } else {
                            statuses_checkboxes.get(0).setChecked(false);
                            statusesList.remove(tage + "");


                        }

                    }
                    UpdateView(true);
                });

                statuses_checkboxes.add(checkBox);
                status_layout.addView(checkBox);
            }

            UpdateView(true);


        }


    }

    private void UpdateView(boolean ischecked) {


        if (statusesList != null) {

            String fitlerType = "";
            int count = 0;
            if (statusesList.size() == 0) {
                fitlerType = "";
            } else if (statusesList.size() == 5) {
                CheckAllStatuses(ischecked);
                fitlerType = getString(R.string.esp_lib_text_all);
            } else {

                for (String s : statusesList) {
                    if (s != "0") {
                        if (fitlerType.length() == 0) {
                            fitlerType = statuses_checkboxes.get(Integer.parseInt(s)).getText().toString();
                        } else {

                            if (fitlerType.equalsIgnoreCase(getString(R.string.esp_lib_text_neww)))
                                fitlerType = "";

                            String status = statuses_checkboxes.get(Integer.parseInt(s)).getText().toString();
                            if (status.equalsIgnoreCase(getString(R.string.esp_lib_text_accepted)) ||
                                    status.equalsIgnoreCase(getString(R.string.esp_lib_text_rejected)) ||
                                    status.equalsIgnoreCase(getString(R.string.esp_lib_text_cancelled))) {
                                fitlerType += ", " + status;
                            }

                        }
                    }
                }

            }

            statuses.setText(fitlerType);
        }
    }

    private void CheckAllStatuses(boolean checked) {

        if (statuses_checkboxes != null && statuses_checkboxes.size() > 0) {
            for (AppCompatCheckBox cb : statuses_checkboxes) {
                cb.setChecked(checked);
                int tage = (int) cb.getTag();
                if (checked) {
                    if (tage != 0) {
                        if (!statusesList.contains(tage + "")) {
                            statusesList.add(tage + "");
                        }
                    }

                    cb.setTextColor(bContext.getResources().getColor(R.color.esp_lib_color_black));
                } else {
                    statusesList.remove(tage + "");
                    cb.setTextColor(bContext.getResources().getColor(R.color.esp_lib_color_dark_grey));
                }

            }
        }
    }

    private void ApplyFilter() {

        if (statusesList == null || statusesList.size() == 0) {
            ESP_LIB_Shared.getInstance().showAlertMessage(getString(R.string.esp_lib_text_filter), getString(R.string.esp_lib_text_filter_error), bContext);
            return;
        } else if (ESPLIBFilterDefinitionAdapter != null && ESPLIBFilterDao != null &&
                (ESPLIBFilterDao.getDefinitionIds() == null || ESPLIBFilterDao.getDefinitionIds().size() == 0)) {
            ESP_LIB_Shared.getInstance().showAlertMessage(getString(R.string.esp_lib_text_filter), getString(R.string.esp_lib_text_definition_filter_error), bContext);
            return;
        }


        if (statusesList.size() < 5
                || ESPLIBFilterDao != null && (ESPLIBFilterDao.getDefinitionIds() != null
                && ESPLIBFilterDao.getDefinitionIds().size() > 0)) {
            ESPLIBFilterDao.setFilterApplied(true);
        } else {
            ESPLIBFilterDao.setFilterApplied(false);
        }


        if (txtdefinitionstatuses.getText().toString().contains(getString(R.string.esp_lib_text_all))) isOpenFilterApplied = false;
        else if (!txtdefinitionstatuses.getText().toString().contains(getString(R.string.esp_lib_text_all))) isOpenFilterApplied = true;

        if (txtdefinitionstatuses.getText().toString().contains(getString(R.string.esp_lib_text_all)) && (status_row.getVisibility() == View.VISIBLE && statuses.getText().toString().contains(getString(R.string.esp_lib_text_all)))) {
            isCloseFilterApplied = false;
        }
        else if (!txtdefinitionstatuses.getText().toString().contains(getString(R.string.esp_lib_text_all)) || (status_row.getVisibility() == View.VISIBLE && !statuses.getText().toString().contains(getString(R.string.esp_lib_text_all)))) {
            isCloseFilterApplied = true;
        }

        EventBus.getDefault().post(new EventOptions.EventRefreshData());

        Bundle bnd = new Bundle();
        bnd.putBoolean("whatodo", true);
        Intent intent = new Intent();
        intent.putExtras(bnd);
        setResult(2, intent);
        finish();
    }

    @Override
    public void onBackPressed() {

    /*    Bundle bnd = new Bundle();
        bnd.putBoolean("whatodo", false);
        Intent intent = new Intent();
        intent.putExtras(bnd);
        setResult(2, intent);
        super.onBackPressed();*/
        if (ESPLIBFilterDao != null)
            ApplyFilter();
        else
            super.onBackPressed();
    }

    private void setGravity() {
        ESP_LIB_SharedPreference pref = new ESP_LIB_SharedPreference(bContext);
        if (pref.getLanguage().equalsIgnoreCase("ar")) {
            statuses.setGravity(Gravity.RIGHT);
            txtbystatus.setGravity(Gravity.RIGHT);

        } else {
            statuses.setGravity(Gravity.LEFT);
            txtbystatus.setGravity(Gravity.LEFT);
        }
    }

    private void start_loading_animation() {

        nestedscrollview.setVisibility(View.GONE);
        shimmer_view_container.setVisibility(View.VISIBLE);


    }

    private void stop_loading_animation() {

        nestedscrollview.setVisibility(View.VISIBLE);
        shimmer_view_container.setVisibility(View.GONE);

    }


    @Override
    public void selectedValues(List<ESP_LIB_FilterDefinitionSortDAO> filterDefinitionList, int position, boolean checked) {
        List<Integer> applcaition_checkboxes = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < filterDefinitionList.size(); i++) {
            ESP_LIB_FilterDefinitionSortDAO ESPLIBFilterDefinitionSortDAO = filterDefinitionList.get(i);
            if (ESPLIBFilterDefinitionSortDAO.isCheck() && ESPLIBFilterDefinitionSortDAO.getId() != 1122) // 1122 = id of All
            {
                applcaition_checkboxes.add(ESPLIBFilterDefinitionSortDAO.getId());
                stringBuilder.append(filterDefinitionList.get(i).getName());
                stringBuilder.append(", ");
            }
        }


        ESPLIBFilterDao.setDefinitionIds(applcaition_checkboxes);
        checkUncheck(checked, filterDefinitionList, position, stringBuilder.toString());


    }

    @Override
    public void selectedSortValues(ESP_LIB_FilterDefinitionSortDAO ESPLIBFilterDefinitionSortDAO, List<ESP_LIB_FilterDefinitionSortDAO> filterSortByListSort, int position) {
        ESPLIBFilterDao.setSortBy(ESPLIBFilterDefinitionSortDAO.getId());
        preSelectedSortBy(filterSortByListSort);
    }

    private void checkUncheck(boolean isCheck, List<ESP_LIB_FilterDefinitionSortDAO> filterDefinitionList, int position, String selectedStrings) {
        if (position == 0) {
            for (int i = 0; i < filterDefinitionList.size(); i++) {
                filterDefinitionList.get(i).setCheck(isCheck);
            }

            if (ESPLIBFilterDefinitionAdapter != null && !rvdefintionList.isComputingLayout())
                ESPLIBFilterDefinitionAdapter.notifyDataSetChanged();
        } else {
            checkAllText(filterDefinitionList);
        }

        setAllStatus(filterDefinitionList, selectedStrings);

    }

    private void checkAllText(List<ESP_LIB_FilterDefinitionSortDAO> filterDefinitionList) {
        if (filterDefinitionList.size() > 0 && ESPLIBFilterDao != null &&
                (ESPLIBFilterDao.getDefinitionIds().size() == (filterDefinitionList.size() - 1))
                && !filterDefinitionList.get(0).isCheck()) {
            filterDefinitionList.get(0).setCheck(true);
            if (ESPLIBFilterDefinitionAdapter != null)
                ESPLIBFilterDefinitionAdapter.notifyItemChanged(0);
        }


    }

    private void setAllStatus(List<ESP_LIB_FilterDefinitionSortDAO> filterDefinitionList, String selectedStrings) {
        for (int i = 0; i < filterDefinitionList.size(); i++) {

            if (!filterDefinitionList.get(i).isCheck()) {
                txtdefinitionstatuses.setText(selectedStrings.replaceAll(", $", ""));
                break;
            } else {
                txtdefinitionstatuses.setText(getString(R.string.esp_lib_text_all));
            }

        }

        if (filterDefinitionList.size() > 0) {
            if (txtdefinitionstatuses.getText().toString().equalsIgnoreCase(getString(R.string.esp_lib_text_all))) {
                List<Integer> applcaition_checkboxes = new ArrayList<>();
                applcaition_checkboxes.add(0);
                ESPLIBFilterDao.setDefinitionIds(applcaition_checkboxes);
            }
        }
    }
}
