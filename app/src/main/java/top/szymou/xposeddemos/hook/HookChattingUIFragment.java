package top.szymou.xposeddemos.hook;

import android.util.Log;

import java.lang.reflect.Method;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * @ClassName HookChattingUIFragment
 * @Description
 * @Author 熟知宇某
 * @Blog https://blog.csdn.net/weixin_43548748
 * @Date 2022/9/6 16:48
 * @Version 1.0
 */
public class HookChattingUIFragment implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        XposedBridge.log("HookChattingUIFragment开始hook");
        Log.i("HookChattingUIFragmentLog", "开始hook");
        if (!lpparam.packageName.equals("com.tencent.mm")) return;
        Class clzz = XposedHelpers.findClass("com.tencent.mm.ui.chatting.HookChattingUIFragment", lpparam.classLoader);
        Method[] methods = clzz.getDeclaredMethods();
        for (Method method : methods) {
            Log.i("HookChattingUIFragmentLog", method.toString());
        }
    }
}
