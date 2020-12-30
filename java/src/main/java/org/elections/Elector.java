package org.elections;

public class Elector {
    private final String name;
    private final String district;

    public Elector(String name, String district) {
        this.name = name;
        this.district = district;
    }

    public String getName() {
        return name;
    }

    public String getDistrict() {
        return district;
    }
}
