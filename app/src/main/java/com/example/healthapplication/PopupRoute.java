package com.example.healthapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class PopupRoute extends Fragment {

    View mView;

    GalleryAdapterRoutes2 galleryAdapter;
    RecyclerView recyclerView;
    ArrayList<RouteModel> routeList;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView=inflater.inflate(R.layout.popup_route, container, false);

        routeList = new ArrayList<RouteModel>();
        galleryAdapter = new GalleryAdapterRoutes2(routeList);
        recyclerView = mView.findViewById(R.id.recycler_view);

        getRoutes();

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(galleryAdapter);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {

            }

            @Override
            public void onLongClick(View view, int position) {
                String imgName = routeList.get(position).getImgName();
                getLatLng(imgName);
                Walk.flag=true;

                Fragment fragment=new Walk();
                FragmentManager fragmentManager= getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_container1, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        }));

        return mView;

    }

    void getRoutes(){
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
                        String imgPath= "http://ubuntu@ec2-52-78-174-144.ap-northeast-2.compute.amazonaws.com/"+imgName;   //이미지는 상대경로라서 앞에 서버 주소를 써야한다.
                        routeList.add(new RouteModel(imgPath, imgName));

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

    void getLatLng(String imgName){
        new Thread(){
            @Override
            public void run() {
                String serverUri="http://ec2-52-78-174-144.ap-northeast-2.compute.amazonaws.com/getLatLng.php";
                String parameters = "imgPath="+imgName;
                try {
                    URL url= new URL(serverUri);
                    HttpURLConnection connection= (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setDoInput(true);
                    connection.setUseCaches(false);

                    OutputStream outputStream = connection.getOutputStream();
                    outputStream.write(parameters.getBytes("UTF-8"));
                    outputStream.flush();
                    outputStream.close();

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

                    // MapsFragment의 places에 위도와 경도값을 입력함

                    MapsFragment.places = new ArrayList<String>();

                    int i=0;
                    for(String row : rows){
                        //한줄 데이터에서 한 칸씩 분리
                        String[] datas=row.split("&");
                        if(datas.length!=2) continue;
                        MapsFragment.places.add(datas[0]); // latitude
                        MapsFragment.places.add(datas[1]); // longitude
                        Log.d("latlng","zzzzzzzz"+MapsFragment.places.get(i++));
                        Log.d("latlng","zzzzzzzz"+MapsFragment.places.get(i++));

                    }

                } catch (MalformedURLException e) { e.printStackTrace(); } catch (IOException e) {e.printStackTrace();}
            }
        }.start();
    }


}