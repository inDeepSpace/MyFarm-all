package com.example.zhangnan.myfarm;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.zhangnan.myfarm.activity_information.FieldsInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangnan on 17/4/22.
 */

public class FieldsFragment extends Fragment {

    private RecyclerView recyclerView;
    private int recyclerViewClickTag = 0;//RerecyclerView点击标志
    private Intent i;

    private EditText searchEditText;
    private ImageView searchCancel;
    private ImageView searchOk;

    private Map<String,String> mqttMessagesMap = new HashMap<>();
    private SoundAdapter soundAdapter;
    private List<FieldsInfo> fieldsInfos=new ArrayList<>();
    public static int clickItemPosition;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_fields, container, false);
        new GetFiledInfoTask().execute();
        recyclerView = (RecyclerView) view.findViewById(R.id.fields_recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2));
        recyclerView.addItemDecoration(new MyFarmActivity.MyFarmItemDecoration(2));
        recyclerView.setAdapter(soundAdapter = new SoundAdapter(fieldsInfos));
        return view;
    }


    class SoundHodler extends RecyclerView.ViewHolder implements View.OnClickListener{

        private LinearLayout fieldsLinearLayout;
        private TextView tempTextView;
        private TextView humidityTextView;
        private TextView phTextView;
        private TextView fieldIdTextView;
        private TextView fieldNameTextView;

        public SoundHodler(View view) {
            super(view);
            fieldsLinearLayout = (LinearLayout)view.findViewById(R.id.fields_list_item_linear_layout);
            fieldsLinearLayout.setOnClickListener(this);
            tempTextView= (TextView)view.findViewById(R.id.fields_list_item_temp_text_view);
            humidityTextView= (TextView)view.findViewById(R.id.fields_list_item_humidity_text_view);
            phTextView= (TextView)view.findViewById(R.id.fields_list_item_ph_text_view);
            fieldIdTextView=(TextView)view.findViewById(R.id.fields_list_item_id_text_view);
            fieldNameTextView= (TextView) view.findViewById(R.id.fields_list_item_name_text_view);
        }


        public void bindHolder(FieldsInfo field){
            tempTextView.setText(field.getTemp());
            humidityTextView.setText(field.getHumidity());
            phTextView.setText(field.getIllumination());
            fieldIdTextView.setText(field.getId()+"号田");
            fieldNameTextView.setText(field.getName());
        }

        @Override
        public void onClick(View v) {
            clickItemPosition = getPosition() + 1;
            replaceFragment(new FieldsDetailsFragment(),"FieldsDetailsFragment");
        }
    }



    class SoundAdapter extends RecyclerView.Adapter<SoundHodler>{
        private List<FieldsInfo> fields;

        public SoundAdapter(List<FieldsInfo> fields) {
            this.fields = fields;
        }

        @Override
        public SoundHodler onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater= LayoutInflater.from(getActivity());
            View v=inflater.inflate(R.layout.fields_list_item,parent,false);
            return new SoundHodler(v);
        }

        @Override
        public void onBindViewHolder(SoundHodler soundHodler, int position) {
            FieldsInfo fieldsInfo=fields.get(position);
            soundHodler.bindHolder(fieldsInfo);
        }

        @Override
        public int getItemCount() {
            return fields.size();
        }


    }



    private class GetFiledInfoTask extends AsyncTask<Void,Void,List<FieldsInfo>>{

        @Override
        protected List<FieldsInfo> doInBackground(Void... params) {
            return new VisitServer().getFields();
        }

        @Override
        protected void onPostExecute(List<FieldsInfo> fieldsInfos) {
            recyclerView.setAdapter(new SoundAdapter(fieldsInfos));

        }
    }

    public void replaceFragment(Fragment fragment, String tag) {
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment, tag);
        transaction.commit();
    }

}
