package com.athena.cameratest

import android.graphics.PointF
import android.graphics.RectF
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.TransitionManager
import android.util.SizeF
import androidx.core.view.isVisible
import com.athena.cameratest.databinding.ActivityMainBinding
import com.athena.face_recognition.Camera
import com.athena.face_recognition.recognition.FaceAnalyzerListener

//50. 리스너도 상속 받아야함 FaceAnalyzerListener
//51. 오버라이드

class MainActivity : AppCompatActivity(), FaceAnalyzerListener {

    //7. 뷰바인딩 사용
    private lateinit var binding : ActivityMainBinding

    //50.
    private val camera = Camera(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //7. 뷰바인딩 사용
        binding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding.root)
            //apply : 인스턴스를 새로 생성하고 특정변수에 할당하기 전에 초기화작업을 해준다 4
            //새로운 인스턴스를 반환한다.
            //57.
            setProgressText("시작하기 눌러주세요")

            //50.
            camera.initCamera(binding.cameraLayout, this@MainActivity)



            //8. START 버튼에 리스너 달기
            binding.startDetectButton.setOnClickListener {
            it.isVisible = false //버튼을 누르면 안보이게 된다.

                //70.
                binding.faceOverlayView.reset() //시작하기 버튼을 누르면 내가 그려둔 path를 초기화 한다.

                //58.
                camera.startFaceDetect()

                //56.
                setProgressText("얼굴을 보여주세요")
            }

            //9. 앱모듈 만들기 : face_recognition


    }

    override fun detect() {

    }

    override fun stopDetect() {
        //59.
        camera.stopFaceDetect()
        //53 다시 노출
        reset()
    }

    override fun notDetect() {

        //71.
        binding.faceOverlayView
    }

    override fun detectProgress(progress: Float, message: String) {

        //55.
        setProgressText(message)

        //80.
        binding.faceOverlayView.setProgress(progress)

    }

    override fun faceSize(rectF: RectF, sizeF: SizeF, pointF: PointF) {
      //72.
        binding.faceOverlayView.setSize(rectF, sizeF, pointF)
        //실질적으로 path를 실행하는걸 실시간으로 연출
    }

    //60. 권한받아왔을때 권한처리
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        camera.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    //52.
    private fun reset(){
        binding.startDetectButton.isVisible = true

    }

    //54.
    private fun setProgressText(text:String){
        TransitionManager.beginDelayedTransition(binding.root)
        binding.progressTextView.text = text
    }


}