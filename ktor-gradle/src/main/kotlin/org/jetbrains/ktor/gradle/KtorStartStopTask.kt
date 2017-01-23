package org.jetbrains.ktor.gradle

import org.gradle.api.plugins.*
import org.gradle.api.tasks.*
import java.io.*
import java.net.*

open class KtorStartStopTask : AbstractStartStopTask<Int>() {
    @Input
    var start: Boolean = true

    @Internal
    private val ext = project.extensions.getByType(KtorExtension::class.java)

    @get:Input
    val port: Int
        get() = ext.port ?: throw IllegalArgumentException("ktor port not configured")

    @get:Input
    val jvmOptions: Array<String>
        get() = ext.jvmOptions

    @get:Input
    val workDir: File
        get() = ext.workDir?.let { project.file(it) } ?: project.projectDir

    private val logTailer = LogTail({ serverLog().toPath() })

    @Input
    var shutdownPath = "/ktor/application/shutdown"

    @Input
    var mainClass = "org.jetbrains.ktor.jetty.DevelopmentHost"

    init {
        logTailer.rememberLogStartPosition()
    }

    override val identifier = "ktor"
    override fun checkIsRunning(stopInfo: Int?): Boolean {
        return stopInfo != null &&
                try {
                    Socket("localhost", stopInfo).close()
                    true
                } catch (e: IOException) {
                    false
                }
    }

    override fun beforeStart() = port

    override fun serverLog() = project.buildDir.resolve("$identifier-$port.log")

    override fun builder(): ProcessBuilder {
        return ProcessBuilder(
                listOf("java", "-cp")
                        + (project.configurations.flatMap { it.files.filter { it.canRead() && it.extension == "jar" } }
                        + project.convention.findPlugin(JavaPluginConvention::class.java).sourceSets.getByName("main").output.toList()
                        )
                        .distinct().joinToString(File.pathSeparator) { it.absolutePath }
                        + listOf(
                        "-Dktor.deployment.port=$port",
                        "-Dktor.deployment.autoreload=true",
                        "-Dktor.deployment.shutdown.url=$shutdownPath")
                        + jvmOptions
                        + mainClass
        ).directory(workDir)
    }

    override fun stop(state: Int?) {
        if (state != null) {
            try {
                URL("http://localhost:$port$shutdownPath").readText()
            } catch (ignore: IOException) {
            }
        }
    }

    override fun readState(file: File) = file.readText().trim().toInt()
    override fun writeState(file: File, state: Int) {
        file.writeText("$state\n")
    }

    @TaskAction
    fun start() {
        if (start) {
            try {
                doStart()
            } catch (t: Throwable) {
                logTailer.dumpLog()
                throw t
            }
        } else {
            doStop()
        }
    }
}