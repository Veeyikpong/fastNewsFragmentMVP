package com.veeyikpong.fastnews.ui.searchnews.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.veeyikpong.fastnews.*
import com.veeyikpong.fastnews.ui.searchnews.search.adapter.NewsAdapter
import kotlinx.android.synthetic.main.fragment_search.*
import android.widget.EditText
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.veeyikpong.fastnews.api.response.News
import com.veeyikpong.fastnews.ui.searchnews.details.NewsDetailsFragment
import kotlinx.android.synthetic.main.activity_main.*


class SearchFragment : Fragment(), SearchContract.View {

    private lateinit var presenter: SearchContract.Presenter
    private lateinit var newsAdapter: NewsAdapter

    override fun setPresenter(presenter: SearchContract.Presenter) {
        this.presenter = presenter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setPresenter(SearchPresenter(this))
        newsAdapter = NewsAdapter(requireContext(),ArrayList())
        newsAdapter.setOnItemClickListener(object: NewsAdapter.OnItemClickListener{
            override fun onItemClick(news: News) {
                val bundle = Bundle()
                bundle.putSerializable(AppConstants.BUNDLE_KEY_NEWS,news)
                requireActivity().fragmentContainer.addFragment(NewsDetailsFragment(),bundle)
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        presenter.init()

        btn_search.setOnClickListener {
            search()
        }

        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                search()

                return false
            }
        })

        with(rv_news){
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(DividerItemDecoration(requireContext(),DividerItemDecoration.VERTICAL))
        }
    }

    fun search(){
        if(validate()){
            searchView.clearFocus()
            presenter.searchNews(searchView.query.toString())
        }
    }

    fun validate(): Boolean{
        var validated = true

        if(searchView.query.isBlank()){
            validated = false
            searchView.context.resources.getIdentifier("android:id/search_src_text", null, null)
            val editText = searchView.findViewById(id) as EditText
            editText.error = getString(R.string.err_search_text_empty)
        }

        return validated
    }

    override fun displayTotalResult(totalResult: String) {
        tv_totalResult.text = getString(R.string.results_found,totalResult)
    }

    override fun showNews(newsList: List<News>) {
        if(::newsAdapter.isInitialized) {
            newsAdapter.updateList(newsList)
        }
        ll_result.visibility = View.VISIBLE
    }

    override fun showError(errorMessage: String) {
        if(errorMessage.isBlank()) {
            Toast.makeText(requireContext(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
        }
    }

    override fun showLoading() {
        progressBar.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        progressBar.visibility = View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()
    }
}
