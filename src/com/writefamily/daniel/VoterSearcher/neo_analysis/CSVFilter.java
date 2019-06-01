/*
 * Copyright (C) 2019 Daniel Write
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.writefamily.daniel.VoterSearcher.neo_analysis;

import org.apache.commons.csv.CSVRecord;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CSVFilter {
    private final List<Map.Entry<CSVField, String>> filters = new ArrayList<>();

    public static CSVFilter instance() {
        return new CSVFilter();
    }

    public CSVFilter filter(CSVField field, String... values) {
        for (String value : values) {
            filters.add(new AbstractMap.SimpleEntry<>(field, value));
        }

        return this;
    }

    public boolean isInFilter(CSVRecord record) {
        boolean okay = true;
        for (Map.Entry<CSVField, String> filter : filters) {
            switch (filter.getKey()) {
                case FIRST_NAME:
                    okay = okay && record.get("FIRST_NAME").equalsIgnoreCase(filter.getValue());
                    break;
                case LAST_NAME:
                    okay = okay && record.get("LAST_NAME").equalsIgnoreCase(filter.getValue());
                    break;
                case BIRTH_YEAR:
                    okay = okay && record.get("BIRTH_DATE").split("-")[0].equalsIgnoreCase(filter.getValue());
                    break;
                case CITY:
                    okay = okay && record.get("RESIDENTIAL_CITY").equalsIgnoreCase(filter.getValue());
                    break;
                case PARTY:
                    okay = okay && record.get("PARTY_AFFILIATION").equalsIgnoreCase(filter.getValue());
                    break;
            }
        }

        return okay;
    }
}
