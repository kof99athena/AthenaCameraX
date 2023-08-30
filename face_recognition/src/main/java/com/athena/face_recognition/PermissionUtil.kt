package com.athena.face_recognition

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

//20. 퍼미션을 받는 object 만들기 (체크와 리퀘스트 요구하는거)
object PermissionUtil {


    //21. 퍼미션 받았는지 안받았는지 확인
    fun checkPermission(context : Context, permissionList : List<String>) : Boolean {
        permissionList.forEach {
            //퍼미션 받았는지 안받았는지 확인
            if(ContextCompat.checkSelfPermission(
                context,
                it //너가 받은 it 즉 퍼미션
            )== PackageManager.PERMISSION_DENIED){
                //퍼미션 안받았으면 false

                return false
            }
        } //forEach문

        return true //퍼미션 받았으면 true

    } //checkPermission method

    //22. 체크 완료 했으면 퍼미션 요구해야징
    fun requestPermission(activity:Activity , permissionList: List<String>){
        ActivityCompat.requestPermissions(activity,permissionList.toTypedArray(),1)
        //request 코드 일단 아무거나 넣기, 나중에는 사용처에 맞게 넣어야함..
    }
}