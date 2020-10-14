package com.esp.library.utilities.interfaces

import com.esp.library.utilities.data.filters.ESP_LIB_FilterDefinitionSortDAO

interface ESP_LIB_FilterListener {

    fun selectedValues(ESPLIBFilterDefinitionSortList: List<ESP_LIB_FilterDefinitionSortDAO>, position: Int, checked: Boolean);
    fun selectedSortValues(ESPLIBFilterDefinitionSortList: ESP_LIB_FilterDefinitionSortDAO, ESPLIBFilterSortByListSort: List<ESP_LIB_FilterDefinitionSortDAO>, position: Int);
}
