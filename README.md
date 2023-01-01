Voici un document d'explication sur comment utiliser gradle pour compiler et exécuter ce projet java.


Le code se trouve dans src/main/java/
Les ressources dans src/main/java/resources/
Les tests en JUnit5 sont dans test/java/model/

Pour compiler ce projet, il suffit de faire une fois ./gradlew build

Puis à chaque fois qu'on veut exécuter le code, il faut faire ./gradlew run

Si l'on veut lancer les tests il faut faire ./gradlew test



Une fois le programme lancé, nous sommes dans un menu ou l'on peut choisir un des differents modes de jeu suivants :

1) Normal Solo:
    Il s'agit d'un mode non compétitif ou l'on a 20 mots à écrire, les fautes ne sont pas comptabilisées.
    Le jeu se termine quand le joueur a écrit les 20 mots.
2) Compétitif Solo:
    Il s'agit d'un mode compétitif. On a un certain nombre de mots, chaque fois qu'un temps donné est passé,
    un mot est rajouté à la liste. Il y a un système de vie. Si le joueur se trompe, ou qu'un mot est ajouté
    à la liste sans qu'il n'y ait de place, ce dernier perd une vie. Il y a des mots bleus qui redonnent des vies.
    Le jeu se termine quand le joueur n'a plus de vie.
3) Multijoueur:
    Il s'agit d'un mode de jeu a plusieurs. Les joueurs ont chacun une liste de mot différente, certains sont rouges.
    Les mots rouges tapés en une seule fois sont envoyés aux adversaires. Pareillement, il y a un système de points de vies.
    Si le joueur se trompe dans un mot ou qu'il n'y a plus de place dans sa liste de mots, alors il perd une vie.
    Le jeu se termine pour le joueur qui a perdu, les autres peuvent continuer.

A la fin de chacun des modes de jeu, il y a un affichage des statistiques telles que la régularité, la précision, le score et la vitesse.



En plus de ces informations, nous pouvons préciser que lorsqu'on écrit pas la bonne lettre,
elle n'est pas comptabilisée. Il n'est donc pas nécessaire de l'effacer. Par contre, si le mode de jeu
le demande, une vie est effectivement perdue.








