package kasogg.com.imageselector.resourceselect;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import kasogg.com.imageselector.R;
import kasogg.com.imageselector.XLBaseActivity;
import kasogg.com.imageselector.resourceselect.adapter.ResourceSelectAdapter;
import kasogg.com.imageselector.resourceselect.fragment.BaseSelectFragment;
import kasogg.com.imageselector.resourceselect.imagefetcher.ResourceFetcher;
import kasogg.com.imageselector.resourceselect.model.ResourceBucket;
import kasogg.com.imageselector.resourceselect.model.ResourceItem;
import kasogg.com.imageselector.resourceselect.widget.BucketListPopupWindow;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by KasoGG on 2016/6/29.
 */
public class ResourceSelectActivity extends XLBaseActivity implements BaseSelectFragment.OnFragmentInteractionListener {
    public static final int RESULT_SELECTED = 1;
    public static final int FILE_TYPE_IMAGE = 1;
    public static final int FILE_TYPE_VIDEO = 2;

    public static final String PARAM_TAB_COUNT = "PARAM_TAB_COUNT";
    public static final String PARAM_SELECTED_IMAGE_LIST = "PARAM_SELECTED_IMAGE_LIST";
    public static final String PARAM_SELECTED_VIDEO_LIST = "PARAM_SELECTED_VIDEO_LIST";
    public static final String PARAM_SELECTED_LIST = "PARAM_SELECTED_LIST";
    private static final int DEFAULT_IMAGE_MAX_COUNT = 9;
    private static final int DEFAULT_VIDEO_MAX_COUNT = 1;

    private TextView mTvTitleRight;
    private ArrayList<String> mSelectedList = new ArrayList<>();
    private ArrayList<String> mSelectedImagePathList = new ArrayList<>();
    private ArrayList<String> mSelectedVideoPathList = new ArrayList<>();

    private int mFileType = FILE_TYPE_IMAGE;
    private int mTabCount = 3;
    private int mImageMaxCount;
    private int mVideoMaxCount;

    public static void showImageSelect(Activity activity, int requestCode, ArrayList<String> selectedList) {
        show(activity, requestCode, selectedList, null, FILE_TYPE_IMAGE, DEFAULT_IMAGE_MAX_COUNT, DEFAULT_VIDEO_MAX_COUNT, 1);
    }

    public static void showVideoSelect(Activity activity, int requestCode, ArrayList<String> selectedList) {
        show(activity, requestCode, null, selectedList, FILE_TYPE_VIDEO, DEFAULT_IMAGE_MAX_COUNT, DEFAULT_VIDEO_MAX_COUNT, 1);
    }

    public static void showAllSelect(Activity activity, int requestCode, ArrayList<String> selectedImageList, ArrayList<String> selectedVideoList) {
        show(activity, requestCode, selectedImageList, selectedVideoList, FILE_TYPE_IMAGE, DEFAULT_IMAGE_MAX_COUNT, DEFAULT_VIDEO_MAX_COUNT, 2);
    }

    public static void show(Activity activity, int requestCode, ArrayList<String> selectedImageList, ArrayList<String> selectedVideoList, int defaultFileType, int
            imageMaxCount, int videoMaxCount, int tabCount) {
        Intent intent = new Intent(activity, ResourceSelectActivity.class);
        intent.putExtra(PARAM_SELECTED_IMAGE_LIST, selectedImageList);
        intent.putExtra(PARAM_SELECTED_VIDEO_LIST, selectedVideoList);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_resource_select);
    }

    @Override
    protected void initParams() {
        mTabCount = getIntent().getIntExtra(PARAM_TAB_COUNT, 1);
        mSelectedImagePathList = (ArrayList<String>) getIntent().getSerializableExtra(PARAM_SELECTED_IMAGE_LIST);
        mSelectedVideoPathList = (ArrayList<String>) getIntent().getSerializableExtra(PARAM_SELECTED_VIDEO_LIST);
        if (mSelectedImagePathList == null) {
            mSelectedImagePathList = new ArrayList<>();
        }
        if (mSelectedVideoPathList == null) {
            mSelectedVideoPathList = new ArrayList<>();
        }
    }

    @Override
    protected void initViews() {
        initTabLayout();
        mTvTitleRight = bindViewWithClick(R.id.title_right_text);
        initTabContent();
        initData(true);
    }

    private void initTabLayout() {
        TabLayout tabLayout = bindView(R.id.tabLayout);
        if (mTabCount == 1) {
            tabLayout.setVisibility(View.GONE);
            return;
        }
        tabLayout.addTab(tabLayout.newTab().setText("照片"));
        tabLayout.addTab(tabLayout.newTab().setText("视频"));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mFileType = "照片".equals(tab.getText()) ? FILE_TYPE_IMAGE : FILE_TYPE_VIDEO;
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void initData(boolean refresh) {

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.title_right_text:
                Intent data = new Intent();
                data.putExtra(PARAM_SELECTED_LIST, mSelectedList);
                setResult(RESULT_SELECTED, data);
                finish();
                break;
        }
    }

    @Override
    public void onSelectedListChange(int selectedCount, int maxCount) {
        mTvTitleRight.setText("完成 " + selectedCount + "/" + maxCount);
    }

    public enum SwitchType {
        IMAGE,
        VIDEO,
        THIRD_PARTY,
        IMAGE_AND_VIDEO,
        ALL
    }
}
