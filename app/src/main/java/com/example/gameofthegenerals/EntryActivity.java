package com.example.gameofthegenerals;

import java.io.IOException;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.RadioButton;

public class EntryActivity extends Activity
{
	private MediaPlayer playerActivityTransition;
	private boolean isEasy;
	private boolean isMedium;
	private boolean isHard;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_entry);
		isEasy = true;
	}
	
	@Override
	protected void onStart()
	{
		super.onStart();
		this.playerActivityTransition = MediaPlayer.create(this, R.raw.activity_transition);
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
	}

	@Override
	public void onBackPressed()
	{
		new AlertDialog.Builder(this)
		.setTitle(R.string.app_name)
		.setMessage(R.string.exit_message)
		.setPositiveButton(R.string.exit_yes, new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
				finish();
			}
		})
		.setNegativeButton(R.string.exit_no, new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.dismiss();
			}
		})
		.show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.entry, menu);
		return true;
	}
	
	public void showBoard(View view)
	{
		playActivityTransition();
		// initialize the difficulty global variable
		RadioButton radioBtn1 = (RadioButton) findViewById(R.id.easy);
		RadioButton radioBtn2 = (RadioButton) findViewById(R.id.medium);
		RadioButton radioBtn3 = (RadioButton) findViewById(R.id.hard);
		if (radioBtn1.isChecked() == true)
		{ BoardActivity.difficulty = 1; }
		else if (radioBtn2.isChecked() == true)
		{ BoardActivity.difficulty = 2; }
		else if (radioBtn3.isChecked() == true)
		{ BoardActivity.difficulty = 3; }
		
		BoardActivity.gameMode = 1;
		Intent intent = new Intent(this, BoardActivity.class);
		startActivity(intent);
	}

	private void playActivityTransition()
	{
		this.playerActivityTransition.stop();
		try
		{ this.playerActivityTransition.prepare(); }
		catch (IllegalStateException e)
		{}
		catch (IOException e)
		{}
		this.playerActivityTransition.seekTo(0);
		this.playerActivityTransition.start();
	}
	
	public void clickEasy(View view)
	{ 
		isMedium = false;
		isHard = false;
		if (isEasy == false)
		{
			isEasy = true;
		}
	}
	public void clickMedium(View view)
	{
		isEasy = false;
		isHard = false;
		if (isMedium == false)
		{
			isMedium = true;
		}
	}
	public void clickHard(View view)
	{
		isEasy = false;
		isMedium = false;
		if (isHard == false)
		{
			isHard = true;
		}
	}
}
