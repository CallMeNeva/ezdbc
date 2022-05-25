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

package ru.spbstu.edu.ezdbc.sql.expr.op;

import ru.spbstu.edu.ezdbc.sql.SQLConvertible;

public enum LogicalOperator implements SQLConvertible {
    AND("AND", Type.BINARY),
    EQUAL_TO("=", Type.BINARY),
    GREATER_THAN(">", Type.BINARY),
    GREATER_THAN_OR_EQUAL_TO(">=", Type.BINARY),
    LESS_THAN("<", Type.BINARY),
    LESS_THAN_OR_EQUAL_TO("<=", Type.BINARY),
    NOT("NOT", Type.UNARY),
    NOT_EQUAL_TO("<>", Type.BINARY),
    OR("OR", Type.BINARY);

    public enum Type {
        UNARY,
        BINARY;
    }

    private final String syntax;
    private final Type type;

    LogicalOperator(String syntax, Type type) {
        this.syntax = syntax;
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public boolean isUnary() {
        return type == Type.UNARY;
    }

    public boolean isBinary() {
        return type == Type.BINARY;
    }

    @Override
    public String toSQLString() {
        return syntax;
    }
}
