package com.example.netflixreposjava;

import jakarta.ws.rs.*;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.PriorityQueue;
import com.google.gson.*;

import static com.example.netflixreposjava.ResourceUtil.getBufferFromUrl;

@Path("/view/bottom")
public class ViewBottomResource {
    private static List<NetflixRepo> repo_cache;
    private static List<String> repo_str_cache;
    private static Timestamp timestamp;
    // update cache for every 300 seconds.
    private static final int update_period_in_ms = 300000;

    List<NetflixRepo> getRepos() {
        List<NetflixRepo> res = new ArrayList<>();

        Gson gson = new GsonBuilder().setLenient().create();
        for (String repo : repo_str_cache) {
            JsonObject[] convertedObjects = gson.fromJson(repo, JsonObject[].class);
            for (JsonObject jsonObject : convertedObjects) {
                NetflixRepo netflixRepo = new NetflixRepo(jsonObject.get("full_name").getAsString(), jsonObject.get("forks_count").getAsInt(),
                        jsonObject.get("stargazers_count").getAsInt(), jsonObject.get("open_issues_count").getAsInt(),
                        jsonObject.get("updated_at").getAsString());
                res.add(netflixRepo);
                if (netflixRepo.name.equals("Netflix/vizceral-react")
                || netflixRepo.name.equals("Netflix/titus-controllers-api") || netflixRepo.name.equals("Netflix/signal-wrapper")) {
                    System.out.println("netflix repo name " + netflixRepo.name + ", last update " + netflixRepo.last_update + ", open issues " + netflixRepo.open_issues);
                }
            }
        }

        return res;
    }

    @Path("/{N}/forks")
    @GET
    @Produces("application/json")
    public LinkedHashMap<String, Integer> getBottomNForks(@PathParam("N") int n) throws IOException, NoSuchAlgorithmException, KeyManagementException {

        Timestamp current_ts = new Timestamp(System.currentTimeMillis());
        if (repo_str_cache == null || timestamp == null || current_ts.getTime() - timestamp.getTime() > update_period_in_ms) {
            repo_str_cache = getBufferFromUrl("https://api.github.com/orgs/Netflix/repos");
            timestamp = current_ts;
            repo_cache = getRepos();
        }

        LinkedHashMap<String, Integer> res = new LinkedHashMap<>();
        // max heap
        PriorityQueue<NetflixRepo> priorityQueue = new PriorityQueue<>(n, new ForkComparator());
        for (NetflixRepo repo : repo_cache) {
            priorityQueue.add(repo);
            if (priorityQueue.size() > n) {
                priorityQueue.remove();
            }
        }
        while (!priorityQueue.isEmpty()) {
            NetflixRepo repo = priorityQueue.poll();
            res.put(repo.name, repo.forks);
        }
        return res;
    }

    @Path("/{N}/last_updated")
    @GET
    @Produces("application/json")
    public LinkedHashMap<String, String> getBottomNLastUpdated(@PathParam("N") int n) throws IOException, NoSuchAlgorithmException, KeyManagementException {

        Timestamp current_ts = new Timestamp(System.currentTimeMillis());
        if (repo_str_cache == null || timestamp == null || current_ts.getTime() - timestamp.getTime() > update_period_in_ms) {
            repo_str_cache = getBufferFromUrl("https://api.github.com/orgs/Netflix/repos");
            timestamp = current_ts;
            repo_cache = getRepos();
        }

        LinkedHashMap<String, String> res = new LinkedHashMap<>();
        // max heap
        PriorityQueue<NetflixRepo> priorityQueue = new PriorityQueue<>(n, new LastUpdateComparator());
        for (NetflixRepo repo : repo_cache) {
            priorityQueue.add(repo);
            if (priorityQueue.size() > n) {
                priorityQueue.remove();
            }
        }
        while (!priorityQueue.isEmpty()) {
            NetflixRepo repo = priorityQueue.poll();
            res.put(repo.name, repo.last_update);
        }
        return res;
    }

    @Path("/{N}/open_issues")
    @GET
    @Produces("application/json")
    public LinkedHashMap<String, Integer> getBottomNOpenIssues(@PathParam("N") int n) throws IOException, NoSuchAlgorithmException, KeyManagementException {

        Timestamp current_ts = new Timestamp(System.currentTimeMillis());
        if (repo_str_cache == null || timestamp == null || current_ts.getTime() - timestamp.getTime() > update_period_in_ms) {
            repo_str_cache = getBufferFromUrl("https://api.github.com/orgs/Netflix/repos");
            timestamp = current_ts;
            repo_cache = getRepos();
        }

        LinkedHashMap<String, Integer> res = new LinkedHashMap<>();
        // max heap
        PriorityQueue<NetflixRepo> priorityQueue = new PriorityQueue<>(n, new OpenIssueComparator());
        for (NetflixRepo repo : repo_cache) {
            priorityQueue.add(repo);
            if (priorityQueue.size() > n) {
                priorityQueue.remove();
            }
        }
        while (!priorityQueue.isEmpty()) {
            NetflixRepo repo = priorityQueue.poll();
            res.put(repo.name, repo.open_issues);
        }
        return res;
    }

    @Path("/{N}/stars")
    @GET
    @Produces("application/json")
    public LinkedHashMap<String, Integer> getBottomNStars(@PathParam("N") int n) throws IOException, NoSuchAlgorithmException, KeyManagementException {

        Timestamp current_ts = new Timestamp(System.currentTimeMillis());
        if (repo_str_cache == null || timestamp == null || current_ts.getTime() - timestamp.getTime() > update_period_in_ms) {
            repo_str_cache = getBufferFromUrl("https://api.github.com/orgs/Netflix/repos");
            timestamp = current_ts;
            repo_cache = getRepos();
        }

        LinkedHashMap<String, Integer> res = new LinkedHashMap<>();
        // max heap
        PriorityQueue<NetflixRepo> priorityQueue = new PriorityQueue<>(n, new StarComparator());
        for (NetflixRepo repo : repo_cache) {
            priorityQueue.add(repo);
            if (priorityQueue.size() > n) {
                priorityQueue.remove();
            }
        }
        while (!priorityQueue.isEmpty()) {
            NetflixRepo repo = priorityQueue.poll();
            res.put(repo.name, repo.stars);
        }
        return res;
    }
}
