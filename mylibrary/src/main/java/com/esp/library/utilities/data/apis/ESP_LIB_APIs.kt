package utilities.data.apis

import com.esp.library.exceedersesp.controllers.Profile.ESP_LIB_BasicDAO
import com.esp.library.utilities.data.applicants.ESP_LIB_CancelApplicationDAO
import com.esp.library.utilities.data.applicants.ESP_LIB_FaceDAO
import com.esp.library.utilities.data.applicants.signature.ESP_LIB_SignatureDAO
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.http.*
import utilities.data.applicants.*
import utilities.data.applicants.addapplication.*
import utilities.data.applicants.dynamics.ESP_LIB_DynamicResponseDAO
import utilities.data.applicants.dynamics.ESP_LIB_DynamicStagesCriteriaListDAO
import utilities.data.applicants.feedback.ESP_LIB_ApplicationsFeedbackDAO
import utilities.data.applicants.profile.ESP_LIB_ApplicationProfileDAO
import utilities.data.applicants.profile.ESP_LIB_RealTimeValuesDAO
import utilities.data.filters.ESP_LIB_FilterDAO
import utilities.data.filters.ESP_LIB_FilterDefinitionSortDAO
import utilities.data.lookup.ESP_LIB_LookupInfoListDAO
import utilities.data.lookup.ESP_LIB_LookupInfoListDetailDAO
import utilities.data.lookup.ESP_LIB_LookupInfoSearchDAO
import utilities.data.lookup.ESP_LIB_LookupItemDetailDAO
import utilities.data.setup.ESP_LIB_IdenediAuthDAO
import utilities.data.setup.ESP_LIB_OrganizationPersonaDao
import utilities.data.setup.ESP_LIB_TokenDAO
import utilities.model.ESP_LIB_Labels
import java.util.*

interface ESP_LIB_APIs {


    @get:GET("applicant/profileStatus/0")
    val userProfileStatus: Call<String>

    @get:GET("settings/")
    val getESPLIBSettings: Call<ESP_LIB_SettingsDAO>

    @get:GET("lookup/info/list")
    val ESPLIBLookupInfoList: Call<List<ESP_LIB_LookupInfoListDAO>>

    @get:GET("orguser/personas")
    val organizations: Call<List<ESP_LIB_OrganizationPersonaDao>>

    @FormUrlEncoded
    @POST("token")
    fun getToken(@Field("grant_type") grant_type: String?, @Field("username") username: String?,
                 @Field("password") password: String?, @Field("client_id") client_id: String?): Call<ESP_LIB_TokenDAO>


    @FormUrlEncoded
    @POST("token")
    fun getIdenedirefreshToken(@Field("grant_type") grant_type: String?, @Field("username") username: String?,
                               @Field("password") password: String?, @Field("client_id") client_id: String?,
                               @Field("idenedi_code") idenedi_code: String): Call<ESP_LIB_TokenDAO>

    @POST("idenedi/linkUser")
    fun linkIdenediUser(@Body ESPLIBIdenediAuthDAO: ESP_LIB_IdenediAuthDAO?): Call<ESP_LIB_IdenediAuthDAO>


    @GET("/webapi/idenedi/linkProfile")
    fun linkIdenediProfile(@Query("idenediCode") idenediCode: String): Call<Any>

    /*@POST("token")
    fun getIdenediToken(): Call<Any>*/

    @FormUrlEncoded
    @POST("token")
    fun getRefreshToken(@Query("id") id: String?, @Field("grant_type") grant_type: String?,
                        @Field("username") username: String?, @Field("password") password: String?,
                        @Field("client_id") client_id: String?, @Field("scope") scope: String?,
                        @Field("refresh_token") refresh_token: String?): Call<ESP_LIB_TokenDAO>


    @POST("fbtoken")
    fun postFirebaseToken(@Body ESPLIBFirebaseTokenDAO: ESP_LIB_FirebaseTokenDAO): Call<ESP_LIB_FirebaseTokenDAO>

    @DELETE("fbtoken/{fbTokenId}")
    fun deleteFirebaseToken(@Path("fbTokenId") fbTokenId: String): Call<ESP_LIB_FirebaseTokenDAO>

    /* @GET("application/all")
    Call<ResponseApplicationsDAO> GetUserApplications(@Query("status") String status, @Query("search") String search, @Query("PageNo") int PageNo, @Query("RecordPerPage") int RecordPerPage);

    @GET("application/list")
    Call<ResponseApplicationsDAO> GetUserSubApplicationsList(@Query("search") String search,@Query("filter") int filter, @Query("PageNo") int PageNo, @Query("RecordPerPage") int RecordPerPage ,
                                                             @Query("isMySpace") boolean isMySpace,@Query("sortBy") int sortBy,@Query("applicantId") String applicantId,
                                                             @Query("definationId") int definationId);*/


    /* @POST("application/listV2")
     fun GetUserApplicationsV2(@Body filterDAO: FilterDAO): Call<ResponseApplicationsDAO>*/

    @POST("verify")
    fun verifyfaceId(@Body ESPLIBFaceDAO: ESP_LIB_FaceDAO): Call<ESP_LIB_FaceDAO>

    @Multipart
    @POST("face/Verify")
    fun verifyface(@Part file: MultipartBody.Part?): Call<Any>

    @POST("application/listV4")
    fun getUserApplicationsV4(@Body ESPLIBFilterDAO: ESP_LIB_FilterDAO): Call<ESP_LIB_ResponseApplicationsDAO>

    @POST("application/assigned")
    fun getUserAssigned(@Body ESPLIBFilterDAO: ESP_LIB_FilterDAO): Call<ESP_LIB_ResponseApplicationsDAO>

    @POST("application/definitionForFilter")
    fun getDefinitioList(@Body ESPLIBFilterDAO: ESP_LIB_FilterDAO): Call<List<ESP_LIB_FilterDefinitionSortDAO>>

    /*   @POST("application/reassign/assessment")
       fun reAssignData(@Query("applicationId") applicationId: Int?, @Query("newOwnerId") newOwnerId: Int,
                        @Body dynamicStagesCriteriaListDAO: DynamicStagesCriteriaListDAO?): Call<DynamicStagesCriteriaListDAO>*/

    @POST("application/reassign/assessment")
    fun reAssignData(@Body ESPLIBDynamicStagesCriteriaListDAO: ESP_LIB_DynamicStagesCriteriaListDAO?): Call<Any>

    @POST("application/getCalculatedValues")
    fun getCalculatedValues(@Body ESPLIBDynamicResponseDAO: ESP_LIB_DynamicResponseDAO): Call<List<ESP_LIB_CalculatedMappedFieldsDAO>>

    @POST("applicant/getRealTimeValues/0/{id}/0")
    fun getRealTimeValues(@Path("id") id: Int, @Body ESPLIBRealTimeValuesDAO: List<ESP_LIB_RealTimeValuesDAO>): Call<List<ESP_LIB_CalculatedMappedFieldsDAO>>

    @GET("label/getlabel")
    fun getLabels(): Call<ESP_LIB_Labels>

    @GET("applicant/")
    fun Getapplicant(): Call<ESP_LIB_ApplicationProfileDAO>

    /* @GET("application/details/{id}")
    Call<DynamicResponseDAO> GetApplicationDetail(@Query("id") String id);*/

    @DELETE("application/{id}")
    fun deleteApplication(@Path("id") id: Int): Call<Any>

    @GET("application/detailsv2/{id}")
    fun GetApplicationDetailv2(@Path("id") id: String): Call<ESP_LIB_DynamicResponseDAO>

    @GET("application/feedback/{id}")
    fun GetApplicationFeedBack(@Path("id") applicationId: String): Call<List<ESP_LIB_ApplicationsFeedbackDAO>>

    @GET("application/linkedApplicationInfo/{id}")
    fun GetLinkApplicationInfo(@Path("id") applicationId: String): Call<ESP_LIB_LinkApplicationsDAO>

    @GET("category/AllWithQuery")
    fun AllCategories(): Call<List<ESP_LIB_CategoryAndDefinationsDAO>>

    @GET("category/AllWithQuery")
    fun AllWithQuery(): Call<List<ESP_LIB_DefinationsCategoriesDAO>>

    @GET("submittalRequest")
    fun getSubDefinitionList(): Call<List<ESP_LIB_CategoryAndDefinationsDAO>>

    @GET("submittalRequest/feeds")
    fun getfeedsList(): Call<List<ESP_LIB_ApplicationsDAO>>

    @GET("organization/users")
    fun getUser(): Call<List<ESP_LIB_UsersListDAO>>

    @GET("assessment/assessors/active")
    fun getCancelUser(@Query("applicationId") applicationId: Int?): Call<List<ESP_LIB_UsersListDAO>>

   /* @GET("definition")
    fun AllDefincations(@Query("categoryId") categoryId: Int?): Call<List<ESP_LIB_CategoryAndDefinationsDAO>>*/

    @GET("definition/forApplication/{id}")
    fun AllDefincationForm(@Path("id") id: Int?): Call<ESP_LIB_DynamicResponseDAO>

    @GET("definition/forApplicationV2/{id}/{parent_id}")
    fun getSubDefincationForm(@Path("id") id: Int?, @Path("parent_id") parent_id: Int?): Call<ESP_LIB_DynamicResponseDAO>

    @GET("submittalRequest/{id}/{parent_id}")
    fun getSubmittalForm(@Path("id") id: Int?, @Path("parent_id") parent_id: Int?): Call<ESP_LIB_SubmittalApplicationsDAO>

    @GET("application/dismiss/{applicationId}/{type}")
    fun getdismissApplication(@Path("applicationId") applicationId: Int?, @Path("type") type: String?): Call<Any>

    @GET("lookupitem/list/{lookupid}/0")
    fun Lookups(@Path("lookupid") lookupid: Int?): Call<List<ESP_LIB_LookUpDAO>>

    @GET("lookupitem/Item/{itemid}")
    fun getLookupItemDetail(@Path("itemid") itemid: Int?): Call<ESP_LIB_LookupItemDetailDAO>

    @POST("lookupitem/Items")
    fun postLookUpItems(@Body ESPLIBLookupInfoListItem: ESP_LIB_LookupInfoSearchDAO): Call<ESP_LIB_LookupInfoListDetailDAO>

    //@POST("application/submit")
    @POST("application/submitv2")
    fun SubmitApplication(@Body ESPLIBDynamicResponseDAO: ESP_LIB_DynamicResponseDAO): Call<Int>

    //@POST("application/create")
    @POST("application/createv2")
    fun DraftApplication(@Body filterDAOESPLIB: ESP_LIB_DynamicResponseDAO): Call<Int>

    @POST("application/respond")
    fun AcceptRejectApplication(@Body ESPLIBPost: ESP_LIB_PostApplicationsStatusDAO): Call<Int>


    /*@PUT("application/comments")
    fun AddEditComments_(@Body post: PostApplicationsCriteriaCommentsDAO): Call<Int>*/

    @PUT("applicant/")
    fun saveApplicantData(@Body post: ESP_LIB_ApplicationProfileDAO.Applicant): Call<Int>

    @PUT("application/cancel")
    fun cancelRequestData(@Body post: ESP_LIB_CancelApplicationDAO): Call<Any>

    @PUT("application/allowLinkedApplicationSubmission/{applicationId}/{isSubmissionAllowed}")
    fun saveLinkApplicationInfo(@Path("applicationId") applicationId: Int?,
                                @Path("isSubmissionAllowed") isSubmissionAllowed: Boolean?): Call<ESP_LIB_LinkApplicationsDAO>

    @PUT("applicant/basic")
    fun saveBasicData(@Body post: ESP_LIB_BasicDAO): Call<ESP_LIB_BasicDAO>

    @PUT("applicant/section/{sectionid}/{index}")
    fun updateApplicantDataBySectionId(@Path("sectionid") sectionid: Int?, @Path("index") index: Int?, @Body post: ArrayList<ESP_LIB_ApplicationProfileDAO.Values>): Call<ESP_LIB_ApplicationProfileDAO.Values>

    @POST("applicant/section/{sectionid}")
    fun saveApplicantDataBySectionId(@Path("sectionid") sectionid: Int?, @Body post: ArrayList<ESP_LIB_ApplicationProfileDAO.Values>): Call<Int>



    @Multipart
    @POST("signature")
    fun sendSignature(
            @Part file: MultipartBody.Part?,
            @Part("type") type: RequestBody,
            @Part("fontFamily") FontFamily: RequestBody,
            @Part("signatoryName") signatoryName: RequestBody,
            @Part("fileGuid") FileGuid: RequestBody


    ): Call<Any>


    @GET("signature")
    fun getSignature(): Call<ESP_LIB_SignatureDAO>



    @Multipart
    @PUT("application/comments")
    fun addComments(
            @Part("assessmentId") assessmentId: Int,
            @Part("comments") comments: RequestBody
    ): Call<Int>


    @Multipart
    @PUT("application/comments")
    fun EditComments(
            @Part("id") id: Int,
            @Part("assessmentId") assessmentId: Int,
            @Part("comments") comments: RequestBody
    ): Call<Int>



    @GET("currency")
    fun getCurrency(): Call<List<ESP_LIB_CurrencyDAO>>


  /*  @Multipart
    @PUT("application/comments")
    fun feedbackComments(
            @Part file: MultipartBody.Part?,
            @Part("applicationId") applicationId: Int,
            @Part("comments") comments: RequestBody,
            @Part("isVisibletoApplicant") isVisibletoApplicant: Boolean


    ): Call<Int>*/

    @Multipart
    @PUT("assessment/comments")
    fun feedbackComments(
            @Part file: MultipartBody.Part?,
            @Part("applicationId") applicationId: Int,
            @Part("comments") comments: RequestBody,
            @Part("assessmentId") assessmentId: Int,
            @Part("isVisibletoApplicant") isVisibletoApplicant: Boolean


    ): Call<Int>

    @POST("verify")
    fun uploadDetect(@Body ESPLIBPost: JSONObject): Call<Any>

    @Multipart
    @POST("upload")
    fun upload(@Part file: MultipartBody.Part): Call<ESP_LIB_ResponseFileUploadDAO>

    @Multipart
    @POST("applicant/picture")
    fun picture(@Part file: MultipartBody.Part?): Call<String>

}
