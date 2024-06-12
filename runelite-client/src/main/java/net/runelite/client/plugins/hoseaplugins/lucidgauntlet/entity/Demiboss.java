package net.runelite.client.plugins.hoseaplugins.lucidgauntlet.entity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.runelite.api.NPC;
import net.runelite.api.NpcID;

import java.awt.*;
import java.util.Set;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Demiboss
{
    @EqualsAndHashCode.Include
    private final NPC npc;

    private final Type type;

    public Demiboss(final NPC npc)
    {
        this.npc = npc;
        this.type = Type.fromId(npc.getId());
    }

    @AllArgsConstructor
    public enum Type
    {
        BEAR(Set.of(NpcID.CRYSTALLINE_BEAR, NpcID.CORRUPTED_BEAR), Color.RED),
        DARK_BEAST(Set.of(NpcID.CRYSTALLINE_DARK_BEAST, NpcID.CORRUPTED_DARK_BEAST), Color.GREEN),
        DRAGON(Set.of(NpcID.CRYSTALLINE_DRAGON, NpcID.CORRUPTED_DRAGON), Color.BLUE);

        private final Set<Integer> ids;

        @Getter
        private final Color outlineColor;

        static Type fromId(final int id)
        {
            for (final Type type : Type.values())
            {
                if (type.ids.contains(id))
                {
                    return type;
                }
            }

            return null;
        }
    }
}

