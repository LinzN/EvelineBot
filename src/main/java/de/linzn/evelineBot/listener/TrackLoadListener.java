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

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import de.linzn.evelineBot.EvelineBot;
import de.linzn.evelineBot.core.MediaManager;
import net.dv8tion.jda.api.entities.Member;

public class TrackLoadListener implements AudioLoadResultHandler {

    private static final int PLAYLIST_LIMIT = 200;
    private final Member author;
    private final MediaManager mediaManager;

    public TrackLoadListener(Member author, MediaManager mediaManager) {
        this.author = author;
        this.mediaManager = mediaManager;
    }

    @Override
    public void trackLoaded(AudioTrack track) {
        mediaManager.getTrackManager().queue(track, author);
        mediaManager.updateInterface_QUEUE();
    }

    @Override
    public void playlistLoaded(AudioPlaylist playlist) {
        EvelineBot.LOGGER.INFO("Loading playlist with " + playlist.getTracks().size() + " entries");
        if (playlist.getSelectedTrack() != null) {
            trackLoaded(playlist.getSelectedTrack());
        } else if (playlist.isSearchResult()) {
            trackLoaded(playlist.getTracks().get(0));
        } else {
            for (int i = 0; i < Math.min(playlist.getTracks().size(), PLAYLIST_LIMIT); i++) {
                mediaManager.getTrackManager().queue(playlist.getTracks().get(i), author);
            }
            mediaManager.updateInterface_QUEUE();
        }
    }

    @Override
    public void noMatches() {
        EvelineBot.LOGGER.WARNING("No track found!");
    }

    @Override
    public void loadFailed(FriendlyException exception) {
        EvelineBot.LOGGER.ERROR("ERROR while loading track with exception " + exception.getLocalizedMessage());
    }
}
