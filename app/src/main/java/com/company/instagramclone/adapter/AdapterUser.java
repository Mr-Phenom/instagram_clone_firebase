package com.company.instagramclone.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.company.instagramclone.MainActivity;
import com.company.instagramclone.R;
import com.company.instagramclone.fragment.ProfileFragment;
import com.company.instagramclone.modelclass.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterUser extends RecyclerView.Adapter<AdapterUser.ViewHolder>{

    private Context context;
    private List<UserModel> list;
    private boolean isFragment;
    private FirebaseUser firebaseUser;

    public AdapterUser(Context context, List<UserModel> list, boolean isFragment) {
        this.context = context;
        this.list = list;
        this.isFragment = isFragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_users,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        UserModel user = list.get(position);
        holder.follow.setVisibility(View.VISIBLE);

        holder.userName.setText(user.getUserName());
        holder.fullName.setText(user.getName());
        Picasso.get().load(user.getImageUrl()).placeholder(R.drawable.person_24).into(holder.imageProfile);

        isFollowed(user.getId(),holder.follow);

        if(user.getId().equals(firebaseUser.getUid()))
        {
            holder.follow.setVisibility(View.GONE);
        }

        holder.follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.follow.getText().equals("Follow"))
                {
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("following").child(user.getId()).setValue(true);

                    FirebaseDatabase.getInstance().getReference().child("Follow").child(user.getId()).child("followers")
                            .child(firebaseUser.getUid()).setValue(true);

                    addNotification(user.getId());
                }
                else
                {
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("following").child(user.getId()).removeValue();

                    FirebaseDatabase.getInstance().getReference().child("Follow").child(user.getId()).child("followers")
                            .child(firebaseUser.getUid()).removeValue();
                }
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isFragment)
                {
                    context.getSharedPreferences("PROFILE",Context.MODE_PRIVATE).edit().putString("profileId", user.getId()).apply();

                    ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ProfileFragment()).commit();
                }

                else
                {
                    Intent intent = new Intent(context, MainActivity.class);
                    intent.putExtra("publisherId",user.getId());
                    context.startActivity(intent);
                }
            }
        });
    }

    private void addNotification(String id) {

        HashMap<String ,Object> map = new HashMap<>();

        map.put("userId",firebaseUser.getUid());
        map.put("text","started follow you");
        map.put("isPost",false);


        FirebaseDatabase.getInstance().getReference().child("Notifications").child(id).push().setValue(map);
    }

    private void isFollowed(String id, Button follow) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                .child("following");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(id).exists())
                {
                    follow.setText("Following");
                }
                else
                    follow.setText("Follow");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public CircleImageView imageProfile;
        public TextView userName,fullName;
        public Button follow;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageProfile=itemView.findViewById(R.id.image_profile_item);
            userName=itemView.findViewById(R.id.username_item);
            fullName=itemView.findViewById(R.id.fullname_item);
            follow=itemView.findViewById(R.id.button_follow_item);


        }
    }
}
