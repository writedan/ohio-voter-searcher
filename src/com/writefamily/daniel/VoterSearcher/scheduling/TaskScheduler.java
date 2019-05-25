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

package com.writefamily.daniel.VoterSearcher.scheduling;

public class TaskScheduler {
    private final int processors;
    private Processor[] processorArray;

    public TaskScheduler(int processors) {
        this.processors = processors;
        processorArray = new Processor[processors];
        for (int i = 0; i < processors; i++) {
            processorArray[i] = new Processor(i + 1);
        }
    }

    public void scheduleTask(Runnable runnable) {
        Processor minimimDensityProcessor = null;
        for (Processor processor : processorArray) {
            minimimDensityProcessor = processor.compareTo(minimimDensityProcessor) < 0 ? processor : minimimDensityProcessor;
        }
        minimimDensityProcessor.addTask(runnable);
    }
}
