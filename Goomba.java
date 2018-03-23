package gregoryj17.mariogame;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

public class Goomba extends Sprite {

	static Bitmap image;
	static Bitmap burnedimage;

	boolean right;
	int turnSteps;
	int curSteps;
	final int stepSize=1;
	int prevX;
	int frame=0;
	int burnedframe=-1;

	public Goomba(int x, int y){
		this.x=x;
		this.y=y;
		right=true;
		turnSteps=300;
		curSteps=0;
		w=99;
		h=118;
	}

	public Goomba(Json json){
		this.x=(int)json.getLong("x");
		this.y=(int)json.getLong("y");
		right=json.getBool("right");
		turnSteps=(int)json.getLong("turnSteps");
		curSteps=(int)json.getLong("curSteps");
		w=99;
		h=118;
	}


	void update() {
		prevX=x;
		x+=stepSize*(right?1:-1);
		curSteps++;
		if(curSteps>=turnSteps){
			curSteps=0;
			right=!right;
		}
		for(Sprite s : m.sprites){
			if(collidesWith(s)){
				if(s instanceof Tube||s instanceof Mario)getOutOfObject(s);
				else if(s instanceof Fireball){
					if(burnedframe==-1)burnedframe=frame;
					s.toRemove=true;
				}
			}
		}
		if(burnedframe!=-1&&frame>=burnedframe+10){
			toRemove=true;
		}
		frame++;
	}

	void getOutOfObject(Sprite s){
		if(prevX+w<s.x&&x+w>=s.x){
			x=s.x-w-1;
		}
		else if(prevX>s.x+s.w&&x<=s.x+s.w){
			x=s.x+s.w+1;
		}
		curSteps=0;
		right=!right;
	}

	void draw(Canvas c, Paint p){
		c.drawBitmap(getImage(),x-m.scrollPos,y,p);
	}

	Bitmap getImage(){
		try{
			if(image==null){
                throw new RuntimeException("Image not loaded!");
			}
			else if(burnedframe!=-1&&burnedimage==null){
                throw new RuntimeException("Image not loaded!");
			}
		}catch(Exception e){
			e.printStackTrace(System.err);
			System.exit(1);
		}
		if(burnedframe!=-1)return burnedimage;
		return image;
	}

	Json marshal(){
		Json ob = Json.newObject();
		ob.add("x",x);
		ob.add("y",y);
		ob.add("right",right);
		ob.add("turnSteps",turnSteps);
		ob.add("curSteps",curSteps);
		return ob;
	}

}
