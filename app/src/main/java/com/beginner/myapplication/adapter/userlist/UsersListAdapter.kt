package com.beginner.myapplication.adapter.userlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.beginner.myapplication.R
import com.beginner.myapplication.utils.UserResource
import com.beginner.myapplication.utils.handler.MainHandler

class UsersListAdapter(list: ArrayList<UserResource>) : RecyclerView.Adapter<UsersListViewHolder>() {

    private var usersList = list
    private var usersListSize = list.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersListViewHolder {
        val viewHolder =
            LayoutInflater.from(parent.context).inflate(R.layout.item_users_list, parent, false)
        return UsersListViewHolder(viewHolder)
    }

    override fun getItemCount(): Int {
        return usersListSize
    }

    override fun onBindViewHolder(holder: UsersListViewHolder, position: Int) {
        if (position < usersListSize) {
            try {
                val userItem = usersList[position]

                holder.insert(userItem, position)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            if (position == (usersListSize - 1)) {
                (holder.itemView.context as MainHandler).stopLoad()
            }
        }
    }

    fun changeUsersList(newUsersList: ArrayList<UserResource>) {
        usersList = newUsersList
        usersListSize = usersList.size

        notifyDataSetChanged()
    }

    fun deleteUser(position: Int) {
        usersList.removeAt(position)
        usersListSize = usersList.size
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, usersListSize)
    }

    fun deleteUserFromFav(position: Int) {
        usersList[position].favState = false
        notifyItemChanged(position)
    }
}

