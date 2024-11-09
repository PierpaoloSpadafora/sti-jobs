
# STI Jobs

## Avviare Docker

1. Assicuratevi di essere in `./sti-jobs/`.
2. Eseguite:

   ```bash
   docker-compose --env-file .env up --build
   ```

#### oppure se siete su windows

2. Eseguite:

   ```bash
   ./RUNALL.bat
   ```

Docker scaricherà l'immagine di PostgreSQL da Docker Hub, avviando un’istanza e creando un un unico database chiamato `sti-jobs`.
Al primo avvio del backend, JPA provvederà a creare automaticamente le tabelle necessarie.

## Definition of Ready (DoR)

Perché un ticket sia considerato "Ready" per lo sviluppo, devono essere soddisfatti i seguenti criteri:

1. **Descrizione Completa**: Il ticket contiene una descrizione dettagliata degli obiettivi e del contesto. Specifica chiaramente quale funzionalità deve essere testata e quali sono i criteri di successo.
2. **Casi di Test Preliminari**: I casi di test principali sono stati identificati e documentati, specificando cosa deve essere verificato nei test automatizzati.
3. **Ambiente Configurato**: Gli ambienti di test e sviluppo sono pronti e configurati correttamente. È stata verificata la disponibilità delle dipendenze e delle librerie necessarie (in questo caso, JUnit).
4. **Prerequisiti Soddisfatti**: Tutti i requisiti precedenti o le dipendenze (es. implementazioni o bug fix) sono stati completati e chiusi.
5. **Accettazione e Priorità**: Il ticket è stato approvato dal Product Owner o dal referente tecnico ed è stato assegnato un livello di priorità.
6. **Definition of Done (DoD)**: La Definition of Done per il task è stata chiaramente definita e inclusa nel ticket.

## Definition of Done (DoD) per le user stories

Perché una user story sia considerata "Done", devono essere soddisfatti i seguenti criteri:

1. **Completata tutte le relative task**: Tutte le task associate alla user story sono state completate e soddisfano i criteri di "Done".
2. **Approvato dal Product Owner**: La user story è stata approvata dal Product Owner o dal referente tecnico.
3. **Merge in dev**: Il codice è stato integrato nel branch `dev` tramite una pull request.

## Definition of Done (DoD) per le tasks

Perché una task sia considerata "Done", devono essere soddisfatti i seguenti criteri:

1. **Il progetto builda senza errori**: Il progetto compila senza errori e tutte le dipendenze sono state risolte correttamente.
2. **Gli unit test sono stati scritti/aggiornati**: Sono stati scritti o aggiornati i test di unità per verificare il corretto funzionamento del codice.
3. **Tutti i test passano**: Tutti i test di unità e di integrazione passano senza errori.




