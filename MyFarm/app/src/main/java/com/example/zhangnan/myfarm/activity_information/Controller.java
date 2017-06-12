package com.example.zhangnan.myfarm.activity_information;

import android.graphics.LightingColorFilter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Far-away on 17/5/24.
 */

public class Controller {
    private List<Draught_FanController> draught_fansController;
    private List<LightController> lightControllers;
    private List<Film_SideController> film_sideControllers;
    private List<Film_TopController> film_topControllers;
    private List<WaningController> waningControllers;
    private List<Water_PumpController> water_pumpControllers;

    public String getCurrentController() {
        return currentController;
    }

    public void setCurrentController(String currentController) {
        this.currentController = currentController;
    }

    private String currentController;

    public Controller() {
        draught_fansController=new ArrayList<>();
        lightControllers=new ArrayList<>();
        film_sideControllers=new ArrayList<>();
        film_topControllers=new ArrayList<>();
        waningControllers=new ArrayList<>();
        water_pumpControllers=new ArrayList<>();
    }

    public List<Draught_FanController> getDraught_fansController() {
        return draught_fansController;
    }

    public void setDraught_fans(List<Draught_FanController> draught_fens) {
        this.draught_fansController = draught_fens;
    }

    public List<LightController> getLightControllers() {
        return lightControllers;
    }

    public void setLightControllers(List<LightController> lightControllers) {
        this.lightControllers = lightControllers;
    }

    public List<Film_SideController> getFilm_sideControllers() {
        return film_sideControllers;
    }

    public void setFilm_sideControllers(List<Film_SideController> film_sideControllers) {
        this.film_sideControllers = film_sideControllers;
    }

    public List<Film_TopController> getFilm_topControllers() {
        return film_topControllers;
    }

    public void setFilm_topControllers(List<Film_TopController> film_topControllers) {
        this.film_topControllers = film_topControllers;
    }

    public List<WaningController> getWaningControllers() {
        return waningControllers;
    }

    public void setWaningControllers(List<WaningController> waningControllers) {
        this.waningControllers = waningControllers;
    }

    public List<Water_PumpController> getWater_pumpControllers() {
        return water_pumpControllers;
    }

    public void setWater_pumpControllers(List<Water_PumpController> water_pumpControllers) {
        this.water_pumpControllers = water_pumpControllers;
    }

}
