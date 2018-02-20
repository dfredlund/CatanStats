package com.dave.CatanStats;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import com.dave.CatanStats.CatanGame.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 Created: DFredlund 02/19/2017
 */

public class CatanPreGameOptionsActivity extends Activity implements PlayerDialogFragment.PlayerDialogFragmentListener{

	public static final String POSITION = "Position";
	ListView playerList;
	ArrayList<Player> playerArrayList;
	ArrayAdapter<Player> playerArrayAdapter;
	Player[] defaultPlayerArray = new Player[] {new Player(PlayerColor.RED, null), new Player(PlayerColor.BLUE, null),
			new Player(PlayerColor.WHITE, null), new Player(PlayerColor.GREEN, null), new Player(PlayerColor.ORANGE, null),
			new Player(PlayerColor.BROWN, null)};
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_catan_pregame_options);

		playerList = findViewById(R.id.playerListView);
		Spinner playerNumberSpinner = findViewById(R.id.numberPlayersDropdown);
		final Button startGameButton = findViewById(R.id.startGamePregameOptions);
		final Button cancelButton = findViewById(R.id.cancelPregameOptions);

		playerArrayList = new ArrayList<Player>();
		Integer[] numberPlayers = new Integer[]{3,4,5,6};
		ArrayAdapter<Integer> playerNumberAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,numberPlayers);
		playerNumberSpinner.setAdapter(playerNumberAdapter);
		playerNumberAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		//Default to 3 players, populate with default players
		playerNumberSpinner.setSelection(0);
		SetNumberPlayers(numberPlayers[0]);
		playerArrayAdapter = new CatanPreGameOptionsPlayerAdapter(this,playerArrayList);
		playerList.setAdapter(playerArrayAdapter);
		//region listeners
		startGameButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra(CatanGameActivity.PLAYER_LIST, playerArrayList);
				setResult(RESULT_OK, intent);
				finish();
			}
		});

		cancelButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				setResult(RESULT_CANCELED, intent);
				finish();
			}
		});

		playerNumberSpinner.setOnItemSelectedListener(
				new AdapterView.OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> parent, View view, int position,
					                           long id)
					{
						SetNumberPlayers(numberPlayers[position]);
					}

					@Override
					public void onNothingSelected(AdapterView<?> parent)
					{
					}
				}
		);
		playerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				FragmentManager fragmentManager = getFragmentManager();
				PlayerDialogFragment playerDialogFragment = PlayerDialogFragment.newInstance(playerArrayAdapter.getItem(position),position);
				playerDialogFragment.show(fragmentManager,"android_dialog_player");
			}
		});
		//endregion

	}

	private void SetNumberPlayers(int numPlayers)
	{
		if(playerArrayList.size() < numPlayers)
		{
			for(int i = playerArrayList.size(); i<numPlayers; i++)
				playerArrayList.add(defaultPlayerArray[i]);
		}
		else
		{
			for(int i = playerArrayList.size(); i>numPlayers; i--)
				playerArrayList.remove(i-1);
		}
	}

	public void onFinishEditPlayerDialog(CatanGame.PlayerColor color, String playerName, int position)
	{
		Player player = playerArrayList.get(position);
		player.setColor(color);
		player.setName(playerName);
		playerArrayAdapter.notifyDataSetChanged();
	}
}
