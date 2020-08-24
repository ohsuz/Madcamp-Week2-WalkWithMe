package com.example.healthapplication;

import android.os.Bundle;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class Leader extends AppCompatActivity{
    ArrayList<People> peopleList;
    RecyclerView leaderView;
    LeaderAdapter leaderAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader);

        leaderView = findViewById(R.id.leaderView);
        leaderView.setHasFixedSize(true);
        leaderView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        peopleList = new ArrayList<>();
        leaderAdapter = new LeaderAdapter(peopleList);
        leaderView.setAdapter(leaderAdapter);


        getLeaderboard();

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(leaderView.getContext(), new LinearLayoutManager(this).getOrientation());
        leaderView.addItemDecoration(dividerItemDecoration);

    }

    void getLeaderboard() {
        new Thread() {
            @Override
            public void run() {
                String serverUri = "http://ec2-52-78-174-144.ap-northeast-2.compute.amazonaws.com/getLeaderboard.php";
                try {
                    URL url = new URL(serverUri);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setDoInput(true);
                    connection.setUseCaches(false);
                    InputStream is = connection.getInputStream();
                    InputStreamReader isr = new InputStreamReader(is);
                    BufferedReader reader = new BufferedReader(isr);
                    final StringBuffer buffer = new StringBuffer();
                    String line = reader.readLine();
                    while (line != null) {
                        buffer.append(line + "\n");
                        line = reader.readLine();
                    }
                    //읽어온 문자열에서 row(레코드)별로 분리하여 배열로 리턴하기
                    String[] rows = buffer.toString().split(";");

                    for (String row : rows) {
                        //한줄 데이터에서 한 칸씩 분리
                        String[] datas = row.split("&");
                        if (datas.length != 2) continue;
                        String id = datas[0];
                        double sum = Double.parseDouble(datas[1]);

                        peopleList.add(new People(id, sum));
                    }

                    int n = peopleList.size();
                    for (int i = 0; i < n - 1; i++) {
                        for (int j = 0; j < n - i - 1; j++) {
                            if (peopleList.get(j).getDistance() < peopleList.get(j + 1).getDistance()) {
                                // swap arr[j+1] and arr[i]
                                People temp = peopleList.get(j);
                                peopleList.set(j, peopleList.get(j + 1));
                                peopleList.set(j + 1, temp);
                            }
                        }
                    }

                    //리스트뷰 갱신
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            leaderAdapter.notifyDataSetChanged();
                        }
                    });
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }
}
