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

import de.linzn.evelineBot.commands.CommandHandler;
import de.linzn.evelineBot.core.MediaManager;
import de.linzn.evelineBot.listener.ReactionListener;
import de.linzn.evelineBot.listener.SetupListener;
import de.linzn.evelineBot.utils.JavaUtils;
import de.linzn.simplyConfiguration.FileConfiguration;
import de.linzn.simplyLogger.LogSystem;
import de.linzn.simplyLogger.Logger;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.utils.Compression;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.util.concurrent.Executors;

public class EvelineBot {
    public static Logger LOGGER;
    public static LogSystem logSystem;

    private static EvelineBot evelineBot;
    private static String version;

    private final MediaManager mediaManager;
    private final CommandHandler commandHandler;
    private final Configuration configuration;
    private JDA jda;

    public EvelineBot() {
        version = JavaUtils.getVersion();
        evelineBot = this;
        this.configuration = new Configuration();
        this.commandHandler = new CommandHandler(this);
        this.mediaManager = new MediaManager(this);

        JDABuilder jdaBuilder = JDABuilder.createLight(getConfig().getString("token"));
        jdaBuilder.addEventListeners(commandHandler, new SetupListener(this.mediaManager), new ReactionListener(this.mediaManager));
        jdaBuilder.setBulkDeleteSplittingEnabled(false);
        jdaBuilder.setAutoReconnect(true);
        jdaBuilder.setCallbackPool(Executors.newSingleThreadExecutor());
        jdaBuilder.setGatewayPool(Executors.newScheduledThreadPool(4));
        jdaBuilder.setRateLimitPool(Executors.newScheduledThreadPool(4));
        jdaBuilder.setCompression(Compression.NONE);
        try {
            this.jda = jdaBuilder.build();
            this.jda.awaitReady();
            this.mediaManager.setupManager();
        } catch (LoginException | InterruptedException e) {
            EvelineBot.LOGGER.ERROR(e);
        }
    }

    public static void main(String[] args) {
        logSystem = new LogSystem("Eveline");
        logSystem.setFileLogger(new File("logs"));
        LOGGER = logSystem.getLogger();
        new EvelineBot();
    }


    public static String getVersion() {
        return version;
    }

    public static EvelineBot getInstance() {
        return evelineBot;
    }

    public MediaManager getMediaManager() {
        return this.mediaManager;
    }

    public JDA getJda() {
        return jda;
    }

    public CommandHandler getCommandHandler() {
        return commandHandler;
    }

    public FileConfiguration getConfig() {
        return this.configuration.getFileConfiguration();
    }
}