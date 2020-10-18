package com.starway.starrobot.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.starway.starrobot.R;
import com.starway.starrobot.activity.BaseActivity;
import com.starway.starrobot.activity.adapter.CommomListAdapter;
import com.starway.starrobot.activity.controller.CountdownController;
import com.starway.starrobot.bean.CommonListItem;
import com.starway.starrobot.sqLite.SQLiteHelper;
import com.starway.starrobot.sqLite.SiteBean;
import com.starway.starrobot.utils.Common;

import java.util.ArrayList;
import java.util.List;

public class GuideFragment extends Fragment implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    private OnSiteItemClickListener listener;

    private SQLiteHelper sqLiteHelper;
    private BaseActivity baseActivity;
    private ArrayList<CommonListItem> list = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_guide, container, false);

        baseActivity = (BaseActivity) getActivity();
        sqLiteHelper = SQLiteHelper.getSqLiteHelper(getContext());
        ListView listView = view.findViewById(R.id.listView);

        //读取以保存的地点数据
        List<SiteBean> siteBean= sqLiteHelper.queryAllSiteBean(SQLiteHelper.QUERY_AVAILABLE);
        for (SiteBean site : siteBean) {
            list.add(new CommonListItem()
                    .setTitle(site.getName())
                    .setObject(site)
            );
        }

        CommomListAdapter adapter = new CommomListAdapter(getContext(), R.layout.item_common, list);
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);
        listView.setAdapter(adapter);

        return view;
    }

    /**
     * 设置列表项点击回调事件
     *
     */
    public GuideFragment setListener(OnSiteItemClickListener listener) {
        this.listener = listener;
        return this;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (listener != null) {
            listener.onItemClick((SiteBean) list.get(position).getObject());
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        if (listener != null) {
            listener.onItemLongClick((SiteBean) list.get(position).getObject());
        }
        return true;
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            baseActivity.setTitle(R.string.menu_space);
        }
    }

    /**
     * 地点列表项点击回调
     */
    public interface OnSiteItemClickListener {
        void onItemClick(SiteBean site);
        void onItemLongClick(SiteBean site);
    }
}
