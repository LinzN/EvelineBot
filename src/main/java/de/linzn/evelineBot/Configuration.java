/*
 * Copyright (C) 2020. Niklas Linz - All Rights Reserved
 * You may use, distribute and modify this code under the
 * terms of the LGPLv3 license, which unfortunately won't be
 * written for another century.
 *
 * You should have received a copy of the LGPLv3 license with
 * this file. If not, please write to: niklas.linz@enigmar.de
 *
 */

package de.linzn.evelineBot;

import de.linzn.simplyConfiguration.FileConfiguration;
import de.linzn.simplyConfiguration.provider.YamlConfiguration;

import java.io.File;

public class Configuration {
    private final FileConfiguration fileConfiguration;

    public Configuration() {
        fileConfiguration = YamlConfiguration.loadConfiguration(new File("settings.yml"));
        fileConfiguration.get("prefix", ".");
        fileConfiguration.get("token", "xxxxxxxxx:xxxxxxxxx");
        fileConfiguration.get("voiceChannel", "DefaultVoice");
        fileConfiguration.get("commandChannel", "Allgemein");
        fileConfiguration.get("guildId", "xxxxxxx");
        fileConfiguration.get("defaultVolume", 10);
        fileConfiguration.get("youtubeToken", "xxx");

        fileConfiguration.save();
    }

    public FileConfiguration getFileConfiguration() {
        return this.fileConfiguration;
    }
}
