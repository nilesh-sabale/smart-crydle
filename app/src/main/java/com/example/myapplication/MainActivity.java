// app/src/main/java/com/example/myapplication/MainActivity.java
package com.example.myapplication;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView tempTextView, humidityTextView;
    private RecyclerView eventsRecyclerView;
    private FloatingActionButton refreshFab;

    private EventsAdapter adapter;
    private ArrayList<CryEvent> eventList = new ArrayList<>();

    private DatabaseReference envRef, cryRef;
    public static final String CRY_CHANNEL    = "cry_alerts";
    public static final String OBJECT_CHANNEL = "object_alerts";
    public static final String ENV_CHANNEL    = "env_alerts";

    FirebaseAuth auth;
    FirebaseUser user;

    // Thresholds
    //private static final float TEMP_HIGH = 37.5f;
    private static final float TEMP_HIGH = 37.5f;
    private static final float TEMP_LOW  = 20.0f;
    private static final float HUM_HIGH  = 70.0f;
    private static final float HUM_LOW   = 30.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        if (user == null) {
            startActivity(new Intent(this, Login.class));
            finish();
            return;
        }

        // Top app bar setup
        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);
        setSupportActionBar(topAppBar);

        // Bind views
        tempTextView       = findViewById(R.id.tempTextView);
        humidityTextView   = findViewById(R.id.humidityTextView);
        eventsRecyclerView = findViewById(R.id.eventsRecyclerView);
        refreshFab         = findViewById(R.id.refreshFab);

        // RecyclerView adapter
        adapter = new EventsAdapter(eventList);
        eventsRecyclerView.setAdapter(adapter);

        // Firebase references
        envRef = FirebaseDatabase.getInstance().getReference("env-status");
        cryRef = FirebaseDatabase.getInstance().getReference("cry-detection");

        // Create notification channels
        createNotificationChannel(CRY_CHANNEL, "Cry Alerts", "Alerts when baby cry is detected");
        createNotificationChannel(OBJECT_CHANNEL, "Object Alerts", "Alerts when object is detected");
        createNotificationChannel(ENV_CHANNEL, "Environment Alerts", "Alerts when temperature or humidity is out of safe range");

        // Cry detection listener
        cryRef.addChildEventListener(new ChildEventListener() {
            @Override public void onChildAdded(@NonNull DataSnapshot snap, String prev) {
                CryEvent e = snap.getValue(CryEvent.class);
                if (e != null) handleEventNotification(e);
            }
            @Override public void onChildChanged(@NonNull DataSnapshot s, String p) { }
            @Override public void onChildRemoved(@NonNull DataSnapshot s) { }
            @Override public void onChildMoved(@NonNull DataSnapshot s, String p) { }
            @Override public void onCancelled(@NonNull DatabaseError err) {
                Log.e("CryListener", "Cancelled", err.toException());
            }
        });

        // Realtime environment update with alert logic
//        envRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snap) {
//                EnvStatus s = snap.getValue(EnvStatus.class);
//                if (s != null) {
//                    float temp = s.getTemperature();
//                    float hum  = s.getHumidity();
//                    tempTextView.setText(temp + " °C");
//                    humidityTextView.setText(hum + " %");
//
//                    // Check thresholds and send alerts
//                    if (temp > TEMP_HIGH) {
//                        sendEnvAlert("High Temperature", "Temperature is " + temp + "°C, above safe threshold.");
//                    } else if (temp < TEMP_LOW) {
//                        sendEnvAlert("Low Temperature", "Temperature is " + temp + "°C, below safe threshold.");
//                    }
//                    if (hum > HUM_HIGH) {
//                        sendEnvAlert("High Humidity", "Humidity is " + hum + "% , above safe threshold.");
//                    } else if (hum < HUM_LOW) {
//                        sendEnvAlert("Low Humidity", "Humidity is " + hum + "% , below safe threshold.");
//                    }
//                }
//            }
//            @Override public void onCancelled(@NonNull DatabaseError err) {
//                Log.e("EnvStatus", "Read failed", err.toException());
//            }
//        });
        envRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snap) {
                EnvStatus s = snap.getValue(EnvStatus.class);
                if (s != null) {
                    float temp = s.getTemperature();
                    float hum  = s.getHumidity();
                    tempTextView.setText(temp + " °C");
                    humidityTextView.setText(hum + " %");

                    long nowTs = System.currentTimeMillis();

                    if (temp > TEMP_HIGH) {
                        sendEnvAlert("High Temperature", "Temperature is " + temp + "°C, above safe threshold.");
                        eventList.add(0, new CryEvent(s.getDeviceId(), "env_alert",
                                String.format(Locale.getDefault(), "Temp: %.1f°C", temp), 0f, nowTs));
                        adapter.notifyItemInserted(0);
                    } else if (temp < TEMP_LOW) {
                        sendEnvAlert("Low Temperature", "Temperature is " + temp + "°C, below safe threshold.");
                        eventList.add(0, new CryEvent(s.getDeviceId(), "env_alert",
                                String.format(Locale.getDefault(), "Temp: %.1f°C", temp), 0f, nowTs));
                        adapter.notifyItemInserted(0);
                    }

                    if (hum > HUM_HIGH) {
                        sendEnvAlert("High Humidity", "Humidity is " + hum + "%, above safe threshold.");
                        eventList.add(0, new CryEvent(s.getDeviceId(), "env_alert",
                                String.format(Locale.getDefault(), "Hum: %.1f%%", hum), 0f, nowTs));
                        adapter.notifyItemInserted(0);
                    } else if (hum < HUM_LOW) {
                        sendEnvAlert("Low Humidity", "Humidity is " + hum + "%, below safe threshold.");
                        eventList.add(0, new CryEvent(s.getDeviceId(), "env_alert",
                                String.format(Locale.getDefault(), "Hum: %.1f%%", hum), 0f, nowTs));
                        adapter.notifyItemInserted(0);
                    }
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError err) { /*…*/ }
        });


        // Load last 20 cry events
        cryRef.limitToLast(20).addValueEventListener(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot snap) {
                eventList.clear();
                for (DataSnapshot c : snap.getChildren()) {
                    CryEvent e = c.getValue(CryEvent.class);
                    if (e != null) eventList.add(0, e);
                }
                adapter.notifyDataSetChanged();
            }
            @Override public void onCancelled(@NonNull DatabaseError err) {
                Log.e("CryList", "Load failed", err.toException());
            }
        });

        // FAB refresh
        refreshFab.setOnClickListener(v -> {
            envRef.get().addOnSuccessListener(snapshot -> {
                EnvStatus s = snapshot.getValue(EnvStatus.class);
                if (s != null) {
                    tempTextView.setText(s.getTemperature() + " °C");
                    humidityTextView.setText(s.getHumidity() + " %");
                }
            }).addOnFailureListener(e ->
                    Log.e("Refresh", "env-status fetch failed", e)
            );

            cryRef.limitToLast(20).get().addOnSuccessListener(snapshot -> {
                eventList.clear();
                for (DataSnapshot child : snapshot.getChildren()) {
                    CryEvent e = child.getValue(CryEvent.class);
                    if (e != null) eventList.add(0, e);
                }
                adapter.notifyDataSetChanged();
            }).addOnFailureListener(e ->
                    Log.e("Refresh", "cry-detection fetch failed", e)
            );
        });

    }

    private void sendEnvAlert(String title, String body) {
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder b = new NotificationCompat.Builder(this, ENV_CHANNEL)
                .setSmallIcon(R.drawable.ic_baby)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true);
        nm.notify((int) System.currentTimeMillis(), b.build());
    }

    private void handleEventNotification(CryEvent e) {
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String title, body, channelId;

        if ("cry_detected".equals(e.getEvent())) {
            title     = "Baby Cry Detected!";
            body      = "Reason: " + e.getReason() + " (" + Math.round(e.getConfidence() * 100) + "%)";
            channelId = CRY_CHANNEL;
        } else {
            title     = "Object Detected!";
            body      = "Reason: " + e.getReason();
            channelId = OBJECT_CHANNEL;
        }

        NotificationCompat.Builder b = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_baby)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true);

        nm.notify((int) e.getTimestamp(), b.build());
    }

    private void createNotificationChannel(String id, String name, String desc) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel ch = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_HIGH);
            ch.setDescription(desc);
            ((NotificationManager) getSystemService(NotificationManager.class))
                    .createNotificationChannel(ch);
        }
    }

    // Inflate logout menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_app_bar_menu, menu);
        return true;
    }

    // Handle logout
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            auth.signOut();
            startActivity(new Intent(this, Login.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
