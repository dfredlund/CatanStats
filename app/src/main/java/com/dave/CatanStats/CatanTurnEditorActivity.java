package com.dave.CatanStats;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.dave.CatanStats.CatanGame.*;

import java.util.List;
import java.util.stream.Collectors;

import static com.dave.CatanStats.CatanGameActivity.PLAYER_LIST;
import static com.dave.CatanStats.CatanGameActivity.TURN;

/**
 Created: DFredlund 02/13/2017
 */

public class CatanTurnEditorActivity extends Activity {


	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_catan_turn_editor);

		CatanTurn catanTurn = (CatanTurn) getIntent().getSerializableExtra(TURN);
		List<Player> playerList = (List<Player>) getIntent().getSerializableExtra(PLAYER_LIST);
		Spinner playerName = (Spinner) findViewById(R.id.playerNameDropdown);
		TextView rollValue = findViewById(R.id.rollAmountEditText);
		final Button buttonSubmitTurnEditor = findViewById(R.id.buttonSubmitTurnEditor);
		final Button buttonCancelTurnEditor = findViewById(R.id.buttonCancelTurnEditor);

		ArrayAdapter<Player> arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item
				                                                    ,playerList.stream().collect(Collectors.toList()));
		playerName.setAdapter(arrayAdapter);
		rollValue.setText(Integer.toString(catanTurn.getRollValue()));

		//Default spinner to current turn's player
		Player currentPlayer = catanTurn.getPlayer();
		if(currentPlayer != null)
		{
			playerName.setSelection(arrayAdapter.getPosition(currentPlayer));
		}

		//region set button listeners
		buttonSubmitTurnEditor.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Spinner playerName = (Spinner) findViewById(R.id.playerNameDropdown);
				EditText rollValue = (EditText) findViewById(R.id.rollAmountEditText);
				CatanTurn catanTurn = (CatanTurn) getIntent().getSerializableExtra(TURN);
				String rollAmount = rollValue.getText().toString();
				catanTurn.setRollValue(Integer.parseInt(rollAmount));
				catanTurn.setPlayer((Player)playerName.getSelectedItem());
				Intent intent = new Intent();
				intent.putExtra(TURN, catanTurn);
				setResult(RESULT_OK, intent);
				finish();
			}
		});

		buttonCancelTurnEditor.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				setResult(RESULT_CANCELED, intent);
				finish();
			}
		});

		rollValue.setSelectAllOnFocus(true);
		rollValue.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v)
			{
				v.clearFocus();
				v.requestFocus();
			}
		});
		//endregion
	}
}
