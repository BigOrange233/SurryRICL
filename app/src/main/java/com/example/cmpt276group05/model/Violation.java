package com.example.cmpt276group05.model;

/*
* data class for Violation
* */
public class Violation{
    private String code;//violation code
    private String cirtical;//violation cirtical
    private String desc;//violation description
    private String repeat;//repeat or not repeat

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getCirtical() {
        return cirtical;
    }

    public void setCirtical(String cirtical) {
        this.cirtical = cirtical;
    }

    public String getRepeat() {
        return repeat;
    }

    public void setRepeat(String repeat) {
        this.repeat = repeat;
    }
}
