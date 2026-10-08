package com.manhwatracker.app

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class ChapterGridAdapter(
    context: Context,
    private val isRead: (Int) -> Boolean,
) : BaseAdapter() {
    private val density = context.resources.displayMetrics.density
    private var chapterCount = 0

    fun setChapterCount(count: Int) {
        chapterCount = count.coerceIn(0, Manhwa.MAX_CHAPTERS)
        notifyDataSetChanged()
    }

    override fun getCount(): Int = chapterCount

    override fun getItem(position: Int): Int = position + 1

    override fun getItemId(position: Int): Long = (position + 1).toLong()

    override fun hasStableIds(): Boolean = true

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val chapter = getItem(position)
        val read = isRead(chapter)
        val cell = (convertView as? TextView) ?: TextView(parent.context).apply {
            gravity = Gravity.CENTER
            textSize = 14f
            minHeight = dp(48)
            minimumHeight = dp(48)
            isFocusable = false
            isClickable = false
            layoutParams = android.widget.AbsListView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(52),
            )
        }
        cell.text = chapter.toString()
        cell.contentDescription = if (read) {
            "فصل " + chapter + "، خوانده‌شده"
        } else {
            "فصل " + chapter + "، نخوانده"
        }
        cell.setTextColor(if (read) COLOR_BACKGROUND else COLOR_TEXT)
        cell.setTypeface(null, if (read) Typeface.BOLD else Typeface.NORMAL)
        cell.background = rounded(if (read) COLOR_PRIMARY else COLOR_SURFACE)
        return cell
    }

    private fun rounded(color: Int): GradientDrawable =
        GradientDrawable().apply {
            setColor(color)
            cornerRadius = dp(8).toFloat()
        }

    private fun dp(value: Int): Int = (value * density).toInt()

    companion object {
        private val COLOR_BACKGROUND = android.graphics.Color.rgb(18, 18, 18)
        private val COLOR_SURFACE = android.graphics.Color.rgb(30, 30, 30)
        private val COLOR_TEXT = android.graphics.Color.rgb(238, 238, 238)
        private val COLOR_PRIMARY = android.graphics.Color.rgb(187, 134, 252)
    }
}
