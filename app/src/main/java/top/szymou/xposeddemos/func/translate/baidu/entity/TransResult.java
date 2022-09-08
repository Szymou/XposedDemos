package top.szymou.xposeddemos.func.translate.baidu.entity;

import java.util.List;

/**
 * @ClassName TransResult
 * @Description
 * @Author 熟知宇某
 * @Blog https://blog.csdn.net/weixin_43548748
 * @Date 2022/9/8 11:58
 * @Version 1.0
 */
public class TransResult {
    private String from;
    private String to;
    private List<TransResultDetails> trans_result;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public List<TransResultDetails> getTrans_result() {
        return trans_result;
    }

    public void setTrans_result(List<TransResultDetails> trans_result) {
        this.trans_result = trans_result;
    }
}
