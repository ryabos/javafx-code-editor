package com.ryabos.codeeditor;

public class Suggestion {
    private final String suggestion;
    private final int caretPosition;

    public Suggestion(String suggestion, int caretPosition) {
        this.suggestion = suggestion;
        this.caretPosition = caretPosition;
    }

    @Override
    public String toString() {
        return "Suggestion{" +
                "suggestion='" + suggestion + '\'' +
                ", caretPosition=" + caretPosition +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Suggestion that = (Suggestion) o;

        if (getCaretPosition() != that.getCaretPosition()) return false;
        return getSuggestion().equals(that.getSuggestion());
    }

    @Override
    public int hashCode() {
        int result = getSuggestion().hashCode();
        result = 31 * result + getCaretPosition();
        return result;
    }

    public String getSuggestion() {
        return suggestion;
    }

    public int getCaretPosition() {
        return caretPosition;
    }
}
