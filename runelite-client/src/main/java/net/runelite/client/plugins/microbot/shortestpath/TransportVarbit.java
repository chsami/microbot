package shortestpath;

import lombok.Getter;

@Getter
public class TransportVarbit {
    final int varbitId;

    final int value;

    public TransportVarbit(int varbitId, int value) {
        this.varbitId = varbitId;
        this.value = value;
    }
}
