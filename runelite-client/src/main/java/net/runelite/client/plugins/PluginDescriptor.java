/*
 * Copyright (c) 2017, Adam <Adam@sigterm.info>
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
package net.runelite.client.plugins;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface PluginDescriptor {
    public String Nate = "<html>[<font color=orange>N</font>] ";
    public String Mocrosoft = "<html>[<font color=#b8f704M>M</font>] ";
    public String OG = "<html>[<font color=#FF69B4>O</font>] ";
    public String Default = "<html>[<font color=green>D</font>] ";
    public String SaCo = "<html>[<font color=#0d937b>S</font>] ";
    public String Bank = "<html>[<font color=#9900ff>B</font>] ";
    public String Forn = "<html>[<font color=#AF2B1E>F</font>] ";

    public String Lucid = "<html>[<font color=#32CD32>L</font>] ";

    public String xKylee = "<html><font color=\"#32CD32\">K</font>";

    String name();

    /**
     * Internal name used in the config.
     */
    String configName() default "";

    /**
     * A short, one-line summary of the plugin.
     */
    String description() default "";

    /**
     * A list of plugin keywords, used (together with the name) when searching for plugins.
     * Each tag should not contain any spaces, and should be fully lowercase.
     */
    String[] tags() default {};

    /**
     * A list of plugin names that are mutually exclusive with this plugin. Any plugins
     * with a name or conflicts value that matches this will be disabled when this plugin
     * is started
     */
    String[] conflicts() default {};

    /**
     * If this plugin should be defaulted to on. Plugin-Hub plugins should always
     * have this set to true (the default), since having them off by defaults means
     * the user has to install the plugin, then separately enable it, which is confusing.
     */
    boolean enabledByDefault() default true;

    /**
     * always on
     */
    boolean alwaysOn() default false;

    /**
     * Whether or not plugin is hidden from configuration panel
     */
    boolean hidden() default false;

    boolean developerPlugin() default false;

    /**
     * If this plugin should be loaded when there is no {@link net.runelite.api.Client}
     */
    boolean loadWhenOutdated() default false;

    boolean loadInSafeMode() default true;
}
