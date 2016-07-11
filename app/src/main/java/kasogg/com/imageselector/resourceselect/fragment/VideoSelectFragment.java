package kasogg.com.imageselector.resourceselect.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.util.Locale;

import kasogg.com.imageselector.R;
import kasogg.com.imageselector.resourceselect.ResourceSelectActivity;
import kasogg.com.imageselector.resourceselect.ResourceSelectActivity.SelectType;
import kasogg.com.imageselector.resourceselect.adapter.ResourceSelectAdapter;
import kasogg.com.imageselector.resourceselect.constants.ImageSelectConstants;

public class VideoSelectFragment extends BaseSelectFragment {
    public static VideoSelectFragment newInstance(int pageMaxCount, int maxCount, SelectType selectType) {
        VideoSelectFragment fragment = new VideoSelectFragment();
        Bundle args = new Bundle();
        args.putInt(ImageSelectConstants.PARAM_MAX_COUNT, maxCount);
        args.putInt(ImageSelectConstants.PARAM_PAGE_MAX_COUNT, pageMaxCount);
        args.putSerializable(ImageSelectConstants.PARAM_SELECT_TYPE, selectType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initParams() {
        super.initParams();
        mFileType = ResourceSelectActivity.FILE_TYPE_VIDEO;
        mToastStr = String.format(Locale.getDefault(), "您最多能选择%d个视频", mPageMaxCount);
    }

    @Override
    public void onAddClick(ResourceSelectAdapter.ViewHolder viewHolder) {
        //TODO 拍照 视频
        Toast.makeText(getContext(), "视频", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tv_preview:
                //TODO 打开预览视频
                break;
        }
    }
}
