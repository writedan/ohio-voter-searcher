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

import java.util.Stack;

public class Processor implements Comparable {
    private final Stack<Runnable> tasks = new Stack<>();
    private final int processorId;
    private int iteration = 0;
    private Thread runner = new Thread(() -> {
        while (tasks.size() > 0) {
            tasks.pop().run();
        }
    });

    public Processor(int processorId) {
        this.processorId = processorId;
    }

    public synchronized void addTask(Runnable task) {
        tasks.add(task);
        if (!runner.isAlive() || runner.getState() == Thread.State.TERMINATED) {
            iteration += 1;
            runner = new Thread(() -> {
                while (tasks.size() > 0) {
                    tasks.pop().run();
                }
            });
            runner.start();
        }
    }

    public int getTaskDensity() {
        return tasks.size();
    }

    @Override
    public int compareTo(Object o) {
        if (o == null) {
            return 1; // something is always greater than nothing
        } else if (o instanceof Processor) {
            Processor p = (Processor) o;
            return this.getTaskDensity() - p.getTaskDensity();
        } else {
            throw new IllegalArgumentException("Object must be of type Processor");
        }
    }
}
