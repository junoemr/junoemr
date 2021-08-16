#!/bin/bash

# Check for required inputs, image name and tag
build_flag='false'
image_name=""
image_tag=""

print_usage() {
  echo "Usage: 2 arguments (image_name, image_tag) are required" \
    "(i.e. ./run-local-image.sh -i test-ojd-1234 -t v1.0.0)." \
    "Set -b to also build the image"
}

while getopts 'bh?i:t:' option; do
  case "${option}" in
  b) build_flag='true' ;;
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

fi

if [ "$build_flag" == "true" ]; then
  echo "Building OpenShift image..."
  ./build-openshift-image.sh -i $image_name -t $image_tag
fi

echo "Running image $image_name:$image_tag"
docker rm $image_name
docker run -v ~/localhost.properties:/opt/juno/juno.properties:Z \
  --ulimit nofile=524288:524288 \
  --name $image_name \
  --network="host" \
  -e "JAVA_TOOL_OPTIONS=-Djuno.propertiesFilename=/opt/juno/juno.properties" \
  -p 8080:8080 -t $image_name:$image_tag
