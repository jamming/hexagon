package com.hexagonkt.templates

import com.hexagonkt.helpers.Glob
import com.hexagonkt.helpers.toDate
import com.hexagonkt.helpers.Jvm
import com.hexagonkt.injection.InjectionManager.injector
import java.time.LocalDateTime
import java.util.Locale

/**
 * The TemplateManager handles multiple templates engines.
 */
object TemplateManager {

    var adapters: Map<Regex, TemplatePort> = injectTemplateAdapters()

    internal fun injectTemplateAdapters(): Map<Regex, TemplatePort> =
        injector.injectMap<TemplatePort>()
            .mapKeys {
                when (val key = it.key) {
                    is String -> key.toRegex()
                    is Glob -> key.regex
                    is Regex -> key
                    else -> error("Adapter bound to invalid tag: ${key::class.qualifiedName}")
                }
            }

    /**
     * Render a template with a registered template engine.
     *
     * @param resource selects engine and template, i.e. "html:template.html" uses the
     * engine registered under prefix "html" to render the template "template.html"
     *
     * @throws IllegalArgumentException if no engine for prefix was found
     */
    fun render(resource: String, context: Map<String, *>, locale: Locale = Jvm.locale): String {
        return render(findAdapter(resource), resource, context, locale)
    }

    fun render(
        adapter: TemplatePort,
        resource: String,
        context: Map<String, *>,
        locale: Locale = Jvm.locale
    ): String {

        val now = LocalDateTime.now().toDate()
        val defaultProperties = mapOf("_template_" to resource, "_now_" to now)
        return adapter.render(resource, context + defaultProperties, locale)
    }

    fun render(
        resource: String,
        locale: Locale = Jvm.locale,
        vararg context: Pair<String, *>
    ): String =
        render(findAdapter(resource), resource, locale, *context)

    fun render(
        adapter: TemplatePort,
        resource: String,
        locale: Locale = Jvm.locale,
        vararg context: Pair<String, *>
    ): String =
        render(adapter, resource, linkedMapOf(*context), locale)

    private fun findAdapter(resource: String): TemplatePort =
        adapters
            .filter { it.key.matches(resource) }
            .firstNotNullOfOrNull { it.value  }
            ?: throw IllegalArgumentException("No adapter found for resource: $resource")
}
