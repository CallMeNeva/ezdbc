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
import ru.spbstu.edu.ezdbc.sql.expr.id.Identifier;
import ru.spbstu.edu.ezdbc.sql.expr.literal.AbstractLiteral;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public final class InsertionQuery extends AbstractModificationQuery {

    private Map<Identifier, AbstractLiteral<?>> insertionMappings;

    public InsertionQuery(Identifier tableIdentifier, Map<Identifier, AbstractLiteral<?>> insertionMappings) {
        super(tableIdentifier);
        setInsertionMappings(insertionMappings);
    }

    public InsertionQuery(String tableName, Map<Identifier, AbstractLiteral<?>> insertionMappings) {
        super(tableName);
        setInsertionMappings(insertionMappings);
    }

    public Map<Identifier, AbstractLiteral<?>> getInsertionMappings() {
        return insertionMappings;
    }

    public void setInsertionMappings(Map<Identifier, AbstractLiteral<?>> insertionMappings) {
        this.insertionMappings = Objects.requireNonNull(insertionMappings);
    }

    @Override
    public String toSQLString() {
        if (insertionMappings.isEmpty()) {
            throw new IllegalStateException("No columns and values specified for insertion query");
        }

        String packedColumns = pack(insertionMappings.keySet());
        String packedValues = pack(insertionMappings.values());
        return String.format("INSERT INTO %s %s VALUES %s", getTableIdentifier().toSQLString(), packedColumns, packedValues);
    }

    // Joins a Collection of stuff into the following form: (item1, item2, item3)
    // https://www.w3schools.com/sql/sql_insert.asp
    private static <T extends SQLConvertible> String pack(Collection<T> items) {
        return items.stream()
                .map(SQLConvertible::toSQLString)
                .collect(Collectors.joining(", ", "(", ")"));
    }

    // TODO: Equals, hashCode and toString
}
