package com.rodaClone.driver.drawer.faq.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.rodaClone.driver.R
import com.rodaClone.driver.connection.responseModels.Faq

class FaqNewAdapter (private val faq:ArrayList<Faq>): RecyclerView.Adapter<FaqNewAdapter.MyViewHolder>() {
    var pressed = false
    inner class MyViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val questiontv: TextView =  view.findViewById(R.id.faq_question_helper)
        val answerTv: TextView = view.findViewById(R.id.faq_answer_helper)
        val imageView: ImageView = view.findViewById(R.id.imageView_faq_helper)
        val imageViewDown: ImageView = view.findViewById(R.id.imageView_down_faq_helper)
        val cardView: CardView = view.findViewById(R.id.card_view_faq_helper)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FaqNewAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.faq_list_helper, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: FaqNewAdapter.MyViewHolder, position: Int) {
        holder.questiontv.text = faq[position].question
        holder.answerTv.text = faq[position].answer
        holder.cardView.setOnClickListener{
            if (pressed){
                pressed = false
                holder.imageView.visibility = View.VISIBLE
                holder.imageViewDown.visibility = View.GONE
                holder.answerTv.visibility = View.GONE

            }
            else{
                pressed = true
                holder.imageView.visibility = View.GONE
                holder.imageViewDown.visibility = View.VISIBLE
                holder.answerTv.visibility = View.VISIBLE
            }
        }

    }

    override fun getItemCount(): Int {
        return faq.size
    }
}