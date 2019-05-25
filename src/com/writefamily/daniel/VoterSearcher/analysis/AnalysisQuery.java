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

package com.writefamily.daniel.VoterSearcher.analysis;

import org.apache.commons.csv.CSVRecord;

import java.util.*;

public class AnalysisQuery {
    private CSVAnalysis analysis;
    private Set<Long> indexes;

    protected AnalysisQuery(CSVAnalysis analysis, Set<Long> indexes) {
        this.analysis = analysis;
        this.indexes = indexes;
    }

    public List<CSVRecord> getRecords() {
        List<CSVRecord> records = new ArrayList<>();
        for (Long i : indexes) {
            records.add(analysis.getRecord(i));
        }
        return records;
    }

    public AnalysisQuery filter(String type, String... values) {
        /*Map<String, TreeSet<Long>> map;
        switch(type) {
            case "FIRST_NAME":
                map = analysis.firstNames;
                break;
            case "LAST_NAME":
                map = analysis.lastNames;
                break;
            case "BIRTH_YEAR":
                map = analysis.birthYears;
                break;
            case "PARTY":
                map = analysis.parties;
                break;
            case "CITY":
                map = analysis.cities;
                break;
            default:
                throw new IllegalArgumentException("Unknown type = " + type);
        }*/
        Set<Long> filteredValues = new TreeSet<>();
        List<String> nifty = Arrays.asList(values);
        outer:
        for (Map.Entry<String, Set<Map.Entry<Long, CSVRecord>>> entry : analysis.rawAnalysis.get(type).entrySet()) {
            if (nifty.contains(entry.getKey())) {
                for (Map.Entry<Long, CSVRecord> r : entry.getValue()) {
                    if (indexes.contains(r.getKey())) {
                        filteredValues.add(r.getKey());
                    }
                }
            }
        }
        return new AnalysisQuery(analysis, filteredValues);
    }
}
