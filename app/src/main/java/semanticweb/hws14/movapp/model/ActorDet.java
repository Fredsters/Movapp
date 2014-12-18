package semanticweb.hws14.movapp.model;

import java.util.ArrayList;

/**
 * Created by Frederik on 14.11.2014.
 */
public class ActorDet {
//the data class for actor in detail page

    private String wikiAbstract;
    private String birthName;
    private String birthDate;
    private String name;
    private ArrayList<String> birthPlace;
    private String nationality;
    private String occupation;
    private String pictureURL;
    private int children;
    private int activeYear;
    private String homepage;
    private ArrayList<String> movies;
    private String partner;
    private ArrayList<String> parent;
    private ArrayList<String> roles;

    private EventActorListener eventActorListener = null;

    public ActorDet(String wikiAbstract, String birthName, String birthDate, String name, ArrayList<String> birthPlace, String nationality, String occupation, String pictureURL, int children, int activeYear, String homepage, ArrayList<String> movies, String partner, ArrayList<String> parent, ArrayList<String> roles) {
        this.wikiAbstract = wikiAbstract;
        this.birthName = birthName;
        this.birthDate = birthDate;
        this.name = name;
        this.birthPlace = birthPlace;
        this.nationality = nationality;
        this.occupation = occupation;
        this.pictureURL = pictureURL;
        this.children = children;
        this.activeYear = activeYear;
        this.homepage = homepage;
        this.movies = movies;
        this.partner = partner;
        this.parent = parent;
        this.roles = roles;
    }

    public ActorDet(){
        this.wikiAbstract = "";
        this.birthName = "";
        this.birthDate = "";
        this.name = "";
        this.birthPlace = new ArrayList<String>();
        this.nationality = "";
        this.occupation = "";
        this.pictureURL = "";
        this.children = 0;
        this.activeYear = 0;
        this.homepage = "";
        this.movies = new ArrayList<String>();
        this.partner = "";
        this.parent = new ArrayList<String>();
        this.roles = new ArrayList<String>();
    }

    public void addMovie(String movieName){
        if(!movies.contains(movieName)) {
            movies.add(movieName);
        }
    }

    public void addRole (String role){
        if(!roles.contains(role)) {
            roles.add(role);
        }
    }

    public void addChild (String childName){
        if(!parent.contains(childName)) {
            parent.add(childName);
        }
    }

    public void addBirthPlace (String birthPlaceName){
        if(!birthPlace.contains(birthPlaceName)) {
            birthPlace.add(birthPlaceName);
        }
    }

    public EventActorListener geteListener() {
        return eventActorListener;
    }

    public void setOnFinishedEventListener(EventActorListener listener) {
        eventActorListener = listener;
    }

    public String getWikiAbstract() {
        return wikiAbstract;
    }

    public void setWikiAbstract(String wikiAbstract) {
        this.wikiAbstract = wikiAbstract;
    }

    public String getBirthName() {
        return birthName;
    }

    public void setBirthName(String birthName) {
        this.birthName = birthName;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getPictureURL() {
        return pictureURL;
    }

    public void setPictureURL(String pictureURL) {
        this.pictureURL = pictureURL;
    }

    public int getChildren() {
        return children;
    }

    public void setChildren(int children) {
        this.children = children;
    }

    public int getActiveYear() {
        return activeYear;
    }

    public void setActiveYear(int activeYear) {
        this.activeYear = activeYear;
    }

    public String getHomepage() {
        return homepage;
    }

    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    public ArrayList<String> getMovies() {
        return movies;
    }

    public void setMovies(ArrayList<String> movies) {
        this.movies = movies;
    }

    public String getPartner() {
        return partner;
    }

    public void setPartner(String partner) {
        this.partner = partner;
    }

    public ArrayList<String> getParent() {
        return parent;
    }

    public void setParent(ArrayList<String> parent) {
        this.parent = parent;
    }

    public ArrayList<String> getBirthPlace() {
        return birthPlace;
    }

    public void setBirthPlace(ArrayList<String> birthPlace) {
        this.birthPlace = birthPlace;
    }

    public ArrayList<String> getRoles() {
        return roles;
    }

    public void setRoles(ArrayList<String> roles) {
        this.roles = roles;
    }

    @Override
    public String toString() {
        return "ActorDet{" +
                "wikiAbstract='" + wikiAbstract + '\'' +
                ", birthName='" + birthName + '\'' +
                ", birthDate='" + birthDate + '\'' +
                ", name='" + name + '\'' +
                ", birthPlace='" + birthPlace + '\'' +
                ", nationality='" + nationality + '\'' +
                ", occupation='" + occupation + '\'' +
                ", pictureURL='" + pictureURL + '\'' +
                ", children=" + children +
                ", activeYear=" + activeYear +
                ", homepage='" + homepage + '\'' +
                ", movies=" + movies +
                ", partner='" + partner + '\'' +
                ", parent='" + parent + '\'' +
                ", eventActorListener=" + eventActorListener +
                '}';
    }

    public String createTvOutOfList(ArrayList list){
        ArrayList<String> newList = list;
        String result="";

        for( String newListItem : newList){

            result = result + newListItem + "\n";
        }
        return result;
    }
}
