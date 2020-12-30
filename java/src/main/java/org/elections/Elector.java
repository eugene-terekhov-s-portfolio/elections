package org.elections;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        
        if (this==o) return true;
        if (o==null || getClass()!=o.getClass()) return false;
        Elector elector = (Elector) o;
        return name.equals(elector.name) && district.equals(elector.district);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, district);
    }
}
