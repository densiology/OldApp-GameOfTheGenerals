package com.example.gameofthegenerals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;

public class BoardActivity extends Activity
{
	public static byte gameMode;
	public static byte difficulty; // initialized in the entry activity.
	private boolean hasRed;
	private String pendingPiece;
	private View pendingDest;
	private View origView;
	private String overriddenColor;
	private String[] overriddenColors;
	private int[] overriddenIDs;

	private Opponent opponent;
	private boolean isFinished;
	
	private MediaPlayer playerActivityTransition;
	private MediaPlayer playerWhoop;
	private MediaPlayer playerWhoopShort;
	private MediaPlayer playerYeah;
	private MediaPlayer playerAwww;
	private MediaPlayer playerFling;
	private MediaPlayer playerVictory;
	private MediaPlayer playerLoser;
	private MediaPlayer playerHuh;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_board);
		this.overriddenColors = new String[4];
		this.overriddenIDs = new int[4];
		this.opponent = new Opponent();
		gameMode = 1;
	}
	
	@Override
	protected void onStart()
	{
		super.onStart();
		this.playerActivityTransition = MediaPlayer.create(this, R.raw.activity_transition);
		this.playerWhoop = MediaPlayer.create(this, R.raw.whoop);
		this.playerWhoopShort = MediaPlayer.create(this, R.raw.whoop_short);
		this.playerYeah = MediaPlayer.create(this, R.raw.yeah);
		this.playerAwww = MediaPlayer.create(this, R.raw.awww);
		this.playerFling = MediaPlayer.create(this, R.raw.fling);
		this.playerVictory = MediaPlayer.create(this, R.raw.victory);
		this.playerLoser = MediaPlayer.create(this, R.raw.loser);
		this.playerHuh = MediaPlayer.create(this, R.raw.huh);
	}
	
	@Override
	protected void onPause()
	{
		super.onPause();
		overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
	}

	@Override
	protected void onStop()
	{
		super.onStop();
		this.playerActivityTransition.release();
		this.playerWhoop.release();
		this.playerWhoopShort.release();
		this.playerYeah.release();
		this.playerAwww.release();
		this.playerFling.release();
		this.playerVictory.release();
		this.playerLoser.release();
		this.playerHuh.release();
	}
	
	@Override
	public void onBackPressed()
	{
		new AlertDialog.Builder(this)
		.setTitle(R.string.app_name)
		.setMessage(R.string.quit_message)
		.setPositiveButton(R.string.quit_yes, new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.dismiss();
				playActivityTransition();
				BoardActivity.this.finish();
			}
		})
		.setNegativeButton(R.string.quit_no, new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.dismiss();
			}
		})
		.show();
	}

	/******************* UTILITY METHODS ***********************/
	// (Setup - You) Returns Drawable according to the pending piece variable.
	private Drawable extractedDrawable()
	{
		if (this.pendingPiece.equals("xcapt"))
		{ return getResources().getDrawable(R.drawable.capt_img); }
		else if (this.pendingPiece.equals("xcensored"))
		{ return getResources().getDrawable(R.drawable.censored_img); }
		else if (this.pendingPiece.equals("xcol"))
		{ return getResources().getDrawable(R.drawable.col_img); }
		else if (this.pendingPiece.equals("xcollt"))
		{ return getResources().getDrawable(R.drawable.collt_img); }
		else if (this.pendingPiece.equals("xflag"))
		{ return getResources().getDrawable(R.drawable.flag_img); }
		else if (this.pendingPiece.equals("xgen1star"))
		{ return getResources().getDrawable(R.drawable.gen1star_img); }
		else if (this.pendingPiece.equals("xgen2star"))
		{ return getResources().getDrawable(R.drawable.gen2star_img); }
		else if (this.pendingPiece.equals("xgen3star"))
		{ return getResources().getDrawable(R.drawable.gen3star_img); }
		else if (this.pendingPiece.equals("xgen4star"))
		{ return getResources().getDrawable(R.drawable.gen4star_img); }
		else if (this.pendingPiece.equals("xgen5star"))
		{ return getResources().getDrawable(R.drawable.gen5star_img); }
		else if (this.pendingPiece.equals("xlieut1st"))
		{ return getResources().getDrawable(R.drawable.lieut1st_img); }
		else if (this.pendingPiece.equals("xlieut2nd"))
		{ return getResources().getDrawable(R.drawable.lieut2nd_img); }
		else if (this.pendingPiece.equals("xmajor"))
		{ return getResources().getDrawable(R.drawable.major_img); }
		else if (this.pendingPiece.equals("xprivate"))
		{ return getResources().getDrawable(R.drawable.private_img); }
		else if (this.pendingPiece.equals("xsgt"))
		{ return getResources().getDrawable(R.drawable.sgt_img); }
		else if (this.pendingPiece.equals("xspy"))
		{ return getResources().getDrawable(R.drawable.spy_img); }
		else if (this.pendingPiece.startsWith("z"))
		{ return getResources().getDrawable(R.drawable.censored_img); }
		else { return null; }
	}

	// (Setup - You) Returns resource color according to overridden color variable.
	private int extractedColor()
	{
		if (this.overriddenColor.equals("dgray"))
		{ return R.color.dark_gray; }
		else if (this.overriddenColor.equals("lgray"))
		{ return R.color.light_gray; }
		else if (this.overriddenColor.equals("dgreen"))
		{ return R.color.dark_green; }
		else if (this.overriddenColor.equals("lgreen"))
		{ return R.color.light_green; }
		else { return 0; }
	}
	
	// (Setup - You) Returns resource colors according to overridden colors array variable.
	private int[] extractedColors()
	{
		int[] intViewIds = new int[4];
		for (int x = 0; x < 4; x++)
		{
			if (this.overriddenColors[x].equals("dgreen"))
			{
				intViewIds[x] = R.color.dark_green;
			}
			else if (this.overriddenColors[x].equals("lgreen"))
			{
				intViewIds[x] = R.color.light_green;
			}
			else
			{
				intViewIds[x] = 0;
			}
		}
		return intViewIds;
	}
	
	// (Setup - Enemy AI) Put enemy units on board (also the tags).
	protected void putEnemyUnits()
	{
		// clean all units on the enemy base
		ImageView imgView = (ImageView) findViewById(R.id.z1);
		imgView.setImageDrawable(null);
		imgView = (ImageView) findViewById(R.id.z2);
		imgView.setImageDrawable(null);
		imgView = (ImageView) findViewById(R.id.z3);
		imgView.setImageDrawable(null);
		imgView = (ImageView) findViewById(R.id.z4);
		imgView.setImageDrawable(null);
		imgView = (ImageView) findViewById(R.id.z5);
		imgView.setImageDrawable(null);
		imgView = (ImageView) findViewById(R.id.z6);
		imgView.setImageDrawable(null);
		imgView = (ImageView) findViewById(R.id.z7);
		imgView.setImageDrawable(null);
		imgView = (ImageView) findViewById(R.id.z8);
		imgView.setImageDrawable(null);
		imgView = (ImageView) findViewById(R.id.z9);
		imgView.setImageDrawable(null);
		imgView = (ImageView) findViewById(R.id.z10);
		imgView.setImageDrawable(null);
		imgView = (ImageView) findViewById(R.id.z11);
		imgView.setImageDrawable(null);
		imgView = (ImageView) findViewById(R.id.z12);
		imgView.setImageDrawable(null);
		imgView = (ImageView) findViewById(R.id.z13);
		imgView.setImageDrawable(null);
		imgView = (ImageView) findViewById(R.id.z14);
		imgView.setImageDrawable(null);
		imgView = (ImageView) findViewById(R.id.z15);
		imgView.setImageDrawable(null);
		imgView = (ImageView) findViewById(R.id.z16);
		imgView.setImageDrawable(null);
		imgView = (ImageView) findViewById(R.id.z17);
		imgView.setImageDrawable(null);
		imgView = (ImageView) findViewById(R.id.z18);
		imgView.setImageDrawable(null);
		imgView = (ImageView) findViewById(R.id.z19);
		imgView.setImageDrawable(null);
		imgView = (ImageView) findViewById(R.id.z20);
		imgView.setImageDrawable(null);
		imgView = (ImageView) findViewById(R.id.z21);
		imgView.setImageDrawable(null);
		
		String[] arrayTag = opponent.setupTags();
		// put enemy units (image and tag) on board
		imgView = (ImageView) findViewById(R.id.a1);
		imgView.setImageResource(R.drawable.censored_img);
		imgView.setTag(imgView.getTag().toString() + "_z" + arrayTag[0]);
		imgView = (ImageView) findViewById(R.id.a2);
		imgView.setImageResource(R.drawable.censored_img);
		imgView.setTag(imgView.getTag().toString() + "_z" + arrayTag[1]);
		imgView = (ImageView) findViewById(R.id.a3);
		imgView.setImageResource(R.drawable.censored_img);
		imgView.setTag(imgView.getTag().toString() + "_z" + arrayTag[2]);
		imgView = (ImageView) findViewById(R.id.a4);
		imgView.setImageResource(R.drawable.censored_img);
		imgView.setTag(imgView.getTag().toString() + "_z" + arrayTag[3]);
		imgView = (ImageView) findViewById(R.id.a5);
		imgView.setImageResource(R.drawable.censored_img);
		imgView.setTag(imgView.getTag().toString() + "_z" + arrayTag[4]);
		imgView = (ImageView) findViewById(R.id.a6);
		imgView.setImageResource(R.drawable.censored_img);
		imgView.setTag(imgView.getTag().toString() + "_z" + arrayTag[5]);
		imgView = (ImageView) findViewById(R.id.a7);
		imgView.setImageResource(R.drawable.censored_img);
		imgView.setTag(imgView.getTag().toString() + "_z" + arrayTag[6]);
		imgView = (ImageView) findViewById(R.id.a8);
		imgView.setImageResource(R.drawable.censored_img);
		imgView.setTag(imgView.getTag().toString() + "_z" + arrayTag[7]);
		imgView = (ImageView) findViewById(R.id.a9);
		imgView.setImageResource(R.drawable.censored_img);
		imgView.setTag(imgView.getTag().toString() + "_z" + arrayTag[8]);
		imgView = (ImageView) findViewById(R.id.b1);
		imgView.setImageResource(R.drawable.censored_img);
		imgView.setTag(imgView.getTag().toString() + "_z" + arrayTag[9]);
		imgView = (ImageView) findViewById(R.id.b2);
		imgView.setImageResource(R.drawable.censored_img);
		imgView.setTag(imgView.getTag().toString() + "_z" + arrayTag[10]);
		imgView = (ImageView) findViewById(R.id.b3);
		imgView.setImageResource(R.drawable.censored_img);
		imgView.setTag(imgView.getTag().toString() + "_z" + arrayTag[11]);
		imgView = (ImageView) findViewById(R.id.b4);
		imgView.setImageResource(R.drawable.censored_img);
		imgView.setTag(imgView.getTag().toString() + "_z" + arrayTag[12]);
		imgView = (ImageView) findViewById(R.id.b5);
		imgView.setImageResource(R.drawable.censored_img);
		imgView.setTag(imgView.getTag().toString() + "_z" + arrayTag[13]);
		imgView = (ImageView) findViewById(R.id.b6);
		imgView.setImageResource(R.drawable.censored_img);
		imgView.setTag(imgView.getTag().toString() + "_z" + arrayTag[14]);
		imgView = (ImageView) findViewById(R.id.b7);
		imgView.setImageResource(R.drawable.censored_img);
		imgView.setTag(imgView.getTag().toString() + "_z" + arrayTag[15]);
		imgView = (ImageView) findViewById(R.id.b8);
		imgView.setImageResource(R.drawable.censored_img);
		imgView.setTag(imgView.getTag().toString() + "_z" + arrayTag[16]);
		imgView = (ImageView) findViewById(R.id.b9);
		imgView.setImageResource(R.drawable.censored_img);
		imgView.setTag(imgView.getTag().toString() + "_z" + arrayTag[17]);
		imgView = (ImageView) findViewById(R.id.c1);
		imgView.setImageResource(R.drawable.censored_img);
		imgView.setTag(imgView.getTag().toString() + "_z" + arrayTag[18]);
		imgView = (ImageView) findViewById(R.id.c5);
		imgView.setImageResource(R.drawable.censored_img);
		imgView.setTag(imgView.getTag().toString() + "_z" + arrayTag[19]);
		imgView = (ImageView) findViewById(R.id.c9);
		imgView.setImageResource(R.drawable.censored_img);
		imgView.setTag(imgView.getTag().toString() + "_z" + arrayTag[20]);
	}
	
	// (Gameplay - Enemy AI - used only by formAvailableMovesToAI())
	// returns formatted string (unit_1001)
	// the parameter is the enemy unit View
	protected String formatAIMove(View view)
	{
		// using showMoves, store the available views of the enemy unit
		int[] moves = showMoves(view.getId(), "z");
		// (store it in a format (unit_1001))
		String unit = view.getTag().toString().substring(view.getTag().toString().indexOf("_")+2) + "_";
		for (int x = 0; x < 4; x++)
		{
			if (moves[x] != 0)
			{ unit = unit + "1"; }
			else
			{ unit = unit + "0"; }
		}
		return unit;
	}
	
	// (Gameplay - Enemy AI) Send available moves to the AI opponent class.
	protected HashMap<Integer, String> formAvailableMovesToAI()
	{
		int viewId;
		View view;
		HashMap<Integer, String> hashMap = new HashMap<Integer, String>();
		// (explore board Views one by one)
		int[] arrayIds = {R.id.a1, R.id.a2, R.id.a3, R.id.a4, R.id.a5, R.id.a6, R.id.a7, R.id.a8, R.id.a9, 
						  R.id.b1, R.id.b2, R.id.b3, R.id.b4, R.id.b5, R.id.b6, R.id.b7, R.id.b8, R.id.b9, 
						  R.id.c1, R.id.c2, R.id.c3, R.id.c4, R.id.c5, R.id.c6, R.id.c7, R.id.c8, R.id.c9, 
						  R.id.d1, R.id.d2, R.id.d3, R.id.d4, R.id.d5, R.id.d6, R.id.d7, R.id.d8, R.id.d9, 
						  R.id.e1, R.id.e2, R.id.e3, R.id.e4, R.id.e5, R.id.e6, R.id.e7, R.id.e8, R.id.e9, 
						  R.id.f1, R.id.f2, R.id.f3, R.id.f4, R.id.f5, R.id.f6, R.id.f7, R.id.f8, R.id.f9, 
						  R.id.g1, R.id.g2, R.id.g3, R.id.g4, R.id.g5, R.id.g6, R.id.g7, R.id.g8, R.id.g9, 
						  R.id.h1, R.id.h2, R.id.h3, R.id.h4, R.id.h5, R.id.h6, R.id.h7, R.id.h8, R.id.h9};
		for (int x = 0; x < 72; x++)
		{
			viewId = arrayIds[x];
			view = findViewById(viewId);
			String tag = view.getTag().toString();
			if (tag.contains("_z"))
			{
				if (formatAIMove(view).substring(formatAIMove(view).indexOf("_")+1).contains("1"))
				{
					hashMap.put(viewId, formatAIMove(view));
				}
			}
		}
		return hashMap;
	}
	
	// (Gameplay - Enemy AI) Manages all AI sessions.
	protected void manageAI()
	{
		// formAvailableMovesToAI() and pass that to opponent's moveFromOpponent()
		HashMap<Integer, String> move = this.opponent.moveFromOpponent(formAvailableMovesToAI());
		// using data from moveFromOpponent(), write code so that you can pass it to executeMoves()
		List<Integer> keys = new ArrayList<Integer>(move.keySet());
		int key = keys.get(0);
		String value = move.get(key);
		View view1 = findViewById(key);
		View view2 = null;
		int[] availableMoves = showMoves(key, "z");
		if (value.substring(value.indexOf("_")+1).equals("up"))
		{
			view2 = findViewById(availableMoves[0]);
		}
		else if (value.substring(value.indexOf("_")+1).equals("down"))
		{
			view2 = findViewById(availableMoves[1]);
		}
		else if (value.substring(value.indexOf("_")+1).equals("left"))
		{
			view2 = findViewById(availableMoves[2]);
		}
		else if (value.substring(value.indexOf("_")+1).equals("right"))
		{
			view2 = findViewById(availableMoves[3]);
		}
		// executeMoves() of the opponent
		executeMoves(view1, view2);
	}
	
	// (Gameplay - Both Sides) returns view ids in up-down-left-right order (i.e. R.id.c5, 0, R.id.a5, 0)
	// the parameter is the selected (red) view id unit (i.e. R.id.a3) and what side it is from (x or z)
	protected int[] showMoves(int box, String side)
	{
		int[] allMoves = new int[4];
		int[] availableViewIds = new int[4];
		
		if (box == R.id.a1)
		{ allMoves[0] = 0; allMoves[1] = R.id.b1; allMoves[2] = 0; allMoves[3] = R.id.a2; }
		else if (box == R.id.a2)
		{ allMoves[0] = 0; allMoves[1] = R.id.b2; allMoves[2] = R.id.a1; allMoves[3] = R.id.a3; }
		else if (box == R.id.a3)
		{ allMoves[0] = 0; allMoves[1] = R.id.b3; allMoves[2] = R.id.a2; allMoves[3] = R.id.a4; }
		else if (box == R.id.a4)
		{ allMoves[0] = 0; allMoves[1] = R.id.b4; allMoves[2] = R.id.a3; allMoves[3] = R.id.a5; }
		else if (box == R.id.a5)
		{ allMoves[0] = 0; allMoves[1] = R.id.b5; allMoves[2] = R.id.a4; allMoves[3] = R.id.a6; }
		else if (box == R.id.a6)
		{ allMoves[0] = 0; allMoves[1] = R.id.b6; allMoves[2] = R.id.a5; allMoves[3] = R.id.a7; }
		else if (box == R.id.a7)
		{ allMoves[0] = 0; allMoves[1] = R.id.b7; allMoves[2] = R.id.a6; allMoves[3] = R.id.a8; }
		else if (box == R.id.a8)
		{ allMoves[0] = 0; allMoves[1] = R.id.b8; allMoves[2] = R.id.a7; allMoves[3] = R.id.a9; }
		else if (box == R.id.a9)
		{ allMoves[0] = 0; allMoves[1] = R.id.b9; allMoves[2] = R.id.a8; allMoves[3] = 0; }
		
		else if (box == R.id.b1)
		{ allMoves[0] = R.id.a1; allMoves[1] = R.id.c1; allMoves[2] = 0; allMoves[3] = R.id.b2; }
		else if (box == R.id.b2)
		{ allMoves[0] = R.id.a2; allMoves[1] = R.id.c2; allMoves[2] = R.id.b1; allMoves[3] = R.id.b3; }
		else if (box == R.id.b3)
		{ allMoves[0] = R.id.a3; allMoves[1] = R.id.c3; allMoves[2] = R.id.b2; allMoves[3] = R.id.b4; }
		else if (box == R.id.b4)
		{ allMoves[0] = R.id.a4; allMoves[1] = R.id.c4; allMoves[2] = R.id.b3; allMoves[3] = R.id.b5; }
		else if (box == R.id.b5)
		{ allMoves[0] = R.id.a5; allMoves[1] = R.id.c5; allMoves[2] = R.id.b4; allMoves[3] = R.id.b6; }
		else if (box == R.id.b6)
		{ allMoves[0] = R.id.a6; allMoves[1] = R.id.c6; allMoves[2] = R.id.b5; allMoves[3] = R.id.b7; }
		else if (box == R.id.b7)
		{ allMoves[0] = R.id.a7; allMoves[1] = R.id.c7; allMoves[2] = R.id.b6; allMoves[3] = R.id.b8; }
		else if (box == R.id.b8)
		{ allMoves[0] = R.id.a8; allMoves[1] = R.id.c8; allMoves[2] = R.id.b7; allMoves[3] = R.id.b9; }
		else if (box == R.id.b9)
		{ allMoves[0] = R.id.a9; allMoves[1] = R.id.c9; allMoves[2] = R.id.b8; allMoves[3] = 0; }
		
		else if (box == R.id.c1)
		{ allMoves[0] = R.id.b1; allMoves[1] = R.id.d1; allMoves[2] = 0; allMoves[3] = R.id.c2; }
		else if (box == R.id.c2)
		{ allMoves[0] = R.id.b2; allMoves[1] = R.id.d2; allMoves[2] = R.id.c1; allMoves[3] = R.id.c3; }
		else if (box == R.id.c3)
		{ allMoves[0] = R.id.b3; allMoves[1] = R.id.d3; allMoves[2] = R.id.c2; allMoves[3] = R.id.c4; }
		else if (box == R.id.c4)
		{ allMoves[0] = R.id.b4; allMoves[1] = R.id.d4; allMoves[2] = R.id.c3; allMoves[3] = R.id.c5; }
		else if (box == R.id.c5)
		{ allMoves[0] = R.id.b5; allMoves[1] = R.id.d5; allMoves[2] = R.id.c4; allMoves[3] = R.id.c6; }
		else if (box == R.id.c6)
		{ allMoves[0] = R.id.b6; allMoves[1] = R.id.d6; allMoves[2] = R.id.c5; allMoves[3] = R.id.c7; }
		else if (box == R.id.c7)
		{ allMoves[0] = R.id.b7; allMoves[1] = R.id.d7; allMoves[2] = R.id.c6; allMoves[3] = R.id.c8; }
		else if (box == R.id.c8)
		{ allMoves[0] = R.id.b8; allMoves[1] = R.id.d8; allMoves[2] = R.id.c7; allMoves[3] = R.id.c9; }
		else if (box == R.id.c9)
		{ allMoves[0] = R.id.b9; allMoves[1] = R.id.d9; allMoves[2] = R.id.c8; allMoves[3] = 0; }
		
		else if (box == R.id.d1)
		{ allMoves[0] = R.id.c1; allMoves[1] = R.id.e1; allMoves[2] = 0; allMoves[3] = R.id.d2; }
		else if (box == R.id.d2)
		{ allMoves[0] = R.id.c2; allMoves[1] = R.id.e2; allMoves[2] = R.id.d1; allMoves[3] = R.id.d3; }
		else if (box == R.id.d3)
		{ allMoves[0] = R.id.c3; allMoves[1] = R.id.e3; allMoves[2] = R.id.d2; allMoves[3] = R.id.d4; }
		else if (box == R.id.d4)
		{ allMoves[0] = R.id.c4; allMoves[1] = R.id.e4; allMoves[2] = R.id.d3; allMoves[3] = R.id.d5; }
		else if (box == R.id.d5)
		{ allMoves[0] = R.id.c5; allMoves[1] = R.id.e5; allMoves[2] = R.id.d4; allMoves[3] = R.id.d6; }
		else if (box == R.id.d6)
		{ allMoves[0] = R.id.c6; allMoves[1] = R.id.e6; allMoves[2] = R.id.d5; allMoves[3] = R.id.d7; }
		else if (box == R.id.d7)
		{ allMoves[0] = R.id.c7; allMoves[1] = R.id.e7; allMoves[2] = R.id.d6; allMoves[3] = R.id.d8; }
		else if (box == R.id.d8)
		{ allMoves[0] = R.id.c8; allMoves[1] = R.id.e8; allMoves[2] = R.id.d7; allMoves[3] = R.id.d9; }
		else if (box == R.id.d9)
		{ allMoves[0] = R.id.c9; allMoves[1] = R.id.e9; allMoves[2] = R.id.d8; allMoves[3] = 0; }
		
		else if (box == R.id.e1)
		{ allMoves[0] = R.id.d1; allMoves[1] = R.id.f1; allMoves[2] = 0; allMoves[3] = R.id.e2; }
		else if (box == R.id.e2)
		{ allMoves[0] = R.id.d2; allMoves[1] = R.id.f2; allMoves[2] = R.id.e1; allMoves[3] = R.id.e3; }
		else if (box == R.id.e3)
		{ allMoves[0] = R.id.d3; allMoves[1] = R.id.f3; allMoves[2] = R.id.e2; allMoves[3] = R.id.e4; }
		else if (box == R.id.e4)
		{ allMoves[0] = R.id.d4; allMoves[1] = R.id.f4; allMoves[2] = R.id.e3; allMoves[3] = R.id.e5; }
		else if (box == R.id.e5)
		{ allMoves[0] = R.id.d5; allMoves[1] = R.id.f5; allMoves[2] = R.id.e4; allMoves[3] = R.id.e6; }
		else if (box == R.id.e6)
		{ allMoves[0] = R.id.d6; allMoves[1] = R.id.f6; allMoves[2] = R.id.e5; allMoves[3] = R.id.e7; }
		else if (box == R.id.e7)
		{ allMoves[0] = R.id.d7; allMoves[1] = R.id.f7; allMoves[2] = R.id.e6; allMoves[3] = R.id.e8; }
		else if (box == R.id.e8)
		{ allMoves[0] = R.id.d8; allMoves[1] = R.id.f8; allMoves[2] = R.id.e7; allMoves[3] = R.id.e9; }
		else if (box == R.id.e9)
		{ allMoves[0] = R.id.d9; allMoves[1] = R.id.f9; allMoves[2] = R.id.e8; allMoves[3] = 0; }
		
		else if (box == R.id.f1)
		{ allMoves[0] = R.id.e1; allMoves[1] = R.id.g1; allMoves[2] = 0; allMoves[3] = R.id.f2; }
		else if (box == R.id.f2)
		{ allMoves[0] = R.id.e2; allMoves[1] = R.id.g2; allMoves[2] = R.id.f1; allMoves[3] = R.id.f3; }
		else if (box == R.id.f3)
		{ allMoves[0] = R.id.e3; allMoves[1] = R.id.g3; allMoves[2] = R.id.f2; allMoves[3] = R.id.f4; }
		else if (box == R.id.f4)
		{ allMoves[0] = R.id.e4; allMoves[1] = R.id.g4; allMoves[2] = R.id.f3; allMoves[3] = R.id.f5; }
		else if (box == R.id.f5)
		{ allMoves[0] = R.id.e5; allMoves[1] = R.id.g5; allMoves[2] = R.id.f4; allMoves[3] = R.id.f6; }
		else if (box == R.id.f6)
		{ allMoves[0] = R.id.e6; allMoves[1] = R.id.g6; allMoves[2] = R.id.f5; allMoves[3] = R.id.f7; }
		else if (box == R.id.f7)
		{ allMoves[0] = R.id.e7; allMoves[1] = R.id.g7; allMoves[2] = R.id.f6; allMoves[3] = R.id.f8; }
		else if (box == R.id.f8)
		{ allMoves[0] = R.id.e8; allMoves[1] = R.id.g8; allMoves[2] = R.id.f7; allMoves[3] = R.id.f9; }
		else if (box == R.id.f9)
		{ allMoves[0] = R.id.e9; allMoves[1] = R.id.g9; allMoves[2] = R.id.f8; allMoves[3] = 0; }
		
		else if (box == R.id.g1)
		{ allMoves[0] = R.id.f1; allMoves[1] = R.id.h1; allMoves[2] = 0; allMoves[3] = R.id.g2; }
		else if (box == R.id.g2)
		{ allMoves[0] = R.id.f2; allMoves[1] = R.id.h2; allMoves[2] = R.id.g1; allMoves[3] = R.id.g3; }
		else if (box == R.id.g3)
		{ allMoves[0] = R.id.f3; allMoves[1] = R.id.h3; allMoves[2] = R.id.g2; allMoves[3] = R.id.g4; }
		else if (box == R.id.g4)
		{ allMoves[0] = R.id.f4; allMoves[1] = R.id.h4; allMoves[2] = R.id.g3; allMoves[3] = R.id.g5; }
		else if (box == R.id.g5)
		{ allMoves[0] = R.id.f5; allMoves[1] = R.id.h5; allMoves[2] = R.id.g4; allMoves[3] = R.id.g6; }
		else if (box == R.id.g6)
		{ allMoves[0] = R.id.f6; allMoves[1] = R.id.h6; allMoves[2] = R.id.g5; allMoves[3] = R.id.g7; }
		else if (box == R.id.g7)
		{ allMoves[0] = R.id.f7; allMoves[1] = R.id.h7; allMoves[2] = R.id.g6; allMoves[3] = R.id.g8; }
		else if (box == R.id.g8)
		{ allMoves[0] = R.id.f8; allMoves[1] = R.id.h8; allMoves[2] = R.id.g7; allMoves[3] = R.id.g9; }
		else if (box == R.id.g9)
		{ allMoves[0] = R.id.f9; allMoves[1] = R.id.h9; allMoves[2] = R.id.g8; allMoves[3] = 0; }
		
		else if (box == R.id.h1)
		{ allMoves[0] = R.id.g1; allMoves[1] = 0; allMoves[2] = 0; allMoves[3] = R.id.h2; }
		else if (box == R.id.h2)
		{ allMoves[0] = R.id.g2; allMoves[1] = 0; allMoves[2] = R.id.h1; allMoves[3] = R.id.h3; }
		else if (box == R.id.h3)
		{ allMoves[0] = R.id.g3; allMoves[1] = 0; allMoves[2] = R.id.h2; allMoves[3] = R.id.h4; }
		else if (box == R.id.h4)
		{ allMoves[0] = R.id.g4; allMoves[1] = 0; allMoves[2] = R.id.h3; allMoves[3] = R.id.h5; }
		else if (box == R.id.h5)
		{ allMoves[0] = R.id.g5; allMoves[1] = 0; allMoves[2] = R.id.h4; allMoves[3] = R.id.h6; }
		else if (box == R.id.h6)
		{ allMoves[0] = R.id.g6; allMoves[1] = 0; allMoves[2] = R.id.h5; allMoves[3] = R.id.h7; }
		else if (box == R.id.h7)
		{ allMoves[0] = R.id.g7; allMoves[1] = 0; allMoves[2] = R.id.h6; allMoves[3] = R.id.h8; }
		else if (box == R.id.h8)
		{ allMoves[0] = R.id.g8; allMoves[1] = 0; allMoves[2] = R.id.h7; allMoves[3] = R.id.h9; }
		else if (box == R.id.h9)
		{ allMoves[0] = R.id.g9; allMoves[1] = 0; allMoves[2] = R.id.h8; allMoves[3] = 0; }
		
		for (int x = 0; x < 4; x++)
		{
			// this works on both sides (you or opponent)
			if ((allMoves[x] == 0) || findViewById(allMoves[x]).getTag().toString().contains(side))
			{ availableViewIds[x] = 0; }
			else
			{ availableViewIds[x] = allMoves[x]; }
		}
		return availableViewIds;
	}
	
	// (Gameplay - Both Sides) This method actually executes the moves (either you or the enemy's moves)
	// returns true if the game is over; otherwise false
	// the parameters are the views from and to (i.e. View c3, View d3)
	protected boolean executeMoves(View unit1, View unit2)
	{
		ImageView imgViewUnit1 = (ImageView) unit1;
		ImageView imgViewUnit2 = (ImageView) unit2;
		String tagUnit1 = (String) imgViewUnit1.getTag();
		String tagUnit2 = (String) imgViewUnit2.getTag();
		int unit2Id = unit2.getId();
		// IF the unit simply attempts to move to a blank box
		if (!unit2.getTag().toString().contains("_"))
		{
			// if it is your flag that reached the end
			if (tagUnit1.contains("xflag") && 
				((unit2Id == R.id.a1) || (unit2Id == R.id.a2) || (unit2Id == R.id.a3) || (unit2Id == R.id.a4) || (unit2Id == R.id.a5) || 
				 (unit2Id == R.id.a6) || (unit2Id == R.id.a7) || (unit2Id == R.id.a8) || (unit2Id == R.id.a9)) &&
				 (checkBeside(unit2, "x")))
			{
				playVictory();
				imgViewUnit2.setImageDrawable(imgViewUnit1.getDrawable());
				imgViewUnit1.setImageDrawable(null);
				new AlertDialog.Builder(this)
				.setMessage(R.string.you_flag_message)
				.setPositiveButton(R.string.end_game_yes, new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int which)
					{
						dialog.dismiss();
						playActivityTransition();
						BoardActivity.this.finish();
					}
				})
				.setCancelable(false)
				.show();
				return true;
			}
			// if it is the enemy's flag that reached your end
			else if (tagUnit1.contains("zflag") && 
					 ((unit2Id == R.id.h1) || (unit2Id == R.id.h2) || (unit2Id == R.id.h3) || (unit2Id == R.id.h4) || (unit2Id == R.id.h5) || 
					  (unit2Id == R.id.h6) || (unit2Id == R.id.h7) || (unit2Id == R.id.h8) || (unit2Id == R.id.h9)) &&
					  (checkBeside(unit2, "z")))
			{
				playLoser();
				imgViewUnit2.setImageDrawable(imgViewUnit1.getDrawable());
				imgViewUnit1.setImageDrawable(null);
				new AlertDialog.Builder(this)
				.setMessage(R.string.enemy_flag_message)
				.setPositiveButton(R.string.end_game_yes, new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int which)
					{
						dialog.dismiss();
						playActivityTransition();
						BoardActivity.this.finish();
					}
				})
				.setCancelable(false)
				.show();
				return true;
			}
			// as normal, if it moves to a blank box
			else
			{
				playWhoopShort();
				// change the physical units
				imgViewUnit2.setImageDrawable(imgViewUnit1.getDrawable());
				imgViewUnit1.setImageDrawable(null);
				// change the unit tags (i.e. xgen5star only; not colors; can be applied alone to AI but not on you)
				// define further code for YOU in button pressed
				imgViewUnit1.setTag(tagUnit1.substring(0, tagUnit1.indexOf("_")));
				imgViewUnit2.setTag(tagUnit2 + "_" + tagUnit1.substring(tagUnit1.indexOf("_")+1));
			}
		}
		// IF the unit attempts to move to a box with an opposing unit
		else
		{
			Drawable unit1Drawable = imgViewUnit1.getDrawable();
			// +2 to skip the x or z char
			String unit1st = tagUnit1.substring(tagUnit1.indexOf("_")+2);
			String unit2nd = tagUnit2.substring(tagUnit2.indexOf("_")+2);
			String battleResult = decideBattle(unit1st, unit2nd);
			// if decideBattle returns 0 (both are same)
			if (battleResult.equals("0"))
			{
				playFling();
				// (get rid of both units)
				// change the physical units and tags
				if (tagUnit1.contains("x"))
				{
					putUnitOnBase(imgViewUnit1, "x");
					putUnitOnBase(imgViewUnit2, "z");
					unit1.setTag(tagUnit1.substring(0, tagUnit1.indexOf("_")));
					unit2.setTag(tagUnit2.substring(0, tagUnit2.indexOf("_")));
				}
				else if (tagUnit1.contains("z"))
				{
					putUnitOnBase(imgViewUnit1, "z");
					putUnitOnBase(imgViewUnit2, "x");
					unit1.setTag(tagUnit1.substring(0, tagUnit1.indexOf("_")));
					unit2.setTag(tagUnit2.substring(0, tagUnit2.indexOf("_")));
				}
			}
			// else if decideBattle returns 1 (one of them is a flag)
			else if (battleResult.equals("1"))
			{
				// you ate the enemy's flag
				if (tagUnit1.contains("_x") && !unit1st.equals("flag"))
				{
					playVictory();
					putUnitOnBase(imgViewUnit2, "z");
					imgViewUnit2.setImageDrawable(unit1Drawable);
					imgViewUnit1.setImageDrawable(null);
					new AlertDialog.Builder(this)
					.setMessage(R.string.capture_flag_message)
					.setPositiveButton(R.string.end_game_yes, new DialogInterface.OnClickListener()
					{
						public void onClick(DialogInterface dialog, int which)
						{
							dialog.dismiss();
							playActivityTransition();
							BoardActivity.this.finish();
						}
					})
					.setCancelable(false)
					.show();
					return true;
				}
				// the enemy ate your flag
				else if (tagUnit1.contains("_z") && !unit1st.equals("flag"))
				{
					playLoser();
					putUnitOnBase(imgViewUnit2, "x");
					imgViewUnit2.setImageDrawable(unit1Drawable);
					imgViewUnit1.setImageDrawable(null);
					new AlertDialog.Builder(this)
					.setMessage(R.string.captured_flag_message)
					.setPositiveButton(R.string.end_game_yes, new DialogInterface.OnClickListener()
					{
						public void onClick(DialogInterface dialog, int which)
						{
							dialog.dismiss();
							playActivityTransition();
							BoardActivity.this.finish();
						}
					})
					.setCancelable(false)
					.show();
					return true;
				}
				// you moved your flag to a normal enemy unit (you lose)
				else if (tagUnit1.contains("_x") && unit1st.equals("flag"))
				{
					playLoser();
					putUnitOnBase(imgViewUnit1, "x");
					new AlertDialog.Builder(this)
					.setMessage(R.string.flag_charge_message)
					.setPositiveButton(R.string.end_game_yes, new DialogInterface.OnClickListener()
					{
						public void onClick(DialogInterface dialog, int which)
						{
							dialog.dismiss();
							playActivityTransition();
							BoardActivity.this.finish();
						}
					})
					.setCancelable(false)
					.show();
					return true;
				}
				// the enemy moved its flag to one of your normal units (you win)
				else if (tagUnit1.contains("_z") && unit1st.equals("flag"))
				{
					playVictory();
					putUnitOnBase(imgViewUnit1, "z");
					new AlertDialog.Builder(this)
					.setMessage(R.string.flag_charged_message)
					.setPositiveButton(R.string.end_game_yes, new DialogInterface.OnClickListener()
					{
						public void onClick(DialogInterface dialog, int which)
						{
							dialog.dismiss();
							playActivityTransition();
							BoardActivity.this.finish();
						}
					})
					.setCancelable(false)
					.show();
					return true;
				}
			}
			// else if decideBattle returns 2 (both flags)
			else if (battleResult.equals("2"))
			{
				// if your flag eats the enemy's flag
				if (tagUnit1.contains("_x"))
				{
					playVictory();
					putUnitOnBase(imgViewUnit2, "z");
					imgViewUnit2.setImageDrawable(unit1Drawable);
					imgViewUnit1.setImageDrawable(null);
					new AlertDialog.Builder(this)
					.setMessage(R.string.capture_flag_message)
					.setPositiveButton(R.string.end_game_yes, new DialogInterface.OnClickListener()
					{
						public void onClick(DialogInterface dialog, int which)
						{
							dialog.dismiss();
							playActivityTransition();
							BoardActivity.this.finish();
						}
					})
					.setCancelable(false)
					.show();
					return true;
				}
				// if the enemy's flag eats your flag
				else if (tagUnit1.contains("_z"))
				{
					playLoser();
					putUnitOnBase(imgViewUnit2, "x");
					imgViewUnit2.setImageDrawable(unit1Drawable);
					imgViewUnit1.setImageDrawable(null);
					new AlertDialog.Builder(this)
					.setMessage(R.string.captured_flag_message)
					.setPositiveButton(R.string.end_game_yes, new DialogInterface.OnClickListener()
					{
						public void onClick(DialogInterface dialog, int which)
						{
							dialog.dismiss();
							playActivityTransition();
							BoardActivity.this.finish();
						}
					})
					.setCancelable(false)
					.show();
					return true;
				}
			}
			// else (normal clash)
			else
			{
				String unit1withSide = tagUnit1.substring(tagUnit1.indexOf("_")+1);
				String unit2withSide = tagUnit2.substring(tagUnit2.indexOf("_")+1);
				//(move according to who is the winner)
				// if the winning piece is from YOU (you attacked the enemy)
				if (unit1withSide.startsWith("x") && unit1st.equals(battleResult))
				{
					playYeah();
					putUnitOnBase(imgViewUnit2, "z");
					imgViewUnit2.setImageDrawable(unit1Drawable);
					imgViewUnit1.setImageDrawable(null);
					unit1.setTag(tagUnit1.substring(0, tagUnit1.indexOf("_")));
					unit2.setTag(tagUnit2.substring(0, tagUnit2.indexOf("_")) + "_" + unit1withSide);
				}
				// if the winning piece is from YOU (the enemy attacked you)
				else if (unit2withSide.startsWith("x") && unit2nd.equals(battleResult))
				{
					playYeah();
					putUnitOnBase(imgViewUnit1, "z");
					imgViewUnit1.setImageDrawable(null);
					unit1.setTag(tagUnit1.substring(0, tagUnit1.indexOf("_")));
				}
				// if the winning piece is from the enemy (the enemy attacked you)
				else if (unit1withSide.startsWith("z") && unit1st.equals(battleResult))
				{
					playAwww();
					putUnitOnBase(imgViewUnit2, "x");
					imgViewUnit2.setImageDrawable(unit1Drawable);
					imgViewUnit1.setImageDrawable(null);
					unit1.setTag(tagUnit1.substring(0, tagUnit1.indexOf("_")));
					unit2.setTag(tagUnit2.substring(0, tagUnit2.indexOf("_")) + "_" + unit1withSide);
				}
				// if the winning piece is from the enemy (you attacked the enemy)
				else if (unit2withSide.startsWith("z") && unit2nd.equals(battleResult))
				{
					playAwww();
					putUnitOnBase(imgViewUnit1, "x");
					imgViewUnit1.setImageDrawable(null);
					unit1.setTag(tagUnit1.substring(0, tagUnit1.indexOf("_")));
				}
			}
		}
		return false;
	}
	
	// (Gameplay - Both Sides - used only by executeMoves()). Puts the physical units to the bases
	// returns boolean (if successful or not)
	// the parameters are: the image view to which the unit is located and which side is it (x or z)
	private boolean putUnitOnBase(ImageView box, String side)
	{
		Drawable unit = box.getDrawable();
		box.setImageDrawable(null);
		if (side.equals("x"))
		{
			ImageView baseView = (ImageView) findViewById(R.id.y1);
			if (baseView.getDrawable() == null)
			{ baseView.setImageDrawable(unit); return true; }
			baseView = (ImageView) findViewById(R.id.y2);
			if (baseView.getDrawable() == null)
			{ baseView.setImageDrawable(unit); return true; }
			baseView = (ImageView) findViewById(R.id.y3);
			if (baseView.getDrawable() == null)
			{ baseView.setImageDrawable(unit); return true; }
			baseView = (ImageView) findViewById(R.id.y4);
			if (baseView.getDrawable() == null)
			{ baseView.setImageDrawable(unit); return true; }
			baseView = (ImageView) findViewById(R.id.y5);
			if (baseView.getDrawable() == null)
			{ baseView.setImageDrawable(unit); return true; }
			baseView = (ImageView) findViewById(R.id.y6);
			if (baseView.getDrawable() == null)
			{ baseView.setImageDrawable(unit); return true; }
			baseView = (ImageView) findViewById(R.id.y7);
			if (baseView.getDrawable() == null)
			{ baseView.setImageDrawable(unit); return true; }
			baseView = (ImageView) findViewById(R.id.y8);
			if (baseView.getDrawable() == null)
			{ baseView.setImageDrawable(unit); return true; }
			baseView = (ImageView) findViewById(R.id.y9);
			if (baseView.getDrawable() == null)
			{ baseView.setImageDrawable(unit); return true; }
			baseView = (ImageView) findViewById(R.id.y10);
			if (baseView.getDrawable() == null)
			{ baseView.setImageDrawable(unit); return true; }
			baseView = (ImageView) findViewById(R.id.y11);
			if (baseView.getDrawable() == null)
			{ baseView.setImageDrawable(unit); return true; }
			baseView = (ImageView) findViewById(R.id.y12);
			if (baseView.getDrawable() == null)
			{ baseView.setImageDrawable(unit); return true; }
			baseView = (ImageView) findViewById(R.id.y13);
			if (baseView.getDrawable() == null)
			{ baseView.setImageDrawable(unit); return true; }
			baseView = (ImageView) findViewById(R.id.y14);
			if (baseView.getDrawable() == null)
			{ baseView.setImageDrawable(unit); return true; }
			baseView = (ImageView) findViewById(R.id.y15);
			if (baseView.getDrawable() == null)
			{ baseView.setImageDrawable(unit); return true; }
			baseView = (ImageView) findViewById(R.id.y16);
			if (baseView.getDrawable() == null)
			{ baseView.setImageDrawable(unit); return true; }
			baseView = (ImageView) findViewById(R.id.y17);
			if (baseView.getDrawable() == null)
			{ baseView.setImageDrawable(unit); return true; }
			baseView = (ImageView) findViewById(R.id.y18);
			if (baseView.getDrawable() == null)
			{ baseView.setImageDrawable(unit); return true; }
			baseView = (ImageView) findViewById(R.id.y19);
			if (baseView.getDrawable() == null)
			{ baseView.setImageDrawable(unit); return true; }
			baseView = (ImageView) findViewById(R.id.y20);
			if (baseView.getDrawable() == null)
			{ baseView.setImageDrawable(unit); return true; }
			baseView = (ImageView) findViewById(R.id.y21);
			if (baseView.getDrawable() == null)
			{ baseView.setImageDrawable(unit); return true; }
		}
		else if (side.equals("z"))
		{
			ImageView baseView = (ImageView) findViewById(R.id.z1);
			if (baseView.getDrawable() == null)
			{ baseView.setImageDrawable(unit); return true; }
			baseView = (ImageView) findViewById(R.id.z2);
			if (baseView.getDrawable() == null)
			{ baseView.setImageDrawable(unit); return true; }
			baseView = (ImageView) findViewById(R.id.z3);
			if (baseView.getDrawable() == null)
			{ baseView.setImageDrawable(unit); return true; }
			baseView = (ImageView) findViewById(R.id.z4);
			if (baseView.getDrawable() == null)
			{ baseView.setImageDrawable(unit); return true; }
			baseView = (ImageView) findViewById(R.id.z5);
			if (baseView.getDrawable() == null)
			{ baseView.setImageDrawable(unit); return true; }
			baseView = (ImageView) findViewById(R.id.z6);
			if (baseView.getDrawable() == null)
			{ baseView.setImageDrawable(unit); return true; }
			baseView = (ImageView) findViewById(R.id.z7);
			if (baseView.getDrawable() == null)
			{ baseView.setImageDrawable(unit); return true; }
			baseView = (ImageView) findViewById(R.id.z8);
			if (baseView.getDrawable() == null)
			{ baseView.setImageDrawable(unit); return true; }
			baseView = (ImageView) findViewById(R.id.z9);
			if (baseView.getDrawable() == null)
			{ baseView.setImageDrawable(unit); return true; }
			baseView = (ImageView) findViewById(R.id.z10);
			if (baseView.getDrawable() == null)
			{ baseView.setImageDrawable(unit); return true; }
			baseView = (ImageView) findViewById(R.id.z11);
			if (baseView.getDrawable() == null)
			{ baseView.setImageDrawable(unit); return true; }
			baseView = (ImageView) findViewById(R.id.z12);
			if (baseView.getDrawable() == null)
			{ baseView.setImageDrawable(unit); return true; }
			baseView = (ImageView) findViewById(R.id.z13);
			if (baseView.getDrawable() == null)
			{ baseView.setImageDrawable(unit); return true; }
			baseView = (ImageView) findViewById(R.id.z14);
			if (baseView.getDrawable() == null)
			{ baseView.setImageDrawable(unit); return true; }
			baseView = (ImageView) findViewById(R.id.z15);
			if (baseView.getDrawable() == null)
			{ baseView.setImageDrawable(unit); return true; }
			baseView = (ImageView) findViewById(R.id.z16);
			if (baseView.getDrawable() == null)
			{ baseView.setImageDrawable(unit); return true; }
			baseView = (ImageView) findViewById(R.id.z17);
			if (baseView.getDrawable() == null)
			{ baseView.setImageDrawable(unit); return true; }
			baseView = (ImageView) findViewById(R.id.z18);
			if (baseView.getDrawable() == null)
			{ baseView.setImageDrawable(unit); return true; }
			baseView = (ImageView) findViewById(R.id.z19);
			if (baseView.getDrawable() == null)
			{ baseView.setImageDrawable(unit); return true; }
			baseView = (ImageView) findViewById(R.id.z20);
			if (baseView.getDrawable() == null)
			{ baseView.setImageDrawable(unit); return true; }
			baseView = (ImageView) findViewById(R.id.z21);
			if (baseView.getDrawable() == null)
			{ baseView.setImageDrawable(unit); return true; }
		}
		return false;
	}
	
	// (Gameplay - Both Sides - used only by executeMoves()). Check if there are units beside a flag view.
	// returns true if the view has no enemy units beside it; otherwise false.
	// the parameter is the target view
	private boolean checkBeside(View view, String side)
	{
		int viewId = view.getId();
		if (side == "x")
		{
			if (viewId == R.id.a1)
			{
				if (findViewById(R.id.a2).getTag().toString().contains("z"))
				{ return false; }
			}
			else if (viewId == R.id.a2)
			{
				if (findViewById(R.id.a1).getTag().toString().contains("z") || findViewById(R.id.a3).getTag().toString().contains("z"))
				{ return false; }
			}
			else if (viewId == R.id.a3)
			{
				if (findViewById(R.id.a2).getTag().toString().contains("z") || findViewById(R.id.a4).getTag().toString().contains("z"))
				{ return false; }
			}
			else if (viewId == R.id.a4)
			{
				if (findViewById(R.id.a3).getTag().toString().contains("z") || findViewById(R.id.a5).getTag().toString().contains("z"))
				{ return false; }
			}
			else if (viewId == R.id.a5)
			{
				if (findViewById(R.id.a4).getTag().toString().contains("z") || findViewById(R.id.a6).getTag().toString().contains("z"))
				{ return false; }
			}
			else if (viewId == R.id.a6)
			{
				if (findViewById(R.id.a5).getTag().toString().contains("z") || findViewById(R.id.a7).getTag().toString().contains("z"))
				{ return false; }
			}
			else if (viewId == R.id.a7)
			{
				if (findViewById(R.id.a6).getTag().toString().contains("z") || findViewById(R.id.a8).getTag().toString().contains("z"))
				{ return false; }
			}
			else if (viewId == R.id.a8)
			{
				if (findViewById(R.id.a7).getTag().toString().contains("z") || findViewById(R.id.a9).getTag().toString().contains("z"))
				{ return false; }
			}
			else if (viewId == R.id.a9)
			{
				if (findViewById(R.id.a8).getTag().toString().contains("z"))
				{ return false; }
			}
		}
		else if (side == "z")
		{
			if (viewId == R.id.h1)
			{
				if (findViewById(R.id.h2).getTag().toString().contains("x"))
				{ return false; }
			}
			else if (viewId == R.id.h2)
			{
				if (findViewById(R.id.h1).getTag().toString().contains("x") || findViewById(R.id.h3).getTag().toString().contains("x"))
				{ return false; }
			}
			else if (viewId == R.id.h3)
			{
				if (findViewById(R.id.h2).getTag().toString().contains("x") || findViewById(R.id.h4).getTag().toString().contains("x"))
				{ return false; }
			}
			else if (viewId == R.id.h4)
			{
				if (findViewById(R.id.h3).getTag().toString().contains("x") || findViewById(R.id.h5).getTag().toString().contains("x"))
				{ return false; }
			}
			else if (viewId == R.id.h5)
			{
				if (findViewById(R.id.h4).getTag().toString().contains("x") || findViewById(R.id.h6).getTag().toString().contains("x"))
				{ return false; }
			}
			else if (viewId == R.id.h6)
			{
				if (findViewById(R.id.h5).getTag().toString().contains("x") || findViewById(R.id.h7).getTag().toString().contains("x"))
				{ return false; }
			}
			else if (viewId == R.id.h7)
			{
				if (findViewById(R.id.h6).getTag().toString().contains("x") || findViewById(R.id.h8).getTag().toString().contains("x"))
				{ return false; }
			}
			else if (viewId == R.id.h8)
			{
				if (findViewById(R.id.h7).getTag().toString().contains("x") || findViewById(R.id.h9).getTag().toString().contains("x"))
				{ return false; }
			}
			else if (viewId == R.id.h9)
			{
				if (findViewById(R.id.h8).getTag().toString().contains("x"))
				{ return false; }
			}
		}
		return true;
	}
	
	// (Gameplay - Both Sides) Returns the winning piece (i.e. gen3star)
	// Returns 0 if both piece are the same, returns 1 if one of them is a flag or
	// returns 2 if both are flags
	// the parameters are the two clashed units (i.e. col, gen3star)
	protected String decideBattle(String unit1, String unit2)
	{
		ArrayList<String> mUnits = new ArrayList<String>();
		mUnits.add(0, "sgt");
		mUnits.add(1, "lieut2nd");
		mUnits.add(2, "lieut1st");
		mUnits.add(3, "capt");
		mUnits.add(4, "major");
		mUnits.add(5, "collt");
		mUnits.add(6, "col");
		mUnits.add(7, "gen1star");
		mUnits.add(8, "gen2star");
		mUnits.add(9, "gen3star");
		mUnits.add(10, "gen4star");
		mUnits.add(11, "gen5star");
		
		for (String unit : mUnits)
		{
			if ((unit1.equals("private") || unit2.equals("private")) &&
				(unit1.equals(unit) || unit2.equals(unit)))
			{ return unit; }
			else if ((unit1.equals("spy") || unit2.equals("spy")) &&
					(unit1.equals(unit) || unit2.equals(unit)))
			{ return "spy"; }
		}
		
		if (unit1.equals(unit2))
		{
			if (unit1.equals("flag"))
			{ return "2"; }
			else
			{ return "0"; }
		}
		else if ((unit1.equals("private") || unit2.equals("private")) &&
				 (unit1.equals("spy") || unit2.equals("spy")))
		{ return "private"; }
		// condition for if both are flags is already checked, so this is safe.
		else if (unit1.equals("flag") || unit2.equals("flag"))
		{ return "1"; }
		else if (mUnits.indexOf(unit1) > mUnits.indexOf(unit2))
		{ return unit1; }
		else if (mUnits.indexOf(unit2) > mUnits.indexOf(unit1))
		{ return unit2; }
		else
		{ return "0"; }
	}
	

	/******************* CLICK METHODS ***********************/
	
	// (Setup - You) Clicked a box during setup.
	private void clickedDuringSetup(View view)
	{
		if (gameMode == 1)
		{
			String tag = (String) view.getTag(); // gets the tag of the clicked view
			ImageView imageView = (ImageView) view;
			Drawable actualImage = imageView.getDrawable();
			if (actualImage == null)
			{
				// if a box w/ no image is clicked and there is a red box somewhere
				if (this.hasRed == true)
				{
					playWhoop();
					// set the image of the box
					imageView.setImageDrawable(extractedDrawable());
					// set the tag of the box
					view.setTag(tag + "_" + this.pendingPiece);
					// erase the image from the original box
					ImageView imageView2 = (ImageView) this.origView;
					imageView2.setImageDrawable(null);
					// change the color of the original box from red to its former
					this.origView.setBackgroundResource(extractedColor());
					// change the tag of the original box
					this.origView.setTag(this.overriddenColor);
					// then nullify hasRed, pendingPiece, fromView and overriddenColor
					this.hasRed = false;
					this.pendingPiece = null;
					this.origView = null;
					this.overriddenColor = null;
				}
			}
			else
			{
				// if the same red box is clicked, return that box to original form
				if (tag.substring(0, tag.indexOf("_")).equals("red"))
				{
					// change the color of the box from red to its former
					view.setBackgroundResource(extractedColor());
					// change the tag
					view.setTag(this.overriddenColor + "_" + this.pendingPiece);
					// then nullify hasRed, pendingPiece, overriddenColor, origView
					this.hasRed = false;
					this.pendingPiece = null;
					this.origView = null;
					this.overriddenColor = null;
				}
				// if a box w/ an image is clicked and there is a red box somewhere
				else if (this.hasRed == true)
				{
					// change the color of the original box from red to its former
					this.origView.setBackgroundResource(extractedColor());
					// change the tag of the original box
					this.origView.setTag(this.overriddenColor + "_" + this.pendingPiece);
					// set the box red
					view.setBackgroundResource(R.color.red);
					// set the tag of the box (before that, preserve its color)
					this.overriddenColor = tag.substring(0, tag.indexOf("_"));
					view.setTag("red_" + tag.substring(tag.indexOf("_")+1));
					// set variables hasRed, pendingPiece and fromView (see above for overriddenColor)
					this.hasRed = true;
					this.pendingPiece = tag.substring(tag.indexOf("_")+1);
					this.origView = view;
				}
				// if a box w/ an image is clicked but there is no red box somewhere
				else
				{
					// set variables hasRed, pendingPiece, fromView and overriddenColor
					this.hasRed = true;
					this.pendingPiece = tag.substring(tag.indexOf("_")+1);
					this.origView = view;
					this.overriddenColor = tag.substring(0, tag.indexOf("_"));
					// set the box red
					view.setBackgroundResource(R.color.red);
					// set the tag of the box
					view.setTag("red_" + this.pendingPiece);
				}
			}
		}
	}
	
	// (Setup - You) Clicked the button during setup.
	private void clickedBtnDuringSetup(View view)
	{
		String collected = "";
		View eachView = findViewById(R.id.y1);
		collected = (String) eachView.getTag();
		eachView = findViewById(R.id.y2);
		collected = collected + (String) eachView.getTag();
		eachView = findViewById(R.id.y3);
		collected = collected + (String) eachView.getTag();
		eachView = findViewById(R.id.y4);
		collected = collected + (String) eachView.getTag();
		eachView = findViewById(R.id.y5);
		collected = collected + (String) eachView.getTag();
		eachView = findViewById(R.id.y6);
		collected = collected + (String) eachView.getTag();
		eachView = findViewById(R.id.y7);
		collected = collected + (String) eachView.getTag();
		eachView = findViewById(R.id.y8);
		collected = collected + (String) eachView.getTag();
		eachView = findViewById(R.id.y9);
		collected = collected + (String) eachView.getTag();
		eachView = findViewById(R.id.y10);
		collected = collected + (String) eachView.getTag();
		eachView = findViewById(R.id.y11);
		collected = collected + (String) eachView.getTag();
		eachView = findViewById(R.id.y12);
		collected = collected + (String) eachView.getTag();
		eachView = findViewById(R.id.y13);
		collected = collected + (String) eachView.getTag();
		eachView = findViewById(R.id.y14);
		collected = collected + (String) eachView.getTag();
		eachView = findViewById(R.id.y15);
		collected = collected + (String) eachView.getTag();
		eachView = findViewById(R.id.y16);
		collected = collected + (String) eachView.getTag();
		eachView = findViewById(R.id.y17);
		collected = collected + (String) eachView.getTag();
		eachView = findViewById(R.id.y18);
		collected = collected + (String) eachView.getTag();
		eachView = findViewById(R.id.y19);
		collected = collected + (String) eachView.getTag();
		eachView = findViewById(R.id.y20);
		collected = collected + (String) eachView.getTag();
		eachView = findViewById(R.id.y21);
		collected = collected + (String) eachView.getTag();
		// if all units are on the board
		if (collected.contains("_") == false)
		{
			playActivityTransition();
			// if there is a red box on board while the button is pressed
			if (this.hasRed == true)
			{
				// change the color of the original box from red to its former
				this.origView.setBackgroundResource(extractedColor());
				// change the tag
				this.origView.setTag(this.overriddenColor + "_" + this.pendingPiece);
				// then nullify hasRed, pendingPiece, overriddenColor, origView
				this.hasRed = false;
				this.pendingPiece = null;
				this.origView = null;
				this.overriddenColor = null;
			}
			// put enemy units
			putEnemyUnits();
			// change texts of message and button
			TextView txtView = (TextView) findViewById(R.id.your_txtview);
			txtView.setText(R.string.your_turn);
			Button btn = (Button) view;
			btn.setText(R.string.move);
			// clear class variables (just to be sure)
			this.hasRed = false;
			this.pendingPiece = null;
			this.origView = null;
			this.overriddenColor = null;
			// change game mode to 2
			gameMode = 2;
		}
		// if there is at least 1 unit remaining on the base
		else
		{
			playHuh();
			Toast.makeText(getApplicationContext(), R.string.remaining_message, Toast.LENGTH_SHORT).show();
		}
	}
	
	// (Gameplay - You) Clicked a box during your turn.
	private void clickedDuringYourTurn(View view)
	{
		if (gameMode == 2)
		{
			String tag = view.getTag().toString(); // gets the tag of the clicked view
			// IF the clicked box is YOUR unit
			if (view.getTag().toString().contains("x"))
			{
				// if the same red box is clicked
				if (view.getTag().toString().contains("red"))
				{
					// restore the color of the box
					view.setBackgroundResource(extractedColor());
					// restore the tag of the box
					view.setTag(this.overriddenColor + "_" + tag.substring(tag.indexOf("_")+1));
					// restore the color and tag of the other boxes
					int[] otherViewIds = showMoves(view.getId(), "x");
					for (int x = 0; x < 4; x++)
					{
						// if it is not available (not shaded to light red)
						if (otherViewIds[x] == 0)
						{
							// do nothing
							continue;
						}
						// if it is available (shaded to light red)
						else
						{
							View otherView = findViewById(otherViewIds[x]);
							otherView.setBackgroundResource(extractedColors()[x]);
							// if it is on enemy box
							if (otherView.getTag().toString().contains("_z"))
							{
								// set tag
								otherView.setTag(this.overriddenColors[x] + "_" + 
										otherView.getTag().toString().substring(otherView.getTag().toString().indexOf("_")+1));
							}
							// if blank
							else
							{
								// set tag
								otherView.setTag(this.overriddenColors[x]);
							}
						}
					}
					// nullify ALL variables
					this.hasRed = false;
					this.pendingPiece = null;
					this.pendingDest = null;
					this.overriddenColor = null;
					for (int x = 0; x < 4; x++)
					{ this.overriddenColors[x] = null;
					  this.overriddenIDs[x] = 0; }
					this.origView = null;
				}
				// if your unit is clicked and there is a red box somewhere
				else if (this.hasRed == true)
				{
					// restore the color of the original box
					this.origView.setBackgroundResource(extractedColor());
					// restore the tag of the original box
					this.origView.setTag(this.overriddenColor + "_" + 
							this.origView.getTag().toString().substring(this.origView.getTag().toString().indexOf("_")+1));
					// restore the colors and tags of the original shaded
					int[] otherViewIds = showMoves(this.origView.getId(), "x");
					for (int x = 0; x < 4; x++)
					{
						// if it is not available (not shaded to light red)
						if (otherViewIds[x] == 0)
						{
							// do nothing
							continue;
						}
						// if it is available (shaded to light red)
						else
						{
							View otherView = findViewById(otherViewIds[x]);
							otherView.setBackgroundResource(extractedColors()[x]);
							// if it is on enemy box
							if (otherView.getTag().toString().contains("_z"))
							{
								// set tag
								otherView.setTag(this.overriddenColors[x] + "_" + 
										otherView.getTag().toString().substring(otherView.getTag().toString().indexOf("_")+1));
							}
							// if blank
							else
							{
								// set tag
								otherView.setTag(this.overriddenColors[x]);
							}
						}
					}
					// nullify ALL variables
					this.hasRed = false;
					this.pendingPiece = null;
					this.pendingDest = null;
					this.overriddenColor = null;
					for (int x = 0; x < 4; x++)
					{ this.overriddenColors[x] = null;
					  this.overriddenIDs[x] = 0; }
					this.origView = null;
					// make the box red
					view.setBackgroundResource(R.color.red);
					// store the original color in overriddenColor
					this.overriddenColor = tag.substring(0, tag.indexOf("_"));
					// change the tag of the box to red_(xunit)
					view.setTag("red_" + tag.substring(tag.indexOf("_")+1));
					// check if there are available moves. if there are, make them light red
					// also (1) store the overriddenColors[] and
					// (2) change the tag of others to lred or lred_(zunit) in the process
					int[] otherViewIds2 = showMoves(view.getId(), "x");
					for (int x = 0; x < 4; x++)
					{
						// if it is not available (not shadable to light red)
						if (otherViewIds2[x] == 0)
						{
							this.overriddenColors[x] = "";
						}
						// if it is available (shadable to light red)
						else
						{
							// extra: for the moving part, ids are preserved
							this.overriddenIDs[x] = otherViewIds2[x];
							View otherView = findViewById(otherViewIds2[x]);
							otherView.setBackgroundResource(R.color.light_red);
							// if it is on enemy box
							if (otherView.getTag().toString().contains("_z"))
							{
								String enemyTag = otherView.getTag().toString();
								// (1)
								this.overriddenColors[x] = enemyTag.substring(0, enemyTag.indexOf("_"));
								// (2)
								otherView.setTag("lred_" + enemyTag.substring(enemyTag.indexOf("_")+1));
							}
							// if blank
							else
							{
								// (1)
								this.overriddenColors[x] = otherView.getTag().toString();
								// (2)
								otherView.setTag("lred");
							}
						}
					}
					// write to hasRed, pendingPiece, and origView (for overriddenColor/s, refer above)
					this.hasRed = true;
					this.pendingPiece = tag.substring(tag.indexOf("_")+1);
					this.origView = view;
					// nullify pendingDest
					this.pendingDest = null;
				}
				// if your unit is clicked and there is NO red box yet
				else
				{
					// make the box red
					view.setBackgroundResource(R.color.red);
					// store the original color in overriddenColor
					this.overriddenColor = tag.substring(0, tag.indexOf("_"));
					// change the tag of the box to red_(xunit)
					view.setTag("red_" + tag.substring(tag.indexOf("_")+1));
					// check if there are available moves. if there are, make them light red
					// also (1) store the overriddenColors[] and 
					// (2) change the tag of others to lred or lred_(zunit) in the process
					int[] otherViewIds = showMoves(view.getId(), "x");
					for (int x = 0; x < 4; x++)
					{
						// if it is not available (not shadable to light red)
						if (otherViewIds[x] == 0)
						{
							this.overriddenColors[x] = "";
						}
						// if it is available (shadable to light red)
						else
						{
							// extra: for the moving part, ids are preserved
							this.overriddenIDs[x] = otherViewIds[x];
							View otherView = findViewById(otherViewIds[x]);
							otherView.setBackgroundResource(R.color.light_red);
							// if it is on enemy box
							if (otherView.getTag().toString().contains("_z"))
							{
								String enemyTag = otherView.getTag().toString();
								// (1)
								this.overriddenColors[x] = enemyTag.substring(0, enemyTag.indexOf("_"));
								// (2)
								otherView.setTag("lred_" + enemyTag.substring(enemyTag.indexOf("_")+1));
							}
							// if blank
							else
							{
								// (1)
								this.overriddenColors[x] = otherView.getTag().toString();
								// (2)
								otherView.setTag("lred");
							}
						}
					}
					// write to hasRed, pendingPiece, and origView (for overriddenColor/s, refer above)
					this.hasRed = true;
					this.pendingPiece = tag.substring(tag.indexOf("_")+1);
					this.origView = view;
					// nullify pendingDest
					this.pendingDest = null;
				}
			}
			// IF either light red or dark red is pressed 
			else if (view.getTag().toString().contains("lred"))
			{
				// if there is a dark red box somewhere
				if (this.pendingDest != null)
				{
					// make the dark red box to light
					this.pendingDest.setBackgroundResource(R.color.light_red);
					// make the current light box to dark
					view.setBackgroundResource(R.color.dark_red);
					// set pendingDest to current box
					this.pendingDest = view;
				}
				// if there is no red box somewhere
				else
				{	
					// change the color to dark red
					view.setBackgroundResource(R.color.dark_red);
					// write to pendingDest
					this.pendingDest = view;
				}
			}
		}
	}
	
	// (Gameplay - You) Clicked the button during your turn.
	private void clickedBtnDuringYourTurn(View view)
	{
		if (this.pendingDest != null)
		{
			// revert back the colors and tags of YOU
			// for tags, just the color. units (physical and tags) will be switched in executeMove()
			String tagOrigView = this.origView.getTag().toString();
			this.origView.setBackgroundResource(extractedColor());
			this.origView.setTag(this.overriddenColor + "_" + tagOrigView.substring(tagOrigView.indexOf("_")+1));
			int[] otherViewIds = showMoves(this.origView.getId(), "x");
			for (int x = 0; x < 4; x++)
			{
				if (otherViewIds[x] == 0)
				{
					continue;
				}
				else
				{
					View otherView = findViewById(otherViewIds[x]);
					// set the color of other boxes
					otherView.setBackgroundResource(extractedColors()[x]);
					// (now set the tag of the other boxes)
					// if it is on enemy box, just change the color tag
					if (otherView.getTag().toString().contains("_z"))
					{
						otherView.setTag(this.overriddenColors[x] + "_" + 
								otherView.getTag().toString().substring(otherView.getTag().toString().indexOf("_")+1));
					}
					// if blank, just change the color tag
					else
					{
						otherView.setTag(this.overriddenColors[x]);
					}
				}
			}
			// executeMove()
			this.isFinished = executeMoves(this.origView, this.pendingDest);
			if (this.isFinished == true)
			{
			}
			else
			{
				// nullify ALL variables
				this.hasRed = false;
				this.pendingPiece = null;
				this.pendingDest = null;
				this.overriddenColor = null;
				for (int x = 0; x < 4; x++)
				{ this.overriddenColors[x] = null;
				  this.overriddenIDs[x] = 0; }
				this.origView = null;
				// delay time (before that, change text of textview and disable button)
				TextView txtView = (TextView) findViewById(R.id.your_txtview);
				Button btnView = (Button) view;
				txtView.setText(R.string.enemys_turn);
				btnView.setEnabled(false);
				gameMode = 3;
				new Handler().postDelayed(new Runnable() 
					{  
					   public void run() 
					   { 
						   	// manageAI()
							manageAI();
							// change back texts of textview and button
							TextView txtView = (TextView) findViewById(R.id.your_txtview);
							Button btnView = (Button) findViewById(R.id.main_button);
							txtView.setText(R.string.your_turn);
							btnView.setEnabled(true);
							gameMode = 2;
					   	}
					}, 1000);
			}
		}
		// if no pending move
		else
		{
			playHuh();
			Toast.makeText(getApplicationContext(), R.string.pending_message, Toast.LENGTH_SHORT).show();
		}
	}
	
	/******************* SOUND METHODS ***********************/
	
	public void playActivityTransition()
	{
		this.playerActivityTransition.stop();
		try { this.playerActivityTransition.prepare(); }
		catch (IllegalStateException e) {}
		catch (IOException e) {}
		this.playerActivityTransition.seekTo(0);
		this.playerActivityTransition.start();
	}

    public void playWhoop()
	{
		this.playerWhoop.stop();
		try { this.playerWhoop.prepare(); }
		catch (IllegalStateException e) {}
		catch (IOException e) {}
		this.playerWhoop.seekTo(0);
		this.playerWhoop.start();
	}
	
	public void playWhoopShort()
	{
		this.playerWhoopShort.stop();
		try { this.playerWhoopShort.prepare(); }
		catch (IllegalStateException e) {}
		catch (IOException e) {}
		this.playerWhoopShort.seekTo(0);
		this.playerWhoopShort.start();
	}
	
	public void playYeah()
	{
		this.playerYeah.stop();
		try { this.playerYeah.prepare(); }
		catch (IllegalStateException e) {}
		catch (IOException e) {}
		this.playerYeah.seekTo(0);
		this.playerYeah.start();
	}
	
	public void playAwww()
	{
		this.playerAwww.stop();
		try { this.playerAwww.prepare(); }
		catch (IllegalStateException e) {}
		catch (IOException e) {}
		this.playerAwww.seekTo(0);
		this.playerAwww.start();
	}
	
	public void playFling()
	{
		this.playerFling.stop();
		try { this.playerFling.prepare(); }
		catch (IllegalStateException e) {}
		catch (IOException e) {}
		this.playerFling.seekTo(0);
		this.playerFling.start();
	}
	
	public void playVictory()
	{
		this.playerVictory.stop();
		try { this.playerVictory.prepare(); }
		catch (IllegalStateException e) {}
		catch (IOException e) {}
		this.playerVictory.seekTo(0);
		this.playerVictory.start();
	}
	
	public void playLoser()
	{
		this.playerLoser.stop();
		try { this.playerLoser.prepare(); }
		catch (IllegalStateException e) {}
		catch (IOException e) {}
		this.playerLoser.seekTo(0);
		this.playerLoser.start();
	}
	
	public void playHuh()
	{
		this.playerHuh.stop();
		try { this.playerHuh.prepare(); }
		catch (IllegalStateException e) {}
		catch (IOException e) {}
		this.playerHuh.seekTo(0);
		this.playerHuh.start();
	}
	
	/******************* BUTTON METHODS ***********************/
	
	public void clickBtn(View view)
	{
		if (gameMode == 1)
		{ clickedBtnDuringSetup(view); }
		else if (gameMode == 2)
		{ clickedBtnDuringYourTurn(view); }
	}
	
	public void clickY1(View view) { clickedDuringSetup(view); }
	public void clickY2(View view) { clickedDuringSetup(view); }
	public void clickY3(View view) { clickedDuringSetup(view); }
	public void clickY4(View view) { clickedDuringSetup(view); }
	public void clickY5(View view) { clickedDuringSetup(view); }
	public void clickY6(View view) { clickedDuringSetup(view); }
	public void clickY7(View view) { clickedDuringSetup(view); }
	public void clickY8(View view) { clickedDuringSetup(view); }
	public void clickY9(View view) { clickedDuringSetup(view); }
	public void clickY10(View view) { clickedDuringSetup(view); }
	public void clickY11(View view) { clickedDuringSetup(view); }
	public void clickY12(View view) { clickedDuringSetup(view); }
	public void clickY13(View view) { clickedDuringSetup(view); }
	public void clickY14(View view) { clickedDuringSetup(view); }
	public void clickY15(View view) { clickedDuringSetup(view); }
	public void clickY16(View view) { clickedDuringSetup(view); }
	public void clickY17(View view) { clickedDuringSetup(view); }
	public void clickY18(View view) { clickedDuringSetup(view); }
	public void clickY19(View view) { clickedDuringSetup(view); }
	public void clickY20(View view) { clickedDuringSetup(view); }
	public void clickY21(View view) { clickedDuringSetup(view); }

	public void clickA1(View view) { clickedDuringYourTurn(view); }
	public void clickA2(View view) { clickedDuringYourTurn(view); }
	public void clickA3(View view) { clickedDuringYourTurn(view); }
	public void clickA4(View view) { clickedDuringYourTurn(view); }
	public void clickA5(View view) { clickedDuringYourTurn(view); }
	public void clickA6(View view) { clickedDuringYourTurn(view); }
	public void clickA7(View view) { clickedDuringYourTurn(view); }
	public void clickA8(View view) { clickedDuringYourTurn(view); }
	public void clickA9(View view) { clickedDuringYourTurn(view); }
	public void clickB1(View view) { clickedDuringYourTurn(view); }
	public void clickB2(View view) { clickedDuringYourTurn(view); }
	public void clickB3(View view) { clickedDuringYourTurn(view); }
	public void clickB4(View view) { clickedDuringYourTurn(view); }
	public void clickB5(View view) { clickedDuringYourTurn(view); }
	public void clickB6(View view) { clickedDuringYourTurn(view); }
	public void clickB7(View view) { clickedDuringYourTurn(view); }
	public void clickB8(View view) { clickedDuringYourTurn(view); }
	public void clickB9(View view) { clickedDuringYourTurn(view); }
	public void clickC1(View view) { clickedDuringYourTurn(view); }
	public void clickC2(View view) { clickedDuringYourTurn(view); }
	public void clickC3(View view) { clickedDuringYourTurn(view); }
	public void clickC4(View view) { clickedDuringYourTurn(view); }
	public void clickC5(View view) { clickedDuringYourTurn(view); }
	public void clickC6(View view) { clickedDuringYourTurn(view); }
	public void clickC7(View view) { clickedDuringYourTurn(view); }
	public void clickC8(View view) { clickedDuringYourTurn(view); }
	public void clickC9(View view) { clickedDuringYourTurn(view); }
	public void clickD1(View view) { clickedDuringYourTurn(view); }
	public void clickD2(View view) { clickedDuringYourTurn(view); }
	public void clickD3(View view) { clickedDuringYourTurn(view); }
	public void clickD4(View view) { clickedDuringYourTurn(view); }
	public void clickD5(View view) { clickedDuringYourTurn(view); }
	public void clickD6(View view) { clickedDuringYourTurn(view); }
	public void clickD7(View view) { clickedDuringYourTurn(view); }
	public void clickD8(View view) { clickedDuringYourTurn(view); }
	public void clickD9(View view) { clickedDuringYourTurn(view); }
	public void clickE1(View view) { clickedDuringYourTurn(view); }
	public void clickE2(View view) { clickedDuringYourTurn(view); }
	public void clickE3(View view) { clickedDuringYourTurn(view); }
	public void clickE4(View view) { clickedDuringYourTurn(view); }
	public void clickE5(View view) { clickedDuringYourTurn(view); }
	public void clickE6(View view) { clickedDuringYourTurn(view); }
	public void clickE7(View view) { clickedDuringYourTurn(view); }
	public void clickE8(View view) { clickedDuringYourTurn(view); }
	public void clickE9(View view) { clickedDuringYourTurn(view); }
	public void clickF1(View view) { clickedDuringSetup(view); clickedDuringYourTurn(view); }
	public void clickF2(View view) { clickedDuringSetup(view); clickedDuringYourTurn(view); }
	public void clickF3(View view) { clickedDuringSetup(view); clickedDuringYourTurn(view); }
	public void clickF4(View view) { clickedDuringSetup(view); clickedDuringYourTurn(view); }
	public void clickF5(View view) { clickedDuringSetup(view); clickedDuringYourTurn(view); }
	public void clickF6(View view) { clickedDuringSetup(view); clickedDuringYourTurn(view); }
	public void clickF7(View view) { clickedDuringSetup(view); clickedDuringYourTurn(view); }
	public void clickF8(View view) { clickedDuringSetup(view); clickedDuringYourTurn(view); }
	public void clickF9(View view) { clickedDuringSetup(view); clickedDuringYourTurn(view); }
	public void clickG1(View view) { clickedDuringSetup(view); clickedDuringYourTurn(view); }
	public void clickG2(View view) { clickedDuringSetup(view); clickedDuringYourTurn(view); }
	public void clickG3(View view) { clickedDuringSetup(view); clickedDuringYourTurn(view); }
	public void clickG4(View view) { clickedDuringSetup(view); clickedDuringYourTurn(view); }
	public void clickG5(View view) { clickedDuringSetup(view); clickedDuringYourTurn(view); }
	public void clickG6(View view) { clickedDuringSetup(view); clickedDuringYourTurn(view); }
	public void clickG7(View view) { clickedDuringSetup(view); clickedDuringYourTurn(view); }
	public void clickG8(View view) { clickedDuringSetup(view); clickedDuringYourTurn(view); }
	public void clickG9(View view) { clickedDuringSetup(view); clickedDuringYourTurn(view); }
	public void clickH1(View view) { clickedDuringSetup(view); clickedDuringYourTurn(view); }
	public void clickH2(View view) { clickedDuringSetup(view); clickedDuringYourTurn(view); }
	public void clickH3(View view) { clickedDuringSetup(view); clickedDuringYourTurn(view); }
	public void clickH4(View view) { clickedDuringSetup(view); clickedDuringYourTurn(view); }
	public void clickH5(View view) { clickedDuringSetup(view); clickedDuringYourTurn(view); }
	public void clickH6(View view) { clickedDuringSetup(view); clickedDuringYourTurn(view); }
	public void clickH7(View view) { clickedDuringSetup(view); clickedDuringYourTurn(view); }
	public void clickH8(View view) { clickedDuringSetup(view); clickedDuringYourTurn(view); }
	public void clickH9(View view) { clickedDuringSetup(view); clickedDuringYourTurn(view); }
	//public void clickCheck(View view) { checkTags(); }
}