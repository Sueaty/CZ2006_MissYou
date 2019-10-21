package com.example.missyou;


import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.util.*;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;
import java.util.List;
import java.lang.Object;

import static androidx.constraintlayout.widget.Constraints.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    private RecyclerView blog_list_view;
    public List<userpost> post_list;
    private FirebaseFirestore firebaseFirestore;
    private postRecyclerAdapter PostRecyclerAdapter;
    private FirebaseAuth firebaseAuth;
    private Boolean isFirstPageFirstLoad = true;
    private DocumentSnapshot lastVisible;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
 public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        post_list = new ArrayList<>();
        blog_list_view = view.findViewById(R.id.blog_list_view);
       // blog_list_view = getActivity().findViewById(R.id.blog_list_view); //blog_list_view
        firebaseAuth = FirebaseAuth.getInstance();
        PostRecyclerAdapter = new postRecyclerAdapter(post_list);
        blog_list_view.setLayoutManager(new LinearLayoutManager(container.getContext()));  // having  change
        blog_list_view.setAdapter(PostRecyclerAdapter);
//        blog_list_view.setHasFixedSize(true);


        /* if (firebaseAuth.getCurrentUser() != null) {

            firebaseFirestore = FirebaseFirestore.getInstance();

            blog_list_view.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    Boolean reachedBottom = !recyclerView.canScrollVertically(1);

                    if (reachedBottom) {

                      //  loadMorePost();

                    }

                }
            }); */
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("Posts").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e!=null){
                    Log.d(TAG,"Error:"+e.getMessage());
                }
                else {
                    if (!queryDocumentSnapshots.isEmpty()) {

                        if (isFirstPageFirstLoad) {

                            lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
                            post_list.clear();

                        }
                        for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                            // for (DocumentChange doc: queryDocumentSnapshots.getDocumentChanges())
                            if (doc.getType() == DocumentChange.Type.ADDED) {

                                String blogPostId = doc.getDocument().getId();
                                userpost blogPost = doc.getDocument().toObject(userpost.class);//.withId(blogPostId);

                                post_list.add(blogPost);
                                PostRecyclerAdapter.notifyDataSetChanged();

                                //if (isFirstPageFirstLoad) {

                                    //post_list.add(blogPost);

                               // } //else {

                                    //post_list.add(0, blogPost);

                               //}


                            }
                        }
                    }
                }
            }

        });


           /* Query firstQuery = firebaseFirestore.collection("Posts").orderBy("timestamp", Query.Direction.DESCENDING).limit(3);
            firstQuery.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                    if (!documentSnapshots.isEmpty()) {

                        if (isFirstPageFirstLoad) {

                            lastVisible = documentSnapshots.getDocuments().get(documentSnapshots.size() - 1);
                            post_list.clear();

                        }

                        for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                            if (doc.getType() == DocumentChange.Type.ADDED) {

                                String blogPostId = doc.getDocument().getId();
                                //userpost blogPost = doc.getDocument().toObject(userpost.class).withId(blogPostId);
                                userpost userPost = doc.getDocument().toObject(userpost.class);

                                if (isFirstPageFirstLoad) {

                                    post_list.add(userPost);

                                } else {

                                    post_list.add(0, userPost);

                                }


                                postRecyclerAdapter.notifyDataSetChanged();

                            }
                        }

                        isFirstPageFirstLoad = false;

                    }

                }

            }); */
        return view;
    }
}

   /* public void loadMorePost() {
        if (firebaseAuth.getCurrentUser() != null) {

            Query nextQuery = firebaseFirestore.collection("Posts")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .startAfter(lastVisible)
                    .limit(3);

            nextQuery.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                    if (!documentSnapshots.isEmpty()) {

                        lastVisible = documentSnapshots.getDocuments().get(documentSnapshots.size() - 1);
                        for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                            if (doc.getType() == DocumentChange.Type.ADDED) {

                                String blogPostId = doc.getDocument().getId();
                                userpost blogPost = doc.getDocument().toObject(userpost.class);//.withId(blogPostId);
                                post_list.add(blogPost);

                                postRecyclerAdapter.notifyDataSetChanged();
                            }

                        }
                    }

                }
            });

        }

    } */




