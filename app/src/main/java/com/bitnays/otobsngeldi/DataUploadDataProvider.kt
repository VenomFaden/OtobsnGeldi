package com.bitnays.otobsngeldi

import org.chromium.net.UploadDataProvider
import org.chromium.net.UploadDataSink
import java.nio.ByteBuffer

class DataUploadDataProvider(private val data: ByteArray) : UploadDataProvider() {
    override fun getLength(): Long {
        return  0
    }

    override fun read(
        uploadDataSink: UploadDataSink?,
        byteBuffer: ByteBuffer?
    ) {

    }

    override fun rewind(uploadDataSink: UploadDataSink?) {

    }
}