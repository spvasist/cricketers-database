package com.myvpn.spvasista.creative.DAOs;

import com.myvpn.spvasista.creative.Util.Utils;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by srikanth on 25-12-2016.
 */
public class PlayerUrlInfo implements Comparable<PlayerUrlInfo>{
    String playerUrl;
    LocalDateTime lastUpdated;
    int playerID;

    public String getPlayerUrl() {
        return playerUrl;
    }

    public void setPlayerUrl(String playerUrl) {
        this.playerUrl = playerUrl;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public PlayerUrlInfo(String _playerUrl) {
        this.playerUrl = _playerUrl;
        this.lastUpdated = LocalDateTime.now();

        playerID = Utils.getPlayerIdFromUrl(playerUrl);
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int compareTo(PlayerUrlInfo o) {
        if(o == null) return 1;
        if(playerID == o.playerID) return 0;
        if(playerID < o.playerID) return -1;
        return 1;
    }
}
