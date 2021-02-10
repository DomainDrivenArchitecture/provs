package io.provs.docker.images


interface DockerImage {
    fun imageName() : String
    fun imageText() : String
}

/**
 * Dockerfile based ubuntu image added by a non-root default user.
 */
@Suppress("unused")  // used by other libraries
class UbuntuPlusUser(private val userName: String = "testuser") : DockerImage {

    override fun imageName(): String {
        return "ubuntu_plus_user"
    }

    override fun imageText(): String {
        return """
FROM ubuntu:18.04

ARG DEBIAN_FRONTEND=noninteractive

RUN apt-get update && apt-get -y install sudo
RUN useradd -m $userName && echo "$userName:$userName" | chpasswd && adduser $userName sudo
RUN echo "$userName ALL=(ALL:ALL) NOPASSWD: ALL" | sudo tee /etc/sudoers.d/$userName

USER $userName
CMD /bin/bash
WORKDIR /home/$userName
"""
    }
}
