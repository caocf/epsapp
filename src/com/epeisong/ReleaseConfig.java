package com.epeisong;

/**
 * 发布时的配置
 * 
 * @author poet
 * 
 */
public class ReleaseConfig {

    public static boolean DEBUGGING = true;

    public static void main(String[] args) {
        if (DEBUGGING) {
            System.out.println("调试模式");
            return;
        }
        System.out.println("可以发布\n注意修改版本号");
    }
}
