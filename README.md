SpaceRacer
==========

1. Ant-Befehle

1.1 Kompilieren
  
  ant compile         # kompiliert das Spiel
  ant build           # erstellt aus kompiliertem Spiel ein Jar-Archiv

1.2 Dokumentation

  ant javadoc         # erstellt die JavaDocs
  
1.3 Releases (Zip-Archiv)

  ant win             # erstellt ein Windows Release
  ant linux           # erstellt ein Linux Release
  ant linux64         # erstellt ein Linux (64 bit) Release
  ant mac             # erstellt ein MacOSX Release
  
  ant all             # erstellt alle Releases
  ant new             # löscht alle vorhanden Releases und erstellt das 
                      # aktuelle neu
                   
1.4 Spiel starten
  
  ant run-win         # startet das Spiel unter Windows
  ant run-linux       # startet das Spiel unter Linux
  ant run-linux64     # startet das Spiel unter Linux (64 bit)
  ant run-mac         # startet das Spiel unter MacOSX
  
1.5 Spiel testen (Profiler: profile.txt)

  ant profile-win     # testet das Spiel unter Windows
  ant profile-linux   # testet das Spiel unter Linux
  ant profile-linux64 # testet das Spiel unter Linux (64 bit)
  ant profile-mac     # testet das Spiel unter MacOSX

1.6 Cleanup

  ant clean           # löscht build-Verzeichnis (alle *.class Dateien)
  ant clean-release   # löscht alle Releases (in release/)
  

2. Team

  Stefan Kreuter        Projektleitung

  Knüchel Christian     Model
 
  Brunner Thomas        View

  Gift Sebastian        Control

  Kleeeis Dominik       Control: Physik

  Hackenberg Martin     Control: Kollisionserkennung 
 
  Bouillon Jan          Leveldesign, Dialogdesign 
 
  Blank Julius          Leveldesign, Dialogdesign, Sound
 
  Brock Bernhard        Netzwerk 
