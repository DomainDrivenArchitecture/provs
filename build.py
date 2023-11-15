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
    }

    build = ReleaseMixin(project, input)
    build.initialize_build_dir()

@task
def patch(project):
    linttest(project, "PATCH")
    release(project)


@task
def minor(project):
    linttest(project, "MINOR")
    release(project)


@task
def major(project):
    linttest(project, "MAJOR")
    release(project)


@task
def dev(project):
    linttest(project, "NONE")


@task
def prepare(project):
    build = get_devops_build(project)
    build.prepare_release()


@task
def tag(project):
    build = get_devops_build(project)
    build.tag_bump_and_push_release()


@task
def build(project):
    print("---------- build stage ----------")
    run("./gradlew assemble", shell=True)


@task
def package(project):
    run("./gradlew -x test jar", shell=True)
    run("./gradlew -x test uberjarDesktop", shell=True)
    run("./gradlew -x test uberjarServer", shell=True)
    run("./gradlew -x test uberjarSyspec", shell=True)
    run("cd build/libs/ && find . -type f -exec sha256sum {} \; | sort > sha256sum.lst", shell=True)
    run("cd build/libs/ && find . -type f -exec sha512sum {} \; | sort > sha512sum.lst", shell=True)


def release(project):
    prepare(project)
    tag(project)

def linttest(project, release_type):
    build(project)