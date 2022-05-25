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

package ru.spbstu.edu.ezdbc.sql.expr.literal;

import ru.spbstu.edu.ezdbc.sql.expr.Expression;

public abstract class AbstractLiteral<T> implements Expression {

    private static final String SQL_NULL_VALUE = "NULL";
    private static final char QUOTE = '\'';

    private final T value;
    private final boolean requiresQuotes;

    protected AbstractLiteral(T value, boolean requiresQuotes) {
        this.value = value;
        this.requiresQuotes = requiresQuotes;
    }

    public final T getValue() {
        return value;
    }

    public final boolean requiresQuotes() {
        return requiresQuotes;
    }

    // TODO: Equals, hashCode and toString (final)

    @Override
    public final String toSQLString() {
        if (value == null) {
            return SQL_NULL_VALUE;
        }

        String raw = toSQLStringImpl(value);
        return requiresQuotes ? (QUOTE + raw + QUOTE) : raw;
    }

    protected String toSQLStringImpl(T value) {
        return value.toString(); // Default impl. Override for a custom conversion.
    }
}
