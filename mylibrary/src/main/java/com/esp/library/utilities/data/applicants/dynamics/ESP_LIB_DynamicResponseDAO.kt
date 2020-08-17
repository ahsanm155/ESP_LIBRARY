package utilities.data.applicants.dynamics

import com.esp.library.utilities.data.applicants.ESP_LIB_SummaryDAO
import utilities.data.ESP_LIB_Base
import utilities.data.applicants.ESP_LIB_ApplicationsDAO
import java.io.Serializable


class ESP_LIB_DynamicResponseDAO : ESP_LIB_Base(), Serializable {


    var applicantsCounts: Int = 0
    var applicantId: Int = 0
    var applicationId: Int = 0
    var parentApplicationId: Int = 0
    var categoryId: Int = 0
    var createdBy: Int = 0
    var delegationsCount: Int = 0
    var applicationStatusId: Int = 0
    var applicationStatus: String? = null
    var applicationComment: String? = null
    var applicationSubmittedDate: String? = null
    var applicationCreatedDate: String? = null
    var applicantName: String? = null
    var applicantPictureStatus: String? = null
    var applicationNumber: String? = null
    var currentStageId: String? = null
    var type: String? = null
    var stageVisibilityApplicant: String? = null
    var permissions: List<String>? = null
    var id: Int = 0
    var profileTemplateId: Int = 0
    var referenceApplicationId: Int = 0
    var name: String? = null
    var profileTemplate: String? = null
    var isActive: Boolean = false
    var typeId: Int = 0
    var defaultRequestSectionId: Int = 0
    var submissionRequestSectionCustomFieldId: Int = 0
    var submissionRequestDetailsSCFId: Int = 0
    var description: String? = null
    var category: String? = null
    var isPublished: Boolean = false
    var isprofileCompleted: Boolean = false
    var isProfileCompletionRequired: Boolean = false
    var createdOn: String? = null
    var endDate: String? = null
    var stages: List<ESP_LIB_DynamicStagesDAO>? = null
    var form: ESP_LIB_DynamicFormDAO? = null
    var details: ESP_LIB_DynamicFormValuesDAO? = null
    var summary: ESP_LIB_SummaryDAO? = null

    //Use For Post Data SubmitV2Api
    var sectionValues: List<ESP_LIB_DynamicSectionValuesDAO>? = null

    companion object {
        var BUNDLE_KEY = "DynamicResponseDAO"
    }
}
