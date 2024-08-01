package net.runelite.client.plugins.microbot.roguesden.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;

@Getter
@AllArgsConstructor
public enum RoguesDenWorldArea {
    BANK(new WorldArea(3035, 4958, 33, 34, 1)),
    OBSTACLE1(new WorldArea(3050, 4992, 14,16,1)),
    OBSTACLE2(new WorldArea(3042, 4995, 7, 5, 1)),
    OBSTACLE3(new WorldArea(3037, 4997, 5, 5, 1)),
    OBSTACLE4(new WorldArea(3028, 5002, 10, 1, 1)),
    OBSTACLE5(new WorldArea(3024, 5001, 4, 2, 1)),
    OBSTACLE6(new WorldArea(3018, 5000, 6,4, 1)),
    OBSTACLE7(new WorldArea(3015, 5002, 3, 2, 1)),
    OBSTACLE8(new WorldArea(3014, 5003, 1, 1, 1)),
    OBSTACLE9(new WorldArea(3012, 5001, 1, 1, 1)),
    OBSTACLE10(new WorldArea(3005, 5003, 4, 1, 1)),
    OBSTACLE11(new WorldArea(2994, 4995, 11, 11, 1)),
    OBSTACLE12A(new WorldArea(2971, 5000, 18, 20, 1)),
    OBSTACLE12B(new WorldArea(2970, 5017, 1, 1, 1)),
    OBSTACLE13(new WorldArea(2960, 5015, 9, 11, 1)),
    OBSTACLE14(new WorldArea(2950, 5028, 15, 3, 1)),
    OBSTACLE15(new WorldArea(2955, 5031, 11, 19, 1)),
    OBSTACLE16(new WorldArea(2962, 5050, 1, 1, 1)),
    LONGROUTE1(new WorldArea(2956, 5054, 10, 15, 1)),
    LONGROUTE2(new WorldArea(2957, 5072, 1, 2, 1)),
    LONGROUTE3(new WorldArea(2953, 5075, 6, 20, 1)),
    LONGROUTE4(new WorldArea(2950, 5098, 13, 13, 1)),
    LONGROUTE5(new WorldArea(2963, 5103, 1, 1, 1)),
    LONGROUTE6(new WorldArea(2964, 5098, 10, 4, 1)),
    LONGROUTE7(new WorldArea(2972, 5094, 1, 1, 1)),
    LONGROUTE8(new WorldArea(2967, 5086, 9, 8, 1)),
    LONGROUTE9(new WorldArea(2976, 5086, 7, 2, 1)),
    LONGROUTE10(new WorldArea(2983, 5087, 10, 3, 1)),
    LONGROUTE11(new WorldArea(2993, 5089, 8, 1, 1)),
    LONGROUTE12(new WorldArea(3003, 5080, 21, 11, 1)),
    LONGROUTE13(new WorldArea(3024, 5078, 7, 5, 1)),
    LONGROUTE14(new WorldArea(3031, 5078, 6, 2, 1)),
    LONGROUTE15(new WorldArea(3032, 5075, 5, 3, 1)),
    LONGROUTE16(new WorldArea(3037, 5075, 3, 5, 1)),
    LONGROUTE17(new WorldArea(3040, 5075, 3, 5, 1)),
    LONGROUTE18(new WorldArea(3043, 5069, 2, 9, 1)),
    LONGROUTE19(new WorldArea(3040, 5067, 5, 2, 1)),
    LONGROUTE20(new WorldArea(3040, 5069, 3, 3, 1)),
    LONGROUTE21(new WorldArea(3037, 5069, 3, 3, 1)),
    LONGROUTE22(new WorldArea(3037, 5032, 3, 37, 1)),
    LONGROUTE23(new WorldArea(3029, 5033, 8, 2, 1)),
    LONGROUTE24(new WorldArea(3028, 5033, 1, 1, 1)),
    LONGROUTE25(new WorldArea(3015, 5030, 10, 6, 1)),
    LONGROUTE26(new WorldArea(3011, 5032, 4, 3, 1)),
    LONGROUTE27(new WorldArea(3010, 5033, 1, 1, 1)),
    LONGROUTE28(new WorldArea(3009, 5033, 1, 1, 1)),
    LONGROUTE29(new WorldArea(3008, 5033, 1, 1, 1)),
    LONGROUTE30(new WorldArea(3003, 5032, 2, 3, 1)),
    LONGROUTE31(new WorldArea(2997, 5032, 4, 6, 1)),
    LONGROUTE32(new WorldArea(2998, 5038, 1, 1, 1)),
    LONGROUTE33(new WorldArea(2991, 5040, 9, 5, 1)),
    LONGROUTE34(new WorldArea(2992, 5045, 1, 8, 1)),
    LONGROUTE35(new WorldArea(2992, 5053, 1, 14, 1)),
    LONGROUTE36(new WorldArea(2992, 5067, 1, 8, 1)),
    LONGROUTE37A(new WorldArea(2992, 5075, 9, 5, 1)),
    LONGROUTE37B(new WorldArea(2999, 5063, 10, 12, 1)),
    LONGROUTE38(new WorldArea(3009, 5063, 1, 1, 1)),
    LONGROUTE39(new WorldArea(3028, 5055, 3, 2, 1)),
    LONGROUTE40(new WorldArea(3028, 5051, 3, 3, 1)),
    LONGROUTE41(new WorldArea(3010, 5043, 21, 5, 1))
    ;


    private final WorldArea worldArea;
}
