package com.example.healthapplication;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */


public class RouteUIFrag extends Fragment {

    View mView;
    Context mContext;

    public RouteUIFrag() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView=inflater.inflate(R.layout.uifrag, container, false);
        mContext=getContext();

        TextView km=(TextView)mView.findViewById(R.id.textView);
        TextView time=(TextView)mView.findViewById(R.id.textView2);
        TextView cal=(TextView)mView.findViewById(R.id.textView3);
        TextView km_h=(TextView)mView.findViewById(R.id.textView4);


        if(MapsFragment.end_time==null){
            km.setText("0km");
            time.setText("0:0");
            cal.setText("0");
            km_h.setText("0");
        }
        else {
            double dis=MapsFragment.distance;
            WalkInfo.setKm(String.format("%.2f",dis));
            WalkInfo.setCal( String.format("%.2f",(dis * 50.0))+ "");
            WalkInfo.setKm_h(String.format("%.2f",(dis / time_h(MapsFragment.start_time, MapsFragment.end_time) ))+ "");
            WalkInfo.setTime(cal_time(MapsFragment.start_time, MapsFragment.end_time));

            km.setText(WalkInfo.getKm());
            time.setText(WalkInfo.getTime());
            cal.setText(WalkInfo.getCal());
            km_h.setText(WalkInfo.getKm_h());
        }

        return mView;
    }

    public String cal_time(int start_time[], int end_time[]){
        /* h m s*/
        // change to s
        int start_second= start_time[0]*3600+ start_time[1]*60+ start_time[2];
        int end_second= end_time[0]*3600+ end_time[1]*60+ end_time[2];

        int total=end_second-start_second;
        System.out.println("@@@@@@@@@@@@@222"+total);
        String time= total/60+":" + total%60;

        return time;

    }
    public double time_h(int start_time[], int end_time[]){
        /* h m s*/
        // change to s
        int start_second= start_time[0]*3600+ start_time[1]*60+ start_time[2];
        int end_second= end_time[0]*3600+ end_time[1]*60+ end_time[2];

        int total=end_second-start_second;

        double distance= total/3600.0;

        return distance;

    }



}
