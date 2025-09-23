package com.inventory.utils;

import java.util.Scanner;

public class InputHelper {
    private static Scanner scanner = new Scanner(System.in);

    public static String getString(String prompt) {
        System.out.print(prompt + ": ");
        return scanner.nextLine();
    }

    public static int getInt(String prompt) {
        System.out.print(prompt + ": ");
        return scanner.nextInt();
    }
}
