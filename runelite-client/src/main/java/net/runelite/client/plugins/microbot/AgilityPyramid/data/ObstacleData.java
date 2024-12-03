package net.runelite.client.plugins.microbot.AgilityPyramid.data;

import net.runelite.client.plugins.microbot.AgilityPyramid.data.Obstacle;

import java.util.ArrayList;
import java.util.List;

public class ObstacleData {
    // Create the lists as static for global access or instance-level for controlled access
    public static final List<Obstacle> obstaclesL1 = new ArrayList<>();
    public static final List<Obstacle> obstaclesL2 = new ArrayList<>();
    public static final List<Obstacle> obstaclesL3 = new ArrayList<>();
    public static final List<Obstacle> obstaclesL4 = new ArrayList<>();
    public static final List<Obstacle> obstaclesL5 = new ArrayList<>();

    static {

        //add obstacles to layer-1 obstacles
        obstaclesL1.add(new Obstacle("Stairs", "Climb-down", 3354,2833,1,1, 10857));
        obstaclesL1.add(new Obstacle("Low wall", "Climb-over", 3354, 2833, 2, 16, 10865));
        obstaclesL1.add(new Obstacle("Ledge", "Cross", 3354, 2850, 11, 3, 10860));
        obstaclesL1.add(new Obstacle("Plank", "Cross", 3368, 2845, 8, 8, 10867));
        obstaclesL1.add(new Obstacle("Gap", "Cross", 3371, 2831, 5, 10, 10882));
        obstaclesL1.add(new Obstacle("Ledge", "Cross", 3363, 2831, 5, 2, 10886));
        obstaclesL1.add(new Obstacle("Stairs", "Climb-up", 3356, 2831, 4, 3, 10857));


        //add obstacles to layer2-obstacles
        obstaclesL2.add(new Obstacle("Stairs", "Climb-down", 3356,2835,1,1, 10857));
        obstaclesL2.add(new Obstacle("Gap", "Cross", 3356, 2835, 2, 3, 10884));
        obstaclesL2.add(new Obstacle("Gap", "Jump", 3356, 2841, 2, 7, 10859));
        obstaclesL2.add(new Obstacle("Gap", "Cross", 3356, 2849, 5, 2, 10861));
        obstaclesL2.add(new Obstacle("Ledge", "Cross", 3364, 2841, 10, 11, 10860));
        obstaclesL2.add(new Obstacle("Low wall", "Climb-over", 3370, 2833, 4, 4, 10865));
        obstaclesL2.add(new Obstacle("Gap", "Jump", 3365, 2833, 5, 2, 10859));
        obstaclesL2.add(new Obstacle("Stairs", "Climb-up", 3358, 2833, 6, 3, 10857));


        //add objects to layer3-obstacles
        obstaclesL3.add(new Obstacle("Stairs", "Climb-down", 3358,2837,1,1, 10857));
        obstaclesL3.add(new Obstacle("Low wall", "Climb-over", 3358, 2837, 2, 2,10865 ));
        obstaclesL3.add(new Obstacle("Ledge", "Cross", 3358, 2840, 2, 3, 10888));
        obstaclesL3.add(new Obstacle("Gap", "Jump", 3358, 2843, 14, 6, 10859));
        obstaclesL3.add(new Obstacle("Plank", "Cross", 3370, 2835, 2, 6, 10867));
        obstaclesL3.add(new Obstacle("Stairs", "Climb-up", 3360, 2835, 6, 2, 10857));

        //add layer-
        obstaclesL4.add(new Obstacle("Stairs", "Climb-down", 3040,4695,1,1, 10857));
        obstaclesL4.add(new Obstacle("Gap", "Jump", 3040, 4695, 2, 3, 10859));
        obstaclesL4.add(new Obstacle("Low wall", "Climb-over", 3040, 4699, 3, 4, 10865));
        obstaclesL4.add(new Obstacle("Gap", "Jump", 3043, 4696, 7, 7, 10859));
        obstaclesL4.add(new Obstacle("Low wall", "Climb-over", 3047, 4693, 3, 2, 10865));
        obstaclesL4.add(new Obstacle("Stairs", "Climb-up", 3042, 4693, 5,3, 10857));


        //layer 5
        //!!!!!! getting the golden pyramid does not move you anywhere, handle accordingly
        obstaclesL5.add(new Obstacle("Stairs", "Climb-down", 3042,4697,1,1, 10857));
        obstaclesL5.add(new Obstacle("Climbing rocks", "Climb", 3042, 4697, 5, 4, 10851));
        obstaclesL5.add(new Obstacle("Gap", "Jump", 3046, 4698, 2, 3, 10859));
        obstaclesL5.add(new Obstacle("Doorway", "Enter", 3044, 4695, 4, 2, 10855));











        /*
        //add obstacles to layer-1 obstacles
        obstaclesL1.add(new Obstacle("Low wall", "Climb-over", 3354, 2848, 3355, 2833));
        obstaclesL1.add(new Obstacle("Ledge", "Cross", 3363, 2852, 3354, 2850));
        obstaclesL1.add(new Obstacle("Plank", "Cross", 3368, 2852, 3375, 2845));
        obstaclesL1.add(new Obstacle("Gap", "Cross", 3375, 2840, 3372, 2831));
        obstaclesL1.add(new Obstacle("Ledge", "Cross", 3367, 2832, 3364, 2831));
        obstaclesL1.add(new Obstacle("Stairs", "Climb-up", 3359, 2832, 3356, 2831));


        //add obstacles to layer2-obstacles
        obstaclesL2.add(new Obstacle("Gap", "Cross", 3356, 2836, 3356, 2835));
        obstaclesL2.add(new Obstacle("Gap", "Jump", 3356, 2846, 3357, 2841));
        obstaclesL2.add(new Obstacle("Gap", "Cross", 3356, 2849, 3359, 2850));
        obstaclesL2.add(new Obstacle("Ledge", "Cross", 3364, 2850, 3373, 2841));
        obstaclesL2.add(new Obstacle("Low wall", "Climb-over", 3373, 2836, 3371, 2833));
        obstaclesL2.add(new Obstacle("Gap", "Jump", 3369, 2833, 3366, 2834));
        obstaclesL2.add(new Obstacle("Stairs", "Climb-up", 3363, 2834, 3358, 2833));

        //add objects to layer3-obstacles
        obstaclesL3.add(new Obstacle("Low wall", "Climb-over", 3358, 2837, 3359, 2838));
        obstaclesL3.add(new Obstacle("Ledge", "Cross", 3359, 2840, 3358, 2842));
        obstaclesL3.add(new Obstacle("Gap", "Jump", 3358, 2848, 3371, 2843));
        obstaclesL3.add(new Obstacle("Plank", "Cross", 3371, 2840, 3370, 2835));
        obstaclesL3.add(new Obstacle("Stairs", "Climb-up", 3365, 2835, 3360, 2836));

        //add layer-4
        obstaclesL4.add(new Obstacle("Gap", "Jump", 3040, 4695, 3041, 4696));
        obstaclesL4.add(new Obstacle("Low wall", "Climb-over", 3040, 4699, 3041, 4702));
        obstaclesL4.add(new Obstacle("Gap", "Jump", 3043, 4702, 3049, 4697));
        obstaclesL4.add(new Obstacle("Low wall", "Climb-over", 3049, 4694, 3048, 4693));
        obstaclesL4.add(new Obstacle("Stairs", "Climb-up", 3046, 4694, 3042, 4693));


        //layer 5
        //!!!!!! getting the golden pyramid does not move you anywhere, handle accordingly
        obstaclesL5.add(new Obstacle("Gap", "Jump", 3042, 4697, 3047, 4700));
        obstaclesL5.add(new Obstacle("Doorway", "Enter", 3047, 4696, 3044, 4695));

         */
    }
}