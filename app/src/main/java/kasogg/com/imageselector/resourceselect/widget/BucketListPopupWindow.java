package kasogg.com.imageselector.resourceselect.widget;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;

import java.util.List;

import kasogg.com.imageselector.R;
import kasogg.com.imageselector.resourceselect.adapter.BucketAdapter;
import kasogg.com.imageselector.resourceselect.model.ResourceBucket;

/**
 * Created by KasoGG on 2016/6/29.
 */
public class BucketListPopupWindow extends PopupWindow {
    private Activity mContext;

    private List<ResourceBucket> mBucketList;
    private BucketSelectedListener mBucketSelectedListener;
    private int mFileType;

    public void showAtLocation(View anchor) {
        showAsDropDown(anchor);
        mContext.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        WindowManager.LayoutParams lp = mContext.getWindow().getAttributes();
        lp.alpha = 0.5f;
        mContext.getWindow().setAttributes(lp);
    }

    public BucketListPopupWindow(Activity context, int height, List<ResourceBucket> bucketList, int fileType) {
        super(ViewGroup.LayoutParams.MATCH_PARENT, height);
        mContext = context;
        mBucketList = bucketList;
        mFileType = fileType;
        setAnimationStyle(R.style.AnimBottom);
        setBackgroundDrawable(new BitmapDrawable(context.getResources(), (Bitmap) null));
        setOutsideTouchable(true);
        setFocusable(true);
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                mContext.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                WindowManager.LayoutParams lp = mContext.getWindow().getAttributes();
                lp.alpha = 1.0f;
                mContext.getWindow().setAttributes(lp);
            }
        });
        initViews();
    }

    public void initViews() {
        RecyclerView recyclerView = new RecyclerView(mContext);
        recyclerView.setBackgroundColor(Color.WHITE);
        recyclerView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        BucketAdapter adapter = new BucketAdapter(mContext, mBucketList, mFileType);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new BucketAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, ResourceBucket item) {
                dismiss();
                if (mBucketSelectedListener != null) {
                    mBucketSelectedListener.onBucketSelected(item);
                }
            }
        });
        setContentView(recyclerView);
    }

    public interface BucketSelectedListener {
        void onBucketSelected(ResourceBucket bucket);
    }

    public void setBucketSelectedListener(BucketSelectedListener bucketSelectedListener) {
        this.mBucketSelectedListener = bucketSelectedListener;
    }

}