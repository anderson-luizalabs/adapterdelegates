/*
 * Copyright (c) 2025 LuizaLabs.
 */
package com.hannesdorfmann.adapterdelegates4.sample

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter
import com.hannesdorfmann.adapterdelegates4.sample.model.ContentItem
import com.hannesdorfmann.adapterdelegates4.sample.viewmodel.MainViewModel

/**
 * Simple MainActivity that works without custom layouts
 */
class SimpleMainActivity : AppCompatActivity() {
    private lateinit var viewModel: MainViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ListDelegationAdapter<List<ContentItem>>

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // Create RecyclerView programmatically
        recyclerView = RecyclerView(this)
        setContentView(recyclerView)

        // Apply window insets for Edge-to-Edge
        ViewCompat.setOnApplyWindowInsetsListener(recyclerView) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(
                left = systemBars.left,
                top = systemBars.top,
                right = systemBars.right,
                bottom = systemBars.bottom,
            )
            insets
        }

        setupViewModel()
        setupRecyclerView()
        observeViewModel()

        // Load data
        viewModel.loadContent()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
    }

    private fun setupRecyclerView() {
        // Create adapter with delegates
        adapter =
            ListDelegationAdapter(
                FeaturedArticleDelegate { article ->
                    Toast.makeText(this, "Clicked: ${article.title}", Toast.LENGTH_SHORT).show()
                },
                ArticleDelegate { article ->
                    Toast.makeText(this, "Article: ${article.title}", Toast.LENGTH_SHORT).show()
                },
                SectionHeaderDelegate(),
                LoadingDelegate(),
                ErrorDelegate {
                    viewModel.retry()
                },
            )

        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@SimpleMainActivity)
            this.adapter = this@SimpleMainActivity.adapter
        }
    }

    private fun observeViewModel() {
        viewModel.contentList.observe(this) { items ->
            adapter.items = items
            adapter.notifyDataSetChanged()
        }

        viewModel.errorMessage.observe(this) { message ->
            message?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
