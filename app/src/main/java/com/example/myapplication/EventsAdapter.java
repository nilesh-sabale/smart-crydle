//// app/src/main/java/com/example/myapplication/EventsAdapter.java
//package com.example.myapplication;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.recyclerview.widget.RecyclerView;
//
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.List;
//import java.util.Locale;
//
//public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.ViewHolder> {
//
//    private final List<CryEvent> events;
//    private final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss dd/MM", Locale.getDefault());
//
//    public EventsAdapter(List<CryEvent> events) {
//        this.events = events;
//    }
//
//    @Override
//    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View v = LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.item_event, parent, false);
//        return new ViewHolder(v);
//    }
//
//    @Override
//    public void onBindViewHolder(ViewHolder h, int pos) {
//        CryEvent e = events.get(pos);
//        boolean isCry = "cry_detected".equals(e.getEvent());
//        h.eventReason.setText(
//                isCry
//                        ? "Cry: " + e.getReason()
//                        : "Object: " + e.getReason()
//        );
//        h.eventTimestamp.setText( sdf.format(new Date(e.getTimestamp())) );
//        h.eventIcon.setImageResource(
//                isCry
//                        ? R.drawable.ic_cry
//                        : R.drawable.ic_object
//        );
//    }
//
//    @Override public int getItemCount() { return events.size(); }
//
//    static class ViewHolder extends RecyclerView.ViewHolder {
//        ImageView eventIcon;
//        TextView  eventReason, eventTimestamp;
//        ViewHolder(View v) {
//            super(v);
//            eventIcon      = v.findViewById(R.id.eventIcon);
//            eventReason    = v.findViewById(R.id.eventReason);
//            eventTimestamp = v.findViewById(R.id.eventTimestamp);
//        }
//    }
//}
// app/src/main/java/com/example/myapplication/EventsAdapter.java
package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.ViewHolder> {

    private final List<CryEvent> events;
    //private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM", Locale.getDefault());

    public EventsAdapter(List<CryEvent> events) {
        this.events = events;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder h, int pos) {
        CryEvent e = events.get(pos);
        String evt = e.getEvent();
        String reasonText;
        int    iconRes;

        switch (evt) {
            case "cry_detected":
                reasonText = "Cry: " + e.getReason();
                iconRes    = R.drawable.ic_cry;
                break;
            case "object_detected":
                reasonText = "Object: " + e.getReason();
                iconRes    = R.drawable.ic_object;
                break;
            case "env_alert":   // <— NEW
                reasonText = "Env: " + e.getReason();
                iconRes    = R.drawable.ic_baby;  // or a custom R.drawable.ic_env
                break;
            default:
                reasonText = e.getReason();
                iconRes    = R.drawable.ic_baby;
        }

        h.eventReason.setText(reasonText);
        //h.eventTimestamp.setText(sdf.format(new Date(e.getTimestamp())));
        h.eventIcon.setImageResource(iconRes);
    }

    @Override public int getItemCount() { return events.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView eventIcon;
        TextView  eventReason, eventTimestamp;
        ViewHolder(View v) {
            super(v);
            eventIcon      = v.findViewById(R.id.eventIcon);
            eventReason    = v.findViewById(R.id.eventReason);
            eventTimestamp = v.findViewById(R.id.eventTimestamp);
        }
    }
}

