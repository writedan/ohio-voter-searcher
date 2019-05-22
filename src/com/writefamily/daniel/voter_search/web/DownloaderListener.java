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

import java.net.URL;

public class DownloaderListener {
    /**
     * Called when the Downloader begins on a URL
     *
     * @param url
     */
    public void startDownload(URL url) {
    }

    /**
     * Called when the Downloader finishes on a URL
     *
     * @param url
     */
    public void endDownload(URL url) {
    }

    /**
     * Called when the Downloader progresses on a URL
     *
     * @param url
     * @param bytesDownloaded
     * @param totalBytes
     */
    public void progressDownload(URL url, int bytesDownloaded, int totalBytes) {
    }
}
