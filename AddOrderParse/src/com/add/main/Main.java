package com.add.main;

/**
 * Created by Kuajing on 2017/3/20.
 */
public class Main {

    public static void main(String[] args) {


        ThreadUtils orderThread = ThreadUtils.getThreadUtils("upload");
        /**
         * 如果close为true则说明线程被暂停过，则修改值之后直接运行即可。
         * 这里虽然启动第一个是在主线程运行，但是第二个就会自动在分线程运行的
         */
        orderThread.setClose(false);
        orderThread.start();


        ThreadUtils downloadThread = ThreadUtils.getThreadUtils("download");
        downloadThread.setClose(false);
        downloadThread.run();

    }
}
