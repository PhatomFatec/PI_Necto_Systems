# Como utilizar:

### Programas necessários:
```
- Eclipse IDE
- PostgreSQL
```

## Instalação:

1. Faça o download do projeto:
```
- git clone https://github.com/PhatomFatec/PI_Necto_Systems.git
  ou
- Botão code e download ZIP
- Extraia o arquivo ZIP.
```

2. Adicionando a extensão pg_stat_statements:
```
- Abra o pgAdmin e faça o login do banco.
- Abra a Query Tool da database (botão direito na database).
- Dentro da Query Tool insira esse código "CREATE EXTENSION pg_stat_statements;".
- Execute a Query (F5).
- Caso tenha executado com sucesso, deve retornar a mensagem "Query returned successfully".
- Execute esse mesmo passo para todas as databases criadas em seu PostgreSQL.
```
[Ver em imagens](https://github.com/PhatomFatec/PI_Necto_Systems/tree/main/Instala%C3%A7%C3%A3o/img/pg_stat_statements)


3. Abrindo o projeto no Eclipse
```
- Abra o eclipse.
- Vá até a aba File, no canto superior esquerdo.
- Selecione a opção "Open projects from File System".
- Aperte no botão "Directory".
- Vá até o caminho onde a pasta do projeto foi colocada e selecione ela.
- Aberte o botão "Finish".
```
[Ver em imagens](https://github.com/PhatomFatec/PI_Necto_Systems/tree/main/Instala%C3%A7%C3%A3o/img/ImportProject)

4. Alterando o banco padrão
```
- Com o projeto aberto no eclipse, abra a classe chamada conexao.java.
- Vá até a linha 277 e coloque o nome da sua database dentro dos parênteses
			conexao conx = new conexao(NomeDatabaseAqui);
```
[Ver em imagens](google.com)
