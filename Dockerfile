FROM ubuntu:latest

ARG DEBIAN_FRONTEND=noninteractive

RUN apt-get update && apt-get -y install apt-utils adduser sudo

RUN useradd -m testuser && echo "testuser:testuserpw" | chpasswd && adduser testuser sudo
RUN echo "testuser ALL=(ALL:ALL) NOPASSWD: ALL" | sudo tee /etc/sudoers.d/testuser

USER testuser
CMD /bin/bash
WORKDIR /home/testuser
