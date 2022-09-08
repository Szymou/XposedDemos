package top.szymou.xposeddemos.hook.entity;

public class MsgEntity {
    private Boolean empty;
    private Integer stability;
    private MsgDetailsEntity values;

    public MsgEntity(){}
    public MsgEntity(Boolean empty, Integer stability){
        this.empty = empty;
        this.stability = stability;
    }

    public Boolean getEmpty() {
        return empty;
    }

    public void setEmpty(Boolean empty) {
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
