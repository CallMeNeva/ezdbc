/*
 * MIT License
 *
 * Copyright (c) 2022 Maxim Altoukhov
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package ru.spbstu.edu.ezdbc.url;

import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.assertj.core.api.Assumptions.assumeThatCode;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
class PortTests {

    @Nested
    @Order(1)
    class ValueValidation {

        @ParameterizedTest
        @ValueSource(ints = {-1, 65536, Integer.MIN_VALUE, Integer.MAX_VALUE})
        void rejectsInvalid(int value) {
            assertThatIllegalArgumentException().isThrownBy(() -> new Port(value));
        }

        @ParameterizedTest
        @ValueSource(ints = {0, 1024, 49152, 65535, 3306, 1433, 1521, 5432})
        void acceptsValid(int value) {
            assertThatNoException().isThrownBy(() -> new Port(value));
        }
    }

    @Nested
    @Order(2)
    class StringFormValidation {

        @ParameterizedTest
        @MethodSource("provideFormTestArgs")
        void expectedFormMatchesActual(int portValue, String expectedStringForm) {
            assumeThatCode(() -> new Port(portValue)).doesNotThrowAnyException();
            Port port = new Port(portValue);

            assertThat(port.toString()).isEqualTo(expectedStringForm);
        }

        private static Stream<Arguments> provideFormTestArgs() {
            Arguments case1 = Arguments.of(0, "0");
            Arguments case2 = Arguments.of(1024, "1024");
            Arguments case3 = Arguments.of(1433, "1433");
            Arguments case4 = Arguments.of(1521, "1521");
            Arguments case5 = Arguments.of(3306, "3306");
            Arguments case6 = Arguments.of(5432, "5432");
            Arguments case7 = Arguments.of(65535, "65535");
            return Stream.of(case1, case2, case3, case4, case5, case6, case7);
        }
    }

    @Nested
    @Order(3)
    class StringParseValidation {

        @Test
        void rejectsNull() {
            assertThatNullPointerException().isThrownBy(() -> Port.parse(null));
        }

        @ParameterizedTest
        @ValueSource(strings = {"Twenty One", "Twenty 1", "-21", "65536"})
        @EmptySource
        void rejectsInvalid(String s) {
            assertThatIllegalArgumentException().isThrownBy(() -> Port.parse(s));
        }

        // FIXME: Besides no exceptions, we also need to check whether the actual produced instance is what we expect.
        @ParameterizedTest
        @ValueSource(strings = {"0", "1024", "49152", "65535", "3306", "1433", "1521", "5432"})
        void acceptsValid(String s) {
            assertThatNoException().isThrownBy(() -> Port.parse(s));
        }
    }
}
