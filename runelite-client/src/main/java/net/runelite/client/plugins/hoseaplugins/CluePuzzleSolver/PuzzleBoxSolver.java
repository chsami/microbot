package net.runelite.client.plugins.hoseaplugins.CluePuzzleSolver;

import net.runelite.client.plugins.hoseaplugins.ethanapi.Packets.MousePackets;
import net.runelite.client.plugins.hoseaplugins.ethanapi.Packets.WidgetPackets;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.ComponentID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.puzzlesolver.solver.PuzzleSolver;
import net.runelite.client.plugins.puzzlesolver.solver.PuzzleState;
import net.runelite.client.plugins.puzzlesolver.solver.heuristics.ManhattanDistance;
import net.runelite.client.plugins.puzzlesolver.solver.pathfinding.IDAStar;
import net.runelite.client.plugins.puzzlesolver.solver.pathfinding.IDAStarMM;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;

import static net.runelite.client.plugins.puzzlesolver.solver.PuzzleSolver.BLANK_TILE_VALUE;
import static net.runelite.client.plugins.puzzlesolver.solver.PuzzleSolver.DIMENSION;

@Slf4j
@PluginDescriptor(
        name = "<html><font color=\"#FF9DF9\">[PP]</font> Puzzle Box Solver</html>",
        description = "Solves puzzle boxes for you.",
        enabledByDefault = false,
        tags = {"ethan", "piggy", "plugin", "clue", "puzzle"}
)
public class PuzzleBoxSolver extends Plugin {
    @Inject private Client client;
    @Inject private ScheduledExecutorService executorService;

    private PuzzleSolver solver;
    private Future<?> solverFuture;
    private int[] cachedItems;

    @Subscribe
    public void onGameTick(GameTick gameTick) {
        boolean useNormalSolver = true;
        ItemContainer container = client.getItemContainer(InventoryID.PUZZLE_BOX);

        if (container == null) return;

        Widget puzzleBox = client.getWidget(ComponentID.PUZZLE_BOX_VISIBLE_BOX);

        if (puzzleBox == null) return;

        int[] itemIds = getItemIds(container, useNormalSolver);
        boolean shouldCache = false;

        if (solver != null) {
            if (solver.hasFailed()) {
                log.warn("Puzzle solver failed");
            }
            else {
                if (solver.hasSolution()) {
                    boolean foundPosition = false;

                    // Find the current state by looking at the current step and then the next 5 steps
                    for (int i = 0; i < 6; i++) {
                        int j = solver.getPosition() + i;

                        if (j == solver.getStepCount()) {
                            break;
                        }

                        PuzzleState currentState = solver.getStep(j);

                        // If this is false, player has moved the empty tile
                        if (currentState != null && currentState.hasPieces(itemIds)) {
                            foundPosition = true;
                            solver.setPosition(j);
                            if (i > 0) {
                                shouldCache = true;
                            }
                            break;
                        }
                    }

                    // If looking at the next steps didn't find the current state,
                    // see if we can find the current state in the 5 previous steps
                    if (!foundPosition) {
                        for (int i = 1; i < 6; i++) {
                            int j = solver.getPosition() - i;

                            if (j < 0) {
                                break;
                            }

                            PuzzleState currentState = solver.getStep(j);

                            if (currentState != null && currentState.hasPieces(itemIds)) {
                                foundPosition = true;
                                shouldCache = true;
                                solver.setPosition(j);
                                break;
                            }
                        }
                    }

                    if (foundPosition) {
                        // Snag the next 5 steps
                        for (int i = 1; i < solver.getStepCount(); i++) {
                            int j = solver.getPosition() + i;

                            if (j >= solver.getStepCount()) {
                                break;
                            }

                            PuzzleState futureMove = solver.getStep(j);

                            if (futureMove == null) {
                                break;
                            }

                            int child = futureMove.getEmptyPiece();
                            var children = puzzleBox.getDynamicChildren();
                            var childWidget = children[child];
                            MousePackets.queueClickPacket();
                            WidgetPackets.queueWidgetAction(childWidget, "Move");
                        }
                    }
                }
            }
        }

        // Solve the puzzle if we don't have an up to date solution
        if (solver == null || cachedItems == null || (!shouldCache && solver.hasExceededWaitDuration() && !Arrays.equals(cachedItems, itemIds))) {
            solve(itemIds, useNormalSolver);
            shouldCache = true;
        }

        if (shouldCache) {
            cacheItems(itemIds);
        }
    }

    /*
     * Copied from PuzzleSolverPlugin.java
     * :p
     */

    private int[] getItemIds(ItemContainer container, boolean useNormalSolver) {
        int[] itemIds = new int[DIMENSION * DIMENSION];

        Item[] items = container.getItems();

        for (int i = 0; i < items.length; i++) {
            itemIds[i] = items[i].getId();
        }

        // If blank is in the last position, items doesn't contain it, so let's add it manually
        if (itemIds.length > items.length) {
            itemIds[items.length] = BLANK_TILE_VALUE;
        }

        return convertToSolverFormat(itemIds, useNormalSolver);
    }

    private int[] convertToSolverFormat(int[] items, boolean useNormalSolver) {
        int lowestId = Integer.MAX_VALUE;

        int[] convertedItems = new int[items.length];

        for (int id : items) {
            if (id == BLANK_TILE_VALUE) {
                continue;
            }

            if (lowestId > id) {
                lowestId = id;
            }
        }

        for (int i = 0; i < items.length; i++) {
            if (items[i] != BLANK_TILE_VALUE) {
                int value = items[i] - lowestId;

                // The MM puzzle has gaps
                if (!useNormalSolver) {
                    value /= 2;
                }

                convertedItems[i] = value;
            }
            else {
                convertedItems[i] = BLANK_TILE_VALUE;
            }
        }

        return convertedItems;
    }

    private void cacheItems(int[] items) {
        cachedItems = new int[items.length];
        System.arraycopy(items, 0, cachedItems, 0, cachedItems.length);
    }

    private void solve(int[] items, boolean useNormalSolver) {
        if (solverFuture != null) {
            solverFuture.cancel(true);
        }

        PuzzleState puzzleState = new PuzzleState(items);

        if (useNormalSolver) {
            solver = new PuzzleSolver(new IDAStar(new ManhattanDistance()), puzzleState);
        }
        else {
            solver = new PuzzleSolver(new IDAStarMM(new ManhattanDistance()), puzzleState);
        }

        solverFuture = executorService.submit(solver);
    }
}