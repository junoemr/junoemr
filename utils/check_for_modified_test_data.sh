#!/bin/bash

modified_table_names=()
echo "SHOW FULL TABLES WHERE table_type != 'VIEW' AND tables_in_oscar_test NOT LIKE '%_maventest';" | mysql oscar_test | while read -r table type ; do
	if [[ "VIEW" != $type && "Tables_in_oscar_test" != $table  ]] ; then

		#row_count=$(echo "SELECT count(*) FROM \`oscar_test\`.\`$table\`;" | mysql --batch -N oscar_test)
		#row_count_default=$(echo "SELECT count(*) FROM \`oscar_test\`.\`${table}_maventest\`;" | mysql --batch -N oscar_test)

		checksum_value=$(echo "CHECKSUM TABLE \`oscar_test\`.\`$table\`;" | mysql --batch -N oscar_test | cut -f2)
		checksum_value_default=$(echo "CHECKSUM TABLE \`oscar_test\`.\`${table}_maventest\`;" | mysql --batch -N oscar_test | cut -f2)

		#echo "$table"

		#if [[ $table == "issue" && $row_count != "15875" || $row_count != 0 ]] ; then
		if [[ $checksum_value != $checksum_value_default ]] ; then
			#echo "$table (rows: $row_count, expected: $row_count_default)"
			echo $table
			modified_table_names+=( $table )
		fi
	fi
done

