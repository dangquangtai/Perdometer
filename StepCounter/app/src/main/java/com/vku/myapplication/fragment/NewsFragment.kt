package com.vku.myapplication.fragment

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.vku.myapplication.R


lateinit var wb_webView: WebView

class NewsFragment : Fragment() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_news, container, false)
        wb_webView = view.findViewById(R.id.wb_webView)
        webViewSetup()

        return view
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun webViewSetup() {
        wb_webView.webViewClient = WebViewClient()
        wb_webView.apply {
            loadUrl("https://vnexpress.net/tag/van-dong-93093")
            settings.javaScriptEnabled = true
            settings.safeBrowsingEnabled = true

        }
    }


}