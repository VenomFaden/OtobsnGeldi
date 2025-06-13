package com.bitnays.otobsngeldi

import org.chromium.net.UploadDataProvider
import org.chromium.net.UploadDataSink
import java.nio.ByteBuffer

class DataUploadDataProvider(private val data: ByteArray) : UploadDataProvider() {
    override fun getLength(): Long {
            TODO("Not yet implemented")
    }

    override fun read(
        uploadDataSink: UploadDataSink?,
        byteBuffer: ByteBuffer?
    ) {
        TODO("Not yet implemented")
    }

    override fun rewind(uploadDataSink: UploadDataSink?) {
        TODO("Not yet implemented")
    }
}