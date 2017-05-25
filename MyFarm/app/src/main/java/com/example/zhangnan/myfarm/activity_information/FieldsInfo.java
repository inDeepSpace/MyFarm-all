package com.example.zhangnan.myfarm.activity_information;

/**
 * Created by Far-away on 17/5/19.
 */

public class FieldsInfo {
    private String id;
    private String temp;
    private String name;
    private String humidity;
    private String ph;
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getIllumination() {
        return ph;
    }

    public void setIllumination(String illumination) {
        this.ph = illumination;
    }

    public FieldsInfo(String id, String temp, String humidity, String ph,String name) {
        this.id = id;
        this.temp = temp;
        this.humidity = humidity;
        this.ph = ph;
        this.name=name;
    }
}
