package com.example.zhangnan.myfarm;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.zhangnan.myfarm.activity_information.FieldsInfo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangnan on 17/4/22.
 */

public class FieldsActivity extends AppCompatActivity{

    private RecyclerView recyclerView;
    private int recyclerViewClickTag = 0;//RerecyclerView点击标志kjjkj
    private Intent i;

    private EditText searchEditText;
    private ImageView searchCancel;
    private ImageView searchOk;

    private Map<String,String> mqttMessagesMap = new HashMap<>();
    private SoundAdapter soundAdapter;
    private List<FieldsInfo> fieldsInfos=new ArrayList<>();
    public static int clickItemPosition;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fields);
        new GetFiledInfoTask().execute();
        getWindow().setStatusBarColor(getResources().getColor(R.color.app_green));

        recyclerView = (RecyclerView) findViewById(R.id.fields_recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(this,1));
        recyclerView.setAdapter(soundAdapter = new SoundAdapter(fieldsInfos));
        new Thread(new Runnable() {
            @Override
            public void run() {
                new VisitServer().postJson("{}");
            }
        }).start();
    }

    private void initSearch(){
        searchEditText = (EditText) findViewById(R.id.fields_search_edit_text);
    }

    private class SoundHodler extends RecyclerView.ViewHolder implements View.OnClickListener{

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
        public void onClick(View view) {
                clickItemPosition = getPosition() + 1;
                i = FieldsDetailsActivity.newIntent(FieldsActivity.this);
                Bundle bundle = new Bundle();
                bundle.putString("name", (String) fieldNameTextView.getText());
                bundle.putInt("position",getPosition()+1);
                i.putExtras(bundle);
                startActivity(i);
        }
    }



    private class SoundAdapter extends RecyclerView.Adapter<SoundHodler>{
        private List<FieldsInfo> fields;

        public SoundAdapter(List<FieldsInfo> fields) {
            this.fields = fields;
        }

        @Override
        public SoundHodler onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater= LayoutInflater.from(FieldsActivity.this);
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



    public static Intent newIntent(Context packageContext){
        Intent i = new Intent(packageContext,FieldsActivity.class);
        return i;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
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


}
