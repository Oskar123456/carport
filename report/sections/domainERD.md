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

For at omdanne domænemodellen til en model der kan bruges til at implementere en database for 
en IT-løsning, kræves det, at mange-til-mange relationer omdannes til en-til-mange og mange-til-en 
relationer. Derudover skal domænets data helst bringes på normalform (det er dog ikke altid 
nødvendigt eller hensigtsmæssigt).

Nedenfor ses en _ERD_ model for en mulig implementation af en database.

- IMG : ERD.puml

