package com.rodaa.client.drawer.searchPlace.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rodaClone.driver.base.BaseViewHolder
import com.rodaClone.driver.connection.ConnectionHelper
import com.rodaClone.driver.connection.FavPlace
import com.rodaClone.driver.databinding.SearchPlaceAdapterBinding
import com.rodaClone.driver.drawer.searchPlace.SearchPlaceNavigator
import com.rodaClone.driver.ut.SessionMaintainence

class SearchPlaceAdapter(private val favPlaceList: MutableList<FavPlace.Favourite>, private val session: SessionMaintainence,
                         private val mConnect: ConnectionHelper, private val navigator : SearchPlaceNavigator
) :
    RecyclerView.Adapter<BaseViewHolder>()  {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ChildViewHolder {
        val binding = SearchPlaceAdapterBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false)
        return ChildViewHolder(binding,favPlaceList,session,mConnect,navigator)
    }




    override fun getItemCount(): Int {
        return favPlaceList.size
    }

    fun addList(favPlace: List<FavPlace.Favourite>) {
        favPlaceList.clear()
        favPlaceList.addAll(favPlace)
        notifyDataSetChanged()
    }

    class ChildViewHolder(private val mBinding: SearchPlaceAdapterBinding ,  private val favPlaceList: MutableList<FavPlace.Favourite>, private val session: SessionMaintainence,
                          private val mConnect: ConnectionHelper,  private val navigator : SearchPlaceNavigator) :
        BaseViewHolder(mBinding.root), ChildSearchPlaceViewModel.ChildSearchPlaceItemListener {
        private var childPlaceFavViewModel: ChildSearchPlaceViewModel? = null
        override fun onBind(position: Int) {
            val favPlace: FavPlace.Favourite = favPlaceList[position]
            childPlaceFavViewModel = ChildSearchPlaceViewModel(favPlace,this,session , mConnect)
            mBinding.viewModel = childPlaceFavViewModel
            mBinding.executePendingBindings()
        }

        override fun itemSelected(favPlace: FavPlace.Favourite?) {
            navigator.itemSelected(favPlace)
        }

        override fun isNetworkConnected(): Boolean {
            /*
            This method is not used in this adapter
             */
            return false
        }

        override fun showNetworkUnAvailable() {

        }




    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

}