package utilities.interfaces

import utilities.data.applicants.ESP_LIB_ApplicationsDAO

interface ESP_LIB_DeleteDraftListener {

    fun deletedraftApplication(ESPLIBApplicationsDAO: ESP_LIB_ApplicationsDAO)

}
