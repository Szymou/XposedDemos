package top.szymou.xposeddemos.func.translate.kingmoutaint;

import android.util.Log;

import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @ClassName KingTransApi
 * @Description 解析静态前端翻译
 * @Author 熟知宇某
 * @Blog https://blog.csdn.net/weixin_43548748
 * @Date 2022/9/14 9:04
 * @Version 1.0
 */
public class KingTransApi {


    public static String getTransResult(String query){
        OkHttpClient okHttpClient = new OkHttpClient();

        String sendUrl = "https://www.iciba.com/word?w=" + query;

        //1、获取到请求的对象
        Request request = new Request.Builder().url(sendUrl).get().build();
        //2、获取到回调的对象
        Call call = okHttpClient.newCall(request);
        //3、同步请求,得到响应的对象
        try {
            Response response = call.execute();
            String res = response.body().string();
//            Log.i("HookWechat response", res);
            AnalyseRules analyseRules = new AnalyseRules();
            List<Map<String, String>> maps = analyseRules.maps;
            for (Map<String, String> map : maps) {
                String prefix = map.get("prefix");
                String suffix = map.get("suffix");
                if (res.contains(prefix) && res.contains(suffix)){
                    String json = res.substring(res.indexOf(prefix) + prefix.length(), res.indexOf(suffix));
                    if (json != null && json.startsWith("[") && json.endsWith("]")){
                        try {
                            List<String> resList = JSONObject.parseObject(json, List.class);
                            String com = "";
                            for (String r : resList) {
//                            Log.e("HookWechat 测试", s);
                                com += r + ";\n";
                            }
                            if (null != com) json = com;
                        }catch (Throwable e){
                            Log.e("HookWechat 不是数组", json, e);
                        }

                    }
                    Log.w("HookWechat transRes", query + "->" + json);
                    return json;
                }
                continue;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
