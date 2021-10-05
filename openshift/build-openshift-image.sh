#!/bin/bash

# Check for required inputs, image name and tag, and options skip build flag
skip_flag="false"
environment=osdev
registry=default-route-openshift-image-registry.apps.osdev.internal.cloudpractice.ca
image_name=""
image_tag=""

print_usage() {
  echo "Usage: 2 arguments (image_name, image_tag) are required" \
    "(i.e. ./build-openshift-image.sh -s -i test-ojd-1234 -t v1.0.0)."
  echo "Set -s to skip the build process and only create the image."
  echo "Environment defaults to osdev cluster, use -e prod for production (can only select osdev or prod)."
  echo "Docker will require sudo access, you will need to login once the build is completed"
}

while getopts 'sh?e:i:t:' option; do
  case "${option}" in
  s) skip_flag='true' ;;
  h | [?])
    print_usage
    exit 1
    ;;
  e) environment="${OPTARG}" ;;
  i) image_name="${OPTARG}" ;;
  t) image_tag="${OPTARG}" ;;
  *)
    echo "Unexpected option ${option}"
    exit 1
    ;;
  esac
done

# Check if logged into OpenShift
if ! oc whoami; then
  echo "Not logged into OpenShift, log in and try again"
  exit 1
fi

# Validate image name and tag
if [[ -z $image_name || -z $image_tag ]]; then
  print_usage
  exit 1
fi

# Validate OpenShift environment
if [ "$environment" == "osdev" ]; then
  registry=default-route-openshift-image-registry.apps.osdev.internal.cloudpractice.ca
elif [ "$environment" == "prod" ]; then
  registry=default-route-openshift-image-registry.apps.prod.cldmd.net
else
  echo "Environment can only be osdev or prod"
  print_usage
  exit 1
fi

# Check for correct OpenShift cluster
current_registry=$(oc get route default-route -n openshift-image-registry --template='{{ .spec.host }}')
if ! echo $current_registry | grep -q $environment; then
  echo "You selected environment $environment, but current image registry is $current_registry"
  echo "Log into the correct OpenShift environment and try again"
  exit 1
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
  echo "Creating imagestream or use existing imagestream"

  if ! oc get imagestreams -n juno-build -o name | cut -d/ -f2 | grep -q "$image_name"; then
    oc create imagestream $image_name -n juno-build
  else
    echo "Imagestream already exists, skip creating"
  fi

  echo "Using OpenShift cluster $environment, image registry $registry to create image $image_name with tag $image_tag"
  image_path=$registry/juno-build/$image_name:$image_tag
  echo $(oc whoami -t) | sudo docker login --username $(oc whoami) --password-stdin $image_path
  sudo docker build -t $image_name:$image_tag -f ./openshift/Dockerfile .
  sudo docker tag $image_name:$image_tag $image_path
  sudo docker push $image_path
else
  echo "Build failed"
fi
