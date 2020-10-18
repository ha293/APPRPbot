package com.starway.starrobot.activity.backstage;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.starway.starrobot.R;
import com.starway.starrobot.activity.BaseActivity;
import com.starway.starrobot.activity.adapter.WifiListAdapter;
import com.starway.starrobot.service.NetworkService;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class WiFiManageActivity extends BaseActivity {

    private ListView listView;
    private View loadingView;
    private WifiManager wifiManager;
    private TextView wifiState;
    private WifiListAdapter wifiListAdapter;
    private WifiConfiguration config;

    private static final int WIFICIPHER_NOPASS = 1;
    private static final int WIFICIPHER_WEP = 2;
    private static final int WIFICIPHER_WPA = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backstage_wifi);

        initView();
        initBroadcastReceiver();
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sendBroadcast(new Intent().setAction(NetworkService.CHECK_NETWORK));
    }

    private void initView() {
        showFuncButton(true);
        setFuncBtnIcon(ICON_RELOAD);
        setTitle(R.string.setting_wifi);
        setSubtitle("WIFI未连接");

        setOnFuncBtnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoadingView(true);
                search();
            }
        });

        loadingView = findViewById(R.id.loading);
        wifiState = findViewById(R.id.wifi_state);
        listView = findViewById(R.id.WiFi_list);

        wifiListAdapter = new WifiListAdapter(this, R.layout.item_backstage_wifi);
        listView.setAdapter(wifiListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final ScanResult scanResult = wifiListAdapter.getItem(position);
                String capabilities = scanResult.capabilities;
                int type = WIFICIPHER_WPA;
                if (!TextUtils.isEmpty(capabilities)) {
                    if (capabilities.contains("WPA") || capabilities.contains("wpa")) {
                        type = WIFICIPHER_WPA;
                    } else if (capabilities.contains("WEP") || capabilities.contains("wep")) {
                        type = WIFICIPHER_WEP;
                    } else {
                        type = WIFICIPHER_NOPASS;
                    }
                }
                config = isExist(scanResult.SSID);
                if (config == null) {
                    if (type != WIFICIPHER_NOPASS) {//需要密码
                        showWIFICoonectDialog(scanResult.SSID, type);
                        return;
                    } else {
                        connect(createWifiInfo(scanResult.SSID, "", type));
                    }
                } else {
                    connect(config);
                }
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final ScanResult scanResult = wifiListAdapter.getItem(position);
                if (scanResult != null) {
                    new AlertDialog.Builder(WiFiManageActivity.this)
                            .setMessage("是否清除该网络配置")
                            .setNegativeButton("取消", null)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    removeWifiBySsid(scanResult.SSID);
                                }
                            }).show();
                }
                return false;
            }
        });
    }


    public void showWIFICoonectDialog(final String SSID, final int type) {
        final EditText editText = new EditText(WiFiManageActivity.this);
        new AlertDialog.Builder(WiFiManageActivity.this)
                .setMessage("请输入无线网络密码")
                .setView(editText)
                .setNegativeButton("取消", null)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        connect(createWifiInfo(SSID, editText.getText().toString(), type));
                    }
                }).show();
    }

    public void showLoadingView(boolean flag) {
        if (flag) {
            listView.setVisibility(View.INVISIBLE);
            loadingView.setVisibility(View.VISIBLE);
        } else {
            listView.setVisibility(View.VISIBLE);
            loadingView.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 判断当前wifi是否有存在
     *
     * @param SSID
     * @return
     */
    private WifiConfiguration isExist(String SSID) {
        List<WifiConfiguration> existingConfigs = wifiManager.getConfiguredNetworks();
        for (WifiConfiguration existingConfig : existingConfigs) {
            if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
                return existingConfig;
            }
        }
        return null;
    }

    private void connect(WifiConfiguration config) {
        wifiState.setText("连接中...");
        wifiManager.disconnect();
        wifiManager.enableNetwork(wifiManager.addNetwork(config), true);
    }

    public void removeWifiBySsid(String tSSID) {
        Log.d("wifi", "try to removeWifiBySsid, targetSsid=" + tSSID);
        List<WifiConfiguration> wifiConfigs = wifiManager.getConfiguredNetworks();

        for (WifiConfiguration wifiConfig : wifiConfigs) {
            String ssid = wifiConfig.SSID;
            Log.d("wifi", "removeWifiBySsid ssid=" + ssid);
            if (ssid.indexOf(tSSID)>-1) {
                Log.d("wifi", "removeWifiBySsid success, SSID = " + wifiConfig.SSID + " netId = " + String.valueOf(wifiConfig.networkId));
                wifiManager.removeNetwork(wifiConfig.networkId);
                wifiManager.saveConfiguration();
            }
        }
    }

    private void search() {
        wifiManager.startScan();
    }

    private void initBroadcastReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        intentFilter.addAction(WifiManager.RSSI_CHANGED_ACTION);

        registerReceiver(receiver, intentFilter);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action.equals(wifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {

                // wifi已成功扫描到可用wifi。
                List<ScanResult> scanResults = removeDuplicateSSID(wifiManager.getScanResults());
                wifiListAdapter.clear();
                wifiListAdapter.addAll(scanResults);

                showLoadingView(false); //显示搜索结果
            } else if (action.equals(wifiManager.WIFI_STATE_CHANGED_ACTION)) {

                int wifiState = intent.getIntExtra(
                        WifiManager.EXTRA_WIFI_STATE, 0);
                switch (wifiState) {
                    case WifiManager.WIFI_STATE_ENABLED:
                        //获取到wifi开启的广播时，开始扫描
                        wifiManager.startScan();
                        setSubtitle("正在扫描无线网络");
                        break;
                    case WifiManager.WIFI_STATE_DISABLED:
                        //wifi关闭发出的广播
                        setSubtitle("无线网络未开启");
                        break;
                }
            } else if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
                NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (info.getState().equals(NetworkInfo.State.DISCONNECTED)) {
                    wifiState.setText("未连接");
                    setSubtitle("未连接到无线网络");
                } else if (info.getState().equals(NetworkInfo.State.CONNECTED)) {
                    WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                    final WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                    wifiState.setText("无线网络已连接");
                    setSubtitle("已连接到：" + wifiInfo.getSSID());
                    System.out.println("已连接到：" + wifiInfo.getSSID());
                } else {
                    NetworkInfo.DetailedState state = info.getDetailedState();

                    if (state == state.CONNECTING) {
                        wifiState.setText("连接中...");
                    } else if (state == state.AUTHENTICATING) {
                        wifiState.setText("正在验证身份信息...");
                    } else if (state == state.OBTAINING_IPADDR) {
                        wifiState.setText("正在获取IP地址...");
                    } else if (state == state.FAILED) {
                        wifiState.setText("连接失败");
//                        removeWifiBySsid();
                    }
                }
            }
        }
    };

    public WifiConfiguration createWifiInfo(String SSID, String password, int type) {
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + SSID + "\"";
        if (type == WIFICIPHER_NOPASS) {
            config.wepKeys[0] = "\"" + "\"";
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        } else if (type == WIFICIPHER_WEP) {
            config.preSharedKey = "\"" + password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms
                    .set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedGroupCiphers
                    .set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers
                    .set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers
                    .set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers
                    .set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        } else if (type == WIFICIPHER_WPA) {
            config.preSharedKey = "\"" + password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms
                    .set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers
                    .set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement
                    .set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.TKIP);
            // config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.allowedGroupCiphers
                    .set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;
        } else {
            return null;
        }
        return config;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }


    /**
     * 合并重复的信号(保留信号最强的)
     * @param srcList
     * @return
     */
    public List removeDuplicateSSID(List<ScanResult> srcList) {
        List<ScanResult> dstList = new ArrayList();
        Iterator its = srcList.iterator();
        while(its.hasNext()) {
            ScanResult src = (ScanResult) its.next();
            Iterator itd = dstList.iterator();
            boolean containSSID = false;
            while (itd.hasNext()) {
                ScanResult dst = (ScanResult) itd.next();
                if (src.SSID.equals(dst.SSID)) {
                    containSSID = true;
                    if (src.level > dst.level) {
                        dstList.remove(dst);
                        dstList.add(src);
                    }
                    break;
                }
            }
            if (!containSSID) {
                dstList.add(src);
            }
        }
        return dstList;
    }
}
