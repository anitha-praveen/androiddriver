package com.rodaa.client.drawer.searchPlace.adapter

import androidx.databinding.ObservableField
import com.rodaClone.driver.base.BaseVM
import com.rodaClone.driver.base.BaseViewOperator
import com.rodaClone.driver.connection.BaseResponse
import com.rodaClone.driver.connection.ConnectionHelper
import com.rodaClone.driver.connection.CustomException
import com.rodaClone.driver.connection.FavPlace
import com.rodaClone.driver.drawer.searchPlace.SearchPlaceNavigator
import com.rodaClone.driver.ut.SessionMaintainence
import retrofit2.Call


class ChildSearchPlaceViewModel(
    var favPlace: FavPlace.Favourite, adapterLister: ChildSearchPlaceItemListener, session: SessionMaintainence,
    mConnect: ConnectionHelper
) :
    BaseVM<BaseResponse, SearchPlaceNavigator>(session, mConnect) {
    var place: ObservableField<String> = ObservableField(favPlace.address)
    var title: ObservableField<String> = ObservableField(favPlace.title)
    var adapterlister: ChildSearchPlaceItemListener = adapterLister

    /** favourite icon tapped on places search result  */
    fun onFavSelected() {
        adapterlister.itemSelected(favPlace)
    }




    val map: HashMap<String, String>?
        get() = null

    /** interface for places search result  */
    interface ChildSearchPlaceItemListener : BaseViewOperator {
        fun itemSelected(favPlace: FavPlace.Favourite?)
    }

    override fun onSuccessfulApi(response: BaseResponse?) {

    }

    override fun onFailureApi(call: Call<BaseResponse?>?, t: CustomException?) {

    }

}

