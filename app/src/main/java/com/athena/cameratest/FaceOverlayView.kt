package com.athena.cameratest

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PathMeasure
import android.graphics.Point
import android.graphics.PointF
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RectF
import android.util.AttributeSet
import android.util.SizeF
import android.view.View
import androidx.core.graphics.times
import androidx.core.graphics.xor


//61. FaceOverlayView를 만들어준다.
//@JvmOverloads는 Kotlin 코드에서 기본값이 있는 매개변수를 사용할 때,
//Java 코드에서 각각의 매개변수를 모두 명시적으로 입력하지 않고도 사용할 수 있도록 도와주는 애노테이션.

class FaceOverlayView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null, //AttributeSet는 View의 속성 정보(attribute)를 담고있는 객체이다. ex) 텍스트 내용, 버튼텍스트, 이미지뷰 리소스등
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) { //뷰를 상속 받는다.


    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        //Paint.ANTI_ALIAS_FLAG 켜자.
        //paint 객체는 안티 앨리어싱이 활성화된 상태이다.
        //이제 이 paint 객체를 사용하여 그래픽을 그릴 때 경계선과 모서리가 더 부드럽게 나타날 것입니다.
        color = Color.BLACK
        alpha = 90
        style = Paint.Style.FILL
    }

    //62. 윤곽선을 따라서 dash를 그릴예정
    private val facePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.GRAY
        style = Paint.Style.STROKE
        strokeWidth = 10F
        pathEffect = DashPathEffect(floatArrayOf(10f, 10f), 0F)
    }


    //73. 프로그레스바 표현하기
    private val progressPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.YELLOW
        style = Paint.Style.STROKE
        strokeWidth = 10F
    }


    //63. 마스크를 씌우기 (동그랗게 씌우기)
    private val maskPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
        // PorterDuffXfermode 클래스는 Android 그래픽 시스템에서 사용되는 클래스
        // 두 개의 그래픽 객체가 겹치는 경우 그들의 픽셀을 어떻게 결합하고 혼합할지를 정의하는 모드를 제어하는 데 사용됨.
    }

    //64.
    private val facePath = Path()

    //74. 프로그레스를 움직일때 점차적으로 증가 시킬것이기때문에
    private var progress = 0F

    //65.
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawOverlay(canvas)

        //76 . 오버레이부터 그린다음에 프로그레스를 써줘야 제대로 구현된다.
        drawProgress(canvas)
    }

    //66.
    private fun drawOverlay(canvas: Canvas) {
        canvas.drawRect(
            0F,
            0F,
            canvas.width.toFloat(),
            canvas.height.toFloat(),
            backgroundPaint
        )
        canvas.drawPath(facePath, maskPaint)
        canvas.drawPath(facePath, facePaint)
    }

    //67. 얼굴인식 모드에서 위 아래가 작게 나오기때문에 offset을 임의로 추가
    fun setSize(rectF: RectF, sizeF: SizeF, pointF: PointF) {
        val topOffset = sizeF.width / 2
        val bottomOffset = sizeF.width / 5

        with(facePath) {
            reset()
            moveTo(pointF.x, rectF.top) //포인트를 가운데 둔다.

            //베지에 곡선 활용
            cubicTo(
                rectF.right + topOffset,
                rectF.top,
                rectF.right + bottomOffset,
                rectF.bottom,
                pointF.x,
                rectF.bottom
            )//가운데 상단에서 시작해서 오른쪽으로 움직이면서 그림을 그린다.

            cubicTo(
                rectF.left - bottomOffset,
                rectF.bottom,
                rectF.left - topOffset,
                rectF.top,
                pointF.x,
                rectF.top
            )

            close()
        }
        postInvalidate()
    }//setSize method

    //78.모듈에서 프로그레스를 던져줄때 , 이쪽으로 들어와서 이걸애니메이션으로 처리하고 onDraw에서 일함
    fun setProgress(progress : Float){
        ValueAnimator.ofFloat(this.progress, progress).apply {
            duration = ANIMATE_DURATION
            addUpdateListener {
                this@FaceOverlayView.progress = it.animatedValue as Float
                invalidate()
            }
        }.start()
    }



    //68.
    fun reset(){
        facePath.reset()

        //79.
        progress = 0F

        invalidate()
    }

//    private val progressPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
//        color = Color.YELLOW
//        style = Paint.Style.STROKE
//        strokeWidth = 10F
//    }

    //75.실제 프로그레스가 구현되는함수
    private fun drawProgress(canvas: Canvas){
        val measure = PathMeasure(facePath,true)
        //실제 프로그레스를 구현하는것은 PathMeasure를 통해서 path의 크기를 구하고
        //그걸 dash로 표현하기
        val pathLength = measure.length
        val total = pathLength - (pathLength*(progress/100))
        val pathEffect = DashPathEffect(floatArrayOf(pathLength,pathLength),total)
        //dash는 공간있어서 점선이 표시되는데, 이부분은 점선이 없고 지속적으로 넣아준다.

        progressPaint.pathEffect = pathEffect
        canvas.drawPath(facePath, progressPaint)
    }

    companion object{
        //77. 프로그레스바가 증가할때 애니메이션
        private const val ANIMATE_DURATION = 500L
    }
}