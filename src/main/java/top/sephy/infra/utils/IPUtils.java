package top.sephy.infra.utils;

import java.security.SecureRandom;
import java.util.Random;

/**
 * @author sephy
 * @date 2022-02-05 23:02
 */
public class IPUtils {

    private IPUtils() {}

    public static String generateRandomIP() {
        Random r = new SecureRandom();
        return r.nextInt(256) + "." + r.nextInt(256) + "." + r.nextInt(256) + "." + r.nextInt(256);
    }
}
