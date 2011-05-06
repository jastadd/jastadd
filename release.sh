#!/bin/sh
VERSION=R`date +%Y%m%d`
echo Do the following commands to create and upload jastadd2 version ${VERSION}

# Create a zip file jastadd2-src.zip for the source distribution
echo make source-zip
#make source-zip

# Create a zip file jastadd2-bin.zip for the binary distribution.
echo make bin-zip
#make bin-zip

# Create a new dir at jastadd.org
echo ssh login.cs.lth.se \"cd /cs/jastadd/releases/jastadd2 \&\& mkdir ${VERSION}\"
#ssh login.cs.lth.se "cd /cs/jastadd/releases/jastadd2 && mkdir ${VERSION}"

# Upload the zip files and appropriate documentation to jastadd.org
echo scp jastadd2-src.jar jastadd2-bin.zip doc/*.html doc/*.php login.cs.lth.se:/cs/jastadd/releases/jastadd2/${VERSION}
#scp jastadd2-src.jar jastadd2-bin.zip doc/*.html doc/*.php login.cs.lth.se:/cs/jastadd/releases/jastadd2/${VERSION}

# Cleaning up
echo rm -f jastadd2-bin.zip jastadd2-src.zip
#rm -f jastadd2-bin.zip jastadd2-src.zip
