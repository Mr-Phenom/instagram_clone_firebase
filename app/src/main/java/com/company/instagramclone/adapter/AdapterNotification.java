package com.company.instagramclone.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.company.instagramclone.R;
import com.company.instagramclone.fragment.PostDetailFragment;
import com.company.instagramclone.fragment.ProfileFragment;
import com.company.instagramclone.modelclass.NotificationModel;
import com.company.instagramclone.modelclass.PostModel;
import com.company.instagramclone.modelclass.UserModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.Collection;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterNotification extends RecyclerView.Adapter<AdapterNotification.ViewHolder>{

    private Context context;
    private List<NotificationModel> notifications;

    public AdapterNotification(Context context, List<NotificationModel> notifications) {
        this.context = context;
        this.notifications = notifications;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.notification_item,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        NotificationModel notification = notifications.get(position);
        getUser(holder.imageProfile,holder.userName,notification.getUserId());
        holder.comment.setText(notification.getText());

        if(notification.getText().equals("liked your post"))
        {
            notification.setPost(true);
            holder.postImage.setVisibility(View.VISIBLE);
            getPostImage(holder.postImage,notification.getPostId());
            Log.d("post id ki",notification.getPostId());
        }
        else
        {
            notification.setPost(false);
            holder.postImage.setVisibility(View.GONE);
            Log.d("post id ki2",notification.isPost()+"");
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(notification.isPost())
                {
                    context.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit().putString("postId",notification.getPostId()).apply();
                    ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new PostDetailFragment()).commit();

                }
                else
                {
                    context.getSharedPreferences("PROFILE",Context.MODE_PRIVATE).edit().putString("profileId",notification.getUserId()).apply();
                    ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ProfileFragment()).commit();
                }
            }
        });

    }

    private void getPostImage(ImageView postImage, String postId) {

        FirebaseDatabase.getInstance().getReference().child("Posts").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                PostModel post = snapshot.getValue(PostModel.class);
                Picasso.get().load(post.getImageUrl()).placeholder(R.drawable.insta_logo).into(postImage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getUser(CircleImageView imageProfile, TextView userName,String userId) {

        FirebaseDatabase.getInstance().getReference().child("User").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserModel user = snapshot.getValue(UserModel.class);
                Picasso.get().load(user.getImageUrl()).placeholder(R.drawable.person_24).into(imageProfile);
                userName.setText(user.getUserName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView postImage;
        public CircleImageView imageProfile;
        public TextView userName,comment;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageProfile = itemView.findViewById(R.id.image_profile_notification_item);
            postImage = itemView.findViewById(R.id.post_image_notification_item);
            userName = itemView.findViewById(R.id.userName_notification_item);
            comment = itemView.findViewById(R.id.comment_notification_item);

        }
    }
}
