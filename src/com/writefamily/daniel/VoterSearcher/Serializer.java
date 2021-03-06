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
import org.apache.commons.csv.CSVRecord;

import java.io.*;

public class Serializer {
    private DataOutputStream outputStream;

    public Serializer(OutputStream outputStream) {
        this.outputStream = new DataOutputStream(outputStream);
    }

    public synchronized void write(byte[] packet) throws IOException {
        outputStream.writeInt(packet.length);
        for (byte b : packet) {
            outputStream.writeByte(b);
        }

        outputStream.flush();
    }

    // call this method when a county begins downloading
    public synchronized void beginCountyDownload(int countyCode, long length) throws IOException {
        ByteArrayOutputStream store = new ByteArrayOutputStream();
        DataOutputStream mani = new DataOutputStream(store);

        mani.writeByte(PacketCode.BEGIN_COUNTY_DOWNLOAD.code);
        mani.writeInt(countyCode);
        mani.writeLong(length);

        this.write(store.toByteArray());
    }

    // call this method when a county progresses its download
    public synchronized void countyDownloadProgress(int countyCode, int percent) throws IOException {
        ByteArrayOutputStream store = new ByteArrayOutputStream();
        DataOutputStream mani = new DataOutputStream(store);

        mani.writeByte(PacketCode.COUNTY_DOWNLOAD_PROGRESS.code);
        mani.writeInt(countyCode);
        mani.writeInt(percent);

        this.write(store.toByteArray());
    }

    // call this method when a county completes download/analyze
    public synchronized void countyComplete(int countyCode) throws IOException {
        ByteArrayOutputStream store = new ByteArrayOutputStream();
        DataOutputStream mani = new DataOutputStream(store);

        mani.writeByte(PacketCode.COUNTY_COMPLETE.code);
        mani.writeInt(countyCode);

        this.write(store.toByteArray());
    }

    // call this method when a new record is found
    public synchronized void recordFound(int countyCode, CSVRecord record) throws IOException {
        ByteArrayOutputStream store = new ByteArrayOutputStream();
        DataOutputStream mani = new DataOutputStream(store);
        ObjectOutputStream recordWriter = new ObjectOutputStream(mani);

        mani.writeByte(PacketCode.RECORD_FOUND.code);
        mani.writeInt(countyCode);
        recordWriter.writeObject(CSVAnalyzer.formatRecord(record));

        this.write(store.toByteArray());
    }

    private enum PacketCode {
        BEGIN_COUNTY_DOWNLOAD(0x00), COUNTY_DOWNLOAD_PROGRESS(0x01), COUNTY_COMPLETE(0x02), RECORD_FOUND(0x03);

        public final byte code;

        PacketCode(int code) {
            this.code = (byte) code;
        }
    }
}
