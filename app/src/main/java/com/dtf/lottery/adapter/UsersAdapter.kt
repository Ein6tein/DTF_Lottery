package com.dtf.lottery.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dtf.lottery.R
import com.dtf.lottery.databinding.RowContenderBinding
import com.dtf.lottery.model.User
import java.text.SimpleDateFormat
import java.util.*

class UsersAdapter(private val context: Context) : RecyclerView.Adapter<UsersAdapter.UserViewHolder>() {

    val users: MutableList<User> = mutableListOf()
    private val glide = Glide.with(context)
    private val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        return UserViewHolder(RowContenderBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.setUser(users[position])
    }

    override fun getItemCount(): Int = users.size

    fun setUsers(users: List<User>) {
        this.users.addAll(users)
        notifyDataSetChanged()
    }

    inner class UserViewHolder(private val binding: RowContenderBinding) : RecyclerView.ViewHolder(binding.root) {

        fun setUser(user: User) {
            glide.asDrawable().load(user.image).into(binding.ivIcon)

            binding.tvNickName.text = user.nickname
            binding.tvRegistrationDate.text = dateFormat.format(Date(user.dateCreated))
            binding.tvRating.text = user.rating.toString()
            binding.tvRating.setTextColor(ContextCompat.getColor(context, if (user.rating < 0) R.color.red else R.color.green))
        }
    }
}