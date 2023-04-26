# NetflixReposJava
## How to build/run/test the service?
1. Clone this project
2. Run "mvn clean install"
3. Follow [this link](https://www.jetbrains.com/help/idea/creating-and-running-your-first-restful-web-service.html)
to build the web service using Jakarta EE 10 and Glassfish 7 in IntelliJ Idea 2023. Note that only Ultimate (not Community Edition) can support web services. 
4. Once starting the Glassfish server, there should be a "Hello world" prompt showing up at this endpoint:
http://localhost:8080/NetflixReposJava-1.0-SNAPSHOT/api/hello-world
5. Please use "test.sh" in the "test/" folder to run tests. I got all tests passed in my local environment.

## How to do health check?
One can visit the following endpoint (HealthCheckResource) for health check:
http://localhost:8080/NetflixReposJava-1.0-SNAPSHOT/api/health-check

## How to config port?
If using IntelliJ Idea, one can change the port number by clicking "Run->edit configuration" and modify the port in the URL field of the prompt. 

## What about rate limiting?
The service reads the GITHUB_API_TOKEN env variable (see ResourceUtil) and pass it in the HTTP header for authorization to overcome rate-limit restrictions. 

## What other endpoints does the service support?
The following endpoints are supported and cached periodically:

1. "/" (BaseResource)
http://localhost:8080/NetflixReposJava-1.0-SNAPSHOT/api/
2. "/orgs/Netflix/members" (MemberResource)
http://localhost:8080/NetflixReposJava-1.0-SNAPSHOT/api/orgs/Netflix/members
3. "/orgs/Netflix" (NetflixResource)
http://localhost:8080/NetflixReposJava-1.0-SNAPSHOT/api/orgs/Netflix
4. "/orgs/Netflix/repos" (RepoResource)
http://localhost:8080/NetflixReposJava-1.0-SNAPSHOT/api/orgs/Netflix/repos
5. "/users/{userName}/orgs" (UserOrgResource)
http://localhost:8080/NetflixReposJava-1.0-SNAPSHOT/api/users/{userName}/orgs
6. "/view/bottom/{N}" (ViewBottomResource)
http://localhost:8080/NetflixReposJava-1.0-SNAPSHOT/api/view/bottom/{N}/forks
http://localhost:8080/NetflixReposJava-1.0-SNAPSHOT/api/view/bottom/{N}/stars
http://localhost:8080/NetflixReposJava-1.0-SNAPSHOT/api/view/bottom/{N}/open_issues
http://localhost:8080/NetflixReposJava-1.0-SNAPSHOT/api/view/bottom/{N}/last_updated

For all endpoints, we prefer to use Chrome to load the page faster.

## What are design decisions/trade-offs being made here?
For "/view/bottom/{N}" endpoints, I cached all repos as a list of NetflixRepo objects, there are two ways moving forward (assume m = number of Netflix repos):
1. Don't do more in the caching part, whenever a "/view/bottom/{N}" request comes in, just compute the bottom N from the cache using priority queue.

Time complexity: O(mLogN)

Space complexity: O(m)

2. Have one more cache per sorting method, i.e. fork_cache to sort by forks. When we need to update cache, we also sort all the caches based on those comparators, then we just return the bottom N items from the cache.

Time complexity: O(4 * mLogm) when updating the cache; O(N) when not updating the cache (i.e. when requests come within the caching period)

Space complexity: O(5 * m)

The trade-off is basically between time and space. I tried both (the current implementation uses the first option) and did not see much difference in terms of latency since everything happens in memory (when there is no need to update the cache). I think it's because m is relatively small (O(300)). The diff could be more obvious when it comes to 10k or 100k repos. 
