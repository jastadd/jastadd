#!/bin/sh

# set version string
VERSION=R`date +%Y%m%d`

echo "# Follow these steps to release a new jastadd2 version ${VERSION}:"

echo
echo "# 1. Patch the html files to use the correct version number"

echo "sed -e 's/\\(JastAdd2 Release \\)R[0123456789]*/"'\\1'"${VERSION}/' \\"
echo "    doc/index.html > doc/index.html.new"
echo "mv doc/index.html.new doc/index.html"

echo "sed -e 's/\\(manual for JastAdd2 \\)R[0123456789]*/"'\\1'"${VERSION}/' \\"
echo "    doc/reference-manual.html >doc/reference-manual.html.new"
echo "mv doc/reference-manual.html.new doc/reference-manual.html"

echo
echo "# 2. Create the release zip files jastadd2-src.zip and jastadd2-bin.zip"
echo "ant release"

echo
echo "# 3. Create a new dir at jastadd.org"
echo "ssh login.cs.lth.se \"cd /cs/jastadd/releases/jastadd2 &&"\
	"mkdir ${VERSION}\""
#ssh login.cs.lth.se "cd /cs/jastadd/releases/jastadd2 && mkdir ${VERSION}"

echo
echo "# 4. Upload the zip files and appropriate documentation to jastadd.org"
echo "scp jastadd2.jar jastadd2-src.zip jastadd2-bin.zip doc/*.html doc/*.php\\"
echo "    login.cs.lth.se:/cs/jastadd/releases/jastadd2/${VERSION}"
#scp jastadd2-src.jar jastadd2-bin.zip doc/*.html doc/*.php login.cs.lth.se:/cs/jastadd/releases/jastadd2/${VERSION}

echo "# 5. Cleaning up"
echo "rm -f jastadd2-bin.zip jastadd2-src.zip"

# Check that it works
echo ------------------------------------------------------
echo Browse to the website and check that everything works.
echo Then ant clean and commit.
echo ------------------------------------------------------

# Tag in SVN
echo "svn copy http://svn.cs.lth.se/svn/jastadd/trunk/JastAdd2 \\"
echo "    http://svn.cs.lth.se/svn/jastadd/tags/JastAdd2/${VERSION} \\"
echo "    -m \"Release ${VERSION} of JastAdd2\""
