package com.starway.starrobot.activity.backstage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;

import com.starway.starrobot.R;
import com.starway.starrobot.utils.SiteHelper;
import com.starway.starrobot.sqLite.SiteBean;
import com.starway.starrobot.utils.Common;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author:Edgar.Li
 * @Date:2018-04-11 9:36
 * @version:
 * @purpose:
 * @Description:
 */
public class SiteEditActivity extends Activity implements View.OnClickListener {

    private Spinner spinner;//下拉选择框

    private ArrayAdapter<String> arr_adapter;//下拉选择框的适配器
    private List<String> data_list = new ArrayList<>();//数据

    private EditText insertName;
    private EditText insertDescribe;
    private Button btnAddLite;
    private Button btnCancel;
    private View laodView;
    private View editView;
    private SiteHelper siteHelper = new SiteHelper();
    private SiteBean siteBean = new SiteBean();
    private RadioButton site_startUsing;
    private RadioButton site_forbidden;
    int flag = 0;

    String name;
    String place;
    String describe;
    int type;
    SiteBean getSiteBean;
    int reloadCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_backstage_addsite);
        initview();

        reloadCount = 0;
        Intent intent = getIntent();
        type = intent.getIntExtra("type", 0);
        if (type == UPDATE_REQUESTCODE) {
            getSiteBean = (SiteBean) intent.getSerializableExtra("siteBean");

        }
    }

    private void initview() {
        spinner = findViewById(R.id.insert_place);
        laodView = findViewById(R.id.siteLoad);
        editView = findViewById(R.id.siteEdit);
        btnAddLite = findViewById(R.id.insert_confirm);
        btnCancel = findViewById(R.id.insert_cancel);
        insertDescribe = findViewById(R.id.insert_describe);
        insertName = findViewById(R.id.insert_name);

        site_forbidden = findViewById(R.id.site_forbidden);
        site_startUsing = findViewById(R.id.site_startUsing);

        final InputMethodManager inputMethodManager = (InputMethodManager) SiteEditActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
        spinner.setOnTouchListener(new View.OnTouchListener() { //点击下拉框是隐藏输入法
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
                return false;
            }
        });

        btnAddLite.setOnClickListener(this);

        btnCancel.setOnClickListener(this);

        siteHelper.setOnSiteLoadedListener(new SiteHelper.OnSiteLoadedListener() {
            @Override
            public void onLoaded(boolean result, List<String> sites) {
                if (result) {
                    data_list.addAll(sites);
                    arr_adapter.notifyDataSetChanged();
                    laodView.setVisibility(View.INVISIBLE);
                    editView.setVisibility(View.VISIBLE);
                    try {

                        showSite();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    reloadCount++;
                    if (reloadCount < 5) {
                        siteHelper.reload();
                        Log.e("addSite", "获取地点，重新加载");
//                        Common.showToast(SiteEditActivity.this, "获取失败，重新加载");
                    }
                }
            }
        });


        //适配器
        arr_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data_list);
        //设置样式
        arr_adapter.setDropDownViewResource(android.R.layout.simple_list_item_checked);
        //加载适配器
        spinner.setAdapter(arr_adapter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.insert_confirm:
                if (type == UPDATE_REQUESTCODE) {
                    updateSite();
                } else {
                    addSite();
                }
                break;
            case R.id.insert_cancel:
                if (type == UPDATE_REQUESTCODE) {
                    setResult(UPDATE_RESULTCODE);
                } else {
                    setResult(INSERT_RESULTCODE);
                }
                finish();
                break;
        }
    }


    private void addSite() {
        /**
         * @Author:Edgar.Li
         * @Date:2018-04-11 14:49
         * @Description:获得的插入数据
         * @return:void
         *
         */
        name = insertName.getText().toString();
        place = (String) spinner.getSelectedItem();
        describe = insertDescribe.getText().toString();
        if (site_forbidden.isChecked()) {
            flag = -1;
        } else {
            flag = 0;
        }
        if (!name.isEmpty()) {
            if (!describe.isEmpty()) {
                siteBean.setDescribe(describe)
                        .setName(name)
                        .setPlace(place)
                        .setFlag(flag);
                Intent intent = new Intent();
                intent.putExtra("siteBean", siteBean);
                setResult(INSERT_REQUESTCODE, intent);
                finish();
            } else {
                Common.showToast(this, "信息不完整！");
            }
        } else {
            Common.showToast(this, "信息不完整！");
        }
    }

    private void showSite() {
        /**
         * @Author:Edgar.Li
         * @Date:2018-04-12 9:38
         * @Description:显示数据
         * @return:void
         *
         */
        if (getSiteBean != null) {
            insertDescribe.setText(getSiteBean.getDescribe());
            insertName.setText(getSiteBean.getName());
            int x = data_list.indexOf(getSiteBean.getPlace());
            spinner.setSelection(x > -1 ? x : 0, true);
            if (getSiteBean.getFlag() == -1) {
                site_forbidden.setChecked(true);
            } else if (getSiteBean.getFlag() == 1) {
                site_forbidden.setEnabled(false);
            }
            btnAddLite.setText("修改");
            btnCancel.setText("取消");
        }
    }

    private void updateSite() {
        /**
         * @Author:Edgar.Li
         * @Date:2018-04-12 10:42
         * @Description:
         * @return:void
         *
         */
        name = insertName.getText().toString();
        place = (String) spinner.getSelectedItem();
        describe = insertDescribe.getText().toString();

        if (site_forbidden.isChecked()) {
            flag = -1;
        } else {
            flag = getSiteBean.getFlag();
        }
        if (!name.isEmpty()) {
            if (!describe.isEmpty()) {
                getSiteBean.setDescribe(describe)
                        .setName(name)
                        .setPlace(place)
                        .setFlag(flag);
                Intent intent = new Intent();
                intent.putExtra("siteBean", getSiteBean);
                setResult(UPDATE_REQUESTCODE, intent);
                finish();
            } else {
                Common.showToast(this, "信息不完整！");
            }
        } else {
            Common.showToast(this, "信息不完整！");
        }
    }

    /**
     * 插入结果代码
     */
    public static final int INSERT_RESULTCODE = -1;
    /**
     * 插入请求代码
     */
    public static final int INSERT_REQUESTCODE = 1;

    /**
     * 修改请求代码
     */
    public static final int UPDATE_REQUESTCODE = 2;
    /**
     * 修改结果代码
     */
    public static final int UPDATE_RESULTCODE = -2;
}
