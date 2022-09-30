package com.example.encryption

import java.security.MessageDigest


object ShaCrypt {
    @JvmStatic fun shaEncrypt(str:String) = SHA256.encrypt(str)
}

private object SHA256{

    /***
     *
     */
    fun encrypt(str:String):String{
        val bytes = MessageDigest.getInstance("SHA-256").digest(str.toByteArray())
        return toHexString(bytes)
    }

    /***
     * 코틀린에서는 CharArray + Shift방식의 성능이 가능 짧은 시간 hex변환 가능
     */
    fun toHexString(byteArray: ByteArray): String {
        val digits = "0123456789ABCDEF"
        val hexChars = CharArray(byteArray.size * 2)
        for (i in byteArray.indices) {
            val v = byteArray[i].toInt() and 0xff
            hexChars[i * 2] = digits[v shr 4]
            hexChars[i * 2 + 1] = digits[v and 0xf]
        }
        return String(hexChars)
    }

}