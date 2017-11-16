package com.forufamily.gradle.plugin.tasks.aj.java

import org.aspectj.org.eclipse.jdt.core.dom.*

class AjAstSourceFlattener extends AjNaiveASTFlattener {
    @Override
    boolean visit(TypeDeclaration node) {
        println("TypeDeclaration: ${node?.name}, ${node?.properties()}")

        def visitor = this
        if (null != node && node instanceof AspectDeclaration) {
            buffer.append("\n@org.aspectj.lang.annotation.Aspect\n")
            buffer.append("public class ${node.name} {\n\n")
            callPrintIndent()
            node.bodyDeclarations().forEach {
                if (it instanceof ASTNode) it.accept(visitor)
            }
            buffer.append("}")
            return false
        }
        return true
    }

    @Override
    boolean visit(PointcutDeclaration node) {
        println("PointcutDeclaration: ${node?.name}, ${node?.properties()}")
        if (null != node) {
            callPrintIndent()
            buffer.append(" @org.aspectj.lang.annotation.Pointcut(\"")
            node.designator.accept(this)
            buffer.append("\")")
            buffer.append("\n")
            callPrintIndent()
            buffer.append(" public void ")
            node.name.accept(this)
            buffer.append("(")
            def parameters = node.parameters()
            def iterator = parameters.iterator()
            while (iterator.hasNext()) {
                val element = ++iterator as SingleVariableDeclaration
                buffer.append(element.type.toString() + " " + element.name)
                if (iterator.hasNext())
                    buffer.append(", ")
            }
            buffer.append(") {\n")
            callPrintIndent()
            buffer.append(" }\n\n")
            return false
        }
        return true
    }

    @Override
    boolean visit(AdviceDeclaration node) {
        println("AdviceDeclaration: ${node?.body}, ${node?.pointcut}, ${node?.properties()}")
        return super.visit(node)
    }

    @Override
    boolean visit(AroundAdviceDeclaration node) {
        if (!adviceProcess(node, "@org.aspectj.lang.annotation.Around", "Object")) {
            def flattener = new AjNaiveASTFlattener()
            node.body.accept(flattener)
            if (!flattener.result.contains("return")) {
                buffer.insert(buffer.lastIndexOf("}"), "  return null;\n")
            }
            return false
        }
        return true
    }

    @Override
    boolean visit(BeforeAdviceDeclaration node) {
        return !adviceProcess(node, "@org.aspectj.lang.annotation.Before", "void")
    }

    @Override
    boolean visit(AfterAdviceDeclaration node) {
        return !adviceProcess(node, "@org.aspectj.lang.annotation.After", "void")
    }

    private boolean adviceProcess(ASTNode node, String annotation, String returnType) {
        if (null != node) {
            callPrintIndent()
            buffer.append(" $annotation(").append("\"")
            node.pointcut.accept(this)
            buffer.append("()\")\n")

            callPrintIndent()
            buffer.append(" public $returnType advice")
            node.pointcut.accept(this)
            buffer.append("(org.aspectj.lang.JoinPoint thisJoinPoint) ")
            node.body.accept(this)

            return true
        }
        return false
    }

    @Override
    boolean visit(AfterThrowingAdviceDeclaration node) {
        println("AfterThrowingAdviceDeclaration: ${node?.body}, ${node?.pointcut}, ${node?.properties()}")
        return super.visit(node)
    }

    @Override
    boolean visit(AfterReturningAdviceDeclaration node) {
        println("AfterReturningAdviceDeclaration: ${node?.body}, ${node?.pointcut}, ${node?.properties()}")
        return super.visit(node)
    }

    private void callPrintIndent() {
        def method = AjNaiveASTFlattener.class.getDeclaredMethod("printIndent")
        method.accessible = true
        method.invoke(this)
    }
}
