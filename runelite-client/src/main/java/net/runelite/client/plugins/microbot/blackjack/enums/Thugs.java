package net.runelite.client.plugins.microbot.blackjack.enums;

import lombok.AllArgsConstructor;
import net.runelite.api.coords.WorldPoint;

@AllArgsConstructor
public enum Thugs {
    MENAPHITE("Menaphite Thug",
            55,
            //TODO GOT IT! OpenAI can be such a blessing.
            new int[]{6242, 6243},
            //TODO OKAY! so first two values are the tile outside the door to run to, second two values are the tile to run to for getting to the ladder/stairs/etc.
            //TODO yup! it has that.
            new int[]{3346,2955,3352,2960},
            true,
            new WorldPoint(3344, 2955, 0),
            Area.MenaphiteHut,
            new WorldPoint(3345, 2955, 0)),
    BANDIT("Bandit",
            56,//TODO yup!
            new int[]{6261,6260},
            new int[]{3364,2998,3364,3002},
            false,
            new WorldPoint(3364, 3001, 0),
            Area.BeardedBanditHut,
            new WorldPoint(3364, 2999, 0)),//TODO exactly. like I did here.
    BEARDED_BANDIT("Bandit",
            41,
            //TODO OH! thank you, I put the wrong values here
            new int[]{6261,6260},
            //TODO yes these are duplicated, but only because the stairs are immediately next to the player. I should probably have the script check the player's proximity
            //TODO found the bug
            new int[]{3364,2998,3364,3002},
            false,
            new WorldPoint(3364, 3001, 0),
            Area.BeardedBanditHut,
            //TODO oh ffs. a value I forgot to change.
            new WorldPoint(3364, 2999, 0));
//TODO bearded bandit is already inside a building with a ladder, so it doesn't need to open the curtain to run away.
    //TODO I have code for that.
    public final String displayName;
    public final int thugLevel;
    //TODO thinking of my naming
    public final int[] escapeObjectTile;
    public final int[] escapeTiles;
    public boolean needsToLeaveHut;
    public final WorldPoint location;
    public final Area thugArea;
    public final WorldPoint door;
    @Override
    public String toString() {
        return super.toString();
    }

}
