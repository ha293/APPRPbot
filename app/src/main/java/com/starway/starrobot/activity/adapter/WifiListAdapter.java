package com.starway.starrobot.activity.adapter;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.starway.starrobot.R;

/**
 * @Author:Edgar.Li
 * @Date:2018-04-09 22:01
 * @version:
 * @purpose:
 * @Description:
 */
public class WifiListAdapter extends ArrayAdapter<ScanResult> {
    private final LayoutInflater mInflater;
    private int mResource;

    public WifiListAdapter(Context context, int wifi_list_item) {
        super(context, wifi_list_item);
        mInflater = LayoutInflater.from(context);
        mResource = wifi_list_item;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(mResource, parent, false);
        }
        TextView name = convertView.findViewById(R.id.wifi_name);
        TextView rssi = convertView.findViewById(R.id.wifi_rssi);
        TextView mac = convertView.findViewById(R.id.wifi_mac);

        ScanResult scanResult = getItem(position);
        name.setText(scanResult.SSID);
        mac.setText(scanResult.BSSID);

        int level = scanResult.level;
        if (level <= 0 && level >= -50) {
            rssi.setText("信号良好");
        } else if (level < -50 && level >= -70) {
            rssi.setText("信号较好");
        } else if (level < -70 && level >= -80) {
            rssi.setText("信号一般");
        } else if (level < -80 && level >= -100) {
            rssi.setText("信号较差");
        } else {
            rssi.setText("信号很差");
        }

        return convertView;
    }


}
