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

package ru.spbstu.edu.ezdbc.url;

import java.util.Objects;

// TODO: JavaDocs
public record Port(int value) implements Comparable<Port> {

    private static final int MIN_VALUE = 0;
    private static final int MAX_VALUE = 65535;

    public static final Port MIN = new Port(MIN_VALUE);

    public static final Port MAX = new Port(MAX_VALUE);

    public Port {
        if (value < MIN_VALUE || value > MAX_VALUE) {
            throw new IllegalArgumentException("Invalid port value: " + value + " (allowed range: [" + MIN_VALUE + ", " + MAX_VALUE + "])");
        }
    }

    public static Port parse(String s) {
        Objects.requireNonNull(s, "Can't parse null string");

        int value = Integer.parseInt(s);
        return new Port(value);
    }

    @Override
    public String toString() {
        return Integer.toString(value);
    }

    @Override
    public int compareTo(Port other) {
        // TODO: Unit testing
        return Integer.compare(value, other.value);
    }
}
