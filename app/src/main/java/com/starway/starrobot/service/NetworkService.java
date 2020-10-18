package com.starway.starrobot.service;

import android.app.AlertDialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.WindowManager;

import com.starway.starrobot.R;
import com.starway.starrobot.utils.Common;
import com.starway.starrobot.utils.HttpUtil;

import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.impl.BaseUser;
import org.apache.ftpserver.usermanager.impl.WritePermission;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class NetworkService extends Service {

    public static final String CHECK_NETWORK = "com.starway.starrobot.NETWORK";
    public static final String ACTION_FTP_START = "com.starway.starrobot.FTPSTART";
    public static final String ACTION_FTP_STOP = "com.starway.starrobot.FTPSTOP";

    public static final String FTP_HOME_PATH = "/mnt/sdcard/SCROBOT/";
    private static final String FTP_USERNAME = "robot";
    private static final int FTP_PORT = 2121;

    private String ftpPassword = "0000";
    private static FtpInfo ftpInfo = new FtpInfo();

    private FtpServer ftpServer;
    private FtpServerFactory serverFactory;
    private WifiManager wifiManager;
    private Timer timer = new Timer();
    private AlertDialog dialogNetError;
    private AlertDialog dialogDisconnect;

    private TimerTask fileCheckTask = new TimerTask() {
        @Override
        public void run() {
            Common.CopyAssets(getApplicationContext(), "scrobot", FTP_HOME_PATH, false);
        }
    };

    @Override
    public void onCreate() {
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(CHECK_NETWORK);
        registerReceiver(broadcastReceiver, filter);
        initDialog();

        File dir = new File(FTP_HOME_PATH);
        if (!dir.exists()) {
            dir.mkdir();
        }

        timer.schedule(fileCheckTask, 0, 60000); //每隔一分钟检查一次文件情况
    }

    /**
     * 初始化两个系统对话框:
     * dialogNetError 网络不可用
     * dialogDisconnect 失去连接
     */
    private void initDialog() {
        dialogNetError = new AlertDialog.Builder(getApplicationContext()).setTitle(R.string.app_name)
                .setMessage("当前网络无效，请检查网络是否可用！")
                .setPositiveButton("好的", null)
                .create();
        dialogDisconnect = new AlertDialog.Builder(getApplicationContext()).setTitle(R.string.app_name)
                .setMessage("未连接到网络！")
                .setPositiveButton("好的", null)
                .create();
        dialogNetError.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dialogDisconnect.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
    }

    private void startFtpServer() {
        if (ftpServer == null || ftpServer.isStopped()) {
            try {
                ftpPassword = String.valueOf((int) ((Math.random() * 9 + 1) * 1000)); //随机生成四位密码

                serverFactory = new FtpServerFactory();
                ListenerFactory factory = new ListenerFactory();
                factory.setPort(FTP_PORT);
                serverFactory.addListener("default", factory.createListener());
                BaseUser user = new BaseUser();
                user.setName(FTP_USERNAME);
                user.setPassword(ftpPassword);
                user.setHomeDirectory(FTP_HOME_PATH);

                List<Authority> authorities = new ArrayList<>();
                authorities.add(new WritePermission());
                user.setAuthorities(authorities);

                serverFactory.getUserManager().save(user);
                ftpServer = serverFactory.createServer();
                ftpServer.start();

                ftpInfo.setAvailable(true)
                        .setAddress("FTP://" + getIpAddress() + ":" + FTP_PORT)
                        .setUser(FTP_USERNAME)
                        .setPassword(ftpPassword);

                sendBroadcast(new Intent()
                        .setAction(ACTION_FTP_START)
                        .putExtra("ftp", ftpInfo)
                );

                Log.i("FTP", "FTP服务已启动: " + ftpInfo);

            } catch (Exception e) {
                Log.i("FTP", "FTP服务启动失败：" + e.toString());
            }

        }
    }

    /**
     * 停止FTP服务
     */
    private void stopFtpServer() {
        if (ftpServer != null && !ftpServer.isStopped()) {
            ftpInfo.setAvailable(false);
            ftpServer.stop();
            Log.e("FTP", "FTP服务已暂停");
            sendBroadcast(new Intent().setAction(ACTION_FTP_STOP));
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    } //保证服务被杀死后会自动重启

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        timer.cancel(); //停止所有任务
        unregisterReceiver(broadcastReceiver); //注销广播接收器
        stopFtpServer(); //停止FTP服务
    }

    public static FtpInfo getFtpAddrInfo() {
        return ftpInfo;
    }

    /**
     * 获取本机Wifi连接的IP地址
     *
     * @return 返回IP地址的点分十进制表示
     */
    private String getIpAddress() {
        if (wifiManager.isWifiEnabled()) {
            WifiInfo wi = wifiManager.getConnectionInfo();
            int ipAdd = wi.getIpAddress();
            return (ipAdd & 0xFF) + "." + ((ipAdd >> 8) & 0xFF) + "." + ((ipAdd >> 16) & 0xFF) + "." + (ipAdd >> 24 & 0xFF);
        }
        return null;
    }

    /**
     * 不可联网时弹出提示
     */
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            dialogNetError.show();
            return false;
        }
    });

    /**
     * 检查当前网络是否可以联网（无耻地借用了一下baidu.com）
     */
    private void checkNetworkAvailable() {
        HttpUtil.getInstance().get("http://baidu.com", new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                handler.sendEmptyMessage(0);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

            }
        });
    }

    /**
     * 广播接收监听网络状态变化
     */
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(CHECK_NETWORK)) {
                System.out.println("请求检查网络状态");
                checkNetworkAvailable();
            } else {
                ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

                boolean flag = !Common.isActivityInFront(context, getPackageName() +
                        ".activity.backstage.WiFiManageActivity"); //如果当前activity不是网络连接页面则进行检查
                if (wifi.isConnected()) {
                    dialogDisconnect.hide();
                    startFtpServer();
                } else {
                    stopFtpServer();
                    if (flag) { //网络断开后就不用再次检查网络是否可用了
                        dialogDisconnect.show();
                        flag = false;
                    }
                }
                if (flag) {
                    checkNetworkAvailable();
                }
            }
        }
    };

    public static class FtpInfo implements Serializable {
        private String address;
        private String user;
        private String password;
        private boolean available = false;

        public String getAddress() {
            return address;
        }

        public FtpInfo setAddress(String address) {
            this.address = address;
            return this;
        }

        public String getUser() {
            return user;
        }

        public FtpInfo setUser(String user) {
            this.user = user;
            return this;
        }

        public String getPassword() {
            return password;
        }

        public FtpInfo setPassword(String password) {
            this.password = password;
            return this;
        }

        public boolean isAvailable() {
            return available;
        }

        public FtpInfo setAvailable(boolean available) {
            this.available = available;
            if (!available) {
                this.address = this.user = this.password = null;
            }
            return this;
        }

        @Override
        public String toString() {
            return "FtpInfo{" +
                    "address='" + address + '\'' +
                    ", user='" + user + '\'' +
                    ", password='" + password + '\'' +
                    ", available=" + available +
                    '}';
        }
    }

}
