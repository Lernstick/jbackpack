#!/bin/sh
for ICON in $(cd src; find -name "*.png" | sed 's|./|/|')
do
	find -name "*.java" | xargs grep -q "\"${ICON}\""
	if [ $? != 0 ]
	then
		echo "icon \"${ICON}\" not found"
	fi
done
