package com.buinak

import net.lingala.zip4j.core.ZipFile
import net.lingala.zip4j.exception.ZipException
import net.lingala.zip4j.exception.ZipExceptionConstants
import net.lingala.zip4j.model.FileHeader
import java.io.File
import java.io.IOException

object Verifier {
    fun verify(path: String, password: String): Boolean {
        try {
            val zipFile = ZipFile(File(path))
            if (zipFile.isEncrypted) {
                zipFile.setPassword(password)
            }
            val fileHeaders = zipFile.fileHeaders as List<FileHeader>

            for (fileHeader in fileHeaders) {
                val `is` = zipFile.getInputStream(fileHeader)
                val b = ByteArray(4 * 4096)
                while (`is`.read(b) != -1) {
                    //Do nothing as we just want to verify password
                }
                `is`.close()
                return true
            }
        } catch (e: Exception) {
            return false
        }

        return true
    }
}