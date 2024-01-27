package com.company.instagramclone.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.company.instagramclone.CommentActivity;
import com.company.instagramclone.FollowersActivity;
import com.company.instagramclone.R;
import com.company.instagramclone.fragment.PostDetailFragment;
import com.company.instagramclone.fragment.ProfileFragment;
import com.company.instagramclone.modelclass.PostModel;
import com.company.instagramclone.modelclass.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hendraanggrian.appcompat.socialview.widget.SocialTextView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class AdapterPost extends RecyclerView.Adapter<AdapterPost.ViewHolder>{

    private Context context;
    private List<PostModel> posts;
    private FirebaseUser firebaseUser;


    public AdapterPost(Context context, List<PostModel> posts) {
        this.context = context;
        this.posts = posts;
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.post_item,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {





        PostModel post = posts.get(position);

        Picasso.get().load(post.getImageUrl()).into(holder.postImage);

        holder.description.setText(post.getDescription());

        FirebaseDatabase.getInstance().getReference().child("User").child(post.getPublisher()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserModel user = snapshot.getValue(UserModel.class);
                if(user.getImageUrl().equals("default"))
                {
                    Picasso.get().load(R.drawable.person_24).into(holder.imageProfile);
                }
                else
                {
                    Picasso.get().load(user.getImageUrl()).into(holder.imageProfile);
                }

                holder.username.setText(user.getUserName());
                holder.author.setText(user.getName());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        isLiked(post.getPostId(),holder.likes);
        numberOfLikes(post.getPostId(),holder.no_of_likes);
        numberOfComments(post.getPostId(),holder.no_of_comments);
        isSaved(post.getPostId(),holder.save);

        holder.likes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.likes.getTag().equals("Like"))
                {
                    FirebaseDatabase.getInstance().getReference().child("Likes")
                            .child(post.getPostId()).child(firebaseUser.getUid()).setValue(true);

                    addNotification(post.getPostId(),post.getPublisher());
                }
                else
                {
                    FirebaseDatabase.getInstance().getReference().child("Likes")
                            .child(post.getPostId()).child(firebaseUser.getUid()).removeValue();
                }
            }
        });

        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CommentActivity.class);
                intent.putExtra("postId",post.getPostId());
                intent.putExtra("authorId",post.getPublisher());
                context.startActivity(intent);
            }
        });
        holder.no_of_comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CommentActivity.class);
                intent.putExtra("postId",post.getPostId());
                intent.putExtra("authorId",post.getPublisher());
                context.startActivity(intent);
            }
        });
        holder.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.save.getTag().equals("save"))
                {
                    FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid())
                            .child(post.getPostId()).setValue(true);
                }
                else
                {
                    FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid())
                            .child(post.getPostId()).removeValue();
                }
            }
        });

        holder.imageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.getSharedPreferences("PROFILE",Context.MODE_PRIVATE).edit().putString("profileId",post.getPublisher()).apply();
                ((FragmentActivity)context).getSupportFragmentManager()
                        .beginTransaction().replace(R.id.fragment_container,new ProfileFragment()).commit();
            }
        });
        holder.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.getSharedPreferences("PROFILE",Context.MODE_PRIVATE).edit().putString("profileId",post.getPublisher()).apply();
                ((FragmentActivity)context).getSupportFragmentManager()
                        .beginTransaction().replace(R.id.fragment_container,new ProfileFragment()).commit();
            }
        });
        holder.author.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.getSharedPreferences("PROFILE",Context.MODE_PRIVATE).edit().putString("profileId",post.getPublisher()).apply();
                ((FragmentActivity)context).getSupportFragmentManager()
                        .beginTransaction().replace(R.id.fragment_container,new ProfileFragment()).commit();
            }
        });
        holder.postImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit().putString("postId", post.getPostId()).apply();
                ((FragmentActivity)context).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container,new PostDetailFragment()).commit();

            }
        });


        holder.no_of_likes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, FollowersActivity.class);
                intent.putExtra("id",post.getPostId());
                intent.putExtra("title","likes");
                context.startActivity(intent);
            }
        });
    }

    private void addNotification(String postId, String publisher) {

        HashMap<String,Object> map = new HashMap<>();

        map.put("userId",firebaseUser.getUid());
        map.put("text","liked your post");
        map.put("postId",postId);
        map.put("isPost",true);

        FirebaseDatabase.getInstance().getReference().child("Notifications").child(publisher).push().setValue(map);
    }

    private void isSaved(String postId, ImageView save) {
        FirebaseDatabase.getInstance().getReference().child("Saves").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.child(postId).exists())
                        {
                            save.setImageResource(R.drawable.save_fill_icon);
                            save.setTag("saved");
                        }
                        else
                        {
                            save.setImageResource(R.drawable.save_icon);
                            save.setTag("save");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView imageProfile,postImage,likes,comment,save,more;
        public TextView username,no_of_likes,author,no_of_comments;
        SocialTextView description;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageProfile = itemView.findViewById(R.id.image_profile_post_item);
            postImage = itemView.findViewById(R.id.post_image_item);
            likes = itemView.findViewById(R.id.like);
            comment = itemView.findViewById(R.id.comment);
            save = itemView.findViewById(R.id.save);
            more = itemView.findViewById(R.id.more);
            username = itemView.findViewById(R.id.username_post_item);
            no_of_likes = itemView.findViewById(R.id.no_of_likes_text);
            no_of_comments = itemView.findViewById(R.id.no_of_comments);
            author = itemView.findViewById(R.id.author);
            description = itemView.findViewById(R.id.description_post_item);





        }
    }
    private void isLiked(String postId,ImageView imageView)
    {
        FirebaseDatabase.getInstance().getReference().child("Likes")
                .child(postId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.child(firebaseUser.getUid()).exists())
                        {
                            imageView.setImageResource(R.drawable.liked_2_icon);
                            imageView.setTag("Liked");
                        }
                        else
                        {
                            imageView.setImageResource(R.drawable.like_icon);
                            imageView.setTag("Like");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void numberOfLikes(String postId,TextView text)
    {
        FirebaseDatabase.getInstance().getReference().child("Likes").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                text.setText(snapshot.getChildrenCount() + " likes");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void numberOfComments(String postId,TextView text)
    {
        FirebaseDatabase.getInstance().getReference().child("Comments").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                text.setText("View all "+snapshot.getChildrenCount()+" comments");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
