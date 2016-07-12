package kasogg.com.imageselector.resourceselect.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.util.Locale;

import kasogg.com.imageselector.R;
import kasogg.com.imageselector.resourceselect.adapter.ResourceSelectAdapter;
import kasogg.com.imageselector.resourceselect.constants.ResourceSelectConstants;
import kasogg.com.imageselector.resourceselect.constants.SelectType;

public class ImageSelectFragment extends BaseSelectFragment {

    public static ImageSelectFragment newInstance(int pageMaxCount, int maxCount, SelectType selectType) {
        ImageSelectFragment fragment = new ImageSelectFragment();
        Bundle args = new Bundle();
        args.putInt(ResourceSelectConstants.PARAM_MAX_COUNT, maxCount);
        args.putInt(ResourceSelectConstants.PARAM_PAGE_MAX_COUNT, pageMaxCount);
        args.putSerializable(ResourceSelectConstants.PARAM_SELECT_TYPE, selectType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initParams() {
        super.initParams();
        mFileType = ResourceSelectConstants.FILE_TYPE_IMAGE;
        mToastStr = String.format(Locale.getDefault(), "您最多能选择%d张图片", mPageMaxCount);
    }

    @Override
    public void onAddClick(ResourceSelectAdapter.ViewHolder viewHolder) {
        //TODO 拍照 视频
        Toast.makeText(getContext(), "拍照", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tv_preview:
                //TODO 打开预览图片
                break;
        }
    }

}
