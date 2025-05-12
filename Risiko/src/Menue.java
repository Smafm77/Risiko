public class menue{
    public enum Befehl {
        ANGRIFF(1),
        BEWEGEN(2),
        INFO(3),
        UEBERSICHT(4),
        ZUGBEENDEN(5),
        SPIELBEENDEN(666);

        private final int auswahl;

        Befehl(int auswahl) {
            this.code = code;
        }

        public int getAuswahl() {
            return auswahl;
        }

        public static Befehl fromInt(int auswahl) {
            for (Befehl b : Befehl.values()) {
                if (b.getAuswahl() == auswahl) {
                    return b;
                }
            }
        }
    }
    Scanner scanner = new Scanner(System.in);
    public Befehl eingabeEinlesen(){
        while (true){
            menueAnzeigen();
            int auswahl = scanner.nextInt();
            scanner.nextLine();
            return Befehl.fromInt(auswahl);
        }
    }
    public void menueAnzeigen(){
        System.out.println("Du bist am Zug :" + spieler.getName());
        System.out.println("Was willst du tun? ");
        System.out.println("1: Angreifen");
        System.out.println("2: Truppen bewegen");
        System.out.println("3: Infos über...");
        System.out.println("4: Übersicht meiner Gebiete");
        System.out.println("5: Zug beenden");
        System.out.println("666: Spiel beenden");
    }
}