package com.starway.starrobot.activity.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import com.starway.starrobot.R;
import com.starway.starrobot.sqLite.SQLiteHelper;
import com.starway.starrobot.sqLite.SiteBean;

import java.util.List;

/**
 * @Author:Edgar.Li
 * @Date:2018-04-10 17:14
 * @version:
 * @purpose:
 * @Description:定义siteList的适配器
 */
public class SiteListAdapter extends ArrayAdapter {
    private final LayoutInflater mInflater;
    private int mResource;
    private List<SiteBean> data;
    private TextView name;
    private TextView descride;
    private TextView place;
    private TextView hadUsing;
    private SQLiteHelper sqLiteHelper;
    private View deleteSite;
    private View btnUpdateSite;
    private RadioButton btnStart;

    public SiteListAdapter(Context context, List<SiteBean> data, int resource, SQLiteHelper sqLiteHelper) {
        super(context, resource);
        mInflater = LayoutInflater.from(context);
        mResource = resource;
        this.data = data;
        this.sqLiteHelper = sqLiteHelper;
    }


    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(mResource, parent, false);
        }

        btnStart = convertView.findViewById(R.id.site_start);
        name = convertView.findViewById(R.id.site_name);
        descride = convertView.findViewById(R.id.site_describe);
        place = convertView.findViewById(R.id.site_place);
        hadUsing = convertView.findViewById(R.id.had_using);
        deleteSite = convertView.findViewById(R.id.site_delete);
        btnUpdateSite = convertView.findViewById(R.id.site_update);

        place.setText(data.get(position).getPlace());
        descride.setText(String.valueOf(data.get(position).getDescribe()));
        name.setText(data.get(position).getName());

        hadUsing.setText(data.get(position).getFlag() == -1 ? "[已禁用]" : "");
        btnStart.setChecked(data.get(position).getFlag() == 1);

        deleteSite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteSiteListener.onDeleteClick(position, data.get(position).getId());
            }
        });

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSite.onStartingPointSelected(position, data.get(position));
            }
        });

        btnUpdateSite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateSite.onUpdateSIte(position, data.get(position));
            }
        });
        return convertView;
    }


    @Override
    public int getCount() {
        if (data == null) return 0;
        return data.size();
    }


    /**
     * 删除按钮的监听
     */
    public interface DeleteSiteListener {
        void onDeleteClick(int i, int id);
    }

    private DeleteSiteListener deleteSiteListener;

    public void setDeleteSiteListener(DeleteSiteListener deleteSiteListener) {
        this.deleteSiteListener = deleteSiteListener;
    }


    /**
     * 修改按钮的监听
     */
    public interface UpdateSite {
        void onUpdateSIte(int i, SiteBean siteBean);
    }

    private UpdateSite updateSite;

    public void setUpdateSite(UpdateSite updateSite) {
        this.updateSite = updateSite;
    }


    /**
     * 单选按钮的监听
     */
    public interface StartSite {
        void onStartingPointSelected(int i, SiteBean siteBean);
    }

    private StartSite startSite;

    public void setOnStartingPointSelected(StartSite startSite) {
        this.startSite = startSite;
    }
}
