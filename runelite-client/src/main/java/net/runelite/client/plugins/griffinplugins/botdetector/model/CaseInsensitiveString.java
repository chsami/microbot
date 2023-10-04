/*
 * Copyright (c) 2016 under CC BY 3.0, CrypticCabub <https://stackoverflow.com/q/39026195>
 * Copyright (c) 2021 under BSD 2, Ferrariic, Seltzer Bro, Cyborger1
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.griffinplugins.botdetector.model;

import lombok.Value;

/**
 * A string wrapper that makes .equals a caseInsensitive match
 * <p>
 *     a collection that wraps a String mapping in CaseInsensitiveStrings will still accept a String but will now
 *     return a caseInsensitive match rather than a caseSensitive one
 * </p>
 */
@Value
public class CaseInsensitiveString
{
	String str;

	public static CaseInsensitiveString wrap(String str)
	{
		return new CaseInsensitiveString(str);
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}

		if (o == null)
		{
			return false;
		}

		if (o.getClass() == getClass())
		{
			// Is another CaseInsensitiveString
			CaseInsensitiveString that = (CaseInsensitiveString) o;
			return (str != null) ? str.equalsIgnoreCase(that.str) : that.str == null;
		}

		if (o.getClass() == String.class)
		{
			// Is just a regular String
			String that = (String) o;
			return that.equalsIgnoreCase(str);
		}

		return false;
	}

	@Override
	public int hashCode()
	{
		return (str != null) ? str.toUpperCase().hashCode() : 0;
	}

	@Override
	public String toString()
	{
		return str;
	}
}
