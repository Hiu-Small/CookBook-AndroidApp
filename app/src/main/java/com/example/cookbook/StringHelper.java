package com.example.cookbook;

import java.text.Normalizer;
import java.util.regex.Pattern;

public class StringHelper {
    public static String removeAccents(String s) {
        if (s == null) return "";
        String temp = Normalizer.normalize(s, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(temp).replaceAll("").toLowerCase().replace('đ', 'd').replace('Đ', 'd');
    }
}