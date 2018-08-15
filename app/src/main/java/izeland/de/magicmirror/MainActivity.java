package izeland.de.magicmirror;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

public class MainActivity extends AppCompatActivity implements DataConnection.DataLoadedCallback, AdapterView.OnItemClickListener
{

	private ListView mListView;
	private DataConnection mDataConnection;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		mDataConnection = new DataConnection(this, getString(R.string.auth_key));

		mListView = findViewById(R.id.entries_list);

		final FloatingActionButton fab = findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				NewEntryDialog dialog = new NewEntryDialog();
				dialog.setDataConnection(mDataConnection);
				dialog.show(getFragmentManager(), null);
			}
		});
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		mDataConnection.refresh();
	}

	@Override
	public void onDataLoaded(List<Entry> entries)
	{
		mListView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1, entries));
		mListView.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id)
	{
		Entry entry = (Entry) mListView.getAdapter().getItem(position);
		System.out.println(entry.getId());
		mDataConnection.delete(entry.getId());
	}
}
