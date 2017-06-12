package com.example.zhangnan.myfarm;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.example.zhangnan.myfarm.activity_information.Controller;
import com.example.zhangnan.myfarm.activity_information.Draught_FanController;
import com.example.zhangnan.myfarm.activity_information.FieldsInfo;
import com.example.zhangnan.myfarm.activity_information.Film_SideController;
import com.example.zhangnan.myfarm.activity_information.Film_TopController;
import com.example.zhangnan.myfarm.activity_information.LightController;
import com.example.zhangnan.myfarm.activity_information.WaningController;
import com.example.zhangnan.myfarm.activity_information.Water_PumpController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Far-away on 17/5/19.
 */

public class VisitServer {
    private String fieldsUrl = "http://farm.kideasoft.com/fields";
    private String controllerDetailUrl="http://farm.kideasoft.com/controllers";
    private OkHttpClient okHttpClient;
    private static final String TAG="VisitServer";
    private static final MediaType JSON=MediaType.parse("application/json; charset=utf-8");
    private String controlUrl="http://farm.kideasoft.com/set_judgement";



    public String getJsonString(String url){
        String jsonString=null;
        Request request=new Request.Builder().url(url).build();
        okHttpClient=new OkHttpClient();
        try {
            Response response=okHttpClient.newCall(request).execute();
            if (response.isSuccessful()){
                jsonString=response.body().string();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Log.d(TAG, "getJsonString: "+jsonString);
        return jsonString;
    }

    public void parseFields(List<FieldsInfo> fields, JSONObject jsonBody){
        try {
            JSONArray fieldsArray=jsonBody.getJSONArray("fields");
            for (int i=0;i<fieldsArray.length();i++){
                JSONObject filedObject=fieldsArray.getJSONObject(i);
                FieldsInfo filedsInfo=new FieldsInfo(filedObject.getString("id"),filedObject.getString("temp"),filedObject.getString("humidity"),
                filedObject.getString("ph"),filedObject.getString("name"));
                fields.add(filedsInfo);
                //Log.d(TAG, "parseFields: "+filedObject.getString("id"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public List<FieldsInfo> getFields(){
        List<FieldsInfo> fields=new ArrayList<>();
        String string=getJsonString(fieldsUrl);
        try {
            JSONObject object=new JSONObject(string);
            parseFields(fields,object);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Log.d(TAG, "getFields: "+fields.size());
        return fields;
    }

    //Controller detail
    public void parseControllers(Controller controller,JSONObject jsonObject){
        try {
            JSONObject object=jsonObject.getJSONObject("controller");

            JSONArray  jsonArrayL=object.getJSONArray("light");
            for(int i=0;i<jsonArrayL.length();i++){
                JSONObject jsonObjectL=jsonArrayL.getJSONObject(i);
                controller.getLightControllers().add(new LightController(jsonObjectL.getString("id"),jsonObjectL.getString("type"),
                jsonObjectL.getString("state")));
                //Log.d(TAG, "parseControllers: 11"+controller.getLightControllers().get(i).getState());
            }
            JSONArray jsonArrayWP=object.getJSONArray("water_pump");
            for (int i=0;i<jsonArrayWP.length();i++){
                JSONObject jsonObjectWP=jsonArrayWP.getJSONObject(i);
                controller.getWater_pumpControllers().add(new Water_PumpController(jsonObjectWP.getString("id"),jsonObjectWP.getString("type"),
                jsonObjectWP.getString("state")));
                //Log.d(TAG, "parseControllers: 22"+controller.getWater_pumpControllers().get(i).getType());
            }
            JSONArray jsonArrayD=object.getJSONArray("draught_fan");
            for (int i=0;i<jsonArrayD.length();i++){
                JSONObject jsonObjectD=jsonArrayD.getJSONObject(i);
                controller.getDraught_fansController().add(new Draught_FanController(jsonObjectD.getString("id"),jsonObjectD.getString("type"),
                        jsonObjectD.getString("state")));
                //Log.d(TAG, "parseControllers: 33"+controller.getWater_pumpControllers().get(i).getId());
            }
            JSONArray jsonArrayW=object.getJSONArray("waning");
            for (int i=0;i<jsonArrayW.length();i++){
                JSONObject jsonObjectW=jsonArrayW.getJSONObject(i);
                controller.getWaningControllers().add(new WaningController(jsonObjectW.getString("id"),jsonObjectW.getString("type"),
                        jsonObjectW.getString("state")));
                //Log.d(TAG, "parseControllers: 44"+controller.getWaningControllers().get(i).getType());
            }
            JSONArray jsonArrayFS=object.getJSONArray("film_side");
            for (int i=0;i<jsonArrayFS.length();i++){
                JSONObject jsonObjectFS=jsonArrayFS.getJSONObject(i);
                controller.getFilm_sideControllers().add(new Film_SideController(jsonObjectFS.getString("id"),jsonObjectFS.getString("type"),
                        jsonObjectFS.getString("state")));
                //Log.d(TAG, "parseControllers: 55"+controller.getFilm_sideControllers().get(i).getType());
            }
            JSONArray jsonArrayFT=object.getJSONArray("film_top");
            for (int i=0;i<jsonArrayFT.length();i++){
                JSONObject jsonObjectFT=jsonArrayFT.getJSONObject(i);
                controller.getFilm_topControllers().add(new Film_TopController(jsonObjectFT.getString("id"),jsonObjectFT.getString("type"),
                        jsonObjectFT.getString("state")));
                //Log.d(TAG, "parseControllers: 66"+controller.getFilm_topControllers().get(i).getType());
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Controller getControllers(){
        Controller controller=new Controller();
        String string=getJsonString(controllerDetailUrl);
        try {
            JSONObject object=new JSONObject(string);
            parseControllers(controller,object);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Log.d(TAG, "getControllers:77 "+controller.getLightControllers().get(1).getState());
        return controller;
    }
    //Post
    public String postJson(String json){
        okHttpClient=new OkHttpClient();
        String jsonString=null;
        RequestBody requestBody=RequestBody.create(JSON,json);
        Request request=new Request.Builder().url(controlUrl).post(requestBody).build();
        try {
            Response response=okHttpClient.newCall(request).execute();
            Log.d(TAG, "postJson: !!!!!");
            if(response.isSuccessful()){
                Log.d(TAG, "postJson: ++++++");
                jsonString=response.body().string();
                Log.d(TAG, "postJson: "+jsonString);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonString;
    }
}
