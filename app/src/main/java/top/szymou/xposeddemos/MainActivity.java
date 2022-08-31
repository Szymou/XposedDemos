package top.szymou.xposeddemos;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Button button_x;
    private TextView txDemo1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button_x = (Button)findViewById(R.id.button_x);
        txDemo1 = (TextView) findViewById(R.id.txdemo1);
        button_x.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("TAG", "点击按钮");
                Toast.makeText(MainActivity.this, getToast(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    /**
     * 被Demo1 hook的方法
     * @return
     */
    public String getToast(){
        return "HELLO, 熟知宇某";
    }
}