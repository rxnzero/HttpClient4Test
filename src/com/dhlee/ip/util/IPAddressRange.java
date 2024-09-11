package com.dhlee.ip.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class IPAddressRange {
    public static void main(String[] args) {
        String startIP = "192.168.1.1"; // ���� IP �ּ�
        String endIP = "192.168.1.10"; // �� IP �ּ�

        List<String> ipList = generateIPRange(startIP, endIP);
        
        // ������ IP ��� ���
        for (String ip : ipList) {
            System.out.println(ip);
        }
    }

    public static List<String> generateIPRange(String startIP, String endIP) {
        List<String> ipList = new ArrayList<>();
        
        try {
            InetAddress start = InetAddress.getByName(startIP);
            InetAddress end = InetAddress.getByName(endIP);

            byte[] startBytes = start.getAddress();
            byte[] endBytes = end.getAddress();

            long startLong = bytesToLong(startBytes);
            long endLong = bytesToLong(endBytes);

            for (long current = startLong; current <= endLong; current++) {
                ipList.add(longToIP(current));
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        return ipList;
    }
    
    public static List<String> generateIPRange(String input) {
    	List<String> ipList = new ArrayList<>();
    	String[] parts = input.split("\\.");
        
        if (parts.length != 4 || !parts[3].equals("*")) {
            System.out.println("Invalid input format. Please provide IP in the format 192.168.10.*");
            return ipList;
        }
        
        String networkPrefix = parts[0] + "." + parts[1] + "." + parts[2] + ".";
        
        System.out.println("IP Range:");
        for (int i = 0; i <= 255; i++) {
        	ipList.add(networkPrefix + i);
        }
        return ipList;
    }
    
    // ����Ʈ�� long ������ ��ȯ�ϴ� ����� �޼���
    public static long bytesToLong(byte[] bytes) {
        long result = 0;
        for (byte b : bytes) {
            result = result << 8 | (b & 0xFF);
        }
        return result;
    }

    // long ���� IP ���ڿ��� ��ȯ�ϴ� ����� �޼���
    public static String longToIP(long ip) {
        StringBuilder sb = new StringBuilder(15);
        for (int i = 0; i < 4; i++) {
            sb.insert(0, Long.toString(ip & 0xff));
            if (i < 3) {
                sb.insert(0, '.');
            }
            ip = ip >> 8;
        }
        return sb.toString();
    }
    
    
}
