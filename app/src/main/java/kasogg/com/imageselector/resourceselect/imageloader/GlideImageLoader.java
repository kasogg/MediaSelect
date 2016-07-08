package kasogg.com.imageselector.resourceselect.imageloader;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

/**
 * Created by KasoGG on 2016/6/30.
 */
public class GlideImageLoader implements ImageLoader {
    private GlideImageLoader() {
    }

    @Override
    public void loadImage(Context context, String path, final ImageView imageView, final ImageLoadListener listener) {
        Glide.with(context).load(path).into(new GlideDrawableImageViewTarget(imageView) {
            @Override
            public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
                super.onResourceReady(resource, animation);
                listener.onLoadSuccess(resource, imageView);
            }
        });
    }

    public static GlideImageLoader getInstance() {
        return Nested.instance;
    }

    private static class Nested {
        private static GlideImageLoader instance = new GlideImageLoader();
    }
}
