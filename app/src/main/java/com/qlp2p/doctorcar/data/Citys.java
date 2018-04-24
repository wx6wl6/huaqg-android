package com.qlp2p.doctorcar.data;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Citys {

    public String id;
    public String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public Citys(JSONObject jsonObject){
        id = jsonObject.getString("id");
        name = jsonObject.getString("name");
    }

    @Override
    public String toString() {
        return "Citys{" +
                "code='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}

