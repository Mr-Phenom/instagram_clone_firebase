package com.company.instagramclone.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.company.instagramclone.R;
import com.company.instagramclone.adapter.AdapterPost;
import com.company.instagramclone.modelclass.PostModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class PostDetailFragment extends Fragment {

    private String postId;
    private RecyclerView recyclerView;
    private AdapterPost adapterPost;
    private List<PostModel> postList;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.fragment_post_detail, container, false);

       postId = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE).getString("postId","NONE");

       recyclerView = view.findViewById(R.id.recyclerViewPostDetail);
       recyclerView.setHasFixedSize(true);
       recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
       postList = new ArrayList<>();
       adapterPost = new AdapterPost(getContext(),postList);
       recyclerView.setAdapter(adapterPost);

        FirebaseDatabase.getInstance().getReference().child("Posts").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                postList.add(snapshot.getValue(PostModel.class));
                adapterPost.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }
}