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

package com.writefamily.daniel.voter_search.web;

import com.writefamily.daniel.voter_search.Main;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Stack;

public class Downloader {
    private Stack<URL> downloads = new Stack<>();
    private List<DownloaderListener> listeners = new ArrayList<>();

    public Downloader(Collection<URL> downloads) {
        this.downloads.addAll(downloads);
    }

    public Downloader() {
    }

    public void addDownload(URL url) {
        downloads.add(url);
    }

    public void addListener(DownloaderListener listener) {
        listeners.add(listener);
    }

    public void removeListener(DownloaderListener listener) {
        listeners.remove(listener);
    }

    public void startDownloading() {
        while (downloads.size() > 0) {
            final URL url = downloads.pop();
            Main.TASK_SCHEDULER.scheduleTask(() -> {
                System.out.println(url);
            });
        }
    }
}
