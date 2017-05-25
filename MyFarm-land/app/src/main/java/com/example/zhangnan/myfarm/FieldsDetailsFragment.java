package com.example.zhangnan.myfarm;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

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
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.melnykov.fab.FloatingActionButton;
import com.melnykov.fab.ObservableScrollView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.example.zhangnan.myfarm.FieldsFragment.clickItemPosition;

/**
 * Created by zhangnan on 17/4/28.
 */

public class FieldsDetailsFragment extends Fragment {


    private ViewPager viewPager_banner;
    private ImageView[] mImageViews;
    private int[] imgIdArray;
    private BarChart barChart;
    private RecyclerView fieldsDetailsRecyclerView;
    private String[] name ={"light","co2","water","salt"};
    private SeekBar tmembraneSeekBar;
    private SeekBar nmembraneSeekBar;
    private Switch lightSwitch;
    private Switch lampSwitch;
    private String fieldsName;

    private FieldsDetailsInfo fieldsDetailsInfo;
    private int fieldsDetailsInfoCount = 0;
    private int defaultCount = 4;
    public Map<Integer, String> fieldsDetailsSensorsInfoMap = new HashMap();
    public Map<Integer, Float> fieldsDetailsControlsInfoMap = new HashMap();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_fields_details, container, false);
        imgIdArray = new int[]{R.drawable.img1, R.drawable.img2, R.drawable.img3,R.drawable.img4,R.drawable.img5};
        mImageViews = new ImageView[imgIdArray.length];
        for(int i=0; i<mImageViews.length; i++){
            ImageView imageView = new ImageView(view.getContext());
            imageView.setBackgroundResource(imgIdArray[i]);
            mImageViews[i] = imageView;

        }

        fieldsDetailsRecyclerView = (RecyclerView)view.findViewById(R.id.fields_details_recycler_view);
        fieldsDetailsRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2));
        fieldsDetailsRecyclerView.addItemDecoration(new MyFieldsDetailsItemDecoration(5));
        fieldsDetailsRecyclerView.setAdapter(new FieldsDetailsAdapter());

        viewPager_banner = (ViewPager) view.findViewById(R.id.fields_list_item_view_pager_banner);
        viewPager_banner.setAdapter(new BannerViewPagerAdapter());

        drawChart(view);
        initFloatingActionButton(view);
        //getIntentMessage();
        fieldsDetailsSensorsInfoToString();
        fieldsDetailsControlInfoToString();
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
            fieldsDetailsHodler.sensorsNameTextView.setTextSize(15);
            fieldsDetailsHodler.sensorsNameTextView.setTextColor(Color.parseColor("#e0e0e0"));
            fieldsDetailsHodler.sensorsNameTextView.setText("正在加载...");

            //从fieldsDetailsSensorsInfoMap取出值给每一个item更新数据
            if (!fieldsDetailsControlsInfoMap.isEmpty()) {
                fieldsDetailsHodler.sensorsDetailsTextView.setText(fieldsDetailsSensorsInfoMap.get(position));
            }

        }

        @Override
        public int getItemCount() {
            if (fieldsDetailsInfoCount == 0){
                fieldsDetailsInfoCount = defaultCount;
            }
            return fieldsDetailsInfoCount;
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

    public void drawChart(View view){
        barChart = (BarChart)view.findViewById(R.id.bar_chart);

        List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(1,1));
        entries.add(new BarEntry(2,2));
        entries.add(new BarEntry(3,3));
        entries.add(new BarEntry(4,4));
        entries.add(new BarEntry(5,5));
        entries.add(new BarEntry(6,6));
        BarDataSet dataSet = new BarDataSet(entries,"");
       dataSet.setColors(ColorTemplate.COLORFUL_COLORS);

        BarData data = new BarData(dataSet);

        barChart.setDescription(null);
        barChart.setDrawValueAboveBar(false);
        barChart.setData(data);
    }

    public class MyFieldsDetailsItemDecoration extends RecyclerView.ItemDecoration{
        int mSpace;

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            if (parent.getChildAdapterPosition(view)%2 == 0) {
                outRect.right = mSpace;
                outRect.bottom = mSpace;
                outRect.top = mSpace;
            } else{
                outRect.left = mSpace;
                outRect.bottom = mSpace;
                outRect.top = mSpace;
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
        if (!fieldsDetailsControlsInfoMap.isEmpty()){
                if ((float)fieldsDetailsControlsInfoMap.get(k++) == 1.0f){
                    lampSwitch.setChecked(true);
                }else {
                    lampSwitch.setChecked(false);
                }

                if ((float)fieldsDetailsControlsInfoMap.get(k++) == 1.0f){
                    lightSwitch.setChecked(true);
                }else lampSwitch.setChecked(false);


                tmembraneSeekBar.setProgress((int) (fieldsDetailsControlsInfoMap.get(k++) * 100));
                nmembraneSeekBar.setProgress((int) (fieldsDetailsControlsInfoMap.get(k++) * 100));
            }
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



    //得到上一个页面intent传来的FieldsDetailsInfo
    private void getIntentMessage(){

            int position =clickItemPosition+1;

            System.out.println("*******************"+fieldsName);
            if (!MqttMessages.messageMap.isEmpty())
            {
                fieldsDetailsInfo =  MqttMessages.messageMap.get(position);
                fieldsDetailsInfoCount = fieldsDetailsInfo.getCount();
            }

    }

    //将FieldsDetailsInfo对象中的sensor元素的每个数组以String存放在fieldsDetailsSensorsInfoMap中
    private void fieldsDetailsSensorsInfoToString(){
        if (fieldsDetailsInfo != null){
            int k = 0;
            for (int i = 0;i < fieldsDetailsInfo.getLight().length;i++){
                light[] l = fieldsDetailsInfo.getLight();
                fieldsDetailsSensorsInfoMap.put(k++,String.valueOf(l[i].getC())+
                        String.valueOf(l[i].getLux())+
                        String.valueOf(l[i].getPh()));
            }

            for (int i = 0;i < fieldsDetailsInfo.getCo2().length;i++){
                co2[] c = fieldsDetailsInfo.getCo2();
                fieldsDetailsSensorsInfoMap.put(k++,String.valueOf(c[i].getC())+
                        String.valueOf(c[i].getCo2())+
                        String.valueOf(c[i].getPh()));
            }

            for (int i = 0;i < fieldsDetailsInfo.getWater().length;i++){
                water[] w = fieldsDetailsInfo.getWater();
                fieldsDetailsSensorsInfoMap.put(k++,String.valueOf(w[i].getC())+
                        String.valueOf(w[i].getPe()));
            }

            for (int i = 0;i < fieldsDetailsInfo.getSalt().length;i++){
                salt[] s = fieldsDetailsInfo.getSalt();
                fieldsDetailsSensorsInfoMap.put(k++,String.valueOf(s[i].getMg()+
                        s[i].getUs()));
            }

        }

    }

    //同上
    private void fieldsDetailsControlInfoToString(){

        if (fieldsDetailsInfo != null){
            int k = 0;

            for (int i = 0;i < fieldsDetailsInfo.getLamp().length;i++){
                lamp[] l = fieldsDetailsInfo.getLamp();
                fieldsDetailsControlsInfoMap.put(k++, (float) l[i].getValue());
            }

            for (int i = 0;i < fieldsDetailsInfo.getWeb().length;i++){
                web[] w = fieldsDetailsInfo.getWeb();
                fieldsDetailsControlsInfoMap.put(k++, (float) w[i].getValue());
            }

            for (int i = 0;i < fieldsDetailsInfo.getNmembrane().length;i++){
                nmembrane[] n = fieldsDetailsInfo.getNmembrane();
                fieldsDetailsControlsInfoMap.put(k++,n[i].getValue());
            }

            for (int i = 0;i < fieldsDetailsInfo.getTmembrane().length;i++){
                tmembrane[] t = fieldsDetailsInfo.getTmembrane();
                fieldsDetailsControlsInfoMap.put(k++,t[i].getValue());
            }
        }

    }

}


