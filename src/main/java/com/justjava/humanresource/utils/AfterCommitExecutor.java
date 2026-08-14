package com.justjava.humanresource.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.concurrent.Executor;

@Slf4j
@Component
public class AfterCommitExecutor {

    private final Executor notificationTaskExecutor;

    public AfterCommitExecutor(@Qualifier("notificationTaskExecutor") Executor notificationTaskExecutor) {
        this.notificationTaskExecutor = notificationTaskExecutor;
    }

    public void runAfterCommit(Runnable runnable) {
        if (runnable == null) {
            return;
        }

        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    runAsync(runnable);
                }
            });
        } else {
            runAsync(runnable);
        }
    }

    private void runAsync(Runnable runnable) {
        try {
            notificationTaskExecutor.execute(() -> runSafely(runnable));
        } catch (RuntimeException e) {
            log.warn("AfterCommitExecutor: could not schedule callback, ignoring: {}", e.getMessage(), e);
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
