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

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.csv.CSVRecord;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Contains the learned data of a CVS file
 * a separate analysis wil be done for each county
 */
public class CSVAnalysis {
    /*protected Map<String, TreeSet<Long>> firstNames = new HashMap<>();
    protected Map<String, TreeSet<Long>> lastNames = new HashMap<>();
    protected Map<String, TreeSet<Long>> birthYears = new HashMap<>();
    protected Map<String, TreeSet<Long>> parties = new HashMap<>();
    protected Map<String, TreeSet<Long>> cities = new HashMap<>();*/
    protected Map<String, Map<String, Set<Map.Entry<Long, CSVRecord>>>> rawAnalysis;
    private byte[] checksum;
    private LinkedHashMap<Long, CSVRecord> records = new LinkedHashMap<>();

    protected void addRecord(CSVRecord record) {
        records.put(record.getRecordNumber(), record);
    }

    public void setChecksum(byte[] data) {
        this.checksum = DigestUtils.md5(data);
    }

    public boolean equalChecksum(CSVAnalysis csvAnalysis) {
        return checksum == csvAnalysis.checksum;
    }

    public void interpret(Map<String, Map<String, Set<Map.Entry<Long, CSVRecord>>>> rawAnalysis) {
        /*for (Map.Entry<String, Map<String, Set<Map.Entry<Long, CSVRecord>>>> type : rawAnalysis.entrySet()) {
            Map<String, TreeSet<Long>> map;
            switch(type.getKey()) {
                case "FIRST_NAME":
                    map = firstNames;
                    break;
                case "LAST_NAME":
                    map = lastNames;
                    break;
                case "BIRTH_YEAR":
                    map = birthYears;
                    break;
                case "PARTY":
                    map = parties;
                    break;
                case "CITY":
                    map = cities;
                    break;
                default:
                    throw new IllegalArgumentException("Unknown type = " + type.getKey());
            }
            for (Map.Entry<String, Set<Map.Entry<Long, CSVRecord>>> classifer : type.getValue().entrySet()) {
                if (null == map.get(classifer.getKey())) {
                    map.put(classifer.getKey(), new TreeSet<>());
                }
                for (Iterator<Map.Entry<Long, CSVRecord>> it = classifer.getValue().iterator(); it.hasNext(); ) {
                    Map.Entry<Long, CSVRecord> recordEntry = it.next();
                    map.get(classifer.getKey()).add(recordEntry.getKey());
                    records.add(recordEntry.getValue()); // lets hope it preserves the indexs
                }
            }
        }*/
        // its brolem amd i dont know why and im very tired and just want the filter to work
        this.rawAnalysis = rawAnalysis;
    }

    public AnalysisQuery filter(String type, String... values) {
        /**Map<String, TreeSet<Long>> map;
         switch(type) {
         case "FIRST_NAME":
         map = firstNames;
         break;
         case "LAST_NAME":
         map = lastNames;
         break;
         case "BIRTH_YEAR":
         map = birthYears;
         break;
         case "PARTY":
         map = parties;
         break;
         case "CITY":
         map = cities;
         break;
         default:
         throw new IllegalArgumentException("Unknown type = " + type);
         }

         Set<Long> filteredValues = new TreeSet<>();
         List<String> nifty = Arrays.asList(values);
         for (Map.Entry<String, TreeSet<Long>> entry : map.entrySet()) {
         System.out.println(entry.getKey());
         if (nifty.contains(entry.getKey())) {
         filteredValues.addAll(entry.getValue());
         break;
         }
         }

         return new AnalysisQuery(this, filteredValues);*/
        Set<Long> filteredValues = new TreeSet<>();
        Map<String, Set<Map.Entry<Long, CSVRecord>>> map = rawAnalysis.get(type);
        for (String value : values) {
            for (Map.Entry<Long, CSVRecord> entry : map.get(value)) {
                filteredValues.add(entry.getKey());
            }
        }
        return new AnalysisQuery(this, filteredValues);
    }

    public CSVRecord getRecord(long i) {
        return records.get(i);
    }
}
