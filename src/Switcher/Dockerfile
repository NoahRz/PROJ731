FROM alpine

WORKDIR /root/switcher_rmi_docker

RUN apk add openjdk8
ENV JAVA_HOME /usr/lib/jvm/java-1.8-openjdk
ENV PATH $PATH:$JAVA_HOME/bin

COPY Switcher/Switcher.java /root/switcher_rmi_docker
COPY Machine/Machine.java /root/switcher_rmi_docker
COPY Switcher/Controle.java /root/switcher_rmi_docker
COPY Machine/Notification.java /root/switcher_rmi_docker


RUN javac *.java

ENTRYPOINT java Switcher