# NetflixReposJava
I followed [this link](https://www.jetbrains.com/help/idea/creating-and-running-your-first-restful-web-service.html)
to build my first Java web service using Jakarta EE 10 and Glassfish 7 in IntelliJ Idea 2023. 
Note that only Ultimate (not Community Edition) can support web services. 

Once starting the Glassfish server, there should be a "Hello world" prompt showing up at this endpoint:
http://localhost:8080/NetflixReposJava-1.0-SNAPSHOT/api/hello-world

One can view all Netflix members by visiting this endpoint:
http://localhost:8080/NetflixReposJava-1.0-SNAPSHOT/api/orgs/Netflix/members

One can view the bottom N repository results (including all pages) based on forks/stars/open issues/last updates via the following endpoints:
http://localhost:8080/NetflixReposJava-1.0-SNAPSHOT/api/view/bottom/N/forks?n=5

http://localhost:8080/NetflixReposJava-1.0-SNAPSHOT/api/view/bottom/N/stars?n=5

http://localhost:8080/NetflixReposJava-1.0-SNAPSHOT/api/view/bottom/N/open_issues?n=5

http://localhost:8080/NetflixReposJava-1.0-SNAPSHOT/api/view/bottom/N/last_updated?n=5

Note that one can specify N as a query param in the endpoints.

TODO (will work on them incrementally)
1. The service should read a GITHUB_API_TOKEN environment variable to overcome API rate-limit restrictions.
2. The service should implement a /healthcheck endpoint that returns HTTP 200 when the service is ready to serve API responses.
3. The service should accept a port parameter on startup, so that we can customize how it runs.
4. Take a look at tests
5. Cache the following endpoints periodically
1) /orgs/Netflix/members
2) /orgs/Netflix/repos
