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

import com.writefamily.daniel.VoterSearcher.scheduling.TaskScheduler;

import java.io.File;
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
            "PIKE", "PORTAGE", "PUTNAM", "RICHLAND", "ROSS", "SANDUSKY",
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
                    lastName.add(val);
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

    }
}
