package com.app.oldermore.common;

public class CommonClass{
    public static SettingModel GetSettingValue(){
        SettingModel ret = new SettingModel();
        try {
            ret.setFontSize(20);
            ret.setBgColor("#ffffff");
        }catch (Exception ex){

        }
        return ret;
    }
}

