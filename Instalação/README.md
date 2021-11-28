# Como executar:

### Faça download do executável:

Pelo [Github](https://github.com/PhatomFatec/PI_Necto_Systems/blob/main/executavel.zip)

## Como executar:

1. Faça download e extraia o arquivo ZIP

2. Abra o arquivo **login.txt**

3. Altere os parâmetros:

```
(1) - URL do seu banco.
(2) - Login do postgreSQL.
(3) - Senha do postgreSQL.
```
<img src="https://cdn.discordapp.com/attachments/870416846338273280/906997803354583140/unknown.png"/>

```
Salve o arquivo **login.txt** após as alterações.
```
4. Abra o terminal do windows dentro da pasta do projeto.
<img src="https://cdn.discordapp.com/attachments/870416846338273280/907001955396771910/unknown.png"  width="600"/>

5. Execute o comando: **java -jar executavel.jar**.
<img src="https://cdn.discordapp.com/attachments/870416846338273280/906999137470410782/unknown.png" width="600"/>

6. Dentro do shell será mostrado os resultados das metricas.
<img src="https://cdn.discordapp.com/attachments/870416846338273280/906999750547607613/unknown.png" width="600"/>

7. Ademais, execute o SQLiteStudio.

8. Na barra de menu do sistema, clique em "Database > Add a database".
<img src="https://user-images.githubusercontent.com/80851038/143785397-b3bba2bd-9b90-4260-9d11-8b154337a328.png" width="300"/>

9. No menu "Database", aperte o botão "+" na aba "File". Em seguida selecione o arquivo "historico.db" na pasta "executavel" que você baixou, e Salvar.
<img src="https://user-images.githubusercontent.com/80851038/143785515-300b5b61-913e-47d2-acc3-c78c60bb6472.png" width="600"/>

10. Concluindo, assim que clicar no botão "refresh" você verá o banco historico com todos os dados armazenados das métricas.
<img src="https://user-images.githubusercontent.com/80851038/143785953-71867b24-d0bc-4b2e-a27e-5e18b29768d5.png" width="260"/>

**OBS:** Lembre-se de sempre clicar em "refresh" quando executar o banco no SQLiteStudio

