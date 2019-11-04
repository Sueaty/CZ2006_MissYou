package com.example.missyou;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.Date;
import java.util.List;
import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.Toast;

public class postRecyclerAdapter extends RecyclerView.Adapter<postRecyclerAdapter.ViewHolder> {
    public List<userpost>post_list;
    public Context context;
    private TextView descView;
    private TextView userHp;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private View mView;
    private ImageView blogImageView;

    public LatLng location;


    //private View mapView;

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
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.setIsRecyclable(false);


        String desc_data = post_list.get(position).getDesc();

        holder.setDescText(desc_data);

        String user_phone = post_list.get(position).getUser_phone();
        holder.setPost_hp(user_phone);

       // double latitude = post_list.get(position).getLatitude();
        //double longitude = post_list.get(position).getLongitude();
////////////////////////////////////////////////////////////////////
        LatLng location = post_list.get(position).getLocation();
         holder.setLocation(location);


        String image_url = post_list.get(position).getImage_url();
        String thumbUri = post_list.get(position).getImage_thumb();
        holder.setBlogImage(image_url, thumbUri);


        String user_id = post_list.get(position).getUser_id();

        //User Data will be retrieved here...
        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful()){

                    String userName = task.getResult().getString("name");
                    String userImage = task.getResult().getString("image");

                    holder.setUserData(userName, userImage);


                } else {

                    //Firebase Exception
                    System.out.println("Requested entity was not found");

                }

            }
        });




        // get time display in post
        try {
            long millisecond = post_list.get(position).getTimestamp().getTime();
            String dateString = DateFormat.format("MM/dd/yyyy", new Date(millisecond)).toString();
            holder.setTime(dateString);
        } catch (Exception e) {

            Toast.makeText(context, "Exception : " + e.getMessage(), Toast.LENGTH_SHORT).show();

        }


    }

    @Override
    public int getItemCount() {
        return post_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;
       private TextView LocView;
        private TextView post_des; //descView
        private ImageView post_img; //blogImageView
        private TextView post_date; //blogDate
        private TextView post_hp;


        private TextView username;  //blogUserName
        private ImageView profilepic; //blogUserImage


        public ViewHolder(View itemView) {

            super(itemView);
            mView = itemView;
            init(mView);

        }

        public void setDescText(String descText){

            descView = mView.findViewById(R.id.post_des);
            descView.setText(descText);

        }

        public void setPost_hp (String post_hp)
        {
            userHp= mView.findViewById(R.id.post_hp);
            userHp.setText(post_hp);
        }

        public void setBlogImage(String downloadUri, String thumbUri){

            blogImageView = mView.findViewById(R.id.post_img);

            RequestOptions requestOptions = new RequestOptions();

            Glide.with(context).applyDefaultRequestOptions(requestOptions).load(downloadUri).thumbnail(
                    Glide.with(context).load(thumbUri)
            ).into(blogImageView);

        }
/*
        public void setLatitude(double latitude){

            LocView = mView.findViewById(R.id.toMap);
            LocView.setText(latitude);

        }

        public void setLongitude( double longitude){





        }


*/

 public void setLocation(LatLng location){

     //lostFoundPetsLocationActivity lostFoundLoc = new lostFoundPetsLocationActivity();

   // LatLng location = new LatLng(latitude , longitude);



  //LocView = mView.findViewById(R.id.post_hp);//////////change!!
    //LocView.setText(location.toString());
 }



        public void setTime(String date) {

            post_date = mView.findViewById(R.id.post_date);
            post_date.setText(date);

        }


        public void setUserData(String name, String image){   // user profile pic and name

            profilepic= mView.findViewById(R.id.profile_pic);
            username = mView.findViewById(R.id.user_name);
            username.setText(name);

            RequestOptions placeholderOption = new RequestOptions();
            placeholderOption.placeholder(R.drawable.default_profile_image);

            Glide.with(context).applyDefaultRequestOptions(placeholderOption).load(image).into(profilepic);

        }

    }


    private void init(View v) {

        ImageButton toMap = (ImageButton) v.findViewById(R.id.toMap);

        toMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,  lostFoundPetsLocationActivity.class);
                context.startActivity(intent);
            }
        });
    }



}