/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.elasticsearch.hadoop.rest.query;

import org.elasticsearch.hadoop.util.Assert;

import java.io.IOException;

public class SimpleQueryParser {
     public QueryBuilder parse(String raw) throws IOException {
        if (raw.startsWith("?")) {
            return parseURI(raw.substring(1));
        } else if (raw.startsWith("{")) {
            return new RawQueryBuilder(raw);

        } else {
            throw new IllegalArgumentException("Failed to parse query: " + raw);
        }
    }

    private QueryBuilder parseURI(String raw) {
        // break down the uri into parameters
        QueryStringQueryBuilder builder = new QueryStringQueryBuilder();
        for (String token : raw.split("&")) {
            int indexOf = token.indexOf("=");
            Assert.isTrue(indexOf > 0, String.format("Cannot token [%s] in uri query [%s]", token, raw));
            String name = token.substring(0, indexOf);
            String value = token.substring(indexOf + 1);
            applyURIParameter(builder, name, value);

        }
        return builder;
    }

    private void applyURIParameter(QueryStringQueryBuilder builder, String name, String value) {
        if (name.equals("q") || name.equals("query")) {
            builder.query(value);
        } else if (name.equals("df") || name.equals("default_field")) {
            builder.defaultField(value);
        } else if (name.equals("analyzer") || name.equals("analyzer")) {
            builder.analyzer(value);
        } else if (name.equals("lowercase_expanded_terms") || name.equals("lowercase_expanded_terms")) {
            builder.lowercaseExpandedTerms(Boolean.parseBoolean(value.trim()));

        } else if (name.equals("analyze_wildcard") || name.equals("analyze_wildcard")) {
            builder.analyzeWildcard(Boolean.parseBoolean(value.trim()));
        } else if (name.equals("default_operator") || name.equals("default_operator")) {
            builder.defaultOperator(value);

        } else  if (name.equals("lenient") || name.equals("lenient")) {
            builder.lenient(Boolean.parseBoolean(value.trim()));
        } else {
            throw new IllegalArgumentException("Unknown " + name + " parameter; please change the URI query into a Query DLS (see 'Query String Query')");
        }
    }
}
