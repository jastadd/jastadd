JastAdd2
========

JastAdd2 was developed at Lund University by Torbjorn Ekman, Gorel Hedin,
and Eva Magnusson. JastAdd2 has recieved additional contributions from
Emma Söderberg, Jesper Öqvist and Niklas Fors.

For additional contributors, see the change logs.

License
-------

Copyright (c) 2005-2013, The JastAdd Team. All rights reserved.

JastAdd2 is  is covered by the modified BSD License. For the full license text
see the LICENSE file.

Obtaining JastAdd2
------------------

The latest version of JastAdd2 can be found at http://jastadd2.org

Dependencies
------------

JastAdd2 requires a Java runtime environment to run, and a JDK to build.
JastAdd2 uses JavaCC, JJTree, and Apache Ant. JavaCC and JJTree are included in
the source tree of JastAdd2, so the only external tools needed are Java
and Ant.

Building
--------

Builds are done by using the Apache Ant script build.xml:

* Ordinary build (generate and compile):

        > ant

* Create a new jar file (jastadd2.jar)

        > ant jar

* Create a new source zip file (jastadd2-src.zip)

        > ant source-zip

* Create a zip file containing the main jar plus release notes and reference
  manual (jastadd2-bin.zip)

        > ant bin-zip

* Make a new release (builds the source zip and binary zip):

        > ant release

* Delete generated files

        > ant clean

* Bootstrap jastadd2 (replace the jastadd2.jar used to build jastadd2).
  For stability, bootstrap is done seldom and only with extra testing.

        > ant bootstrap

Release Process
---------------

  1. Make sure the following files have appropriate content:
     - ChangeLog (Check against commit log, add note about the release)
     - doc/reference-manual.html
     - doc/release-notes.html (Add suitable high-level content for this release)
  2. Create a new jar file:
      `> ant jar`
  3. Run all test cases.
  4. Run the release script which will show you commands for doing the release.
     `> ./release.sh`
     This will involve:
     - Creating a new tagged version R20110506 (or other appropriate date)
     - Patching html files with version R20110506
     - Creating a zip file jastadd2-src.zip for the source distribution
     - Creating a zip file jastadd2-bin.zip for the binary distribution.
     - Upload the zip files and appropriate documentation to jastadd.org
     - Cleaning up
  5. Browse to jastadd.org/releases/jastadd2/R20110506 and check the content.
  6. Ant clean and commit
  7. Tag the current version according to ./release.sh instructions.
  8. Update the web pages to reflect the new release on the download and news pages.
     - Check out the web pages at http://svn.cs.lth.se/svn/jastadd-research/web
     - Update index-file.html with news item about the new release
     - Update download.html to link to the new release
     - Update documentation/reference_manual.php to link to latest reference manual
     - Commit the web pages
     - Do `./publish` (to check out the newest web on the real web site)

Tools Used
----------

Special tools used (jars included in the tools-dir):

* JastAdd2: The semantics are defined using JastAdd itself.
* JJTree:   Builds the AST used by JavaCC
* JavaCC:   For parsing aspect source files

General tools used (assumed to be available on your platform):

* JVM:      A Java Virtual Machine
* Ant:      Building JastAdd2

File Types
----------

* `.ast`      JastAdd abstract syntax tree files
* `.jrag`     JastAdd semantics files. Usually declarative.
* `.jadd`     JastAdd semantics files. Usually imperative.
* `.java`     Regular Java sources
* `.jjt`      JavaCC files, using the JJTree AST-building commands.

Directory Structure
-------------------

* `src/java` Java source files
    - `src/java/jastadd/`
        - JastAdd.java  The main class. Reads command line args and options.
        Compiles the .ast and .jrag files to Java files.
        - JastAdd.jrag  Extends the top AST node (Grammar) with methods for
        adding .ast and .jrag info.
        - CommandLineArguments.java
        A general class for reading in args and options
        from the cmd line.
        - JastAddTask.java
        Implements an ANT task for running jastadd, including
        support for various args and options.

* `src/jastadd/` JastAdd aspect and AST specifications
* `src/javacc/` JavaCC files
* `src/res/` Resource files
    - `src/res/template/` Template files
* `src/gen/` Generated source files
* `doc` Documentation html files.
    - `reference-manual.html`   The reference manual
    - `release-notes.html`      The release nodes
    - `index.html`              For download from the web
    - `*.php`                   Helper files for browsing uploaded files on the web
* `tools`      Tools used to build JastAdd
* `README.md`  This file.
* `LICENSE`    Text file with the jastadd LICENSE.
* `release.sh` A script printing the commands for doing a release.
* `ChangeLog`  A textfile recording changes done to JastAdd, intended for
               users of JastAdd (focusing on external behavior).
* `ChangeLogUntil2010.txt`
               A textfile containing cvs log messages until 2010 when the
               system was moved to svn.

Understanding the Implementation
--------------------------------

Overall behavior when running JastAdd:

* First, options are read and global flags are set.
* Then, an incomplete AST is built by parsing the .ast file.
* Then, the .jrag files are parsed, and their information is weaved into the AST.
* Now, the AST is complete, and attributes can be accessed.
* Then, possible errors are printed.
* Then, target AST classes are generated.

Versions
--------

To keep track of JastAdd versions, the version number (based on the current
date) is patched into both the source file JastAdd.java, and the manifest file.
The script doing this, newrelease, is called when building.

Parsing
-------

JavaCC and its tree-building extension, JJTree, are used for parsing. They
assume top classes called Node and SimpleNode. Normally, JJTree generates its
own AST node subclasses, but we "fool" it to use the AST classes generated by
JastAdd instead (by generating them before we run JavaCC/JJTree).

However, we let JJTree generate the AST classes for the Java code used inside
equations in .jrag files. This is because these expressions are not analyzed by
JastAdd. They are just unparsed at appropriate places in the generated Java
code.
