package jp.rsooo.app.cockroach;

import android.graphics.Rect;
import java.util.*;

public class StageInitializer {
	final int width;
	final int height;
	public Stage[] STAGE = new Stage[20];
	//���C�u�񕜗ʂ�\��
	public int[] liferecove = {0,0,0,1,1,1,1,2,0,0,5,5,5,5,30,0,0,0,0,0,0,0,0,0,0,0,0,0};
	public static final int INITSTAGE = 0;

    final private int garbageSize;
    final private int gboxSize;

	StageInitializer(int w, int h) {
		width = w;
		height = h;
        garbageSize = height / 4;
        gboxSize = height / 6;
	}

	void createStage() {
		{
			int num = 8;
			int period = 4000;
			int backgroundId = 0;
			List<Rect> gabagelist = new ArrayList<Rect>();
			List<Rect> trashlist = new ArrayList<Rect>();
			List<CockGeneratePoint> genPtlist = new ArrayList<CockGeneratePoint>();

			Rect g1 = new Rect(width * 3 / 5, height / 8, width * 3 / 5 + garbageSize,
					height / 8 + garbageSize);
			gabagelist.add(g1);
			CockGeneratePoint gen1 = new CockGeneratePoint(
					this.width * 3 / 5 + 50, height / 8 + 50, new int[] { 0,
						15, 14, 13, 12, 11, 10, 9 }, 2);
			genPtlist.add(gen1);
			STAGE[0] = new Stage(num, period, backgroundId,gabagelist, trashlist, genPtlist);
		}
		{
			int num = 15;
			int period = 1000;
			int backgroundId = 0;
			List<Rect> gabagelist = new ArrayList<Rect>();
			List<Rect> trashlist = new ArrayList<Rect>();
			List<CockGeneratePoint> genPtlist = new ArrayList<CockGeneratePoint>();

			Rect g1 = new Rect(width * 3 / 5, height / 8, width * 3 / 5 + garbageSize,
					height / 8 + garbageSize);
			// gabagelist.add(g1);
			gabagelist.add(g1);
			CockGeneratePoint gen1 = new CockGeneratePoint(
					this.width * 3 / 5 + 50, height / 8 + 50, new int[] { 0,
							15, 14, 13, 12, 11, 10, 9 }, 2);
			genPtlist.add(gen1);
			Rect g2 = new Rect(width / 8, height / 6, width / 8 + garbageSize,
					height / 6 + garbageSize);
			gabagelist.add(g2);
			CockGeneratePoint gen2 = new CockGeneratePoint(this.width / 8 + 50,
					height / 8 + 50, new int[] { 13, 12, 11, 10, 9, 8, 7 }, 1);
			genPtlist.add(gen2);
			STAGE[1] = new Stage(num, period, backgroundId, gabagelist, trashlist, genPtlist);
		}
		{
			int num = 15;
			int period = 1000;
			int backgroundId = 0;
			List<Rect> gabagelist = new ArrayList<Rect>();
			List<Rect> trashlist = new ArrayList<Rect>();
			List<CockGeneratePoint> genPtlist = new ArrayList<CockGeneratePoint>();

			Rect g1 = new Rect(width * 3 / 5, height / 8, width * 3 / 5 + garbageSize,
					height / 8 + garbageSize);
			// gabagelist.add(g1);
			gabagelist.add(g1);
			CockGeneratePoint gen1 = new CockGeneratePoint(
					this.width * 3 / 5 + 50, height / 8 + 50, new int[] { 0,
							15, 14, 13, 12, 11, 10, 9, }, 6);
			genPtlist.add(gen1);
			Rect g2 = new Rect(width / 8, height / 8, width / 8 + garbageSize,
					height / 8 + garbageSize);
			gabagelist.add(g2);
			CockGeneratePoint gen2 = new CockGeneratePoint(this.width / 8 + 50,
					height / 8 + 50, new int[] { 13, 12, 11, 10, 9, 8, 7 }, 1);
			genPtlist.add(gen2);
			genPtlist.add(new CockGeneratePoint(-20,
					height / 2, new int[] { 7,9}, 7));
			
			
			
			Rect g3 = new Rect(width * 1 / 2, height * 1 / 2, width * 1 / 2 + gboxSize,
					height * 1 / 2 + gboxSize * 2);
			// gabagelist.add(g1);
			trashlist.add(g3);
			STAGE[2] = new Stage(num, period, backgroundId, gabagelist, trashlist, genPtlist);
		}
		/*
		{
			int num = 40;
			int period = 700;
			int backgroundId = 0;
			List<Rect> gabagelist = new ArrayList<Rect>();
			List<Rect> trashlist = new ArrayList<Rect>();
			List<CockGeneratePoint> genPtlist = new ArrayList<CockGeneratePoint>();

			Rect g1 = new Rect(width * 3 / 5, height / 8, width * 3 / 5 + 100,
					height / 8 + 100);
			// gabagelist.add(g1);
			gabagelist.add(g1);
			CockGeneratePoint gen1 = new CockGeneratePoint(
					this.width * 3 / 5 + 50, height / 8 + 50, new int[] { 0,
							15, 14, 13, 12, 11, 10, 9, }, 2);
			genPtlist.add(gen1);
			Rect g2 = new Rect(width / 8, height / 8, width / 8 + 100,
					height / 8 + 100);
			gabagelist.add(g2);
			CockGeneratePoint gen2 = new CockGeneratePoint(this.width / 8 + 50,
					height / 8 + 50, new int[] { 13, 12, 11, 10, 9, 8, 7 }, 1);
			genPtlist.add(gen2);
			Rect g3 = new Rect(width * 1 / 2, height * 1 / 2, width * 1 / 2 + 50,
					height * 1 / 2 + 100);
			// gabagelist.add(g1);
			trashlist.add(g3);
			Rect g4 = new Rect(width * 1 / 2, height / 8, width * 1 / 2 + 100,
					height / 8 + 100);
			// gabagelist.add(g1);
			gabagelist.add(g4);
			CockGeneratePoint gen3 = new CockGeneratePoint(
					this.width * 1 / 2 + 50, height / 8 + 50, new int[] { 0,
							15, 14, 13, 12, 11, 10, 9, 8, 7 }, 2);
			genPtlist.add(gen3);
			STAGE[3] = new Stage(num, period, backgroundId, gabagelist, trashlist, genPtlist);
		}
		*/
		{
			int num = 20;
			int period = 800;
			int backgroundId = 1;
			List<Rect> gabagelist = new ArrayList<Rect>();
			List<Rect> trashlist = new ArrayList<Rect>();
			List<CockGeneratePoint> genPtlist = new ArrayList<CockGeneratePoint>();

			Rect g1 = new Rect(width * 3 / 5, height / 8, width * 3 / 5 + garbageSize,
					height / 8 + garbageSize);
			gabagelist.add(g1);
			CockGeneratePoint gen1 = new CockGeneratePoint(
					this.width * 3 / 5 + 50, height / 8 + 50, new int[] { 0,
							15,14,13,12, 11, 10, 9, }, 4);
			CockGeneratePoint gen2 = new CockGeneratePoint(
					this.width + 20 , height + 20, new int[] { 
							1,2,3 }, 3);
			genPtlist.add(new CockGeneratePoint(-20,
					height * 2 / 3, new int[] { 7,9}, 4));
			
			genPtlist.add(gen1);
			genPtlist.add(gen2);
			STAGE[3] = new Stage(num, period, backgroundId,gabagelist, trashlist, genPtlist);
		} 
		{
			int num = 20;
			int period = 700;
			int backgroundId = 1;
			List<Rect> gabagelist = new ArrayList<Rect>();
			List<Rect> trashlist = new ArrayList<Rect>();
			List<CockGeneratePoint> genPtlist = new ArrayList<CockGeneratePoint>();

			Rect g1 = new Rect(width * 3 / 5, height / 8, width * 3 / 5 + garbageSize,
					height / 8 + garbageSize);
			gabagelist.add(g1);
			CockGeneratePoint gen1 = new CockGeneratePoint(
					this.width * 3 / 5 + 50, height / 8 + 50, new int[] { 0,
							15,14,13, 12, 11, 10, 9, }, 5);
			CockGeneratePoint gen2 = new CockGeneratePoint(
					this.width + 20 , height + 20, new int[] { 
							1,2,3 }, 3);
			Rect g3 = new Rect(width * 1 / 2, height * 1 / 2, width * 1 / 2 + gboxSize,
					height * 1 / 2 + gboxSize * 2);
			trashlist.add(g3);
			
			
			genPtlist.add(gen1);
			genPtlist.add(gen2);
            genPtlist.add(new CockGeneratePoint(-20, height / 5, new int[]{8, 9},7));

            STAGE[4] = new Stage(num, period, backgroundId,gabagelist, trashlist, genPtlist);
		} 
		{
			int num = 30;
			int period = 700;
			int backgroundId = 2;
			List<Rect> gabagelist = new ArrayList<Rect>();
			List<Rect> trashlist = new ArrayList<Rect>();
			List<CockGeneratePoint> genPtlist = new ArrayList<CockGeneratePoint>();

			//Rect g1 = new Rect(width * 3 / 5, height / 8, width * 3 / 5 + 100,
			//		height / 8 + 100);
			// gabagelist.add(g1);
//			gabagelist.add(g1);
			CockGeneratePoint gen1 = new CockGeneratePoint(
					this.width * 2 / 3 , height / 3 , new int[] { 0,
						15, 14, 13, 12, 11, 10, 9, 8, 7}, 7);
			genPtlist.add(gen1);
			//Rect g2 = new Rect(width / 8, height / 8, width / 8 + 100,
			//		height / 8 + 100);
			//gabagelist.add(g2);
			CockGeneratePoint gen2 = new CockGeneratePoint(-20,
					height + 20, new int[] {6,7 }, 6);
			genPtlist.add(gen2);
			genPtlist.add(new CockGeneratePoint(-20, height / 2, new int[]{8, 9}, 1));
			STAGE[5] = new Stage(num, period, backgroundId, gabagelist, trashlist, genPtlist);
		}
		{
			int num = 50;
			int period = 650;
			int backgroundId = 2;
			List<Rect> gabagelist = new ArrayList<Rect>();
			List<Rect> trashlist = new ArrayList<Rect>();
			List<CockGeneratePoint> genPtlist = new ArrayList<CockGeneratePoint>();

			//Rect g1 = new Rect(width * 3 / 5, height / 8, width * 3 / 5 + 100,
			//		height / 8 + 100);
			// gabagelist.add(g1);
//			gabagelist.add(g1);
			CockGeneratePoint gen1 = new CockGeneratePoint(
					this.width * 2 / 3 , height / 3 , new int[] { 0,
						15, 14, 13, 12, 11, 10, 9, 8, 7}, 8);
			genPtlist.add(gen1);
			Rect g2 = new Rect(width / 6, height / 5, width / 6 + garbageSize,
					height / 5 + garbageSize);
			gabagelist.add(g2);
			CockGeneratePoint gen2 = new CockGeneratePoint(-20,
					height + 20, new int[] {6,7 }, 9);
			genPtlist.add(gen2);
			genPtlist.add(new CockGeneratePoint(width / 6 + 35, height / 5 + 35, new int[]{8, 9, 10 }, 2));
			genPtlist.add(new CockGeneratePoint(-20, height / 2, new int[]{8, 9}, 1));
            genPtlist.add(new CockGeneratePoint(-20, height / 5, new int[]{8, 9},7));

            STAGE[6] = new Stage(num, period, backgroundId, gabagelist, trashlist, genPtlist);
		}
		{
			int num = 5;
			int period = 2000;
			int backgroundId = 3;
			List<Rect> gabagelist = new ArrayList<Rect>();
			List<Rect> trashlist = new ArrayList<Rect>();
			List<CockGeneratePoint> genPtlist = new ArrayList<CockGeneratePoint>();

			CockGeneratePoint gen1 = new CockGeneratePoint(
					-20, height * 2 / 3, new int[] { 6,7,
							}, 8);
			genPtlist.add(gen1);
/*			CockGeneratePoint gen2 = new CockGeneratePoint(this.width / 8 + 50,
					height / 8 + 50, new int[] { 13, 12, 11, 10, 9, 8, 7 }, 2);
			genPtlist.add(gen2);
			Rect g4 = new Rect(width * 1 / 3, height / 8, width * 1 / 3 + 100,
					height / 8 + 100);
			// gabagelist.add(g1);
			gabagelist.add(g4);
*/			STAGE[7] = new Stage(num, period, backgroundId, gabagelist, trashlist, genPtlist);
		}
		{
			int num = 8;
			int period = 1500;
			int backgroundId = 3;
			List<Rect> gabagelist = new ArrayList<Rect>();
			List<Rect> trashlist = new ArrayList<Rect>();
			List<CockGeneratePoint> genPtlist = new ArrayList<CockGeneratePoint>();

			CockGeneratePoint gen1 = new CockGeneratePoint(
					-20, height * 2 / 3, new int[] { 6,7,
							}, 10);
			genPtlist.add(gen1);
			genPtlist.add(new CockGeneratePoint(-20, height * 4 / 5, new int[]{8},7));
			genPtlist.add(new CockGeneratePoint(-20, height / 2, new int[]{8,9},7));
			
			Rect g3 = new Rect(width * 3 / 4, height * 2 / 3, width * 3 / 4 + gboxSize,
					height * 2 / 3 + gboxSize * 2);
			trashlist.add(g3);

			STAGE[8] = new Stage(num, period, backgroundId,gabagelist, trashlist, genPtlist);
		}
		{
			int num = 80;
			int period = 300;
			int backgroundId = 4;
			List<Rect> gabagelist = new ArrayList<Rect>();
			List<Rect> trashlist = new ArrayList<Rect>();
			List<CockGeneratePoint> genPtlist = new ArrayList<CockGeneratePoint>();

			Rect g1 = new Rect(width * 3 / 5, height / 8, width * 3 / 5 + garbageSize,
					height / 8 + garbageSize);
			// gabagelist.add(g1);
			gabagelist.add(g1);
			CockGeneratePoint gen1 = new CockGeneratePoint(
					this.width * 3 / 5 + 50, height / 8 + 50, new int[] { 0,
							15, 14, 13, 12, 11, 10, 9, 8, 7 }, 1);
			genPtlist.add(gen1);
			Rect g2 = new Rect(width / 8, height / 8, width / 8 + garbageSize,
					height / 8 + garbageSize);
			gabagelist.add(g2);
			CockGeneratePoint gen2 = new CockGeneratePoint(this.width / 8 + 50,
					height / 8 + 50, new int[] { 13, 12, 11, 10, 9, 8, 7 }, 1);
			genPtlist.add(gen2);
            genPtlist.add(new CockGeneratePoint(-20, height / 5, new int[]{8, 9, 10},10));

            STAGE[9] = new Stage(num, period, backgroundId, gabagelist, trashlist, genPtlist);
		}
		{
			int num = 120;
			int period = 400;
			int backgroundId = 4;
			List<Rect> gabagelist = new ArrayList<Rect>();
			List<Rect> trashlist = new ArrayList<Rect>();
			List<CockGeneratePoint> genPtlist = new ArrayList<CockGeneratePoint>();

			Rect g1 = new Rect(width * 3 / 5, height / 8, width * 3 / 5 + garbageSize,
					height / 8 + garbageSize);
			// gabagelist.add(g1);
			gabagelist.add(g1);
			CockGeneratePoint gen1 = new CockGeneratePoint(
					this.width * 3 / 5 + 50, height / 8 + 50, new int[] { 0,
							15, 14, 13, 12, 11, 10, 9, 8, 7 }, 4);
			genPtlist.add(gen1);
			genPtlist.add(new CockGeneratePoint(width / 3 + 50, height / 2 + 50, new int[]{0,1,15,7,8,9},2));
			Rect g2 = new Rect(width / 8, height / 8, width / 8 + garbageSize,
					height / 8 + garbageSize);
			gabagelist.add(g2);
			gabagelist.add(new Rect(width / 3, height / 2, width / 3 + 120, height / 2 + 120));
			CockGeneratePoint gen2 = new CockGeneratePoint(this.width / 8 + 50,
					height / 8 + 50, new int[] { 13, 12, 11, 10, 9, 8, 7 }, 4);
			genPtlist.add(gen2);
			STAGE[10] = new Stage(num, period, backgroundId, gabagelist, trashlist, genPtlist);
		}
		{
			int num = 100;
			int period = 300;
			int backgroundId = 5;
			List<Rect> gabagelist = new ArrayList<Rect>();
			List<Rect> trashlist = new ArrayList<Rect>();
			List<CockGeneratePoint> genPtlist = new ArrayList<CockGeneratePoint>();

//			58.4%,72.9%;
			genPtlist.add(new CockGeneratePoint((int)(width * 0.55), (int)(height * 0.7), new int[]{0,1,15,7,8,9},3));
			genPtlist.add(new CockGeneratePoint((width + 20), height / 4, new int[]{15,14,13},4));
				
			STAGE[11] = new Stage(num, period, backgroundId, gabagelist, trashlist, genPtlist);
		}
		{
			int num = 130;
			int period = 300;
			int backgroundId = 5;
			List<Rect> gabagelist = new ArrayList<Rect>();
			List<Rect> trashlist = new ArrayList<Rect>();
			List<CockGeneratePoint> genPtlist = new ArrayList<CockGeneratePoint>();

//			58.4%,72.9%;
			genPtlist.add(new CockGeneratePoint((int)(width * 0.55), (int)(height * 0.7), new int[]{0,1,15,7,8,9},4));
			genPtlist.add(new CockGeneratePoint((width + 20), height / 4, new int[]{15,14,13},5));
			genPtlist.add(new CockGeneratePoint((-20), height * 3 / 4, new int[]{8,7},5));
			
			STAGE[12] = new Stage(num, period, backgroundId,gabagelist, trashlist, genPtlist);
		}
		{
			int num = 160;
			int period = 250;
			int backgroundId = 6;
			List<Rect> gabagelist = new ArrayList<Rect>();
			List<Rect> trashlist = new ArrayList<Rect>();
			List<CockGeneratePoint> genPtlist = new ArrayList<CockGeneratePoint>();

			genPtlist.add(new CockGeneratePoint((int)(width * 0.584), (int)(height * 0.729), new int[]{0,1,15,7,8,9},3));
			genPtlist.add(new CockGeneratePoint(-20, height / 2, new int[]{9,8,7},5));
			genPtlist.add(new CockGeneratePoint((width + 20), height / 4, new int[]{15,14,13},4));
			genPtlist.add(new CockGeneratePoint((-20), height * 3 / 4, new int[]{8,7},4));
			STAGE[13] = new Stage(num, period, backgroundId, gabagelist, trashlist, genPtlist);
		}
		{
			int num = 170;
			int period = 300;
			int backgroundId = 7;
			List<Rect> gabagelist = new ArrayList<Rect>();
			List<Rect> trashlist = new ArrayList<Rect>();
			List<CockGeneratePoint> genPtlist = new ArrayList<CockGeneratePoint>();

			Rect g1 = new Rect(width * 3 / 5, height / 8, width * 3 / 5 + garbageSize,
					height / 8 + garbageSize);
			// gabagelist.add(g1);
			gabagelist.add(g1);
			CockGeneratePoint gen1 = new CockGeneratePoint(
					this.width * 3 / 5 + 50, height / 8 + 50, new int[] { 0,
							15, 14, 13, 12, 11, 10, 9, 8, 7 }, 4);
			genPtlist.add(gen1);
			Rect g2 = new Rect(width / 8, height / 8, width / 8 + garbageSize,
					height / 8 + garbageSize);
			gabagelist.add(g2);
			CockGeneratePoint gen2 = new CockGeneratePoint(this.width / 8 + 50,
					height / 8 + 50, new int[] { 13, 12, 11, 10, 9, 8, 7 }, 3);
			genPtlist.add(gen2);
			Rect g3 = new Rect(width * 1 / 2, height * 1 / 2, width * 1 / 2 + gboxSize,
					height * 1 / 2 + gboxSize * 2);
			// gabagelist.add(g1);
			trashlist.add(g3);
			STAGE[14] = new Stage(num, period, backgroundId, gabagelist, trashlist, genPtlist);
		}
		{
			int num = 5480;
			int period = 100;
			int backgroundId = 3;
			List<Rect> gabagelist = new ArrayList<Rect>();
			List<Rect> trashlist = new ArrayList<Rect>();
			List<CockGeneratePoint> genPtlist = new ArrayList<CockGeneratePoint>();

			Rect g1 = new Rect(width * 3 / 5, height / 8, width * 3 / 5 + garbageSize,
					height / 8 + garbageSize);
			// gabagelist.add(g1);
			gabagelist.add(g1);
			CockGeneratePoint gen1 = new CockGeneratePoint(
					this.width * 3 / 5 + 50, height / 8 + 50, new int[] { 0,
							15, 14, 13, 12, 11, 10, 9, 8, 7 }, 4);
			genPtlist.add(gen1);
			Rect g2 = new Rect(width / 8, height / 8, width / 8 + garbageSize,
					height / 8 + garbageSize);
			gabagelist.add(g2);
			CockGeneratePoint gen2 = new CockGeneratePoint(this.width / 8 + 50,
					height / 8 + 50, new int[] { 13, 12, 11, 10, 9, 8, 7 }, 3);
			genPtlist.add(gen2);
			Rect g3 = new Rect(width * 1 / 2, height * 1 / 2, width * 1 / 2 + gboxSize,
					height * 1 / 2 + gboxSize * 2);
			// gabagelist.add(g1);
			trashlist.add(g3);
			Rect g4 = new Rect(width * 1 / 3, height / 8, width * 1 / 3 + garbageSize,
					height / 8 + garbageSize);
			// gabagelist.add(g1);
			gabagelist.add(g4);
			CockGeneratePoint gen3 = new CockGeneratePoint(
					this.width * 1 / 3 + 50, height / 8 + 50, new int[] { 0,
							15, 14, 13, 12, 11, 10, 9, 8, 7 }, 3);
			genPtlist.add(gen3);
			STAGE[15] = new Stage(num, period, backgroundId, gabagelist, trashlist, genPtlist);
		}
		{
			int num = 50;
			int period = 200;
			int backgroundId = 4;
			List<Rect> gabagelist = new ArrayList<Rect>();
			List<Rect> trashlist = new ArrayList<Rect>();
			List<CockGeneratePoint> genPtlist = new ArrayList<CockGeneratePoint>();

			Rect g1 = new Rect(width * 3 / 5, height / 8, width * 3 / 5 + garbageSize,
					height / 8 + garbageSize);
			gabagelist.add(g1);
			CockGeneratePoint gen1 = new CockGeneratePoint(
					this.width * 3 / 5 + 50, height / 8 + 50, new int[] { 0,
							15, 14, 13, 12, 11, 10, 9, 8, 7 }, 3);
			genPtlist.add(gen1);
			STAGE[16] = new Stage(num, period, backgroundId,gabagelist, trashlist, genPtlist);
		}
		{
			int num = 100;
			int period = 200;
			int backgroundId = 4;
			List<Rect> gabagelist = new ArrayList<Rect>();
			List<Rect> trashlist = new ArrayList<Rect>();
			List<CockGeneratePoint> genPtlist = new ArrayList<CockGeneratePoint>();

			Rect g1 = new Rect(width * 3 / 5, height / 8, width * 3 / 5 + garbageSize,
					height / 8 + garbageSize);
			// gabagelist.add(g1);
			gabagelist.add(g1);
			CockGeneratePoint gen1 = new CockGeneratePoint(
					this.width * 3 / 5 + 50, height / 8 + 50, new int[] { 0,
							15, 14, 13, 12, 11, 10, 9, 8, 7 }, 4);
			genPtlist.add(gen1);
			Rect g2 = new Rect(width / 8, height / 8, width / 8 + garbageSize,
					height / 8 + garbageSize);
			gabagelist.add(g2);
			CockGeneratePoint gen2 = new CockGeneratePoint(this.width / 8 + 50,
					height / 8 + 50, new int[] { 13, 12, 11, 10, 9, 8, 7 }, 2);
			genPtlist.add(gen2);
			STAGE[17] = new Stage(num, period, backgroundId, gabagelist, trashlist, genPtlist);
		}
		{
			int num = 150;
			int period = 200;
			int backgroundId = 4;
			List<Rect> gabagelist = new ArrayList<Rect>();
			List<Rect> trashlist = new ArrayList<Rect>();
			List<CockGeneratePoint> genPtlist = new ArrayList<CockGeneratePoint>();

			Rect g1 = new Rect(width * 3 / 5, height / 8, width * 3 / 5 + garbageSize,
					height / 8 + garbageSize);
			// gabagelist.add(g1);
			gabagelist.add(g1);
			CockGeneratePoint gen1 = new CockGeneratePoint(
					this.width * 3 / 5 + 50, height / 8 + 50, new int[] { 0,
							15, 14, 13, 12, 11, 10, 9, 8, 7 }, 4);
			genPtlist.add(gen1);
			Rect g2 = new Rect(width / 8, height / 8, width / 8 + garbageSize,
					height / 8 + garbageSize);
			gabagelist.add(g2);
			CockGeneratePoint gen2 = new CockGeneratePoint(this.width / 8 + 50,
					height / 8 + 50, new int[] { 13, 12, 11, 10, 9, 8, 7 }, 3);
			genPtlist.add(gen2);
			Rect g3 = new Rect(width * 1 / 2, height * 1 / 2, width * 1 / 2 + gboxSize,
					height * 1 / 2 + gboxSize * 2);
			// gabagelist.add(g1);
			trashlist.add(g3);
			STAGE[18] = new Stage(num, period, backgroundId, gabagelist, trashlist, genPtlist);
		}
		{
			int num = 1000;
			int period = 200;
			int backgroundId = 4;
			List<Rect> gabagelist = new ArrayList<Rect>();
			List<Rect> trashlist = new ArrayList<Rect>();
			List<CockGeneratePoint> genPtlist = new ArrayList<CockGeneratePoint>();

			Rect g1 = new Rect(width * 3 / 5, height / 8, width * 3 / 5 + garbageSize,
					height / 8 + garbageSize);
			// gabagelist.add(g1);
			gabagelist.add(g1);
			CockGeneratePoint gen1 = new CockGeneratePoint(
					this.width * 3 / 5 + 50, height / 8 + 50, new int[] { 0,
							15, 14, 13, 12, 11, 10, 9, 8, 7 }, 4);
			genPtlist.add(gen1);
			Rect g2 = new Rect(width / 8, height / 8, width / 8 + garbageSize,
					height / 8 + garbageSize);
			gabagelist.add(g2);
			CockGeneratePoint gen2 = new CockGeneratePoint(this.width / 8 + 50,
					height / 8 + 50, new int[] { 13, 12, 11, 10, 9, 8, 7 }, 3);
			genPtlist.add(gen2);
			Rect g3 = new Rect(width * 1 / 2, height * 1 / 2, width * 1 / 2 + 50,
					height * 1 / 2 + 100);
			// gabagelist.add(g1);
			trashlist.add(g3);
			Rect g4 = new Rect(width * 1 / 3, height / 8, width * 1 / 3 + 100,
					height / 8 + 100);
			// gabagelist.add(g1);
			gabagelist.add(g4);
			CockGeneratePoint gen3 = new CockGeneratePoint(
					this.width * 1 / 3 + 50, height / 8 + 50, new int[] { 0,
							15, 14, 13, 12, 11, 10, 9, 8, 7 }, 6);
			genPtlist.add(gen3);
			STAGE[19] = new Stage(num, period, backgroundId, gabagelist, trashlist, genPtlist);
		}
	}

}
