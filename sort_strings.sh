#!/bin/sh
for BUNDLE in src/ch/fhnw/jbackpack/Strings*
do
	sort ${BUNDLE} > tmp
	mv tmp ${BUNDLE}
done
