package utilities.data.applicants

import java.io.Serializable

import utilities.data.ESP_LIB_Base
import utilities.data.applicants.dynamics.ESP_LIB_DynamicCriteriaCustomFieldsDetailDAO
import utilities.data.applicants.dynamics.ESP_LIB_DynamicCriteriaCustomFieldsLookUpsDAO
import utilities.data.applicants.dynamics.ESP_LIB_DynamicFormSectionFieldDAO
import utilities.data.applicants.dynamics.ESP_LIB_DynamicFormSectionFieldLookupValuesDAO
import utilities.data.applicants.dynamics.ESP_LIB_DynamicFormValuesDetailsDAO


class ESP_LIB_ApplicationDetailFieldsDAO : ESP_LIB_Base(), Serializable {

    var type: Int = 0
    var fieldName: String? = null
    var fieldvalue: String? = null
    var downloadURL: String? = null
    var photo_detail: ESP_LIB_DynamicFormValuesDetailsDAO? = null
    var singleSelection: List<ESP_LIB_DynamicFormSectionFieldLookupValuesDAO>? = null
    var multiSelection: List<ESP_LIB_DynamicFormSectionFieldLookupValuesDAO>? = null

    var photo_detailCriteria: ESP_LIB_DynamicCriteriaCustomFieldsDetailDAO? = null
    var singleSelectionCriteria: List<ESP_LIB_DynamicCriteriaCustomFieldsLookUpsDAO>? = null
    var multiselectionCriteria: List<ESP_LIB_DynamicCriteriaCustomFieldsLookUpsDAO>? = null
    var isViewGenerated: Boolean = false
    var isFileDownloaded: Boolean = false
    var isFileDownling: Boolean = false
    var isSection: Boolean = false
    var sectionname: String? = null
    var fieldsDAO: ESP_LIB_DynamicFormSectionFieldDAO? = null

    lateinit var tag: String
    var sectionId: Int = 0
    var lookupId: Int = 0

    companion object {
        var BUNDLE_KEY = "ApplicationDetailFieldsDAO"
    }
}
