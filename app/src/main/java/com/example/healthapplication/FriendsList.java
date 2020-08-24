package com.example.healthapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import android.app.AlertDialog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
public class FriendsList extends AppCompatActivity {
    private static String FRIENDS =  "http://ec2-52-78-174-144.ap-northeast-2.compute.amazonaws.com/findFriendsTemp.php";
    String id = UserId.getId();
    RecyclerView friendsView;
    ArrayList<Friends> friendsList;
    ArrayList<String> friendsListTemp;
    FriendsAdapter fAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friendslist);
        Intent intent = getIntent();
        friendsListTemp = intent.getStringArrayListExtra("friends");
        // 리사이클러뷰, 어댑터, 리스트 등 초기 설정
        friendsView = findViewById(R.id.friendsView);
        friendsView.setHasFixedSize(true);
        friendsView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        friendsList = new ArrayList<>();
        fAdapter = new FriendsAdapter(friendsList);
        friendsView.setAdapter(fAdapter);
        friendsList.clear();
        friendsView.setAdapter(fAdapter);
        for(String f: friendsListTemp){
            Friends friend = new Friends(f);
            friendsList.add(friend);
            fAdapter.notifyDataSetChanged();
        }
        // 아이템들 사이에 선 추가
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(friendsView.getContext(), new LinearLayoutManager(this).getOrientation());
        friendsView.addItemDecoration(dividerItemDecoration);
        friendsView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), friendsView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
            }
            @Override
            public void onLongClick(View view, int position) {
                String friendId = friendsList.get(position).getFriendsId();
                addFriend(friendId, position);
            }
        }));
    }
    private void addFriend(final String friendId, int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(FriendsList.this);
        builder.setTitle("친구 추가");
        builder.setMessage(friendId+" 님을 친구 추가하시겠습니까?");
        builder.setNegativeButton("예",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // 1. mate 테이블에 추가
                        insertMate(friendId);
                        // 2. 현재 리스트에서 안 보이게 삭제 (mate에 추가되면 친구리스트 다시 들어올 땐 자동으로 안 보이니까)
                        Intent intent = new Intent(FriendsList.this, MainActivity.class);
                        startActivity(intent);
                    }
                });
        builder.setPositiveButton("아니오",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });
        builder.show();
    }
    void insertMate(final String friendId){
        new Thread(){
            @Override
            public void run() {
                String serverUri="http://ec2-52-78-174-144.ap-northeast-2.compute.amazonaws.com/insertMate.php";
                String parameters = "id="+id+"&mid="+friendId;
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
                } catch (MalformedURLException e) { e.printStackTrace(); } catch (IOException e) {e.printStackTrace();}
            }
        }.start();
    }
}