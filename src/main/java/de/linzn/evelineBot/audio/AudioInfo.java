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

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Member;

public class AudioInfo {

    private final AudioTrack track;
    private final Member author;

    AudioInfo(AudioTrack track, Member author) {
        this.track = track;
        this.author = author;
    }

    public AudioTrack getTrack() {
        return track;
    }


    public Member getAuthor() {
        return author;
    }

}