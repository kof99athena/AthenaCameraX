package com.athena.face_recognition.recognition

import android.graphics.PointF
import android.graphics.RectF
import android.util.SizeF

//29. callback 처리하기
interface FaceAnalyzerListener {

    fun detect() //얼굴 인식 됐을 때
    fun stopDetect()
    fun notDetect() //얼굴 인식 불가했을때
    fun detectProgress(progress : Float, message : String) //어느정도 인식진행되었는지 그부분 체크
    fun faceSize(rectF : RectF, sizeF: SizeF, pointF: PointF) //각 사이즈 넘기기
}