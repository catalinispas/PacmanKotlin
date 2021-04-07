package org.pondar.pacmankotlin

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.widget.Button
import android.widget.TextView
import java.util.ArrayList
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.view.*
import java.util.*
import kotlin.math.abs

/**
 *
 * This class should contain all your game logic
 */

class Game(private var context: Context, view: TextView) {

    private var pointsView: TextView = view
    var points: Int = 0

    // Bitmaps
    var pacBitmap: Bitmap
    var coinBitmap: Bitmap
    var enemyBitmap: Bitmap
    var gameOverBitmap: Bitmap
    var gameWonBitmap: Bitmap
    var gamePausedBitmap: Bitmap

    // pacman starting position
    var pacx: Int = 0
    var pacy: Int = 0
    var direction = 0

    // timer & counter
    var counter: Int = 0
    var timer: Int = 30

    // game status
    var running: Boolean = false
    var isPuased: Boolean = false
    var isGameOver: Boolean = false
    var isGameWon: Boolean = false

    // enemy direction
    var directionEnemy = 1

    // did we initialize the coins?
    var coinsInitialized = false

    // did we initialize the enemies?
    var enemiesInitialized = false

    // coins
    var numOfCoins: Int = 6
    var coins = ArrayList<GoldCoin>()

    //the list of enemies
    var enemies = ArrayList<Enemy>()

    //a reference to the GameView
    private var gameView: GameView? = null
    private var h: Int = 0
    private var w: Int = 0 //height and width of screen


    //The init code is called when we create a new Game class.
    //it's a good place to initialize our images.
    init {
        pacBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.pacman_down)
        coinBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.goldcoin)
        enemyBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.enemy)
        gameOverBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.game_over_png_small)
        gameWonBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.you_win_small)
        gamePausedBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.paused_small)
    }

    fun setGameView(view: GameView) {
        this.gameView = view
    }


    //initialize goldcoins also here
    fun initializeGoldcoins() {

        //initialize coins
        for (i in 1..numOfCoins) coins.add(GoldCoin((60..(w - 50 - coinBitmap.width)).random(), (60..(h - 60 - coinBitmap.height)).random(), false))
        coinsInitialized = true
    }

    fun newGame() {
        pacx = 50
        pacy = 400
        direction = 0 // if not set to 0 it will move in the last used direction upon start

        coins = ArrayList<GoldCoin>()

        coinsInitialized = false

        points = 0
        pointsView.text = "${context.resources.getString(R.string.points)} $points"

        timer = 20
        counter = 0

        // Reset all the game status vars
        running = false
        isPuased = false
        isGameOver = false
        isGameWon = false

        // Redraw pacman so it doesn't stay dead
        pacBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.pacman_down)

        gameView?.invalidate() //redraw screen


    }

    //initialize enemies
    fun initializeEnemy() {
        enemies.add(Enemy(w / 2, h / 2))
        enemiesInitialized = true
    }

    //TODO check if game over because you run out of time
    fun isGameOver() {
        if (timer == 0) {
            timer = 0
            counter = 0
            running = false
            isGameOver = true
            Toast.makeText(context, "Time out! Game Over!", Toast.LENGTH_SHORT).show()
        }
    }

    fun setSize(h: Int, w: Int) {
        this.h = h
        this.w = w
    }

    fun movePacmanRight(pixels: Int) {
        if (pacx + pixels + pacBitmap.width < w) {
            pacx += pixels
            doCollisionCheck()
            direction = 4
        }
        if (pacBitmap != BitmapFactory.decodeResource(context.resources, R.drawable.pacman_right)) {
            pacBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.pacman_right)
        }
    }

    fun movePacmanLeft(pixels: Int) {
        if (pacx - pixels > 0) {
            pacx -= pixels
            doCollisionCheck()
            direction = 3
        }
        if (pacBitmap != BitmapFactory.decodeResource(context.resources, R.drawable.pacman_left)) {
            pacBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.pacman_left)
        }
    }

    fun movePacmanUp(pixels: Int) {
        if (pacy - pixels > 0) {
            pacy -= pixels
            doCollisionCheck()
            direction = 1
        }
        if (pacBitmap != BitmapFactory.decodeResource(context.resources, R.drawable.pacman_up)) {
            pacBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.pacman_up)
        }
    }

    fun movePacmanDown(pixels: Int) {
        if (pacy + pixels + pacBitmap.height < h) {
            pacy += pixels
            doCollisionCheck()
            direction = 2
        }
        if (pacBitmap != BitmapFactory.decodeResource(context.resources, R.drawable.pacman_down)) {
            pacBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.pacman_down)
        }
    }

    //Move the enemies
    fun moveEnemy(pixels: Int) {
        for (enemy in enemies) {
            if (directionEnemy == 2) {
                // ---- DOWN
                if (enemy.enemyy + pixels + enemyBitmap.height < h) {
                    enemy.enemyy += pixels
                    directionEnemy = 2
                } else {
                    // change direction if at edge
                    directionEnemy = 1
                }

            } else {
                // ---- UP
                if (enemy.enemyy - pixels > 0) {
                    enemy.enemyy -= pixels
                    directionEnemy = 1
                } else {
                    // change direction if at edge
                    directionEnemy = 2
                }
            }
        }
    }

    fun doCollisionCheck() {

        //coin collision
        for (coin in coins) {
            if (pacx + pacBitmap.width > coin.coinX + coinBitmap.width / 2
                    && pacy + pacBitmap.height > coin.coinY + coinBitmap.width / 2
                    && pacx < coin.coinX
                    && pacy < coin.coinY) {
                if (!coin.taken) {
                    points++
                    "${context.resources.getString(R.string.points)} $points".also { pointsView.text = it }
                    coin.taken = true
                }

                // win condition is number of points = number of initialised coins
                if (points == numOfCoins) {
                    //stop the game
                    running = false
                    isGameWon = true
                    Toast.makeText(context, "Yay! You won!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        //enemy collision
        for (enemy in enemies) {
            if ((pacx < enemy.enemyx + enemyBitmap.width &&
                            pacx + pacBitmap.width > enemy.enemyx &&
                            pacy < enemy.enemyy + enemyBitmap.height &&
                            pacy + pacBitmap.height > enemy.enemyy)) {
                //stop the game
                running = false
                isGameOver = true

                //draw the defeated pacman
                pacBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.pacman_dead)

                gameView?.invalidate() //redraw screen
            }
        }

    }
}

