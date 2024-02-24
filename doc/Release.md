# How to create a release

A release can be created by creating a git tag for a commit. The tag must follow this pattern: 
release-1.2 or release-1.2.3

I.e.: release-X.X.Z where X, Y, Z are the major, minor resp. the patch level of the release. Z can be omitted.

**Note:** Such kind of release tags should only be applied to commits in the main branch.

```
#adjust [version]
vi build.gradle

# release via git
git commit -am "release"
git tag -am "release" release-[release version no]
git push --follow-tags

# bump version - increase version and add -SNAPSHOT
vi build.gradle
git commit -am "version bump"
git push
```