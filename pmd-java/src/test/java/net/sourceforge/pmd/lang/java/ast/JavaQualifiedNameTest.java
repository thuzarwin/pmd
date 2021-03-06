/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import static net.sourceforge.pmd.lang.java.ParserTstUtil.getNodes;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import net.sourceforge.pmd.lang.java.ParserTstUtil;


/**
 * @author Clément Fournier
 */
public class JavaQualifiedNameTest {


    /** Provides a hook into the package-private reset method for the local indices counter. */
    public static void resetLocalIndicesCounterHook() {
        QualifiedNameFactory.resetGlobalIndexCounters();
    }


    @Before
    public void setUp() {
        resetLocalIndicesCounterHook();
    }


    @Test
    public void testEmptyPackage() {
        final String TEST = "class Foo {}";
        Set<ASTClassOrInterfaceDeclaration> nodes = getNodes(ASTClassOrInterfaceDeclaration.class,
                                                             TEST);
        for (ASTClassOrInterfaceDeclaration coid : nodes) {
            JavaQualifiedName qname = coid.getQualifiedName();
            assertEquals("Foo", qname.toString());
            assertTrue(qname.getPackageList().isEmpty());
            assertTrue(qname.isUnnamedPackage());
            assertEquals(1, qname.getClassList().size());
            assertNull(qname.getOperation());
        }
    }


    @Test
    public void testPackage() {
        final String TEST = "package foo.bar; class Bzaz{}";

        Set<ASTClassOrInterfaceDeclaration> nodes = getNodes(ASTClassOrInterfaceDeclaration.class,
                                                             TEST);
        for (ASTClassOrInterfaceDeclaration coid : nodes) {
            JavaQualifiedName qname = coid.getQualifiedName();
            assertEquals("foo.bar.Bzaz", qname.toString());
            assertEquals(2, qname.getPackageList().size());
            assertEquals(1, qname.getClassList().size());
            assertNull(qname.getOperation());
        }
    }


    @Test
    public void testNestedClass() {
        final String TEST = "package foo.bar; class Bzaz{ class Bor{ class Foo{}}}";

        Set<ASTClassOrInterfaceDeclaration> nodes = getNodes(ASTClassOrInterfaceDeclaration.class,
                                                             TEST);

        for (ASTClassOrInterfaceDeclaration coid : nodes) {
            JavaQualifiedName qname = coid.getQualifiedName();
            switch (coid.getImage()) {
            case "Foo":
                assertEquals("foo.bar.Bzaz$Bor$Foo",
                             qname.toString());
                assertEquals(3, qname.getClassList().size());
                break;
            default:
                break;
            }
        }
    }


    @Test
    public void testNestedEnum() {
        final String TEST = "package foo.bar; class Foo { enum Bzaz{HOO;}}";

        Set<ASTEnumDeclaration> nodes = getNodes(ASTEnumDeclaration.class, TEST);

        for (ASTEnumDeclaration coid : nodes) {
            JavaQualifiedName qname = coid.getQualifiedName();
            assertEquals("foo.bar.Foo$Bzaz", qname.toString());
            assertEquals(2, qname.getPackageList().size());
            assertEquals(2, qname.getClassList().size());
            assertNull(qname.getOperation());
        }
    }


    @Test
    public void testEnum() {
        final String TEST = "package foo.bar; enum Bzaz{HOO;}";

        Set<ASTEnumDeclaration> nodes = getNodes(ASTEnumDeclaration.class, TEST);

        for (ASTEnumDeclaration coid : nodes) {
            JavaQualifiedName qname = coid.getQualifiedName();
            assertEquals("foo.bar.Bzaz", qname.toString());
            assertEquals(2, qname.getPackageList().size());
            assertEquals(1, qname.getClassList().size());
            assertNull(qname.getOperation());
        }
    }


    @Test
    public void testEnumMethodMember() {
        final String TEST = "package foo.bar; enum Bzaz{HOO; void foo(){}}";

        Set<ASTMethodDeclaration> nodes = getNodes(ASTMethodDeclaration.class, TEST);

        for (ASTMethodDeclaration coid : nodes) {
            JavaQualifiedName qname = coid.getQualifiedName();
            assertEquals("foo.bar.Bzaz#foo()", qname.toString());
            assertEquals(2, qname.getPackageList().size());
            assertEquals(1, qname.getClassList().size());
            assertEquals("foo()", qname.getOperation());
        }
    }


    @Test
    public void testNestedEmptyPackage() {
        final String TEST = "class Bzaz{ class Bor{ class Foo{}}}";

        Set<ASTClassOrInterfaceDeclaration> nodes = getNodes(ASTClassOrInterfaceDeclaration.class,
                                                             TEST);

        for (ASTClassOrInterfaceDeclaration coid : nodes) {
            JavaQualifiedName qname = coid.getQualifiedName();
            switch (coid.getImage()) {
            case "Foo":
                assertEquals("Bzaz$Bor$Foo",
                             qname.toString());
                assertTrue(qname.getPackageList().isEmpty());
                assertTrue(qname.isUnnamedPackage());
                assertEquals(3, qname.getClassList().size());
                break;
            default:
                break;
            }
        }
    }


    @Test
    public void testMethod() {
        final String TEST = "package bar; class Bzaz{ public void foo(){}}";

        Set<ASTMethodDeclaration> nodes = getNodes(ASTMethodDeclaration.class,
                                                   TEST);

        for (ASTMethodDeclaration declaration : nodes) {
            JavaQualifiedName qname = declaration.getQualifiedName();
            assertEquals("bar.Bzaz#foo()", qname.toString());
            assertNotNull(qname.getOperation());
            assertEquals("foo()", qname.getOperation());

        }
    }


    @Test
    public void testConstructor() {
        final String TEST = "package bar; class Bzaz{ public Bzaz(){}}";

        Set<ASTConstructorDeclaration> nodes = getNodes(ASTConstructorDeclaration.class,
                                                        TEST);

        for (ASTConstructorDeclaration declaration : nodes) {
            JavaQualifiedName qname = declaration.getQualifiedName();
            assertEquals("bar.Bzaz#Bzaz()",
                         qname.toString());
            assertNotNull(qname.getOperation());
            assertEquals("Bzaz()", qname.getOperation());

        }
    }


    @Test
    public void testConstructorWithParams() {
        final String TEST = "package bar; class Bzaz{ public Bzaz(int j, String k){}}";

        Set<ASTConstructorDeclaration> nodes = getNodes(ASTConstructorDeclaration.class,
                                                        TEST);

        for (ASTConstructorDeclaration declaration : nodes) {
            JavaQualifiedName qname = declaration.getQualifiedName();
            assertEquals("bar.Bzaz#Bzaz(int, String)", qname.toString());
            assertNotNull(qname.getOperation());
            assertEquals("Bzaz(int, String)", qname.getOperation());

        }
    }


    @Test
    public void testConstructorOverload() {
        final String TEST = "package bar; class Bzaz{ public Bzaz(int j) {} public Bzaz(int j, String k){}}";

        Set<ASTConstructorDeclaration> nodes = getNodes(ASTConstructorDeclaration.class,
                                                        TEST);

        ASTConstructorDeclaration[] arr = nodes.toArray(new ASTConstructorDeclaration[2]);
        assertNotEquals(arr[0].getQualifiedName(), arr[1].getQualifiedName());
    }


    @Test
    public void testMethodOverload() {
        final String TEST = "package bar; class Bzaz{ public void foo(String j) {} "
                + "public void foo(int j){} public void foo(double k){}}";

        Set<ASTMethodDeclaration> nodes = getNodes(ASTMethodDeclaration.class, TEST);

        ASTMethodDeclaration[] arr = nodes.toArray(new ASTMethodDeclaration[3]);
        assertNotEquals(arr[0].getQualifiedName(), arr[1].getQualifiedName());
        assertNotEquals(arr[1].getQualifiedName(), arr[2].getQualifiedName());
    }


    @Test
    public void testParseClass() {
        JavaQualifiedName outer = QualifiedNameFactory.ofString("foo.bar.Bzaz");
        JavaQualifiedName nested = QualifiedNameFactory.ofString("foo.bar.Bzaz$Bolg");

        assertEquals(1, outer.getClassList().size());
        assertEquals("Bzaz", outer.getClassList().head());

        assertEquals(2, nested.getClassList().size());
        assertEquals("Bzaz", nested.getClassList().head());
        assertEquals("Bolg", nested.getClassList().get(1));
    }


    @Test
    public void testParsePackages() {
        JavaQualifiedName packs = QualifiedNameFactory.ofString("foo.bar.Bzaz$Bolg");
        JavaQualifiedName nopacks = QualifiedNameFactory.ofString("Bzaz");

        assertNotNull(packs.getPackageList());
        assertEquals("foo", packs.getPackageList().get(0));
        assertEquals("bar", packs.getPackageList().get(1));

        assertTrue(nopacks.getPackageList().isEmpty());
    }


    @Test
    public void testParseOperation() {
        JavaQualifiedName noparams = QualifiedNameFactory.ofString("foo.bar.Bzaz$Bolg#bar()");
        JavaQualifiedName params = QualifiedNameFactory.ofString("foo.bar.Bzaz#bar(String, int)");

        assertEquals("bar()", noparams.getOperation());
        assertEquals("bar(String, int)", params.getOperation());
    }


    @Test
    public void testParseLocalClasses() {
        final String SIMPLE = "foo.bar.Bzaz$1Local";
        final String NESTED = "foo.Bar$1Local$Nested";
        JavaQualifiedName simple = QualifiedNameFactory.ofString(SIMPLE);
        JavaQualifiedName nested = QualifiedNameFactory.ofString(NESTED);

        assertNotNull(simple);
        assertTrue(simple.isLocalClass());
        assertFalse(simple.isAnonymousClass());
        assertNotNull(nested);
        assertFalse(nested.isLocalClass());
        assertFalse(simple.isAnonymousClass());

        assertEquals(SIMPLE, simple.toString());
        assertEquals(NESTED, nested.toString());

    }


    @Test
    public void testParseAnonymousClass() {
        final String SIMPLE = "Bzaz$12$13";

        JavaQualifiedName simple = QualifiedNameFactory.ofString(SIMPLE);

        assertNotNull(simple);
        assertTrue(simple.isAnonymousClass());
        assertFalse(simple.isLocalClass());

        assertEquals("12", simple.getClassList().get(1));
        assertEquals("13", simple.getClassList().get(2));

        assertEquals(SIMPLE, simple.toString());
    }

    @Test
    public void testParseLambdaName() {
        final String IN_LAMBDA = "foo.bar.Bzaz$1Local#lambda$null$12";
        final String STATIC = "foo.bar.Bzaz#lambda$static$12";
        final String NEW = "foo.bar.Bzaz#lambda$new$1";
        final String IN_METHOD = "Bzaz#lambda$myMethod$4";

        for (String s : Arrays.asList(IN_LAMBDA, STATIC, NEW, IN_METHOD)) {
            JavaQualifiedName qname = QualifiedNameFactory.ofString(s);
            assertNotNull(qname);
            assertTrue(qname.isLambda());
            assertEquals(s, qname.toString());
            assertEquals(qname, QualifiedNameFactory.ofString(qname.toString()));
        }
    }


    @Test
    public void testParseMalformed() {
        assertNull(QualifiedNameFactory.ofString(".foo.bar.Bzaz"));
        assertNull(QualifiedNameFactory.ofString("foo.bar."));
        assertNull(QualifiedNameFactory.ofString("foo.bar.Bzaz#foo"));
        assertNull(QualifiedNameFactory.ofString("foo.bar.Bzaz()"));
        assertNull(QualifiedNameFactory.ofString("foo.bar.Bzaz#foo(String,)"));
        assertNull(QualifiedNameFactory.ofString("foo.bar.Bzaz#foo(String , int)"));
        assertNull(QualifiedNameFactory.ofString("foo.bar.Bzaz#lambda$static$23(String)"));
        assertNull(QualifiedNameFactory.ofString("foo.bar.Bzaz#lambda$static$"));
    }


    @Test
    public void testSimpleLocalClass() {
        final String TEST = "package bar; class Boron { public void foo(String j) { class Local {} } }";

        List<ASTClassOrInterfaceDeclaration> classes
                = ParserTstUtil.getOrderedNodes(ASTClassOrInterfaceDeclaration.class, TEST);

        JavaQualifiedName qname = QualifiedNameFactory.ofString("bar.Boron$1Local");

        assertEquals(qname, classes.get(1).getQualifiedName());
    }


    @Test
    public void testLocalClassNameClash() {
        final String TEST = "package bar; class Bzaz{ void foo() { class Local {} } {// initializer\n class Local {}}}";

        List<ASTClassOrInterfaceDeclaration> classes
                = ParserTstUtil.getOrderedNodes(ASTClassOrInterfaceDeclaration.class, TEST);

        assertNotEquals(classes.get(1).getQualifiedName(), classes.get(2).getQualifiedName());

        assertEquals(QualifiedNameFactory.ofString("bar.Bzaz$1Local"), classes.get(1).getQualifiedName());
        assertEquals(QualifiedNameFactory.ofString("bar.Bzaz$2Local"), classes.get(2).getQualifiedName());
    }


    @Test
    public void testLocalClassDeepNesting() {
        final String TEST
                = "class Bzaz{ void foo() { "
                + "  class Local { "
                + "    class Nested {"
                + "      {"
                + "        class InnerLocal{}"
                + "      }"
                + "    }"
                + "  }"
                + "}}";

        List<ASTClassOrInterfaceDeclaration> classes
                = ParserTstUtil.getOrderedNodes(ASTClassOrInterfaceDeclaration.class, TEST);

        assertNotEquals(classes.get(1).getQualifiedName(), classes.get(2).getQualifiedName());

        assertEquals(QualifiedNameFactory.ofString("Bzaz$1Local"), classes.get(1).getQualifiedName());
        assertEquals(QualifiedNameFactory.ofString("Bzaz$1Local$Nested"), classes.get(2).getQualifiedName());
        assertEquals(QualifiedNameFactory.ofString("Bzaz$1Local$Nested$1InnerLocal"), classes.get(3).getQualifiedName());
    }


    @Test
    public void testAnonymousClass() {
        final String TEST
                = "class Bzaz{ void foo() { "
                + "  new Runnable() {"
                + "      public void run() {}"
                + "  };"
                + "}}";

        List<ASTAllocationExpression> classes = ParserTstUtil.getOrderedNodes(ASTAllocationExpression.class, TEST);

        assertEquals(QualifiedNameFactory.ofString("Bzaz$1"), QualifiedNameFactory.ofAnonymousClass(classes.get(0)));
        assertFalse(QualifiedNameFactory.ofAnonymousClass(classes.get(0)).isLocalClass());
        assertTrue(QualifiedNameFactory.ofAnonymousClass(classes.get(0)).isAnonymousClass());
        assertTrue("1".equals(QualifiedNameFactory.ofAnonymousClass(classes.get(0)).getClassSimpleName()));
    }


    @Test
    public void testMultipleAnonymousClasses() {
        final String TEST
                = "class Bzaz{ void foo() { "
                + "  new Runnable() {"
                + "      public void run() {}"
                + "  };"
                + "  new Runnable() {"
                + "      public void run() {}"
                + "  };"
                + "}}";

        List<ASTAllocationExpression> classes = ParserTstUtil.getOrderedNodes(ASTAllocationExpression.class, TEST);

        assertNotEquals(classes.get(0), classes.get(1));
        assertEquals(QualifiedNameFactory.ofString("Bzaz$1"), QualifiedNameFactory.ofAnonymousClass(classes.get(0)));
        assertEquals(QualifiedNameFactory.ofString("Bzaz$2"), QualifiedNameFactory.ofAnonymousClass(classes.get(1)));
    }


    @Test
    public void testNestedAnonymousClass() {
        final String TEST
                = "class Bzaz{ void foo() {"
                + "  new Runnable() {"
                + "    public void run() {"
                + "      new Runnable() {"
                + "        public void run() {}"
                + "      };"
                + "    }"
                + "  };"
                + "}}";

        List<ASTAllocationExpression> classes = ParserTstUtil.getOrderedNodes(ASTAllocationExpression.class, TEST);

        assertNotEquals(classes.get(0), classes.get(1));
        assertEquals(QualifiedNameFactory.ofString("Bzaz$1"), QualifiedNameFactory.ofAnonymousClass(classes.get(0)));
        assertEquals(QualifiedNameFactory.ofString("Bzaz$1$1"), QualifiedNameFactory.ofAnonymousClass(classes.get(1)));
    }


    @Test
    public void testLocalInAnonymousClass() {
        final String TEST
                = "class Bzaz{ void foo() {"
                + "  new Runnable() {"
                + "    public void run() {"
                + "      class FooRunnable {}"
                + "    }"
                + "  };"
                + "}}";

        List<ASTClassOrInterfaceDeclaration> classes = ParserTstUtil.getOrderedNodes(ASTClassOrInterfaceDeclaration.class, TEST);

        assertTrue(classes.get(1).isLocal());
        assertEquals(QualifiedNameFactory.ofString("Bzaz$1$1FooRunnable"), classes.get(1).getQualifiedName());
    }

    @Test
    public void testLambdaInStaticInitializer() {
        final String TEST
                = "import java.util.function.*;"
                + "class Bzaz{ "
                + "  static {"
                + "     Consumer<String> l = s -> {"
                + "         System.out.println(s);"
                + "     };"
                + "     l.accept(\"foo\");"
                + "  }"
                + "}";


        List<ASTLambdaExpression> lambdas = ParserTstUtil.getOrderedNodes(ASTLambdaExpression.class, TEST);

        assertEquals(QualifiedNameFactory.ofString("Bzaz#lambda$static$0"), lambdas.get(0).getQualifiedName());
    }


    @Test
    public void testLambdaInInitializerAndConstructor() {
        final String TEST
                = "import java.util.function.*;"
                + "class Bzaz{ "
                + "  {"
                + "     Consumer<String> l = s -> {"
                + "         System.out.println(s);"
                + "     };"
                + "     l.accept(\"foo\");"
                + "  }"
                + "  public Bzaz() {"
                + "     Consumer<String> l = s -> {"
                + "         System.out.println(s);"
                + "     };"
                + "     l.accept(\"foo\");"
                + "  }"
                + "}";

        List<ASTLambdaExpression> lambdas = ParserTstUtil.getOrderedNodes(ASTLambdaExpression.class, TEST);

        assertEquals(QualifiedNameFactory.ofString("Bzaz#lambda$new$0"), lambdas.get(0).getQualifiedName());
        assertEquals(QualifiedNameFactory.ofString("Bzaz#lambda$new$1"), lambdas.get(1).getQualifiedName());
    }


    @Test
    public void testLambdaField() {
        final String TEST
                = "import java.util.function.*;"
                + "public class Bzaz { "
                + "     Consumer<String> l = s -> {"
                + "         System.out.println(s);"
                + "     };"
                + "     public static Consumer<String> k = s -> {"
                + "         System.out.println(s);"
                + "     };"
                + "}";

        List<ASTLambdaExpression> lambdas = ParserTstUtil.getOrderedNodes(ASTLambdaExpression.class, TEST);

        assertEquals(QualifiedNameFactory.ofString("Bzaz#lambda$new$0"), lambdas.get(0).getQualifiedName());
        assertEquals(QualifiedNameFactory.ofString("Bzaz#lambda$static$1"), lambdas.get(1).getQualifiedName());
    }


    @Test
    public void testLambdaInterfaceField() {
        final String TEST
                = "import java.util.function.*;"
                + "public interface Bzaz { "
                + "     Consumer<String> l = s -> {"
                + "         System.out.println(s);"
                + "     };"
                + "     public static Consumer<String> k = s -> {"
                + "         System.out.println(s);"
                + "     };"
                + "}";

        List<ASTLambdaExpression> lambdas = ParserTstUtil.getOrderedNodes(ASTLambdaExpression.class, TEST);

        assertEquals(QualifiedNameFactory.ofString("Bzaz#lambda$static$0"), lambdas.get(0).getQualifiedName());
        assertEquals(QualifiedNameFactory.ofString("Bzaz#lambda$static$1"), lambdas.get(1).getQualifiedName());
    }


    @Test
    public void testLambdaLocalClassField() {
        final String TEST
                = "import java.util.function.*;"
                + "public class Bzaz { "
                + "  public void boo() {"
                + "     class Local {"
                + "         Consumer<String> l = s -> {"
                + "             System.out.println(s);"
                + "         };"
                + "     }"
                + "  }"
                + "}";

        List<ASTLambdaExpression> lambdas = ParserTstUtil.getOrderedNodes(ASTLambdaExpression.class, TEST);

        assertEquals(QualifiedNameFactory.ofString("Bzaz$1Local#lambda$Local$0"), lambdas.get(0).getQualifiedName());
    }


    @Test
    public void testLambdaAnonymousClassField() {
        final String TEST
                = "import java.util.function.*;"
                + "public class Bzaz { "
                + "  public void boo() {"
                + "     new Anonymous() {"
                + "         Consumer<String> l = s -> {"
                + "             System.out.println(s);"
                + "         };"
                + "     };"
                + "  }"
                + "}";

        List<ASTLambdaExpression> lambdas = ParserTstUtil.getOrderedNodes(ASTLambdaExpression.class, TEST);

        assertEquals(QualifiedNameFactory.ofString("Bzaz$1#lambda$$0"), lambdas.get(0).getQualifiedName());

        // This is here because of a bug with the regex parsing, which failed on "Bzaz$1#lambda$$0"
        // because the second segment of the lambda name was the empty string

        assertTrue(lambdas.get(0).getQualifiedName().isLambda());
        assertEquals("lambda$$0", lambdas.get(0).getQualifiedName().getOperation());
        assertEquals(2, lambdas.get(0).getQualifiedName().getClassList().size());
    }


    @Test
    public void testLambdasInMethod() {
        final String TEST
                = "import java.util.function.*;"
                + "class Bzaz{ "
                + "  public void bar() {"
                + "     Consumer<String> l = s -> {"
                + "         System.out.println(s);"
                + "     };"
                + "     l.accept(\"foo\");"
                + "  }"
                + "  public void fooBar() {"
                + "     Consumer<String> l = s -> {"
                + "         System.out.println(s);"
                + "     };"
                + "     l.accept(\"foo\");"
                + "  }"
                + "  public void gollum() {"
                + "     Consumer<String> l = s -> {"
                + "         System.out.println(s);"
                + "     };"
                + "     l.accept(\"foo\");"
                + "  }"
                + "}";

        List<ASTLambdaExpression> lambdas = ParserTstUtil.getOrderedNodes(ASTLambdaExpression.class, TEST);

        assertEquals(QualifiedNameFactory.ofString("Bzaz#lambda$bar$0"), lambdas.get(0).getQualifiedName());
        assertEquals(QualifiedNameFactory.ofString("Bzaz#lambda$fooBar$1"), lambdas.get(1).getQualifiedName());
        assertEquals(QualifiedNameFactory.ofString("Bzaz#lambda$gollum$2"), lambdas.get(2).getQualifiedName());
    }


    @Test
    public void testLambdaCounterBelongsToClass() {
        final String TEST
                = "import java.util.function.*;"
                + "class Bzaz{ "
                + "  static {"
                + "     Consumer<String> l = s -> {"
                + "         System.out.println(s);"
                + "     };"
                + "     l.accept(\"foo\");"
                + "  }"
                + "  public Bzaz() {"
                + "     Consumer<String> l = s -> {"
                + "         System.out.println(s);"
                + "     };"
                + "     l.accept(\"foo\");"
                + "  }"
                + "  public void gollum() {"
                + "     Consumer<String> l = s -> {"
                + "         System.out.println(s);"
                + "     };"
                + "     l.accept(\"foo\");"
                + "     new Runnable() {"
                + "       public void run() {"
                + "         Runnable r = () -> {};"
                + "         r.run();"
                + "       }"
                + "     }.run();"
                + "  }"
                + "}";

        List<ASTLambdaExpression> lambdas = ParserTstUtil.getOrderedNodes(ASTLambdaExpression.class, TEST);

        assertEquals(QualifiedNameFactory.ofString("Bzaz#lambda$static$0"), lambdas.get(0).getQualifiedName());
        assertEquals(QualifiedNameFactory.ofString("Bzaz#lambda$new$1"), lambdas.get(1).getQualifiedName());
        assertEquals(QualifiedNameFactory.ofString("Bzaz#lambda$gollum$2"), lambdas.get(2).getQualifiedName());
        assertEquals(QualifiedNameFactory.ofString("Bzaz$1#lambda$run$0"), lambdas.get(3).getQualifiedName()); // counter starts over for anon class

        // This is here because of a bug with the regex parsing, which caused "Bzaz$1#lambda$run$0"
        // to be parsed as
        // * classes == List("Bzaz", "#lambda", "run", "0").reverse()
        // * localIndices == List(-1, 1, -1, -1)
        // * operation == null
        assertTrue(lambdas.get(3).getQualifiedName().isLambda());
        assertEquals("lambda$run$0", lambdas.get(3).getQualifiedName().getOperation());
        assertEquals(2, lambdas.get(3).getQualifiedName().getClassList().size());
    }


}

