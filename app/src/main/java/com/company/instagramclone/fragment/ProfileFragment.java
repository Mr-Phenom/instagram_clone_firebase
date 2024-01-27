package com.company.instagramclone.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.company.instagramclone.EditProfileActivity;
import com.company.instagramclone.FollowersActivity;
import com.company.instagramclone.OptionsActivity;
import com.company.instagramclone.R;
import com.company.instagramclone.adapter.AdapterPhoto;
import com.company.instagramclone.adapter.AdapterPost;
import com.company.instagramclone.modelclass.PostModel;
import com.company.instagramclone.modelclass.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {
    private CircleImageView imageProfile;
    private ImageView options,myPictures,savedPictures;
    private TextView posts,followers,following,fullname,bio,username;
    private RecyclerView recyclerViewPictures,recyclerViewSaved;
    private AdapterPhoto adapterPhoto;
    private List<PostModel> photoList;
    private AdapterPhoto adapterPostSaved;
    private List<PostModel> savedPost;

    private FirebaseUser firebaseUser;
    private String profileID;

    private Button editProfile;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        String data = getContext().getSharedPreferences("PROFILE", Context.MODE_PRIVATE).getString("profileId","NONE");
        if(data.equals("NONE"))
        {
            profileID = firebaseUser.getUid();
        }
        else
        {
            profileID = data;
        }


        editProfile = view.findViewById(R.id.edit_profile_button_user_profile);
        imageProfile = view.findViewById(R.id.image_profile_user_profile);
        options = view.findViewById(R.id.options_user_profile);
        myPictures = view.findViewById(R.id.my_pictures_user_profile);
        savedPictures = view.findViewById(R.id.saved_pectures_user_profile);
        posts = view.findViewById(R.id.posts_user_profile);
        followers = view.findViewById(R.id.followers_user_profile);
        following = view.findViewById(R.id.following_user_profile);
        fullname = view.findViewById(R.id.fullname_user_profile);
        bio = view.findViewById(R.id.bio_user_profile);
        username = view.findViewById(R.id.username_user_profile);
        recyclerViewPictures = view.findViewById(R.id.recyclerView_view_picture_user_profile);
        recyclerViewPictures.setHasFixedSize(true);
        recyclerViewPictures.setLayoutManager(new GridLayoutManager(getContext(),3));
        photoList=new ArrayList<>();
        adapterPhoto=new AdapterPhoto(getContext(),photoList);
        recyclerViewPictures.setAdapter(adapterPhoto);
        recyclerViewSaved = view.findViewById(R.id.recyclerView_view_saved_user_profile);
        recyclerViewSaved.setHasFixedSize(true);
        recyclerViewSaved.setLayoutManager(new GridLayoutManager(getContext(),3));
        savedPost=new ArrayList<>();
        adapterPostSaved = new AdapterPhoto(getContext(),savedPost);
        recyclerViewSaved.setAdapter(adapterPostSaved);


        userInfo();
        getFollowersAndFollowingCount();
        getPostCount();
        myPhotos();
        mySavedPost();

        if(profileID.equals(firebaseUser.getUid()))
        {
            editProfile.setText("EDIT PROFILE");
        }
        else
        {
            checkFollowingStatus();
        }

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String btntxt = editProfile.getText().toString();
                if(btntxt.equals("EDIT PROFILE"))
                {
                    startActivity(new Intent(getContext(), EditProfileActivity.class));
                }
                else
                {
                    if(btntxt.equals("Follow"))
                    {
                        FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                                .child("following").child(profileID).setValue(true);
                        FirebaseDatabase.getInstance().getReference().child("Follow").child(profileID)
                                .child("followers").child(firebaseUser.getUid()).setValue(true);
                    }
                    else
                    {
                        FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                                .child("following").child(profileID).removeValue();
                        FirebaseDatabase.getInstance().getReference().child("Follow").child(profileID)
                                .child("followers").child(firebaseUser.getUid()).removeValue();
                    }
                }
            }
        });

        myPictures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerViewPictures.setVisibility(View.VISIBLE);
                recyclerViewSaved.setVisibility(View.GONE);
                myPictures.setImageResource(R.drawable.my_pictures_icon);
                savedPictures.setImageResource(R.drawable.save_icon);
            }
        });

        savedPictures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerViewPictures.setVisibility(View.GONE);
                recyclerViewSaved.setVisibility(View.VISIBLE);
                myPictures.setImageResource(R.drawable.my_picture_icon_light);
                savedPictures.setImageResource(R.drawable.save_fill_icon);
            }
        });

        followers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), FollowersActivity.class);
                intent.putExtra("id",profileID);
                intent.putExtra("title","followers");
                startActivity(intent);
            }
        });
        following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), FollowersActivity.class);
                intent.putExtra("id",profileID);
                intent.putExtra("title","following");
                startActivity(intent);
            }
        });

        options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), OptionsActivity.class));
            }
        });

        return view;
    }

    private void mySavedPost() {
        List<String> savedId = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    savedId.add(dataSnapshot.getKey());
                }
                FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        savedPost.clear();

                        for(DataSnapshot dataSnapshot : snapshot.getChildren())
                        {
                            PostModel post = dataSnapshot.getValue(PostModel.class);
                            for(String id:savedId)
                            {
                                if(post.getPostId().equals(id))
                                {
                                    savedPost.add(post);
                                }
                            }
                        }
                        adapterPostSaved.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void myPhotos() {

        FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                photoList.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    PostModel post = dataSnapshot.getValue(PostModel.class);

                    if (post.getPublisher().equals(profileID))
                    {
                        photoList.add(post);
                    }
                }

                Collections.reverse(photoList);
                adapterPhoto.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkFollowingStatus() {
        FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid()).child("following")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.child(profileID).exists())
                        {
                            editProfile.setText("Following");
                        }
                        else
                            editProfile.setText("Follow");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void getPostCount() {

        FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int counter = 0;
                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    PostModel post = dataSnapshot.getValue(PostModel.class);
                    if(post.getPublisher().equals(profileID))
                    {
                        counter++;
                    }
                }
                posts.setText(String.valueOf(counter));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getFollowersAndFollowingCount() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Follow").child(profileID);
        reference.child("followers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followers.setText(snapshot.getChildrenCount()+"");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        reference.child("following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                following.setText(snapshot.getChildrenCount()+"");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void userInfo() {

        FirebaseDatabase.getInstance().getReference().child("User").child(profileID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserModel user = snapshot.getValue(UserModel.class);

                Picasso.get().load(user.getImageUrl()).into(imageProfile);
                username.setText(user.getUserName());
                fullname.setText(user.getName());
                bio.setText(user.getBio());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}