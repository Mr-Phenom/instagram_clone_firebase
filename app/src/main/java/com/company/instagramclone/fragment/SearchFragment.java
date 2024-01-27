package com.company.instagramclone.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.company.instagramclone.R;
import com.company.instagramclone.adapter.AdapterTag;
import com.company.instagramclone.adapter.AdapterUser;
import com.company.instagramclone.modelclass.UserModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hendraanggrian.appcompat.socialview.widget.SocialAutoCompleteTextView;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;


public class SearchFragment extends Fragment {

    RecyclerView recyclerView;
    private SocialAutoCompleteTextView searchBar;
    private List<UserModel> list;
    private AdapterUser adapterUser;

    private RecyclerView recyclerViewTags;
    private List<String> hashTags;
    private List<String> hashTagsCount;
    private AdapterTag adapterTag;

    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        recyclerView = view.findViewById(R.id.recyclerview_users_search);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        searchBar = view.findViewById(R.id.searchbar_search);
        list=new ArrayList<>();
        adapterUser = new AdapterUser(getContext(),list,true);
        recyclerView.setAdapter(adapterUser);

        recyclerViewTags = view.findViewById(R.id.recyclerview_tags_search);
        recyclerViewTags.setHasFixedSize(true);
        recyclerViewTags.setLayoutManager(new LinearLayoutManager(getContext()));

        hashTags = new ArrayList<>();
        hashTagsCount = new ArrayList<>();

        adapterTag = new AdapterTag(getContext(),hashTags,hashTagsCount);
        recyclerViewTags.setAdapter(adapterTag);

        readUsers();

        readTags();

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchUser(s.toString());

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });

        return view;
    }

    private void readTags() {

        FirebaseDatabase.getInstance().getReference().child("HashTags").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                hashTags.clear();
                hashTagsCount.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    hashTags.add(dataSnapshot.getKey());
                    hashTagsCount.add(dataSnapshot.getChildrenCount() + "");
                }

                adapterTag.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readUsers() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("User");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(TextUtils.isEmpty(searchBar.getText().toString()))
                {
                    list.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        UserModel user = dataSnapshot.getValue(UserModel.class);
                        list.add(user);
                    }
                    adapterUser.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void searchUser(String s)
    {
        Query query = FirebaseDatabase.getInstance().getReference().child("User")
                .orderByChild("userName").startAt(s).endAt(s+"\uf8ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for(DataSnapshot dataSnapshot :snapshot.getChildren())
                {
                    UserModel user = dataSnapshot.getValue(UserModel.class);
                    list.add(user);
                }
                adapterUser.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void filter(String text)
    {
        List<String> searchTags = new ArrayList<>();
        List<String> searchTagsCount = new ArrayList<>();

        for(String s : hashTags)
        {
            if(s.toLowerCase().contains(text.toLowerCase()))
            {
                searchTags.add(s);
                searchTagsCount.add(hashTagsCount.get(hashTags.indexOf(s)));
            }
        }
        adapterTag.filter(searchTags,searchTagsCount);
    }

}