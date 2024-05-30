package top.sephy.infra.utils;

public abstract class UrlUtils {

    static String PATH_SEPARATOR = "/";

    public static String joinPathSegment(String... segments) {
        StringBuilder sb = new StringBuilder();
        for (String segment : segments) {
            if (segment == null || segment.isEmpty()) {
                continue;
            }
            if (segment.startsWith(PATH_SEPARATOR)) {
                segment = segment.substring(1);
            }
            if (segment.endsWith(PATH_SEPARATOR)) {
                segment = segment.substring(0, segment.length() - 1);
            }
            sb.append(PATH_SEPARATOR);
            sb.append(segment);
        }
        return sb.toString();
    }
}
