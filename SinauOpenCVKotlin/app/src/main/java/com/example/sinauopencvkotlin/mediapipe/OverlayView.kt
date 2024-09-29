/*
 * Copyright 2023 The TensorFlow Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.sinauopencvkotlin.mediapipe

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.example.sinauopencvkotlin.R
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarker
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult
import kotlin.math.atan
import kotlin.math.max
import kotlin.math.min
import kotlin.math.round

class OverlayView(context: Context?, attrs: AttributeSet?) :
    View(context, attrs) {

    private var results: PoseLandmarkerResult? = null
    private var pointPaint = Paint()
    private var linePaint = Paint()

    private var scaleFactor: Float = 1f
    private var imageWidth: Int = 1
    private var imageHeight: Int = 1

    init {
        initPaints()
    }

    fun clear() {
        results = null
        pointPaint.reset()
        linePaint.reset()
        invalidate()
        initPaints()
    }

    private fun initPaints() {
        linePaint.color =
            ContextCompat.getColor(context!!, R.color.mp_color_primary)
        linePaint.strokeWidth = LANDMARK_STROKE_WIDTH
        linePaint.style = Paint.Style.STROKE

        pointPaint.color = Color.YELLOW
        pointPaint.strokeWidth = LANDMARK_STROKE_WIDTH
        pointPaint.style = Paint.Style.FILL
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        results?.let { poseLandmarkerResult ->
            for(landmark in poseLandmarkerResult.landmarks()) {
                for(normalizedLandmark in landmark) {
                    canvas.drawPoint(
                        normalizedLandmark.x() * imageWidth * scaleFactor,
                        normalizedLandmark.y() * imageHeight * scaleFactor,
                        pointPaint
                    )

                }

                PoseLandmarker.POSE_LANDMARKS.forEach {
                    canvas.drawLine(
                        poseLandmarkerResult.landmarks()[0][it!!.start()].x() * imageWidth * scaleFactor,
                        poseLandmarkerResult.landmarks()[0][it.start()].y() * imageHeight * scaleFactor,
                        poseLandmarkerResult.landmarks()[0][it.end()].x() * imageWidth * scaleFactor,
                        poseLandmarkerResult.landmarks()[0][it.end()].y() * imageHeight * scaleFactor,
                        linePaint)
                }
            }
        }
    }

    fun getAngle() : Double {
        results?.let { poseLandmarkerResult ->
            for(landmark in poseLandmarkerResult.landmarks()) {
                fun isFacingRight(): Boolean {
                    return landmark[23].z() > landmark[24].z() &&
                            landmark[25].z() > landmark[26].z() &&
                            landmark[27].z() > landmark[28].z()
                }

                fun slope(p1: FloatArray, p2:FloatArray) : Float {
                    return (p2[1]-p1[1]) / (p2[0]-p1[0])
                }

                var m1 = 0f
                var m2 = 0f

                var radAngle = 0f
                var degree = 0.0

                var percentage = 0.0

                if(isFacingRight()) {
                    m1 = slope(floatArrayOf(landmark[26].x(), landmark[26].y()),
                        floatArrayOf(landmark[24].x(), landmark[24].y()))

                    m2 = slope(floatArrayOf(landmark[26].x(), landmark[26].y()),
                        floatArrayOf(landmark[28].x(), landmark[28].y()))

                    radAngle = atan((m2-m1)/(1+m1*m2))
                    degree = 180 - round(Math.toDegrees(radAngle.toDouble()))

                    if(degree > 180) {
                        degree -= 180
                    }

                    degree = 180 - degree

                } else {
                    m1 = slope(floatArrayOf(landmark[25].x(), landmark[25].y()),
                        floatArrayOf(landmark[23].x(), landmark[23].y()))

                    m2 = slope(floatArrayOf(landmark[25].x(), landmark[25].y()),
                        floatArrayOf(landmark[27].x(), landmark[27].y()))

                    radAngle = atan((m2-m1)/(1+m1*m2))
                    degree = round(Math.toDegrees(radAngle.toDouble()))

                    if(degree < 0) {
                        degree += 180
                    }

                    degree = 180 - degree
                }

                return degree
            }
        }
        return 0.0
    }

    fun setResults(
        poseLandmarkerResults: PoseLandmarkerResult,
        imageHeight: Int,
        imageWidth: Int,
        runningMode: RunningMode = RunningMode.IMAGE
    ) {
        results = poseLandmarkerResults

        this.imageHeight = imageHeight
        this.imageWidth = imageWidth

        scaleFactor = when (runningMode) {
            RunningMode.IMAGE,
            RunningMode.VIDEO -> {
                min(width * 1f / imageWidth, height * 1f / imageHeight)
            }
            RunningMode.LIVE_STREAM -> {
                // PreviewView is in FILL_START mode. So we need to scale up the
                // landmarks to match with the size that the captured images will be
                // displayed.
                max(width * 1f / imageWidth, height * 1f / imageHeight)
            }
        }
        invalidate()
    }

    companion object {
        private const val LANDMARK_STROKE_WIDTH = 10F
    }
}