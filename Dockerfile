FROM ubuntu:latest

ARG DEBIAN_FRONTEND=noninteractive

RUN apt-get update && apt-get -y install apt-utils sudo
RUN useradd -m testuser && echo "testuserpw:testuser" | chpasswd && adduser testuser sudo
RUN echo "testuser ALL=(ALL:ALL) NOPASSWD: ALL" | sudo tee /etc/sudoers.d/testuser

RUN useradd testuser2 && echo "testuser2pw:testuser2" | chpasswd && adduser testuser2 sudo

USER testuser
CMD /bin/bash
WORKDIR /home/testuser
