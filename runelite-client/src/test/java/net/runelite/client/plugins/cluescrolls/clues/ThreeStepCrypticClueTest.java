/*
 * Copyright (c) 2020, Jordan Atwood <jordan.atwood423@gmail.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.cluescrolls.clues;

import com.google.common.base.Joiner;
import net.runelite.api.Client;
import net.runelite.api.Varbits;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.cluescrolls.ClueScrollPlugin;
import net.runelite.client.plugins.microbot.cluescrolls.clues.CrypticClue;
import net.runelite.client.plugins.microbot.cluescrolls.clues.ThreeStepCrypticClue;
import net.runelite.client.util.Text;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ThreeStepCrypticClueTest
{
	@Mock
	private ClueScrollPlugin plugin;

	@Mock
	private Client client;

	@Test
	public void forTextEmptyString()
	{
		assertNull(ThreeStepCrypticClue.forText("", ""));
	}

	@Test
	public void nonNullLocations()
	{
		when(plugin.getClient()).thenReturn(client);
		when(client.getVarbitValue(Varbits.VIGGORA_LOCATION)).thenReturn(1);

		final String clueText = Joiner.on("<br><br>").join(CrypticClue.CLUES.stream().map(CrypticClue::getText).toArray());
		final ThreeStepCrypticClue clue = ThreeStepCrypticClue.forText(Text.sanitizeMultilineText(clueText).toLowerCase(), clueText);

		assertNotNull(clue);
		for (final WorldPoint location : clue.getLocations(plugin))
		{
			assertNotNull(location);
		}
	}
}
