package org.apache.dubbo.gateway.client.utils;

/**
 * @author chpengzh@foxmail.com
 * @date 2021/2/16 19:20
 */
public class InnerThreadUtils {

    public static boolean delay(long delayMs) {
        try {
            Thread.sleep(delayMs);
            return true;
        } catch (InterruptedException ignore) {
            return false;
        }
    }

}
