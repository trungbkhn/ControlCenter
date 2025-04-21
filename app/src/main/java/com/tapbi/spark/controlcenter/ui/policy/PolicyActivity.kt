package com.tapbi.spark.controlcenter.ui.policy

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.tapbi.spark.controlcenter.R
import com.tapbi.spark.controlcenter.databinding.ActivityPolicyBinding

class PolicyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            // the inflating code that's causing the crash
            val binding = DataBindingUtil.setContentView<ActivityPolicyBinding>(
                this,
                R.layout.activity_policy
            )
            binding.viewHeader.tvTitle.setText(R.string.privacy_policy)
            binding.viewHeader.imBack.setOnClickListener { onBackPressed() }
            binding.webView.loadUrl(getString(R.string.link_policy))
            binding.webView.webViewClient = WebViewClient()
        } catch (e: Exception) {
            if (e.message != null && e.message!!.contains("webview")) {
                // If the system failed to inflate this view because of the WebView (which could
                // be one of several types of exceptions), it likely means that the system WebView
                // is either not present (unlikely) OR in the process of being updated (also unlikely).
                // It's unlikely but we have been receiving a lot of crashes.
                // In this case, show the user a message and finish the activity
                finish()
                try {
                    val url = getString(R.string.link_policy)
                    val i = Intent(Intent.ACTION_VIEW)
                    i.setData(Uri.parse(url))
                    startActivity(i)
                } catch (ignore: Exception) {
                }
            }
        }
    }
}