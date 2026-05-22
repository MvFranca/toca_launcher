package com.example.domo.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.domo.R
import com.example.domo.ui.pin.PinLockActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class AppBlockerService : Service() {

    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.Default + job)

    private var lastForegroundPackage: String = ""

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_ALLOW_PACKAGE -> {
                val pkg = intent.getStringExtra(EXTRA_PACKAGE) ?: return START_STICKY
                temporarilyAllowed.add(pkg)
            }
            else -> {
                startForeground(NOTIFICATION_ID, buildNotification())
                startMonitoring()
            }
        }
        return START_STICKY
    }

    private fun startMonitoring() {
        scope.launch {
            val usageStatsManager = getSystemService(USAGE_STATS_SERVICE) as UsageStatsManager
            while (isActive) {
                val foreground = getForegroundApp(usageStatsManager)

                // Quando o app muda, remove da lista temporária o app anterior
                if (foreground != null && foreground != lastForegroundPackage) {
                    if (lastForegroundPackage.isNotEmpty()) {
                        temporarilyAllowed.remove(lastForegroundPackage)
                    }
                    lastForegroundPackage = foreground
                }

                val shouldBlock = foreground != null
                    && foreground != packageName
                    && isBlocked(foreground)
                    && !temporarilyAllowed.contains(foreground)

                if (shouldBlock) {
                    showPinScreen(foreground!!)
                }

                delay(POLL_INTERVAL_MS)
            }
        }
    }

    private fun getForegroundApp(usageStatsManager: UsageStatsManager): String? {
        val now = System.currentTimeMillis()
        val events = usageStatsManager.queryEvents(now - 5_000, now)
        val event = UsageEvents.Event()
        var foreground: String? = null
        while (events.hasNextEvent()) {
            events.getNextEvent(event)
            if (event.eventType == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                foreground = event.packageName
            }
        }
        return foreground
    }

    private fun isBlocked(packageName: String): Boolean =
        BLOCKED_PACKAGES.contains(packageName)

    private fun showPinScreen(blockedPackage: String) {
        val intent = Intent(this, PinLockActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            putExtra(PinLockActivity.EXTRA_BLOCKED_PACKAGE, blockedPackage)
        }
        startActivity(intent)
    }

    private fun buildNotification(): Notification {
        val channelId = CHANNEL_ID
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (manager.getNotificationChannel(channelId) == null) {
            val channel = NotificationChannel(
                channelId,
                "Controle Parental",
                NotificationManager.IMPORTANCE_LOW,
            )
            manager.createNotificationChannel(channel)
        }
        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Domo — Proteção ativa")
            .setContentText("Monitorando apps em segundo plano.")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setOngoing(true)
            .build()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    companion object {
        private const val NOTIFICATION_ID = 1001
        private const val CHANNEL_ID = "domo_blocker"
        private const val POLL_INTERVAL_MS = 800L

        const val ACTION_ALLOW_PACKAGE = "com.example.domo.ALLOW_PACKAGE"
        const val EXTRA_PACKAGE = "extra_package"

        // Packages liberados temporariamente após PIN correto
        val temporarilyAllowed: MutableSet<String> = mutableSetOf()

        val BLOCKED_PACKAGES: Set<String> = setOf(
            "com.google.android.youtube",
            "com.instagram.android",
            "com.facebook.katana",
            "com.twitter.android",
            "com.zhiliaoapp.musically",   // TikTok
            "com.ss.android.ugc.trill",   // TikTok (variante)
        )
    }
}
