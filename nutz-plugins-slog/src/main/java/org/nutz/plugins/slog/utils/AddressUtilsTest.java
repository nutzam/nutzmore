package org.nutz.plugins.slog.utils;


import org.junit.Test;

/**
 * @Author: Haimming
 * @Date: 2019-04-29 15:52
 * @Version 1.0
 */
public class AddressUtilsTest {

    @Test
    public void TestGetRealAddressByIP() {
        String address =AddressUtils.getRealAddressByIP("46.17.45.164");
        System.out.println(address);
    }
}