package org.pondar.pacmankotlin

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.View


//note we now create our own view class that extends the built-in View class
class GameView : View {

    private var game: Game? = null
    private var h: Int = 0
    private var w: Int = 0 //used for storing our height and width of the view

    fun setGame(game: Game?) {
        this.game = game
    }


    /* The next 3 constructors are needed for the Android view system,
	when we have a custom view.
	 */
    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    //In the onDraw we put all our code that should be
    //drawn whenever we update the screen.
    override fun onDraw(canvas: Canvas) {
        //Here we get the height and weight
        h = canvas.height
        w = canvas.width
        //update the size for the canvas to the game.
        game?.setSize(h, w)

        //are the coins initiazlied?
        //if not initizlise them
        if (!(game!!.coinsInitialized))
            game?.initializeGoldcoins()


        //Making a new paint object
        val paint = Paint()
        canvas.drawColor(Color.WHITE) //clear entire canvas to WHITE color

        //draw the pacman
        canvas.drawBitmap(game!!.pacBitmap, game?.pacx!!.toFloat(),
                game?.pacy!!.toFloat(), paint)

        //draw the coins
        for (coin in game!!.coins) {
            if (!coin.taken){
                canvas.drawBitmap(game!!.coinBitmap, coin.coinX.toFloat(), coin.coinY.toFloat(), paint)
            }
        }

        //if enemies are not initialized
        if (!(game!!.enemiesInitialized))
            game?.initializeEnemy()

        //draw the enemy
        for (enemy in game!!.enemies) {
            canvas.drawBitmap(game!!.enemyBitmap, enemy.enemyx.toFloat(),
                    enemy.enemyy.toFloat(), paint)
        }

        // check for collision
        game?.doCollisionCheck()
        super.onDraw(canvas)


        // check for game over or game won
        if (game!!.isGameOver) { canvas.drawBitmap(game!!.gameOverBitmap, w/2-game!!.gameOverBitmap.width/2.toFloat(), h/2-game!!.gameOverBitmap.height/2.toFloat(), paint) }
        if (game!!.isGameWon) { canvas.drawBitmap(game!!.gameWonBitmap, w/2-game!!.gameWonBitmap.width/2.toFloat(), h/2-game!!.gameWonBitmap.height/2.toFloat(), paint) }
    }

}
