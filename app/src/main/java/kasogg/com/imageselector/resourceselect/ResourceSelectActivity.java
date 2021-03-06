package kasogg.com.imageselector.resourceselect;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

import kasogg.com.imageselector.R;
import kasogg.com.imageselector.XLBaseActivity;
import kasogg.com.imageselector.XLBaseFragment;
import kasogg.com.imageselector.resourceselect.constants.ResourceSelectConstants;
import kasogg.com.imageselector.resourceselect.constants.SelectType;
import kasogg.com.imageselector.resourceselect.fragment.BaseSelectFragment;
import kasogg.com.imageselector.resourceselect.fragment.ImageSelectFragment;
import kasogg.com.imageselector.resourceselect.fragment.ThirdPartySelectFragment;
import kasogg.com.imageselector.resourceselect.fragment.VideoSelectFragment;

public class ResourceSelectActivity extends XLBaseActivity implements BaseSelectFragment.OnFragmentInteractionListener {
    public static final int RESULT_SELECTED = 1;

    public static final String PARAM_SELECTED_LIST = "PARAM_SELECTED_LIST";

    private TextView mTvTitleRight;
    private ViewPager mViewPager;
    private FragmentStatePagerAdapter mPagerAdapter;

    private ArrayList<String> mSelectedList;
    private ArrayList<String> mThirdPartyList;
    private int mMaxCount;
    private int mImageMaxCount;
    private int mVideoMaxCount;
    private int mThirdPartyMaxCount;

    private int mCurrentPosition = 0;
    private String[] mTabArr;
    private SelectType mSelectType;

    public static void showImageSelect(Activity activity, int requestCode, ArrayList<String> selectedList, int maxCount) {
        show(activity, requestCode, SelectType.IMAGE, selectedList, maxCount, maxCount, maxCount, maxCount, null, null);
    }

    public static void showVideoSelect(Activity activity, int requestCode, ArrayList<String> selectedList, int maxCount) {
        show(activity, requestCode, SelectType.VIDEO, selectedList, maxCount, maxCount, maxCount, maxCount, null, null);
    }

    public static void showImageAndVideoSelect(Activity activity, int requestCode, ArrayList<String> selectedList, int imageMaxCount, int videoMaxCount, int
            totalMaxCount) {
        show(activity, requestCode, SelectType.IMAGE_AND_VIDEO, selectedList, imageMaxCount, videoMaxCount, totalMaxCount, totalMaxCount, null, null);
    }

    public static void show(final Activity activity, final int requestCode, final SelectType selectType, final ArrayList<String> selectedList, final int imageMaxCount,
                            final int videoMaxCount, final int thirdPartyMaxCount, final int totalMaxCount, final ArrayList<String> thirdPartyList, final String
                                    thirdPatryTabName) {
        Intent intent = new Intent(activity, ResourceSelectActivity.class);
        intent.putExtra(ResourceSelectConstants.PARAM_MAX_COUNT, totalMaxCount);
        intent.putExtra(ResourceSelectConstants.PARAM_IMAGE_MAX_COUNT, imageMaxCount);
        intent.putExtra(ResourceSelectConstants.PARAM_VIDEO_MAX_COUNT, videoMaxCount);
        intent.putExtra(ResourceSelectConstants.PARAM_THIRD_PARTY_MAX_COUNT, thirdPartyMaxCount);
        intent.putExtra(ResourceSelectConstants.PARAM_SELECT_TYPE, selectType);
        intent.putExtra(PARAM_SELECTED_LIST, selectedList);
        intent.putExtra(ResourceSelectConstants.PARAM_THIRD_PARTY_LIST, thirdPartyList);
        intent.putExtra(ResourceSelectConstants.PARAM_THIRD_PARTY_TAB, thirdPatryTabName);
        activity.startActivityForResult(intent, requestCode);

    }

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_resource_select);
    }

    @Override
    protected void initParams() {
        mSelectType = (SelectType) getIntent().getSerializableExtra(ResourceSelectConstants.PARAM_SELECT_TYPE);
        mMaxCount = getIntent().getIntExtra(ResourceSelectConstants.PARAM_MAX_COUNT, ResourceSelectConstants.DEFAULT_MAX_COUNT);
        mImageMaxCount = getIntent().getIntExtra(ResourceSelectConstants.PARAM_IMAGE_MAX_COUNT, ResourceSelectConstants.DEFAULT_IMAGE_MAX_COUNT);
        mVideoMaxCount = getIntent().getIntExtra(ResourceSelectConstants.PARAM_VIDEO_MAX_COUNT, ResourceSelectConstants.DEFAULT_VIDEO_MAX_COUNT);
        mThirdPartyMaxCount = getIntent().getIntExtra(ResourceSelectConstants.PARAM_THIRD_PARTY_MAX_COUNT, ResourceSelectConstants.DEFAULT_MAX_COUNT);
        mSelectedList = (ArrayList<String>) getIntent().getSerializableExtra(PARAM_SELECTED_LIST);
        mThirdPartyList = (ArrayList<String>) getIntent().getSerializableExtra(ResourceSelectConstants.PARAM_THIRD_PARTY_LIST);
        if (mSelectedList == null) {
            mSelectedList = new ArrayList<>();
        }
        if (mThirdPartyList == null) {
            mThirdPartyList = new ArrayList<>();
        }
        String thirdPartyTabName = getIntent().getStringExtra(ResourceSelectConstants.PARAM_THIRD_PARTY_TAB);
        switch (mSelectType) {
            case IMAGE:
                mTabArr = new String[]{"图片"};
                break;
            case VIDEO:
                mTabArr = new String[]{"视频"};
                break;
            case THIRD_PARTY:
                mTabArr = new String[]{thirdPartyTabName};
                break;
            case IMAGE_AND_VIDEO:
                mTabArr = new String[]{"图片", "视频"};
                break;
            case ALL:
                mTabArr = new String[]{"图片", "视频", thirdPartyTabName};
                break;
        }
    }

    @Override
    protected void initViews() {
        mTvTitleRight = bindViewWithClick(R.id.title_right_text);
        TabLayout tabLayout = bindView(R.id.tabLayout_resource_select);
        if (mTabArr.length == 1) {
            tabLayout.setVisibility(View.GONE);
        }
        mViewPager = bindView(R.id.vp_resource_select);
        mPagerAdapter = new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return getFragment(position, mSelectType);
            }

            @Override
            public int getCount() {
                return mTabArr.length;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return mTabArr[position];
            }
        };
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                mCurrentPosition = position;
                onSelectedListChange(mSelectedList.size(), mMaxCount);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        if (mCurrentPosition != 0) {
            mViewPager.setCurrentItem(mCurrentPosition);
        }
        tabLayout.setupWithViewPager(mViewPager);
    }

    private XLBaseFragment getFragment(int position, SelectType type) {
        XLBaseFragment fragment;
        if (position == 0 && type == SelectType.VIDEO || position == 1) {
            fragment = VideoSelectFragment.newInstance(mVideoMaxCount, mMaxCount, mSelectType);
        } else if (position == 0 && type == SelectType.THIRD_PARTY || position == 2) {
            fragment = ThirdPartySelectFragment.newInstance(mThirdPartyMaxCount, mMaxCount, mThirdPartyList, mSelectType);
        } else {
            fragment = ImageSelectFragment.newInstance(mImageMaxCount, mMaxCount, mSelectType);
        }
        return fragment;
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

    private BaseSelectFragment getCurrentFragment() {
        return (BaseSelectFragment) mPagerAdapter.instantiateItem(mViewPager, mCurrentPosition);
    }

    @Override
    public void onSelectedListChange(int selectedCount, int maxCount) {
        mTvTitleRight.setText(String.format(Locale.getDefault(), "完成%d/%d", selectedCount, maxCount));
    }

    @Override
    public ArrayList<String> getSelectedList() {
        return mSelectedList;
    }

}
