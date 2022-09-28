/*
 * Javalin - https://javalin.io
 * Copyright 2017 David Åse
 * Licensed under Apache 2.0: https://github.com/tipsy/javalin/blob/master/LICENSE
 */

package io.javalin.rendering.template

import gg.jte.ContentType
import gg.jte.TemplateEngine
import gg.jte.output.StringOutput
import gg.jte.resolve.DirectoryCodeResolver
import io.javalin.http.Context
import io.javalin.http.servlet.isLocalhost
import io.javalin.rendering.FileRenderer
import io.javalin.rendering.JavalinRenderer
import io.javalin.rendering.util.RenderingDependency
import io.javalin.rendering.util.Util
import java.io.File

class JavalinJte(
    private val templateEngine: TemplateEngine,
    private val isDevFunction: (Context) -> Boolean = { it.isLocalhost() }
) : FileRenderer {

    private var isDev: Boolean? = null // cached and easily accessible, is set on first request (can't be configured directly by end user)

    override fun render(filePath: String, model: Map<String, Any?>, ctx: Context): String {
        isDev = isDev ?: isDevFunction(ctx)
        val stringOutput = StringOutput()
        templateEngine.render(filePath, model, stringOutput)
        return stringOutput.toString()
    }

    companion object {
        @JvmStatic
        fun init(templateEngine: TemplateEngine? = null, isDevFunction: (Context) -> Boolean = { false }) {
            Util.throwIfNotAvailable(RenderingDependency.JTE)
            Util.throwIfNotAvailable(RenderingDependency.JTE_KOTLIN)
            val fileRenderer = JavalinJte(templateEngine ?: defaultJteEngine(), isDevFunction)
            JavalinRenderer.register(fileRenderer, ".jte", ".kte")
        }

        private fun defaultJteEngine() = TemplateEngine.create(DirectoryCodeResolver(File("src/main/jte").toPath()), ContentType.Html)
    }

}
