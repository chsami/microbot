package net.runelite.client.plugins.microbot.util.prayer;
import net.runelite.api.Varbits;
public enum Prayer {
    THICK_SKIN("Thick Skin",35454985,1,Varbits.PRAYER_THICK_SKIN),
    BURST_STRENGTH("Burst of Strength",35454986,4,Varbits.PRAYER_BURST_OF_STRENGTH),
    CLARITY_THOUGHT("Clarity of Thought",35454987,7,Varbits.PRAYER_CLARITY_OF_THOUGHT),
    SHARP_EYE("Sharp Eye",35455003,8,Varbits.PRAYER_SHARP_EYE),
    MYSTIC_WILL("Mystic Will",35455006,9,Varbits.PRAYER_MYSTIC_WILL),
    ROCK_SKIN("Rock Skin",35454988,10,Varbits.PRAYER_ROCK_SKIN),
    PROTECT_MAGIC("Protect From Magic",35454997,37,Varbits.PRAYER_PROTECT_FROM_MAGIC),
    PROTECT_RANGE("Protect From Missiles",35454998,40,Varbits.PRAYER_PROTECT_FROM_MISSILES),
    PROTECT_MELEE("Protect From Melee",35454999,43,Varbits.PRAYER_PROTECT_FROM_MELEE),
    EAGLE_EYE("Eagle Eye",35455005,44,Varbits.PRAYER_EAGLE_EYE),
    CHIVALRY("Chivalry",35455010,60,Varbits.PRAYER_CHIVALRY),
    PIETY("Piety",35455011,70,Varbits.PRAYER_PIETY),
    RIGOUR("Rigour",35455009,74,Varbits.PRAYER_RIGOUR),
    AUGURY("Augury",35455012,77,Varbits.PRAYER_AUGURY);

    private String name;
    private int index;
    private int level;

    private int pVar;

    Prayer(String name, int index, int level, int pVar){
        this.name = name;
        this.index = index;
        this.level = level;
        this.pVar = pVar;
    }


    public String getName(){
        return name;
    }
    public int getIndex(){
        return index;
    }
    public int getLevel(){
        return level;
    }
    public int getVarbit(){
        return pVar;
    }

}
