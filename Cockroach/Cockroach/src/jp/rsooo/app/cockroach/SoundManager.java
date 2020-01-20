package jp.rsooo.app.cockroach;

import jp.rsooo.app.cockroach.R;


import android.content.Context;
import android.media.MediaPlayer;

/**
 * ‰¹‚ÌŠÇ—
 * @author akira
 *
 */
public class SoundManager {
	MediaPlayer[] players = new MediaPlayer[4];
	Context context;
	
	public SoundManager(Context c){
		context = c;
		createSound();
	} 
	/**
	 * ‰¹‚Ì€”õ
	 */
	public void createSound(){
		players[0] = MediaPlayer.create(context, R.raw.hit);
//		players[1] = MediaPlayer.create(context, R.raw.dead);
//		players[2] = MediaPlayer.create(context, R.raw.pass);
//		players[3] = MediaPlayer.create(context, R.raw.tap);

	}
	
	public void play(int i){
		players[i].start();
	}
	
	public void end(){
		for(MediaPlayer player : players){
			if(player != null){
				player.stop();
				player.release();
				player = null;
			}
		}
	}
	
}
