# ChatServer sur un serveur externe

## Logiciels principaux

### Installer les logiciels principaux

#### PostgreSQL

##### Installation

- **sudo apt install postgresql**

##### Vérifier fonctionnement

- **psql**

#### Redis

##### Installation

- **sudo apt install redis-server**

##### Vérifier fonctionnement

- **redis-cli ping**

### Configurer les logiciels principaux

#### PostgreSQL

##### Ajouter la base de données

Exécuter le fichier database.sql via pgAdmin (https://www.linode.com/docs/guides/how-to-access-postgresql-database-remotely-using-pgadmin-on-windows/)

##### Autoriser les connections extérieures

**Modifier le fichier pg_hba.conf :**

`IPv4 local connections:`

`host	all		all		0.0.0.0/0		trust`

**Modifier le fichier postgresql.conf :**

`listen_addresses = '*'`

**Redémarrer postgreSQL :**

`sudo systemctl restart postgresql`

#### Redis

##### Autoriser les connexions extérieures

**Modifier le fichier redis.conf :**

`bind 0.0.0.0`

**Redémarrer Redis :**

`sudo systemctl restart redis-server`

## Importer le projet

Importer les sources ainsi que les .jar (dans un fichier isolé), sur le serveur externe.

## Lancer le ChatServer

### Topologie du dossier

Dossier principal : `ChatServeur`

`ChatServeur/Lib/fichiersJarDuProjet`

`ChatServeur/ChatServer/src/app/fichiersJavaDuProjet`

### Compiler le programme

Mettre tout les .jar dans le dossier /Lib/, mêmes ceux de ses sous-dossiers.

#### Ligne de commande (depuis app/) :

- `javac -cp *jar:../../../Lib/ *java */*java`

 ### Exécuter le programme

#### Ligne de commande (depuis src/) :

- `java -cp .:../../Lib/*  app.ServerMain`

