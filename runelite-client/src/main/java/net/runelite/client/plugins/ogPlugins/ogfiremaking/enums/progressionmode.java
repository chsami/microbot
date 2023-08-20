package net.runelite.client.plugins.ogPlugins.ogfiremaking.enums;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum progressionmode {
    YES("yes",true),
    NO("no",false);

    private final String answer;
    private final boolean boolAnswer;

    public String getAnswer() {
        return answer;
    }

    public boolean isBoolAnswer() {
        return boolAnswer;
    }
}
