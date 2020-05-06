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

package de.linzn.evelineBot.commands.media;


import de.linzn.evelineBot.EvelineBot;
import de.linzn.evelineBot.commands.ICMD;
import de.linzn.evelineBot.core.MediaManager;
import de.linzn.evelineBot.utils.MessageSender;


public class NextCommand implements ICMD {
    @Override
    public boolean process(String[] args, MessageSender messageSender) {
        MediaManager mediaManager = EvelineBot.getInstance().getMediaManager();
        mediaManager.nextTrack();
        return true;
    }
}
