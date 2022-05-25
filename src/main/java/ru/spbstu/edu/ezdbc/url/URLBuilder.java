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

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Builder for JDBC connection URL strings.
 * <p>
 * This tool is <b>not</b> designed to cover all possible URL formats, as it is practically impossible to do so. Instead, it is a one-size-fits-all
 * solution, designed around the similarities between different URL formats. The URLs produced by this builder are <b>not</b> validated to be correct
 * with respect to the intended format. Moreover, individual parts of the URL are also not strictly validated beyond simple checks for {@code null}s
 * and blanks. Therefore, <b>it is up to the client to ensure sanity</b>.
 * <p>
 * This class is mutable not thread-safe.
 *
 * @see Protocol
 * @see Host
 */
// FIXME:
//  Some of the methods that are documented to only reject blanks actually reject any whitespace present because
//  I was too lazy to update the docs.
public final class URLBuilder {

    private Protocol protocol;
    private Optional<String> protocolSuffix;
    private final List<Host> hosts;
    private Optional<String> schemaName;
    private String propertyListPrefix;
    private final Map<String, String> properties;
    private String propertyDelimiter;

    /**
     * Constructs a new builder with the provided protocol. The rest of the configuration is as follows:
     * <ul>
     *     <li>Protocol suffix — {@code //}</li>
     *     <li>Hosts — <i>none</i></li>
     *     <li>Schema name — <i>none</i></li>
     *     <li>Property list prefix — {@code ?}</li>
     *     <li>Properties — <i>none</i></li>
     *     <li>Property delimiter — {@code &}</li>
     * </ul>
     *
     * @param protocol the JDBC protocol to initialize the builder with (not {@code null})
     * @throws NullPointerException if the provided protocol is {@code null}
     */
    public URLBuilder(Protocol protocol) {
        setProtocol(protocol);

        protocolSuffix = Optional.of("//");
        hosts = new LinkedList<>();
        schemaName = Optional.empty();

        propertyListPrefix = "?";
        properties = new LinkedHashMap<>();
        propertyDelimiter = "&";
    }

    /**
     * Sets the JDBC protocol.
     *
     * @param protocol the JDBC protocol to be used (not {@code null})
     * @return a reference to this object
     * @throws NullPointerException if {@code protocol} is {@code null}
     */
    public URLBuilder setProtocol(Protocol protocol) {
        this.protocol = Objects.requireNonNull(protocol, "Protocol is null");
        return this;
    }

    /**
     * Sets the JDBC protocol "suffix", which is the delimiter between the protocol and the host (or list of hosts) in the URL. For example, in the
     * URL {@code jdbc:mysql://localhost:3306/sakila} the "protocol suffix" would be the {@code //} preceding {@code localhost}. The option to
     * explicitly specify this part is provided due to databases sometimes using a different one, such as Oracle Database using the {@code @}
     * character. It is possible (and recommended) to use {@code null} to indicate "no suffix", which is allowed by some databases. The provided
     * suffix must not be blank otherwise.
     *
     * @param protocolSuffix the protocol suffix (must not be blank, but may be {@code null})
     * @return a reference to this object
     * @throws IllegalArgumentException if the non-{@code null} suffix is blank
     */
    public URLBuilder setProtocolSuffix(String protocolSuffix) {
        if (protocolSuffix != null) {
            validateNoWhitespaceOrEmpty(protocolSuffix);
        }
        this.protocolSuffix = Optional.ofNullable(protocolSuffix);
        return this;
    }

    /**
     * Sets the JDBC protocol suffix to the specified character. See {@link URLBuilder#setProtocolSuffix(String)} for more information.
     *
     * @param protocolSuffix the protocol suffix (must not be whitespace)
     * @return a reference to this object
     * @throws IllegalArgumentException if the provided character is whitespace
     */
    public URLBuilder setProtocolSuffix(char protocolSuffix) {
        return setProtocolSuffix(Character.toString(protocolSuffix));
    }

    /**
     * Adds a database host to the URL.
     *
     * @param host a DB host (not {@code null})
     * @return a reference to this object
     * @throws NullPointerException if the provided host is {@code null}
     */
    public URLBuilder addHost(Host host) {
        Objects.requireNonNull(host, "Host is null"); /* Guarantee with manual null-check regardless of List impl */
        hosts.add(host);
        return this;
    }

    /**
     * Adds each provided database host to the URL. Calling this method is functionally equivalent to manually iterating through every provided host
     * and calling {@link URLBuilder#addHost(Host)} on each one.
     *
     * @param hosts an iterable of hosts (not {@code null})
     * @return a reference to this object
     * @throws NullPointerException if one of the contained elements or the iterable itself is {@code null}
     * @implNote NPE is thrown in the middle of the iteration process, i.e. all elements before the {@code null} one will still be added.
     */
    public URLBuilder addHosts(Iterable<Host> hosts) {
        Objects.requireNonNull(hosts, "Hosts iterable is null");
        hosts.forEach(this::addHost);
        return this;
    }

    /**
     * Adds each provided database host to the URL. Calling this method is functionally equivalent to manually iterating through every provided host
     * and calling {@link URLBuilder#addHost(Host)} on each one.
     *
     * @param hosts a selection of hosts (not {@code null})
     * @return a reference to this object
     * @throws NullPointerException if one of the contained elements or the array itself is {@code null}
     * @implNote NPE is thrown in the middle of the iteration process, i.e. all elements before the {@code null} one will still be added.
     */
    public URLBuilder addHosts(Host... hosts) {
        Objects.requireNonNull(hosts, "Hosts array is null");
        for (Host host : hosts) {
            addHost(host);
        }
        return this;
    }

    /**
     * Sets the schema name. It is possible (and recommended) to use {@code null} to indicate "no specific schema", which is allowed by some
     * databases. The name must not be blank otherwise.
     *
     * @param schemaName the schema's name (must not be blank, but may be {@code null})
     * @return a reference to this object
     * @throws IllegalArgumentException if the non-{@code null} schema name is blank
     */
    public URLBuilder setSchemaName(String schemaName) {
        if ((schemaName != null) && schemaName.isBlank()) {
            throw new IllegalArgumentException("Schema name is blank");
        }
        this.schemaName = Optional.ofNullable(schemaName);
        return this;
    }

    /**
     * Sets the property list prefix, which is the sequence of characters (usually just a single character) that precedes the list of properties,
     * separating it from previous parts of the URL. For example, in the URL {@code jdbc:postgresql://localhost/test?user=jimmy} the "property list
     * prefix" is the {@code ?} character. The provided prefix must be neither {@code null} nor blank.
     *
     * @param propertyListPrefix the prefix to use (neither {@code null} nor blank)
     * @return a reference to this object
     * @throws NullPointerException if the provided string is {@code null}
     * @throws IllegalArgumentException if the provided string is blank
     */
    public URLBuilder setPropertyListPrefix(String propertyListPrefix) {
        Objects.requireNonNull(propertyListPrefix, "Property list prefix is null");
        validateNoWhitespaceOrEmpty(propertyListPrefix);
        this.propertyListPrefix = propertyListPrefix;
        return this;
    }

    /**
     * Sets the property list prefix to a specific character. See {@link URLBuilder#setPropertyListPrefix(String)} for more information.
     *
     * @param propertyListPrefix the prefix to use (must not be whitespace)
     * @return a reference to this object
     * @throws IllegalArgumentException if the provided character is whitespace
     */
    public URLBuilder setPropertyListPrefix(char propertyListPrefix) {
        return setPropertyListPrefix(Character.toString(propertyListPrefix));
    }

    /**
     * Sets the value of a named connection property. Property names must be neither {@code null} nor blank. Property values must be neither
     * {@code null} nor empty.
     *
     * @param name the property's name (neither {@code null} nor blank)
     * @param value the property's value (neither {@code null} nor empty)
     * @return a reference to this object
     * @throws NullPointerException if either of the provided name and value is null
     * @throws IllegalArgumentException if the provided name is blank or the provided value is empty
     */
    public URLBuilder setProperty(String name, String value) {
        Objects.requireNonNull(name, "Property name is null");
        validateNoWhitespaceOrEmpty(name);

        Objects.requireNonNull(value, "Property value is null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("Property value is blank");
        }

        properties.put(name, value);
        return this;
    }

    /**
     * Sets the value of a named connection property to the given {@code byte}. Property name must be neither {@code null} nor blank.
     * <p>
     * Calling this method is equivalent to calling {@link URLBuilder#setProperty(String, String)} with the value wrapped in a
     * {@link Byte#toString(byte)}.
     *
     * @param name the property's name (neither {@code null} nor blank)
     * @param value the property's value
     * @return a reference to this object
     * @throws NullPointerException if the specified property name is {@code null}
     * @throws IllegalArgumentException if the specified property name is blank
     */
    public URLBuilder setProperty(String name, byte value) {
        return setProperty(name, Byte.toString(value));
    }

    /**
     * Sets the value of a named connection property to the given {@code short}. Property name must be neither {@code null} nor blank.
     * <p>
     * Calling this method is equivalent to calling {@link URLBuilder#setProperty(String, String)} with the value wrapped in a
     * {@link Short#toString(short)}.
     *
     * @param name the property's name (neither {@code null} nor blank)
     * @param value the property's value
     * @return a reference to this object
     * @throws NullPointerException if the specified property name is {@code null}
     * @throws IllegalArgumentException if the specified property name is blank
     */
    public URLBuilder setProperty(String name, short value) {
        return setProperty(name, Short.toString(value));
    }

    /**
     * Sets the value of a named connection property to the given {@code int}. Property name must be neither {@code null} nor blank.
     * <p>
     * Calling this method is equivalent to calling {@link URLBuilder#setProperty(String, String)} with the value wrapped in an
     * {@link Integer#toString(int)}.
     *
     * @param name the property's name (neither {@code null} nor blank)
     * @param value the property's value
     * @return a reference to this object
     * @throws NullPointerException if the specified property name is {@code null}
     * @throws IllegalArgumentException if the specified property name is blank
     */
    public URLBuilder setProperty(String name, int value) {
        return setProperty(name, Integer.toString(value));
    }

    /**
     * Sets the value of a named connection property to the given {@code long}. Property name must be neither {@code null} nor blank.
     * <p>
     * Calling this method is equivalent to calling {@link URLBuilder#setProperty(String, String)} with the value wrapped in a
     * {@link Long#toString(long)}.
     *
     * @param name the property's name (neither {@code null} nor blank)
     * @param value the property's value
     * @return a reference to this object
     * @throws NullPointerException if the specified property name is {@code null}
     * @throws IllegalArgumentException if the specified property name is blank
     */
    public URLBuilder setProperty(String name, long value) {
        return setProperty(name, Long.toString(value));
    }

    /**
     * Sets the value of a named connection property to the given {@code float}. Property name must be neither {@code null} nor blank.
     * <p>
     * Calling this method is equivalent to calling {@link URLBuilder#setProperty(String, String)} with the value wrapped in a
     * {@link Float#toString(float)}.
     *
     * @param name the property's name (neither {@code null} nor blank)
     * @param value the property's value
     * @return a reference to this object
     * @throws NullPointerException if the specified property name is {@code null}
     * @throws IllegalArgumentException if the specified property name is blank
     */
    public URLBuilder setProperty(String name, float value) {
        return setProperty(name, Float.toString(value));
    }

    /**
     * Sets the value of a named connection property to the given {@code double}. Property name must be neither {@code null} nor blank.
     * <p>
     * Calling this method is equivalent to calling {@link URLBuilder#setProperty(String, String)} with the value wrapped in a
     * {@link Double#toString(double)}.
     *
     * @param name the property's name (neither {@code null} nor blank)
     * @param value the property's value
     * @return a reference to this object
     * @throws NullPointerException if the specified property name is {@code null}
     * @throws IllegalArgumentException if the specified property name is blank
     */
    public URLBuilder setProperty(String name, double value) {
        return setProperty(name, Double.toString(value));
    }

    /**
     * Sets the value of a named connection property to the given {@code boolean}. Property name must be neither {@code null} nor blank.
     * <p>
     * Calling this method is equivalent to calling {@link URLBuilder#setProperty(String, String)} with the value wrapped in a
     * {@link Boolean#toString(boolean)}.
     *
     * @param name the property's name (neither {@code null} nor blank)
     * @param value the property's value
     * @return a reference to this object
     * @throws NullPointerException if the specified property name is {@code null}
     * @throws IllegalArgumentException if the specified property name is blank
     */
    public URLBuilder setProperty(String name, boolean value) {
        return setProperty(name, Boolean.toString(value));
    }

    /**
     * Sets the value of a named connection property to the given {@code char}. Property name must be neither {@code null} nor blank.
     * <p>
     * Calling this method is equivalent to calling {@link URLBuilder#setProperty(String, String)} with the value wrapped in a
     * {@link Character#toString(char)}.
     *
     * @param name the property's name (neither {@code null} nor blank)
     * @param value the property's value
     * @return a reference to this object
     * @throws NullPointerException if the specified property name is {@code null}
     * @throws IllegalArgumentException if the specified property name is blank
     */
    public URLBuilder setProperty(String name, char value) {
        return setProperty(name, Character.toString(value));
    }

    /**
     * Sets the property delimiter, which must be neither {@code null} nor blank.
     *
     * @param propertyDelimiter the property delimiter (neither {@code null} nor blank)
     * @return a reference to this object
     * @throws NullPointerException if the provided delimiter string is {@code null}
     * @throws IllegalArgumentException if the provided delimiter string is blank
     */
    public URLBuilder setPropertyDelimiter(String propertyDelimiter) {
        Objects.requireNonNull(propertyDelimiter, "Property delimiter is null");
        validateNoWhitespaceOrEmpty(propertyDelimiter);

        this.propertyDelimiter = propertyDelimiter;
        return this;
    }

    /**
     * Sets the property delimiter. See {@link URLBuilder#setPropertyDelimiter(String)} for more information.
     *
     * @param propertyDelimiter the property delimiter (must not be whitespace)
     * @return a reference to this object
     * @throws IllegalArgumentException if the provided property delimiter is whitespace
     */
    public URLBuilder setPropertyDelimiter(char propertyDelimiter) {
        return setPropertyDelimiter(Character.toString(propertyDelimiter));
    }

    /**
     * Returns a DB connection URL built from the current state of the builder. Schema name and property values are percent-encoded.
     *
     * @return a DB connection URL string (never {@code null})
     * @implNote URL properties are appended according to their insertion order via {@link URLBuilder#setProperty(String, String)}
     */
    public String build() {
        StringBuilder sb = new StringBuilder();

        sb.append(protocol);
        protocolSuffix.ifPresent(sb::append);

        /*
         * Not sure if the docs specify the exact return values and/or behavior of Collectors::joining on empty stream or StringBuilder::append on
         * empty string, so better play it safe and just don't do anything if empty. Besides, this might also improve performance.
         */
        if (!hosts.isEmpty()) {
            String joinedHosts = hosts.stream()
                    .map(Host::toString)
                    .collect(Collectors.joining(","));
            sb.append(joinedHosts);
        }

        schemaName.ifPresent(name -> {
            if (!hosts.isEmpty()) {
                sb.append('/');
            }

            String encodedName = encode(name);
            sb.append(encodedName);
        });

        if (!properties.isEmpty()) { /* Same thing as with hosts (see comment above) */
            String joinedProperties = properties.entrySet()
                    .stream()
                    .map(entry -> entry.getKey() + '=' + encode(entry.getValue()))
                    .collect(Collectors.joining(propertyDelimiter, propertyListPrefix, ""));

            sb.append(joinedProperties);
        }

        return sb.toString();
    }

    private static String encode(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }

    private static void validateNoWhitespaceOrEmpty(String s) {
        if (s.isEmpty() || CharMatcher.whitespace().matchesAnyOf(s)) {
            throw new IllegalArgumentException("No whitespace allowed (provided value: \"" + s + "\")");
        }
    }

    /**
     * Checks if this builder is equal to another object.
     *
     * @param obj an object to check against
     * @return {@code true} if and only if the provided object is a non-{@code null} {@code URLBuilder} with identical state
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        return obj instanceof URLBuilder other
                && protocol == other.protocol
                && protocolSuffix.equals(other.protocolSuffix)
                && hosts.equals(other.hosts)
                && schemaName.equals(other.schemaName)
                && propertyListPrefix.equals(other.propertyListPrefix)
                && properties.equals(other.properties)
                && propertyDelimiter.equals(other.propertyDelimiter);
    }

    /**
     * Computes a hash code for this builder.
     *
     * @return a suitable hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(protocol, protocolSuffix, hosts, schemaName, propertyListPrefix, properties, propertyDelimiter);
    }

    /**
     * Converts this builder into a string that describes its state.
     *
     * @return this builder's string representation (never {@code null})
     */
    @Override
    public String toString() {
        String fmt = "URLBuilder[protocol=%s, protocolSuffix=%s, hosts=%s, schemaName=%s, propertyListPrefix=%s, properties=%s, propertyDelimiter=%s]";
        return fmt.formatted(protocol.name(), protocolSuffix, hosts, schemaName, propertyListPrefix, properties, propertyDelimiter);
    }
}
