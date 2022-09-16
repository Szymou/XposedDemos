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
import top.szymou.xposeddemos.func.translate.kingmoutaint.KingTransApi;
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
        if (!lpparam.packageName.equals("com.tencent.mm")) return;
        Class dataClazz = XposedHelpers.findClassIfExists("com.tencent.wcdb.database.SQLiteDatabase", lpparam.classLoader);
        Method updateM = null;
        if (dataClazz != null) {
//            Log.i("HookWechat", dataClazz.toString());
            updateM = dataClazz.getDeclaredMethod("updateWithOnConflict", String.class, ContentValues.class, String.class, String[].class, int.class);
        }
        insertMsgDBListener(lpparam, updateM);
    }


    /**
     * updateWithOnConflict(x,x,x,x,x)
     * "message"
     * {"empty":false,"stability":0,"values":{"msgId":10447886,"msgSvrId":6986905584865588990,"status":2}}
     * "msgId=?"
     * ["10447886"]
     * 0
     */

    /**
     * 插入消息监听
     */
    public static void insertMsgDBListener(XC_LoadPackage.LoadPackageParam lpparam, Method updateM) {
//        Log.i("HookWechat", "进入插入逻辑，biu~biu~biu~~~");
        Object[] arrayOfObject = new Object[5];
        arrayOfObject[0] = String.class;
        arrayOfObject[1] = String.class;
        arrayOfObject[2] = ContentValues.class;
        arrayOfObject[3] = int.class;
        arrayOfObject[4] = new XC_MethodHook() {
            protected void afterHookedMethod(MethodHookParam param) throws IOException, NoSuchMethodException {
//                Log.i("HookWechat", "进入插入的回调函数");
                String str = (String) param.args[0];
                String str2 = (String) param.args[1];
                int i = (int) param.args[3];
                if ("message".equals(str) && "msgId".equals(str2)) {
//                    Log.i("HookWechat：", "insertMsgDBListener-0：" + str);//"message"
//                    Log.i("HookWechat：", "insertMsgDBListener-1：" + str2);//"msgId"
//                    Log.i("HookWechat：", "insertMsgDBListener-2：" + JSONObject.toJSONString(param.args[2]));//json
//                    Log.i("HookWechat：", "insertMsgDBListener-3：" + i);//0

                    MsgEntity msgEntity = JSONObject.parseObject(JSONObject.toJSONString(param.args[2]), MsgEntity.class);
                    MsgDetailsEntity valuse = msgEntity.getValues();
                    if (null == valuse) return;

                    //延迟更新
                    String msgId = valuse.getMsgId();
                    String type = valuse.getType();//1：正常文字消息；57：引用消息，title为消息内容；47：图片
                    String content = valuse.getContent();//type=1，content为消息内容
                    String title = valuse.getTitle();//type=57，title为消息内容
                    String talker = valuse.getTalker();//"5337652256@chatroom"：微信群；//wxid_idwtl1h15tat22：微信名
                    String isSend = valuse.getIsSend();//0是他人的消息；1是我自己的消息

//                    if (talker != null && talker.contains("@chatroom")){
//                        Log.e("HookWechat", "群消息，暂时不进行翻译。");
//                        return;
//                    }
                    if (null != updateM && transApi != null) {
                        updateM.setAccessible(true);
                        //发出去异步更新
                        executorService.execute(() -> {

                            //抖一下更健康
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            String transRes = "loading...";
                            MsgDetailsEntity detailsEntity = new MsgDetailsEntity();
                            detailsEntity.setMsgId(msgId);

                            if ("1".equals(type)) {
                                String finalContent = content;//原文
                                if (content.startsWith("wxid_")) {
                                    int p = content.indexOf(":\n") + 1;
                                    Log.e("HookWechat 群聊截取对方ID之后", content.substring(p));
                                    finalContent = content.substring(p);
                                }
//                                transRes = transApi.getTransResult(content, "auto", "en");//百度翻译接口
                                transRes = KingTransApi.getTransResult(finalContent);//金山解析接口
                                if (transRes == null) return;
                                detailsEntity.setContent(content + "\n" + "-------" + "\n" + transRes);

                            } else if ("57".equals(type)) {//来自于AppMessage
//                                transRes = transApi.getTransResult(title, "auto", "en");
//                                detailsEntity.setTitle(title + "\n" + "-------" + "\n" + transRes + " -- ");
                                /**
                                 * 暂时不支持引用，没研究出来。
                                 */
                                Log.e("HookWechat", "引用消息，不进行翻译。");
                                return;
                            } else {
                                Log.e("HookWechat", "非文字消息，不进行翻译。");
                                return;
                            }

                            MsgEntity entity = new MsgEntity(false, 0);
                            entity.setValues(detailsEntity);
                            try {
//                                Log.i("HookWechat", "进入更新翻译");
                                updateM.invoke(param.thisObject,
                                        "message",
                                        JSONObject.parseObject(JSONObject.toJSONString(entity), ContentValues.class),
                                        "msgId=?",
                                        new String[]{msgId},
                                        0);
//                                Log.i("HookWechat", "更新成功");
                            } catch (Throwable e) {
                                Log.e("HookWechat", "更新失败", e);
                                e.printStackTrace();
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
