package com.company.instagramclone.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.company.instagramclone.R;

import java.util.List;

public class AdapterTag extends RecyclerView.Adapter<AdapterTag.ViewHolder>{

    private Context context;
    private List<String> tags;
    private List<String> tagsCount;

    public AdapterTag(Context context, List<String> tags, List<String> tagsCount) {
        this.context = context;
        this.tags = tags;
        this.tagsCount = tagsCount;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tag,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.tag.setText("#"+tags.get(position));
        holder.number.setText(tagsCount.get(position)+" posts");
    }

    @Override
    public int getItemCount() {
        return tags.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView tag,number;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tag=itemView.findViewById(R.id.textView_hashtag_item);
            number=itemView.findViewById(R.id.textview_number_post_item);
        }
    }

    public void filter(List<String> filterTags, List<String> filerTagsCount)
    {
        this.tags = filterTags;
        this.tagsCount = filerTagsCount;
        notifyDataSetChanged();
    }
}
