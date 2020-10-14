package com.esp.library.utilities.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

import utilities.data.setup.ESP_LIB_IdenediAuthDAO;
import utilities.model.ESP_LIB_Labels;


public class ESP_LIB_SharedPreference {

    private String PREFS_NAME = "savepref";
    String labels = "labels";
    String language = "language";
    String locales = "locales";
    String fbId = "firebaseId";
    String faceId1= "faceId1";
    String faceId2= "faceId2";
    String IdenediCode = "IdenediCode";
    String idenediAuthDAOObject = "idenediAuthDAOObject";
    String firebaseToken = "firebaseToken";
    String refreshToken = "refreshToken";
    String personid = "personid";
    String organizationid = "organizationid";
    String selectedUserRole = "selectedUserRole";
    String idenediClientId = "idenediClientId";
    String idenediLoginUri = "idenediLoginUri";
    String idenediCode = "idenediCode";
    SharedPreferences pref;
    SharedPreferences.Editor editor;


    public ESP_LIB_SharedPreference(Context context) {
        super();
        pref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void savelabels(ESP_LIB_Labels value) {
        Gson gson = new Gson();
        String json = gson.toJson(value);
        editor.putString(labels, json);
        editor.commit();
    }

    public ESP_LIB_Labels getlabels() {
        Gson gson = new Gson();
        String json = pref.getString(labels, null);
        ESP_LIB_Labels obj = gson.fromJson(json, ESP_LIB_Labels.class);

        return obj;
    }



    public void saveProfileFaceId(String value) {
        editor.putString(faceId1, value);
        editor.commit();
    }

    public String getProfileFaceId1() {
        return pref.getString(faceId1, "");
    }

    public void savefaceId2(String value) {
        editor.putString(faceId2, value);
        editor.commit();
    }

    public String getfaceId2() {
        return pref.getString(faceId2, "");
    }

    public void savelanguage(String value) {
        editor.putString(language, value);
        editor.commit();
    }

    public String getLanguage() {
        return pref.getString(language, "en");
    }


    public void saveLocales(String locale) {
        editor.putString(locales, locale);
        editor.commit();
    }

    public String getLocales() {
        return pref.getString(locales, "");
    }

    public void saveFirebaseId(int id) {
        editor.putInt(fbId, id);
        editor.commit();
    }

    public int getFirebaseId() {
        return pref.getInt(fbId, 0);
    }

    public void savePersonaId(int id) {
        editor.putInt(personid, id);
        editor.commit();
    }

    public int getPersonaId() {
        return pref.getInt(personid, 0);
    }

    public void saveOrganizationId(int id) {
        editor.putInt(organizationid, id);
        editor.commit();
    }

    public int getOrganizationId() {
        return pref.getInt(organizationid, 0);
    }

    public void saveFirebaseToken(String token) {
        editor.putString(firebaseToken, token);
        editor.commit();
    }

    public String getFirebaseToken() {
        return pref.getString(firebaseToken, "");
    }

    public void saveidenediClientId(String idenediclientId) {
        editor.putString(idenediClientId, idenediclientId);
        editor.commit();
    }

    public String getidenediClientId() {
        return pref.getString(idenediClientId, null);
    }

    public void saveidenediLoginUri(String idenediLoginUrl) {
        editor.putString(idenediLoginUri, idenediLoginUrl);
        editor.commit();
    }

    public String getidenediLoginUri() {
        return pref.getString(idenediLoginUri, null);
    }


    public void saveRefreshToken(String refreshtoken) {
        editor.putString(refreshToken, refreshtoken);
        editor.commit();
    }

    public String getRefreshToken() {
        return pref.getString(refreshToken, null);
    }


    public void saveSelectedUserRole(String role) {
        editor.putString(selectedUserRole, role);
        editor.commit();
    }

    public String getSelectedUserRole() {
        return pref.getString(selectedUserRole, "");
    }

    public void saveIdenediCode(String role) {
        editor.putString(idenediCode, role);
        editor.commit();
    }

    public String getIdenediCode() {
        return pref.getString(idenediCode, "");
    }


    public void saveidenediAuthDAO(ESP_LIB_IdenediAuthDAO tokenDAO) {

        Gson gson = new Gson();
        String json = gson.toJson(tokenDAO);
        editor.putString(idenediAuthDAOObject, json);
        editor.commit();
    }

    public ESP_LIB_IdenediAuthDAO getidenediAuthDAO() {

        Gson gson = new Gson();
        String json = pref.getString(idenediAuthDAOObject, "");
        ESP_LIB_IdenediAuthDAO obj = gson.fromJson(json, ESP_LIB_IdenediAuthDAO.class);
        return obj;

    }


    public void clearPref() {



        editor.putString(labels, null);
        editor.putString(locales, null);
        editor.putString(firebaseToken, null);
        editor.putString(fbId, null);
        editor.putString(personid, null);
        editor.putString(organizationid, null);
        editor.putString(IdenediCode, null);
        editor.putString(idenediAuthDAOObject, null);
        editor.putString(refreshToken, null);
        editor.putString(idenediCode, null);
        editor.putString(selectedUserRole, null);
        editor.putString(idenediClientId, null);
        editor.commit();


    }


}
