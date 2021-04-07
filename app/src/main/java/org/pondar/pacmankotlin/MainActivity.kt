package org.pondar.pacmankotlin

import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import java.util.*
import kotlinx.android.synthetic.main.activity_main.*



class MainActivity : AppCompatActivity() {
    private var myTimer: Timer = Timer()
    private var countDown: Timer = Timer()

    //reference to the game class.
    private var game: Game? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //makes sure it always runs in portrait mode
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContentView(R.layout.activity_main)

        game = Game(this,pointsView)

        //intialize the game view class and game class
        game?.setGameView(gameView)
        gameView.setGame(game)
        game?.newGame()

        // Controlls
        moveRight.setOnClickListener {
            game?.movePacmanRight(0)
        }
        moveLeft.setOnClickListener {
            game?.movePacmanLeft(0)
        }

        moveUp.setOnClickListener {
            game?.movePacmanUp(0)
        }

        moveDown.setOnClickListener {
            game?.movePacmanDown(0)
        }


        // Menu items
        pause.setOnClickListener {
            game!!.running = false
            game!!.isPuased = true
            gameView.invalidate()
        }

        startButton.setOnClickListener {
            if (!game!!.isGameOver && !game!!.isGameWon) {
                game!!.running = true
                game!!.isPuased = false
            } else {
                Toast.makeText(this, "Start a NEW GAME or share your score!", Toast.LENGTH_SHORT).show()
            }
        }

        reset.setOnClickListener {
            game!!.newGame() //calling the newGame method
            timer.text = getString(R.string.timer, game!!.counter)
            timeLeft.text = getString(R.string.timeLeft, game!!.counter)
        }

        game!!.running = false //should game be running?

        fun timerMethod() {
            this.runOnUiThread(timerTick);
        }

        //we call the timer 5 times each second - pacman
        myTimer.schedule(
                object : TimerTask() {
                    override fun run() {
                        timerMethod()
                    }
                }, 0, 50)

        //0 indicates we startButton now,
        //200 is the number of milliseconds between each call


        fun timerMethodCountDown() {
            this.runOnUiThread(timerTickCountDown);
        }

        //we call the timer 5 times each second - time
        countDown.schedule(
                object : TimerTask() {
                    override fun run() {
                        timerMethodCountDown()
                    }
                }, 0, 1000)

        //0 indicates we startButton now,
        //200 is the number of milliseconds between each call
    }

    override fun onStop() {
        super.onStop()
        //just to make sure if the app is killed, that we stop the timer.
        myTimer.cancel()
    }

    private val timerTickCountDown = Runnable {
        if (game!!.running) {
            //just to make sure if the app is killed, that we stop the timer.
            game!!.timer--
            timeLeft.text = getString(R.string.timeLeft, game!!.timer)
            gameView!!.invalidate()
            game!!.isGameOver()
        }
    }
    private val timerTick = Runnable {
        //This method runs in the same thread as the UI.
        // so we can draw
        if (game!!.running) {
            game!!.counter++

            game!!.moveEnemy(10) // move the enemy
            gameView!!.invalidate()

            //update the counter - notice this is NOT seconds in this example
            //you need TWO counters - one for the timer count down that will
            // run every second and one for the pacman which need to run
            //faster than every second
            timer.text = getString(R.string.timer, game!!.counter)

            //moving pacman
            when (game!!.direction) {
                1 -> game?.movePacmanUp(20)
                2 -> game?.movePacmanDown(20)
                3 -> game?.movePacmanLeft(20)
                4 -> game?.movePacmanRight(20)
            }
            gameView!!.invalidate()

        }


    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        // Share the score
        if (id == R.id.action_share) {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND

                // The share intent body
                putExtra(Intent.EXTRA_TEXT, "I've just gathered ${game?.points} points with ${game?.counter} seconds left in Catalin's KotlinPacman. Can you beat my score?")
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, "Share Highscore!")
            startActivity(shareIntent)

        // New game
        } else if (id == R.id.action_newGame) {
            Toast.makeText(this, "New Game clicked", Toast.LENGTH_SHORT).show()
            game?.newGame()
            timer.text = getString(R.string.timer, game!!.counter)
            timeLeft.text = getString(R.string.timeLeft, game!!.counter)
        }
        return super.onOptionsItemSelected(item)
    }
}
