JOB_NAME="RELEASE_15_BETA"
BUILD_NUMBER="`date +\%s`"
export JOB_NAME BUILD_NUMBER
rm -rf target
mvn -Dmaven.test.skip=true verify 
