package com.justjava.humanresource.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
@Component
public class AfterCommitExecutor {

    public void runAfterCommit(Runnable runnable) {
        if (runnable == null) {
            return;
        }

        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    runSafely(runnable);
                }
            });
        } else {
            runSafely(runnable);
        }
    }

    private void runSafely(Runnable runnable) {
        try {
            runnable.run();
        } catch (Exception e) {
            log.warn("AfterCommitExecutor: callback threw an exception, ignoring: {}", e.getMessage(), e);
        }
    }
}