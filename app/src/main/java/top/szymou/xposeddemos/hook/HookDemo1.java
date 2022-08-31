package top.szymou.xposeddemos.hook;

import android.widget.TextView;

import java.lang.reflect.Field;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;


/**
 * @ClassName HookDemo2
 * @Description
 *              hook无参方法返回；
 *              hook标签内容，修改标签内容
 * @Author 熟知宇某
 * @Blog https://blog.csdn.net/weixin_43548748
 * @Date 2022/8/31 12:04
 * @Version 1.0
 */
public class HookDemo1 implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {

        //判断是否为需要hook的类报名
        if (loadPackageParam.packageName.equals("top.szymou.xposeddemos")) {
            XposedBridge.log("是这个包！是这个包！hook到了！！！！！");
            //获取类
            Class clazz = loadPackageParam.classLoader.loadClass("top.szymou.xposeddemos.MainActivity");
            if (null != clazz){
                XposedHelpers.findAndHookMethod(clazz, "getToast", new XC_MethodHook() {

                    @Override//MethodHookParam：包含与调用方法有关的信息：比较关注的是这个thisObject，代表调用该方法的对象实例，如果是静态方法
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {

                        XposedBridge.log("hook：执行方法之前：修改view");
                        super.beforeHookedMethod(param);
                        Class clz = loadPackageParam.classLoader.loadClass("top.szymou.xposeddemos.MainActivity");
                        //获取类中的属性
                        Field field = clz.getDeclaredField("txDemo1");
                        //设置属性权限
                        field.setAccessible(true);
                        //宇宙级理解：这个thisObject，代表调用该方法的对象实例，如果是静态方法的话，返回一个Null，比如这里调用getToast()方法的是MainActivity，获得的自然是MainActivity实例。
                        TextView textView = (TextView) field.get(param.thisObject);
                        String defaultValue = (String) textView.getText();
                        XposedBridge.log("textView默认值：" + defaultValue);
                        textView.setText(defaultValue + "\n------------\n" + "连我也被劫持了。呜呜.");
                    }


                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        XposedBridge.log("hook：执行方法之后：修改getToast返回值");
                        param.setResult("你好，熟知宇某已劫持了你的方法。");
                    }
                });
            }
        }
    }
}
