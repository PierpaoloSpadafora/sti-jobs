
# STI Jobs

## Avviare Docker

- Assicuratevi di essere in `./sti-jobs/`.
- Eseguite:

   ```bash
   docker-compose --env-file .env up --build
   ```

### oppure se siete su windows

- Eseguite:

   ```bash
   ./RUNALL.bat
   ```

Docker scaricherà l'immagine di PostgreSQL da Docker Hub, avviando un’istanza e creando un un unico database chiamato `sti-jobs`.
Al primo avvio del backend, JPA provvederà a creare automaticamente le tabelle necessarie.

## Definition of Ready (DoR)

1. La descrizione del ticket è chiara e completa, includendo tutti i dettagli necessari per lo sviluppo.
2. Sono stati definiti i casi di test preliminari per la user story o la task e sono stati inclusi nel ticket.
3. L'ambiente di sviluppo è stato configurato correttamente e tutti i requisiti sono stati soddisfatti.
4. Tutti i prerequisiti per lo sviluppo del ticket sono stati soddisfatti.
5. Il ticket è stato approvato dal Product Owner e gli è stato assegnato un livello di priorità.
6. La Definition of Done per il task è stata chiaramente definita e inclusa nel ticket.

## Definition of Done (DoD) per le tasks

1. Il progetto compila senza errori e tutte le dipendenze sono state risolte correttamente.
2. Sono stati scritti o aggiornati i test di unità per verificare il corretto funzionamento del codice.
3. Tutti i test di unità e di integrazione passano senza errori.
4. Il codice è stato integrato nel branch `dev` tramite una pull request.

## Definition of Done (DoD) per le user stories

1. Tutte le task associate alla user story sono state completate e soddisfano la loro DoD.
2. La user story è stata approvata dal Product Owner ( Ilaria🦦 :> )
3. Il codice è stato integrato nel branch `main` tramite una pull request.
