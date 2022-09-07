package top.szymou.xposeddemos.hook;

import android.content.Context;
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

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class WeChatHook3 implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        XposedBridge.log("HOOK=WECHAT");
        if (!lpparam.packageName.contains("com.tencent.mm")) return;

        Class clazz = lpparam.classLoader.loadClass("com.tencent.mm.ui.chatting.ChattingUIFragment");
      /*  XposedHelpers.findAndHookMethod(clazz, "b0", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Field mmChattingListField = clazz.getField("B");
                Object mmChattingListFieldO = mmChattingListField.get(param.thisObject);
                ViewGroup mmChattingListView = (ViewGroup) mmChattingListFieldO;

                for (int i = 0; i < mmChattingListView.getChildCount(); i++) {
                    View childAt = mmChattingListView.getChildAt(i);
                    if (childAt instanceof ListView) {
                        //在ViewGroup下找到了ListView就好办啦
                        ListView listView = (ListView) childAt;
                        ListAdapter adapter = listView.getAdapter();
//                        int adapterCount = adapter.getCount();
//                        Log.i("Demo总共消息", adapterCount + "");
                        //取最后一条消息：
                        int position = listView.getLastVisiblePosition();//adapterCount - 1;
                        int itemViewType = adapter.getItemViewType(position);
                        //查看最后一个消息的类型：目前测试知道 itemViewType == 1是对方文字消息 0是我方文字消息
                        if (itemViewType == 0 || itemViewType == 1){
//                            Object o = adapter.getItem(0);
//                            Log.i("Demo消息00", JSON.toJSONString(o));//结果是null

                            JSONObject itemData = JSON.parseObject(JSON.toJSONString(adapter.getItem(position)), JSONObject.class);
                            Log.i("Demo消息", itemData.getString("content"));
                            int adapterCount = adapter.getCount();
                            int listViewCount = listView.getCount();
                            int firstVisiblePosition = listView.getFirstVisiblePosition();
                            int lastVisiblePosition = listView.getLastVisiblePosition();
                            Log.i("Demo adapterGetCount", adapterCount + "");//17
                            Log.i("Demo listViewGetCount", listViewCount + "");//17
                            Log.i("Demo listViewScope",  firstVisiblePosition + "--" + lastVisiblePosition);//9 -- 16

                            View view = listView.getChildAt(position - firstVisiblePosition);//定位到某一条消息
                            Log.i("Demo view",  view.toString());
                            ViewGroup itemView = (ViewGroup) view;//一条消息的View
                            TextView textView = new TextView(mmChattingListView.getContext());
                            textView.setText(itemData.getString("content"));
//                            textView.setLayoutParams(new LinearLayout.LayoutParams(200, 200));
                            Log.i("Demo itemView",  itemView.toString());
                            Log.i("Demo textView",  textView.getText().toString());
                            itemView.addView(textView);

                        }

                    }else {
                        continue;
                    }
                    break;

                }
            }
        });*/

        /*XposedHelpers.findAndHookMethod(clazz, "t0", boolean.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
//                boolean i = (boolean) param.args[0];
//                Log.i("Demo t0方法的参数", i + "");//false

                Field mmChattingListField = clazz.getField("B");
                Object mmChattingListFieldO = mmChattingListField.get(param.thisObject);
                ViewGroup mmChattingListView = (ViewGroup) mmChattingListFieldO;
                ListView listView = (ListView) mmChattingListView.getChildAt(1);
                ListAdapter listAdapter = listView.getAdapter();
                int adapterCount = listAdapter.getCount();
                int listViewCount = listView.getCount();
                Log.i("Demo adapterCount", adapterCount + "");
                Log.i("Demo listViewCount", listViewCount + "");
                Log.i("Demo visibleScope", listView.getFirstVisiblePosition() + "--" + listView.getLastVisiblePosition());
                int lastMsgIndex = adapterCount - 1;

                int realPosition = lastMsgIndex - listView.getFirstVisiblePosition();
                Log.w("Demo", "真实想要的是" + realPosition);
                ViewGroup msgView = (ViewGroup) listView.getChildAt(realPosition);
                if (null != msgView) {
                    //恢复原样
                    for (int i = 0; i < adapterCount; i++) {
                        ViewGroup msgViewFresh = (ViewGroup) listView.getChildAt(i - listView.getFirstVisiblePosition());
                        while (true){
                            if (null != msgViewFresh && msgViewFresh.getChildCount() > 7) {
                                msgViewFresh.removeViewAt(msgViewFresh.getChildCount() - 1);
                            }else {
                                break;
                            }
                        }
                    }
                    //添加翻译
                    Log.w("Demo", "真实想要实际拿到的是" + msgView.getId());
                    TextView textView = new TextView(listView.getContext());
                    textView.setText("测试吧");
                    msgView.addView(textView);
                }

            }
        });*/
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
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
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
                                if (msgType != 0 && msgType != 1) return;
                                View view = (View) param.args[1];//一个消息View
                                ViewGroup msgView = (ViewGroup) view;
                                //清除已添加过的组件
                                while (true) {
                                    if (msgView.getChildCount() > 7) {
                                        msgView.removeViewAt(msgView.getChildCount() - 1);
                                    } else {
                                        break;
                                    }
                                }

                                //获取msg数据，转为json
                                JSONObject itemData = JSON.parseObject(JSON.toJSONString(adapter.getItem(position)), JSONObject.class);
                                String originMsg = itemData.getString("content");
                                Log.i("Demo 消息", originMsg);

                                //添加翻译组件
                                TextView textView = new TextView(listView.getContext());
                                textView.setText("即时翻译：" + originMsg);
                                textView.setTextSize(15);
                                textView.setPadding(0, 20, 0, 0);
                                msgView.addView(textView);

//                                    if (itemData != null && (view != null && view.toString().contains("com.tencent.mm.ui.chatting.viewitems.p"))) {

//                                                View itemViewChild = itemView.getChildAt(0);
//                                                Object tag = itemViewChild.getTag(R.id.wx_parent_has_invoke_msg);
//                                                TextView textView;
//                                                if (tag == null) {
//                                                    textView = new TextView(applicationContext);
//                                                    textView.setGravity(Gravity.CENTER);
//                                                    textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//                                                    itemViewChild.setTag(R.id.wx_parent_has_invoke_msg, textView);
//                                                    textView.setId(R.id.wx_invoke_msg);
//                                                    itemView.addView(textView);
//                                                } else {
//                                                    textView = (TextView) itemViewChild.getTag(R.id.wx_parent_has_invoke_msg);
//                                                }
//                                                textView.setText("");
//                                                textView.setVisibility(View.GONE);


//                                                String field_msgId = itemData.getString("field_msgId");
//                                                WxChatInvokeMsg wxChatInvokeMsg = WxChatInvokeMsgDB.queryByMsgId(applicationContext, field_msgId);
//                                                if (wxChatInvokeMsg != null) {
//                                                    textView.setPadding(10, 5, 10, 5);
//                                                    textView.setBackgroundDrawable(ShapeUtil.getCornerDrawable());
//                                                    textView.setTextColor(Color.parseColor("#666666"));
//                                                    View msgView = itemView.getChildAt(3);
//                                                    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) textView.getLayoutParams();
//                                                    lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
//                                                    lp.addRule(RelativeLayout.BELOW, msgView.getId());
//                                                    lp.bottomMargin = 50;
//                                                    textView.setText("这小子想撤回这个消息");
//                                                    textView.setVisibility(View.VISIBLE);
////                                                    LogUtils.i(position, view, itemViewType, itemData.toJSONString());
//                                                }


                            }

                        });
                    }
                });
    }

    /**
     * 微信聊天界面
     *
     * @param applicationContext
     * @param classLoader
     */
    private void hookWxChatUI(final Context applicationContext, final ClassLoader classLoader) throws
            ClassNotFoundException {
//        XposedHelpers.findAndHookMethod("com.tencent.mm.ui.base.MMPullDownView",
//                classLoader,
//                "onLayout",
//                boolean.class,
//                int.class,
//                int.class,
//                int.class,
//                int.class,
//                new XC_MethodHook() {
//                    @Override
//                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                        Log.i("Demo: hookWxChatUI->", "item data -> ");
//                    }
//                });

//        final Class<?> classIfExists = XposedHelpers.findClassIfExists("com.tencent.mm.ui.chatting.ChattingUI$a", classLoader);
//        if (classIfExists == null) return;
//        XposedHelpers.findAndHookMethod(classIfExists,
//                "onResume",
//                new XC_MethodHook() {
//                    @Override
//                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                        super.beforeHookedMethod(param);
//                        XposedHelpers.findAndHookMethod(classIfExists, "cuO", new XC_MethodHook() {
//                            @Override
//                            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                                super.beforeHookedMethod(param);
//                                Field ySc = param.thisObject.getClass().getDeclaredField("ySc");
//                                ySc.setAccessible(true);
//                                ListView listView = (ListView) ySc.get(param.thisObject);
//                                ListAdapter adapter = listView.getAdapter();
//                                int count = adapter.getCount();
//                                Log.e("Demo: hookWxChatUI->", "listview has " + count + " child");
//                                for (int i = 0; i < count; i++) {
//                                    Object s = adapter.getItem(i);
//                                    Log.e("Demo: hookWxChatUI->", "item data -> " + JSONObject.toJSONString(s));
//                                }
//                            }
//                        });
//                    }
//                });
    }


    /**
     * 第三种方法 调用一次getView()方法；Google推荐的做法
     *
     * @param position 要更新的位置
     */
    private void updateItem(int position, ListView listView) {
        /**
         * todo 做到这里了。呜呜
         */
        //换成CommonAdapter
        ListAdapter adapter = listView.getAdapter();
        /**第一个可见的位置**/
        int firstVisiblePosition = listView.getFirstVisiblePosition();
        /**最后一个可见的位置**/
        int lastVisiblePosition = listView.getLastVisiblePosition();

        /**在看见范围内才更新，不可见的滑动后自动会调用getView方法更新**/
        if (position >= firstVisiblePosition && position <= lastVisiblePosition) {
            /**获取指定位置view对象**/
            View view = listView.getChildAt(position - 2);
            adapter.getView(position, view, listView);
        }

    }

    private void getAllTextViews(final View v) {

        if (v instanceof ViewGroup || v instanceof LinearLayout) {
            ViewGroup vg = (ViewGroup) v;
            for (int i = 0; i < vg.getChildCount(); i++) {
                View child = vg.getChildAt(i);
                getAllTextViews(child);
            }
        } else if (v instanceof TextView) {
//            dealWithTextView((TextView)v); //dealWithTextView(TextView tv)方法：打印TextView中的显示文本
            TextView t = (TextView) v;
            Log.i("Demo打印TexyView", t.getText().toString());
        }
    }
}
