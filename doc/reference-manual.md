Reference Manual for JastAdd 2.2.0
==================================

[Click here](/releases/jastadd2/2.1.13/reference-manual.php) to read the
JastAdd 2.1.13 manual.

## Index

* [Syntax overview](#Cheatsheet)
* [Abstract Syntax](#AbstractSyntax)
    * [Predefined AST classes](#Predefined)
    * [Basic constructs](#Basic), [Naming](#Naming), [Tokens](#Tokens),
      [Inheritance](#Inheriting), [NTAs](#ASTNTAs)
    * [Lists &amp; Opts](#ListsAndOpts), [Building](#treeBuilding), [Using JJTree](#JJTree)
* [Aspects](#Aspects)
    * [Aspect files](#jadd) (`.jadd` and `.jrag` files)
    * [Supported AOP features](#AOPfeatures), [Differences from AspectJ](#AspectJ),
      [Idiom for aspect variables](#IdiomPrivate)
* [Attributes](#Attributes)
    * [Synthesized](#Synthesized), [inherited](#Inherited), [method syntax](#Method),
      [lazy/caching](#Lazy), [refine](#RefineAttr)
    * [Parameterized](#Parameterized), [broadcasting](#Broadcasting),
      [circular](#Circular), [NTAs](#Nonterminal), [collections](#Collection)
* [Rewrites](#Rewrites)
* [Command line syntax](#Command)

## <a id="Cheatsheet"></a>Quick syntax overview

### AST Specification Syntax

Syntax for AST class declarations in `.ast` files:

Syntax                  | Meaning
------------------------|--------
`A;`                    | AST class
`B: S;`                 | AST subclass (B is a subclass of S)
`abstract A;`           | AST class, abstract
`B ::= Y;`              | Child component `Y`.
`B ::= MyY:Y;`          | Child component `MyY` of type `Y`.
`X ::= C*;`             | List component `C`, containing `C` nodes.
`X ::= MyC:C*;`         | List component `MyC`, containing `C` nodes.
`Y ::= [D];`            | Optional component `D`.
`Y ::= [MyD:D];`        | Optional component `MyD` of type `D`.
`Z ::= <E>;`            | Token component `E` of type `String`.
`Z ::= <F:Integer>;`    | Token component `F` of type `Integer`.
`U ::= /V/;`            | [NTA](#Nonterminal) component `V`.
`U ::= /G:V/;`          | [NTA](#Nonterminal) component `G` of type `V`.

### Aspect declarations

Syntax for attribute declarations in `.jrag` and `.jadd` files:

Declaration                         | Meaning
------------------------------------|--------
`aspect N { decl* }`                | Aspect declaration
`syn T A.c();`                      | Synthesized attribute
`syn T A.c() = exp;`                | Synthesized attribute, default equation
`syn T A.c() { stmt* }`             | Synthesized attribute, method body
`syn lazy T A.c() { stmt* }`        | Synthesized attribute, cached
`syn circular X A.c() [bot] = exp;` | Synthesized attribute, circular
`syn nta X A.c() = exp;`            | Nonterminal attribute (NTA)
`eq B.c() = exp;`                   | Synthesized equation
`eq B.c() { stmt* }`                | Synthesized equation, method body
`inh X A.i();`                      | Inherited attribute
`inh lazy X A.i();`                 | Inherited attribute, cached
`eq B.getChild().i() = exp;`        | Inherited equation, broadcast
`eq B.getChild().i() { stmt* }`     | Inherited equation, broadcast, method body
`eq B.getA().i() = exp;`            | Inherited equation for the `A` child of `B` nodes.
`eq B.getDecl(int index).i() = exp;` | Inherited equation for the `Decl` list component of `B` nodes.
`coll LinkedList<B> A.c() [new LinkedList<B>()] root A;` | Collection attribute
`coll LinkedList<B> A.c();`         | Collection attribute, short form
`B contributes exp when cond to A.c() for targetexp;` | Collection contribution
`B contributes exp when cond to A.c();` | Collection root contribution
`refine FileName thing = exp;`      | Refine an attribute equation (synthesized or inherited)
`refine FileName thing { stmt* }`   | Refine an attribute or intertype method
`rewrite A { when condition to B { stmt* } }` | AST node rewrite
`uncache A.x();`                    | Never cache `A.x()`
`cache A.y(int a);`                 | Please cache `A.y(int)`
`public void A.m(int x) { stmt* }`  | Intertype declared method
`public String A.f = exp;`          | Intertype declared field


## <a id="AbstractSyntax"></a>Abstract syntax

Abstract grammars are specified in `.ast` files and are used to generate a Java
class hierarchy. The classes in an abstract grammar are referred to as AST
classes. The AST classes are used by the parser to build Abstract Syntax Trees
(ASTs).

### <a id="Predefined"></a>Predefined AST classes

The AST classes include user declared classes in the abstract grammar, as well
as a few predefined AST classes that are implicitly generated. The predefined
AST classes are described in the table below.

<table border=1>
<tr>
    <th>Predefined AST class</th>
    <th>Purpose</th>
    <th>Accessing children</th>
</tr>
<tr>
    <td>
        <a id="ASTNode"></a><pre>ASTNode</pre>
    </td>
    <td>
        This is the base class which all other AST classes extend.
        Children are numbered from <code>0</code> to <code>getNumChild() - 1</code>.
    </td>
    <td>
        Children are accessed using the generated methods <code>getNumChild()</code> and <code>getChild(int)</code>.

        <p><pre>public ASTNode&lt;T extends ASTNode&gt; implements Cloneable {
  int getNumChild();
  ASTNode getChild(int index);
  ASTNode getParent();
  Iterable&lt;T&gt; astChildren();
  Iterator&lt;T&gt; astChildIterator();
}</pre>
    </td>
</tr>
<tr>
    <td>
        <pre><a id="List"></a>List</pre>
    </td>
    <td>
        Contains elements of list components in AST classes.
    </td>
    <td>
        <code>getNumChild()</code> and <code>getChild(int)</code> are inherited
        from <code>ASTNode</code>. The enhanced <code>for</code> statement can
        be used on the list since <code>List</code> implements
        <code>Iterable&lt;ASTNode&gt;</code>.

        <p><pre>public List&lt;T extends ASTNode&gt; extends ASTNode&lt;T&gt; implements Iterable&lt;T&gt; {
}</pre>
    </td>
</tr>
<tr>
    <td>
        <a id="Opt"></a><pre>Opt</pre>
    </td>
    <td>
        Used to implement optional components in AST classes. Has 0 or 1 child.
    </td>
    <td>
        <code>getNumChild()</code> and <code>getChild(int)</code> inherited
        from <code>ASTNode</code> can be used when accessing <code>Opt</code> nodes directly.

        <p><pre>public Opt&lt;T extends ASTNode&gt; extends ASTNode&lt;T&gt; {
}</pre>
    </td>
</tr>
</table>

See also &quot;[About Lists and Opts](#ListsAndOpts)&quot;.

### Abstract syntax constructs

The table below documents the syntax used to declare user declared AST classes
in `.ast` files.

#### <a id="Basic"></a>Basic constructs

<table border=1>
<tr>
    <th>Construct</th>
    <th>Meaning</th>
    <th><a id="typedTraversalAPI">Generated API</a></th>
</tr>
<tr>
    <td>
        <pre>abstract A;</pre>
    </td>
    <td>
        <a id="abstract"></a><code>A</code> is an abstract AST class.
        <code>A</code> corresponds to a nonterminal in the context free grammar.
    </td>
    <td>
        <pre>abstract class A extends ASTNode { }</pre>
    </td>
</tr>
<tr>
    <td>
        <pre>B: A ::= ...;</pre>
    </td>
    <td>
        <a id="subclass"></a><code>B</code> is a concrete subclass of <code>A</code>.
        <code>B</code> corresponds to a production of <code>A</code> in the context-free grammar.
    </td>
    <td>
        <pre>class B extends A { }</pre>
    </td>
</tr>
<tr>
    <td>
        <pre>C: A ::= A B C;</pre>
    </td>
    <td>
        C has three <a id="children"></a>children of types A, B, and C.

        The API supports typed traversal of the children.
    </td>
    <td>
        <pre>class C extends A {
  A getA();
  B getB();
  C getC();
}</pre>
    </td>
</tr>
<tr>
    <td>
        <pre>D: A;</pre>
    </td>
    <td>
        <code>D</code> has no children.

        <code>D</code> corresponds to an <a id="empty"></a>empty production of <code>A</code>.
    </td>
    <td>
        <pre>class D extends A { }</pre>
    </td>
</tr>
<tr>
    <td>
        <pre>E1 ::= A;</pre>
    </td>
    <td>
        <code>E1</code> has a child of type <code>A</code>.
    </td>
    <td>
        <pre>class E1 {
  A getA();
}</pre>
    </td>
</tr>
<tr>
    <td>
        <pre>E2 ::= &#91;B&#93;;</pre>
    </td>
    <td>
        <a id="optional"></a><code>E2</code> has an optional component of type <code>B</code>.
    </td>
    <td>
        <pre>class E2 {
  boolean hasB();
  B getB();
}</pre>
    </td>
</tr>
<tr>
    <td>
        <pre>E3 ::= C&#42;;</pre>
    </td>
    <td>
        <code>E3</code> has a list component of zero or more <code>C</code> nodes.
    </td>
    <td>
        <pre>class E3 {
  int getNumC();
  C getC(int);
  List&lt;C&gt; getCList();
}</pre>
    </td>
</tr>
<tr>
    <td>
        <pre>E4 ::= &lt;D&gt;;</pre>
    </td>
    <td>
        <code>E4</code> has a token component of type <code>D</code>.

        <p>The set method is intended to be used only by the parser to set the token
        value. The token value should not be changed after tree construction, and
        the set method should not be used by an attribute equation as it has side
        effects.
    </td>
    <td>
        <pre>class E4 {
  String getD();
  void setD(String);
}</pre>
    </td>
</tr>
<tr>
    <td>
        <a id="ASTNTAs"></a><pre>A ::= /D/;</pre>
    </td>
    <td>
        <code>A</code> has a nonterminal attribute (NTA) component <code>D</code>.
        The <code>D</code> component is not created by the parser, it is instead
        computed on demand, using an attribute equation.
        See <a href="#Nonterminal">specifying NTAs</a> for more info.
    </td>
    <td>
        <pre>class A {
  D getD();
}</pre>
    </td>
</tr>
</table>

#### <a id="Naming"></a>Naming children

<table border=1>
<tr>
    <th>Construct</th>
    <th>Meaning</th>
    <th>Generated API</th>
</tr>
<tr>
    <td>
        <pre>F ::= Foo:A Bar:B;</pre>
    </td>
    <td>
        It is possible to give components custom names.

        <p><em>Note!</em> If there is more than one child of the same type, they <em>must</em> be named.
    </td>
    <td>
        <pre>class F {
  A getFoo();
  B getBar();
}</pre>
    </td>
</tr>
<tr>
    <td>
        <pre>G ::= Thing:B*;</pre>
    </td>
    <td>
        List components can be named.
    </td>
    <td>
        <pre>class G {
  int getNumThing();
  B getThing(int);
  List&lt;B&gt; getThingList();
}</pre>
    </td>
</tr>
<tr>
    <td>
        <pre>H ::= [Foo:X];</pre>
    </td>
    <td>
        Optional components can be named.
    </td>
    <td>
        <pre>class H {
  boolean hasFoo();
  X getFoo();
}</pre>
    </td>
</tr>
</table>

#### <a id="Tokens"></a>Typed tokens

Tokens are implictly <code>String</code> typed. But you can also give a token
an explicit type:

<table border=1>
<tr>
    <th>Construct</th>
    <th>Meaning</th>
    <th>Generated API</th>
</tr>
<tr>
    <td>
        <pre>A ::= &lt;T&gt;;</pre>
    </td>
    <td>
        Here, <code>T</code> is a token of the type <code>String</code>.
    </td>
    <td>
        <pre>class A {
  String getT();
  void setT(String);
}</pre>
    </td>
</tr>
<tr>
    <td>
        <pre>A ::= &lt;T:String&gt;;</pre>
    </td>
    <td>
        This is equivalent to the example above.
    </td>
    <td></td>
</tr>
<tr>
    <td>
        <pre>A ::= &lt;T:int&gt;;</pre>
    </td>
    <td>
        Here, T is a token of the Java primitive type int.
    </td>
    <td>
        <pre>class A {
  int getT();
  void setT(int);
}</pre>
    </td>
</tr>
<tr>
    <td>
        <pre>A ::= &lt;Ref:B&gt;;</pre>
    </td>
    <td>
        Here, Ref is an intra-AST reference to a node of type B. This is a
        static reference to another node in the AST. The reference is not
        computed, rather it is set once during tree building.
    </td>
    <td>
        <pre>class A {
  B getRef();
  void setRef(B);
}</pre>
    </td>
</tr>
</table>

#### <a id="Inheriting"></a>Inheriting children

AST class children are inherited by subtypes.

<table border=1>
<tr>
    <th>Construct</th>
    <th>Meaning</th>
    <th>Generated API</th>
</tr>
<tr>
    <td>
<pre>abstract A ::= B C;
D: A;
E: A;</pre>
    </td>
    <td>
        <code>D</code> and <code>E</code> are subclasses of <code>A</code> and
        inherit the children of <code>A</code>.
    </td>
    <td>
        <pre>abstract class A extends ASTNode {
  B getB();
  C getC();
}
class D extends A { }
class E extends A { }</pre>
    </td>
</tr>
<tr>
    <td>
        <pre>A ::= B C;
D: A ::= F;</pre>
    </td>
    <td>
        A subclass declaration can add children, but not remove children from the superclass.
        Here, <code>D</code> has the children <code>B</code>, <code>C</code>,
        <code>F</code>.
    </td>
    <td>
        <pre>class A extends ASTNode {
  B getB();
  C getC();
}
class D extends A {
  F getF();
}</pre>
    </td>
</tr>
<tr>
    <td>
        <pre>A ::= B C;
D: A ::= C F B;</pre>
    </td>
    <td>
        Subclasses can repeat superclass children to change the child order.
        Here, the order of children in <code>D</code> is: <code>C</code>,
        <code>B</code>, <code>F</code>. This affects the generated constructors
        of <code>D</code> and the children accessed by <code>getChild(int)</code>.
        See below for <a href="#treeBuilding">more info about generated constructors</a>.
    </td>
    <td>
        Same as above.
    </td>
</tr>
</table>

### <a id="ListsAndOpts"></a>List and Opt components

JastAdd generates accessor methods to access optional and list components. The
generated methods can be used instead of accessing the `Opt` and `List`
container directly.  The generated accessor methods are listed in the table
below.  The `Opt` and `List` nodes can be accessed by `getXOpt()` and
`getXList()`, if needed.

<table border=1>
<tr>
    <th>Construct</th>
    <th>Generated API</th>
    <th>Example use</th>
</tr>
<tr>
    <td>
        <pre>A ::= &#91;B&#93;;</pre>
    </td>
    <td>
        <pre>class A {
  boolean hasB();
  B getB();
  Opt&lt;B&gt; getBOpt();
}</pre>
    </td>
    <td>
        <pre>A a = ...;
if (a.hasB()) {
  B b = a.getB();
  ...
}
</pre>
    </td>
</tr>
<tr>
    <td>
        <pre>C ::= D&#42;;</pre>
    </td>
    <td>
        <pre>class C {
  int getNumD();
  D getD(int index);
  List&lt;D&gt; getDList();
}</pre>
    </td>
    <td>
        <pre>C c = ...;
for (D d : c.getDList()) {
    ...
}</pre>
</td>
</tr>
</table>

### <a id="treeBuilding"></a>Building AST nodes

Use the following constructor API to build the AST. Typically you build the
AST in the action routines of your parser.  But you can of course also create
an AST by coding it explicitly, e.g., in a test case. If you use JavaCC and
JJTree, [see below](#JJTree).

<table border=1>
<tr>
    <th>AST declaration</th>
    <th>Generated constructor</th>
    <th>Comment</th>
</tr>
<tr>
    <td>
        <pre>A ::= B C &#91;D&#93; E&#42; &lt;G&gt;;</pre>
    </td>
    <td>
        <pre>A(B, C, Opt&lt;D&gt;, List&lt;E&gt;, String)</pre>
    </td>
    <td>The constructor parameter order is same as the child order.</td>
</tr>
<tr>
    <td>
        <pre>A ::= A /B/ C;</pre>
    </td>
    <td>
        <pre>A(A, C)</pre>
    </td>
    <td>Nonterminal attributes are not initialized via the constructor.</td>
</tr>
</table>

The predefined AST classes `Opt` and `List` have some default constructors to help
with building trees:

<table border=1>
<tr>
    <th>Predefined AST class</th>
    <th>Generated constructors</th>
</tr>
<tr>
    <td>
        <pre>List&lt;T&gt;</pre>
    </td>
    <td>
        <ul>
            <li><code>List()</code> create an empty list.
            <li><code>List(T...)</code> this constructor accepts a variable
            number of AST nodes as arguments, and adds the arguments as
            children of the constructed list.
            <li><code>List(Collection&lt;T&gt;)</code> adds all AST nodes in a
            collection to the list.
        </ul>
    </td>
    <td>The constructor parameter order is same as the child order.</td>
</tr>
<tr>
    <td>
        <pre>Opt&lt;T&gt;</pre>
    </td>
    <td>
        <p><ul>
            <li><code>Opt()</code> creates an empty optional.
            <li><code>Opt(T)</code> creates an optional containing the given AST node.
        </ul>
    </td>
    <td>Nonterminal attributes are not initialized via the constructor.</td>
</tr>
</table>

The `List` node constructor that takes no arguments can be used together with
the `add` method which returns the list itself, so you can chain multiple list
additions after creating a new node, like this:

    A1 a = ...;
    A2 a = ...;
    List<A> list = new List<A>().add(a1).add(a2);

Below is an example of building an AST based on the grammar

    A ::= B*;
    B ::= C;
    C ::= <ID>;

An example AST for this grammar can be built like this:

    A = new A(new List<B>(new B(new C("foo")), new B(new C("bar"))));


### <a id="JJTree"></a>Building ASTs using JJTree

If you use JJTree, the tree building code is generated by JJTree. You can use
the "#X" notation in the JJTree specification to guide the node creation.

JJTree maintains a stack of created nodes. The "#X" notation means:

1. Create a new object of type `X`.
2. Pop the nodes that were created during this parse method and insert them as
   children to the new `X` node.
3. Push the new `X` node.

You need to explicitly create `List` and `Opt` nodes. When the parsing structure
does not fit the abstract tree, e.g. when parsing expressions, you need to use
some additional tricks. You also need to set token values explicitly.

### <a id="Aspects"></a>Aspects

JastAdd aspects support *intertype declarations* for AST classes. An
intertype declaration is a declaration that appears in an aspect file, but that
actually belongs to an AST class. The JastAdd system reads the aspect files and
weaves intertype declarations into the target AST classes.

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

The aspect syntax is similar to that of AspectJ, but in contrast JastAdd
aspects are not real language constructs. The JastAdd system simply reads the
aspect files and inserts the intertype declarations into the appropriate AST
classes. For example, the method `m()` and its implementations are inserted
into classes `Stmt`, `WhileStmt`, and `IfStmt`. And the declaration of the
field `count` is inserted into the class `Stmt`. Import declarations are
inserted into all AST classes for which there are intertype declarations in the
aspect. So, the import of `java.lang.util.*` is inserted into `Stmt.java`,
`WhileStmt.java`, and `IfStmt.java`. For a more detailed discussion on the
similarities and differences between JastAdd aspects and AspectJ, see
[below](#AspectJ).

The aspect names, e.g., A and B above, do not show up in the woven Java code,
other than in generated documentation comments for woven attributes and
intertype declarations. Aspect names are used for [refine
declarations](#refine).

Aspect names are a way to indicate the purpose of the aspect. A common idiom
for naming aspects is to have one aspect per aspect file, and give the aspect
the same name as the filename sans the extension.

### <a id="jadd"></a>JADD and JRAG files

An aspect file can have the suffix `.jadd` or `.jrag`. The JastAdd system does
not differ between these two types of files, but we recommend the following
use:

* Use `.jrag` files for declarative aspects, i.e., where you add
attributes, equations, and rewrites to the AST classes
* Use `.jadd` files for imperative aspects, i.e., where you add
ordinary fields and methods to the AST classes


It is perfectly fine to not follow this convention, i.e., to mix both
imperative and declarative features in the same aspect, but we try to follow
the convention in our examples in order to enhance the readability of a system.

### <a id="example"></a><a id="imperative"></a>Example imperative aspect (JADD)

Here is an example *imperative* aspect that adds pretty printing behavior to
some AST classes. Typically, this file would be named `PrettyPrint.jadd`:

    aspect PrettyPrint {
      void WhileStmt.pp() {
        System.out.format("while %s do %s%n", getExp().pp(), getStmt().pp());
      }
      void IfStmt.pp() { ... }
      void Exp.pp() { ... }
    }

### <a id="declarative"></a>Example declarative aspect (JRAG)

Here is an example *declarative* aspect that adds type checking to some
AST classes. Typically, this file would be named `TypeCheck.jrag`:

    import TypeSystem.Type;
    aspect TypeCheck {
      syn Type Exp.actualType();
      eq LogicalExp.actualType() = Type.boolean();
      eq IdUse.actualType() = decl().getType();
      ...
      inh Type Exp.expectedType();
      eq WhileStmt.getExp().expectedType() = Type.boolean();
      syn boolean Exp.typeError() = !(actualType().equals(expectedType());
    }

### <a id="AOPfeatures"></a>Supported AOP features

<table border=1>
<tr>
    <th>Feature</th>
    <th>Comment</th>
</tr>
<tr>
    <td>
        Intertype declaration of AST fields, methods, and constructors.
    </td>
    <td>
        See the prettyprinting example above. The declarations are inserted into the
        corresponding AST classes by the AST weaver. Any modifiers (public, private,
        static, etc.), are interpreted in the context of the AST class. I.e., not as in
        AspectJ where the public/private modifiers relate to the aspect.
    </td>
</tr>
<tr>
    <td>
        Intertype declaration of attributes, equations, and rewrites.
    </td>
    <td>
        See the type checking example above. For more details,
        see <a href="#Attributes">Attributes</a>.

        Note that access modifiers (public, private, etc.) are not supported for
        attributes. All declared attributes generate public accessor methods in the AST
        classes.
    </td>
</tr>
<tr>
    <td>
        Declare additional interfaces for AST classes.
    </td>
    <td>
        In an aspect you can write

        <p><pre>WhileStmt implements LoopInterface;</pre>

        <p>This inserts an "implements LoopInterface" clause in the generated
        <code>WhileStmt</code> class.
    </td>
</tr>
<tr>
    <td>
        Declare classes and interfaces in an aspect.
    </td>
    <td>
        In an aspect you can write

        <p><pre>interface I { ... }
class C { ... }</pre>

        <p>This is equivalent to declaring the interface and class
        in separate ordinary Java files. The possibility to declare
        them inside an aspect is just for convenience.
    </td>
</tr>
<tr>
    <td>
        <a id="refine"></a>Refine a method declared in another aspect.
    </td>
    <td>
        For extensibility it is often useful to be able to replace or refine
        methods declared in another aspect.  This can be done using a "refine"
        clause. In the following example, the aspect A declares a method
        <code>m()</code> in the class <code>C</code>. In the aspect
        <code>B</code>, the method is replaced, using a "refine" clause. This
        is similar to overriding a method in a subclass, but here the
        "overridden" method is in the same class, just defined in another
        aspect.  Inside the body of the refined method, the original method can
        be called explicitly.  This is similar to a call to super for
        overriding methods.

        <p><pre>aspect A {
  void C.m() { ... }
}

aspect B {
  refine A void C.m() { // Similar to overriding.
    ...
    refined(); // Similar to call to super.
    ...
  }
}</pre>

        <p>Note that the refine clause explicitly states which aspect is refined (A in
        this case).  Additional aspects may further refine the method. For example, an
        aspect C can refine the method refined in B.

        <p>The original method can be called using the keyword "refined". JastAdd2
        replaces all occurrences of this keyword with the new name of the refined
        method. Be careful with how you use refined - even occurrences in string
        literals or comments are replaced!  With the command line flag "--refineLegacy"
        you can use the legacy syntax for calling the refined method:

        <p><pre>aspect A {
  void C.m() { ... }
}

aspect B {
  refine A void C.m() { // similar to overriding
    ...
    AspectFile.C.m();
    ...
  }
}</pre>

        <p><em>Note:</em> AspectFile is the name of the file (minus extension)
        that contains the original declaration.
    </td>
</tr>
</table>

### <a id="AspectJ"></a>Similarities and differences from AspectJ

The aspect concept in JastAdd was developed in parallel to the AspectJ
development, and we have gradually adopted the AspectJ syntax, for features
that are similar.  The important similarity between JastAdd aspects and AspectJ
aspects is the intertype declarations. In addition, JastAdd aspects support
attribute grammar features which AspectJ does not. Note, however, that JastAdd
supports intertype declarations only for the AST classes, not for classes in
general as AspectJ does. There are many other features of AspectJ that are not
supported in JastAdd, for example:

* Fields and methods private to an aspect are not supported.
* Declaration of additional parent classes is not supported.
* Dynamic features like AspectJ's pointcuts or advice are not supported.

### <a id="IdiomPrivate"></a>Idiom for private fields and methods

As mentioned, JastAdd does not support fields and methods that are private to
an aspect. As a workaround idiom, such fields and methods can be implemented as
(non-private) static fields and methods in class `ASTNode`. As an example,
consider the pretty printer. We might want to parameterize the pretty printer methods so
that it can pretty print to any `PrintStream` object, not only on `System.out`.
Here is how you could write this in AspectJ and the corresponding JastAdd
implementation:

<table border=1>
<tr>
    <th>AspectJ code</th>
    <th>JastAdd code</th>
</tr>
<tr>
    <td>
        <pre>aspect PrettyPrinter {
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
}</pre>
    </td>
    <td>
        <pre>aspect PrettyPrinter {
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
}</pre>
    </td>
</tr>
</table>

## <a id="Attributes"></a>Attributes

Attributes are specified in [JastAdd aspect files](#Aspects).

Different kinds of attributes are documented in the following sections.

### <a id="Basic"></a><a id="Synthesized"></a>Synthesized attributes

<table border=1>
<tr>
    <th>Syntax</th>
    <th>Meaning</th>
</tr>
<tr>
    <td>
        <pre>syn T A.x();</pre>
    </td>
    <td>
        Declares a synthesized attribute <code>x</code> of type <code>T</code> in class A.

        <p>There must be equations defining x in A (if A is concrete) or in all
        concrete subclasses of A (if A is abstract).

        <p><em>Note!</em> Synthesized attributes are conceptually equivalent to abstract
        virtual functions (without side-effects). The main difference is that their
        values may be cached (see below). They can be accessed in the same way as
        virtual functions. I.e., the declaration generates the following Java API:

        <p><pre>T A.x();</pre>
    </td>
</tr>
<tr>
    <td>
        <pre>eq A.x() = <em>Java-expr;</em></pre>
    </td>
    <td>
        The equation defines the value of the synthesized attribute x of AST nodes of
        type A.

        <p>The Java-expression that defines the value must be free from externally visible
        side-effects. The context of the expression is the class A, and any part of the
        class A's API may be used in the computation, including accesses to other
        attributes.

        <p><em>Note!</em> Equations defining synthesized attributes are conceptually
        equivalent to virtual method implementations (without side-effects).
    </td>
</tr>
<tr>
    <td>
        <pre>eq B.x() = <em>Java-expr</em>;</pre>
    </td>
    <td>
        Suppose B is a subclass of A. This equation overrides the corresponding
        (default) equation for A.x().

        <p><em>Note!</em> This is equivalent to overriding method implementations.
    </td>
</tr>
<tr>
    <td height=70>
        <pre>syn T A.x() = <em>Java-expr;</em></pre>
    </td>
    <td height=70>
        <a id="Shorthand"></a>The declaration of a synthesized attribute and the
        (default) equation for it can be written in one clause. So
        the clause to the left is equivalent to:

        <p><pre>syn T A.x();
eq A.x() = <em>Java-expression;</em></pre>
    </td>
</tr>
</table>

### <a id="Inherited"></a>Inherited attributes

Inherited attributes propagate information down in the AST. When an inherited
attribute is evaluated, the evaluation code first searches upward in the AST
for a node that can compute the inherited attribute. The equation may be on the
parent of the current node, or an ancestor, even the root of the tree.

<table border=1>
<tr>
    <th>Syntax</th>
    <th>Meaning</th>
</tr>
<tr>
    <td width=266>
        <pre>inh T A.y();</pre>
    </td>
    <td>
        y is an inherited attribute in class A and of type T.
        There must be equations defining y in all classes that have
        children of type A. If a class has several children of type
        A, there must be one equation for each of them.

        Inherited attributes can be accessed in the same way as
        synthesized attributes. I.e., the declaration generates the
        following Java API:

        <pre>T A.y();</pre>

        <em>Note!</em> Inherited attributes differ from ordinary
        virtual functions in that their definitions
        (equations/method implementations) are located in the
        <em>parent</em> AST node, rather than in the node itself.

        <em>Note!</em> The concept of <em>inherited</em> attributes
        in this Attribute Grammar sense is completely different from
        object-oriented inheritance. Both attribute grammars and
        object-orientation were invented in the late 60's and the
        use of the same term "inheritance" is probably a mere
        coincidence: In AGs, inheritance takes place between nodes
        in a syntax tree. In OO, inheritance takes place between
        classes in a class hierarchy.
    </td>
</tr>
<tr>
    <td width=266>
        <pre>eq C.getA().y() = <em>Java-expr;</em></pre>
    </td>
    <td>
        This equation defines the value of the inherited attribute
        y() of the A child of a C node.

        <em>Note!</em> The Java-expression executes in the context of
        C.

        <em>Note!</em> The equation is similar to a method
        implementation.

        <em>Note!</em> The equation actually applies to all inherited
        attributes y in the subtree rooted at A, provided that they
        declare the y attribute. See below under broadcast
        attributes.
    </td>
</tr>
<tr>
    <td width=266>
        <pre>eq D.getA().y() = <em>Java-expr</em>;</pre>
    </td>
    <td>
        Suppose D is a subclass of C. In this case, the equation
        overrides the previous one.

        <p><em>Note!</em> This is analogous to overriding a virtual
        method implementation.
    </td>
</tr>
</table>

### <a id="Method"></a>Method syntax

It is possible to write the computation of an attribute value as a
method body instead of as a single expression.  This may be convenient
when the computation is complex.  Inside the method body it is possible
to use ordinary imperative Java code with local variables, assignments,
loops, etc. However, the net result of the computation must not have
any side-effects. (Currently, JastAdd does not check the absence of
such side-effects, but future versions might do so.)

Example of a method body in a synthesized attribute:

<pre>syn T A.x() {
  ...
  return <em>Java-expr</em>;
}</pre>


### <a id="Lazy"></a>Cached attributes

An attribute can be declared *lazy* in order to speed up subsequent
evaluations of the attribute.  An attribute that is declared lazy will have its
value is cached after the first access to it. The next time the attribute is
accessed, the cached value is returned directly. We recommend that attributes
that are expensive to compute and that are accessed multiple times should be
declared lazy. For example, declaration bindings and type attributes are good
candidates for caching. JastAdd has facilities for automatically computing good
cache configurations based on profiling, but this is not yet documented here.

Example lazy attribute declaration:

    syn lazy A.x();


Another way to change the caching behaviour of attributes is to use a separate
cache declaration:


<table border=1>
<tr>
    <th>Syntax</th>
    <th>Meaning</th>
</tr>
<tr>
    <td>
        <pre>uncache A.x();</pre>
    </td>
    <td>
        This prevents attribute x of class A from ever being cached, though
        attributes with the same name in subtypes of A can still be cached if
        declared lazy.
    </td>
</td>
<tr>
    <td>
        <pre>cache A.x();</pre>
    </td>
    <td>
        This tells JastAdd to cache attribute x of class A.
    </td>
</td>
</table>

Cache declarations take precedence over the `lazy` keyword, but conflicting
cache declarations for a single attribute will cause JastAdd to report an error
as there is no way to select the proper caching strategy.

### <a id="RefineAttr"></a>Refining attributes

Equations defined in one aspect can be refined in another aspect, in the same
way as methods can be refined, see [JastAdd aspect files](#Aspects).  In the
example below, the equation replaces the corresponding equation declared in the
aspect named `S`:

<pre>refine S eq B.x() = <em>Java-expr;</em></pre>

The value of the original equation in `S` can be accessed by the expression
`S.B.x()`. Older JastAdd code accessed the original equation by using the
expression `refined()`.

### <a id="Parameterized"></a>Parameterized attributes

Attributes can have parameters. This is a bit unusual for attribute grammars,
but a natural generalization when attributes are viewed as virtual functions.

<table border=1>
<tr>
    <th>Syntax</th>
    <th>Meaning</th>
</tr>
<tr>
    <td width=266>
        <pre>syn T A.x(int a);
eq A.x(int a) {
  return <em>Java-expr</em>;
}</pre>
    </td>
    <td>
        Here, x is a parameterized synthesized attribute. The equation is
        similar to a method implementation and the argument values can be used
        in the computation of the resulting value.
    </td>
</tr>
<tr>
    <td width=266>
        <pre>inh T A.y(int a);
eq C.getA().y(int a) {
  return <em>Java-expr</em>;
}</pre>
    </td>
    <td>
        Here, y is a parameterized inherited attribute. The equation executes
        in the context of C and can in addition access the arguments (a in this
        case).
    </td>
</tr>
</table>

### <a id="Broadcasting"></a>Broadcasting inherited attributes

Often, an inherited attribute is used in a number of places in a subtree. If
basic inherited attributes are used, the value needs to be copied explicitly
using inherited attributes in all the intermediate nodes. For convenience,
JastAdd supports another technique called broadcasting, where an inherited
attribute is available in every node of a complete subtree. An equation
defining an inherited attribute actually broadcasts the value to the complete
subtree of the child. By using this technique, no explicit copy attributes are
needed.

<table border=1>
<tr>
    <th>Syntax</th>
    <th>Meaning</th>
</tr>
<tr>
    <td width=266>
        <pre>eq C.getA().y() = ...;
inh T A.y();</pre>
    </td>
    <td>
        Here, the equation defines an inherited attribute y() declared in the A
        child of a C node. This equation actually applies not only to the
        inherited y() attribute of the A child, but to all inherited y()
        attributes in the whole subtree of A. In order to for a node N in the
        subtree to access y(), the attribute must, however, be exposed by
        declaring y() as an inherited attribute of N.
    </td>
</tr>
<tr>
    <td width=266>
        <pre>inh T B.y();</pre>
    </td>
    <td>
        Here, the attribute y() is exposed in B by declaring it as an inherited
        attribute there. If there is a B node that is in the subtree rooted at the A
        that is a child of a C node, then the equation above will apply.
    </td>
</tr>
</table>

#### Overruling broadcast definitions

A broadcast definition of an attribute a() applies to all nodes in a subtree
rooted by N. If, however, there is a node in the subtree which has another
equation that defines a() for a child M, that equation will take precedence for
defining a() in M and its subtree.

#### Differentiating between children in a list

When defining an inherited attribute of a child node that
is an element of a list, it is sometimes useful to know
which index the child node has. This can be done as
follows:

<pre>C ::= E&#42;;
eq C.getE(int index).y() = expr;</pre>

Here, a `C` node has a list of `E` children. When defining the `y()` attribute
of a given (subtree of an) `E` child, the value might depend on the index of
the child. For example, if the `E` nodes are actual arguments of a procedure,
we might want to pass down the expected type of each argument.

The example equation shows how to declare the index as a parameter of the
`getE()` method, and to access the index in the equation body.

### <a id="Circular"></a>Circular attributes

Attributes can be circularly defined, meaning that the value of the attribute
can depend (indirectly) on itself. Circular attributes are evaluated
iteratively, starting with a start value given in the declaration of the
attribute. The evaluation stops when the value equals that for the previous
iteration.

Circular attributes are always cached. They do not need to be declared "lazy".

It is an error if a lazy attribute is circular, but not declared as such.  With
the `visitCheck` and `componentCheck` options this can be detected at runtime,
and an exception will be generated. To be sure that the evaluation of circular
attributes will converge, the values should be arranged into lattices of finite
height, the bottom values should be used as starting values, and each equation
on the cycle should be monotonic with respect to the lattices.

<pre>syn T A.x(int a) circular &#91;<span style="font-style: italic;">bv</span>&#93;;
eq A.x(int a) = <span style="font-style: italic;">rv</span>;</pre>

Here, the attribute x is a circular attribute. The starting value is *bv* (a
Java expression).

The equation defines x as having the value computed by the Java expression
`rv`.  Note that `rv` may depend (directly or indirectly) on x.

If an attribute `A.x()` that was not declared circular becomes part of a
circular evaluation, for example by adding an extension aspect, then it is
essential that the original attribute is never cached. This can ensured using
the `uncache` declaration described above:

    uncache A.x();


Attribute systems with circular attributes are well defined if at least one
attribute on every possible circular dependency cycle is declared circular and
the other attributes on all cycles are either also declared circular or
declared uncached as above.

### <a id="Nonterminal"></a>Nonterminal attributes

Nonterminal attributes (NTAs) are nodes in the AST. Whereas normal AST nodes
are built by the parser, the NTAs are viewed as attributes and are defined by
equations.

* NTAs can be inherited or synthesized.
* The value in the equation should be a freshly built AST subtree. It should
  be complete in the sense that all its children should also be freshly
  created nodes (i.e., they are not allowed to be initialized to null).
* The NTA can itself have attributes that can be accessed like normal
  attributes.
* If the NTA has inherited attributes, there must be equations for those
  attributes in some ancestor, as for normal children.

#### Declaration Syntax

    syn nta C A.anNTA() = new C();


#### Older syntax

In the older syntax, a nonterminal attribute is added as follows:

* Declare the NTA in the ast file, see also [NTAs in the abstract
  syntax](#ASTNTAs).
* Declare the NTA as an attribute in a jrag file. It can be declared as a
  synthesized or an inherited attribute.  The name of the attribute should be
  the same as in the [AST traversal API](#typedTraversalAPI), e.g.,
  *getX* if the NTA is called *X*.
* Add equations defining the NTA. The defining value should be a new AST of the
  appropriate type, created using the [AST building API](#treeBuilding).

Note that if the NTA is a list or an optional node, you need to create the
appropriate AST with a `List` or an `Opt` node as its root. See examples below.

#### Simple synthesized NTA

*In an .ast file:*

    A ::= B /C/;


*In a .jrag file:*

    syn C A.getC() = new C();


The NTA *C* is declared in the .ast file. It is then declared as a
synthesized attribute *getC()* in the .jrag file.  The equation is
provided directly in the declaration and creates a new *C* node.

#### List NTA

*In an .ast file:*

    A ::= B /C*/;


*In a .jrag file:*

    syn C A.getCList() =
        new List()
            .add(new C())
            .add(new C());

The list NTA *C* is declared in the .ast file. It is then declared as a
synthesized attribute *getCList()* (the same name as in the [implementation
level traversal API](#ListsAndOpts)) in the `.jrag` file.  The equation is
provided directly in the declaration and creates a *List* node to which is
added a number of *C* nodes (two in this example).

### <a id="Collection"></a>Collection attributes

Collection attributes have composite values that are defined by so called
*contributions* that each add a small piece to the composite value. The
contributions may be located in any nodes in the AST.

#### Declaration syntax

The syntax for declaring a collection attribute looks like this:

    coll T A.c() [fresh] with m root R;


The individual parts of the declaration above are:

1. **T** is the type of the attribute. Usually `T` is a subtype of
   `java.lang.Collection`.
2. **A** is AST class on which the attribute is evaluated.
3. The **.c()** part declares the attribute name, in this case `c`.
4. (optional) **[fresh]** tells JastAdd how the intermediate collection result
   is initialized. The Java expression **fresh** creates an empty instance of
   the result type.  This part is optional if `T` is a concrete type with a
   default constructor, if it is omitted the default constructor of the type
   `T` is used, i.e. `new T()`.
5. (optional) **with m** specifies the name of a method to be used for updating
   the intermediate collection object. This part is optional and the default
   method `add` is used if no update method is specified.  The update method
   must fulfill these requirements:
    * The method `m`, should be a one-argument method of `T`.
    * The method `m` should mutate the `T` object by adding one object to it.
    * The method `m` should be commutative, in the sense that the order of
      calling `m` for different contributions should yield the same resulting
      `T` value.
6. (optional) The **root R** part declares the collection root type. The
   collection mechanism starts by finding the nearest ancestor node of type `R`
   for the `A` node which the collection attribute is evaluated on. The subtree
   rooted at that nearest `R` ancestor is searched for contributions to
   `A.c()`, this means that the collection is scoped to the subtree of `R`, and
   contributions outside that tree are not visible.


#### Contribution declaration

When JastAdd evaluates a collection attribute it first performs a "survey" of the AST,
searching for contributions to the given collection attribute. Contributions are added
by using contribution statements like below:

    N1 contributes value-exp
        when cond-exp
        to N2.a()
        for N2-ref-exp;

Let's look at each part of the above template statement:

* **N1** is the type of AST nodes that provide this particular contribution to
  the target collection attribute.
* **value-exp** is a Java expression that evaluates to an object to be added to
  the intermediate collection of the target collection attribute.
* (optional) **when cond-exp** is an optional contribution condition: the contribution is
  only added to the target collection attribute if the Java expression
  `cond-exp` evaluates to `true`.
* **N2** is the node type where the target collection attribute is declared.
* **.a()** is the name of the target collection attribute.
* (optional) **for N2-ref-exp** gives a Java expression, `N2-ref-exp`, which evaluates to
  a reference to the AST node that owns the collection attribute this
  contribution is contributing to. This is the target expression, and it can be
  omitted if the target node is identical to the collection root node.

One can optionally contribute one contribution to multiple target nodes by using this syntax:

    N1 contributes value-exp
        when cond-exp
        to N2.a()
        for each N2-ref-set;

where `N2-ref-set` is a Java expression that evaluates to an `Iterable<N2>` containing
references for the set of contribution target nodes.

It is possible to contribute multiple values in a single contribution by using this syntax:

    N1 contributes each value-exp
        when cond-exp
        to N2.a()
        for each N2-ref-set;

Note the **each** before `value-exp`. This syntax works if `value-exp` has the
type `Iterable<E>` where `E` is the element type of the collection attribute.
For example, if the collection attribute is declared as `coll
LinkedList<String> ...` then `value-exp` should have the type
`Iterable<String>`.

## <a id="Rewrites"></a>Rewrites

JastAdd has a mechanism for replacing AST nodes by a rewritten version of the
node whenever the node is first accessed. Rewrites are declared using rewrite
rules, described below.

### <a id="Unconditional"></a>Unconditional rewrite rule

<pre>rewrite A {
  to B {
    ...
    return <em>exp</em>;
  }
}</pre>

An A node will be replaced by the node specified in the Java expression
*exp*. This will happen as soon as the A node is accessed (by a get()
method from its parent), so if you traverse the tree you will only be able to
access the final rewritten nodes.

A and B must be AST classes.

The *exp* must be of type B.

Let the set S be the superclasses of A (including A) that occur on right-hand
sides of productions in the abstract syntax. B must be a subclass of all
classes in S. This guarantees that replacing an A node by a B node does not
break the rules in the abstract syntax.

The code in the body of the rewrite may access and rearrange the nodes in the
subtree rooted at A, but not any other nodes in the AST. Furthermore, the code
may not have any other side effects.

### <a id="Conditional"></a>Conditional rewrite rule

<pre>rewrite A {
  when ( <em>condition</em> )
    to B {
      ...
      return <em>exp</em>;
    }
}</pre>

The conditional rewrite works in the same way as the unconditional one, but
performs the replacement only if the boolean expression *condition* is
true. The condition may access anything in the AST, e.g., attributes, other
tree nodes and their attributes, etc.

### <a id="Iterative"></a>Iterative rewriting

After a node has been replaced according to a rewrite rule, all conditional
rewrite rules are checked again, and a new rewrite may be performed. This is
iterated until no rule conditions hold.

### <a id="Order"></a>Order of rewriting

At each iteration of rewriting, the rule conditions are evaluated in a certain
order. The first condition that is true is used for rewriting in that
iteration. The order in which rule condition evaluation occurs is the
following:

* conditions in superclasses are evaluated before conditions in subclasses
* conditions within an aspect file are evaluated in lexical order
* conditions in different aspect files are evaluated in the order the files are
  listed in the jastadd command.

### <a id="Confluency"></a>Confluency

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


### <a id="ShorthandRewrite"></a>Shorthand notation

If you have several conditional rewrite rules, you may write them inside
the same rewrite block. So, e.g., writing

<table>
<tr>
<td>
<pre>rewrite A {
  when ( <em>condition-1</em> )
  to B {
    ...
    return <em>exp-1</em>
  }
  when ( <em>condition-2</em> )
  to C {
    ...
    return <em>exp-2</em>
  }
}</pre>
</td>
<td style="padding: 2em">
... is equivalent to:
</td>
<td>
<pre>rewrite A {
   when <em>condition-1</em>
   to B {
      ...
      return <em>exp-1</em>
   }
}
rewrite A {
  when <em>condition-2</em>
  to C {
    ...
    return <em>exp-2</em>
  }
}</pre>
</td>
</tr>
</table>

Sometimes you don't need a block for computing the resulting node. It may be
sufficient with an expression. In that case, you may simply write the
expression instead of the block, e.g., as follows:

<table>
<tr>
<td>
<pre>rewrite A {
   when ( <em>condition-1</em> )
   to B <em>exp-1</em>
   when ( <em>condition-2</em> )
   to C <em>exp-2</em>
}</pre>
</td>
<td style="padding: 2em">
... which is equivalent to:
</td>
<td>
<pre>rewrite A {
   when ( <em>condition-1</em> )
   to B { return <em>exp-1</em> }
   when ( <em>condition-2</em> )
   to C { <em>return exp-2</em> }
}</pre>
</td>
</tr>
</table>


## <a id="Command"></a>Running JastAdd from the command line

### Synopsis

    java -jar jastadd2.jar [options] <source files>

Source file arguments are names of `.ast`, `.jrag` and `.jadd` files.  At least
one `.ast` file must be provided, otherwise JastAdd will not generate any code.
Some of the available options are listed below.


### Options

More details about JastAdd options can be printed by passing the `--help` option
to JastAdd.

Here is a short summary of available options:

Option                 | Purpose
-----------------------|--------
`--help`               | Prints help text and stops.
`--version`            | Prints version information and stops.
`--package=PPP`        | Optional package for generated files, default is none.
`--o=DDD`              | Optional base output directory, default is current directory.
`--beaver`             | Use beaver base node.
`--jjtree`             | Use jjtree base node, this requires `--grammar` to be set.
`--grammar=GGG`        | The parser for the grammar is called GGG, required when using jjtree.
`--rewrite=regular`    | Enable ReRAGs support.
`--visitCheck=false`   | Disable circularity check for attributes.
`--cacheCycle=false`   | Disable cache cyle optimization for circular attributes.

### Example

The following command generates classes according to the AST description in
`Toy.ast`.  The generated classes are placed in the package `ast`. The
specifications in the Jrag and Jadd files are translated and woven into the
generated classes.

    java -jar jastadd2.jar --package=ast Toy.ast \
        NameAnalysis.jrag TypeAnalysis.jrag PrettyPrinter.jadd

### ANT task

The options above are also available in an ANT task. The names for the options
are the same as above, and for flag options like `--beaver` you specify
`beaver="true"`, for other multi-value options you specify the option value you
want, for example `grammar="GGG"` as above.
