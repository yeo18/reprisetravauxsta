package com.example.demo.util;

public class HtmlSanitizer {

    private static final java.util.regex.Pattern HTML_TAGS = java.util.regex.Pattern.compile("<[^>]*>");
    private static final java.util.regex.Pattern SCRIPT_TAGS = java.util.regex.Pattern.compile("<script[^>]*>[^<]*</script>", java.util.regex.Pattern.CASE_INSENSITIVE);
    private static final java.util.regex.Pattern EVENT_HANDLERS = java.util.regex.Pattern.compile("\\bon\\w+\\s*=\\s*['\"][^'\"]*['\"]", java.util.regex.Pattern.CASE_INSENSITIVE);
    private static final java.util.regex.Pattern JAVASCRIPT_PROTOCOL = java.util.regex.Pattern.compile("javascript\\s*:", java.util.regex.Pattern.CASE_INSENSITIVE);

    public static String sanitize(String input) {
        if (input == null) return null;
        String result = input.trim();
        result = SCRIPT_TAGS.matcher(result).replaceAll("");
        result = HTML_TAGS.matcher(result).replaceAll("");
        result = EVENT_HANDLERS.matcher(result).replaceAll("");
        result = JAVASCRIPT_PROTOCOL.matcher(result).replaceAll("");
        result = result.replaceAll("[\u0000-\u001F\u007F-\u009F]", "");
        return result;
    }
}
