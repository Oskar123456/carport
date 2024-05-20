### DOMÆNE ANALYSE

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

For at omdanne domænemodellen til en model der kan bruges til at implementere en database for 
en IT-løsning, kræves det, at mange-til-mange relationer omdannes til en-til-mange og mange-til-en 
relationer. Derudover skal domænets data helst bringes på normalform (det er dog ikke altid 
nødvendigt eller hensigtsmæssigt).

Lad os gaa i dybden med kravene til en endelig database ud fra den udarbejdede domaeneanalyse:

Varehus

For at kunne saelge noget, skal der vaere nogle varehuse, og disse skal kunne indeholde 
varer, samt have en adresse tilknyttet sig, for at understoette en leveringsfunktion 
gennem hjemmesiden. Dvs. hvorfra kan en given vare sendes fra.

Kunde & ansat/saelger

For at have nogen at saelge til, og nogen at betjene disse, kraeves det at kunne 
slaa kunder og ansatte/saelgere op i databasen. 
Disse skal have kontaktinformation samt adresser tilknyttet sig, for f.eks. 
at kunne understoette en leveringsfunktion som i ovenstaende afsnit.

Produkt

For at kunne vise varer frem paa hjemmesiden kraeves det, at it-loesning 
kan slaa alle produkter op i en databasen. Ansatte skal ogsaa kunne opdatere 
disse loebende.

For at understoette sortering og soegning, skal produkter have en masse information 
tilknyttet sig, som f.eks. kategori, specifikation (bredde: 40cm), pris mm. De skal 
muligvis ogsaa have billeder og andet dokumentation, som kan vises paa hjemmesiden.

Bestilling

Til sidst skal der vaere lister over bestillinger, som henviser til tidspunkt, status, 
kunde og saelger involveret osv. 

-- ERD

Nedenfor ses et ER-diagram over den endelige databaseloesning:

- IMG : ERD

Forklaring:

product:
Et produkt har navn, beskrivelse, pris og links, 
derudover er det tilknyttet dets kategorier, specifikationer, 
dokumenter og billeder gennem vha. fremmednoegler; dvs. foelgende 
tabeller binder naevnte egenskaber til det enkelte produkt, for 
at nedbryde mange-til-mange relation til en-til-mange:

	product_category
	product_specification
	product_documentation
	product_image
	product_component

Det var vigtigt i database designet, at et produkt kunne tilknyttes 
vilkaarlige specifikationer, eftersom Fogs sortiment er saa diverst, og 
samtidig understoetter en sorteret soegefunktion, hvor de enkelte 
specifikationsvaerdier kan justeres i soegningen. Altsaa man skal 
kunne udvaelge de varer fra en given soegning med f.eks. en hvis 
bredde eller hoejde osv., hvilket kraever at it-loesningen skal 
kunne finde faellesnaevnere blandt soegningens produkters' 
specifikationer. Eksempelvis skal en soegning paa stolper og braedder 
give mulighed for at filtere/sortere efter vilkaarlige laengder/bredder, 
da alle produkter i de kategorier skal vaere tilknyttet de specifikationer.

Det er ogsaa vaerd at naevne, at et produkt kan tilknyttes 
en vilkaarlig maengde af andre produkter (dog ikke det selv 
af aabenlyse aarsager som cirkulaer afhaengighed). Det skylles at, 
som naevnt i _forundersoegelsen_, produkter som f.eks. en carport 
jo kan indeholde en liste over de dele det bestaar af, navnligt 
en stykliste som:
	Carport:
	Stykliste:
		3x stolper 2x tagplader

category:
En kategori er blot et navn som f.eks. "carport". Derudover er 
kategorier tilknyttet deres egne specifikationer, og det er 
gjort for at soerge for, at hver kategori kan have faellesnaevnere. 
Dvs. en stolpe kunne vaere en kategori, der var tilknyttet laengde, 
bredde og hoejde. Paa den maade kan it-systemet vide, hvilke 
specifikationer der er kraevede for et givent produkt ved 
indsaettelse i databasen.

	category_specification

specification:
En specifikation har et navn og en enhed/'unit', 
f.eks. ["stolpe" mm] osv.

image:
Raa bytes, samt format-beskrivelse (meget vigtig ifbm. systemets 
fortolkning af den raa data).

warehouse:
Indeholder blot en adresse.

users:
Baade kunder og ansatte er brugere i systemet. Den eneste forskel 
er i den rolle/'role' de har faaet tildelt i raekken.

orders:
En bestilling indeholder fremmednoegler til saelger, koeber, statuskode 
og leveringsdetaljer. Derudover er hvert produkt som indgaar i en bestilling 
tilknyttet denne gennem en en-til-en tabel, for at nedbryde mange-til-mange 
relationen fra domaeneanalysen.
	order_product
	shipment
	payment
	status_code



















