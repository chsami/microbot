/*
 * Copyright (c) 2019, dillydill123 <https://github.com/dillydill123>
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
package net.runelite.client.plugins.microbot.inventorysetups;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public enum InventorySetupsFilteringModeID
{
	// All items
	ALL(0),

	// Only Inventory
	INVENTORY(1),

	// Only Equipment
	EQUIPMENT(2),

	// Only Additional Filtered Items
	ADDITIONAL_FILTERED_ITEMS(3);

	private final int type;

	private static final List<InventorySetupsFilteringModeID> VALUES;

	static
	{
		VALUES = new ArrayList<>();
		Collections.addAll(VALUES, InventorySetupsFilteringModeID.values());
	}

	InventorySetupsFilteringModeID(int type)
	{
		this.type = type;
	}

	public int getType()
	{
		return type;
	}

	public static List<InventorySetupsFilteringModeID> getValues()
	{
		return VALUES;
	}
}
