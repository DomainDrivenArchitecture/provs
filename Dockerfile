# image for usage in ci pipeline
FROM ubuntu:22.04

ARG DEBIAN_FRONTEND=noninteractive

RUN apt-get update && apt-get -y install apt-utils sudo

RUN useradd -m testuser && echo "testuser:testuserpw" | chpasswd && usermod -aG sudo testuser
RUN echo "testuser ALL=(ALL:ALL) NOPASSWD: ALL" | sudo tee /etc/sudoers.d/testuser

USER testuser
CMD /bin/bash
WORKDIR /home/testuser
