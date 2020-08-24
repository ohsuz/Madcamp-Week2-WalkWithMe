package com.example.healthapplication;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyPage#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyPage extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    TextView totalUse, totalKm, totalTime, totalCal, userId;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private static String FRIENDS =  "http://ec2-52-78-174-144.ap-northeast-2.compute.amazonaws.com/findFriendsTemp.php";
    String id = UserId.getId();
    RecyclerView mateView;
    ArrayList<Friends> mateList;
    FriendsAdapter fAdapter;
    ArrayList<String> friendsList = new ArrayList<String>();

    int sumUse ;
    double sumKm, sumCal ;
    String sumTime;

    public MyPage() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyPage.
     */
    // TODO: Rename and change types and number of parameters
    public static MyPage newInstance(String param1, String param2) {
        MyPage fragment = new MyPage();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.fragment_my_page, container, false);
        ImageView plus = (ImageView)layout.findViewById(R.id.plus);
        ImageView history=(ImageView)layout.findViewById(R.id.walk_history);
        ImageView leaderboard =(ImageView)layout.findViewById(R.id.leaderboard);
        userId = (TextView)layout.findViewById(R.id.userId);
        totalUse = (TextView)layout.findViewById(R.id.totalUse);
        totalKm = (TextView)layout.findViewById(R.id.totalKm);
        totalTime = (TextView)layout.findViewById(R.id.totalTime);
        totalCal = (TextView)layout.findViewById(R.id.totalCal);
        sumCal=0.0;
        sumKm=0.0;
        sumTime="0:0";
        sumUse=0;

        getWalkInfo();



        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_CONTACTS},1000);
        final ArrayList<String> phone = readContacts();
// 리사이클러뷰 설정
        mateView = layout.findViewById(R.id.mateView);
        mateView.setHasFixedSize(true);
        mateView.setLayoutManager(new LinearLayoutManager(getActivity()));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mateView.getContext(), layout.getOrientation()); // 아이템들 사이에 선 추가
        mateView.addItemDecoration(dividerItemDecoration);
// 배열, 어댑터 설정
        mateList = new ArrayList<>();
        fAdapter = new FriendsAdapter(mateList);
        mateView.setAdapter(fAdapter);
        if(mateList!=null){
            mateList.clear();
        }
        getMate();
        for(String p: phone) {
            ContentValues values = new ContentValues();
            values.put("phone", p);
            values.put("id", id);
            FriendRequest friendRequest = new FriendRequest(FRIENDS, values);
            friendRequest.execute();
        }


        leaderboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Leader.class);
                startActivity(intent);
            }
        });


        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment=new RecyclerViewFragment();
                FragmentManager fragmentManager= getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.content, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), FriendsList.class);
                intent.putStringArrayListExtra("friends", friendsList);
                startActivity(intent);
            }
        });

        return layout;
    }

    // 디바이스 내에 있는 전화번호를 contacts 배열에 담음
    private ArrayList<String> readContacts() {
        ArrayList<String> contacts = new ArrayList<String>();
        Cursor c = getActivity().getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                null, null, null);
        while (c.moveToNext()) {
            String phone = c
                    .getString(c
                            .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            contacts.add(phone);
        }
        c.close();
        return contacts;
    }

    public class FriendRequest extends AsyncTask<Void, Void, String> {
        private String url;
        private ContentValues values;
        FriendRequest(String url, ContentValues values){
            this.url = url;
            this.values = values;
        }
        @Override
        protected String doInBackground(Void... voids) {
            String result;
            RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection();
            result = requestHttpURLConnection.request(url, values);
            return result;
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            String friendResult ="";
            if (result == null){
                Toast.makeText(getActivity(), "ERROR", Toast.LENGTH_SHORT).show();
            }
            else {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(result);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    friendResult = jsonObject.getString("result");
                    if(!friendResult.equals("NO")){
                        friendsList.add(friendResult);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    void getMate(){
        new Thread(){
            @Override
            public void run() {
                String serverUri="http://ec2-52-78-174-144.ap-northeast-2.compute.amazonaws.com/getMate.php";
                String parameters = "id="+id;
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
                    for(int i=0; i<rows.length-1; i++){
                        //한줄 데이터에서 한 칸씩 분리
                        Friends mate = new Friends(rows[i]);
                        mateList.add(mate);
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            fAdapter.notifyDataSetChanged();
                        }
                    });
                } catch (MalformedURLException e) { e.printStackTrace(); } catch (IOException e) {e.printStackTrace();}
            }
        }.start();
    }

    void getWalkInfo(){
        new Thread(){
            @Override
            public void run() {
                String serverUri="http://ec2-52-78-174-144.ap-northeast-2.compute.amazonaws.com/getWalkInfo.php";
                String parameters = "id="+id;
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

                    sumUse = rows.length-1; // 총 이용 횟수

                    // km, time, cal
                    for(String row:rows){
                        String[] datas = row.split("&");
                        if(datas.length!=3){
                            continue;
                        }

                        // km, time, cal
                        sumKm += Double.parseDouble(datas[0]);
                        String time=datas[1];
                        int idx=time.indexOf(":");
                        String min= time.substring(0,idx);
                        String sec= time.substring(idx+1);

                        int min_d=Integer.parseInt(min);
                        int sec_d=Integer.parseInt(sec);

                        String time2= sumTime;
                        int idx2=time2.indexOf(":");
                        String min2=time2.substring(0,idx2);
                        String sec2=time2.substring(idx2+1);

                        int  min2_d= Integer.parseInt(min2);
                        int  sec2_d=Integer.parseInt(sec2);

                        int total_min=min_d+min2_d;
                        int total_sec= sec_d+sec2_d;
                        if(total_sec>=60){
                            total_sec=total_sec-60;
                            total_min++;
                        }

                        sumTime = total_min+":"+total_sec;
                        sumCal += Double.parseDouble(datas[2]);
                        System.out.println(sumKm+""+sumTime+sumCal+"");
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            userId.setText(UserId.getId()+"님, 반갑습니다!");
                            totalUse.setText("총 산책 수: "+ Integer.toString(sumUse));
                            totalKm.setText("총 산책 거리: "+ Double.toString(sumKm)+" km");
                            int index=sumTime.indexOf(":");
                            String min=sumTime.substring(0,index);
                            String second=sumTime.substring(index+1);
                            totalTime.setText("총 산책 시간: "+min+"m "+second+"s");
                            totalCal.setText("총 소모 칼로리: "+ Double.toString(sumCal)+" cal");
                        }
                    });

                } catch (MalformedURLException e) { e.printStackTrace(); } catch (IOException e) {e.printStackTrace();}
            }
        }.start();
    }
}

