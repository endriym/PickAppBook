package com.munity.pickappbook.core.data.repository

import com.munity.pickappbook.core.data.remote.model.GetPickupLineListRequest
import com.munity.pickappbook.core.ui.components.SortType

fun SortType.asNetworkModel(): GetPickupLineListRequest.SortType =
    when (this) {
        SortType.New -> GetPickupLineListRequest.SortType.NEW
        SortType.Best -> GetPickupLineListRequest.SortType.BEST_OF_ALL_TIME
        SortType.Trending -> GetPickupLineListRequest.SortType.TRENDING
        SortType.Random -> GetPickupLineListRequest.SortType.RANDOM
    }
