JOB_NAME="RELEASE_12_1"
BUILD_NUMBER="`date +\%s`"
rm -rf target
mvn -Dmaven.test.skip=true verify
