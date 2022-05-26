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

package ru.spbstu.edu.ezdbc.sql.query;

import ru.spbstu.edu.ezdbc.sql.SQLConvertible;
import ru.spbstu.edu.ezdbc.sql.expr.Expression;
import ru.spbstu.edu.ezdbc.sql.expr.id.Identifier;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

// https://www.w3schools.com/sql/sql_select.asp
public final class SelectionQuery extends AbstractSQLQuery<ResultSet> {

    private static final String WILDCARD_SYNTAX = "*";

    private List<Selectable> selections;
    private Expression filter;

    public SelectionQuery(Identifier tableIdentifier) {
        super(tableIdentifier);
        setSelections((List<Selectable>) null);
        setFilter(null);
    }

    public SelectionQuery(String tableName) {
        super(tableName);
        setSelections((List<Selectable>) null);
        setFilter(null);
    }

    public List<Selectable> getSelections() {
        return selections;
    }

    public void setSelections(List<Selectable> selections) {
        this.selections = selections;
    }

    public void setSelections(Selectable... selections) {
        this.selections = Arrays.asList(selections);
    }

    public Expression getFilter() {
        return filter;
    }

    public void setFilter(Expression filter) {
        this.filter = filter;
    }

    @Override
    public String toSQLString() {
        String joinedSelections = (selections == null || selections.isEmpty()) ? WILDCARD_SYNTAX : selections.stream()
                .map(SQLConvertible::toSQLString)
                .collect(Collectors.joining(", "));

        StringBuilder sb = new StringBuilder("SELECT ")
                .append(joinedSelections)
                .append(" FROM ")
                .append(getTableName());

        if (filter != null) {
            sb.append(" WHERE ");
            sb.append(filter.toSQLString());
        }

        return sb.toString();
    }

    @Override
    public ResultSet execute(Statement statement) throws SQLException {
        String sql = toSQLString();
        return statement.executeQuery(sql);
    }

    @Override
    public ResultSet execute(Connection connection) throws SQLException {
        // Do NOT use try-with-resources because else ResultSet closes as well. This is not the case for modification queries, so we
        // override the default behaviour specifically for SelectionQuery instead of changing it for everyone.
        Statement statement = connection.createStatement();
        return execute(statement);
    }

    // TODO: Equals, hashCode and toString
}
