package com.example.piaopiao

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.View
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
    private lateinit var bubbleContainView: RelativeLayout
    private lateinit var dayTv: TextView

    private val stringList =
        arrayOf(
            "预备备",
            "嘿嘿",
            "可爱死咯",
            "不可以吗",
            "高贵得很",
            "等下下",
            "你要道理还是要女票",
            "哼",
            "坏蛋",
            "男票",
            "阿票",
            "票票",
            "想男票了",
            "男票困困",
            "男票抱抱",
            "男票亲亲",
            "吃饱饱",
            "分什么你我",
            "男票～我好喜欢你呀",
            "早安男票",
            "男票我醒了",
            "男票我想你了",
            "我最～喜欢男票了",
            "晚安男票",
            "男票亲我",
            "女票，你好好看啊",
            "女票，我好想你啊",
            "老婆，老婆，老婆，我爱你",
            "老婆仔","老公仔"


        )

    /**
     * 2时间戳
     */
    private val startTime = 1628784001000

    var colorBlue: Int = 0
    var colorPink: Int = 0

    private lateinit var sheepIv:ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        actionBar?.hide()
        supportActionBar?.hide()
        StatusBarHelper.setStatusBarHint(this, Color.parseColor("#f3f3f3f3"), true)
        bubbleContainView = findViewById(R.id.rootContainer)
        dayTv = findViewById(R.id.tv_days)
        colorBlue = this.resources.getColor(R.color.my_blue)
        colorPink = this.resources.getColor(R.color.my_pink)
        showList.addAll(stringList)
        sheepIv=findViewById(R.id.sheep)
        sheepIv.setOnClickListener {
        }
        initView()
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
        val snowSize= Random(System.currentTimeMillis()).nextInt(10,90)
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
        return Random(System.currentTimeMillis()).nextInt(100, getScreenWidth(this)-100)
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

        GlobalScope.launch (Dispatchers.Main){
            repeat(Int.MAX_VALUE) {
                delay(Random(System.currentTimeMillis()).nextLong(10,2000))
                val imageView=ImageView(this@MainActivity)
                startSnow(imageView)
            }
        }
        GlobalScope.launch (Dispatchers.Main){
            repeat(Int.MAX_VALUE) {
                delay(Random(System.currentTimeMillis()).nextLong(10,2000))
                val imageView=ImageView(this@MainActivity)
                startSnow(imageView)
            }
        }
    }

    val showList = ArrayList<String>()

    private fun randomText(): String {
        var result = ""
        while (true) {
            val tempResult =
                stringList[Random(System.currentTimeMillis()).nextInt(0, stringList.size)]
            if (showList.contains(tempResult)) {
                showList.remove(tempResult)
                result = tempResult
                break
            }
            if (showList.size == 0) {
                showList.addAll(stringList)
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
        bubbleContainView.addView(view, randomTextLayoutParams())
        val translationX = getScreenWidth(this)
        val animator =
            ObjectAnimator.ofFloat(view, "translationX", 0f + translationX / 2, (0f - translationX))
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
    val snowList= arrayListOf<Int>(R.drawable.snow1,R.drawable.snow2,R.drawable.snow3)

    private fun randomSnow():Int{
        return snowList[Random(System.currentTimeMillis()).nextInt(0,snowList.size)]
    }

    private fun startSnow(view:ImageView){
        view.setImageResource(randomSnow())
        view.scaleType=ImageView.ScaleType.FIT_XY
        bubbleContainView.addView(view, randomImageLayoutParams())
        val translationY = getScreenHeight(this)
        val animator = ObjectAnimator.ofFloat(view, "translationY", 0f,  translationY.toFloat())
        animator.duration = randomTime()
//        animator.interpolator = LinearInterpolator()
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

    private fun randomTime():Long{
        return Random(System.currentTimeMillis()).nextLong(5000,10000)
    }

    /**
     * 获取屏幕宽度
     */
    private fun getScreenWidth(context: Context): Int {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        return wm.defaultDisplay.width
    }

    private fun getScreenHeight(context: Context): Int {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        return wm.defaultDisplay.height
    }

}