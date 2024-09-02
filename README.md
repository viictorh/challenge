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

## Considerações

* Da forma que foi desenvolvido, o arquivo para upload pode ter tamanho **ilimitado**. 
* Pelo arquivo de exemplo, entendi que no mesmo arquivo podem existir outros lotes e cartões. Sendo assim, criei a logica do cadastro por lote.
* Como na busca pelo cartão traz "um" identificador, considerei que os cartões não podem ser cadastrados repetidos, independente do lote. 
* A inserção de apenas um cartão não tem a obrigatoriedade de informar o lote.
* Escalabilidade: A aplicação da API é stateless, ou seja, pode-se subir quantos containers desejar para realizar a escalabilidade horizontal e processamento de diversas requisições (adicionando um loadbalancer, com nginx ou até api-gateway + eureka). Com mais tempo e mais informações, poderia decidir se valeria a pena retornar pro usuário final a resposta de conclusão do upload com um ID de acompanhamento e realizar o processamento em background e multi-thread, ou ainda utilizar filas de processamento (talvez com rabbitmq) para que houvesse "workers" de processamento de lotes. 
 