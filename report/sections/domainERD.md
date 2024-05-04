### DOMÆNE ANALYSE

- beskriv/forklar motivationen til en domæneanalyse (f.eks. at den muliggør database-design, som 
i takt muliggør implementation af IT-løsningsprototype)

På baggrund af det udleverede virksomhedsbesøg, samt en grundig gennemgang af online materialer, 
er der foretaget en domæneanalyse af Johannes Fog byggemarked.

Overordnet set drejer forretningen om at sælge nogle varer (gennem en ansat f.eks. en sælger) 
fra et lager, enten for sig selv, eller sammenstykket til et færdigt produkt, til kunder. 
Altså kan domænet beskrives som en sammensætning af nogle kunder, ansatte, materialer (på et lager), 
produkter og bestillinger.

Gennem hjemmesiden er det muligt, at se en liste over forretningens produkter, sorteret efter 
kategori. Derudover er det muligt at sortere/filtrere listen yderligere ud fra f.eks. mål, pris osv., 
samt ved en konventionel tekstsøgning.

For at bestille et produkt, skal dets materialer/komponenter selvfølgelig være til stede på et lager, 
og derudover er det nødvendigt at kunne bestemme hvor produktet skal hentes eller sendes fra, før 
en bestilling kan behandles.

Diagrammet nedenfor er en umiddelbar __domænemodel__, udarbejdet i begyndelsen af projektet, som 
et udgangspunkt til en mere detaljeret skitsering af den data, en eventuel IT-løsning vil få brug for.

- IMG : domainmodel.puml

De egentlige fysiske besiddelser hos forretning er _materialer_/_varer_ på et lager. Siden 
forretningen f.eks. i forbindelse med salg af en carport, ikke sælger disse hver for sig, men 
som et samlet produkt, vælger vi i vores model at koble _materialer_ til _produkter_. Disse 
produkter kan altså bestå af en eller flere _materialer_, og kobles til en kunde (sælges), 
ved at oprette en _bestilling_. En bestilling består af et _produkt_, en _kunde_, en _ansat_ 
(sælger) og et eller flere datoer/tidspunkter (dvs. bestillingsdato, leveringsdato etc.).

For at muliggøre sortering og en mere finkornet søgning mellem _produkter_, skal de først 
og fremmest tilhører en _kategori_, dels for at kunne opdele søgningen overordnet, som på 
hjemmesiden, men også for at kunne muliggøre en yderligere sortering på baggrund af 
fællesnævnere. Det kunne f.eks. være at alle carporte har en højde eller en pris, osv. 
Alle produkter har således en eller flere kategorier.

Med domæneanalysen færdig er vi nu i stand til at skabe en model for IT-løsningens _database_.

### DATABASE

https://vertabelo.com/blog/er-diagram-for-online-shop/

"As general conditions, the ER diagram for an online shopping system must be normalized up to the third normal form. The reason for this is that the online shopping system is purely transactional, so it must support constant and concurrent updates of the tables that make up the schema. And it must support those updates while strictly maintaining data integrity and consistency."



For at omdanne domænemodellen til en model der kan bruges til at implementere en database for 
en IT-løsning, kræves det, at mange-til-mange relationer omdannes til en-til-mange og mange-til-en 
relationer. Derudover skal domænets data helst bringes på normalform (det er dog ikke altid 
nødvendigt eller hensigtsmæssigt).

Nedenfor ses en _ERD_ model for en mulig implementation af en database.

- IMG : ERD

#### DATABASE NOTER

Forklar hvorfor vi går fra mangetilmange til entilmange med mellemledstabeller (navn?).

##### produkt

Et produkt skulle tilknyttes en række informationer, f.eks. et billede, pris, 
noget dokumentation samt eventuelle eksterne links. Derudover skal det fremgå, 
hvor produktet findes fysisk, dvs. på hvilke lagre, samt antal.

I domæneanalysen blev det tydeligt, at et produkt dels kan være en simpel 
"atomisk" genstand som et brædde, men også en mængde af disse, f.eks. er en 
carport først og fremmest en carport, men samtidig findes den ikke på lageret. 
Den består derimod af dele, som faktisk findes på lageret. Med det sagt, 
valgte vi, at et et produkt i databasen kan tilknyttes et vilkårligt antal 
produkter i form relationen produkt <-- produktkomponent.

##### specs

produktspecifikation: motiveret af høj diversitet blandt produkter, og derfor 
deres fællesnænvnere. Dette er problem ifbm. søgning/sortering. Man skal kunne 
sortere carporte efter længde, bredde, osv., men hvad med varer, der måske har 
andre fællesnævnere (længde og bredde er universelle) som vægt, plads, type 
af forskellige dele på produktet.
Løsning: tabel over specifikationer, med navn og enhed. tabel til at 
koble et produkt til en specifikation, sammen med detaljerne for denne, f.eks. 

- (produkt)carport <-- (detaljer)fladt --> (specifikationsnavn)tagtype

Problematik: datatype for detaljer er fastlåst, dvs. en målbar specifikation 
som længde skulle måske gemmes som en varchar/streng. I vores forundersøgelse 
af Fogs hjemmeside lader deres søgefunktion rent faktisk til at behandle dem 
således, dvs., man sortere varer som en carport efter længde, ligesom man 
ville sortere dem efter mærke; ved at vælge en eller flere fra en liste, og 
ikke __min < x < max__, som nemt kunne implementeres med et prædikat; 
__SELECT * FROM table WHERE length > min && length < max__. Den samme 
forspørgsel ville formentlig være meget kompliceret og dyr (arbejdsmæssigt 
for serveren), at implementere.

Hvor om alting er, valgte vi ovenstående løsning, altså at alle 
specifikationsdetaljer fik samme datatype.

##### bestilling

En bestilling skal tilknyttes et produkt, en ansat (user), en kunde (user), 
nogle leveringsdetaljer. Derudover blev der lavet en tabel til statuskoder. 
Dermed er det muligt at holde styr på, hvor i forløbet en bestilling er, 
og derved f.eks. opfylde kravet om, at en stykliste på en skræddersyet 
carport først må frigives til kunden, efter betaling.

User-story # beskriver en indkøbskurv, som ikke indgår i database designet. 
Grunden er, at der er tale om midlertidige valg, som højst sandsynligt 
ændres undervejs, og derfor ville det ikke være hensynsmæssigt, at ligge 
dem i- og opdatere databasen ved hver ændring.

For at opfylde user-story #, skal en bestilling have sin egen uafhængige pris, 
for at lade en sælger give en særtilbud til kunden.

##### users/kunde/ansat

##### kommunikation

Indgår ikke i databasen, da korrespondence bliver eksternt i form af email.

Eller... ?
