package com.myvpn.spvasista.creative.DAOs;

import java.util.List;
import java.util.Set;

/**
 * Created by srikanth on 25-12-2016.
 */
public class PlayerUrlInfoList {
    public List<PlayerUrlInfo> getUrlInfoList() {
        return urlInfoList;
    }

    public void setUrlInfoList(List<PlayerUrlInfo> urlInfoList) {
        this.urlInfoList = urlInfoList;
    }

    List<PlayerUrlInfo> urlInfoList;
    volatile int currentIndex = 0;
    private static final int PAGE_SIZE = 100;

    public List<PlayerUrlInfo> getNextBatch() {
        synchronized (this) {
            if(currentIndex >= urlInfoList.size())
                return null;
            int from = currentIndex;
            int to = from + PAGE_SIZE - 1;
            if (to >= urlInfoList.size())
                to = urlInfoList.size() - 1;
            System.out.println(String.format("\n--------------------------- %d to %d ----------------------------\n", from, to));
            List<PlayerUrlInfo> subList = urlInfoList.subList(from, to);
            currentIndex = to + 1;
            return subList;
        }
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}
