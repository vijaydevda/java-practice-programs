package com.vijay.programs;

public class Factorial {


    public static int get(int number){

        int result = 1;

        while(number > 0){

            result = result * number--;
        }
        return result;
    }
}
