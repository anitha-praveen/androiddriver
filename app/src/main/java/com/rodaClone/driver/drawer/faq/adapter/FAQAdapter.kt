package com.rodaClone.driver.drawer.faq.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import com.rodaClone.driver.R
import com.rodaClone.driver.connection.responseModels.Faq
import java.util.HashMap

class FAQAdapter(context: Context, titleList: List<String>, faqMap: HashMap<String, MutableList<Faq>>) :
    BaseExpandableListAdapter() {
    var context: Context = context
    var titleList: List<String> = titleList
    var faqMap: HashMap<String, MutableList<Faq>> = HashMap<String, MutableList<Faq>>()
    override fun getGroupCount(): Int {
        return titleList.size
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return faqMap[titleList[groupPosition]]!!.size
    }

    override fun getGroup(groupPosition: Int): Any {
        return titleList[groupPosition]
    }

    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        return faqMap[titleList[groupPosition]]!![childPosition]
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun getGroupView(
        groupPosition: Int,
        isExpanded: Boolean,
        convertView: View?,
        parent: ViewGroup
    ): View {
        val viewHolder: HeaderViewHolder
        val rowView: View
        if (convertView == null) {
            val layoutInflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            rowView = layoutInflater.inflate(R.layout.faq_list_header, parent, false)
            viewHolder = HeaderViewHolder(rowView)
            rowView.tag = viewHolder
        } else {
            rowView = convertView
            viewHolder = rowView.tag as HeaderViewHolder
        }
        viewHolder.tTitle.text = titleList[groupPosition]
        viewHolder.indicator.rotation = if (isExpanded) 0f else 270f
        viewHolder.rl.setBackgroundResource(if (isExpanded) R.drawable.ic_faq_header_expanded else R.drawable.ic_faq_header_normal)
        return rowView
    }

    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup
    ): View {
        val viewHolder: FaqViewHolder
        val rowView: View
        if (convertView == null) {
            val layoutInflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            rowView = layoutInflater.inflate(R.layout.faq_list_item, parent, false)
            viewHolder = FaqViewHolder(rowView)
            rowView.tag = viewHolder
        } else {
            rowView = convertView
            viewHolder = rowView.tag as FaqViewHolder
        }
        viewHolder.faqQuestion.setText(faqMap[titleList[groupPosition]]!![childPosition].question)
        viewHolder.faqAns.setText(faqMap[titleList[groupPosition]]!![childPosition].answer)
        viewHolder.indicator.rotation =
            if (faqMap[titleList[groupPosition]]!![childPosition].isExpanded) 0f else 270f
        viewHolder.rlAns.visibility =
            if (faqMap[titleList[groupPosition]]!![childPosition].isExpanded) View.VISIBLE else View.GONE
        viewHolder.rlQuestion.setOnClickListener { view: View? ->
            val faqItem: Faq = faqMap[titleList[groupPosition]]!![childPosition]
            faqItem.isExpanded = !faqItem.isExpanded
            notifyDataSetChanged()
        }
        return rowView
    }

    override fun isChildSelectable(groupPosition: Int, childāPosition: Int): Boolean {
        return true
    }

    inner class FaqViewHolder internal constructor(view: View) {
        var rlQuestion: View = view.findViewById(R.id.rl_faq_question)
        var rlAns: View = view.findViewById(R.id.rl_faq_ans)
        var indicator: AppCompatImageView = view.findViewById(R.id.faq_question_indicator)
        var faqQuestion: TextView = view.findViewById(R.id.faq_question)
        var faqAns: TextView = view.findViewById(R.id.faq_ans)

    }

    inner class HeaderViewHolder internal constructor(view: View) {
        var rl: View = view.findViewById(R.id.rl_faq_header)
        var tTitle: TextView = view.findViewById(R.id.faq_title)
        var indicator: ImageView = view.findViewById(R.id.faq_title_indicator)

    }

    init {
        this.faqMap = faqMap
    }
}


