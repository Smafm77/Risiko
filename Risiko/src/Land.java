public class Land {
    public String name;
    public Spieler besitzer;
    public int einheiten;
    public int strength;
    public String[] nachbarn;

    public Land(int strength, String name, String... nachbarn) {
        this.strength = strength;
        this.name = name;
        this.nachbarn = nachbarn;
    }


    //Wir brauchen wahrscheinlich erstmal unsere Map damit wir wissen welches Land welchen Nachbarn hat.
    public void setBesitzer(Spieler spieler) {
        this.besitzer = spieler;
    }

    //ToDo schreib eine Methode die Checkt ob es eine nutzbare verbindung mit einem anderen Land über bereits besetzte Länder gibt
    // --> separiert ob ziel eigenes, oder benachbartes fremdes Terretorium ist

}
