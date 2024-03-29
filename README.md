# JastAdd2

JastAdd2 was developed at Lund University by Torbjörn Ekman, Görel
Hedin, and Eva Magnusson. JastAdd2 has recieved additional contributions from
Emma Söderberg, Jesper Öqvist and Niklas Fors.

For additional contributors, see the change logs.

## License

Copyright (c) 2005-2020, The JastAdd Team. All rights reserved.

JastAdd2 is covered by the modified BSD License. For the full license text
see the LICENSE file.

## Obtaining JastAdd2

The latest version of JastAdd2 can be found at [jastadd.org][1].


## Dependencies

JastAdd2 requires a Java Runtime Environment (JRE) to run, and a JDK to build.
The minimum required Java version for JastAdd2 is Java SE 6.

JastAdd2 uses JavaCC, JJTree, and Apache Ant. JavaCC and JJTree are included in
the source tree of JastAdd2, so the only external tools needed are Java and
Ant. See licenses/javacc-BSD for the full license text.

The context-free grammar used in JastAdd is based on an example from JavaCC.

The README file for the binary distribution (README-binary-dist.md) is written
in the markup language Markdown. This file is converted to HTML using a Python
script during the build script. The Python script requires the Python-Markdown
package for Python. The Python-Markdown package can be installed using `pip
install markdown`.


## Building

Builds are done by using the Gradle script (`gradlew` on Unix-likes, `gradlew.bat` on Windows):

* Build `jastadd2.jar`:

        > ./gradlew

* Build a new release (builds jar file, source- and binary zip files). Requires the release
  version to be specified with `-PnewVersion=XX`

        > ./gradlew release -PnewVersion=2.123

* Delete generated files:

        > ./gradlew clean


## File Types

* `.ast`      JastAdd abstract syntax tree files
* `.jrag`     JastAdd semantics files. Usually declarative.
* `.jadd`     JastAdd semantics files. Usually imperative.
* `.java`     Regular Java sources
* `.jjt`      JavaCC files, using the JJTree AST-building commands.


## Directory Structure

* `src/java` Java source files.
    - `src/java/org/jastadd/`
        - `JastAdd.java` The main class. Compiles `*.ast` and `*.jrag` files to Java files.
        - `Configuration.java` Encapsulates JastAdd configuration options. Can be used
        to parse command line arguments.
        - `JastAddTask.java` Implements an ANT task for running JastAdd,
        including support for most available options.
* `src/jastadd/` JastAdd aspect and AST specifications
* `src/javacc/` Parser specification files for JavaCC.
* `src/res/Version.properties` Contains the JastAdd version property string.
* `src/template` Template files for code generation.
* `src/gen/` Generated source files.
* `doc` Documentation html files.
    - `reference-manual.html` The reference manual
    - `release-notes.html` The release nodes
    - `index.html` For download from the web
    - `*.php` Helper files for browsing uploaded files on the web
* `tools` 3rd party jar files used to build build JastAdd.
* `README.md` This file.
* `LICENSE` Text file with the jastadd LICENSE.
* `release.sh` A script printing the commands for doing a release.
* `ChangeLog` A textfile recording changes done to JastAdd, intended for
    users of JastAdd (focusing on external behavior).
* `ChangeLogUntil2010.txt`
    A textfile containing cvs log messages until 2010 when the
    system was moved to svn.

## Understanding the Implementation

Overall behavior when running JastAdd:

* First, options are read and global flags are set.
* Then, an incomplete AST is built by parsing the .ast file.
* Then, the .jrag files are parsed, and their information is weaved into the AST.
* Now, the AST is complete, and attributes can be accessed.
* Then, possible errors are printed.
* Then, target AST classes are generated.


## Parsing

JavaCC and its tree-building extension, JJTree, are used for parsing. They
assume top classes called Node and SimpleNode. Normally, JJTree generates its
own AST node subclasses, but we "fool" it to use the AST classes generated by
JastAdd instead (by generating them before we run JavaCC/JJTree).

However, we let JJTree generate the AST classes for the Java code used inside
equations in .jrag files. This is because these expressions are not analyzed by
JastAdd. They are just unparsed at appropriate places in the generated Java
code.

[1]: http://jastadd.org
