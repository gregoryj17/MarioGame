package gregoryj17.mariogame;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

public class Mario extends Sprite {

    double vert_vel=0;
    int frame;
    int lastGrounded;
    int pic=0;
    boolean moving=false;
    static Bitmap[] images = new Bitmap[5];
    int prevX,prevY;
    int heat=0;

    public Mario(MainActivity.Model model){
    	x=200;
    	y=0;
    	w=60;
    	h=95;
		m=model;
	}

    void update()
    {
		vert_vel += 1.2;
		prevY=y;
		y += vert_vel;
		if(y > 500)
		{
			vert_vel = 0.0;
			y = 500; // snap back to the ground
			lastGrounded=frame;
		}
		prevX=x;
		x=m.scrollPos+200;


		for(Sprite s : m.sprites){
			if(collidesWith(s)){
				if(s instanceof Tube)getOutOfTube(s);
				else if(s instanceof Goomba) enemyBounce(s);
				else if(s instanceof Fireball) heat++;
			}
		}

        frame++;
        if(moving)pic=(pic+1)%5;
    }

    void getOutOfTube(Sprite s){
    	if(prevX+w<s.x&&x+w>=s.x){
    		x=s.x-w-1;
    		m.scrollPos=x-200;
		}
		else if(prevX>s.x+s.w&&x<=s.x+s.w){
    		x=s.x+s.w+1;
    		m.scrollPos=x-200;
		}

		if(prevY+h<s.y&&y+h>=s.y){
    		y=s.y-h-1;
    		vert_vel=0;
    		lastGrounded=frame;
		}
		else if(prevY>s.y+s.h&&y<=s.y+s.h){
    		y=s.y+s.h+1;
    		vert_vel=0;
		}
	}

	void enemyBounce(Sprite s){
		if(prevX+w<s.x&&x+w>=s.x){
			x=s.x-w-1;
			m.scrollPos=x-200;
		}
		else if(prevX>s.x+s.w&&x<=s.x+s.w){
			x=s.x+s.w+1;
			m.scrollPos=x-200;
		}

		if(prevY+h<s.y&&y+h>=s.y){
			y=s.y-h-1;
			vert_vel=-20;
			lastGrounded=frame-4;
			s.toRemove=true;
		}
		else if(prevY>s.y+s.h&&y<=s.y+s.h){
			y=s.y+s.h+1;
			vert_vel=0;
		}
	}

    void draw(Canvas c, Paint p){
		c.drawBitmap(getImage(),x-m.scrollPos,y,p);
    }

    void jump(){
        if(frame-lastGrounded<=5) {
            vert_vel -= 5;
        }
    }

    void moving(boolean left, boolean right){
        moving = ((left^right)||vert_vel!=0);
    }

    Bitmap getImage(){
        try{
            if(images[pic]==null){
                throw new RuntimeException("Image not loaded!");
            }
        }catch(Exception e){
            e.printStackTrace(System.err);
            System.exit(1);
        }

        return images[pic];
    }

}
