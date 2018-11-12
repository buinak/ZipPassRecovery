package com.buinak

import net.lingala.zip4j.core.ZipFile
import net.lingala.zip4j.exception.ZipException
import net.lingala.zip4j.exception.ZipExceptionConstants
import net.lingala.zip4j.io.ZipInputStream
import net.lingala.zip4j.model.FileHeader
import java.io.File
import java.io.IOException

class Verifier(path: String) {

    val zipFile: ZipFile = ZipFile(File(path))

    fun verify(password: String): Boolean {
        val ins: ZipInputStream
        try {
            if (zipFile.isEncrypted) {
                zipFile.setPassword(password)
            }
            val fileHeaders = zipFile.fileHeaders as List<FileHeader>

            for (fileHeader in fileHeaders) {
                ins = zipFile.getInputStream(fileHeader)
                val b = ByteArray(4 * 4096)
                while (ins.read(b) != -1) {
                    //Do nothing as we just want to verify password
                }
                ins.close()
                return true
            }
        } catch (e: Exception) {
            return false
        }

        return true
    }
}