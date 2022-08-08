# OpenSearch Docker Compose

- https://opensearch.org/docs/latest/
- https://opensearch.org/downloads.html
- https://github.com/opensearch-project/OpenSearch
- https://github.com/opensearch-project/OpenSearch-Dashboards
- https://opensearch.org/docs/latest/clients/logstash/index/

## docker-compose.yml 확인

elasticsearch 를 대체하는 opensearch, kibana 를 대체하는 opensearch-dashboards 그리고, logstash-oss-with-opensearch-output-plugin 의 도커 파일들을 하나로 묶어준다.

- OpenSearch: Data store and search engine
- OpenSearch Dashboards: Search frontend and visualizations
- logstash-oss-with-opensearch-output-plugin: real-time event processing engine

## 구동 (OpenSearch, OpenSearch Dashboards, Logstash)

```
docker-compose up -d
```

## Logstash 확인

logstash 폴더의 config/logstash.yml 과 pipeline/logstash.conf 환경설정으로 구동된다.
logstash의 pipeline은 input, filter, output으로 구성된다.

- input: 마이크로서비스들로부터 log 이벤트들을 받는다
- filter: input으로 받은 이벤트들을 output으로 전송하기 전에 원하는 형태로 변형한다.
- output: OpenSearch 로 filtered 이벤트들을 전송한다.

logstash.conf 파일에 다음과 같이 input과 output pipeline을 정의한다.
tcp 5001 로 json 요청을 listen 하고 있다가, 이벤트를 수신하면 stdout으로 그대로 출력함과 동시에, opensearch로 "logstash-logs-%{+YYYY.MM.dd}" 를 인덱스 패턴으로 하여 전송한다.

```
# logstash.conf 파일

input {
  tcp {
    port => 5001
    codec => json
  }
}

output {

  stdout {}

  opensearch {
    hosts => ["https://opensearch:9200"]
    index => "logstash-logs-%{+YYYY.MM.dd}"
    user => "admin"
    password => "admin"
    ssl => true
    ssl_certificate_verification => false
  }
}
```

tcp 5001 포트가 listen 하고 있는지 확인한다.

```
# 연결상태 확인
nc -zv localhost 5001

# 포트 확인
lsof -i :5001
netstat -anv | grep 5001
```

5001 로 샘플로 작성된 json 파일을 전송해 본다.

```
nc localhost 5001 < send-message-sample.json
```

최종 30 라인을 실시간 로그를 확인할 수 있다.

```
docker logs --tail 30 -f logstash

[2022-02-01T18:29:15,127][WARN ][deprecation.logstash.codecs.jsonlines][main][00782acb5a7f7800ad3abe3f3cdb9bd203d3700b30ede21cdfbfeb1247906e9a] Relying on default value of `pipeline.ecs_compatibility`, which may change in a future major release of Logstash. To avoid unexpected changes when upgrading Logstash, please explicitly declare your desired ECS Compatibility mode.
{
      "@version" => "1",
          "name" => "John",
    "@timestamp" => 2022-02-01T09:29:15.137Z,
          "port" => 59338,
           "age" => 30,
           "car" => nil,
          "host" => "gateway"
}
```

## OpenSearch Dashboards 확인

브라우저에서 다음 URL로 이동하여 admin / admin 으로 접속한다.

```
http://localhost:5601/
```

- OpenSearch Dashboards > Discover 에서 확인할 수 있다.

## logstash.conf 변경 적용

logstash.conf를 변경한 후 적용하려면

```
docker stop logstash
docker start logstash

# 또는 restart
docker restart logstash
```

## logstash 컨테이너 터미널 접속

```
docker exec -it logstash /bin/bash 
```