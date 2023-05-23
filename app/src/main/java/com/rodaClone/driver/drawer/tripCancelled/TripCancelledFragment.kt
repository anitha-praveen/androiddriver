package com.rodaClone.driver.drawer.tripCancelled

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.rodaClone.driver.BR
import com.rodaClone.driver.R
import com.rodaClone.driver.base.BaseFragment
import com.rodaClone.driver.databinding.FragmentTripCancelBinding
import com.rodaClone.driver.drawer.DrawerActivity
import com.rodaClone.driver.ut.Config
import javax.inject.Inject


class TripCancelledFragment : BaseFragment<FragmentTripCancelBinding, TripCancelledVM>(),
    TripCancelledNavigator {
    companion object {
        const val TAG = "TripCancelledDialog"
    }

    private lateinit var binding: FragmentTripCancelBinding
    var mMediaPlayer: MediaPlayer? = null
    //val args: TripCancelledFragmentArgs by navArgs()

    @Inject
    lateinit var vmProviderFactory: ViewModelProvider.Factory
    private val vm by lazy {
        ViewModelProvider(this, vmProviderFactory).get(TripCancelledVM::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                close()
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = getmBinding()
        vm.setNavigator(this)
        vm.mode.set("${arguments?.getInt(Config.mode)}")
    }


    override fun close() {
        if (arguments?.getInt(Config.mode)== 1)
            (requireActivity() as DrawerActivity).navigateFirstTabWithClearStack()
        else
            (requireActivity() as DrawerActivity).reqProgress()
    }


    private fun voiceMsg() {
        val audioManager = requireActivity().getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND)

        if (arguments?.getInt(Config.mode) == 1)
            mMediaPlayer = MediaPlayer.create(activity, R.raw.speech)
        else if (arguments?.getInt(Config.mode) == 2)
            mMediaPlayer = MediaPlayer.create(activity, R.raw.pickup_change)
        else
            mMediaPlayer = MediaPlayer.create(activity, R.raw.drop_change)

        if (!mMediaPlayer!!.isPlaying) {
            mMediaPlayer!!.setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            mMediaPlayer!!.isLooping = true
            mMediaPlayer!!.start()
        }
    }

    override fun onResume() {
        super.onResume()
        voiceMsg()
    }

    override fun onPause() {
        super.onPause()
        if (mMediaPlayer != null) {
            mMediaPlayer!!.stop()
            mMediaPlayer!!.reset()
        }
    }

    override fun onStop() {
        super.onStop()
        if (mMediaPlayer != null) mMediaPlayer!!.stop()
    }

    override fun onDestroy() {
        if (mMediaPlayer != null) mMediaPlayer!!.stop()
        super.onDestroy()
    }

    override fun getLayoutId() = R.layout.fragment_trip_cancel


    override fun getBR() = BR.viewModel


    override fun getVMClass() = vm

}