package com.example.piaopiao

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.animation.LinearInterpolator
import android.widget.RelativeLayout
import android.widget.RelativeLayout.ALIGN_PARENT_END
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    private lateinit var bubbleContainView: RelativeLayout
    private lateinit var testTv: TextView

    private val stringList = arrayOf("哈哈", "嘿嘿", "77", "88")

    /**
     * 2时间戳
     */
    private val startTime = 1628870400000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        actionBar?.hide()
        supportActionBar?.hide()
        StatusBarHelper.setStatusBarHint(this, Color.parseColor("#f3f3f3f3"), true)
        bubbleContainView = findViewById(R.id.rootContainer)
    }

    private fun randomLayoutParams(): RelativeLayout.LayoutParams {
        val layoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
        layoutParams.addRule(ALIGN_PARENT_END)
        layoutParams.topMargin = randomY()
        return layoutParams
    }

    override fun onResume() {
        super.onResume()
        initView()
    }

    private fun randomY(): Int {
        val hAdd = StatusBarHelper.getStatusBarHeight(this)
        return Random(System.currentTimeMillis()).nextInt(hAdd, 150 + hAdd)
    }

    private fun initView() {
        GlobalScope.launch(Dispatchers.Main) {
            repeat(1000) {
                repeat(stringList.size) {
                    delay(2000)
                    val textView = TextView(this@MainActivity)
                    textView.text = stringList[it]
                    startBubble(textView)
                }
            }
        }

    }

    private fun startBubble(view: TextView) {
        bubbleContainView.addView(view, randomLayoutParams())
        val translationX = getScreenWidth(this)
        val animator = ObjectAnimator.ofFloat(view, "translationX", 0f, (0f - translationX))
        animator.duration = 5000
        animator.interpolator = LinearInterpolator()
        animator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {

            }

            override fun onAnimationEnd(animation: Animator?) {
                view.visibility = View.GONE
                bubbleContainView.removeView(view)
            }

            override fun onAnimationCancel(animation: Animator?) {

            }

            override fun onAnimationStart(animation: Animator?) {

            }

        })
        animator.start()
    }

    /**
     * 获取屏幕宽度
     */
    private fun getScreenWidth(context: Context): Int {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        return wm.defaultDisplay.width
    }

}