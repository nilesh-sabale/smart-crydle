// app/src/main/java/com/example/myapplication/CryEvent.java
package com.example.myapplication;

//public class CryEvent {
//    private String deviceId;
//    private String event;
//    private String reason;
//    private float  confidence;
//    private long   timestamp;
//
//    public CryEvent() { }  // required by Firebase
//
//    public String getDeviceId()   { return deviceId; }
//    public String getEvent()      { return event; }
//    public String getReason()     { return reason; }
//    public float   getConfidence(){ return confidence; }
//    public long    getTimestamp() { return timestamp; }
//}
// app/src/main/java/com/example/myapplication/CryEvent.java

public class CryEvent {
    private String deviceId;
    private String event;       // "cry_detected", "object_detected", or now "env_alert"
    private String reason;
    private float  confidence;  // unused for env, can set 0
    private long   timestamp;

    public CryEvent() { }  // required by Firebase

    // <— NEW
    public CryEvent(String deviceId, String event, String reason, float confidence, long timestamp) {
        this.deviceId   = deviceId;
        this.event      = event;
        this.reason     = reason;
        this.confidence = confidence;
        this.timestamp  = timestamp;
    }
    // —>

    public String getDeviceId()   { return deviceId; }
    public String getEvent()      { return event; }
    public String getReason()     { return reason; }
    public float   getConfidence(){ return confidence; }
    public long    getTimestamp() { return timestamp; }
}
