package com.example.zhangnan.myfarm.activity_information;

/**
 * Created by zhangnan on 17/5/22.
 */

public class FieldsDetailsInfo {
    private int id;
    private com.example.zhangnan.myfarm.activity_information.blower[] blower;
    private com.example.zhangnan.myfarm.activity_information.co2[] co2;
    private com.example.zhangnan.myfarm.activity_information.lamp[] lamp;
    private com.example.zhangnan.myfarm.activity_information.light[] light;
    private com.example.zhangnan.myfarm.activity_information.nmembrane[] nmembrane;
    private com.example.zhangnan.myfarm.activity_information.pump[] pump;
    private com.example.zhangnan.myfarm.activity_information.salt[] salt;
    private com.example.zhangnan.myfarm.activity_information.tmembrane[] tmembrane;
    private com.example.zhangnan.myfarm.activity_information.water[] water;
    private com.example.zhangnan.myfarm.activity_information.web[] web;
    private int count;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public com.example.zhangnan.myfarm.activity_information.blower[] getBlower() {
        return blower;
    }

    public void setBlower(com.example.zhangnan.myfarm.activity_information.blower[] blower) {
        this.blower = blower;
    }

    public com.example.zhangnan.myfarm.activity_information.co2[] getCo2() {
        return co2;
    }

    public void setCo2(com.example.zhangnan.myfarm.activity_information.co2[] co2) {
        this.co2 = co2;
    }

    public com.example.zhangnan.myfarm.activity_information.lamp[] getLamp() {
        return lamp;
    }

    public void setLamp(com.example.zhangnan.myfarm.activity_information.lamp[] lamp) {
        this.lamp = lamp;
    }

    public com.example.zhangnan.myfarm.activity_information.light[] getLight() {
        return light;
    }

    public void setLight(com.example.zhangnan.myfarm.activity_information.light[] light) {
        this.light = light;
    }

    public com.example.zhangnan.myfarm.activity_information.nmembrane[] getNmembrane() {
        return nmembrane;
    }

    public void setNmembrane(com.example.zhangnan.myfarm.activity_information.nmembrane[] nmembrane) {
        this.nmembrane = nmembrane;
    }

    public com.example.zhangnan.myfarm.activity_information.pump[] getPump() {
        return pump;
    }

    public void setPump(com.example.zhangnan.myfarm.activity_information.pump[] pump) {
        this.pump = pump;
    }

    public com.example.zhangnan.myfarm.activity_information.salt[] getSalt() {
        return salt;
    }

    public void setSalt(com.example.zhangnan.myfarm.activity_information.salt[] salt) {
        this.salt = salt;
    }

    public com.example.zhangnan.myfarm.activity_information.tmembrane[] getTmembrane() {
        return tmembrane;
    }

    public void setTmembrane(com.example.zhangnan.myfarm.activity_information.tmembrane[] tmembrane) {
        this.tmembrane = tmembrane;
    }

    public com.example.zhangnan.myfarm.activity_information.water[] getWater() {
        return water;
    }

    public void setWater(com.example.zhangnan.myfarm.activity_information.water[] water) {
        this.water = water;
    }

    public com.example.zhangnan.myfarm.activity_information.web[] getWeb() {
        return web;
    }

    public void setWeb(com.example.zhangnan.myfarm.activity_information.web[] web) {
        this.web = web;
    }

    public int getSensorsCount() {
        count = 0 ;
        if (co2 != null && co2.length != 0){
            int co = co2.length;
            count+=co;
        }
        if (light != null && light.length != 0){
            int li = light.length;
            count+=li;
        }

        if (salt != null && salt.length != 0){
            int sa = salt.length;
            count+=sa;
        }

        if (water != null && water.length != 0){
            int wa = water.length;
            count+=wa;
        }


        return count;
    }

}
