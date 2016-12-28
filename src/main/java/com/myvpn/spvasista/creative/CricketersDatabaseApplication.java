package com.myvpn.spvasista.creative;

import com.myvpn.spvasista.creative.DAOs.PlayerUrlInfoList;
import com.myvpn.spvasista.creative.Util.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.net.URISyntaxException;


@SpringBootApplication
public class CricketersDatabaseApplication {

    public static void main(String[] args) throws IOException, URISyntaxException, InterruptedException {
        SpringApplication.run(CricketersDatabaseApplication.class, args);

        PlayerUrlInfoList urlInfoList = Utils.downloadSaveAndGetPlayerUrlInfoList();

        Utils.downloadAndSavePlayerInfoList(urlInfoList);
    }
}
