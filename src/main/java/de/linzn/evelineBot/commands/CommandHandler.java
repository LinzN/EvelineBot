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

package de.linzn.evelineBot.commands;

import de.linzn.evelineBot.EvelineBot;
import de.linzn.evelineBot.commands.media.*;
import de.linzn.evelineBot.utils.MessageSender;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CommandHandler extends ListenerAdapter {

    private final Map<String, ICMD> commandList;
    private final EvelineBot evelineBot;

    public CommandHandler(EvelineBot evelineBot) {
        this.commandList = new HashMap<>();
        this.evelineBot = evelineBot;
        addCommands();
    }

    @Override
    public void onMessageReceived(final MessageReceivedEvent e) {
        if (e.getChannelType() == ChannelType.PRIVATE) {
            return;
        }
        if (e.getTextChannel() != evelineBot.getMediaManager().getTextChannel()) {
            return;
        }

        if (e.getAuthor().isBot()) {
            return;
        }

        if (e.getGuild().getSelfMember().hasPermission(e.getTextChannel(), Permission.MESSAGE_MANAGE)) {
            e.getMessage().delete().queue();
        }

        if (e.getMessage().getContentRaw().startsWith(EvelineBot.getInstance().getConfig().getString("prefix"))) {
            String noPrefix = e.getMessage().getContentRaw().substring(EvelineBot.getInstance().getConfig().getString("prefix").length());
            String[] inputArray = noPrefix.split(" ");
            if (inputArray.length >= 1) {
                String command = inputArray[0];
                String[] args = Arrays.copyOfRange(inputArray, 1, inputArray.length);

                if (this.commandList.containsKey(command.toLowerCase())) {
                    EvelineBot.LOGGER.INFO("Run command " + command);
                    this.commandList.get(command).process(args, new MessageSender(e));
                }
            }
        } else {
            this.commandList.get("play").process(e.getMessage().getContentRaw().split(" "), new MessageSender(e));
        }
    }

    public void registerCommand(String command, ICMD icmd) {
        this.commandList.put(command.toLowerCase(), icmd);
    }

    private void addCommands() {
        registerCommand("play", new PlayCommand());
        registerCommand("next", new NextCommand());
        registerCommand("shuffle", new ShuffleCommand());
        registerCommand("reset", new ResetCommand());
        registerCommand("stop", new StopCommand());
        registerCommand("volume", new VolumeCommand());
    }
}
