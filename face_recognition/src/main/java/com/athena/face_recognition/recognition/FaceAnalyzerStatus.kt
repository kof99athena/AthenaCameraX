package com.athena.face_recognition.recognition


//28 상태를 나타내는 FaceAnalyzerStatus enum 클래스 만들어주기
internal enum class FaceAnalyzerStatus {
    //intenal 외부 접근을 막아준다
    //5개의 속성
    Detect, UnDetect, Smile, RightWink, LeftWink
}