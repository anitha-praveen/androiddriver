package com.rodaClone.driver.ut

import android.animation.ObjectAnimator
import android.animation.TypeEvaluator
import android.util.Property
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker


object MarkerAnimation {

    fun animateMarker(
        marker: Marker?,
        finalPosition: LatLng?,
        latLngInterpolator: LatLngInterpolator
    ) {
        val typeEvaluator: TypeEvaluator<LatLng> =
            TypeEvaluator { fraction, startValue, endValue ->
                latLngInterpolator.interpolate(
                    fraction,
                    startValue!!,
                    endValue!!
                )
            }
        val property: Property<Marker, LatLng> = Property.of(
            Marker::class.java,
            LatLng::class.java, "position"
        )
        val animator = ObjectAnimator.ofObject(marker, property, typeEvaluator, finalPosition)
        animator.duration = 3000
        animator.start()
    }
}