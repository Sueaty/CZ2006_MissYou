package com.example.missyou;


import android.view.View;
import android.view.ViewGroup;
//import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.ViewGroup;
import java.util.List;
import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import android.widget.TextView;
import android.view.LayoutInflater;
import android.widget.ImageView;

public class postRecyclerAdapter extends RecyclerView.Adapter<postRecyclerAdapter.ViewHolder> {
    public List<userpost>post_list;
    public Context context;
    private TextView descView;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private View mView;
    private ImageView blogImageView;


    public postRecyclerAdapter(List<userpost>post_list){
        this.post_list = post_list;

    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_post_item, parent, false);
        context = parent.getContext();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setIsRecyclable(false);

        String desc_data = post_list.get(position).getDesc();
        holder.setDescText(desc_data);

        String image_url = post_list.get(position).getImage_url();
        String thumbUri = post_list.get(position).getImage_thumb();
        holder.setBlogImage(image_url, thumbUri);

        String user_id = post_list.get(position).getUser_id();



    }

    @Override
    public int getItemCount() {
        return post_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;

        private TextView post_des; //descView
        private ImageView post_img; //blogImageView
        private TextView post_date; //blogDate

        private TextView user_name;  //blogUserName
        private ImageView profile_pic; //blogUserImage


        public ViewHolder(View itemView) {

            super(itemView);
            mView = itemView;

        }

        public void setDescText(String descText){

            descView = mView.findViewById(R.id.post_des);
            descView.setText(descText);

        }

        public void setBlogImage(String downloadUri, String thumbUri){

            blogImageView = mView.findViewById(R.id.post_img);

            RequestOptions requestOptions = new RequestOptions();
            //requestOptions.placeholder(R.drawable.image_placeholder);

            Glide.with(context).applyDefaultRequestOptions(requestOptions).load(downloadUri).thumbnail(
                    Glide.with(context).load(thumbUri)
            ).into(blogImageView);

        }

    }


}