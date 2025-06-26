package com.bitnays.otobsngeldi

import android.util.Log
import org.chromium.net.CronetException
import org.chromium.net.UrlRequest
import org.chromium.net.UrlResponseInfo
import java.nio.ByteBuffer
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

class RequestCallback(private val onResponse: (String) -> Unit,
                      private val onError: (CronetException?) -> Unit ) : UrlRequest.Callback() {
    private var requestBody = StringBuilder()
    override fun onRedirectReceived(
        request: UrlRequest?,
        info: UrlResponseInfo?,
        newLocationUrl: String?
    ) {}

    override fun onResponseStarted(
        request: UrlRequest?,
        info: UrlResponseInfo?
    ) {
        if (info?.httpStatusCode== 200)
        {
            request?.read(ByteBuffer.allocateDirect(1024))
        }
    }

    override fun onReadCompleted(
        request: UrlRequest?,
        info: UrlResponseInfo?,
        byteBuffer: ByteBuffer?) {
        byteBuffer?.flip()
        val byteData = ByteArray(byteBuffer?.remaining() ?: 0 )
        byteBuffer?.get(byteData)
        val chunk = byteData.toString(Charset.defaultCharset())
        requestBody.append(chunk)
        byteBuffer?.clear()
        request?.read(ByteBuffer.allocateDirect(1024))
      }

    override fun onSucceeded(
        request: UrlRequest?,
        info: UrlResponseInfo?
    ) {
        onResponse(requestBody.toString())
    }

    override fun onFailed(
        request: UrlRequest?,
        info: UrlResponseInfo?,
        error: CronetException?
    ) {
        onError(error)
    }
}