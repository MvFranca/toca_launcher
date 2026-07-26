package com.example.domo.feature.home.data

import com.example.domo.core.model.AppInfo

interface AppRepository {
    suspend fun getInstalledApps(): List<AppInfo>
}
