package com.example.netflixreposjava;

import java.util.*;

public class NetflixRepo {
    String name;
    int forks;
    int stars;
    int open_issues;
    String last_update;
    public NetflixRepo(String name, int forks, int stars, int open_issues, String last_update) {
        this.name = name;
        this.forks = forks;
        this.stars = stars;
        this.open_issues = open_issues;
        this.last_update = last_update;
    }
}

class ForkComparator implements Comparator<NetflixRepo> {

    @Override
    public int compare(NetflixRepo o1, NetflixRepo o2) {
        if (o1.forks != o2.forks) {
            return o2.forks - o1.forks;
        }
        return o1.name.compareTo(o2.name);
    }
}

class OpenIssueComparator implements Comparator<NetflixRepo> {

    @Override
    public int compare(NetflixRepo o1, NetflixRepo o2) {
        if (o1.open_issues != o2.open_issues) {
            return o2.open_issues - o1.open_issues;
        }
        return o1.name.compareTo(o2.name);
    }
}

class StarComparator implements Comparator<NetflixRepo> {

    @Override
    public int compare(NetflixRepo o1, NetflixRepo o2) {
        if (o1.stars != o2.stars) {
            return o2.stars - o1.stars;
        }
        return o1.name.compareTo(o2.name);
    }
}

class LastUpdateComparator implements Comparator<NetflixRepo> {

    @Override
    public int compare(NetflixRepo o1, NetflixRepo o2) {
        if (!o1.last_update.equals(o2.last_update)) {
            return o2.last_update.compareTo(o1.last_update);
        }
        return o1.name.compareTo(o2.name);
    }
}


