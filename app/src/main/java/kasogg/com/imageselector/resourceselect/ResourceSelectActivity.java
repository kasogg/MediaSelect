package kasogg.com.imageselector.resourceselect;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Locale;

import kasogg.com.imageselector.R;
import kasogg.com.imageselector.XLBaseActivity;
import kasogg.com.imageselector.XLBaseFragment;
import kasogg.com.imageselector.resourceselect.fragment.BaseSelectFragment;
import kasogg.com.imageselector.resourceselect.fragment.ImageSelectFragment;
import kasogg.com.imageselector.resourceselect.fragment.VideoSelectFragment;

public class ResourceSelectActivity extends XLBaseActivity implements BaseSelectFragment.OnFragmentInteractionListener {
    public static final int RESULT_SELECTED = 1;
    public static final int FILE_TYPE_IMAGE = 1;
    public static final int FILE_TYPE_VIDEO = 2;

    public static final String PARAM_SELECTED_LIST = "PARAM_SELECTED_LIST";
    public static final String PARAM_SELECTED_RESULT_LIST = "PARAM_SELECTED_RESULT_LIST";
    public static final String PARAM_SWITCH_TYPE = "PARAM_SWITCH_TYPE";
    public static final String PARAM_MAX_COUNT = "PARAM_MAX_COUNT";
    private static final int DEFAULT_IMAGE_MAX_COUNT = 9;

    private TextView mTvTitleRight;
    private ViewPager mViewPager;
    private FragmentPagerAdapter mPagerAdapter;

    private ArrayList<String> mSelectedResultList = new ArrayList<>();
    private ArrayList<String> mSelectedImageList = new ArrayList<>();
    private int mMaxCount;

    private int mCurrentPosition = 0;
    private String[] mTabArr;
    private SwitchType mSwitchType;

    public static void show(Activity activity, int requestCode, SwitchType switchType, ArrayList<String> selectedList, int maxCount) {
        Intent intent = new Intent(activity, ResourceSelectActivity.class);
        intent.putExtra(PARAM_SELECTED_LIST, selectedList);
        intent.putExtra(PARAM_MAX_COUNT, maxCount);
        intent.putExtra(PARAM_SWITCH_TYPE, switchType);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_resource_select);
    }

    @Override
    protected void initParams() {
        mSwitchType = (SwitchType) getIntent().getSerializableExtra(PARAM_SWITCH_TYPE);
        mMaxCount = getIntent().getIntExtra(PARAM_MAX_COUNT, DEFAULT_IMAGE_MAX_COUNT);
        mSelectedImageList = (ArrayList<String>) getIntent().getSerializableExtra(PARAM_SELECTED_LIST);
        if (mSelectedImageList == null) {
            mSelectedImageList = new ArrayList<>();
        }
        switch (mSwitchType) {
            case IMAGE:
                mTabArr = new String[]{"图片"};
                break;
            case VIDEO:
                mTabArr = new String[]{"视频"};
                break;
            case THIRD_PARTY:
                mTabArr = new String[]{"云盘"};
                break;
            case IMAGE_AND_VIDEO:
                mTabArr = new String[]{"图片", "视频"};
                break;
            case ALL:
                mTabArr = new String[]{"图片", "视频", "云盘"};
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
        mPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return getFragment(position, mSwitchType);
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

    private XLBaseFragment getFragment(int position, SwitchType type) {
        XLBaseFragment fragment;
        switch (position) {
            case 0:
                switch (type) {
                    case IMAGE:
                        fragment = ImageSelectFragment.newInstance(mMaxCount, mSelectedImageList);
                        break;
                    case VIDEO:
                        fragment = VideoSelectFragment.newInstance(mMaxCount, mSelectedImageList);
                        break;
                    case THIRD_PARTY:
                        //TODO 第三方
                        fragment = null;
                        break;
                    default:
                        fragment = ImageSelectFragment.newInstance(mMaxCount, mSelectedImageList);
                        break;
                }
                break;
            case 1:
                fragment = VideoSelectFragment.newInstance(mMaxCount, mSelectedImageList);
                break;
            case 2:
                //TODO 第三方
            default:
                fragment = ImageSelectFragment.newInstance(mMaxCount, mSelectedImageList);
                break;
        }
        return fragment;
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.title_right_text:
                Intent data = new Intent();
                mSelectedResultList.addAll(getCurrentFragment().getSelectedList());
                data.putExtra(PARAM_SELECTED_RESULT_LIST, mSelectedResultList);
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

    public enum SwitchType implements Serializable {
        IMAGE,
        VIDEO,
        THIRD_PARTY,
        IMAGE_AND_VIDEO,
        ALL
    }
}
