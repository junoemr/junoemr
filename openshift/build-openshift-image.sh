#!/bin/bash

# Check for required inputs, image name and tag, and options skip build flag
skip_flag="false"
image_name=""
image_tag=""

print_usage() {
  echo "Usage: 2 arguments (image_name, image_tag) are required" \
    "(i.e. ./build-openshift-image.sh -s -i test-ojd-1234 -t v1.0.0)." \
    "Set -s to skip the build process and only create the image"
}

while getopts 'sh?i:t:' option; do
  case "${option}" in
  s) skip_flag='true' ;;
  h | [?])
    print_usage
    exit 1
    ;;
  i) image_name="${OPTARG}" ;;
  t) image_tag="${OPTARG}" ;;
  *)
    echo "Unexpected option ${option}"
    exit 1
    ;;
  esac
done

if [[ -z $image_name || -z $image_tag ]]; then
  print_usage
  exit 1
else
  echo "Creating image $image_name:$image_tag"
fi

# Need to run this from the base folder
# ${0%/*} gets the directory of this script, and we cd up one level
cd "${0%/*}/.."

if [ "$skip_flag" == "true" ]; then
  echo "Skipping build..."
else
  echo "Starting build..."
  ./build.sh
fi

if [ $? -eq 0 ]; then
  oc project juno-build
  oc create imagestream $image_name -n juno-build
  HOST=$(oc get route default-route -n openshift-image-registry --template='{{ .spec.host }}')
  docker login -u $(oc whoami) -p $(oc whoami -t) $HOST/juno-build/$image_name:$image_tag
  docker build -t $image_name:$image_tag -f ./openshift/Dockerfile .
  docker tag $image_name:$image_tag $HOST/juno-build/$image_name:$image_tag
  docker push $HOST/juno-build/$image_name:$image_tag
else
  echo "Build failed"
fi
