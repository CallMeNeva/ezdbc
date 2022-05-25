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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
class URLBuilderTests {

    @Nested
    @Order(1)
    class ParameterValidation {

        private URLBuilder builderUnderTest;

        @BeforeEach
        void recreateBuilderUnderTest() {
            builderUnderTest = new URLBuilder(Protocol.MYSQL);
        }

        @Nested
        class ProtocolValidation {

            @Test
            void rejectsNullOnConstruction() {
                assertThatNullPointerException().isThrownBy(() -> new URLBuilder(null));
            }

            @Test
            void rejectsNullOnSet() {
                assertThatNullPointerException().isThrownBy(() -> builderUnderTest.setProtocol(null));
            }
        }

        @Nested
        class ProtocolSuffixValidation {

            @ParameterizedTest
            @ValueSource(strings = {" ://", ":// ", ": //", " ", "\t", "\n"})
            @EmptySource
            void rejectsInvalid(String suffix) {
                assertThatIllegalArgumentException().isThrownBy(() -> builderUnderTest.setProtocolSuffix(suffix));
            }

            @ParameterizedTest
            @ValueSource(strings = {"://", "@"})
            @NullSource
            void acceptsValid(String suffix) {
                assertThatNoException().isThrownBy(() -> builderUnderTest.setProtocolSuffix(suffix));
            }
        }

        @Nested
        class HostsValidation {

            @Test
            void rejectsNullHost() {
                assertThatNullPointerException().isThrownBy(() -> builderUnderTest.addHost(null));
            }

            @ParameterizedTest
            @MethodSource("provideInvalidIterables")
            @NullSource
            void rejectsInvalidIterables(Iterable<Host> iterable) {
                assertThatNullPointerException().isThrownBy(() -> builderUnderTest.addHosts(iterable));
            }

            private static Stream<Arguments> provideInvalidIterables() {
                List<Host> hosts = new ArrayList<>(); // Use ArrayList instead of List::of because the former allows nulls
                hosts.add(Host.LOCALHOST);
                hosts.add(null);

                Arguments arg = Arguments.of(hosts);
                return Stream.of(arg);
            }

            @ParameterizedTest
            @MethodSource("provideInvalidVarargs")
            @NullSource
            void rejectsInvalidVarargs(Host[] varargs) {
                assertThatNullPointerException().isThrownBy(() -> builderUnderTest.addHosts(varargs));
            }

            private static Stream<Arguments> provideInvalidVarargs() {
                Host[] hosts = {Host.LOCALHOST, null};

                Arguments arg = Arguments.of((Object) hosts);
                return Stream.of(arg);
            }
        }

        @Nested
        class SchemaNameValidation {

            @ParameterizedTest
            @ValueSource(strings = {"   ", "\t \t", "\t \n", "\n\n\n"})
            @EmptySource
            void rejectsInvalid(String schemaName) {
                assertThatIllegalArgumentException().isThrownBy(() -> builderUnderTest.setSchemaName(schemaName));
            }

            @ParameterizedTest
            @ValueSource(strings = {"customers", "online orders"})
            @NullSource
            void acceptsValid(String schemaName) {
                assertThatNoException().isThrownBy(() -> builderUnderTest.setSchemaName(schemaName));
            }
        }

        @Nested
        class PropertyListPrefixValidation {

            @Test
            void rejectsNull() {
                assertThatNullPointerException().isThrownBy(() -> builderUnderTest.setPropertyListPrefix(null));
            }

            @ParameterizedTest
            @ValueSource(strings = {" ?", "? ", " ? ", "HELLO WORLD!", "   ", "\t", "\n"})
            @EmptySource
            void rejectsInvalid(String prefix) {
                assertThatIllegalArgumentException().isThrownBy(() -> builderUnderTest.setPropertyListPrefix(prefix));
            }

            @ParameterizedTest
            @ValueSource(strings = {"?", "&", "technically_ok_prefix"})
            void acceptsValid(String prefix) {
                assertThatNoException().isThrownBy(() -> builderUnderTest.setPropertyListPrefix(prefix));
            }
        }

        @Nested
        class PropertyValidation {

            @ParameterizedTest
            @MethodSource("provideNulls")
            void rejectsNulls(String name, String value) {
                assertThatNullPointerException().isThrownBy(() -> builderUnderTest.setProperty(name, value));
            }

            private static Stream<Arguments> provideNulls() {
                Arguments nullNameCase = Arguments.of(null, "test");
                Arguments nullValueCase = Arguments.of("test", null);
                return Stream.of(nullNameCase, nullValueCase);
            }

            @ParameterizedTest
            @MethodSource("provideInvalid")
            void rejectsInvalid(String name, String value) {
                assertThatIllegalArgumentException().isThrownBy(() -> builderUnderTest.setProperty(name, value));
            }

            private static Stream<Arguments> provideInvalid() {
                Arguments whitespaceInNameCase = Arguments.of("user name", "root");
                Arguments blankNameCase = Arguments.of("\t\n", "hello");
                Arguments emptyNameCase = Arguments.of("", "world");
                Arguments blankValueCase = Arguments.of("name", "   ");
                Arguments emptyValueCase = Arguments.of("password", "");

                return Stream.of(whitespaceInNameCase, blankNameCase, emptyNameCase, blankValueCase, emptyValueCase);
            }
        }

        @Nested
        class PropertyDelimiterValidation {

            @Test
            void rejectsNull() {
                assertThatNullPointerException().isThrownBy(() -> builderUnderTest.setPropertyDelimiter(null));
            }

            @ParameterizedTest
            @ValueSource(strings = {" &", "& ", " & ", "HELLO WORLD!", "   ", "\t", "\n"})
            @EmptySource
            void rejectsInvalid(String prefix) {
                assertThatIllegalArgumentException().isThrownBy(() -> builderUnderTest.setPropertyDelimiter(prefix));
            }

            @ParameterizedTest
            @ValueSource(strings = {",", "&", "technically_ok_delim"})
            void acceptsValid(String prefix) {
                assertThatNoException().isThrownBy(() -> builderUnderTest.setPropertyDelimiter(prefix));
            }
        }
    }

    @Nested
    @Order(2)
    class ResultValidation {

        @ParameterizedTest
        @MethodSource("provideResultTestArgs")
        void expectedResultMatchesActual(URLBuilder builderConfig, String expectedResult) {
            // FIXME: We should first explicitly assume that builder setters won't actually reject valid values

            assertThat(builderConfig.build()).isEqualTo(expectedResult);
        }

        private static Stream<Arguments> provideResultTestArgs() {
            URLBuilder mysqlLocalhostNoPort = new URLBuilder(Protocol.MYSQL)
                    .setProtocolSuffix("//")
                    .addHost(Host.LOCALHOST)
                    .setSchemaName("sakila");
            String mysqlLocalHostNoPortExpectedUrl = "jdbc:mysql://localhost/sakila";
            Arguments arg1 = Arguments.of(mysqlLocalhostNoPort, mysqlLocalHostNoPortExpectedUrl);

            URLBuilder postgresqlSchemaOnly = new URLBuilder(Protocol.POSTGRESQL)
                    .setProtocolSuffix(null)
                    .setSchemaName("test_db");
            String postgresqlSchemaOnlyExpectedUrl = "jdbc:postgresql:test_db";
            Arguments arg2 = Arguments.of(postgresqlSchemaOnly, postgresqlSchemaOnlyExpectedUrl);

            URLBuilder oraclePrefix = new URLBuilder(Protocol.ORACLE_THIN)
                    .setProtocolSuffix('@')
                    .addHost(Host.localhostWithPort(new Port(1521)))
                    .setSchemaName("test_db");
            String oraclePrefixExpectedUrl = "jdbc:oracle:thin:@localhost:1521/test_db";
            Arguments arg3 = Arguments.of(oraclePrefix, oraclePrefixExpectedUrl);

            URLBuilder sqlServerNoHostsMultiParams = new URLBuilder(Protocol.SQL_SERVER)
                    .setProtocolSuffix("//")
                    .setPropertyListPrefix(';')
                    .setPropertyDelimiter(';')
                    .setProperty("servername", "server_name")
                    .setProperty("integratedSecurity", true);
            String sqlServerNoHostsMultiParamsExpectedUrl = "jdbc:sqlserver://;servername=server_name;integratedSecurity=true";
            Arguments arg4 = Arguments.of(sqlServerNoHostsMultiParams, sqlServerNoHostsMultiParamsExpectedUrl);

            URLBuilder redshiftMultiHost = new URLBuilder(Protocol.REDSHIFT)
                    .setProtocolSuffix("//")
                    .addHosts(new Host("example.org"), new Host("example.com"))
                    .setSchemaName("MyBusiness")
                    .setPropertyListPrefix('?')
                    .setPropertyDelimiter('?')
                    .setProperty("ssl", false);
            String redshiftMultiHostExpectedUrl = "jdbc:redshift://example.org,example.com/MyBusiness?ssl=false";
            Arguments arg5 = Arguments.of(redshiftMultiHost, redshiftMultiHostExpectedUrl);

            return Stream.of(arg1, arg2, arg3, arg4, arg5);
        }
    }
}
