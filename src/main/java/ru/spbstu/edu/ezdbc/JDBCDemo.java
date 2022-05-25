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

package ru.spbstu.edu.ezdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

public class JDBCDemo {

    public static void main(String[] args) throws SQLException {
        // Greet the user
        System.out.println("Hello World! This is a JDBC demo.");

        // Create URL and establish a connection
        int port = 3306;
        String host = "localhost:" + port;
        String schemaName = "mysql";
        String url = String.format("jdbc:mysql://%s/%s", host, schemaName);
        Connection connection = DriverManager.getConnection(url, "root", "Root1@Root2");

        // Create and execute query
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT table_name, last_update FROM innodb_index_stats WHERE stat_value > 2;");

        // Process ResultSet - in this case, print comma-delimited to stdout
        while (resultSet.next()) {
            String tableName = resultSet.getString("table_name");
            Timestamp lastUpdate = resultSet.getTimestamp("last_update");

            String row = String.join(" | ", tableName, lastUpdate.toString());
            System.out.println(row);
        }

        // Is our ResultSet closed?
        System.out.println("ResultSet closed: " + resultSet.isClosed());
    }
}
