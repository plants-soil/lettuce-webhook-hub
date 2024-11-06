package com.plantssoil.common.persistence.rdbms.beans;

import javax.persistence.Embeddable;

@Embeddable
public class Address {
    private String addressNo;
    private String street;
    private String city;
    private String province;

    public Address() {
    }

    public Address(String addressNo, String street, String city, String province) {
        this.addressNo = addressNo;
        this.street = street;
        this.city = city;
        this.province = province;
    }

    public String getAddressNo() {
        return addressNo;
    }

    public void setAddressNo(String addressNo) {
        this.addressNo = addressNo;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }
}
