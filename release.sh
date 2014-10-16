#!/bin/bash

if [ $# -lt "1" ]; then
    echo "Usage: $0 VERSION"
    exit 1
fi

VERSION=$1

echo "JastAdd2 Release $VERSION"
echo "======================"
echo
echo "This script will tag, build and upload JastAdd2 $VERSION to"
echo "http://jastadd.org/releases/jastadd2/$VERSION"
echo

while true; do
  read -p "Do you wish to edit doc/release-notes.md now? (yes/no) " yn
  case $yn in 
    [Yy]* )
      if [[ -z "$EDITOR" ]]; then
        EDITOR=vim
      fi
      $EDITOR doc/release-notes.md
      ant build-doc
      echo "Generated doc/release-notes.html - check that the markup looks OK"
      break
      ;;
    [Nn]* ) break;;
    * ) echo "Please answer yes or no.";;
  esac
done

# show staged changes
git add doc/release-notes.md
git status -sb

echo "NB: All **staged** changes will be added to the release commit!"
echo "See the above status message for changes that will be committed."

while true; do
  read -p "Proceed? (yes/no) " yn
  case $yn in 
    [Yy]* ) break;;
    [Nn]* ) exit;;
    * ) echo "Please answer yes or no.";;
  esac
done

echo "Building release $VERSION..."
ant release -Dversion=$VERSION

echo "Uploading files to jastadd.org..."
# --chmod=g+w sets group write permission
rsync -av --chmod=g+w \
  jastadd2-src.zip \
  jastadd2-bin.zip \
  README.md \
  doc/*.php \
  doc/index.md \
  doc/reference-manual.html \
  doc/release-notes.html \
  login.cs.lth.se:/cs/jastadd/releases/jastadd2/${VERSION}

echo
echo "Check that it works!"
echo "--------------------"
echo "1. Update the web pages to include links to the new release."
echo "   * update index.md"
echo "   * update download.md"
echo "   * update documentation/reference-manual.php"
echo "2. Browse to the website and check that everything looks okay."
echo "3. Push the release commit"
echo "    git push origin master"
echo "4. Push the release tag"
echo "    git push origin ${VERSION}"
echo "5. Tag the jastadd-test repository with the new JastAdd2 version"
echo "    cd ../jastadd-test"
echo "    git tag -a ${VERSION} -m \"Tests for JastAdd2 ${VERSION}\""
echo "    git push origin ${VERSION}"
echo "6. Upload artifacts to JastAdd Maven repository"
echo "    gradle uploadArchives"

