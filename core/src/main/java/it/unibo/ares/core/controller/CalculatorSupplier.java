package it.unibo.ares.core.controller;

import it.unibo.ares.core.api.InitializationApi;
import it.unibo.ares.core.api.SimulationControlApi;
import it.unibo.ares.core.utils.Pair;
import it.unibo.ares.core.utils.parameters.Parameters;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Flow.Subscriber;

/**
 * This class is used as an entry point for the simulation system, it is used to
 * access the initialization and the controller of the simulations.
 */
public final class CalculatorSupplier implements InitializationApi, SimulationControlApi {
    private static volatile CalculatorSupplier instance;

    private final SimulationsController controller;
    private final SimulationInitializer initializer;
    private final Ticker ticker;

    /**
     * Starts the simulation with the given initialization id.
     *
     * @param initializationId The id of the initialization to start.
     * @param subscriber       instance of the subscriber which the data of the
     *                         simulation will be passed to.
     * @return The id of the simulation.
     */
    public String startSimulation(final String initializationId, final Subscriber<SimulationOutputData> subscriber) {
        Pair<String, Simulation> resp = initializer.startSimulation(initializationId);
        controller.addSimulation(resp.getFirst(), resp.getSecond());
        controller.startSimulation(initializationId);
        controller.subscribe(initializationId, subscriber);
        return initializationId;
    }

    /**
     * Returns the singleton instance of the calculator supplier.
     *
     * @return the singleton instance of the calculator supplier.
     */
    public static CalculatorSupplier getInstance() {
        CalculatorSupplier curr = instance;

        if (curr != null) {
            return curr;
        }
        synchronized (CalculatorSupplier.class) {
            if (instance == null) {
                instance = new CalculatorSupplier(
                        new SimulationsControllerImpl(), new SimulationInitializerImpl());
            }
            return instance;
        }
    }

    private CalculatorSupplier(final SimulationsController c, final SimulationInitializer i) {
        this.controller = c;
        this.initializer = i;
        this.ticker = new TickerImpl(controller::makeModelsTick, 0, 50);
        ticker.start();
    }

    @Override
    public List<String> getRunningSimulations() {
        return controller.getRunningSimulations();
    }

    @Override
    public void removeSimulation(final String id) {
        controller.removeSimulation(id);
    }

    @Override
    public void pauseSimulation(final String id) {
        controller.pauseSimulation(id);
    }

    @Override
    public void startSimulation(final String id) {
        controller.startSimulation(id);
    }

    @Override
    public Set<String> getModels() {
        return initializer.getModels();
    }

    @Override
    public String setModel(final String modelId) {
        return initializer.setModel(modelId);
    }

    @Override
    public Parameters getModelParametersParameters(final String initializationId) {
        return initializer.getModelParametersParameters(initializationId);
    }

    @Override
    public void setModelParameter(final String initializationId, final String key, final Object value) {
        initializer.setModelParameter(initializationId, key, value);
    }

    @Override
    public Set<String> getAgentsSimplified(final String initializationId) {
        return initializer.getAgentsSimplified(initializationId);
    }

    @Override
    public Parameters getAgentParametersSimplified(final String initializationId, final String agentId) {
        return initializer.getAgentParametersSimplified(initializationId, agentId);
    }

    @Override
    public void setAgentParameterSimplified(final String initializationId, final String agentId, final String key,
            final Object value) {
        initializer.setAgentParameterSimplified(initializationId, agentId, key, value);
    }
}