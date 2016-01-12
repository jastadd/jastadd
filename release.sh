#!/bin/bash

# Copyright (c) 2011-2015, The JastAdd Team
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

# This script is used to simplify and standardize the release process. Things
# that we should add at some point:
#
# * Signing the JastAdd Jar file and publishing the cryptographic signature.

set -eu

if [ $# -lt "1" ]; then
    echo "Usage: $0 VERSION"
    exit 1
fi

update_website ()
{
    echo "Updating webpages..."
    if [ ! -d website ]; then
        git clone git@bitbucket.org:jastadd/web.git website
    else
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
    vim -e website/web/index.md <<EOF
%s/^\(News:\n-----\n\)/\1\r### Date: JastAdd2 Release ${VERSION}\r\rVersion ${VERSION} of JastAdd2 has been released!\r\r* [Download it here!](\/releases\/jastadd2\/${VERSION}\/index.php)\r* [View Release Notes](http:\/\/jastadd.org\/releases\/jastadd2\/${VERSION}\/release-notes.php)\r/
w
EOF
    vim -e website/web/download.md <<EOF
%s/^\(JastAdd2\n--------\n\n\)/\1* [JastAdd2 Release ${VERSION}](\/releases\/jastadd2\/${VERSION})\r/
w
EOF
    vim -e website/web/documentation/reference-manual.php <<EOF
%s/\(releases\/jastadd2\/\)\d\+\.\d\+\.\d\+\(\/reference-manual.html\)/\1${VERSION}\2/
w
EOF

    pushd website
    echo "Publishing to staging website http://jastadd.org/testweb..."
    ./testpublish.sh
    git add static/releases
    git commit -am "Update for JastAdd release ${VERSION}"
    echo
    echo "Published to http://jastadd.org/testweb - go check it out!"

    while true; do
      read -p "Push website changes to bitbucket and continue? (yes/no) " yn
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

# Show staged changes.
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

update_web

echo
echo "Post-release checklist"
echo "----------------------"
echo
echo "1. Tag the jastadd-test repository with the new JastAdd2 version:"
echo "    cd ../jastadd-test"
echo "    git tag -a ${VERSION} -m \"Tests for JastAdd2 ${VERSION}\""
echo "    git push origin ${VERSION}"
echo "2. Push the JastAdd2 release commit:"
echo "    git push origin master"
echo "7. Push the release tag:"
echo "    git push origin ${VERSION}"
echo "8. Upload artifacts to JastAdd Maven repository:"
echo "    gradle uploadArchives -PsshUser=username"
