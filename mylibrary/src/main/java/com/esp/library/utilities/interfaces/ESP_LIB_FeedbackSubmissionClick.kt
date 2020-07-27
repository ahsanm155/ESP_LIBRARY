package utilities.interfaces

import utilities.data.applicants.dynamics.ESP_LIB_DynamicStagesCriteriaListDAO
import utilities.data.applicants.dynamics.ESP_LIB_DynamicStagesDAO

interface ESP_LIB_FeedbackSubmissionClick {

    fun feedbackClick(b: Boolean, criteriaListDAOESPLIB: ESP_LIB_DynamicStagesCriteriaListDAO?, ESPLIBDynamicStagesDAO: ESP_LIB_DynamicStagesDAO?, position: Int)
}
