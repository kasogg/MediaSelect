package kasogg.com.imageselector;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by ixciel on 14-10-1.
 */
public abstract class XLBaseActivity extends FragmentActivity implements View.OnClickListener, View.OnTouchListener {
    protected View rootView;
    private ProgressDialog progressDlg;

    protected static void show(Activity activity, int requestCode, Intent intent, Class<?> cls) {
        intent.setClass(activity, cls);
        activity.startActivityForResult(intent, requestCode);
    }

    protected static void show(Fragment fragment, int requestCode, Intent intent, Class<?> cls) {
        intent.setClass(fragment.getActivity(), cls);
        fragment.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        rootView = getWindow().getDecorView().findViewById(android.R.id.content);
    }

    protected abstract void initParams();

    protected abstract void initViews();

    protected void showToast(final int res) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(XLBaseActivity.this, res, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void showToast(final String sMsg) {
        if (TextUtils.isEmpty(sMsg)) {
            return;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(XLBaseActivity.this, sMsg, Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void showToast(final String sMsg, final int duration) {
        if (TextUtils.isEmpty(sMsg)) {
            return;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(XLBaseActivity.this, sMsg, duration).show();
            }
        });
    }

    /**
     * Show loading dialog
     *
     * @param sMsg the message to display
     */
    public void displayLoadingDlg(String sMsg) {
        displayLoadingDlg(sMsg, true, true);
    }

    protected void displayLoadingDlg(String sMsg, boolean cancelable, boolean indeterminate) {
        if (progressDlg != null && progressDlg.isShowing()) {
            progressDlg.setMessage(sMsg);
        } else {
            progressDlg = new ProgressDialog(this);
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
    public void displayLoadingDlg(int resId) {
        displayLoadingDlg(getString(resId));
    }

    /**
     * Dismiss the loading dialog
     */
    public void dismissLoadingDlg() {
        try {
            if (progressDlg != null && progressDlg.isShowing()) {
                progressDlg.cancel();
            }
        } catch (IllegalArgumentException e) {
        }
    }

    protected boolean isViewEmpty(Object object) {
        if (object != null) {
            try {
                Method method = object.getClass().getMethod("getText");
                if (method != null) {
                    String text = (method.invoke(object)).toString();
                    return TextUtils.isEmpty(text);
                }
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        }
        return true;
    }

    protected <T> T bindViewWithClick(int id) {
        View view = findViewById(id);
        if (view != null) {
            view.setOnClickListener(this);
        }
        return (T) view;
    }

    protected <T> T bindView(int id) {
        View view = findViewById(id);
        return (T) view;
    }

    @Override
    public void onClick(View v) {
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }

    public void setContentView(int pageNameId) {
        super.setContentView(pageNameId);
        initParams();
        initViews();
    }

    /**
     * 不带参数的页面跳转
     *
     * @param targetActivity
     */
    protected void jumpTo(Class<? extends Activity> targetActivity) {
        Intent jumpIntent = new Intent(this, targetActivity);
        jumpIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        this.startActivity(jumpIntent);
    }

    /**
     * 带参数的页面跳转
     *
     * @param targetActivity
     * @param pBundle
     */
    protected void jumpTo(Class<?> targetActivity, Bundle pBundle) {
        Intent intent = new Intent(this, targetActivity);
        if (pBundle != null) {
            intent.putExtras(pBundle);
        }
        startActivity(intent);
    }
}
