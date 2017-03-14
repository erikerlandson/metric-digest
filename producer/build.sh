cp ../target/scala-2.11/metric-digest-assembly-0.1.0.jar .
docker build -t manyangled/metric-digest-producer:latest .
docker push manyangled/metric-digest-producer:latest
