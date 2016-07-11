package kasogg.com.imageselector.resourceselect.fragment;

import android.os.Bundle;

import java.util.ArrayList;

import kasogg.com.imageselector.resourceselect.ResourceSelectActivity;

public class ImageSelectFragment extends BaseSelectFragment {

    public static ImageSelectFragment newInstance(int maxCount, ArrayList<String> selectedList) {
        ImageSelectFragment fragment = new ImageSelectFragment();
        Bundle args = new Bundle();
        args.putInt(PARAM_MAX_COUNT, maxCount);
        args.putInt(PARAM_FILE_TYPE, ResourceSelectActivity.FILE_TYPE_IMAGE);
        args.putSerializable(PARAM_SELECTED_LIST, selectedList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initParams() {
        super.initParams();
        mFileType = ResourceSelectActivity.FILE_TYPE_IMAGE;
    }

}
