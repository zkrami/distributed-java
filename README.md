## Blockchaine centralisé 
Le projet contient toutes les fonctionalités avant la partie avancée. 

*De brefs commentaires ont également été ajoutés en tête de chaque classe pour expliquer son fonctionnement.* 

### Serveur 

Le serveur fournit trois services aux mineurs. 

- Un service d'initialisation auquel le mineur peut se connecter pour demander la blockchain actuelle du serveur. 
*Au lieu d'initialiser la blockchain dans le mineur, le mineur demande la blockchain complète du servuer. Ce qui permet au mineur de se connecter plus tard*

- Un service multicast auquel le mineur peut s'abonner pour écouter en permanence les nouveaux blocs insérés. 
*Le multicast a été implémentée en utilisant une socket `Tcp` car les messages envoyés sont importants, et il est nécessaire de s'assurer de leur arrivée*

- Un service de vérification de transaction auquel le mineur peut envoyer une transaction avec un sel pour la vérifier et la diffuser. 

Pour simuler la durée de vérification d'une transaction. une command ```  Thread.sleep(1000); ``` a été rajouté dans la tâche de verification. Pour permettre de tester le cas où le mineur n'attend pas la réponse du serveur. 
### Client 

Le client envoie une transaction à un mineur choisi.
Le client doit insérer les informations de la transaction (montant, payeur, receveur) et le port du mineur choisi.
Le client peut envoyer plusieurs transactions sans attendre la réponse du mineur. 
Si le client envoie une transaction non-valide, il recoit un message transaction non-valide. (Un payeur ou receveur qui n'existe pas, ou le payeur n'a pas le montant) 
### Mineur 

Le mineur reçoit en permanence des transactions à valider de la part des clients.

Le mineur a deux files. Une file de mining pour trouver un sel pour une transaction (`miningExceutor`), et une autre file pour vérifier le sel trouvé en communiquant avec le serveur (`validationExceutor`).

Le mineur a deux blockchains. Une chaîne qui est ajoutée uniquement par les transactions reçues du canal de multicast (vérifiée par le serveur).  Et une chaîne locale. 

Quand le mineur trouve un nouveau sel. Il ajoute une nouvelle jonction à la chaîne locale, met la transaction dans la file de vérification et enchaîne la chaîne locale. Au cas où le mineur reçoit un refus du serveur, il arrête tout travail pour trouver un sel. Il réinitialise la chaîne locale par l'autre chaine et transmet toutes les tâches de la file de validation à la file de mining. Parce que toutes les jonctions dans la file de validation ont été construites à la base de la jonction qui a été refusée par le serveur.

Les tâches sont transmises d'une file à l'autre de manière à exploiter au maximum le mineur mais en même temps sans en perdre aucune et sans en passer aucune sans être vérifié dans le serveur.

Dans le code, il n'y a pas de chaîne locale, c'est seulement un pointeur vers la dernière jonction. Le principe est d'avoir une chaîne en laquelle on peut avoir confiance et une autre chaîne qui contient des transactions non validées.

Pour que le mineur n'attende pas la réponse du serveur, il essaie d'augmenter la difficulté de la même manière que le serveur.


Lors du démarrage d'un mineur, le mineur affiche son port affectué par le système d'exploitation.


## Lancer le projet 

Afin de simplifier le lancement du projet. 
Des `jar` ont été ajoutés avec le projet. 

Pour tester le projet. 
Commencez par démarrer le serveur `java -cp DistributedProgramming.jar cryptomonaie.serveur.Serveur`
Ensuite, démarrer un mineur ou plusieur `java -cp DistributedProgramming.jar cryptomonaie.mining.Mineur`
*Souvenez-vous du port affiché dans le mineur*
Ensuite, lancez un client `java -cp DistributedProgramming.jar cryptomonaie.client.Client` et suivez l'indication pour envoyer une transaction.


## Partage des tâches effectuées au sein du binôme.
On n'a pas divisé des tâches entre nous. 
La plus part du temps on se réunait pour travailer ensemble afin d'avancer plus vite. 