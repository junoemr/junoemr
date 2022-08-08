
registry=default-route-openshift-image-registry.apps.osdev.internal.cloudpractice.ca


print_usage() {
  echo "Usage: transfer_image_to_github.sh -e <environment> -p <project> -i <image> -t <tag>"
  echo "  e.g. transfer_image_to_github.sh -e osdev -p juno-build -i juno-spring-boot -t juno_sring_boot-20201-06-23.0"
  echo ""
  echo "-e - environment to use \(osdev or prod\)"
  echo "-p - project to use \(e.g. juno-build, juno-images\)"
  echo "-i - image stream to transfer from" 
  echo "-t - tag within the image stream to transfer"
  echo ""
  echo "Requires that oc be logged in to the relevant cluster and a local docker be accessible."
  echo "Also please provide a github token in \"/root/.ghcr.io_token\" that can has write access to the CloudMD-SSI package repository."
  echo "e.g. sudo vi ~/.ghcr.io_token"
}

while getopts 'h?e:p:i:t:' option; do
  case "${option}" in
  h | [?])
    print_usage
    exit 1
    ;;
  e) environment="${OPTARG}" ;;
  p) project="${OPTARG}" ;;
  i) image_name="${OPTARG}" ;;
  t) image_tag="${OPTARG}" ;;
  *)
    echo "Unexpected option ${option}"
    exit 1
    ;;
  esac
done

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

RED='\033[0;31m'
NC='\033[0m' # No Color

openshift_image_path=$registry/$project/$image_name:$image_tag
github_image_path="ghcr.io/cloudmd-ssi/$image_name:$image_tag"

echo ""
echo "Logging into registries..."
echo -e "\033[1;33m"

echo "GitHub"
sudo cat ~/.ghcr.io_token | sudo docker login ghcr.io --username jordan-nicholas --password-stdin
RESULT=$?
if [ $RESULT -ne 0 ]; then
	echo "Error logging into ghcr.io.  Please provide a valid token in the /root/.ghcr.io_token file"
fi
echo ""
echo "Openshift"
echo $(oc whoami -t) | sudo docker login --username $(oc whoami) --password-stdin $openshift_image_path
RESULT=$?
if [ $RESULT -ne 0 ]; then
	echo "Error logging into the openshift image registry.  Please make sure oc has an active connection to the cluster."
fi

echo -e "\033[0m"

echo ""
echo -e "Using OpenShift cluster $RED$environment$NC, image registry $RED$registry$NC to copy image $RED$image_name$NC with tag $RED$image_tag$NC to github"
echo ""
echo -e "\033[1;33m"
  
sudo docker pull $openshift_image_path
sudo docker tag $openshift_image_path $github_image_path
sudo docker push $github_image_path
echo -e "\033[0m"
