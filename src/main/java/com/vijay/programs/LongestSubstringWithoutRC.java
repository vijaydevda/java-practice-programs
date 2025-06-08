package com.vijay.programs;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class LongestSubstringWithoutRC {

    public static String longestSubstring(String s) {
        int left = 0, right = 0;
        int maxLength = 0, maxStart = 0;
        Set<Character> set = new LinkedHashSet<>();

        while (right < s.length()) {
            char currentChar = s.charAt(right);

            // If character is already in set, remove from left until it's gone
            while (set.contains(currentChar)) {
                System.out.println("characters in window "+set);
                set.remove(s.charAt(left));

                left++;
            }

            set.add(currentChar);
            right++;
            if (right - left > maxLength) {
                maxLength = right - left;
                maxStart = left;
            }


        }

        return s.substring(maxStart, maxStart + maxLength);
    }

    public static void main(String[] args) {
        String input = "pwwkew";
        String result = longestSubstring(input);
        System.out.println("Longest substring without repeating characters: " + result);
    }
}
