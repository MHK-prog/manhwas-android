package com.manhwatracker.app

import org.json.JSONArray
import org.json.JSONObject

enum class ReadingStatus(val label: String) {
    READING("در حال خواندن"),
    PAUSED("متوقف"),
    COMPLETED("تمام‌شده");

    companion object {
        fun fromStorage(value: String?): ReadingStatus =
            values().firstOrNull { it.name == value } ?: READING
    }
}

data class Manhwa(
    val title: String,
    val totalChapters: Int?,
    val readChapters: Set<Int>,
    val status: ReadingStatus = ReadingStatus.READING,
    val updatedAt: Long = 0L,
) {
    companion object {
        const val MAX_CHAPTERS = 5_000

        fun parseArray(json: String): List<Manhwa> {
            val array = JSONArray(json)
            return buildList(array.length()) {
                for (index in 0 until array.length()) {
                    val value = array.optJSONObject(index) ?: continue
                    val title = value.optString("title").trim().take(120)
                    if (title.isBlank()) continue

                    val rawTotal = when {
                        value.has("totalChapters") -> value.opt("totalChapters")
                        else -> value.opt("total")
                    }
                    val total = when (rawTotal) {
                        null, JSONObject.NULL -> null
                        else -> rawTotal.toString().toIntOrNull()
                            ?.takeIf { it in 1..MAX_CHAPTERS }
                    }
                    val chapters = value.optJSONArray("readChapters")
                    val read = buildSet {
                        if (chapters != null) {
                            for (chapterIndex in 0 until chapters.length()) {
                                val chapter = chapters.optInt(chapterIndex, -1)
                                if (chapter in 1..MAX_CHAPTERS && (total == null || chapter <= total)) {
                                    add(chapter)
                                }
                            }
                        }
                    }
                    add(
                        Manhwa(
                            title = title,
                            totalChapters = total,
                            readChapters = read,
                            status = ReadingStatus.fromStorage(
                                if (value.isNull("status")) null else value.optString("status"),
                            ),
                            updatedAt = value.optLong("updatedAt", 0L).coerceAtLeast(0L),
                        ),
                    )
                }
            }
        }

        fun toJsonArray(items: List<Manhwa>): JSONArray = JSONArray().apply {
            items.forEach { manhwa ->
                put(
                    JSONObject()
                        .put("title", manhwa.title)
                        .put("totalChapters", manhwa.totalChapters ?: JSONObject.NULL)
                        .put("readChapters", JSONArray().apply {
                            manhwa.readChapters
                                .asSequence()
                                .filter { it in 1..MAX_CHAPTERS }
                                .sorted()
                                .forEach(::put)
                        })
                        .put("status", manhwa.status.name)
                        .put("updatedAt", manhwa.updatedAt.coerceAtLeast(0L)),
                )
            }
        }
    }
}
