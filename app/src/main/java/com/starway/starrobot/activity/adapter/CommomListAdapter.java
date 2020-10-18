package com.starway.starrobot.activity.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.starway.starrobot.R;
import com.starway.starrobot.bean.CommonListItem;

import java.util.ArrayList;

/**
 * Created by iBelieve on 2018/4/17.
 */

public class CommomListAdapter extends ArrayAdapter {

    private LayoutInflater mInflater;
    private Context mContext;
    private int mResource;
    private ArrayList<CommonListItem> mList;

    public CommomListAdapter(@NonNull Context context, int resource, ArrayList<CommonListItem> dat) {
        super(context, resource);
        intit(context, resource, dat);
    }

    public void intit(Context context, int resource, ArrayList<CommonListItem> list) {
        mInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.mResource = resource;
        this.mList = list;
    }

    @Override
    public int getCount() {
        if (mList == null) {
            return 0;
        }
        return mList.size();
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(mResource, parent, false);
            holder.number = (TextView) convertView.findViewById(R.id.number);
            holder.title = (TextView) convertView.findViewById(R.id.title);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final CommonListItem data = mList.get(position);

        holder.number.setText(String.valueOf(position+1)+".");
        holder.title.setText(data.getTitle());

        return convertView;
    }

    public final class ViewHolder {
        public TextView number;
        public TextView title;
    }

}
