from subprocess import run
from pybuilder.core import init, task
from ddadevops import *

default_task = "dev"

name = "provs"
PROJECT_ROOT_PATH = "."


@init
def initialize(project):
    input = {
        "name": name,
        "module": "notused",
        "stage": "notused",
        "project_root_path": PROJECT_ROOT_PATH,
        "build_types": [],
        "mixin_types": ["RELEASE"],
        "release_primary_build_file": "build.gradle",
        "release_secondary_build_files": [],
        # release artifacts
        "release_artifact_server_url": "https://repo.prod.meissa.de",
        "release_organisation": "meissa",
        "release_repository_name": name,
        "release_artifacts": [
            "build/libs/provs-server.jar",
            "build/libs/provs-desktop.jar",
            "build/libs/provs-syspec.jar",
            "build/libs/sha256sum.lst",
            "build/libs/sha512sum.lst",
        ],
    }


@task
def build(project):
    run("./gradlew assemble", shell=True)


@task
def dev(project):
    build(project)


@task
def patch(project):
    """
    updates version to next patch level, creates a tag, creates new SNAPSHOT version,
    executes git commit and push
    """
    increase_version_number(project, "PATCH")
    release(project)


@task
def minor(project):
    """ updates version to next minor level, creates new SNAPSHOT version, executes git commit and push """
    increase_version_number(project, "MINOR")
    release(project)


@task
def major(project):
    """ updates version to next major level, creates new SNAPSHOT version, executes git commit and push """
    increase_version_number(project, "MAJOR")
    release(project)


@task
def tag(project):
    build = get_devops_build(project)
    build.tag_bump_and_push_release()


@task
def release(project):
    build = get_devops_build(project)
    build.prepare_release()
    tag(project)


@task
def package(project):
    run("./gradlew assemble -x test jar", shell=True)
    run("./gradlew assemble -x test uberjarDesktop", shell=True)
    run("./gradlew assemble -x test uberjarServer", shell=True)
    run("./gradlew assemble -x test uberjarSyspec", shell=True)
    run("cd build/libs/ && find . -type f -exec sha256sum {} \; | sort > sha256sum.lst", shell=True)
    run("cd build/libs/ && find . -type f -exec sha512sum {} \; | sort > sha512sum.lst", shell=True)


@task
def publish_artifacts(project):
    build = get_devops_build(project)
    build.publish_artifacts()


def increase_version_number(project, release_type):
    build = get_devops_build(project)
    build.update_release_type(release_type)
