package com.ryabos.codeeditor;

import java.util.Scanner;

public class SuggestionSupplier {
    private final String pattern;
    private final String regex;

    public SuggestionSupplier(String pattern) {
        this.pattern = pattern;
        regex = pattern
                .replaceAll("\\(", "\\\\((")
                .replaceAll("\\)", ")\\\\)")
                .replaceAll(" \\[", " (\\s*")
                .replaceAll("]", ")?")
                .replaceAll("<\\w+>", "\\\\w+")
                .replaceAll("\\{[\\w, ]+}", "[\\\\w,\\s]+");
    }

    public SuggestionList apply(String command) {
        Scanner regexScanner = new Scanner(regex);
        Scanner commandScanner = new Scanner(command);
        Scanner patternScanner = new Scanner(pattern);
        if (regexScanner.hasNext() && commandScanner.hasNext() && patternScanner.hasNext()) {
            String commandStart = commandScanner.next();
            String regexStart = regexScanner.next();
            String patternStart = patternScanner.next();
            if (commandStart.matches(regexStart)) {
                while (regexScanner.hasNext() && commandScanner.hasNext() && patternScanner.hasNext()) {
                    StringBuilder commandFragment = new StringBuilder(commandScanner.next());
                    while (commandScanner.hasNext() && commandFragment.toString().endsWith(",")) {
                        commandFragment.append(" ").append(commandScanner.next());
                    }
                    String regexFragment = regexScanner.next();
                    String patternFragment = patternScanner.next();

                    if (commandFragment.toString().matches(regexFragment)) {
                        if (!commandScanner.hasNext() && patternScanner.hasNext()) {
                            String result = (command.endsWith(" ") ? "" : " ") +
                                    patternScanner.next().replaceAll("\\(.+\\)", "()");
                            SuggestionList suggestion = new SuggestionList();
                            suggestion.add(new Suggestion(result, command.length() + result.lastIndexOf('(' + 1)));
                            return suggestion;
                        }
                    } else if (patternFragment.startsWith(commandFragment.toString())) {
                        SuggestionList suggestion = new SuggestionList();
                        String result = patternFragment.substring(commandFragment.length()).replaceAll("\\(.+\\)", "()");
                        suggestion.add(new Suggestion(result, command.length() + result.lastIndexOf('(' + 1)));
                        return suggestion;
                    }
                }
                SuggestionList suggestion = new SuggestionList();
                if (patternScanner.hasNext()) {
                    String result = (command.endsWith(" ") ? "" : " ") + patternScanner.next().replaceAll("\\(.+\\)", "()");
                    suggestion.add(new Suggestion(result, command.length() + result.lastIndexOf('(' + 1)));
                }
                return suggestion;
            } else if (patternStart.startsWith(commandStart)) {
                StringBuilder builder = new StringBuilder(patternStart.substring(commandStart.length()));
                if (patternScanner.hasNext()) {
                    builder.append(" ").append(patternScanner.next().replaceAll("\\(.+\\)", "()"));
                }
                SuggestionList suggestion = new SuggestionList();
                String result = builder.toString();
                suggestion.add(new Suggestion(result, commandStart.length() + result.indexOf('(' + 1)));
                return suggestion;
            } else {
                return new SuggestionList();
            }
        } else {
            return new SuggestionList();
        }
    }
}
