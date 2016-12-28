package com.myvpn.spvasista.creative.Util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.myvpn.spvasista.creative.DAOs.PlayerUrlInfo;
import com.myvpn.spvasista.creative.DAOs.PlayerUrlInfoList;
import com.myvpn.spvasista.creative.Handlers.PlayersInfoHandler;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by srikanth on 24-12-2016.
 * This is a utility class.
 */
public class Utils {
    static final Object lock = new Object();
    static MongoClient client = new MongoClient();

    public static List<Map<String, Object>> parseTableStats(Element _element) {

        Elements rows = _element.select("tr");
        //Get headers
        Elements headers = rows.get(0).select("th");
        List<String> headerList = new ArrayList<>();
        for (Element e : headers) {
            String s = e.attr("title");
            s = s.replace(' ', '_');
            headerList.add(s);
        }

        //Get values
        List<Map<String, Object>> list = new ArrayList<>();
        for (int i = 1; i < rows.size(); i++) {
            Map<String, Object> valueMap = new HashMap<>();
            int keyIndex = 0;
            int keyListSize = headerList.size();
            Element row = rows.get(i);
            Elements values = row.select("td");
            for (Element e : values) {
                String s = e.text().replace("<b>", "").replace("</b>", "");
                try {
                    float val = Float.parseFloat(s);
                    valueMap.put(headerList.get(keyIndex % keyListSize), val);
                } catch (Exception ex) {
                    valueMap.put(headerList.get(keyIndex % keyListSize), s);
                }

                keyIndex++;
            }
            list.add(valueMap);
        }
        return list;
    }

    public static List<PlayerUrlInfo> discoverAllPlayers() throws IOException, URISyntaxException {
        String rootUrlStrTemplate = "http://www.espncricinfo.com/ci/content/player/caps.html?country=%d;class=%d";
        List<String> urlList = new ArrayList<>();
        for (int i = 1; i <= 9; i++) {
            for (int j = 1; j <= 3; j++) {
                String url = String.format(rootUrlStrTemplate, i, j);
                Document doc = Jsoup.parse((new URL(url)), 240000);
                Elements elems = doc.getElementsByClass("ciPlayername");
                for (Element e : elems) {
                    String partUrl = e.select("a").get(0).attr("href");


                    URL imgUrl = (new URI("http", "www.espncricinfo.com", partUrl, "")).toURL();
                    String urlStr = imgUrl.toString();
                    urlStr = urlStr.substring(0, urlStr.indexOf(".html") + 5);
                    urlList.add(urlStr);

                }
            }
        }
        Set<String> setWithUniqueValues = new HashSet<>(urlList);
        List<PlayerUrlInfo> list = new ArrayList<>();
        for (String s : setWithUniqueValues) {
            list.add((new PlayerUrlInfo(s)));
        }
        return list;
    }

    public static boolean saveDataToMongoDB(Object data, String colName, String dbName) throws JsonProcessingException {
        boolean retVal = false;
        synchronized (lock) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                String json = mapper.writeValueAsString(data);


                MongoDatabase db = client.getDatabase(dbName);
                MongoCollection collection = db.getCollection(colName);

                collection.insertOne(org.bson.Document.parse(json));
                retVal = true;
            } catch (Exception e) {
                System.out.println(e.toString());
            }
        }
        return retVal;
    }

    public static int getPlayerIdFromUrl(String url) {
        int playerId = -1;
        Pattern p = Pattern.compile("\\d+");
        Matcher m = p.matcher(url);
        String id = "";
        while (m.find()) {
            id = m.group();
            break;
        }
        try {
            playerId = Integer.parseInt(id);
        } catch (Exception ex) {
        }
        return playerId;
    }

    public static PlayerUrlInfoList downloadSaveAndGetPlayerUrlInfoList() throws IOException, URISyntaxException {
        List<PlayerUrlInfo> list = Utils.discoverAllPlayers();
        Collections.sort(list, Collections.reverseOrder());
        PlayerUrlInfoList urlInfoList = new PlayerUrlInfoList();
        urlInfoList.setUrlInfoList(list);
        Utils.saveDataToMongoDB(urlInfoList, "player_urls", "srikanth");
        return urlInfoList;
    }

    public static void downloadAndSavePlayerInfoList(PlayerUrlInfoList urlInfoList) {
        Thread[] threads = new Thread[400];
        for (int i = 0; i < 400; i++) {
            threads[i] = new Thread(new PlayersInfoHandler(urlInfoList.getNextBatch()));
            threads[i].start();
        }
    }
}
