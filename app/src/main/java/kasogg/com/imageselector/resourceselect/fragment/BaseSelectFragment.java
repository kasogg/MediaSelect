package kasogg.com.imageselector.resourceselect.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import kasogg.com.imageselector.R;
import kasogg.com.imageselector.XLBaseFragment;
import kasogg.com.imageselector.resourceselect.ResourceSelectActivity;
import kasogg.com.imageselector.resourceselect.adapter.ResourceSelectAdapter;
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
 * Created by KasoGG on 2016/7/8.
 */
public class BaseSelectFragment extends XLBaseFragment implements ResourceSelectAdapter.OnItemClickListener {
    public static final String PARAM_SELECTED_LIST = "PARAM_SELECTED_LIST";
    public static final String PARAM_MAX_COUNT = "PARAM_IMAGE_MAX_COUNT";
    protected static final int DEFAULT_MAX_COUNT = 9;
    protected static final int COLUMN_COUNT = 4;

    protected OnFragmentInteractionListener mListener;

    protected TextView mTvBucketChoose;
    protected TextView mTvPreview;
    protected BucketListPopupWindow mPopupWindow;
    protected ResourceSelectAdapter mAdapter;
    protected List<ResourceBucket> mBucketList;
    protected List<ResourceItem> mResourceItemList;

    protected ArrayList<String> mSelectedList = new ArrayList<>();
    protected int mMaxCount;
    protected int mFileType = ResourceSelectActivity.FILE_TYPE_IMAGE;

    public static BaseSelectFragment newInstance(int maxCount, ArrayList<String> selectedList) {
        BaseSelectFragment fragment = new BaseSelectFragment();
        Bundle args = new Bundle();
        args.putInt(PARAM_MAX_COUNT, maxCount);
        args.putSerializable(PARAM_SELECTED_LIST, selectedList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initParams();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_image_select;
    }

    protected void initParams() {
        if (getArguments() != null) {
            mMaxCount = getArguments().getInt(PARAM_MAX_COUNT, DEFAULT_MAX_COUNT);
            mSelectedList = (ArrayList<String>) getArguments().getSerializable(PARAM_SELECTED_LIST);
        }
        if (mSelectedList == null) {
            mSelectedList = new ArrayList<>();
        }
        mFileType = ResourceSelectActivity.FILE_TYPE_IMAGE;
    }

    @Override
    protected void initViews() {
        mTvBucketChoose = bindViewWithClick(R.id.tv_bucket_choose);
        mTvPreview = bindViewWithClick(R.id.tv_preview);
        RecyclerView recyclerView = bindView(R.id.rv_list_resource_select);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), COLUMN_COUNT));
        mAdapter = new ResourceSelectAdapter(getActivity(), mResourceItemList);
        mAdapter.setOnItemClickListener(this);
        recyclerView.setAdapter(mAdapter);
        initTabContent();
        initData(true);
    }

    @Override
    public void bindData() {
        final long startTime = System.currentTimeMillis();
        Observable.from(mBucketList = ResourceFetcher.getBucketList(getActivity(), true, mFileType)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers
                .mainThread()).doOnNext(new Action1<ResourceBucket>() {
            @Override
            public void call(ResourceBucket resourceBucket) {
                mPopupWindow = new BucketListPopupWindow(getActivity(), (int) (getActivity().getWindowManager().getDefaultDisplay().getHeight() * 0.65), mBucketList,
                        mFileType);
                mPopupWindow.setBucketSelectedListener(new BucketListPopupWindow.BucketSelectedListener() {
                    @Override
                    public void onBucketSelected(ResourceBucket bucket) {
                        // 因为第一次加载是所有的图片的bucket，跟其它bucket里的图片指向同一地址，所以不用再次筛选
                        // mResourceItemList = bucket.imageList;
                        // filterSelectedResource();
                        mAdapter.setAndRefresh(bucket.imageList);
                        mTvBucketChoose.setText(bucket.bucketName);
                    }
                });
            }
        }).first().subscribe(new Action1<ResourceBucket>() {
            @Override
            public void call(ResourceBucket resourceBucket) {
                if (resourceBucket != null) {
                    mResourceItemList = resourceBucket.imageList;
                    filterSelectedResource();
                    mAdapter.setAndRefresh(mResourceItemList);
                    mTvBucketChoose.setText(resourceBucket.bucketName);
                }
                long endTime = System.currentTimeMillis();
                Log.i("TIME", "use time: " + (endTime - startTime) + " ms");
            }
        });
    }

    @Override
    public boolean doAction(String actionCode, Object arg) {
        return false;
    }

    @Override
    public void onAddClick(ResourceSelectAdapter.ViewHolder viewHolder) {
        //TODO 拍照 视频
        Toast.makeText(getContext(), "拍照", Toast.LENGTH_SHORT).show();
        Log.d("asd", "TAKE");
    }

    @Override
    public void onItemClick(ResourceSelectAdapter.ViewHolder viewHolder, int position, ResourceItem item) {
        if (!item.isSelected) {
            mSelectedList.remove(item.sourcePath);
        } else if (mSelectedList.size() < mMaxCount) {
            mSelectedList.add(item.sourcePath);
        } else {
            mAdapter.selectItem(viewHolder, item, false);
            Toast.makeText(getContext(), "您最多能选择" + mMaxCount + "张图片", Toast.LENGTH_SHORT).show();
        }
        int selectedCount = mSelectedList.size();
        mTvPreview.setEnabled(selectedCount > 0);
        mListener.onSelectedListChange(selectedCount, mMaxCount);
    }

    private void initTabContent() {
        int selectedCount = mSelectedList.size();
        mTvPreview.setEnabled(selectedCount > 0);
        mListener.onSelectedListChange(selectedCount, mMaxCount);
    }

    private void initData(boolean refresh) {

    }

    private void filterSelectedResource() {
        if (mSelectedList.size() <= 0) {
            return;
        }
        Observable.from(mResourceItemList).filter(new Func1<ResourceItem, Boolean>() {
            @Override
            public Boolean call(ResourceItem resourceItem) {
                return mSelectedList.contains(resourceItem.sourcePath);
            }
        }).subscribe(new Action1<ResourceItem>() {
            @Override
            public void call(ResourceItem resourceItem) {
                resourceItem.isSelected = true;
            }
        });
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tv_bucket_choose:
                if (mPopupWindow != null) {
                    mPopupWindow.showAtLocation(mTvBucketChoose);
                }
                break;
            case R.id.tv_preview:
                //TODO 打开预览图片
                break;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onSelectedListChange(int selectedCount, int maxCount);
    }
}