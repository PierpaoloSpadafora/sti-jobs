import os

def merge_files(src_folder, output_file):
  print(f"Inizio processo di merging.")
  print(f"Cartella di origine: {src_folder}")
  print(f"File di output: {output_file}\n")

  # Verifica se la cartella di origine esiste
  if not os.path.exists(src_folder):
    print(f"Errore: La cartella di origine '{src_folder}' non esiste.")
    return

  # Crea la directory di output se non esiste
  output_dir = os.path.dirname(output_file)
  if output_dir and not os.path.exists(output_dir):
    try:
      os.makedirs(output_dir)
      print(f"Creata la directory di output: {output_dir}")
    except Exception as e:
      print(f"Errore nella creazione della directory di output '{output_dir}': {e}")
      return
  else:
    if output_dir:
      print(f"La directory di output esiste già: {output_dir}")
    else:
      print("La directory di output è la directory corrente.")

  file_count = 0  # Contatore per i file processati

  try:
    with open(output_file, 'w', encoding='utf-8') as merged_file:
      for root, dirs, files in os.walk(src_folder):
        print(f"Esplorando la cartella: {root}")

        # Salta la directory 'assets' se presente
        if 'assets' in dirs:
          dirs.remove('assets')
          print(f"Saltata la directory 'assets' in: {root}")

        for file_name in files:
          file_path = os.path.join(root, file_name)
          print(f"Processando file: {file_path}")

          # Verifica che sia un file regolare
          if os.path.isfile(file_path):
            try:
              with open(file_path, 'r', encoding='utf-8') as f:
                # Scrive il percorso relativo del file come commento
                relative_path = os.path.relpath(file_path, src_folder)
                merged_file.write(f"// {relative_path}\n")
                content = f.read()
                merged_file.write(content)
                merged_file.write("\n")
                file_count += 1
                print(f"File '{relative_path}' aggiunto al file di output.")
            except Exception as e:
              print(f"Errore nella lettura del file '{file_path}': {e}")
          else:
            print(f"Saltato '{file_path}' perché non è un file regolare.")

    print(f"\nMerging completato. Totale file aggiunti: {file_count}")
  except Exception as e:
    print(f"Errore nell'apertura del file di output '{output_file}': {e}")

if __name__ == "__main__":
  source_folder = "./src"
  output_file_path = "./merged_scripts.txt"
  merge_files(source_folder, output_file_path)
