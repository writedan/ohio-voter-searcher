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

package com.writefamily.daniel.VoterSearcher;

import com.writefamily.daniel.VoterSearcher.analysis.CSVAnalyzer;
import com.writefamily.daniel.VoterSearcher.analysis.CSVField;
import com.writefamily.daniel.VoterSearcher.analysis.CSVFilter;
import com.writefamily.daniel.VoterSearcher.scheduling.TaskScheduler;
import org.apache.commons.csv.CSVRecord;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;

import java.io.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class Main {
    public static final TaskScheduler TASK_SCHEDULER = new TaskScheduler(Runtime.getRuntime().availableProcessors());
    public static final String OVR_BASE_URL = "https://www6.sos.state.oh.us/ords/f?p=VOTERFTP:DOWNLOAD::FILE:NO:2:P2_PRODUCT_NUMBER:";
    public static final String[] COUNTY_ARRAY = {
            "ADAMS", "ALLEN", "ASHLAND", "ASHTABULA", "ATHENS", "AUGLAIZE",
            "BELMONT", "BROWN", "BUTLER", "CARROLL", "CHAMPAIGN", "CLARK",
            "CLERMONT", "CLINTON", "COLUMBIANA", "COSHOCTON", "CRAWFORD",
            "CUYAHOGA", "DARKE", "DEFIANCE", "DELAWARE", "ERIE", "FAIRFIELD",
            "FAYETTE", "FRANKLIN", "FULTON", "GALLIA", "GEAUGA", "GREENE",
            "GUERNSEY", "HAMILTON", "HANCOCK", "HARDIN", "HARRISON", "HENRY",
            "HIGHLAND", "HOCKING", "HOLMES", "HURON", "JACKSON", "JEFFERSON",
            "KNOX", "LAKE", "LAWRENCE", "LICKING", "LOGAN", "LORAIN",
            "LUCAS", "MADISON", "MAHONING", "MARION", "MEDINA", "MEIGS",
            "MERCER", "MIAMI", "MONROE", "MONTGOMERY", "MORGAN", "MORROW",
            "MUSKINGUM", "NOBLE", "OTTAWA", "PAULDING", "PERRY", "PICKAWAY",
            "PIKE", "PORTAGE", "PREBLE", "PUTNAM", "RICHLAND", "ROSS", "SANDUSKY",
            "SCIOTO", "SENECA", "SHELBY", "STARK", "SUMMIT", "TRUMBULL",
            "TUSCARAWAS", "UNION", "VANWERT", "VINTON", "WARREN", "WASHINGTON",
            "WAYNE", "WILLIAMS", "WOOD", "WYANDOT"
    };
    public static final File BASE_DIR = new File(".ohio_voter_searcher" + File.separator);

    public static void main(String[] args) {
        Main.BASE_DIR.mkdirs();
        try {
            CSVFilter filter = CSVFilter.instance();
            for (String s : args) {
                String[] arg = s.split("=");
                String key = arg[0];
                String val = arg[1];

                CSVField field = CSVField.valueOf(key.toUpperCase());
                filter.filter(field, val); // returns self to facillitate chaining, but not necessary
            }

            // we use the county values to isolate the files to download
            List<String> counties = filter.getValues(CSVField.COUNTY);
            int[] countyCodes; // the county codes that will be downloaded and used
            if (counties.size() == 0) {
                // use all counties
                countyCodes = new int[COUNTY_ARRAY.length];
                for (int i = 0; i < countyCodes.length; i++) {
                    countyCodes[i] = i + 1;
                }
            } else {
                countyCodes = new int[counties.size()];
                for (int i = 0; i < countyCodes.length; i++) {
                    for (int x = 0; x < COUNTY_ARRAY.length; x++) {
                        if (counties.get(i).equalsIgnoreCase(COUNTY_ARRAY[x])) {
                            countyCodes[i] = x + 1;
                            break;
                        }
                    }
                }
            }

            Serializer serializer = new Serializer(System.out);

            SSLContextBuilder builder = new SSLContextBuilder();
            builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
            SSLConnectionSocketFactory scsf = new SSLConnectionSocketFactory(builder.build());

            CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(scsf).build();

            for (int code : countyCodes) {
                File countySaveFile = new File(Main.BASE_DIR.getAbsolutePath() + File.separator + COUNTY_ARRAY[code - 1]);
                //long days = ChronoUnit.DAYS.between(LocalDate.now(), LocalDate.ofEpochDay(CSVAnalyzer.getSaveDate(countySaveFile)));
                long days = ChronoUnit.DAYS.between(LocalDate.ofEpochDay(CSVAnalyzer.getSaveDate(countySaveFile)), LocalDate.now());

                InputStream inputStream;
                OutputStream outputStream = null;
                long contentLength;
                if (days >= 8) {
                    HttpGet request = new HttpGet(Main.OVR_BASE_URL + code);
                    CloseableHttpResponse response = httpClient.execute(request);

                    contentLength = Integer.parseInt(response.getFirstHeader("Content-Length").getValue());
                    inputStream = response.getEntity().getContent();

                    outputStream = new FileOutputStream(countySaveFile);

                    DataOutputStream dateWriter = new DataOutputStream(outputStream);
                    dateWriter.writeLong(LocalDate.now().toEpochDay());
                    dateWriter.flush();
                } else {
                    countySaveFile.createNewFile();
                    inputStream = new FileInputStream(countySaveFile);
                    contentLength = countySaveFile.length();
                }

                serializer.beginCountyDownload(code, contentLength);

                ByteArrayOutputStream dataStore = new ByteArrayOutputStream();
                int n, oldPercent = 0;
                double transferred = 0.0;

                while ((n = inputStream.read()) != -1) {
                    dataStore.write(n);
                    if (outputStream != null) {
                        outputStream.write(n); // save file if it doesnt exist
                        outputStream.flush();
                    }

                    transferred += 1;
                    int percent = (int) ((transferred / contentLength) * 100);
                    if (percent > oldPercent) {
                        oldPercent = percent;
                        serializer.countyDownloadProgress(code, percent);
                    }
                }

                byte[] data = dataStore.toByteArray();

                serializer.countyComplete(code);

                List<CSVRecord> records = CSVAnalyzer.analyze(new ByteArrayInputStream(data), filter);
                for (CSVRecord record : records) {
                    serializer.recordFound(code, record);
                }
            }
        } catch (Throwable error) {
            // TODO handle this
            error.printStackTrace(System.err);
            System.exit(ErrorCode.GENERAL_ERROR.exitCode);
        }
    }
}
