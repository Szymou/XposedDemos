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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
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
import top.szymou.xposeddemos.hook.entity.ConstStorage;

public class WeChatHook3 implements IXposedHookLoadPackage {
    private final ExecutorService executorService = Executors.newFixedThreadPool(18);
    private Set<String> threadView = new HashSet<>();

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
                            @Override
                            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                                int position = (int) param.args[0];
                                View view = (View) param.args[1];//????????????View
                                ViewGroup msgView = (ViewGroup) view;
                                if (msgView == null) return;
//
                                //???????????????????????????
                                freshViews(msgView);
                                if (position < (adapter.getCount() - ConstStorage.viewScope)) return;
                                //?????????????????????
                                int msgType = adapter.getItemViewType(position);
                                if (msgType != 0 && msgType != 1) return;

                                //??????msg???????????????json
                                JSONObject itemData = JSON.parseObject(JSON.toJSONString(adapter.getItem(position)), JSONObject.class);
                                String originMsg = itemData.getString("content");
                                new Thread(() -> {
                                    if (originMsg != null && originMsg.contains("###&")){
                                        String str = originMsg.substring(originMsg.indexOf("&"));
                                        Log.i("Demo ??????????????????", str);
                                    }
                                }).start();

                                if (threadView.contains(originMsg)) {
//                                    freshViews(msgView);
                                    TextView textView = new TextView(listView.getContext());
                                    textView.setText(ConstStorage.transResult.get(originMsg));
                                    msgView.addView(textView);
                                    return;
                                }
                                if (threadView.size() >= 31) {
                                    threadView.clear();
                                }
                                threadView.add(originMsg);

//                                freshViews(msgView);
                                TextView textView = new TextView(listView.getContext());
                                msgView.addView(textView);
                                Log.i("Demo", originMsg);
                                executorService.submit(() -> {
                                    textView.post(() -> {
                                        while (true) {
                                            if (ConstStorage.transResult.get(originMsg) == null) continue;
                                            textView.setText(ConstStorage.transResult.get(originMsg));
                                            break;
                                        }
                                    });
                                });
                            }

                        });
                    }
                });
    }

    private void freshViews(ViewGroup msgView) {
        while (true) {
            if (null != msgView && msgView.getChildCount() > 7) {
                msgView.removeViewAt(msgView.getChildCount() - 1);
            } else {
                break;
            }
        }
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
            Log.i("Demo ??????", originMsg);
            TextView textView = new TextView(context);
            if (textView.getParent() != null) return;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Log.i("Demo ??????", "1s");
            }
            textView.setText("?????????" + originMsg);// + transApi.getTransResult(originMsg, "auto", "en")
            if (null != viewGroup) {
                //???????????????????????????
                freshViews(viewGroup);
                viewGroup.addView(textView);
            }
        }
    }
}
