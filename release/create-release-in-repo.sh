#!/bin/bash
# Creates a release on a gitea server with assets (provs-desktop.jar, provs-server.jar, provs-syspec.jar)

readonly release_url="https://repo.prod.meissa.de/api/v1/repos/meissa/provs/releases"

if [[ -z "${RELEASE_TOKEN}" ]]; then
  echo "Error: RELEASE_TOKEN not set."; exit 7
else
  token="${RELEASE_TOKEN}"
fi

if [[ -z "${CI_COMMIT_TAG}" ]]; then
  echo "Error: CI_COMMIT_TAG not set."; exit 7
else
  release_tag="${CI_COMMIT_TAG}"
fi

result=$(curl -X 'POST' "${release_url}" -H 'accept: application/json' -H 'Content-Type: application/json' -d "{ \"body\": \"Provides jar-files for release ${release_tag}\\nAttention: The \\\"Source Code\\\"-files below are not up-to-date!\", \"tag_name\": \"${release_tag}\" }" -H "Authorization: token ${token}")

regex="\{\"id\":([0-9]+),"
if [[ $result =~ $regex ]]; then
  release_id="${BASH_REMATCH[1]}"
else
  echo "Error: release_id not found in: ${result}"; exit 7
fi

cd build/libs/ || { echo "Error: cd not possible"; exit 7; }

curl -X 'POST' "${release_url}/${release_id}/assets" -H 'accept: application/json' -H "Authorization: token ${token}" -H 'Content-Type: multipart/form-data' -F 'attachment=@provs-desktop.jar;type=application/x-java-archive'
curl -X 'POST' "${release_url}/${release_id}/assets" -H 'accept: application/json' -H "Authorization: token ${token}" -H 'Content-Type: multipart/form-data' -F 'attachment=@provs-server.jar;type=application/x-java-archive'
curl -X 'POST' "${release_url}/${release_id}/assets" -H 'accept: application/json' -H "Authorization: token ${token}" -H 'Content-Type: multipart/form-data' -F 'attachment=@provs-syspec.jar;type=application/x-java-archive'
curl -X 'POST' "${release_url}/${release_id}/assets" -H 'accept: application/json' -H "Authorization: token ${token}" -H 'Content-Type: multipart/form-data' -F 'attachment=@sha256sum.lst;type=text/plain'
curl -X 'POST' "${release_url}/${release_id}/assets" -H 'accept: application/json' -H "Authorization: token ${token}" -H 'Content-Type: multipart/form-data' -F 'attachment=@sha512sum.lst;type=text/plain'
