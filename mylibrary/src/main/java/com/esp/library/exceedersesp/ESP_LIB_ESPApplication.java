package com.esp.library.exceedersesp;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import androidx.multidex.MultiDexApplication;
import androidx.recyclerview.widget.RecyclerView;

import com.esp.library.R;

import java.util.List;

import utilities.data.applicants.addapplication.ESP_LIB_CurrencyDAO;
import utilities.data.filters.ESP_LIB_FilterDAO;
import utilities.data.setup.ESP_LIB_TokenDAO;
import utilities.data.setup.ESP_LIB_UserDAO;

public class ESP_LIB_ESPApplication extends MultiDexApplication {

    Context bContext;
    ESP_LIB_UserDAO user;
    ESP_LIB_TokenDAO tokenPersonas;
    ESP_LIB_FilterDAO filter;
    ESP_LIB_FilterDAO filterTemp;
    List<ESP_LIB_CurrencyDAO> currencies;

    boolean isOnCLosedTab = false; // used for library
    boolean isComponent = false; // used for library
    boolean isSpecificApplication = false; // used for library
    boolean goToMainScreen = false; // used for library
    boolean isSetFont = false; // used for library
    String applicationTextFont = null; // used for library
    String access_token = null;
    int applicationTheme=R.style.ESP_LIB_Styles_AppTheme;

    public int getApplicationTheme() {
        return applicationTheme;
    }

    public void setApplicationTheme(int applicationTheme) {
        this.applicationTheme = applicationTheme;
    }

    public boolean isSpecificApplication() {
        return isSpecificApplication;
    }

    public void setSpecificApplication(boolean specificApplication) {
        isSpecificApplication = specificApplication;
    }

    public boolean isSetFont() {
        return isSetFont;
    }

    public void setSetFont(boolean setFont) {
        isSetFont = setFont;
    }

    public boolean isOnCLosedTab() {
        return isOnCLosedTab;
    }

    public void setOnCLosedTab(boolean onCLosedTab) {
        isOnCLosedTab = onCLosedTab;
    }

    public String getApplicationTextFont() {
        return applicationTextFont;
    }

    public void setApplicationTextFont(String applicationTextFont) {
        this.applicationTextFont = applicationTextFont;
    }

    public boolean isGoToMainScreen() {
        return goToMainScreen;
    }

    public void setGoToMainScreen(boolean goToMainScreen) {
        this.goToMainScreen = goToMainScreen;
    }

    public boolean isComponent() {
        return isComponent;
    }

    public void setComponent(boolean component) {
        isComponent = component;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }


    static ESP_LIB_ESPApplication application = null;

    public static ESP_LIB_ESPApplication getInstance() {

        if (application == null)
            return application = new ESP_LIB_ESPApplication();
        else
            return application;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        bContext = getApplicationContext();
    }

    public ESP_LIB_UserDAO getUser() {
        return user;
    }


    public ESP_LIB_TokenDAO getTokenPersonas() {
        return tokenPersonas;
    }

    public void setTokenPersonas(ESP_LIB_TokenDAO tokenPersonas) {
        this.tokenPersonas = tokenPersonas;
    }

    public void setUser(ESP_LIB_UserDAO user) {
        this.user = user;
    }

    public ESP_LIB_FilterDAO getFilter() {
        return filter;
    }

    public void setFilter(ESP_LIB_FilterDAO filter) {
        this.filter = filter;
    }

    public ESP_LIB_FilterDAO getFilterTemp() {
        return filterTemp;
    }

    public void setFilterTemp(ESP_LIB_FilterDAO filterTemp) {
        this.filterTemp = filterTemp;
    }

    public List<ESP_LIB_CurrencyDAO> getCurrencies() {
        return currencies;
    }

    public void setCurrencies(List<ESP_LIB_CurrencyDAO> currencies) {
        this.currencies = currencies;
    }

}
