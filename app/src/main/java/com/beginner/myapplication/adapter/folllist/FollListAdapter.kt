package com.beginner.myapplication.adapter.folllist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.beginner.myapplication.R
import com.beginner.myapplication.features.userdetail.UserFollTabFragment
import com.beginner.myapplication.model.UserDetail
import com.beginner.myapplication.utils.handler.MainHandler

class FollListAdapter(follList: ArrayList<UserDetail?>, ctx: UserFollTabFragment) :
    RecyclerView.Adapter<FollListViewHolder>() {

    private var userFoll = follList
    private val userFollCount = follList.size
    private var isNoNet = false

    val context = ctx

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FollListViewHolder {
        val viewHolder = if (viewType == 0) {
            LayoutInflater.from(parent.context).inflate(R.layout.item_user_foll_list, parent, false)
        } else {
            LayoutInflater.from(parent.context).inflate(R.layout.fragment_no_net, parent, false)
        }
        return (FollListViewHolder(viewHolder))
    }

    override fun getItemCount(): Int {
        return userFollCount
    }

    override fun onBindViewHolder(holder: FollListViewHolder, position: Int) {
        if (position < userFollCount) {
            try {
                holder.insert(userFoll[position]!!)
            } catch (e: Throwable) {
                e.printStackTrace()
            }

            if (position == (userFollCount - 1)) {
                (context as MainHandler).stopLoad()
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (!isNoNet) 0 else 1
    }

}