package com.sty.viewpager.tab.view;

import java.io.Serializable;

/**
 * Created by tian on 2018/11/2.
 */

public class LevelData implements Serializable {
    private static final long serialVersionUID = 1L;

    private int grade;
    private int need_growth_value; //达到当前等级所需要的累计成长值（非本等级与上一等级的差值）
    private String description;

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public int getNeed_growth_value() {
        return need_growth_value;
    }

    public void setNeed_growth_value(int need_growth_value) {
        this.need_growth_value = need_growth_value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
