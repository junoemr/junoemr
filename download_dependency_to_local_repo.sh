#!/usr/bin/env sh

while getopts ":a:r:" opt; do
  case ${opt} in
    a) group_id="$(echo "${OPTARG}" | cut -d ':' -f 1)"
      artifact_id="$(echo "${OPTARG}" | cut -d ':' -f 2)"
      packaging="$(echo "${OPTARG}" | cut -d ':' -f 3)"
      version="$(echo "${OPTARG}" | cut -d ':' -f 4)"
      ;;
    r)
      repository="${OPTARG}"
      ;;
    \?) echo "Invalid option -${OPTARG}"
      exit 1
  esac
done

file="${artifact_id}-${version}.${packaging}"
curl \
  --location \
  --output "${file}" \
  "${repository}/${group_id}/${artifact_id}/${version}/${file}"

mvn \
  install:install-file \
  -Dfile="${file}" \
  -DgroupId="${group_id}" \
  -DartifactId="${artifact_id}" \
  -Dversion="${version}" \
  -Dpackaging="${packaging}" \
  -DlocalRepositoryPath=./local_repo

rm "${file}"