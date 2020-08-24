package com.example.healthapplication;

import android.Manifest;
import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.SimpleMultiPartRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.GoogleMap.SnapshotReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.view.View.VISIBLE;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Walk#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Walk extends Fragment {

    View mView;
    public static boolean flag=false;
    Button stop_walk;

    /*
    서버에 보낼 데이터: 사용자 아이디
     */
    String id = UserId.getId();

    public Walk() {
        // Required empty public constructor
    }
    public static Walk newInstance(){ return new Walk();}

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Walk.
     */
    // TODO: Rename and change types and number of parameters
    public static Walk newInstance(String param1, String param2) {
        Walk fragment = new Walk();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
      mView= inflater.inflate(R.layout.fragment_walk, container, false);

        FrameLayout f1=(FrameLayout)mView.findViewById(R.id.frame_container1);
        FrameLayout f2=(FrameLayout)mView.findViewById(R.id.route);
        LinearLayout.LayoutParams oldParams=new LinearLayout.LayoutParams(1100,1470);
        LinearLayout.LayoutParams oldParams1=new LinearLayout.LayoutParams(1100,400);
        stop_walk = (Button)mView.findViewById(R.id.stop_walk);

        // 시작: 루트 팝업창에서 루트를 선택하여 이 화면으로 이동한 경우
      if(flag){
          stop_walk.setVisibility(VISIBLE);
          Fragment fragment=new MapsFragment();
          FragmentManager fragmentManager= getActivity().getSupportFragmentManager();
          FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
          fragmentTransaction.replace(R.id.frame_container1, fragment);
          fragmentTransaction.addToBackStack(null);
          fragmentTransaction.commit();
      }


      // 캡쳐할 영역
      final LinearLayout capture = (LinearLayout)mView.findViewById(R.id.capture);

        f1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Fragment fragment=new PopupRoute();
                FragmentManager fragmentManager= getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_container1, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

                return false;
            }
        });

        stop_walk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1000);

                // 찍은 점들 보여주는 Log
                System.out.println("POINTSSSS !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                for(LatLng a : MapsFragment.click){
                    System.out.println(a.latitude+" "+a.longitude);
                }

                mView.setBackgroundColor(0xFFFFFF);
                stop_walk.setVisibility(mView.GONE);
                f1.setLayoutParams(oldParams);
                f2.setLayoutParams(oldParams1);
                Fragment fragment = new RouteUIFrag();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.route, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

                //  캡쳐 & 디바이스에 저장 끝 -> 서버에 업로드
                try {
                    CaptureMapScreen();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

      return mView;
    }

    public void CaptureMapScreen(){

        final Bitmap[] bitmap = new Bitmap[1];
        String path = Environment.getExternalStorageDirectory().getPath() + "/Pictures/WalkHistory"; // 내장메모리 루트 경로: /data
        File file = new File(path);

        // 처음 생성한 path 경로가 존재하지 않는 경우 경로를 새로 생성함
        if(!file.exists()){
            file.mkdirs();
            Toast.makeText(getActivity(), "캡쳐 폴더가 생성되었습니다.", Toast.LENGTH_SHORT).show();
        }

        SimpleDateFormat day = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date();
        WalkInfo.setImgPath(path+"/Walk"+day.format(date)+".jpeg");

        SnapshotReadyCallback callback = new SnapshotReadyCallback() {
            FileOutputStream fos =null;
            @Override
            public void onSnapshotReady(Bitmap snapshot) {
                // TODO Auto-generated method stub
                bitmap[0] = snapshot;
                try {
                    fos = new FileOutputStream(WalkInfo.getImgPath());
                    bitmap[0].compress(Bitmap.CompressFormat.JPEG, 90, fos);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    insertWalkHistory();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        };
        MapsFragment.mMap.snapshot(callback);
    }

    public void insertWalkHistory() throws InterruptedException {
        String serverUrl="http://ec2-52-78-174-144.ap-northeast-2.compute.amazonaws.com/insertWalkHistory.php";
        //파일 전송 요청 객체 생성
        SimpleMultiPartRequest smpr= new SimpleMultiPartRequest(Request.Method.POST, serverUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                new AlertDialog.Builder(getActivity()).setMessage("응답:"+response).create().show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "ERROR", Toast.LENGTH_SHORT).show();
            }
        });
        //요청 객체에 보낼 데이터를 추가
        smpr.addStringParam("id", id);
        smpr.addFile("img", WalkInfo.getImgPath());
        smpr.addStringParam("km", WalkInfo.getKm());
        smpr.addStringParam("cal", WalkInfo.getCal());
        smpr.addStringParam("time", WalkInfo.getTime());
        smpr.addStringParam("km_h", WalkInfo.getKm_h());

        //요청객체를 서버로 보낼 객체 생성
        RequestQueue requestQueue= Volley.newRequestQueue(getActivity());
        requestQueue.add(smpr);
    }
}