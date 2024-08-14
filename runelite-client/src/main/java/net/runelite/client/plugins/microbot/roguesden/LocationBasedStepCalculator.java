package net.runelite.client.plugins.microbot.roguesden;

import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.roguesden.model.*;
import net.runelite.client.plugins.microbot.roguesden.steps.*;

import javax.inject.Inject;
import java.util.AbstractMap;
import java.util.Map;

import static net.runelite.client.plugins.microbot.roguesden.model.RoguesDenWorldArea.*;

public class LocationBasedStepCalculator {

    @Inject
    public LocationBasedStepCalculator(final BotApi botApi)
    {
        this.botApi = botApi;
    }

    private final BotApi botApi;
    private Map<RoguesDenWorldArea, Step> locationToStepMap;

    public Step getCurrentStep()
    {
        final WorldPoint currentPoint = botApi.getCurrentPlayerLocation();

        for (var entry : getLocationToStepMap().entrySet())
        {
            if (entry.getKey().getWorldArea().contains(currentPoint))
            {
                return entry.getValue();
            }
        }

        return new DefaultStep();
    }

    private Map<RoguesDenWorldArea, Step> getLocationToStepMap()
    {
        if (locationToStepMap == null)
        {
            constructStepMap();
        }

        return locationToStepMap;
    }

    private void constructStepMap()
    {
        locationToStepMap = Map.ofEntries(
                new AbstractMap.SimpleEntry<>(BANK, new EnterCourseStep(botApi, "1, Enter Course")),
                new AbstractMap.SimpleEntry<>(OBSTACLE1, new InteractGameObjectStep(botApi, "2, Contortion Bars", new WorldPoint(3049, 4997, 1))),
                new AbstractMap.SimpleEntry<>(OBSTACLE2, new MoveToLocationStep(botApi, "3, Pendulum", new WorldPoint(3041, 4997, 1))),
                new AbstractMap.SimpleEntry<>(OBSTACLE3, new MoveToLocationStep(botApi, "4, Walk to corner", new WorldPoint(3037, 5002, 1))),
                new AbstractMap.SimpleEntry<>(OBSTACLE4, new MoveToLocationStep(botApi, "5, Walk along edge", new WorldPoint(3027, 5002, 1))),
                new AbstractMap.SimpleEntry<>(OBSTACLE5, new InteractWallObjectStep(botApi, "6, Bars", new WorldPoint(3024, 5001, 1), "Open")),
                new AbstractMap.SimpleEntry<>(OBSTACLE6, new MoveToLocationStep(botApi, "7, Run up to wall trap", new WorldPoint(3017, 5003, 1), 20)),
                new AbstractMap.SimpleEntry<>(OBSTACLE7, new MoveToLocationStep(botApi, "8, Run to safe tile", new WorldPoint(3014, 5003, 1), 20)),
                new AbstractMap.SimpleEntry<>(OBSTACLE8, new MoveToLocationStep(botApi, "9, Run to safe tile", new WorldPoint(3012, 5001, 1), 20)),
                new AbstractMap.SimpleEntry<>(OBSTACLE9, new MoveToLocationStep(botApi, "10, Run to safe tile", new WorldPoint(3008, 5003, 1), 20)),
                new AbstractMap.SimpleEntry<>(OBSTACLE10, new MoveToLocationStep(botApi, "11, Run to safe tile", new WorldPoint(3004, 5003, 1), 20)),
                new AbstractMap.SimpleEntry<>(OBSTACLE11, new InteractGameObjectStep(botApi, "12, Climb ledge", new WorldPoint(2993, 5004, 1))),
                new AbstractMap.SimpleEntry<>(OBSTACLE12A, new MoveToLocationStep(botApi, "13, Walk to jump saw", new WorldPoint(2970, 5017, 1))),
                new AbstractMap.SimpleEntry<>(OBSTACLE12B, new MoveToLocationStep(botApi, "14, Jump saw", new WorldPoint(2969, 5017, 1))),
                new AbstractMap.SimpleEntry<>(OBSTACLE13, new MoveToLocationStep(botApi, "15, Pendulum", new WorldPoint(2961, 5026, 1))),
                new AbstractMap.SimpleEntry<>(OBSTACLE14, new MoveToLocationStep(botApi, "16, Walk over log", new WorldPoint(2958, 5031, 1))),
                new AbstractMap.SimpleEntry<>(OBSTACLE15, new MoveToLocationStep(botApi, "17, Run up to trap", new WorldPoint(2962, 5050, 1))),
                new AbstractMap.SimpleEntry<>(OBSTACLE16, new MoveToLocationStep(botApi, "18, Run over traps", new WorldPoint(2962, 5054, 1), 20)),

                //long route
                new AbstractMap.SimpleEntry<>(LONGROUTE1, new MoveToLocationStep(botApi, "19, Enter passage", new WorldPoint(2957, 5069, 1))),
                new AbstractMap.SimpleEntry<>(LONGROUTE2, new MoveToLocationStep(botApi, "20, Jump saw", new WorldPoint(2957, 5074, 1))),
                new AbstractMap.SimpleEntry<>(LONGROUTE3, new MoveToLocationStep(botApi, "21, Enter passage", new WorldPoint(2955, 5095, 1))),
                new AbstractMap.SimpleEntry<>(LONGROUTE4, new MoveToLocationStep(botApi, "22, Walk to trap", new WorldPoint(2963, 5103, 1))),
                new AbstractMap.SimpleEntry<>(LONGROUTE5, new MoveToLocationStep(botApi, "23, Run over trap", new WorldPoint(2964, 5101, 1), 20)),
                new AbstractMap.SimpleEntry<>(LONGROUTE6, new MoveToLocationStep(botApi, "24, Run over trap to passage", new WorldPoint(2972, 5097, 1), 20)),
                new AbstractMap.SimpleEntry<>(LONGROUTE7, new InteractWallObjectStep(botApi, "25, Open grill", new WorldPoint(2972, 5094, 1), "Open")),
                new AbstractMap.SimpleEntry<>(LONGROUTE8, new MoveToLocationStep(botApi, "26, Walk around blades", new WorldPoint(2976, 5086, 1))),
                new AbstractMap.SimpleEntry<>(LONGROUTE9, new InteractGameObjectStep(botApi, "27, Climb ledge", new WorldPoint(2983, 5087, 1))),
                new AbstractMap.SimpleEntry<>(LONGROUTE10, new InteractGameObjectStep(botApi, "28, Search wall", new WorldPoint(2993, 5090, 1), "Search")),
                new AbstractMap.SimpleEntry<>(LONGROUTE11, new MoveToLocationStep(botApi, "29, Run over traps", new WorldPoint(3001, 5089, 1), 20)),
                new AbstractMap.SimpleEntry<>(LONGROUTE12, new OpenTileDoorStep(botApi, "30, Opening tile door")),
                new AbstractMap.SimpleEntry<>(LONGROUTE13, new InteractWallObjectStep(botApi, "31, Open grill", new WorldPoint(3030, 5079, 1), "Open")),
                new AbstractMap.SimpleEntry<>(LONGROUTE14, new InteractWallObjectStep(botApi, "32, Open grill", new WorldPoint(3032, 5078, 1), "Open")),
                new AbstractMap.SimpleEntry<>(LONGROUTE15, new InteractWallObjectStep(botApi, "33, Open grill", new WorldPoint(3036, 5076, 1), "Open")),
                new AbstractMap.SimpleEntry<>(LONGROUTE16, new InteractWallObjectStep(botApi, "34, Open grill", new WorldPoint(3039, 5079, 1), "Open")),
                new AbstractMap.SimpleEntry<>(LONGROUTE17, new InteractWallObjectStep(botApi, "35, Open grill", new WorldPoint(3042, 5076, 1), "Open")),
                new AbstractMap.SimpleEntry<>(LONGROUTE18, new InteractWallObjectStep(botApi, "36, Open grill", new WorldPoint(3044, 5069, 1), "Open")),
                new AbstractMap.SimpleEntry<>(LONGROUTE19, new InteractWallObjectStep(botApi, "37, Open grill", new WorldPoint(3041, 5068, 1), "Open")),
                new AbstractMap.SimpleEntry<>(LONGROUTE20, new InteractWallObjectStep(botApi, "38, Open grill", new WorldPoint(3040, 5070, 1), "Open")),
                new AbstractMap.SimpleEntry<>(LONGROUTE21, new InteractWallObjectStep(botApi, "39, Open grill", new WorldPoint(3038, 5069, 1), "Open")),
                new AbstractMap.SimpleEntry<>(LONGROUTE22, new MoveToLocationStep(botApi, "40, Walk south", new WorldPoint(3036, 5033, 1))),
                new AbstractMap.SimpleEntry<>(LONGROUTE23, new MoveToLocationStep(botApi, "41, Walk to trap", new WorldPoint(3028, 5033, 1))),
                new AbstractMap.SimpleEntry<>(LONGROUTE24, new MoveToLocationStep(botApi, "42, Run over trap", new WorldPoint(3023, 5033, 1), 20)),
                new AbstractMap.SimpleEntry<>(LONGROUTE25, new InteractWallObjectStep(botApi, "43, Open grill", new WorldPoint(3015, 5033, 1), "Open")),
                new AbstractMap.SimpleEntry<>(LONGROUTE26, new MoveToLocationStep(botApi, "44, Run over traps", new WorldPoint(3010, 5033, 1), 20)),
                new AbstractMap.SimpleEntry<>(LONGROUTE27, new InteractWallObjectStep(botApi, "45, Open grill", new WorldPoint(3010, 5033, 1), "Open")),
                new AbstractMap.SimpleEntry<>(LONGROUTE28, new MoveToLocationStep(botApi, "46, Walk to traps", new WorldPoint(3008, 5033, 1))),
                new AbstractMap.SimpleEntry<>(LONGROUTE29, new MoveToLocationStep(botApi, "47, Run over traps", new WorldPoint(3004, 5033, 1), 20)),
                new AbstractMap.SimpleEntry<>(LONGROUTE30, new MoveToLocationStep(botApi, "48, Pendulum", new WorldPoint(3002, 5032, 1))),
                new AbstractMap.SimpleEntry<>(LONGROUTE31, new MoveToLocationStep(botApi, "49, Walk to traps", new WorldPoint(2998, 5038, 1))),
                new AbstractMap.SimpleEntry<>(LONGROUTE32, new MoveToLocationStep(botApi, "50, Run over traps", new WorldPoint(2998, 5040, 1), 20)),
                new AbstractMap.SimpleEntry<>(LONGROUTE33, new MoveToLocationStep(botApi, "51, Walk to traps", new WorldPoint(2992, 5045, 1))),
                new AbstractMap.SimpleEntry<>(LONGROUTE34, new MoveToLocationStep(botApi, "52, Run over traps", new WorldPoint(2992, 5053, 1), 20)),
                new AbstractMap.SimpleEntry<>(LONGROUTE35, new MoveToLocationStep(botApi, "53, Walk to traps", new WorldPoint(2992, 5067, 1))),
                new AbstractMap.SimpleEntry<>(LONGROUTE36, new MoveToLocationStep(botApi, "54, Run over traps", new WorldPoint(2992, 5075, 1), 20)),
                new AbstractMap.SimpleEntry<>(LONGROUTE37A, new MoveToLocationStep(botApi, "55, Walk to item", new WorldPoint(3009, 5063, 1))),
                new AbstractMap.SimpleEntry<>(LONGROUTE37B, new MoveToLocationStep(botApi, "56, Walk to item", new WorldPoint(3009, 5063, 1))),
                new AbstractMap.SimpleEntry<>(LONGROUTE38, new StunGuardStep(botApi)),
                new AbstractMap.SimpleEntry<>(LONGROUTE39, new MoveToLocationStep(botApi, "58, Pendulum", new WorldPoint(3028, 5053, 1))),
                new AbstractMap.SimpleEntry<>(LONGROUTE40, new MoveToLocationStep(botApi, "59, Pendulum", new WorldPoint(3028, 5049, 1))),
                new AbstractMap.SimpleEntry<>(LONGROUTE41, new CrackSafeStep(botApi))
        );
    }
}
