package com.dave.CatanStats;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;
import java.util.stream.Collectors;

import static com.dave.CatanStats.CatanPreGameOptionsActivity.POSITION;

/**
Created: DFredlund 02/19/2018
 */

public class PlayerDialogFragment extends DialogFragment
{
	private Spinner playerColor;
	private EditText playerName;
	public interface PlayerDialogFragmentListener
	{
		void onFinishEditPlayerDialog(CatanGame.PlayerColor color, String playerName, int position);
	}

	public PlayerDialogFragment(){};

	public static PlayerDialogFragment newInstance(CatanGame.Player player, int position)
	{
		PlayerDialogFragment playerDialogFragment = new PlayerDialogFragment();
		Bundle args = new Bundle();
		args.putSerializable(CatanTurn.PLAYER,player);
		args.putInt(POSITION,position);
		playerDialogFragment.setArguments(args);
		return playerDialogFragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.android_dialog_player,viewGroup);
		playerColor = view.findViewById(R.id.dialogPlayerColor);
		playerName = view.findViewById(R.id.dialogPlayerName);
		final Button submitButton = view.findViewById(R.id.dialogPlayerSubmit);
		final Button cancelButton = view.findViewById(R.id.dialogPlayerCancel);
		ArrayAdapter<CatanGame.PlayerColor> playerColorAdapter =
				new ArrayAdapter(this.getContext(), android.R.layout.simple_spinner_item,CatanGame.GetPlayerColors());
		playerColor.setAdapter(playerColorAdapter);
		CatanGame.Player player = (CatanGame.Player) getArguments().getSerializable(CatanTurn.PLAYER);

		//Default Player Color and name, if populated
		playerColor.setSelection(CatanGame.GetPlayerColors().indexOf(player.getColor()));
		if(!(player.getName() == null) && !player.getName().isEmpty())
		playerName.setText(player.getName());

		submitButton.setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v)
					{
						PlayerDialogFragmentListener listener = (PlayerDialogFragmentListener) getActivity();
						listener.onFinishEditPlayerDialog(playerColorAdapter.getItem(playerColor.getSelectedItemPosition()),
								playerName.getText().toString(), getArguments().getInt(POSITION));
						dismiss();
					}
				}
		);
		cancelButton.setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v)
					{
						dismiss();
					}
				}
		);
		return view;
	}
}
