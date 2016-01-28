#!/bin/bash

# Copyright (c) 2011-2016, The JastAdd Team
# All rights reserved.
#
# Redistribution and use in source and binary forms, with or without
# modification, are permitted provided that the following conditions are met:
#
#     * Redistributions of source code must retain the above copyright notice,
#       this list of conditions and the following disclaimer.
#     * Redistributions in binary form must reproduce the above copyright
#       notice, this list of conditions and the following disclaimer in the
#       documentation and/or other materials provided with the distribution.
#     * Neither the name of the Lund University nor the names of its
#       contributors may be used to endorse or promote products derived from
#       this software without specific prior written permission.
#
# THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
# AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
# IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
# ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
# LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
# CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
# SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
# INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
# CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
# ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
# POSSIBILITY OF SUCH DAMAGE.

# This script is used to automate the JastAdd release process.
# The script does the following:
# 1. Ask if doc/release-notes.md is up-to-date.
# 2. Build binary and source Zip files for the release.
# 3. Add updated release notes and Version.properties in release commit with Git.
# 4. Tag the release in Git.
# 5. Update web pages.
#    a. Clone the web repository in ./website
#    b. Copy release files (refman, releasenotes, bin/src zip) to web repo.
#    c. Update version names in web files.
#    d. Publish web changes to staging website.
#    e. Publish web changes to live website.
#    f. Push web changes to Bitbucket.
# 6. Tag the test suite for this release.
#    a. Clone the test repository in ./jastadd-test
#    b. Tag the latest commit with the current JastAdd release version.
#    c. Push the tag to Bitbucket.

set -eu

if [ $# -lt "1" ]; then
    echo "Usage: $0 VERSION"
    exit 1
fi

update_website ()
{
    echo "Updating webpages..."
    if [ ! -d website ]; then
        echo "Cloning jastadd/web.git into ./website"
        git clone git@bitbucket.org:jastadd/web.git website
    else
        echo "Updating jastadd/web.git clone in ./website"
        pushd website
        git clean -fd
        git fetch origin
        git reset --hard origin/master
        popd
    fi

    # Copy files to the website repository.
    rsync -v \
        jastadd2-src.zip \
        jastadd2-bin.zip \
        README.md \
        doc/*.php \
        doc/index.md \
        doc/reference-manual.html \
        doc/release-notes.html \
        "website/static/releases/jastadd2/${VERSION}"

    # Update links.
    echo "Updating links..."
    echo "Updating website/web/index.md"
    vim -e website/web/index.md <<EOF
%s/^\(News:\n-----\n\)/\1\r### Date: JastAdd2 Release ${VERSION}\r\rVersion ${VERSION} of JastAdd2 has been released!\r\r* [Download it here!](\/releases\/jastadd2\/${VERSION}\/index.php)\r* [View Release Notes](\/releases\/jastadd2\/${VERSION}\/release-notes.php)\r/
w
EOF
    echo "Updating website/web/download.md"
    vim -e website/web/download.md <<EOF
%s/^\(JastAdd2\n--------\n\n\)/\1* [JastAdd2 Release ${VERSION}](\/releases\/jastadd2\/${VERSION})\r/
w
EOF
    echo "Updating website/web/documentation/reference-manual.php"
    vim -e website/web/documentation/reference-manual.php <<EOF
%s/\(releases\/jastadd2\/\)\d\+\.\d\+\.\d\+\(\/reference-manual.html\)/\1${VERSION}\2/
w
EOF
    echo "Updating website/static/releases/jastadd2/${VERSION}/reference-manual.html"
    vim -e "website/static/releases/jastadd2/${VERSION}/reference-manual.html" <<EOF
%s/\(Reference Manual for JastAdd \)\d\+\.\d\+\.\d\+/\1${VERSION}/
w
EOF

    pushd website
    echo "Publishing to staging website http://jastadd.org/testweb"
    ./testpublish.sh
    git add static/releases
    git commit -am "Update for JastAdd release ${VERSION}"
    echo
    echo "Published to http://jastadd.org/testweb - go check it out!"

    while true; do
      read -p "Publish live website and push changes to Bitbucket? (yes/no) " yn
      case $yn in
        [Yy]* ) break;;
        [Nn]* ) exit;;
        * ) echo "Please answer yes or no.";;
      esac
    done

    ./publish.sh
    git push
    popd
}

update_tests ()
{
    echo "Tagging test repository..."
    if [ ! -d tests ]; then
        echo "Cloning jastadd/jastadd-test into ./tests"
        git clone git@bitbucket.org:jastadd/jastadd-test.git tests
    else
        echo "Updating jastadd/jastadd-test clone in ./tests"
        pushd tests
        git clean -fd
        git fetch origin
        git reset --hard origin/master
        popd
    fi

    pushd tests
    git tag -a "${VERSION}" -m "Tests for JastAdd ${VERSION}"
    git push origin "${VERSION}"
    popd
}

VERSION="${1}"

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
      ${EDITOR:-vim} doc/release-notes.md
      gradle documentation
      echo "Generated doc/release-notes.html - check that the markup looks OK"
      break
      ;;
    [Nn]* ) break;;
    * ) echo "Please answer yes or no.";;
  esac
done

git add doc/release-notes.md

# Show staged changes.
git status -sb

echo "Building release ${VERSION}..."
gradle clean release "-PnewVersion=${VERSION}"
VERSIONFILE='src/res/Version.properties'
git add "${VERSIONFILE}"
git commit -m "Release ${VERSION}"
git tag -a "${VERSION}" -m "Version ${VERSION}"

update_website

update_tests

while true; do
  read -p "Push the JastAdd release commit to Bitbucket? (yes/no) " yn
  case $yn in
    [Yy]* ) break;;
    [Nn]* ) exit;;
    * ) echo "Please answer yes or no.";;
  esac
done

git push origin master
git push origin "${VERSION}"

echo "Release finished. To upload artifacts to the Central Repository, use this command:"
echo "    gradle uploadArchives -PsshUser=username"
