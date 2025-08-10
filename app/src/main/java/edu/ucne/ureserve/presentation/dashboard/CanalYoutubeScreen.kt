package edu.ucne.ureserve.presentation.youtube

import android.annotation.SuppressLint
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import edu.ucne.ureserve.presentation.dashboard.YoutubeConstants

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun CanalYoutubeScreen() {
    Column(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    configureWebViewSettings()
                    loadUrl(YoutubeConstants.YOUTUBE_URL)
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}

private fun WebView.configureWebViewSettings() {
    settings.apply {
        javaScriptEnabled = true
        domStorageEnabled = true
        loadWithOverviewMode = true
        useWideViewPort = true
        cacheMode = YoutubeConstants.CACHE_MODE
    }
    webViewClient = WebViewClient()
}

@Preview(showBackground = true)
@Composable
fun CanalYoutubeScreenPreview() {
    CanalYoutubeScreen()
}