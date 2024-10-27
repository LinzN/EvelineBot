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

package de.linzn.evelineBot.core;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import de.linzn.evelineBot.EvelineBot;
import de.linzn.evelineBot.audio.AudioInfo;
import de.linzn.evelineBot.audio.AudioPlayerSendHandler;
import de.linzn.evelineBot.audio.TrackManager;
import de.linzn.evelineBot.listener.TrackLoadListener;
import de.linzn.evelineBot.utils.InterfaceBuilder;
import de.linzn.evelineBot.utils.MessageSender;
import de.linzn.openJL.pairs.Pair;
import dev.lavalink.youtube.YoutubeAudioSourceManager;
import dev.lavalink.youtube.clients.*;
import dev.lavalink.youtube.clients.skeleton.Client;
import dev.lavalink.youtube.http.YoutubeOauth2Handler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

public class MediaManager {

    private final EvelineBot evelineBot;
    private final AudioPlayerManager myManager = new DefaultAudioPlayerManager();

    private Guild guild;
    private VoiceChannel voiceChannel;
    private TextChannel textChannel;
    private Pair<AudioPlayer, TrackManager> player;

    private String top_id;
    private String queue_id;

    public MediaManager(EvelineBot evelineBot) {
        this.evelineBot = evelineBot;
        YoutubeAudioSourceManager youtube = new YoutubeAudioSourceManager(/*allowSearch:*/ true, new Music(), new Web(), new AndroidTestsuite(), new TvHtml5Embedded());
        youtube.useOauth2(EvelineBot.getInstance().getConfig().getString("youtubeToken"), true);
        myManager.registerSourceManager(youtube);
        AudioSourceManagers.registerRemoteSources(myManager);
    }

    public void setupManager() {
        this.guild = evelineBot.getJda().getGuildById(evelineBot.getConfig().getString("guildId"));
        voiceChannel = guild.getVoiceChannelsByName(evelineBot.getConfig().getString("voiceChannel"), true).get(0);
        textChannel = guild.getTextChannelsByName(evelineBot.getConfig().getString("commandChannel"), true).get(0);
        AudioPlayer nPlayer = myManager.createPlayer();
        nPlayer.setVolume(evelineBot.getConfig().getInt("defaultVolume"));
        TrackManager manager = new TrackManager(nPlayer, this);
        nPlayer.addListener(manager);
        guild.getAudioManager().setSendingHandler(new AudioPlayerSendHandler(nPlayer));
        player = new Pair<>(nPlayer, manager);
        this.clearHistory();
        if (this.top_id == null) {
            textChannel.sendMessage(new EmbedBuilder().setTitle("TOP_INFO").build()).queue();
        }

        if (this.queue_id == null) {
            textChannel.sendMessage("QUEUE_INFO").queue(null, null);
        }
        guild.getAudioManager().setSelfDeafened(true);
        guild.getAudioManager().openAudioConnection(voiceChannel);
    }

    public void resumePause() {
        if (!isIdle()) {
            getPlayer().setPaused(!getPlayer().isPaused());
        }
    }


    public void reset() {
        getPlayer().destroy();
        getTrackManager().purgeQueue();
        player = null;
        setupManager();
    }

    public void loadTrack(String identifier, Member author) {
        myManager.loadItemOrdered(guild, identifier, new TrackLoadListener(author, this));
    }

    public boolean isIdle() {
        return getPlayer().getPlayingTrack() == null;
    }

    public void nextTrack() {
        if (!isIdle()) {
            getPlayer().stopTrack();
        }
    }

    public void shuffle() {
        if (!isIdle()) {
            getTrackManager().shuffleQueue();
        }
    }

    public void stop() {
        this.getTrackManager().purgeQueue();
        if (!isIdle()) {
            this.getPlayer().stopTrack();
        }
    }


    private void clearHistory() {
        MessageHistory history = MessageHistory.getHistoryFromBeginning(textChannel).complete();
        List<Message> mess = history.getRetrievedHistory();
        for(Message m: mess){
            m.delete().queue();
        }
        top_id = null;
        queue_id = null;
    }

    public void runMediaSearch(String input, MessageSender messageSender) {
        String output;
        if (input.startsWith("http://") || input.startsWith("https://")) {
            output = input;
        } else {
            output = "ytsearch: " + input;
        }

        loadTrack(output, messageSender.getMember());
    }

    public void updateInterface_TOP() {
        EvelineBot.LOGGER.INFO("UPDATE_INTERFACE_TOP");
        EmbedBuilder messageEmbedTop = new EmbedBuilder();

        if (this.getPlayer().getPlayingTrack() == null) {
            messageEmbedTop.setTitle("EVELINE Music");
            messageEmbedTop.addField("Playing", "No Tracks playing at the moment!", true);
            messageEmbedTop.addField("Volume", this.getPlayer().getVolume() + " %", true);
            messageEmbedTop.addField("Version", EvelineBot.getVersion(), true);
            messageEmbedTop.setColor(Color.GREEN);
            messageEmbedTop.setImage("https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/f/f4a8b53f-5f6b-4a1f-a484-925691f1411a/dazzmko-41abbab2-b4a1-4826-8c76-7ac60472eccc.png?token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1cm46YXBwOjdlMGQxODg5ODIyNjQzNzNhNWYwZDQxNWVhMGQyNmUwIiwiaXNzIjoidXJuOmFwcDo3ZTBkMTg4OTgyMjY0MzczYTVmMGQ0MTVlYTBkMjZlMCIsIm9iaiI6W1t7InBhdGgiOiJcL2ZcL2Y0YThiNTNmLTVmNmItNGExZi1hNDg0LTkyNTY5MWYxNDExYVwvZGF6em1rby00MWFiYmFiMi1iNGExLTQ4MjYtOGM3Ni03YWM2MDQ3MmVjY2MucG5nIn1dXSwiYXVkIjpbInVybjpzZXJ2aWNlOmZpbGUuZG93bmxvYWQiXX0.v7cYmfd71X9QmPqAlPKu6EqB6961L9hFvPfdtLH93xs");
            messageEmbedTop.setFooter("Powered by MirraNET in STEM Environment", null);
            EvelineBot.getInstance().getJda().getPresence().setActivity(Activity.playing("Nichts :("));
        } else {
            messageEmbedTop.setTitle("EVELINE Music");
            messageEmbedTop.addField("Playing", this.getTrackManager().currentPlaying.getTrack().getInfo().title, true);
            messageEmbedTop.addField("Volume", this.getPlayer().getVolume() + " %", true);
            messageEmbedTop.addField("Version", EvelineBot.getVersion(), true);
            messageEmbedTop.setColor(Color.BLUE);
            messageEmbedTop.setImage(this.getTrackManager().currentPlaying.getImageURL());
            String nickname = this.getTrackManager().currentPlaying.getAuthor().getNickname();
            if (nickname == null) {
                nickname = this.getTrackManager().currentPlaying.getAuthor().getEffectiveName();
            }
            messageEmbedTop.setFooter("Request from: " + nickname, null);
            evelineBot.getJda().getPresence().setActivity(Activity.playing(this.getTrackManager().currentPlaying.getTrack().getInfo().title));

        }

        textChannel.editMessageById(this.top_id, messageEmbedTop.build()).queue(message -> {
            message.addReaction("U+23EF").queue();
            message.addReaction("U+23F9").queue();
            message.addReaction("U+23ED").queue();
            message.addReaction("U+1F500").queue();
            message.addReaction("U+2620").queue();
        });


    }

    public void updateInterface_QUEUE() {
        EvelineBot.LOGGER.INFO("UPDATE_INTERFACE_QUEUE");
        Message messageQueue;

        if (getTrackManager().getQueuedTracks().isEmpty()) {
            messageQueue = new MessageBuilder("Queue list: \n" + "The queue is empty!").build();
        } else {
            StringBuilder sb = new StringBuilder();
            Set<AudioInfo> queue = getTrackManager().getQueuedTracks();
            queue.forEach(audioInfo -> sb.append(InterfaceBuilder.buildQueueMessage(audioInfo)));


            if (sb.length() <= 1930) {
                messageQueue = new MessageBuilder("Queue list: \n" + "**>** " + sb.toString()).build();
            } else {
                messageQueue = new MessageBuilder("Queue list: \n" + "**>** " + sb.substring(0, 1930) + "...").build();
            }
        }
        textChannel.editMessageById(this.queue_id, messageQueue).queue();
    }

    public AudioPlayer getPlayer() {
        return player.getKey();
    }

    public TrackManager getTrackManager() {
        return player.getValue();
    }

    public Guild getGuild() {
        return guild;
    }

    public String getQueue_id() {
        return queue_id;
    }

    public void setQueue_id(String id) {
        this.queue_id = id;
    }

    public String getTop_id() {
        return top_id;
    }

    public void setTop_id(String id) {
        this.top_id = id;
    }

    public TextChannel getTextChannel() {
        return textChannel;
    }

    public VoiceChannel getVoiceChannel() {
        return voiceChannel;
    }
}
