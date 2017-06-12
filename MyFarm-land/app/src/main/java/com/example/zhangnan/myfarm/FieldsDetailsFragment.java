package com.example.zhangnan.myfarm;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import com.example.zhangnan.myfarm.ChartUtils.ChartUtils;
import com.example.zhangnan.myfarm.OneSelfView.FlushView;
import com.example.zhangnan.myfarm.activity_information.FieldsDetailsInfo;
import com.example.zhangnan.myfarm.activity_information.co2;
import com.example.zhangnan.myfarm.activity_information.light;
import com.example.zhangnan.myfarm.activity_information.salt;
import com.example.zhangnan.myfarm.activity_information.water;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.melnykov.fab.FloatingActionButton;
import com.melnykov.fab.ObservableScrollView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangnan on 17/4/28.
 */

public class FieldsDetailsFragment extends Fragment {


    private ViewPager viewPager_banner;
    private ImageView[] mImageViews;
    private int[] imgIdArray;
    private LineChart lineChart;
    private RecyclerView fieldsDetailsRecyclerView;
    private String[] name ={"light","co2","water","salt"};
    private SeekBar tmembraneSeekBar;
    private SeekBar nmembraneSeekBar;
    private Switch lightSwitch;
    private Switch lampSwitch;
    private String fieldsName;
    private FlushView mUCFlushView;

    private MqttMessages mQttMessages;
    private RecyclerView.Adapter fieldsDetailsAdapter;

    private FieldsDetailsInfo mFieldsDetailsInfo;
    private int count;
    public Map<Integer, String> fieldsDetailsSensorsInfoMap = new HashMap();
    private String[] sensorsName;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //订阅当前田地详情主题
        mQttMessages = new MqttMessages("fields"+String.valueOf(FieldsFragment.clickItemPosition));
        Log.d("topic","fields"+String.valueOf(FieldsFragment.clickItemPosition));

        MqttMessages.updateUIHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 1){
                    mFieldsDetailsInfo = (FieldsDetailsInfo) msg.obj;
                    count = mFieldsDetailsInfo.getSensorsCount();
                    updateData();
                    updateUI();
                    Log.d("count", String.valueOf(count));
                }
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_fields_details, container, false);

        mUCFlushView = (FlushView) view.findViewById(R.id.landing_view);

        imgIdArray = new int[]{R.drawable.img1, R.drawable.img2, R.drawable.img3,R.drawable.img4,R.drawable.img5};
        mImageViews = new ImageView[imgIdArray.length];
        for(int i=0; i<mImageViews.length; i++){
            ImageView imageView = new ImageView(view.getContext());
            imageView.setBackgroundResource(imgIdArray[i]);
            mImageViews[i] = imageView;

        }

        System.out.println("***********************"+count);

        fieldsDetailsRecyclerView = (RecyclerView)view.findViewById(R.id.fields_details_recycler_view);
        fieldsDetailsRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2));
        fieldsDetailsRecyclerView.addItemDecoration(new MyFieldsDetailsItemDecoration(2));
        fieldsDetailsRecyclerView.setAdapter(fieldsDetailsAdapter = new FieldsDetailsAdapter());



        viewPager_banner = (ViewPager) view.findViewById(R.id.fields_list_item_view_pager_banner);
        viewPager_banner.setAdapter(new BannerViewPagerAdapter());


        lineChart = (LineChart)view.findViewById(R.id.line_chart);
        ChartUtils.initChart(lineChart);
        ChartUtils.notifyDataSetChanged(lineChart, getData(), ChartUtils.dayValue);

        initFloatingActionButton(view);

        initControls(view);
        changeSwitchText();
        return view;
    }

    private  class FieldsDetailsHodler extends RecyclerView.ViewHolder {

        public  TextView sensorsNameTextView;
        public  TextView sensorsDetailsTextView;

        public FieldsDetailsHodler(LayoutInflater inflater, ViewGroup container) {
            super(inflater.inflate(R.layout.fields_details_list_item,container,false));
            sensorsNameTextView = (TextView)itemView.findViewById(R.id.sensors_name);
            sensorsDetailsTextView = (TextView)itemView.findViewById(R.id.sensors_details);
        }
    }

    private class FieldsDetailsAdapter extends RecyclerView.Adapter<FieldsDetailsHodler>{


        @Override
        public FieldsDetailsHodler onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater= LayoutInflater.from(getActivity());
            return new FieldsDetailsHodler(inflater,parent);
        }

        @Override
        public void onBindViewHolder(FieldsDetailsHodler fieldsDetailsHodler, int position) {
            fieldsDetailsHodler.sensorsNameTextView.setText(sensorsName[position]);
            fieldsDetailsHodler.sensorsDetailsTextView.setTextSize(15);
            fieldsDetailsHodler.sensorsDetailsTextView.setTextColor(Color.parseColor("#000000"));
            fieldsDetailsHodler.sensorsDetailsTextView.setText(fieldsDetailsSensorsInfoMap.get(position));
        }

        @Override
        public int getItemCount() {
            return count;
        }
    }

    private class BannerViewPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return imgIdArray.length;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(View container, int position, Object object) {
            ((ViewPager)container).removeView(mImageViews[position % mImageViews.length]);
        }

        @Override
        public Object instantiateItem(View container, int position) {
            ((ViewPager)container).addView(mImageViews[position % mImageViews.length], 0);
            return mImageViews[position % mImageViews.length];
        }
    }


    public void initFloatingActionButton(View view){
        ObservableScrollView scrollView = (ObservableScrollView) view.findViewById(R.id.fab_scroll_view);
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.floating_action_button);
        fab.attachToScrollView(scrollView);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i  = MonitorActivity.newIntent(getActivity());
                startActivity(i);
            }
        });
    }

    private List<Entry> getData() {
        List<Entry> values = new ArrayList<>();
        values.add(new Entry(0, 13));
        values.add(new Entry(1, 14));
        values.add(new Entry(2, 15));
        values.add(new Entry(3, 30));
        values.add(new Entry(4, 25));
        values.add(new Entry(5, 1));
        values.add(new Entry(6, 20));
        return values;
    }

    public class MyFieldsDetailsItemDecoration extends RecyclerView.ItemDecoration{
        int mSpace;

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            if (parent.getChildAdapterPosition(view)%2 == 0) {
                outRect.right = mSpace;
                outRect.bottom = mSpace;
            } else{
                outRect.left = mSpace;
                outRect.bottom = mSpace;
            }

        }

        public MyFieldsDetailsItemDecoration(int space) {
            this.mSpace = space;
        }

    }

    public void initControls(View view){
        int k = 0;
        lampSwitch = (Switch) view.findViewById(R.id.fields_details_fan_switch);
        lightSwitch = (Switch) view.findViewById(R.id.fields_details_light_switch);
        tmembraneSeekBar = (SeekBar) view.findViewById(R.id.tmembrane_seek_bar);
        nmembraneSeekBar = (SeekBar) view.findViewById(R.id.nmembrane_seek_bar);
//        if (!MqttMessages.fieldsDetailsControlsInfoMap.isEmpty()){
//                if ((float)MqttMessages.fieldsDetailsControlsInfoMap.get(k++) == 1.0f){
//                    lampSwitch.setChecked(true);
//                }else {
//                    lampSwitch.setChecked(false);
//                }
//
//                if ((float)MqttMessages.fieldsDetailsControlsInfoMap.get(k++) == 1.0f){
//                    lightSwitch.setChecked(true);
//                }else lampSwitch.setChecked(false);
//
//
//                tmembraneSeekBar.setProgress((int) (fieldsDetailsControlsInfoMap.get(k++) * 100));
//                nmembraneSeekBar.setProgress((int) (fieldsDetailsControlsInfoMap.get(k++) * 100));
//            }
    }

    public void changeSwitchText(){
        lightSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    lightSwitch.setText("开");
                }else {
                    lightSwitch.setText("关");
                }
            }
        });

        lampSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    lampSwitch.setText("开");
                }else {
                    lampSwitch.setText("关");
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mQttMessages.close();
    }

    @Override
    public void onPause() {
        super.onPause();
        mQttMessages.close();
    }

    @Override
    public void onStop() {
        super.onStop();
        mQttMessages.close();
    }

    private void updateData(){
        fieldsDetailsSensorsInfoToString();
        fieldsDetailsAdapter.notifyDataSetChanged();
    }

    private void updateUI(){
        if (count != 0){
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(0,0);
            mUCFlushView.setLayoutParams(param);
        }else {
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
            mUCFlushView.setLayoutParams(param);
        }
    }

    private void fieldsDetailsSensorsInfoToString(){
        if (mFieldsDetailsInfo != null){
            int k = 0;
            sensorsName = new String[count];

            if (mFieldsDetailsInfo.getLight() != null) {
                for (int i = 0; i < mFieldsDetailsInfo.getLight().length; i++) {
                    light[] l = mFieldsDetailsInfo.getLight();
                    sensorsName[k] = "光照温湿度变送器" + String.valueOf(l[i].getId());
                    fieldsDetailsSensorsInfoMap.put(k++, "温度：" + String.valueOf(l[i].getC()) + '\n' +
                            "酸碱度：" + String.valueOf(l[i].getPh()) + '\n' +
                            "光照强度：" + String.valueOf(l[i].getLux()));

                }
            }

            if (mFieldsDetailsInfo.getCo2() != null) {
                for (int i = 0; i < mFieldsDetailsInfo.getCo2().length; i++) {
                    co2[] c = mFieldsDetailsInfo.getCo2();
                    sensorsName[k] = "二氧化碳温湿度变送器" + String.valueOf(c[i].getId());
                    fieldsDetailsSensorsInfoMap.put(k++, "温度：" + String.valueOf(c[i].getC()) + '\n' +
                            "酸碱度：" + String.valueOf(c[i].getPh()) + '\n' +
                            "二氧化碳浓度：" + String.valueOf(c[i].getCo2()));
                }
            }

            if (mFieldsDetailsInfo.getWater() != null){
                for (int i = 0;i < mFieldsDetailsInfo.getWater().length;i++){
                    water[] w = mFieldsDetailsInfo.getWater();
                    sensorsName[k] = "土壤水分传感器" + String.valueOf(w[i].getId());
                    fieldsDetailsSensorsInfoMap.put(k++,"温度："+String.valueOf(w[i].getC())+'\n'+
                            "湿度："+String.valueOf(w[i].getPe()));
                }
            }


            if (mFieldsDetailsInfo.getSalt() != null ){
                for (int i = 0;i < mFieldsDetailsInfo.getSalt().length;i++){
                    salt[] s = mFieldsDetailsInfo.getSalt();
                    sensorsName[k] = "土壤检测传感" + String.valueOf(s[i].getId());
                    fieldsDetailsSensorsInfoMap.put(k++,"电导率："+String.valueOf(s[i].getMg())+'\n'+
                            "盐分："+s[i].getUs());
                }
            }


        }

    }
}


