package com.ryabos.codeeditor;

import java.util.LinkedList;
import java.util.List;

public class SuggestionList {
    private List<Suggestion> suggestions = new LinkedList<>();

    public List<Suggestion> getSuggestions() {
        return new LinkedList<>(suggestions);
    }

    void add(Suggestion suggestion) {
        suggestions.add(suggestion);
    }

    @Override
    public String toString() {
        return "SuggestionList{" +
                "suggestions=" + suggestions +
                '}';
    }
}
