package top.szymou.xposeddemos.hook;

import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.alibaba.fastjson.JSONObject;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import top.szymou.xposeddemos.func.translate.baidu.TransApi;
import top.szymou.xposeddemos.hook.entity.ConstStorage;
import top.szymou.xposeddemos.hook.entity.MsgDetailsEntity;
import top.szymou.xposeddemos.hook.entity.MsgEntity;

/**
 * @ClassName WeChatHook
 * @Description
 * @Author 熟知宇某
 * @Blog https://blog.csdn.net/weixin_43548748
 * @Date 2022/9/1 10:43
 * @Version 1.0
 */
public class WeChatHook implements IXposedHookLoadPackage {
    private static ExecutorService executorService = Executors.newFixedThreadPool(10);
    private static TransApi transApi = new TransApi("20180521000163748", "2Tc9nOCD66jwc4tgKLaU");
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        XposedBridge.log("HookWechat包名" + lpparam.packageName);

        /*XposedHelpers.findAndHookMethod("com.tencent.wcdb.database.SQLiteDatabase", lpparam.classLoader, "updateWithOnConflict",
                String.class, ContentValues.class, String.class, String[].class, int.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        String str0 = JSONObject.toJSONString(param.args[0]);
                        String str1 = JSONObject.toJSONString(param.args[1]);
                        String str2 = JSONObject.toJSONString(param.args[2]);
                        String str3 = JSONObject.toJSONString(param.args[3]);
                        String str4 = JSONObject.toJSONString(param.args[4]);
                        Log.i("HookWechat", "update-0：" + str0);
                        Log.i("HookWechat", "update-1：" + str1);
                        Log.i("HookWechat", "update-2：" + str2);
                        Log.i("HookWechat", "update-3：" + str3);
                        Log.i("HookWechat", "update-4：" + str4);
                    }
                });

        XposedHelpers.findAndHookMethod("com.tencent.wcdb.database.SQLiteDatabase", lpparam.classLoader, "insertWithOnConflict",
                String.class, String.class, ContentValues.class, int.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        String str0 = JSONObject.toJSONString(param.args[0]);
                        String str1 = JSONObject.toJSONString(param.args[1]);
                        String str2 = JSONObject.toJSONString(param.args[2]);
                        String str3 = JSONObject.toJSONString(param.args[3]);
                        Log.e("HookWechat", "insert-0：" + str0);
                        Log.e("HookWechat", "insert-1：" + str1);
                        Log.e("HookWechat", "insert-2：" + str2);
                        Log.e("HookWechat", "insert-3：" + str3);
                    }
                });*/

//        if (lpparam.packageName.equals("com.tencent.mm")) {
            Class dataClazz = XposedHelpers.findClassIfExists("com.tencent.wcdb.database.SQLiteDatabase", lpparam.classLoader);
            Method updateM = null;
            if (dataClazz != null){
                Log.i("HookWechat", dataClazz.toString());
                updateM = dataClazz.getDeclaredMethod("updateWithOnConflict", String.class, ContentValues.class, String.class, String[].class, int.class);
            }
            insertMsgDBListener(lpparam, updateM);
//        }
        /*if (lpparam.packageName.equals("com.tencent.mm")) {
            XposedBridge.log("hook中微信chatting包.");

            Class clz = lpparam.classLoader.loadClass("com.tencent.mm.ui.chatting.ChattingUI");
            Field[] fields = clz.getDeclaredFields();
            for (Field field : fields) {
                XposedBridge.log("属性：" + field.getName());
            }
            XposedHelpers.findAndHookMethod(clz, "onCreate", Bundle.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    Class c = lpparam.classLoader.loadClass("com.tencent.mm.ui.chatting.ChattingUI");
                    Field[] fields = c.getDeclaredFields();
                    for (Field field : fields) {
//                        XposedBridge.log("属性：" + field.getName());
                        XposedBridge.log("属性--：" + field.getName() + "=>" + JSONObject.toJSONString(field.get(param.thisObject)));
                    }
                }
            });
//            Class clz = lpparam.classLoader.loadClass("com.tencent.mm.ui.chatting.ChattingUIFragment");
//            Field[] fields = clz.getDeclaredFields();
//            for (Field field : fields) {
//                XposedBridge.log("属性：" + field.getName());
//            }
//            Method[] ms = clz.getDeclaredMethods();
//            for (Method m : ms) {
//                XposedBridge.log("方法：" + m.getName());
//            }
//            XposedHelpers.findAndHookMethod(clz, "hideVKB", new XC_MethodHook() {
//                @Override
//                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                    super.beforeHookedMethod(param);
//                    Class c = lpparam.classLoader.loadClass("com.tencent.mm.ui.chatting.ChattingUIFragment");
//
//                    Field[] fields = c.getFields();
//                    for (Field field : fields) {
//                        try {
//                            XposedBridge.log("属性：" + field.getName() + "=>" + JSONObject.toJSONString(field.get(param.thisObject)));
//                        }catch (RuntimeException e){
//                            XposedBridge.log("没有属性：" + field.getName());
//                        }
//                    }
//                }

//                @Override
//                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                    super.afterHookedMethod(param);
//                    Class c = lpparam.classLoader.loadClass("com.tencent.mm.ui.chatting.ChattingUIFragment");
//
//                    Field[] fields = c.getFields();
//                    for (Field field : fields) {
//                        try {
//                            XposedBridge.log("属性：" + field.getName() + "=>" + JSONObject.toJSONString(field.get(param.thisObject)));
//                        }catch (RuntimeException e){
//                            XposedBridge.log("没有属性：" + field.getName());
//                        }
//                    }
//                }
//            });


        }*/
    }

    /**
     * 插入消息监听
     */
    public static void insertMsgDBListener(XC_LoadPackage.LoadPackageParam lpparam, Method updateM) {
        Log.i("HookWechat", "进入插入");
        Object[] arrayOfObject = new Object[5];
        arrayOfObject[0] = String.class;
        arrayOfObject[1] = String.class;
        arrayOfObject[2] = ContentValues.class;
        arrayOfObject[3] = int.class;
        arrayOfObject[4] = new XC_MethodHook() {
            protected void beforeHookedMethod(MethodHookParam param) throws IOException, NoSuchMethodException {
                Log.i("HookWechat", "进入插入的回调函数");
                //发出去异步更新就好了
                String str = JSONObject.toJSONString(param.args[0]);
                String str2 = (String) param.args[1];
                int i = (int) param.args[3];
                Log.i("HookWechat：", "insertMsgDBListener-0：" + str);//"message"
                Log.i("HookWechat：", "insertMsgDBListener-1：" + str2);//"msgId"
                Log.i("HookWechat：", "insertMsgDBListener-2：" + JSONObject.toJSONString(param.args[2]));//json
                Log.i("HookWechat：", "insertMsgDBListener-3：" + i);
                if ("msgId".equals(str2)){
                    Log.i("HookWechat", "进入消息插入的操作");
                    MsgEntity msgEntity = JSONObject.parseObject(JSONObject.toJSONString(param.args[2]), MsgEntity.class);
                    param.args[2] = JSONObject.parseObject(JSONObject.toJSONString(msgEntity), ContentValues.class);
                    MsgDetailsEntity valuse = msgEntity.getValues();
//                    valuse.setContent(valuse.getContent() + "\n" + "---" + "\n" + "这是翻译");
                    if (null == valuse) return;
                    String originMsg = valuse.getContent();
                    new Thread(() -> {
                        String s = transApi.getTransResult(originMsg, "auto", "en");
                        if (null == s) s = "failed";
                        Log.i("Demo trans", s);
                        ConstStorage.transResult.put(originMsg, s);
                        if (ConstStorage.transResult.size() > 1010) {
                            ConstStorage.transResult.clear();
                        }
                    }).start();
                    //延迟更新
                    String msgId = valuse.getMsgId();
                    if (false){
//                    if (null != updateM){
                        executorService.execute(() -> {
                            updateM.setAccessible(true);
                            /**
                             * updateWithOnConflict(x,x,x,x,x)
                             * "message"
                             * {"empty":false,"stability":0,"values":{"msgId":10447886,"msgSvrId":6986905584865588990,"status":2}}
                             * "msgId=?"
                             * ["10447886"]
                             * 0
                             */
                            MsgEntity entity = new MsgEntity(false, 0);
                            MsgDetailsEntity detailsEntity = new MsgDetailsEntity();
                            detailsEntity.setMsgId(msgId);
                            detailsEntity.setContent(valuse.getContent() + "\n" + "---" + "\n" + "翻译");
                            entity.setValues(detailsEntity);
                            try {
                                Log.i("HookWechat", "进入更新翻译");
                                updateM.invoke(param.thisObject,
                                        "message",
                                        JSONObject.parseObject(JSONObject.toJSONString(entity), ContentValues.class),
                                        "msgId=?",
                                        new String[]{msgId},
                                        0);
                            }  catch (Throwable e) {
                                Log.e("HookWechat", "更新失败", e);
                                XposedBridge.log("HookWechat" + "更新失败");
                            }
                        });
                    }
                }

            }
        };
        XposedHelpers.findAndHookMethod(XposedHelpers.findClass("com.tencent.wcdb.database.SQLiteDatabase", lpparam.classLoader), "insertWithOnConflict", arrayOfObject);//insertWithOnConflict
    }


}
