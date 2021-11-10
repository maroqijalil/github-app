package com.beginner.consumerapp.adapter

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class ListDecoration(private val margin: Int): RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.bottom = margin - (margin/2)
        outRect.top = margin - (margin/2)
        outRect.left = margin
        outRect.right = margin
    }
}