#!/bin/sh
VERSION=R`date +%Y%m%d`
echo Do the following commands to create and upload jastadd2 version ${VERSION}

# Patch the html files to use the correct version number
echo "sed -e '/JastAdd2 Release R/ s/Release R......../Release '${VERSION}'/' doc/index.html >doc/index.html.new"
echo rm doc/index.html
echo mv doc/index.html.new doc/index.html

echo "sed -e '/Reference manual for JastAdd2 R/ s/JastAdd2 R......../JastAdd2 '${VERSION}'/' doc/reference-manual.html >doc/reference-manual.html.new"
echo rm doc/reference-manual.html
echo mv doc/reference-manual.html.new doc/reference-manual.html

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
echo scp jastadd2.jar jastadd2-src.zip jastadd2-bin.zip doc/*.html doc/*.php login.cs.lth.se:/cs/jastadd/releases/jastadd2/${VERSION}
#scp jastadd2-src.jar jastadd2-bin.zip doc/*.html doc/*.php login.cs.lth.se:/cs/jastadd/releases/jastadd2/${VERSION}

# Cleaning up
echo rm -f jastadd2-bin.zip jastadd2-src.zip
#rm -f jastadd2-bin.zip jastadd2-src.zip

# Check that it works
echo ------------------------------------------------------
echo Browse to the website and check that everything works.
echo Then make clean and commit.
echo ------------------------------------------------------

# Tag in SVN
echo "svn copy http://svn.cs.lth.se/svn/jastadd/trunk/JastAdd2 http://svn.cs.lth.se/svn/jastadd/tags/JastAdd2/${VERSION} -m \"Release ${VERSION} of JastAdd2\" "
