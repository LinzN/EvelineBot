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


import de.linzn.evelineBot.core.MediaManager;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import static net.dv8tion.jda.internal.JDAImpl.LOG;

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
                    LOG.info("ResumePause Reaction");
                    mediaManager.resumePause();
                } else if (event.getReactionEmote().getAsCodepoints().equalsIgnoreCase("U+23F9")) {
                    mediaManager.stop();
                    LOG.info("Stop Reaction");
                } else if (event.getReactionEmote().getAsCodepoints().equalsIgnoreCase("U+23ED")) {
                    mediaManager.nextTrack();
                    LOG.info("Next Reaction");
                } else if (event.getReactionEmote().getAsCodepoints().equalsIgnoreCase("U+1F500")) {
                    mediaManager.shuffle();
                    LOG.info("Shuffle Reaction");
                } else if (event.getReactionEmote().getAsCodepoints().equalsIgnoreCase("U+2620")) {
                    mediaManager.reset();
                    LOG.info("Kill Reaction");
                }
            }
        }
    }
}
