#Usage:
#./send_to_jira.sh = builds oscar_12_2_2013-11-11.war
#./send_to_jira.sh .2 = builds oscar_12_2_2013-11-11.2.war

DATE=`date +%Y-%m-%d` 
OSCAR_VERSION=oscar_12_2
DELIMITER=_

scp -P 20202 target/oscar-SNAPSHOT.war jira.oh.ca:/var/storage/oscar_versions/$OSCAR_VERSION$DELIMITER$DATE$1.war
