package com.jeffsieu.tasktracker;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by Jeff on 26/6/2016.
 */
public class Task implements Parcelable {
    private static final DateFormat format = SimpleDateFormat.getDateInstance();
    private Calendar date;
    private String name;

    public Task() {
        date = GregorianCalendar.getInstance();
        name = "";
    }

    public Calendar getDate() {
        return date;
    }

    public String getDateString() {
		date.set(date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
		Calendar now = GregorianCalendar.getInstance();
		Calendar today = GregorianCalendar.getInstance();
		Calendar dateDay = GregorianCalendar.getInstance();
		today.set(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
		dateDay.set(date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
		long daysDifference = (long) Math.round((dateDay.getTimeInMillis() - today.getTimeInMillis())/1000/60/60/24);
		long millisDifference = date.getTimeInMillis() - now.getTimeInMillis();
		int hoursDifference = Math.round(millisDifference/1000/60/60);
		if (daysDifference == -1) {
			return "Yesterday";
		}
		if (daysDifference == 0) {
			return "Today";
		}
		if (daysDifference == 1) {
			return "Tomorrow";
		}
		if (Math.abs(daysDifference) < 7) {
			String prefix = "";
			if (daysDifference < 0)
				prefix = "Last ";
			switch (date.get(Calendar.DAY_OF_WEEK) % 7) {
				case 0:
					return prefix+"Saturday";
				case 1:
					return prefix+"Sunday";
				case 2:
					return prefix+"Monday";
				case 3:
					return prefix+"Tuesday";
				case 4:
					return prefix+"Wednesday";
				case 5:
					return prefix+"Thursday";
				case 6:
					return prefix+"Friday";
			}
        }
        format.setCalendar(date);
        String dateFormatted = format.format(date.getTime());
        return dateFormatted;
    }

	public String getTimeLeft() {
		date.set(date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
		Calendar now = GregorianCalendar.getInstance();
		Calendar today = GregorianCalendar.getInstance();
		Calendar dateDay = GregorianCalendar.getInstance();
		today.set(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
		dateDay.set(date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
		long daysDifference = (long) Math.round((dateDay.getTimeInMillis() - today.getTimeInMillis())/1000/60/60/24);
		long millisDifference = date.getTimeInMillis() - now.getTimeInMillis();
		int hoursDifference = Math.round(millisDifference/1000/60/60);
		if (daysDifference > 0) {
			if (daysDifference == 1)
				return daysDifference + " day left";
			return daysDifference + " days left";
		} else if (daysDifference == 0 && millisDifference > 0) {
			if (hoursDifference == 1)
				return hoursDifference + " hour left";
			return hoursDifference + " hours left";
		}

		return "Overdue";
	}

    public void setDate(Calendar date) {
        this.date = date;
    }

    public void setDate(int year, int month, int date) {
        this.date.set(year, month, date);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void importFromTask(Task task) {
		this.name = task.getName();
		this.date = task.getDate();
    }

    protected Task(Parcel in) {
        date = (Calendar) in.readValue(Calendar.class.getClassLoader());
        name = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(date);
        dest.writeString(name);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Task> CREATOR = new Parcelable.Creator<Task>() {
        @Override
        public Task createFromParcel(Parcel in) {
            return new Task(in);
        }

        @Override
        public Task[] newArray(int size) {
            return new Task[size];
        }
    };
}