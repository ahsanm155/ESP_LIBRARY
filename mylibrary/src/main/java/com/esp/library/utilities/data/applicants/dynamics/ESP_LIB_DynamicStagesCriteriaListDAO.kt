package utilities.data.applicants.dynamics

import com.esp.library.utilities.data.applicants.signature.ESP_LIB_SignatureDAO
import java.io.Serializable

import utilities.data.ESP_LIB_Base


class ESP_LIB_DynamicStagesCriteriaListDAO : ESP_LIB_Base(), Serializable {

    var id: Int = 0
    var name: String? = null
    var stageId: Int = 0
    var ownerId: Int = 0
    var ownerIdJobRole: Int = 0
    var ownerIdUserLookupCustomFieldPath: String? = null
    var isOwner: Boolean = false
    var isValidate: Boolean = false
    var isEnabled: Boolean = false
    var reassignAssessments: Boolean = false
    var hasApplication: Boolean = false
    var isExpended: Boolean = false
    var isSigned: Boolean = false
    var weight: Int = 0
    var daysToComplete: Int = 0
    var subApplicantId: Int = 0
    var subApplicationId: Int = 0
    var subDefinitionId: Int = 0
    var ownerName: String? = null
    var ownerEmailAddress: String? = null
    var reminder: String? = null
    var assessmentStatus: String? = null
    var approveText: String? = null
    var rejectText: String? = null
    var signature: ESP_LIB_SignatureDAO? = null
    var type: String? = null
    var assessmentId: Int = 0
    var comments: List<ESP_LIB_DynamicStagesCriteriaCommentsListDAO>? = null
    var customFields:  List<ESP_LIB_DynamicFormSectionFieldDAO>? = null
    var customFieldsCount: Int = 0
    var isSystem: Boolean = false
    var permissions: List<String>? = null
    lateinit var form: ESP_LIB_DynamicFormDAO
    lateinit var formValues: List<ESP_LIB_DynamicFormValuesDAO>

    companion object {
        var BUNDLE_KEY = "DynamicStagesCriteriaListDAO"
    }
}
