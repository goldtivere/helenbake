package com.helenbake.helenbake.util;



import org.apache.commons.compress.utils.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Random;

public class GenericUtil {
    public static String getStoragePath() {
        String path = "";
        if (OSValidator.isWindows()) {
            path = GenericUtil.getWindowsDrive() + "\\ProgramData\\xmlcallschema\\";
        } else {
            path = "/tmp/xmlcallschema/";
        }
        return path;
    }
    public static String getWindowsDrive() {
        return System.getenv("SystemDrive");
    }
    public static String[] values() {
        String values[] = {"Zero", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine"};
        return values;

    }
    public static String wordEquivalent(String random) {
        String[] p = random.split("(?!^)");
        StringBuilder sdp = new StringBuilder();

        for (String n : p) {
            if (n.equalsIgnoreCase("0")) {
                sdp.append(values()[0]).append(", ");
            } else if (n.equalsIgnoreCase("1")) {
                sdp.append(values()[1]).append(", ");
            } else if (n.equalsIgnoreCase("2")) {
                sdp.append(values()[2]).append(", ");
            } else if (n.equalsIgnoreCase("3")) {
                sdp.append(values()[3]).append(", ");
            } else if (n.equalsIgnoreCase("4")) {
                sdp.append(values()[4]).append(", ");
            } else if (n.equalsIgnoreCase("5")) {
                sdp.append(values()[5]).append(", ");
            } else if (n.equalsIgnoreCase("6")) {
                sdp.append(values()[6]).append(", ");
            } else if (n.equalsIgnoreCase("7")) {
                sdp.append(values()[7]).append(", ");
            } else if (n.equalsIgnoreCase("8")) {
                sdp.append(values()[8]).append(", ");
            } else if (n.equalsIgnoreCase("9")) {
                sdp.append(values()[9]).append(", ");
            }

        }
        return sdp.toString();
    }
    public static LocalDateTime[] getDateRange(LocalDateTime from, LocalDateTime to) {
        if (from == null) {
            from = LocalDateTime.now();
        }
        if (to == null || to.isBefore(from)) {
            to = from;
        }
        from = GenericUtil.truncateTime(from);
        to = GenericUtil.ceilTime(to);
        return new LocalDateTime[] {from, to};
    }
    public static LocalDateTime truncateTime(LocalDateTime dateTime) {
        return dateTime.truncatedTo(ChronoUnit.DAYS);
    }
    public static LocalDateTime ceilTime(LocalDateTime dateTime) {
        return dateTime.truncatedTo(ChronoUnit.DAYS).plusHours(24).minusNanos(1);
    }
    public static String generateRandom() {
        Random rnd = new Random();
        int a = 1000 + rnd.nextInt(9000);

        return String.valueOf(a);
    }

    public static String stripString(String val)
    {
        return val.replaceAll("[^A-Za-z0-9]","");

    }
    public static String generateRandomDigits(int length) {
        SecureRandom random = new SecureRandom();
        String result = "";
        for (int i = 0; i < length; i++) {
            result += random.nextInt(10);
        }
        return result;
    }
    public static String generateRandomString(int length) {
        char[] chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();
        char[] randomChars = new char[length];
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < length; i++) {
            randomChars[i] = chars[random.nextInt(chars.length)];
        }

        return new String(randomChars);
    }

    public static String getUniqueID()
    {
        return LocalDateTime.now().toString();
    }
    public static byte[] pathToByteArray(String path) throws IOException {
        File file = Paths.get(path).toFile();
        return IOUtils.toByteArray(new FileInputStream(file));
    }

    public static byte[] pathToByteArrayFileInputStream(FileInputStream path) throws IOException {
        return IOUtils.toByteArray(path);
    }
}
