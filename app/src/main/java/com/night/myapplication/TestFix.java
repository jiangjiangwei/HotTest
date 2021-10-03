package com.night.myapplication;


/**
 * dx --dex -output=hotfixed.dex TestFix.class
 */
public class TestFix {
    public int test() {
        int a = 1;
        int b = 2;
        int z = (a + b) * 10;
//        throw new IllegalStateException("hhhhhhhhhhhhhhhhhhhhhh");
        return z;
    }
}
