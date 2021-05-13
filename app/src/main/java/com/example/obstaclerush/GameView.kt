package com.example.obstaclerush

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Context.WINDOW_SERVICE
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.CountDownTimer
import android.preference.PreferenceManager
import android.util.DisplayMetrics
import android.util.Log
import android.view.*


class GameView(context: Context) : SurfaceView(context), SurfaceHolder.Callback {

    private var radius: Float = 100f
    private val paint: Paint = Paint()
    private val paint_blocks: Paint = Paint()
    private val paint_text: Paint = Paint()
    private var xval: Float = 500f
    private var yval: Float = 0.0f
    private var width: Float = 0.0f
    private var height: Float = 0.0f
    private var score: Int = 0
    private var speed: Float = 10f
    private var ispaused: Boolean = false
    lateinit var c: Canvas
    val sholder: SurfaceHolder? = holder
    var coordinates = Array(6, {FloatArray(4)}) //{x1, x2, y1, y2}

    init{
        paint_blocks.isAntiAlias
        paint_text.isAntiAlias
        paint_text.color = Color.RED
        paint_blocks.color =  Color.BLACK
        paint.isAntiAlias
        paint.color = Color.RED

        val displayMetrics = DisplayMetrics()
        val windowsManager = context.getSystemService(WINDOW_SERVICE) as WindowManager
        windowsManager.defaultDisplay.getMetrics(displayMetrics)
        val WIDTH = displayMetrics.widthPixels
        val HEIGHT = displayMetrics.heightPixels

        width = WIDTH.toFloat()
        height = HEIGHT.toFloat()
        yval = height / 2
        initArrayFill()

        if (sholder != null){
            sholder?.addCallback(this)
        }
    }

    fun initArrayFill(){

        var prevy: Float = 0f
        var prevx: Float = (200..width.toInt()).random().toFloat()

        for (i in 0..5){
            var y2: Float = prevy
            var y1: Float = y2 - (height/4)
            var x2: Float = prevx
            var x1: Float = x2 - 200f

            insertArray(i, x1, x2, y1, y2)

            if (x2 < 200f){
                x1 = x2
                x2 += 200f
            }

            prevy = y1
            prevx = (200..width.toInt()).random().toFloat()
        }
    }

    fun saveData(){
        val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor:SharedPreferences.Editor =  sharedPreferences.edit()

        editor.putInt("score", score)
        editor.putFloat("speed", speed)
        editor.putFloat("xval", xval)
        editor.putFloat("yval", yval)

        for (i in 0..5){
            for (n in 0..3){
                editor.putFloat("($i, $n)", coordinates[i][n])
            }
        }

        editor.apply()
    }

    fun loadData(){

        ispaused = false

        val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor:SharedPreferences.Editor =  sharedPreferences.edit()

        score = sharedPreferences.getInt("score", score)
        speed = sharedPreferences.getFloat("speed", speed)
        xval = sharedPreferences.getFloat("xval", xval)
        yval = sharedPreferences.getFloat("yval", yval)

        for (i in 0..5){
            for (n in 0..3){
                coordinates[i][n] = sharedPreferences.getFloat("($i, $n)", coordinates[i][n])
            }
        }
    }

    fun insertArray(pos: Int, x1: Float, x2: Float, y1: Float, y2: Float){

        val tempArray = arrayOf(x1, x2, y1, y2)

        for (i in 0..3){
            coordinates[pos][i] = tempArray[i]
        }
    }

    fun checkBounds(){
        for (i in 0..5){
            if (coordinates[i][2] > height){

                if (i == 0){

                    var x2: Float = (200..width.toInt()).random().toFloat()
                    var x1: Float = x2 - 200f
                    var y2: Float = coordinates[5][2] - 1f
                    var y1: Float = y2 - (height/4)

                    insertArray(i, x1, x2, y1, y2)
                }else{
                    var x2: Float = (200..width.toInt()).random().toFloat()
                    var x1: Float = x2 - 200f
                    var y2: Float = coordinates[i-1][2] - 1f
                    var y1: Float = y2 - (height/4)

                    insertArray(i, x1, x2, y1, y2)
                }
            }
        }
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        loadData()
        render()
        timer(8)
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
    }

    fun render(){
        if (sholder != null && !ispaused) {
            try{
                c = sholder.lockCanvas()
                draw(c)
            }
            catch(e: NullPointerException){
                ispaused = true;
                //val viewMain = Intent(context, MainActivity::class.java)
                //context.startActivity(viewMain)
            }
        }
    }

    fun timer(countDownClock: Long){

        object : CountDownTimer((1000 * countDownClock) + 1000, 10) {
            override fun onTick(millisUntilFinished: Long) {
                if (!ispaused){
                    for (i in 0..5){
                        coordinates[i][2] += speed
                        coordinates[i][3] += speed
                    }

                    saveData()
                    render()
                }else{
                    cancel()
                }
            }

            override fun onFinish() {
                speed += 5
                score++
                saveData()
                if (ispaused) {cancel()}
                timer(countDownClock)
            }
        }.start()
    }

    override fun draw(canvas: Canvas?){
        super.draw(canvas)

        canvas?.drawColor(Color.WHITE)
        paint_text.textSize = 100f

        for (i in 0..5){
            if(col_check(coordinates[i][0], coordinates[i][1], coordinates[i][2], coordinates[i][3])){
                ispaused = true;

                val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
                val editor:SharedPreferences.Editor =  sharedPreferences.edit()
                editor.clear()
                editor.apply()

                val viewScores = Intent(context, ScoresActivity::class.java)
                viewScores.putExtra("score", score)
                context.startActivity(viewScores)
            }

            canvas?.drawRect(coordinates[i][0], coordinates[i][2], coordinates[i][1], coordinates[i][3], paint_blocks)
        }

        canvas?.drawCircle(xval, yval,radius, paint)
        canvas?.drawText("Score: $score", width / 2.5f, 100f, paint_text)
        checkBounds()

        sholder?.unlockCanvasAndPost(canvas)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (!ispaused) {
            xval = event!!.x
        }

        return true
    }


    fun col_check(x1: Float, x2: Float, y1: Float, y2: Float): Boolean{

        val rect_width: Float = x2 - x1
        val rect_height: Float = y2 - y1
        val dist_x: Float = xval - x1
        val dist_y: Float = yval - y1

        if ((dist_x > rect_width + radius) || (dist_y > rect_height + radius) || (dist_x < (radius * -1)) || (dist_y < (radius * -1))){return false}
        //else if ((dist_x <= rect_width/2) || (dist_y <= rect_height/2)) {return true}

        return true
    }
}