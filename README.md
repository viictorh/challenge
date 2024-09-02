# Challenge

## Sobre

* O cliente poderá inserir os dados através de requisições informando um único cartão ou a partir de arquivo TXT a API.
* Por serem dados sensíveis toda informação deve ser armazenada de maneira segura no banco de dados.
* O cliente consulta se determinado número de cartão completo existe na base de dados e retorna um identificador único do sistema;
* Logar as requisições de uso da API e seus retornos.

## Execução

* Para executar o projeto basta acessar a raiz do projeto e subir via docker com o comando `docker-compose up`. Por padrão, a aplicação rodará na porta `8080` e o banco na porta `3306`.
* Ainda na pasta raiz, existe o arquivo **postman.json** que pode ser importado para execução dos endpoints e o arquivo **batch-example.txt** utilizado para validar upload de arquivo
* Existe 1 usuário criado no sistema: login **admin**, senha **admin**. Com este usuário é possível utilizar todos os endpoints.
* Existe a possibilidade de criar um novo usuário pelo endpoint `/auth/signup`. Este usuário terá a permissao de **user**, podendo apenas consultar cartões e não cadastrar.

### Upload de arquivo

* O upload do arquivo pode ser utilizando o postman e requisitando o endpoint `/creditcard/file-upload` realizando a requisição como **binary** e selecionando o arquivo.
Ex da requisição via CURL: 

BASH (Linux)

``` 
curl -X POST \
     -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE3MjUyODYxMTksImV4cCI6MTcyNTM3MjUxOSwiaWQiOjEsImxvZ2luIjoiYWRtaW4iLCJwcm9maWxlIjpbeyJhdXRob3JpdHkiOiJST0xFX0FETUlOIn1dfQ.NjWN2c5ZiBccUuet8U23Zg3JQ9939cfc23yBI0AAX28" \
     -H "Content-Type: text/plain" \
     --data-binary "@/C:/Users/victor/Downloads/DESAFIO-HYPERATIVA - MULTIPLOS.txt" \
     http://localhost:8080/creditcard/file-upload

```

Windows:

```
curl -X POST ^
     -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE3MjUyODYxMTksImV4cCI6MTcyNTM3MjUxOSwiaWQiOjEsImxvZ2luIjoiYWRtaW4iLCJwcm9maWxlIjpbeyJhdXRob3JpdHkiOiJST0xFX0FETUlOIn1dfQ.NjWN2c5ZiBccUuet8U23Zg3JQ9939cfc23yBI0AAX28" ^
     -H "Content-Type: text/plain" ^
     --data-binary @"C:\Users\victor\Downloads\DESAFIO-HYPERATIVA - MULTIPLOS.txt" ^
     http://localhost:8080/creditcard/file-upload
```


## Considerações

* Da forma que foi desenvolvido, o arquivo para upload pode ter tamanho **ilimitado**. 
* Pelo arquivo de exemplo, entendi que no mesmo arquivo podem existir outros lotes e cartões. Sendo assim, criei a logica do cadastro por lote.
* Como na busca pelo cartão traz "um" identificador, considerei que os cartões não podem ser cadastrados repetidos, independente do lote. 
* A inserção de apenas um cartão não tem a obrigatoriedade de informar o lote.
* Escalabilidade: A aplicação da API é stateless, ou seja, pode-se subir quantos containers desejar para realizar a escalabilidade horizontal e processamento de diversas requisições (adicionando um loadbalancer, com nginx ou até api-gateway + eureka). Com mais tempo e mais informações, poderia decidir se valeria a pena retornar pro usuário final a resposta de conclusão do upload com um ID de acompanhamento e realizar o processamento em background e multi-thread, ou ainda utilizar filas de processamento (talvez com rabbitmq) para que houvesse "workers" de processamento de lotes. 
 