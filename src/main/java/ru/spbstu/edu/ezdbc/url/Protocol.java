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

import java.util.StringJoiner;

/**
 * Defines a JDBC protocol.
 * <p>
 * Registered trademarks and service marks are the property of their respective owners.
 */
public enum Protocol {

    /**
     * Protocol for MySQL connections as per
     * <a href="https://dev.mysql.com/doc/connector-j/8.0/en/connector-j-reference-jdbc-url-format.html">the official documentation</a>.
     */
    MYSQL("mysql"),

    /**
     * Protocol for MySQL load-balancing connections as per
     * <a href="https://dev.mysql.com/doc/connector-j/8.0/en/connector-j-reference-jdbc-url-format.html">the official documentation</a>.
     */
    MYSQL_LOADBALANCE("mysql", "loadbalance"),

    /**
     * Protocol for MySQL replication connections as per
     * <a href="https://dev.mysql.com/doc/connector-j/8.0/en/connector-j-reference-jdbc-url-format.html">the official documentation</a>.
     */
    MYSQL_REPLICATION("mysql", "replication"),

    /**
     * Protocol for MySQL connections that make use of DNS SRV records as per
     * <a href="https://dev.mysql.com/doc/connector-j/8.0/en/connector-j-reference-jdbc-url-format.html">the official documentation</a>.
     */
    MYSQL_SRV("mysql+srv"),

    /**
     * Protocol for MySQL load-balancing connections that make use of DNS SRV records as per
     * <a href="https://dev.mysql.com/doc/connector-j/8.0/en/connector-j-reference-jdbc-url-format.html">the official documentation</a>.
     */
    MYSQL_SRV_LOADBALANCE("mysql+srv", "loadbalance"),

    /**
     * Protocol for MySQL replication connections that make use of DNS SRV records as per
     * <a href="https://dev.mysql.com/doc/connector-j/8.0/en/connector-j-reference-jdbc-url-format.html">the official documentation</a>.
     */
    MYSQL_SRV_REPLICATION("mysql+srv", "replication"),

    /**
     * Protocol for "thin" Oracle Database connections as per
     * <a href="https://docs.oracle.com/en/database/oracle/oracle-database/21/jajdb/">the official documentation</a>.
     */
    ORACLE_THIN("oracle", "thin"),

    /**
     * Protocol for PostgreSQL connections as per
     * <a href="https://jdbc.postgresql.org/documentation/80/connect.html">the official documentation</a>.
     */
    POSTGRESQL("postgresql"),

    /**
     * Protocol for Amazon Redshift connections as per
     * <a href="https://docs.aws.amazon.com/redshift/latest/mgmt/jdbc20-obtain-url.html">the official documentation</a>.
     */
    REDSHIFT("redshift"),

    /**
     * Protocol for Microsoft SQL Server connections as per
     * <a href="https://docs.microsoft.com/en-us/sql/connect/jdbc/building-the-connection-url?view=sql-server-ver15">the official documentation</a>.
     */
    SQL_SERVER("sqlserver");

    /* Support for other DBs can be added on demand */

    private static final String DELIMITER = ":";

    private final String stringForm;

    Protocol(String... subcomponents) {
        StringJoiner joiner = new StringJoiner(DELIMITER, "", DELIMITER);

        joiner.add("jdbc");
        for (String subcomponent : subcomponents) {
            joiner.add(subcomponent);
        }

        stringForm = joiner.toString();
    }

    /**
     * Returns this protocol's string representation. The string's format is {@code jdbc:subcomponent_1:subcomponent_n:}, where the amount of
     * "subcomponents" is not strictly limited, e.g. {@code jdbc:hello:world:}.
     *
     * @return a string (never {@code null})
     */
    @Override
    public String toString() {
        return stringForm;
    }
}
