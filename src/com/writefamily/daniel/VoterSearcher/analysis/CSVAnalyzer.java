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

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CSVAnalyzer {
    protected static CSVFormat CSV_FORMAT = CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim();

    public static List<CSVRecord> analyze(InputStream inputStream, CSVFilter filter) throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Reader reader = new BufferedReader(new InputStreamReader(inputStream));
        CSVParser parser = new CSVParser(reader, CSVAnalyzer.CSV_FORMAT);

        // we are going to do some ugly code here to get the nextRecord() method available
        Method method = parser.getClass().getDeclaredMethod("nextRecord");
        method.setAccessible(true);

        List<CSVRecord> records = new ArrayList<>();
        CSVRecord record;
        while ((record = (CSVRecord) method.invoke(parser)) != null) {
            if (filter.isInFilter(record)) {
                records.add(record);
            }
        }

        return records;
    }

    public static Map<String, Object> formatRecord(CSVRecord record) {
        Map<String, String> votingRecord = new HashMap<>();
        Map<String, Object> voter = new HashMap<>();
        Map<String, String> voterFile = record.toMap();
        for (String key : voterFile.keySet()) {
            if (key.contains("PRIMARY-") || key.contains("GENERAL-") || key.contains("SPECIAL-")) {
                votingRecord.put(key, voterFile.get(key));
            } else {
                voter.put(key, voterFile.get(key));
            }
        }

        voter.put("VOTER_HISTORY", votingRecord);
        return voter;
    }
}
