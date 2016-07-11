package kasogg.com.imageselector;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

import kasogg.com.imageselector.resourceselect.ResourceSelectActivity;

public class MainActivity extends XLBaseActivity {
    public static final int REQUEST_SELECT_IMAGE = 1001;
    private ArrayList<String> mSelectedList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void initParams() {
    }

    @Override
    protected void initViews() {
        findViewById(R.id.tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ResourceSelectActivity.showThirdParty(MainActivity.this, REQUEST_SELECT_IMAGE, ResourceSelectActivity.SelectType.ALL, mSelectedList, 9, 1, 9, 9, null,
                        "云盘");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SELECT_IMAGE) {
            switch (resultCode) {
                case ResourceSelectActivity.RESULT_SELECTED:
                    mSelectedList = (ArrayList<String>) data.getSerializableExtra(ResourceSelectActivity.PARAM_SELECTED_LIST);
                    Toast.makeText(getApplicationContext(), "大小" + mSelectedList.size() + "张", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
