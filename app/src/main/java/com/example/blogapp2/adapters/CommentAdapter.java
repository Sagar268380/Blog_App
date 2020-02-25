package com.example.blogapp2.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.blogapp2.R;
import com.example.blogapp2.models.Comments;

import org.w3c.dom.Comment;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.myCommentAdapter> {

    Context context;
    List<Comments>al;
   public CommentAdapter(Context context,List<Comments>al){
        this.context=context;
        this.al=al;
    }

    @NonNull
    @Override
    public myCommentAdapter onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
       LayoutInflater linearLayout= LayoutInflater.from(context);
       View v=linearLayout.inflate(R.layout.row_comment,viewGroup,false);
       myCommentAdapter myCommentAdapter=new myCommentAdapter(v);

        return myCommentAdapter;
    }

    @Override
    public void onBindViewHolder(@NonNull myCommentAdapter myCommentAdapter, int i) {

        Glide.with(context).load(al.get(i).getUimg()).into(myCommentAdapter.commentUserImage);
        myCommentAdapter.commentUserName.setText(al.get(i).getUname());
        myCommentAdapter.commentUSerContent.setText(al.get(i).getContent());
       myCommentAdapter.commentUserTime.setText(timestampToString((Long)(al.get(i).getTimeStamp())));
    }

    @Override
    public int getItemCount() {
        return al.size();
    }

    class myCommentAdapter extends RecyclerView.ViewHolder{

        ImageView commentUserImage;
        TextView commentUserName,commentUSerContent,commentUserTime;
        myCommentAdapter(View view){
            super(view);
            commentUserImage=view.findViewById(R.id.comment_user_img);
            commentUSerContent=view.findViewById(R.id.comment_content);
            commentUserName=view.findViewById(R.id.comment_username);
            commentUserTime=view.findViewById(R.id.comment_date);
        }
   }

    private String timestampToString(long time) {

        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        String date = DateFormat.format("hh:mm",calendar).toString();
        return date;


    }

}
