package com.pinyougou.shop.test;

import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;

import org.junit.Test;

import java.io.IOException;
import java.sql.ClientInfoStatus;

public class FastfsTest {

    @Test
    public void uploadFastdfs() throws Exception {
        ClientGlobal.init("H:\\pinyougou\\pinyougou-parent\\pinyougou-shop-web\\src\\main\\resources\\config\\fdfs_client.conf");

        TrackerClient trackerClient = new TrackerClient();

        TrackerServer trackerServer = trackerClient.getConnection();

        StorageClient storageClient = new StorageClient();

        String[] jpgs = storageClient.upload_file("I:\\pic\\u=8685675,2171717858&fm=26&gp=0.jpg", "jpg", null);
        for (String jpg : jpgs) {
            System.out.println(jpg);
        }
    }
}
