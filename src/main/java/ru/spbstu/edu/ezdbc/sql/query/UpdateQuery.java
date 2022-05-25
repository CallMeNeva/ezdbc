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

import ru.spbstu.edu.ezdbc.sql.expr.Expression;
import ru.spbstu.edu.ezdbc.sql.expr.id.Identifier;
import ru.spbstu.edu.ezdbc.sql.expr.literal.AbstractLiteral;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

// https://www.w3schools.com/sql/sql_update.asp
public final class UpdateQuery extends AbstractModificationQuery {

    private Map<Identifier, AbstractLiteral<?>> updateMappings;
    private Expression condition;

    public UpdateQuery(Identifier tableIdentifier, Map<Identifier, AbstractLiteral<?>> updateMappings, Expression condition) {
        super(tableIdentifier);
        setUpdateMappings(updateMappings);
        setCondition(condition);
    }

    public UpdateQuery(Identifier tableIdentifier, Map<Identifier, AbstractLiteral<?>> updateMappings) {
        this(tableIdentifier, updateMappings, null);
    }

    public UpdateQuery(String tableName, Map<Identifier, AbstractLiteral<?>> updateMappings, Expression condition) {
        super(tableName);
        setUpdateMappings(updateMappings);
        setCondition(condition);
    }

    public UpdateQuery(String tableName, Map<Identifier, AbstractLiteral<?>> updateMappings) {
        this(tableName, updateMappings, null);
    }

    public Map<Identifier, AbstractLiteral<?>> getUpdateMappings() {
        return updateMappings;
    }

    public void setUpdateMappings(Map<Identifier, AbstractLiteral<?>> updateMappings) {
        this.updateMappings = Objects.requireNonNull(updateMappings);
    }

    public Expression getCondition() {
        return condition;
    }

    public void setCondition(Expression condition) {
        this.condition = condition;
    }

    @Override
    public String toSQLString() {
        if (updateMappings.isEmpty()) {
            throw new IllegalStateException("No columns and values specified for update query");
        }

        StringBuilder sb = new StringBuilder("UPDATE ");
        sb.append(getTableName());
        sb.append(" SET ");

        String joinedPairs = updateMappings.entrySet()
                .stream()
                .map(entry -> {
                    Identifier column = entry.getKey();
                    AbstractLiteral<?> value = entry.getValue();
                    return column.toSQLString() + '=' + value.toSQLString();
                })
                .collect(Collectors.joining(", "));
        sb.append(joinedPairs);

        if (condition != null) {
            sb.append(" WHERE ");
            sb.append(condition.toSQLString());
        }

        return sb.toString();
    }

    // TODO: Equals, hashCode and toString
}
