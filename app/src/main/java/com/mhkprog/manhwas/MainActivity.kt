package com.mhkprog.manhwas

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.webkit.WebViewAssetLoader
import java.util.concurrent.Executors
import org.json.JSONObject

class MainActivity : ComponentActivity() {
    private lateinit var webView: WebView
    private lateinit var assetLoader: WebViewAssetLoader
    private val io = Executors.newSingleThreadExecutor()
    private val prefs by lazy { getSharedPreferences("storage", MODE_PRIVATE) }
    private var pendingExport: String? = null

    private val folderPicker = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri = result.data?.data ?: return@registerForActivityResult
            val flags = result.data?.flags ?: 0
            contentResolver.takePersistableUriPermission(uri, flags and (Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION))
            prefs.edit().putString("tree", uri.toString()).apply()
            val existing = readData(uri)
            val name = uri.lastPathSegment?.substringAfterLast(':')?.ifBlank { "پوشهٔ انتخاب‌شده" } ?: "پوشهٔ انتخاب‌شده"
            val payload = JSONObject.quote(existing ?: "")
            webView.evaluateJavascript("window.onAndroidFolderPicked($payload, ${JSONObject.quote(name)})", null)
        }
    }

    private val exportPicker = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val value = pendingExport
        pendingExport = null
        if (result.resultCode == Activity.RESULT_OK && value != null) {
            result.data?.data?.let { uri -> contentResolver.openOutputStream(uri, "wt")?.bufferedWriter(Charsets.UTF_8)?.use { it.write(value) } }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        assetLoader = WebViewAssetLoader.Builder()
            .addPathHandler("/assets/", WebViewAssetLoader.AssetsPathHandler(this))
            .build()
        webView = WebView(this)
        setContentView(webView)
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.allowFileAccess = false
        webView.settings.allowContentAccess = false
        webView.settings.setSupportMultipleWindows(false)
        webView.webChromeClient = WebChromeClient()
        webView.webViewClient = object : WebViewClient() {
            override fun shouldInterceptRequest(view: WebView, request: WebResourceRequest) = assetLoader.shouldInterceptRequest(request.url)
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean =
                request.url.host != "appassets.androidplatform.net" || request.url.scheme != "https"
            override fun onPageFinished(view: WebView, url: String) {
                prefs.getString("tree", null)?.let { raw ->
                    val uri = Uri.parse(raw)
                    val label = uri.lastPathSegment?.substringAfterLast(':') ?: "پوشهٔ انتخاب‌شده"
                    view.evaluateJavascript("window.onAndroidStorageReady(${JSONObject.quote(label)})", null)
                }
            }
        }
        webView.addJavascriptInterface(AndroidStorage(), "AndroidStorage")
        webView.loadUrl("https://appassets.androidplatform.net/assets/www/index.html")
    }

    private fun folderUri() = prefs.getString("tree", null)?.let(Uri::parse)

    private fun readData(tree: Uri): String? {
        return try {
            val root = androidx.documentfile.provider.DocumentFile.fromTreeUri(this, tree) ?: return null
            val file = root.findFile("manhwas.json") ?: return null
            contentResolver.openInputStream(file.uri)?.bufferedReader(Charsets.UTF_8)?.use { it.readText() }
        } catch (_: Exception) { null }
    }

    private fun writeData(json: String) {
        val tree = folderUri() ?: return
        io.execute {
            try {
                val root = androidx.documentfile.provider.DocumentFile.fromTreeUri(this, tree) ?: return@execute
                val file = root.findFile("manhwas.json") ?: root.createFile("application/json", "manhwas.json") ?: return@execute
                contentResolver.openOutputStream(file.uri, "wt")?.bufferedWriter(Charsets.UTF_8)?.use { it.write(json) }
            } catch (_: Exception) { }
        }
    }

    inner class AndroidStorage {
        @JavascriptInterface fun loadData(): String? = folderUri()?.let(::readData)
        @JavascriptInterface fun saveData(json: String) { writeData(json) }
        @JavascriptInterface fun chooseFolder() = runOnUiThread {
            folderPicker.launch(Intent(Intent.ACTION_OPEN_DOCUMENT_TREE))
        }
        @JavascriptInterface fun exportBackup(json: String) = runOnUiThread {
            pendingExport = json
            val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "application/json"
                putExtra(Intent.EXTRA_TITLE, "manhwas-backup.json")
            }
            exportPicker.launch(intent)
        }
    }

    @Deprecated("Deprecated in Android")
    override fun onBackPressed() {
        webView.evaluateJavascript("document.getElementById('view-details').classList.contains('hidden')", ValueCallback { isList ->
            if (isList == "false") webView.evaluateJavascript("document.getElementById('btn-back').click()", null) else super.onBackPressed()
        })
    }
}
