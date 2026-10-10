package com.mhkprog.manhwas

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.media.ExifInterface
import android.view.WindowManager
import android.widget.FrameLayout
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.webkit.WebResourceResponse
import android.graphics.Color
import androidx.activity.ComponentActivity
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.webkit.WebViewAssetLoader
import androidx.documentfile.provider.DocumentFile
import java.util.concurrent.Executors
import org.json.JSONObject

class MainActivity : ComponentActivity() {
    private lateinit var webView: WebView
    private lateinit var assetLoader: WebViewAssetLoader
    private val io = Executors.newSingleThreadExecutor()
    private val prefs by lazy { getSharedPreferences("storage", MODE_PRIVATE) }
    private var pendingExport: String? = null
    @Volatile private var pendingCoverId: String? = null

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
            pendingCoverId?.let { imagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }
        } else pendingCoverId = null
    }

    private val imagePicker = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        val id = pendingCoverId
        pendingCoverId = null
        if (uri != null && id != null) saveCover(uri, id)
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
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = false
            isAppearanceLightNavigationBars = false
        }
        assetLoader = WebViewAssetLoader.Builder()
            .addPathHandler("/assets/", WebViewAssetLoader.AssetsPathHandler(this))
            .addPathHandler("/covers/", WebViewAssetLoader.PathHandler { path -> openCover(path) })
            .build()
        webView = WebView(this)
        webView.setBackgroundColor(Color.rgb(18, 18, 18))
        val root = FrameLayout(this).apply {
            setBackgroundColor(Color.rgb(18, 18, 18))
            addView(webView, FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT,
            ))
        }
        ViewCompat.setOnApplyWindowInsetsListener(root) { view, insets ->
            val safe = insets.getInsets(
                WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout()
            )
            view.setPadding(safe.left, safe.top, safe.right, safe.bottom)
            insets
        }
        setContentView(root)
        ViewCompat.requestApplyInsets(root)
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

    private fun coverFolder(tree: Uri, create: Boolean): DocumentFile? {
        val root = DocumentFile.fromTreeUri(this, tree) ?: return null
        var folder = root.findFile(".manhwas-covers")
        if (folder == null && create) folder = root.createDirectory(".manhwas-covers")
        if (folder != null && create && folder.findFile(".nomedia") == null) {
            folder.createFile("application/octet-stream", ".nomedia")
                ?: throw IllegalStateException("Could not hide cover folder from media galleries")
        }
        return folder
    }

    private fun openCover(path: String): WebResourceResponse? {
        if (!path.matches(Regex("cover-[A-Za-z0-9_-]{1,80}\\.jpg"))) {
            return WebResourceResponse("text/plain", "UTF-8", null)
        }
        return try {
            val folder = folderUri()?.let { coverFolder(it, create = false) } ?: return WebResourceResponse("text/plain", "UTF-8", null)
            val file = folder.findFile(path) ?: return WebResourceResponse("text/plain", "UTF-8", null)
            WebResourceResponse("image/jpeg", null, contentResolver.openInputStream(file.uri))
        } catch (_: Exception) {
            WebResourceResponse("text/plain", "UTF-8", null)
        }
    }

    private fun saveCover(sourceUri: Uri, id: String) {
        if (!id.matches(Regex("[A-Za-z0-9_-]{1,80}"))) return
        io.execute {
            var decoded: Bitmap? = null
            var oriented: Bitmap? = null
            try {
                val tree = folderUri() ?: throw IllegalStateException("Storage folder not selected")
                val folder = coverFolder(tree, create = true) ?: throw IllegalStateException("Could not create cover folder")
                val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                contentResolver.openInputStream(sourceUri)?.use { BitmapFactory.decodeStream(it, null, bounds) }
                    ?: throw IllegalStateException("Could not read selected image")
                if (bounds.outWidth <= 0 || bounds.outHeight <= 0) throw IllegalArgumentException("Invalid image")

                var sample = 1
                while (bounds.outWidth / sample > 1000 || bounds.outHeight / sample > 1400) sample *= 2
                val options = BitmapFactory.Options().apply { inSampleSize = sample; inPreferredConfig = Bitmap.Config.RGB_565 }
                decoded = contentResolver.openInputStream(sourceUri)?.use { BitmapFactory.decodeStream(it, null, options) }
                    ?: throw IllegalStateException("Could not decode selected image")

                val orientation = contentResolver.openInputStream(sourceUri)?.use {
                    ExifInterface(it).getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
                } ?: ExifInterface.ORIENTATION_NORMAL
                val degrees = when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> 90f
                    ExifInterface.ORIENTATION_ROTATE_180 -> 180f
                    ExifInterface.ORIENTATION_ROTATE_270 -> 270f
                    else -> 0f
                }
                oriented = if (degrees == 0f) decoded else Bitmap.createBitmap(
                    decoded!!, 0, 0, decoded!!.width, decoded!!.height, Matrix().apply { postRotate(degrees) }, true
                )

                val name = "cover-$id.jpg"
                folder.findFile(name)?.delete()
                val destination = folder.createFile("image/jpeg", name) ?: throw IllegalStateException("Could not create cover file")
                contentResolver.openOutputStream(destination.uri, "wt")?.use { output ->
                    if (!oriented!!.compress(Bitmap.CompressFormat.JPEG, 84, output)) throw IllegalStateException("Could not save cover")
                } ?: throw IllegalStateException("Could not open cover file")
                runOnUiThread { webView.evaluateJavascript("window.onAndroidCoverSaved(${JSONObject.quote(id)})", null) }
            } catch (_: Exception) {
                runOnUiThread { webView.evaluateJavascript("window.onAndroidCoverError(${JSONObject.quote(id)})", null) }
            } finally {
                if (oriented !== decoded) oriented?.recycle()
                decoded?.recycle()
            }
        }
    }

    private fun deleteCover(id: String) {
        if (!id.matches(Regex("[A-Za-z0-9_-]{1,80}"))) return
        val tree = folderUri() ?: return
        io.execute {
            try {
                coverFolder(tree, create = false)?.findFile("cover-$id.jpg")?.delete()
            } catch (_: Exception) { }
        }
    }

    inner class AndroidStorage {
        @JavascriptInterface fun loadData(): String? = folderUri()?.let(::readData)
        @JavascriptInterface fun saveData(json: String) { writeData(json) }
        @JavascriptInterface fun chooseFolder() = runOnUiThread {
            folderPicker.launch(Intent(Intent.ACTION_OPEN_DOCUMENT_TREE))
        }
        @JavascriptInterface fun chooseCover(id: String) = runOnUiThread {
            if (!id.matches(Regex("[A-Za-z0-9_-]{1,80}"))) return@runOnUiThread
            pendingCoverId = id
            if (folderUri() == null) folderPicker.launch(Intent(Intent.ACTION_OPEN_DOCUMENT_TREE))
            else imagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
        @JavascriptInterface fun deleteCover(id: String) { this@MainActivity.deleteCover(id) }
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
        webView.evaluateJavascript("window.handleNativeBack ? window.handleNativeBack() : false", { handled ->
            if (handled != "true") super.onBackPressed()
        })
    }
}
