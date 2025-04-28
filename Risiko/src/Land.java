public class Land {
    public String name;
    public Spieler besitzer;
    public int einheiten;
    public String[] nachbarn;

    //Nutzen wir eine konstante Anzahl an Ländern? Wäre vermutlich die beste Lösung mit einer konstanten Weltkarte. Allerdings würde es auch die Anzahl
    //der Spieler beeinflussen, wenn alle Länder am Anfang gerecht durch die Spieler geteilt werden soll.
    //Bswp. können 30 Länder nur gerecht bei 2, 3, 5 oder 6 (höhere Anzahl würde ich ausschließen) aufgeteilt werden.
    //Beeinschränken wir also die Spieleranzahl, die Länderanzahl oder die Gerechtigkeit?

    public Land(String name, String... nachbarn ){
        this.name = name;
        this.nachbarn = nachbarn;
    }
    //Wir brauchen wahrscheinlich erstmal unsere Map damit wir wissen welches Land welchen Nachbarn hat.
}
