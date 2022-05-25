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

import ru.spbstu.edu.ezdbc.util.fn.ThrowingConsumer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public class PerRowResultSetConsumer implements ResultSetConsumer {

    private final ThrowingConsumer<ResultSet, SQLException> rowConsumer;

    public PerRowResultSetConsumer(ThrowingConsumer<ResultSet, SQLException> rowConsumer) {
        this.rowConsumer = Objects.requireNonNull(rowConsumer);
    }

    public ThrowingConsumer<ResultSet, SQLException> getRowConsumer() {
        return rowConsumer;
    }

    @Override
    public void consume(ResultSet rs) throws SQLException {
        Objects.requireNonNull(rs);
        while (rs.next()) {
            rowConsumer.consume(rs);
        }
    }
}
