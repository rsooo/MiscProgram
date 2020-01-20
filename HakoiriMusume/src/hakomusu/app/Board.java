package hakomusu.app;

import java.util.EnumSet;

import android.graphics.Point;
import android.util.Log;

public class Board {
	static final int MAP_WIDTH = 4;
	static final int MAP_HEIGHT = 5;
	enum DIRECTION{
		UP, DOWN, LEFT, RIGHT
	}
		
	Chip[] chipList = new Chip[10];
	//private int[][] board = new int[4][];
	private int[][] board = {
			{0,1,1,2},
			{0,1,1,2},
			{8,4,4,9},
			{8,3,5,9},
			{6,-1,-1,7}
			};
//			{0,1,1,2,-1},
//			{0,1,1,2,-1},
//			{8,4,4,9,-1},
//			{8,3,5,9,-1},
//			{6,-1,-1,7,-1}
//			};
	

	
	public Board(){
		this.createChipandBoard();
	}
	/**
	 * チップのデータの初期化。ここでの添字がチップ番号に相当する
	 */
	private void createChipandBoard(){
		chipList[0] = new Chip(1,2,0,new Point(0,0));
		chipList[1] = new Chip(2,2,1,new Point(1,0));
		chipList[2] = new Chip(1,2,2,new Point(3,0));
		chipList[3] = new Chip(1,1,3,new Point(1,3));
		chipList[4] = new Chip(2,1,4,new Point(1,2));
		chipList[5] = new Chip(1,1,5,new Point(2,3));
		chipList[6] = new Chip(1,1,6,new Point(0,4));
		chipList[7] = new Chip(1,1,7,new Point(3,4));
		chipList[8] = new Chip(1,2,8,new Point(0,2));
		chipList[9] = new Chip(1,2,9,new Point(3,2));
	}
	
	public Chip getChip(int x, int y){
		return chipList[board[y][x]];
	}
	public int getChipId(int x, int y){
		return board[y][x];
	}
	
	/**
	 * チップが動けるかどうかを判定する
	 * @param chipLocation
	 * @param touchLocation
	 */
	public EnumSet<DIRECTION> moveCheck(Point chipLocation, Point touchLocation){
		Log.i("test", "move check begin");
		EnumSet<DIRECTION> enumSet = EnumSet.noneOf(DIRECTION.class);
		final int mar = 10;
		
		//左右のチェック
		int deffx = (chipLocation.x) - (touchLocation.x);
		if(deffx < -mar){
			enumSet.add(DIRECTION.RIGHT); //右方向移動チェック
		}else if(deffx > mar){
			enumSet.add(DIRECTION.LEFT); //左方向移動チェック			
		}
		
		final int deffy = (chipLocation.y) - (touchLocation.y);
		if(deffy < -mar){
			enumSet.add(DIRECTION.DOWN); //下方向移動チェック
		}else if(deffy > mar){
			enumSet.add(DIRECTION.UP); //上方向移動チェック			
		}
		Log.i("test", "move check end");	
		return enumSet;	
	}
	
	public boolean checkMoveUP(Chip chip){
		Point chipLocation = chip.getLocation();
		if(chipLocation.y == 0){
			return false;
		}
		for(int i = 0;i < chip.getWidth(); i++ ){
			final int x = chipLocation.x + i;
			final int y = chipLocation.y - 1;
			if(board[y][x] != -1){
				return false;
			}
		}
		return true;
	}
	
	public boolean checkMoveDOWN(Chip chip){
		Point chipLocation = chip.getLocation();
		if(chipLocation.y + chip.getHeight() - 1 == MAP_HEIGHT-1){
			return false;
		}
		for(int i = 0;i < chip.getWidth(); i++ ){
			final int x = chipLocation.x + i;
			final int y = chipLocation.y + chip.height;
			if(board[y][x] != -1){
				return false;
			}
		}
		return true;
	}

	public boolean checkMoveLEFT(Chip chip){
		Log.i("test", "check moveleft begin");
		Point chipLocation = chip.getLocation();
		if(chipLocation.x == 0){
			Log.i("test", "check moveleft end:return false");
			return false;
		}
		for(int i = 0;i < chip.getHeight(); i++ ){
			final int x = chipLocation.x - 1;
			final int y = chipLocation.y + i;
			if(board[y][x] != -1){
				Log.i("test", "check moveleft end:return false");
				return false;
			}
		}
		Log.i("test", "check moveleft end:return true");
		return true;
	}

	public boolean checkMoveRIGHT(Chip chip){
		Point chipLocation = chip.getLocation();
		if(chipLocation.x + chip.width - 1 == MAP_WIDTH -1){
			return false;
		}
		for(int i = 0;i < chip.getHeight(); i++ ){
			final int x = chipLocation.x + chip.width;
			final int y = chipLocation.y + i;
			if(board[y][x] != -1){
				return false;
			}
		}
		return true;
	}
	
	public void moveDOWN(Chip chip){
		Point chipLocation = chip.getLocation();
		for(int i = 0;i < chip.width;i++){
			final int x = chipLocation.x + i;
			final int y = chipLocation.y + chip.height;
			board[y][x] = chip.id;
			//動いた分空白増える
			board[chipLocation.y][chipLocation.x+i] = -1;
		}
		//chip.location.x = x;
		chip.location.y = chip.location.y + 1;
	}

	public void moveUP(Chip chip){
		Point chipLocation = chip.getLocation();
		for(int i = 0;i < chip.width;i++){
			final int x = chipLocation.x + i;
			final int y = chipLocation.y - 1;
			board[y][x] = chip.id;
			//動いた分空白増える
			board[chipLocation.y + chip.height - 1][chipLocation.x+i] = -1;
		}
		chip.location.y = chip.location.y - 1;
	}	
	
	public void moveLEFT(Chip chip){
		Point chipLocation = chip.getLocation();
		for(int i = 0;i < chip.height;i++){
			final int x = chipLocation.x - 1;
			final int y = chipLocation.y + i;
			board[y][x] = chip.id;
			//動いた分空白増える
			board[chipLocation.y + i][chipLocation.x + chip.width - 1] = -1;
		}
		chip.location.x = chip.location.x - 1;

	}

	public void moveRIGHT(Chip chip){
		Point chipLocation = chip.getLocation();
		for(int i = 0;i < chip.height;i++){
			final int x = chipLocation.x + chip.width;
			final int y = chipLocation.y + i;
			board[y][x] = chip.id;
			//動いた分空白増える
			board[chipLocation.y + i][chipLocation.x] = -1;			
		}
		chip.location.x = chip.location.x + 1;
	}

	public void printmap(){
		for(int i = 0;i < MAP_HEIGHT;i++){
			for(int j = 0;j < MAP_WIDTH;j++){
				System.out.print(board[i][j] + ",");
			}
			System.out.println("");
		}
	}
	
	public String toString(){
		StringBuffer sb = new StringBuffer();
		for(int i = 0;i < MAP_HEIGHT;i++){
			for(int j = 0;j < MAP_WIDTH;j++){
				sb.append(board[i][j] + ",");
			}
		}
		return sb.toString();
	}
	
	/**
	 * クリアしたかどうかを判定する
	 * @return
	 */
	public boolean checkClaer(){
		if(board[3][1] == 1 && board[3][2] == 1 && board[4][1] == 1 && board[4][2] == 1){
			return true;
		}
		return false;
	}
}
