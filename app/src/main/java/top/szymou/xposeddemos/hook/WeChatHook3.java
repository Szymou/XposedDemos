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
        XposedHelpers.findAndHookMethod(clazz, "b0", new XC_MethodHook() {
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

                            View view = listView.getChildAt(lastVisiblePosition);//定位到某一条消息
                            ViewGroup itemView = (ViewGroup) view;//一条消息的View
                            TextView textView = new TextView(mmChattingListView.getContext());
                            textView.setText(itemData.getString("content"));
                            textView.setLayoutParams(new LinearLayout.LayoutParams(200, 200));
                            itemView.addView(textView);


                            /**
                             * itemView
                                0:::class android.widget.LinearLayout
                                1:::class android.widget.LinearLayout
                                2:::class android.widget.TextView    //2：时间
                                3:::class android.widget.CheckBox    //选择框
                                4:::class android.widget.LinearLayout
                                5:::class android.view.View
                                6:::class android.widget.ImageView
                            */


//                            View layoutView = itemView.getChildAt(4);//class android.widget.LinearLayout
//                            ViewGroup layoutViewGroup = (ViewGroup)layoutView;//一条消息的View的某个组件
//                            View layoutView2 = layoutViewGroup.getChildAt(3);//class android.widget.LinearLayout
//                            ViewGroup layoutViewGroup2 = (ViewGroup)layoutView2;
//                            View layoutView3 = layoutViewGroup2.getChildAt(1);//class android.widget.LinearLayout
//                            ViewGroup layoutViewGroup3 = (ViewGroup)layoutView3;
//                            View layoutView4 = layoutViewGroup3.getChildAt(3);//class android.widget.LinearLayout
//                            ViewGroup layoutViewGroup4 = (ViewGroup)layoutView4;
//                            TextView textView = new TextView(mmChattingListView.getContext());
//                            textView.setText("测试");
//                            layoutViewGroup4.addView(textView);
//                            View layoutView5 = layoutViewGroup4.getChildAt(0);//MMNeat7extView

//                            TextView textView = (TextView) layoutViewGroup2.getChildAt(0);
//                            Log.i("Demo textView", JSONObject.toJSONString(textView.getText()));

                        }

                    }else {
                        continue;
                    }
                    break;

                }
            }
        });
//        Class clazz = lpparam.classLoader.loadClass("com.tencent.mm.ui.chatting.view.MMChattingListView");
//        Class adapterClass = lpparam.classLoader.loadClass("com.tencent.mm.pluginsdk.ui.tools.f0");
//        Field[] fields = adapterClass.getDeclaredFields();
//        Log.i("HOOK=WECHAT=属性数量：", String.valueOf(fields.length));
//        for (Field field : fields) {
//            Log.i("HOOK=WECHAT=属性：", field.toString());
//        }
//        XposedHelpers.findAndHookMethod(clazz, "setAdapter", adapterClass, new XC_MethodHook() {
//            @Override
//            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
////                Log.i("HOOK=WECHAT=属性：", "进来看看");
////                BaseAdapter adapter = (BaseAdapter) param.args[0];
////                Log.i("HOOK=WECHAT=setAdapter：", JSONObject.toJSONString(adapter));
//            }
//        });


//        Class clazz = lpparam.classLoader.loadClass("com.tencent.mm.ui.chatting.ChattingUIFragment");
//        Field[] fields = clazz.getDeclaredFields();
//        for (Field field : fields) {
//            Log.i("HOOK=WECHAT=属性：", field.toString());
//        }
//
//        Method[] methods = clazz.getDeclaredMethods();
//        for (Method method : methods) {
//            Log.i("HOOK=WECHAT=方法：", method.toString());
//        }


//        Class clazz = lpparam.classLoader.loadClass("com.tencent.mm.ui.chatting.view.MMChattingListView");
////        Field field = clazz.getField("U0");
////        Log.i("HOOK=WECHAT=属性：", field.toString());
//        Field[] fields = clazz.getDeclaredFields();
//        for (Field field : fields) {
//            Log.i("HOOK=WECHAT=属性：", field.toString());
//        }
//
//        Method[] methods = clazz.getDeclaredMethods();
//        for (Method method : methods) {
//            Log.i("HOOK=WECHAT=方法：", method.toString());
//        }

//        Class clazz = lpparam.classLoader.loadClass("com.tencent.mm.ui.chatting.ChattingUIFragment");
//        XposedHelpers.findAndHookMethod(clazz, "getFirstVisiblePosition", new XC_MethodHook() {
//            @Override
//            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
////                Log.e("我要数据：", "--", new Throwable("cc"));
//                Field field = clazz.getField("B");
//                field.setAccessible(true);
//                View view = (View) field.get(param.thisObject);
//                Log.i("我要数据：", view.toString());
//
//            }
//        });

//        Class c = lpparam.classLoader.loadClass("rr2.a");
//        Field[] fields = c.getFields();
//        for (Field field : fields) {
//            Log.i("属性：", field.toString());
//        }

//        hookWxChatUIMM(null, lpparam.classLoader);
//        hookWxChatUIMM(null, lpparam.classLoader);
    }


    private void hookWxChatUIMM(final Context applicationContext, final ClassLoader classLoader) {
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
//                        super.beforeHookedMethod(param);
                        Log.i("Demo", "进来玩，快点。");
//                        ViewGroup mMPullDownView = (ViewGroup) param.thisObject;
//                        if (mMPullDownView.getVisibility() == View.GONE) return;
                        /*for (int i = 0; i < mMPullDownView.getChildCount(); i++) {
                            View childAt = mMPullDownView.getChildAt(i);
                            if (childAt instanceof ListView) {
                                final ListView listView = (ListView) childAt;
                                final ListAdapter adapter = listView.getAdapter();

                                XposedHelpers.findAndHookMethod(adapter.getClass(), "getView", int.class, View.class, ViewGroup.class, new XC_MethodHook() {
                                    @Override
                                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                                        super.beforeHookedMethod(param);
                                        int position = (int) param.args[0];
                                        View view = (View) param.args[1];
                                        JSONObject itemData = null;
//                                        if (position < adapter.getCount()) {
                                        if (position == adapter.getCount() - 1) {
                                            itemData = JSON.parseObject(JSON.toJSONString(adapter.getItem(position)), JSONObject.class);
                                            Log.i("Demo：", itemData.getString("content"));

                                            ViewGroup itemView = (ViewGroup) view;
                                            Log.i("Demo==", itemView.toString());

                                            if (itemData != null && (view != null && view.toString().contains("com.tencent.mm.ui.chatting.viewitems.p"))) {

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
                                        }
//

                                    }

                                });

                            }
                            break;
                        }*/
                    }
                });
    }

    /**
     * 微信聊天界面
     *
     * @param applicationContext
     * @param classLoader
     */
    private void hookWxChatUI(final Context applicationContext, final ClassLoader classLoader) throws ClassNotFoundException {
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
        } else if (v instanceof TextView ) {
//            dealWithTextView((TextView)v); //dealWithTextView(TextView tv)方法：打印TextView中的显示文本
            TextView t = (TextView) v;
            Log.i("Demo打印TexyView", t.getText().toString());
        }
    }
}
