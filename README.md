
-----

# Payment Notification Service

Este é um serviço de backend para um sistema de pagamentos simplificado, construído com **Java 25** e **Spring Boot**. Ele gerencia usuários e lojistas, processa transações (depósitos e transferências) e utiliza **Apache Kafka** para notificações assíncronas de eventos. O serviço também é configurado para enviar notificações por e-mail usando o **Google Mail (SMTP)** com um token de acesso (App Password).

## ✨ Funcionalidades Principais

* **Autenticação e Autorização:** Cadastro e login para Usuários (`USER`) e Lojistas (`COMPANY`).
* **Operações Financeiras:** Endpoints para depósito e transferência de valores.
* **Gerenciamento de Entidades:** Operações CRUD para Usuários(Pessoas físicas) e Empresas(Pessoa jurídica).
* **Mensageria Assíncrona:** Utiliza **Apache Kafka** para notificar sobre transações.
* **Notificações por E-mail:** Envia e-mails de notificação usando o **Google Mail** (requer um "Token de Acesso" / "Senha de App").
* **Testes:** Cobertura de testes unitários com **JUnit 5**.

-----

## 🛠️ Stack Tecnológica

* **Backend:** Java 25, Spring Boot 4.0.2, Spring Security, Spring Data JPA
* **Database:** MySQL 9.6.0 (Via Docker)
* **Mensageria:** Apache Kafka (Via Docker)
* **Testes:** JUnit 5
* **Containerização:** Docker & Docker Compose
* **Notificações:** JavaMail (com Google SMTP)

-----

## 🏁 Começando

Siga estas instruções para configurar e executar o ambiente de desenvolvimento localmente.

### Pré-requisitos

* [Java 25 (JDK)](https://learn.microsoft.com/en-us/java/openjdk/download)
* [Apache Maven](https://maven.apache.org/download.cgi)
* [Docker](https://www.docker.com/products/docker-desktop/)
* [Docker Compose](https://docs.docker.com/compose/install/)
* Uma conta Google com **"Senhas de App"** ativada. (Veja [como gerar uma](https://support.google.com/accounts/answer/185833))

### 1\. Configuração do Ambiente

Primeiro, clone este repositório:

```bash
git clone https://github.com/Danilo-dps/payments-service.git
cd pay
```

Crie um arquivo `.env` na raiz do projeto, baseado no `docker-compose.yml`. Você também precisará adicionar as variáveis para o serviço de e-mail do Spring. Se for pelo **Intellij community** é necessário adicionar as variaveis de ambiente via aba **run** 

**Arquivo `.env` (Exemplo):**

```
# Variáveis do Docker Compose
MYSQL_ROOT_PASSWORD = senha_aqui
MYSQL_DATABASE = nome_do_banco
MYSQL_PORT = porta_da_aplicacao
KAFKA_PORT = porta_kafka_listener
KAFKA_CONTROLLER_PORT = porta_kafka_controller
```

**Importante:** Adicione `SPRING_MAIL_PASSWORD` ao seu arquivo `application.yml` (ou `application.properties`) para que o Spring possa usá-lo. Geralmente, você faria referência à variável de ambiente:

```yaml
# Em src/main/resources/application.properties
spring:
  mail:
    host: smtp.gmail.com
    port: 587
    username: ${EMAIL_USER}
    password: ${SPRING_MAIL_PASSWORD}
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true
```

### 2\. Iniciando a Infraestrutura (Docker)

O arquivo `docker-compose.yml` provisiona toda a infraestrutura necessária (MySQL, Kafka).
* [docker-compose.yml](https://github.com/Danilo-dps/docker-yamls/blob/main/payments-notification/docker-compose.yml)

Para iniciar todos os serviços em background:

```bash

# Comando para ser usado na pasta que está o docker-compose.yml
# /caminho/onde/está/o/arquivo
docker compose up -d
```

### 3\. Executando a Aplicação Java

Após a infraestrutura estar rodando, você pode iniciar a aplicação Spring Boot:

```bash

# Pode rodar pela IDE, sem usar o terminal
# Compile e execute usando o Maven
mvn spring-boot:run
```

Ou simplesmente execute a classe principal `Application` a partir da sua IDE (IntelliJ, VSCode, etc.).

### 4\. Acessando os Serviços

* **API:** `http://localhost:8080` (ou a porta definida no seu `application.yaml`)
* **MySQL:** `localhost:3306` (ou altere para outra disponível)

-----

## 🧪 Testes

Este projeto utiliza **JUnit 5** para testes unitários. Para rodar a suíte de testes, execute o seguinte comando Maven:

```bash

# Pode rodar pela IDE, sem usar o terminal
mvn test
```

-----

## 🚀 API Endpoints

Abaixo está um resumo dos endpoints da API disponíveis.

### 🔐 Autenticação (`/auth`)

* `POST /auth/v1/login`

    * Autentica um usuário e retorna um token JWT.
    * **Body:** `SignUpRequest`

* `POST /auth/v1/signup`

    * Registra um novo usuário, ao informar CPF gera usuário PF, CNPJ gera usuário PJ.
    * **Body:** `SignInRequest`

### 💸 Operações (`/operations`)

* `POST /operations/v1/deposit`

    * Realiza um depósito na conta do usuário autenticado.
    * **Autorização:** Requer uma role `USER`.
    * **Body:** `DepositRequest`

* `POST /operations/v1/transfer`

    * Realiza uma transferência da conta do usuário autenticado para outro usuário.
    * **Autorização:** Requer uma role `USER`.
    * **Body:** `TransactionRequest`

* `GET /operations/v1/deposit/{profileId}`

  * Busca depósitos na conta do usuário autenticado.
  * **Autorização:** Requer uma role `USER`.
  * **Param:** `profileId`

* `GET /operations/v1/transaction/{profileId}`

  * Busca transferências da conta do usuário autenticado para outro usuário.
  * **Autorização:** Requer uma role `USER`.
  * **Param:** `profileId`

### 👤 Usuário (`/profile/v1`)

* `GET /profile/v1/id/{profileId}`

    * Busca um usuário pelo seu UUID.

* `GET /profile/v1/profileEmail/{profileEmail}`

    * Busca um usuário pelo seu e-mail.

* `PUT /profile/v1/update/{profileId}`

    * Atualiza os dados de um usuário.
    * **Param** `profileId`
    * **Body:** `ProfileRequestUpdate`

* `DELETE /profile/v1/delete/{profileId}`

    * Exclui um usuário.

-----