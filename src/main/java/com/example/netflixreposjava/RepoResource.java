package com.example.netflixreposjava;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import static com.example.netflixreposjava.ResourceUtil.getBufferFromUrl;

@Path("/view/bottom/N")
public class RepoResource {
    @Path("/forks")
    @GET
    @Produces("application/json")
    public LinkedHashMap<String, Integer> getBottomNForks(@QueryParam("n")int n) throws IOException, NoSuchAlgorithmException, KeyManagementException {
        // http://localhost:8080/NetflixReposJava-1.0-SNAPSHOT/api/view/bottom/N/forks?n=5

        List<NetflixRepo> repoList = getRepos();

        LinkedHashMap<String, Integer> res = new LinkedHashMap<>();
        // max heap
        PriorityQueue<NetflixRepo> priorityQueue = new PriorityQueue<>(n, new ForkComparator());
        for (NetflixRepo repo : repoList) {
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

    @Path("/last_updated")
    @GET
    @Produces("application/json")
    public LinkedHashMap<String, String> getBottomNLastUpdates(@QueryParam("n")int n) throws IOException, NoSuchAlgorithmException, KeyManagementException {
        // http://localhost:8080/NetflixReposJava-1.0-SNAPSHOT/api/view/bottom/N/last_updated?n=5

        List<NetflixRepo> repoList = getRepos();

        LinkedHashMap<String, String> res = new LinkedHashMap<>();
        // max heap
        PriorityQueue<NetflixRepo> priorityQueue = new PriorityQueue<>(n, new LastUpdateComparator());
        for (NetflixRepo repo : repoList) {
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

    @Path("/open_issues")
    @GET
    @Produces("application/json")
    public LinkedHashMap<String, Integer> getBottomNOpenIssues(@QueryParam("n")int n) throws IOException, NoSuchAlgorithmException, KeyManagementException {
        // http://localhost:8080/NetflixReposJava-1.0-SNAPSHOT/api/view/bottom/N/open_issues?n=5

        List<NetflixRepo> repoList = getRepos();

        LinkedHashMap<String, Integer> res = new LinkedHashMap<>();
        // max heap
        PriorityQueue<NetflixRepo> priorityQueue = new PriorityQueue<>(n, new OpenIssueComparator());
        for (NetflixRepo repo : repoList) {
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

    @Path("/stars")
    @GET
    @Produces("application/json")
    public LinkedHashMap<String, Integer> getBottomNStars(@QueryParam("n")int n) throws IOException, NoSuchAlgorithmException, KeyManagementException {
        // http://localhost:8080/NetflixReposJava-1.0-SNAPSHOT/api/view/bottom/N/stars?n=5

        List<NetflixRepo> repoList = getRepos();

        LinkedHashMap<String, Integer> res = new LinkedHashMap<>();
        // max heap
        PriorityQueue<NetflixRepo> priorityQueue = new PriorityQueue<>(n, new StarComparator());
        for (NetflixRepo repo : repoList) {
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

    public List<NetflixRepo> getRepos() throws IOException, NoSuchAlgorithmException, KeyManagementException {
        String github_url = "https://github.com";
        String repo_endpoint  = "/orgs/Netflix/repositories";
        String repo_buffer = getBufferFromUrl(github_url + repo_endpoint);
        List<NetflixRepo> repoList = new ArrayList<>();
        String name_mark = "class=\"d-inline-block\"";
        String fork_mark = "aria-label=\"fork\"";
        String svg_mark = "</svg>";
        String end_mark = "</";
        String datetime_mark = "datetime=\"";
        String allowed_chars = "-._";
        int index = repo_buffer.indexOf(name_mark);
        while (index >= 0) {
            // get name
            repo_buffer = repo_buffer.substring(index + name_mark.length());
            int end = repo_buffer.indexOf(end_mark);
            String s = repo_buffer.substring(0, end);
            StringBuilder name = new StringBuilder();
            for (int i = 0; i < s.length(); i++) {
                char c = s.charAt(i);
                if (Character.isLetterOrDigit(c) || allowed_chars.indexOf(c) >= 0) {
                    name.append(c);
                }
            }
            System.out.println(name.toString());
            // get forks
            index = repo_buffer.indexOf(fork_mark);
            repo_buffer = repo_buffer.substring(index + fork_mark.length());
            int begin = repo_buffer.indexOf(svg_mark);
            repo_buffer = repo_buffer.substring(begin + svg_mark.length());
            end = repo_buffer.indexOf(end_mark);
            s = repo_buffer.substring(0, end);
            StringBuilder ss = new StringBuilder();
            for (int i = 0; i < s.length(); i++) {
                char c = s.charAt(i);
                if (Character.isLetterOrDigit(c)) {
                    ss.append(c);
                }
            }

            int forks = Integer.parseInt(ss.toString());
            System.out.println("forks: " + forks);
            // get stars
            begin = repo_buffer.indexOf(svg_mark);
            repo_buffer = repo_buffer.substring(begin + svg_mark.length());
            end = repo_buffer.indexOf(end_mark);
            s = repo_buffer.substring(0, end);
            ss = new StringBuilder();
            for (int i = 0; i < s.length(); i++) {
                char c = s.charAt(i);
                if (Character.isDigit(c)) {
                    ss.append(c);
                }
            }
            int stars = Integer.parseInt(ss.toString());
            System.out.println("stars: " + stars);
            // open issue
            begin = repo_buffer.indexOf(svg_mark);
            repo_buffer = repo_buffer.substring(begin + svg_mark.length());
            end = repo_buffer.indexOf(end_mark);
            s = repo_buffer.substring(0, end);
            ss = new StringBuilder();
            for (int i = 0; i < s.length(); i++) {
                char c = s.charAt(i);
                if (Character.isDigit(c)) {
                    ss.append(c);
                }
            }
            int open_issues = Integer.parseInt(ss.toString());
            System.out.println("open issues: " + open_issues);
            // last update
            begin = repo_buffer.indexOf(datetime_mark);
            repo_buffer = repo_buffer.substring(begin + datetime_mark.length());
            end = repo_buffer.indexOf("\"");
            String last_update = repo_buffer.substring(0, end);
            System.out.println("last update: " + last_update);
            repoList.add(new NetflixRepo(name.toString(), forks, stars, open_issues, last_update));
            index = repo_buffer.indexOf(name_mark);
        }
        return repoList;
    }


}