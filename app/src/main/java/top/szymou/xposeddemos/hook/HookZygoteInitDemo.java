package top.szymou.xposeddemos.hook;

import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

/**
 * @ClassName HookZygoteInitDemo
 * @Description IXposedHookZygoteInit：在Zygote启动时调用，用于系统服务的Hook 回调方法initZygote()
 * @Author 熟知宇某
 * @Blog https://blog.csdn.net/weixin_43548748
 * @Date 2022/8/31 14:52
 * @Version 1.0
 */
public class HookZygoteInitDemo implements IXposedHookZygoteInit {
    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {
        XposedBridge.log("启动时，初始化initZygote方法执行");
    }
}
