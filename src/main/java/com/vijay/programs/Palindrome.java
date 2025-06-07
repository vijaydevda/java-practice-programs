package com.vijay.programs;

public class Palindrome {

    public static boolean check(String word) {
        System.out.println("word for checking " + word);
        char[] charArray = word.toCharArray();

        for (int i = 0; i < charArray.length/2; i++) {

            if (charArray[i] != charArray[charArray.length - i - 1]) {
                System.out.println("word is not a palindrome");
                return false;
            }
        }
        System.out.println("word is palindrome");
        return true;
    }
}
