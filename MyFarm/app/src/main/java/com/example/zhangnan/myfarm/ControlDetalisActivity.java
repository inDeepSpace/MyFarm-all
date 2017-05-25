package com.example.zhangnan.myfarm;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.example.zhangnan.myfarm.activity_information.Controller;

/**
 * Created by zhangnan on 17/5/3.
 */

public class ControlDetalisActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private String TAG="CDA";
    private static final String EXTRA_POSITION="com.example.zhangnan.myfarm.position";
    private int namePosition;
    private TextView controllerTitleTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control_details);
        getWindow().setStatusBarColor(getResources().getColor(R.color.app_green));
        namePosition=getIntent().getIntExtra(EXTRA_POSITION,0);
        controllerTitleTextView= (TextView) findViewById(R.id.control_details_title_text_view);
        controllerTitleTextView.setText(new ControlActivity().name[namePosition].toString());
        new getControllerInfoTask().execute();
        recyclerView = (RecyclerView) findViewById(R.id.control_details_recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(this,1));

    }

    private class SoundHolder extends RecyclerView.ViewHolder{
        private TextView idTextView;
        private Switch aSwitch;
        private SeekBar aSeekBar;

        public SoundHolder(View view) {
            super(view);
            switch (namePosition){
                case 0:case 1:case 2:case 3:{
                    idTextView= (TextView) view.findViewById(R.id.control_list_item_id_text_view);
                    aSwitch= (Switch) view.findViewById(R.id.control_list_item_switch);
                }break;
                default:{
                    idTextView= (TextView) view.findViewById(R.id.control_details_list_item2_id_text_view);
                    aSeekBar= (SeekBar) view.findViewById(R.id.control_details_list_item2_seek_bar);
                }break;
            }

        }

        public void bindHolder(Controller controller,int position){
            switch (namePosition){
                case 0:{
                    idTextView.setText(controller.getWater_pumpControllers().get(position).getId());
                    aSwitch.setChecked(controller.getWater_pumpControllers().get(position).getState());
                }break;
                case 1:{
                    idTextView.setText(controller.getDraught_fensController().get(position).getId());
                    aSwitch.setChecked(controller.getDraught_fensController().get(position).getState());
                }break;
                case 2:{
                    idTextView.setText(controller.getLightControllers().get(position).getId());
                    aSwitch.setChecked(controller.getLightControllers().get(position).getState());
                }break;
                case 3:{
                    idTextView.setText(controller.getWaningControllers().get(position).getId());
                    aSwitch.setChecked(controller.getWaningControllers().get(position).getState());
                }break;
                case 4:{
                    idTextView.setText(controller.getFilm_sideControllers().get(position).getId());
                    aSeekBar.setProgress(controller.getFilm_sideControllers().get(position).getState());
                }break;
                case 5:{
                    idTextView.setText(controller.getFilm_topControllers().get(position).getId());
                    aSeekBar.setProgress(controller.getFilm_topControllers().get(position).getState());
                }break;

            }
        }

    }


    private class SoundAdapter extends RecyclerView.Adapter<SoundHolder>{
            Controller controller;

        public SoundAdapter(Controller controller) {
            this.controller = controller;
        }

        @Override
        public SoundHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater= LayoutInflater.from(ControlDetalisActivity.this);
            switch (namePosition){
                case 0:case 1:case 2:case 3:{
                   return new SoundHolder(inflater.inflate(R.layout.control_details_list_item,parent,false));
                }
                default:return new SoundHolder(inflater.inflate(R.layout.control_details_list_item2,parent,false));
            }


        }

        @Override
        public void onBindViewHolder(final SoundHolder soundHodler, int position) {
            soundHodler.bindHolder(controller,position);
            if (soundHodler.aSwitch.isChecked()){
                soundHodler.aSwitch.setText("开");
            }else{
                soundHodler.aSwitch.setText("关");
            }
            soundHodler.aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked){
                        soundHodler.aSwitch.setText("开");
                    }else {
                        soundHodler.aSwitch.setText("关");
                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            switch (namePosition){
                case 0:return controller.getWater_pumpControllers().size();
                case 1:return controller.getDraught_fensController().size();
                case 2:return controller.getLightControllers().size();
                case 3:return controller.getWaningControllers().size();
                case 4:return controller.getFilm_sideControllers().size();
                case 5:return controller.getFilm_topControllers().size();
                default:return controller.getWater_pumpControllers().size();
            }
        }

    }

    public static Intent newIntent(Context packageContext,int position){
        Intent i = new Intent(packageContext,ControlDetalisActivity.class);
        i.putExtra(EXTRA_POSITION,position);
        return i;
    }

    public class getControllerInfoTask extends AsyncTask<Void,Void,Controller>{


        @Override
        protected Controller doInBackground(Void... params) {
            return new VisitServer().getControllers();
        }

        @Override
        protected void onPostExecute(Controller controller) {
            recyclerView.setAdapter(new SoundAdapter(controller));

        }
    }

}
