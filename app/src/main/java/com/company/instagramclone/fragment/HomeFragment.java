package com.company.instagramclone.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.style.AlignmentSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.company.instagramclone.R;
import com.company.instagramclone.adapter.AdapterPost;
import com.company.instagramclone.modelclass.PostModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.checkerframework.checker.index.qual.PolySameLen;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {

    private RecyclerView recyclerViewPosts,recyclerViewStory;
    private AdapterPost adapterPost;
    private List<PostModel> postList;
    private List<String> followingList;


    public HomeFragment() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerViewPosts = view.findViewById(R.id.recyclerView_post_home);
        recyclerViewPosts.setHasFixedSize(true);
        LinearLayoutManager layout = new LinearLayoutManager(getContext());
        layout.setStackFromEnd(true);
        layout.setReverseLayout(true);
        recyclerViewPosts.setLayoutManager(layout);

        followingList = new ArrayList<>();
        postList = new ArrayList<>();
        adapterPost = new AdapterPost(getContext(),postList);


        checkFollowingUsers();


        recyclerViewPosts.setAdapter(adapterPost);


        return view;
    }

    private void checkFollowingUsers() {

        followingList.clear();
        FirebaseDatabase.getInstance().getReference().child("Follow").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("following").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        followingList.clear();
                        for(DataSnapshot dataSnapshot : snapshot.getChildren())
                        {
                            followingList.add(dataSnapshot.getKey());
                        }

                        adapterPost.notifyDataSetChanged();
                        followingList.add(FirebaseAuth.getInstance().getCurrentUser().getUid());

                        readPost();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    private void readPost() {
        postList.removeAll(postList);
        FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    PostModel post = dataSnapshot.getValue(PostModel.class);
                    for(int i =0;i<followingList.size();i++)
                    {
                        if((post.getPublisher().toString()).equals(followingList.get(i).toString()))
                        {
                            postList.add(post);

                        }
                    }
                }
                adapterPost.notifyDataSetChanged();

            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


//        Log.d("size of list",String.valueOf(followingList.size()));
//        Log.d("size of list2",String.valueOf(postList.size()));
    }
}