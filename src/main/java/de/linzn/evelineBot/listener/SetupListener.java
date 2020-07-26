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
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import static net.dv8tion.jda.internal.JDAImpl.LOG;

public class SetupListener extends ListenerAdapter {

    private final MediaManager mediaManager;

    public SetupListener(MediaManager mediaManager) {
        this.mediaManager = mediaManager;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent e) {
        if (!e.getAuthor().isBot()) {
            return;
        }
        if (e.getMessage().getEmbeds().size() >= 1) {
            if (e.getMessage().getEmbeds().get(0).getTitle().startsWith("TOP_INFO")) {
                mediaManager.setTop_id(e.getMessageId());
                LOG.info("Setup TOP_INFO ID: " + e.getMessageId());
                mediaManager.updateInterface_TOP();
            }
        }
       /* if (e.getMessage().getContentRaw().startsWith("TOP_INFO")) {
            mediaManager.setTop_id(e.getMessageId());
            LOG.info("Setup TOP_INFO ID: " + e.getMessageId());
            mediaManager.updateInterface_TOP();
        }*/

        if (e.getMessage().getContentRaw().startsWith("QUEUE_INFO")) {
            mediaManager.setQueue_id(e.getMessageId());
            LOG.info("Setup QUEUE_INFO ID: " + e.getMessageId());
            mediaManager.updateInterface_QUEUE();
        }
    }
}
