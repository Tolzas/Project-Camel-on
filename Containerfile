FROM docker.io/alpine:3.18

RUN apk update
RUN apk add openjdk17
Copy . .

RUN javac -target 1.7 -source 1.7 src/*.java -d bin/
WORKDIR bin/
CMD java Camelon
