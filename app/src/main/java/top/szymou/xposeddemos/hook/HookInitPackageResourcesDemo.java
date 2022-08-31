package top.szymou.xposeddemos.hook;

import android.graphics.Color;
import android.view.ViewGroup;
import android.widget.TextView;

import de.robv.android.xposed.IXposedHookInitPackageResources;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_InitPackageResources;
import de.robv.android.xposed.callbacks.XC_LayoutInflated;

/**
 * @ClassName HookInitPackageResourcesTest
 * @Description
 *  在资源布局初始化时会回被执行(inflate方法)
 *  回调方法：handleInitPackageResources(XC_InitPackageResources.InitPackageResourcesParam resparam)
 *  InitPackageResourcesParam包含两个参数，包名和XResource(资源相关)
 *  有了这个XResource对象，就可以拿到布局资源树了，通过重写hookLayout方法，
 * @Author 熟知宇某
 * @Blog https://blog.csdn.net/weixin_43548748
 * @Date 2022/8/31 14:53
 * @Version 1.0
 */
public class HookInitPackageResourcesDemo implements IXposedHookInitPackageResources {
    @Override
    public void handleInitPackageResources(XC_InitPackageResources.InitPackageResourcesParam resparam) throws Throwable {
        //InitPackageResourcesParam包含两个参数：包名和XResource(资源相关)
        XposedBridge.log("hook到包名：" + resparam.packageName);
        resparam.res.hookLayout(resparam.packageName, "layout", "activity_main", new XC_LayoutInflated() {
            @Override
            public void handleLayoutInflated(LayoutInflatedParam liparam) throws Throwable {
                //LayoutInflatedParam，里面这个view就是布局资源树了，可以拿到遍历，拿到某个特定控件，然后做一些骚操作。
                XposedBridge.log("hook到资源activity_main布局了。");
                //遍历查看资源树
                printView((ViewGroup) liparam.view, 1);
                //getIdentifier()第一个参数格式是：包名 + : +资源文件夹名 + / +资源名；是这种格式 然后其他的可以为null
//                TextView tttt = (TextView) liparam.view.findViewById(liparam.res.getIdentifier("tttt", "id", resparam.packageName));
                TextView tttt = (TextView) liparam.view.findViewById(liparam.res.getIdentifier(resparam.packageName + ":tttt", "id", resparam.packageName));
                tttt.setText("tttt的view终于找到了");
                tttt.setTextColor(Color.RED);
                XposedBridge.log("layout  resNames.res.getIdentifier:" + liparam.res.getIdentifier("tttt", "id", resparam.packageName));
                XposedBridge.log("layout  resNames.fullname:" + liparam.resNames.fullName);
                XposedBridge.log("layout  resNames.id:" + liparam.resNames.id);
                XposedBridge.log("layout  resNames.name:" + liparam.resNames.name);
                XposedBridge.log("layout  resNames.pkg:" + liparam.resNames.pkg);
                XposedBridge.log("layout  resNames.type:" + liparam.resNames.type);
                XposedBridge.log("layout  resNames.variant:" + liparam.variant);
                XposedBridge.log("layout  resNames.view:" + liparam.view);
            }
        });

        //这里不行，不知道是不是这样赋参
        resparam.res.hookLayout(resparam.packageName, "id", "tttt", new XC_LayoutInflated() {
            @Override
            public void handleLayoutInflated(LayoutInflatedParam liparam) throws Throwable {
                XposedBridge.log("hook到资源tttt布局了。");
                //遍历查看资源树
                printView((ViewGroup) liparam.view, 1);
            }
        });
//还不会用
//        resparam.res.setReplacement(resparam.packageName, "string", "tttt", "这就是变化？？？");

    }


    //遍历资源布局树，并打印出来
    private void printView(ViewGroup view, int deep) {
        XposedBridge.log("遍历布局开始。");
        String viewgroupDeepFormat = "";
        String viewDeepFormat = "";
        for (int i = 0; i < deep - 1; i++) {
            viewgroupDeepFormat += "\t";
        }
        viewDeepFormat = viewgroupDeepFormat + "\t";
        XposedBridge.log(viewgroupDeepFormat + view.toString());
        int count = view.getChildCount();
        for (int i = 0; i < count; i++) {
            if (view.getChildAt(i) instanceof ViewGroup) {
                printView((ViewGroup) view.getChildAt(i), deep + 1);
            } else {
                XposedBridge.log(viewDeepFormat + view.getChildAt(i).toString());
            }
        }
        XposedBridge.log("遍历布局结束。");
    }
}
