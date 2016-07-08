package kasogg.com.imageselector.resourceselect.imageloader;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

/**
 * Created by KasoGG on 2016/6/30.
 */
public interface ImageLoader {
    void loadImage(Context mContext, String path, ImageView imageView, ImageLoadListener listener);

    interface ImageLoadListener {
        void onLoadSuccess(Drawable drawable, ImageView imageView);
    }
}
