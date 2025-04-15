public class Spieler {
    public String name;
    public String farbe;
    public int einheiten;
    public boolean alive;

    public void sterben(){
        this.alive = false;
    }
}
