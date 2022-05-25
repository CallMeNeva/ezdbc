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


import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class ProtocolTests {

    @Nested
    class StringFormValidation {

        @ParameterizedTest
        @MethodSource("provideFormTestArgs")
        void expectedFormMatchesActual(Protocol protocol, String expectedStringForm) {
            assertThat(protocol.toString()).isEqualTo(expectedStringForm);
        }

        private static Stream<Arguments> provideFormTestArgs() {
            Arguments mysqlCase = Arguments.of(Protocol.MYSQL, "jdbc:mysql:");
            Arguments postgresqlCase = Arguments.of(Protocol.POSTGRESQL, "jdbc:postgresql:");
            Arguments oracleCase = Arguments.of(Protocol.ORACLE_THIN, "jdbc:oracle:thin:");
            Arguments sqlServerCase = Arguments.of(Protocol.SQL_SERVER, "jdbc:sqlserver:");
            Arguments redshiftCase = Arguments.of(Protocol.REDSHIFT, "jdbc:redshift:");
            return Stream.of(mysqlCase, postgresqlCase, oracleCase, sqlServerCase, redshiftCase);
        }
    }
}
