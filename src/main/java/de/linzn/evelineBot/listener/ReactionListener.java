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

package de.linzn.evelineBot.listener;


import de.linzn.evelineBot.EvelineBot;
import de.linzn.evelineBot.core.MediaManager;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ReactionListener extends ListenerAdapter {

    private final MediaManager mediaManager;

    public ReactionListener(MediaManager mediaManager) {
        this.mediaManager = mediaManager;
    }

    @Override
    public void onGuildMessageReactionAdd(final GuildMessageReactionAddEvent event) {
        if (event.getReaction().isSelf()) {
            return;
        }
        event.getReaction().removeReaction(event.getUser()).queue();

        if (event.getChannel().equals(mediaManager.getTextChannel())) {
            if (event.getMessageId().equalsIgnoreCase(mediaManager.getTop_id())) {
                if (event.getReactionEmote().getAsCodepoints().equalsIgnoreCase("U+23EF")) {
                    EvelineBot.LOGGER.INFO("ResumePause Reaction");
                    mediaManager.resumePause();
                } else if (event.getReactionEmote().getAsCodepoints().equalsIgnoreCase("U+23F9")) {
                    mediaManager.stop();
                    EvelineBot.LOGGER.INFO("Stop Reaction");
                } else if (event.getReactionEmote().getAsCodepoints().equalsIgnoreCase("U+23ED")) {
                    mediaManager.nextTrack();
                    EvelineBot.LOGGER.INFO("Next Reaction");
                } else if (event.getReactionEmote().getAsCodepoints().equalsIgnoreCase("U+1F500")) {
                    mediaManager.shuffle();
                    EvelineBot.LOGGER.INFO("Shuffle Reaction");
                } else if (event.getReactionEmote().getAsCodepoints().equalsIgnoreCase("U+2620")) {
                    mediaManager.reset();
                    EvelineBot.LOGGER.INFO("Kill Reaction");
                }
            }
        }
    }
}
