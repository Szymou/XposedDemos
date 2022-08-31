# XposedDemos
Xposed插件模板
## 步骤：
### 1.AndroidManifest.xml的<application>标签内部添加<meta-data>元素，代表是xposed模块.

```xml
<!--是一个xposed模块-->
<meta-data android:name="xposedmodule" android:value="true" />
<!--模块描述-->
<meta-data android:name="xposeddescription" android:value="这是一个神奇的Xposed模块/插件。--熟知宇某" />
<!--模块最小兼容-->
<meta-data android:name="xposedminversion" android:value="82" />
```

### 2.在app模块下的build.gradle文件中添加Xposed必要的依赖包

```apl
dependencies {
    compileOnly 'de.robv.android.xposed:api:82'
	...
}
```

如果引入失败，则需要在项目根目录settings.gradle文件中添加`jcenter()`仓库

```apl
pluginManagement {
    repositories {
        ...
        jcenter()
    }
}
dependencyResolutionManagement {
    repositories {
        ...
        jcenter()
    }
}
```

### 3.通过hook修改某方法的返回值

**正常程序**：按理点击按钮，会弹出吐司“HELLO, 熟知宇某”

```java
public class MainActivity extends AppCompatActivity {

    private Button button_x;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button_x = (Button)findViewById(R.id.button_x);
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
```

**hook程序**：通过hook劫持并修改getToast()返回值:

```java
public class HookDemo1 implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {

        //判断是否为需要hook的类报名
        if (loadPackageParam.packageName.equals("top.szymou.xposeddemos")) {
            XposedBridge.log("是这个包！是这个包！hook到了！！！！！");
            //获取类
            Class clazz = loadPackageParam.classLoader.loadClass("top.szymou.xposeddemos.MainActivity");
            if (null != clazz){
                XposedHelpers.findAndHookMethod(clazz, "getToast", new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        XposedBridge.log("hook：执行方法之后");
                        //.setResult  修改函数的返回结果
                        param.setResult("你好，熟知宇某已劫持了你的方法。");
                    }
                });
            }
        }
    }
}
```

建议此时安装测试：

<img src="/readme_img/1.jpg" style="zoom:25%;" />

点击按钮：提示getToast()正常返回的“HELLO, 熟知宇某”，说明getToast方法还没有被劫持。

### 4.添加初始化配置

添加assets文件夹，并在其中新建xposed_init文本文件。

![](/readme_img/4.png)

xposed_init内容为，hook类的全限定类名。

```
top.szymou.xposeddemos.hook.HookDemo1
```

### 5.安装、测试

安装完之后，xposed会提示启动模块，启动模块记得重启。

<img src="/readme_img/2.jpg" style="zoom:25%;" />

Lsposed日志：

![](/readme_img/3.jpg)

### 完结