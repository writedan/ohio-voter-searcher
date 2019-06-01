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

import com.writefamily.daniel.VoterSearcher.analysis.AnalysisQuery;
import com.writefamily.daniel.VoterSearcher.analysis.CSVAnalysis;
import com.writefamily.daniel.VoterSearcher.analysis.CSVAnalyzer;
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
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
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
    public static final File BASE_DIR = new File("datas" + File.separator);

    public static void main(String[] args) {
        List<String> firstName = new ArrayList<>(), lastName = new ArrayList<>(), birthYear = new ArrayList<>(),
                party = new ArrayList<>(), city = new ArrayList<>(), county = new ArrayList<>();
        for (int i = 0; i < args.length; i++) {
            String[] arg = args[i].split("=");
            String key = arg[0];
            String val = arg[1];
            switch (key) {
                case "FIRST_NAME":
                    firstName.addAll(Arrays.asList(val.split(";")));
                    break;
                case "LAST_NAME":
                    lastName.addAll(Arrays.asList(val.split(";")));
                    break;
                case "BIRTH_YEAR":
                    birthYear.addAll(Arrays.asList(val.split(";")));
                    break;
                case "PARTY":
                    party.addAll(Arrays.asList(val.split(";")));
                    break;
                case "CITY":
                    city.addAll(Arrays.asList(val.split(";")));
                    break;
                case "COUNTY":
                    county.addAll(Arrays.asList(val.split(";")));
                    break;
            }
        }

        if (county.size() == 0) {
            county.addAll(Arrays.asList(COUNTY_ARRAY));
        }

        int[] usingCounties = new int[county.size()];
        for (int i = 0; i < usingCounties.length; i++) {
            String needle = county.get(i);
            for (int j = 0; j < COUNTY_ARRAY.length; j++) {
                if (needle.equalsIgnoreCase(COUNTY_ARRAY[j])) {
                    usingCounties[i] = j;
                }
            }
        }

        for (int i = 0; i < usingCounties.length; i++) {
            int countyCode = usingCounties[i] + 1;
            // task scheduling causes a too high memory load to be worth the increase in efficiency
            //Main.TASK_SCHEDULER.scheduleTask(() -> {
            try {
                SSLContextBuilder builder = new SSLContextBuilder();
                builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
                SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(builder.build());

                CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
                HttpGet httpGet = new HttpGet(Main.OVR_BASE_URL + countyCode);
                CloseableHttpResponse httpResponse = httpClient.execute(httpGet);

                ByteArrayOutputStream store = new ByteArrayOutputStream();
                int b;
                while ((b = httpResponse.getEntity().getContent().read()) != -1) {
                    store.write(b);
                }
                byte[] data = store.toByteArray();
                CSVAnalysis analysis = CSVAnalyzer.analyze(new InputStreamReader(new ByteArrayInputStream(data)));
                AnalysisQuery query = analysis.filter("FIRST_NAME", firstName.toArray(new String[0]))
                        .filter("LAST_NAME", lastName.toArray(new String[0]))
                        .filter("BIRTH_YEAR", birthYear.toArray(new String[0]))
                        .filter("PARTY", party.toArray(new String[0]))
                        .filter("CITY", city.toArray(new String[0]));
                for (CSVRecord record : query.getRecords()) {
                    System.out.println(CSVAnalyzer.formatRecord(record));
                    System.out.println();
                }

                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (KeyStoreException e) {
                e.printStackTrace();
            } catch (KeyManagementException e) {
                e.printStackTrace();
            }
            //});
        }
    }
}
