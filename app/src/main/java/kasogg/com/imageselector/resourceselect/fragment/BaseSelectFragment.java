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
import java.util.Locale;

import kasogg.com.imageselector.R;
import kasogg.com.imageselector.XLBaseFragment;
import kasogg.com.imageselector.resourceselect.adapter.ResourceSelectAdapter;
import kasogg.com.imageselector.resourceselect.constants.ResourceSelectConstants;
import kasogg.com.imageselector.resourceselect.constants.SelectType;
import kasogg.com.imageselector.resourceselect.imagefetcher.ResourceFetcher;
import kasogg.com.imageselector.resourceselect.model.ResourceBucket;
import kasogg.com.imageselector.resourceselect.model.ResourceItem;
import kasogg.com.imageselector.resourceselect.widget.BucketListPopupWindow;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class BaseSelectFragment extends XLBaseFragment implements ResourceSelectAdapter.OnItemClickListener {
    protected OnFragmentInteractionListener mListener;

    protected TextView mTvBucketChoose;
    protected TextView mTvPreview;
    protected BucketListPopupWindow mPopupWindow;
    protected ResourceSelectAdapter mAdapter;
    protected List<ResourceBucket> mBucketList;
    protected List<ResourceItem> mResourceItemList;

    protected ArrayList<ResourceItem> mSelectedList = new ArrayList<>();
    protected int mMaxCount;
    protected int mPageMaxCount;
    protected int mFileType = ResourceSelectConstants.FILE_TYPE_IMAGE;
    protected SelectType mSelectType;
    protected String mToastStr;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initParams();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_resource_select;
    }

    protected void initParams() {
        if (getArguments() != null) {
            mMaxCount = getArguments().getInt(ResourceSelectConstants.PARAM_MAX_COUNT, ResourceSelectConstants.DEFAULT_MAX_COUNT);
            mPageMaxCount = getArguments().getInt(ResourceSelectConstants.PARAM_PAGE_MAX_COUNT, ResourceSelectConstants.DEFAULT_IMAGE_MAX_COUNT);
            mSelectType = (SelectType) getArguments().getSerializable(ResourceSelectConstants.PARAM_SELECT_TYPE);
        }
        mFileType = ResourceSelectConstants.FILE_TYPE_IMAGE;
    }

    @Override
    protected void initViews() {
        mTvBucketChoose = bindViewWithClick(R.id.tv_bucket_choose);
        mTvPreview = bindViewWithClick(R.id.tv_preview);
        RecyclerView recyclerView = bindView(R.id.rv_list_resource_select);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), ResourceSelectConstants.COLUMN_COUNT));
        initAdapter();
        mAdapter.setOnItemClickListener(this);
        recyclerView.setAdapter(mAdapter);
        mTvPreview.setEnabled(mSelectedList.size() > 0);
    }

    protected void initAdapter() {
        mAdapter = new ResourceSelectAdapter(getActivity(), mResourceItemList);
    }

    @Override
    public void bindData() {
        final long startTime = System.currentTimeMillis();
        Observable.from(mBucketList = ResourceFetcher.getBucketList(getActivity(), mFileType)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Action1<ResourceBucket>() {
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

    }

    @Override
    public void onItemClick(ResourceSelectAdapter.ViewHolder viewHolder, int position, ResourceItem item) {
        ArrayList<String> commonSelectedList = mListener.getSelectedList();
        if (!item.isSelected) {
            mSelectedList.remove(item);
            commonSelectedList.remove(item.sourcePath);
        } else if (commonSelectedList.size() < mMaxCount && mSelectedList.size() < mPageMaxCount) {
            mSelectedList.add(item);
            commonSelectedList.add(item.sourcePath);
        } else {
            mAdapter.selectItem(viewHolder, item, false);
            if (mSelectedList.size() >= mPageMaxCount) {
                Toast.makeText(getContext(), mToastStr, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), String.format(Locale.getDefault(), "您最多能选择%d个资源", mMaxCount), Toast.LENGTH_SHORT).show();
            }
        }
        mTvPreview.setEnabled(mSelectedList.size() > 0);
        if (mSelectType == SelectType.IMAGE_AND_VIDEO || mSelectType == SelectType.ALL) {
            mListener.onSelectedListChange(commonSelectedList.size(), mMaxCount);
        } else {
            mListener.onSelectedListChange(mSelectedList.size(), mMaxCount);
        }
    }

    protected void filterSelectedResource() {
        final ArrayList<String> commonSelectedList = mListener.getSelectedList();
        if (commonSelectedList.size() <= 0) {
            return;
        }
        Observable.from(mResourceItemList).filter(new Func1<ResourceItem, Boolean>() {
            @Override
            public Boolean call(ResourceItem resourceItem) {
                return commonSelectedList.contains(resourceItem.sourcePath);
            }
        }).subscribe(new Action1<ResourceItem>() {
            @Override
            public void call(ResourceItem resourceItem) {
                resourceItem.isSelected = true;
                mSelectedList.add(resourceItem);
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

        ArrayList<String> getSelectedList();
    }
}
