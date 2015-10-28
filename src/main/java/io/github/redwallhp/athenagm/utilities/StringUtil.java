package io.github.redwallhp.athenagm.utilities;


import org.bukkit.ChatColor;

import java.util.List;

public class StringUtil {


    public static String joinArray(String separator, String[] arr) {
        StringBuilder sb = new StringBuilder();
        for (String s : arr) {
            sb.append(s);
            sb.append(separator);
        }
        return sb.toString().trim();
    }


    public static String zebraList(List<String> list, ChatColor color1, ChatColor color2) {
        StringBuilder sb = new StringBuilder();
        for (String item : list) {
            if (list.indexOf(item) % 2 == 0) {
                sb.append(color1);
            }
            else {
                sb.append(color2);
            }
            sb.append(item);
            if (list.indexOf(item) != (list.size() - 1)) {
                sb.append(color1);
                sb.append(", ");
            }
        }
        return sb.toString();
    }


}
