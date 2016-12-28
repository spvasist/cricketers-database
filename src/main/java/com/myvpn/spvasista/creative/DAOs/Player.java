package com.myvpn.spvasista.creative.DAOs;

import com.myvpn.spvasista.creative.Util.Utils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by srikanth on 23-12-2016.
 */

public class Player {

    String playerUrl;
    int playerID;
    String commonName;
    String country;
    byte[] image;
    Map<String, String> properties = new HashMap<>();
    List<Map<String, Object>> battingAndFieldingStats;
    List<Map<String, Object>> bowlingStats;
    List<Map<String, Object>> battingListStats;
    List<Map<String, Object>> bowlingListStats;
    List<Map<String, Object>> fieldingListStats;
    String _battingListUrlTemplate = "?class=11;template=results;type=batting;view=innings;wrappertype=print";
    String _bowlingListUrlTemplate = "?class=11;template=results;type=bowling;view=innings;wrappertype=print";
    String _fieldingListUrlTemplate = "?class=11;template=results;type=fielding;view=innings;wrappertype=print";

    public String getPlayerUrl() {
        return playerUrl;
    }

    public void setPlayerUrl(String playerUrl) {
        this.playerUrl = playerUrl;
    }

    public String getCommonName() {
        return commonName;
    }

    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    public List<Map<String, Object>> getBattingAndFieldingStats() {
        return battingAndFieldingStats;
    }

    public void setBattingAndFieldingStats(List<Map<String, Object>> battingAndFieldingStats) {
        this.battingAndFieldingStats = battingAndFieldingStats;
    }

    public List<Map<String, Object>> getBowlingStats() {
        return bowlingStats;
    }

    public void setBowlingStats(List<Map<String, Object>> bowlingStats) {
        this.bowlingStats = bowlingStats;
    }

    public List<Map<String, Object>> getBattingListStats() {
        return battingListStats;
    }

    public void setBattingListStats(List<Map<String, Object>> battingListStats) {
        this.battingListStats = battingListStats;
    }

    public List<Map<String, Object>> getBowlingListStats() {
        return bowlingListStats;
    }

    public void setBowlingListStats(List<Map<String, Object>> bowlingListStats) {
        this.bowlingListStats = bowlingListStats;
    }

    public List<Map<String, Object>> getFieldingListStats() {
        return fieldingListStats;
    }

    public void setFieldingListStats(List<Map<String, Object>> fieldingListStats) {
        this.fieldingListStats = fieldingListStats;
    }

    public int getPlayerID() {
        return playerID;
    }

    public void setPlayerID(int playerID) {
        this.playerID = playerID;
    }

    public Player(String playerUrl) throws IOException, URISyntaxException {
        this.playerUrl = playerUrl;

        parsePlayerStats();
    }

    void parsePlayerStats() throws IOException, URISyntaxException {
        Document doc = Jsoup.parse(new URL(playerUrl),240000);

        playerID = Utils.getPlayerIdFromUrl(playerUrl);
        // Get common name and country

        Elements entireDetails = doc.getElementsByClass("pnl490M");
        Element nameAndCountry = entireDetails
                .get(0)
                .getElementsByClass("ciPlayernametxt")
                .get(0);

        commonName = nameAndCountry
                .select("h1")
                .get(0)
                .text();

        country = nameAndCountry
                .select("b")
                .get(0)
                .text();

        // get photo
        String imgElementSrc = "";
        try {


            imgElementSrc = entireDetails
                    .get(0)
                    .select("img")
                    .get(0)
                    .attr("src");
        } catch (Exception e) {
        }

        URL url = new URL(playerUrl);
        String host = url.getHost();
        URL imgUrl = (new URI("http", host, imgElementSrc, "")).toURL();
        String urlStr = imgUrl.toString();
        urlStr = urlStr.substring(0, urlStr.indexOf(".html") + 5);
        image = getProfileImage(urlStr);

        //get other details

        Elements infos = entireDetails.select("div")
                .get(3).select("p");

        for (Element e : infos) {

            String key = e.select("b").get(0).text();
            String valueString = "";
            Elements values = e.select("span");
            for (Element val : values) {

                valueString += val.text();
            }

            properties.put(key, valueString);
        }

        //Get batting and bowling stats
        Elements battingAndBowlingTable = entireDetails
                .get(0)
                .getElementsByClass("engineTable");

        //Get batting stats

        battingAndFieldingStats = Utils.parseTableStats(battingAndBowlingTable.get(0));

        //Get bowling stats

        bowlingStats = Utils.parseTableStats(battingAndBowlingTable.get(1));

        //Get all career records
        {
            String allUrl = playerUrl.replace("content", "engine");
            //Get all batting records
            Document careerBattingDoc = Jsoup.parse((new URL(allUrl + _battingListUrlTemplate)), 240000);
            Elements batEles = careerBattingDoc.select("table");
            battingListStats = Utils.parseTableStats(batEles.get(3));
            //Get all bowling records
            Document careerBowlingDoc = Jsoup.parse((new URL(allUrl + _bowlingListUrlTemplate)), 240000);
            Elements bowlEles = careerBowlingDoc.select("table");
            bowlingListStats = Utils.parseTableStats(bowlEles.get(3));
            //Get all fielding records
            Document careerFieldingDoc = Jsoup.parse((new URL(allUrl + _fieldingListUrlTemplate)), 240000);
            Elements fieldEles = careerFieldingDoc.select("table");
            fieldingListStats = Utils.parseTableStats(fieldEles.get(3));
        }
    }

    public byte[] getProfileImage(String link) {
        URL url;
        InputStream is = null;
        BufferedReader br;
        String line;


        try {
            url = new URL(link);
            BufferedImage img = ImageIO.read(url);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(img, "jpg", baos);
            return baos.toByteArray();

        } catch (MalformedURLException mue) {

        } catch (IOException ioe) {

        } finally {
            try {
                if (is != null) is.close();
            } catch (IOException ioe) {
                // nothing to see here
            }
        }
        return new byte[0];
    }
}