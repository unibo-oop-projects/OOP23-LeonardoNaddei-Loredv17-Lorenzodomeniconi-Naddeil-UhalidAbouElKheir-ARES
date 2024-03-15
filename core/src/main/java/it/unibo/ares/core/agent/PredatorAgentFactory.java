
package it.unibo.ares.core.agent;

import it.unibo.ares.core.utils.parameters.ParameterDomainImpl;
import it.unibo.ares.core.utils.parameters.ParameterImpl;
import it.unibo.ares.core.utils.pos.Pos;
import it.unibo.ares.core.utils.pos.PosImpl;
import it.unibo.ares.core.utils.state.State;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A factory class for creating predator and prey agents for the Predator-Prey
 * Model.
 */
public final class PredatorAgentFactory implements AgentFactory {
    private static final String PREDATOR = "Predator";
    private static final String PREY = "Prey";

    private Set<Pos> getNeighboringPositions(final State state, final Pos position, final int visionRadius) {
        return state.getPosByPosAndRadius(position, visionRadius).stream()
                .filter(p -> !p.equals(position))
                .collect(Collectors.toSet());
    }

    private Optional<Pos> findPrey(final State state, final Pos position, final int visionRadius) {
        Set<Pos> neighbors = getNeighboringPositions(state, position, visionRadius);
        return neighbors.stream().filter(p -> state.getAgentAt(p).isPresent())
                .filter(p -> PREY.equals(state.getAgentAt(p).get().getType())).findFirst();
    }

    public Agent createPredatorAgent() {
        AgentBuilder builder = new AgentBuilderImpl();

        builder.addParameter(new ParameterImpl<Integer>("visionRadiusPredator", Integer.class,
                new ParameterDomainImpl<>("Raggio di visione dell'agente predatore (0 - n)", (Integer i) -> i > 0),
                true));

        builder.addStrategy((state, pos) -> {
            var visionRadius = state.getAgentAt(pos)
                    .orElseThrow(() -> new IllegalAccessError("No agents at that pos"))
                    .getParameters()
                    .getParameter("visionRadiusPredator", Integer.class)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Agent has no visionRadiusPredator parameter"))
                    .getValue();

            findPrey(state, pos, visionRadius).ifPresent(preyPosition -> {
                state.removeAgent(preyPosition, state.getAgentAt(preyPosition).get());
                state.moveAgent(pos, preyPosition);
            });
            return state;
        });

        var agent = builder.build();
        agent.setType(PREDATOR);
        return agent;
    }

    @Override
    public Agent createAgent() {
        return createPredatorAgent();
    }
}
