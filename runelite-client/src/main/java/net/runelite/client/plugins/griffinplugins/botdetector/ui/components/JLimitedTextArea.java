/*
 * Copyright (c) 2016 under CC BY 3.0, Francisco J. Güemes Sevilla <https://stackoverflow.com/a/14849680>
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
package net.runelite.client.plugins.griffinplugins.botdetector.ui.components;

import javax.swing.JTextArea;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

/**
 * An extension of {@link JTextArea} that automatically implements
 * a default {@link Document} model that limits the number of characters
 * that can be entered to the given {@code limit} in {@link #JLimitedTextArea(int)}.
 */
public class JLimitedTextArea extends JTextArea
{
	private final int limit;

	/**
	 * Instanciates a {@link JTextArea} implementing a default {@link Document} model
	 * that limits the number of characters that can be entered.
	 * @param limit The maximum number of characters that can be entered in the underlying {@link JTextArea}.
	 */
	public JLimitedTextArea(int limit)
	{
		super();
		this.limit = limit;
	}

	@Override
	protected Document createDefaultModel()
	{
		return new LimitDocument();
	}

	private class LimitDocument extends PlainDocument
	{
		@Override
		public void insertString( int offset, String  str, AttributeSet attr ) throws BadLocationException
		{
			if (str == null) return;

			if ((getLength() + str.length()) <= limit)
			{
				super.insertString(offset, str, attr);
			}
		}
	}
}
