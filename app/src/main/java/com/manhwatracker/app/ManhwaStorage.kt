package com.manhwatracker.app

import android.content.Context
import android.net.Uri
import android.provider.DocumentsContract
import java.io.IOException

class ManhwaStorage(context: Context) {
    private val appContext = context.applicationContext
    private val resolver = appContext.contentResolver
    private val preferences = appContext.getSharedPreferences("manhwas_storage", Context.MODE_PRIVATE)

    fun selectedTree(): Uri? =
        preferences.getString(KEY_TREE_URI, null)?.let(Uri::parse)

    fun rememberTree(uri: Uri) {
        preferences.edit().putString(KEY_TREE_URI, uri.toString()).apply()
    }

    fun clearTree() {
        preferences.edit().remove(KEY_TREE_URI).apply()
    }

    fun load(tree: Uri): List<Manhwa>? {
        val file = findDataFile(tree, create = false) ?: return null
        val json = resolver.openInputStream(file)?.bufferedReader(Charsets.UTF_8)?.use { it.readText() }
            ?: throw IOException("Could not open the data file")
        return Manhwa.parseArray(json)
    }

    fun save(tree: Uri, items: List<Manhwa>) {
        val file = findDataFile(tree, create = true)
            ?: throw IOException("Could not create the data file")
        val output = resolver.openOutputStream(file, "wt")
            ?: throw IOException("Could not write the data file")
        output.bufferedWriter(Charsets.UTF_8).use {
            it.write(Manhwa.toJsonArray(items).toString(2))
        }
    }

    fun exportTo(file: Uri, items: List<Manhwa>) {
        val output = resolver.openOutputStream(file, "wt")
            ?: throw IOException("Could not write the backup file")
        output.bufferedWriter(Charsets.UTF_8).use {
            it.write(Manhwa.toJsonArray(items).toString(2))
        }
    }

    fun displayName(uri: Uri): String {
        val documentId = DocumentsContract.getTreeDocumentId(uri)
        val document = DocumentsContract.buildDocumentUriUsingTree(uri, documentId)
        resolver.query(
            document,
            arrayOf(DocumentsContract.Document.COLUMN_DISPLAY_NAME),
            null,
            null,
            null,
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                val nameIndex = cursor.getColumnIndex(DocumentsContract.Document.COLUMN_DISPLAY_NAME)
                if (nameIndex >= 0) return cursor.getString(nameIndex)
            }
        }
        return "پوشهٔ انتخاب‌شده"
    }

    private fun findDataFile(tree: Uri, create: Boolean): Uri? {
        val rootDocumentId = DocumentsContract.getTreeDocumentId(tree)
        val children = DocumentsContract.buildChildDocumentsUriUsingTree(tree, rootDocumentId)
        resolver.query(
            children,
            arrayOf(
                DocumentsContract.Document.COLUMN_DOCUMENT_ID,
                DocumentsContract.Document.COLUMN_DISPLAY_NAME,
            ),
            null,
            null,
            null,
        )?.use { cursor ->
            val idIndex = cursor.getColumnIndex(DocumentsContract.Document.COLUMN_DOCUMENT_ID)
            val nameIndex = cursor.getColumnIndex(DocumentsContract.Document.COLUMN_DISPLAY_NAME)
            while (cursor.moveToNext()) {
                if (idIndex >= 0 && nameIndex >= 0 && cursor.getString(nameIndex) == DATA_FILE) {
                    return DocumentsContract.buildDocumentUriUsingTree(tree, cursor.getString(idIndex))
                }
            }
        }

        if (!create) return null
        val root = DocumentsContract.buildDocumentUriUsingTree(tree, rootDocumentId)
        return DocumentsContract.createDocument(resolver, root, "application/json", DATA_FILE)
    }

    companion object {
        private const val KEY_TREE_URI = "tree_uri"
        private const val DATA_FILE = "manhwas.json"
    }
}
