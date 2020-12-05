package com.dtf.lottery.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dtf.lottery.adapter.UsersAdapter
import com.dtf.lottery.databinding.ActivityMainBinding
import com.dtf.lottery.model.User
import com.dtf.lottery.network.NetworkRepository

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var winner1: User
    private lateinit var winner2: User
    private lateinit var winner3: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()
        val adapter = UsersAdapter(this)
        binding.rvUsers.adapter = adapter
        binding.rvUsers.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        binding.rvUsers.addItemDecoration(DividerItemDecoration(this, RecyclerView.VERTICAL))
        val networkRepository = NetworkRepository()
        networkRepository.users.observe(this) { adapter.setUsers(it) }
        networkRepository.start()

        binding.btnSelect.setOnClickListener {
            winner1 = adapter.users.random()
            winner2 = adapter.users.random()
            winner3 = adapter.users.random()

            binding.tvWinner1.text = winner1.nickname
            binding.tvWinner2.text = winner2.nickname
            binding.tvWinner3.text = winner3.nickname
        }

        binding.tvWinner1.setOnClickListener { binding.rvUsers.smoothScrollToPosition(adapter.users.indexOf(winner1)) }
        binding.tvWinner2.setOnClickListener { binding.rvUsers.smoothScrollToPosition(adapter.users.indexOf(winner2)) }
        binding.tvWinner3.setOnClickListener { binding.rvUsers.smoothScrollToPosition(adapter.users.indexOf(winner3)) }
    }
}