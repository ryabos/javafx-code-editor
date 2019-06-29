package com.ryabos.colors;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.ryabos.colors.Color.*;

class ColorSupplierTest {
    private static final String commandPattern = "send -text(<text>)";
    private ColorSupplier supplier;

    @BeforeEach
    void setUp() {
        supplier = new ColorSupplier(commandPattern);
    }

    @Test
    void arbitraryString() {
        Color[] colors = supplier.apply("qwe");

        Assertions.assertThat(colors).hasSize(3).containsOnly(Color.ORDINARY);
    }

    @Test
    void commentedString() {
        Color[] colors = supplier.apply("//qwe");

        Assertions.assertThat(colors).hasSize(5).containsOnly(Color.COMMENTED);
    }

    @Test
    void keyword() {
        Color[] colors = supplier.apply("send");

        Assertions.assertThat(colors).hasSize(4).containsOnly(KEYWORD);
    }

    @Test
    void commentedKeyword() {
        Color[] colors = supplier.apply("//send");

        Assertions.assertThat(colors).hasSize(6).containsOnly(Color.COMMENTED);
    }

    @Test
    void commandWithCompleteParameter() {
        Color[] colors = supplier.apply("send -text(hello)");

        for (int i = 0; i < 4; i++) {
            Assertions.assertThat(colors[i]).isEqualTo(KEYWORD);
        }
        Assertions.assertThat(colors[4]).isEqualTo(ORDINARY);
        for (int i = 5; i < 10; i++) {
            Assertions.assertThat(colors[i]).isEqualTo(PARAMETER);
        }
        Assertions.assertThat(colors[10]).isEqualTo(ORDINARY);
        for (int i = 11; i < 16; i++) {
            Assertions.assertThat(colors[i]).isEqualTo(ARGUMENT);
        }
        Assertions.assertThat(colors[16]).isEqualTo(ORDINARY);
    }

    @Test
    void commandWithIncompleteParameter() {
        Color[] colors = supplier.apply("send -text(hell");

        for (int i = 0; i < 4; i++) {
            Assertions.assertThat(colors[i]).isEqualTo(KEYWORD);
        }
        for (int i = 4; i < 15; i++) {
            Assertions.assertThat(colors[i]).isEqualTo(ORDINARY);
        }
    }
}