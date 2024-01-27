package com.company.instagramclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.company.instagramclone.adapter.AdapterComment;
import com.company.instagramclone.modelclass.CommentModel;
import com.company.instagramclone.modelclass.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentActivity extends AppCompatActivity {

    private EditText addComment;
    private TextView post;
    private CircleImageView imageProfile;

    private String postId,authorId;

    private RecyclerView recyclerView;
    private AdapterComment adapterComment;
    private List<CommentModel> commentList;

    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        Toolbar toolbar = findViewById(R.id.toolbar_comment);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Comments");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Intent intent = getIntent();
        postId = intent.getStringExtra("postId");
        authorId =  intent.getStringExtra("authorId");

        recyclerView = findViewById(R.id.recyclerView_comment);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        commentList = new ArrayList<>();
        adapterComment = new AdapterComment(this,commentList,postId);

        recyclerView.setAdapter(adapterComment);

        addComment = findViewById(R.id.comment_comment);
        imageProfile = findViewById(R.id.image_profile_comment);
        post = findViewById(R.id.post_comment);



        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        getUserImage();
        getComment();
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(addComment.getText()))
                {
                    Toast.makeText(CommentActivity.this, "No comment added!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    putComment();
                }
            }
        });
    }

    private void getComment()
    {
        FirebaseDatabase.getInstance().getReference().child("Comments").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                commentList.clear();

                for(DataSnapshot dataSnapshot :snapshot.getChildren())
                {
                    CommentModel comment = dataSnapshot.getValue(CommentModel.class);
                    commentList.add(comment);
                }

                adapterComment.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void putComment() {
        HashMap<String,Object> map = new HashMap<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Comments").child(postId);

        String id = reference.push().getKey();

        map.put("id",id);
        map.put("comment",addComment.getText().toString());
        map.put("publisher",firebaseUser.getUid());
        reference.child(id)
                .setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(CommentActivity.this, "Comment Added", Toast.LENGTH_SHORT).show();
                            addComment.setText("");
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

                            // Hide the soft keyboard
                            if (imm != null) {
                                imm.hideSoftInputFromWindow(addComment.getWindowToken(), 0);
                            }
                        }
                        else
                        {
                            Toast.makeText(CommentActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private void getUserImage() {

        FirebaseDatabase.getInstance().getReference().child("User").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserModel user = snapshot.getValue(UserModel.class);
                if(user.getImageUrl().equals("default"))
                {
                    Picasso.get().load(R.drawable.person_24).into(imageProfile);
                }
                else
                {
                    Picasso.get().load(user.getImageUrl()).placeholder(R.drawable.person_24).into(imageProfile);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}