package edu.usc.UscAR.custom;

import com.beyondar.android.world.GeoObject;

/**
 * Created by Youngmin on 2016. 6. 2..
 */
public class CustomGeoObject extends GeoObject {
    private double mAltitude;
    private String mId;
    private String code;
    private String name;
    private String description;
    private double longitude; // this may be split into longitude and latitude.
    private double latitude;
    private String photo;
    private String url;
    private String campus;
    private String address;
    private String accessmap;
    private String type;


    public CustomGeoObject(long id) {
        super(id);
        this.setVisible(true);
    }

    public CustomGeoObject() {
        this.setVisible(true);
    }

    //getter
    public String getmId() {
        return this.mId;
    }

    public String getCode() {
        return this.code;
    }

    public String getmName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public String getPhoto() {
        return this.photo;
    }

    public String getUrl() {
        return this.url;
    }

    public String getCampus() {
        return this.campus;
    }

    public String getAddress() {
        return this.address;
    }

    public String getAccessmap() {
        return accessmap;
    }

    public String getType() {
        return type;
    }

    //setter
    public void setmId(String id) {
        this.mId = id;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setmName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setCampus(String campus) {
        this.campus = campus;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setAccessmap(String accessmap) {
        this.accessmap = accessmap;
    }

    public void setType(String type) {
        this.type = type;
    }
}
