package com.esp.library.exceedersesp.fragments.applications;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.esp.library.R;
import com.esp.library.exceedersesp.ESP_LIB_ESPApplication;
import com.esp.library.utilities.common.ESP_LIB_Shared;
import com.esp.library.utilities.common.ESP_LIB_SharedPreference;
import com.esp.library.exceedersesp.ESP_LIB_BaseActivity;
import com.esp.library.exceedersesp.controllers.applications.filters.ESP_LIB_FilterActivity;
import com.facebook.shimmer.ShimmerFrameLayout;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import utilities.adapters.setup.ESP_LIB_FilterItemsAdapter;
import utilities.adapters.setup.applications.ESP_LIB_ListApplicationCategoryAndDefinationAdapter;
import utilities.data.apis.ESP_LIB_APIs;
import utilities.data.applicants.addapplication.ESP_LIB_CategoryAndDefinationsDAO;
import utilities.data.applicants.addapplication.ESP_LIB_DefinationsCategoriesDAO;
import utilities.interfaces.ESP_LIB_DeleteFilterListener;

public class ESP_LIB_AddApplicationCategoryAndDefinationsFragment extends Fragment implements ESP_LIB_DeleteFilterListener {

    ESP_LIB_BaseActivity bContext;
    private ESP_LIB_ListApplicationCategoryAndDefinationAdapter mDefAdapter;


    Call<List<ESP_LIB_CategoryAndDefinationsDAO>> cat_call = null;
    Call<List<ESP_LIB_DefinationsCategoriesDAO>> def_call = null;
    InputMethodManager imm = null;
    LinearLayout no_application_available_div;
    RecyclerView defination_list;
    TextView message_error;
    TextView message_error_detail;
    ShimmerFrameLayout shimmer_view_container;
    TextView listcount;
    EditText etxtsearch;
    ImageButton ibClear;
    ImageView ivfilter;
    RecyclerView filter_horizontal_list;
    SwipeRefreshLayout swipeRefreshLayout;


    private static final int HIDE_THRESHOLD = 20;
    ESP_LIB_FilterItemsAdapter filter_adapter;
    ESP_LIB_SharedPreference pref;
    List<ESP_LIB_CategoryAndDefinationsDAO> cat_list = new ArrayList<>();
    List<ESP_LIB_CategoryAndDefinationsDAO> cat_list_filtered = new ArrayList<>();
    RecyclerView.LayoutManager mDefLayoutManager;
    List<ESP_LIB_DefinationsCategoriesDAO> actualResponse;
    public static ArrayList categoryAndDefinationsDAOFilteredList = new ArrayList<ESP_LIB_CategoryAndDefinationsDAO>();

    public ESP_LIB_AddApplicationCategoryAndDefinationsFragment() {
        // Required empty public constructor
    }

    public static ESP_LIB_AddApplicationCategoryAndDefinationsFragment newInstance() {
        ESP_LIB_AddApplicationCategoryAndDefinationsFragment fragment = new ESP_LIB_AddApplicationCategoryAndDefinationsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.esp_lib_fragment_application_category_definations, container, false);
        initailizeIds(v);
        initailize();


        ivfilter.setOnClickListener(view -> {
            Intent i = new Intent(getActivity(), ESP_LIB_FilterActivity.class);
            i.putExtra("categoryAndDefinationsDAOFilteredList", categoryAndDefinationsDAOFilteredList);
            i.putExtra("actualResponse", (Serializable) actualResponse);
            startActivity(i);
        });

        etxtsearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.length() == 0) {
                    ibClear.setVisibility(View.GONE);
                    mDefAdapter = new ESP_LIB_ListApplicationCategoryAndDefinationAdapter(cat_list_filtered, bContext, getString(R.string.esp_lib_text_definition));
                    defination_list.setAdapter(mDefAdapter);
                } else {
                    mDefAdapter.getFilter().filter(s);
                    ibClear.setVisibility(View.VISIBLE);
                }


            }

            @Override
            public void afterTextChanged(Editable s) {
                listcount.setText(mDefAdapter.getItemCount() + " " + pref.getlabels().getApplication());
            }
        });

        etxtsearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    ESP_LIB_Shared.getInstance().hideKeyboard(bContext);
                }
                return false;
            }
        });

        ibClear.setOnClickListener(view -> etxtsearch.setText(""));



        swipeRefreshLayout.setOnRefreshListener(() -> {
            categoryAndDefinationsDAOFilteredList.clear();
            filter_horizontal_list.setVisibility(View.GONE);
            ivfilter.setBackgroundResource(R.drawable.esp_lib_drawable_ic_filter_gray);
            etxtsearch.setText("");
            loadData();
        });

        return v;
    }

    private void initailizeIds(View v) {
        no_application_available_div = v.findViewById(R.id.no_application_available_div);
        defination_list = v.findViewById(R.id.defination_list);
        message_error = v.findViewById(R.id.message_error);
        message_error_detail = v.findViewById(R.id.message_error_detail);
        shimmer_view_container = v.findViewById(R.id.shimmer_view_container);
        listcount = v.findViewById(R.id.listcount);
        etxtsearch = v.findViewById(R.id.etxtsearch);
        ibClear = v.findViewById(R.id.ibClear);
        ivfilter = v.findViewById(R.id.ivfilter);
        filter_horizontal_list = v.findViewById(R.id.filter_horizontal_list);
        swipeRefreshLayout = v.findViewById(R.id.swipeRefreshLayout);
    }

    private void initailize() {


        bContext = (ESP_LIB_BaseActivity) requireContext();
        imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (getActivity() != null) {
            getActivity().getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        }
        pref = new ESP_LIB_SharedPreference(requireContext());

        filter_horizontal_list.setHasFixedSize(true);
        filter_horizontal_list.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        filter_horizontal_list.setItemAnimator(new DefaultItemAnimator());


        mDefLayoutManager = new LinearLayoutManager(requireContext());
        defination_list.setHasFixedSize(true);
        defination_list.setLayoutManager(mDefLayoutManager);
        defination_list.setItemAnimator(new DefaultItemAnimator());
        defination_list.setNestedScrollingEnabled(true);
        int themeColor = ContextCompat.getColor(requireContext(),R.color.colorPrimaryDark);
        swipeRefreshLayout.setColorSchemeColors(themeColor, themeColor, themeColor);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadData();


    }

    private void loadData() {

        if (ESP_LIB_Shared.getInstance().isWifiConnected(bContext)) {
            LoadDefinations(0);
        } else {
            ESP_LIB_Shared.getInstance().showAlertMessage(getString(R.string.esp_lib_text_internet_error_heading), getString(R.string.esp_lib_text_internet_connection_error), bContext);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        filter_adapter = new ESP_LIB_FilterItemsAdapter(categoryAndDefinationsDAOFilteredList, requireContext());
        filter_adapter.setActivitContext(this);
        filter_horizontal_list.setAdapter(filter_adapter);
        populateFilters();
        etxtsearch.setText("");
        if (ESP_LIB_ESPApplication.getInstance().isGoToMainScreen()) {
            ESP_LIB_ESPApplication.getInstance().setGoToMainScreen(false);
            bContext.finish();
        }

    }

    private void populateFilters() {
        cat_list_filtered.clear();
        if (categoryAndDefinationsDAOFilteredList.size() > 0) {
            ivfilter.setBackgroundResource(R.drawable.esp_lib_drawable_ic_filter_green);
            filter_horizontal_list.setVisibility(View.VISIBLE);

        } else {
            ivfilter.setBackgroundResource(R.drawable.esp_lib_drawable_ic_filter_gray);
            filter_horizontal_list.setVisibility(View.GONE);
            cat_list_filtered.addAll(cat_list);
        }

        ArrayList<Integer> categoriesIds = new ArrayList<>();
        for (int i = 0; i < categoryAndDefinationsDAOFilteredList.size(); i++) {
            ESP_LIB_CategoryAndDefinationsDAO df = (ESP_LIB_CategoryAndDefinationsDAO) categoryAndDefinationsDAOFilteredList.get(i);
            categoriesIds.add(df.getId());

            for (int h = 0; h < cat_list.size(); h++) {
                if (cat_list.get(h).getTypeId() == df.getId()) {
                    cat_list_filtered.add(cat_list.get(h));
                }
            }

        }

        mDefAdapter = new ESP_LIB_ListApplicationCategoryAndDefinationAdapter(cat_list_filtered, bContext, getString(R.string.esp_lib_text_definition));
        defination_list.setAdapter(mDefAdapter);
        listcount.setText(mDefAdapter.getItemCount() + " " + pref.getlabels().getApplication());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        categoryAndDefinationsDAOFilteredList.clear();

    }


    private void LoadDefinations(int categoryId) {

        start_loading_animation();
        /* APIs Mapping respective Object*/
        ESP_LIB_APIs apis = ESP_LIB_Shared.getInstance().retroFitObject(bContext);
        //  def_call = apis.AllDefincations(categoryId);
        def_call = apis.AllWithQuery();
        def_call.enqueue(new Callback<List<ESP_LIB_DefinationsCategoriesDAO>>() {
            @Override
            public void onResponse(Call<List<ESP_LIB_DefinationsCategoriesDAO>> call, Response<List<ESP_LIB_DefinationsCategoriesDAO>> response) {
                stop_loading_animation();

                if (response.body() != null && response.body().size() > 0) {
                    actualResponse = response.body();
                    cat_list.clear();
                    List<ESP_LIB_DefinationsCategoriesDAO> body = response.body();
                    for (int i = 0; i < body.size(); i++) {
                        List<ESP_LIB_CategoryAndDefinationsDAO> category = body.get(i).getDefinitions();
                        if (category != null) {
                            for (int k = 0; k < category.size(); k++) {
                                ESP_LIB_CategoryAndDefinationsDAO ESPLIBCategoryAndDefinationsDAO = category.get(k);
                                if (ESPLIBCategoryAndDefinationsDAO != null) {
                                    if (ESPLIBCategoryAndDefinationsDAO.isActive()) {
                                        cat_list.add(ESPLIBCategoryAndDefinationsDAO);
                                    }
                                }
                            }
                        }


                    }
                    if (cat_list.size() > 0) {
                        try {
                            cat_list_filtered.addAll(cat_list);
                            mDefAdapter = new ESP_LIB_ListApplicationCategoryAndDefinationAdapter(cat_list, bContext, getString(R.string.esp_lib_text_definition));
                            defination_list.setAdapter(mDefAdapter);
                            defination_list.setVisibility(View.VISIBLE);
                            listcount.setText(mDefAdapter.getItemCount() + " " + pref.getlabels().getApplication());
                        } catch (Exception e) {
                        }
                        SuccessResponse();
                    } else {
                        UnSuccessResponse();
                    }
                } else {
                    UnSuccessResponse();
                }
            }

            @Override
            public void onFailure(Call<List<ESP_LIB_DefinationsCategoriesDAO>> call, Throwable t) {
                ESP_LIB_Shared.getInstance().messageBox(t.getMessage(), bContext);
                stop_loading_animation();
                UnSuccessResponse();
            }
        });

    }//

    @Override
    public void onDestroyView() {

        if (cat_call != null) {
            cat_call.cancel();
        }

        if (def_call != null) {
            def_call.cancel();
        }

        super.onDestroyView();
    }

    private void start_loading_animation() {
        shimmer_view_container.setVisibility(View.VISIBLE);
    }

    private void stop_loading_animation() {
        shimmer_view_container.setVisibility(View.GONE);
    }

    private void SuccessResponse() {
        swipeRefreshLayout.setRefreshing(false);
        defination_list.setVisibility(View.VISIBLE);
        no_application_available_div.setVisibility(View.GONE);
    }

    private void UnSuccessResponse() {
        swipeRefreshLayout.setRefreshing(false);
        message_error.setText(bContext.getResources().getString(R.string.esp_lib_text_no_defination_error));
        message_error_detail.setText(bContext.getResources().getString(R.string.esp_lib_text_no_defination_error));
        defination_list.setVisibility(View.GONE);
        no_application_available_div.setVisibility(View.VISIBLE);
    }

    public void UpdateDefincation(ESP_LIB_CategoryAndDefinationsDAO cat) {
        if (cat != null) {
            LoadDefinations(cat.getId());
        }
    }

    @Override
    public void deleteFilters(@NotNull ESP_LIB_CategoryAndDefinationsDAO filtersList) {

        if (ESP_LIB_Shared.getInstance().isWifiConnected(bContext)) {
            if (filter_adapter != null) {
                categoryAndDefinationsDAOFilteredList.remove(filtersList);
                filter_adapter.notifyDataSetChanged();
                populateFilters();
            }
        } else {
            ESP_LIB_Shared.getInstance().showAlertMessage(getString(R.string.esp_lib_text_internet_error_heading), getString(R.string.esp_lib_text_internet_connection_error), bContext);
        }
    }


}
