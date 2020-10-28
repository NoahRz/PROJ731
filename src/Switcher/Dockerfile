FROM alpine

WORKDIR /root/switcher_rmi_docker

RUN apk add openjdk11
ENV JAVA_HOME /usr/lib/jvm/default-jvm/
ENV PATH $PATH:$JAVA_HOME/bin

COPY Switcher/Switcher.java /root/switcher_rmi_docker
COPY Machine/Machine.java /root/switcher_rmi_docker
COPY Switcher/Controle.java /root/switcher_rmi_docker
COPY Machine/Notification.java /root/switcher_rmi_docker
COPY Switcher/entrypoint.sh /root/switcher_rmi_docker


RUN javac *.java

ENTRYPOINT ["sh", "entrypoint.sh"]