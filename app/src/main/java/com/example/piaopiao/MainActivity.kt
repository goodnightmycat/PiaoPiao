package com.example.piaopiao

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.RelativeLayout.ALIGN_PARENT_END
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

/**
 * Use JDK 11
 */
class MainActivity : AppCompatActivity() {
    private lateinit var rootContainView: RelativeLayout
    private lateinit var dayTv: TextView


    /**
     * 2时间戳
     */
    private val startTime = 1628784001000

    var colorBlue: Int = 0
    var colorPink: Int = 0

    private lateinit var sheepIv: ImageView
    private lateinit var catIv: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        actionBar?.hide()
        supportActionBar?.hide()
        StatusBarHelper.setStatusBarHint(this, Color.parseColor("#f3f3f3f3"), true)
        rootContainView = findViewById(R.id.rootContainer)
        dayTv = findViewById(R.id.tv_days)
        colorBlue = this.resources.getColor(R.color.my_blue)
        colorPink = this.resources.getColor(R.color.my_pink)
        sheepIv = findViewById(R.id.sheep)
        sheepIv.setOnClickListener {
            startFirework()
        }
        catIv = findViewById(R.id.cat)
        catIv.setOnClickListener {
            initView()
        }
    }

    private val fireworkView by lazy { FireworkView(this, null) }


    private fun startFirework() {
        rootContainView.removeView(fireworkView)
        rootContainView.addView(
            fireworkView,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
    }

    override fun onResume() {
        super.onResume()
        val days = (System.currentTimeMillis() - startTime) / (1000 * 60 * 60 * 24)
        dayTv.text = "$days"
    }

    private fun randomTextLayoutParams(): RelativeLayout.LayoutParams {
        val layoutParams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.addRule(ALIGN_PARENT_END)
        layoutParams.topMargin = randomY()
        return layoutParams
    }

    private fun randomImageLayoutParams(): RelativeLayout.LayoutParams {
        val snowSize = Random(System.currentTimeMillis()).nextInt(10, 90)
        val layoutParams = RelativeLayout.LayoutParams(
            snowSize,
            snowSize
        )
        layoutParams.marginStart = randomX()
        return layoutParams
    }

    private fun randomY(): Int {
        val hAdd = StatusBarHelper.getStatusBarHeight(this)
        return Random(System.currentTimeMillis()).nextInt(hAdd, 150 + hAdd)
    }

    private fun randomX(): Int {
        return Random(System.currentTimeMillis()).nextInt(100, screenWith - 100)
    }

    private fun initView() {
        GlobalScope.launch(Dispatchers.Main) {
            repeat(1000) {
                delay(2000)
                val textView = TextView(this@MainActivity)
                textView.setTextColor(randomColor())
                textView.text = randomText()
                startBubble(textView)
            }
        }

        GlobalScope.launch(Dispatchers.Main) {
            repeat(Int.MAX_VALUE) {
                delay(Random(System.currentTimeMillis()).nextLong(10, 2000))
                val imageView = ImageView(this@MainActivity)
                startSnow(imageView)
            }
        }
        GlobalScope.launch(Dispatchers.Main) {
            repeat(Int.MAX_VALUE) {
                delay(Random(System.currentTimeMillis()).nextLong(10, 2000))
                val imageView = ImageView(this@MainActivity)
                startSnow(imageView)
            }
        }
    }

    val showList = ArrayList<String>()

    private fun randomText(): String {
        var result = ""
        while (true) {
            val tempResult =
                DataHelper.stringList[Random(System.currentTimeMillis()).nextInt(
                    0,
                    DataHelper.stringList.size
                )]
            if (showList.contains(tempResult)) {
                showList.remove(tempResult)
                result = tempResult
                break
            }
            if (showList.size == 0) {
                showList.addAll(DataHelper.stringList)
            }
        }
        return result
    }

    private fun randomColor(): Int {
        val int = Random(System.currentTimeMillis()).nextInt()
        return if (int % 2 == 0) {
            colorBlue
        } else {
            colorPink
        }
    }

    private fun startBubble(view: TextView) {
        rootContainView.addView(view, randomTextLayoutParams())
        val translationX = screenWith
        val animator =
            ObjectAnimator.ofFloat(view, "translationX", 0f + translationX / 2, (0f - translationX))
        animator.duration = 5000
        animator.interpolator = LinearInterpolator()
        animator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {

            }

            override fun onAnimationEnd(animation: Animator?) {
                view.visibility = View.GONE
                rootContainView.removeView(view)
            }

            override fun onAnimationCancel(animation: Animator?) {

            }

            override fun onAnimationStart(animation: Animator?) {

            }

        })
        animator.start()
    }

    val snowList = arrayListOf<Int>(R.drawable.snow1, R.drawable.snow2, R.drawable.snow3)

    private fun randomSnow(): Int {
        return snowList[Random(System.currentTimeMillis()).nextInt(0, snowList.size)]
    }

    private fun startSnow(view: ImageView) {
        view.setImageResource(randomSnow())
        view.scaleType = ImageView.ScaleType.FIT_XY
        rootContainView.addView(view, randomImageLayoutParams())
        val translationY = screenHeight
        val animator = ObjectAnimator.ofFloat(view, "translationY", 0f, translationY.toFloat())
        animator.duration = randomTime()
//        animator.interpolator = LinearInterpolator()
        animator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {

            }

            override fun onAnimationEnd(animation: Animator?) {
                view.visibility = View.GONE
                rootContainView.removeView(view)
            }

            override fun onAnimationCancel(animation: Animator?) {

            }

            override fun onAnimationStart(animation: Animator?) {

            }

        })
        animator.start()
    }

    private fun randomTime(): Long {
        return Random(System.currentTimeMillis()).nextLong(5000, 10000)
    }

    private val screenWith: Int by lazy {
        val wm = this.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        wm.defaultDisplay.width
    }

    private val screenHeight: Int by lazy {
        val wm = this.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        wm.defaultDisplay.height
    }

}