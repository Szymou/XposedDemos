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
import java.lang.reflect.Method;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
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
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        XposedBridge.log("开始hook微信:" + lpparam.packageName);
        if (lpparam.packageName.equals("com.tencent.mm")) {
            insertMsgDBListener(lpparam);
        }
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
    public static void insertMsgDBListener(XC_LoadPackage.LoadPackageParam lpparam) {
//        XposedBridge.log("insertMsgDBListener 开始");
        Object[] arrayOfObject = new Object[5];
        arrayOfObject[0] = String.class;
        arrayOfObject[1] = String.class;
        arrayOfObject[2] = ContentValues.class;
        arrayOfObject[3] = int.class;
        arrayOfObject[4] = new XC_MethodHook() {
            protected void afterHookedMethod(MethodHookParam paramAnonymousMethodHookParam) throws XmlPullParserException, IOException {
                //发出去异步更新就好了


//                String str = JSONObject.toJSONString(paramAnonymousMethodHookParam.args[0]);
//                String str2 = JSONObject.toJSONString(paramAnonymousMethodHookParam.args[1]);
//                MsgEntity msgEntity = JSONObject.parseObject(JSONObject.toJSONString(paramAnonymousMethodHookParam.args[2]), MsgEntity.class);
//                int i = (int) paramAnonymousMethodHookParam.args[3];
//
//                MsgDetailsEntity valuse = msgEntity.getValues();
//                valuse.setContent(valuse.getContent() + "\n" + "---" + "\n" + "这是翻译");
//
//                XposedBridge.log("insertMsgDBListener 0：" + str);
//                XposedBridge.log("insertMsgDBListener 1：" + str2);
//                XposedBridge.log("insertMsgDBListener 1：" + JSONObject.toJSONString(msgEntity));
//                XposedBridge.log("insertMsgDBListener 2：" + i);
//
//                paramAnonymousMethodHookParam.args[2] = JSONObject.parseObject(JSONObject.toJSONString(msgEntity), ContentValues.class);
//
//                Log.i("HookWechat：", "insertMsgDBListener-0：" + str);
//                Log.i("HookWechat：", "insertMsgDBListener-1：" + str2);
//                Log.i("HookWechat：", "insertMsgDBListener-2：" + JSONObject.toJSONString(paramAnonymousMethodHookParam.args[2]));
//                Log.i("HookWechat：", "insertMsgDBListener-2：" + i);
            }
        };
        XposedHelpers.findAndHookMethod(XposedHelpers.findClass("com.tencent.wcdb.database.SQLiteDatabase", lpparam.classLoader), "insertWithOnConflict", arrayOfObject);//insertWithOnConflict
//        XposedBridge.log("insertMsgDBListener 结束");
    }


}
