package com.apon.taalmaatjes.backend.api.returns;

public class RangeReportReturn {
    private Integer minAge;
    private Integer maxAge;
    private String sex;
    private boolean isNew;
    private Integer count;

    public RangeReportReturn(Integer minAge, Integer maxAge, String sex, boolean isNew, Integer count) {
        this.minAge = minAge;
        this.maxAge = maxAge;
        this.sex = sex;
        this.count = count;
        this.isNew = isNew;
    }

    public Integer getMinAge() {
        return minAge;
    }

    public void setMinAge(Integer minAge) {
        this.minAge = minAge;
    }

    public Integer getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(Integer maxAge) {
        this.maxAge = maxAge;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }
}
