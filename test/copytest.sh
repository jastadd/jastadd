#!/bin/sh

echo "Copying "$1" to "$2":"

SRC=$1".java"
DEST=$2".java"
echo -n "  "$SRC " to "$DEST" ... "
cp $SRC $DEST
echo "done."

SRC=$1".jrag"
DEST=$2".jrag"
echo -n "  "$SRC " to "$DEST" ... "
cp $SRC $DEST
echo "done."

SRC=$1".ast"
DEST=$2".ast"
echo -n "  "$SRC " to "$DEST" ... "
cp $SRC $DEST
echo "done."

SRC=$1".result"
DEST=$2".result"
echo -n "  "$SRC " to "$DEST" ... "
cp $SRC $DEST
echo "done."

SRC=$1".info"
DEST=$2".info"
echo -n "  "$SRC " to "$DEST" ... "
cp $SRC $DEST
echo "done."

SRC=$1".options"
DEST=$2".options"
if [ -f $SRC ]; then
  echo -n "  "$SRC " to "$DEST" ... "
  cp $SRC $DEST
  echo "done."
fi
