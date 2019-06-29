package com.ryabos.colors;

import java.util.Scanner;

public class ColorSupplier {
    private final String pattern;
    private final String regex;

    public ColorSupplier(String pattern) {
        this.pattern = pattern;
        regex = pattern
                .replaceAll("\\(", "\\\\((")
                .replaceAll("\\)", ")\\\\)")
                .replaceAll(" \\[", " (\\s*")
                .replaceAll("]", ")?")
                .replaceAll("<\\w+>", "\\\\w+")
                .replaceAll("\\{[\\w, ]+}", "[\\\\w,\\s]+");
    }

    public Color[] apply(String command) {
        Color[] colors = new Color[command.length()];
        int index = 0;
        if (command.startsWith("//")) {
            for (int i = 0; i < command.length(); i++) {
                colors[index++] = Color.COMMENTED;
            }
            return colors;
        } else {
            Scanner regexScanner = new Scanner(regex);
            Scanner commandScanner = new Scanner(command);
            Scanner patternScanner = new Scanner(pattern);

            if (regexScanner.hasNext() && commandScanner.hasNext() && patternScanner.hasNext()) {
                String commandStart = commandScanner.next();
                String patternStart = patternScanner.next();
                regexScanner.next();

                boolean keywordFound = patternStart.equals(commandStart);
                if (keywordFound) {
                    for (int i = 0; i < commandStart.length(); i++) {
                        colors[index++] = Color.KEYWORD;
                    }
                } else {
                    for (int i = 0; i < commandStart.length(); i++) {
                        colors[index++] = Color.ORDINARY;
                    }
                }

                while (regexScanner.hasNext() && commandScanner.hasNext()) {
                    colors[index++] = Color.ORDINARY;
                    StringBuilder commandFragment = new StringBuilder(commandScanner.next());
                    while (commandScanner.hasNext() && commandFragment.toString().endsWith(",")) {
                        commandFragment.append(" ").append(commandScanner.next());
                    }

                    boolean completeParameterDetected = commandFragment.toString().matches(regexScanner.next());
                    if (completeParameterDetected) {
                        int leftBraceIndex = commandFragment.indexOf("(");
                        for (int i = 0; i < leftBraceIndex; i++) {
                            colors[index++] = Color.PARAMETER;
                        }
                        colors[index++] = Color.ORDINARY;
                        for (int i = leftBraceIndex + 1; i < commandFragment.length() - 1; i++) {
                            colors[index++] = Color.ARGUMENT;
                        }
                        colors[index++] = Color.ORDINARY;
                    } else {
                        for (int i = 0; i < commandFragment.length(); i++) {
                            colors[index++] = Color.ORDINARY;
                        }
                    }
                }
            }
        }
        return colors;
    }
}
