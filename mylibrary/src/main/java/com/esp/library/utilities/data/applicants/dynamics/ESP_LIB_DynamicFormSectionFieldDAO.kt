package utilities.data.applicants.dynamics

import utilities.data.ESP_LIB_Base
import utilities.data.applicants.addapplication.ESP_LIB_LookUpDAO
import java.io.Serializable

class ESP_LIB_DynamicFormSectionFieldDAO : ESP_LIB_Base() ,Serializable{
    var id: Int = 0
    var objectId: Int = 0
    var label: String? = null
    var value: String? = null
    var currencyValue: String? = null
    var isRequired: Boolean = false
    var isCommon: Boolean = false
    var isVisible: Boolean = false
    var minVal: Int = 0
    var maxVal: Int = 0
    var minDate: String? = null
    var maxDate: String? = null
    var isReadOnly: Boolean = false
    var canDelete: Boolean = false
    var order: Int = 0
    var createdBy: Int = 0
    var type: Int = 0
    var createdOn: String? = null
    var lookupValues: List<ESP_LIB_DynamicFormSectionFieldLookupValuesDAO>? = null
    var details: ESP_LIB_DyanmicFormSectionFieldDetailsDAO? = null
    var isSystem: Boolean = false
    var allowedValuesCriteria: String? = null
    var allowedValuesCriteriaArray: List<String>? = null
    var selectedCurrencyId: Int = 0
    var selectedCurrencySymbol: String? = null
    var sectionTemplateFiledId: Int = 0
    var sectionCustomFieldId: Int = 0
    var lookUpId: Int = 0
    var lookupValue: String? = null
    var isTitleField: Boolean = false
    var isMappedCalculatedField: Boolean = false
    var isCanDisabled: Boolean = false
    var isViewGenerated: Boolean = false
    var post: ESP_LIB_DynamicFormValuesDAO? = null
    var error_field: String? = null
    var sectionId: Int = 0
    var sectionIndex: Int = 0
    var isShowToUserOnly: Boolean = false
    var tag: Int = 0
    var removePos: Int = 0
    var updatePositionAttachment: Int = 0
    var lookUpDAO: ESP_LIB_LookUpDAO? = null
    var isShowInList: Boolean = false
    var hasValue: Boolean = false
    var isTigger: Boolean = false
    var sectionType: Int = 0
    var targetFieldType: Int =0;
    var isValidate: Boolean = false // True when the filed validation is correct.

    companion object {

        var BUNDLE_KEY = "DynamicFormSectionFieldDAO"
    }
}
