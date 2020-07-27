package utilities.data.applicants.dynamics

import java.io.Serializable

import utilities.data.ESP_LIB_Base


class ESP_LIB_DynamicStagesCriteriaCommentsListDAO : ESP_LIB_Base(), Serializable {

    var id: Int = 0
    var userId: Int = 0
    var commentUserId: Int = 0
    var assessmentId: Int = 0
    var applicationId: Int = 0
    var fullName: String? = null
    var imageUrl: String? = null
    var attachments: List<ESP_LIB_DyanmicFormSectionFieldDetailsDAO>? = null
    var comment: String? = null
    var isVisibletoApplicant: Boolean = false
    var createdOn: String? = null
    var isOwner: Boolean = false
    var isAdmin: Boolean = false

    companion object {
        var BUNDLE_KEY = "DynamicStagesCriteriaCommentsListDAO"
    }
}
