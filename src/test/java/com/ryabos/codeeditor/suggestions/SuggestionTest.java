package com.ryabos.codeeditor.suggestions;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class SuggestionTest {
    @Test
    void equalityTest() {
        Assertions.assertThat(new Suggestion("qwe", 4)).isEqualTo(new Suggestion("qwe", 4));
        Assertions.assertThat(new Suggestion("qwe", 4)).isNotEqualTo(new Suggestion("qweasd", 4));
        Assertions.assertThat(new Suggestion("qwe", 4)).isNotEqualTo(new Suggestion("qwe", 5));
    }
}