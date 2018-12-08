package com.hexagonkt.http

fun parseQueryParameters (query: String): Map<String, String> =
    if (query.isBlank())
        mapOf()
    else
        query.split("&".toRegex())
            .map {
                val keyValue = it.split("=").map(String::trim)
                val key = keyValue[0]
                val value = if (keyValue.size == 2) keyValue[1] else ""
                key to value
            }
            .toMap(LinkedHashMap())