package net.runelite.client.plugins.hoseaplugins.RooftopAgility;

import lombok.AccessLevel;
import lombok.Getter;
import net.runelite.api.ObjectID;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;

import static net.runelite.api.ObjectID.BALANCING_ROPE_23557;
import static net.runelite.api.ObjectID.BANK_BOOTH_10355;
import static net.runelite.api.ObjectID.BANK_BOOTH_24347;
import static net.runelite.api.ObjectID.BANK_BOOTH_25808;
import static net.runelite.api.ObjectID.BANNER_14937;
import static net.runelite.api.ObjectID.BASKET_14935;
import static net.runelite.api.ObjectID.CABLE;
import static net.runelite.api.ObjectID.CHIMNEY_36227;
import static net.runelite.api.ObjectID.CLOTHES_LINE;
import static net.runelite.api.ObjectID.CRATE_11632;
import static net.runelite.api.ObjectID.DARK_HOLE_36229;
import static net.runelite.api.ObjectID.DARK_HOLE_36238;
import static net.runelite.api.ObjectID.DRYING_LINE;
import static net.runelite.api.ObjectID.EDGE;
import static net.runelite.api.ObjectID.EDGE_14925;
import static net.runelite.api.ObjectID.EDGE_14931;
import static net.runelite.api.ObjectID.GAP_11631;
import static net.runelite.api.ObjectID.GAP_14399;
import static net.runelite.api.ObjectID.GAP_14414;
import static net.runelite.api.ObjectID.GAP_14833;
import static net.runelite.api.ObjectID.GAP_14834;
import static net.runelite.api.ObjectID.GAP_14835;
import static net.runelite.api.ObjectID.GAP_14844;
import static net.runelite.api.ObjectID.GAP_14845;
import static net.runelite.api.ObjectID.GAP_14846;
import static net.runelite.api.ObjectID.GAP_14847;
import static net.runelite.api.ObjectID.GAP_14848;
import static net.runelite.api.ObjectID.GAP_14897;
import static net.runelite.api.ObjectID.GAP_14903;
import static net.runelite.api.ObjectID.GAP_14904;
import static net.runelite.api.ObjectID.GAP_14919;
import static net.runelite.api.ObjectID.GAP_14928;
import static net.runelite.api.ObjectID.GAP_14929;
import static net.runelite.api.ObjectID.GAP_14930;
import static net.runelite.api.ObjectID.GAP_14938;
import static net.runelite.api.ObjectID.GAP_14947;
import static net.runelite.api.ObjectID.GAP_14990;
import static net.runelite.api.ObjectID.GAP_14991;
import static net.runelite.api.ObjectID.GAP_15609;
import static net.runelite.api.ObjectID.GAP_15610;
import static net.runelite.api.ObjectID.GAP_15611;
import static net.runelite.api.ObjectID.GAP_15612;
import static net.runelite.api.ObjectID.HAND_HOLDS_14901;
import static net.runelite.api.ObjectID.LADDER_36221;
import static net.runelite.api.ObjectID.LADDER_36231;
import static net.runelite.api.ObjectID.LADDER_36232;
import static net.runelite.api.ObjectID.LEDGE_14836;
import static net.runelite.api.ObjectID.LEDGE_14920;
import static net.runelite.api.ObjectID.LEDGE_14921;
import static net.runelite.api.ObjectID.LEDGE_14922;
import static net.runelite.api.ObjectID.LEDGE_14924;
import static net.runelite.api.ObjectID.LOG_BALANCE_23145;
import static net.runelite.api.ObjectID.MARKET_STALL_14936;
import static net.runelite.api.ObjectID.MONKEYBARS;
import static net.runelite.api.ObjectID.MONKEYBARS_15417;
import static net.runelite.api.ObjectID.NARROW_WALL;
import static net.runelite.api.ObjectID.OBSTACLE_NET_23134;
import static net.runelite.api.ObjectID.OBSTACLE_NET_23135;
import static net.runelite.api.ObjectID.OBSTACLE_PIPE_23139;
import static net.runelite.api.ObjectID.PILE_OF_FISH;
import static net.runelite.api.ObjectID.PLANK_26635;
import static net.runelite.api.ObjectID.POLEVAULT;
import static net.runelite.api.ObjectID.ROOF_EDGE;
import static net.runelite.api.ObjectID.ROOF_TOP_BEAMS;
import static net.runelite.api.ObjectID.ROPE_15487;
import static net.runelite.api.ObjectID.ROPE_BRIDGE_36233;
import static net.runelite.api.ObjectID.ROPE_BRIDGE_36235;
import static net.runelite.api.ObjectID.ROUGH_WALL;
import static net.runelite.api.ObjectID.ROUGH_WALL_11633;
import static net.runelite.api.ObjectID.ROUGH_WALL_14412;
import static net.runelite.api.ObjectID.ROUGH_WALL_14898;
import static net.runelite.api.ObjectID.ROUGH_WALL_14940;
import static net.runelite.api.ObjectID.ROUGH_WALL_14946;
import static net.runelite.api.ObjectID.STEEP_ROOF;
import static net.runelite.api.ObjectID.STEPPING_STONE_15412;
import static net.runelite.api.ObjectID.TALL_TREE_14843;
import static net.runelite.api.ObjectID.TIGHTROPE;
import static net.runelite.api.ObjectID.TIGHTROPE_11406;
import static net.runelite.api.ObjectID.TIGHTROPE_14398;
import static net.runelite.api.ObjectID.TIGHTROPE_14409;
import static net.runelite.api.ObjectID.TIGHTROPE_14899;
import static net.runelite.api.ObjectID.TIGHTROPE_14905;
import static net.runelite.api.ObjectID.TIGHTROPE_14911;
import static net.runelite.api.ObjectID.TIGHTROPE_14932;
import static net.runelite.api.ObjectID.TIGHTROPE_14987;
import static net.runelite.api.ObjectID.TIGHTROPE_14992;
import static net.runelite.api.ObjectID.TIGHTROPE_36225;
import static net.runelite.api.ObjectID.TIGHTROPE_36234;
import static net.runelite.api.ObjectID.TIGHTROPE_36236;
import static net.runelite.api.ObjectID.TIGHTROPE_36237;
import static net.runelite.api.ObjectID.TREE_14939;
import static net.runelite.api.ObjectID.TREE_14944;
import static net.runelite.api.ObjectID.TREE_BRANCH_23559;
import static net.runelite.api.ObjectID.TREE_BRANCH_23560;
import static net.runelite.api.ObjectID.TROPICAL_TREE_14404;
import static net.runelite.api.ObjectID.TROPICAL_TREE_15414;
import static net.runelite.api.ObjectID.TROPICAL_TREE_16062;
import static net.runelite.api.ObjectID.WALL_11630;
import static net.runelite.api.ObjectID.WALL_14832;
import static net.runelite.api.ObjectID.WALL_14927;
import static net.runelite.api.ObjectID.WOODEN_BEAMS;
import static net.runelite.api.ObjectID.ZIP_LINE_14403;

public enum Obstacles {
    //TREE GNOME
    GNOME_LOG(new WorldPoint(2470, 3435, 0), new WorldPoint(2489, 3447, 0), LOG_BALANCE_23145),
    GNOME_NET(new WorldPoint(2470, 3423, 0), new WorldPoint(2477, 3430, 0), OBSTACLE_NET_23134),
    GNOME_TREE(new WorldPoint(2470, 3421, 1), new WorldPoint(2476, 3425, 1), TREE_BRANCH_23559),
    GNOME_ROPE(new WorldPoint(2469, 3416, 2), new WorldPoint(2479, 3423, 2), BALANCING_ROPE_23557),
    GNOME_TREE_TWO(new WorldPoint(2482, 3416, 2), new WorldPoint(2489, 3423, 2), TREE_BRANCH_23560),
    GNOME_NET_TWO(new WorldPoint(2482, 3418, 0), new WorldPoint(2489, 3427, 0), OBSTACLE_NET_23135),
    GNOME_PIPE(new WorldPoint(2482, 3427, 0), new WorldPoint(2489, 3433, 0), OBSTACLE_PIPE_23139),
    //DRAYNOR
    DRAY_WALL(new WorldPoint(3082, 3238, 0), new WorldPoint(3105, 3293, 0), ROUGH_WALL, BANK_BOOTH_10355),
    DRAY_TIGHTROPE(new WorldPoint(3096, 3275, 3), new WorldPoint(3103, 3282, 3), TIGHTROPE),
    DRAY_TIGHTROPE_TWO(new WorldPoint(3086, 3271, 3), new WorldPoint(3093, 3279, 3), TIGHTROPE_11406),
    DRAY_NARROW_WALL(new WorldPoint(3087, 3263, 3), new WorldPoint(3095, 3269, 3), NARROW_WALL),
    DRAY_WALL_TWO(new WorldPoint(3082, 3256, 3), new WorldPoint(3089, 3262, 3), WALL_11630), //COULD CONFLICT WITH NEXT LINE
    DRAY_GAP(new WorldPoint(3087, 3254, 3), new WorldPoint(3095, 3256, 3), GAP_11631),
    DRAY_CRATE(new WorldPoint(3095, 3255, 3), new WorldPoint(3102, 3262, 3), CRATE_11632),
    //ALKHARID
    ALK_ROUGHWALL(new WorldPoint(3268, 3159, 0), new WorldPoint(3322, 3200, 0), ROUGH_WALL_11633),
    ALK_TIGHTROPE(new WorldPoint(3270, 3179, 3), new WorldPoint(3278, 3193, 3), TIGHTROPE_14398),
    ALK_CABLE(new WorldPoint(3263, 3160, 3), new WorldPoint(3274, 3174, 3), CABLE),
    ALK_ZIPLINE(new WorldPoint(3282, 3159, 3), new WorldPoint(3303, 3176, 3), ZIP_LINE_14403),
    ALK_TROPICAL_TREE(new WorldPoint(3312, 3159, 1), new WorldPoint(3319, 3166, 1), TROPICAL_TREE_14404),
    ALK_ROOF_TOP_BEAMS(new WorldPoint(3311, 3172, 2), new WorldPoint(3319, 3180, 2), ROOF_TOP_BEAMS),
    ALK_TIGHTROPE_TWO(new WorldPoint(3311, 3180, 3), new WorldPoint(3319, 3187, 3), TIGHTROPE_14409),
    ALK_GAP(new WorldPoint(3296, 3184, 3), new WorldPoint(3306, 3194, 3), GAP_14399),
    //VARROCK
    COURSE_GROUND(new WorldPoint(3184, 3386, 0), new WorldPoint(3258, 3428, 0), ROUGH_WALL_14412, ObjectID.BANK_BOOTH_10583),
    ROOFTOP_ONE(new WorldPoint(3213, 3409, 3), new WorldPoint(3220, 3420, 3), CLOTHES_LINE),
    ROOFTOP_TWO(new WorldPoint(3200, 3412, 3), new WorldPoint(3209, 3420, 3), GAP_14414),
    CROSSWALK(new WorldPoint(3192, 3415, 1), new WorldPoint(3198, 3417, 1), WALL_14832),
    ROOFTOP_THREE(new WorldPoint(3191, 3401, 3), new WorldPoint(3198, 3407, 3), GAP_14833),
    ROOFTOP_FOUR(new WorldPoint(3181, 3393, 3), new WorldPoint(3209, 3401, 3), GAP_14834),
    ROOFTOP_FIVE(new WorldPoint(3217, 3392, 3), new WorldPoint(3233, 3404, 3), GAP_14835),
    ROOFTOP_SIX(new WorldPoint(3235, 3402, 3), new WorldPoint(3240, 3409, 3), LEDGE_14836),
    ROOFTOP_SEVEN(new WorldPoint(3235, 3410, 3), new WorldPoint(3240, 3416, 3), EDGE),
    //Canifis
    CAN_GROUND(new WorldPoint(3459, 3464, 0), new WorldPoint(3519, 3514, 0), TALL_TREE_14843, BANK_BOOTH_24347),
    CAN_ROOFTOP_ONE(new WorldPoint(3504, 3491, 2), new WorldPoint(3512, 3499, 2), GAP_14844),
    CAN_ROOFTOP_TWO(new WorldPoint(3495, 3503, 2), new WorldPoint(3505, 3508, 2), GAP_14845),
    CAN_ROOFTOP_THREE(new WorldPoint(3484, 3498, 2), new WorldPoint(3494, 3506, 2), GAP_14848),
    CAN_ROOFTOP_FOUR(new WorldPoint(3474, 3491, 3), new WorldPoint(3481, 3501, 3), GAP_14846),
    CAN_ROOFTOP_FIVE(new WorldPoint(3477, 3481, 2), new WorldPoint(3485, 3488, 2), POLEVAULT),
    CAN_ROOFTOP_SIX(new WorldPoint(3488, 3468, 3), new WorldPoint(3505, 3480, 3), GAP_14847),
    CAN_ROOFTOP_SEVEN(new WorldPoint(3508, 3474, 2), new WorldPoint(3517, 3484, 2), GAP_14897),
    //APE ATOLL
    APE_STEPSTONE(new WorldPoint(2754, 2741, 0), new WorldPoint(2784, 2751, 0), STEPPING_STONE_15412),
    APE_TROPTREE1(new WorldPoint(2753, 2742, 0), new WorldPoint(2751, 2739, 0), TROPICAL_TREE_15414),
    APE_MONKEYBARS(new WorldPoint(2753, 2742, 2), new WorldPoint(2752, 2741, 2), MONKEYBARS_15417),
    APE_SKULLSLOPE(new WorldPoint(2747, 2741, 0), new WorldPoint(2746, 2741, 0), 1747),
    APE_ROPE(new WorldPoint(2735, 2726, 0), new WorldPoint(2754, 2742, 0), ROPE_15487),
    APE_TROPTREE2(new WorldPoint(2755, 2726, 0), new WorldPoint(2760, 2737, 0), TROPICAL_TREE_16062),
    //FALADOR
    FAL_GROUND(new WorldPoint(3008, 3328, 0), new WorldPoint(3071, 3391, 0), ROUGH_WALL_14898, ObjectID.BANK_BOOTH_24101),
    FAL_ROOFTOP_ONE(new WorldPoint(3034, 3342, 3), new WorldPoint(3040, 3347, 3), TIGHTROPE_14899),
    FAL_ROOFTOP_TWO(new WorldPoint(3043, 3341, 3), new WorldPoint(3051, 3350, 3), HAND_HOLDS_14901),
    FAL_ROOFTOP_THREE(new WorldPoint(3047, 3356, 3), new WorldPoint(3051, 3359, 3), GAP_14903),
    FAL_ROOFTOP_FOUR(new WorldPoint(3044, 3360, 3), new WorldPoint(3049, 3367, 3), GAP_14904),
    FAL_ROOFTOP_FIVE(new WorldPoint(3033, 3360, 3), new WorldPoint(3042, 3364, 3), TIGHTROPE_14905),
    FAL_ROOFTOP_SIX(new WorldPoint(3025, 3352, 3), new WorldPoint(3029, 3355, 3), TIGHTROPE_14911),
    FAL_ROOFTOP_SEVEN(new WorldPoint(3008, 3352, 3), new WorldPoint(3021, 3358, 3), GAP_14919),
    FAL_ROOFTOP_EIGHT(new WorldPoint(3015, 3343, 3), new WorldPoint(3022, 3350, 3), LEDGE_14920),
    FAL_ROOFTOP_NINE(new WorldPoint(3010, 3343, 3), new WorldPoint(3015, 3347, 3), LEDGE_14921),
    FAL_ROOFTOP_TEN(new WorldPoint(3008, 3335, 3), new WorldPoint(3014, 3343, 3), LEDGE_14922),
    FAL_ROOFTOP_ELEVEN(new WorldPoint(3013, 3331, 3), new WorldPoint(3018, 3335, 3), LEDGE_14924),
    FAL_ROOFTOP_TWELVE(new WorldPoint(3019, 3331, 3), new WorldPoint(3027, 3335, 3), EDGE_14925),
    //SEERS
    SEERS_GROUND(new WorldPoint(2689, 3457, 0), new WorldPoint(2750, 3517, 0), WALL_14927, BANK_BOOTH_25808),
    SEERS_ROOF_ONE(new WorldPoint(2720, 3489, 3), new WorldPoint(2731, 3498, 3), GAP_14928),
    SEERS_ROOF_TWO(new WorldPoint(2702, 3486, 2), new WorldPoint(2714, 3499, 2), TIGHTROPE_14932),
    SEERS_ROOF_THREE(new WorldPoint(2707, 3475, 2), new WorldPoint(2717, 3483, 2), GAP_14929),
    SEERS_ROOF_FOUR(new WorldPoint(2697, 3468, 3), new WorldPoint(2718, 3478, 3), GAP_14930),
    SEERS_ROOF_FIVE(new WorldPoint(2689, 3458, 2), new WorldPoint(2704, 3467, 2), EDGE_14931),
    //Pollniveach
    POLL_GROUND(new WorldPoint(3328, 2944, 0), new WorldPoint(3392, 3008, 0), BASKET_14935),
    POLL_ROOF_ONE(new WorldPoint(3346, 2963, 1), new WorldPoint(3352, 2969, 1), MARKET_STALL_14936),
    POLL_ROOF_TWO(new WorldPoint(3352, 2973, 1), new WorldPoint(3356, 2977, 1), BANNER_14937),
    POLL_ROOF_THREE(new WorldPoint(3360, 2977, 1), new WorldPoint(3363, 2980, 1), GAP_14938),
    POLL_ROOF_FOUR(new WorldPoint(3366, 2976, 1), new WorldPoint(3372, 2975, 1), TREE_14939),
    POLL_ROOF_FIVE(new WorldPoint(3365, 2982, 1), new WorldPoint(3370, 2987, 1), ROUGH_WALL_14940),
    POLL_ROOF_SIX(new WorldPoint(3355, 2980, 2), new WorldPoint(3366, 2986, 2), MONKEYBARS),
    POLL_ROOF_SEVEN(new WorldPoint(3357, 2991, 2), new WorldPoint(3367, 2996, 2), TREE_14944),
    POLL_ROOF_EIGHT(new WorldPoint(3356, 3000, 2), new WorldPoint(3363, 3005, 2), DRYING_LINE),
    //Prifddinas
    PRIF_LADDER(new WorldPoint(3237, 6099, 0), new WorldPoint(3275, 6114, 0), LADDER_36221, ObjectID.BANK_BOOTH_10355),
    PRIF_TIGHTROPE(new WorldPoint(3254, 6102, 2), new WorldPoint(3259, 6112, 2), TIGHTROPE_36225),//TIGHTROPE_36255
    PRIF_CHIMNEY(new WorldPoint(3271, 6104, 2), new WorldPoint(3276, 6107, 2), CHIMNEY_36227),
    PRIF_ROOFEDGE(new WorldPoint(3268, 6111, 2), new WorldPoint(3270, 6116, 2), ROOF_EDGE),
    PRIF_DARK_HOLE(new WorldPoint(3267, 6115, 0), new WorldPoint(3271, 6119, 0), DARK_HOLE_36229),
    PRIF_LADDER_TWO(new WorldPoint(2239, 3386, 0), new WorldPoint(2272, 3410, 0), LADDER_36231),
    PRIF_LADDER_FAIL(new WorldPoint(3265, 6138, 0), new WorldPoint(3276, 6150, 0), LADDER_36232),
    PRIF_ROPE_BRIDGE(new WorldPoint(2264, 3388, 2), new WorldPoint(2270, 3394, 2), ROPE_BRIDGE_36233),
    PRIF_TIGHTROPE_TWO(new WorldPoint(2252, 3386, 2), new WorldPoint(2259, 3391, 2), TIGHTROPE_36234),
    PRIF_ROPE_BRIDGE_TWO(new WorldPoint(2242, 3393, 2), new WorldPoint(2248, 3399, 2), ROPE_BRIDGE_36235),
    PRIF_TIGHTROPE_THREE(new WorldPoint(2243, 3404, 2), new WorldPoint(2249, 3411, 2), TIGHTROPE_36236),
    PRIF_TIGHTROPE_FOUR(new WorldPoint(2248, 3414, 2), new WorldPoint(2254, 3420, 2), TIGHTROPE_36237),
    PRIF_DARKHOLE_TWO(new WorldPoint(2255, 3424, 0), new WorldPoint(2263, 3436, 0), DARK_HOLE_36238),
    //Rellekka
    RELL_GROUND(new WorldPoint(2612, 3654, 0), new WorldPoint(2672, 3687, 0), ROUGH_WALL_14946),
    RELL_ROOF_ONE(new WorldPoint(2621, 3671, 3), new WorldPoint(2627, 3677, 3), GAP_14947),
    RELL_ROOF_TWO(new WorldPoint(2614, 3657, 3), new WorldPoint(2623, 3669, 3), TIGHTROPE_14987),
    RELL_ROOF_THREE(new WorldPoint(2625, 3649, 3), new WorldPoint(2631, 3656, 3), GAP_14990),
    RELL_ROOF_FOUR(new WorldPoint(2638, 3648, 3), new WorldPoint(2645, 3654, 3), GAP_14991),
    RELL_ROOF_FIVE(new WorldPoint(2642, 3656, 3), new WorldPoint(2651, 3663, 3), TIGHTROPE_14992),
    RELL_ROOF_SIX(new WorldPoint(2654, 3663, 3), new WorldPoint(2667, 3686, 3), PILE_OF_FISH),
    //Ardougne
    ARDY_GROUND(new WorldPoint(2640, 3274, 0), new WorldPoint(2678, 3321, 0), WOODEN_BEAMS, ObjectID.BANK_BOOTH_10355),
    ARDY_GAP(new WorldPoint(2670, 3298, 3), new WorldPoint(2675, 3312, 3), GAP_15609),
    ARDY_BEAM(new WorldPoint(2660, 3317, 3), new WorldPoint(2666, 3323, 3), PLANK_26635),
    ARDY_GAP_TWO(new WorldPoint(2652, 3317, 3), new WorldPoint(2658, 3322, 3), GAP_15610),
    ARDY_GAP_THREE(new WorldPoint(2647, 3310, 3), new WorldPoint(2654, 3315, 3), GAP_15611),
    ARDY_STEEP_ROOF(new WorldPoint(2650, 3299, 3), new WorldPoint(2656, 3310, 3), STEEP_ROOF),
    ARDY_GAP_FOUR(new WorldPoint(2653, 3290, 3), new WorldPoint(2658, 3298, 3), GAP_15612);

    @Getter(AccessLevel.PUBLIC)
    private final WorldArea location;

    @Getter(AccessLevel.PUBLIC)
    private final int obstacleId;

    @Getter(AccessLevel.PUBLIC)
    private int bankID = 0;

    Obstacles(final WorldPoint min, final WorldPoint max, final int obstacleId) {
        int width = max.getX() - min.getX() + 1;
        int height = max.getY() - min.getY() + 1;
        this.location = new WorldArea(min, width, height);
        this.obstacleId = obstacleId;
    }

    Obstacles(final WorldPoint min, final WorldPoint max, final int obstacleId, final int bankID) {
        int width = max.getX() - min.getX() + 1;
        int height = max.getY() - min.getY() + 1;
        this.location = new WorldArea(min, width, height);
        this.obstacleId = obstacleId;
        this.bankID = bankID;
    }

    public static Obstacles getObstacle(WorldPoint worldPoint) {
        for (Obstacles obstacle : values()) {
            if (obstacle.getLocation().distanceTo(worldPoint) == 0) {
                return obstacle;
            }
        }
        return null;
    }
}
