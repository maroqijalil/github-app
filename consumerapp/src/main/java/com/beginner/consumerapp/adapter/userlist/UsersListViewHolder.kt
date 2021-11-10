package com.beginner.consumerapp.adapter.userlist

import android.annotation.SuppressLint
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.beginner.consumerapp.R
import com.beginner.consumerapp.model.UserDrawable
import com.beginner.consumerapp.utils.DBHelper
import com.beginner.consumerapp.utils.UserResource
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_users_list.view.*

class UsersListViewHolder(item: View) : RecyclerView.ViewHolder(item) {

    @SuppressLint("SetTextI18n")
    fun insert(user: UserResource, pos: Int) {
        with(itemView) {
            setupFavButton(this, user)
            setupAvatar(this, user)

            user_name.text = user.data.name
            user_username.text = user.data.login

            if (user.data.followers > 1) {
                user_followers.text =
                    user.data.followers.toString() + " " + this.context.getString(
                        R.string.followers
                    )
            } else if (user.data.followers <= 1) {
                user_followers.text =
                    user.data.followers.toString() + " " + this.context.getString(
                        R.string.follower
                    )
            }
            if (user.data.public_repos > 1) {
                user_repositories.text =
                    user.data.public_repos.toString() + " " + this.context.getString(
                        R.string.repositories
                    )
            } else if (user.data.public_repos <= 1) {
                user_repositories.text =
                    user.data.public_repos.toString() + " " + this.context.getString(
                        R.string.repository
                    )
            }

            fav_user_button.setOnClickListener {
                if (user.favState) {
                    (it.context as DBHelper).showAlert(
                        context.getString(R.string.del_title),
                        context.getString(R.string.del_question) + " " + user.data.login,
                        user.data, pos
                    )
                } else {
                    (it.context as DBHelper).updateFavList()
                }
            }
        }
    }

    private fun setupAvatar(v: View, user: UserResource) {
        with(v) {
            if (UserDrawable.drawable[user.data.avatar_url] != null) {
                Picasso.get()
                    .load(UserDrawable.drawable[user.data.avatar_url]!!)
                    .into(user_avatar)
            } else {
                Picasso.get()
                    .load(user.data.avatar_url)
                    .into(user_avatar)
            }
        }
    }

    private fun setupFavButton(v: View, user: UserResource) {
        with(v) {
            if (user.favState) {
                Picasso.get()
                    .load(R.drawable.baseline_favorite_black_36dp)
                    .into(fav_user_button)
            } else {
                Picasso.get()
                    .load(R.drawable.baseline_favorite_border_black_36dp)
                    .into(fav_user_button)
            }
        }
    }
}