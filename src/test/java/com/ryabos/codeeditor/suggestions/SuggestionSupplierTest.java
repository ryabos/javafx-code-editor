package com.ryabos.codeeditor.suggestions;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

class SuggestionSupplierTest {
    private static final String commandPattern = "send" +
            " -text(<text>)" +
            " -method(email|sms)" +
            " -addressee({name})" +
            " [-theme(<theme>)]" +
            " [-priority(1|2|3)]" +
            " [-tags({tag})]";
    private SuggestionSupplier supplier;

    @BeforeEach
    void setUp() {
        supplier = new SuggestionSupplier(commandPattern);
    }

    @Test
    void when_arbitraryStringIsPassed_then_emptyListIsReturned() {
        SuggestionList suggestion = supplier.apply("qwe");

        List<Suggestion> suggestions = suggestion.getSuggestions();

        Assertions.assertThat(suggestions).isEmpty();
    }

    @Test
    void when_unfinishedCommandNameIsPassed_then_commandNameIsReturnedWIthFirstParameter() {
        SuggestionList suggestion = supplier.apply("se");

        List<Suggestion> suggestions = suggestion.getSuggestions();

        Assertions.assertThat(suggestions).containsExactly(new Suggestion("nd -text()", 11));
    }

    @Test
    void when_finishedValidCommandIsPassed_then_emptyListIsReturned() {
        SuggestionList suggestion = supplier.apply("send" +
                " -text(hello)" +
                " -method(sms)" +
                " -addressee(SaintP, Moscow23)" +
                " -theme(message)" +
                " -priority(1)" +
                " -tags(vacation, work)");

        List<Suggestion> suggestions = suggestion.getSuggestions();

        Assertions.assertThat(suggestions).isEmpty();
    }

    @Test
    void when_unfinishedValidCommandIsPassed_then_parameterIsReturned() {
        SuggestionList suggestion = supplier.apply("send" +
                " -text(hello)");

        List<Suggestion> suggestions = suggestion.getSuggestions();

        Assertions.assertThat(suggestions).containsExactly(new Suggestion(" -method()", 26));
    }

    @Test
    void when_unfinishedValidCommandIsPassed_then_parameterIsReturned_2() {
        SuggestionList suggestion = supplier.apply("send -");

        List<Suggestion> suggestions = suggestion.getSuggestions();

        Assertions.assertThat(suggestions).containsExactly(new Suggestion("text()", 11));
    }

    @Test
    void when_unfinishedValidCommandIsPassed_then_parameterIsReturned_3() {
        SuggestionList suggestion = supplier.apply("send");

        List<Suggestion> suggestions = suggestion.getSuggestions();

        Assertions.assertThat(suggestions).containsExactly(new Suggestion(" -text()", 11));
    }

    @Test
    void when_unfinishedValidCommandIsPassed_then_parameterIsReturned_4() {
        SuggestionList suggestion = supplier.apply("send ");

        List<Suggestion> suggestions = suggestion.getSuggestions();

        Assertions.assertThat(suggestions).containsExactly(new Suggestion("-text()", 11));
    }

    @Test
    void when_unfinishedValidCommandIsPassed_then_parameterIsReturned_5() {
        SuggestionList suggestion = supplier.apply("send -");

        List<Suggestion> suggestions = suggestion.getSuggestions();

        Assertions.assertThat(suggestions).containsExactly(new Suggestion("text()", 11));
    }
}