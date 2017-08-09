package com.castlight.dataversioningpoc.manualsemanticversions;

/**
 * Created by anantm on 8/4/17.
 */
public class SemanticVersion {

    private int majorVersion;
    private int minorVersion;
    private int patchVersion;

    public SemanticVersion(){

    }

    public SemanticVersion(String version) {
        this.majorVersion = Integer.parseInt(version.split("\\.")[0]);
        this.minorVersion = Integer.parseInt(version.split("\\.")[1]);
        this.patchVersion = Integer.parseInt(version.split("\\.")[2]);

    }

    public int getMajorVersion() {
        return majorVersion;
    }

    public void setMajorVersion(int majorVersion) {
        this.majorVersion = majorVersion;
    }

    public int getMinorVersion() {
        return minorVersion;
    }

    public void setMinorVersion(int minorVersion) {
        this.minorVersion = minorVersion;
    }

    public int getPatchVersion() {
        return patchVersion;
    }

    public void setPatchVersion(int patchVersion) {
        this.patchVersion = patchVersion;
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append(majorVersion)
                .append(".")
                .append(minorVersion)
                .append(".")
                .append(patchVersion).toString();

    }
}
