package com.exceedersesp;

import android.content.Intent;
import android.os.Bundle;

import com.esp.library.exceedersesp.ESP_LIB_BaseActivity;
import com.esp.library.exceedersesp.ESP_LIB_ESPApplication;
import com.esp.library.exceedersesp.controllers.ESP_LIB_SplashScreenActivity;
import com.esp.library.exceedersesp.controllers.applications.ESP_LIB_AddApplicationsFromScreenActivity;
import com.esp.library.exceedersesp.controllers.applications.filters.ESP_LIB_FilterScreenActivity;
import com.esp.library.utilities.common.ESP_LIB_Constants;
import com.esp.library.utilities.common.ESP_LIB_SharedPreference;

import java.util.ArrayList;
import java.util.List;

import utilities.data.applicants.addapplication.ESP_LIB_CategoryAndDefinationsDAO;
import utilities.model.ESP_LIB_Labels;

public class MainActivityESPLIB extends ESP_LIB_BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*setInstance();
        setLabels();*/

        /*
ESPApplication.getInstance().setSpecificApplication(false);
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        ApplicationActivityTabs submit_request = ApplicationActivityTabs.Companion.newInstance();
        FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.request_fragment, submit_request);
        fragmentTransaction.commit();*/

        /*try {
            Intent myIntent = new Intent(this,Class.forName("com.esp.library.exceedersesp.controllers.applications.AddApplicationsActivity"));
            startActivity(myIntent );
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }*/

        /*ESPApplication.getInstance().setSetFont(false);
        ESPApplication.getInstance().setApplicationTextFont("greatvibes_regular.otf");*/


        startActivity(new Intent(this, ESP_LIB_SplashScreenActivity.class));
        finish();

       /* callSpecificApplication()*/


    }

    private void setInstance() {
        ESP_LIB_Constants.base_url = "https://qaesp.azurewebsites.net/webapi/";
        ESP_LIB_ESPApplication.getInstance().setComponent(true);
      //  ESPApplication.getInstance().setAccess_token("J6Gyv8wAbKe-0NImtuExwCVojB3bLPHUl-U2-2BptYAXM0sng6rzxk_PT8bNgK1b-MvakpkxC3obhbsHq3Hmx2JzkrwOAAq2zp2Q8rxA_sg7PDiJ3iWTVo_mHCz3piL9pUjsJd9ZenPS8BUs9W37sauratAvQNLmJNbbDQL-DWsQUV-f1h95cnHDBUeKxCunFi8TMoLhH3bD1VNSm6zgWW1txgswla5zlYLyUqjlOpIMuApSo8ScQIa0Cb8qffrNoMoIn3TNn9-Irzz10j53UkGVWWU1jL9uo0AYlOt0aNbgKDLXBjwg_k3_8Eqaabi6Jg_QzppL1M4GaYjgV2Kd463_ZqO-n2DSi1_OlKjM9LVGOjZ4rk3sIIFtT6DoRC14hoNc-zW1ddxDVlMw-Mwl2ub6X2L6n75-b1OV-97Cm1XtNWmWHvFDHEkp2X0xAs2VV2EKRatsaAd9w8aUAyoKRNtr-u2MQT-IeMRojYX9ChPRkeAuii7tbeYZWAx0qE0_tn6tC69s_mNUbflksqnYXc6I1hrfDUngvi-9SPeYTjtaNNXpasmjdGatD6H887P6tcQF1VM2TiMJip592HKcY6CbVyWi4udvgFPrbQa-9xFNF3AoJ_MHcfcdK1tIt3ooC_ub9-YzQKvDK4PoKwCGanmAiK9naJbSsGsggbB8p4InkznpbcsTzpergQPDm6gGnbetooxrSwTUVRDs6Z3hi2AI43lPR2iZCKfYWbNf4_Pq5rBlfPz-UAKLA3RYVnmek-zsu8c_J03sfAP-yO3djxHNow07c1_czsF9OaZ2yx4RgKAbUj9hb4-io_lcMiBFX6_icxGHruNxUOjYxOO39PpZxqxH4GQefCZZ1uSwJ_Hza9h8jfot_Zc09mD_XCw8VBULJxN6tIK4CAo0s5-V7UkuvSJDZ-Otrl1YtetRvzeh0CFDfemtZuexCtI0Zw83XstYmPRFJqytAoTYyucUG-sLBPl_rvxQG0-tl5NYYYhTIzEm4uPq2aZJDPaT3Ox_aF-UZAkkd9wehtNCHpq6ppg0a6AolGpt-eTYr_MuC9dWVKFFjae604Lkgc7MLczyy0YTwpR5i-kQHXj2xog7X88tYQILHIXE3Q-_e7y1s9CSJ0lKtNuEBmZqm03rLf30s_huqBSpiJ9Xj5UToqSulek6eVQ1BhByDicOMAsJZ135fX3T6-o_6Zc0jmVGinoHriqDc2-tkpQ1K_0hXd7y5U06X964UaAsCMLEAqj0cWQ38xnruY9NOomwhl5ZgsqH");
        ESP_LIB_ESPApplication.getInstance().setAccess_token("Ft0vzWMpiJ-__5MO_JGpQoF3zpklRbl_f2O9kR73bHUkzFDgE4jNP1Qu3P9L7jio-AwpGudDW58UKFLTxv8cS4prsT0QHkVdy6jZerkRgFwSQvPlbzcBLksj-QUFKL5_9CD0nzTyCu6Alo7p8uVs6rqdyHfz9A96fAVRx3Ht_dlQ1ZfQTpJuyehgEnl5QcKX3nxicaq1maMcCpI53ZI-ydD381KieMMMB5REElGnvTOwyK28qkgmRPjI3MloPLi6RdxIFtiiY_MXv_JupMOALoZCVj6g-ykwr-90s5vX9tOJj9QGcyVPoJm6nc8vH88MTyrWSAnr3hwUwMzOTxtGAazWt4BfQGkjP5QsdrgraPxFQFQaSresLynfXWnATQEYfFjOO4YpQwuTMYOcjjX9gm-uHUZqua27f5dRwe18fe2gCzXF1aBbVrh85v7x2PmQDqWWTQznWz7yyhqHslgAnNIvfIQBZCGQLyaJufUOoOU5VllWV4OPR7l2W9YZCUISg8TCEyV3Nvnw0adaN3mOaERsAWhmiMlAqOy5IvN-DWGPJK1D24o63yGaRNZNSbus6xZzksyjKrdvKz7LscC1HDJRt4QuOBzC44yD14UgIr8ynttMD996leACWsABHGMVEeO2YFsPYvYbLzvsDL7FtBJLUIG1X03Ysb829NXr8_VdWQ6PIPnMQ_he5AMBnN6eR3g9K_qnUepi00tT5EEfi2nTgEYx80UqQJwI9N1myvbkYfIea-3oI7x7TgrQlIHDnWGXRRea9yXRloCu8hD_eXoiLEvMqQCU4Ws7KBJ8_uWORdn6rpgvasx90fVoiyh2B05RCkCxrheSvobNg95NgHDrQtnd7gbG5X1ayx4pi26pqEP0oMMGEjTB4pTh_3U9fhkPhVmWdJy1S6htfH8RTVmvL_LG0ydsqX0kxa9wxkMH8AjPq5zoctQJ8yP2VRV-J6sGDr4F7EosajR8V9XFTGGXfyj1OQw3ieQ-EHtzWgAsb8aPtHOx6GdJ_I2apEPW");
        List<Integer> applcaition_checkboxes = new ArrayList<>();
        applcaition_checkboxes.add(0);
        if (ESP_LIB_FilterScreenActivity.getInstance().ESPLIBFilterDao != null)
            ESP_LIB_FilterScreenActivity.getInstance().ESPLIBFilterDao.setDefinitionIds(applcaition_checkboxes);
    }

    private void setLabels() {
        ESP_LIB_Labels ESPLIBLabels = new ESP_LIB_Labels();
        ESPLIBLabels.setApplication(getString(R.string.esp_lib_text_application));
        ESPLIBLabels.setApplications(getString(R.string.esp_lib_text_applications));
        ESPLIBLabels.setApplicant(getString(R.string.esp_lib_text_applicant));
        ESPLIBLabels.setApplicants(getString(R.string.esp_lib_text_applicants));
        ESPLIBLabels.setDefinition(getString(R.string.esp_lib_text_definition));
        ESPLIBLabels.setDefinitions(getString(R.string.esp_lib_text_definitions));
        ESPLIBLabels.setSubmissionRequests(getString(R.string.esp_lib_text_submissionrequest));

        ESP_LIB_SharedPreference pref = new ESP_LIB_SharedPreference(this);
        pref.savelabels(ESPLIBLabels);
    }

    private void callSpecificApplication()
    {
        ESP_LIB_ESPApplication.getInstance().setSpecificApplication(true);
        ESP_LIB_CategoryAndDefinationsDAO ESPLIBCategoryAndDefinationsDAO = new ESP_LIB_CategoryAndDefinationsDAO();
        ESPLIBCategoryAndDefinationsDAO.setId(294);  // set your desire application id here
        Bundle bundle = new Bundle();
        bundle.putSerializable(ESP_LIB_CategoryAndDefinationsDAO.Companion.getBUNDLE_KEY(), ESPLIBCategoryAndDefinationsDAO);
        bundle.putString("toolbarheading", "malik");
        Intent i = new Intent(this, ESP_LIB_AddApplicationsFromScreenActivity.class);
        i.putExtras(bundle);
        startActivity(i);
    }
}
