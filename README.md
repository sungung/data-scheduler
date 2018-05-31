# Install Spring boot
```
mkdir api && cd api
curl https://start.spring.io/starter.tgz -d dependencies=web,data-jpa,quartz,devtools -d name=api | tar -xzvf -


# Install application
mvn clean package

# Test

## Create a job

$ curl --header "Content-Type: application/json" -XPOST localhost:8080/schedule/groups/report/jobs --data '{"name":"rep1","description":"unit report","schedules":[{"id":null,"start":"2018-05-31T00:00:00.000+0000","end":"2019-06-31T00:00:00.000+0000","suspended":false,"cron-expression":"0/10 * * * * ?","last-fired":null,"next-fire":null}],"scheduled":true,"class-name":"com.sungung.scheduler.api.service.SimpleJob","is-scheduled":true,"job-group":{"name":"report","description":"job group for reporting jobs","node":null},"job-data":{}}' -i

HTTP/1.1 201
Location: http://localhost:8080/schedule/groups/report/jobs/rep1
Content-Length: 0
Date: Thu, 31 May 2018 06:25:32 GMT


## Get a job detail

$ curl http://localhost:8080/schedule/groups/report/jobs/rep1 -i                                                                  HTTP/1.1 200
Content-Type: application/json;charset=UTF-8
Transfer-Encoding: chunked
Date: Thu, 31 May 2018 06:26:00 GMT

{"name":"rep1","description":"unit report","schedules":[{"id":null,"start":"2018-05-31T00:00:00.000+0000","end":"2019-07-01T00:00:00.000+0000","suspended":false,"cron-expression":"0/10 * * * * ?","last-fired":null,"next-fire":"2018-05-31T00:00:00.000+0000"}],"scheduled":true,"class-name":"com.sungung.scheduler.api.service.SimpleJob","is-scheduled":true,"job-group":{"name":"report","description":"job group for reporting jobs","node":null},"job-data":{}}
