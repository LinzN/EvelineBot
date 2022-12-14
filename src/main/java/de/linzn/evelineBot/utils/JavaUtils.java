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

package de.linzn.evelineBot.utils;

import de.linzn.evelineBot.EvelineBot;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

public class JavaUtils {
    public static String getVersion() {
        String version;
        String res = "META-INF/maven/de.linzn/evelineBot/pom.properties";
        URL url = Thread.currentThread().getContextClassLoader().getResource(res);
        if (url == null) {
            version = "N.A";
        } else {
            Properties props = new Properties();
            try {
                props.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(res));
            } catch (IOException e) {
                EvelineBot.LOGGER.ERROR(e);
            }
            version = props.getProperty("version");
        }

        return version;
    }
}
