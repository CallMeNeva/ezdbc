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

// https://www.w3schools.com/sql/sql_delete.asp
public final class DeletionQuery extends AbstractModificationQuery {

    private Expression condition;

    public DeletionQuery(Identifier tableIdentifier, Expression condition) {
        super(tableIdentifier);
        setCondition(condition);
    }

    public DeletionQuery(Identifier tableIdentifier) {
        this(tableIdentifier, null);
    }

    public DeletionQuery(String tableName, Expression condition) {
        super(tableName);
        setCondition(condition);
    }

    public DeletionQuery(String tableName) {
        this(tableName, null);
    }

    public Expression getCondition() {
        return condition;
    }

    public void setCondition(Expression condition) {
        this.condition = condition;
    }

    @Override
    public String toSQLString() {
        StringBuilder sb = new StringBuilder("DELETE FROM ");
        sb.append(getTableName());

        if (condition != null) {
            sb.append(" WHERE ");
            sb.append(condition.toSQLString());
        }

        return sb.toString();
    }

    // TODO: Equals, hashCode and toString
}
