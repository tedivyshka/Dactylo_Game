Voici un document d'explication sur comment utiliser gradle pour compiler et exécuter ce projet java.

Pour commencer, inutile de toucher aux dossiers build, .gradle, gradle ainsi que settings.gradle

Le code se trouve dans src/main/java/
Les ressources (le .txt) dans src/main/java/resources 

À chaque modification du code (ou ajout d'un doc dans les sources),
il faudra recompiler tous les fichiers en faisant ./gradlew build
Ça va modifier le dossier build et ce qu'il contient.

Ensuite pour run le projet, il faut faire java -jar build/libs/dactylo_game.jar

Le document build.gradle peut être modifié pour ajouter des dépendances (je n'ai pas encore compris comment ça marche)
ou alors pour ajouter des plugins.

Si l'on venait à modifier l'emplacement du Main, pour compiler il faudrait changer
jar {
    manifest {
        attributes 'Main-Class': 'FicMain'
    }
}
en replacement FicMain par le nouveau fichier contenant le Main.

