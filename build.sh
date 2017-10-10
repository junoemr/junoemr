JOB_NAME="RELEASE_15_BETA"
BUILD_NUMBER="`date +\%s`"
export JOB_NAME BUILD_NUMBER
CATALINA_HOME=/usr/java/apache-tomcat
rm -rf target
mvn process-resources -Dmaven.test.skip=true verify $1
