package com.example.healthapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.FriendsViewHolder> {
    private ArrayList<Friends> friendsList;
    public FriendsAdapter(ArrayList<Friends> flist){
        this.friendsList = flist;
    }
    // 레이아웃 파일에 있는 UI 컴포넌트를 WordListViewHolder 클래스의 멤버 변수와 연결
    public class FriendsViewHolder extends RecyclerView.ViewHolder{
        TextView friendId;
        public FriendsViewHolder(View view){
            super(view);
            friendId = (TextView)view.findViewById(R.id.friendId);
        }
    }
    @NonNull
    @Override
    public FriendsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_friends,viewGroup,false);
        return new FriendsAdapter.FriendsViewHolder(view);
    }
    // onBindViewHolder가 호출될 때 WordListViewHolder에 데이터를 추가함함
    @Override
    public void onBindViewHolder(@NonNull FriendsViewHolder viewholder, int position) {
        viewholder.friendId.setText(friendsList.get(position).getFriendsId());
    }
    @Override
    public int getItemCount() {
        return (null != friendsList ? friendsList.size() : 0);
    }
}