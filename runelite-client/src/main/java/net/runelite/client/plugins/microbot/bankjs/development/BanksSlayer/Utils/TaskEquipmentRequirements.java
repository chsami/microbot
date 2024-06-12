package net.runelite.client.plugins.microbot.bankjs.development.BanksSlayer.Utils;

import java.util.List;

public class TaskEquipmentRequirements {
    private final List<Integer> mandatoryEquipment;
    private final List<Integer> optionalEquipment;

    public TaskEquipmentRequirements(List<Integer> mandatoryEquipment, List<Integer> optionalEquipment) {
        this.mandatoryEquipment = mandatoryEquipment;
        this.optionalEquipment = optionalEquipment;
    }

    public List<Integer> getMandatoryEquipment() {
        return mandatoryEquipment;
    }

    public List<Integer> getOptionalEquipment() {
        return optionalEquipment;
    }
}

