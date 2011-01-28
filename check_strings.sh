#!/bin/sh
for BUNDLE in src/ch/fhnw/jbackpack/Strings*
do
	echo "processing bundle ${BUNDLE}"
	while read LINE
	do
		KEY=$(echo ${LINE} | awk -F= '{ print $1 }')
		find -name "*.java" -print0 | xargs -0 grep -q "\"${KEY}\""
		if [ $? != 0 ]
		then
			echo "KEY \"${KEY}\" not found"
		fi
	done <${BUNDLE}
done
