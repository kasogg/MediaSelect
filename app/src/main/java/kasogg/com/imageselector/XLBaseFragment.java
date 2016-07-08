package kasogg.com.imageselector;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 提供了fragment的封装后基类
 * 避免了重复的创建View、数据加载操作
 * <p/>
 * 子类的操作步骤
 * 0：获取传值参数 {@link #onCreate(Bundle)}
 * 1：初始化控件 {@link #onViewCreated(View, Bundle)}
 * 2：赋值数据 {@link #bindData()}
 * POWERED BY 陈俊杰 2015-12-22
 */
public abstract class XLBaseFragment extends Fragment implements View.OnClickListener {
    /**
     * 视图对象
     */
    public View rootView;
    /**
     * 加载进度
     */
    private ProgressDialog progressDlg;
    private ExecutorService workerThread = Executors.newFixedThreadPool(10);

    private boolean isNeedReset = false;
    private Unbinder mUnbinder;

    /**
     * 布局文件ID
     */
    protected abstract int getLayoutId();

    /**
     * 初始化布局
     */
    protected abstract void initViews();

    /**
     * 载入数据
     */
    public abstract void bindData();

    /**
     * 与外界元素进行交互
     *
     * @param actionCode
     * @param arg        参数
     */
    public abstract boolean doAction(String actionCode, Object arg);

    @Override
    public final View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(getLayoutId(), container, false);
            mUnbinder = ButterKnife.bind(this, rootView);

            if (isNeedReset) {
                initViews();
            }
        } else {
            mUnbinder = ButterKnife.bind(this, rootView);
        }

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        if (!isNeedReset) {
            rootView = view;
            initViews();
        }
    }

    @Override
    public final void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        bindData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (rootView == null) {
            return;
        }

        ViewGroup mParent = (ViewGroup) rootView.getParent();

        if (mParent == null) {
            return;
        }

        mParent.removeView(rootView);
        mUnbinder.unbind();
    }

    protected <T> T bindView(int id) {
        View view = rootView.findViewById(id);
        return (T) view;
    }

    protected <T> T bindViewWithClick(int id) {
        View view = rootView.findViewById(id);
        if (view != null) {
            view.setOnClickListener(this);
        }
        return (T) view;
    }

    /**
     * Show loading dialog
     *
     * @param sMsg the message to display
     */
    protected void displayLoadingDlg(String sMsg) {

        displayLoadingDlg(sMsg, true, true);
    }

    protected void displayLoadingDlg(String sMsg, boolean cancelable, boolean indeterminate) {
        if (progressDlg != null && progressDlg.isShowing()) {
            progressDlg.setMessage(sMsg);
        } else {
            progressDlg = new ProgressDialog(getActivity());
            progressDlg.setMessage(sMsg);
            progressDlg.setIndeterminate(indeterminate);
            progressDlg.setCancelable(cancelable);
            progressDlg.show();
        }
    }

    /**
     * Show loading dialog
     *
     * @param resId message resId in string.xml to display
     */
    protected void displayLoadingDlg(int resId) {
        displayLoadingDlg(getString(resId));
    }

    /**
     * Dismiss the loading dialog
     */
    protected void dismissLoadingDlg() {
        if (progressDlg != null && progressDlg.isShowing()) {
            progressDlg.cancel();
        }
    }

    /**
     * 不带参数的页面跳转
     *
     * @param targetActivity
     */
    protected void turnToActivity(Class<?> targetActivity) {
        Intent jumpIntent = new Intent(getActivity(), targetActivity);
        jumpIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(jumpIntent);
    }

    /**
     * 带参数的页面跳转
     *
     * @param targetActivity
     * @param pBundle
     */
    protected void turnToActivity(Class<?> targetActivity, Bundle pBundle) {
        Intent intent = new Intent(getActivity(), targetActivity);
        if (pBundle != null) {
            intent.putExtras(pBundle);
        }
        startActivity(intent);
    }

    protected void turnToActivityForResult(Class<?> targetActivity, int requestCode, Intent intent) {
        intent.setClass(getActivity(), targetActivity);
        startActivityForResult(intent, requestCode);
    }

    @SuppressWarnings("unchecked")
    protected <T extends View> T findViewById(View view, int id) {
        return (T) view.findViewById(id);
    }

    public void showToast(final int res) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getContext(), res, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void showToast(final String sMsg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getContext(), sMsg, Toast.LENGTH_SHORT).show();

            }
        });
    }

    protected void hideSoftInput(View view) {
        ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public final void runOnUiThread(Runnable action) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(action);
        }
    }

    public void runOnWorkerThread(Runnable runnable) {
        workerThread.execute(runnable);
    }

    @Override
    public void onClick(View v) {

    }

    public void setIsNeedReset(boolean isNeedReset) {
        this.isNeedReset = isNeedReset;
    }

}
