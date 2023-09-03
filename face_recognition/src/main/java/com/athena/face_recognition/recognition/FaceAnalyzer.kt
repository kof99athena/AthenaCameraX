package com.athena.face_recognition.recognition

import android.graphics.PointF
import android.graphics.RectF
import android.media.Image
import android.util.SizeF
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.view.PreviewView
import androidx.lifecycle.Lifecycle
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import kotlin.math.E
import kotlin.math.abs

//30. 실제로 ML kit을 구현 할 수 있는 class
internal class FaceAnalyzer (
    lifecycle : Lifecycle,
    private val preview : PreviewView,
    private val listener : FaceAnalyzerListener?
): ImageAnalysis.Analyzer {

    private var widthScaleFactor = 1.0f
    private var heightScaleFactor = 1.0f


    //36. 얼굴인식 모드가 후방으로 되어있다. 이걸 좌우로 반전해주는 코드를 넣어야함.
    //여러개 지정해준다.
    private var preCenterX = 0F
    private var preCenterY = 0F
    private var preHeight = 0F
    private var preWidth = 0F


    private val options = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE) //정확도 가장 우선시
        .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL) // 윤곽선 받아오기 추가
        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL) //표정 받아오기
        .setMinFaceSize(0.4f) //최소 얼굴 크기
        .build()

    private val detector = FaceDetection.getClient(options)

    //32. 아직 감지되지 않았다.
    private var detectStatus = FaceAnalyzerStatus.UnDetect

    //33. 얼굴 인식 진행 상황에 대한 함수
    private val successListener = OnSuccessListener<List<Face>> { faces ->
        val face = faces.firstOrNull()
        if(face!=null){
            if(detectStatus == FaceAnalyzerStatus.UnDetect){
                detectStatus = FaceAnalyzerStatus.Detect
                listener?.detect()
                listener?.detectProgress(25F, "얼굴 인식 완료. \n 왼쪽 눈 깜빡이세요.")
            }else if (detectStatus == FaceAnalyzerStatus.Detect
                && (face.leftEyeOpenProbability ?: 0F) > EYE_SUCESS_VALUE
                && (face.rightEyeOpenProbability ?: 0F) < EYE_SUCESS_VALUE){
                //왼쪽눈이 윙크가 된거로 판단
                detectStatus = FaceAnalyzerStatus.LeftWink
                listener?.detectProgress(50F,"오른쪽 눈 깜빡이세요.")
            }else if( detectStatus == FaceAnalyzerStatus.LeftWink
                && (face.leftEyeOpenProbability ?: 0F) < EYE_SUCESS_VALUE
                && (face.rightEyeOpenProbability ?: 0F) < EYE_SUCESS_VALUE) {
                   detectStatus = FaceAnalyzerStatus.RightWink
                   listener?.detectProgress(75F,"활짝 웃어보세요.")
                }else if (detectStatus == FaceAnalyzerStatus.RightWink
                && (face.smilingProbability ?: 0F) > SMILE_SUCESS_VALUE ){
                   detectStatus = FaceAnalyzerStatus.Smile
                    listener?.detectProgress(100F, "얼굴 인식이 되었습니다. ")
                listener?.stopDetect()
                detector.close()
            }
            //42, 만든 함수 호출하기
            calDetectSize(face) //이렇게 하면 인식하고, face가 있을때마다 좌우반전해주면서 액티비티로 넘어간다
        }else if (detectStatus != FaceAnalyzerStatus.UnDetect && detectStatus != FaceAnalyzerStatus.Smile){
            //중간단계이면 처음으로 돌려버림
            detectStatus = FaceAnalyzerStatus.UnDetect
            listener?.notDetect()
            listener?.detectProgress(0F,"얼굴 인식을 하지 못했습니다. \n처음으로 돌아갑니다. ")
        }
    }




    private val failListener = OnFailureListener{ e ->
        //34.
        detectStatus == FaceAnalyzerStatus.UnDetect
    }

    //35. 라이프 사이클 엮기
    init {

        lifecycle.addObserver(detector)
    }




    override fun analyze(image: ImageProxy) {
        widthScaleFactor = preview.width.toFloat() / image.height
        heightScaleFactor = preview.height.toFloat() / image.width
        detectFaces(image) //얼굴을 인식하는 함수
    }

    private fun detectFaces (imageProxy: ImageProxy) {
        val image = InputImage.fromMediaImage(
            imageProxy.image as Image,
            imageProxy.imageInfo.rotationDegrees
        )
        detector.process(image)
            .addOnSuccessListener (successListener)
            .addOnFailureListener (failListener)
            .addOnCompleteListener {
                imageProxy.close()
            }
    }

    //37.
    private fun calDetectSize(face : Face){
        val rect = face.boundingBox
        val boxWidth = rect.right - rect.left
        val boxHeight = rect.bottom - rect.top

        val left = rect.right.translateX() - (boxHeight/2)
        val top = rect.top.translateY() - (boxHeight/2)
        val right = rect.left.translateX() + (boxWidth/2)
        val bottom = rect.bottom.translateY()

        //39. 계산된 영역의 너비와 높이, 중앙점을 계산하자
        val width = right - left
        val height = bottom - top
        val centerX = left + (width / 2)
        val centerY = top + (height / 2)

        //40. 얼굴 인식을 베젤 곡선으로 만들자
        // 픽셀이 작아서..  깨져보일수있다. 이걸 부드럽게 만들어야한다.

        //41.
        if(abs(preCenterX - centerX) > PIVOT_OFFSET //좌우로 움직이거나
            || abs(preCenterY - centerY) > PIVOT_OFFSET //상하로 움직이거나
            || abs(preWidth - width) > SIZE_OFFSET //줌 아웃
            || abs(preHeight - height) > SIZE_OFFSET //줌 인 했을때
        ){
            //4개를 판단해서 액티비티에 넘겨준다.
            listener?.faceSize(
                RectF(left,top, right, bottom),
                SizeF(width, height),
                PointF(centerX, centerY)
            )

            //이렇게 저장해서 계산한다
            preCenterX = centerX
            preCenterY = centerY
            preWidth = width
            preHeight = height
        }

    }


    //38. x값을 옮겨줘야하는데 계속 사용할것이니 extention 함수 만들어서 편하게 만들기
    private fun Int.translateX() = preview.width - (toFloat() * widthScaleFactor)
    private fun Int.translateY() = toFloat() * heightScaleFactor


    //31. 사람인지 아닌지 분석하는 코드를 추가하자
    companion object{
        private const val EYE_SUCESS_VALUE = 0.1F //윙크를 했을때, 어느정도 깜빡여야 성공하는가 그 수치
        private const val SMILE_SUCESS_VALUE = 0.8F  //웃을때

        //40.
        private const val PIVOT_OFFSET = 15
        private const val SIZE_OFFSET = 30
    }
}














