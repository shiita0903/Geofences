package jp.shiita.geofences

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 0)
            }
        }

        addButton.setOnClickListener {
            val geofencingClient = LocationServices.getGeofencingClient(this)

            val geofence = Geofence.Builder()
                .setRequestId("Geofence")
                .setCircularRegion(35.681098, 139.767062, 100f)     // 東京駅から半径100m
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
                .build()

            val request = GeofencingRequest.Builder()
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .addGeofence(geofence)
                .build()

            // IntentServiceは使えない
//            val pendingIntent = PendingIntent.getService(
//                this,
//                0,
//                Intent(this, GeofenceTransitionsIntentService::class.java),
//                PendingIntent.FLAG_UPDATE_CURRENT)

            // BroadcastReceiverに変更
            val pendingIntent = PendingIntent.getBroadcast(
                this,
                0,
                Intent(this, GeofenceTransitionsBroadcastReceiver::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT)

            // Geofenceの設置
            geofencingClient.addGeofences(request, pendingIntent)?.also {
                it.addOnSuccessListener { Toast.makeText(this, "addOnSuccess", Toast.LENGTH_SHORT).show() }
                it.addOnFailureListener { Toast.makeText(this, "addOnFailure", Toast.LENGTH_SHORT).show() }
            }
        }
    }
}
