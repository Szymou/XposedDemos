package top.szymou.xposeddemos.func.translate.baidu;

import android.util.Log;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import top.szymou.xposeddemos.func.translate.baidu.entity.TransResult;
import top.szymou.xposeddemos.func.translate.baidu.entity.TransResultDetails;

public class TransApi {
    private static final String TRANS_API_HOST = "http://api.fanyi.baidu.com/api/trans/vip/translate";

    private String appid;
    private String securityKey;
    private OkHttpClient okHttpClient = new OkHttpClient();

    public TransApi(String appid, String securityKey) {
        this.appid = appid;
        this.securityKey = securityKey;
    }

    public String getTransResult(String query, String from, String to) {
//        HttpThread httpThread = new HttpThread(query, from, to);
//        FutureTask<String> futureTask = new FutureTask(httpThread);
//        new Thread(futureTask).start();
//        try {
//            return futureTask.get();
//        } catch (ExecutionException e) {
//            Log.e("Demo", "请求错误", e);
//        } catch (InterruptedException e) {
//            Log.e("Demo", "请求错误", e);
//        }

        Map<String, String> params = buildParams(query, from, to);
        String sendUrl = HttpGet.getUrlWithQueryString(TRANS_API_HOST, params);
        try {
            //1、获取到请求的对象
            Request request = new Request.Builder().url(sendUrl).get().build();
            //2、获取到回调的对象
            Call call = okHttpClient.newCall(request);
            //3、同步请求,得到响应的对象
            Response response = call.execute();//同步
//            Response response = null;
//            call.enqueue(new Callback() {
//                @Override
//                public void onFailure(@NonNull Call call, @NonNull IOException e) {
//                    Log.e("Demo", "请求失败", e);
//                }
//
//                @Override
//                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
//                    Log.e("Demo", "请求成功" + response.body().string());
//                }
//            });//异步

            //4、获取到响应体信息
            TransResult result = JSONObject.parseObject(response.body().string(), TransResult.class);
            List<TransResultDetails> detailsList = result.getTrans_result();
            TransResultDetails details = detailsList.get(0);
            Log.i("Demo", "翻译结果" + details.getDst());
            return JSONObject.toJSONString(response);
        } catch (IOException e) {
            Log.e("Demo", "请求错误", e);
        }
        return "null";
    }

    private Map<String, String> buildParams(String query, String from, String to) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("q", query);
        params.put("from", from);
        params.put("to", to);

        params.put("appid", appid);

        // 随机数
        String salt = String.valueOf(System.currentTimeMillis());
        params.put("salt", salt);

        // 签名
        String src = appid + query + salt + securityKey; // 加密前的原文
        params.put("sign", MD5.md5(src));

        return params;
    }

    class HttpThread implements Callable<String>{
        private String query;
        private String from;
        private String to;
        public HttpThread(String query, String from, String to){
            this.query = query;
            this.from = from;
            this.to = to;
        }
        @Override
        public String call() {
            return null;
        }
    }

}
