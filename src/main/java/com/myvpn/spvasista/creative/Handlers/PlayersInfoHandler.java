package com.myvpn.spvasista.creative.Handlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mongodb.DBObject;
import com.myvpn.spvasista.creative.DAOs.Player;
import com.myvpn.spvasista.creative.DAOs.PlayerUrlInfo;
import com.myvpn.spvasista.creative.Util.Utils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by srikanth on 26-12-2016.
 */
public class PlayersInfoHandler implements Runnable {

    List<PlayerUrlInfo> infoList;

    public PlayersInfoHandler(List<PlayerUrlInfo> _infoList)
    {
        infoList = _infoList;
    }

    @Override
    public void run() {
        if(infoList == null || infoList.size() ==0)
            return;

        List<DBObject> documents = new ArrayList<>();
        for (PlayerUrlInfo urlInfo : infoList) {
            Player player = null;
            try {
                player = new Player(urlInfo.getPlayerUrl());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }

            try {
                Utils.saveDataToMongoDB(player, "player_stats", "srikanth");
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
    }
}
