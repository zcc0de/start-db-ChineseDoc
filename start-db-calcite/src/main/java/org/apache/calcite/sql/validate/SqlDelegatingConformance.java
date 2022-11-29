/*
 * This file is inherited from Apache Calcite and modifed by ST-Lab under apache license.
 * You can find the original code from
 *
 * https://github.com/apache/calcite
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to you under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.calcite.sql.validate;

/** 将sqlconformance的接口和其实现委托到一个对象中
 * Implementation of {@link SqlConformance} that delegates all methods to
 * another object. You can create a sub-class that overrides particular
 * methods.
 */
public class SqlDelegatingConformance extends SqlAbstractConformance {
    private final SqlConformance delegate;

    /** Creates a SqlDelegatingConformance. */
    protected SqlDelegatingConformance(SqlConformance delegate) {
        this.delegate = delegate;
    }

    @Override
    public boolean isGroupByAlias() {
        return delegate.isGroupByAlias();
    }

    @Override
    public boolean isGroupByOrdinal() {
        return delegate.isGroupByOrdinal();
    }

    @Override
    public boolean isHavingAlias() {
        return delegate.isGroupByAlias();
    }

    @Override
    public boolean isSortByOrdinal() {
        return delegate.isSortByOrdinal();
    }

    @Override
    public boolean isSortByAlias() {
        return delegate.isSortByAlias();
    }

    @Override
    public boolean isSortByAliasObscures() {
        return delegate.isSortByAliasObscures();
    }

    @Override
    public boolean isFromRequired() {
        return delegate.isFromRequired();
    }

    @Override
    public boolean isBangEqualAllowed() {
        return delegate.isBangEqualAllowed();
    }

    @Override
    public boolean isMinusAllowed() {
        return delegate.isMinusAllowed();
    }

    @Override
    public boolean isInsertSubsetColumnsAllowed() {
        return delegate.isInsertSubsetColumnsAllowed();
    }

    @Override
    public boolean allowNiladicParentheses() {
        return delegate.allowNiladicParentheses();
    }

    @Override
    public boolean allowAliasUnnestItems() {
        return delegate.allowAliasUnnestItems();
    }

    @Override
    public boolean allowSelectTableFunction() {
        return delegate.allowSelectTableFunction();
    }

}