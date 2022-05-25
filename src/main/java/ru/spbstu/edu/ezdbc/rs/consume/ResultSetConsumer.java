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

package ru.spbstu.edu.ezdbc.rs.consume;

import ru.spbstu.edu.ezdbc.util.fn.ThrowableHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

@FunctionalInterface
public interface ResultSetConsumer {

    void consume(ResultSet rs) throws SQLException;

    default void consumeSafely(ResultSet rs, ThrowableHandler<SQLException> exceptionHandler) {
        Objects.requireNonNull(exceptionHandler);
        try {
            consume(rs);
        } catch (SQLException e) {
            exceptionHandler.handle(e);
        }
    }

    default void consumeAndClose(ResultSet rs) throws SQLException {
        try (rs) {
            consume(rs);
        }
    }

    default void consumeSafelyAndClose(ResultSet rs, ThrowableHandler<SQLException> exceptionHandler) {
        Objects.requireNonNull(exceptionHandler);
        try (rs) {
            consume(rs);
        } catch (SQLException e) {
            exceptionHandler.handle(e);
        }
    }
}
