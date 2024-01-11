package com.example.fastfood.Model;

import java.util.List;

public class Request {

    private  String phone;
    private String name;
    private String adress;
    private String total;
    private  String status;
    private String paymentState;
    private List<Order> foods; //List food order

    public Request() {
    }

    public Request(String paymentState, String phone, String name, String adress, String total, String status, List<Order> foods) {

        this.phone = phone;
        this.name = name;
        this.adress = adress;
        this.status = status;
        this.total = total;

        this.paymentState = paymentState;
        this.foods = foods;
    }

    public String getPaymentState() {
        return paymentState;
    }

    public void setPaymentState(String paymentState) {
        this.paymentState = paymentState;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Order> getFoods() {
        return foods;
    }

    public void setFoods(List<Order> foods) {
        this.foods = foods;
    }
}
