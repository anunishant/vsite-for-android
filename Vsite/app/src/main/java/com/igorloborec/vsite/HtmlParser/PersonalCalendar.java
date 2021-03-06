/*
 * Copyright (C) 2015  Bikonja
 */

package com.igorloborec.vsite.HtmlParser;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Bikonja on 4.3.2015..
 */
public class PersonalCalendar {
    public static final String DATE_FORMAT = "dd.MM.yyyy. HH:mm";
    public static final SimpleDateFormat dateFormatter = new SimpleDateFormat(DATE_FORMAT);

    protected ArrayList<PersonalCalendarEntry> _entries;

    public ArrayList<PersonalCalendarEntry> get_entries() {
        return _entries;
    }

    public PersonalCalendar()
    {
        _entries = new ArrayList<PersonalCalendarEntry>();
    }

    public PersonalCalendar(String html) {
        Document document = Jsoup.parse(html);
        _entries = new ArrayList<PersonalCalendarEntry>();

        // TODO: Sanity checks, logs, ...
        Elements entryDivs = document.select(".bubbleInfo");

        Pattern datesPattern = Pattern.compile("([0-9]{2}\\.[0-9]{2}\\.[0-9]{4}\\.) ([0-9]{2}:[0-9]{2}) ([0-9]{2}:[0-9]{2}) ([0-9]h)");

        for (Element entryDiv : entryDivs) {
            PersonalCalendarEntry entry = new PersonalCalendarEntry();
            Element triggerBox = entryDiv.select("div.trigger").get(0);
            Element popupBox = entryDiv.select("div.popup").get(0);

            String[] triggerBoxEntries = Helper.GetNodeTextWithNewLines(triggerBox).split("\\r?\\n");
            int triggerBoxIndex = triggerBoxEntries[0].isEmpty() ? 1 : 0;
            String subjectAbbr = triggerBoxEntries[triggerBoxIndex].trim();
            String subjectType = triggerBoxEntries[triggerBoxIndex + 1].trim();
            String subjectGroup = triggerBoxEntries[triggerBoxIndex + 2].trim();

            String[] popupLines = Helper.GetNodeTextWithNewLines(popupBox).split("\\r?\\n");
            String subjectUrl = popupBox.select("a").get(0).attr("href");
            String subjectLocation = popupLines[1].trim();
            Date subjectStart = null;
            Date subjectEnd = null;
            String subjectDuration = "";
            Matcher matcher = datesPattern.matcher(popupBox.text());
            if (matcher.find())
            {
                try {
                    subjectStart = dateFormatter.parse(matcher.group(1) + " " + matcher.group(2));
                    subjectEnd = dateFormatter.parse(matcher.group(1) + " " + matcher.group(3));
                    subjectDuration = matcher.group(4);
                } catch (ParseException e) {
                    e.printStackTrace();

                    subjectStart = null;
                    subjectEnd = null;
                    subjectDuration = "";
                }
            }
            String subjectHolder = popupLines[3].trim();
            String subjectStatus = popupLines[popupLines.length - 1].trim();

            entry.set_subjectAbbr(subjectAbbr);
            entry.set_subjectType(subjectType);
            entry.set_subjectGroup(subjectGroup);
            entry.set_subjectUrl(subjectUrl);
            entry.set_location(subjectLocation);
            entry.set_location(subjectLocation);
            entry.set_start(subjectStart);
            entry.set_end(subjectEnd);
            entry.set_duration(subjectDuration);
            entry.set_subjectHolder(subjectHolder);
            entry.set_status(subjectStatus);

            _entries.add(entry);
        }
    }
}