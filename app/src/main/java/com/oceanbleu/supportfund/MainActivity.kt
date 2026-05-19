package com.oceanbleu.supportfund

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebChromeClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.oceanbleu.supportfund.ui.theme.SupportFundTheme
import android.webkit.WebView
import android.webkit.WebViewClient
import android.webkit.WebSettings
import android.webkit.JavascriptInterface
import android.widget.Toast

class MainActivity : ComponentActivity() {
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SupportFundTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    WebViewContainer(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@SuppressLint("JavascriptInterface")
@Composable
fun WebViewContainer(modifier: Modifier = Modifier) {
    AndroidView(
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.allowFileAccess = true
                settings.cacheMode = WebSettings.LOAD_NO_CACHE
                webChromeClient = WebChromeClient()
                addJavascriptInterface(WebAppInterface(this), "AndroidBridge")
                loadUrl("file:///android_asset/supportFund.html")
            }
        },
        modifier = modifier.fillMaxSize()
    )
}

private class WebAppInterface(private val webView: WebView) {
    @JavascriptInterface
    fun sendDataToApp(message: String) {
        val data = try {
            org.json.JSONObject(message)
        } catch (e: Exception) {
            null
        }

        data?.let {
            val endpoint = it.optString("endpoint")
            val payload = it.optJSONObject("data")

            if (endpoint == "/api/auth/signin") {
                val accessToken = payload?.optString("accessToken", "")
                val refreshToken = payload?.optString("refreshToken", "")

                webView.post {
                    Toast.makeText(
                        webView.context,
                        "AccessToken: $accessToken\nRefreshToken: $refreshToken", Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}

/*
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SupportFundTheme {
        Greeting("Android")
    }
}
*/