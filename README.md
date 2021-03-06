# Pametni ugovori o alotmanu
Repozitorijum za projekte na predmetima Pravna informatika i Semantički veb na master akademskim studijama u okviru studijskog programa Primenjene računarske nauke i informatika, modul Inteligentni sistemi.

## Uputstvo za pokretanje

Opisani postupak pokretanja sistema se odnosi na Windows operativni sistem.

###### Potreban softver:
- Java
- MySQL sistem za upravljanje bazama podataka
- Node package manager (npm)
- Angular CLI
- Ganache CLI

Pokrenuti testnu mrežu pokretanjem komande `ganache-cli -m "myth like bonus scare over problem client lizard pioneer submit female collect"`.

Pokrenuti MySQL server.

Pokrenuti izvršnu verziju Spring Boot aplikacije komandom `java -jar backend\target\allotment-0.0.1-SNAPSHOT.jar`.

Pozvati komandu `npm install` u direktorijumu `frontend\`.

Pokrenuti frontend aplikaciju komandom `ng serve`.

Frontend aplikacija će se pokrenuti na adresi http://localhost:4200/.


Logovanje je moguće izvršiti korišćenjem kredencijala nekih od predefinisanih korisnika. Neki od njih su navedeni u nastavku.

> Predstavnik agencije
>
> Username: ppera
>
> Password: 123

> Predstavnik ugostitelja
>
> Username: mmika
>
> Password: 123

## Napomene

Pristup podacima o raspoloživom stanju u wei-ma na nalozima je moguć na adresi http://localhost:8080/api/web3j/balancesWei.
Redni broj balansa odgovara `id` vrednosti organizacije (prva vrednost je vrednost koju na raspologanju ima agencija ili ugostitelj koji ima id=1 itd.).

Provere funkcionalnosti finalizacije ugovora nakon što je istekao i treba li uvesti restrikcije agenciji zbog loše predsezone moguće je izvršiti pravljenjem rezervacija čiji je kraj u prošlosti. 
Primeri metoda koje vrše ove provere se izvršavaju na svakih 60 sekundi (klasa `ScheduledTasks` na putanji `backend\src\main\java\rs\ac\uns\ftn\informatics\legal_tech\allotment\`).
Da bi ovo bilo moguće potrebno je izbaciti proveru koja zabranjuje pravljenje rezervacija u prošlosti, tako da je neophodno zakomentarisati raspon linija od 249 do 253 u fajlu na putanji `frontend\src\app\propose\propose.component.ts`.

Ukoliko se menja sadržaj pametnog ugovora potrebno je pokrenuti skriptu `compile.bat`, koja kompajlira fajl `Allotment.sol` i smešta generisani java fajl na odgovarajuću putanju.
