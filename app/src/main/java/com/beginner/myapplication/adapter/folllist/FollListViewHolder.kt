package com.beginner.myapplication.adapter.folllist

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.beginner.myapplication.model.UserDetail
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_user_foll_list.view.*

class FollListViewHolder(item: View): RecyclerView.ViewHolder(item) {

    fun insert(foll: UserDetail) {
        with (itemView) {
            foll_name.text = foll.name
            foll_username.text = foll.login

            Picasso.get()
                .load(foll.avatar_url)
                .into(foll_avatar)
        }
    }
}