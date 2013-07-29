JastAdd2 Release Notes
======================

R20130412
---------

### General

* Added option for indentation type in the JastAdd Ant Task
* Moved JastAdd main class to the package `org.jastadd`
(but left a deprecated class with the same name as a working entry point
for backward compatibility)
* Removed global static variables to allow concurrent JastAdd instances
in the same JVM
* Added JastAdd entry points that allow custom output/error streams

### Error handling

* Help text is printed to the standard out stream rather than the standard
error stream
* Improved error handling for missing equations of synthesized attributes:
now only the minimum set of classes requiring an equation is reported

### Performance

* Minimum list size now only affects `List` nodes (previously
also affected `Opt` nodes)
* It is now possible to specify a custom minimum list size with the
`minListSize` option

R20130312
---------

### Incremental evaluation

* Merged Emma S&ouml;derberg's incremental evaluation features into JastAdd2
* The new option `fullFlush` was added to enable more thorough flush behaviour (makes `flushCache` flush rewrites and NTAs)

### Removed obsolete features

* Dropped doxygen support
* Dropped J2ME code generation support
* Dropped parent interface support (for inherited equation searching)

### Internals

* Much of the JastAdd2 internals have been rewritten to improve readability

R20130212
---------

### Bug fixes

* Fixed bug concerning weaving interface refinements
* Fixed various bugs
* Fixed a code generation error in jjtree mode

### Error handling

* Multiple equivalent declarations of an inherited attribute now only raise a warning, rather than an error
* The warning for a duplicate inherited declaration includes the previous declaration location
* Improved error messages for method/equation refinement errors
* The path to the output directory is included in the error message for a missing output directory

### API changes

* `ASTNode.copy()` now sets the parent pointer of the copy to `null`
* Removed the redundant static method `ASTNode.getChild(ASTNode,int)`

### Performance

* Removed redundant NTA child initialization
* Increased minimum (non-empty) child array size from 1 to 4
* The child array for non-List, non-Opt nodes is now initialized to fit all children in the node constructor (removes redundant child-array growing)

### Other changes

* JastAdd2 no longer depends on AspectJ
* The `--doxygen` option is now deprecated
* Added the `--indent` command-line option which allows changing the indentation in generated code. Can be set to tabs or 2-, 4-, and 8 space indentation.

R20121112
---------

### Bugfix Release

* Fixed error in fullCopy causing some non-NTA children to not be copied correctly. This bug sometimes caused NullPointerExceptions or faulty behaviour in generated code.

R20121112
---------

### Bugfix Release

* Fixed error in fullCopy causing some non-NTA children to not be copied correctly. This bug sometimes caused NullPointerExceptions or faulty behaviour in generated code.

R20121026
---------

### Various bug fixes

* Fixed bug on Windows - backslashes in file paths now work correctly.
* NTA children are no longer copied by fullCopy.
* Removed the generated method for accessing value maps of parameterized attributes.
* Fixed error related to two synthesized attributes with the same name but different parameters.
* It is now possible to use static import statements in aspect files.
* Added a method getNumXNoTransform to count the number of elements in a list child without triggering rewrites.

R20121011
---------

### Various bug fixes

* getChild now returns null if the children array is null
* inherited equations lacking a return statement now give compilation error
* improved recognition of the "refined" keyword inside Java code in equations.
* Synthesized NTA with parameters may now have null values
* fullCopy now sets the parent of the copied tree to null
* insertChild and removeChild now update the childIndex field correctly
* Fixed how debug code escapes file names, to work also for Windows OS.
* Fixed bug in code generation for Contributes-clauses without when-part.
* Circular attributes now generate Java 1.4 code when the java1.4 option is set.

### Improved output handling

* Error messages, warnings, and help messages are now printed on stderr instead of on stdout.
* JastAdd now returns exit code zero when just printing version or help information.

### Improved option handling

* The options --noComponentCheck, --lazyMaps, and --refineLegacy are now by default ON when running from the command line.
* The default behavior is now the same when running from the command line as when running through ANT.
* Command line options are no longer case sensitive.
* Some options are deprecated. A warning is printed if such an option is used.
* Warnings are printed for some other cases, like giving an option twice, giving an option without an expected argument, etc.

R20110902
---------

### JavaDoc support

JastAdd now generates Java files with JavaDoc comments for built-in APIs and
APIs for attributes. See the upcoming RagDoll tool for more information.

### JDK7 compliance  

Code generated by older versions of JastAdd might give compile time errors when
compiling using javac from JDK 7. This is because JastAdd previously generated
code where private fields were accessed in an erroneous way according to the
JLS. Previous javac versions accepted such code, but the bug has been fixed in
JDK 7. This version of JastAdd generates code not giving this problem.

