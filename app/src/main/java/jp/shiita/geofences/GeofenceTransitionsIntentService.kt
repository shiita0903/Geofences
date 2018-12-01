package jp.shiita.geofences

import android.app.IntentService
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.support.v4.app.NotificationCompat
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent


class GeofenceTransitionsIntentService : IntentService("Geofence") {
    override fun onHandleIntent(intent: Intent?) {
        val geofencingEvent = GeofencingEvent.fromIntent(intent)

        when (geofencingEvent.geofenceTransition) {
            Geofence.GEOFENCE_TRANSITION_ENTER -> sendNotification("Enter")
            Geofence.GEOFENCE_TRANSITION_EXIT -> sendNotification("Exit")
            else -> sendNotification("error")
        }
    }

    private fun sendNotification(text: String) {
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel(manager)

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setStyle(NotificationCompat.BigTextStyle())
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Geofence")
            .setContentText(text)
            .build()
        manager.notify(0, notification)
    }

    private fun createNotificationChannel(manager: NotificationManager) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            if (manager.getNotificationChannel(CHANNEL_ID) == null) {
                val channel = NotificationChannel(CHANNEL_ID, "name", NotificationManager.IMPORTANCE_HIGH)
                channel.description = "description"
                manager.createNotificationChannel(channel)
            }
        }
    }

    companion object {
        private const val CHANNEL_ID = "channelId"
    }
}