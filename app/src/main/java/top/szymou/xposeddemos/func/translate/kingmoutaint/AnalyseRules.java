package top.szymou.xposeddemos.func.translate.kingmoutaint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName AnalyseRules
 * @Description 解析html规则
 * @Author 熟知宇某
 * @Blog https://blog.csdn.net/weixin_43548748
 * @Date 2022/9/14 11:08
 * @Version 1.0
 */
public class AnalyseRules {

    /**
     * 情况一："translate_result":"Today is a good day","translate_type"
     * 情况二："means":["hello","hi","How do you do!"]}],"word_symbol":
     */
    public List<Map<String, String>> maps = new ArrayList<>();
    public AnalyseRules(){
        {
            Map<String, String> map = new HashMap<>();
            map.put("prefix", "\"translate_result\":\"");
            map.put("suffix", "\",\"translate_type\"");
            maps.add(map);
        }
        {
            Map<String, String> map = new HashMap<>();
            map.put("prefix", "\"means\":");
            map.put("suffix", "}],\"word_symbol\"");
            maps.add(map);
        }
    }
}
