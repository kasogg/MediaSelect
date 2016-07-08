package kasogg.com.imageselector.resourceselect.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import kasogg.com.imageselector.R;
import kasogg.com.imageselector.resourceselect.ResourceSelectActivity;
import kasogg.com.imageselector.resourceselect.model.ResourceBucket;

public class BucketAdapter extends RecyclerView.Adapter<BucketAdapter.ViewHolder> {
    private List<ResourceBucket> mImageItemList;
    private Context mContext;
    private OnItemClickListener mOnItemClickListener;
    private int mFileType;

    public BucketAdapter(Context context, List<ResourceBucket> imageItemList, int fileType) {
        mImageItemList = imageItemList;
        mContext = context;
        mFileType = fileType;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_iamge_bucket, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        final ResourceBucket item = mImageItemList.get(position);
        viewHolder.tvBucketName.setText(item.bucketName);
        String suffix = mFileType == ResourceSelectActivity.FILE_TYPE_IMAGE ? "张图片" : "个视频";
        viewHolder.tvImageCount.setText(String.valueOf(item.count) + suffix);
        if (item.imageList != null && item.imageList.size() > 0) {
            Glide.with(mContext).load(item.imageList.get(0).sourcePath).into(viewHolder.ivBucket);
        }
        if (mOnItemClickListener != null) {
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(position, item);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mImageItemList == null ? 0 : mImageItemList.size();
    }

    public void setAndRefresh(List<ResourceBucket> dataList) {
        this.mImageItemList = dataList;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivBucket;
        TextView tvBucketName;
        TextView tvImageCount;

        public ViewHolder(View itemView) {
            super(itemView);
            ivBucket = (ImageView) itemView.findViewById(R.id.iv_bucket);
            tvBucketName = (TextView) itemView.findViewById(R.id.tv_bucket_name);
            tvImageCount = (TextView) itemView.findViewById(R.id.tv_bucket_image_count);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position, ResourceBucket item);
    }
}
