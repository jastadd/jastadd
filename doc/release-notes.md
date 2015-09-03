JastAdd2 Release Notes
======================

2.1.12 - 2015-09-03
-------------------

### Bug Fixes

* Fixed problems causing inherited attributes to not work for nodes types only
  occuring as synthesized attributes.
[bug1](https://bitbucket.org/jastadd/jastadd2/issues/217/report-missing-inherited-equations-for)
[bug2](https://bitbucket.org/jastadd/jastadd2/issues/224/node-type-only-used-as-nta-treated-as-root)
* Added support for the [Java 7 diamond
  feature](https://docs.oracle.com/javase/7/docs/technotes/guides/language/type-inference-generic-instance-creation.html).
[More info](https://bitbucket.org/jastadd/jastadd2/issues/218/java-7-diamond-is-not-parsed-by-jastadd)

### Parsing

* Removed support for package declarations in aspect files. Package
  declarations did not affect code generation in any way, but this change can
cause old JastAdd code to fail to compile because package declarations now
cause a syntax error in the JastAdd parser. Removing the package declaration
should resolve this.  [More
info](https://bitbucket.org/jastadd/jastadd2/issues/227/remove-support-for-package-declaration-in)

### Code Generation

* Code generation was improved to remove some redundant generated code.
* Improved indentation problems.
* Declared-at comments are now generated in inherited attribute methods. [More
  info](https://bitbucket.org/jastadd/jastadd2/issues/211/generate-declared-at-comments-in-inherited)
* `ASTNode.state` is now private.

### Command-line Interface

* Added `--stateClassName` option, which can be used to customize the name of
  the generated class `ASTNode$State`.

2.1.11 - 2015-02-06
-------------------

### Bug Fixes

* NTAs using the `nta` keyword on the equation and simultaneously having the
  corresponding child declared using the `/NTA/` syntax in the grammar now
store their value in the child array.  This fixes a long-standing issue where
such NTAs effectively discarded their equation value. A new warning message is
generated to warn about NTAs that were affected by this issue. The warning can
be silenced by removing the `nta` keyword from the NTA equation. [More
info](https://bitbucket.org/jastadd/jastadd2/issue/198/component-declared-nta-in-both-ast-and)
* Removed a declaration order dependency between interface declarations and
  synthesized attributes. [More
info](https://bitbucket.org/jastadd/jastadd2/issue/194/declaration-order-dependency-for)

### Code Generation

* It is now possible to use JastAdd keywords like `syn`, `eq`, and `inh` as
  regular Java identifiers in certain Java code contexts such as attribute
equations or method bodies. [More
info](https://bitbucket.org/jastadd/jastadd2/issue/195/jastadd-keywords-in-java-code)
* Removed redundant and incorrect cache checks for some types of NTAs. [More
  info](https://bitbucket.org/jastadd/jastadd2/issue/182/nta-double-cache-check)
* Setter methods are no longer generated for NTA children and in the case where
  a setter method is inherited from a non-NTA child in a superclass the set
method will throw an exception if it is called attempting to set the NTA child.
[More
info](https://bitbucket.org/jastadd/jastadd2/issue/200/do-not-generate-set-methods-for-nta)
* Removed inherited attribute return type from `Define_XXX` method names.
* Missing contributions for a collection attribute now result in a warning
  rather than an error. [More
info](https://bitbucket.org/jastadd/jastadd2/issue/203/missing-contributions-for-collection)
* Fixed multi-level inheritance of collection attribute declaration through
  interface inheritance. [More
info](https://bitbucket.org/jastadd/jastadd2/issue/204/multi-level-interface-inheritance-of)
* Many fixes to the indentation of generated code.

### Command-line Interface

* Options are now sorted by name in the `--help` output.
* Added the `--ASTNodeSuper` option to set a custom superclass for ASTNode.
  [More
info](https://bitbucket.org/jastadd/jastadd2/issue/107/custom-superclass-for-astnode)
* Added the `--dot` option to generate a class diagram for the input grammar in
  the Dot format.
* Added the `--generateImplicits` option. Setting this to `false` means that
  ASTNode, List, and Opt must be declared in the grammar. This makes it
possible to declare ASTNode abstract.
* The `--rewrite` option accepts the `true` value again. The `true` value was
  unintentionally removed in version 2.1.9.
* The `--java1.4` option has been disabled. If Java 1.4 support is required
  then a third-party translator can be used, such as Retrotranslator
(http://retrotranslator.sourceforge.net/)

2.1.10 - 2014-10-10
-------------------

### Bug Fixes

* Fixed regression since version 2.1.9 causing collection attributes on
  interfaces to generate illegal code (incorrect return type on interface
method).

2.1.9 - 2014-09-01
------------------

### Bug Fixes

* Allow inherited attributes to be declared on List and Opt, even if not
  declared on other nodes.
* Fixed error where a cloned node would not be able to be rewritten if the node
  it cloned was already rewritten once.
* Interface declarations nested in a class declaration should no longer cause
  JastAdd to generate a top-level interface with the same name.
* Removed aspect extends and implements constructs that were never used (these
  were parsed by JastAdd but then ignored).
* Removed indirect call to flushRewriteCache from clone.  This caused a bug
  when rewrites, flush, and treeCopy were used together.

### Code Generation

* Aspect top-level enum declarations are now supported.
* JastAdd2 can now weave generic method inter-type declarations such
  as `public <T> void A.m()`

### Command-line Interface

* Deprecated some command-line options:
    - doc (unused)
    - java1.4 (not tested)
    - noLazyMaps (equivalent to lazyMaps=false)
    - noVisitCheck (equivalent to visitCheck=flase)
    - noCacheCycle (equivalent to cacheCycle=false)
    - noRefineLegacy (equivalent to refineLegacy=false)
    - noComponentCheck (componentCheck was already off by default)
    - noInhEqCheck (equivalent to inhEqCheck=false)
    - noStatic (the name was misleading, renamed to staticState)
    - deterministic (collection attributes are now always deterministic)
* Added or updated some options:
    + staticState (replaces noStatic)
    + incremental: added 'none' option (this is the default value)
* More information is printed when using deprecated options (added "deprecated
  since" and optional deprecation description)
* The JastAdd Ant task now behaves more like the command-line interface. The
  same warnings are printed for deprecated options and some options that were
not available in the Ant task have been added.
* JastAdd no longer warns about missing inherited equations for inherited
  attributes declared on ASTNode.
* Subtypes of List and Opt are no longer considered root nodes for the missing
  inherited equation warnings.

2.1.8 - 2014-06-11
------------------

### Bug Fixes

* Fixed regression introduced in 2.1.7 which made JastAdd generate faulty
  Java code for some tracing interfaces

2.1.7 - 2014-06-10
------------------

### Code Generation

* JastAdd now generates annotations for children, tokens and attributes
  (`@Child`, `@ListChild`, `@OptChild`, `@Token`, `@Attribute`).

* Removed obsolete aspect "sons" declaration.

* JastAdd no longer strips modifiers from aspect interface declarations.

* JastAdd no longer removes static or adds public modifier to aspect class
  declarations.

### Bug Fixes

* Fixed regression introduced in 2.1.6 which caused circular collection
  attributes to crash JastAdd.

* Fixed some cases where the order of parsing aspect files could produce
  different results.

2.1.6 - 2013-11-29
------------------

### Performance

* JastAdd now inlines all equation compute methods where the equation is a
  single Java expression.

* JastAdd uses a new inherited equation checking analysis that runs quicker on
  grammars with many inherited attributes. This can reduce JastAdd compile
times for large projects.

### Stability

* Fixed potential crash during refinement processing.

### Command-Line Interface

* JastAdd now reports a warning if multiple values are given to an option that
  only accepts one value argument.

### Code Generation

* JastAdd now generates the trace class as the nested class
  `ASTNode$State.Trace`. The global `Trace` instance is accessed using the
method `ASTNode$State.trace()`.

* Added the `ASTNode.treeCopyNoTransform()` method to replace
  `ASTNode.fullCopy()`, deprecating `fullCopy`.

* Added the `ASTNode.doFullTraversal()` method which uses `getChild()` to
  traverse the subtree and trigger rewrites.

* Added the `ASTNode.treeCopy()` method which uses `doFullTraversal()` to
  trigger rewrites before copying the tree with `treeCopyNoTransform()`.

### Flushing

* JastAdd now supports a flush flag which allows for configuring of flushing.
  The flag takes the following values: `attr`, `coll`, `rewrite`, `full`.
Default is `attr` and `coll`. The `rewrite` value makes flushing of outermost
rewrites possible by storing their initial values. The `full` value includes
all values.

* The default flushing API provided by JastAdd has been extended with a
  `flushTreeCache()` method calling `flushCache()` and traversing the tree. A
call to this method together with the use of the `flush=full` will result in a
full flush of an AST.

* Attributes are flushed via separate reset methods, one per cache attribute.

* All cached attributes in a node are flushed from a `flushAttrCache()` method
  which is called from `flushCache()`.

* Rewrites are flushed from a `flushRewriteCache()` method which is called from
  `flushCache()`.

2.1.5 - 2013-09-17
------------------

### Tracing & Caching

* Added support for static configuration of tracer events via the `--tracing`
  flag. The flag can now be given a comma separated list of event categories
  to be generated. Accepted values are `compute`,`cache`,`rewrite`,`circular`,
  and `copy`. All events are generated by default.

* Added support for a `--cache` flag taking one of the following values: `all`,
  `none`, `config`, `implicit`, and `analyze`. This flag provides a global
  cache configuration, replacing local configuration with the `lazy` keyword.
  The `config` and ` implicit` values use a `.config` file to provide a cache
  configuration on attribute level. The `analyze` value uses tracing together
  with full caching to analyze caching behavior and to compute a cache
  configuration via the `CacheAnalyzer` class.

* The Ant task `jastadd.JastAddTask` now prints a deprecation warning. The
  classname `org.jastadd.JastAddTask` should be used instead.

### Command-line Interface

* JastAdd now requires at least one grammar file (.ast) given on the command
  line.

* If there is any problem in the JastAdd configuration, JastAdd no longer
  prints the full help text. A single line is printed describing how to get
more help. This avoids the problem of the help text drowning out error
messages.

* Undeprecated the `--visitCheck` option (will replace `--noVisitCheck` in the
  future by adding a boolean argument)

* Undeprecated the `--cacheCycle` option (will replace `--noCacheCycle` in the
  future by adding boolean argument)

2.1.4 - 2013-09-04
------------------

* Fixed regression in version 2.1.3 that caused the generated `Tracer` class to
  always be generated in the default package. If it is generated, the `Tracer`
class is now generated in the same package as the rest of the AST.

* The generated methods `clone`, `copy`, and `fullCopy` can now be refined just
  like regular aspect methods.

* The undocumented `sons` declaration is now deprecated. This feature did not
  actually alter code generation in any way, but was still used in for example
JastAddJ. If the JastAdd parser encounters such a declaration it now generates
a warning.

* JastAdd no longer requires an implicit root node in the AST grammar. A root
  node is a non-abstract node that can not be a child of any other node type.
Root nodes are still required for collection attributes, so if you use a
collection attribute with a grammar that lacks an implicit root node, you must
declare the root node in the attribute declaration.

2.1.3 - 2013-08-27
------------------

* The warning messages for missing inherited equations have been improved by
  including an example AST path that lacks an equation for the attribute.

    An example of the new warning messages: "missing inherited equation for
attribute attr in class Expr when being child of X (path: Program->C->X->Expr)"

* JastAdd no longer generates the `Tracer` class when the `--tracing` option is
  absent.

* Disabled the generation of an unused interface named `Filter`. This interface
  was generated by the previous version of JastAdd as a side-effect of how the
`Tracer` class was generated.

2.1.2 - 2013-08-26
------------------

### Newly Deprecated

* **Interface Modifiers:** JastAdd strips all modifiers from interface
  declarations.  This stripping of access modifiers is probably an
unintentional error in the parser.

    In future versions of JastAdd the interface modifiers will be retained in
the generated code.  Unfortunately if incorrect modifiers are given to an
interface declaration in an aspect, then the generated code will fail to
compile.

    Note that the `protected`, `private` and `static` modifiers are only
allowed on member interfaces (declared inside an enclosing class). Top-level
interface declarations should not use `protected`, `private`, or `static`.

    JastAdd now prints warning messages for each interface declaration that
has any modifiers. Check that your interfaces use correct modifiers to ensure
that your code continues to compile even with future JastAdd versions.

* **Type Declaration Modifiers:** In addition to the stripping of modifiers
  from interface declarations JastAdd alters the modifiers of class and
interface declarations by removing any occurrence of `static` and adding the
`public` modifier if the modifier list was empty.

    We want to remove this behaviour from JastAdd, so a new warning message is
generated for all type declarations where the implicit `public` modifier is
added. The message warns that future versions of JastAdd will cease to make the
class `public` automatically.  To silence the warning, simply add `public` or
`protected` modifiers to the declaration.

* **SuppressWarnings:** JastAdd no longer generates `@SuppressWarnings`
  annotations. The purpose of using the annotation was to reduce the number of
Java warnings in the generated code - however JastAdd was not smart enough to
correctly use the annotation, and if the annotation is incorrectly used it will
raise another warning. Ideally the number of warnings should be reduced by
generating better code instead.

    The `--suppressWarnings` flag, and corresponding JastAddTask option, has
been deprecated and will be removed from JastAdd in a future release.

### Improvements and Additions

* **Renaming ASTNode, List, Opt:** New options have been added to JastAdd that
  allow renaming the generated `ASTNode`, `List` and `Opt` classes.  The names
can be specified using the following command-line flags (corresponding options
for the Ant task are also available):

    - `--ASTNode=<NewName>` ASTNode will be named "NewName"
    - `--List=<NewName>` List will be named "NewName"
    - `--Opt=<NewName>` Opt will be named "NewName"

* **Annotations:** Annotations are now supported for synthesized attributes and
  aspect-declared classes and interfaces!

    Annotations on attributes are allowed before the attribute keyword (`syn`,
`eq`). For synthesized attributes the annotation is generated on the method
with the same name - not the "compute" method.  Annotations are not yet
supported for other kinds of attributes although they should now produce more
readable error messages rather than standard mysterious parser syntax errors.

* **Tracing:** Attribute evaluation tracing is now done in-memory by the
  generated class `Tracer`.

* **Comments:** Fixed several issues with comments in the generated code:

    - fixed indentation issues
    - fixed double unparsing of documentation comments
    - removed redundant empty line after comments
    - removed incorrectly generated `@ast method` tag

* JastAdd now generates the utility method `List.addAll(Collection)` which can
  be used to initialize an AST List with a set of children.

* List components now get a "has" accessor which returns `true` if the list
  component is non-empty.

* Added flag `--lineColumnNumbers` which makes JastAdd generate methods to get
  and set start- and end positions for AST nodes. This can be used with version
1.0.2 and later of JastAddParser (by passing `--no-beaver-symbol` to
JastAddParser) to remove the need of having beaver.Symbol as a supertype of
ASTNode when Beaver is used as the parser generator. See the JastAddParser
documentation for further details.

### Internal Changes

* The syntax for annotating collection attributes has been slightly changed.
  Annotations are now only allowed before the `coll` keyword, rather than
after. The supported annotations for collection attributes are not discussed in
the documentation, so we don't expect this change to affect anyone. It is
possible that these annotations will disappear in the future anyway - use at
your own peril!

* Generated source files have been moved to the package org.jastadd

* The build script now compiles to ant-bin in order to avoid conflicts with the
  eclipse project

2.1.1 - 2013-07-29
------------------

### Bugfixes

* Fixed error that could cause `fullCopy` to initialize NTA children with
  incorrect child indices.
* The JastAdd Ant Task now throws a BuildException if JastAdd failed to
  generate an AST instead of silently terminating the Ant build.
* Fixed potential null access in generated code.


### Other

* Added a new List constructor that takes a variable number of children and
  inserts them into the list.
* Improved analysis and error handling of inherited attributes.
* JastAdd2 now uses a new version numbering system.

R20130412
---------

### General

* Added option for indentation type in the JastAdd Ant Task
* Moved JastAdd main class to the package `org.jastadd` (but left a deprecated
  class with the same name as a working entry point for backward compatibility)
* Removed global static variables to allow concurrent JastAdd instances in the
  same JVM
* Added JastAdd entry points that allow custom output/error streams

### Error handling

* Help text is printed to the standard out stream rather than the standard
  error stream
* Improved error handling for missing equations of synthesized attributes: now
  only the minimum set of classes requiring an equation is reported

### Performance

* Minimum list size now only affects `List` nodes (previously also affected
  `Opt` nodes)
* It is now possible to specify a custom minimum list size with the
  `minListSize` option

R20130312
---------

### Incremental evaluation

* Merged Emma S&ouml;derberg's incremental evaluation features into JastAdd2
* The new option `fullFlush` was added to enable more thorough flush behaviour
  (makes `flushCache` flush rewrites and NTAs)

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

* Multiple equivalent declarations of an inherited attribute now only raise a
  warning, rather than an error
* The warning for a duplicate inherited declaration includes the previous
  declaration location
* Improved error messages for method/equation refinement errors
* The path to the output directory is included in the error message for a
  missing output directory

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
* Added the `--indent` command-line option which allows changing the
  indentation in generated code. Can be set to tabs or 2-, 4-, and 8 space
indentation.

R20121112
---------

### Bugfix Release

* Fixed error in fullCopy causing some non-NTA children to not be copied
  correctly. This bug sometimes caused NullPointerExceptions or faulty
behaviour in generated code.

R20121026
---------

### Various bug fixes

* Fixed bug on Windows - backslashes in file paths now work correctly.
* NTA children are no longer copied by fullCopy.
* Removed the generated method for accessing value maps of parameterized
  attributes.
* Fixed error related to two synthesized attributes with the same name but
  different parameters.
* It is now possible to use static import statements in aspect files.
* Added a method getNumXNoTransform to count the number of elements in a list
  child without triggering rewrites.

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
* Circular attributes now generate Java 1.4 code when the java1.4 option is
  set.

### Improved output handling

* Error messages, warnings, and help messages are now printed on stderr instead
  of on stdout.
* JastAdd now returns exit code zero when just printing version or help
  information.

### Improved option handling

* The options --noComponentCheck, --lazyMaps, and --refineLegacy are now by
  default ON when running from the command line.
* The default behavior is now the same when running from the command line as
  when running through ANT.
* Command line options are no longer case sensitive.
* Some options are deprecated. A warning is printed if such an option is used.
* Warnings are printed for some other cases, like giving an option twice,
  giving an option without an expected argument, etc.

R20110902
---------

### JavaDoc support

* JastAdd now generates Java files with JavaDoc comments for built-in APIs and
  APIs for attributes. See the upcoming RagDoll tool for more information.

### JDK7 compliance

* Code generated by older versions of JastAdd might give compile time errors
  when compiling using javac from JDK 7. This is because JastAdd previously
generated code where private fields were accessed in an erroneous way according
to the JLS. Previous javac versions accepted such code, but the bug has been
fixed in JDK 7. This version of JastAdd generates code not giving this problem.

