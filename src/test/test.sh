#!/bin/bash

APP_PORT=${1:-8080}
HEALTHCHECK_PORT=${2:-$APP_PORT}
BASE_URL="http://localhost:$APP_PORT/NetflixReposJava-1.0-SNAPSHOT/api"
HEALTHCHECK_URL="http://localhost:$HEALTHCHECK_PORT/NetflixReposJava-1.0-SNAPSHOT/api/health-check"

for TOOL in bc curl jq wc awk sort uniq tr head tail; do
    if ! which $TOOL >/dev/null; then
        echo "ERROR: $TOOL is not available in the PATH"
        exit 1
    fi
done

PASS=0
FAIL=0
TOTAL=0

function describe() {
    echo -n "$1"
    let TOTAL=$TOTAL+1
}

function pass() {
    echo "pass"
    let PASS=$PASS+1
}

function fail() {
    RESPONSE=$1
    EXPECTED=$2
    echo "failed"
    echo "  expected=$EXPECTED"
    echo "  response=$RESPONSE"
    let FAIL=$FAIL+1
}

function report() {
    PCT=$(echo "scale=2; $PASS / $TOTAL * 100" |bc)
    echo "$PASS/$TOTAL ($PCT%) tests passed"
}

describe "test-01-01: healthcheck = "

ATTEMPTS=0
while true; do
    let ATTEMPTS=$ATTEMPTS+1
    RESPONSE=$(curl -s -o /dev/null -w '%{http_code}' "$HEALTHCHECK_URL")
    if [[ $RESPONSE == "200" ]]; then
        let TIME=$ATTEMPTS*15
        echo -n "($TIME seconds) "; pass
        break
    else
        if [[ $ATTEMPTS -gt 24 ]]; then
            let TIME=$ATTEMPTS*15
            echo -n "($TIME seconds) "; fail
            break
        fi
        sleep 15
    fi
done

describe "test-02-01: / key count = "

COUNT=$(curl -s "$BASE_URL" |jq -r 'keys |.[]' |wc -l |awk '{print $1}')

if [[ $COUNT -eq 33 ]]; then
    pass
else
    fail "$COUNT" "33"
fi

describe "test-02-02: / repository_search_url value = "

VALUE=$(curl -s "$BASE_URL" |jq -r '.repository_search_url')

if [[ "$VALUE" == "https://api.github.com/search/repositories?q={query}{&page,per_page,sort,order}" ]]; then
    pass
else
    fail "$VALUE" "https://api.github.com/search/repositories?q={query}{&page,per_page,sort,order}"
fi

describe "test-02-03: / organization_repositories_url value = "

VALUE=$(curl -s "$BASE_URL" |jq -r '.organization_repositories_url')

if [[ "$VALUE" == "https://api.github.com/orgs/{org}/repos{?type,page,per_page,sort}" ]]; then
    pass
else
    fail "$VALUE" "https://api.github.com/orgs/{org}/repos{?type,page,per_page,sort}"
fi

describe "test-03-01: /orgs/Netflix key count = "

COUNT=$(curl -s "$BASE_URL/orgs/Netflix" |jq -r 'keys |.[]' |wc -l |awk '{print $1}')

if [[ $COUNT -eq 29 ]]; then
    pass
else
    fail "$COUNT" "29"
fi

describe "test-03-02: /orgs/Netflix avatar_url = "

VALUE=$(curl -s "$BASE_URL/orgs/Netflix" |jq -r '.avatar_url')

if [[ "$VALUE" == "https://avatars.githubusercontent.com/u/913567?v=4" ]]; then
    pass
else
    fail "$VALUE" "https://avatars.githubusercontent.com/u/913567?v=4"
fi

describe "test-03-03: /orgs/Netflix location = "

VALUE=$(curl -s "$BASE_URL/orgs/Netflix" |jq -r '.location')

if [[ "$VALUE" == "Los Gatos, California" ]]; then
    pass
else
    fail "$VALUE" "Los Gatos, California"
fi

describe "test-04-01: /orgs/Netflix/members object count = "

COUNT=$(curl -s "$BASE_URL/orgs/Netflix/members" |jq -r '. |length')

if [[ $COUNT -eq 29 ]]; then
    pass
else
    fail "$COUNT" "29"
fi

describe "test-04-02: /orgs/Netflix/members login first alpha case-insensitive = "

VALUE=$(curl -s "$BASE_URL/orgs/Netflix/members" |jq -r '.[] |.login' |tr '[:upper:]' '[:lower:]' |sort |head -1)

if [[ "$VALUE" == "amirziai" ]]; then
    pass
else
    fail "$VALUE" "amirziai"
fi

describe "test-04-03: /orgs/Netflix/members login first alpha case-sensitive = "

VALUE=$(curl -s "$BASE_URL/orgs/Netflix/members" |jq -r '.[] |.login' |sort |head -1)

if [[ "$VALUE" == "DanielThomas" ]]; then
    pass
else
    fail "$VALUE" "DanielThomas"
fi

describe "test-04-04: /orgs/Netflix/members login last alpha case-insensitive = "

VALUE=$(curl -s "$BASE_URL/orgs/Netflix/members" |jq -r '.[] |.login' |tr '[:upper:]' '[:lower:]' |sort |tail -1)

if [[ "$VALUE" == "xuorig" ]]; then
    pass
else
    fail "$VALUE" "xuorig"
fi

describe "test-04-05: /orgs/Netflix/members id first = "

VALUE=$(curl -s "$BASE_URL/orgs/Netflix/members" |jq -r '.[] |.id' |sort -n |head -1)

if [[ "$VALUE" == "48100" ]]; then
    pass
else
    fail "$VALUE" "48100"
fi

describe "test-04-06: /orgs/Netflix/members id last = "

VALUE=$(curl -s "$BASE_URL/orgs/Netflix/members" |jq -r '.[] |.id' |sort -n |tail -1)

if [[ "$VALUE" == "8961464" ]]; then
    pass
else
    fail "$VALUE" "8961464"
fi

describe "test-04-07: /users/antonio-osorio/orgs proxy = "

VALUE=$(curl -s "$BASE_URL/users/antonio-osorio/orgs" |jq -r '.[] |.login' |tr '\n' ':')

if [[ "$VALUE" == "Netflix:" ]]; then
    pass
else
    fail "$VALUE" "Netflix:"
fi

describe "test-04-08: /users/wesleytodd/orgs proxy = "

VALUE=$(curl -s "$BASE_URL/users/wesleytodd/orgs" |jq -r '.[] |.login' |tr '\n' ':')

if [[ "$VALUE" == "Netflix:expressjs:restify:Node-Ops:jshttp:pillarjs:nodejs:MusicMapIo:migratejs:pkgjs:" ]]; then
    pass
else
    fail "$VALUE" "Netflix:expressjs:restify:Node-Ops:jshttp:pillarjs:nodejs:MusicMapIo:migratejs:pkgjs:"
fi

describe "test-05-01: /orgs/Netflix/repos object count = "

COUNT_ARR=$(curl -s "$BASE_URL/orgs/Netflix/repos" |jq -r '. |length')

COUNT=0

# shellcheck disable=SC2068
for i in ${COUNT_ARR[@]}; do
  # shellcheck disable=SC2219
  let COUNT+=$i
done

if [[ $COUNT -gt 177 ]] && [[ $COUNT -lt 227 ]]; then
    pass
else
    fail "$COUNT" "177..227"
fi

describe "test-05-02: /orgs/Netflix/repos full_name first alpha case-insensitive = "

VALUE=$(curl -s "$BASE_URL/orgs/Netflix/repos" |jq -r '.[] |.full_name' |tr '[:upper:]' '[:lower:]' |sort |head -1)

if [[ "$VALUE" == "netflix/.github" ]]; then
    pass
else
    fail "$VALUE" "netflix/.github"
fi

describe "test-05-03: /orgs/Netflix/members full_name first alpha case-sensitive = "

VALUE=$(curl -s "$BASE_URL/orgs/Netflix/members" |jq -r '.[] |.full_name' |sort |head -1)

if [[ "$VALUE" == "null" ]]; then
    pass
else
    fail "$VALUE" "null"
fi

describe "test-05-04: /orgs/Netflix/members login last alpha case-insensitive = "

VALUE=$(curl -s "$BASE_URL/orgs/Netflix/members" |jq -r '.[] |.full_name' |tr '[:upper:]' '[:lower:]' |sort |tail -1)

if [[ "$VALUE" == "null" ]]; then
    pass
else
    fail "$VALUE" "null"
fi

describe "test-05-05: /orgs/Netflix/repos id first = "

VALUE=$(curl -s "$BASE_URL/orgs/Netflix/repos" |jq -r '.[] |.id' |sort -n |head -1)

if [[ "$VALUE" == "2044029" ]]; then
    pass
else
    fail "$VALUE" "2044029"
fi

describe "test-05-06: /orgs/Netflix/repos id last = "

VALUE=$(curl -s "$BASE_URL/orgs/Netflix/repos" |jq -r '.[] |.id' |sort -n |tail -1)

if [[ "$VALUE" == "596850631" ]]; then
    pass
else
    fail "$VALUE" "596850631"
fi

describe "test-05-07: /orgs/Netflix/repos languages unique = "

VALUE=$(curl -s "$BASE_URL/orgs/Netflix/repos" |jq -r '.[] |.language' |sort -u |tr '\n' ':')

if [[ "$VALUE" == "C:C#:C++:Clojure:D:Dockerfile:Go:Groovy:HCL:HTML:Java:JavaScript:Jupyter Notebook:Kotlin:Makefile:Python:R:Ruby:Scala:Shell:TypeScript:null:" ]]; then
    pass
else
    fail "$VALUE" "C:C#:C++:Clojure:D:Dockerfile:Go:Groovy:HCL:HTML:Java:JavaScript:Jupyter Notebook:Kotlin:Makefile:Python:R:Ruby:Scala:Shell:TypeScript:null:"
fi

describe "test-06-01: /view/bottom/5/forks = "

VALUE=$(curl -s "$BASE_URL/view/bottom/5/forks" |tr -d '\n' |sed -e 's/ //g')

if [[ "$VALUE" == '{"Netflix/dgs-examples-kotlin-2.7":2,"Netflix/go-iomux":1,"Netflix/nflx-geofeed":1,"Netflix/conductor-docs":0,"Netflix/eclipse-mat":0}' ]]; then
    pass
else
    fail "$VALUE" '{"Netflix/dgs-examples-kotlin-2.7":2,"Netflix/go-iomux":1,"Netflix/nflx-geofeed":1,"Netflix/conductor-docs":0,"Netflix/eclipse-mat":0}'
fi

describe "test-06-02: /view/bottom/10/forks = "

VALUE=$(curl -s "$BASE_URL/view/bottom/10/forks" |tr -d '\n' |sed -e 's/ //g')

if [[ "$VALUE" == '{"Netflix/eclipse-jifa":3,"Netflix/metaflow-nflx-extensions":3,"Netflix/metrics-client-go":3,"Netflix/taskintrospection":3,"Netflix/dgs-examples-java.latest":2,"Netflix/dgs-examples-kotlin-2.7":2,"Netflix/go-iomux":1,"Netflix/nflx-geofeed":1,"Netflix/conductor-docs":0,"Netflix/eclipse-mat":0}' ]]; then
    pass
else
    fail "$VALUE" '{"Netflix/eclipse-jifa":3,"Netflix/metaflow-nflx-extensions":3,"Netflix/metrics-client-go":3,"Netflix/taskintrospection":3,"Netflix/dgs-examples-java.latest":2,"Netflix/dgs-examples-kotlin-2.7":2,"Netflix/go-iomux":1,"Netflix/nflx-geofeed":1,"Netflix/conductor-docs":0,"Netflix/eclipse-mat":0}'
fi

describe "test-06-03: /view/bottom/5/last_updated = "

VALUE=$(curl -s "$BASE_URL/view/bottom/5/last_updated" |tr -d '\n' |sed -e 's/ //g')

if [[ "$VALUE" == '{"Netflix/netflixoss-npm-build-infrastructure":"2022-03-29T16:24:54Z","Netflix/mantis-api":"2022-03-22T21:52:15Z","Netflix/titus-controllers-api":"2022-01-25T03:45:16Z","Netflix/eclipse-mat":"2022-01-19T19:58:04Z","Netflix/mantis-rxnetty":"2021-08-10T20:08:55Z"}' ]]; then
    pass
else
    fail "$VALUE" '{"Netflix/netflixoss-npm-build-infrastructure":"2022-03-29T16:24:54Z","Netflix/mantis-api":"2022-03-22T21:52:15Z","Netflix/titus-controllers-api":"2022-01-25T03:45:16Z","Netflix/eclipse-mat":"2022-01-19T19:58:04Z","Netflix/mantis-rxnetty":"2021-08-10T20:08:55Z"}'
fi

describe "test-06-04: /view/bottom/10/last_updated = "

VALUE=$(curl -s "$BASE_URL/view/bottom/10/last_updated" |tr -d '\n' |sed -e 's/ //g')

if [[ "$VALUE" == '{"Netflix/falcor-datasource-chainer":"2022-03-29T16:26:27Z","Netflix/falcor-json-graph":"2022-03-29T16:26:09Z","Netflix/mantis-examples":"2022-03-29T16:26:03Z","Netflix/mantis-connectors":"2022-03-29T16:26:01Z","Netflix/mantis-source-jobs":"2022-03-29T16:25:56Z","Netflix/netflixoss-npm-build-infrastructure":"2022-03-29T16:24:54Z","Netflix/mantis-api":"2022-03-22T21:52:15Z","Netflix/titus-controllers-api":"2022-01-25T03:45:16Z","Netflix/eclipse-mat":"2022-01-19T19:58:04Z","Netflix/mantis-rxnetty":"2021-08-10T20:08:55Z"}' ]]; then
    pass
else
    fail "$VALUE" '{"Netflix/falcor-datasource-chainer":"2022-03-29T16:26:27Z","Netflix/falcor-json-graph":"2022-03-29T16:26:09Z","Netflix/mantis-examples":"2022-03-29T16:26:03Z","Netflix/mantis-connectors":"2022-03-29T16:26:01Z","Netflix/mantis-source-jobs":"2022-03-29T16:25:56Z","Netflix/netflixoss-npm-build-infrastructure":"2022-03-29T16:24:54Z","Netflix/mantis-api":"2022-03-22T21:52:15Z","Netflix/titus-controllers-api":"2022-01-25T03:45:16Z","Netflix/eclipse-mat":"2022-01-19T19:58:04Z","Netflix/mantis-rxnetty":"2021-08-10T20:08:55Z"}'
fi

describe "test-06-05: /view/bottom/5/open_issues = "

VALUE=$(curl -s "$BASE_URL/view/bottom/5/open_issues" |tr -d '\n' |sed -e 's/ //g')

if [[ "$VALUE" == '{"Netflix/titus-executor":0,"Netflix/titus-kube-common":0,"Netflix/tslint-config-netflix":0,"Netflix/user2020-metaflow-tutorial":0,"Netflix/webpack-parse-query":0}' ]]; then
    pass
else
    fail "$VALUE" '{"Netflix/titus-executor":0,"Netflix/titus-kube-common":0,"Netflix/tslint-config-netflix":0,"Netflix/user2020-metaflow-tutorial":0,"Netflix/webpack-parse-query":0}'
fi

describe "test-06-06: /view/bottom/10/open_issues = "

VALUE=$(curl -s "$BASE_URL/view/bottom/10/open_issues" |tr -d '\n' |sed -e 's/ //g')

if [[ "$VALUE" == '{"Netflix/techreports":0,"Netflix/titus":0,"Netflix/titus-api-definitions":0,"Netflix/titus-control-plane":0,"Netflix/titus-controllers-api":0,"Netflix/titus-executor":0,"Netflix/titus-kube-common":0,"Netflix/tslint-config-netflix":0,"Netflix/user2020-metaflow-tutorial":0,"Netflix/webpack-parse-query":0}' ]]; then
    pass
else
    fail "$VALUE" '{"Netflix/techreports":0,"Netflix/titus":0,"Netflix/titus-api-definitions":0,"Netflix/titus-control-plane":0,"Netflix/titus-controllers-api":0,"Netflix/titus-executor":0,"Netflix/titus-kube-common":0,"Netflix/tslint-config-netflix":0,"Netflix/user2020-metaflow-tutorial":0,"Netflix/webpack-parse-query":0}'
fi

describe "test-06-07: /view/bottom/5/stars = "

VALUE=$(curl -s "$BASE_URL/view/bottom/5/stars" |tr -d '\n' |sed -e 's/ //g')

if [[ "$VALUE" == '{"Netflix/dgs-examples-java-2.7":1,"Netflix/dgs-examples-java.latest":1,"Netflix/dgs-examples-kotlin-2.7":1,"Netflix/eclipse-mat":1,"Netflix/conductor-docs":0}' ]]; then
    pass
else
    fail "$VALUE" '{"Netflix/dgs-examples-java-2.7":1,"Netflix/dgs-examples-java.latest":1,"Netflix/dgs-examples-kotlin-2.7":1,"Netflix/eclipse-mat":1,"Netflix/conductor-docs":0}'
fi

describe "test-06-08: /view/bottom/10/stars = "

VALUE=$(curl -s "$BASE_URL/view/bottom/10/stars" |tr -d '\n' |sed -e 's/ //g')

if [[ "$VALUE" == '{"Netflix/netflixoss-npm-build-infrastructure":4,"Netflix/eclipse-jifa":3,"Netflix/mantis-rxnetty":3,"Netflix/taskintrospection":2,"Netflix/titus-controllers-api":2,"Netflix/dgs-examples-java-2.7":1,"Netflix/dgs-examples-java.latest":1,"Netflix/dgs-examples-kotlin-2.7":1,"Netflix/eclipse-mat":1,"Netflix/conductor-docs":0}' ]]; then
    pass
else
    fail "$VALUE" '{"Netflix/netflixoss-npm-build-infrastructure":4,"Netflix/eclipse-jifa":3,"Netflix/mantis-rxnetty":3,"Netflix/taskintrospection":2,"Netflix/titus-controllers-api":2,"Netflix/dgs-examples-java-2.7":1,"Netflix/dgs-examples-java.latest":1,"Netflix/dgs-examples-kotlin-2.7":1,"Netflix/eclipse-mat":1,"Netflix/conductor-docs":0}'
fi

report
