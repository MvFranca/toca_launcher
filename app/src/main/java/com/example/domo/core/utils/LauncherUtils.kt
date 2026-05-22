package com.example.domo.core.utils

import android.app.role.RoleManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.provider.Settings

object LauncherUtils {

    fun isDefaultLauncher(context: Context): Boolean {
        val roleManager = context.getSystemService(RoleManager::class.java)
        return roleManager.isRoleHeld(RoleManager.ROLE_HOME)
    }

    fun openDefaultLauncherSettings(context: Context) {
        // Tenta abrir direto a tela de Home app (funciona no Android puro / Pixel)
        // Se a marca personalizar o sistema e bloquear esse intent, cai no fallback
        val launched = tryStartIntent(context, Intent(Settings.ACTION_HOME_SETTINGS))
        if (!launched) {
            // Fallback: tela geral de "Apps padrão", disponível em praticamente todas as marcas
            tryStartIntent(context, Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS))
        }
    }

    private fun tryStartIntent(context: Context, intent: Intent): Boolean {
        return try {
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
            true
        } catch (e: ActivityNotFoundException) {
            false
        }
    }
}
