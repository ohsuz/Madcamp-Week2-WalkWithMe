package com.example.healthapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class LeaderAdapter extends RecyclerView.Adapter<LeaderAdapter.LeaderViewHolder>{
    private ArrayList<People> peopleList;

    public LeaderAdapter(ArrayList<People> flist){
        this.peopleList = flist;
    }
    // 레이아웃 파일에 있는 UI 컴포넌트를 WordListViewHolder 클래스의 멤버 변수와 연결
    public class LeaderViewHolder extends RecyclerView.ViewHolder{
        TextView leaderId;
        ImageView numberImg;
        public LeaderViewHolder(View view){
            super(view);
            leaderId = (TextView)view.findViewById(R.id.leaderId);
            numberImg = (ImageView)view.findViewById(R.id.numberImage);
        }
    }
    @NonNull
    @Override
    public LeaderViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_leaderboard,viewGroup,false);
        return new LeaderAdapter.LeaderViewHolder(view);
    }
    // onBindViewHolder가 호출될 때 WordListViewHolder에 데이터를 추가함함
    @Override
    public void onBindViewHolder(@NonNull LeaderAdapter.LeaderViewHolder viewholder, int position) {
        viewholder.leaderId.setText(peopleList.get(position).getName()+" ( "+peopleList.get(position).getDistance()+"km ) ");
        switch(position){
            case 0:
                viewholder.numberImg.setImageResource(R.drawable.one);
                break;
            case 1:
                viewholder.numberImg.setImageResource(R.drawable.two);
                break;
            case 2:
                viewholder.numberImg.setImageResource(R.drawable.three);
                break;
            case 3:
                viewholder.numberImg.setImageResource(R.drawable.four);
                break;
            case 4:
                viewholder.numberImg.setImageResource(R.drawable.five);
                break;
            case 5:
                viewholder.numberImg.setImageResource(R.drawable.six);
                break;
            case 6:
                viewholder.numberImg.setImageResource(R.drawable.seven);
                break;
            case 7:
                viewholder.numberImg.setImageResource(R.drawable.eight);
                break;
            case 8:
                viewholder.numberImg.setImageResource(R.drawable.nine);
                break;
            case 9:
                viewholder.numberImg.setImageResource(R.drawable.ten);
                break;
            default:
                viewholder.numberImg.setImageResource(R.drawable.user_basic);
                break;
        }
    }
    @Override
    public int getItemCount() {
        return (null != peopleList ? peopleList.size() : 0);
    }
}
