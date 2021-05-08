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
import de.linzn.evelineBot.utils.helper.YouTubeHelper;
import net.dv8tion.jda.api.entities.Member;

public class AudioInfo {

    private final AudioTrack track;
    private final Member author;
    private final String source;

    AudioInfo(AudioTrack track, Member author) {
        this.track = track;
        this.author = author;
        this.source = track.getSourceManager().getSourceName();
    }

    public AudioTrack getTrack() {
        return track;
    }

    public String getYoutubeID() {
        return new YouTubeHelper().extractVideoIdFromUrl(this.track.getInfo().uri);
    }

    public String getImageURL() {
        String imageURL;
        switch (source) {
            case "youtube":
                imageURL = "https://img.youtube.com/vi/" + getYoutubeID() + "/hqdefault.jpg";
                break;
            default:
                imageURL = "https://coub-anubis-a.akamaized.net/coub_storage/coub/simple/cw_timeline_pic/a55449e4464/f2e8c21cd0f3a62a3c3b7/1419345846_image.jpg";
                break;
        }
        return imageURL;
    }


    public Member getAuthor() {
        return author;
    }

}