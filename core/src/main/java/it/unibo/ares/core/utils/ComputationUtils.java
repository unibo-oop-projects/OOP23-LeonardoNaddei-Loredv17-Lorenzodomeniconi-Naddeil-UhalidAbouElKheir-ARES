package it.unibo.ares.core.utils;

import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import it.unibo.ares.core.utils.directionvector.DirectionVector;
import it.unibo.ares.core.utils.directionvector.DirectionVectorImpl;
import it.unibo.ares.core.utils.pos.Pos;
import it.unibo.ares.core.utils.pos.PosImpl;
import it.unibo.ares.core.utils.state.State;
import it.unibo.ares.core.utils.state.StateImpl;

public class ComputationUtils {
    /**
     * Test weather a position is inside a cone
     * 
     * @param pos      the position to test
     * @param center   the position of the observer
     * @param dir      the direction of the observer
     * @param distance the observable distance
     * @param angle    the observable angle
     * @return
     */
    public static boolean insideCone(final Pos pos, final Pos center, final DirectionVector dir, final Integer distance,
            final Integer angle) {
        double radAng = Math.toRadians(angle);

        DirectionVector vectorToNewPoint = new DirectionVectorImpl(pos.diff(center).getX(),
                pos.diff(center).getY());
        double dotProduct = dir.getNormalized().pointProduct(vectorToNewPoint.getNormalized());
        double radAngleBetween = Math.acos(dotProduct);

        return radAngleBetween <= radAng && vectorToNewPoint.getMagnitude() <= distance;
    }

    /**
     * Limits the given value to the range [0, max - 1].
     *
     * @param curr The current value.
     * @param max  The maximum value.
     * @return The limited value.
     */
    private static int limit(final int curr, final int max) {
        return curr < 0 ? 0 : curr > (max - 1) ? (max - 1) : curr;
    }

    /**
     * Limits the given position to the size of the environment.
     *
     * @param pos  The current position.
     * @param size The size of the environment.
     * @return The limited position.
     */
    public static Pos limit(final Pos pos, final Pair<Integer, Integer> size) {
        return new PosImpl(limit(pos.getX(), size.getFirst()), limit(pos.getY(), size.getSecond()));
    }

    /**
     * Moves the agent in the given direction by the given step size.
     *
     * @param initialPos The initial position of the agent.
     * @param dir        The direction in which the agent should move.
     * @param stepSize   The number of steps the agent should take.
     * @return The new position of the agent.
     */
    public static Pos move(final Pos initialPos, final DirectionVector dir, final Integer stepSize) {
        return new PosImpl(initialPos.getX() + dir.getNormalizedX() * stepSize,
                initialPos.getY() + dir.getNormalizedY() * stepSize);
    }

    /**
     * Generates a random direction for the agent to move in.
     *
     * @return The random direction.
     */
    public static DirectionVectorImpl getRandomDirection(Random r) {
        final int negBound = -10, posBound = 10;
        int x = r.nextInt(negBound, posBound), y = r.nextInt(negBound, posBound);
        if (x == 0 && y == 0) {
            return getRandomDirection(r);
        }
        return new DirectionVectorImpl(x, y);
    }

    public static Set<Pos> computeCloseCells(final Pos pos, final DirectionVector dir, final Integer distance,
            final Integer angle) {
        int xSign = dir.getX() > 0 ? 1 : -1;
        int ySign = dir.getY() > 0 ? 1 : -1;

        State a = new StateImpl(
                Math.abs(pos.getX() + xSign * (distance + 1)),
                Math.abs(pos.getY() + ySign * (distance + 1)));

        return a.getPosByPosAndRadius(pos, distance)
                .stream()
                .filter(p -> ComputationUtils.insideCone(p, pos, dir, distance, angle))
                .collect(Collectors.toSet());
    }

}
