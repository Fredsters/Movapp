package semanticweb.hws14.movapp.helper;

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
}
