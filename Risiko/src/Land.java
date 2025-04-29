public class Land {
    public String name;
    public Spieler besitzer;
    public int einheiten;
    public String[] nachbarn;

    public Land(String name, String... nachbarn) {
        this.name = name;
        this.nachbarn = nachbarn;
    }


    //Wir brauchen wahrscheinlich erstmal unsere Map damit wir wissen welches Land welchen Nachbarn hat.
    public void setBesitzer(Spieler spieler) {
        this.besitzer = spieler;


    }

}
