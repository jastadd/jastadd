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
      gradle documentation
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

echo "Building release ${VERSION}..."
gradle clean release "-PnewVersion=${VERSION}"

if [ "$?" -ne "0" ]; then
	exit $?
fi

echo "Ready to upload artifacts to jastadd.org..."
while true; do
  read -p "Proceed? (yes/no) " yn
  case $yn in
    [Yy]* ) break;;
    [Nn]* ) exit;;
    * ) echo "Please answer yes or no.";;
  esac
done

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
echo "Release Checklist"
echo "-----------------"
echo "1. Update web pages with links to the new release:"
echo "   * update index.md"
echo "   * update download.md"
echo "   * update documentation/reference-manual.php"
echo "2. Publish the web pages to the test website:"
echo "    ./testpublish.sh"
echo "3. Browse to http://jastadd.org/testweb and check that everything looks okay"
echo "4. Publish to the production website:"
echo "    ./publish.sh"
echo "    git commit -am \"Release ${VERSION}\""
echo "    git push origin master"
echo "5. Commit and push the website changes:"
echo "6. Tag the jastadd-test repository with the new JastAdd2 version:"
echo "    cd ../jastadd-test"
echo "    git tag -a ${VERSION} -m \"Tests for JastAdd2 ${VERSION}\""
echo "    git push origin ${VERSION}"
echo "7. Push the JastAdd2 release commit:"
echo "    git push origin master"
echo "8. Push the release tag:"
echo "    git push origin ${VERSION}"
echo "9. Upload artifacts to JastAdd Maven repository:"
echo "    gradle uploadArchives"

