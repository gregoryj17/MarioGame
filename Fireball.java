package gregoryj17.mariogame;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class Fireball extends Sprite {

	static Bitmap image;
	double vert_vel=0;
	double horiz_vel=4;
	int prevX,prevY;

	public Fireball(int xx, int yy){
		x=xx;
		y=yy;
		w=47;
		h=47;
	}

	public void update(){
		vert_vel += 1.2;
		prevY=y;
		y += vert_vel;
		if(y > 548)
		{
			vert_vel-=0.5;
			vert_vel *= -1.0;
			y = 548; // snap back to the ground
		}
		prevX=x;
		x+=horiz_vel;
		if(x-m.scrollPos>1600||x-m.scrollPos<-50){
			toRemove=true;
		}
		for(Sprite s : m.sprites){
			if(collidesWith(s)){
				if(s instanceof Tube)getOutOfObject(s);
			}
		}
	}

	void getOutOfObject(Sprite s){
		if(prevX+w<s.x&&x+w>=s.x){
			x=s.x-w-1;
			horiz_vel*=-1;
		}
		else if(prevX>s.x+s.w&&x<=s.x+s.w){
			x=s.x+s.w+1;
			horiz_vel*=-1;
		}

		if(prevY+h<s.y&&y+h>=s.y){
			y=s.y-h-1;
			vert_vel*=-1;
		}
		else if(prevY>s.y+s.h&&y<=s.y+s.h){
			y=s.y+s.h+1;
			vert_vel*=-1;
		}
	}

	public void draw(Canvas c, Paint p){
		c.drawBitmap(getImage(), x-m.scrollPos, y, p);
	}

	public Bitmap getImage(){
		try{
			if(image==null){
				throw new RuntimeException("Image not loaded!");
			}
		}catch(Exception e){
			e.printStackTrace(System.err);
			System.exit(1);
		}
		return image;
	}

}
