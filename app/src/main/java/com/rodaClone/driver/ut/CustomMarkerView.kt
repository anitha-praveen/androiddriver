package com.rodaClone.driver.ut

import android.content.Context
import android.view.View
import android.widget.TextView
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.highlight.Highlight
import com.rodaClone.driver.R


class CustomMarkerView(context: Context?, layoutResource: Int) :
    MarkerView(context, layoutResource) {
    private val tvContent: TextView = findViewById<View>(R.id.tvContent) as TextView

    // callbacks everytime the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    fun refreshContent(e: Map.Entry<*, *>, highlight: Highlight?) {
        tvContent.text = "${e.value}"  // set the entry-value as the display text
    }

    fun getXOffset(xpos: Float): Int {
        // this will center the marker-view horizontally
        return -(width / 2)
    }

    fun getYOffset(ypos: Float): Int {
        // this will cause the marker-view to be above the selected value
        return -height
    }

    init {
        // this markerview only displays a textview
    }
}