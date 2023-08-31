package com.athena.face_recognition

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.app.ComponentActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner

import com.athena.face_recognition.recognition.FaceAnalyzer
import com.athena.face_recognition.recognition.FaceAnalyzerListener
import com.google.common.util.concurrent.ListenableFuture
import java.lang.Exception
import java.util.concurrent.Executors
/* 카메라 프리뷰를 세팅할 수 있는 class */

//25. 카메라 권한이 없다가 권한이 다시 생긴 경우! : ActivityCompat.OnRequestPermissionsResultCallback

class Camera(private val context:Context) : ActivityCompat.OnRequestPermissionsResultCallback {



    //11. Camera class 생성. context 생성자

    //12. 프리뷰 늦은 초기화하기
    private val preview by lazy{
        Preview.Builder()
            .build()
            .also{
                it.setSurfaceProvider(previewView.surfaceProvider)
                //setSurfaceProvider : 어떤 Surface를 렌더링 할건지?
            }
    }

    //13. 카메라를 전면으로 설정하기 (전면이 선택될 수 있도록, 기본은 후면이다.. )
    private val cameraSelector by lazy {
        CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
            .build()
    }

    //14. ProcessCameraProvider 객체의 역할 : Camera의 생명주기를 Activity와 같은 LifeCycleOwner의 생명주기에 Binding 시킨다
    private lateinit var cameraProviderFuture : ListenableFuture<ProcessCameraProvider>
    private lateinit var previewView: PreviewView //12

    //15.
    private var cameraExecutor = Executors.newSingleThreadExecutor()

    //43. 이제 실제 이 함수들을 메인에 연결하고 뷰에 노출을 시키자.
    private var listener : FaceAnalyzerListener?=null



    //16. 메인액티비티에서 layout을 받아오기 - 여기에 프리뷰를 설정한다.
    //우리로 따지면 만들어둔 framelayout
    fun initCamera(layout: ViewGroup, listener: FaceAnalyzerListener){
        //44. 파라미터에 listener 받아오기
        //45. 리스너 받아오고
        this.listener = listener


        previewView = PreviewView(context)
        layout.addView(previewView)

        //24
        permissonCheck(context)
    }


    //23. PermissonUtil을 가지고 퍼미션 체크하기
    private fun permissonCheck(context: Context){
        val permissionList = listOf(Manifest.permission.CAMERA)
        if (!PermissionUtil.checkPermission(context,permissionList)){
            //퍼미션 안받아오면 PermissionUtil이 리퀘스트하게 해줭
            PermissionUtil.requestPermission(context as Activity, permissionList)
        } else {
            openPreview()
        }
    }

    //17. 프리뷰가 바로 활성화 될수있게끔
    private fun openPreview(){
        cameraProviderFuture = ProcessCameraProvider.getInstance(context)
            .also { providerFuture ->
                providerFuture.addListener({
                  //실제 프리뷰를 start시키는 메소드를 넣어줘야한다.

                    //46.
                    startPreview(context)
                }, ContextCompat.getMainExecutor(context))
            }
    }//openPreview method

    //18.
    private fun startPreview(context: Context){
        val cameraProvider = cameraProviderFuture.get()
        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                context as LifecycleOwner,cameraSelector,preview
            )
        }catch(e:Exception) {
            e.stackTrace
        }
    }//startPreview method


    //47. 메인엑티비티에서 호출해서 실제 얼굴 인식 시키는 함수
    fun startFaceDetect(){
        val cameraProvider = cameraProviderFuture.get()
        val faceAnalyzer = FaceAnalyzer((context as ComponentActivity).lifecycle , previewView, listener)
        val analsisUseCase = ImageAnalysis.Builder()
            .build()
            .also {
                it.setAnalyzer(
                    cameraExecutor,
                    faceAnalyzer
                )
            }
        try {
            cameraProvider.bindToLifecycle(
                context as LifecycleOwner,
                cameraSelector,
                preview,
                analsisUseCase
            )
        }catch (e : Exception){

        }
    }

    //48. 그만
    fun stopFaceDetect(){
        try {
            cameraProviderFuture.get().unbindAll()
            previewView.releasePointerCapture()
        }catch (e : Exception){

        }
    }

    //49. 이제 이걸 앱 모듈에 붙이자


    //26. override해서 함수를 얻어오자. 권한을 획득했을때
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        var flag = true
        if (grantResults.isNotEmpty()){
            for ((i,_)in permissions.withIndex()){
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED){
                    flag = false
                }
            }
            if (flag){
                //만약 true면 프리뷰를 열고
                openPreview()
            }else{
                //아니면 토스트 띄워
                Toast.makeText(context, "권한이 없습니다. ", Toast.LENGTH_SHORT).show()
                (context as Activity).finish()
            }
        }
    } //onRequestPermissionsResult method


}//class Camera
















