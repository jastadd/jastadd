ANTPATH=./tools/ant.jar:./tools/ant-launcher.jar
ASPECTJ=java -classpath tools/aspectj-1.5.3/aspectjlib.jar:tools/aspectj-1.5.3/aspectjrt.jar:tools/aspectj-1.5.3/aspectjtools.jar:tools/aspectj-1.5.3/aspectjweaver.jar:$(ANTPATH) org.aspectj.tools.ajc.Main -source 1.4
JAVACC=java -classpath tools/JavaCC.jar org.javacc.parser.Main -JDK_VERSION=1.4
JJTREE=java -classpath tools/JavaCC.jar org.javacc.jjtree.Main -JDK_VERSION=1.4
JASTADD=java -jar tools/jastadd2.jar --java1.4

JRAGFILES=ast/ClassRelations.jrag ast/ComponentsUtil.jrag ast/Errorcheck.jrag ast/JaddCodeGen.jrag ast/NameBinding.jrag \
 jrag/Attributes.jrag jrag/Circular.jrag jrag/CollectionAttributes.jrag jrag/Errorcheck.jrag jrag/JragCodeGen.jrag jrag/NameBinding.jrag

all : ast/AST/Grammar.java ast/AST/Ast.java jrag/AST/JragParser.java jastadd/JastAdd.java
	chmod u+x ./newrelease && ./newrelease && $(ASPECTJ) `find ast jastadd jrag org -name "*.java"`

ast/AST/Ast.java : ast/AST/Ast.jj
	$(JAVACC) -OUTPUT_DIRECTORY=ast/AST ast/AST/Ast.jj

ast/AST/Ast.jj : ast/Ast.jjt
	$(JJTREE) -OUTPUT_DIRECTORY=ast/AST -NODE_PREFIX=\"\" ast/Ast.jjt

ast/AST/Grammar.java : ast/Ast.ast $(JRAGFILES)
	$(JASTADD) --jjtree --rewrite --grammar=Ast --package=ast.AST $(JRAGFILES) ast/Ast.ast
  
jrag/AST/JragParser.java : jrag/AST/Jrag.jj
	$(JAVACC) -OUTPUT_DIRECTORY=jrag/AST jrag/AST/Jrag.jj

jrag/AST/Jrag.jj : jrag/Jrag.jjt
	$(JJTREE) -OUTPUT_DIRECTORY=jrag/AST jrag/Jrag.jjt

jar : all
	jar -cmf manifest jastadd2.jar LICENSE ast/*.class ast/AST/*.class jastadd/*.class jrag/*.class jrag/AST/*.class org/aspectj/lang/*.class org/aspectj/runtime/*.class org/aspectj/runtime/internal/*.class org/aspectj/runtime/reflect/*.class

#source-jar: all
#	jar -cmf manifest jastadd2-src.jar LICENSE manifest ast/*.ast ast/*.jjt ast/*.jrag ast/*.java jrag/*.jrag jrag/*.java jrag/*.jjt jrag/AST/SimpleNode.java jastadd/*.java jastadd/*.jrag Makefile newrelease org/aspectj/lang/*.class org/aspectj/runtime/*.class org/aspectj/runtime/internal/*.class org/aspectj/runtime/reflect/*.class tools/*.jar jrag/AST/Token.java doc/reference-manual.html doc/release-notes.html
#	mkdir jastadd2-src
#	cd jastadd2-src && jar -xf ../jastadd2-src.jar && cd ..
#	jar -cmf manifest jastadd2-src.jar jastadd2-src
#	rm -rf jastadd2-src

source-zip: all
	jar -cmf manifest jastadd2-src-temp.jar LICENSE manifest ast/*.ast ast/*.jjt ast/*.jrag ast/*.java jrag/*.jrag jrag/*.java jrag/*.jjt jrag/AST/SimpleNode.java jastadd/*.java jastadd/*.jrag Makefile newrelease org/aspectj/lang/*.class org/aspectj/runtime/*.class org/aspectj/runtime/internal/*.class org/aspectj/runtime/reflect/*.class tools/*.jar jrag/AST/Token.java doc/reference-manual.html doc/release-notes.html
	mkdir jastadd2-src
	cd jastadd2-src && jar -xf ../jastadd2-src-temp.jar && cd ..
	rm jastadd2-src-temp.jar
	zip -r jastadd2-src jastadd2-src
	rm -rf jastadd2-src

bin-zip: jar
	mkdir jastadd2-bin
	cp -p jastadd2.jar jastadd2-bin
	cp -p doc/reference-manual.html doc/release-notes.html jastadd2-bin
	zip -r jastadd2-bin jastadd2-bin
	rm -rf jastadd2-bin

bootstrap : jar
	cp -f jastadd2.jar tools/

release: jar source-zip
	@echo "new release compiled into jastadd2.jar and jastadd2-src.zip"

clean :
	/bin/rm -f jastadd/*.class ast/AST/* jrag/*.class jrag/AST/*.class jrag/AST/AST* jrag/AST/Jrag.jj jrag/AST/JragParser* jrag/AST/JJT* jrag/AST/Node.* jrag/AST/JavaCharStream* jrag/AST/ParseException*

RunTests.class: RunTests.java
	javac $^

test: jar RunTests.class
	@echo "==================="
	@echo Running unit tests!
	@java RunTests | grep "test/Test[0-9]*\.java passed" > passed
	@echo "======================"
	@echo Comparing test results
	@diff -s test/shouldpass passed
