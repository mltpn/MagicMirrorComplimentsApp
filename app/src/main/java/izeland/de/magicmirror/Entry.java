package izeland.de.magicmirror;

/**
 * Created by malte on 04.02.17.
 */
public class Entry
{
	private int mId;
	private String mValue;

	Entry(int id, String value)
	{
		mId = id;
		mValue = value;
	}

	public int getId()
	{
		return mId;
	}

	public void setId(int id)
	{
		mId = id;
	}

	public String getValue()
	{
		return mValue;
	}

	public void setValue(String value)
	{
		mValue = value;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (o == null || getClass() != o.getClass())
		{
			return false;
		}

		Entry entry = (Entry) o;

		if (mId != entry.mId)
		{
			return false;
		}
		return mValue != null ? mValue.equals(entry.mValue) : entry.mValue == null;

	}

	@Override
	public int hashCode()
	{
		int result = mId;
		result = 31 * result + (mValue != null ? mValue.hashCode() : 0);
		return result;
	}

	@Override
	public String toString()
	{
		return mValue;
	}
}
