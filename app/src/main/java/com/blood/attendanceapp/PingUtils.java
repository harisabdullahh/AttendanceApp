package com.blood.attendanceapp;

import java.io.IOException;
import java.net.InetAddress;

public class PingUtils {

    public static boolean isServerReachable(String serverAddress, int timeout) {
        try {
            InetAddress inetAddress = InetAddress.getByName(serverAddress);
            return inetAddress.isReachable(timeout);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
