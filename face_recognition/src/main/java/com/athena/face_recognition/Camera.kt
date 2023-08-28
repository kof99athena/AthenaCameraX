package com.athena.face_recognition

import android.content.Context
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.common.util.concurrent.ListenableFuture
import java.lang.Exception
import java.util.concurrent.Executors
/* 카메라 프리뷰를 세팅할 수 있는 class */


class Camera(private val context:Context) {
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

    //16. 메인액티비티에서 layout을 받아오기 - 여기에 프리뷰를 설정한다.
    //우리로 따지면 만들어둔 framelayout
    fun initCamera(layout: ViewGroup){
        previewView = PreviewView(context)
        layout.addView(previewView)
    }

    //17. 프리뷰가 바로 활성화 될수있게끔
    private fun openPreview(){
        cameraProviderFuture = ProcessCameraProvider.getInstance(context)
            .also { providerFuture ->
                providerFuture.addListener({
                  //실제 프리뷰를 start시키는 메소드를 넣어줘야한다.
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
    }
}
















