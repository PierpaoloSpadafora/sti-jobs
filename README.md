
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