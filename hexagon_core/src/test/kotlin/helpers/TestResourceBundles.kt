@file:Suppress("ClassName", "unused")
package com.hexagonkt.helpers

import java.net.URL

internal class SampleBundle : UrlResourceBundle(URL("classpath:sample.yml"))
internal class SampleBundle_es : UrlResourceBundle(URL("classpath:sample_es.yml"))
