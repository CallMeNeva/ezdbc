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

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
class HostTests {

    @Nested
    @Order(1)
    class HostnameValidation {

        @Test
        void rejectsNull() {
            assertThatNullPointerException().isThrownBy(() -> new Host(null, null));
        }

        @ParameterizedTest
        @ValueSource(strings = {"Hello World!", "example . com", " ", "\t", "\n"})
        @EmptySource
        void rejectsInvalid(String hostname) {
            assertThatIllegalArgumentException().isThrownBy(() -> new Host(hostname, null));
        }
    }

    @Nested
    @Order(2)
    class StringFormValidation {

        @ParameterizedTest
        @MethodSource("provideFormTestArgs")
        void expectedFormMatchesActual(Host host, String expectedStringForm) {
            assertThat(host.toString()).isEqualTo(expectedStringForm);
        }

        private static Stream<Arguments> provideFormTestArgs() {
            Arguments caseWithPort = Arguments.of(new Host("example.com", new Port(3306)), "example.com:3306");
            Arguments caseWithoutPort = Arguments.of(new Host("example.org", null), "example.org");
            return Stream.of(caseWithPort, caseWithoutPort);
        }
    }

    @Nested
    @Order(3)
    class StringParseValidation {

        @Test
        void rejectsNull() {
            assertThatNullPointerException().isThrownBy(() -> Host.parse(null));
        }

        @ParameterizedTest
        @ValueSource(strings = {"Hello World!", "Hello World:36", "\t", "\t:123", "example.com:", "example.org:1287361", "example.org:-1"})
        @EmptySource
        void rejectsInvalid(String s) {
            assertThatIllegalArgumentException().isThrownBy(() -> Host.parse(s));
        }

        // FIXME: Besides no exceptions, we also need to check whether the actual produced instance is what we expect.
        @ParameterizedTest
        @ValueSource(strings = {"localhost", "example.com", "ruz.spbstu.ru", "localhost:0", "example.org:5432", "spbstu.ru:65535"})
        void acceptsValid(String s) {
            assertThatNoException().isThrownBy(() -> Host.parse(s));
        }
    }
}
