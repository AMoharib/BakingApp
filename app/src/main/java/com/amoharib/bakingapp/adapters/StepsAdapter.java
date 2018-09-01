package com.amoharib.bakingapp.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amoharib.bakingapp.R;
import com.amoharib.bakingapp.model.Step;
import com.amoharib.bakingapp.util.ClickCallBack;
import com.amoharib.bakingapp.util.NetworkUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StepsAdapter extends RecyclerView.Adapter<StepsAdapter.VH> {


    private List<Step> steps;
    private Context context;
    private ClickCallBack clickCallBack;

    public void setClickCallBack(ClickCallBack clickCallBack) {
        this.clickCallBack = clickCallBack;
    }

    public StepsAdapter(List<Step> steps, Context context) {
        this.steps = steps;
        this.context = context;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_list, parent, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, final int position) {
        final Step step = steps.get(position);
        holder.stepDetailsTv.setText(step.getShortDescription());

        holder.playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickCallBack.onClick(step, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return steps.size();
    }

    public class VH extends RecyclerView.ViewHolder {
        @BindView(R.id.step_thumbnail)
        RoundedImageView stepThumbnail;
        @BindView(R.id.step_details_tv)
        TextView stepDetailsTv;
        @BindView(R.id.play_btn)
        ImageView playBtn;

        public VH(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
