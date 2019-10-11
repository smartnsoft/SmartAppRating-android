package com.smartnsoft.smartapprating.utils;

import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author Adrien Vitti
 * @since 2018.02.12
 */

public final class DateUtils
{
  /**
   * <p>Checks if two date objects are on the same day ignoring time.</p>
   * <p>28 Mar 2002 13:45 and 28 Mar 2002 06:01 would return true.
   * 28 Mar 2002 13:45 and 12 Mar 2002 13:45 would return false.
   * </p>
   *
   * @param date1 the first date, not altered, not null
   * @param date2 the second date, not altered, not null
   * @return true if they represent the same day
   * @throws IllegalArgumentException if either date is <code>null</code>
   * @since 2.1
   */
  public static boolean isSameDay(Date date1, Date date2)
  {
    if (date1 == null || date2 == null)
    {
      throw new IllegalArgumentException("The date must not be null");
    }
    final Calendar cal1 = Calendar.getInstance();
    cal1.setTime(date1);
    final Calendar cal2 = Calendar.getInstance();
    cal2.setTime(date2);
    return isSameDay(cal1, cal2);
  }

  /**
   * <p>Checks if two calendar objects are on the same day ignoring time.</p>
   * <p>28 Mar 2002 13:45 and 28 Mar 2002 06:01 would return true.
   * 28 Mar 2002 13:45 and 12 Mar 2002 13:45 would return false.
   * </p>
   *
   * @param cal1 the first calendar, not altered, not null
   * @param cal2 the second calendar, not altered, not null
   * @return true if they represent the same day
   * @throws IllegalArgumentException if either calendar is <code>null</code>
   * @since 2.1
   */
  private static boolean isSameDay(Calendar cal1, Calendar cal2)
  {
    if (cal1 == null || cal2 == null)
    {
      throw new IllegalArgumentException("The date must not be null");
    }
    return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
        cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
        cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR));
  }

  /**
   * Adds a number of days to a date returning a new object.
   * The original {@code Date} is unchanged.
   *
   * @param date   the date, not null
   * @param amount the amount to add, may be negative
   * @return the new {@code Date} with the amount added
   * @throws IllegalArgumentException if the date is null
   */
  public static Date addDays(Date date, int amount)
  {
    return add(date, Calendar.DAY_OF_MONTH, amount);
  }

  /**
   * Adds to a date returning a new object.
   * The original {@code Date} is unchanged.
   *
   * @param date          the date, not null
   * @param calendarField the calendar field to add to
   * @param amount        the amount to add, may be negative
   * @return the new {@code Date} with the amount added
   * @throws IllegalArgumentException if the date is null
   */
  private static Date add(Date date, int calendarField, int amount)
  {
    if (date == null)
    {
      throw new IllegalArgumentException("The date must not be null");
    }
    final Calendar c = Calendar.getInstance();
    c.setTime(date);
    c.add(calendarField, amount);
    return c.getTime();
  }
}
