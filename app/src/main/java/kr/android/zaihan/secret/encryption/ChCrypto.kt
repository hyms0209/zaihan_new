package com.example.encryption

import android.util.Base64
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec


object ChCrypto {
    @JvmStatic fun aesEncrypt(v:String, secretKey:String, vector: String) = AES256.encrypt(v, secretKey, vector)
    @JvmStatic fun aesDecrypt(v:String, secretKey:String, vector: String) = AES256.decrypt(v, secretKey, vector)
}


private object AES256{

    //비밀키 초기화 객체 생성
    /***
     * 암호화 객체 생성
     * @param opmpde : Cipher.ENCRYPT_MODE(암호화) ,  Cipher.DECRYPT_MODE(복호화)
     * @param secretKey : 암호화 키
     * @para, vector : 암호화 벡터값
     */
    private fun cipher(opmode:Int, secretKey:String, vector:String): Cipher {
        //비밀키 32자리 확인
        if(secretKey.length != 32) throw RuntimeException("SecretKey length is not 32 chars")
        if(vector.length != 16) throw RuntimeException("vector length is not 16 chars")

        //암호화 객체 생성 AES/CBC/Padding방식
        val c = Cipher.getInstance("AES/CBC/PKCS5Padding")
        //암호화 키
        val sk = SecretKeySpec(secretKey.toByteArray(Charsets.UTF_8), "AES")
        //초기화 백터
        val iv = IvParameterSpec(vector.toByteArray(Charsets.UTF_8))
        //초기화
        c.init(opmode, sk, iv)

        return c
    }

    //암호화 할 내용, 비밀키 받아 암호화
    fun encrypt(str:String, secretKey:String, vector:String):String{
        //암호화
        val encrypted = cipher(Cipher.ENCRYPT_MODE, secretKey, vector).doFinal(str.toByteArray(Charsets.UTF_8))
        //인코딩
        //nFlag의 값에 따라 문자열 출력결과가 달라짐(기본 : Default , NO_WRAP: 개행문자 제거 ...)
        return android.util.Base64.encodeToString(encrypted, Base64.DEFAULT)
    }

    //복호화 할 내용, 비밀키 받아 복호화
    fun decrypt(str:String, secretKey:String, vector: String):String{
        //디코딩
        val byteStr = android.util.Base64.decode(str.toByteArray(Charsets.UTF_8),Base64.DEFAULT)
        //디코딩 후 복호화
        return String(cipher(Cipher.DECRYPT_MODE, secretKey, vector).doFinal(byteStr))
    }
}