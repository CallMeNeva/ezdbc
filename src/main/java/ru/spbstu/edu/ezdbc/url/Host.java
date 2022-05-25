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

import com.google.common.base.CharMatcher;

import java.util.Objects;

// TODO: JavaDocs
public record Host(String name, Port port) {

    private static final char SEPARATOR = ':';
    private static final String LOCALHOST_VALUE = "localhost";

    public static final Host LOCALHOST = new Host(LOCALHOST_VALUE);

    public Host {
        Objects.requireNonNull(name, "Hostname can't be null");

        if (name.isEmpty()) {
            throw new IllegalArgumentException("Hostname can't be empty");
        }

        if (CharMatcher.whitespace().matchesAnyOf(name)) {
            throw new IllegalArgumentException("Invalid hostname: " + name);
        }
    }

    public Host(String name) {
        this(name, null);
    }

    public static Host parse(String s) {
        Objects.requireNonNull(s, "Can't parse null string");

        int separatorIndex = s.indexOf(SEPARATOR);
        // OPTIMIZATION: If 's' is "localhost", return predefined localhost instance
        if (separatorIndex == -1) {
            return new Host(s);
        }

        String hostname = s.substring(0, separatorIndex);
        int portValue = Integer.parseInt(s, (separatorIndex + 1), s.length(), 10);

        return new Host(hostname, new Port(portValue));
    }

    public static Host localhostWithPort(Port port) {
        return new Host(LOCALHOST_VALUE, port);
    }

    @Override
    public String toString() {
        return port == null ? name : (name + SEPARATOR + port.value());
    }
}
