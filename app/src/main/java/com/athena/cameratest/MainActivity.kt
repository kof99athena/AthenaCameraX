package com.athena.cameratest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.isVisible
import com.athena.cameratest.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    //7. 뷰바인딩 사용
    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //7. 뷰바인딩 사용
        binding = ActivityMainBinding.inflate(layoutInflater).apply {
            setContentView(root)
            //apply : 인스턴스를 새로 생성하고 특정변수에 할당하기 전에 초기화작업을 해준다 4
            //새로운 인스턴스를 반환한다.

            //8. START 버튼에 리스너 달기
            startDetectButton.setOnClickListener {
            it.isVisible = false //버튼을 누르면 안보이게 된다.

            }
        }

    }
}