package com.apon.taalmaatjes.backend.api.returns;

public enum ReportRange {
    RANGE1625M(16, 25, "M"), RANGE1625F(16, 25, "F"),
    RANGE2635M(26, 35, "M"), RANGE2635F(26, 35, "F"),
    RANGE3645M(35, 45, "M"), RANGE3645F(35, 45, "F"),
    RANGE4655M(46, 55, "M"), RANGE4655F(46, 55, "F"),
    RANGE5664M(56, 64, "M"), RANGE5664F(56, 64, "F"),
    RANGE65pM(65, 999, "M"), RANGE65pF(65, 999, "F");

    final int minAge;
    final int maxAge;
    final String sex;

    ReportRange(int minAge, int maxAge, String sex) {
        this.minAge = minAge;
        this.maxAge = maxAge;
        this.sex = sex;
    }

    public int getMinAge() {
        return minAge;
    }

    public int getMaxAge() {
        return maxAge;
    }

    public String getSex() {
        return sex;
    }
}
