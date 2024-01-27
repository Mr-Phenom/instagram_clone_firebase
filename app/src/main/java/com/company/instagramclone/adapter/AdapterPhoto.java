package com.company.instagramclone.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.company.instagramclone.R;
import com.company.instagramclone.fragment.PostDetailFragment;
import com.company.instagramclone.modelclass.PostModel;
import com.squareup.picasso.Picasso;

import org.checkerframework.checker.units.qual.C;

import java.util.List;

public class AdapterPhoto extends RecyclerView.Adapter<AdapterPhoto.ViewHolder>{

    private Context context;
    private List<PostModel> posts;

    public AdapterPhoto(Context context, List<PostModel> posts) {
        this.context = context;
        this.posts = posts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_my_photos,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PostModel post = posts.get(position);
        Picasso.get().load(post.getImageUrl()).into(holder.post_image);

        holder.post_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit().putString("postId", post.getPostId()).apply();
                ((FragmentActivity)context).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container,new PostDetailFragment()).commit();

            }
        });

    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView post_image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            post_image = itemView.findViewById(R.id.item_image_my_photo);
        }
    }
}
