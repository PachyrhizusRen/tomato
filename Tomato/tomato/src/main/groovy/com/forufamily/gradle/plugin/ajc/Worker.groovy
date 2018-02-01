package com.forufamily.gradle.plugin.ajc

import com.forufamily.gradle.plugin.config.Configurations
import com.forufamily.gradle.plugin.tasks.AjFileProcessTask
import org.aspectj.bridge.IMessage
import org.aspectj.bridge.MessageHandler
import org.aspectj.tools.ajc.Main
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.logging.Logger
import org.gradle.api.tasks.compile.JavaCompile

class Worker {
    private final Logger log
    private List<File> inPath = new ArrayList<>()
    private List<File> aspectPath = new ArrayList<>()
    private List<File> classPath = new ArrayList<>()
    private List<String> ajcArgs = new ArrayList<>()

    private String destination
    private String encoding
    private String bootClassPath
    private String sourceCompatibility
    private String targetCompatibility

    Worker(Project project) {
        log = project.logger

        // call .aj files
        AjFileProcessTask.attach(project)

        project.afterEvaluate {
            def configuration = new Configurations(project)
            configuration.variants.all { variant ->
                JavaCompile javaCompile = variant.hasProperty('javaCompiler') ? variant.javaCompiler : variant.javaCompile
                encoding = javaCompile.options.encoding
                bootClassPath = configuration.bootClasspath.join(File.pathSeparator)
                sourceCompatibility = javaCompile.sourceCompatibility
                targetCompatibility = javaCompile.targetCompatibility
            }
        }
        ajcArgs.addAll project.tomato.ajcArgs ?: []
    }

    def getInPath() {
        inPath
    }

    def getAspectPath() {
        aspectPath
    }

    def getClassPath() {
        classPath
    }

    def getBootClassPath() {
        bootClassPath
    }

    def config(Closure closure) {
        closure.delegate = this
        closure()
    }

    void doWork() {
        // 如果输入路径为空，不需要执行，如果执行会抛出"no sources specified"错误
        if (inPath.isEmpty()) {
            "inPath集合中没有数据，放弃织入-------------".info()
            return
        }

        //http://www.eclipse.org/aspectj/doc/released/devguide/ajc-ref.html
        "织入结果放入:${destination}".info()
        List<String> args = [
                "-showWeaveInfo",
                "-encoding", encoding,
                "-source", sourceCompatibility,
                "-target", targetCompatibility,
                "-d", destination,
                "-classpath", classPath.join(File.pathSeparator),
                "-bootclasspath", bootClassPath
        ]

        if (!inPath.isEmpty()) {
            args << '-inpath' << inPath.join(File.pathSeparator)
        }
        if (!aspectPath.isEmpty()) {
            args << '-aspectpath' << aspectPath.join(File.pathSeparator)
        }

        if (!ajcArgs.isEmpty()) {
            if (!ajcArgs.contains('-Xlint')) {
                args << '-Xlint:ignore'
            }
            if (!ajcArgs.contains('-warn')) {
                args << '-warn:none'
            }
            args.addAll ajcArgs
        } else {
            args << '-Xlint:ignore' << '-warn:none'
        }
        //"参数列表:$args".info()

        MessageHandler handler = new MessageHandler(false)
        handler.dontIgnore(IMessage.WEAVEINFO)
        Main m = new Main()
        m.run(args as String[], handler)
        for (IMessage message : handler.getMessages(null, true)) {
            switch (message.getKind()) {
                case IMessage.ABORT:
                case IMessage.ERROR:
                case IMessage.FAIL:
                    log.error message.message, message.thrown
                    throw new GradleException(message.message, message.thrown)
                case IMessage.WARNING:
                    log.warn message.message, message.thrown
                    break
                case IMessage.WEAVEINFO:
                    "${message.message}".info()
                    log.info message.message, message.thrown
                    break
                case IMessage.INFO:
                    log.info message.message, message.thrown
                    break
                case IMessage.DEBUG:
                    log.debug message.message, message.thrown
                    break
            }
        }
        m.quit()
    }
}
