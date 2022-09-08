package top.szymou.xposeddemos.hook;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.lang.reflect.Field;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import top.szymou.xposeddemos.func.translate.baidu.TransApi;
import top.szymou.xposeddemos.func.translate.baidu.entity.Waitting;

public class WeChatHook3 implements IXposedHookLoadPackage {
    private final ExecutorService executorService = Executors.newFixedThreadPool(1);
    private final TransApi transApi = new TransApi("20180521000163748", "2Tc9nOCD66jwc4tgKLaU");
    private Handler handler;
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        XposedBridge.log("HOOK=WECHAT");
        if (!lpparam.packageName.contains("com.tencent.mm")) return;
        hookWxChatUIMM(lpparam.classLoader);
    }


    private void hookWxChatUIMM(final ClassLoader classLoader) {
        XposedHelpers.findAndHookMethod("com.tencent.mm.ui.base.MMPullDownView",
                classLoader,
                "onLayout",
                boolean.class,
                int.class,
                int.class,
                int.class,
                int.class,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                        super.beforeHookedMethod(param);
                        ViewGroup mMPullDownView = (ViewGroup) param.thisObject;
                        if (mMPullDownView.getVisibility() == View.GONE) return;
                        Log.i("Demo", mMPullDownView.getChildCount() + "");
                        View childAt = mMPullDownView.getChildAt(1);
                        final ListView listView = (ListView) childAt;
                        final ListAdapter adapter = listView.getAdapter();

                        XposedHelpers.findAndHookMethod(adapter.getClass(), "getView", int.class, View.class, ViewGroup.class, new XC_MethodHook() {
                            @Override
                            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                                super.beforeHookedMethod(param);
                                int position = (int) param.args[0];
                                int msgType = adapter.getItemViewType(position);
                                View view = (View) param.args[1];//一个消息View
                                ViewGroup msgView = (ViewGroup) view;
                                //清除已添加过的组件
                                while (true) {
                                    if (null != msgView && msgView.getChildCount() > 7) {
                                        msgView.removeViewAt(msgView.getChildCount() - 1);
                                    } else {
                                        break;
                                    }
                                }
                                if (msgType != 0 && msgType != 1) return;

                                //获取msg数据，转为json
                                JSONObject itemData = JSON.parseObject(JSON.toJSONString(adapter.getItem(position)), JSONObject.class);
                                String originMsg = itemData.getString("content");
                                Log.i("Demo 消息", originMsg);
//                                executorService.execute(() -> {
                                TextView textView = new TextView(listView.getContext());
                                textView.setText("即时翻译：" + originMsg);
                                handler = new Handler() {
                                    //handleMessage为处理消息的方法
                                    @Override
                                    public void handleMessage(Message msg) {
                                        super.handleMessage(msg);
                                        if (null != msgView) {
                                            msgView.addView(textView);
                                            Log.i("Demo 消息----", originMsg);
                                        }
                                    }
                                };
                                new Thread(() -> {
                                    //添加翻译组件
                                    handler.handleMessage(null);
                                }).start();
                            }

                        });
                    }
                });
    }

}
