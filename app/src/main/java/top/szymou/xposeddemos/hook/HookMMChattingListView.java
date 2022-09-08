package top.szymou.xposeddemos.hook;

import android.content.Context;
import android.util.Log;

import java.lang.reflect.Method;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * @ClassName HookMMChattingListView
 * @Description
 * @Author 熟知宇某
 * @Blog https://blog.csdn.net/weixin_43548748
 * @Date 2022/9/6 16:23
 * @Version 1.0
 */
public class HookMMChattingListView implements IXposedHookLoadPackage {

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        XposedBridge.log("MMChattingListView开始hook");
        Log.i("MMChattingListViewLog", "开始hook");
        if (!lpparam.packageName.equals("com.tencent.mm")) return;
        Class clzz = XposedHelpers.findClass("com.tencent.mm.ui.chatting.view.MMChattingListView", lpparam.classLoader);
//        Method[] methods = clzz.getDeclaredMethods();
//        for (Method method : methods) {
//            Log.i("MMChattingListViewLog", method.toString());
//        }
        XposedHelpers.findAndHookMethod(clzz,
                "i", Context.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        Log.i("MMChattingListViewLog", "hook进来了");
                    }
                });
    }
}
