package com.example.healthapplication;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
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
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

// A fragment to hold RecylcerView
public class RecyclerViewFragment extends Fragment implements GalleryItemClickListener{

    String id = UserId.getId();


    View mView;
    Context mContext;

    GalleryAdapter galleryAdapter;
    RecyclerView recyclerView;

    ArrayList<ImageModel> galleryList;

    public static final String TAG = RecyclerViewFragment.class.getSimpleName();

    public RecyclerViewFragment() {
    }

    public static RecyclerViewFragment newInstance() {
        return new RecyclerViewFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_recycler_view, container, false);
        mContext = getContext();
        return mView;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        // GalleryAdapter 사용
        super.onViewCreated(view, savedInstanceState);
        galleryList = new ArrayList<ImageModel>();

        // 사용자 ID에 해당하는 Walk History Image를 서버에서 읽어오기
        getWalkHistory();

        galleryAdapter = new GalleryAdapter(galleryList, this);
        recyclerView = view.findViewById(R.id.recycler_view);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(galleryAdapter);
    }

    @Override
    public void onGalleryItemClickListener(int position, ImageModel imageModel, ImageView imageView) {
        GalleryViewPagerFragment galleryViewPagerFragment = GalleryViewPagerFragment.newInstance(position, galleryList);

        getFragmentManager()
                .beginTransaction()
                .addSharedElement(imageView, ViewCompat.getTransitionName(imageView))
                .addToBackStack(TAG)
                .replace(android.R.id.content, galleryViewPagerFragment)
                .commit();
    }

    void getWalkHistory(){
        new Thread(){
            @Override
            public void run() {
                String serverUri="http://ec2-52-78-174-144.ap-northeast-2.compute.amazonaws.com/getWalkHistory.php";
                String parameters = "id="+id;
                try {
                    URL url= new URL(serverUri);
                    HttpURLConnection connection= (HttpURLConnection) url.openConnection();
                    // connection.setRequestMethod("GET");
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
                    // 데이터 초기화
                    galleryList.clear();
                    for(String row : rows){
                        //한줄 데이터에서 한 칸씩 분리
                        String[] datas=row.split("&");
                        if(datas.length!=2) continue;
                        String imgPath= "http://ubuntu@ec2-52-78-174-144.ap-northeast-2.compute.amazonaws.com/"+datas[0];   //이미지는 상대경로라서 앞에 서버 주소를 써야한다.
                        String date=datas[1];
                        //대량의 데이터 ArrayList에 추가
                        galleryList.add(new ImageModel(date, imgPath));
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

}