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

echo "Building release..."
ant release -Dversion=$VERSION

echo "Creating new directory at jastadd.org..."
ssh login.cs.lth.se "mkdir /cs/jastadd/releases/jastadd2/$VERSION"

echo "Uploading files to jastadd.org..."
scp jastadd2-src.zip jastadd2-bin.zip doc/*.html doc/*.php \
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

