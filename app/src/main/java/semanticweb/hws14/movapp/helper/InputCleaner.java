package semanticweb.hws14.movapp.helper;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Resource;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Frederik on 01.11.2014.
 */
public class InputCleaner {

    public static String cleanActorName(String actorName) {
        actorName = actorName.trim();
        final StringBuilder result = new StringBuilder(actorName.length());
        String[] words = actorName.split("\\s");
        for(int i=0; i<words.length; i++) {
            if(i>0) {
                result.append(" ");
            }
            result.append(Character.toUpperCase(words[i].charAt(0))).append(words[i].substring(1).toLowerCase());
        }
        return result.toString();
    }

    public static int cleanReleaseYear(Literal year) {
        String yearString;
        if(year == null) {
            yearString = "0";
        } else {
            yearString = year.toString();
        }
        Pattern p = Pattern.compile("\\d\\d\\d\\d");
        Matcher m = p.matcher(yearString);
        if(m.find()) {
            yearString = m.group();
        }
        return Integer.parseInt(yearString);
    }

    public static String cleanMovieTitle (String title) {
        return title.replace("and", "&");
    }

    public static String cleanImdbId(Resource url) {
        String imdbId = "";
        if(url == null) {
            imdbId = "0";
        } else {
            imdbId = url.getURI().toString();
        }
        Pattern p = Pattern.compile("tt[\\d]+");
        Matcher m = p.matcher(imdbId);
        if(m.find()) {
            imdbId = m.group();
        }
        return imdbId;
    }
}