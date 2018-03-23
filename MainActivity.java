package gregoryj17.mariogame;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
{
    Model model;
    GameView view;
    GameController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        model = new Model();
        view = new GameView(this, model);
        controller = new GameController(model, view);
        setContentView(view);

        model.startGame();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        controller.resume();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        controller.pause();
    }



    static class Model
    {
        ArrayList<Sprite> sprites;
        Mario mario;
        int scrollPos=0;

        Model(){
            sprites = new ArrayList<Sprite>();
            mario = new Mario(this);
            sprites.add(mario);
        }

        public void spawnGoomba(int x, int y){
            Goomba g = new Goomba(x,y);
            sprites.add(g);
        }

        public void shootFlame(){
            Fireball f = new Fireball(mario.x,mario.y);
            sprites.add(f);
        }

        void scroll(int scrollAmount){
            scrollPos += scrollAmount;
        }

        void update()
        {
            for(Sprite s : sprites){
                s.update();
            }
            for(int i=0;i<sprites.size();i++){
                if(sprites.get(i).toRemove){
                    sprites.remove(i);
                    i--;
                }
            }
        }

        void startGame(){
            sprites = new ArrayList<Sprite>();
            sprites.add(mario);

            sprites.add(new Tube(557,496));
            sprites.add(new Tube(875,437));

            sprites.add(new Goomba(766,478));
        }

    }




    static class GameView extends SurfaceView
    {
        SurfaceHolder ourHolder;
        Canvas canvas;
        Paint paint;
        Model model;
        GameController controller;


        public GameView(Context context, Model m)
        {
            super(context);
            model = m;

            // Initialize ourHolder and paint objects
            ourHolder = getHolder();
            paint = new Paint();

            // Load the images
            Fireball.image = BitmapFactory.decodeResource(this.getResources(),
                    R.mipmap.fireball);
            Goomba.image = BitmapFactory.decodeResource(this.getResources(),
                    R.mipmap.goomba);
            Goomba.burnedimage = BitmapFactory.decodeResource(this.getResources(),
                    R.mipmap.goomba_fire);
            Tube.image = BitmapFactory.decodeResource(this.getResources(),
                    R.mipmap.tube);
            Mario.images[0] = BitmapFactory.decodeResource(this.getResources(),
                    R.mipmap.mario1);
            Mario.images[1] = BitmapFactory.decodeResource(this.getResources(),
                    R.mipmap.mario2);
            Mario.images[2] = BitmapFactory.decodeResource(this.getResources(),
                    R.mipmap.mario3);
            Mario.images[3] = BitmapFactory.decodeResource(this.getResources(),
                    R.mipmap.mario4);
            Mario.images[4] = BitmapFactory.decodeResource(this.getResources(),
                    R.mipmap.mario5);

        }

        void setController(GameController c)
        {
            controller = c;
        }

        public void update()
        {
            if (!ourHolder.getSurface().isValid())
                return;
            canvas = ourHolder.lockCanvas();

            // Draw the background color
            canvas.drawColor(Color.argb(255, 128, 200, 200));

            canvas.drawLine(0,630,2000,630,paint);

            // Draw the score
            /*paint.setColor(Color.argb(255,  200, 128, 0));
            paint.setTextSize(45);
            canvas.drawText("Score:" + score, 25, 35, paint);*/

            // Draw the turtle
            for(Sprite s : model.sprites){
                s.draw(canvas, paint);
            }
            model.mario.draw(canvas,paint);

            ourHolder.unlockCanvasAndPost(canvas);
        }

        // The SurfaceView class (which GameView extends) already
        // implements onTouchListener, so we override this method
        // and pass the event to the controller.
        @Override
        public boolean onTouchEvent(MotionEvent motionEvent)
        {
            controller.onTouchEvent(motionEvent);
            return true;
        }
    }




    static class GameController implements Runnable
    {
        volatile boolean playing;
        Thread gameThread = null;
        Model model;
        GameView view;
        boolean keyLeft;
        boolean keyRight;
        boolean keyUp;

        GameController(Model m, GameView v)
        {
            model = m;
            view = v;
            view.setController(this);
            playing = true;
        }

        void update()
        {
            int scrollSpeed = 6;
            if(keyLeft){
                model.scroll(-1*scrollSpeed);
            }
            if(keyRight){
                model.scroll(scrollSpeed);
            }
            if(keyUp){
                model.mario.jump();
            }
            model.mario.moving(keyLeft,keyRight);
        }

        @Override
        public void run()
        {
            while(playing)
            {
                //long time = System.currentTimeMillis();
                this.update();
                model.update();
                view.update();

                try {
                    Thread.sleep(20);
                } catch(Exception e) {
                    Log.e("Error:", "sleeping");
                    System.exit(1);
                }
            }
        }


        void onTouchEvent(MotionEvent motionEvent)
        {
            switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN: // Player touched the screen
                    //Top-Left, Jump
                    if(motionEvent.getX()<(getWidth()/2)&&motionEvent.getY()<(getHeight()/2)){
                        keyUp=true;
                    }
                    //Top-Right, Fireball
                    else if(motionEvent.getX()>=(getWidth()/2)&&motionEvent.getY()<(getHeight()/2)){
                        model.shootFlame();
                    }
                    //Bottom-Left, Move left
                    else if(motionEvent.getX()<(getWidth()/2)&&motionEvent.getY()>=(getHeight()/2)){
                        keyLeft=true;
                    }
                    //Bottom-Right, Move right
                    else{
                        keyRight=true;
                    }
                    break;

                case MotionEvent.ACTION_UP: // Player withdrew finger
                    keyUp=false;
                    keyLeft=false;
                    keyRight=false;
                    break;
            }
        }

        // Shut down the game thread.
        public void pause() {
            playing = false;
            try {
                gameThread.join();
            } catch (InterruptedException e) {
                Log.e("Error:", "joining thread");
                System.exit(1);
            }

        }

        // Restart the game thread.
        public void resume() {
            playing = true;
            gameThread = new Thread(this);
            gameThread.start();
        }

        public static int getWidth() {
            return Resources.getSystem().getDisplayMetrics().widthPixels;
        }

        public static int getHeight() {
            return Resources.getSystem().getDisplayMetrics().heightPixels;
        }
    }
}