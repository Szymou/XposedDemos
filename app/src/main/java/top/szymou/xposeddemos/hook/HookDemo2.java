package top.szymou.xposeddemos.hook;


import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * @ClassName HookDemo2
 * @Description hook其他APP的方法
 * @Author 熟知宇某
 * @Blog https://blog.csdn.net/weixin_43548748
 * @Date 2022/8/31 12:04
 * @Version 1.0
 */
public class HookDemo2 implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        //其他app
        if (lpparam.packageName.equals("top.szymou.xposedapplicatio")) {

            XposedBridge.log("hook到其他app的包了！！！");

            Class clazz = lpparam.classLoader.loadClass("top.szymou.xposedapplicatio.MainActivity");
            XposedHelpers.findAndHookMethod(clazz, "toastMessage", new XC_MethodHook() {
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    XposedBridge.log("跨应用搞起来！");
                }
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    param.setResult("你已被劫持略略略~");
                }

            });

        }
//        if (lpparam.packageName.equals("top.szymou.xposeddemos")){
//            XposedBridge.log("劫持标签，修改标签内容");
//            XposedHelpers.findAndHookMethod("top.szymou.xposeddemos.MainActivity", lpparam.classLoader, "getToast", new XC_MethodHook() {
//                @Override
//                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                    super.beforeHookedMethod(param);
//
//                }
////
////                @Override
////                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
////                    super.afterHookedMethod(param);
////                }
//            });
//        }
    }
}
