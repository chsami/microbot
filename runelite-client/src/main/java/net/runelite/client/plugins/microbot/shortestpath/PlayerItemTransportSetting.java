package shortestpath;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PlayerItemTransportSetting {
    None("None"),
    Inventory("Inventory"),
    InventoryNonConsumable("Inventory (perm)"),
    All("All"),
    AllNonConsumable("All (perm)");

    private final String type;

    @Override
    public String toString() {return type;}
}
