# Prueba Zitro

## Iniciar la aplicación

Para iniciar simplemente importamos el proyecto como Existing Gradle Project y hacemos click derecho sobre la clase Application. A continuación, seleccionamos Run As ->Java Application. 

Siguiendo las indicaciones del enunciado donde se define un juego del casino como un objeto individual, he definido cada uno de los juegos como un servicio separado, pero por temas de simplificación solo he implementado el juego “Slot”. Para implementar los demás solo hace falta seguir la misma estructura que define slot, cambiando los parámetros del juego a voluntad.

### Reglas del juego
Para jugar un juego/realizar apuestas primero necesitamos iniciar el juego. De esta manera el juego nos asigna un identificador interno y registra nuestro tiempo de entrada. Una vez iniciado, podemos comenzar a realizar apuestas siguiendo las restricciones de cada juego. 

Cada juego implementa una interface CasinoGame en donde se definen las funcionalidades y atributos compartidos. Cada juego tiene una configuración inicial de:
* Nombre del juego
* Apuesta mínima y máxima 
* Probabilidad de recompensa 
* Horas máximas de juego que puede estar el usuario 

He decidido que cada juego defina un número de horas máximas que pueden estar los jugadores de manera individual (y como indica el enunciado en donde el usuario tiene un número máximo de horas de juego), de esta manera el sistema solo compara el tiempo que cada usuario lleva jugando a partir de registrar la fecha de inicio de juego o sesión.

## Implementación
La aplicación principal está compuesta de varios submódulos:
* Client: Define las APIs de cada servicio/juego, se puede usar para proporcionar contratos de interacciones cliente/servidor.
* Model: Define la lógica de los juegos.
* Repository: Repositorios de objetos y entidades que definen los sistemas de almacenamiento y 
* Auth: Capa de seguridad para configurar el uso de OAuth2.
* Test: Validar los servicios y la lógica de negocio.

Algunos de los servicios implementados (para el juego Slot) se encuentran en el paquete principal (nombrados nombredeljuegoSvc e.g. SlotSvc) y son los siguientes:

GET /slot
- Devuelve la configuración del juego (parámetros a seguir al realizar una apuesta)

GET /slot/bet
- Devuelve toda la lista de transacciones que se han realizado en el juego. 

GET /slot/{id}/bet
- Devuelve la transacción por un identificador especifico. 
- En TransactionRepository también se definen funcionalidades para obtener transacciones por usuario, juego o apuesta menor a un valor. 

POST /slot
- Iniciar el juego, recibe un usuario, le asigna un identificador interno y registra su hora de entrada al juego.

POST /slot/{id}/bet
- Recibe un identificador de usuario y una apuesta. A partir de las características del juego realiza una apuesta y crea/almacena la operación. Devuelve el resultado de la transacción realizada.

## Consideraciones 
- He añadido una capa de seguridad básica con OAuth2, en donde la aplicación usuario al ser externa primero debe solicitar un token para poder acceder a los servicios. Tened en cuenta que gran parte de la implementación no es la óptima (usa keystore) ya que hay información hardcode y se debería usar un certificado SSL real. 

- No he podido usar Maven para el control de dependencias, ya que el framework me estaba dando demasiados problemas con partes que ya habían sido testeadas. De esta manera me he visto obligado a usar Gradle y un esqueleto de dependencias que había utilizado en el pasado. Espero que no sea ningún inconveniente para iniciar la aplicación. 

