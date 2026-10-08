package com.manhwatracker.app

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.GridView
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.Spinner
import android.widget.TextView
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import java.util.concurrent.Executors

class MainActivity : Activity() {
    private lateinit var storage: ManhwaStorage
    private val worker = Executors.newSingleThreadExecutor()
    private val mainHandler = Handler(Looper.getMainLooper())

    private lateinit var root: LinearLayout
    private lateinit var contentHost: FrameLayout
    private lateinit var folderStatus: TextView
    private lateinit var addButton: Button

    private var treeUri: Uri? = null
    private var folderName: String? = null
    private var items = mutableListOf<Manhwa>()
    private var selectedIndex = -1
    private var isLoading = false
    private var pendingExport: List<Manhwa> = emptyList()
    private var detailProgress: TextView? = null
    private var detailGrid: GridView? = null
    private var chapterAdapter: ChapterGridAdapter? = null
    private var loadMoreButton: Button? = null
    private var unknownChapterLimit = INITIAL_UNKNOWN_CHAPTERS

    private var searchQuery = ""
    private var statusFilter: ReadingStatus? = null
    private var alphabeticalSort = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = COLOR_BACKGROUND
        window.navigationBarColor = COLOR_BACKGROUND
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        storage = ManhwaStorage(this)
        buildShell()

        val savedTree = storage.selectedTree()
        if (savedTree == null) {
            renderList()
            root.post { openFolderPicker() }
        } else {
            treeUri = savedTree
            isLoading = true
            renderList()
            loadSavedFolder(savedTree)
        }
    }

    override fun onDestroy() {
        worker.shutdown()
        super.onDestroy()
    }

    @Deprecated("Deprecated in Android, kept for compatibility with the platform picker result API.")
    override fun onActivityResult(requestCode: Int, resultCode: Int, result: Intent?) {
        super.onActivityResult(requestCode, resultCode, result)
        if (resultCode != RESULT_OK) return

        when (requestCode) {
            REQUEST_FOLDER -> {
                val folderResult = result ?: return
                val uri = folderResult.data ?: return
                acceptFolder(uri, folderResult.flags)
            }
            REQUEST_EXPORT -> result?.data?.let(::exportBackup)
        }
    }

    @Deprecated("Use the platform back callback when migrating to AndroidX.")
    override fun onBackPressed() {
        if (selectedIndex >= 0) {
            selectedIndex = -1
            renderList()
        } else {
            super.onBackPressed()
        }
    }

    private fun buildShell() {
        root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutDirection = View.LAYOUT_DIRECTION_RTL
            setBackgroundColor(COLOR_BACKGROUND)
            setPadding(dp(16), dp(8), dp(16), dp(8))
            fitsSystemWindows = true
        }

        val header = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            layoutDirection = View.LAYOUT_DIRECTION_RTL
        }
        val title = makeText("مانهواها", 22f, COLOR_TEXT, bold = true)
        header.addView(title, LinearLayout.LayoutParams(0, dp(52), 1f))
        header.addView(makeButton("پوشه", primary = false) { openFolderPicker() })
        header.addView(makeButton("⋮", primary = false) { showDataMenu() }.apply {
            contentDescription = "مدیریت داده‌ها"
        })
        root.addView(
            header,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
            ),
        )

        folderStatus = makeText("پوشهٔ داده انتخاب نشده", 12f, COLOR_SUBTEXT)
        folderStatus.setPadding(0, 0, 0, dp(8))
        root.addView(folderStatus)

        val divider = View(this).apply { setBackgroundColor(COLOR_BORDER) }
        root.addView(
            divider,
            LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(1)),
        )

        addButton = makeButton("+ افزودن مانهوا", primary = true) { showManhwaEditor() }
        val addParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
        ).apply {
            topMargin = dp(12)
            bottomMargin = dp(8)
        }
        root.addView(addButton, addParams)

        contentHost = FrameLayout(this)
        root.addView(
            contentHost,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                1f,
            ),
        )

        val footer = makeText("نسخهٔ ۱٫۰٫۰", 11f, COLOR_SUBTEXT).apply {
            gravity = Gravity.CENTER
            setPadding(0, dp(8), 0, 0)
        }
        root.addView(footer)
        setContentView(root)
    }

    private fun loadSavedFolder(uri: Uri) {
        worker.execute {
            try {
                val loaded = storage.load(uri) ?: emptyList<Manhwa>().also { storage.save(uri, it) }
                val name = storage.displayName(uri)
                mainHandler.post {
                    if (isFinishing || treeUri != uri) return@post
                    items = loaded.toMutableList()
                    folderName = name
                    isLoading = false
                    renderList()
                }
            } catch (_: Exception) {
                mainHandler.post {
                    if (isFinishing || treeUri != uri) return@post
                    treeUri = null
                    storage.clearTree()
                    isLoading = false
                    folderName = null
                    renderList()
                    showToast("به پوشه یا فایل داده دسترسی نیست؛ پوشه را دوباره انتخاب کن.")
                }
            }
        }
    }

    private fun acceptFolder(uri: Uri, grantedFlags: Int) {
        val previousTree = treeUri
        val flags = grantedFlags and (
            Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        )
        val requiredFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        if (flags != requiredFlags) {
            showToast("اجازهٔ خواندن و نوشتن پوشه داده نشد.")
            return
        }
        try {
            contentResolver.takePersistableUriPermission(uri, flags)
        } catch (_: Exception) {
            showToast("دسترسی پایدار پوشه داده نشد؛ پوشهٔ دیگری انتخاب کن.")
            return
        }
        if (previousTree != null && previousTree != uri) {
            try {
                contentResolver.releasePersistableUriPermission(previousTree, flags)
            } catch (_: Exception) {
                // The old grant may already have been revoked by the provider.
            }
        }

        val previousItems = items.toList()
        treeUri = uri
        folderName = null
        isLoading = true
        storage.rememberTree(uri)
        renderList()

        worker.execute {
            try {
                val existing = storage.load(uri)
                val selectedItems = existing ?: previousItems
                if (existing == null) storage.save(uri, selectedItems)
                val name = storage.displayName(uri)
                mainHandler.post {
                    if (isFinishing || treeUri != uri) return@post
                    items = selectedItems.toMutableList()
                    folderName = name
                    isLoading = false
                    renderList()
                    showToast("داده‌ها در این پوشه ذخیره می‌شوند.")
                }
            } catch (_: Exception) {
                mainHandler.post {
                    if (isFinishing || treeUri != uri) return@post
                    try {
                        contentResolver.releasePersistableUriPermission(uri, flags)
                    } catch (_: Exception) {
                        // Ignore a grant that the provider has already revoked.
                    }
                    treeUri = null
                    storage.clearTree()
                    folderName = null
                    isLoading = false
                    renderList()
                    showToast("خواندن یا نوشتن در پوشه ممکن نشد.")
                }
            }
        }
    }

    private fun renderList() {
        selectedIndex = -1
        addButton.isEnabled = treeUri != null && !isLoading
        addButton.alpha = if (addButton.isEnabled) 1f else 0.55f
        folderStatus.text = when {
            isLoading -> "در حال آماده‌سازی پوشه…"
            treeUri == null -> "پوشهٔ داده انتخاب نشده"
            folderName.isNullOrBlank() -> "محل ذخیره: پوشهٔ انتخاب‌شده"
            else -> "محل ذخیره: " + folderName
        }

        contentHost.removeAllViews()
        if (isLoading) {
            contentHost.addView(emptyMessage("در حال خواندن داده‌ها…"))
            return
        }
        if (treeUri == null) {
            val noFolder = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER
                layoutDirection = View.LAYOUT_DIRECTION_RTL
            }
            noFolder.addView(emptyMessage("برای ذخیرهٔ دائمی، یک پوشه انتخاب کن."))
            noFolder.addView(makeButton("انتخاب پوشه", primary = true) { openFolderPicker() })
            contentHost.addView(noFolder)
            return
        }

        val page = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutDirection = View.LAYOUT_DIRECTION_RTL
        }
        val searchInput = createInput("جست‌وجو در عنوان‌ها").apply {
            setText(searchQuery)
            contentDescription = "جست‌وجو در عنوان‌های مانهوا"
        }
        page.addView(searchInput, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            dp(52),
        ))

        val filterScroller = HorizontalScrollView(this).apply {
            isHorizontalScrollBarEnabled = false
            layoutDirection = View.LAYOUT_DIRECTION_RTL
        }
        val filterRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutDirection = View.LAYOUT_DIRECTION_RTL
        }
        addFilterButton(filterRow, "همه", null)
        ReadingStatus.values().forEach { status ->
            addFilterButton(filterRow, status.label, status)
        }
        filterScroller.addView(filterRow)
        page.addView(filterScroller, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            dp(52),
        ).apply { topMargin = dp(4) })

        val sortControl = makeButton(sortLabel(), primary = false) {}
        sortControl.contentDescription = "تغییر روش مرتب‌سازی فهرست"
        page.addView(sortControl, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            dp(48),
        ).apply { topMargin = dp(4) })

        val cardsScroll = ScrollView(this).apply {
            isFillViewport = true
            layoutDirection = View.LAYOUT_DIRECTION_RTL
        }
        val cards = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutDirection = View.LAYOUT_DIRECTION_RTL
            setPadding(0, dp(10), 0, dp(12))
        }
        cardsScroll.addView(cards)
        page.addView(cardsScroll, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            0,
            1f,
        ))
        contentHost.addView(page)

        searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchQuery = s?.toString().orEmpty()
                renderCardsInto(cards)
            }
            override fun afterTextChanged(s: Editable?) = Unit
        })
        renderCardsInto(cards)
        sortControl.setOnClickListener {
            alphabeticalSort = !alphabeticalSort
            sortControl.text = sortLabel()
            renderCardsInto(cards)
        }
    }

    private fun addFilterButton(
        row: LinearLayout,
        label: String,
        filter: ReadingStatus?,
    ) {
        val selected = statusFilter == filter
        val button = makeButton(label, primary = selected) {
            statusFilter = filter
            renderList()
        }
        button.contentDescription = if (selected) label + "، فیلتر فعال" else "فیلتر " + label
        row.addView(button, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            dp(48),
        ).apply {
            if (row.childCount > 0) marginStart = dp(6)
        })
    }

    private fun sortLabel(): String =
        if (alphabeticalSort) "مرتب‌سازی: الفبایی" else "مرتب‌سازی: آخرین تغییر"

    private fun renderCardsInto(cards: LinearLayout) {
        cards.removeAllViews()
        val query = searchQuery.trim()
        val matching = items.withIndex()
            .filter { (_, manhwa) ->
                (statusFilter == null || manhwa.status == statusFilter) &&
                    (query.isEmpty() || manhwa.title.contains(query, ignoreCase = true))
            }
            .let { indexed ->
                if (alphabeticalSort) {
                    indexed.sortedBy { it.value.title.lowercase() }
                } else {
                    indexed.sortedWith(
                        compareByDescending<IndexedValue<Manhwa>> { it.value.updatedAt }
                            .thenBy { it.index },
                    )
                }
            }

        if (matching.isEmpty()) {
            val message = if (items.isEmpty()) {
                "هنوز مانهوا‌ای ثبت نکرده‌ای."
            } else {
                "موردی با این جست‌وجو یا فیلتر پیدا نشد."
            }
            cards.addView(emptyMessage(message))
            return
        }
        matching.forEach { (index, manhwa) -> cards.addView(createManhwaCard(index, manhwa)) }
    }

    private fun createManhwaCard(index: Int, manhwa: Manhwa): View {
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutDirection = View.LAYOUT_DIRECTION_RTL
            background = rounded(COLOR_SURFACE, 12f)
            setPadding(dp(14), dp(12), dp(14), dp(12))
            isClickable = true
            isFocusable = true
            setOnClickListener { openDetails(index) }
        }
        val params = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
        ).apply { bottomMargin = dp(10) }

        val top = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            layoutDirection = View.LAYOUT_DIRECTION_RTL
        }
        val title = makeText(manhwa.title, 16f, COLOR_TEXT, bold = true)
        top.addView(title, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))
        val deleteButton = makeButton("حذف", primary = false) { confirmDelete(index) }.apply {
            setTextColor(COLOR_DANGER)
            contentDescription = "حذف " + manhwa.title
        }
        top.addView(deleteButton)
        card.addView(top)

        val counterText = if (manhwa.totalChapters == null) {
            manhwa.readChapters.size.toString() + " فصل خوانده شده · تعداد کل نامشخص"
        } else {
            val percent = ((manhwa.readChapters.size * 100f) / manhwa.totalChapters)
                .toInt()
                .coerceIn(0, 100)
            manhwa.readChapters.size.toString() + " از " + manhwa.totalChapters +
                " فصل · " + percent + "٪"
        }
        val counter = makeText(counterText, 12f, COLOR_SUBTEXT)
        counter.setPadding(0, dp(6), 0, dp(4))
        card.addView(counter)

        val statusLine = makeText("وضعیت: " + manhwa.status.label, 12f, COLOR_SUBTEXT)
        statusLine.setPadding(0, 0, 0, dp(6))
        card.addView(statusLine)

        manhwa.totalChapters?.let { total ->
            val percent = ((manhwa.readChapters.size * 100f) / total).toInt().coerceIn(0, 100)
            val progress = ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal).apply {
                max = 100
                this.progress = percent
                progressTintList = ColorStateList.valueOf(COLOR_PRIMARY)
                progressBackgroundTintList = ColorStateList.valueOf(COLOR_BORDER)
                contentDescription = "پیشرفت " + percent + " درصد"
            }
            card.addView(progress, LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(6),
            ))
        }
        return card.also { it.layoutParams = params }
    }

    private fun openDetails(index: Int) {
        if (index !in items.indices) return
        selectedIndex = index
        renderDetails()
    }

    private fun renderDetails() {
        val index = selectedIndex
        val manhwa = items.getOrNull(index) ?: run {
            renderList()
            return
        }
        contentHost.removeAllViews()
        detailProgress = null
        detailGrid = null
        chapterAdapter = null
        loadMoreButton = null

        val grid = GridView(this).apply {
            numColumns = 5
            horizontalSpacing = dp(5)
            verticalSpacing = dp(5)
            stretchMode = GridView.STRETCH_COLUMN_WIDTH
            gravity = Gravity.CENTER
            layoutDirection = View.LAYOUT_DIRECTION_RTL
            isVerticalScrollBarEnabled = true
            isNestedScrollingEnabled = false
            selector = android.graphics.drawable.ColorDrawable(Color.TRANSPARENT)
            setPadding(0, dp(8), 0, dp(12))
        }
        val header = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutDirection = View.LAYOUT_DIRECTION_RTL
            setPadding(0, dp(4), 0, dp(10))
        }
        val controls = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            layoutDirection = View.LAYOUT_DIRECTION_RTL
        }
        controls.addView(makeButton("بازگشت", primary = false) {
            selectedIndex = -1
            renderList()
        })
        detailProgress = makeText("", 13f, COLOR_SUBTEXT).apply {
            gravity = Gravity.CENTER
            contentDescription = "تعداد فصل‌های خوانده‌شده"
        }
        controls.addView(detailProgress, LinearLayout.LayoutParams(0, dp(48), 1f))
        controls.addView(makeButton("ویرایش", primary = false) { showManhwaEditor(index) })
        header.addView(controls)

        header.addView(makeText(manhwa.title, 20f, COLOR_TEXT, bold = true).apply {
            setPadding(0, dp(12), 0, dp(3))
        })
        header.addView(makeText("وضعیت: " + manhwa.status.label, 13f, COLOR_SUBTEXT).apply {
            setPadding(0, 0, 0, dp(8))
        })
        header.addView(makeButton("رفتن به فصل بعدی", primary = true) {
            goToNextUnread(index)
        })
        header.addView(makeText(
            "برای تغییر وضعیت، روی شمارهٔ فصل بزن. فصل‌های خوانده‌شده رنگی هستند.",
            12f,
            COLOR_SUBTEXT,
        ).apply { setPadding(0, dp(8), 0, 0) })
        grid.addHeaderView(header, null, false)

        if (manhwa.totalChapters == null) {
            val minimumForReadChapters = (manhwa.readChapters.maxOrNull() ?: 0) + 20
            unknownChapterLimit = minOf(
                Manhwa.MAX_CHAPTERS,
                maxOf(
                    INITIAL_UNKNOWN_CHAPTERS,
                    ((minimumForReadChapters + CHAPTER_PAGE_SIZE - 1) / CHAPTER_PAGE_SIZE) *
                        CHAPTER_PAGE_SIZE,
                ),
            )
        }
        if (manhwa.totalChapters == null && unknownChapterLimit < Manhwa.MAX_CHAPTERS) {
            loadMoreButton = makeButton("نمایش ۱۰۰ فصل بعدی", primary = false) {
                expandUnknownChapters()
            }
            grid.addFooterView(loadMoreButton, null, true)
        }
        val count = manhwa.totalChapters ?: unknownChapterLimit
        chapterAdapter = ChapterGridAdapter(this) { chapter ->
            items.getOrNull(index)?.readChapters?.contains(chapter) == true
        }.apply { setChapterCount(count) }
        grid.adapter = chapterAdapter
        grid.setOnItemClickListener { _, _, position, _ ->
            val chapter = position - grid.headerViewsCount + 1
            val currentLimit = items.getOrNull(index)?.totalChapters ?: unknownChapterLimit
            if (chapter in 1..currentLimit) toggleChapter(index, chapter)
        }
        detailGrid = grid
        contentHost.addView(grid, FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT,
        ))
        updateDetailProgress(manhwa)
    }

    private fun toggleChapter(index: Int, chapter: Int) {
        val current = items.getOrNull(index) ?: return
        val updatedRead = current.readChapters.toMutableSet()
        if (!updatedRead.add(chapter)) updatedRead.remove(chapter)
        items[index] = current.copy(
            readChapters = updatedRead,
            updatedAt = System.currentTimeMillis(),
        )
        chapterAdapter?.notifyDataSetChanged()
        updateDetailProgress(items[index])
        saveCurrent()
    }

    private fun updateDetailProgress(manhwa: Manhwa) {
        detailProgress?.text = if (manhwa.totalChapters == null) {
            manhwa.readChapters.size.toString() + " فصل خوانده"
        } else {
            manhwa.readChapters.size.toString() + " / " + manhwa.totalChapters
        }
    }

    private fun goToNextUnread(index: Int) {
        val manhwa = items.getOrNull(index) ?: return
        val searchLimit = manhwa.totalChapters ?: Manhwa.MAX_CHAPTERS
        val next = (1..searchLimit).firstOrNull { it !in manhwa.readChapters }
        if (next == null) {
            showToast("همهٔ فصل‌های ثبت‌شده خوانده شده‌اند.")
            return
        }
        if (manhwa.totalChapters == null && next > unknownChapterLimit) {
            unknownChapterLimit = minOf(
                Manhwa.MAX_CHAPTERS,
                ((next + CHAPTER_PAGE_SIZE - 1) / CHAPTER_PAGE_SIZE) * CHAPTER_PAGE_SIZE,
            )
            chapterAdapter?.setChapterCount(unknownChapterLimit)
            refreshLoadMoreFooter()
        }
        detailGrid?.post {
            detailGrid?.smoothScrollToPosition((detailGrid?.headerViewsCount ?: 0) + next - 1)
        }
    }

    private fun expandUnknownChapters() {
        if (unknownChapterLimit >= Manhwa.MAX_CHAPTERS) return
        unknownChapterLimit = minOf(
            Manhwa.MAX_CHAPTERS,
            unknownChapterLimit + CHAPTER_PAGE_SIZE,
        )
        chapterAdapter?.setChapterCount(unknownChapterLimit)
        refreshLoadMoreFooter()
    }

    private fun refreshLoadMoreFooter() {
        val button = loadMoreButton ?: return
        if (unknownChapterLimit >= Manhwa.MAX_CHAPTERS) {
            detailGrid?.removeFooterView(button)
            loadMoreButton = null
        }
    }

    private fun showManhwaEditor(index: Int? = null) {
        if (treeUri == null) {
            showToast("اول پوشهٔ ذخیره‌سازی را انتخاب کن.")
            openFolderPicker()
            return
        }
        val old = index?.let(items::getOrNull)
        val form = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutDirection = View.LAYOUT_DIRECTION_RTL
            setPadding(dp(20), dp(8), dp(20), 0)
        }
        val titleInput = createInput("نام مانهوا").apply { setText(old?.title.orEmpty()) }
        val totalInput = createInput("تعداد کل فصل‌ها (اختیاری)", numberOnly = true).apply {
            setText(old?.totalChapters?.toString().orEmpty())
        }
        form.addView(titleInput)
        form.addView(totalInput, spacedInputParams())

        var readInput: EditText? = null
        if (old == null) {
            readInput = createInput("خوانده تا فصل (اختیاری)", numberOnly = true)
            form.addView(readInput, spacedInputParams())
        }

        val statusSpinner = Spinner(this).apply {
            layoutDirection = View.LAYOUT_DIRECTION_RTL
            adapter = ArrayAdapter(
                this@MainActivity,
                android.R.layout.simple_spinner_item,
                ReadingStatus.values().map { it.label },
            ).also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
            setSelection(old?.status?.ordinal ?: ReadingStatus.READING.ordinal)
            contentDescription = "وضعیت مطالعه"
        }
        form.addView(statusSpinner, spacedInputParams())

        AlertDialog.Builder(this)
            .setTitle(if (old == null) "افزودن مانهوا" else "ویرایش مانهوا")
            .setView(form)
            .setNegativeButton("انصراف", null)
            .setPositiveButton(if (old == null) "ذخیره" else "ثبت تغییرات", null)
            .create()
            .also { dialog ->
                dialog.setOnShowListener {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                        val title = titleInput.text.toString().trim().take(120)
                        val rawTotal = totalInput.text.toString().trim()
                        val total = if (rawTotal.isBlank()) null else rawTotal.toIntOrNull()
                        val readTo = readInput?.text?.toString()?.toIntOrNull() ?: 0
                        if (title.isBlank()) {
                            titleInput.error = "نام را وارد کن."
                            return@setOnClickListener
                        }
                        if (rawTotal.isNotBlank() && (total == null || total !in 1..Manhwa.MAX_CHAPTERS)) {
                            totalInput.error = "تعداد باید بین ۱ تا ۵۰۰۰ باشد یا خالی بماند."
                            return@setOnClickListener
                        }
                        if (old == null && readTo !in 0..Manhwa.MAX_CHAPTERS) {
                            readInput?.error = "شمارهٔ فصل باید بین ۰ تا ۵۰۰۰ باشد."
                            return@setOnClickListener
                        }
                        if (old == null && total != null && readTo > total) {
                            readInput?.error = "فصل خوانده‌شده نمی‌تواند از تعداد کل بیشتر باشد."
                            return@setOnClickListener
                        }

                        val status = ReadingStatus.values().getOrElse(statusSpinner.selectedItemPosition) {
                            ReadingStatus.READING
                        }
                        val readChapters = if (old == null) {
                            if (readTo <= 0) emptySet() else (1..readTo).toSet()
                        } else {
                            old.readChapters.filterTo(mutableSetOf()) {
                                total == null || it <= total
                            }
                        }
                        val updated = Manhwa(
                            title = title,
                            totalChapters = total,
                            readChapters = readChapters,
                            status = status,
                            updatedAt = System.currentTimeMillis(),
                        )
                        if (index == null) {
                            items.add(updated)
                        } else if (index in items.indices) {
                            items[index] = updated
                        }
                        dialog.dismiss()
                        if (index == null) renderList() else renderDetails()
                        saveCurrent()
                    }
                }
                dialog.show()
            }
    }

    private fun confirmDelete(index: Int) {
        val manhwa = items.getOrNull(index) ?: return
        AlertDialog.Builder(this)
            .setTitle("حذف مانهوا")
            .setMessage("«" + manhwa.title + "» حذف شود؟")
            .setNegativeButton("انصراف", null)
            .setPositiveButton("حذف") { _, _ ->
                items.removeAt(index)
                selectedIndex = -1
                renderList()
                saveCurrent()
            }
            .show()
    }

    private fun showDataMenu() {
        val actions = arrayOf("انتخاب پوشهٔ ذخیره‌سازی", "گرفتن بکاپ JSON")
        AlertDialog.Builder(this)
            .setTitle("مدیریت داده‌ها")
            .setItems(actions) { _, choice ->
                when (choice) {
                    0 -> openFolderPicker()
                    1 -> openExportPicker()
                }
            }
            .show()
    }

    private fun openFolderPicker() {
        try {
            startActivityForResult(
                Intent(Intent.ACTION_OPEN_DOCUMENT_TREE),
                REQUEST_FOLDER,
            )
        } catch (_: Exception) {
            showToast("انتخاب‌گر پوشه در این دستگاه در دسترس نیست.")
        }
    }

    private fun openExportPicker() {
        pendingExport = items.toList()
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/json"
            putExtra(Intent.EXTRA_TITLE, "manhwas-backup-" + System.currentTimeMillis() + ".json")
        }
        try {
            startActivityForResult(intent, REQUEST_EXPORT)
        } catch (_: Exception) {
            showToast("ذخیره‌سازی فایل در این دستگاه در دسترس نیست.")
        }
    }

    private fun exportBackup(file: Uri) {
        val snapshot = pendingExport
        worker.execute {
            try {
                storage.exportTo(file, snapshot)
                mainHandler.post { if (!isFinishing) showToast("بکاپ ذخیره شد.") }
            } catch (_: Exception) {
                mainHandler.post { if (!isFinishing) showToast("ذخیرهٔ بکاپ انجام نشد.") }
            }
        }
    }

    private fun saveCurrent() {
        val target = treeUri
        if (target == null) {
            showToast("برای ذخیره، یک پوشه انتخاب کن.")
            return
        }
        val snapshot = items.toList()
        worker.execute {
            try {
                storage.save(target, snapshot)
            } catch (_: Exception) {
                mainHandler.post {
                    if (!isFinishing) showToast("ذخیره‌سازی انجام نشد؛ دسترسی پوشه را بررسی کن.")
                }
            }
        }
    }

    private fun createInput(hint: String, numberOnly: Boolean = false): EditText =
        EditText(this).apply {
            this.hint = hint
            setTextColor(COLOR_TEXT)
            setHintTextColor(COLOR_SUBTEXT)
            textSize = 15f
            textDirection = View.TEXT_DIRECTION_FIRST_STRONG_RTL
            if (numberOnly) {
                inputType = android.text.InputType.TYPE_CLASS_NUMBER
            } else {
                inputType = android.text.InputType.TYPE_CLASS_TEXT or
                    android.text.InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
                isSingleLine = true
            }
        }

    private fun spacedInputParams(): LinearLayout.LayoutParams =
        LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
        ).apply { topMargin = dp(8) }

    private fun emptyMessage(message: String): TextView =
        makeText(message, 15f, COLOR_SUBTEXT).apply {
            gravity = Gravity.CENTER
            setPadding(dp(24), dp(28), dp(24), dp(28))
        }

    private fun makeText(
        value: String,
        size: Float,
        color: Int,
        bold: Boolean = false,
    ): TextView = TextView(this).apply {
        text = value
        textSize = size
        setTextColor(color)
        textDirection = View.TEXT_DIRECTION_FIRST_STRONG_RTL
        gravity = Gravity.CENTER_VERTICAL or Gravity.START
        if (bold) setTypeface(typeface, Typeface.BOLD)
    }

    private fun makeButton(
        label: String,
        primary: Boolean,
        onClick: () -> Unit,
    ): Button = Button(this).apply {
        text = label
        transformationMethod = null
        textSize = 13f
        minHeight = dp(48)
        minimumHeight = dp(48)
        minWidth = dp(0)
        minimumWidth = dp(0)
        setPadding(dp(10), 0, dp(10), 0)
        backgroundTintList = ColorStateList.valueOf(
            if (primary) COLOR_PRIMARY else COLOR_SURFACE,
        )
        setTextColor(if (primary) COLOR_BACKGROUND else COLOR_TEXT)
        isEnabled = true
        setOnClickListener { onClick() }
    }

    private fun rounded(color: Int, radiusDp: Float): GradientDrawable =
        GradientDrawable().apply {
            setColor(color)
            cornerRadius = dp(radiusDp.toInt()).toFloat()
        }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()

    companion object {
        private const val REQUEST_FOLDER = 1001
        private const val REQUEST_EXPORT = 1003
        private const val INITIAL_UNKNOWN_CHAPTERS = 100
        private const val CHAPTER_PAGE_SIZE = 100
        private val COLOR_BACKGROUND = Color.rgb(18, 18, 18)
        private val COLOR_SURFACE = Color.rgb(30, 30, 30)
        private val COLOR_TEXT = Color.rgb(238, 238, 238)
        private val COLOR_SUBTEXT = Color.rgb(170, 170, 170)
        private val COLOR_BORDER = Color.rgb(55, 55, 55)
        private val COLOR_PRIMARY = Color.rgb(187, 134, 252)
        private val COLOR_DANGER = Color.rgb(207, 102, 121)
    }
}
