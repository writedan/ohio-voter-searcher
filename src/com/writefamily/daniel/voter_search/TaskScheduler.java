/*
 * Copyright (C) 2019  Daniel Write
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

package com.writefamily.daniel.voter_search;

import java.util.Stack;

public class TaskScheduler {
    private final int threads;
    private final Processor[] processors;

    /**
     * The number of tasks currently assigned to a thread
     */
    public TaskScheduler(int threads) {
        this.threads = threads;
        this.processors = new Processor[threads];
        for (int i = 0; i < this.processors.length; i++) {
            this.processors[i] = new Processor();
        }
        System.out.println("[TaskScheduler] " + threads + " threads");
    }

    public synchronized void scheduleTask(Runnable task) {
        int min = Integer.MAX_VALUE;
        int min_id = -1;
        for (int i = 0; i < processors.length; i++) {
            if (min > processors[i].getTasksScheduled()) {
                min = processors[i].getTasksScheduled();
                min_id = i;
            }
        }
        processors[min_id].scheduleTask(task);
        if (!processors[min_id].isAlive()) {
            processors[min_id].start();
        }
    }

    private static class Processor extends Thread {
        private final Stack<Runnable> tasks = new Stack<>();

        public synchronized int getTasksScheduled() {
            return tasks.size();
        }

        public synchronized void scheduleTask(Runnable task) {
            tasks.push(task);
        }

        public void run() {
            while (true) {
                try {
                    tasks.pop().run();
                } catch (Exception e) {
                    // TODO handle general errors
                }
            }
        }
    }
}
