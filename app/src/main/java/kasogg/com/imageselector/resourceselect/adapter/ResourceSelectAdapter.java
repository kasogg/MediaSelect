package kasogg.com.imageselector.resourceselect.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

import kasogg.com.imageselector.R;
import kasogg.com.imageselector.resourceselect.imageloader.GlideImageLoader;
import kasogg.com.imageselector.resourceselect.imageloader.ImageLoader;
import kasogg.com.imageselector.resourceselect.model.ResourceItem;

public class ResourceSelectAdapter extends RecyclerView.Adapter<ResourceSelectAdapter.ViewHolder> {

    private List<ResourceItem> mResourceItemList;
    private Context mContext;
    private OnItemClickListener mOnItemClickListener;

    public ResourceSelectAdapter(Context context, List<ResourceItem> resourceItemList) {
        mResourceItemList = resourceItemList;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_select, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        if (position == 0) {
            viewHolder.ivImage.setImageResource(R.mipmap.ic_take_video);
            viewHolder.ivSelect.setVisibility(View.GONE);
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onAddClick(viewHolder);
                    }
                }
            });
            return;
        }
        viewHolder.ivSelect.setVisibility(View.VISIBLE);
        final ResourceItem item = mResourceItemList.get(position - 1);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectItem(viewHolder, item, !viewHolder.ivSelect.isSelected());
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(viewHolder, viewHolder.getAdapterPosition(), item);
                }
            }
        });
        GlideImageLoader.getInstance().loadImage(mContext, item.sourcePath, viewHolder.ivImage, new ImageLoader.ImageLoadListener() {
            @Override
            public void onLoadSuccess(Drawable drawable, ImageView imageView) {
                selectItem(viewHolder, item, item.isSelected);
            }
        });
    }

    public void selectItem(ViewHolder viewHolder, ResourceItem item, boolean isSelected) {
        item.isSelected = isSelected;
        viewHolder.ivSelect.setSelected(item.isSelected);
        Drawable drawable = viewHolder.ivImage.getDrawable();
        if (drawable != null) {
            if (isSelected) {
                drawable.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
            } else {
                drawable.clearColorFilter();
            }
            viewHolder.ivImage.setImageDrawable(drawable);
        }
    }

    @Override
    public int getItemCount() {
        return mResourceItemList == null ? 1 : mResourceItemList.size() + 1;
    }

    public void setAndRefresh(List<ResourceItem> dataList) {
        this.mResourceItemList = dataList;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        ImageView ivSelect;

        public ViewHolder(View itemView) {
            super(itemView);
            ivImage = (ImageView) itemView.findViewById(R.id.iv_image);
            ivSelect = (ImageView) itemView.findViewById(R.id.iv_select);
        }
    }

    public interface OnItemClickListener {
        void onAddClick(ViewHolder viewHolder);

        void onItemClick(ViewHolder viewHolder, int position, ResourceItem item);
    }
}
