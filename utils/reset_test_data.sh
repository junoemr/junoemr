#!/bin/bash

while true; do
    read -p "Running this will reset the oscar_test database?  Do you wish to continue?" yn
    case $yn in
        [Yy]* ) break;;
        [Nn]* ) exit;;
        * ) echo "Please answer yes or no.";;
    esac
done

echo "SHOW FULL TABLES WHERE table_type != 'VIEW' AND tables_in_oscar_test NOT LIKE '%_maventest';" | mysql oscar_test | while read -r table type ; do
	if [[ "VIEW" != $type && "Tables_in_oscar_test" != $table  ]] ; then
		echo "Resetting $table"
		echo "SET FOREIGN_KEY_CHECKS = 0; \
			TRUNCATE \`oscar_test\`.\`$table\`;\
			INSERT INTO \`oscar_test\`.\`$table\` \
			  SELECT * FROM \`oscar_test\`.\`${table}_maventest\`;\
			SET FOREIGN_KEY_CHECKS = 1;" | mysql oscar_test
	fi
done
