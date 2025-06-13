package com.bitnays.otobsngeldi

import android.util.Log
import org.chromium.net.CronetException
import org.chromium.net.UrlRequest
import org.chromium.net.UrlResponseInfo
import java.nio.ByteBuffer
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

class RequestCallback : UrlRequest.Callback() {
    private var requestBody = StringBuilder()
    override fun onRedirectReceived(
        request: UrlRequest?,
        info: UrlResponseInfo?,
        newLocationUrl: String?
    ) {
        Log.d("saaa","1"+info.toString())

    }

    override fun onResponseStarted(
        request: UrlRequest?,
        info: UrlResponseInfo?
    ) {
        Log.d("saaa","2"+info.toString())
        if (info?.httpStatusCode== 200)
        {
            request?.read(ByteBuffer.allocateDirect(1024))
        }
    }

    override fun onReadCompleted(
        request: UrlRequest?,
        info: UrlResponseInfo?,
        byteBuffer: ByteBuffer?
    ) {
        byteBuffer?.flip()

        val byteData = ByteArray(byteBuffer?.remaining() ?: 0 )
        byteBuffer?.get(byteData)

        val chunk = byteData.toString(Charset.defaultCharset())
        requestBody.append(chunk)
        Log.d("saaa","3.3.3.3"+chunk)

        byteBuffer?.clear()
        request?.read(ByteBuffer.allocateDirect(1024))
      }

    override fun onSucceeded(
        request: UrlRequest?,
        info: UrlResponseInfo?
    ) {
        Log.d("saaa","4"+requestBody)
    }

    override fun onFailed(
        request: UrlRequest?,
        info: UrlResponseInfo?,
        error: CronetException?
    ) {
        Log.d("saaa","5"+info.toString()+error.toString())
    }
}