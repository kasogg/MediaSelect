package kasogg.com.imageselector.resourceselect.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import kasogg.com.imageselector.R;
import kasogg.com.imageselector.resourceselect.ResourceSelectActivity.SelectType;
import kasogg.com.imageselector.resourceselect.adapter.ResourceSelectAdapter;
import kasogg.com.imageselector.resourceselect.adapter.ResourceThirdPartySelectAdapter;
import kasogg.com.imageselector.resourceselect.constants.ResourceSelectConstants;
import kasogg.com.imageselector.resourceselect.model.ResourceItem;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class ThirdPartySelectFragment extends BaseSelectFragment {
    private ArrayList<String> mThirdPartyList;

    public static ThirdPartySelectFragment newInstance(int pageMaxCount, int maxCount, ArrayList<String> thirdPartyList, SelectType selectType) {
        ThirdPartySelectFragment fragment = new ThirdPartySelectFragment();
        Bundle args = new Bundle();
        args.putInt(ResourceSelectConstants.PARAM_MAX_COUNT, maxCount);
        args.putInt(ResourceSelectConstants.PARAM_PAGE_MAX_COUNT, pageMaxCount);
        args.putSerializable(ResourceSelectConstants.PARAM_THIRD_PARTY_LIST, thirdPartyList);
        args.putSerializable(ResourceSelectConstants.PARAM_SELECT_TYPE, selectType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initParams() {
        super.initParams();
        mFileType = ResourceSelectConstants.FILE_TYPE_OTHER;
        if (getArguments() != null) {
            mThirdPartyList = (ArrayList<String>) getArguments().getSerializable(ResourceSelectConstants.PARAM_THIRD_PARTY_LIST);
        }
        if (mThirdPartyList == null) {
            mThirdPartyList = new ArrayList<>();
        }
    }

    @Override
    protected void initAdapter() {
        mAdapter = new ResourceThirdPartySelectAdapter(getActivity(), mResourceItemList);
    }

    @Override
    public void bindData() {
        Observable.from(mThirdPartyList).map(new Func1<String, ResourceItem>() {
            @Override
            public ResourceItem call(String s) {
                ResourceItem item = new ResourceItem();
                item.sourcePath = s;
                return item;
            }
        }).toList().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<List<ResourceItem>>() {
            @Override
            public void call(List<ResourceItem> resourceItems) {
                mResourceItemList = resourceItems;
                filterSelectedResource();
                mAdapter.setAndRefresh(mResourceItemList);
            }
        });
        mTvBucketChoose.setVisibility(View.GONE);
    }

    @Override
    public void onItemClick(ResourceSelectAdapter.ViewHolder viewHolder, int position, ResourceItem item) {
        ArrayList<String> commonSelectedList = mListener.getSelectedList();
        if (!item.isSelected) {
            mSelectedList.remove(item);
            commonSelectedList.remove(item.sourcePath);
        } else if (commonSelectedList.size() < mMaxCount && getSelectedList().size() < mPageMaxCount) {
            mSelectedList.add(item);
            commonSelectedList.add(item.sourcePath);
        } else {
            mAdapter.selectItem(viewHolder, item, false);
            if (commonSelectedList.size() >= mMaxCount) {
                Toast.makeText(getContext(), String.format(Locale.getDefault(), "您最多能选择%d个资源", mMaxCount), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), mToastStr, Toast.LENGTH_SHORT).show();
            }
        }
        mTvPreview.setEnabled(mSelectedList.size() > 0);
        if (mSelectType == SelectType.IMAGE_AND_VIDEO || mSelectType == SelectType.ALL) {
            mListener.onSelectedListChange(commonSelectedList.size(), mMaxCount);
        } else {
            mListener.onSelectedListChange(getSelectedList().size(), mMaxCount);
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tv_preview:
                //TODO 打开预览资源
                break;
        }
    }

}
