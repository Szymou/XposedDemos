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
import java.util.concurrent.TimeUnit;

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
    private OkHttpClient okHttpClient = new OkHttpClient().newBuilder().connectTimeout(1, TimeUnit.SECONDS).readTimeout(1, TimeUnit.SECONDS).build();

    public TransApi(String appid, String securityKey) {
        this.appid = appid;
        this.securityKey = securityKey;
    }

    public String getTransResult(String query, String from, String to) {
        Map<String, String> params = buildParams(query, from, to);
        String sendUrl = HttpGet.getUrlWithQueryString(TRANS_API_HOST, params);
        try {
            //1、获取到请求的对象
            Request request = new Request.Builder().url(sendUrl).get().build();
            //2、获取到回调的对象
            Call call = okHttpClient.newCall(request);
            //3、同步请求,得到响应的对象
            Response response = call.execute();//同步
            //4、获取到响应体信息
            TransResult result = JSONObject.parseObject(response.body().string(), TransResult.class);
            List<TransResultDetails> detailsList = result.getTrans_result();
            if (detailsList != null && detailsList.size() > 0){
                TransResultDetails details = detailsList.get(0);
                Log.i("HookWechat", "翻译结果" + details.getDst());
                return details.getDst();
            }
        } catch (IOException e) {
            Log.e("HookWechat", "请求错误", e);
        }
        return null;
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


}
