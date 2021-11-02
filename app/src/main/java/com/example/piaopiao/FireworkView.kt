package com.example.piaopiao

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import kotlin.math.cos
import kotlin.math.sin

/**
 * @author fanxiaoyang
 * date 2021/10/15
 * desc 烟花view
 */
class FireworkView : View {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    /**
     * 半径
     */
    val r = 300

    /**
     * 分成32分
     */
    val devide = 32

    /**
     * 圆心x轴坐标
     */
    val a = 500f

    /**
     * 圆心y轴坐标
     */
    val b = 500f

    init {

    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        initPointList(a, b)
        val p = Paint()
        p.color = Color.RED // 设置红色
        canvas?.let {
            pointList.forEach {
                canvas.drawLine(a, b, it.x.toFloat(), it.y.toFloat(), p)
            }
        }
    }

    val pointList = ArrayList<MyPoint>()

    /**
     * a，b是圆心坐标
     */
    private fun initPointList(a: Float, b: Float) {
        pointList.clear()
        repeat(devide) {
            val x = a + (r * cos(it * 2 * Math.PI / devide))
            val y = b + (r * sin(it * 2 * Math.PI / devide))
            val myPoint = MyPoint()
            myPoint.x = x
            myPoint.y = y
            pointList.add(myPoint)
        }
    }

    class MyPoint {
        var x: Double = 0.toDouble()
        var y: Double = 0.toDouble()
    }


}