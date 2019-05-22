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
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class Downloader {
    private Stack<URL> downloads = new Stack<>();
    private List<DownloaderListener> listeners = new ArrayList<>();
    private final CloseableHttpClient httpClient = HttpClients.createDefault();

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
                try {

                    // this is necessary to trust the self-signed certificate of the OH SOS website
                    SSLContextBuilder builder = new SSLContextBuilder();
                    builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
                    SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(builder.build());

                    CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
                    HttpHead httpHead = new HttpHead(url.toURI());
                    CloseableHttpResponse httpResponse = httpClient.execute(httpHead);
                    try {
                        System.out.println(url + ": " + Arrays.asList(httpResponse.getAllHeaders()));
                    } finally {
                        httpResponse.close();
                        httpClient.close();
                    }
                } catch (URISyntaxException e) {
                    Main.fatalError(e);
                } catch (ClientProtocolException e) {
                    Main.fatalError(e);
                } catch (IOException e) {
                    Main.fatalError(e);
                } catch (NoSuchAlgorithmException e) {
                    Main.fatalError(e);
                } catch (KeyStoreException e) {
                    Main.fatalError(e, "Try updating your Java version.");
                } catch (KeyManagementException e) {
                    Main.fatalError(e, "Try updating your Java version.");
                }
            });

        }
    }
}
