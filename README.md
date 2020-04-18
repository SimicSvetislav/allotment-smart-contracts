# allotment-smart-contracts
Projekat za predmet Pravna informatika na master akademskim studijama u okviru studijskog programa Primenjene računarske nauke i informatika, modul Inteligentni sistemi.

## Uputstvo za pokretanje

Opisani postupak pokretanja sistema se odnosi na Windows operativni sistem.

###### Potreban softver:
- Java
- Maven
- MySQL sistem za upravljanje bazama podataka
- Node package manager (npm)
- Angular-CLI
- Git

Pokrenuti komandu `ganache-cli` iz *command prompt*-a i prekopirati adrese i privatne ključeve u fajl `accounts.data`, koji se nalazi na putanji `backend\src\main\resources\`.
Pokrenuti MySQL server.
Pokrenuti Spring Boot aplikaciju u direktorijumu `backend\` korišćenjem komandne linije ili nekog razvojnog okruženje, preporučeno *Eclipse*.

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

Pri prvom pokretanju u application.properties fajlu treba da budu otkomentarisani parametri oznаčeni za prvo pokretanje, kako bi se izvršila inicijalizacija baze podataka.

Ukoliko se menja sadržaj pametnog ugovora potrebno je pokrenuti skriptu `complie.bat`, koja kompajlira fajl `Allotment.sol` i smešta generisani java fajl na odgovarajuću putanju.
