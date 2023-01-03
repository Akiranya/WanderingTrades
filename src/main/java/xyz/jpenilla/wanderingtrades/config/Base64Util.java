package xyz.jpenilla.wanderingtrades.config;

import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

public class Base64Util {

    public static String encode(byte[] buf) {
        return java.util.Base64.getEncoder().encodeToString(buf);
    }

    public static byte[] decode(String src) {
        try {
            return java.util.Base64.getDecoder().decode(src);
        } catch (IllegalArgumentException e) {
            // compatible with the previously used base64 encoder
            try {
                return Base64Coder.decodeLines(src);
            } catch (Exception ignored) {
                throw e;
            }
        }
    }

    private Base64Util() {}

}

