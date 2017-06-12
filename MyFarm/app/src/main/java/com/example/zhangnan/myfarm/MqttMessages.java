package com.example.zhangnan.myfarm;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.zhangnan.myfarm.activity_information.FieldsDetailsInfo;
import com.example.zhangnan.myfarm.activity_information.blower;
import com.example.zhangnan.myfarm.activity_information.co2;
import com.example.zhangnan.myfarm.activity_information.lamp;
import com.example.zhangnan.myfarm.activity_information.light;
import com.example.zhangnan.myfarm.activity_information.nmembrane;
import com.example.zhangnan.myfarm.activity_information.pump;
import com.example.zhangnan.myfarm.activity_information.salt;
import com.example.zhangnan.myfarm.activity_information.tmembrane;
import com.example.zhangnan.myfarm.activity_information.water;
import com.example.zhangnan.myfarm.activity_information.web;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhangnan on 17/4/24.
 */

public class MqttMessages{

    private String host = "tcp://10.0.2.2:1883";
    private Handler handler;
    private MqttClient client;
    private String myTopic;
    private MqttConnectOptions options;
    private ScheduledExecutorService scheduler;
    private String[] name ={"light","co2","water","salt"};

    //田地所有传感器数据JavaBean
    public  FieldsDetailsInfo fieldsDetailsInfo;

    public static Handler updateUIHandler;

    public MqttMessages(String myTopic){
        this.myTopic =myTopic;
        init();
        connect();
        getMessages();
    }

    public void getMessages(){
        init();
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.what == 1) {

                    String mqttInfo = msg.obj.toString();
                    Log.d("json",mqttInfo);
                    if (mqttInfo.length() != 0){
                        fieldsDetailsInfo = new FieldsDetailsInfo();
                        parserJson(mqttInfo);
                        sendMessages();
                    }

                } else if(msg.what == 2) {

                    try {
                        client.subscribe(myTopic, 1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if(msg.what == 3) {

                }
            }
        };
        startReconnect();
    }

    public void startReconnect() {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                if(!client.isConnected()) {
                    connect();
                }
            }
        }, 0 * 1000, 1 * 1000, TimeUnit.MILLISECONDS);
    }

    public void init() {
        try {
            client = new MqttClient(host, "test", new MemoryPersistence());
            options = new MqttConnectOptions();
            options.setCleanSession(true);
            options.setConnectionTimeout(10);
            options.setKeepAliveInterval(20);
            client.setCallback(new MqttCallback() {

                @Override
                public void connectionLost(Throwable cause) {
                    //连接丢失后，重连
                    System.out.println("connectionLost----------");
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    //publish
                    System.out.println("deliveryComplete---------"
                            + token.isComplete());
                }

                @Override
                public void messageArrived(String topicName, MqttMessage message)
                        throws Exception {
                    //subscribe
                    System.out.println("messageArrived----------");
                    Message msg = new Message();
                    msg.what = 1;
                    msg.obj = message.toString();
                    handler.sendMessage(msg);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void connect() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    client.connect(options);
                    client.subscribe(myTopic, 1);
                    Message msg = new Message();
                    msg.what = 2;
                    handler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                    Message msg = new Message();
                    msg.what = 3;
                    handler.sendMessage(msg);
                }
            }
        }).start();
    }

    public  void close() {
        try {
            scheduler.shutdown();
            client.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private  void parserJson(final String j) {

        JSONObject jsonObject = JSON.parseObject(j);
        getFieldId(jsonObject);
        getSensors(jsonObject);
        getControls(jsonObject);

    }

    private void getSensors(JSONObject jsonObject){
        JSONArray sensors =jsonObject.getJSONArray("sensors");

        if(sensors != null){
            for (int i = 0; i < sensors.size(); i++) {
                if (i == 0) {
                    JSONArray sensor = sensors.getJSONObject(i).getJSONArray(name[i]);
                    light[] ls = new light[sensor.size()];
                    for (int k = 0; k < sensor.size(); k++) {
                        //Log.d("chuanganqi",String.valueOf(sensor.size()));
                        light l = JSON.parseObject(sensor.getJSONObject(k).toString(),light.class);
                        ls[k]=l;
                        //Log.d("changdu",String.valueOf(ls.length));
                        fieldsDetailsInfo.setLight(ls);
                    }

                    //Log.d("ceshi",String.valueOf(fieldsDetailsInfo.getLight()[1].getLux()));
                }

                if (i == 1) {
                    JSONArray sensor = sensors.getJSONObject(i).getJSONArray(name[i]);
                    co2 [] co2s = new co2[sensor.size()];
                    for (int k = 0; k < sensor.size(); k++) {
                        co2 c = JSON.parseObject(sensor.getJSONObject(k).toString(),co2.class);

                        co2s[k] = c;
                        fieldsDetailsInfo.setCo2(co2s);
                    }
                }

                if (i == 2) {
                    JSONArray sensor = sensors.getJSONObject(i).getJSONArray(name[i]);
                    water[] waters = new water[sensor.size()];
                    for (int k = 0; k < sensor.size(); k++) {
                        water w = JSON.parseObject(sensor.getJSONObject(k).toString(),water.class);

                        waters[k] = w;
                        fieldsDetailsInfo.setWater(waters);
                    }
                }

                if (i == 3) {
                    JSONArray sensor = sensors.getJSONObject(i).getJSONArray(name[i]);
                    salt[] salts = new salt[sensor.size()];
                    for (int k = 0; k < sensor.size(); k++) {
                        salt s = JSON.parseObject(sensor.getJSONObject(k).toString(),salt.class);

                        salts[k] = s;
                        fieldsDetailsInfo.setSalt(salts);
                    }
                }
            }
        }




    }

    private void getControls(JSONObject jsonObject){
        String[] name ={"blower","lamp","web","nmembrane","tmembrane","pump"};
        JSONArray controls =jsonObject.getJSONArray("controls");
        if (controls != null) {
            for (int i = 0; i < controls.size(); i++) {
                if (i == 0) {
                    JSONArray control = controls.getJSONObject(i).getJSONArray(name[i]);
                    blower[] blowers = new blower[control.size()];
                    for (int k = 0; k < control.size(); k++) {
                        blower b = JSON.parseObject(control.getJSONObject(k).toString(),blower.class);

                        blowers[k] = b;
                        fieldsDetailsInfo.setBlower(blowers);
                    }


                }

                if (i == 1) {
                    JSONArray control = controls.getJSONObject(i).getJSONArray(name[i]);
                    lamp[] lamps = new lamp[control.size()];

                    for (int k = 0; k < control.size(); k++) {
                        lamp l = JSON.parseObject(control.getJSONObject(k).toString(),lamp.class);

                        lamps[k] = l;
                        fieldsDetailsInfo.setLamp(lamps);
                    }

                }

                if (i == 2) {
                    JSONArray control = controls.getJSONObject(i).getJSONArray(name[i]);
                    web[] webs = new web[control.size()];
                    for (int k = 0; k < control.size(); k++) {
                        web w = JSON.parseObject(control.getJSONObject(k).toString(),web.class);

                        webs[k] = w;
                        fieldsDetailsInfo.setWeb(webs);
                    }

                }

                if (i == 3) {
                    JSONArray control = controls.getJSONObject(i).getJSONArray(name[i]);
                    nmembrane[] nmembranes = new nmembrane[control.size()];
                    for (int k = 0; k < control.size(); k++) {
                        nmembrane n = JSON.parseObject(control.getJSONObject(k).toString(),nmembrane.class);

                        nmembranes[k] = n;
                        fieldsDetailsInfo.setNmembrane(nmembranes);
                    }

                }

                if (i == 4) {
                    JSONArray control = controls.getJSONObject(i).getJSONArray(name[i]);
                    tmembrane[] tmembranes = new tmembrane[control.size()];
                    for (int k = 0; k < control.size(); k++) {
                        tmembrane t = JSON.parseObject(control.getJSONObject(k).toString(),tmembrane.class);

                        tmembranes[k] = t;
                        fieldsDetailsInfo.setTmembrane(tmembranes);
                    }

                }

                if (i == 5) {
                    JSONArray control = controls.getJSONObject(i).getJSONArray(name[i]);
                    pump[] pumps = new pump[control.size()];
                    for (int k = 0; k < control.size(); k++) {
                        pump p = JSON.parseObject(control.getJSONObject(k).toString(),pump.class);

                        pumps[k] = p;
                        fieldsDetailsInfo.setPump(pumps);
                    }

                }

            }
        }
    }

    private  void getFieldId(JSONObject jsonObject){
        fieldsDetailsInfo.setId(jsonObject.getInteger("id"));
    }

    private void sendMessages(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message updateUIMessage= new Message();
                updateUIMessage.what = 1;
                updateUIMessage.obj = fieldsDetailsInfo;
                updateUIHandler.sendMessage(updateUIMessage);
            }
        }).start();

    }


}
