package top.szymou.xposeddemos.hook.entity;

public class MsgEntity {
    private String empty;
    private Integer stability;
    private MsgDetailsEntity values;

    public String getEmpty() {
        return empty;
    }

    public void setEmpty(String empty) {
        this.empty = empty;
    }

    public Integer getStability() {
        return stability;
    }

    public void setStability(Integer stability) {
        this.stability = stability;
    }

    public MsgDetailsEntity getValues() {
        return values;
    }

    public void setValues(MsgDetailsEntity values) {
        this.values = values;
    }

}
