# Reference manual for JastAdd2

## Index

* [Abstract Syntax](#AbstractSyntax)
    * [Predefined AST classes](#Predefined)
    * [Basic constructs](#Basic), [Naming](#Naming), [Tokens](#Tokens), [Class hierarchy](#Hierarchy), [Inheritance](#Inheriting), [NTAs](#ASTNTAs)
    * [Lists &amp; Opts](#ListsAndOpts), [Creating](#creationAPI), [Using JJTree](#JJTree)
* [Aspects](#Aspects)
    * [Aspect files](#jadd) (defined in .jadd and .jrag files)
    * [Supported AOP features](#AOPfeatures), [Differences from AspectJ](#AspectJ), [Idiom for aspect variables](#IdiomPrivate)
      
* [Attributes](#Attributes)
    * [Synthesized](#Synthesized), [inherited](#Inherited), [method syntax](#Method), [lazy/caching](#Lazy), [refine](#RefineAttr)
    * [Parameterized](#Parameterized), [broadcasting](#Broadcasting), [rewrites](#Rewrites), [circular](#Circular), [NTAs](#Nonterminal), [collections](#Collection)
* [Command line syntax](#Command)

# <a id="AbstractSyntax"></a>Abstract syntax

Abstract grammars are specified in .ast files and correspond to a
Java class hierarchy.

### <a id="Predefined"></a>Predefined Java classes in the hierarchy:

<TABLE BORDER=1>
<TR>
<TD>
<B>Predefined AST class</B>
</TD>
<TD>
<B>Description</B>
</TD>
<TD>
**Java API for <a id="untyped"></a>untyped traversal**
</TD>
</TR>
<TR>
<TD>
<PRE><a id="ASTNode"></a>ASTNode</PRE>
</TD>
<TD>
The topmost node in the hierarchy.

Supports traversal at the relatively "untyped" level of ASTNode.<br />
Children are numbered from 0 to getNumChild()-1.<br />
</TD>
<TD>
<PRE>class ASTNode extends Object implements Iterable {
  int getNumChild();
  ASTNode getChild(int);
  ASTNode getParent();
}</PRE>
</TD>
</TR>
<TR>
<TD>
<PRE><a id="List"></a>List</PRE>
</TD>
<TD>
Used to implement lists.
</TD>
<TD>
<PRE>class List extends ASTNode { }</PRE>
</TD>
</TR>
<TR>
<TD>
<PRE><a id="Opt"></a>Opt</PRE>
</TD>
<TD>
Used to implement optionals.

Has 0 or 1 child.
</TD>
<TD>
<PRE>class Opt extends ASTNode { }</PRE>
</TD>
</TR>
</TABLE>

*Note!* The traversal at this &quot;untyped&quot; level includes List and Opt nodes. See also &quot;[About Lists and Opts](#ListsAndOpts)&quot;.

<!--- TODO:
Example traversal of the complete AST:

<TABLE BORDER = 1>
<TR>
<TD>
**Java 1.4 style traversal**
</TD>
<TD>
**Java 5 style traversal**
</TD>
</TR>
<TR>
<TD>
<PRE>for (int k=0;k&lt;getNumChild()) {
  ...getChild(k)...
}</PRE>
</TD>
<TD>
<PRE>for (ASTNode n : this) {
  // access each child n here
}</PRE>
</TD>
</TR>
</TABLE>
-->

### <a id="constructs"></a>Abstract syntax constructs in the .ast file

<!--- TODO: Show List and Opt interfaces individually, before the mixed example. And add getBOpt and getCList ops. -->

<TABLE BORDER=1>
<TR>
<TD>
**<a id="Basic"></a>Basic constructs**
</TD>
<TD></TD>
<TD>
<B><a id="typedTraversalAPI">Java API for typed
traversal</a></B>
</TD>
</TR>
<TR>
<TD>
<PRE>abstract A;</PRE>
</TD>
<TD>
<a id="abstract"></a>A is an abstract class.<BR>
A corresponds to a nonterminal
</TD>
<TD>
<PRE>abstract class A extends ASTNode { }</PRE>
</TD>
</TR>
<TR>
<TD>
<PRE>B: A ::= ...;</PRE>
</TD>
<TD>
<a id="subclass"></a>B is a concrete subclass of A<BR>
B corresponds to a production of A
</TD>
<TD>
<PRE>class B extends A { }</PRE>
</TD>
</TR>
<TR>
<TD>
<PRE>C: A ::= A B C;</PRE>
</TD>
<TD>
C has three <a id="children"></a>children of types A, B, and C.

The API supports typed traversal of the children.
</TD>
<TD>
<PRE>class C extends A {
  A getA();
  B getB();
  C getC();</PRE>
</TD>
</TR>
<TR>
<TD>
<PRE>D: A;</PRE>
</TD>
<TD>
D has no children.

D corresponds to an <a id="empty"></a>empty production of A
</TD>
<TD>
<PRE>class D extends A { }</PRE>
</TD>
</TR>
<TR>
<TD>
<PRE>E: A ::= A &#91;B&#93; C* &lt;D&gt;;</PRE>
</TD>
<TD>
E has a child of type A, an optional child of<a id="optional"></a> type B, a
list of zero or more C children, and a token of type D.
</TD>
<TD>
<PRE>class E extends A {
  A getA();
  boolean hasB();
  B getB();
  int getNumC();
  C getC(int);
  String getD();
  setD(String);
}</PRE>
</TD>
</TR>
</TABLE>

<TABLE BORDER=1>
<TR>
<TD>
<B><a id="Naming"></a>Naming children</B>
</TD>
<TD></TD>
<TD></TD>
</TR>
<TR>
<TD>
<PRE>F: A ::= Foo:A Bar:B;</PRE>
</TD>
<TD>
It is possible to name children.
</TD>
<TD>
<PRE>class F extends A {
  A getFoo();
  B getBar();
}</PRE>
</TD>
</TR>
<TR>
<TD>
<PRE>G: A ::=
  A
  First:B
  C 
  Second:B
  D;</PRE>
</TD>
<TD>
*Note!* If there is more than one child of the same type, they *must* be named.
Here there are two children of type B. They are distinguished with the names
"First" and "Second".
</TD>
<TD>
<PRE>class G extends A {
  A getA();
  B getFirst();
  C getC();
  B getSecond();
  D getD();
}</PRE>
</TD>
</TR>
</TABLE>

<!--- TODO Add discussion of intrinsic attributes and inter-ast refs-->
<TABLE BORDER=1>
<TR>
<TD>
<B><a id="Tokens"></a>Typed tokens</B>
</TD>
<TD>
Tokens are implictly typed by String. But you can also give a token an explicit
type.
</TD>
<TD></TD>
</TR>
<TR>
<TD>
<PRE>A ::= &lt;T&gt;</PRE>
</TD>
<TD>
Here, T is a token of the Java type String.<BR> (The set method is intended to
be used only by the parser, to set the token value. This is useful when you are
using JavaCC with JJTree for parsing, since JJTree can construct the AST nodes
for you, but you will need to set the token values explicitly using the set
method.)
</TD>
<TD>
<PRE>class A extends ASTNode {
  String getT();
  setT(String);
}</PRE>
</TD>
</TR>
<TR>
<TD>
<PRE>A ::= &lt;T:String&gt;</PRE>
</TD>
<TD>
This is equivalent to the example above.
</TD>
<TD>
</TD>
</TR>
<TR>
<TD>
<PRE>A ::= &lt;T:int&gt;</PRE>
</TD>
<TD>
Here, T is a token of the Java primitive type int.
</TD>
<TD>
<PRE>class A extends ASTNode {
  int getT();
  setT(int);
}</PRE>
</TD>
</TR>
</TABLE>

<TABLE BORDER=1>
<TR>
<TD>
<B>Class <a id="Hierarchy"></a>hierarchy</B>
</TD>
<TD>
The class hierarchy can contain any number of levels.
</TD>
<TD></TD>
</TR>
<TR>
<TD>
For example:
</TD>
<TD>
<PRE>abstract A;
abstract B: A;
C: B;
D: C;</PRE>
</TD>
<TD>
<PRE>abstract class A extends ASTNode { }
abstract class B extends A { }
class C extends B { }
class D extends C { }</PRE>
</TD>
</TR>
</TABLE>

<TABLE BORDER=1>
<TR>
<TD>
<B><a id="Inheriting"></a>Inheriting children</B>
</TD>
<TD>

</TD>
<TD>

</TD>
</TR>
<TR>
<TD>
<PRE>abstract A ::= B C;
D: A;
E: A;</PRE>
</TD>
<TD>
Children are inherited by subclasses
</TD>
<TD>
<PRE>abstract class A extends ASTNode {
  B getB();
  C getC();
}
class D extends A { }
class E extends A { }</PRE>
</TD>
</TR>
<TR>
<TD>
<PRE>abstract A ::= B C;
D: A ::= F;</PRE>
</TD>
<TD>
Subclasses can add children to those specified by the
superclass.
</TD>
<TD>
<PRE>abstract class A extends ASTNode {
  B getB();
  C getC();
}
class D extends A {
  F getF();
}</PRE>
</TD>
</TR>
<TR>
<TD>
... example to be added ...
</TD>
<TD>
Subclasses can repeat/override children ... (to be explained)
</TD>
<TD> </TD>
</TR>
</TABLE>

<TABLE BORDER=1>
<TR>
<TD>
<B>Nonterminal attributes (<a id="ASTNTAs"></a>NTAs)</B>
</TD>
<TD> </TD>
<TD> </TD>
</TR>
<TR>
<TD>
<PRE>A ::= B C /D/</PRE>
</TD>
<TD>
A has three children: a B child, a C child, and a D child. The D child is an
NTA (nonterminal attribute): It is not created by the parser, but must be
defined by an equation. See [specifying NTAs](#Nonterminal).
</TD>
<TD>
<PRE>class A {
  B getB();
  C getC();
  D getD();
}</PRE>
</TD>
</TR>
</TABLE>

### About Lists and Opts<a id="ListsAndOpts"></a>

The AST representation has additional Opt and List nodes in order
to implement children that are optionals or lists. By using hasX(), getX(), and getX(int), you need not access the Opt and List nodes to traverse the AST. But the Opt and List nodes can be accessed by getXOpt() and getXList(), making it convenient to iterate over the optional/list children. See examples below. Note also that if you use the untyped traversal methods
(getChild(), etc.), you will stop also on the Opt and List nodes.

<TABLE BORDER=1>
<TR>
<TD>
<B>.ast construct</B>
</TD>
<TD>
<B>Java API</B>
</TD>
<TD>
<B>Example use</B>
</TD>
</TR>
<!--- TODO: Check Opt in Test case before showing example here.
<TR>
<TD>
<PRE>A ::= &#91;B&#93;</PRE>
</TD>
<TD>
<PRE>class A extends ASTNode {
  ...
  Opt getBOpt();
}</PRE>
</TD>
<TD>
<PRE>A a = ...;
for (B b : getBOpt()) {
  // Access b (the optional child).
}
</PRE>
</TD>
</TR>
-->
<TR>
<TD>
<PRE>C ::= D*</PRE>
</TD>
<TD>
<PRE>class C extends ASTNode {
  ...
    List getDList();
}</PRE>
</TD>
<TD>
<PRE>C c = ...;
for (D d : getDList()) {
  // Access each d (list children).
}</PRE>
</TD>
</TR>
</TABLE>

### <a id="creationAPI"></a>Creating AST nodes

Use the following constructor API to create the AST. Typically you create the
AST in the action routines of your parser.  But you can of course also create
an AST by coding it explicitly, e.g., in a test case. If you use JavaCC+JJTree,
see below.

<TABLE BORDER=1>
<TR>
<TD>
<B>AST class</B>
</TD>
<TD>
<B>Java API for creating AST nodes</B>
</TD>
</TR>
<TR>
<TD>
<PRE>A ::= B C &#91;D&#93; E* &lt;G&gt;</PRE>
</TD>
<TD>
<PRE>class A extends ASTNode {
  A(B, C, Opt, List, String); //Arguments must not be null
}</PRE>
</TD>
</TR>
<TR>
<TD>
<PRE>List</PRE>
</TD>
<TD>
<PRE>class List extends ASTNode {
  List();
  add(ASTNode); // Returns the same List object
}</PRE>
</TD>
</TR>
<TR>
<TD>
<PRE>Opt</PRE>
</TD>
<TD>
<PRE>class Opt extends ASTNode {
  Opt();
  Opt(ASTNode); // The argument may be null
}</PRE>
</TD>
</TR>
</TABLE>

### Building ASTs using <a id="JJTree"></a>JJTree

If you use JJTree, the creational code is generated. You use the "#X" notation
in the JJTree specification to guide the node creation.

JJTree maintains a stack of created nodes. The "#X" notation means:

* create a new object of type X
   
* pop the nodes that were created during this parse method and
insert them as children to the new X node
   
* then push the new X node

You need to explicitly create List and Opt nodes. When the parsing structure
does not fit the abstract tree, e.g. when parsing expressions, you need to use
some additional tricks. You also need to set token values explicitly. An
example is available in [assignment
3](http://cs.lth.se/kurs/eda180_compiler_construction/assignments/3_tree_building/)
in the Lund University compiler course. Download the example zip file and look
at the file CalcTree/specification/Parser.jjt .

### <a id="Aspects"></a>Aspects

JastAdd aspects support <I>intertype declarations</I> for AST classes. An
intertype declaration is a declaration that appears in an aspect file, but that
actually belongs to an AST class. The JastAdd system reads the aspect files and
weaves the intertype declarations into the appropriate AST classes.

The kinds of intertype declarations that can occur in an aspect include
ordinary Java declarations like methods and fields, and attribute grammar
declarations like attributes, equations, and rewrites.

An aspect file can contain import declarations and one or more aspects, e.g.:

    import java.lang.util.*;
    aspect A {
      abstract public void Stmt.m();
      public void WhileStmt.m() { ... }
      public void IfStmt.m() { ... }
      ...
    }
    aspect B {
      private boolean Stmt.count = 0;
    }

The aspect syntax is similar to AspectJ. But in contrast to AspectJ, the
aspects are not real language constructs. The JastAdd system simply reads the
aspect files and inserts the intertype declarations into the appropriate AST
classes. For example, the method "m" and its implementations are inserted into
classes Stmt, WhileStmt, and IfStmt. And the declaration of the field "count"
is inserted into the class Stmt. Import declarations are inserted into all AST
classes for which there are intertype declarations in the aspect. So, the
import of java.lang.util.* is inserted into Stmt, WhileStmt, and IfStmt. For a
more detailed discussion on the similarities and differences between JastAdd
aspects and AspectJ, see [below](#AspectJ).

The aspect names, e.g., A and B above, do not show up in the woven Java code.
They can be regarded simply as a way to name the purpose of the aspect, for
readability. The aspect names are also used for controlling the order of refine
declarations.

### <a id="jadd"></a>.jadd and .jrag files

An aspect file can have the suffix .jadd or .jrag. The JastAdd system does not
differ between these two types of files, but we recommend the following use:

* Use .jrag files for declarative aspects, i.e., where you add
attributes, equations, and rewrites to the AST classes
   
* Use .jadd files for imperative aspects, i.e., where you add
ordinary fields and methods to the AST classes


It is perfectly fine to not follow this convention, i.e., to mix both
imperative and declarative features in the same aspect, but we try to follow
the convention in our examples in order to enhance the readability of a system.

### <a id="example"></a></a>Example <a id="imperative"> imperative aspect (.jadd)

Here is an example <em>imperative</em> aspect that adds pretty printing
behavior to some AST classes. Typically, this file would be named
PrettyPrinter.jadd:

    aspect PrettyPrinter {
      void ASTNode.pp(String indent) { }
      void WhileStmt.pp(String indent) {
         System.out.println(indent + "while " + getExp().pp(indent + "   ") + " do");
            getStmt().pp(indent + "   ");
      }
      void IfStmt.pp(String indent) { ... }
      ...
    }

### Example <a id="declarative"></a>declarative aspect (.jrag)

Here is an example <em>declarative</em> aspect that adds type checking to some
AST classes. Typically, this file would be named TypeChecking.jrag:

    import TypeSystem.Type;
    aspect TypeChecking {
      syn Type Exp.actualType();
      inh Type Exp.expectedType();
      eq LogicalExp.actualType() = Type.boolean();
      eq IdUse.actualType() = decl().getType();
      ...
      eq WhileStmt.getExp().expectedType() = Type.boolean();
      syn boolean Exp.typeError() = ! (actualType().equals(expectedType());
    }

### Supported AOP <a id="AOPfeatures"></a>features

<TABLE BORDER=1>
<TR>
<TD>
<B>Feature</B>
</TD>
<TD>
<B>Comment</B>
</TD>
</TR>
<TR>
<TD>
intertype declaration of AST fields, methods, and
constructors.
</TD>
<TD>
See the prettyprinting example above. The declarations are inserted into the
corresponding AST classes by the AST weaver. Any modifiers (public, private,
static, etc.), are interpreted in the context of the AST class. I.e., not as in
AspectJ where the public/private modifiers relate to the aspect.
</TD>
</TR>
<TR>
<TD>
intertype declaration of attributes, equations, and
rewrites
</TD>
<TD>
See the type checking example above. For more details,
[Attributes](#Attributes).

Note that access modifiers (public, private, etc.) are not supported for
attributes. All declared attributes generate public accessor methods in the AST
classes.
</TD>
</TR>
<TR>
<TD>
declare additional interfaces for AST classes
</TD>
<TD>
E.g., in an aspect you can write

<PRE>WhileStmt implements LoopInterface;</PRE>

This will insert an "implements LoopInterface" clause in
the generated WhileStmt class.
</TD>
</TR>
<TR>
<TD>
declare classes and interfaces in an aspect
</TD>
<TD>
E.g., in an aspect you can write

<PRE>interface I { ... }
class C { ... }</PRE>

This is equivalent to declaring the interface and class
in separate ordinary Java files. The possibility to declare
them inside an aspect is just for convenience.
</TD>
</TR>
<TR>
<TD>
<a id="refine"></a>refine a method declared in another aspect
(This feature is available in JastAdd version R20051107 and later.)
</TD>
<TD>
Often, it is useful to be able to replace or refine methods declared in
another aspect.  This can be done using a "refine" clause. In the following
example, the aspect A declares a method m() in the class C. In the aspect B,
the method is replaced, using a "refine" clause. This is similar to overriding
a method in a subclass, but here the "overridden" method is in the same class,
just defined in another aspect.  Inside the body of the refined method, the
original method can be called explicitly.  This is similar to a call to super
for overriding methods.

<PRE>aspect A {
  void C.m() { ... }
}

aspect B {
  refine A void C.m() { // similar to overriding
    ...
    refined(); // similar to call to super
    ...
  }
}</PRE>
         
Note that the refine clause explicitly states which aspect is refined (A in
this case).  Additional aspects may further refine the method. For example, an
aspect C can refine the method refined in B.

The original method can be called using the keyword "refined". JastAdd2
replaces all occurrences of this keyword with the new name of the refined
method. Be careful with how you use refined - even occurrences in string
literals or comments are replaced!  With the command line flag "--refineLegacy"
you can use the legacy syntax for calling the refined method:

<PRE>aspect A {
  void C.m() { ... }
}

aspect B {
  refine A void C.m() { // similar to overriding
    ...
    AspectFile.C.m();
    ...
  }
}</PRE>

*Note:* AspectFile is the name of the file (minus extension) that contains the
original declaration.
</TD>
</TR>
</TABLE>

### <a id="AspectJ"></a>Similarities and differences from AspectJ

The aspect concept in JastAdd was developed in parallel to the AspectJ
development, and we have gradually adopted the AspectJ syntax, for  features
that are similar.  The important similarity between JastAdd aspects and AspectJ
aspects is the intertype declarations. In addition, JastAdd aspects support
attribute grammar features which AspectJ does not. Note, however, that JastAdd
supports intertype declarations only for the AST classes, not for classes in
general like AspectJ. There are many other features of AspectJ that are not
supported in JastAdd, e.g.:

* Fields and methods private to an aspect are not
   supported.
   
* Declaration of additional parent classes is not
   supported.
   
* Dynamic features like AspectJ's pointcuts or advice are not
   supported.
</UL>

### <a id="IdiomPrivate"></a>Idiom for private fields and methods

As mentioned, JastAdd does not support fields and methods that are private to
an aspect. As a workaround idiom, such fields and methods can be implemented as
(non-private) static fields and methods in class ASTNode. As an example,
consider the pretty printer. We might want to parameterize the prettyprinter so
that it can pretty print on any PrintStream object and not only on System.out.
Here is how you could write this in AspectJ and the corresponding JastAdd
implementation:

<TABLE BORDER=1>
<TR>
<TD>
<B>AspectJ code</B>
</TD>
<TD>
<B>JastAdd code</B>
</TD>
</TR>
<TR>
<TD>
<PRE>aspect PrettyPrinter {
  private PrintStream ppStream = null;
  public void prettyprint(ASTNode n, PrintStream s) {
    ppStream = s;
    n.pp("");
    ppStream = null;
  }
  void ASTNode.pp(String indent) { }
  void WhileStmt.pp(String indent) {
    ...
    ppStream.println(...);
    ...
  }
  ...
}</PRE>
</TD>
<TD>
<PRE>aspect PrettyPrinter {
  static PrintStream ASTNode.ppStream = null;
  public void ASTNode.prettyprint(PrintStream s) {
    ppStream = s;
    pp("");
    ppStream = null;
  }
  void ASTNode.pp(String indent) { }
  void WhileStmt.pp(String indent) {
    ...
    ppStream.println(...);
    ...
  }
  ...
}</PRE>
</TD>
</TR>
</TABLE>

<h2><a id="Attributes"></a>Attributes</h2>

Attributes are specified in [JastAdd aspect files](#Aspects).

### <a id="Basic"></a>Basic attribute mechanisms

<TABLE BORDER=1>

<TR>
<TD WIDTH=266>
<B><a id="Synthesized"></a>Synthesized attributes</B>
</TD>
<TD>

</TD>
</TR>
<TR>
<TD WIDTH=266>
<PRE>syn T A.x();</PRE>
</TD>
<TD>
x is a synthesized attribute in class A and of type T.

There must be equations defining x in A (if A is concrete) or in all concrete
subclasses of A (if A is abstract).

*Note!* Synthesized attributes are conceptually equivalent to abstract
virtual functions (without side-effects). The main difference is that their
values may be cached (see below). They can be accessed in the same way as
virtual functions. I.e., the declaration generates the following Java API:

<PRE>T A.x();</PRE>
</TD>
</TR>
<TR>
<TD WIDTH=266>
<PRE>eq A.x() = <I>Java-expr;</I></PRE>
</TD>
<TD>
The equation defines the value of the synthesized attribute x of AST nodes of
type A.

The Java-expression that defines the value must be free from externally visible
side-effects. The context of the expression is the class A, and any part of the
class A's API may be used in the computation, including accesses to other
attributes.

*Note!* Equations defining synthesized attributes are conceptually
equivalent to virtual method implementations (without side-effects).
</TD>
</TR>
<TR>
<TD WIDTH=266>
<PRE>eq B.x() = <I>Java-expr;</I></PRE>
</TD>
<TD>
Suppose B is a subclass to A. This equation overrides the
corresponding (default) equation for A.x().

*Note!* This is equivalent to overriding method
implementations.
</TD>
</TR>
<TR>
<TD WIDTH=266>
<B><a id="Shorthand"></a>Shorthand for synthesized
attributes</B>
</TD>
<TD></TD>
</TR>
<TR>
<TD WIDTH=266 HEIGHT=70>
<PRE>syn T A.x() = <I>Java-expr;</I></PRE>
</TD>
<TD HEIGHT=70>
The declaration of a synthesized attribute and the
(default) equation for it can be written in one clause. So
the clause to the left is equivalent to:

<PRE>syn T A.x();
eq A.x() = <I>Java-expression;</I></PRE>
</TD>
</TR>
</tbody>
</TABLE>

<TABLE BORDER=1>
<tbody>
<TR>
<TD WIDTH=266>
<B><a id="Inherited"></a>Inherited attributes</B>
</TD>
<TD>

</TD>
</TR>
<TR>
<TD WIDTH=266>
<PRE>inh T A.y();</PRE>
</TD>
<TD>
y is an inherited attribute in class A and of type T.
There must be equations defining y in all classes that have
children of type A. If a class has several children of type
A, there must be one equation for each of them.

Inherited attributes can be accessed in the same way as
synthesized attributes. I.e., the declaration generates the
following Java API:

<PRE>T A.y();</PRE>

<I>Note! </I>Inherited attributes differ from ordinary
virtual functions in that their definitions
(equations/method implementations) are located in the
<I>parent</I> AST node, rather than in the node itself.

<I>Note!</I> The concept of <I>inherited</I> attributes
in this Attribute Grammar sense is completely different from
object-oriented inheritance. Both attribute grammars and
object-orientation were invented in the late 60's and the
use of the same term "inheritance" is probably a mere
coincidence: In AGs, inheritance takes place between nodes
in a syntax tree. In OO, inheritance takes place between
classes in a class hierarchy.
</TD>
</TR>
<TR>
<TD WIDTH=266>
<PRE>eq C.getA().y() = <I>Java-expr;</I></PRE>
</TD>
<TD>
This equation defines the value of the inherited attribute
y() of the A child of a C node.

<I>Note! </I>The Java-expression executes in the context of
C.

<I>Note!</I> The equation is similar to a method
implementation.

<I>Note!</I> The equation actually applies to all inherited
attributes y in the subtree rooted at A, provided that they
declare the y attribute. See below under broadcast
attributes.
</TD>
</TR>
<TR>
<TD WIDTH=266>
<PRE>eq D.getA().y() = <I>Java-expr</I>;</PRE>
</TD>
<TD>
Suppose D is a subclass of C. In this case, the equation
overrides the previous one.

<I>Note!</I> This is analogous to overriding a virtual
method implementation.
</TD>
</TR>
</tbody>
</TABLE>

<TABLE BORDER=1>
<tbody>
<TR>
<TD WIDTH=266>
<B><a id="Method"></a>Method syntax</B>
</TD>
<TD>

</TD>
</TR>
<TR>
<TD WIDTH=266>
<PRE>syn T A.x() {
  ...
    return <I>Java-expr</I>;
}</PRE>
</TD>
<TD>
It is possible to write the computation of an attribute value as a method body
instead of as a single expression.  This may be convenient when the computation
is complex.  Inside the method body it is possible to use ordinary imperative
Java code with local variables, assignments, loops, etc. However, the net
result of the computation must not have any side-effects. (Currently, JastAdd
does not check the absence of such side-effects, but future versions might do
so.)
</TD>
</TR>
<TR>
<TD WIDTH=266>
**<a id="Lazy"></a>Lazy attributes**<br /> (cached attributes)
</TD>
<TD>
An attribute can be declared <em>lazy</em> in order to speed up the evaluation.
An attribute that is declared lazy will automatically have its value is cached
after the first access to it. The next time the attribute is accessed, the
cached value is returned directly. We recommend that attributes that are
expensive to compute and that are accessed multiple times should be declared
lazy. For example, declaration bindings and type attributes are good candidates
for caching. JastAdd has facilities for automatically computing good cache
configurations based on profiling, but this is not yet documented here.
</TD>
</TR>
<TR>
<TD WIDTH=266>
<PRE>syn lazy A.x();</PRE>
</TD>
<TD>
Here, the attribute x of class A is declared lazy.
</TD>
</TR>
<TR>
<TD WIDTH=266>
<B><a id="RefineAttr"></a>Refine attribute</B>
</TD>
<TD> </TD>
</TR>
<TR>
<TD WIDTH=266>
<PRE>refine S eq B.x() = <I>Java-expr;</I></PRE>
</TD>
<TD>
Equations defined in one aspect can be refined in another aspect, in the same
way as methods can be refined, see [JastAdd aspect files](#Aspects).  In this
example, the equation replaces the corresponding equation declared in the
aspect S. The value from the original equation in S can be accessed by the
expression S.B.x() (This feature is available in JastAdd version R20051107 and
later.)
</TD>
</TR>

</tbody>
</TABLE>


### <a id="Parameterized"></a>Parameterized attributes

<TABLE BORDER=1><tbody>
<TR>
<TD WIDTH=266>
<B>Parameterized attributes</B>
</TD>
<TD>
Attributes can have parameters. This is a bit unusual for attribute grammars,
but a natural generalization when you view attributes as virtual functions.
</TD>
</TR>
<TR>
<TD WIDTH=266>
<PRE>syn T A.x(int a);
eq A.x(int a) {
  return <I>Java-expr</I>;
}</PRE>
</TD>
<TD>
Here, x is a parameterized synthesized attribute. The equation is similar to a
method implementation and the argument values can be used in the computation of
the resulting value.
</TD>
</TR>
<TR>
<TD WIDTH=266>
<PRE>inh T A.y(int a);
eq C.getA().y(int a) {
  return <I>Java-expr</I>;
}</PRE>
</TD>
<TD>
Here, y is a parameterized inherited attribute. The equation executes in the
context of C and can in addition access the arguments (a in this case).
</TD>
</TR></tbody>
</TABLE>

### <a id="Broadcasting"></a>Broadcasting inherited attributes

<TABLE BORDER=1><tbody>
<TR>
<TD WIDTH=266>
<B>Broadcasting inherited attributes</B>
</TD>
<TD>
Often, an inherited attribute is used in a number of places in a subtree. If
basic inherited attributes are used, the value needs to be copied explicitly
using inherited attributes in all the intermediate nodes. For convenience,
JastAdd supports another technique, namely broadcasting of an inherited
attribute to a complete subtree. An equation defining an inherited attribute
actually broadcasts the value to the complete subtree of the child. By using
this technique, no explicit copy attributes are needed.
</TD>
</TR>
<TR>
<TD WIDTH=266>
<PRE>eq C.getA().y() = ...;
inh T A.y();</PRE>
</TD>
<TD>
Here, the equation defines an inherited attribute y() declared in the A child
of a C node. This equation actually applies not only to the inherited y()
attribute of the A child, but to all inherited y() attributes in the whole
subtree of A. In order to for a node N in the subtree to access y(), the
attribute must, however, be exposed by declaring y() as an inherited attribute
of N.
</TD>
</TR>
<TR>
<TD WIDTH=266>
<PRE>inh T B.y();</PRE>
</TD>
<TD>
Here, the attribute y() is exposed in B by declaring it as an inherited
attribute there. If there is a B node that is in the subtree rooted at the A
that is a child of a C node, then the equation above will apply.
</TD>
</TR>
<TR>
<TD WIDTH=266 HEIGHT=22>
<B>Overruling broadcast definitions</B>
</TD>
<TD HEIGHT=22>
A broadcast definition of an attribute a() applies to all nodes in a subtree
rooted by N. If, however, there is a node in the subtree which has another
equation that defines a() for a child M, that equation will take precedence for
defining a() in M and its subtree.
</TD>
</TR>
<TR>
<TD WIDTH=266>
<B>Differentiating between children in a list</B>
</TD>
<TD>
When defining an inherited attribute of a child node that
is an element of a list, it is sometimes useful to know
which index the child node has. This can be done as
follows:
</TD>
</TR>
<TR>
<TD WIDTH=266>
<PRE>C ::= E*;
eq C.getE(int index).y() = ...index...</PRE>
</TD>
<TD>
Here, a C node has a list of E children. When defining the y() attribute of a
given (subtree of an) E child, the value might depend on the index of the
child. For example, if the E nodes are actual arguments of a procedure, we
might want to pass down the expected type of each argument.

The example equation shows how to declare the index as a parameter of the
getE() method, and to access the index in the equation body.
</TD>
</TR></tbody>
</TABLE>

### <a id="Rewrites"></a>Rewrites

<TABLE BORDER=1><tbody>
<TR>
<TD>
<B><a id="Unconditional"></a>Unconditional rewrite
rule</B>
</TD>
<TD> </TD>
</TR>
<TR>
<TD>
<PRE>rewrite A {
  to B {
    ...
    return <I>exp</I>;
  }
}</PRE>
</TD>
<TD>
An A node will be replaced by the node specified in the Java expression
<I>exp</I>. This will happen as soon as the A node is accessed (by a get()
method from its parent), so if you traverse the tree you will only be able to
access the final rewritten nodes.

A and B must be AST classes.

The <I>exp</I> must be of type B.

Let the set S be the superclasses of A (including A) that occur on right-hand
sides of productions in the abstract syntax. B must be a subclass of all
classes in S. This guarantees that replacing an A node by a B node does not
break the rules in the abstract syntax.

The code in the body of the rewrite may access and rearrange the nodes in the
subtree rooted at A, but not any other nodes in the AST. Furthermore, the code
may not have any other side effects.
</TD>
</TR>
<TR>
<TD>
<B><a id="Conditional"></a>Conditional rewrite
rule</B>
</TD>
<TD>

</TD>
</TR>
<TR>
<TD>
<PRE>rewrite A {
  when ( <I>condition</I> )
    to B {
      ...
      return <I>exp</I>;
    }
}</PRE>
</TD>
<TD>
The conditional rewrite works in the same way as the unconditional one, but
performs the replacement only if the boolean expression <I>condition</I> is
true. The condition may access anything in the AST, e.g., attributes, other
tree nodes and their attributes, etc.
</TD>
</TR>
<TR>
<TD>
<B><a id="Iterative"></a>Iterative rewriting</B>
</TD>
<TD>
After a node has been replaced according to a rewrite rule, all conditional
rewrite rules are checked again, and a new rewrite may be performed. This is
iterated until no rule conditions hold.
</TD>
</TR>
<TR>
<TD>
<B><a id="Order"></a>Order of rewriting</B>
</TD>
<TD>
At each iteration of rewriting, the rule conditions are evaluated in a certain
order. The first condition that is true is used for rewriting in that
iteration. The order in which rule condition evaluation occurs is the
following:

* conditions in superclasses are evaluated before conditions in subclasses

* conditions within an aspect file are evaluated in lexical order

* conditions in different aspect files are evaluated in the order the files are
listed in the jastadd command.
</TR>
<TR>
<TD>
**<a id="Confluency"></a>Confluency**
</TD>
<TD>
If the order of rewriting of a node does not effect the final result, the rules
are said to be *confluent*.  This is highly desirable, since it makes the
specification more readable to not have to take lexical order of rules into
account. However, JastAdd cannot check that the rules are confluent. In cases
where several conditions for a node are true at the same time, we recommend
that you contemplate the rules and try to find out if they could be
non-confluent. In that case, we recommend you to refine the conditions so that
only one can apply at a time. This makes your specification independent of
lexical order. Note that it is often useful to have several different rules
that apply at the same time for a given node, but which are confluent.
</TD>
</TR>
<TR>
<TD>
<B><a id="ShorthandRewrite"></a>Shorthand
notation</B>
</TD>
<TD> </TD>
</TR>
<TR>
<TD>
If you have several conditional rewrite rules, you may write them together. So,
e.g., writing

<PRE>rewrite A {
  when ( <I>condition-1</I> )
  to B {
    ...
    return <I>exp-1</I> 
  }
  when ( <I>condition-2</I> )
  to C {
    ...
    return <I>exp-2</I> 
  }
}</PRE>
</TD>
<TD>
... is equivalent to:

<PRE>rewrite A {
   when <I>condition-1</I> 
   to B {
      ...
      return <I>exp-1</I> 
   }
}
rewrite A {
  when <I>condition-2</I> 
  to C {
    ...
    return <I>exp-2</I> 
  }
}</PRE>
</TD>
</TR>
<TR>
<TD>
Sometimes you don't need a block for computing the resulting node. It may be
sufficient with an expression. In that case, you may simply write the
expression instead of the block, e.g., as follows:

<PRE>rewrite A {
  when ( <I>condition-1</I> )
  to B <I>exp-1</I> 
  when ( <I>condition-2</I> )
  to C <I>exp-2</I> 
}</PRE>
</TD>
<TD>
... which is equivalent to

<PRE>rewrite A {
   when ( <I>condition-1</I> )
   to B { return <I>exp-1</I> }
   when ( <I>condition-2</I> )
   to C { <I>return exp-2</I> }
}</PRE>
</TD>
</TR></tbody>
</TABLE>

### <a id="Circular"></a>Circular attributes

<TABLE BORDER=1><tbody>
<TR>
<TD>
<B>Circular attributes</B>
</TD>
<TD>
Attributes can be circularly defined. I.e., the value of the attribute can
depend (indirectly) on itself. Circular attributes are evaluated iteratively,
starting with a start value given in the declaration of the attribute. The
evaluation stops when the value equals that for the previous iteration.

Circular attributes are always cached. They do not need to be declared "lazy".

If a lazy attribute is circular, but not declared as such, this will be
detected at runtime, and an exception will be generated.<BR> To be sure that
the evaluation of circular attributes will converge, the values should be
arranged into lattices of finite height, the bottom values should be used as
starting values, and each equation on the cycle should be monotonic with
respect to the lattices.
</TD>
</TR>
<TR>
<TD>
<PRE>syn T A.x(int a) circular &#91;<span style="font-style: italic;">bv</span>&#93;;
eq A.x(int a) = <span style="font-style: italic;">rv</span>;</PRE>
</TD>
<TD>
Here, the attribute x is a circular attribute. The starting value is <span
style="font-style: italic;">bv</span> (a Java expression).

The equation defines x as having the value computed by the Java expression
`rv`.  Note that `rv` may depend (directly or indirectly) on x.
</TD>
</TR></tbody>
</TABLE>

### <a id="Nonterminal"></a>Nonterminal attributes

<TABLE BORDER="1">
<tr>
<td>
**Newer syntax**

<pre>syn nta C A.anNTA() = new C();</pre>
</td>
<td>
Nonterminal attributes (NTAs) are nodes in the AST. Whereas normal AST nodes
are built by the parser, the NTAs are viewed as attributes and are defined by
equations.

*   NTAs can be inherited or synthesized.

*   The value in the equation should be a freshly built AST subtree. It should
    be complete in the sense that all its children should also be freshly
    created nodes (i.e., they are not allowed to be initialized to null).

*   The NTA can itself have attributes that can be accessed like normal
    attributes.

*   If the NTA has inherited attributes, there must be equations for those
    attributes in some ancestor, as for normal children.

</td>
</tr>
<tr>
<td>
**Older syntax**
</td>
<td>
In the older syntax, you introduce a nonterminal attribute as follows:

*   Declare the NTA in the ast file, (See also [NTAs in the abstract syntax](#ASTNTAs)).

*   Declare the NTA as an attribute in a jrag file. It can be declared as a
    synthesized or an inherited attribute.
    The name of the attribute should be the same as in the
    [AST traversal API](#typedTraversalAPI), e.g., <em>getX</em> if the NTA
    is called <em>X</em>.

*   Add equations defining the NTA. The defining value should be a new AST of
    the appropriate type, created using the [AST creation API](#creationAPI).

Note that if the NTA is a List or an Optional node, you need to create the
appropriate AST with a List or an Opt node as its root. See examples below.
</td>
</tr>
<tr>
<td>
**Simple synthesized NTA**
</td>
<td> </td>
</tr>
<tr>
<td>
*In an .ast file:*

<pre>A ::= B /C/;</pre>
<em>In a .jrag file:</em>
<pre>syn C A.getC() = new C();</pre>
</td>
<td>
The NTA *C* is declared in the .ast file. It is then declared as a
synthesized attribute *getC()* in the .jrag file.  The equation is
provided directly in the declaration and creates a new *C* node.
</td>
</tr>
<tr>
<td>
**List NTA**
</td>
<td> </td>
</tr>
<tr>
<td>
*In an .ast file:*

<pre>A ::= B /C*/;</pre>

*In a .jrag file:*

<pre>syn C A.getCList() =
new List().
add(new C()).
add(new C());</pre>
</td>
<td>
The list NTA *C* is declared in the .ast file. It is then declared as a synthesized attribute *getCList()*
(the same name as in the [implementation level traversal API](#ListsAndOpts)) in the .jrag file.
The equation is provided directly in the declaration and creates a <em>List</em> node to which is added a number of
<em>C</em> nodes (two in this example).

</td>
</tr>
</TABLE>

### <a id="Collection"></a>Collection attributes

<TABLE BORDER="1">
<tr>
<td>
**Collection attributes**
</td>
<td>
Collection attributes have composite values that are defined by so called
<em>contributions</em> that each add a small piece to the composite value. The
contributions may be located in any nodes in the AST.
</td>
</tr>
<tr>
<td>
**Collection attribute declaration**

<pre>coll T A.c() [fresh] with m;</pre>
</td>
<td>
This example shows a declaration of the collection attribute c in nodetype A, and with type T.

*   Within square brackets, a Java expression, <em>fresh</em>, should be
    written that provides a fresh object of type T. For example, new T().

*   The composite value will be built by the underlying JastAdd machinery by
    calling the method m for each contribution.

*   The method m, should be a one-argument method of T.

*   The method m should mutate the T object by adding a contribution to it.

*   The method m should be commutative, in the sense that the order of calling
    m for different contributions should yield the same resulting T value.
</td>
</tr>
<tr>
<td>
**Contribution declaration**

<pre>N1 contributes value-exp
  when cond-exp
  to N2.a()
  for N2-ref-exp;</pre>
</td>
<td>
This declares that the nodetype N1 contributes <em>value-exp</em> to a
collection attribute N2.a(), or more precisely, to the attribute a in the N2
object denoted by the expression <em>N2-ref-exp</em>.

*   N<em>2-ref-exp</em> should be a Java expression denoting an object of type N2.

*   The *value-exp* should be a Java expression of the argument type of the method m.

*   The contribution is only applied when the boolean condition *cond-exp* is
    true.

*   The "when"-clause may be omitted.
</td>
</tr>
<tr>
<td>
**Contributions to a set of collections**

<pre>N1 contributes value-exp
  to N2.a()
  for each N2-ref-set-exp;</pre>
</td>
<td>
A contribution declaration can define that a certain value is contributed to
a whole set of collection attributes by using the "for each" form.

*   N2-ref-set-exp should denote an object of the Java type java.lang.Iterable, which should contain objects of type N2.

*   The contribution is added to each of these N2 objects.

</td>
</tr>
<tr>
<td>
**Scoped collections**

<pre>coll N1.a [fresh] with m root R</pre>
</td>
<td>
  A collection may be scoped to a certain subtree of the AST. This means that
only contributions inside that subtree will be applied when constructing the
collection value.

*   If R is the same nodetype as N1 (or a subtype of N1), then the subtree root will be the N1 object.

*   If R is some other nodetype, the subtree root will be the closets object of type R on the way from N1 towards the AST root.
</td>
</tr>
</table>

## <a id="Command"></a>Running JastAdd from the command line

### Synopsis

<PRE>
  java -jar jastadd2.jar <I>options arguments</I>
</PRE>

### Options

<PRE>
  --help <I>(prints help text and stops)</I>
  --version <I>(prints version information and stops)</I>
  --package=PPP <I>(optional package for generated files, default is none)</I>
  --o=DDD <I>(optional base output directory, default is current directory)</I>
  --beaver <I>(use beaver base node)</I>
  --jjtree <I>(use jjtree base node, this requires --grammar to be set)</I>
  --grammar=GGG <I>(the parser for the grammar is called GGG, required when using jjtree)</I>
  --rewrite <I>(enable ReRAGs support)</I>
  --novisitcheck <I>(disable circularity check for attributes)</I>
  --noCacheCycle <I>(disable cache cyle optimization for circular attributes)</I>
  --java1.4 <I>(generate Java 1.4 source code, rather than Java 5)</I>
</PRE>

### Arguments

Names of .ast, .jrag and .jadd source files.

### Example

  The following command generates classes according to the AST description in Toy.ast.
  The generated classes are placed in the package ast. The specifications in the jrag and jadd
  files are translated and woven into the generated classes.

<PRE>
  java -jar jastadd2.jar --package=ast Toy.ast \
    NameAnalysis.jrag TypeAnalysis.jrag PrettyPrinter.jadd
</PRE>

### ANT task

The options above are also available in an ANT task. For generating Java 1.4
source code, write "java14=true" (without the decimal point). For the other
options, use the same names as above.
