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

import java.util.ArrayList;
import java.util.Locale;

import kasogg.com.imageselector.R;
import kasogg.com.imageselector.XLBaseActivity;
import kasogg.com.imageselector.XLBaseFragment;
import kasogg.com.imageselector.resourceselect.fragment.BaseSelectFragment;

/**
 * Created by KasoGG on 2016/6/29.
 */
public class ResourceSelectActivity extends XLBaseActivity implements BaseSelectFragment.OnFragmentInteractionListener {
    public static final int RESULT_SELECTED = 1;
    public static final int FILE_TYPE_IMAGE = 1;
    public static final int FILE_TYPE_VIDEO = 2;

    public static final String PARAM_MAX_COUNT = "PARAM_MAX_COUNT";
    public static final String PARAM_SELECTED_IMAGE_LIST = "PARAM_SELECTED_IMAGE_LIST";
    public static final String PARAM_SELECTED_VIDEO_LIST = "PARAM_SELECTED_VIDEO_LIST";
    public static final String PARAM_SELECTED_RESULT_LIST = "PARAM_SELECTED_RESULT_LIST";
    private static final int DEFAULT_IMAGE_MAX_COUNT = 9;

    private TextView mTvTitleRight;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private FragmentPagerAdapter mPagerAdapter;

    private ArrayList<String> mSelectedResultList = new ArrayList<>();
    private ArrayList<String> mSelectedImageList = new ArrayList<>();
    private int mMaxCount;

    private int mCurrentPosition = 0;
    private String[] mTabArr = new String[]{"图片"};

    public static void show(Activity activity, int requestCode, ArrayList<String> selectedImageList, int maxCount) {
        Intent intent = new Intent(activity, ResourceSelectActivity.class);
        intent.putExtra(PARAM_SELECTED_IMAGE_LIST, selectedImageList);
        intent.putExtra(PARAM_MAX_COUNT, maxCount);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_resource_select);
    }

    @Override
    protected void initParams() {
        mSelectedImageList = (ArrayList<String>) getIntent().getSerializableExtra(PARAM_SELECTED_IMAGE_LIST);
        mMaxCount = getIntent().getIntExtra(PARAM_MAX_COUNT, DEFAULT_IMAGE_MAX_COUNT);
        if (mSelectedImageList == null) {
            mSelectedImageList = new ArrayList<>();
        }
    }

    @Override
    protected void initViews() {
        mTvTitleRight = bindViewWithClick(R.id.title_right_text);
        mTabLayout = bindView(R.id.tabLayout_resource_select);
        mViewPager = bindView(R.id.vp_resource_select);
        mPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return getFragment(position);
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
        mTabLayout.setupWithViewPager(mViewPager);
    }

    private XLBaseFragment getFragment(int position) {
        XLBaseFragment fragment;
        switch (position) {
            case 0:
                fragment = BaseSelectFragment.newInstance(mMaxCount, mSelectedImageList);
                break;
            default:
                fragment = BaseSelectFragment.newInstance(mMaxCount, mSelectedImageList);
                break;
        }
        return fragment;
    }

    private void initData() {

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

    public enum SwitchType {
        IMAGE,
        VIDEO,
        THIRD_PARTY,
        IMAGE_AND_VIDEO,
        ALL
    }
}
