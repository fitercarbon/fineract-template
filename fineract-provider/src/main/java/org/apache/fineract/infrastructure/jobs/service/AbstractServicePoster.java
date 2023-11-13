/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.fineract.infrastructure.jobs.service;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public abstract class AbstractServicePoster {

    public Queue<List<Long>> queue = new ArrayDeque<>();
    public int queueSize = 1;

    /**
     * Safelly extract sublists from a list, using indexe from and to.
     *
     * @param list
     * @param fromIndex
     * @param toIndex
     * @param <T>
     * @return
     */
    public <T> List<T> safeSubList(List<T> list, int fromIndex, int toIndex) {
        int size = list.size();
        if (fromIndex >= size || toIndex <= 0 || fromIndex >= toIndex) {
            return Collections.emptyList();
        }

        fromIndex = Math.max(0, fromIndex);
        toIndex = Math.min(size, toIndex);

        return list.subList(fromIndex, toIndex);
    }

    public void checkCompletion(List<Future<Void>> responses) throws ExecutionException, InterruptedException {

        for (Future f : responses) {
            f.get();
        }
        boolean allThreadsExecuted = false;
        int noOfThreadsExecuted = 0;
        for (Future<Void> future : responses) {
            if (future.isDone()) {
                noOfThreadsExecuted++;
            }
        }
        allThreadsExecuted = noOfThreadsExecuted == responses.size();
        if (!allThreadsExecuted) {
            log.error("All threads could not execute.");
        } else {
            System.out.println("###### /DONE SAVINGS ###################");
        }
    }

}
