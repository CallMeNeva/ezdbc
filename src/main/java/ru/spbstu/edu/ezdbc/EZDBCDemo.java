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

import ru.spbstu.edu.ezdbc.rs.consume.ResultSetConsumer;
import ru.spbstu.edu.ezdbc.rs.consume.ResultSetPrinter;
import ru.spbstu.edu.ezdbc.sql.expr.BinaryExpression;
import ru.spbstu.edu.ezdbc.sql.expr.Expression;
import ru.spbstu.edu.ezdbc.sql.expr.id.Identifier;
import ru.spbstu.edu.ezdbc.sql.expr.literal.IntLiteral;
import ru.spbstu.edu.ezdbc.sql.expr.op.LogicalOperator;
import ru.spbstu.edu.ezdbc.sql.query.Selectable;
import ru.spbstu.edu.ezdbc.sql.query.SelectionQuery;
import ru.spbstu.edu.ezdbc.url.Host;
import ru.spbstu.edu.ezdbc.url.Port;
import ru.spbstu.edu.ezdbc.url.Protocol;
import ru.spbstu.edu.ezdbc.url.URLBuilder;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class EZDBCDemo {

    public static void main(String[] args) throws SQLException {
        // Greet the user
        System.out.println("Hello World! This is an EZDBC demo.");

        // Create URL and establish a connection
        Port port = new Port(3306);
        Host host = Host.localhostWithPort(port);
        String url = new URLBuilder(Protocol.MYSQL)
                .addHost(host)
                .setSchemaName("mysql")
                .setProperty("user", "root")
                .setProperty("password", "Root1@Root2")
                .build();
        Connection connection = DriverManager.getConnection(url);

        // Create and execute query
        Identifier tableNameColumnId = new Identifier("table_name");
        Identifier lastUpdateColumnId = new Identifier("last_update");
        List<Selectable> selections = List.of(tableNameColumnId, lastUpdateColumnId);

        Expression filter = new BinaryExpression(
                new Identifier("stat_value"),
                LogicalOperator.GREATER_THAN,
                new IntLiteral(2)
        );

        SelectionQuery query = new SelectionQuery("innodb_index_stats");
        query.setSelections(selections);
        query.setFilter(filter);
        ResultSet resultSet = query.execute(connection);

        // Process ResultSet
        ResultSetConsumer consumer = ResultSetPrinter.simple(System.out, " | ", tableNameColumnId.name(), lastUpdateColumnId.name());
        consumer.consumeSafelyAndClose(resultSet, System.err::println);

        // Is our ResultSet closed?
        System.out.println("ResultSet closed: " + resultSet.isClosed());
    }
}
