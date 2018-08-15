package izeland.de.magicmirror;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by malte on 04.02.17.
 */
public class DataConnection
{

	private static final String TAG = "DataConnection";

	private static final String URL = "http://magicmirror.ize-land.de/index.php";
	private static String AUTH_KEY_PREFIX;
	private static URL mUrl;


	static
	{
		try
		{
			mUrl = new URL(URL);
		}
		catch (MalformedURLException e)
		{
			Log.e(TAG, "static initializer: ", e);
		}
	}

	private DataLoadedCallback mDataLoadedCallback;


	DataConnection(DataLoadedCallback dataLoadedCallback, String key)
	{
		AUTH_KEY_PREFIX = String.format("auth_key=%s&", key);
		mDataLoadedCallback = dataLoadedCallback;
	}

	void insert(String newText)
	{
		AsyncNetworkTask asyncNetworkTask = new AsyncNetworkTask(false);
		asyncNetworkTask.execute(newText);
	}

	void delete(int id)
	{
		AsyncNetworkTask asyncNetworkTask = new AsyncNetworkTask(true);
		asyncNetworkTask.execute(Integer.toString(id));
	}

	void refresh()
	{
		AsyncNetworkTask asyncNetworkTask = new AsyncNetworkTask(false);
		asyncNetworkTask.execute();
	}

	@NonNull
	private String encodeInsertPostData(@Nullable final String[] strings) throws UnsupportedEncodingException
	{
		final StringBuilder stringBuilder = new StringBuilder();
		if (strings != null)
		{
			stringBuilder.append("send=");

			StringBuilder jsonString = new StringBuilder(); // Add [ for json handling
			for (int i = 0; i < strings.length; i++)
			{
				jsonString.append(strings[i]);
				if (i < strings.length - 1)
				{
					jsonString.append(",");
				}
			}

			// Sending side
			byte[] data = jsonString.toString().getBytes("UTF-8");
			String base64 = Base64.encodeToString(data, Base64.NO_WRAP);

			stringBuilder.append("&data");
			stringBuilder.append("=");
			stringBuilder.append(base64);
		}

		Log.d(TAG, stringBuilder.toString());
		return stringBuilder.toString();
	}

	@NonNull
	private List<Entry> parseJsonResult(String string)
	{
		final List<Entry> entryList = new ArrayList<>();

		try
		{
			final JSONArray jsonArray = new JSONArray(string);
			for (int i = 0; i < jsonArray.length(); i++)
			{
				try
				{
					JSONObject jsonObject = jsonArray.getJSONObject(i);
					int id = Integer.parseInt(jsonObject.optString("id", ""));
					String text = new String(Base64.decode(jsonObject.optString("value", ""), Base64.NO_WRAP), "UTF-8");
					entryList.add(new Entry(id, text));
				}
				catch (UnsupportedEncodingException | IllegalArgumentException e)
				{
					e.printStackTrace();
				}
			}
		}
		catch (JSONException e)
		{
			Log.e(TAG, "parseJsonResult: ", e);
		}

		return entryList;

	}


	public interface DataLoadedCallback
	{
		void onDataLoaded(List<Entry> entries);
	}

	private class AsyncNetworkTask extends AsyncTask<String, Void, List<Entry>>
	{

		private boolean mDelete;

		AsyncNetworkTask(boolean delete)
		{
			mDelete = delete;
		}

		@Override
		protected List<Entry> doInBackground(String... strings)
		{
			HttpURLConnection urlConnection = null;
			try
			{
				urlConnection = (HttpURLConnection) mUrl.openConnection();
				urlConnection.setDoOutput(true);
				urlConnection.setDoInput(true);

				urlConnection.setRequestMethod("POST");
				urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

				String postParameters = AUTH_KEY_PREFIX;

				if (mDelete)
				{
					if (strings != null && strings.length == 1)
					{
						postParameters += "delete=" + strings[0];
					}
				}
				else if (strings != null && strings.length > 0)
				{
					postParameters += encodeInsertPostData(strings);
				}

				urlConnection.setFixedLengthStreamingMode(postParameters.getBytes().length);

				final PrintWriter outputPrintWriter = new PrintWriter(urlConnection.getOutputStream());
				outputPrintWriter.print(postParameters);
				outputPrintWriter.flush();
				outputPrintWriter.close();

				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

				String data = "";
				String string;
				while ((string = bufferedReader.readLine()) != null)
				{
					data += string;
				}
				return parseJsonResult(data);
			}
			catch (IOException e)
			{
				Log.e(TAG, "doInBackground: ", e);
			}
			finally
			{
				if (urlConnection != null)
				{
					urlConnection.disconnect();
				}
			}
			return null;
		}


		@Override
		protected void onPostExecute(List<Entry> entries)
		{
			if (mDataLoadedCallback != null)
			{
				mDataLoadedCallback.onDataLoaded(entries);
			}
		}
	}
}
