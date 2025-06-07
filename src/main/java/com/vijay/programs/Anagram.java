package com.vijay.programs;

public class Anagram {

    public static boolean isAnagram(String word1, String word2) {

        if (word1 == null || word2 == null) {
            return false;
        }

        if (word1.length() != word2.length()) {
            return false;
        }

        if (word1.equalsIgnoreCase(word2)) {
            return true;
        }
        word1 = word1.toLowerCase();
        word2 = word2.toLowerCase();

        char tempChar;
        for (int i = 0; i < word1.length(); i++) {
            tempChar = word1.charAt(i);
            if (!word2.contains(String.valueOf(tempChar))) {
                return false;
            }

        }
        System.out.println("word " + word1 + " is anagram of " + word2);
        return true;
    }
}
