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

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.Reader;
import java.util.*;

/**
 * This class analyzes the CVS files and figures data ranges as to speed up search time
 */
public class CSVAnalyzer {
    public static Map<String, Map<String, Set<Map.Entry<Long, CSVRecord>>>> analyze(Reader reader) throws IOException {
        CSVFormat vfrFormat = CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim();
        CSVParser csvParser = new CSVParser(reader, vfrFormat);

        //   TYPE        KEY                  INDEX RECORD
        Map<String, Map<String, Set<Map.Entry<Long, CSVRecord>>>> rawAnalysis = new HashMap<>();
        rawAnalysis.put("LAST_NAME", new HashMap<>());
        rawAnalysis.put("FIRST_NAME", new HashMap<>());
        rawAnalysis.put("BIRTH_YEAR", new HashMap<>());
        rawAnalysis.put("PARTY", new HashMap<>());
        rawAnalysis.put("CITY", new HashMap<>());
        for (CSVRecord record : csvParser.getRecords()) {
            String lastName = record.get("LAST_NAME").split("")[0];
            String firstName = record.get("FIRST_NAME").split("")[0]; // names are indexed by first letter alone
            String birthYear = record.get("DATE_OF_BIRTH").split("-")[0];
            String party = record.get("PARTY_AFFILIATION");
            String city = record.get("RESIDENTIAL_CITY");
            long recordIndex = record.getRecordNumber();
            if (!rawAnalysis.get("LAST_NAME").containsKey(lastName)) {
                rawAnalysis.get("LAST_NAME").put(lastName, new HashSet<>());
            }
            if (!rawAnalysis.get("FIRST_NAME").containsKey(firstName)) {
                rawAnalysis.get("FIRST_NAME").put(firstName, new HashSet<>());
            }
            if (!rawAnalysis.get("BIRTH_YEAR").containsKey(birthYear)) {
                rawAnalysis.get("BIRTH_YEAR").put(birthYear, new HashSet<>());
            }
            if (!rawAnalysis.get("PARTY").containsKey(party)) {
                rawAnalysis.get("PARTY").put(party, new HashSet<>());
            }
            if (!rawAnalysis.get("CITY").containsKey(city)) {
                rawAnalysis.get("CITY").put(city, new HashSet<>());
            }

            rawAnalysis.get("LAST_NAME").get(lastName).add(new AbstractMap.SimpleEntry<>(recordIndex, record));
            rawAnalysis.get("FIRST_NAME").get(firstName).add(new AbstractMap.SimpleEntry<>(recordIndex, record));
            rawAnalysis.get("BIRTH_YEAR").get(birthYear).add(new AbstractMap.SimpleEntry<>(recordIndex, record));
            rawAnalysis.get("PARTY").get(party).add(new AbstractMap.SimpleEntry<>(recordIndex, record));
            rawAnalysis.get("CITY").get(city).add(new AbstractMap.SimpleEntry<>(recordIndex, record));
        }

        return rawAnalysis;
    }

    public static CSVAnalysis learn(Reader reader) throws IOException {
        CSVFormat vfrFormat = CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim();
        CSVParser csvParser = new CSVParser(reader, vfrFormat);

        CSVAnalysis csvAnalysis = new CSVAnalysis();

        //   TYPE        KEY                  INDEX RECORD
        Map<String, Map<String, Set<Map.Entry<Long, CSVRecord>>>> rawAnalysis = new HashMap<>();
        rawAnalysis.put("LAST_NAME", new HashMap<>());
        rawAnalysis.put("FIRST_NAME", new HashMap<>());
        rawAnalysis.put("BIRTH_YEAR", new HashMap<>());
        rawAnalysis.put("PARTY", new HashMap<>());
        rawAnalysis.put("CITY", new HashMap<>());
        for (CSVRecord record : csvParser.getRecords()) {
            csvAnalysis.addRecord(record);
            String lastName = record.get("LAST_NAME").split("")[0];
            String firstName = record.get("FIRST_NAME").split("")[0]; // names are indexed by first letter alone
            String birthYear = record.get("DATE_OF_BIRTH").split("-")[0];
            String party = record.get("PARTY_AFFILIATION");
            String city = record.get("RESIDENTIAL_CITY");
            long recordIndex = record.getRecordNumber();
            if (!rawAnalysis.get("LAST_NAME").containsKey(lastName)) {
                rawAnalysis.get("LAST_NAME").put(lastName, new HashSet<>());
            }
            if (!rawAnalysis.get("FIRST_NAME").containsKey(firstName)) {
                rawAnalysis.get("FIRST_NAME").put(firstName, new HashSet<>());
            }
            if (!rawAnalysis.get("BIRTH_YEAR").containsKey(birthYear)) {
                rawAnalysis.get("BIRTH_YEAR").put(birthYear, new HashSet<>());
            }
            if (!rawAnalysis.get("PARTY").containsKey(party)) {
                rawAnalysis.get("PARTY").put(party, new HashSet<>());
            }
            if (!rawAnalysis.get("CITY").containsKey(city)) {
                rawAnalysis.get("CITY").put(city, new HashSet<>());
            }

            rawAnalysis.get("LAST_NAME").get(lastName).add(new AbstractMap.SimpleEntry<>(recordIndex, record));
            rawAnalysis.get("FIRST_NAME").get(firstName).add(new AbstractMap.SimpleEntry<>(recordIndex, record));
            rawAnalysis.get("BIRTH_YEAR").get(birthYear).add(new AbstractMap.SimpleEntry<>(recordIndex, record));
            rawAnalysis.get("PARTY").get(party).add(new AbstractMap.SimpleEntry<>(recordIndex, record));
            rawAnalysis.get("CITY").get(city).add(new AbstractMap.SimpleEntry<>(recordIndex, record));
        }

        csvAnalysis.interpret(rawAnalysis);
        return csvAnalysis;
    }

    public static String formatRecord(CSVRecord record) {
        return String.format("%s, %s %s\nRegistered %s", record.get("LAST_NAME"), record.get("FIRST_NAME"),
                record.get("MIDDLE_NAME"), record.get("PARTY_AFFILIATION"));
    }
}