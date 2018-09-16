package com.jby.chafor.packageTable;

public class PackageTableObject {
    private String feature;
    private String trial;
    private String premium;

    public PackageTableObject(String feature, String trial, String premium) {
        this.feature = feature;
        this.trial = trial;
        this.premium = premium;
    }

    public String getFeature() {
        return feature;
    }

    public String getTrial() {
        return trial;
    }

    public String getPremium() {
        return premium;
    }
}
