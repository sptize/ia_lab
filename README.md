# ia_lab (JAVA 25)
Proyecto de implementación de algoritmos de búsqueda informada: A*, greedy algorithm, hill climbing, mini-max. 

## Compilar y ejecutar

```bash
javac -d out $(find src -name "*.java")
java -cp out com.busquedainformada.ia_lab.IaLab
```

En Windows (PowerShell):

```powershell
javac -d out (Get-ChildItem -Recurse src -Filter *.java).FullName
java -cp out com.busquedainformada.ia_lab.IaLab
```