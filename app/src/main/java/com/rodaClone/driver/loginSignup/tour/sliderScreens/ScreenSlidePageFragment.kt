package com.rodaClone.driver.loginSignup.tour.sliderScreens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.rodaClone.driver.R
import com.rodaClone.driver.connection.TranslationModel

class ScreenSlidePageFragment : Fragment() {
    lateinit var title : TextView
    lateinit var title_desc : TextView
    lateinit var image : ImageView
    companion object{
        var pos:Int = 0
        var translation:TranslationModel? = null
        fun modelPos(pos:Int,translation:TranslationModel?){
            this.pos = pos
            this.translation = translation
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_screen_slide_page, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        image = view.findViewById(R.id.image)
        title = view.findViewById(R.id.title)
        title_desc = view.findViewById(R.id.description)
        when(pos){
            0 -> {
                image.setImageResource(R.drawable.ic_intro_screens_first_content)
                title.text = translation?.txt_veh_selection
                title_desc.text = translation?.veh_sel_driver_desc
            }
            1 ->{
                image.setImageResource(R.drawable.ic_intro_screen_content_second)
                title.text = translation?.txt_live_tracking
                title_desc.text = translation?.live_ride_driver_desc
            }
        }
    }
}