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

package de.linzn.evelineBot.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import de.linzn.evelineBot.core.MediaManager;
import net.dv8tion.jda.api.entities.Member;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

import static net.dv8tion.jda.internal.JDAImpl.LOG;

public class TrackManager extends AudioEventAdapter {

    private final AudioPlayer audioPlayer;
    private final Queue<AudioInfo> queue;
    private final MediaManager mediaManager;
    public AudioInfo currentPlaying;

    public TrackManager(AudioPlayer audioPlayer, MediaManager mediaManager) {
        this.mediaManager = mediaManager;
        this.audioPlayer = audioPlayer;
        this.queue = new LinkedBlockingQueue<>();
    }

    public void queue(AudioTrack track, Member author) {
        LOG.info("Add " + track.getInfo().title + " to queue. Requested by " + author.getEffectiveName());
        AudioInfo info = new AudioInfo(track, author);
        this.queue.add(info);

        if (audioPlayer.getPlayingTrack() == null) {
            this.audioPlayer.playTrack(track);
        }
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        LOG.info("Start track " + track.getInfo().title);
        this.currentPlaying = this.queue.remove();
        this.mediaManager.updateInterface_TOP();
        this.mediaManager.updateInterface_QUEUE();
    }

    @Override
    public void onPlayerPause(AudioPlayer player) {
        LOG.info("Pause track " + player.getPlayingTrack().getInfo().title);
        this.mediaManager.updateInterface_TOP();
        super.onPlayerPause(player);
    }

    @Override
    public void onPlayerResume(AudioPlayer player) {
        LOG.info("Resume track " + player.getPlayingTrack().getInfo().title);
        this.mediaManager.updateInterface_TOP();
        super.onPlayerResume(player);
    }

    @Override
    public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
        LOG.error("ERROR while playing track " + track.getInfo().title + " with error " + exception.getLocalizedMessage());
        this.mediaManager.updateInterface_TOP();
        super.onTrackException(player, track, exception);
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        currentPlaying = null;
        if (!queue.isEmpty()) {
            LOG.info("Track end with " + endReason + " and starting new one!");
            player.playTrack(this.queue.element().getTrack());
        } else {
            LOG.info("Track end with " + endReason + "!");
            this.mediaManager.updateInterface_TOP();
            this.mediaManager.updateInterface_QUEUE();
        }


    }

    public void shuffleQueue() {
        List<AudioInfo> tQueue = new ArrayList<>(getQueuedTracks());
        Collections.shuffle(tQueue);
        purgeQueue();
        queue.addAll(tQueue);
    }

    public Set<AudioInfo> getQueuedTracks() {
        return new LinkedHashSet<>(queue);
    }

    public void purgeQueue() {
        queue.clear();
    }

}