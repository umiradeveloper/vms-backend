package org.sim.umira.services;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

@ApplicationScoped
public class ExecutorConfig {
    @Produces
    @SuperappsExecutor
    public ExecutorService executor() {
        return Executors.newFixedThreadPool(5);
    }
}
