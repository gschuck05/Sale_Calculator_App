package com.example.sale_calculator.activities;

import com.google.gson.Gson;

public class SaleCalc {
    private double sale, cost;

    public SaleCalc ()
    {
        this.sale = 0;
        this.cost = 0;
    }

    public SaleCalc (double sale, double cost)
    {
        this.cost = checkAndGetGreaterThanZero(cost, "Cost");
        this.sale = checkAndGetGreaterThanZero(sale, "Sale");
    }

    public void setSale (double sale)
    {
        this.sale = checkAndGetGreaterThanZero(sale, "Sale");
    }

    public void setCost(double cost)
    {
        this.cost = checkAndGetGreaterThanZero(cost, "Cost");
    }

    private double checkAndGetGreaterThanZero (double value, String description)
    {
        if (value >0)
            return value;
        else
            throw new IllegalArgumentException (description + " must be greater than zero.");
    }

    public double getSale()
    {
        return sale;
    }

    public double getCost()
    {
        return cost;
    }

    public double getSalePrice()
    {
        if (sale > 0 && cost > 0) {
//            return 703 * (cost / (sale * sale));
            return cost - getDiscount();
        }
        else
            throw new IllegalStateException ("Cannot get sale price before setting cost and sale.");
    }

    public double getDiscount(){
        if (sale > 0 && cost > 0){
            return (sale/100) * getCost();
        } else {
            throw new IllegalStateException ("Cannot get discount before setting cost and sale.");
        }
    }

//    public String getBmiGroup ()
//    {
//        String bmiGroup;
//        double bmi = getSalePrice();
//
//        if (bmi < 18.5)
//            bmiGroup = "Underweight";
//        else if (bmi < 25)
//            bmiGroup = "Normal Weight";
//        else if (bmi < 30)
//            bmiGroup = "Overweight";
//        else
//            bmiGroup = "Obese";
//
//        return bmiGroup;
//    }

    public static SaleCalc getObjectFromJSONString (String json)
    {
        Gson gson = new Gson ();
        return gson.fromJson (json, SaleCalc.class);
    }

    public static String getJSONStringFromObject (SaleCalc object)
    {
        Gson gson = new Gson ();
        return gson.toJson (object);
    }

    public String getJSONStringFromThis()
    {
        return SaleCalc.getJSONStringFromObject (this);
    }

    public static SaleCalc getGameFromJSON (String json)
    {
        Gson gson = new Gson ();
        return gson.fromJson (json, SaleCalc.class);
    }

    public static String getJSONFromGame (SaleCalc obj)
    {
        Gson gson = new Gson ();
        return gson.toJson (obj);
    }

    public String getJSONFromCurrentGame()
    {
        return getJSONFromGame(this);
    }

}
