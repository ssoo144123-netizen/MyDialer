package com.example.mydialer;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

public class BlockList {
    private static final String PREF = "block_prefs";
    private static final String KEY = "numbers";

    public static boolean isBlocked(Context c, String number) {
        String n = normalize(number);
        return !n.isEmpty() && get(c).contains(n);
    }

    public static void add(Context c, String number) {
        String n = normalize(number);
        if (n.isEmpty()) return;
        Set<String> s = get(c);
        s.add(n);
        prefs(c).edit().putStringSet(KEY, s).apply();
    }

    private static Set<String> get(Context c) {
        // کپی می‌کنیم، چون Set برگشتی از SharedPreferences نباید تغییر کند
        return new HashSet<>(prefs(c).getStringSet(KEY, new HashSet<String>()));
    }

    private static SharedPreferences prefs(Context c) {
        return c.getSharedPreferences(PREF, Context.MODE_PRIVATE);
    }

    static String normalize(String number) {
        if (number == null) return "";
        return number.replaceAll("[^0-9+]", "");
    }
}
