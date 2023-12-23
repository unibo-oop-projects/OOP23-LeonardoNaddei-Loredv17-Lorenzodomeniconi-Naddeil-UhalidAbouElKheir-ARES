package it.unibo.ares.agent;

import static org.junit.jupiter.api.Assertions.*;

import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unibo.ares.utils.Pair;
import it.unibo.ares.utils.Pos;
import it.unibo.ares.utils.PosImpl;
import it.unibo.ares.utils.State;
import it.unibo.ares.utils.StateImpl;
import it.unibo.ares.utils.parameters.ParameterImpl;

public class AgentTest {
    private AgentBuilderImpl agentBuilder;

    @BeforeEach
    public void setUp() {
        agentBuilder = new AgentBuilderImpl();
    }

    private State getTestState() {
        State state = new StateImpl();
        return state;
    }

    private Agent getSimpleTestAgent() {
        agentBuilder.addStrategy((state, pos) -> state);
        return agentBuilder.build();
    }

    @Test
    public void SimpleAgentTest(){

        State simpleTestState = getTestState();
        Pos testPos = new PosImpl(1, 1);
        Agent agent = getSimpleTestAgent();

        assertEquals(agent.tick(simpleTestState, testPos), simpleTestState);
    }

    private boolean isAgentOfSameType(Agent a, Agent b){
        Integer type1 = a.getParameters().getParameter("type", Integer.class).get().getValue();
        Integer type2 = b.getParameters().getParameter("type", Integer.class).get().getValue();
        return type1.equals(type2);
    }

    private Agent getAgentWithStrategyAndWithParameter(ParameterImpl<Integer> parameter) {
        AgentBuilder b  =  new AgentBuilderImpl();

        //Removes all the agents of different types
        b.addStrategy((state, pos) -> {

            state.getAgents().stream()
            .filter(pair -> !isAgentOfSameType(pair.getSecond(), state.getAgentAt(pos)))
            .forEach(pair -> state.removeAgent(pair.getFirst(), pair.getSecond()));

            return state;
        });
        b.addParameter(parameter);
        return b.build();
    }

    @Test
    public void AgentWithStrategyAndParametersTest(){
        Agent agent1a = getAgentWithStrategyAndWithParameter(new ParameterImpl<Integer>("type", 1));
        Agent agent1b = getAgentWithStrategyAndWithParameter(new ParameterImpl<Integer>("type", 1));
        Agent agent2 = getAgentWithStrategyAndWithParameter(new ParameterImpl<Integer>("type", 2));

        State state = getTestState();
        state.addAgent(new PosImpl(1, 1), agent1a);
        state.addAgent(new PosImpl(1, 2), agent1b);
        state.addAgent(new PosImpl(1, 3), agent2);
    
        assertEquals(state.getAgents().size(), 3);

        state = agent1a.tick(state, new PosImpl(1, 1));
        assertEquals(2, state.getAgents().size());

        assertTrue(state.getAgents().stream().map(Pair::getSecond).collect(Collectors.toList()).contains(agent1a));
        assertTrue(state.getAgents().stream().map(Pair::getSecond).collect(Collectors.toList()).contains(agent1b));


    }

        @Test
    public void AgentWithStrategyAndParametersTest2(){
        Agent agent1a = getAgentWithStrategyAndWithParameter(new ParameterImpl<Integer>("type", 1));
        Agent agent1b = getAgentWithStrategyAndWithParameter(new ParameterImpl<Integer>("type", 1));
        Agent agent2 = getAgentWithStrategyAndWithParameter(new ParameterImpl<Integer>("type", 2));

        State state = getTestState();
        state.addAgent(new PosImpl(1, 1), agent1a);
        state.addAgent(new PosImpl(1, 2), agent1b);
        state.addAgent(new PosImpl(1, 3), agent2);
    
        assertEquals(state.getAgents().size(), 3);

        state = agent2.tick(state, new PosImpl(1, 3));
        assertEquals(1, state.getAgents().size());

        assertTrue(!state.getAgents().stream().map(Pair::getSecond).collect(Collectors.toList()).contains(agent1a));
        assertTrue(!state.getAgents().stream().map(Pair::getSecond).collect(Collectors.toList()).contains(agent1b));


    }

}
