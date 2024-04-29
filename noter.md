### SEMESTER PROJEKT - FOG BYGGEMARKED CARPORT

#### BAGGRUND

Bestilling af carporte hos Fog Byggemarked.



##### nøgleord og noter

carport, levering, egne mål, specialmål, bestilling, 
mail med ønsker, indkørselshøjde, vælg mellem carport/skur, 
fladtag, taghældning, spær, (redskabsrum; bræddebeklædning, gulv), 
stykliste, tegning, rem, water or lod, universalbeslag, 
rejsning (tag), stærn (Beslaget benyttes til fastgørelse
på spær bag sternbræt på udhæng), 

VALG: bredde, længde, tag, skur

STYKLISTE: antal, vare nr., beskrivelse, 
hjælpe detaljer (hvor, hvordan osv.), længde, bredde, højde, 
pris?

E-mail med forspørgsel; kunde vil gerne have et tilbud på 
denne carport (spec.). Personligt svar med f.eks. tegning (ikke 
stykliste). Det de sælger er en service som sammensætter 
varer fra deres lager, inkl. instruktioner osv. Dvs. de har 
ikke lyst til at give en indkøbsliste, fordi så kan kunder 
sammenstykke carporten med varer fra konkurrenter.

Det er muligt for dem, at se om en kundeforspørgsel ikke 
hænger sammen. Så kan de ringe dem op, f.eks er deres mål 
forkerte, så i stedet for at lade kunden købe noget forkert, kan 
de redde dem.

De har en liste over håndværkere/tømrere, som de kan 
henvise til, hvis folk ikke selv vil bygge dem.

Modeller for belastning på stolper/spær ud fra tagvægt osv.

Rem bærer taget.

Trekants- og enkeltspær.

### DEV

Initiativ --> epic --> tickets/stories/tasks

#### TICKETS

indeholder user-story og accept kriterier.

##### user stories

Som x vil jeg y for at kunne z.

- Som kunde : opret bruger : log ind : bestille/modtage mail.

- Som kunde vil jeg gerne kunne søge/sortere carporte igennem, 
for at finde en der passer til min grund.

+ Risiko ????

- Som kunde vil jeg gerne kunne lægge varer i en kurv, og 
se hvad den samlede pris er, inkl. transport, samt bestille.

+ Risiko: 

- Som kunde vil jeg gerne kunne se alle detaljer omkring 
carporten, før jeg køber, f.eks. mål, pris, tegning etc.

- Som kunde vil jeg gerne kunne specificere/selv sammensætte 
en carport, som passer til lige præcis mit behov, og få 
hjælp til sammensætningen online.

+ Risiko: Kunden kan få en dårlig oplevelse, hvis de ikke 
gør det ordentligt.

- Som admin vil jeg gerne kunne sammensætte et forslag for 
kunden på baggrund af deres bestilling, og i den forbindelse 
se vejledende priser og alle detaljer på styklisten, samt 
bygningsmanual/beskrivelse og bestillingsdetaljer til kunden, 
som ikke indeholder stykliste.

+ Risiko: Svært at implementere skræddersyede tegninger / 
manualer ???

##### accept kriterier

Definition af done.

Som x der gør y sker z.

Meningen er at være specifik, så vi kan teste objektivt. 
Funktionelle- og ikke-funktionelle krav.

### INTERESSENTANALYSE

#### FORMÅL

Giv projektleder overblik over de personer, der har 
interesse i projektet (stakeholder), samt at gøre 
det muligt at foretage vurdering af deres oplevelse 
af fordele og ulemper.

#### TYPER
^ (y) - påvirket
|
| gidels  | ressource person |
| ekstern | grå eminence     |
-----> (x) - indflydelse

### RISIKO

#### HÅNDTERING

skal besvare følgende spørgsmål.

1. Hvad er det vi prøver at opnå.
2. Hvad kan påvirke projektet.
3. Hvilke risici er vigtigst? p(x) * w(x)
4. Hvad kan vi gøre.
5. Virkede tiltag.
6. Hvad har ændret sig.

### Noter fra info møde d. 29 maj: 
1. OBS!! Skal afleveres d. 24 maj på WISE flow
2. Mødes med underviser en gang om ugen
3. Skær ting i små bidder, så det er bliver håndgribeligt
4. Der skal laves en beregningsmotor i Java
5. Hellere lave lidt, men lave det virkelig godt!
6. Husk at tiltænke at spær, rem og brædder har en vis længde, så når det er på specialmål skal man have de rigtige antal, så man senere som kunde kan skære til og få de rigtige mål
7. Lad vær med at blive forblændet af styklistens længde, start med rem, spær og brædder
8. Vi skal KUN lave med fladt tag
9. Vi skal lave noget der ligner deres eksisterende “byg-selv-carporte”
10. Vi behøver ikke en indkøbskurv
11. Måske bestillingsflow med 2-3 steps (sider)
12. Giv en ordre en ordrestatus (admin kan ændre status)
13. Kunde kan første se stykliste når ordrestatus er ændret til betalt
14. Tegning kan ses så snart oplysninger er tastet ind
15. FOG er dygtigere til at vejlede (deres speciale)
16. Vi behøver ikke lave belastningsberegning
17. Det vigtigste er PDF’ens mål, brug dem som guideline! (maks 3,10 mellem hver stolpe. Nice to have; kan ændres)
18. Lad vær med at gå op i millimeter
19. Vindbånd sidder på rem’en
20. Man kan godt bestille at man vil have skur på, men det behøver ikke vises på tegningen (kan være en US langt nede på listen: nice to have)
21. Mandag d. 29 maj: Lav først domænemodel (den virkelige verden)
22. ER-diagram (den virtuelle verden), laves ud fra domænemodel
23. Postgres kodeord gemmes som miljøvariable på egen computer
24. Lav navigationsdiagram over websites flow

