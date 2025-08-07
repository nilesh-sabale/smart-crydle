// app/src/main/java/com/example/myapplication/EnvStatus.java
package com.example.myapplication;

public class EnvStatus {
    private String deviceId;
    private float temperature;
    private float humidity;
    private long timestamp;

    public EnvStatus() { }  // required by Firebase

    public String getDeviceId()    { return deviceId; }
    public float   getTemperature(){ return temperature; }
    public float   getHumidity()   { return humidity; }
    public long    getTimestamp()  { return timestamp; }
}
