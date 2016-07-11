package kasogg.com.imageselector.resourceselect.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import java.util.List;

import kasogg.com.imageselector.resourceselect.imageloader.GlideImageLoader;
import kasogg.com.imageselector.resourceselect.imageloader.ImageLoader;
import kasogg.com.imageselector.resourceselect.model.ResourceItem;

public class ResourceThirdPartySelectAdapter extends ResourceSelectAdapter {
    public ResourceThirdPartySelectAdapter(Context context, List<ResourceItem> resourceItemList) {
        super(context, resourceItemList);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        final ResourceItem item = mResourceItemList.get(position);
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

    @Override
    public int getItemCount() {
        return mResourceItemList == null ? 0 : mResourceItemList.size();
    }
}
