package net.runelite.client.plugins.jrPlugins.AutoRifts.data;

import lombok.Getter;
import lombok.Setter;

public class Pouch{

    public Pouch(int id,int maxEssence){
        this.pouchID=id;
        this.currentEssence=0;
        this.essenceTotal = maxEssence;
    }

    @Getter
    @Setter
    int pouchID;

    @Getter
    @Setter
    int currentEssence;

    @Getter
    int essenceTotal;
}
