#!/bin/bash

# set version string
VERSION=R`date +%Y%m%d`

echo "This will script will tag, build and upload JastAdd2 $VERSION" \
  "to jastadd.org/releases/jastadd2/$VERSION"
echo
echo "IMPORTANT: Please update doc/release-notes.html before proceeding!"
echo

while true; do
  read -p "Proceed? " yn
  case $yn in 
    [Yy]* ) break;;
    [Nn]* ) exit;;
    * ) echo "Please answer yes or no.";;
  esac
done

echo "Bumping version string..."
echo "version=$VERSION" > src/res/JastAdd.properties

echo "Patching documentation files to use the correct version number..."
sed -e 's/\(JastAdd2 Release \)R[0-9]*/\1'${VERSION}'/' \
    doc/index.md > doc/index.md.new
mv doc/index.md.new doc/index.md
sed -e 's/\(manual for JastAdd2 \)R[0-9]*/\1'${VERSION}'/' \
    doc/reference-manual.html > doc/reference-manual.html.new
mv doc/reference-manual.html.new doc/reference-manual.html

echo "Committing changes..."
git commit -m "Bumped version string" src/res/JastAdd.properties \
  doc/index.md doc/reference-manual.html

echo "Tagging the release..."
git tag -a $VERSION -m "Version $VERSION"

echo "Building release..."
ant release

echo "Creating new directory at jastadd.org..."
ssh login.cs.lth.se "mkdir /cs/jastadd/releases/jastadd2/$VERSION"

echo "Uploading files to jastadd.org..."
scp jastadd2.jar jastadd2-src.zip jastadd2-bin.zip doc/*.html doc/*.php \
    doc/*.md README.md login.cs.lth.se:/cs/jastadd/releases/jastadd2/${VERSION}

echo "Setting group write permission for uploaded files..."
ssh login.cs.lth.se "chmod -R g+w /cs/jastadd/releases/jastadd2/$VERSION"

echo
echo "Check that it works!"
echo "--------------------"
echo "1. Browse to the website and check that everything looks okay."
echo "2. Update the web pages to include links to the new release."
echo "3. Push the changes to git."
echo "    git push origin ${VERSION}"

