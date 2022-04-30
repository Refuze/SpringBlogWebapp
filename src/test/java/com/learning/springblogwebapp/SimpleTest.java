package com.learning.springblogwebapp;

import org.junit.Assert;
import org.junit.Test;

public class SimpleTest {
    @Test
    public void test(){
        int x = 2;
        int y = 10;
        Assert.assertEquals(12, x + y);
    }
}
