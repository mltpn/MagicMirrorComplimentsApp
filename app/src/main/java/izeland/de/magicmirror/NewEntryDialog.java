package izeland.de.magicmirror;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

/**
 * Created by malte on 05.02.17.
 */

public class NewEntryDialog extends DialogFragment implements View.OnClickListener
{
	private EditText mEditText;
	private DataConnection mDataConnection;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{

		final View view = inflater.inflate(R.layout.new_entry_dialog, container, false);
		mEditText = (EditText) view.findViewById(R.id.new_entry_edit_text);
		view.findViewById(R.id.cancel_button).setOnClickListener(this);
		view.findViewById(R.id.ok_button).setOnClickListener(this);

		return view;
	}

	@Override
	public void onResume()
	{
		super.onResume();
		mEditText.requestFocus();
	}

	@Override
	public void onClick(View v)
	{
		if (v.getId() == R.id.cancel_button)
		{
			dismiss();
		}
		else if (v.getId() == R.id.ok_button)
		{
			mDataConnection.insert(mEditText.getText().toString());
			dismiss();
		}
	}

	public void setDataConnection(DataConnection dataConnection)
	{
		mDataConnection = dataConnection;
	}
}
