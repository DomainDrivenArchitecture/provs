package org.domaindrivenarchitecture.provs.framework.core.docker.dockerimages


interface DockerImage {
    fun imageName() : String
    fun imageText() : String
}

/**
 * Provides a docker image based on ubuntu additionally with a non-root default user and sudo installed
 */
class UbuntuPlusUser(private val userName: String = "testuser") : DockerImage {

    override fun imageName(): String {
        return "ubuntu_24_plus_user"
    }

    override fun imageText(): String {
        return """
FROM ubuntu:24.04

ARG DEBIAN_FRONTEND=noninteractive

RUN apt-get update && apt-get -y install sudo
RUN useradd -m $userName && echo "$userName:$userName" | chpasswd && usermod -aG sudo $userName
RUN echo "$userName ALL=(ALL:ALL) NOPASSWD: ALL" | sudo tee /etc/sudoers.d/$userName

USER $userName
CMD /bin/bash
WORKDIR /home/$userName
"""
    }
}
