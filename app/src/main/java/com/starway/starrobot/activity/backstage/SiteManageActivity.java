package com.starway.starrobot.activity.backstage;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ListView;


import com.starway.starrobot.R;
import com.starway.starrobot.activity.BaseActivity;
import com.starway.starrobot.activity.adapter.SiteListAdapter;
import com.starway.starrobot.sqLite.SQLiteHelper;
import com.starway.starrobot.sqLite.SiteBean;
import com.starway.starrobot.utils.Common;

import java.util.List;

import static com.starway.starrobot.activity.backstage.SiteEditActivity.*;


/**
 * @Author:Edgar.Li
 * @Date:2018-04-10 15:14
 * @version:
 * @purpose:
 * @Description:
 */
public class SiteManageActivity extends BaseActivity {

    private ListView listView;
    private SiteListAdapter siteListAdapter;

    private SQLiteHelper sqLiteHelper;
    List<SiteBean> siteBeans;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backstage_sitemanage);


        sqLiteHelper = SQLiteHelper.getSqLiteHelper(this);
        initView();

    }

    private void initView() {

        setTitle(R.string.setting_site);
        setFuncBtnIcon(ICON_ADD);
        showFuncButton(true);
        setOnFuncBtnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSite();
            }
        });

        listView = findViewById(R.id.site_list);

        siteBeans = sqLiteHelper.queryAllSiteBean(SQLiteHelper.QUERY_ALL);
        siteListAdapter = new SiteListAdapter(this, siteBeans, R.layout.item_backstage_site, sqLiteHelper);
        listView.setAdapter(siteListAdapter);


        //删除地点
        siteListAdapter.setDeleteSiteListener(new SiteListAdapter.DeleteSiteListener() {
            @Override
            public void onDeleteClick(final int i, final int id) {
                if (siteBeans.get(i).getFlag() != 1) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SiteManageActivity.this)
                            .setMessage("确定删除该地点信息吗？")
                            .setNegativeButton("取消", null)
                            .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int which) {
                                    siteBeans.remove(i);
                                    sqLiteHelper.deleteSiteBean(id);
                                    siteListAdapter.notifyDataSetInvalidated();
                                }
                            });
                    builder.show();
                } else {
                    Common.showToast(SiteManageActivity.this, "禁止删除起点！");
                }
            }
        });

        //修改
        siteListAdapter.setUpdateSite(new SiteListAdapter.UpdateSite() {
            @Override
            public void onUpdateSIte(int i, SiteBean siteBean) {
                updateSite(siteBean);
            }
        });

        //设置起点坐标
        siteListAdapter.setOnStartingPointSelected(new SiteListAdapter.StartSite() {
            @Override
            public void onStartingPointSelected(int i, final SiteBean siteBean) {
                if (siteBean.getFlag() != 1) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SiteManageActivity.this)
                            .setMessage("确定设置当前点为起点吗？")
                            .setPositiveButton("设置", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    for (SiteBean s : siteBeans) {
                                        if (s.getFlag() == 1) {
                                            s.setFlag(0);
                                            sqLiteHelper.updateSiteBean(s);
                                        }
                                        if (s.getId() == siteBean.getId()) {
                                            siteBean.setFlag(1);
                                            sqLiteHelper.updateSiteBean(siteBean);
                                        }
                                    }
                                    siteListAdapter.notifyDataSetInvalidated();
                                }
                            }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    siteListAdapter.notifyDataSetInvalidated();
                                }
                            });

                    builder.show();
                }
            }
        });
    }

    private void addSite() {
        Intent intent = new Intent(SiteManageActivity.this, SiteEditActivity.class);
        intent.putExtra("type", INSERT_REQUESTCODE);
//        Common.showToast(this, String.valueOf(intent.getIntExtra("type",0)));
        startActivityForResult(intent, INSERT_REQUESTCODE);
    }

    private void updateSite(SiteBean siteBean) {
        /**
         * @Author:Edgar.Li
         * @param siteBean
         * @Date:2018-04-12 10:08
         * @Description: 修改数据
         * @return:void
         *
         */
        Intent intent = new Intent(SiteManageActivity.this, SiteEditActivity.class);
        intent.putExtra("type", UPDATE_REQUESTCODE);
        intent.putExtra("siteBean", siteBean);
        startActivityForResult(intent, UPDATE_REQUESTCODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case INSERT_REQUESTCODE:
                if (resultCode == INSERT_REQUESTCODE) {
                    if (data != null) {
                        SiteBean s = (SiteBean) data.getSerializableExtra("siteBean");
                        sqLiteHelper.insertSiteBean(s);
                        siteBeans.add(s);
                        siteListAdapter.notifyDataSetInvalidated();
                        Common.showToast(this, "添加成功!");
                    }
                }
                break;
            case UPDATE_REQUESTCODE:
                if (resultCode == UPDATE_REQUESTCODE) {
                    SiteBean s = (SiteBean) data.getSerializableExtra("siteBean");
                    sqLiteHelper.updateSiteBean(s);

                    for (int i = 0; i < siteBeans.size(); i++) {
                        if (siteBeans.get(i).getId() == s.getId()) {
                            siteBeans.set(i, s);
                        }
                    }
                    siteListAdapter.notifyDataSetInvalidated();
                    Common.showToast(this, "修改成功");

                }
        }
    }
}
