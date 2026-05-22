package com.example.domo.data.repository

import com.example.domo.domain.model.AppInfo

interface AppRepository {
    suspend fun getInstalledApps(): List<AppInfo>
}
