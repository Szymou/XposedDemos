package top.szymou.xposeddemos;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private Button button_x;
    private Button getTransButton_x;
    private TextView txDemo1;
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button_x = (Button) findViewById(R.id.button_x);
        txDemo1 = (TextView) findViewById(R.id.txdemo1);
        button_x.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("TAG", "点击按钮");
                Toast.makeText(MainActivity.this, getToast(), Toast.LENGTH_SHORT).show();
            }
        });

        getTransButton_x = findViewById(R.id.getTrans);
        editText = findViewById(R.id.edit);
        getTransButton_x.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                OkHttpClient okHttpClient = new OkHttpClient().newBuilder().connectTimeout(1, TimeUnit.SECONDS).readTimeout(1, TimeUnit.SECONDS).build();
                ExecutorService executorService = Executors.newSingleThreadExecutor();
                String q = editText.getText().toString();

                String sendUrl = "https://www.iciba.com/word?w=" + q;

                //1、获取到请求的对象
                Request request = new Request.Builder().url(sendUrl).get().build();
                //2、获取到回调的对象
                Call call = okHttpClient.newCall(request);
                //3、同步请求,得到响应的对象
                executorService.submit(() -> {
                    try {
                        Response response;//同步
                        response = call.execute();
                        String res = response.body().string();
                        Log.i("testB response", res);
                        String filiter = "\"means\":\"";
                        String end = "\",\"word_symbol\"";
                        String json = res.substring(res.indexOf(filiter) + filiter.length(), res.indexOf(end));
                        Log.i("testB", json);
                        txDemo1.setText(json);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });


            }
        });
    }

    /**
     * 学习配置：https://www.bbsmax.com/A/ZOJPoK2ezv/
     * @param view
     */
    //sp保存
    public void saveToSP(View view) {
        //文件名字，存储模式
        SharedPreferences sp = getSharedPreferences("sp_config", Context.MODE_PRIVATE);
        sp.edit().putString("mykey","aaa").apply();
        sp.edit().putString("mykey1","bbb").apply();
    }

    //从使用sp获取数据
    public void getSP(View view) {
        //从文件获取
        SharedPreferences sp = getSharedPreferences("sp_config", Context.MODE_PRIVATE);
        //获取sp全部数据
        Map<String, ?> all = sp.getAll();
        Log.d("TAG", "获取的全部数据--->: "+all);
        //根据key获取value,如果不存在mykey就直接使用默认值
        String string = sp.getString("mykey", "默认");
        String string1 = sp.getString("mykey2", "默认");
        Log.d("TAG", "string--->mykey(存在)--->: "+string);
        Log.d("TAG", "string1--->mykey2(不存在)--->: "+string1);
        Toast.makeText(this, string, Toast.LENGTH_SHORT).show();
    }


    /**
     * 被Demo1 hook的方法
     *
     * @return
     */
    public String getToast() {
        return "HELLO, 熟知宇某";
    }
}