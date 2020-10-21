FROM alpine

WORKDIR /root/switcher_rmi_docker

RUN apk add openjdk8
ENV JAVA_HOME /usr/lib/jvm/java-1.8-openjdk
ENV PATH $PATH:$JAVA_HOME/bin

COPY Client/Client.java /root/switcher_rmi_docker
COPY Machine/Machine.java /root/switcher_rmi_docker
COPY Server/Hello.java /root/switcher_rmi_docker

RUN javac *.java

ENTRYPOINT java Client