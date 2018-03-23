package gregoryj17.mariogame;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import java.io.File;

public class Tube extends Sprite{
	
	//Tube position
	static Bitmap image;
	
	//Constructs a tube with a position
	public Tube(int x, int y){
		this.x=x;
		this.y=y;
		w=55;
		h=400;
	}
	
	public Tube(Json json){
		this.x=(int)json.getLong("x");
		this.y=(int)json.getLong("y");
		w=55;
		h=400;
	}
	
	boolean wasClicked(int clickX, int clickY){
		return (clickX>=x&&clickX<=x+w&&clickY>=y&&clickY<=y+h);
	}

	public void update(){

    }

    public void draw(Canvas c, Paint p){
	    c.drawBitmap(getImage(), x-m.scrollPos, y, p);
    }

	Json marshal(){
		Json ob = Json.newObject();
		ob.add("x",x);
		ob.add("y",y);
		return ob;
	}

	Bitmap getImage(){
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
