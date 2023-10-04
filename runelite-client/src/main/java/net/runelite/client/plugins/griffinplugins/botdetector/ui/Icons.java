/*
 * Copyright (c) 2021, Ferrariic, Seltzer Bro, Cyborger1
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
package net.runelite.client.plugins.griffinplugins.botdetector.ui;

import java.util.Objects;
import javax.swing.ImageIcon;
import net.runelite.client.util.ImageUtil;

public class Icons
{
	private static final Class<?> PLUGIN_CLASS = BotDetectorPanel.class;

	public static final ImageIcon GITHUB_ICON = new ImageIcon(ImageUtil.loadImageResource(PLUGIN_CLASS, "github.png"));
	public static final ImageIcon DISCORD_ICON = new ImageIcon(ImageUtil.loadImageResource(PLUGIN_CLASS, "discord.png"));
	public static final ImageIcon PATREON_ICON = new ImageIcon(ImageUtil.loadImageResource(PLUGIN_CLASS, "patreon.png"));
	public static final ImageIcon WEB_ICON = new ImageIcon(ImageUtil.loadImageResource(PLUGIN_CLASS, "web.png"));
	public static final ImageIcon TWITTER_ICON = new ImageIcon(ImageUtil.loadImageResource(PLUGIN_CLASS, "twitter.png"));
	public static final ImageIcon WARNING_ICON = new ImageIcon(ImageUtil.loadImageResource(PLUGIN_CLASS, "warning.png"));
	public static final ImageIcon ERROR_ICON = new ImageIcon(ImageUtil.loadImageResource(PLUGIN_CLASS, "error.png"));

	// Must not be ImageUtil.loadImageResource as it produces a static image
	public static final ImageIcon LOADING_SPINNER = new ImageIcon(Objects.requireNonNull(PLUGIN_CLASS.getResource("loading_spinner_darker.gif")));
}
