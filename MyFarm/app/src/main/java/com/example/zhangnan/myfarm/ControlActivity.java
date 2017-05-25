package com.example.zhangnan.myfarm;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by zhangnan on 17/4/25.
 */

public class ControlActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    public String[] name = {"水泵","环流风机","照明灯","遮阳网","侧卷膜","顶卷膜"};
    private SoundAdapter soundAdapter;
    private String TAG="ControlActivity";
    private Intent i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);
        getWindow().setStatusBarColor(getResources().getColor(R.color.app_green));

        recyclerView = (RecyclerView) findViewById(R.id.fragment_control_recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(this,1));
        recyclerView.setAdapter(soundAdapter = new SoundAdapter());

    }


    private class SoundHodler extends RecyclerView.ViewHolder{

        private TextView controlTextViewName;
        private LinearLayout controlLinearLayout;
        private String controllerName;


        public SoundHodler(View view) {
            super(view);
            controlTextViewName = (TextView)itemView.findViewById(R.id.control_list_item_textview_name);

            controlLinearLayout = (LinearLayout)itemView.findViewById(R.id.control_list_item);

            controlLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    i = ControlDetalisActivity.newIntent(ControlActivity.this,recyclerView.getLayoutManager().getPosition(view));
                    Log.d(TAG, "onClick: "+recyclerView.getLayoutManager().getPosition(view));
                    startActivity(i);
                }
            });

        }

    }


    private class SoundAdapter extends RecyclerView.Adapter<SoundHodler>{


        @Override
        public SoundHodler onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater= LayoutInflater.from(ControlActivity.this);
            View view=inflater.inflate(R.layout.control_list_item,parent,false);
            return new SoundHodler(view);
        }

        @Override
        public void onBindViewHolder(SoundHodler soundHodler, int position) {
            soundHodler.controlTextViewName.setText(name[position]);
        }

        @Override
        public int getItemCount() {
            return name.length;
        }

    }


    public static Intent newIntent(Context packageContext){
        Intent i = new Intent(packageContext,ControlActivity.class);
        return i;
    }



}
