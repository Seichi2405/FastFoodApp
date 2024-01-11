package com.example.fastfood.Common;

import com.example.fastfood.Model.User;

public class Common {
    public static User currentUser;


    public static String convertCodeToStatus(String status) {
        if(status.equals("0"))
            return "Đang Đợi";
        else if(status.equals("1"))
            return "Đang Giao";
        else return "Đã Giao";
    }
}
