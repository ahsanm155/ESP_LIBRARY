package utilities.interfaces

import utilities.data.filters.ESP_LIB_FilterDefinitionSortDAO

interface ApplicationsFilterListener {

    fun selectedValues(ESPLIBFilterDefinitionSortList: List<ESP_LIB_FilterDefinitionSortDAO>, position: Int, checked: Boolean);
    fun selectedSortValues(ESPLIBFilterDefinitionSortList: ESP_LIB_FilterDefinitionSortDAO, ESPLIBFilterSortByListSort: List<ESP_LIB_FilterDefinitionSortDAO>, position: Int);
}
