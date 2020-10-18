package com.starway.starrobot.utils;

import android.app.ActivityManager;
import android.app.Instrumentation;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * @Author:Edgar.Li
 * @Date:2018-04-03 8:27
 * @version:
 * @purpose:
 * @Description:
 */
public class Common {

    public static final String AIUI_APPID = "5ab303c5"; //AIUI授权DID
    public static final String DEFAULT_PASSWORD = "4444"; //默认密码
    public static final String SUPER_PASSWORD = "114422331234"; //超级密码

    /**
     * 显示通知
     *
     * @param context 上下文
     * @param hit     通知文本
     */
    public static void showToast(Context context, String hit) {
        Toast.makeText(context, hit, Toast.LENGTH_SHORT).show();
    }


    /**
     * 快速获取SharedPreferences
     * @param context
     * @return
     */
    public static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences("robot", Context.MODE_PRIVATE);
     }

    /**
     * 判断某个activity是否在前台
     *
     * @param context
     * @param className 类名
     * @return
     */
    public static boolean isActivityInFront(Context context, String className) {
        List<ActivityManager.RunningTaskInfo> tasks = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getRunningTasks(1);
        if (tasks == null || tasks.isEmpty() || !TextUtils.equals(((ActivityManager.RunningTaskInfo) tasks.get(0)).topActivity.getClassName(), className)) {
            return false;
        }
        return true;
    }


    public static void back() {
        new Thread() {
            public void run() {
                try {
                    Instrumentation inst = new Instrumentation();
                    inst.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * 拷贝assets下的文件到其他位置
     *
     * @param context
     * @param assetPath 源文件路径
     * @param dstPath 目标路径
     * @param overwrite 是否覆盖
     */
    public static void CopyAssets(Context context, String assetPath, String dstPath,boolean overwrite) {
        Log.i("CopyAssets", "开始拷贝文件 assetPath:" + assetPath + " dstPath:" + dstPath);
        String[] files;
        try {
            files = context.getResources().getAssets().list(assetPath);
        } catch (IOException e1) {
            return;
        }
        File workingPath = new File(dstPath);
        // 如果目标目录不存在则新建一个
        if (!workingPath.exists()) {
            if (!workingPath.mkdirs()) {

            }
        }

        for (String fileName : files) {
            try {
                // 如果是文件夹则继续复制下一层
                if (!fileName.contains(".")) {
                    if (0 == assetPath.length()) {
                        CopyAssets(context, fileName, dstPath + fileName + "/", overwrite);
                    } else {
                        CopyAssets(context, assetPath + "/" + fileName, dstPath + fileName + "/", overwrite);
                    }
                    continue;
                }
                File outFile = new File(workingPath, fileName);
                if (outFile.exists()) {
                    if (overwrite) {
                        outFile.delete();
                    } else {
                        continue;
                    }
                }
                Log.i("CopyAssets", "拷贝文件 S:" + outFile.getPath());
                InputStream is;
                if (assetPath.length() != 0) {
                    is = context.getAssets().open(assetPath + "/" + fileName);
                } else {
                    is = context.getAssets().open(fileName);
                }
                OutputStream os = new FileOutputStream(outFile);

                byte[] buf = new byte[1024];
                int len;
                while ((len = is.read(buf)) > 0) {
                    os.write(buf, 0, len);
                }

                is.close();
                os.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 根据包名启动app
     *
     * @param context     上下文
     * @param packagename 包名
     */
    public static void doStartApplicationWithPackageName(Context context, String packagename) {
        PackageInfo packageinfo = null;
        try {
            packageinfo = context.getPackageManager().getPackageInfo(packagename, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageinfo != null) {
            Intent resolveIntent = new Intent("android.intent.action.MAIN", null);
            resolveIntent.addCategory("android.intent.category.LAUNCHER");
            resolveIntent.setPackage(packageinfo.packageName);
            ResolveInfo resolveinfo = (ResolveInfo) context.getPackageManager().queryIntentActivities(resolveIntent, 0).iterator().next();
            if (resolveinfo != null) {
                String packageName = resolveinfo.activityInfo.packageName;
                String className = resolveinfo.activityInfo.name;
                Intent intent = new Intent("android.intent.action.MAIN");
                intent.addCategory("android.intent.category.LAUNCHER");
                intent.setComponent(new ComponentName(packageName, className));
                context.startActivity(intent);
            }
        }
    }
}
