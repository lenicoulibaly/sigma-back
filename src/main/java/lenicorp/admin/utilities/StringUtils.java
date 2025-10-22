package lenicorp.admin.utilities;

import java.text.Normalizer;

public class StringUtils
{
    public static boolean isBlank(String s)
    {
        return s == null || s.isBlank();
    }

    public static String stripAccents(String string)
    {
        if(isBlank(string)) return "";
        string = string.trim();
        string = Normalizer.normalize(string, Normalizer.Form.NFD);
        string = string.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        return string;
    }

    public static String stripAccentsToUpperCase(String string)
    {
        return stripAccents(string).toUpperCase();
    }

    public static String stripAccentsToLowerCase(String string)
    {
        return stripAccents(string).toLowerCase();
    }

    public static String blankToNull(String str)
    {
        return str == null ? null : str.trim().equals("") ? null : str;
    }

    public static boolean containsIgnoreCase(String baseStr, String searchedStr)
    {
        if(baseStr == null || searchedStr == null) return false;
        return baseStr.toLowerCase().contains(searchedStr.toLowerCase());
    }

    public static boolean containsIgnoreCaseAndAccents(String baseString, String searchedStr)
    {
        if(baseString == null || searchedStr == null) return false;
        return baseString.toLowerCase().contains(searchedStr.toLowerCase()) ? true : stripAccentsToLowerCase(baseString).contains(stripAccentsToLowerCase(searchedStr));
    }
}