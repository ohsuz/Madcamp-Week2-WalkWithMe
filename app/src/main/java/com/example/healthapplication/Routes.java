package com.example.healthapplication;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.example.healthapplication.RouteModel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */

public class Routes extends Fragment implements View.OnClickListener,GalleryItemClickListenerR{

    GalleryAdapterRoutes galleryAdapter;
    RecyclerView recyclerView;
    ArrayList<RouteModel> routeList;

    public static final String TAG = RecyclerViewFragment.class.getSimpleName();
    View mView;
    Context mContext;
    Animation fab_open, fab_close;
    FloatingActionButton fab, fab2, fab3;
    Boolean openFlag=false;
    boolean anistart=false;

    public Routes() {
        // Required empty public constructor
    }
    public static Routes newInstance(){
        return new Routes();
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void anim(){
        if (openFlag) {
            getRoutes(0);
            fab2.startAnimation(fab_close);
            fab3.startAnimation(fab_close);
            fab2.setClickable(false);

            fab3.setClickable(false);
            openFlag = false;
        } else {
            fab2.startAnimation(fab_open);
            fab3.startAnimation(fab_open);
            fab2.setClickable(true);
            fab3.setClickable(true);
            openFlag = true;
        }
    }

    @Override
    public void onClick(View v){
        int id = v.getId();
        switch (id) {
            case R.id.fab:
                anim();
                break;
            case R.id.fab2:
                getRoutes(1);
                break;
            case R.id.fab3:
                getRoutes(2);
                break;
        }
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(galleryAdapter);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_routes, container, false);
        mContext = getContext();

        routeList = new ArrayList<RouteModel>();
        galleryAdapter = new GalleryAdapterRoutes(routeList,this);
        recyclerView = mView.findViewById(R.id.recycler_view);

        getRoutes(0);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(galleryAdapter);

        fab_open = AnimationUtils.loadAnimation(mContext, R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(mContext, R.anim.fab_close);
        fab = mView.findViewById(R.id.fab);
        fab2 = mView.findViewById(R.id.fab2);
        fab3 = mView.findViewById(R.id.fab3);

        fab2.setImageResource(R.drawable.ic_baseline_trending_flat_100);
        fab3.setImageResource(R.drawable.ic_baseline_trending_up_100);

        //버튼 상태 초기화
        fab2.startAnimation(fab_close);
        fab3.startAnimation(fab_close);
        fab2.setClickable(false);
        fab3.setClickable(false);

        fab.setOnClickListener(this);
        fab2.setOnClickListener(this);
        fab3.setOnClickListener(this);
        return mView;
    }


    void getRoutes(int id){
        new Thread(){
            @Override
            public void run() {
                String serverUri="http://ec2-52-78-174-144.ap-northeast-2.compute.amazonaws.com/getRoutes.php";
                try {
                    URL url= new URL(serverUri);
                    HttpURLConnection connection= (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setDoInput(true);
                    connection.setUseCaches(false);

                    InputStream is=connection.getInputStream();
                    InputStreamReader isr= new InputStreamReader(is);
                    BufferedReader reader= new BufferedReader(isr);

                    final StringBuffer buffer= new StringBuffer();
                    String line= reader.readLine();

                    while (line!=null){
                        buffer.append(line+"\n");
                        line= reader.readLine();
                    }

                    //읽어온 문자열에서 row(레코드)별로 분리하여 배열로 리턴하기
                    String[] rows=buffer.toString().split(";");
                    // 데이터 초기화
                    routeList.clear();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            galleryAdapter.notifyDataSetChanged();
                        }
                    });
                    for(String row : rows){
                        //한줄 데이터에서 한 칸씩 분리
                        String[] datas=row.split("&");
                        if(datas.length!=1) continue;
                        String imgName = datas[0];
                        String imgPath= "http://ubuntu@ec2-52-78-174-144.ap-northeast-2.compute.amazonaws.com/"+imgName;

                        // 두 번째 동그라미
                        if(id == 1){
                            // 짧은 산책(20분 미만) 경로 출력
                            if(datas[0].contains("easy")){
                                routeList.add(new RouteModel(imgPath, imgName));
                            }
                        }
                        // 긴 산책 경로(20분 이상) 출력
                        else if(id == 2){
                            if(datas[0].contains("hard")){
                                routeList.add(new RouteModel(imgPath, imgName));
                            }
                        }
                        // 아무 동그라미도 선택하지 않았을 때 -> 모든 사진 출력
                        else{
                            routeList.add(new RouteModel(imgPath, imgName));
                        }

                        //리사이클러뷰 갱신
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                galleryAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                } catch (MalformedURLException e) { e.printStackTrace(); } catch (IOException e) {e.printStackTrace();}
            }
        }.start();
    }

    @Override
    public void onGalleryItemClickListenerR(int position, RouteModel routeModel, ImageView imageView) {
        GalleryViewPagerFragmentR galleryViewPagerFragment = GalleryViewPagerFragmentR.newInstance(position, routeList);

        getFragmentManager()
                .beginTransaction()
                .addSharedElement(imageView, ViewCompat.getTransitionName(imageView))
                .addToBackStack(TAG)
                .replace(android.R.id.content, galleryViewPagerFragment)
                .commit();
    }
}