package com.github.wzq.models;

import java.io.Serializable;

/**
 * Created by wzq on 15/7/2.
 */
public class Address implements Serializable{

    private String province;

    private String city;

    private String district;

    private String zip;

    public Address() {
    }

    public Address(String province, String city, String district, String zip) {
        this.province = province;
        this.city = city;
        this.district = district;
        this.zip = zip;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }
}
