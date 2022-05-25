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

import java.io.PrintStream;
import java.util.StringJoiner;
import java.util.function.Function;

public class ResultSetPrinter extends PerRowResultSetConsumer {

    public ResultSetPrinter(PrintStream out, Function<Object, String> stringifier, String delim, String prefix, String suffix, String... labels) {
        super(resultSet -> {
            StringJoiner joiner = new StringJoiner(delim, prefix, suffix);

            for (String label : labels) {
                Object obj = resultSet.getObject(label);
                String objAsString = stringifier.apply(obj);
                joiner.add(objAsString);
            }

            out.println(joiner);
        });
    }

    public static ResultSetPrinter simple(PrintStream out, String delim, String... labels) {
        return new ResultSetPrinter(out, String::valueOf, delim, "", "", labels);
    }
}
