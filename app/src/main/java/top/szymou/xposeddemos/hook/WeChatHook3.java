package top.szymou.xposeddemos.hook;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
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
    private final ExecutorService executorService = Executors.newFixedThreadPool(18);
    private final TransApi transApi = new TransApi("20180521000163748", "2Tc9nOCD66jwc4tgKLaU");
    private static Handler handler;
    private HashMap<String, Integer> map = new HashMap<>(31);
    private Map<String, String> transDict = new HashMap<>(510);

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        XposedBridge.log("HOOK=WECHAT");
        if (!lpparam.packageName.contains("com.tencent.mm")) return;
        hookWxChatUIMM(lpparam.classLoader);
    }


    private synchronized void hookWxChatUIMM(final ClassLoader classLoader) {
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
//                        Log.i("Demo", mMPullDownView.getChildCount() + "");
                        View childAt = mMPullDownView.getChildAt(1);
                        final ListView listView = (ListView) childAt;
                        final ListAdapter adapter = listView.getAdapter();

                        XposedHelpers.findAndHookMethod(adapter.getClass(), "getView", int.class, View.class, ViewGroup.class, new XC_MethodHook() {
                            @SuppressLint("HandlerLeak")
                            @Override
                            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                                super.beforeHookedMethod(param);
                                int position = (int) param.args[0];
                                View view = (View) param.args[1];//一个消息View
                                ViewGroup msgView = (ViewGroup) view;
                                if (msgView == null) return;
//
//                                //清除已添加过的组件
//                                while (true) {
//                                    if (null != msgView && msgView.getChildCount() > 7) {
//                                        msgView.removeViewAt(msgView.getChildCount() - 1);
//                                    } else {
//                                        break;
//                                    }
//                                }
                                if (adapter.getCount() > 50) return;
                                //非文字消息跳过
                                int msgType = adapter.getItemViewType(position);
                                if (msgType != 0 && msgType != 1) return;

                                //获取msg数据，转为json
                                JSONObject itemData = JSON.parseObject(JSON.toJSONString(adapter.getItem(position)), JSONObject.class);
                                String originMsg = itemData.getString("content");
                                if (map.keySet().contains(originMsg)) {// && map.get(originMsg) > 1
                                    return;
                                }
                                if (map.size() >= 31) {
                                    map.clear();
                                }
                                map.put(originMsg, 0);

                                new Thread(() -> {
                                    String s = transApi.getTransResult(originMsg, "auto", "en");
                                    Log.i("Demo trans", s);
                                    transDict.put(originMsg, s);
                                    if (transDict.size() > 505) {
                                        transDict.clear();
                                    }
                                }).start();
                                TextView textView = new TextView(listView.getContext());
                                textView.setText("loading...");
                                while (true) {
                                    if (null != msgView && msgView.getChildCount() > 7) {
                                        msgView.removeViewAt(msgView.getChildCount() - 1);
                                    } else {
                                        break;
                                    }
                                }
                                msgView.addView(textView);
                                executorService.submit(() -> {
                                    textView.post(() -> {
                                        while (true) {
//                                            if (textView.getText() != null && !"".equals(textView.getText())) break;
                                            if (transDict.get(originMsg) == null) continue;
                                            Log.i("Demo", originMsg + "--" + textView.getText());
                                            textView.setText(transDict.get(originMsg));
                                            break;
                                        }
                                    });
                                });

//                                executorService.submit(() -> {
//                                    Handler uiThread = new Handler(Looper.getMainLooper());
//                                    uiThread.post(() -> {
//                                        // 更新你的UI
//                                        TextView textView = new TextView(listView.getContext());
//                                        textView.setText();
//                                        msgView.addView(textView);
//                                    });
//                                });

//                                TextView textView = new TextView(listView.getContext());
//                                handler = new Handler() {
//                                    //handleMessage为处理消息的方法
//                                    @Override
//                                    public synchronized void handleMessage(Message msg) {
//                                        Log.i("Demo 消息", originMsg);
//                                        if (textView.getParent() != null) return;
//                                        try {
//                                            Thread.sleep(1000);
//                                        } catch (InterruptedException e) {
//                                            Log.i("Demo 延时", "1s");
//                                        }
//                                        textView.setText("翻译：" + originMsg);// + transApi.getTransResult(originMsg, "auto", "en")
//                                        if (null != msgView) {
//                                            //清除已添加过的组件
//                                            while (true) {
//                                                if (null != msgView && msgView.getChildCount() > 7) {
//                                                    msgView.removeViewAt(msgView.getChildCount() - 1);
//                                                } else {
//                                                    break;
//                                                }
//                                            }
//                                            msgView.addView(textView);
//                                        }
//                                    }
//                                };
//                                handler.handleMessage(null);

//                                MyHandle myHandle = new MyHandle(originMsg, msgView, listView.getContext());
//                                new Thread(() -> {
//                                    //添加翻译组件
//                                    myHandle.handleMessage(Message.obtain());
//                                }).start();
                            }

                        });
                    }
                });
    }


    class MyHandle extends Handler {
        private String originMsg;
        private ViewGroup viewGroup;
        private Context context;

        public MyHandle(String originMsg, ViewGroup viewGroup, Context context) {
            this.originMsg = originMsg;
            this.viewGroup = viewGroup;
            this.context = context;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
//            super.handleMessage(msg);
            Log.i("Demo 消息", originMsg);
            TextView textView = new TextView(context);
            if (textView.getParent() != null) return;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Log.i("Demo 延时", "1s");
            }
            textView.setText("翻译：" + originMsg);// + transApi.getTransResult(originMsg, "auto", "en")
            if (null != viewGroup) {
                //清除已添加过的组件
                while (true) {
                    if (null != viewGroup && viewGroup.getChildCount() > 7) {
                        viewGroup.removeViewAt(viewGroup.getChildCount() - 1);
                    } else {
                        break;
                    }
                }
                viewGroup.addView(textView);
            }
        }
    }
}
